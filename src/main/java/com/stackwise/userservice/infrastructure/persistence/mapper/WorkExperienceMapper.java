package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.WorkExperience;
import com.stackwise.userservice.infrastructure.persistence.dto.WorkExperienceDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for WorkExperience domain object <-> WorkExperienceDTO conversion
 * Used by WorkExperiencesJsonConverter for JSONB serialization
 */
@Component
public class WorkExperienceMapper implements DomainDTOMapper<WorkExperience, WorkExperienceDTO> {

    @Override
    public WorkExperienceDTO toDTO(WorkExperience experience) {
        if (experience == null) {
            return null;
        }

        return new WorkExperienceDTO(
                experience.company(),
                experience.position(),
                experience.description(),
                experience.startDate(),
                experience.endDate(),
                experience.isCurrent()
        );
    }

    @Override
    public WorkExperience toDomain(WorkExperienceDTO dto) {
        if (dto == null) {
            return null;
        }

        return new WorkExperience(
                dto.company(),
                dto.position(),
                dto.description(),
                dto.startDate(),
                dto.endDate(),
                dto.isCurrent()
        );
    }
}

