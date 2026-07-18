package com.logan.branchapi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled(
        "Integration test requires local MSSQL TESTDB credentials"
)
@SpringBootTest
class BranchApiApplicationTests {

    @Test
    void contextLoads() {
    }
}