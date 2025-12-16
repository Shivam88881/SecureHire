# Quick Reference Guide - Pure Clean Architecture Implementation

## 🎯 When Adding New Value Objects

### Step 1: Create Pure Domain Value Object
```java
// Location: domain/valueObject/YourValueObject.java
package com.stackwise.userservice.domain.valueObject;

// ❌ NO Jackson imports!
// ❌ NO JPA imports!
// ❌ NO Spring imports!

public class YourValueObject {
    private final String field1;
    private final String field2;
    
    public YourValueObject(String field1, String field2) {
        // Validation logic here
        this.field1 = field1;
        this.field2 = field2;
    }
    
    // Getters only (immutable)
    public String getField1() { return field1; }
    public String getField2() { return field2; }
}
```

### Step 2: Create Infrastructure DTO
```java
// Location: infrastructure/persistence/dto/YourValueObjectDTO.java
package com.stackwise.userservice.infrastructure.persistence.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// ✅ Jackson annotations OK here!
public class YourValueObjectDTO {
    private final String field1;
    private final String field2;
    
    @JsonCreator
    public YourValueObjectDTO(
            @JsonProperty("field1") String field1,
            @JsonProperty("field2") String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }
    
    public String getField1() { return field1; }
    public String getField2() { return field2; }
}
```

### Step 3: Create Mapper
```java
// Location: infrastructure/persistence/mapper/YourValueObjectMapper.java
package com.stackwise.userservice.infrastructure.persistence.mapper;

import com.stackwise.userservice.domain.valueObject.YourValueObject;
import com.stackwise.userservice.infrastructure.persistence.dto.YourValueObjectDTO;

public class YourValueObjectMapper {
    
    public static YourValueObjectDTO toDTO(YourValueObject domain) {
        if (domain == null) return null;
        return new YourValueObjectDTO(
            domain.getField1(),
            domain.getField2()
        );
    }
    
    public static YourValueObject toDomain(YourValueObjectDTO dto) {
        if (dto == null) return null;
        return new YourValueObject(
            dto.getField1(),
            dto.getField2()
        );
    }
}
```

### Step 4: Create JPA Converter
```java
// Location: infrastructure/persistence/converter/YourValueObjectJsonConverter.java
package com.stackwise.userservice.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackwise.userservice.domain.valueObject.YourValueObject;
import com.stackwise.userservice.infrastructure.persistence.dto.YourValueObjectDTO;
import com.stackwise.userservice.infrastructure.persistence.mapper.YourValueObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class YourValueObjectJsonConverter implements AttributeConverter<YourValueObject, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(YourValueObject domain) {
        if (domain == null) return null;
        try {
            YourValueObjectDTO dto = YourValueObjectMapper.toDTO(domain);
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting to JSON", e);
        }
    }
    
    @Override
    public YourValueObject convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            YourValueObjectDTO dto = objectMapper.readValue(json, YourValueObjectDTO.class);
            return YourValueObjectMapper.toDomain(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting from JSON", e);
        }
    }
}
```

### Step 5: Use in JPA Entity
```java
// Location: infrastructure/persistence/entity/UserEntity.java
@Entity
public class UserEntity {
    
    @Convert(converter = YourValueObjectJsonConverter.class)
    @Column(name = "your_field", columnDefinition = "jsonb")
    private YourValueObject yourField;
    
    // Getters and setters
}
```

## ✅ Checklist for New Value Objects

- [ ] Domain value object created (no framework imports)
- [ ] DTO created in infrastructure layer (with Jackson annotations)
- [ ] Mapper created (domain ↔ DTO conversion)
- [ ] JPA Converter created (uses DTO and Mapper)
- [ ] Added to JPA entity with @Convert annotation
- [ ] Compile successful
- [ ] Unit tests for domain object (no mocking!)
- [ ] Integration tests for converter

## 🚫 Rules to Follow

### Domain Layer:
- ❌ NO framework annotations (`@JsonCreator`, `@JsonProperty`, etc.)
- ❌ NO framework imports (`jackson`, `jpa`, `spring`, etc.)
- ❌ NO infrastructure concerns
- ✅ ONLY business logic and validation
- ✅ Pure Java classes
- ✅ Framework-agnostic

### Infrastructure Layer:
- ✅ Framework annotations OK
- ✅ Framework imports OK
- ✅ Technical concerns handled here
- ✅ DTOs mirror domain objects
- ✅ Mappers convert between layers

## 📁 File Organization

```
domain/
├── entity/           → Aggregate roots (pure Java)
├── valueObject/      → Value objects (pure Java)
├── service/          → Domain services (pure Java)
└── repository/       → Repository interfaces (pure Java)

infrastructure/
└── persistence/
    ├── dto/          → DTOs with Jackson annotations
    ├── mapper/       → Domain ↔ DTO converters
    ├── converter/    → JPA converters
    ├── entity/       → JPA entities
    └── repository/   → JPA repository implementations
```

## 🧪 Testing Strategy

### Domain Tests (Pure Unit Tests):
```java
@Test
void shouldValidateBusinessRule() {
    // No @SpringBootTest
    // No @MockBean
    // Just pure Java!
    
    YourValueObject obj = new YourValueObject("value1", "value2");
    assertEquals("value1", obj.getField1());
}
```

### Infrastructure Tests (Integration Tests):
```java
@SpringBootTest
class ConverterIntegrationTest {
    @Autowired
    private EntityManager entityManager;
    
    @Test
    void shouldPersistAndLoadValueObject() {
        // Test the full cycle: Domain → DTO → JSON → DB → JSON → DTO → Domain
    }
}
```

## 💡 Benefits Reminder

1. **Domain Purity**: Business logic isolated from technology
2. **Easy Testing**: Fast unit tests without infrastructure setup
3. **Framework Agnostic**: Swap Jackson, JPA, Spring anytime
4. **Future Proof**: Technology changes don't affect business logic
5. **Team Productivity**: Clear boundaries, easier collaboration

## 🔗 Related Documentation

- `CLEAN_ARCHITECTURE_NO_JACKSON_IMPLEMENTATION.md` - Detailed architecture guide
- `IMPLEMENTATION_COMPLETE_JACKSON_REMOVED.md` - Summary of changes made
- `FINAL_VERIFICATION_COMPLETE.md` - Verification and metrics

---

**Remember**: Keep the domain pure, isolate infrastructure concerns!

