package com.stackwise.userservice.infrastructure.persistence.mapper;

/**
 * Generic mapper interface for converting between domain objects and DTOs
 *
 * @param <DOMAIN> Domain model type
 * @param <DTO> Data Transfer Object type
 *
 * This interface provides a contract for bidirectional conversion
 * between domain objects (pure business logic) and DTOs (infrastructure concerns)
 */
public interface DomainDTOMapper<DOMAIN, DTO> {

    /**
     * Convert domain object to DTO for serialization
     *
     * @param domain The domain object
     * @return The corresponding DTO
     */
    DTO toDTO(DOMAIN domain);

    /**
     * Convert DTO to domain object for deserialization
     *
     * @param dto The DTO
     * @return The corresponding domain object
     */
    DOMAIN toDomain(DTO dto);
}


