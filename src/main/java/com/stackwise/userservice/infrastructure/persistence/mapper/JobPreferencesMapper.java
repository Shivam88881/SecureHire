package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.JobPreferences;
import com.stackwise.userservice.infrastructure.persistence.dto.JobPreferencesDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for JobPreferences domain object <-> JobPreferencesDTO conversion
 * Used by JobPreferencesJsonConverter for JSONB serialization
 */
@Component
public class JobPreferencesMapper implements DomainDTOMapper<JobPreferences, JobPreferencesDTO> {

    @Override
    public JobPreferencesDTO toDTO(JobPreferences preferences) {
        if (preferences == null) {
            return null;
        }

        return new JobPreferencesDTO(
                preferences.desiredRoles(),
                preferences.preferredLocations(),
                preferences.employmentType(),
                preferences.workMode(),
                preferences.expectedSalary(),
                preferences.noticePeriod()
        );
    }

    @Override
    public JobPreferences toDomain(JobPreferencesDTO dto) {
        if (dto == null) {
            return null;
        }

        return new JobPreferences(
                dto.desiredRoles(),
                dto.preferredLocations(),
                dto.employmentType(),
                dto.workMode(),
                dto.expectedSalary(),
                dto.noticePeriod()
        );
    }
}

