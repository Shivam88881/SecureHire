-- Migration: Create users table with JSON columns for Address and SocialLinks
-- Database: PostgreSQL

CREATE TABLE users (
    id UUID PRIMARY KEY,
    auth_user_id UUID NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile VARCHAR(20) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    account_status VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    address TEXT,  -- JSON stored as TEXT
    social_links TEXT,  -- JSON stored as TEXT
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for frequently queried columns
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_auth_user_id ON users(auth_user_id);
CREATE INDEX idx_users_account_status ON users(account_status);

-- Optional: Create GIN index for JSON columns if you need to query inside JSON
-- CREATE INDEX idx_users_address_gin ON users USING GIN ((address::jsonb));
-- CREATE INDEX idx_users_social_links_gin ON users USING GIN ((social_links::jsonb));

-- Example: Validate JSON format (PostgreSQL 12+)
-- ALTER TABLE users ADD CONSTRAINT address_is_json CHECK (address IS NULL OR address::json IS NOT NULL);
-- ALTER TABLE users ADD CONSTRAINT social_links_is_json CHECK (social_links IS NULL OR social_links::json IS NOT NULL);

-- Example data
-- Address JSON format: {"street":"123 Main St","city":"New York","state":"NY","zipCode":"10001","country":"USA"}
-- SocialLinks JSON format: {"links":{"linkedin":"https://linkedin.com/in/johndoe","github":"https://github.com/johndoe"}}

