package com.stackwise.userservice.infrastructure.persistence.entity;

import com.stackwise.userservice.domain.valueObject.*;
import com.stackwise.userservice.infrastructure.persistence.converter.AddressJsonConverter;
import com.stackwise.userservice.infrastructure.persistence.converter.SocialLinksJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for UserProfile - Persistence Layer
 * Maps domain UserProfile to database table
 *
 * Note: Address and SocialLinks are stored as JSON (TEXT) in PostgreSQL
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class UserProfileEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "auth_user_id", nullable = false, unique = true, length = 100)
    private UUID authUserId;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private URI avatarUrl;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus.Status accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role.RoleType role;

    @Column(name = "address", columnDefinition = "TEXT")
    @Convert(converter = AddressJsonConverter.class)
    private Address address;

    @Column(name = "social_links", columnDefinition = "TEXT")
    @Convert(converter = SocialLinksJsonConverter.class)
    private List<SocialLink> socialLinks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    // Default constructor for JPA
    protected UserProfileEntity() {
    }

    // Constructor
    public UserProfileEntity(UUID id, String firstName, String lastName, UUID authUserId, URI avatarUrl,
                             String email, String mobile, String countryCode,
                             boolean emailVerified, AccountStatus.Status accountStatus,
                             Role.RoleType role, Address address, List<SocialLink> socialLinks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authUserId  = authUserId;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.emailVerified = emailVerified;
        this.accountStatus = accountStatus;
        this.role = role;
        this.address = address;
        this.socialLinks = socialLinks;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}

