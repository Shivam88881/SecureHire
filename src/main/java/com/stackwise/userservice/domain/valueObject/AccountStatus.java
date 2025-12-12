package com.stackwise.userservice.domain.valueObject;

public record AccountStatus(Status status) {
    public enum Status {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        DELETED
    }

    public AccountStatus {
        if (status == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }
    }

    public Status getStatus() {
        return status;
    }
}
