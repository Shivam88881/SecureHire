# Migration Guide: SocialLinks Map → List<SocialLink>

## Overview
This guide helps you migrate from the old `Map<String, String>` based `SocialLinks` to the new `List<SocialLink>` design.

---

## What Changed?

### Old Design (Deprecated)
```java
public class SocialLinks {
    private final Map<String, String> links;
}

// Usage
Map<String, String> links = new HashMap<>();
links.put("github", "https://github.com/johndoe");
SocialLinks socialLinks = new SocialLinks(links);
user.updateSocialLinks(socialLinks);
```

### New Design (Current)
```java
public class SocialLink {
    private final String platform;
    private final URI url;
}

// Usage
user.addSocialLink("github", "https://github.com/johndoe");
// OR
List<SocialLink> links = List.of(
    new SocialLink("github", "https://github.com/johndoe")
);
user.updateSocialLinks(links);
```

---

## Database Migration

### Old JSON Format (Map)
```json
{
  "links": {
    "linkedin": "https://linkedin.com/in/johndoe",
    "github": "https://github.com/johndoe"
  }
}
```

### New JSON Format (Array)
```json
[
  {"platform": "linkedin", "url": "https://linkedin.com/in/johndoe"},
  {"platform": "github", "url": "https://github.com/johndoe"}
]
```

### PostgreSQL Migration Script

```sql
-- Step 1: Add a temporary column
ALTER TABLE users ADD COLUMN social_links_new TEXT;

-- Step 2: Migrate data from map format to array format
UPDATE users
SET social_links_new = (
    SELECT jsonb_agg(
        jsonb_build_object(
            'platform', key,
            'url', value
        )
    )
    FROM jsonb_each_text(
        COALESCE(social_links::jsonb -> 'links', '{}'::jsonb)
    ) AS t(key, value)
)::text
WHERE social_links IS NOT NULL 
  AND social_links != ''
  AND social_links != 'null';

-- Step 3: Verify the migration
SELECT 
    id,
    first_name,
    last_name,
    social_links AS old_format,
    social_links_new AS new_format
FROM users
WHERE social_links IS NOT NULL
LIMIT 10;

-- Step 4: Drop old column and rename new one (after verification)
ALTER TABLE users DROP COLUMN social_links;
ALTER TABLE users RENAME COLUMN social_links_new TO social_links;

-- Step 5: Create index for better query performance (optional)
CREATE INDEX idx_users_social_links ON users USING GIN (social_links::jsonb);
```

---

## Code Migration

### 1. Update Domain Layer

**Before:**

```java


Map<String, String> links = new HashMap<>();
links.

put("platform","url");

SocialLinks socialLinks = new SocialLinks(links);
user.

updateSocialLinks(socialLinks);
```

**After:**
```java
import com.stackwise.userservice.domain.valueObject.SocialLink;

user.addSocialLink("platform", "url");
// OR for bulk update
List<SocialLink> links = List.of(
    new SocialLink("platform", "url")
);
user.updateSocialLinks(links);
```

### 2. Update Controllers/DTOs

**Before:**
```java
public class UserDTO {
    private Map<String, String> socialLinks;
    
    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }
}
```

**After:**
```java
public class UserDTO {
    private List<SocialLinkDTO> socialLinks;
    
    public List<SocialLinkDTO> getSocialLinks() {
        return socialLinks;
    }
}

public class SocialLinkDTO {
    private String platform;
    private String url;
    
    // getters/setters
}
```

### 3. Update Service Layer

**Before:**
```java
public void updateSocialLinks(UUID userId, Map<String, String> links) {
    User user = userRepository.findById(userId).orElseThrow();
    user.updateSocialLinks(new SocialLinks(links));
    userRepository.save(user);
}
```

**After:**
```java
public void updateSocialLinks(UUID userId, List<SocialLinkDTO> linksDTO) {
    User user = userRepository.findById(userId).orElseThrow();
    
    List<SocialLink> links = linksDTO.stream()
        .map(dto -> new SocialLink(dto.getPlatform(), dto.getUrl()))
        .collect(Collectors.toList());
    
    user.updateSocialLinks(links);
    userRepository.save(user);
}
```

---

## REST API Changes

### Old Endpoint Request
```json
PUT /api/users/{userId}/social-links
{
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe"
}
```

### New Endpoint Request
```json
PUT /api/users/{userId}/social-links
[
  {
    "platform": "linkedin",
    "url": "https://linkedin.com/in/johndoe"
  },
  {
    "platform": "github",
    "url": "https://github.com/johndoe"
  }
]
```

### Response Format
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "John",
  "lastName": "Doe",
  "socialLinks": [
    {
      "platform": "linkedin",
      "url": "https://linkedin.com/in/johndoe"
    },
    {
      "platform": "github",
      "url": "https://github.com/johndoe"
    }
  ]
}
```

---

## Testing Migration

### Unit Tests

**Test SocialLink Creation:**
```java
@Test
void testSocialLinkCreation() {
    SocialLink link = new SocialLink("github", "https://github.com/johndoe");
    assertEquals("github", link.getPlatform());
    assertEquals("https://github.com/johndoe", link.getUrl());
}

