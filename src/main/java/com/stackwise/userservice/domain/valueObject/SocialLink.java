package com.stackwise.userservice.domain.valueObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * SocialLink Value Object - Immutable
 * Represents a single social media link
 * <p>
 * Pure domain model without any framework dependencies.
 * Serialization concerns are handled by infrastructure layer DTOs.
 */
public record SocialLink(String platform, URI url) {

    /**
     * Compact constructor with validation and normalization
     */
    public SocialLink {
        if (platform == null || platform.trim().isEmpty()) {
            throw new IllegalArgumentException("Platform cannot be null or empty");
        }
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }

        // Validate URL scheme
        String scheme = url.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }

        // Normalize platform name
        platform = platform.trim().toLowerCase();
    }

    /**
     * Convenience constructor that accepts URL as String
     */
    public SocialLink(String platform, String urlString) {
        this(platform, parseUrl(urlString));
    }

    /**
     * Helper method to parse and validate URL string
     */
    private static URI parseUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        try {
            return new URI(urlString.trim());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + urlString, e);
        }
    }

    /**
     * Get URL as String
     */
    public String getUrl() {
        return url.toString();
    }

    /**
     * Get URL as URI object
     */
    public URI getUrlAsURI() {
        return url;
    }

    /**
     * Get platform name
     */
    public String getPlatform() {
        return platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialLink that = (SocialLink) o;
        return platform.equals(that.platform);
    }

    @Override
    public int hashCode() {
        return platform.hashCode();
    }

    @Override
    public String toString() {
        return platform + ": " + url;
    }
}

