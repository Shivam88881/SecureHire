package com.stackwise.userservice.domain.valueObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Value Object representing a mobile phone number with ISO-2 country code.
 * Immutable and validates phone numbers using libphonenumber library.
 */
public record Mobile(String mobileNumber, String countryCode) {
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    /**
     * Creates a new Mobile value object.
     *
     * @param mobileNumber the phone number (can include + prefix and country code)
     * @param countryCode  the ISO-2 country code (e.g., "US", "IN", "GB")
     * @throws IllegalArgumentException if phone number or country code is invalid
     */
    public Mobile {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty");
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }

        String normalizedCountryCode = validateAndNormalizeCountryCode(countryCode);
        validatePhoneNumber(mobileNumber, normalizedCountryCode);

        mobileNumber = mobileNumber;
        countryCode = normalizedCountryCode;
    }

    private static String validateAndNormalizeCountryCode(String countryCode) {
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

    private static void validatePhoneNumber(String phoneNumber, String countryCode) {
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

    @Override
    public String toString() {
        // Return the full mobile number in with country code prefix
        return this.countryCode + this.mobileNumber;
    }
}
