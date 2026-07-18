package com.logan.branchapi.exception;

public class BranchNotFoundException extends RuntimeException {

    public BranchNotFoundException(Long id) {
        super("Branch with ID " + id + " was not found.");
    }

    private BranchNotFoundException(String message) {
        super(message);
    }

    public static BranchNotFoundException forCode(
            String branchCode) {

        return new BranchNotFoundException(
                "Branch with code '" +
                branchCode +
                "' was not found."
        );
    }
}