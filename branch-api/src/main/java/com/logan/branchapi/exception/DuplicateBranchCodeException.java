package com.logan.branchapi.exception;

public class DuplicateBranchCodeException
        extends RuntimeException {

    public DuplicateBranchCodeException(String branchCode) {
        super(
                "Branch code '" +
                branchCode +
                "' already exists."
        );
    }
}