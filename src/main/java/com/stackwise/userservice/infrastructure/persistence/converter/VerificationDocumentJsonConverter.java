package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.VerificationDocument;
import com.stackwise.userservice.infrastructure.persistence.dto.VerificationDocumentDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.DomainDTOMapper;
import com.stackwise.userservice.infrastructure.persistence.mapper.VerificationDocumentMapper;
import jakarta.persistence.Converter;

@Converter
public class VerificationDocumentJsonConverter extends BaseJsonbConverter<VerificationDocument, VerificationDocumentDTO> {


    protected VerificationDocumentJsonConverter() {
        super(new VerificationDocumentMapper(), VerificationDocumentDTO[].class);
    }

    @Override
    protected String getDomainTypeName() {
        return "VerificationDocument";
    }
}
