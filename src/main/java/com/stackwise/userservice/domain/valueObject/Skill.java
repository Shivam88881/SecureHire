package com.stackwise.userservice.domain.valueObject;

import java.util.Objects;

/**
 * Skill Value Object - Immutable
 * Represents a professional skill with proficiency level
 * <p>
 * This will be stored as JSONB in PostgreSQL for efficient querying
 *
 * @param yearsOfExperience Optional
 */
public record Skill(String name, ProficiencyLevel level, Integer yearsOfExperience) {
    public Skill {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be null or empty");
        }
        if (level == null) {
            throw new IllegalArgumentException("Proficiency level cannot be null");
        }
        if (yearsOfExperience != null && yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        // Normalize skill name: lowercase, trimmed
        name = name.trim().toLowerCase();
        level = level;
        yearsOfExperience = yearsOfExperience;
    }

    // Constructor without years of experience
    public Skill(String name, ProficiencyLevel level) {
        this(name, level, null);
    }

    // Constructor with just name (defaults to INTERMEDIATE)
    public Skill(String name) {
        this(name, ProficiencyLevel.INTERMEDIATE, null);
    }

    /**
     * Proficiency levels for skills
     */
    public enum ProficiencyLevel {
        BEGINNER("Beginner - Less than 1 year"),
        INTERMEDIATE("Intermediate - 1-3 years"),
        ADVANCED("Advanced - 3-5 years"),
        EXPERT("Expert - 5+ years");

        private final String description;

        ProficiencyLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name, skill.name); // Skills are equal if names match
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        if (yearsOfExperience != null) {
            return String.format("%s (%s - %d years)", name, level, yearsOfExperience);
        }
        return String.format("%s (%s)", name, level);
    }
}
