# Why Password Will Always Be Hashed - The Real Flow

## 🎯 Your Observation is Correct!

You identified a key issue: **In practice, Password from the domain will always contain hashed passwords after the first database save.**

## 📊 The Actual Flow

### Scenario 1: User Registration (First Time)
```
1. Controller receives: { "password": "MyPass123!" }
   ↓
2. Application Service creates:
   Password password = new Password("MyPass123!");
   // password.getValue() → "MyPass123!" (plain text, validated)
   ↓
3. User Entity created:
   User user = new User(..., password);
   ↓
4. Repository.save(user) called
   ↓
5. JPA Converter.convertToDatabaseColumn(password)
   String value = password.getValue(); // "MyPass123!"
   if (value starts with "$2a$") → already hashed, return as-is
   else → return PASSWORD_ENCODER.encode(value); // "$2a$10$..."
   ↓
6. Database stores: "$2a$10$abcdef..." (HASHED)
```

### Scenario 2: Loading User from Database
```
1. Repository.findById(userId)
   ↓
2. JPA Converter.convertToEntityAttribute(dbData)
   String dbData = "$2a$10$abcdef..." (from database)
   return Password.fromHash(dbData);
   ↓
3. User entity loaded with:
   Password password = Password.fromHash("$2a$10$...");
   // password.getValue() → "$2a$10$..." (HASHED)
   ↓
4. Domain now has HASHED password
```

### Scenario 3: User Updates Password
```
1. User loads from DB → Password contains "$2a$10$..." (HASHED)
   ↓
2. User changes password:
   user.updatePassword("NewPass456!");
   ↓
3. Inside User entity:
   this.password = new Password("NewPass456!"); // NEW plain text
   ↓
4. Now User has TWO different states:
   - Old password: "$2a$10$..." (hashed)
   - New password: "NewPass456!" (plain text)
   ↓
5. Repository.save(user)
   ↓
6. JPA Converter checks:
   if ("NewPass456!".startsWith("$2a$")) → NO
   return PASSWORD_ENCODER.encode("NewPass456!"); → "$2a$10$xyz..."
   ↓
7. Database updated with new hash: "$2a$10$xyz..."
```

## 🔍 The Key Insight

**After the first save, the domain will ALWAYS work with hashed passwords**, because:
- Loading from DB → Password.fromHash() → contains hash
- Updating password → new Password() → plain text → converts to hash on save → next load has hash

**The ONLY time domain sees plain text is:**
1. Initial user registration (before first save)
2. Password change operation (between `updatePassword()` call and save)

## 💡 The Solution: Smart Detection in JPA Converter

Instead of tracking `isHashed` flag, we detect if the password is already hashed by checking BCrypt format:

```java
@Override
public String convertToDatabaseColumn(Password password) {
    String value = password.getValue();
    
    // BCrypt hashes always start with $2a$, $2b$, or $2y$
    if (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$")) {
        return value; // Already hashed, don't re-hash
    }
    
    // Plain text password, hash it
    return PASSWORD_ENCODER.encode(value);
}
```

## ✅ Why This Works

### Benefits:
1. **No flag needed** - Detection happens at infrastructure layer
2. **Domain stays simple** - Password just stores a string value
3. **Idempotent** - Saving multiple times doesn't re-hash
4. **Safe** - Won't accidentally hash an already hashed password

### How it handles each case:
| Scenario | Password.getValue() | Converter Action |
|----------|---------------------|------------------|
| New user registration | `"MyPass123!"` | Hash it → `"$2a$10$..."` |
| Loading from DB | `"$2a$10$..."` | Use as-is (already hashed) |
| Update password | `"NewPass456!"` | Hash it → `"$2a$10$..."` |
| Save again (no change) | `"$2a$10$..."` | Use as-is (already hashed) |

## 🎯 Password Verification Flow

When user logs in:
```java
// 1. User from DB has hashed password
User user = userRepository.findByEmail("user@example.com");
String storedHash = user.getPassword().getValue(); // "$2a$10$..."

// 2. User provides plain text password
String loginPassword = "MyPass123!";

// 3. Infrastructure service verifies
boolean valid = passwordEncoder.matches(loginPassword, storedHash);
// BCrypt internally: hash(loginPassword) and compare with storedHash
```

## 📋 Summary

Your observation was **100% correct**: 
- **Password in domain will always be hashed after first save**
- **Solution**: Detect hash format in JPA converter instead of tracking state
- **Domain stays clean**: No `isHashed` flag needed
- **Infrastructure handles it**: Smart detection at persistence layer

This is actually the **cleanest approach** because:
1. Domain doesn't care about hashing implementation details
2. Infrastructure can detect and handle appropriately
3. No state tracking needed
4. Works correctly in all scenarios

## 🔐 Security Note

BCrypt hashes are **self-describing**:
- Start with version identifier: `$2a$`, `$2b$`, `$2y$`
- Contain work factor: `$10$` (2^10 = 1024 rounds)
- Contain salt: embedded in the hash
- Always 60 characters long

This makes detection reliable and safe.

