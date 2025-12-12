# Visual Architecture Summary

## Complete User Entity Design

```
┌─────────────────────────────────────────────────────────────────┐
│                         User Entity                             │
│                    (Aggregate Root)                             │
├─────────────────────────────────────────────────────────────────┤
│  IDENTITY FIELDS (final - Immutable)                            │
│  ┌──────────────────────────────────────────────────────┐       │
│  │  - id: UUID (final)                                  │       │
│  │  - userId: UUID (final)                              │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                 │
│  MUTABLE FIELDS (Can change via business methods)               │
│  ┌──────────────────────────────────────────────────────┐       │
│  │  - firstName: String                                 │       │
│  │  - lastName: String                                  │       │
│  │  - email: Email (value object)                       │       │
│  │  - mobile: Mobile (value object)                     │       │
│  │  - emailVerified: boolean                            │       │
│  │  - accountStatus: AccountStatus (value object)       │       │
│  │  - role: Role (value object)                         │       │
│  │  - address: Address (JSON) ◄───┐                     │       │
│  │  - socialLinks: List<SocialLink> (JSON) ◄───┐        │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                  │       │      │
└──────────────────────────────────────────────────┼───────┼──────┘
                                                   │       │
                   ┌───────────────────────────────┘       │
                   │                                       │
                   ▼                                       ▼
        ┌──────────────────────┐           ┌──────────────────────┐
        │   Address (JSON)     │           │ List<SocialLink>     │
        │   Value Object       │           │   (JSON Array)       │
        ├──────────────────────┤           ├──────────────────────┤
        │ - street: String     │           │ [                    │
        │ - city: String       │           │   {                  │
        │ - state: String      │           │     platform: String │
        │ - zipCode: String    │           │     url: URI         │
        │ - country: String    │           │   },                 │
        └──────────────────────┘           │   ...                │
                                           │ ]                    │
                                           └──────────────────────┘
```

---

## Data Flow: Domain → Database

```
┌───────────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                                  │
│  (Pure Business Logic - No Infrastructure Dependencies)           │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│   User.java                                                       │
│   ├── id: UUID (final)                                            │
│   ├── firstName: String                                           │
│   ├── lastName: String                                            │
│   ├── address: Address (value object)                             │
│   └── socialLinks: List<SocialLink> (value objects)               │
│                                                                   │
└────────────────────────┬──────────────────────────────────────────┘
                         │
                         │ Mapper converts
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                             │
│        (JPA Entities + Converters)                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   UserEntity.java                                                │
│   ├── id: UUID                                                   │
│   ├── firstName: String                                          │
│   ├── lastName: String                                           │
│   ├── address: Address ──► AddressJsonConverter                 │
│   └── socialLinks: List<SocialLink> ──► SocialLinksJsonConverter│
│                                                                  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ JPA/Hibernate persists
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    DATABASE (PostgreSQL)                         │
├─────────────────────────────────────────────────────────────────┤
│  users table                                                     │
│  ├── id (UUID)                                                   │
│  ├── first_name (VARCHAR)                                        │
│  ├── last_name (VARCHAR)                                         │
│  ├── address (TEXT) ───► {"street":"...", "city":"...", ...}    │
│  └── social_links (TEXT) ───► [{"platform":"...", "url":"..."}] │
└─────────────────────────────────────────────────────────────────┘
```

---

## Service Separation Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      CLIENT (Web/Mobile)                         │
└──────────────┬────────────────────────────┬─────────────────────┘
               │                            │
               │ Profile API                │ Auth API
               │ (GET /users/profile)       │ (POST /auth/login)
               │                            │
               ▼                            ▼
