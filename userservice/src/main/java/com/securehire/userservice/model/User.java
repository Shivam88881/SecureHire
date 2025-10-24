package com.securehire.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;
    private String email;
    private Boolean emailVerified;
    private String password;
    private String userType;
    private String accountStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private OffsetDateTime lastLoginAt;
    private Integer failedLoginAttempts;
    private OffsetDateTime accountLockedUntil;
    private OffsetDateTime passwordChangedAt;
    private Integer version;
    
    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private RecruiterDetail recruiterDetails;
}
