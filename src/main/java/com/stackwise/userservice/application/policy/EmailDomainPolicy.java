package com.stackwise.userservice.application.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Application-level policy for email domain validation.
 * This is where business rules about WHICH domains are allowed belong.
 *
 * This is NOT in the Email value object because:
 * - It's application-specific policy, not email format validation
 * - It may change based on deployment/configuration
 * - It's external to the email structure itself
 */
public class EmailDomainPolicy {

    // Example: Allowed corporate domains
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(Arrays.asList(
        "company.com",
        "subsidiary.com",
        "partner.org"
    ));

    // Example: Blocked disposable email domains
    private static final Set<String> DISPOSABLE_DOMAINS = new HashSet<>(Arrays.asList(
        "tempmail.com",
        "throwaway.email",
        "guerrillamail.com",
        "10minutemail.com"
    ));

    /**
     * Checks if an email domain is allowed by application policy.
     *
     * @param domain the email domain (e.g., "company.com")
     * @return true if domain is allowed
     */
    public boolean isAllowedDomain(String domain) {
        // If no restrictions, all domains allowed
        if (ALLOWED_DOMAINS.isEmpty()) {
            return true;
        }

        return ALLOWED_DOMAINS.contains(domain.toLowerCase());
    }

    /**
     * Checks if an email domain is a known disposable email provider.
     *
     * @param domain the email domain
     * @return true if domain is disposable
     */
    public boolean isDisposableEmailDomain(String domain) {
        return DISPOSABLE_DOMAINS.contains(domain.toLowerCase());
    }

    /**
     * Checks if a domain is a free email provider.
     * Useful if business rule requires corporate emails only.
     */
    public boolean isFreeEmailProvider(String domain) {
        Set<String> freeProviders = new HashSet<>(Arrays.asList(
            "gmail.com",
            "yahoo.com",
            "hotmail.com",
            "outlook.com"
        ));

        return freeProviders.contains(domain.toLowerCase());
    }
}

