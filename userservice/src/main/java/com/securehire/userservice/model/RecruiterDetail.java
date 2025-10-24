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
public class RecruiterDetail {
    @Id()
    @GeneratedValue
    @UuidGenerator
    @Column(name = "recruiter_id", updatable = false, nullable = false)
    private UUID recruiterId;

    private String companyName;
    private String jobTitle;
    private String department;
    private String verificationStatus;

    @Type(JsonType.class)
    @Column(name = "verification_documents", columnDefinition = "jsonb")
    private Map<String, Object> verificationDocuments;

    private OffsetDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User userId;

//    @ManyToOne
    private UUID companyId;

}
