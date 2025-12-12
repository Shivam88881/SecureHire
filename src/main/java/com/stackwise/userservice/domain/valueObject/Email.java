package com.stackwise.userservice.domain.valueObject;

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
public record Email(String emailAddress) {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Compact constructor with validation and normalization.
     * Validates email format and normalizes to lowercase.
     *
     * @throws IllegalArgumentException if email format is invalid
     */
    public Email {
        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        // Normalize to lowercase
        emailAddress = emailAddress.trim().toLowerCase();

        if (!isValidFormat(emailAddress)) {
            throw new IllegalArgumentException(
                String.format("Invalid email format: '%s'. Expected format: user@domain.tld", emailAddress)
            );
        }
    }

    /**
     * Validates email format according to business rules.
     */
    private static boolean isValidFormat(String value) {
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
        return domainPart.length() <= 255 && domainPart.contains(".");
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
    public String toString() {
        return emailAddress;
    }
}
