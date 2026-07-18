package com.logan.branchapi.mapper;

import org.springframework.stereotype.Component;

import com.logan.branchapi.dto.BranchCreateRequest;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchUpdateRequest;
import com.logan.branchapi.entity.Branch;

@Component
public class BranchMapper {

    public Branch toEntity(BranchCreateRequest request) {
        Branch branch = new Branch();

        branch.setBranchCode(normalizeCode(request.branchCode()));
        branch.setBranchName(request.branchName().trim());
        branch.setAddress(request.address().trim());
        branch.setCity(request.city().trim());
        branch.setCountry(request.country().trim());
        branch.setLatitude(request.latitude());
        branch.setLongitude(request.longitude());
        branch.setPhoneNumber(normalizeNullable(request.phoneNumber()));
        branch.setActive(
                request.active() == null || request.active()
        );

        return branch;
    }

    public void updateEntity(
            Branch branch,
            BranchUpdateRequest request) {

        branch.setBranchCode(normalizeCode(request.branchCode()));
        branch.setBranchName(request.branchName().trim());
        branch.setAddress(request.address().trim());
        branch.setCity(request.city().trim());
        branch.setCountry(request.country().trim());
        branch.setLatitude(request.latitude());
        branch.setLongitude(request.longitude());
        branch.setPhoneNumber(normalizeNullable(request.phoneNumber()));
        branch.setActive(request.active());
    }

    public BranchResponse toResponse(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getBranchCode(),
                branch.getBranchName(),
                branch.getAddress(),
                branch.getCity(),
                branch.getCountry(),
                branch.getLatitude(),
                branch.getLongitude(),
                branch.getPhoneNumber(),
                branch.isActive(),
                branch.getCreatedAt(),
                branch.getUpdatedAt()
        );
    }

    private String normalizeCode(String value) {
        return value.trim().toUpperCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}