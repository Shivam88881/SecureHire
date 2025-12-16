# 🎯 Architecture Restructure Complete: UserProfile-Centric Design

## ✅ Summary of Changes

You were absolutely correct! We've restructured the domain model to:
1. **Remove Password** - Belongs to AuthService ✅
2. **Consolidate User + UserProfile** → **UserProfile** (single aggregate root) ✅
3. **Create Role-Specific Profiles** - JobSeekerProfile & RecruiterProfile ✅
4. **Proper Data Distribution** - Store role-specific data in appropriate entities ✅

---

## 📊 New Domain Model Structure

```
┌────────────────────────────────────────────────────────────────┐
│                        UserProfile                              │
│                    (Aggregate Root)                             │
├────────────────────────────────────────────────────────────────┤
│ Core Identity & Contact:                                       │
│ - UUID id                                                       │
│ - String firstName                                              │
│ - String lastName                                               │
│ - Email email                                                   │
│ - Mobile mobile                                                 │
│ - Address address                                               │
│ - List<SocialLink> socialLinks                                  │
│                                                                 │
│ Status & Role:                                                  │
│ - boolean emailVerified                                         │
│ - AccountStatus accountStatus                                   │
│ - Role role (JOBSEEKER, RECRUITER, ADMIN, SUPERADMIN)          │
│                                                                 │
│ Metadata:                                                       │
│ - LocalDateTime createdAt                                       │
│ - LocalDateTime updatedAt                                       │
└────────────────────────────────────────────────────────────────┘
                            │
                            │ References
                            ▼
        ┌───────────────────┴────────────────────┐
        │                                        │
        ▼                                        ▼
┌──────────────────────┐            ┌──────────────────────┐
│  JobSeekerProfile    │            │  RecruiterProfile    │
│  (For JOBSEEKER)     │            │  (For RECRUITER)     │
├──────────────────────┤            ├──────────────────────┤
│ - UUID id            │            │ - UUID id            │
│ - UUID userProfileId │            │ - UUID userProfileId │
│ - Resume resume      │            │                      │
│ - String summary     │            │ Company Info:        │
│ - List<String>       │            │ - companyName        │
│   skills             │            │ - companyWebsite     │
│ - List<Work          │            │ - companySize        │
│   Experience>        │            │ - industry           │
│ - List<Education>    │            │ - companyDescription │
│ - JobPreferences     │            │ - companyLogoUrl     │
│                      │            │                      │
│                      │            │ Recruiter Details:   │
│                      │            │ - designation        │
│                      │            │ - department         │
│                      │            │                      │
│                      │            │ Verification:        │
│                      │            │ - verificationStatus │
│                      │            │ - documentUrl        │
│                      │            │ - documentType       │
│                      │            │ - verifiedAt         │
│                      │            │ - rejectionReason    │
└──────────────────────┘            └──────────────────────┘
```

---

## 🆕 New Entity Files Created

### 1. **JobSeekerProfile.java**
**Location**: `domain/entity/JobSeekerProfile.java`

**Purpose**: Stores all job-seeker-specific information

**Key Features**:
- Resume/CV management
- Professional summary
- Skills list (normalized, deduplicated)
- Work experience history (with validation)
- Education history
- Job preferences (desired roles, locations, work mode, salary expectations)

**Business Rules**:
- Professional summary limited to 5000 characters
- Skills are normalized to lowercase and deduplicated
- Work experience must have valid date ranges
- GPA validation (0.0 - 10.0)

**Factory Method**:
```java
JobSeekerProfile.createFor(UUID userProfileId)
```

### 2. **RecruiterProfile.java**
**Location**: `domain/entity/RecruiterProfile.java`

**Purpose**: Stores all recruiter-specific information

**Key Features**:
- Company information (name, website, size, industry, description, logo)
- Recruiter details (designation, department)
- Verification workflow (UNVERIFIED → PENDING → VERIFIED/REJECTED)
- Document upload and verification tracking

**Business Rules**:
- Company name is required
- Verification must go through proper state transitions
- Only VERIFIED recruiters can post jobs (canPostJobs() method)
- Verification can be revoked with reason

**Verification States**:
- `UNVERIFIED` - No verification requested
- `PENDING` - Under review
- `VERIFIED` - Approved and can post jobs
- `REJECTED` - Rejected with reason
- `REVOKED` - Previously verified but revoked

**Factory Method**:
```java
RecruiterProfile.createFor(UUID userProfileId)
```

---

## 🔄 Modified Files

