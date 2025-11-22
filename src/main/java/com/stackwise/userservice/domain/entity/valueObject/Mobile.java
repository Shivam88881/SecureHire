package com.stackwise.userservice.domain.entity.valueObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.Objects;

/**
 * Value Object representing a mobile phone number with ISO-2 country code.
 * Immutable and validates phone numbers using libphonenumber library.
 */
public class Mobile {
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    private final String mobileNumber;
    private final String countryCode;

    /**
     * Creates a new Mobile value object.
     *
     * @param phoneNumber the phone number (can include + prefix and country code)
     * @param countryCode the ISO-2 country code (e.g., "US", "IN", "GB")
     * @throws IllegalArgumentException if phone number or country code is invalid
     */
    public Mobile(String phoneNumber, String countryCode) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty");
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }

        String normalizedCountryCode = validateAndNormalizeCountryCode(countryCode);
        validatePhoneNumber(phoneNumber, normalizedCountryCode);

        this.mobileNumber = phoneNumber;
        this.countryCode = normalizedCountryCode;
    }

    private String validateAndNormalizeCountryCode(String countryCode) {
        String upperCode = countryCode.trim().toUpperCase();

        if (upperCode.length() != 2) {
            throw new IllegalArgumentException(
                String.format("Country code must be ISO-2 format (2 characters). Got: '%s'", countryCode)
            );
        }

        if (!PHONE_UTIL.getSupportedRegions().contains(upperCode)) {
            throw new IllegalArgumentException(
                String.format("Unsupported country code: '%s'. Expected ISO-2 format (e.g., 'US', 'IN', 'GB')", countryCode)
            );
        }

        return upperCode;
    }

    private void validatePhoneNumber(String phoneNumber, String countryCode) {
        try {
            PhoneNumber parsedNumber = PHONE_UTIL.parse(phoneNumber, countryCode);

            if (!PHONE_UTIL.isValidNumber(parsedNumber)) {
                throw new IllegalArgumentException(
                    String.format("Invalid mobile number '%s' for country code '%s'", phoneNumber, countryCode)
                );
            }
        } catch (NumberParseException e) {
            throw new IllegalArgumentException(
                String.format("Failed to parse mobile number '%s' for country code '%s': %s",
                    phoneNumber, countryCode, e.getMessage()),
                e
            );
        }
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mobile mobile = (Mobile) o;
        return Objects.equals(mobileNumber, mobile.mobileNumber) &&
                Objects.equals(countryCode, mobile.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobileNumber, countryCode);
    }

    @Override
    public String toString() {
        // Return the full mobile number in with country code prefix
        return this.countryCode + this.mobileNumber;
    }
}
