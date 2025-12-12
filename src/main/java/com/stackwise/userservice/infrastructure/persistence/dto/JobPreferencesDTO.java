package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stackwise.userservice.domain.valueObject.JobPreferences;
import com.stackwise.userservice.domain.valueObject.Money;

import java.util.List;

/**
 * Data Transfer Object for Job Preferences
 * Used for JSON serialization/deserialization in the infrastructure layer
 * Stored as JSONB in PostgreSQL
 * <p>
 * This DTO isolates Jackson framework dependencies from the domain layer
 *
 * Note: @JsonProperty on record components eliminates need for explicit constructor
 */
public record JobPreferencesDTO(
        @JsonProperty("desiredRoles") List<String> desiredRoles,
        @JsonProperty("preferredLocations") List<String> preferredLocations,
        @JsonProperty("employmentType") JobPreferences.EmploymentType employmentType,
        @JsonProperty("workMode") JobPreferences.WorkMode workMode,
        @JsonProperty("expectedSalary") Money expectedSalary,
        @JsonProperty("noticePeriod") JobPreferences.NoticePeriod noticePeriod) {
}