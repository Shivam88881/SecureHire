# Clean Architecture - Visual Dependency Flow

## 🏗️ Complete Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                              │
│                    (Controllers, REST Endpoints)                        │
│                                                                         │
│  @RestController                                                        │
│  UserController                                                         │
│      ↓ depends on                                                       │
└──────┼──────────────────────────────────────────────────────────────────┘
       │
       ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                                │
│                      (Use Cases, Orchestration)                         │
│                                                                         │
│  UserApplicationService                                                 │
│    - registerUser(...)                                                  │
│    - authenticateUser(...)                                              │
│    - changePassword(...)                                                │
│      ↓ depends on (interfaces only)                                    │
│      ├── PasswordEncoderPort                                           │
│      └── UserRepositoryPort                                            │
└──────┼──────────────────────────────────────────────────────────────────┘
       │
       ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                                   │
│                   (Business Logic, Entities, Ports)                     │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ ENTITIES (Aggregate Roots)                                       │  │
│  │   User                                                           │  │
│  │     - blockUser()                                                │  │
│  │     - changePassword()                                           │  │
│  │     - updateEmail()                                              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ VALUE OBJECTS (Immutable, Self-Validating)                      │  │
│  │   Email         - validates format                               │  │
│  │   Mobile        - validates phone number                         │  │
│  │   Password      - validates password rules                       │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ PORTS (Interfaces - What we need from outside)                  │  │
│  │   PasswordEncoderPort                                            │  │
│  │     + encode(String): String                                     │  │
│  │     + matches(String, String): boolean                           │  │
│  │                                                                  │  │
│  │   UserRepositoryPort                                             │  │
│  │     + save(User): User                                           │  │
│  │     + findById(UUID): Optional<User>                            │  │
│  │     + findByEmail(String): Optional<User>                       │  │
│  │     + existsByEmail(String): boolean                            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ DOMAIN SERVICES (Multi-entity business logic)                   │  │
│  │   UserDomainService                                              │  │
│  │     - canTransferUser(...)                                       │  │
│  │     - mergeUsers(...)                                            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
                                ↑ implements (pointing inward!)
                                │
┌─────────────────────────────────────────────────────────────────────────┐
│                       INFRASTRUCTURE LAYER                              │
│                  (Technical Implementation Details)                     │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ ADAPTERS (Implementations of Ports)                              │  │
│  │                                                                  │  │
│  │   PasswordEncoder implements PasswordEncoderPort                 │  │
│  │     - Uses BCryptPasswordEncoder (Spring Security)               │  │
│  │     @Component                                                   │  │
│  │                                                                  │  │
│  │   UserRepositoryImpl implements UserRepositoryPort              │  │
│  │     - Uses JPA/Hibernate                                         │  │
│  │     - Maps Entity to Database                                    │  │
│  │     @Repository                                                  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ PERSISTENCE (JPA Entities, Converters)                           │  │
│  │   UserJpaEntity                                                  │  │
│  │   EmailAttributeConverter                                        │  │
│  │   MobileAttributeConverter                                       │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ EXTERNAL SERVICES                                                │  │
│  │   EmailService (SMTP)                                            │  │
│  │   SmsService                                                     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Dependency Direction (The Key!)

### ❌ WRONG (Before):

```
Presentation
    ↓
Application ──┐
    ↓         ↓ (BAD: bidirectional!)
Infrastructure
```

### ✅ CORRECT (After):

```
Presentation
    ↓
Application ──→ Domain ←── Infrastructure
                (Port)     (Adapter)
```

**All arrows point INWARD toward the Domain!**

---

## 📦 Package/Module Dependencies

```
userservice/
│
├── presentation/
│   └── controller/
│       └── UserController.java
│           imports: application.*
│
├── application/
│   └── usecase/
│       └── UserApplicationService.java
│           imports: domain.* (entities, value objects, PORTS)
│           NO imports from infrastructure.*
│
├── domain/
│   ├── entity/
│   │   └── User.java
│   ├── valueobject/
│   │   ├── Email.java
│   │   ├── Mobile.java
│   │   └── Password.java
│   └── service/
│       ├── PasswordEncoderPort.java (INTERFACE)
│       └── UserRepositoryPort.java (INTERFACE)
│           NO imports from infrastructure.*
│           NO imports from application.*
│
└── infrastructure/
    ├── security/
    │   └── PasswordEncoder.java
    │       implements: domain.service.PasswordEncoderPort
    │       imports: domain.*
    └── persistence/
        └── UserRepositoryImpl.java
            implements: domain.service.UserRepositoryPort
            imports: domain.*
```

