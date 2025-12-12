package com.stackwise.userservice.domain.entity;

import com.stackwise.userservice.domain.valueObject.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * JobSeekerProfile Entity - Specific data for job seekers.
 *
 * Contains all job-seeker specific information:
 * - Resume/CV
 * - Skills
 * - Work experience
 * - Education
 * - Professional summary
 * - Job preferences
 */
public class JobSeekerProfile {
    private final UUID id;
    private final UUID userProfileId; // Reference to parent UserProfile
    private Resume resume;
    private String professionalSummary;
    private List<Skill> skills;
    private List<WorkExperience> workExperiences;
    private List<Education> educations;
    private JobPreferences jobPreferences;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public JobSeekerProfile(UUID id, UUID userProfileId) {
        if (userProfileId == null) {
            throw new IllegalArgumentException("UserProfile ID cannot be null");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.userProfileId = userProfileId;
        this.skills = new ArrayList<>();
        this.workExperiences = new ArrayList<>();
        this.educations = new ArrayList<>();
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

    public Resume getResume() {
        return resume;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public List<Skill> getSkills() {
        return skills != null ? Collections.unmodifiableList(skills) : Collections.emptyList();
    }

    public List<WorkExperience> getWorkExperiences() {
        return workExperiences != null ?
            Collections.unmodifiableList(workExperiences) : Collections.emptyList();
    }

    public List<Education> getEducations() {
        return educations != null ?
            Collections.unmodifiableList(educations) : Collections.emptyList();
    }

    public JobPreferences getJobPreferences() {
        return jobPreferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Business methods
    public void updateResume(Resume resume) {
        this.resume = resume;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfessionalSummary(String summary) {
        if (summary != null && summary.length() > 5000) {
            throw new IllegalArgumentException("Professional summary cannot exceed 5000 characters");
        }
        this.professionalSummary = summary != null ? summary.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void addSkill(Skill skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        // Remove if already exists (based on skill name)
        this.skills.removeIf(s -> s.name().equalsIgnoreCase(skill.name().trim().toLowerCase()));
        this.skills.add(skill);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeSkill(String skillName) {
        if (this.skills != null && skillName != null) {
            this.skills.removeIf(s -> s.name().equalsIgnoreCase(skillName.trim().toLowerCase()));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateSkills(List<Skill> skills) {
        if (skills == null) {
            this.skills = new ArrayList<>();
        } else {
            // Deduplicate by skill name
            this.skills = skills.stream()
                .filter(s -> s != null)
                .distinct()
                .toList();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addWorkExperience(WorkExperience experience) {
        if (experience == null) {
            throw new IllegalArgumentException("Work experience cannot be null");
        }
        if (this.workExperiences == null) {
            this.workExperiences = new ArrayList<>();
        }
        this.workExperiences.add(experience);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeWorkExperience(String company, String position) {
        if (this.workExperiences != null && company != null && position != null) {
            // Normalize once instead of on every iteration
            String normalizedCompany = company.trim().toLowerCase();
            String normalizedPosition = position.trim().toLowerCase();
            this.workExperiences.removeIf(exp ->
                exp.company().toLowerCase().equals(normalizedCompany) &&
                exp.position().toLowerCase().equals(normalizedPosition));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addEducation(Education education) {
        if (education == null) {
            throw new IllegalArgumentException("Education cannot be null");
        }
        if (this.educations == null) {
            this.educations = new ArrayList<>();
        }
        this.educations.add(education);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateJobPreferences(JobPreferences preferences) {
        this.jobPreferences = preferences;
        this.updatedAt = LocalDateTime.now();
    }

    // Factory method
    public static JobSeekerProfile createFor(UUID userProfileId) {
        return new JobSeekerProfile(UUID.randomUUID(), userProfileId);
    }
}

