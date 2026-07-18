package com.logan.branchapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.logan.branchapi.dto.BranchCreateRequest;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchWeatherResponse;
import com.logan.branchapi.dto.PageResponse;
import com.logan.branchapi.dto.WeatherResponse;
import com.logan.branchapi.service.BranchService;
import com.logan.branchapi.service.BranchWeatherService;

@WebMvcTest(BranchController.class)
class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BranchService branchService;

    @MockitoBean
    private BranchWeatherService branchWeatherService;

    @Test
    void getBranch_returns200AndBranchJson()
            throws Exception {

        when(branchService.getBranch(1L))
                .thenReturn(branchResponse());

        mockMvc.perform(
                get("/api/branches/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.branchCode")
                                .value("KL001")
                )
                .andExpect(
                        jsonPath("$.city")
                                .value("Kuala Lumpur")
                );
    }

    @Test
    void getBranches_returnsPaginatedResponse()
            throws Exception {

        PageResponse<BranchResponse> pageResponse =
                new PageResponse<>(
                        List.of(branchResponse()),
                        0,
                        10,
                        25,
                        3,
                        true,
                        false,
                        false
                );

        when(branchService.getBranches(0))
                .thenReturn(pageResponse);

        mockMvc.perform(
                get("/api/branches")
                        .queryParam("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.page").value(0)
                )
                .andExpect(
                        jsonPath("$.size").value(10)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(25)
                )
                .andExpect(
                        jsonPath("$.totalPages").value(3)
                )
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1)
                );
    }

    @Test
    void createBranch_returns201AndLocationHeader()
            throws Exception {

        when(
                branchService.createBranch(
                        any(BranchCreateRequest.class)
                )
        ).thenReturn(branchResponse());

        String requestBody = """
                {
                  "branchCode": "KL001",
                  "branchName": "Kuala Lumpur Headquarters",
                  "address": "Menara Maybank, Jalan Tun Perak",
                  "city": "Kuala Lumpur",
                  "country": "Malaysia",
                  "latitude": 3.1478000,
                  "longitude": 101.6953000,
                  "phoneNumber": "03-12345678",
                  "active": true
                }
                """;

        mockMvc.perform(
                post("/api/branches")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(requestBody)
        )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/api/branches/1"
                        )
                )
                .andExpect(
                        jsonPath("$.branchCode")
                                .value("KL001")
                );
    }

    @Test
    void createBranch_returns400ForInvalidRequest()
            throws Exception {

        String invalidRequest = """
                {
                  "branchCode": "",
                  "branchName": "",
                  "address": "",
                  "city": "",
                  "country": "",
                  "latitude": 120,
                  "longitude": 250,
                  "phoneNumber": "123456789012345678901234567890123456",
                  "active": true
                }
                """;

        mockMvc.perform(
                post("/api/branches")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(invalidRequest)
        )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Request validation failed."
                                )
                )
                .andExpect(
                        jsonPath("$.fieldErrors.branchCode")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.fieldErrors.latitude")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.fieldErrors.longitude")
                                .exists()
                );
    }

    @Test
    void getBranchWeather_returnsCombinedResponse()
            throws Exception {

        BranchWeatherResponse response =
                new BranchWeatherResponse(
                        branchResponse(),
                        weatherResponse(),
                        "Open-Meteo"
                );

        when(
                branchWeatherService
                        .getBranchWeather(1L)
        ).thenReturn(response);

        mockMvc.perform(
                get("/api/branches/1/weather")
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.branch.branchCode")
                                .value("KL001")
                )
                .andExpect(
                        jsonPath("$.weather.temperature")
                                .value(25.0)
                )
                .andExpect(
                        jsonPath("$.weather.description")
                                .value("Overcast")
                )
                .andExpect(
                        jsonPath("$.source")
                                .value("Open-Meteo")
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