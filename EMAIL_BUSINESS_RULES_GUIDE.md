# Email Business Rules - Where They Belong in DDD

## 🎯 The Golden Rule

**Value Objects validate their OWN structure and format.**
**Entities/Services validate RELATIONSHIPS and CONTEXT.**

## 📋 Email Business Rules Breakdown

### ✅ In Email Value Object (Format/Structure Rules)

These rules are about the **EMAIL ITSELF**, independent of any context:

| Rule | Example | Why in VO? |
|------|---------|------------|
| **Format validation** | Must match regex pattern | Intrinsic to email |
| **Length constraints** | Local part ≤ 64 chars, domain ≤ 255 chars | Email standard (RFC 5321) |
| **Character rules** | Allowed characters in local/domain parts | Email standard |
| **Structure rules** | Must have exactly one @ symbol | Email format rule |
| **Domain format** | Must have at least one dot in domain | Email structure |
| **Case normalization** | Store as lowercase | Email comparison standard |

**Code Example:**
```java
public class Email {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    public Email(String email) {
        // ✅ Validate format - belongs here
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // ✅ Validate length - belongs here
        String[] parts = email.split("@");
        if (parts[0].length() > 64) {
            throw new IllegalArgumentException("Local part too long");
        }
        
        this.emailAddress = email.toLowerCase(); // ✅ Normalize
    }
    
    // ✅ Helper methods for structure
    public String getDomain() {
        return emailAddress.substring(emailAddress.indexOf('@') + 1);
    }
}
```

---

### ✅ In User Entity (Aggregate-Level Rules)

These rules involve the **USER'S RELATIONSHIP** with their email:

| Rule | Example | Why in Entity? |
|------|---------|----------------|
| **Business logic** | User can't change email within 30 days | User-specific rule |
| **State management** | Email verification status | User state |
| **History tracking** | Track email change attempts | User behavior |
| **Access control** | Only user or admin can change email | User permissions |

**Code Example:**
```java
public class User {
    private Email email;
    private boolean emailVerified;
    private LocalDateTime lastEmailChangeDate;
    
    // ✅ Business rule at entity level
    public void updateEmail(Email newEmail) {
        if (this.isBlocked) {
            throw new BusinessException("Blocked users cannot change email");
        }
        
        // ✅ Business rule: rate limiting
        if (lastEmailChangeDate != null && 
            lastEmailChangeDate.plusDays(30).isAfter(LocalDateTime.now())) {
            throw new BusinessException("Email can only be changed once per 30 days");
        }
        
        this.email = newEmail;
        this.emailVerified = false; // Reset verification
        this.lastEmailChangeDate = LocalDateTime.now();
    }
    
    // ✅ Business method
    public void verifyEmail() {
        this.emailVerified = true;
    }
}
```

---

### ✅ In Application Service (Cross-Cutting Rules)

These rules require **EXTERNAL DEPENDENCIES** or **APPLICATION CONTEXT**:

| Rule | Example | Why in App Service? |
|------|---------|---------------------|
| **Uniqueness** | Email must be unique across all users | Requires repository |
| **Domain whitelist** | Only @company.com emails allowed | Application policy |
| **Domain blacklist** | No disposable email domains | Application policy |
| **External validation** | Verify email via SMTP | Infrastructure concern |
| **Rate limiting** | Max 3 email changes per year | Application-wide policy |

**Code Example:**
```java
@Service
public class UserApplicationService {
    private final UserRepository userRepository;
    private final EmailDomainPolicy emailDomainPolicy;
    
    public void updateUserEmail(UUID userId, String newEmailAddress) {
        // Step 1: Validate format using Email VO
        Email newEmail = new Email(newEmailAddress); // ✅ Format validation
        
        // Step 2: ✅ Check uniqueness (requires repository)
        if (userRepository.existsByEmail(newEmailAddress)) {
            throw new DuplicateEmailException("Email already in use");
        }
        
        // Step 3: ✅ Check domain policy (application rule)
        if (!emailDomainPolicy.isAllowedDomain(newEmail.getDomain())) {
            throw new InvalidDomainException(
                "Email domain not allowed: " + newEmail.getDomain()
            );
        }
        
        // Step 4: ✅ Check against blacklist (application rule)
        if (emailDomainPolicy.isDisposableEmailDomain(newEmail.getDomain())) {
            throw new InvalidDomainException("Disposable email addresses not allowed");
        }
        
        // Step 5: Load user and update
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.updateEmail(newEmail); // ✅ Entity validates its own rules
        
        // Step 6: Save
        userRepository.save(user);
        
        // Step 7: Send verification email
        emailService.sendVerificationEmail(user);
    }
}
```

---

### ✅ In Domain Service (Multi-Entity Rules)

These rules involve **MULTIPLE ENTITIES** or complex domain logic:

