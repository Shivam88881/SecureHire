package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Education
 * Used for JSON serialization/deserialization in the infrastructure layer
 * Stored as JSONB in PostgreSQL
 * <p>
 * This DTO isolates Jackson framework dependencies from the domain layer
 *
 * Note: @JsonProperty on record components eliminates need for explicit constructor
 */
public record EducationDTO(
        @JsonProperty("institution") String institution,
        @JsonProperty("degree") String degree,
        @JsonProperty("fieldOfStudy") String fieldOfStudy,
        @JsonProperty("startDate") LocalDateTime startDate,
        @JsonProperty("endDate") LocalDateTime endDate,
        @JsonProperty("gpa") Double gpa) {
}
