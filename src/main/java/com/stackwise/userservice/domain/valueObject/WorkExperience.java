package com.stackwise.userservice.domain.valueObject;

import java.time.LocalDateTime;

/**
 * @param endDate null if current
 */
public record WorkExperience(String company, String position, String description, LocalDateTime startDate,
                             LocalDateTime endDate, boolean isCurrent) {
    public WorkExperience {

        if (company == null || company.trim().isEmpty()) {
            throw new IllegalArgumentException("Company cannot be null or empty");
        }
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be null or empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (!isCurrent && endDate == null) {
            throw new IllegalArgumentException("End date cannot be null for past positions");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        company = company.trim();
        position = position.trim();
        description = description != null ? description.trim() : null;
    }

}