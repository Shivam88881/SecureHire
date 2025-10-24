package com.securehire.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class UserRole {
    @OneToOne
    private  User userId;
    @OneToOne
    private Role roleId;
    private OffsetDateTime assignedAt;
    private UUID assignedBy;
}
