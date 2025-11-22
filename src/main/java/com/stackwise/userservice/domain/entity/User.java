package com.stackwise.userservice.domain.entity;

import com.stackwise.userservice.domain.entity.valueObject.Email;
import com.stackwise.userservice.domain.entity.valueObject.Mobile;

import java.util.UUID;

/**
 * User Aggregate Root.
 *
 * Design Note on Password Storage:
 * - Password VO is used for VALIDATION only (when creating/updating)
 * - The HASHED password is stored as a plain String field
 * - This separates validation (domain concern) from storage (infrastructure concern)
 * - When loaded from DB, we never reconstruct the Password VO (it's already hashed)
 */
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private Email email;
    private Mobile mobile;
    private String hashedPassword; // Store hashed password as String, NOT Password VO
    private boolean isBlocked;
    
    /**
     * Constructor for creating/loading User.
     * @param hashedPassword - already hashed password string
     */
    public User(UUID id, String firstName, String lastName, Email email, Mobile mobile, String hashedPassword) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.mobile = mobile;
        this.hashedPassword = hashedPassword;
        this.isBlocked = false;
    }

    // Getters
    public UUID getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public Email getEmail() {
        return this.email;
    }

    public Mobile getMobile() {
        return this.mobile;
    }

    /**
     * Gets the hashed password.
     * This is ALWAYS a hashed password, never plain text.
     */
    public String getHashedPassword() {
        return this.hashedPassword;
    }
    
    public boolean isBlocked() {
        return this.isBlocked;
    }

    // Business methods
    public void blockUser() {
        this.isBlocked = true;
    }

    public void unblockUser() {
        this.isBlocked = false;
    }

    /**
     * Updates the user's password.
     * NOTE: The newHashedPassword parameter should be the HASHED password.
     * Validation and hashing happen at the application layer before calling this.
     */
    public void changePassword(String newHashedPassword) {
        if (newHashedPassword == null || newHashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.hashedPassword = newHashedPassword;
    }

    public void updateEmail(String newEmail) {
        // Value object validation will be handled in Email constructor
        this.email = new Email(newEmail);
    }

    public void updateMobile(String newMobile, String countryCode) {
        this.mobile = new Mobile(newMobile, countryCode);
    }

    public static User createUser(String firstName, String lastName, String email, String mobile, String countryCode, String hashedPassword) {
        return new User(
            UUID.randomUUID(),
            firstName,
            lastName,
            new Email(email),
            new Mobile(mobile, countryCode),
            hashedPassword
        );
    }


}
