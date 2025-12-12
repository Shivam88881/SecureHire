package com.stackwise.userservice.domain.valueObject;

/**
 * Role Value Object - Immutable
 * Represents a user's role in the system
 */
public record Role(RoleType roleType) {

    public enum RoleType {
        JOBSEEKER,
        RECRUITER,
        ADMIN,
        SUPERADMIN
    }

    public Role {
        if (roleType == null) {
            throw new IllegalArgumentException("Role type cannot be null");
        }
    }

    /**
     * Convenience constructor that accepts role type as String
     *
     * @param roleTypeStr The role type as string (case-insensitive)
     * @throws IllegalArgumentException if the role type is invalid
     */
    public Role(String roleTypeStr) {
        this(parseRoleType(roleTypeStr));
    }

    /**
     * Helper method to parse and validate role type string
     */
    private static RoleType parseRoleType(String roleTypeStr) {
        if (roleTypeStr == null || roleTypeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Role type string cannot be null or empty");
        }

        try {
            return RoleType.valueOf(roleTypeStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role type: " + roleTypeStr, e);
        }
    }

    /**
     * Gets the role type
     */
    public RoleType getRoleType() {
        return roleType;
    }

    @Override
    public String toString() {
        return roleType.name();
    }
}
