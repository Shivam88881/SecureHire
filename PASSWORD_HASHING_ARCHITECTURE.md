# Password Hashing Architecture - Best Practices

## ✅ Correct Approach: Separation of Concerns

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│                    (Controller/API)                          │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓
┌─────────────────────────────────────────────────────────────┐
│                  APPLICATION LAYER                           │
│           (UserApplicationService)                           │
│  - Orchestrates use cases                                    │
│  - Validates uniqueness (email exists?)                      │
│  - Creates User with validated Password VO                   │
│  - Delegates to repository for persistence                   │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                              │
│                                                               │
│  User Entity:                                                │
│  - Contains business behavior (block, update)                │
│  - Constructor validates inputs                              │
│                                                               │
│  Password Value Object:                                      │
│  - Validates format (regex, length, complexity)              │
│  - Stores plain text temporarily (for validation)            │
│  - Marks if value is hashed or plain                         │
│  - NO HASHING LOGIC (infrastructure concern)                 │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ↓
┌─────────────────────────────────────────────────────────────┐
│                INFRASTRUCTURE LAYER                          │
│                                                               │
│  PasswordEncoder (Spring Bean):                              │
│  - encode(Password) → String (hashed)                        │
│  - matches(plain, hash) → boolean                            │
│                                                               │
│  PasswordAttributeConverter (JPA):                           │
│  - convertToDatabaseColumn() → hashes before save            │
│  - convertToEntityAttribute() → loads as hashed              │
│                                                               │
│  UserRepository (Persistence):                               │
│  - save(User) → triggers converter                           │
│  - findById(UUID) → loads with hashed password               │
└─────────────────────────────────────────────────────────────┘
```

## 📝 Why This Approach is Correct

### 1. **Domain Layer Stays Pure**
- Value objects validate **business rules** (format, length)
- No infrastructure dependencies (no BCrypt in domain)
- Follows Single Responsibility Principle

### 2. **Infrastructure Layer Handles Technical Concerns**
- Password hashing is a **technical implementation detail**
- Can easily swap BCrypt for Argon2, PBKDF2, etc.
- Testable: mock PasswordEncoder in tests

### 3. **Clear Separation of Concerns**
| Layer | Responsibility |
|-------|----------------|
| Domain | Password format validation |
| Infrastructure | Password hashing/verification |
| Application | Orchestration |

## 🔄 Flow Examples

### Registration Flow
```java
// 1. Controller receives request
POST /api/users
{
  "email": "user@example.com",
  "password": "MyPass123!"
}

// 2. Application Service
User user = userApplicationService.registerUser(
    "John", "Doe", "user@example.com", 
    "+1234567890", "US", "MyPass123!"
);

// 3. Password Value Object validates format
Password password = new Password("MyPass123!"); // ✅ Validates regex
password.getValue() → "MyPass123!" (plain text)
password.isHashed() → false

// 4. User Entity created
User user = new User(uuid, firstName, lastName, email, mobile, password);

// 5. Repository saves (triggers JPA converter)
userRepository.save(user);

// 6. PasswordAttributeConverter.convertToDatabaseColumn()
if (!password.isHashed()) {
    return BCrypt.encode(password.getValue());
}

// 7. Database stores: "$2a$10$..."
```

### Login Flow
```java
// 1. Controller receives request
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "MyPass123!"
}

// 2. Application Service loads user
User user = userRepository.findByEmail("user@example.com");
// user.getPassword().getValue() → "$2a$10$..." (hashed)
// user.getPassword().isHashed() → true

// 3. Verify password using infrastructure service
boolean valid = passwordEncoder.matches("MyPass123!", user.getPassword().getValue());
// Returns: true if match
```

## ❌ What NOT to Do

### ❌ DON'T Hash in Value Object Constructor
```java
// BAD - Infrastructure concern in domain layer
public Password(String password) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    this.value = encoder.encode(password); // ❌ Wrong layer
}
```

### ❌ DON'T Create BCrypt Instance Repeatedly
```java
// BAD - Performance issue, creates new instance every time
public String hashPassword(String password) {
    return new BCryptPasswordEncoder().encode(password); // ❌ Inefficient
}
```

### ❌ DON'T Expose Password in Logs/toString
```java
// BAD - Security risk
@Override
public String toString() {
    return this.password; // ❌ Exposes password
}

// GOOD - Protects password
@Override
public String toString() {
    return isHashed ? "[HASHED]" : "[PROTECTED]";
}
```

## ✅ Usage Examples

### Creating a New User
```java
@Service
public class UserApplicationService {
    private final UserRepository userRepository;
    
    public User registerUser(String firstName, String lastName, 
                            String email, String mobile, 
                            String countryCode, String plainPassword) {
        
        // Password VO validates format
        Password password = new Password(plainPassword);
        
        // Create user with plain password
        User user = new User(
            UUID.randomUUID(),
            firstName, lastName,
            new Email(email),
            new Mobile(mobile, countryCode),
            password
        );
        
        // Save - JPA converter handles hashing
        return userRepository.save(user);
    }
}
```

### Authenticating a User
```java
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public boolean authenticate(String email, String plainPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        
        // User's password from DB is hashed
        String hashedPassword = user.getPassword().getValue();
        
        // Use infrastructure service to verify
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
```

### Changing Password
```java
@Service
public class UserApplicationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword().getValue())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        // Create new password (validates format)
        Password newPasswordVO = new Password(newPassword);
        
        // Update user
        user.updatePassword(newPassword);
        
        // Save - JPA converter hashes new password
        userRepository.save(user);
    }
}
```

## 🎯 Key Takeaways

1. **Password Value Object** = Format validation only
2. **Infrastructure Layer** = Hashing/verification logic
3. **JPA Converter** = Automatic hashing on save, loading as hashed
4. **Application Service** = Orchestrates the flow
5. **Domain stays clean** = No infrastructure dependencies

## 🔒 Security Best Practices

- ✅ Never log passwords (plain or hashed)
- ✅ Use BCrypt/Argon2 (computationally expensive)
- ✅ Salt is automatic with BCrypt
- ✅ Hash at persistence layer (not in domain)
- ✅ Validate format before hashing
- ✅ Use constant-time comparison (BCrypt does this)
- ✅ Store only hashed passwords in database

## 📚 References

- Domain-Driven Design (Eric Evans)
- Clean Architecture (Robert C. Martin)
- Spring Security Password Encoding
- OWASP Password Storage Cheat Sheet

