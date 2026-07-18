package com.logan.branchapi.service;

import org.springframework.stereotype.Service;

import com.logan.branchapi.client.WeatherClient;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchWeatherResponse;
import com.logan.branchapi.dto.WeatherResponse;

@Service
public class BranchWeatherService {

    private final BranchService branchService;
    private final WeatherClient weatherClient;

    public BranchWeatherService(
            BranchService branchService,
            WeatherClient weatherClient) {

        this.branchService = branchService;
        this.weatherClient = weatherClient;
    }

    /*
     * Deliberately not marked @Transactional.
     *
     * branchService.getBranch() performs the short,
     * read-only database transaction. That transaction
     * ends before this method waits for Open-Meteo.
     */
    public BranchWeatherResponse getBranchWeather(
            Long branchId) {

        BranchResponse branch =
                branchService.getBranch(branchId);

        WeatherResponse weather =
                weatherClient.getCurrentWeather(
                        branch.latitude(),
                        branch.longitude()
                );

        return new BranchWeatherResponse(
                branch,
                weather,
                "Open-Meteo"
        );
    }
}