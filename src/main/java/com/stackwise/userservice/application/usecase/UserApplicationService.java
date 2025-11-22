package com.stackwise.userservice.application.usecase;

import com.stackwise.userservice.domain.entity.User;
import com.stackwise.userservice.domain.entity.valueObject.Email;
import com.stackwise.userservice.domain.entity.valueObject.Mobile;
import com.stackwise.userservice.domain.entity.valueObject.Password;
import com.stackwise.userservice.domain.service.PasswordEncoderPort;

import java.util.UUID;

/**
 * Application Service (Use Case) for User operations.
 * Orchestrates the flow of data and coordinates domain objects.
 *
 * IMPORTANT: This class depends on PasswordEncoderPort (interface),
 * NOT on the concrete PasswordEncoder implementation.
 *
 * Dependency Direction:
 * Application → Domain (Port/Interface)
 * Infrastructure → Domain (implements Port)
 *
 * This follows Dependency Inversion Principle and Clean Architecture.
 */
public class UserApplicationService {

    private final PasswordEncoderPort passwordEncoder;
    // private final UserRepository userRepository; // Will be added later
    // private final EventPublisher eventPublisher; // Will be added later

    public UserApplicationService(PasswordEncoderPort passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Use Case: Register a new user
     *
     * Flow:
     * 1. Validate password format using Password VO
     * 2. Hash the validated password using infrastructure service
     * 3. Create User entity with hashed password
     * 4. Save to repository
     */
    public User registerUser(String firstName, String lastName, String emailAddress,
                            String phoneNumber, String countryCode, String plainPassword) {

        // Step 1: Check business rules (e.g., email uniqueness)
        // if (userRepository.existsByEmail(emailAddress)) {
        //     throw new DuplicateEmailException("Email already exists: " + emailAddress);
        // }

        // Step 2: Validate password format using Password VO (business rule)
        Password passwordVO = new Password(plainPassword); // Validates format

        // Step 3: Hash the validated password (infrastructure concern)
        String hashedPassword = passwordEncoder.encode(passwordVO.getValue());

        // Step 4: Create value objects
        Email email = new Email(emailAddress);
        Mobile mobile = new Mobile(phoneNumber, countryCode);

        // Step 5: Create the User entity with HASHED password
        User newUser = User.createUser(firstName,lastName,emailAddress,mobile.getMobileNumber(),countryCode,hashedPassword);

        // Step 6: Persist to database
        // User savedUser = userRepository.save(newUser);

        // Step 7: Publish domain event
        // eventPublisher.publish(new UserRegisteredEvent(savedUser));

        return newUser;
    }

    /**
     * Use Case: Authenticate user (login)
     *
     * Flow:
     * 1. Load user from repository
     * 2. Get hashed password from user
     * 3. Verify plain text password matches hash
     */
    public boolean authenticateUser(String emailAddress, String plainPassword) {
        // Step 1: Find user by email
        // User user = userRepository.findByEmail(emailAddress)
        //     .orElseThrow(() -> new UserNotFoundException(emailAddress));

        // Step 2: Get stored hashed password
        // String storedHashedPassword = user.getHashedPassword();

        // Step 3: Verify password using infrastructure service
        // return passwordEncoder.matches(plainPassword, storedHashedPassword);

        return false; // Placeholder
    }

    /**
     * Use Case: Change user password
     *
     * Flow:
     * 1. Load user
     * 2. Verify current password
     * 3. Validate new password format using Password VO
     * 4. Hash new password
     * 5. Update user with hashed password
     */
    public void changePassword(String userId, String currentPassword, String newPassword) {
        // Step 1: Load user
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));

        // Step 2: Verify current password
        // if (!passwordEncoder.matches(currentPassword, user.getHashedPassword())) {
        //     throw new InvalidPasswordException("Current password is incorrect");
        // }

        // Step 3: Validate new password format using Password VO (business rule)
        // Password newPasswordVO = new Password(newPassword); // Validates format

        // Step 4: Hash the new password (infrastructure concern)
        // String newHashedPassword = passwordEncoder.encode(newPasswordVO.getValue());

        // Step 5: Update user entity
        // user.changePassword(newHashedPassword);

        // Step 6: Save
        // userRepository.save(user);

        // Step 7: Publish event
        // eventPublisher.publish(new PasswordChangedEvent(user));
    }

    /**
     * Use Case: Block a user account
     */
    public void blockUser(String userId) {
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        //
        // user.blockUser();
        // userRepository.save(user);
        //
        // eventPublisher.publish(new UserBlockedEvent(user));
    }
}

