# DayNest – Interview Notes

## 30-second pitch
"DayNest combines task management, reminders, custom lists, searchable notes and date-based journaling in one web app. Backend is Spring Boot with REST APIs, Spring Data JPA for persistence and MySQL as the database. The frontend is plain HTML/CSS/JS calling the APIs with fetch."

## Architecture (layered)
Browser (HTML/JS) → @RestController → Repository (Spring Data JPA) → MySQL
- **Model/Entity**: Task, TaskList, Note, JournalEntry – annotated with @Entity, mapped to tables.
- **Repository**: interfaces extending JpaRepository – Spring generates the implementation (no boilerplate SQL).
- **Controller**: maps HTTP verbs to CRUD (GET read, POST create, PUT update, DELETE remove).

## Key concepts to explain
- **@SpringBootApplication** = @Configuration + @EnableAutoConfiguration + @ComponentScan.
- **Dependency Injection**: controllers receive repositories via constructor injection.
- **JPA / Hibernate**: ORM maps Java objects ↔ rows. `ddl-auto=update` auto-creates/alters tables.
- **Derived queries**: `findByDueDateBeforeAndStatusNot(...)` – Spring builds SQL from the method name.
- **@Query (JPQL)**: the dynamic filter – `(:status IS NULL OR t.status = :status)` makes every filter optional in ONE query.
- **Enums with @Enumerated(STRING)**: stored as text, safer than ordinal numbers.
- **Validation**: @NotBlank + @Valid → 400 Bad Request when title is empty.
- **Upsert for journal**: PUT /api/journal/{date} updates if exists, else creates – date column is unique.
- **Upcoming vs Overdue**: due date between today and today+7 / before today, excluding COMPLETED.
- **Progress %** = completed ÷ total × 100.
- **ResponseEntity**: control HTTP status (200, 204, 404).
- **@PrePersist/@PreUpdate**: auto-updates Note.updatedAt.

## Likely questions
1. *PUT vs POST?* POST creates (not idempotent); PUT replaces/updates (idempotent).
2. *Why Spring Data JPA over JDBC?* Less boilerplate, automatic mapping, derived queries; JDBC gives manual control.
3. *What is `@RestController`?* @Controller + @ResponseBody → returns JSON.
4. *How do you handle search in notes?* `ContainingIgnoreCase` derived query (SQL LIKE %q%).
5. *What would you improve?* Spring Security + JWT (multi-user), DTO layer, global exception handler (@ControllerAdvice), pagination, scheduled reminders with @Scheduled, unit tests with JUnit/Mockito.
6. *Why one query for filters?* Avoids many overloaded repository methods; null-check pattern.
7. *Primary key strategy?* IDENTITY – MySQL AUTO_INCREMENT.
8. *Reminders?* Stored as LocalDateTime; real notifications could use @Scheduled to poll for due reminders.
