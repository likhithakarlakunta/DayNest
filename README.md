# DayNest

DayNest is a personal productivity and journaling web application that brings
task management, reminders, custom lists, searchable notes and date-based
journaling into one place.

## Features
- Task management with priority, status (Pending / In Progress / Completed),
  category, due date and reminder time
- Dashboard with today's tasks, completion progress, upcoming deadlines and
  recently updated notes
- Filtering by status, priority, category and due date
- Upcoming and overdue task tracking
- Custom lists to organize tasks
- Notes with keyword search
- Daily journal with one entry per date and optional mood tracking

## Tech Stack
Java 17, Spring Boot, Spring Data JPA, MySQL, REST APIs, HTML, CSS, JavaScript# DayNest – Personal Productivity & Journaling Platform
Java 17 · Spring Boot 3 · Spring Data JPA · MySQL · REST APIs · HTML/CSS/JS

## Run
1. Install JDK 17+, Maven, MySQL.
2. Edit `src/main/resources/application.properties` → set your MySQL password (the `daynest` DB is auto-created).
3. `mvn spring-boot:run`
4. Open http://localhost:8080

## REST API
| Method | URL | Purpose |
|---|---|---|
| GET | /api/tasks?status=&priority=&category=&dueDate=yyyy-mm-dd | List + filter |
| POST / PUT / DELETE | /api/tasks, /api/tasks/{id} | Create / update / delete |
| GET | /api/tasks/upcoming , /api/tasks/overdue | Deadline tracking |
| GET/POST/DELETE | /api/lists | Custom lists |
| GET/POST/PUT/DELETE | /api/notes (?q=search) | Notes + search |
| GET/PUT/DELETE | /api/journal/{yyyy-mm-dd} | One journal entry per date (upsert) |
| GET | /api/dashboard | Progress, today, upcoming, recent notes |
