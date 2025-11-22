package com.stackwise.userservice.domain.service;

/**
 * Port (Interface) for password encoding operations.
 *
 * This interface belongs to the DOMAIN/APPLICATION layer.
 * The implementation belongs to the INFRASTRUCTURE layer.
 *
 * This follows Dependency Inversion Principle:
 * - High-level modules (Application) don't depend on low-level modules (Infrastructure)
 * - Both depend on abstractions (this interface)
 *
 * This is a "Port" in Hexagonal Architecture terminology.
 */
public interface PasswordEncoderPort {

    /**
     * Encodes (hashes) a plain text password.
     *
     * @param plainPassword the plain text password to encode
     * @return the encoded (hashed) password
     */
    String encode(String plainPassword);

    /**
     * Verifies if a plain text password matches an encoded password.
     *
     * @param plainPassword the plain text password to verify
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     */
    boolean matches(String plainPassword, String encodedPassword);
}

