package com.logan.branchapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.logan.branchapi.entity.Branch;

@DataJpaTest
@ActiveProfiles("integration")
class BranchRepositoryIntegrationTest {

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void saveAndReadBranch_usingRealSqlServer() {
        Branch branch = new Branch();

        branch.setBranchCode("INT001");
        branch.setBranchName("Integration Test Branch");
        branch.setAddress("1 Integration Test Road");
        branch.setCity("Kuala Lumpur");
        branch.setCountry("Malaysia");
        branch.setLatitude(
                new BigDecimal("3.1390000")
        );
        branch.setLongitude(
                new BigDecimal("101.6869000")
        );
        branch.setPhoneNumber("03-10000000");
        branch.setActive(true);
        branch.setUpdatedAt(LocalDateTime.now());

        Branch saved =
                branchRepository.saveAndFlush(branch);

        assertThat(saved.getId()).isNotNull();

        Branch found = branchRepository
                .findById(saved.getId())
                .orElseThrow();

        assertThat(found.getBranchCode())
                .isEqualTo("INT001");

        assertThat(found.getBranchName())
                .isEqualTo("Integration Test Branch");
    }
}