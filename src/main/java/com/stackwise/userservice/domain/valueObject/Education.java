package com.stackwise.userservice.domain.valueObject;

import java.time.LocalDateTime;

/**
 * Education - Represents an education entry
 *
 * @param endDate null if ongoing
 * @param gpa     Optional
 */
public record Education(String institution, String degree, String fieldOfStudy, LocalDateTime startDate,
                        LocalDateTime endDate, Double gpa) {
    public Education {

        if (institution == null || institution.trim().isEmpty()) {
            throw new IllegalArgumentException("Institution cannot be null or empty");
        }
        if (degree == null || degree.trim().isEmpty()) {
            throw new IllegalArgumentException("Degree cannot be null or empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (gpa != null && (gpa < 0.0 || gpa > 10.0)) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 10.0");
        }

        institution = institution.trim();
        degree = degree.trim();
        fieldOfStudy = fieldOfStudy != null ? fieldOfStudy.trim() : null;
    }

}
