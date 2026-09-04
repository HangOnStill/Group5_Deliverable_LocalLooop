# LocalLoop — Campus Activity Manager

An Android campus activity-management application built with **Kotlin, Java, XML, SQLite, and UML**. The project supports multiple user roles and workflows around registration, login, event/category management, event discovery, moderation, participation, and administrative actions.

> **Team project and contribution note.** This was a three-person project. My contribution included leading/coordinating the team, owning substantial XML UI layout and screen-flow work, working across Kotlin/Java activity classes and SQLite-backed user data, and producing UML architecture/class-interaction artifacts. The repository contains work from the full team; I do not claim sole authorship of every file.

## What the application demonstrates

- Android activity-based application structure using Kotlin and Java.
- Role-aware workflows for administrators, organizers, and participants.
- Local persistence with SQLite-backed user/application data.
- Event and category creation/editing/moderation workflows.
- Join-request and event-participation flows.
- Reusable adapters/models for list-oriented interfaces.
- UML-based architecture and class-interaction planning.

## Main feature areas

### Authentication and users
- Registration and login flows.
- Role-specific user models (`Admin`, `Organizer`, `Participant`).
- User listing and administration.

### Events and categories
- Add/edit events.
- Event listing and event-detail views.
- Category creation/editing.
- Event moderation and approval-oriented flows.

### Participation
- Join requests.
- “My events” and event discovery screens.
- Adapter-backed list presentation for events, users, requests, and moderation items.

## Repository map

```text
.
├── *Activity.kt / *.java        # Android screen and domain logic
├── activity_*.xml               # Android XML layouts
├── DatabaseHelper.java          # local database support
├── EventRepository.kt           # event data/repository logic
├── EventAdapter.kt              # list presentation
├── UserAdapter.kt               # user list presentation
├── model/
│   ├── Category.kt
│   ├── Event.kt
│   ├── JoinRequest.kt
│   └── User.kt
├── ui/theme/                    # Compose/theme support files
└── UML Class Diagram SEG2105 Deliverable 2.pdf
```

## Suggested review path for a recruiter or engineer

For a quick review, start with:

1. `MainActivity.kt`, `LoginActivity.kt`, and `RegisterActivity.kt` — entry/authentication flow.
2. `DashboardActivity.kt` and `AdminDashboardActivity.kt` — role-oriented navigation.
3. `AddEditEventActivity.kt`, `EventListActivity.kt`, and `EventDetailsActivity.kt` — event lifecycle.
4. `DatabaseHelper.java` and `EventRepository.kt` — persistence/data handling.
5. `EventAdapter.kt`, `UserAdapter.kt`, and `JoinRequestsAdapter.kt` — list/UI composition.
6. `UML Class Diagram SEG2105 Deliverable 2.pdf` — architectural intent.

## My contribution

I can specifically discuss:

- coordinating work in a three-person team;
- designing screen flows and XML layouts;
- integrating Kotlin/Java activity logic with SQLite-backed application data;
- reasoning about user roles and permissions;
- using UML to communicate architecture and class interactions;
- debugging UI/data-flow issues across multiple screens.

## Build note

This repository is a **course-deliverable source snapshot**, not a fully packaged Android Studio export: the current root does not include the complete Gradle wrapper/project scaffolding normally expected for a one-command build. The source, layouts, models, adapters, database helper, and UML artifacts remain directly reviewable.

If this repository is repackaged later, I would place the source under a standard Android Studio project structure (`app/src/main/java`, `app/src/main/res/layout`) and add the Gradle wrapper/manifest files required for reproducible builds.

## Technologies

- Kotlin
- Java
- Android / XML layouts
- SQLite
- UML
- Git/GitHub

## Status

**Completed team course project.** This repository is used as portfolio evidence for Android/mobile development, UI flow design, data persistence, and team-based software engineering.
