package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.Skill;
import com.stackwise.userservice.infrastructure.persistence.dto.SkillDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.SkillMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for Skills List to JSONB
 * Converts List<Skill> ↔ JSONB (PostgreSQL)
 *
 * Extends BaseJsonbConverter to eliminate code duplication
 * Uses SkillMapper for domain-DTO conversion
 *
 * Benefits of this approach:
 * - No duplicate JSON logic
 * - Reusable mapper (can be used elsewhere)
 * - Consistent DTOs with Jackson annotations
 * - Easy to test and maintain
 */
@Converter
public class SkillsJsonConverter extends BaseJsonbConverter<Skill, SkillDTO> {

    public SkillsJsonConverter() {
        super(new SkillMapper(), SkillDTO[].class);
    }

    @Override
    protected String getDomainTypeName() {
        return "Skill";
    }
}


