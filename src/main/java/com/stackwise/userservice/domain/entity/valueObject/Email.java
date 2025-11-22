package com.stackwise.userservice.domain.entity.valueObject;

import java.util.Objects;

/**
 * Value Object representing an email address.
 * Immutable and validates email format.
 *
 * Business Rules (in this Value Object):
 * - Must follow valid email format (user@domain.tld)
 * - Cannot be null or empty
 * - Domain must have at least 2 characters
 *
 * Business Rules (NOT in this Value Object, belong elsewhere):
 * - Email uniqueness across users → Repository/Application Service
 * - Allowed email domains → Application Service or Domain Service
 * - Rate limiting email changes → Application Service
 */
public class Email {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private final String emailAddress;

    /**
     * Creates an Email value object.
     *
     * @param email the email address to validate
     * @throws IllegalArgumentException if email format is invalid
     */
    public Email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String trimmedEmail = email.trim().toLowerCase(); // Normalize to lowercase

        if (!isValidFormat(trimmedEmail)) {
            throw new IllegalArgumentException(
                String.format("Invalid email format: '%s'. Expected format: user@domain.tld", email)
            );
        }

        this.emailAddress = trimmedEmail;
    }

    private boolean isValidFormat(String value) {
        // Validate email format
        if (!value.matches(EMAIL_REGEX)) {
            return false;
        }

        // Additional validations
        String[] parts = value.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        // Local part (before @) validations
        if (localPart.isEmpty() || localPart.length() > 64) {
            return false;
        }

        // Domain part (after @) validations
        if (domainPart.isEmpty() || domainPart.length() > 255) {
            return false;
        }

        // Must have at least one dot in domain
        if (!domainPart.contains(".")) {
            return false;
        }

        return true;
    }

    /**
     * Gets the email address.
     * @return the email address in lowercase
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Gets the domain part of the email (after @).
     * Useful for domain-based business rules.
     */
    public String getDomain() {
        return emailAddress.substring(emailAddress.indexOf('@') + 1);
    }

    /**
     * Gets the local part of the email (before @).
     */
    public String getLocalPart() {
        return emailAddress.substring(0, emailAddress.indexOf('@'));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(emailAddress, email.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress);
    }

    @Override
    public String toString() {
        return emailAddress;
    }
}
