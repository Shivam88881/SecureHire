package com.stackwise.userservice.infrastructure.security;

import com.stackwise.userservice.domain.entity.valueObject.Password;
import com.stackwise.userservice.domain.service.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Infrastructure Adapter implementing PasswordEncoderPort.
 *
 * This is the "Adapter" in Hexagonal Architecture.
 * It implements the Port (interface) defined in the domain layer.
 *
 * Dependency Direction:
 * Infrastructure → Domain (implements domain interface)
 * NOT the other way around!
 */
@Component
public class PasswordEncoder implements PasswordEncoderPort {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashes a plain text password.
     *
     * @param password the Password value object containing plain text
     * @return hashed password string
     */
    public String encode(Password password) {
        return bCryptPasswordEncoder.encode(password.getValue());
    }

    /**
     * Hashes a plain text password string.
     *
     * @param plainPassword the plain text password
     * @return hashed password string
     */
    public String encode(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    /**
     * Verifies if a plain text password matches a hashed password.
     *
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public boolean matches(String plainPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Verifies if a Password value object matches a hashed password.
     *
     * @param password the Password value object containing plain text
     * @param hashedPassword the hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public boolean matches(Password password, String hashedPassword) {
        return bCryptPasswordEncoder.matches(password.getValue(), hashedPassword);
    }
}

