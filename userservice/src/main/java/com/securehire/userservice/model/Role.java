package com.securehire.userservice.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "role_id", updatable = false, nullable = false)
    private UUID roleId;
    private String roleName;
    private String description;

    @Type(JsonType.class)
    @Column(name = "permissions", columnDefinition = "jsonb")
    private Map<String, Object> permissions;

    private OffsetDateTime createdAt;
}
