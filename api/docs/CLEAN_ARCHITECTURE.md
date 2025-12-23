# Structure Overview

This architecture follows **Clean Architecture combined with CQRS**, emphasizing clear separation between business
rules, application orchestration, and technical concerns.

The structure is intentionally modular to support scalability, testability, and technology replacement.

---

```
module
│
├── domain
│   ├── model
│   ├── error
│   ├── service
│   ├── event
│   └── repository
│
├── application
│   ├── command
│   │   ├── dispatcher
│   │   ├── handler
│   │   ├── dto
│   │   └── Command.java
│   │
│   ├── event
│   │   ├── annotation
│   │   ├── handler
│   │   └── Event.java
│   │
│   ├── query
│   │   ├── service
│   │   ├── dto
│   │   └── port
│   │
│   ├── public_api
│   │
│   └── shared
│       ├── annotation
│       ├── port
│       └── ApplicationService.java
│
├── interface
│   ├── rest
│   ├── graphql
│   └── internal
│
└── infrastructure
    ├── command
    ├── persistence
    ├── messaging
    ├── cache
    ├── multi_tenancy
    ├── configuration
    └── web
```

---

## Domain Layer

Core business logic. **Framework-agnostic and technology-independent**.

| Package             | Responsibility                                           |
|---------------------|----------------------------------------------------------|
| `domain.model`      | Entities, Value Objects, Aggregate Roots.                |
| `domain.error`      | Domain-specific exceptions and business rule violations. |
| `domain.service`    | Domain Services for cross-entity business logic.         |
| `domain.event`      | Domain Events representing meaningful state changes.     |
| `domain.repository` | Repository interfaces (contracts only).                  |

---

## Application Layer

Coordinates use cases and defines application-level abstractions.

### Command (Write Side – CQRS)

| Package                          | Responsibility                                                   |
|----------------------------------|------------------------------------------------------------------|
| `application.command.dispatcher` | Command dispatching abstractions (e.g. command bus, scheduling). |
| `application.command.handler`    | Command Handlers orchestrating write use cases.                  |
| `application.command.dto`        | Command input/output DTOs.                                       |
| `application.command.Command`    | Marker or base interface for commands.                           |

---

### Application Events

| Package                        | Responsibility                                                   |
|--------------------------------|------------------------------------------------------------------|
| `application.event.annotation` | Event-related metadata (topics, routing, etc.).                  |
| `application.event.handler`    | Event subscribers reacting to application or integration events. |
| `application.event.Event`      | Base abstraction for application-level events.                   |

---

### Shared Application Components

| Package                                 | Responsibility                                                             |
|-----------------------------------------|----------------------------------------------------------------------------|
| `application.shared.annotation`         | Cross-cutting annotations (transaction scope, etc.).                       |
| `application.shared.port`               | Application-level output ports (cache, lock, event publishing, migration). |
| `application.shared.ApplicationService` | Base class or marker for application services.                             |

---

### Query (Read Side – CQRS)

| Package                     | Responsibility                               |
|-----------------------------|----------------------------------------------|
| `application.query.service` | Read-only application services.              |
| `application.query.dto`     | DTOs optimized for queries.                  |
| `application.query.port`    | Output ports for read models or projections. |

---

### Public API

| Package                  | Responsibility                                                       |
|--------------------------|----------------------------------------------------------------------|
| `application.public_api` | Application-level APIs exposed to other modules or bounded contexts. |

---

## Interface Layer

Handles external communication. **Contains no business logic**.

| Package              | Responsibility                                                   |
|----------------------|------------------------------------------------------------------|
| `interface.rest`     | REST controllers mapping HTTP requests to application DTOs.      |
| `interface.graphql`  | GraphQL resolvers.                                               |
| `interface.internal` | Internal adapters (schedulers, batch jobs, background triggers). |

---

## Infrastructure Layer

Provides **technical implementations**, grouped by **technology → purpose**.

## Dependency Rule

```
interface → application → domain
infrastructure ─────────┘
```

**Rules:**

* Domain depends on nothing
* Application depends only on domain
* Interface depends on application
* Infrastructure depends on all, but nothing depends on it