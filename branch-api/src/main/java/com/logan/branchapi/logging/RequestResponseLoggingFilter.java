package com.logan.branchapi.logging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestResponseLoggingFilter
        extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    RequestResponseLoggingFilter.class
            );

    private static final String CORRELATION_HEADER =
            "X-Correlation-ID";

    private static final String CORRELATION_MDC_KEY =
            "correlationId";

    private static final int MAX_BODY_LENGTH = 10_000;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        /*
         * Log client-facing APIs only.
         * This prevents health checks and static resources
         * from filling the application log.
         */
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId =
                resolveCorrelationId(request);

        MDC.put(CORRELATION_MDC_KEY, correlationId);
        response.setHeader(
                CORRELATION_HEADER,
                correlationId
        );

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(
                        request,
                        MAX_BODY_LENGTH
                );

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(
                    requestWrapper,
                    responseWrapper
            );
        } finally {
            long durationMs =
                    System.currentTimeMillis() - startTime;

            logRequestAndResponse(
                    requestWrapper,
                    responseWrapper,
                    correlationId,
                    durationMs
            );

            /*
             * Critical: the response body is currently inside
             * the caching wrapper. Copy it back to the actual
             * HTTP response before returning to the client.
             */
            responseWrapper.copyBodyToResponse();

            MDC.remove(CORRELATION_MDC_KEY);
        }
    }

    private void logRequestAndResponse(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String correlationId,
            long durationMs) {

        String queryString =
                request.getQueryString() == null
                        ? ""
                        : "?" + request.getQueryString();

        LOGGER.info(
                """
                HTTP REQUEST/RESPONSE
                correlationId={}
                method={}
                uri={} {}
                clientAddress={}
                requestHeaders={}
                requestBody={}
                responseStatus={}
                responseHeaders={}
                responseBody={}
                durationMs={}
                """,
                correlationId,
                request.getMethod(),
                request.getRequestURI(),
                queryString,
                request.getRemoteAddr(),
                getSafeRequestHeaders(request),
                getRequestBody(request),
                response.getStatus(),
                getSafeResponseHeaders(response),
                getResponseBody(response),
                durationMs
        );
    }

    private String resolveCorrelationId(
            HttpServletRequest request) {

        String suppliedId =
                request.getHeader(CORRELATION_HEADER);

        if (StringUtils.hasText(suppliedId)) {
            return suppliedId.trim();
        }

        return UUID.randomUUID().toString();
    }

    private Map<String, String> getSafeRequestHeaders(
            HttpServletRequest request) {

        Map<String, String> headers =
                Collections.list(request.getHeaderNames())
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        headerName -> headerName,
                                        headerName ->
                                                maskHeader(
                                                        headerName,
                                                        request.getHeader(
                                                                headerName
                                                        )
                                                ),
                                        (first, second) -> first,
                                        LinkedHashMap::new
                                )
                        );

        return headers;
    }

    private Map<String, String> getSafeResponseHeaders(
            ContentCachingResponseWrapper response) {

        Map<String, String> headers =
                new LinkedHashMap<>();

        for (String headerName :
                response.getHeaderNames()) {

            headers.put(
                    headerName,
                    maskHeader(
                            headerName,
                            response.getHeader(headerName)
                    )
            );
        }

        return headers;
    }

    private String maskHeader(
            String name,
            String value) {

        if (name == null) {
            return value;
        }

        if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)
                || HttpHeaders.COOKIE.equalsIgnoreCase(name)
                || HttpHeaders.SET_COOKIE
                        .equalsIgnoreCase(name)) {

            return "***MASKED***";
        }

        return value;
    }

    private String getRequestBody(
            ContentCachingRequestWrapper request) {

        byte[] content =
                request.getContentAsByteArray();

        return convertBody(
                content,
                request.getCharacterEncoding(),
                request.getContentType()
        );
    }

    private String getResponseBody(
            ContentCachingResponseWrapper response) {

        byte[] content =
                response.getContentAsByteArray();

        return convertBody(
                content,
                response.getCharacterEncoding(),
                response.getContentType()
        );
    }

    private String convertBody(
            byte[] content,
            String encoding,
            String contentType) {

        if (content == null || content.length == 0) {
            return "<empty>";
        }

        if (!isTextContent(contentType)) {
            return "<binary or unsupported content>";
        }

        Charset charset = StandardCharsets.UTF_8;

        if (StringUtils.hasText(encoding)) {
            try {
                charset = Charset.forName(encoding);
            } catch (Exception ignored) {
                charset = StandardCharsets.UTF_8;
            }
        }

        String body = new String(content, charset);

        /*
         * Prevent passwords from being exposed when a future
         * endpoint receives password-like JSON fields.
         */
        body = maskSensitiveJsonFields(body);

        if (body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH)
                    + "...<truncated>";
        }

        return body;
    }

    private boolean isTextContent(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return true;
        }

        String normalized =
                contentType.toLowerCase();

        return normalized.contains("json")
                || normalized.contains("xml")
                || normalized.contains("text")
                || normalized.contains(
                        "application/x-www-form-urlencoded"
                );
    }

    private String maskSensitiveJsonFields(String body) {
        return body.replaceAll(
                "(?i)(\"(?:password|token|secret|apiKey)\"\\s*:\\s*\")[^\"]*(\")",
                "$1***MASKED***$2"
        );
    }
}