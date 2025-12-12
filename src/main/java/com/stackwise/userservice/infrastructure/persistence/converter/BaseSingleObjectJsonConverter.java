package com.stackwise.userservice.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stackwise.userservice.infrastructure.persistence.mapper.DomainDTOMapper;
import jakarta.persistence.AttributeConverter;

/**
 * Abstract base class for single object JSONB converters
 * Provides common JSON serialization/deserialization logic for single objects (not lists)
 * Reduces code duplication across different converters
 *
 * @param <DOMAIN> Domain model type (e.g., JobPreferences, Address)
 * @param <DTO> Data Transfer Object type (e.g., JobPreferencesDTO, AddressDTO)
 *
 * This is the counterpart to BaseJsonbConverter which handles List<DOMAIN>.
 * Use this when converting a single object to JSONB.
 */
public abstract class BaseSingleObjectJsonConverter<DOMAIN, DTO>
        implements AttributeConverter<DOMAIN, String> {

    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final DomainDTOMapper<DOMAIN, DTO> mapper;
    private final Class<DTO> dtoClass;

    protected BaseSingleObjectJsonConverter(DomainDTOMapper<DOMAIN, DTO> mapper, Class<DTO> dtoClass) {
        this.mapper = mapper;
        this.dtoClass = dtoClass;
    }

    @Override
    public String convertToDatabaseColumn(DOMAIN domain) {
        if (domain == null) {
            return null;
        }

        try {
            DTO dto = mapper.toDTO(domain);
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Error converting " + getDomainTypeName() + " to JSON", e);
        }
    }

    @Override
    public DOMAIN convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            DTO dto = objectMapper.readValue(json, dtoClass);
            return mapper.toDomain(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Error parsing " + getDomainTypeName() + " JSON", e);
        }
    }

    /**
     * Get the domain type name for error messages
     * Override in subclasses for better error reporting
     */
    protected abstract String getDomainTypeName();
}

