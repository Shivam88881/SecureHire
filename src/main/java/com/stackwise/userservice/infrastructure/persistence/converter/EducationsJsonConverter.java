package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.Education;
import com.stackwise.userservice.infrastructure.persistence.dto.EducationDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.EducationMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for Education List to JSONB
 * Converts List<Education> ↔ JSONB (PostgreSQL)
 *
 * Extends BaseJsonbConverter to eliminate code duplication
 * Uses EducationMapper for domain-DTO conversion
 */
@Converter
public class EducationsJsonConverter extends BaseJsonbConverter<Education, EducationDTO> {

    public EducationsJsonConverter() {
        super(new EducationMapper(), EducationDTO[].class);
    }

    @Override
    protected String getDomainTypeName() {
        return "Education";
    }
}

