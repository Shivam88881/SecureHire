package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.Address;
import com.stackwise.userservice.infrastructure.persistence.dto.AddressDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Address domain objects and AddressDTO
 * This mapper lives in the infrastructure layer to keep the domain pure
 */
@Component
public class AddressMapper implements DomainDTOMapper<Address, AddressDTO> {

    /**
     * Convert domain Address to infrastructure DTO
     */
    @Override
    public AddressDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDTO(
                address.street(),
                address.city(),
                address.state(),
                address.zipCode(),
                address.country()
        );
    }

    /**
     * Convert infrastructure DTO to domain Address
     */
    @Override
    public Address toDomain(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Address(
                dto.street(),
                dto.city(),
                dto.state(),
                dto.zipCode(),
                dto.country()
        );
    }
}

