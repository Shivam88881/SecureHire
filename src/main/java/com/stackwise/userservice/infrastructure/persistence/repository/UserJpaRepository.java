package com.stackwise.userservice.infrastructure.persistence.repository;

import com.stackwise.userservice.infrastructure.persistence.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for UserProfile persistence
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserProfileEntity, UUID> {

    Optional<UserProfileEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}


