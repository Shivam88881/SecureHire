package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.Skill;
import com.stackwise.userservice.infrastructure.persistence.dto.SkillDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for Skill domain object <-> SkillDTO conversion
 * Used by SkillsJsonConverter for JSONB serialization
 */
@Component
public class SkillMapper implements DomainDTOMapper<Skill, SkillDTO> {

    @Override
    public SkillDTO toDTO(Skill skill) {
        if (skill == null) {
            return null;
        }

        return new SkillDTO(
                skill.name(),
                skill.level().name(),
                skill.yearsOfExperience()
        );
    }

    @Override
    public Skill toDomain(SkillDTO dto) {
        if (dto == null) {
            return null;
        }

        Skill.ProficiencyLevel level = Skill.ProficiencyLevel.valueOf(dto.level());
        return new Skill(dto.name(), level, dto.yearsOfExperience());
    }
}