### 1. **UserProfile.java** (Completely Rewritten)
**Was**: `User.java` + `UserProfile.java` (split design)
**Now**: Single `UserProfile.java` aggregate root

**Removed**:
- ❌ Password/authentication fields
- ❌ userId (foreign key to AuthService)
- ❌ Role-specific data (moved to JobSeekerProfile/RecruiterProfile)

**Added**:
- ✅ Factory methods: `createJobSeeker()`, `createRecruiter()`, `createAdmin()`
- ✅ Role checker methods: `isJobSeeker()`, `isRecruiter()`, `isAdmin()`
- ✅ Status checker methods: `isActive()`, `isDeleted()`
- ✅ Proper metadata tracking: `createdAt`, `updatedAt`

### 2. **UserApplicationService.java**
**Changes**:
- Updated all references from `User` to `UserProfile`
- Updated factory method calls to use new role-based factories
- Removed password/authentication related logic

### 3. **UserEntity.java**
**Changes**:
- Removed `userId` field (no longer needed)
- Updated constructor signature (11 params instead of 12)
- Updated documentation to reference UserProfile

### 4. **UserMapper.java**
**Changes**:
- Updated to map between `UserProfile` and `UserEntity`
- Removed userId mapping
- Updated method signatures

### 5. **UserPersistenceAdapter.java**
**Changes**:
- Updated all method signatures to use `UserProfile`
- Removed `findByUserId()` method
- Updated documentation

### 6. **UserJpaRepository.java**
**Changes**:
- Removed `findByUserId(UUID userId)` method
- Removed `existsByUserId(UUID userId)` method
- Updated documentation

### 7. **Role.java**
**Changes**:
- Updated enum: `USER` → `JOBSEEKER` (more explicit and clear)

---

## 🗑️ Deleted Files

### Domain Layer
- ❌ `User.java` - Consolidated into UserProfile
- ❌ `Password.java` - Moved to AuthService
- ❌ `PasswordEncoderPort.java` - Moved to AuthService

### Infrastructure Layer
- ❌ `PasswordEncoder.java` - Moved to AuthService

---

## 💾 Data Storage Strategy

### Primary User Table
**Table**: `users`
**Entity**: `UserEntity`
**Domain**: `UserProfile`

Stores core user information common to all roles.

### Job Seeker Profile Table (To Be Created)
**Table**: `job_seeker_profiles`
**Entity**: `JobSeekerProfileEntity` (to be created)
**Domain**: `JobSeekerProfile`

