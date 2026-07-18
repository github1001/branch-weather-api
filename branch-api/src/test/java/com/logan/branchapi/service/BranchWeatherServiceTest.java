package com.logan.branchapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logan.branchapi.client.WeatherClient;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchWeatherResponse;
import com.logan.branchapi.dto.WeatherResponse;

@ExtendWith(MockitoExtension.class)
class BranchWeatherServiceTest {

    @Mock
    private BranchService branchService;

    @Mock
    private WeatherClient weatherClient;

    private BranchWeatherService branchWeatherService;

    @BeforeEach
    void setUp() {
        branchWeatherService =
                new BranchWeatherService(
                        branchService,
                        weatherClient
                );
    }

    @Test
    void getBranchWeather_combinesDatabaseAndWeatherData() {
        BranchResponse branch = branchResponse();
        WeatherResponse weather = weatherResponse();

        when(branchService.getBranch(1L))
                .thenReturn(branch);

        when(
                weatherClient.getCurrentWeather(
                        branch.latitude(),
                        branch.longitude()
                )
        ).thenReturn(weather);

        BranchWeatherResponse result =
                branchWeatherService
                        .getBranchWeather(1L);

        assertThat(result.branch()).isEqualTo(branch);
        assertThat(result.weather()).isEqualTo(weather);
        assertThat(result.source())
                .isEqualTo("Open-Meteo");

        verify(branchService).getBranch(1L);

        verify(weatherClient)
                .getCurrentWeather(
                        new BigDecimal("3.1478000"),
                        new BigDecimal("101.6953000")
                );
    }

    private BranchResponse branchResponse() {
        LocalDateTime timestamp =
                LocalDateTime.of(
                        2026,
                        7,
                        18,
                        22,
                        0
                );

        return new BranchResponse(
                1L,
                "KL001",
                "Kuala Lumpur Headquarters",
                "Menara Maybank, Jalan Tun Perak",
                "Kuala Lumpur",
                "Malaysia",
                new BigDecimal("3.1478000"),
                new BigDecimal("101.6953000"),
                "03-12345678",
                true,
                timestamp,
                timestamp
        );
    }

    private WeatherResponse weatherResponse() {
        return new WeatherResponse(
                "2026-07-18T23:00",
                25.0,
                "°C",
                96,
                "%",
                2.7,
                "km/h",
                3,
                "Overcast",
                "Asia/Kuala_Lumpur"
        );
    }
}