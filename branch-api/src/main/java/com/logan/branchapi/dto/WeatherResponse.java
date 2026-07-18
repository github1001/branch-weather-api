package com.logan.branchapi.dto;

public record WeatherResponse(
        String observedAt,
        Double temperature,
        String temperatureUnit,
        Integer relativeHumidity,
        String humidityUnit,
        Double windSpeed,
        String windSpeedUnit,
        Integer weatherCode,
        String description,
        String timezone
) {
}