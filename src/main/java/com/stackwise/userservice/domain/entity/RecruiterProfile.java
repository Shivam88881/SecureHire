package com.stackwise.userservice.domain.entity;

import java.time.LocalDateTime;
import java.util.*;

/**
 * RecruiterProfile Entity - Specific data for recruiters.
 *
 * Contains all recruiter-specific information:
 * - Company details
 * - Verification status and documents
 * - Recruiter credentials
 * - Company information
 */
public class RecruiterProfile {
    private final UUID id;
    private final UUID userProfileId; // Reference to parent UserProfile

    // Company Information
    private String companyName;
    private String companyWebsite;
    private String companySize; // STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE
    private String industry;
    private String companyDescription;
    private String companyLogoUrl;

    // Recruiter Specific
    private String designation;
    private String department;

    // Verification
    private VerificationStatus verificationStatus;
    private String verificationDocumentUrl; // URL to uploaded document
    private String verificationDocumentType; // COMPANY_ID, BUSINESS_LICENSE, etc.
    private LocalDateTime verificationRequestedAt;
    private LocalDateTime verifiedAt;
    private String verificationRejectionReason;

    // Metadata
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecruiterProfile(UUID id, UUID userProfileId) {
        if (userProfileId == null) {
            throw new IllegalArgumentException("UserProfile ID cannot be null");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.userProfileId = userProfileId;
        this.verificationStatus = VerificationStatus.UNVERIFIED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserProfileId() {
        return userProfileId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public String getCompanySize() {
        return companySize;
    }

    public String getIndustry() {
        return industry;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDepartment() {
        return department;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public String getVerificationDocumentUrl() {
        return verificationDocumentUrl;
    }

    public String getVerificationDocumentType() {
        return verificationDocumentType;
    }

    public LocalDateTime getVerificationRequestedAt() {
        return verificationRequestedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public String getVerificationRejectionReason() {
        return verificationRejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Business methods - Company Information
    public void updateCompanyInfo(
            String companyName,
            String companyWebsite,
            String companySize,
            String industry,
            String companyDescription) {

        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be null or empty");
        }

        this.companyName = companyName.trim();
        this.companyWebsite = companyWebsite != null ? companyWebsite.trim() : null;
        this.companySize = companySize;
        this.industry = industry;
        this.companyDescription = companyDescription != null ? companyDescription.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCompanyLogo(String logoUrl) {
        this.companyLogoUrl = logoUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRecruiterDetails(String designation, String department) {
        this.designation = designation != null ? designation.trim() : null;
        this.department = department != null ? department.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods - Verification
    public void requestVerification(String documentUrl, String documentType) {
        if (documentUrl == null || documentUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification document URL cannot be null or empty");
        }
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Document type cannot be null or empty");
        }

        if (this.verificationStatus == VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Recruiter is already verified");
        }

        this.verificationDocumentUrl = documentUrl.trim();
        this.verificationDocumentType = documentType.trim();
        this.verificationStatus = VerificationStatus.PENDING;
        this.verificationRequestedAt = LocalDateTime.now();
        this.verificationRejectionReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void approveVerification() {
        if (this.verificationStatus != VerificationStatus.PENDING) {
            throw new IllegalStateException(
                "Can only approve verification for pending requests. Current status: " + this.verificationStatus
            );
        }

        this.verificationStatus = VerificationStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
        this.verificationRejectionReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void rejectVerification(String reason) {
        if (this.verificationStatus != VerificationStatus.PENDING) {
            throw new IllegalStateException(
                "Can only reject verification for pending requests. Current status: " + this.verificationStatus
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason cannot be null or empty");
        }

        this.verificationStatus = VerificationStatus.REJECTED;
        this.verificationRejectionReason = reason.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeVerification(String reason) {
        if (this.verificationStatus != VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Can only revoke verification for verified recruiters");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Revocation reason cannot be null or empty");
        }

        this.verificationStatus = VerificationStatus.REVOKED;
        this.verificationRejectionReason = reason.trim();
        this.verifiedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isVerified() {
        return this.verificationStatus == VerificationStatus.VERIFIED;
    }

    public boolean canPostJobs() {
        // Business rule: Only verified recruiters can post jobs
        return isVerified();
    }

    // Enum for verification status
    public enum VerificationStatus {
        UNVERIFIED("Unverified - No verification requested"),
        PENDING("Pending - Verification under review"),
        VERIFIED("Verified - Recruiter is verified"),
        REJECTED("Rejected - Verification was rejected"),
        REVOKED("Revoked - Verification was revoked");

        private final String description;

        VerificationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Factory method
    public static RecruiterProfile createFor(UUID userProfileId) {
        return new RecruiterProfile(UUID.randomUUID(), userProfileId);
    }
}

