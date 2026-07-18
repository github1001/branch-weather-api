package com.logan.branchapi.service;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logan.branchapi.dto.BranchCreateRequest;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchUpdateRequest;
import com.logan.branchapi.dto.PageResponse;
import com.logan.branchapi.entity.Branch;
import com.logan.branchapi.exception.BranchNotFoundException;
import com.logan.branchapi.exception.DuplicateBranchCodeException;
import com.logan.branchapi.mapper.BranchMapper;
import com.logan.branchapi.repository.BranchRepository;

@Service
public class BranchService {

    private static final int PAGE_SIZE = 10;

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public BranchService(
            BranchRepository branchRepository,
            BranchMapper branchMapper) {

        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }

    @Transactional
    public BranchResponse createBranch(
            BranchCreateRequest request) {

        String normalizedCode =
                normalizeCode(request.branchCode());

        if (branchRepository.existsByBranchCode(normalizedCode)) {
            throw new DuplicateBranchCodeException(
                    normalizedCode
            );
        }

        Branch branch = branchMapper.toEntity(request);
        branch.setUpdatedAt(LocalDateTime.now());

        try {
            Branch savedBranch =
                    branchRepository.saveAndFlush(branch);

            return branchMapper.toResponse(savedBranch);

        } catch (DataIntegrityViolationException exception) {
            /*
             * Protects against concurrent requests that insert
             * the same branch code at approximately the same time.
             */
            throw new DuplicateBranchCodeException(
                    normalizedCode
            );
        }
    }

    @Transactional
    public BranchResponse updateBranch(
            Long id,
            BranchUpdateRequest request) {

        Branch branch = findBranchEntity(id);

        String normalizedCode =
                normalizeCode(request.branchCode());

        boolean duplicateExists =
                branchRepository.existsByBranchCodeAndIdNot(
                        normalizedCode,
                        id
                );

        if (duplicateExists) {
            throw new DuplicateBranchCodeException(
                    normalizedCode
            );
        }

        branchMapper.updateEntity(branch, request);
        branch.setUpdatedAt(LocalDateTime.now());

        try {
            Branch updatedBranch =
                    branchRepository.saveAndFlush(branch);

            return branchMapper.toResponse(updatedBranch);

        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateBranchCodeException(
                    normalizedCode
            );
        }
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranch(Long id) {
        Branch branch = findBranchEntity(id);

        return branchMapper.toResponse(branch);
    }

    @Transactional(readOnly = true)
    public PageResponse<BranchResponse> getBranches(
            int page) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number must be zero or greater."
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(
                        Sort.Direction.ASC,
                        "id"
                )
        );

        Page<BranchResponse> responsePage =
                branchRepository
                        .findAll(pageable)
                        .map(branchMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional
    public void deleteBranch(Long id) {
        Branch branch = findBranchEntity(id);

        branchRepository.delete(branch);
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranchByCode(
            String branchCode) {

        String normalizedCode =
                normalizeCode(branchCode);

        Branch branch = branchRepository
                .findByBranchCode(normalizedCode)
                .orElseThrow(
                        () -> BranchNotFoundException.forCode(
                                normalizedCode
                        )
                );

        return branchMapper.toResponse(branch);
    }

    private Branch findBranchEntity(Long id) {
        return branchRepository
                .findById(id)
                .orElseThrow(
                        () -> new BranchNotFoundException(id)
                );
    }

    private String normalizeCode(String branchCode) {
        return branchCode.trim().toUpperCase();
    }
}