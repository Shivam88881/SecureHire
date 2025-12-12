package com.stackwise.userservice.domain.entity;

import com.stackwise.userservice.domain.valueObject.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

/**
 * UserProfile Aggregate Root - Core user profile information.
 *
 * This is the main entity that represents a user in the system.
 * It contains basic profile information common to all users.
 *
 * Separation of Concerns:
 * - This service manages ONLY user profile and business data
 * - Authentication concerns (password, login, tokens) are handled by AuthService
 * - Role-specific data is stored in JobSeekerProfile or RecruiterProfile
 *
 * Clean Architecture:
 * - No framework dependencies
 * - Pure domain logic
 * - Immutable where possible
 */
public class UserProfile {
    // Core Identity
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final UUID authUserId; // FK to AuthService User
    private URI avatarUrl;

    // Contact Information
    private Email email;
    private Mobile mobile;
    private Address address;

    // Social Links
    private List<SocialLink> socialLinks;

    // Status & Verification
    private boolean emailVerified;
    private AccountStatus accountStatus;

    // Role
    private Role role;

    // Metadata
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Resume> resumes;

    /**
     * Constructor for creating/loading UserProfile.
     */
    public UserProfile(
            UUID id,
            String firstName,
            String lastName,
            UUID authUserId,
            URI avatarUrl,
            Email email,
            Mobile mobile,
            Role role) {

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if(authUserId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (mobile == null) {
            throw new IllegalArgumentException("Mobile cannot be null");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.authUserId = authUserId;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.emailVerified = false;
        this.accountStatus = new AccountStatus(AccountStatus.Status.ACTIVE);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== Getters ====================

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UUID getAuthUserId() {
        return authUserId;
    }

    public URI getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Email getEmail() {
        return email;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public Address getAddress() {
        return address;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks != null ?
            Collections.unmodifiableList(socialLinks) :
            Collections.emptyList();
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ==================== Business Methods ====================

    // Email Management
    public void updateEmail(String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = new Email(newEmail);
        this.emailVerified = false; // Reset verification when email changes
        this.updatedAt = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unverifyEmail() {
        this.emailVerified = false;
        this.updatedAt = LocalDateTime.now();
    }

    // Mobile Management
    public void updateMobile(String newMobile, String countryCode) {
        if (newMobile == null || newMobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile cannot be null or empty");
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }
        this.mobile = new Mobile(newMobile, countryCode);
        this.updatedAt = LocalDateTime.now();
    }

    // Address Management
    public void updateAddress(Address newAddress) {
        this.address = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    // Social Links Management
    public void updateSocialLinks(List<SocialLink> newSocialLinks) {
        if (newSocialLinks == null) {
            this.socialLinks = null;
        } else {
            this.socialLinks = new ArrayList<>(newSocialLinks);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addSocialLink(String platform, String url) {
        SocialLink newLink = new SocialLink(platform, url);

        if (this.socialLinks == null) {
            this.socialLinks = new ArrayList<>();
        } else {
            // Remove existing link for the same platform if exists
            this.socialLinks.removeIf(link -> link.platform().equalsIgnoreCase(platform));
        }
        this.socialLinks.add(newLink);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeSocialLink(String platform) {
        if (this.socialLinks != null) {
            this.socialLinks.removeIf(link -> link.platform().equalsIgnoreCase(platform));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public SocialLink getSocialLinkByPlatform(String platform) {
        if (this.socialLinks == null) {
            return null;
        }
        return this.socialLinks.stream()
            .filter(link -> link.platform().equalsIgnoreCase(platform))
            .findFirst()
            .orElse(null);
    }

    // Account Status Management
    public void changeAccountStatus(AccountStatus.Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }

        AccountStatus.Status currentStatus = this.accountStatus.getStatus();

        // Define valid transitions
        boolean isValidTransition = switch (currentStatus) {
            case ACTIVE -> newStatus == AccountStatus.Status.INACTIVE
                    || newStatus == AccountStatus.Status.SUSPENDED
                    || newStatus == AccountStatus.Status.DELETED;
            case INACTIVE, SUSPENDED -> newStatus == AccountStatus.Status.ACTIVE
                    || newStatus == AccountStatus.Status.DELETED;
            case DELETED -> false; // Can't transition from DELETED
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", currentStatus, newStatus)
            );
        }

        // Additional business rules
        if (newStatus == AccountStatus.Status.DELETED
                && this.role.getRoleType() == Role.RoleType.SUPERADMIN) {
            throw new IllegalStateException("Cannot delete a SUPERADMIN account");
        }

        this.accountStatus = new AccountStatus(newStatus);
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteProfile() {
        if (this.role.getRoleType() == Role.RoleType.SUPERADMIN) {
            throw new IllegalArgumentException("Cannot delete a SUPERADMIN user.");
        }
        this.accountStatus = new AccountStatus(AccountStatus.Status.DELETED);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.accountStatus.getStatus() == AccountStatus.Status.ACTIVE;
    }

    public boolean isDeleted() {
        return this.accountStatus.getStatus() == AccountStatus.Status.DELETED;
    }

    // Role checks
    public boolean isJobSeeker() {
        return this.role.getRoleType() == Role.RoleType.JOBSEEKER;
    }

    public boolean isRecruiter() {
        return this.role.getRoleType() == Role.RoleType.RECRUITER;
    }

    public boolean isAdmin() {
        return this.role.getRoleType() == Role.RoleType.ADMIN
            || this.role.getRoleType() == Role.RoleType.SUPERADMIN;
    }

    // ==================== Factory Methods ====================

    /**
     * Factory method to create a new UserProfile.
     * Note: Password/authentication setup happens in AuthService, not here.
     */
    public static UserProfile createJobSeeker(
            String firstName,
            String lastName,
            UUID authUserId,
            URI avatarUrl,
            String email,
            String mobile,
            String countryCode) {

        return new UserProfile(
            UUID.randomUUID(),
            firstName,
            lastName,
            authUserId,
            avatarUrl,
            new Email(email),
            new Mobile(mobile, countryCode),
            new Role(Role.RoleType.JOBSEEKER)
        );
    }

    public static UserProfile createRecruiter(
            String firstName,
            String lastName,
            UUID authUserId,
            URI avatarUrl,
            String email,
            String mobile,
            String countryCode) {

        return new UserProfile(
            UUID.randomUUID(),
            firstName,
            lastName,
            authUserId,
            avatarUrl,
            new Email(email),
            new Mobile(mobile, countryCode),
            new Role(Role.RoleType.RECRUITER)
        );
    }

    public static UserProfile createAdmin(
            String firstName,
            String lastName,
            UUID authUserId,
            URI avatarUrl,
            String email,
            String mobile,
            String countryCode) {

        return new UserProfile(
            UUID.randomUUID(),
            firstName,
            lastName,
            authUserId,
            avatarUrl,
            new Email(email),
            new Mobile(mobile, countryCode),
            new Role(Role.RoleType.ADMIN)
        );
    }

    // ==================== Infrastructure Support ====================
    // These setters are for infrastructure layer use only (persistence)
    // Not part of business logic

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setSocialLinks(List<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void addSocialLinks(SocialLink socialLink) {
        this.socialLinks.add(socialLink);
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setAvatarUrl(URI avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
