package com.securehire.userservice.model;


import io.hypersistence.utils.hibernate.type.basic.Inet;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "log_id", updatable = false, nullable = false)
    private UUID logId;

    private String action;
    private String resourceType;
    private UUID resourceId;
    private Inet ipAddress;
    private String userAgent;

    @Type(JsonType.class)
    @Column(name = "changes", columnDefinition = "jsonb")
    private Map<String,Object> changes;

    private Timestamp createdAt;

    @ManyToOne
    private User userId;
}
