package com.stackwise.userservice.infrastructure.persistence.converter;

import com.stackwise.userservice.domain.valueObject.Address;
import com.stackwise.userservice.infrastructure.persistence.dto.AddressDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.AddressMapper;
import jakarta.persistence.Converter;

/**
 * JPA Converter for Address Value Object to JSONB
 * Converts Address domain object to JSONB for PostgreSQL storage
 *
 * Extends BaseSingleObjectJsonConverter to eliminate code duplication
 * Uses AddressMapper for domain-DTO conversion
 *
 * This converter uses AddressDTO to isolate Jackson framework dependencies
 * from the domain layer, maintaining Clean Architecture principles.
 */
@Converter
public class AddressJsonConverter extends BaseSingleObjectJsonConverter<Address, AddressDTO> {

    public AddressJsonConverter() {
        super(new AddressMapper(), AddressDTO.class);
    }

    @Override
    protected String getDomainTypeName() {
        return "Address";
    }
}


