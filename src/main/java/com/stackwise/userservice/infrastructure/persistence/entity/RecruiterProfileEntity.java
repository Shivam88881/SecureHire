package com.stackwise.userservice.infrastructure.persistence.entity;


import com.stackwise.userservice.domain.entity.RecruiterProfile;
import com.stackwise.userservice.domain.valueObject.VerificationDocument;
import com.stackwise.userservice.infrastructure.persistence.converter.VerificationDocumentJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "recruiter_profiles")
@Entity
public class RecruiterProfileEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_profile_id", nullable = false, unique = true)
    private UUID userProfileId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_website")
    private URI companyWebsite;

    @Column(name = "company_size")
    private RecruiterProfile.CompanySize companySize;

    @Column(name = "industry")
    private String industry;

    @Column(name = "company_description", length = 2000)
    private String companyDescription;

    @Column(name = "company_logo_url")
    private URI companyLogoUrl;

    // Recruiter Specific
    @Column(name = "designation")
    private String designation;

    @Column(name = "department")
    private String department;

    // Verification
    @Column(name = "verification_status")
    private RecruiterProfile.VerificationStatus verificationStatus;

    @Column(name = "verification_documents", columnDefinition = "TEXT")
    @Convert(converter = VerificationDocumentJsonConverter.class)
    private List<VerificationDocument> verificationDocuments;

    @Column(name = "verification_requested_at")
    private LocalDateTime verificationRequestedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_rejection_reason", length = 1000)
    private String verificationRejectionReason;

    // Metadata
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
