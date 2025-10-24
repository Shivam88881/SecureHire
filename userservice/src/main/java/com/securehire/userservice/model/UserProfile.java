package com.securehire.userservice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;


@Entity
@Table
@Getter
@Setter
public class UserProfile {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "profile_id", updatable = false, nullable = false)
    private UUID profileId;

    private String firstName;
    private String lastName;
    private PhoneNumber phoneNumber;
}
