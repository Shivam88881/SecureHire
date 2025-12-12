package com.stackwise.userservice.domain.valueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JobPreferences - Job seeker's job preferences
 *
 * @param employmentType FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
 * @param workMode       REMOTE, HYBRID, ONSITE
 * @param expectedSalary in base currency
 * @param noticePeriod   in days or "IMMEDIATE"
 */
public record JobPreferences(List<String> desiredRoles, List<String> preferredLocations, EmploymentType employmentType,
                             WorkMode workMode, Money expectedSalary, NoticePeriod noticePeriod) {
    public enum EmploymentType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP
    }

    public enum WorkMode {
        REMOTE,
        HYBRID,
        ONSITE
    }

    public enum NoticePeriod {
        IMMEDIATE,
        DAYS_15,
        DAYS_30,
        DAYS_45,
        DAYS_60,
        DAYS_90
    }


    @Override
    public List<String> desiredRoles() {
        return Collections.unmodifiableList(desiredRoles);
    }

    @Override
    public List<String> preferredLocations() {
        return Collections.unmodifiableList(preferredLocations);
    }
}