@Test
void testInvalidUrl() {
    assertThrows(IllegalArgumentException.class, () -> {
        new SocialLink("github", "not-a-url");
    });
}

@Test
void testUrlWithoutScheme() {
    assertThrows(IllegalArgumentException.class, () -> {
        new SocialLink("github", "github.com/johndoe");
    });
}
```

**Test User Social Links Management:**
```java
@Test
void testAddSocialLink() {
    User user = User.createUser("John", "Doe", UUID.randomUUID(),
        "john@example.com", "1234567890", "+1", new Role(Role.RoleType.USER));
    
    user.addSocialLink("github", "https://github.com/johndoe");
    
    List<SocialLink> links = user.getSocialLinks();
    assertEquals(1, links.size());
    assertEquals("github", links.get(0).getPlatform());
}

@Test
void testPlatformUniqueness() {
    User user = User.createUser("John", "Doe", UUID.randomUUID(),
        "john@example.com", "1234567890", "+1", new Role(Role.RoleType.USER));
    
    user.addSocialLink("github", "https://github.com/old");
    user.addSocialLink("github", "https://github.com/new");
    
    List<SocialLink> links = user.getSocialLinks();
    assertEquals(1, links.size());
    assertEquals("https://github.com/new", links.get(0).getUrl());
}
```

### Integration Tests

```java
@Test
void testSocialLinksPersistence() {
    User user = User.createUser("John", "Doe", UUID.randomUUID(),
        "john@example.com", "1234567890", "+1", new Role(Role.RoleType.USER));
    
    user.addSocialLink("linkedin", "https://linkedin.com/in/johndoe");
    user.addSocialLink("github", "https://github.com/johndoe");
    
    User savedUser = userRepository.save(user);
    entityManager.flush();
    entityManager.clear();
    
    User retrievedUser = userRepository.findById(savedUser.getId()).orElseThrow();
    assertEquals(2, retrievedUser.getSocialLinks().size());
}
```

---

## Rollback Plan

If you need to rollback to the old format:

### Database Rollback
```sql
-- Revert array format back to map format
ALTER TABLE users ADD COLUMN social_links_old TEXT;

UPDATE users
SET social_links_old = (
    SELECT jsonb_build_object(
        'links',
        jsonb_object_agg(
            elem->>'platform',
            elem->>'url'
        )
    )
    FROM jsonb_array_elements(social_links::jsonb) AS elem
)::text
WHERE social_links IS NOT NULL;

ALTER TABLE users DROP COLUMN social_links;
ALTER TABLE users RENAME COLUMN social_links_old TO social_links;
```

### Code Rollback
1. Revert to commit before migration
2. Remove `SocialLink.java`
3. Restore `SocialLinks.java` (remove @Deprecated)
4. Update `User.java` to use `SocialLinks` instead of `List<SocialLink>`

---

## Benefits of New Design

| Aspect | Old (Map) | New (List<SocialLink>) |
|--------|-----------|------------------------|
| Type Safety | ❌ String only | ✅ URI validation |
| URL Validation | ❌ Manual | ✅ Automatic |
| Extensibility | ❌ Limited | ✅ Easy to add fields |
| Domain Clarity | ❌ Generic Map | ✅ Explicit SocialLink |
| Error Messages | ❌ Generic | ✅ Specific (URI format) |
| Platform Case | ❌ Case-sensitive | ✅ Case-insensitive |

---

## Timeline

1. **Phase 1: Code Migration** (Week 1)
   - Deploy new code with both formats supported
   - Update all service layer code
   - Update REST API endpoints

2. **Phase 2: Database Migration** (Week 2)
   - Run migration script in staging
   - Verify data integrity
   - Run migration in production

3. **Phase 3: Cleanup** (Week 3)
   - Remove old `SocialLinks` class
   - Update documentation
   - Remove compatibility code

---

## Troubleshooting

### Issue: Invalid URL Format Error
**Error:** `IllegalArgumentException: Invalid URL format`

**Solution:** Ensure all URLs have `http://` or `https://` scheme
```java
// Wrong
new SocialLink("github", "github.com/user");

// Correct
new SocialLink("github", "https://github.com/user");
```

### Issue: Database Migration Failed
**Error:** JSON conversion error

**Solution:** Check for null or malformed JSON in database
```sql
-- Find problematic records
SELECT id, first_name, social_links
FROM users
WHERE social_links IS NOT NULL
  AND (social_links = '' OR social_links = 'null' OR social_links !~ '^\{.*\}$');
```

### Issue: Platform Not Found
**Error:** `null` returned from `getSocialLinkByPlatform()`

**Solution:** Platform comparison is case-insensitive
```java
// Both work
user.addSocialLink("GitHub", "url");
user.getSocialLinkByPlatform("github"); // Still finds it
```

---

## Support

For questions or issues:
1. Check `SOCIAL_LINKS_USAGE_EXAMPLES.md` for usage patterns
2. Check `JSON_STORAGE_DESIGN_DECISIONS.md` for design rationale
3. Review unit tests in `UserTest.java`

