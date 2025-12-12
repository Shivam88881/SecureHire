package com.stackwise.userservice.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stackwise.userservice.infrastructure.persistence.mapper.DomainDTOMapper;
import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for JSONB converters
 * Provides common JSON serialization/deserialization logic
 * Reduces code duplication across different converters
 *
 * @param <DOMAIN> Domain model type (e.g., Skill, WorkExperience)
 * @param <DTO> Data Transfer Object type (e.g., SkillDTO, WorkExperienceDTO)
 */
public abstract class BaseJsonbConverter<DOMAIN, DTO>
        implements AttributeConverter<List<DOMAIN>, String> {

    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final DomainDTOMapper<DOMAIN, DTO> mapper;
    private final Class<DTO[]> dtoArrayClass;

    protected BaseJsonbConverter(DomainDTOMapper<DOMAIN, DTO> mapper, Class<DTO[]> dtoArrayClass) {
        this.mapper = mapper;
        this.dtoArrayClass = dtoArrayClass;
    }

    @Override
    public String convertToDatabaseColumn(List<DOMAIN> domainList) {
        if (domainList == null || domainList.isEmpty()) {
            return null;
        }

        try {
            List<DTO> dtos = domainList.stream()
                    .map(mapper::toDTO)
                    .toList();
            return objectMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Error converting " + getDomainTypeName() + " list to JSON", e);
        }
    }

    @Override
    public List<DOMAIN> convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            DTO[] dtos = objectMapper.readValue(json, dtoArrayClass);
            List<DOMAIN> domainList = new ArrayList<>();
            for (DTO dto : dtos) {
                domainList.add(mapper.toDomain(dto));
            }
            return domainList;
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

