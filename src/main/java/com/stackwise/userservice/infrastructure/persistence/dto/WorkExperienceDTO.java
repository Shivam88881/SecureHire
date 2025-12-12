package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Work Experience
 * Used for JSON serialization/deserialization in the infrastructure layer
 * Stored as JSONB in PostgreSQL
 * Uses ISO-8601 date format for efficient querying
 * <p>
 * This DTO isolates Jackson framework dependencies from the domain layer
 *
 * Note: @JsonProperty on record components eliminates need for explicit constructor
 */
public record WorkExperienceDTO(
        @JsonProperty("company") String company,
        @JsonProperty("position") String position,
        @JsonProperty("description") String description,
        @JsonProperty("startDate") LocalDateTime startDate,
        @JsonProperty("endDate") LocalDateTime endDate,
        @JsonProperty("isCurrent") boolean isCurrent) {
}
