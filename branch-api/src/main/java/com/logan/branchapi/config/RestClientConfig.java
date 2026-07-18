package com.logan.branchapi.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient weatherRestClient(
            @Value("${weather.api.base-url}")
            String weatherApiBaseUrl,

            @Value("${weather.api.connect-timeout}")
            Duration connectTimeout,

            @Value("${weather.api.read-timeout}")
            Duration readTimeout) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(weatherApiBaseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(
                        "User-Agent",
                        "branch-weather-api/1.0"
                )
                .build();
    }
}