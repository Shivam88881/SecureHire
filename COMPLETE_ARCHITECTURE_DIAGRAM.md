# Complete Architecture Diagram

## System Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                           API Gateway                                │
│                    (Authentication, Routing)                         │
└────────────────┬────────────────────────┬───────────────────────────┘
                 │                        │
                 ▼                        ▼
    ┌────────────────────────┐  ┌────────────────────────┐
    │    AuthService         │  │    UserService         │
    │    Port: 8081          │  │    Port: 8080          │
    └────────────────────────┘  └────────────────────────┘
                 │                        │
                 ▼                        ▼
    ┌────────────────────────┐  ┌────────────────────────┐
    │  auth_db (PostgreSQL)  │  │  user_db (PostgreSQL)  │
    │  Port: 5433            │  │  Port: 5432            │
    └────────────────────────┘  └────────────────────────┘
```

---

## UserService Internal Architecture (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  REST Controllers                                             │   │
│  │  - UserProfileController                                      │   │
│  │  - UserRegistrationController                                 │   │
│  │                                                                │   │
│  │  DTOs (Data Transfer Objects)                                 │   │
│  │  - UserCreateRequest, UserProfileResponse                     │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Application Services (Use Cases)                            │   │
│  │  - UserApplicationService                                     │   │
│  │                                                                │   │
│  │  Policies                                                      │   │
│  │  - EmailDomainPolicy                                          │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         DOMAIN LAYER                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Entities (Aggregate Roots)                                  │   │
│  │  ┌────────────────────────────────────────────────────┐     │   │
│  │  │  User                                               │     │   │
│  │  │  - final UUID id                                    │     │   │
│  │  │  - final UUID userId                                │     │   │
│  │  │  - final String firstName                           │     │   │
│  │  │  - final String lastName                            │     │   │
│  │  │  - Email email                                      │     │   │
│  │  │  - Mobile mobile                                    │     │   │
│  │  │  - Address address                                  │     │   │
│  │  │  - SocialLinks socialLinks                          │     │   │
│  │  │  - AccountStatus accountStatus                      │     │   │
│  │  │  - Role role                                        │     │   │
│  │  │                                                      │     │   │
│  │  │  Methods:                                            │     │   │
│  │  │  + verifyEmail()                                    │     │   │
│  │  │  + changeAccountStatus(String)                      │     │   │
│  │  │  + updateAddress(Address)                           │     │   │
│  │  │  + updateSocialLinks(SocialLinks)                   │     │   │
│  │  └────────────────────────────────────────────────────┘     │   │
│  │                                                                │   │
│  │  Value Objects (Immutable)                                    │   │
│  │  - Email                                                       │   │
│  │  - Mobile                                                      │   │
│  │  - Address ────────► Stored as JSON                          │   │
│  │  - SocialLinks ────► Stored as JSON                          │   │
│  │  - AccountStatus                                              │   │
│  │  - Role                                                        │   │
│  │  - Password (moved to AuthService)                           │   │
│  │  - Resume                                                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Persistence                                                  │   │
│  │                                                                │   │
│  │  ┌──────────────────────────────────────────────────┐        │   │
│  │  │  UserEntity (JPA Entity)                         │        │   │
│  │  │  @Table("users")                                 │        │   │
│  │  │                                                   │        │   │
│  │  │  @Column("address", columnDefinition="TEXT")    │        │   │
│  │  │  @Convert(converter=AddressJsonConverter)       │        │   │
│  │  │  private Address address;                        │        │   │
│  │  │                                                   │        │   │
│  │  │  @Column("social_links", columnDefinition="TEXT")│       │   │
│  │  │  @Convert(converter=SocialLinksJsonConverter)   │        │   │
│  │  │  private SocialLinks socialLinks;                │        │   │
│  │  └──────────────────────────────────────────────────┘        │   │
│  │                                                                │   │
│  │  JPA Converters (JSON Serialization)                         │   │
│  │  - AddressJsonConverter: Address ←→ JSON String              │   │
│  │  - SocialLinksJsonConverter: SocialLinks ←→ JSON String      │   │
│  │                                                                │   │
│  │  Repositories                                                  │   │
│  │  - UserJpaRepository (Spring Data JPA)                       │   │
│  │                                                                │   │
│  │  Adapters                                                      │   │
│  │  - UserPersistenceAdapter                                     │   │
│  │                                                                │   │
│  │  Mappers                                                       │   │
│  │  - UserMapper: User (Domain) ←→ UserEntity (JPA)            │   │
│  │                                                                │   │
│  │  Security                                                      │   │
│  │  - PasswordEncoder (moved to AuthService)                    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │   PostgreSQL DB   │
                    │   user_profiles   │
                    └──────────────────┘
```

---

## Database Schema

```sql
CREATE TABLE users (
    id                UUID PRIMARY KEY,           -- User entity ID
    user_id           UUID UNIQUE NOT NULL,       -- Reference to AuthService
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    email             VARCHAR(255) UNIQUE NOT NULL,
    mobile            VARCHAR(20) NOT NULL,
    country_code      VARCHAR(10) NOT NULL,
    email_verified    BOOLEAN DEFAULT FALSE,
    account_status    VARCHAR(20) NOT NULL,       -- ACTIVE, INACTIVE, SUSPENDED, DELETED
    role              VARCHAR(20) NOT NULL,       -- JOBSEEKER, EMPLOYER, ADMIN, SUPERADMIN
    
    -- JSON COLUMNS (TEXT type)
    address           TEXT,                       -- {"street":"...","city":"...",...}
    social_links      TEXT,                       -- {"links":{"linkedin":"...","github":"..."}}
    
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);
```

---

## Data Flow Examples

### 1. User Registration Flow

