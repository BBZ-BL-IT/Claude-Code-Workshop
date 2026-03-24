---
name: entity-scaffold
description: >
  Generates a complete CRUD feature (Entity, Repository, Service, Controller,
  Thymeleaf templates, DataInitializer update) following this project's
  3-layer architecture. Auto-invoke when user asks to create, add, or
  scaffold a new entity, feature, or domain object — especially when
  relationships to existing entities are involved.
allowed-tools: Read, Grep, Glob, Write, Edit
---

# Entity Scaffold — person-crud

Use this skill when adding a new domain object to the application.

Architecture rules come from **CLAUDE.md** — you already have these loaded,
do not re-read them. Concrete implementation patterns (naming, URL scheme,
template structure, flash message variables) come from the **existing code** —
read that before generating.

## Step 0 — Understand the request

Before writing code, clarify with the user:
1. Entity name and fields (types, constraints, required/optional)
2. Relationship to existing entities (none, @ManyToOne, @OneToMany, etc.)
3. Any special validation or business rules
4. Whether the entity needs its own CRUD views or is managed as part
   of a parent entity (e.g. inline in the parent form)

If the user gives a brief instruction like "add addresses to Person",
ask the clarifying questions above before proceeding.

## Step 1 — Read existing implementation patterns

You already know the architecture from CLAUDE.md. Now read the **existing
code** to learn the concrete implementation style you must match:
- An existing Entity (e.g. `model/Person.java`) — field annotations, Lombok usage, table naming
- An existing Repository — query style, method naming
- An existing Service — method signatures, @Transactional usage
- An existing Controller — URL patterns, flash message variable names,
  validation flow, section comment style
- An existing template set (`templates/person/`) — layout integration,
  form structure, table layout, button style
- `DataInitializer.java` — seeding pattern, builder usage

The goal is not to understand the architecture (CLAUDE.md covers that)
but to ensure every generated file looks like it was written by the
same developer who wrote the existing code.

**Do not skip this step.** The generated code must match these patterns exactly.

## Step 2 — Generate the Entity

Location: `src/main/java/ch/example/personcrud/model/`

Follow the existing pattern:
```java
@Entity
@Table(name = "...")          // plural, lowercase
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fields with Bean Validation
    // message = "..." in German (user-facing)
    // Field names in English
}
```

For relationships, apply standard JPA:
- `@ManyToOne` with `@JoinColumn` on the owning side
- `@OneToMany(mappedBy = "...", cascade = CascadeType.ALL, orphanRemoval = true)`
  on the parent side
- Use `fetch = FetchType.LAZY` unless there's a reason not to
- Add the convenience methods `addChild()` / `removeChild()` on the parent
  if using bidirectional @OneToMany

## Step 3 — Generate the Repository

Location: `src/main/java/ch/example/personcrud/repository/`

```java
@Repository
public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
    // Search query if the entity has searchable fields
    // Uniqueness checks (existsBy...) if needed
    // findByParent methods if relationship exists
}
```

## Step 4 — Generate the Service

Location: `src/main/java/ch/example/personcrud/service/`

Follow the existing thin-service pattern:
- `@Service`, `@RequiredArgsConstructor`, `@Transactional`
- `@Transactional(readOnly = true)` on all read methods
- Methods: `findAll()`, `findById()`, `save()`, `delete()`
- Add `search()` if the entity has searchable text fields
- Add relationship-specific methods if needed
  (e.g. `findByPersonId()` for child entities)
- **No HTTP concerns** — no Model, no RedirectAttributes

## Step 5 — Generate the Controller

Location: `src/main/java/ch/example/personcrud/controller/`

Follow the existing pattern exactly:
- `@Controller`, `@RequestMapping("/<entities>")`, `@RequiredArgsConstructor`
- Section comments: `// ─── LIST ───`, `// ─── CREATE ───`, etc.
- URL scheme: `GET /<entities>`, `GET /<entities>/new`,
  `POST /<entities>/new`, `GET /<entities>/{id}`,
  `GET /<entities>/{id}/edit`, `POST /<entities>/{id}/edit`,
  `POST /<entities>/{id}/delete`
- Flash messages for success/error (German, user-facing)
- `@Valid` on every `@ModelAttribute` in POST methods
- Uniqueness checks in the controller (not in service)
- Return `"redirect:/<entities>"` after successful create/update/delete

For child entities with a parent relationship:
- Nest URLs: `/<parents>/{parentId}/<children>/...`
- Always load and validate the parent in each controller method
- Pass the parent to the template via model attribute

## Step 6 — Generate the Templates

Location: `src/main/resources/templates/<entity>/`

Create three templates following the existing pattern:

### list.html
- Uses `th:replace="~{layout :: layout(~{::content})}"`
- Page header with title and "new" button
- Search form (if entity has searchable fields)
- Result count
- Table with all fields, action buttons (detail, edit, delete)
- Empty state with link to create

### form.html (create + edit shared)
- Shared template, distinguished by `${title}` model attribute
- `th:action` switches between create/edit based on `${entity.id}`
- Every field with `th:field`, error display, labels
- Required fields marked with `*`, optional with "(optional)"
- Submit button text changes: "Create" vs "Save"

### detail.html
- Read-only detail table showing all fields
- Edit button, back button
- Danger zone with delete form and confirmation dialog

For child entities, add navigation links between parent and children
in both the parent's detail view and the child's list view.

## Step 7 — Update DataInitializer

Edit `DataInitializer.java`:
- Add sample data for the new entity (3–5 records)
- If relationship exists, link to existing sample data
- Keep the same builder pattern

## Step 8 — Update Navigation

Edit `layout.html`:
- Add a navigation link to the new entity's list view

## Step 9 — Verify

After generating all files:
1. Check that all imports are correct
2. Verify no layer violations (Controller → Service → Repository only)
3. Confirm all URL paths are consistent between controller and templates
4. Ensure the relationship is mapped correctly on both sides
5. Run `mvn compile` mentally — would it compile?

Report to the user what was created and suggest running `mvn spring-boot:run`
to verify.
