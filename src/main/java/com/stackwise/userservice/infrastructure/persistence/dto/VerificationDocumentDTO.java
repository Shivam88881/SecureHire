package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stackwise.userservice.domain.valueObject.VerificationDocument;

import java.net.URI;

public record VerificationDocumentDTO(
        @JsonProperty("documentType") VerificationDocument.DocumentType documentType,
        @JsonProperty("documentUrl") URI documentUrl) {
}
