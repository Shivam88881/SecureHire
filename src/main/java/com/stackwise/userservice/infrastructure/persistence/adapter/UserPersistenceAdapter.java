package com.stackwise.userservice.infrastructure.persistence.adapter;

import com.stackwise.userservice.domain.entity.UserProfile;
import com.stackwise.userservice.infrastructure.persistence.entity.UserProfileEntity;
import com.stackwise.userservice.infrastructure.persistence.mapper.UserMapper;
import com.stackwise.userservice.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence Adapter for UserProfile
 * Implements repository pattern to persist domain entities
 * Separates domain logic from infrastructure concerns
 */
@Component
public class UserPersistenceAdapter {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Save or update a user profile
     */
    public UserProfile save(UserProfile userProfile) {
        UserProfileEntity entity = mapper.toEntity(userProfile);
        UserProfileEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * Find user profile by ID
     */
    public Optional<UserProfile> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Find user profile by email
     */
    public Optional<UserProfile> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    /**
     * Delete user profile
     */
    public void delete(UserProfile userProfile) {
        UserProfileEntity entity = mapper.toEntity(userProfile);
        jpaRepository.delete(entity);
    }

    /**
     * Delete user profile by ID
     */
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

