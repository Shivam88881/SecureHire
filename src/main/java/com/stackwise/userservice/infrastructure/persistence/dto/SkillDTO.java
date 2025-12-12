package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for Skill
 * Used for JSON serialization/deserialization in the infrastructure layer
 * Stored as JSONB in PostgreSQL
 * <p>
 * This DTO isolates Jackson framework dependencies from the domain layer
 *
 * Note: @JsonProperty on record components eliminates need for explicit constructor
 */
public record SkillDTO(
        @JsonProperty("name") String name,
        @JsonProperty("level") String level,
        @JsonProperty("yearsOfExperience") Integer yearsOfExperience) {
}