---

## 🎯 The Flow: User Registration Example

### Request Flow:

```
1. HTTP Request
   POST /api/users
   {
     "firstName": "John",
     "lastName": "Doe",
     "email": "john@example.com",
     "mobile": "+1234567890",
     "countryCode": "US",
     "password": "MyPass123!"
   }
          ↓
   ┌─────────────────────────────────────┐
   │ PRESENTATION: UserController        │
   │ @PostMapping("/api/users")          │
   │ userApplicationService.registerUser │
   └─────────────────────────────────────┘
          ↓
   ┌─────────────────────────────────────────────────────────┐
   │ APPLICATION: UserApplicationService                     │
   │                                                         │
   │ 1. Validate email uniqueness                            │
   │    userRepository.existsByEmail() → Port                │
   │                                                         │
   │ 2. Validate password format                             │
   │    new Password(plainPassword) → Domain VO              │
   │                                                         │
   │ 3. Hash password                                        │
   │    passwordEncoder.encode() → Port                      │
   │                                                         │
   │ 4. Create value objects                                 │
   │    new Email(), new Mobile() → Domain VOs               │
   │                                                         │
   │ 5. Create User entity                                   │
   │    new User(...) → Domain Entity                        │
   │                                                         │
   │ 6. Save to database                                     │
   │    userRepository.save(user) → Port                     │
   │                                                         │
   │ 7. Publish event                                        │
   │    eventPublisher.publish() → Port                      │
   └─────────────────────────────────────────────────────────┘
          ↓
   ┌─────────────────────────────────────┐
   │ DOMAIN: Validation & Business Logic │
   │                                     │
   │ • Email.validate()                  │
   │ • Mobile.validate()                 │
   │ • Password.validate()               │
   │ • User constructor validations      │
   └─────────────────────────────────────┘
          ↓
   ┌─────────────────────────────────────────────┐
   │ INFRASTRUCTURE: Adapters Execute            │
   │                                             │
   │ • PasswordEncoder (BCrypt)                  │
   │   implements PasswordEncoderPort            │
   │                                             │
   │ • UserRepositoryImpl (JPA)                  │
   │   implements UserRepositoryPort             │
   │                                             │
   │ • Database (PostgreSQL/MySQL)               │
   └─────────────────────────────────────────────┘
          ↓
2. HTTP Response
   201 Created
   {
     "id": "uuid",
     "firstName": "John",
     "lastName": "Doe",
     "email": "john@example.com"
   }
```

---

## 🔌 Dependency Injection (Spring Example)

### Configuration:

```java
@Configuration
public class ApplicationConfig {
    
    /**
     * Infrastructure bean implementing domain port
     */
    @Bean
    public PasswordEncoderPort passwordEncoder() {
        return new PasswordEncoder(); // Concrete implementation
    }
    
    /**
     * Application service depends on port (interface)
     */
    @Bean
    public UserApplicationService userApplicationService(
            PasswordEncoderPort passwordEncoder,  // ← Interface!
            UserRepositoryPort userRepository) {  // ← Interface!
        return new UserApplicationService(passwordEncoder, userRepository);
    }
}
```

### Spring wires it up:

```
1. Spring creates PasswordEncoder (infrastructure)
2. Spring sees it implements PasswordEncoderPort
3. Spring injects it into UserApplicationService as PasswordEncoderPort
4. UserApplicationService only knows about the interface!
```

---

## 🧪 Testing Benefits

### Unit Test (Application Layer):

