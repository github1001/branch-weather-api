package com.logan.branchapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BranchUpdateRequest(

        @NotBlank(message = "Branch code is required")
        @Size(
                max = 50,
                message = "Branch code must not exceed 50 characters"
        )
        String branchCode,

        @NotBlank(message = "Branch name is required")
        @Size(
                max = 255,
                message = "Branch name must not exceed 255 characters"
        )
        String branchName,

        @NotBlank(message = "Address is required")
        @Size(
                max = 1000,
                message = "Address must not exceed 1000 characters"
        )
        String address,

        @NotBlank(message = "City is required")
        @Size(
                max = 150,
                message = "City must not exceed 150 characters"
        )
        String city,

        @NotBlank(message = "Country is required")
        @Size(
                max = 100,
                message = "Country must not exceed 100 characters"
        )
        String country,

        @NotNull(message = "Latitude is required")
        @DecimalMin(
                value = "-90.0",
                message = "Latitude must be at least -90"
        )
        @DecimalMax(
                value = "90.0",
                message = "Latitude must not exceed 90"
        )
        @Digits(
                integer = 3,
                fraction = 7,
                message = "Latitude must contain at most 7 decimal places"
        )
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(
                value = "-180.0",
                message = "Longitude must be at least -180"
        )
        @DecimalMax(
                value = "180.0",
                message = "Longitude must not exceed 180"
        )
        @Digits(
                integer = 3,
                fraction = 7,
                message = "Longitude must contain at most 7 decimal places"
        )
        BigDecimal longitude,

        @Size(
                max = 30,
                message = "Phone number must not exceed 30 characters"
        )
        String phoneNumber,

        @NotNull(message = "Active status is required")
        Boolean active

) {
}