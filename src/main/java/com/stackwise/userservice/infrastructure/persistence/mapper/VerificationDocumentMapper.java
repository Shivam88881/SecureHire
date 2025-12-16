package com.stackwise.userservice.infrastructure.persistence.mapper;


import com.stackwise.userservice.domain.valueObject.VerificationDocument;
import com.stackwise.userservice.infrastructure.persistence.dto.VerificationDocumentDTO;
import org.springframework.stereotype.Component;

@Component
public class VerificationDocumentMapper implements DomainDTOMapper<VerificationDocument, VerificationDocumentDTO> {

    @Override
    public VerificationDocumentDTO toDTO(VerificationDocument verificationDocument) {
        if(verificationDocument == null) {
            return null;
        }
        return new VerificationDocumentDTO(
                verificationDocument.getDocumentType(),
                verificationDocument.getDocumentUrl());
    }

    @Override
    public VerificationDocument toDomain(VerificationDocumentDTO verificationDocumentDTO) {
        if(verificationDocumentDTO == null) {
            return null;
        }
        return new VerificationDocument(
                verificationDocumentDTO.documentType(),
                verificationDocumentDTO.documentUrl());
    }
}
