package com.securehire.userservice.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class JobSeekerDetail {
    private UUID jobSeekerId;

    private URI resumeUrl;
    private String currentJobTitle;
    private Integer yearsOfExperience;

    @Type(JsonType.class)
    @Column(name = "skill", columnDefinition = "jsonb")
    private Map<String, Object> skill;

    @Type(JsonType.class)
    @Column(name = "education", columnDefinition = "jsonb")
    private Map<String, Object> education;

    @Type(JsonType.class)
    @Column(name = "work_history", columnDefinition = "jsonb")
    private Map<String, Object> workHistory;

    @Type(JsonType.class)
    @Column(name = "job_preference", columnDefinition = "jsonb")
    private Map<String, Object> jobPreference;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User userId;
}
