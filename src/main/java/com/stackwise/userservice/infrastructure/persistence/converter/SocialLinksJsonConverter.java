package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.SocialLink;
import com.stackwise.userservice.infrastructure.persistence.dto.SocialLinkDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.SocialLinkMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for List<SocialLink> to JSONB Array
 * Converts List of SocialLink domain objects to JSONB array for PostgreSQL storage
 *
 * Extends BaseJsonbConverter to eliminate code duplication
 * Uses SocialLinkMapper for domain-DTO conversion
 *
 * This converter uses SocialLinkDTO to isolate Jackson framework dependencies
 * from the domain layer, maintaining Clean Architecture principles.
 *
 * Example DB storage:
 * [
 *   {"platform": "linkedin", "url": "https://linkedin.com/in/johndoe"},
 *   {"platform": "github", "url": "https://github.com/johndoe"}
 * ]
 */
@Converter
public class SocialLinksJsonConverter extends BaseJsonbConverter<SocialLink, SocialLinkDTO> {

    public SocialLinksJsonConverter() {
        super(new SocialLinkMapper(), SocialLinkDTO[].class);
    }

    @Override
    protected String getDomainTypeName() {
        return "SocialLink";
    }
}

