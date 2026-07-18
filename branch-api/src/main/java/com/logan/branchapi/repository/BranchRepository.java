package com.logan.branchapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logan.branchapi.entity.Branch;

public interface BranchRepository
        extends JpaRepository<Branch, Long> {

    boolean existsByBranchCode(String branchCode);

    boolean existsByBranchCodeAndIdNot(
        String branchCode,
        Long id
    );

    Optional<Branch> findByBranchCode(String branchCode);
}