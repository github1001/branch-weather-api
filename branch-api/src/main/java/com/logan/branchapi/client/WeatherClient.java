package com.logan.branchapi.client;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.logan.branchapi.client.dto.OpenMeteoResponse;
import com.logan.branchapi.dto.WeatherResponse;
import com.logan.branchapi.exception.ThirdPartyApiException;

@Component
public class WeatherClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WeatherClient.class);

    private static final String CURRENT_FIELDS =
            String.join(
                    ",",
                    "temperature_2m",
                    "relative_humidity_2m",
                    "weather_code",
                    "wind_speed_10m"
            );

    private final RestClient weatherRestClient;

    public WeatherClient(RestClient weatherRestClient) {
        this.weatherRestClient = weatherRestClient;
    }

    public WeatherResponse getCurrentWeather(
            BigDecimal latitude,
            BigDecimal longitude) {

        LOGGER.info(
                "Calling Open-Meteo for latitude={}, longitude={}",
                latitude,
                longitude
        );

        try {
            OpenMeteoResponse response =
                    weatherRestClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1/forecast")
                                    .queryParam(
                                            "latitude",
                                            latitude
                                    )
                                    .queryParam(
                                            "longitude",
                                            longitude
                                    )
                                    .queryParam(
                                            "current",
                                            CURRENT_FIELDS
                                    )
                                    .queryParam(
                                            "timezone",
                                            "auto"
                                    )
                                    .build())
                            .retrieve()
                            .body(OpenMeteoResponse.class);

            validateResponse(response);

            OpenMeteoResponse.Current current =
                    response.current();

            OpenMeteoResponse.CurrentUnits units =
                    response.currentUnits();

            WeatherResponse weatherResponse =
                    new WeatherResponse(
                            current.time(),
                            current.temperature(),
                            getUnit(
                                    units == null
                                            ? null
                                            : units.temperature(),
                                    "°C"
                            ),
                            current.relativeHumidity(),
                            getUnit(
                                    units == null
                                            ? null
                                            : units.relativeHumidity(),
                                    "%"
                            ),
                            current.windSpeed(),
                            getUnit(
                                    units == null
                                            ? null
                                            : units.windSpeed(),
                                    "km/h"
                            ),
                            current.weatherCode(),
                            describeWeather(
                                    current.weatherCode()
                            ),
                            response.timezone()
                    );

            LOGGER.info(
                    "Open-Meteo response received: observedAt={}, "
                            + "temperature={}, weatherCode={}",
                    weatherResponse.observedAt(),
                    weatherResponse.temperature(),
                    weatherResponse.weatherCode()
            );

            return weatherResponse;

        } catch (RestClientResponseException exception) {
            LOGGER.error(
                    "Open-Meteo returned HTTP status {}: {}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString(),
                    exception
            );

            throw new ThirdPartyApiException(
                    "The external weather service returned an unsuccessful response.",
                    exception
            );

        } catch (RestClientException exception) {
            LOGGER.error(
                    "Open-Meteo request failed: {}",
                    exception.getMessage(),
                    exception
            );

            throw new ThirdPartyApiException(
                    "The external weather service is currently unavailable.",
                    exception
            );
        }
    }

    private void validateResponse(
            OpenMeteoResponse response) {

        if (response == null
                || response.current() == null) {

            throw new ThirdPartyApiException(
                    "The external weather service returned an empty response."
            );
        }

        if (response.current().temperature() == null
                || response.current().weatherCode() == null) {

            throw new ThirdPartyApiException(
                    "The external weather service returned incomplete weather data."
            );
        }
    }

    private String getUnit(
            String suppliedUnit,
            String defaultUnit) {

        if (suppliedUnit == null
                || suppliedUnit.isBlank()) {

            return defaultUnit;
        }

        return suppliedUnit;
    }

    private String describeWeather(
            Integer weatherCode) {

        if (weatherCode == null) {
            return "Unknown";
        }

        return switch (weatherCode) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55 ->
                    "Drizzle";
            case 56, 57 ->
                    "Freezing drizzle";
            case 61, 63, 65 ->
                    "Rain";
            case 66, 67 ->
                    "Freezing rain";
            case 71, 73, 75, 77 ->
                    "Snow";
            case 80, 81, 82 ->
                    "Rain showers";
            case 85, 86 ->
                    "Snow showers";
            case 95 ->
                    "Thunderstorm";
            case 96, 99 ->
                    "Thunderstorm with hail";
            default ->
                    "Unknown weather condition";
        };
    }
}