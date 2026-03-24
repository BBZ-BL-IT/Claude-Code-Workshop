# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

> **Requirements:** Read [`requirements.md`](./requirements.md) at the start of every conversation — it contains all functional and technical requirements for this project.

## Commands

```bash
# Build
mvn clean package

# Run application (starts on port 8085)
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=<TestClassName>

# Skip tests during build
mvn clean package -DSkipTests
```

## Architecture

Spring Boot 3.2 / Java 21 MVC web app with Thymeleaf templates and an H2 in-memory database.

The application follows a strict **3-layer architecture**. Each layer has a single, clearly defined responsibility. Layers communicate only in one direction: Controller → Service → Repository.

### Layer 1 — Presentation (Controller)
- Located in `controller/`
- Handles all incoming HTTP requests and returns responses (MVC views or redirects)
- Responsible for input validation, flash messages, and duplicate-check logic before delegating to the service
- Must not contain persistence logic; delegates all data access to the Service layer

### Layer 2 — Business Logic (Service)
- Located in `service/`
- Annotated with `@Service` and `@Transactional`
- Orchestrates business rules and coordinates repository calls
- Must not depend on HTTP concerns (no `HttpServletRequest`, no model attributes)

### Layer 3 — Data Access (Repository)
- Located in `repository/`
- Extends Spring Data JPA interfaces; responsible solely for CRUD and query operations
- Custom queries (JPQL/native) live here; no business logic

### Model
- Located in `model/`
- JPA entities annotated with Lombok (`@Data`, `@Builder`) — plain data containers, no logic

### Templates (`src/main/resources/templates/`)
- `layout.html` — shared Thymeleaf layout fragment used by all pages
- Entity-specific subdirectories contain: list view, create/edit form, and detail view

### Supporting Infrastructure
- `DataInitializer.java` — `CommandLineRunner` that seeds sample data on startup if the table is empty

### Key Configuration (`application.properties`)
- Port: `8085`
- H2 console: `http://localhost:8085/h2-console`
- `ddl-auto=create-drop` — schema recreated on each start; data re-seeded via `DataInitializer`
- Thymeleaf cache disabled (dev-friendly)
- Spring Boot DevTools active (hot-reload)

## Testing

IMPORTANT: After completing any code changes, always run `mvn test` to verify all existing tests still pass.

- If no unit tests exist for a new or modified Service class, ask the user whether unit tests should be generated.
- **Test location:** `src/test/java/` mirroring the main source package structure.
- **Test pattern:** Use JUnit 5 + Mockito. Mock the Repository layer in Service tests — never use `@SpringBootTest` for Service unit tests.
- **Naming:** Test class = `<ClassName>Test.java`. Test method names describe the scenario in snake_case or camelCase (e.g., `findAll_returnsAllPersons`).
- See `src/test/java/ch/example/personcrud/service/PersonServiceTest.java` for a reference implementation.
