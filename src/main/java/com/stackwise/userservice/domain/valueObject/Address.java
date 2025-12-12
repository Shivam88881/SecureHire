package com.stackwise.userservice.domain.valueObject;

/**
 * Address Value Object - Immutable
 * Represents a physical address
 * <p>
 * Pure domain model without any framework dependencies.
 * Serialization concerns are handled by infrastructure layer DTOs.
 */
public record Address(String street, String city, String state, String zipCode, String country) {
    public Address {

        // Required fields
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be null or empty");
        }
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Zip code cannot be null or empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        // Street is optional (e.g., for PO boxes, some addresses)
        street = street != null ? street.trim() : null;
        city = city.trim();
        state = state.trim();
        zipCode = zipCode.trim();
        country = country.trim();
    }

}
