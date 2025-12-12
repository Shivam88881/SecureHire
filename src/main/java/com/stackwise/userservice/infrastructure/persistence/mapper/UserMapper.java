package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.entity.UserProfile;
import com.stackwise.userservice.domain.valueObject.*;
import com.stackwise.userservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Domain UserProfile and JPA UserEntity
 * Separates domain layer from infrastructure concerns
 */
@Component
public class UserMapper {

    /**
     * Convert Domain UserProfile to JPA Entity
     */
    public UserEntity toEntity(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        return new UserEntity(
            userProfile.getId(),
            userProfile.getFirstName(),
            userProfile.getLastName(),
            userProfile.getAuthUserId(),
            userProfile.getAvatarUrl(),
            userProfile.getEmail().getEmailAddress(),
            userProfile.getMobile().mobileNumber(),
            userProfile.getMobile().countryCode(),
            userProfile.isEmailVerified(),
            userProfile.getAccountStatus().getStatus(),
            userProfile.getRole().getRoleType(),
            userProfile.getAddress(),
            userProfile.getSocialLinks()
        );
    }

    /**
     * Convert JPA Entity to Domain UserProfile
     */
    public UserProfile toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        Email email = new Email(entity.getEmail());
        Mobile mobile = new Mobile(entity.getMobile(), entity.getCountryCode());
        Role role = new Role(entity.getRole());

        UserProfile userProfile = new UserProfile(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getAuthUserId(),
            entity.getAvatarUrl(),
            email,
            mobile,
            role
        );

        // Set optional fields
        if (entity.isEmailVerified()) {
            userProfile.verifyEmail();
        }

        // Set account status if different from default
        if (entity.getAccountStatus() != AccountStatus.Status.ACTIVE) {
            userProfile.changeAccountStatus(entity.getAccountStatus());
        }

        // Set address if present
        if (entity.getAddress() != null) {
            userProfile.setAddress(entity.getAddress());
        }

        // Set social links if present
        if (entity.getSocialLinks() != null) {
            userProfile.setSocialLinks(entity.getSocialLinks());
        }

        return userProfile;
    }
}