| Rule | Example | Why in Domain Service? |
|------|---------|------------------------|
| **Cross-entity** | Transfer email ownership between users | Involves 2+ entities |
| **Complex logic** | Merge users with same email domain | Domain logic spanning entities |
| **Coordination** | Update email across User + Profile + Account | Multiple aggregates |

**Code Example:**
```java
@Service
public class UserDomainService {
    
    /**
     * Business rule: Users from the same company (email domain) 
     * can be grouped together for organization features.
     */
    public boolean areFromSameOrganization(User user1, User user2) {
        // ✅ Uses Email VO's getDomain() method
        String domain1 = user1.getEmail().getDomain();
        String domain2 = user2.getEmail().getDomain();
        
        return domain1.equals(domain2);
    }
    
    /**
     * Business rule: Merge users with duplicate emails
     * (might happen in data migration scenarios).
     */
    public User mergeUsersWithSameEmail(User primary, User secondary) {
        // ✅ Validate they have the same email
        if (!primary.getEmail().equals(secondary.getEmail())) {
            throw new IllegalArgumentException("Users must have the same email to merge");
        }
        
        // Complex merge logic involving both entities
        // ...
        
        return primary;
    }
}
```

---

## 📊 Quick Decision Tree

```
Is this rule about...

├─ Email FORMAT/STRUCTURE?
│  └─ ✅ Email Value Object
│     Examples: regex, length, @ symbol, domain format
│
├─ THIS USER's relationship with email?
│  └─ ✅ User Entity
│     Examples: verification status, change frequency, permissions
│
├─ MULTIPLE USERS or external dependencies?
│  └─ ✅ Application Service or Domain Service
│     Examples: uniqueness, domain policies, multi-user operations
│
└─ INFRASTRUCTURE concerns?
   └─ ✅ Infrastructure Layer
      Examples: SMTP validation, email sending, storage
```

---

## 💡 Real-World Examples

### Example 1: Corporate Email Policy

**Scenario:** Only employees with @company.com can register.

**Solution:**
```java
// ✅ Email VO: Validates format
Email email = new Email("john@company.com"); // Format validation

// ✅ Application Service: Validates domain policy
if (!email.getDomain().equals("company.com")) {
    throw new InvalidDomainException("Only @company.com emails allowed");
}

// ✅ User Entity: Creates user
User user = new User(uuid, name, email, mobile, hashedPassword);
```

### Example 2: Email Change Verification

**Scenario:** User wants to change email, must verify new email first.

**Solution:**
```java
// ✅ Email VO: Validates format
Email newEmail = new Email("newemail@example.com");

// ✅ Application Service: Checks uniqueness
if (userRepository.existsByEmail(newEmail.getEmailAddress())) {
    throw new DuplicateEmailException();
}

// ✅ User Entity: Updates email and resets verification
user.updateEmail(newEmail); // Sets emailVerified = false

// ✅ Application Service: Sends verification
emailService.sendVerificationEmail(user);
```

### Example 3: Organization Grouping

**Scenario:** Group users from same company by email domain.

**Solution:**
```java
// ✅ Domain Service: Cross-entity logic
public List<User> getUsersFromSameOrganization(User user) {
    String userDomain = user.getEmail().getDomain(); // ✅ Uses Email VO
    
    return userRepository.findByEmailDomain(userDomain);
}
```

---

## ✅ Your Current Email Value Object - Assessment

### What's Already Good ✅
- Format validation (regex)
- Null/empty check
- Immutability (final field)
- equals() method

### What I Improved ✅
- ✅ Added `hashCode()` method (important for collections)
- ✅ Added getter `getEmailAddress()`
- ✅ Added `getDomain()` helper (useful for domain policies)
- ✅ Added `getLocalPart()` helper
- ✅ Email normalization (lowercase)
- ✅ Better validation (length checks, structure validation)
- ✅ Better error messages
- ✅ Documentation explaining what belongs where

---

## 🎯 Summary

| Layer | Email Business Rules |
|-------|---------------------|
| **Email Value Object** | Format, structure, length, character rules |
| **User Entity** | Verification status, change frequency, user-specific rules |
| **Application Service** | Uniqueness, domain policies, rate limiting |
| **Domain Service** | Multi-user operations, cross-entity logic |
| **Infrastructure** | SMTP validation, email sending, external services |

---

## 🔑 Key Takeaway

**Value Objects = Validation of SELF**
**Entities = Business logic involving SELF**
**Services = Logic involving OTHERS or EXTERNAL concerns**

Your Email value object should know:
- ✅ "Am I a valid email format?"
- ✅ "What is my domain?"
- ✅ "What is my local part?"

Your Email value object should NOT know:
- ❌ "Am I unique in the system?" (requires repository)
- ❌ "Is my domain allowed?" (application policy)
- ❌ "Has the user changed me recently?" (user state)

Keep the boundaries clear, and your code will be clean, maintainable, and testable! 🎉

