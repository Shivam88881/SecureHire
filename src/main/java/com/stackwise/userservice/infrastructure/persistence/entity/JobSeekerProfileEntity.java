package com.stackwise.userservice.infrastructure.persistence.entity;

import com.stackwise.userservice.domain.entity.JobSeekerProfile;
import com.stackwise.userservice.domain.valueObject.Education;
import com.stackwise.userservice.domain.valueObject.JobPreferences;
import com.stackwise.userservice.domain.valueObject.Skill;
import com.stackwise.userservice.domain.valueObject.WorkExperience;
import com.stackwise.userservice.infrastructure.persistence.converter.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for JobSeekerProfile - Persistence Layer
 * Maps domain JobSeekerProfile to database table
 *
 * Uses JSONB for efficient storage of complex nested data:
 * - skills: List of skill objects with proficiency levels
 * - workExperiences: Array of work history entries
 * - educations: Array of education entries
 * - jobPreferences: Nested object with job search preferences
 */
@Entity
@Table(name = "job_seeker_profiles")
@Getter
@Setter
public class JobSeekerProfileEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_profile_id", nullable = false, unique = true)
    private UUID userProfileId;

    @Column(name = "resume_url", columnDefinition = "TEXT")
    private String resumeUrl;

    @Column(name = "professional_summary", columnDefinition = "TEXT")
    private String professionalSummary;

    // JSONB column - efficient for querying and indexing
    @Column(name = "skills", columnDefinition = "JSONB")
    @Convert(converter = SkillsJsonConverter.class)
    private List<Skill> skills;

    // JSONB column - stores work history as JSON array
    @Column(name = "work_experiences", columnDefinition = "JSONB")
    @Convert(converter = WorkExperiencesJsonConverter.class)
    private List<WorkExperience> workExperiences;

    // JSONB column - stores education history as JSON array
    @Column(name = "educations", columnDefinition = "JSONB")
    @Convert(converter = EducationsJsonConverter.class)
    private List<Education> educations;

    // JSONB column - stores job preferences as nested JSON object
    @Column(name = "job_preferences", columnDefinition = "JSONB")
    @Convert(converter = JobPreferencesJsonConverter.class)
    private JobPreferences jobPreferences;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA requires default constructor
    protected JobSeekerProfileEntity() {
    }

    public JobSeekerProfileEntity(
            UUID id,
            UUID userProfileId,
            String resumeUrl,
            String professionalSummary,
            List<Skill> skills,
            List<WorkExperience> workExperiences,
            List<Education> educations,
            JobPreferences jobPreferences,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.userProfileId = userProfileId;
        this.resumeUrl = resumeUrl;
        this.professionalSummary = professionalSummary;
        this.skills = skills;
        this.workExperiences = workExperiences;
        this.educations = educations;
        this.jobPreferences = jobPreferences;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Update timestamp before persisting
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

