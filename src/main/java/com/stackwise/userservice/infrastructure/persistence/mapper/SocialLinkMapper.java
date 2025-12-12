package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.SocialLink;
import com.stackwise.userservice.infrastructure.persistence.dto.SocialLinkDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SocialLink domain objects and SocialLinkDTO
 * This mapper lives in the infrastructure layer to keep the domain pure
 */
@Component
public class SocialLinkMapper implements DomainDTOMapper<SocialLink, SocialLinkDTO> {

    /**
     * Convert domain SocialLink to infrastructure DTO
     */
    @Override
    public SocialLinkDTO toDTO(SocialLink socialLink) {
        if (socialLink == null) {
            return null;
        }
        return new SocialLinkDTO(
                socialLink.platform(),
                socialLink.getUrl()
        );
    }

    /**
     * Convert infrastructure DTO to domain SocialLink
     */
    @Override
    public SocialLink toDomain(SocialLinkDTO dto) {
        if (dto == null) {
            return null;
        }
        return new SocialLink(dto.platform(), dto.url());
    }
}

