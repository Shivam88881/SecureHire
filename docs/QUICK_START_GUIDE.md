# Quick Start Guide - JSON Storage Implementation

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven
- Docker (for PostgreSQL)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

---

## Step 1: Start PostgreSQL

Open terminal in project root and run:

```powershell
docker-compose up -d postgres-userdb
```

Verify it's running:
```powershell
docker ps
```

You should see:
```
CONTAINER ID   IMAGE                  STATUS    PORTS
...            postgres:15-alpine     Up        0.0.0.0:5432->5432/tcp
```

---

## Step 2: Create Database Schema

### Option A: Run SQL Manually

Connect to PostgreSQL:
```powershell
docker exec -it securehire-userdb psql -U postgres -d securehire_userdb
```

Copy and paste the SQL from:
`src/main/resources/db/migration/V1__create_users_table.sql`

### Option B: Configure Flyway (Recommended)

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

Uncomment in `application.yaml`:
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

Flyway will auto-run migrations on startup! ✅

---

## Step 3: Build the Project

```powershell
./mvnw clean install
```

Or if you're on Windows and `mvnw` doesn't work:
```powershell
mvn clean install
```

---

## Step 4: Run the Application

```powershell
./mvnw spring-boot:run
```

You should see:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.7)

...
Started UserserviceApplication in 3.456 seconds
```

---

## Step 5: Test JSON Storage

### Create a simple test controller:

```java
package com.stackwise.userservice.presentation.controller;

import com.stackwise.userservice.domain.entity.User;
import com.stackwise.userservice.domain.valueObject.*;
import com.stackwise.userservice.infrastructure.persistence.adapter.UserPersistenceAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private final UserPersistenceAdapter persistence;
    
    public TestController(UserPersistenceAdapter persistence) {
        this.persistence = persistence;
    }
    
    @PostMapping("/user")
    public String testJsonStorage() {
        // 1. Create user
        User user = User.createUser(
            "John",
            "Doe",
            UUID.randomUUID(),
            "john@test.com",
            "1234567890",
            "+1",
            new Role(Role.RoleType.JOBSEEKER)
        );
        
        // 2. Add Address (will be stored as JSON)
        Address address = new Address(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );
        user.updateAddress(address);
        
        // 3. Add Social Links (will be stored as JSON)
        Map<String, String> links = new HashMap<>();
        links.put("linkedin", "https://linkedin.com/in/johndoe");
        links.put("github", "https://github.com/johndoe");
        SocialLinks socialLinks = new SocialLinks(links);
        user.updateSocialLinks(socialLinks);
        
        // 4. Save to database
        User saved = persistence.save(user);
        
        // 5. Load from database
        User loaded = persistence.findById(saved.getId()).orElseThrow();
        
        // 6. Verify Address loaded correctly from JSON
        String city = loaded.getAddress().getCity();
        
        // 7. Verify SocialLinks loaded correctly from JSON
        String linkedIn = loaded.getSocialLinks().getLink("linkedin");
        
        return "Success! City: " + city + ", LinkedIn: " + linkedIn;
    }
    
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable UUID id) {
        return persistence.findById(id).orElseThrow();
    }
}
```

### Test it:

```powershell
# Create user
curl -X POST http://localhost:8080/api/test/user

# Response:
# Success! City: New York, LinkedIn: https://linkedin.com/in/johndoe
```

---

## Step 6: Verify in Database

Connect to PostgreSQL:
```powershell
docker exec -it securehire-userdb psql -U postgres -d securehire_userdb
```

Query:
```sql
SELECT id, first_name, address, social_links FROM users;
```

You should see JSON in the address and social_links columns! ✅

Example:
```
 id                                   | first_name | address                                                      | social_links
--------------------------------------|------------|--------------------------------------------------------------|-------------
 550e8400-e29b-41d4-a716-446655440000 | John       | {"street":"123 Main St","city":"New York","state":"NY",...} | {"links":{"linkedin":"https://...", "github":"https://..."}}
```

---

## Step 7: Test with pgAdmin (Optional)

1. Open browser: http://localhost:5050
2. Login:
   - Email: `admin@securehire.com`
   - Password: `admin`
3. Add server:
   - Host: `postgres-userdb` (or `host.docker.internal` on Windows)
   - Port: `5432`
   - Database: `securehire_userdb`
   - Username: `postgres`
   - Password: `postgres`
4. Browse data in the `users` table

---

## Troubleshooting

### Issue: Can't connect to PostgreSQL

**Solution:**
```powershell
# Check if PostgreSQL is running
docker ps

# If not running, start it
docker-compose up -d postgres-userdb

# Check logs
docker logs securehire-userdb
```

### Issue: "relation users does not exist"

**Solution:**
```powershell
# Run the migration SQL manually
docker exec -it securehire-userdb psql -U postgres -d securehire_userdb -f /path/to/V1__create_users_table.sql
```

Or configure Flyway (see Step 2, Option B)

### Issue: Port 5432 already in use

**Solution:**
```powershell
# Change port in compose.yaml
ports:
  - "5433:5432"  # Use 5433 on host

# Update application.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/securehire_userdb
```

### Issue: Jackson JSON conversion errors

**Solution:**
Make sure `@JsonCreator` and `@JsonProperty` are on your value objects:
```java
@JsonCreator
public Address(@JsonProperty("street") String street, ...) { ... }
```

---

## What You Just Learned

✅ **JSON Storage**: Address and SocialLinks are stored as JSON in PostgreSQL
✅ **Automatic Conversion**: JPA converters handle JSON ↔ Java object conversion
✅ **Type Safety**: You work with Address/SocialLinks objects, not JSON strings
✅ **Clean Architecture**: Domain layer doesn't know about JSON or database
✅ **Microservices**: Profile data separate from auth data

---

## Next Steps

Now that JSON storage is working, you can:

1. **Create proper REST controllers** (instead of test controller)
2. **Add DTOs** for request/response
3. **Add validation** (@Valid, @NotNull, etc.)
4. **Write tests** (unit + integration)
5. **Integrate with AuthService**
6. **Add API documentation** (Swagger/OpenAPI)
7. **Deploy to production**

---

## Useful Commands

```powershell
# Start PostgreSQL
docker-compose up -d postgres-userdb

# Stop PostgreSQL
docker-compose down

# View logs
docker logs -f securehire-userdb

# Connect to database
docker exec -it securehire-userdb psql -U postgres -d securehire_userdb

# Run application
./mvnw spring-boot:run

# Build JAR
./mvnw clean package

# Run tests
./mvnw test
```

---

## Resources

- **Implementation Guide**: `JSON_STORAGE_IMPLEMENTATION_GUIDE.md`
- **Code Examples**: `JSON_STORAGE_USAGE_EXAMPLES.md`
- **Architecture Q&A**: `ARCHITECTURE_QUESTIONS_ANSWERED.md`
- **Final Fields Guide**: `FINAL_FIELDS_DECISION_GUIDE.md`
- **Architecture Diagram**: `COMPLETE_ARCHITECTURE_DIAGRAM.md`
- **This Summary**: `IMPLEMENTATION_SUMMARY.md`

---

## Success Checklist

- [ ] PostgreSQL running in Docker
- [ ] Database schema created
- [ ] Application starts without errors
- [ ] Can save User with Address (stored as JSON)
- [ ] Can save User with SocialLinks (stored as JSON)
- [ ] Can load User and access Address object
- [ ] Can load User and access SocialLinks object
- [ ] Verified JSON in database

**If all checked, you're ready to build the REST API!** 🎉

