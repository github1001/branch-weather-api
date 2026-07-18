package com.logan.branchapi.dto;

public record BranchWeatherResponse(
        BranchResponse branch,
        WeatherResponse weather,
        String source
) {
}