```
┌─────────┐      ┌──────────────┐      ┌──────────────┐
│ Client  │      │ AuthService  │      │ UserService  │
└────┬────┘      └──────┬───────┘      └──────┬───────┘
     │                  │                     │
     │ 1. POST /auth/register               │
     │ {email, password}                     │
     ├─────────────────>│                     │
     │                  │                     │
     │                  │ 2. Hash password    │
     │                  │    Save to auth_db  │
     │                  │                     │
     │ 3. Return userId │                     │
     │<─────────────────┤                     │
     │                  │                     │
     │ 4. POST /users/profile                │
     │ {userId, firstName, lastName, etc}    │
     ├───────────────────────────────────────>│
     │                  │                     │
     │                  │ 5. Create User entity
     │                  │    (with Address JSON)
     │                  │    Save to user_db  │
     │                  │                     │
     │ 6. Return profile│                     │
     │<───────────────────────────────────────┤
     │                  │                     │
```

### 2. Get User Profile Flow

```
┌─────────┐      ┌──────────────┐      ┌──────────────┐
│ Client  │      │ API Gateway  │      │ UserService  │
└────┬────┘      └──────┬───────┘      └──────┬───────┘
     │                  │                     │
     │ 1. GET /users/{userId}/profile        │
     │    Authorization: Bearer <token>      │
     ├─────────────────>│                     │
     │                  │                     │
     │                  │ 2. Validate JWT     │
     │                  │                     │
     │                  │ 3. Forward request  │
     │                  ├────────────────────>│
     │                  │                     │
     │                  │ 4. Query DB         │
     │                  │    (Address JSON → Address object)
     │                  │    (SocialLinks JSON → SocialLinks object)
     │                  │                     │
     │                  │ 5. Return profile   │
     │                  │<────────────────────┤
     │                  │                     │
     │ 6. Return to client                   │
     │<─────────────────┤                     │
     │                  │                     │
```

### 3. Update Address Flow

```
┌─────────┐      ┌──────────────┐      ┌──────────────┐
│ Client  │      │ UserService  │      │ PostgreSQL   │
└────┬────┘      └──────┬───────┘      └──────┬───────┘
     │                  │                     │
     │ 1. PUT /users/{userId}/address        │
     │ {street, city, state, zipCode, country}
     ├─────────────────>│                     │
     │                  │                     │
     │                  │ 2. Load User entity │
     │                  ├────────────────────>│
     │                  │                     │
     │                  │ 3. Return UserEntity│
     │                  │    (address as JSON string)
     │                  │<────────────────────┤
     │                  │                     │
     │                  │ 4. Converter: JSON → Address object
     │                  │ 5. user.updateAddress(newAddress)
     │                  │ 6. Converter: Address object → JSON
     │                  │                     │
     │                  │ 7. UPDATE users SET address = '{"street":...}'
     │                  ├────────────────────>│
     │                  │                     │
     │                  │ 8. Confirm          │
     │                  │<────────────────────┤
     │                  │                     │
     │ 9. Return success│                     │
     │<─────────────────┤                     │
     │                  │                     │
```

---

## JSON Conversion Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                        Java Layer                                 │
│                                                                   │
│  Address address = new Address(                                  │
│      "123 Main St", "New York", "NY", "10001", "USA"            │
│  );                                                               │
│  user.updateAddress(address);                                    │
│                                                                   │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           │ AddressJsonConverter.convertToDatabaseColumn()
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                     JSON String (in memory)                       │
│                                                                   │
│  {"street":"123 Main St","city":"New York","state":"NY",        │
│   "zipCode":"10001","country":"USA"}                             │
│                                                                   │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           │ JPA saves to database
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                     PostgreSQL (TEXT column)                      │
│                                                                   │
│  users table:                                                     │
│  ┌──────┬────────────┬──────────────────────────────────────┐   │
│  │ id   │ first_name │ address                              │   │
│  ├──────┼────────────┼──────────────────────────────────────┤   │
│  │ ... │ John       │ {"street":"123 Main St","city":...}  │   │
│  └──────┴────────────┴──────────────────────────────────────┘   │
│                                                                   │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           │ JPA loads from database
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                     JSON String (in memory)                       │
│                                                                   │
│  {"street":"123 Main St","city":"New York","state":"NY",        │
│   "zipCode":"10001","country":"USA"}                             │
│                                                                   │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           │ AddressJsonConverter.convertToEntityAttribute()
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                        Java Layer                                 │
│                                                                   │
│  Address address = user.getAddress();                            │
│  String city = address.getCity(); // "New York"                 │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Key Design Decisions

### 1. Final Fields
```java
✅ final UUID id;              // Immutable identifier
✅ final UUID userId;          // Immutable reference
✅ final String firstName;     // Immutable for simplicity
✅ final String lastName;      // Immutable for simplicity
```

### 2. JSON Storage
```
Address + SocialLinks stored as JSON because:
✅ Flexible schema (can evolve)
✅ Cohesive data (always fetched together)
✅ Good performance (single query)
✅ Type-safe in Java (via value objects + converters)
```

### 3. Service Separation
```
AuthService:          UserService:
- Password            - Profile data
- Login/Logout        - Address
- JWT tokens          - Social links
- 2FA                 - Business info

Connected via userId reference
```

---

## Summary

**Your architecture is:**
- ✅ **Clean** - Proper layering (Domain → Application → Infrastructure)
- ✅ **Type-safe** - Value objects enforce validation
- ✅ **Flexible** - JSON storage for semi-structured data
- ✅ **Secure** - Auth separated from profile data
- ✅ **Scalable** - Microservices can scale independently
- ✅ **Maintainable** - Clear separation of concerns

**This is production-ready!** 🚀

