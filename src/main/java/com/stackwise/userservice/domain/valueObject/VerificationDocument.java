package com.stackwise.userservice.domain.valueObject;

import com.stackwise.userservice.domain.entity.RecruiterProfile;

import java.net.URI;

public record VerificationDocument(DocumentType documentType, URI documentUrl) {
    public enum DocumentType {
        COMPANY_ID,
        BUSINESS_LICENSE,
        TAX_ID,
        ESLABLISHMENT_CERTIFICATE,
        GST_CERTIFICATE,
        OTHER
    }

    public VerificationDocument{
        if (documentType == null) {
            throw new IllegalArgumentException("Document type cannot be null");
        }
        if (documentUrl == null) {
            throw new IllegalArgumentException("Document URL cannot be null or empty");
        }
    }


    public DocumentType getDocumentType() {
        return documentType;
    }

    public URI getDocumentUrl() {
        return documentUrl;
    }

}
