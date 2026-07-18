package com.logan.branchapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BranchResponse(
        Long id,
        String branchCode,
        String branchName,
        String address,
        String city,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}