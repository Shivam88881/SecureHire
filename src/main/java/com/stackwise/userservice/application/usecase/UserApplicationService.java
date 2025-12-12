package com.stackwise.userservice.application.usecase;

import com.stackwise.userservice.domain.entity.UserProfile;
import com.stackwise.userservice.domain.valueObject.Email;
import com.stackwise.userservice.domain.valueObject.Mobile;
import com.stackwise.userservice.domain.valueObject.Role;

import java.net.URI;
import java.util.UUID;

/**
 * Application Service (Use Case) for User Profile Management.
 * Orchestrates the flow of data and coordinates domain objects.
 *
 * Separation of Concerns:
 * - This service handles ONLY user profile and business data management
 * - Authentication concerns (password, login, tokens) are handled by AuthService
 * - This follows microservices best practices: each service has a single responsibility
 *
 * Clean Architecture:
 * Application → Domain (orchestrates business logic)
 * Infrastructure → Application (provides technical capabilities)
 */
public class UserApplicationService {

    // private final UserRepository userRepository; // Will be added later
    // private final EventPublisher eventPublisher; // Will be added later

    public UserApplicationService() {
        // No password encoder needed - that's AuthService's responsibility
    }

    /**
     * Use Case: Create a new user profile
     *
     * Flow:
     * 1. Validate email uniqueness (business rule)
     * 2. Create value objects
     * 3. Create UserProfile entity
     * 4. Save to repository
     * 5. Publish domain event
     *
     * Note: Password registration happens in AuthService separately
     * 
     * @param authUserId The user ID from AuthService (after authentication is set up)
     */
    public UserProfile createUserProfile(UUID authUserId, String firstName, String lastName, String emailAddress, URI avatarUrl,
                                  String phoneNumber, String countryCode, String role) {

        // no need to check if user pre-exists here, AuthService handles that

        Email email = new Email(emailAddress);
        Mobile mobile = new Mobile(phoneNumber, countryCode);
        Role userRole = new Role(role);

        // Use factory methods based on role
        UserProfile newUserProfile;
        if (userRole.getRoleType() == Role.RoleType.JOBSEEKER) {
            newUserProfile = UserProfile.createJobSeeker(firstName, lastName, authUserId, avatarUrl, emailAddress, phoneNumber, countryCode);
        } else if (userRole.getRoleType() == Role.RoleType.RECRUITER) {
            newUserProfile = UserProfile.createRecruiter(firstName, lastName, authUserId, avatarUrl, emailAddress, phoneNumber, countryCode);
        } else if (userRole.getRoleType() == Role.RoleType.ADMIN) {
            newUserProfile = UserProfile.createAdmin(firstName, lastName, authUserId, avatarUrl, emailAddress, phoneNumber, countryCode);
        } else {
            throw new IllegalArgumentException("Invalid role type: " + role);
        }

        // Persist to database
        // UserProfile savedUserProfile = userRepository.save(newUserProfile);

        // Publish domain event
        // eventPublisher.publish(new UserProfileCreatedEvent(savedUserProfile));

        return newUserProfile;
    }

    /**
     * Use Case: Update user email
     */
    public void updateUserEmail(String userId, String newEmail) {
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        //
        // user.updateEmail(newEmail);
        // userRepository.save(user);
        //
        // eventPublisher.publish(new EmailUpdatedEvent(user));
    }

    /**
     * Use Case: Update user mobile
     */
    public void updateUserMobile(String userId, String newMobile, String countryCode) {
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        //
        // user.updateMobile(newMobile, countryCode);
        // userRepository.save(user);
        //
        // eventPublisher.publish(new MobileUpdatedEvent(user));
    }

    /**
     * Use Case: Verify user email
     */
    public void verifyEmail(String userId) {
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        //
        // user.verifyEmail();
        // userRepository.save(user);
        //
        // eventPublisher.publish(new EmailVerifiedEvent(user));
    }

    /**
     * Use Case: Change account status (ACTIVE, INACTIVE, SUSPENDED, DELETED)
     */
    public void changeAccountStatus(String userId, String newStatus) {
        // User user = userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        //
        // user.changeAccountStatus(newStatus);
        // userRepository.save(user);
        //
        // eventPublisher.publish(new AccountStatusChangedEvent(user, newStatus));
    }

    /**
     * Use Case: Get user profile by ID
     */
    public UserProfile getUserProfile(String userId) {
        // return userRepository.findById(UUID.fromString(userId))
        //     .orElseThrow(() -> new UserNotFoundException(userId));
        return null; // Placeholder
    }

    /**
     * Use Case: Get user profile by email
     */
    public UserProfile getUserProfileByEmail(String email) {
        // return userRepository.findByEmail(email)
        //     .orElseThrow(() -> new UserNotFoundException(email));
        return null; // Placeholder
    }
}

