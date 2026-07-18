package com.logan.branchapi.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(

        Double latitude,
        Double longitude,
        String timezone,

        @JsonProperty("current_units")
        CurrentUnits currentUnits,

        Current current
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(

            String time,

            @JsonProperty("temperature_2m")
            Double temperature,

            @JsonProperty("relative_humidity_2m")
            Integer relativeHumidity,

            @JsonProperty("weather_code")
            Integer weatherCode,

            @JsonProperty("wind_speed_10m")
            Double windSpeed
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentUnits(

            @JsonProperty("temperature_2m")
            String temperature,

            @JsonProperty("relative_humidity_2m")
            String relativeHumidity,

            @JsonProperty("weather_code")
            String weatherCode,

            @JsonProperty("wind_speed_10m")
            String windSpeed
    ) {
    }
}