┌──────────────────────────┐    ┌─────────────────────────┐
│    USER SERVICE          │    │    AUTH SERVICE         │
│  (Profile Management)    │    │  (Authentication)       │
├──────────────────────────┤    ├─────────────────────────┤
│                          │    │                         │
│  Domain:                 │    │  Domain:                │
│  • User (entity)         │    │  • AuthUser (entity)    │
│  • Email (VO)            │    │  • Password (VO)        │
│  • Address (VO)          │    │  • Token (VO)           │
│  • SocialLink (VO)       │    │                         │
│                          │    │  Responsibilities:      │
│  Responsibilities:       │    │  • Password hashing     │
│  • Profile CRUD          │    │  • Login/Logout         │
│  • Email verification    │    │  • Token generation     │
│  • Address management    │    │  • Password reset       │
│  • Social links          │    │  • 2FA                  │
│  • Account status        │    │  • Session management   │
│  • Role management       │    │                         │
│                          │    │                         │
└────────────┬─────────────┘    └──────────┬──────────────┘
             │                             │
             │                             │
             ▼                             ▼
┌──────────────────────────┐    ┌─────────────────────────┐
│   users (table)          │    │   auth_users (table)    │
│  • id                    │    │  • id                   │
│  • first_name            │    │  • user_id (FK)         │
│  • last_name             │    │  • password_hash        │
│  • email                 │    │  • login_attempts       │
│  • address (JSON)        │    │  • last_login           │
│  • social_links (JSON)   │    │  • refresh_token        │
└──────────────────────────┘    └─────────────────────────┘

         Separate Databases (or schemas)
         Independent Scaling
         Clear Boundaries
```

---

## SocialLinks Evolution

### OLD DESIGN ❌
```
┌─────────────────────────┐
│  SocialLinks (wrapper)  │
├─────────────────────────┤
│  links: Map<String,     │
│              String>    │
│                         │
│  {"github": "url",      │
│   "linkedin": "url"}    │
└─────────────────────────┘
        ▲
        │
   Problems:
   ❌ No type safety
   ❌ Manual URL validation
   ❌ Generic Map
   ❌ Hard to extend
```

### NEW DESIGN ✅
```
┌──────────────────────────────────┐
│  User                            │
│  socialLinks: List<SocialLink>   │
└────────────┬─────────────────────┘
             │
             │ contains
             │
             ▼
┌──────────────────────────────────┐
│  SocialLink (value object)       │
├──────────────────────────────────┤
│  - platform: String              │
│  - url: URI (type-safe!)         │
│                                  │
│  Validation:                     │
│  ✅ URI format check             │
│  ✅ Scheme validation (http/s)   │
│  ✅ Platform not empty           │
│  ✅ Case-insensitive matching    │
└──────────────────────────────────┘

Benefits:
✅ Type-safe URI
✅ Automatic validation
✅ Clear domain model
✅ Easy to extend
✅ Better API
```

---

## Field Immutability Decision Tree

```
                    ┌────────────────┐
                    │  Field Type?   │
                    └───────┬────────┘
                            │
            ┌───────────────┴────────────────┐
            │                                │
            ▼                                ▼
    ┌───────────────┐              ┌─────────────────┐
    │   IDENTITY    │              │  CHARACTERISTIC │
    │    FIELD      │              │      FIELD      │
    └───────┬───────┘              └────────┬────────┘
            │                               │
            │                               │
    ┌───────▼────────┐            ┌────────▼────────┐
    │  Make FINAL    │            │  Keep MUTABLE   │
    └────────────────┘            └─────────────────┘
            │                               │
            │                               │
     Examples:                       Examples:
     • id                            • email
     • firstName                     • mobile
     • lastName                      • emailVerified
     • userId                        • accountStatus
                                     • role
                                     • address
                                     • socialLinks

     Reason:                         Reason:
     • Never changes                 • Can change over
     • Part of identity                time
     • Thread-safe                   • Business logic
                                       requires updates
