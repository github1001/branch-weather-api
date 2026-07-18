package com.logan.branchapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.logan.branchapi.dto.BranchCreateRequest;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchUpdateRequest;
import com.logan.branchapi.dto.PageResponse;
import com.logan.branchapi.entity.Branch;
import com.logan.branchapi.exception.BranchNotFoundException;
import com.logan.branchapi.exception.DuplicateBranchCodeException;
import com.logan.branchapi.mapper.BranchMapper;
import com.logan.branchapi.repository.BranchRepository;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchMapper branchMapper;

    private BranchService branchService;

    @BeforeEach
    void setUp() {
        branchService = new BranchService(
                branchRepository,
                branchMapper
        );
    }

    @Test
    void getBranch_returnsMappedBranch_whenBranchExists() {
        Branch branch = branchEntity();
        BranchResponse expected = branchResponse();

        when(branchRepository.findById(1L))
                .thenReturn(Optional.of(branch));

        when(branchMapper.toResponse(branch))
                .thenReturn(expected);

        BranchResponse actual =
                branchService.getBranch(1L);

        assertThat(actual).isEqualTo(expected);

        verify(branchRepository).findById(1L);
        verify(branchMapper).toResponse(branch);
    }

    @Test
    void getBranch_throwsNotFound_whenBranchDoesNotExist() {
        when(branchRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> branchService.getBranch(999L)
        )
                .isInstanceOf(BranchNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createBranch_savesAndReturnsBranch_whenCodeIsUnique() {
        BranchCreateRequest request =
                createRequest("new001");

        Branch unsaved = branchEntity();
        Branch saved = branchEntity();
        BranchResponse expected = branchResponse();

        when(branchRepository.existsByBranchCode("NEW001"))
                .thenReturn(false);

        when(branchMapper.toEntity(request))
                .thenReturn(unsaved);

        when(branchRepository.saveAndFlush(unsaved))
                .thenReturn(saved);

        when(branchMapper.toResponse(saved))
                .thenReturn(expected);

        BranchResponse actual =
                branchService.createBranch(request);

        assertThat(actual).isEqualTo(expected);

        verify(branchRepository)
                .existsByBranchCode("NEW001");

        verify(branchRepository)
                .saveAndFlush(unsaved);

        assertThat(unsaved.getUpdatedAt()).isNotNull();
    }

    @Test
    void createBranch_throwsConflict_whenCodeAlreadyExists() {
        BranchCreateRequest request =
                createRequest("kl001");

        when(branchRepository.existsByBranchCode("KL001"))
                .thenReturn(true);

        assertThatThrownBy(
                () -> branchService.createBranch(request)
        )
                .isInstanceOf(
                        DuplicateBranchCodeException.class
                )
                .hasMessageContaining("KL001");
    }

    @Test
    void updateBranch_updatesEditableFields_whenRequestIsValid() {
        Branch existing = branchEntity();
        BranchUpdateRequest request = updateRequest();
        BranchResponse expected = branchResponse();

        when(branchRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(
                branchRepository
                        .existsByBranchCodeAndIdNot(
                                "KL001",
                                1L
                        )
        ).thenReturn(false);

        when(branchRepository.saveAndFlush(existing))
                .thenReturn(existing);

        when(branchMapper.toResponse(existing))
                .thenReturn(expected);

        BranchResponse actual =
                branchService.updateBranch(1L, request);

        assertThat(actual).isEqualTo(expected);
        assertThat(existing.getUpdatedAt()).isNotNull();

        verify(branchMapper)
                .updateEntity(existing, request);

        verify(branchRepository)
                .saveAndFlush(existing);
    }

    @Test
    void getBranches_enforcesTenRecordsPerPage() {
        List<Branch> branches = List.of(
                branchEntity(),
                branchEntity()
        );

        PageImpl<Branch> repositoryPage =
                new PageImpl<>(branches);

        when(branchRepository.findAll(any(Pageable.class)))
                .thenReturn(repositoryPage);

        when(branchMapper.toResponse(any(Branch.class)))
                .thenReturn(branchResponse());

        PageResponse<BranchResponse> result =
                branchService.getBranches(0);

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(branchRepository)
                .findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void getBranches_rejectsNegativePageNumber() {
        assertThatThrownBy(
                () -> branchService.getBranches(-1)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Page number must be zero or greater."
                );
    }

    @Test
    void deleteBranch_deletesExistingBranch() {
        Branch branch = branchEntity();

        when(branchRepository.findById(1L))
                .thenReturn(Optional.of(branch));

        branchService.deleteBranch(1L);

        verify(branchRepository).delete(branch);
    }

    private BranchCreateRequest createRequest(
            String branchCode) {

        return new BranchCreateRequest(
                branchCode,
                "Test Branch",
                "1 Test Road",
                "Kuala Lumpur",
                "Malaysia",
                new BigDecimal("3.1390000"),
                new BigDecimal("101.6869000"),
                "03-11111111",
                true
        );
    }

    private BranchUpdateRequest updateRequest() {
        return new BranchUpdateRequest(
                "KL001",
                "Updated KL Branch",
                "Updated Address",
                "Kuala Lumpur",
                "Malaysia",
                new BigDecimal("3.1478000"),
                new BigDecimal("101.6953000"),
                "03-12345678",
                true
        );
    }

    private Branch branchEntity() {
        Branch branch = new Branch();

        branch.setBranchCode("KL001");
        branch.setBranchName(
                "Kuala Lumpur Headquarters"
        );
        branch.setAddress(
                "Menara Maybank, Jalan Tun Perak"
        );
        branch.setCity("Kuala Lumpur");
        branch.setCountry("Malaysia");
        branch.setLatitude(
                new BigDecimal("3.1478000")
        );
        branch.setLongitude(
                new BigDecimal("101.6953000")
        );
        branch.setPhoneNumber("03-12345678");
        branch.setActive(true);
        branch.setUpdatedAt(LocalDateTime.now());

        return branch;
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
}