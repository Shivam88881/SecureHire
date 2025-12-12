package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.WorkExperience;
import com.stackwise.userservice.infrastructure.persistence.dto.WorkExperienceDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.WorkExperienceMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for Work Experience List to JSONB
 * Converts List<WorkExperience> ↔ JSONB (PostgreSQL)
 *
 * Extends BaseJsonbConverter to eliminate code duplication
 * Uses WorkExperienceMapper for domain-DTO conversion
 */
@Converter
public class WorkExperiencesJsonConverter extends BaseJsonbConverter<WorkExperience, WorkExperienceDTO> {

    public WorkExperiencesJsonConverter() {
        super(new WorkExperienceMapper(), WorkExperienceDTO[].class);
    }

    @Override
    protected String getDomainTypeName() {
        return "WorkExperience";
    }
}