```java
@Test
public void testRegisterUser() {
    // ✅ Mock the PORT (interface), not the adapter
    PasswordEncoderPort mockEncoder = mock(PasswordEncoderPort.class);
    UserRepositoryPort mockRepo = mock(UserRepositoryPort.class);
    
    when(mockEncoder.encode(anyString())).thenReturn("hashed_password");
    when(mockRepo.existsByEmail(anyString())).thenReturn(false);
    when(mockRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    
    // Create service with mocks
    UserApplicationService service = new UserApplicationService(
        mockEncoder, 
        mockRepo
    );
    
    // Test use case
    User user = service.registerUser(
        "John", "Doe", "john@example.com", 
        "+1234567890", "US", "MyPass123!"
    );
    
    // Assertions
    assertNotNull(user);
    assertEquals("hashed_password", user.getHashedPassword());
    verify(mockEncoder).encode("MyPass123!");
    verify(mockRepo).save(any(User.class));
}
```

**No infrastructure needed! Fast, isolated tests.**

---

## 🔄 Swapping Implementations

### Scenario: Switch from BCrypt to Argon2

```java
// 1. Create new adapter (infrastructure)
@Component
public class Argon2PasswordEncoder implements PasswordEncoderPort {
    
    @Override
    public String encode(String plainPassword) {
        return Argon2PasswordEncoder.encode(plainPassword);
    }
    
    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        return Argon2PasswordEncoder.verify(plainPassword, encodedPassword);
    }
}

// 2. Change Spring configuration
@Configuration
public class ApplicationConfig {
    
    @Bean
    public PasswordEncoderPort passwordEncoder() {
        // return new PasswordEncoder(); // BCrypt
        return new Argon2PasswordEncoder(); // Argon2 ✅
    }
}
```

**That's it! No changes to:**
- ❌ Domain layer (User, Email, Password VOs)
- ❌ Application layer (UserApplicationService)
- ❌ Presentation layer (UserController)

**Only changes to:**
- ✅ Infrastructure (new adapter)
- ✅ Configuration (which adapter to use)

---

## 📊 Comparison Table

| Aspect | Without Ports (Wrong) | With Ports (Correct) |
|--------|----------------------|---------------------|
| **Application imports** | `infrastructure.security.PasswordEncoder` | `domain.service.PasswordEncoderPort` |
| **Dependency direction** | Outward (to infrastructure) | Inward (to domain) |
| **Testing** | Need real BCrypt | Mock interface easily |
| **Flexibility** | Hard to swap | Easy to swap |
| **Domain purity** | Polluted with infrastructure | Pure business logic |
| **Build time** | Application needs infrastructure compiled | Application only needs domain |
| **Compile dependencies** | Application → Infrastructure | Infrastructure → Domain |

---

## 🎓 Architecture Principles Applied

### 1. Dependency Inversion Principle (SOLID - D)
✅ High-level (Application) depends on abstraction (Port)
✅ Low-level (Infrastructure) depends on abstraction (Port)

### 2. Clean Architecture (Uncle Bob)
✅ Inner circles (Domain) don't depend on outer circles
✅ Dependencies point inward
✅ Business logic isolated from frameworks

### 3. Hexagonal Architecture (Ports & Adapters)
✅ Ports define interfaces (PasswordEncoderPort)
✅ Adapters implement interfaces (PasswordEncoder)
✅ Application core isolated from technical details

### 4. Interface Segregation Principle (SOLID - I)
✅ Ports are focused and specific
✅ No fat interfaces

### 5. Open/Closed Principle (SOLID - O)
✅ Open for extension (new adapters)
✅ Closed for modification (existing code unchanged)

---

## 🎉 Summary

### Your Original Question:
> "By this way it's not pointing inward cause application is using infrastructure. Do you think it's correct or should be done by other way?"

### Answer:
**You were 100% CORRECT!** ✅

### What We Fixed:

| Before | After |
|--------|-------|
| Application → PasswordEncoder (infrastructure) | Application → PasswordEncoderPort (domain) |
| Dependency points OUTWARD ❌ | Dependency points INWARD ✅ |
| Violates Clean Architecture | Follows Clean Architecture |
| Hard to test | Easy to test |
| Tight coupling | Loose coupling |

### The Pattern:

```
Application Layer:
  depends on → PasswordEncoderPort (interface in domain)

Domain Layer:
  defines → PasswordEncoderPort (interface)

Infrastructure Layer:
  implements → PasswordEncoderPort (concrete class)
```

**This is professional-grade architecture!** 🏆

