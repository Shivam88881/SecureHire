package com.stackwise.userservice.domain.valueObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public record Resume(String resumeUrl) {
    public Resume {
        if (resumeUrl == null || resumeUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Resume URL cannot be null or empty");
        }
        if (!isValidUrl(resumeUrl)) {
            throw new IllegalArgumentException("Invalid resume URL format");
        }
    }

    private boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(resumeUrl, resume.resumeUrl);
    }

    @Override
    public String toString() {
        return this.resumeUrl;
    }
}