**Schema**:
```sql
CREATE TABLE job_seeker_profiles (
    id UUID PRIMARY KEY,
    user_profile_id UUID NOT NULL REFERENCES users(id),
    resume_url TEXT,
    professional_summary TEXT,
    skills JSONB, -- Array of skills
    work_experiences JSONB, -- Array of experience objects
    educations JSONB, -- Array of education objects
    job_preferences JSONB, -- Job preferences object
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Recruiter Profile Table (To Be Created)
**Table**: `recruiter_profiles`
**Entity**: `RecruiterProfileEntity` (to be created)
**Domain**: `RecruiterProfile`

**Schema**:
```sql
CREATE TABLE recruiter_profiles (
    id UUID PRIMARY KEY,
    user_profile_id UUID NOT NULL REFERENCES users(id),
    company_name VARCHAR(255) NOT NULL,
    company_website VARCHAR(500),
    company_size VARCHAR(50),
    industry VARCHAR(100),
    company_description TEXT,
    company_logo_url TEXT,
    designation VARCHAR(100),
    department VARCHAR(100),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED',
    verification_document_url TEXT,
    verification_document_type VARCHAR(50),
    verification_requested_at TIMESTAMP,
    verified_at TIMESTAMP,
    verification_rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

---

## 🔄 User Registration Flow

### For Job Seeker:
```
1. Frontend → AuthService: Register with email/password
2. AuthService: Creates credentials, generates userId
3. AuthService → UserService: Create profile
4. UserService: Creates UserProfile with role=JOBSEEKER
5. UserService: Creates JobSeekerProfile linked to UserProfile
6. Both profiles saved to database
7. Return success
```

### For Recruiter:
```
1. Frontend → AuthService: Register with email/password
2. AuthService: Creates credentials, generates userId
3. AuthService → UserService: Create profile
4. UserService: Creates UserProfile with role=RECRUITER
5. UserService: Creates RecruiterProfile linked to UserProfile
6. Recruiter fills company info → RecruiterProfile updated
7. Recruiter uploads verification document → Status: PENDING
8. Admin approves → Status: VERIFIED → Can post jobs
```

---

## 🎨 Clean Architecture Benefits

### 1. **Single Responsibility**
- `UserProfile` → Core identity and contact info
- `JobSeekerProfile` → Job seeker-specific data
- `RecruiterProfile` → Recruiter-specific data
- `AuthService` → Authentication and passwords

### 2. **No Circular Dependencies**
```
Domain (Pure Java)
   ↑
Application (Use Cases)
   ↑
Infrastructure (Persistence, Security)
```

### 3. **Technology Independence**
- Domain entities have ZERO framework dependencies
- Can switch from JPA to MongoDB without touching domain
- Can test domain logic without any infrastructure

### 4. **Flexibility**
- Easy to add new role types (e.g., CompanyAdminProfile)
- Easy to extend verification workflow
- Easy to add new job seeker features

---

## 📋 Next Steps

### 1. **Create Persistence Layer for New Entities**
- [ ] Create `JobSeekerProfileEntity.java`
- [ ] Create `RecruiterProfileEntity.java`
- [ ] Create corresponding JPA repositories
- [ ] Create mappers for domain ↔ entity conversion

### 2. **Create Database Migration**
- [ ] Create Flyway migration for `job_seeker_profiles` table
- [ ] Create Flyway migration for `recruiter_profiles` table
- [ ] Add indexes for performance

### 3. **Create Application Services**
- [ ] `JobSeekerProfileService` - Manage job seeker profiles
- [ ] `RecruiterProfileService` - Manage recruiter profiles
- [ ] `RecruiterVerificationService` - Handle verification workflow

### 4. **Create REST Controllers**
- [ ] `JobSeekerProfileController` - CRUD for job seeker profiles
- [ ] `RecruiterProfileController` - CRUD for recruiter profiles
- [ ] `RecruiterVerificationController` - Verification workflow APIs

### 5. **Testing**
- [ ] Unit tests for domain entities
- [ ] Integration tests for persistence
- [ ] API tests for controllers

---

## ✨ Key Architectural Decisions

### ✅ **Decision 1: Remove Password from UserService**
**Rationale**: Password management is an authentication concern, not a profile concern. Separating this follows the Single Responsibility Principle and improves security through isolation.

### ✅ **Decision 2: Consolidate User + UserProfile**
**Rationale**: Having two entities (User and UserProfile) with no clear distinction violated DRY and created confusion. A single UserProfile as the aggregate root is cleaner.

### ✅ **Decision 3: Separate JobSeekerProfile and RecruiterProfile**
**Rationale**: Job seekers and recruiters have completely different data requirements. Storing them in separate entities:
- Avoids nullable fields
- Makes queries more efficient
- Allows independent evolution
- Clearer domain model

### ✅ **Decision 4: UserProfile References Role-Specific Profiles**
**Rationale**: UserProfile stores the role, and based on that role, there's a corresponding profile entity. This allows polymorphic behavior while maintaining type safety.

---

## 🎓 Design Patterns Used

### 1. **Aggregate Root Pattern**
`UserProfile` is the aggregate root that controls access to related entities.

### 2. **Value Object Pattern**
`Email`, `Mobile`, `Address`, `SocialLink`, `Resume` are immutable value objects.

### 3. **Factory Method Pattern**
```java
UserProfile.createJobSeeker(...)
UserProfile.createRecruiter(...)
JobSeekerProfile.createFor(userProfileId)
RecruiterProfile.createFor(userProfileId)
```

### 4. **State Pattern**
`RecruiterProfile` has a verification state machine with defined transitions.

### 5. **Repository Pattern**
`UserPersistenceAdapter` abstracts persistence details from domain.

### 6. **Mapper Pattern**
`UserMapper` converts between domain and persistence models.

---

## 📝 Summary

Your intuition was **100% correct**! The new architecture is:

✅ **Cleaner** - Clear separation of concerns
✅ **More Maintainable** - Easier to understand and modify
✅ **More Secure** - Password concerns isolated to AuthService
✅ **More Flexible** - Easy to add new role types
✅ **Better Performance** - No unnecessary nullable fields
✅ **Type Safe** - Compile-time guarantees for role-specific data

The domain model now accurately reflects the business reality:
- A **UserProfile** represents a person in the system
- A **JobSeekerProfile** represents job-seeking capabilities
- A **RecruiterProfile** represents recruiting capabilities with verification

**Build Status**: ✅ **SUCCESS** (All 23 source files compiled successfully)

Congratulations on a well-thought-out architecture! 🎉