```

---

## JSON Storage Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                   VALUE OBJECT                               │
│           (Domain Layer - Pure Java)                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  @JsonCreator ◄── Pragmatic compromise for JSON storage     │
│  public Address(                                             │
│      @JsonProperty("street") String street,                  │
│      @JsonProperty("city") String city,                      │
│      ...                                                     │
│  ) {                                                         │
│      // Domain validation logic                              │
│      if (city == null) throw new Exception();                │
│      this.city = city;                                       │
│  }                                                           │
│                                                              │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  │ Used by
                  │
                  ▼
┌─────────────────────────────────────────────────────────────┐
│              JPA CONVERTER                                   │
│         (Infrastructure Layer)                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  @Converter                                                  │
│  public class AddressJsonConverter                           │
│      implements AttributeConverter<Address, String> {        │
│                                                              │
│      public String convertToDatabaseColumn(Address addr) {   │
│          return objectMapper.writeValueAsString(addr);       │
│      }                                                       │
│                                                              │
│      public Address convertToEntityAttribute(String json) {  │
│          return objectMapper.readValue(json, Address.class); │
│      }                                                       │
│  }                                                           │
│                                                              │
└─────────────────┬───────────────────────────────────────────┘
                  │
                  │ Persists as
                  │
                  ▼
┌─────────────────────────────────────────────────────────────┐
│                  DATABASE                                    │
│               (PostgreSQL)                                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  address (TEXT column)                                       │
│  {                                                           │
│    "street": "123 Main St",                                  │
│    "city": "San Francisco",                                  │
│    "state": "CA",                                            │
│    "zipCode": "94105",                                       │
│    "country": "USA"                                          │
│  }                                                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘

Why this works:
✅ Value objects ARE stored as JSON (natural fit)
✅ No duplicate DTOs needed (simpler)
✅ Domain validation still pure (no Jackson in logic)
✅ Common practice (widely accepted)
✅ Minimal coupling (only for serialization)
```

---

## Request Flow Example

```
CLIENT REQUEST
    │
    │ POST /api/users
    │ {
    │   "firstName": "John",
    │   "socialLinks": [
    │     {"platform": "github", "url": "https://github.com/john"}
    │   ]
    │ }
    │
    ▼
┌─────────────────────────┐
│   CONTROLLER            │
│   (Web Layer)           │
│                         │
│   UserController        │
│   - Receives DTO        │
│   - Validates input     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   APPLICATION SERVICE   │
│   (Use Case Layer)      │
│                         │
│   UserApplicationService│
│   - Orchestrates flow   │
│   - Converts DTO→Domain │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   DOMAIN LAYER          │
│   (Business Logic)      │
│                         │
│   User.createUser()     │
│   - Validates data      │
│   - Creates entity      │
│   - Business rules      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   PERSISTENCE ADAPTER   │
│   (Infrastructure)      │
│                         │
│   UserPersistenceAdapter│
│   - Domain → Entity     │
│   - Saves to DB         │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   DATABASE              │
│                         │
│   INSERT INTO users     │
│   social_links =        │
│   '[{...}]'::jsonb      │
└─────────────────────────┘
```

---

## Summary Checklist

### ✅ All Questions Answered
- [x] Should id/firstName/lastName be final? → YES
- [x] How to optimize changeAccountStatus()? → Use valueOf() + try-catch
- [x] Should User Service store passwords? → NO (Auth Service)
- [x] How to store JSON in PostgreSQL? → JPA Converters
- [x] Why Jackson annotations in domain? → Pragmatic for JSON storage
- [x] HashMap vs List<SocialLink>? → List is better

### ✅ Implementation Complete
- [x] Made identity fields final
- [x] Created SocialLink value object
- [x] Updated to List<SocialLink>
- [x] Updated JPA converters
- [x] Optimized validation methods
- [x] Added comprehensive documentation

### ✅ Code Quality
- [x] Compiles successfully (0 errors)
- [x] Type-safe (URI validation)
- [x] Immutable where appropriate
- [x] Clean Architecture followed
- [x] Separation of concerns maintained

### 📚 Documentation Created
- [x] QUESTIONS_ANSWERED.md
- [x] JSON_STORAGE_DESIGN_DECISIONS.md
- [x] SOCIAL_LINKS_USAGE_EXAMPLES.md
- [x] MIGRATION_GUIDE_SOCIALLINKS.md
- [x] IMPLEMENTATION_COMPLETE_SUMMARY.md
- [x] VISUAL_ARCHITECTURE_SUMMARY.md (this file)

---

## 🎉 YOU'RE READY!

Your User Service is now properly architected with:
✅ Immutable identity fields
✅ Type-safe social links
✅ Efficient JSON storage
✅ Clear separation from Auth Service
✅ Comprehensive documentation

**Next:** Write tests and implement REST API endpoints!

