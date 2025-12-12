package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for Address
 * Used for JSON serialization/deserialization in the infrastructure layer
 * Stored as JSONB in PostgreSQL
 * <p>
 * This DTO isolates Jackson framework dependencies from the domain layer
 *
 * Note: @JsonProperty on record components eliminates need for explicit constructor
 */
public record AddressDTO(
        @JsonProperty("street") String street,
        @JsonProperty("city") String city,
        @JsonProperty("state") String state,
        @JsonProperty("zipCode") String zipCode,
        @JsonProperty("country") String country) {
}

