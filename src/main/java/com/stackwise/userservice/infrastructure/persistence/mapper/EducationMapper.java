package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.Education;
import com.stackwise.userservice.infrastructure.persistence.dto.EducationDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for Education domain object <-> EducationDTO conversion
 * Used by EducationsJsonConverter for JSONB serialization
 */
@Component
public class EducationMapper implements DomainDTOMapper<Education, EducationDTO> {

    @Override
    public EducationDTO toDTO(Education education) {
        if (education == null) {
            return null;
        }

        return new EducationDTO(
                education.institution(),
                education.degree(),
                education.fieldOfStudy(),
                education.startDate(),
                education.endDate(),
                education.gpa()
        );
    }

    @Override
    public Education toDomain(EducationDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Education(
                dto.institution(),
                dto.degree(),
                dto.fieldOfStudy(),
                dto.startDate(),
                dto.endDate(),
                dto.gpa()
        );
    }
}

