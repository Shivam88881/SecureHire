package com.stackwise.userservice.domain.entity.valueObject;

import java.util.Objects;

/**
 * Value Object representing a validated plain text password.
 *
 * IMPORTANT: This class represents ONLY plain text passwords with business rule validation.
 * It should NEVER contain hashed passwords.
 *
 * For persistence:
 * - Use this VO for validation when creating/updating passwords
 * - Store the HASHED version as a simple String in the entity (not wrapped in Password VO)
 * - This maintains clear separation: VO = validation, String = storage
 */
public class Password {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$#!%*?&]{8,14}$";

    private final String value;

    /**
     * Creates a Password value object with a plain text password.
     * This constructor ALWAYS validates the password against business rules.
     *
     * Use this when:
     * - User registers (new password)
     * - User changes password
     * - Any time you need to validate password format
     *
     * @param plainPassword the plain text password to validate
     * @throws IllegalArgumentException if password format is invalid
     */
    public Password(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (!isValidFormat(plainPassword)) {
            throw new IllegalArgumentException(
                "Password must be 8-14 characters long and contain at least one uppercase letter, " +
                "one lowercase letter, one digit, and one special character (@$#!%*?&)"
            );
        }
        this.value = plainPassword;
    }

    private boolean isValidFormat(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    /**
     * Gets the validated plain text password.
     * This should be hashed by infrastructure layer before persistence.
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        // Never expose the actual password value in logs
        return "[PROTECTED]";
    }
}



