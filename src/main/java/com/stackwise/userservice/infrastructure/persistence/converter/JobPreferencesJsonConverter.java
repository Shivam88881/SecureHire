package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.JobPreferences;
import com.stackwise.userservice.infrastructure.persistence.dto.JobPreferencesDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.JobPreferencesMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for JobPreferences to JSONB
 * Converts JobPreferences ↔ JSONB (PostgreSQL)
 *
 * Extends BaseSingleObjectJsonConverter to eliminate code duplication
 * Uses JobPreferencesMapper for domain-DTO conversion
 *
 * Note: This handles a single object (not a list), so it extends BaseSingleObjectJsonConverter
 * instead of BaseJsonbConverter which is designed for lists.
 */
@Converter
public class JobPreferencesJsonConverter extends BaseSingleObjectJsonConverter<JobPreferences, JobPreferencesDTO> {

    public JobPreferencesJsonConverter() {
        super(new JobPreferencesMapper(), JobPreferencesDTO.class);
    }

    @Override
    protected String getDomainTypeName() {
        return "JobPreferences";
    }
}

