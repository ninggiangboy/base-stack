# Module Structure Overview

This architecture follows **Clean Architecture + CQRS**, with clear separation between domain, application, interface,
and infrastructure layers.

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
│   │   ├── model
│   │   ├── handler
│   │   ├── dto
│   │   └── port
│   │       └── out
│   ├── query
│   │   ├── service
│   │   ├── dto
│   │   └── port
│   │       └── out
│   ├── event
│   │   ├── publisher
│   │   └── subscriber
│   ├── public_api
│   └── shared
│       └── port
│           └── out
│
├── interface
│   ├── rest
│   ├── graphql
│   └── internal
│
└── infrastructure
    ├── persistence
    │   ├── write
    │   └── read
    ├── messaging
    │   ├── producer
    │   └── consumer
    └── configuration
```

---

## Domain Layer

Core business logic. **Framework-agnostic and technology-independent**.

| Package             | Responsibility                                                                        |
|---------------------|---------------------------------------------------------------------------------------|
| `domain.model`      | Entities, Value Objects, Aggregate Roots. Contains core business rules.               |
| `domain.error`      | Domain-specific exceptions and business rule violations.                              |
| `domain.service`    | Domain Services for business logic that does not naturally belong to a single entity. |
| `domain.event`      | Domain Events raised when significant state changes occur.                            |
| `domain.repository` | Repository interfaces (contracts only, no implementations).                           |

---

## Application Layer

Coordinates use cases and connects domain logic with external systems.

### Command (Write Side – CQRS)

| Package                       | Responsibility                                                      |
|-------------------------------|---------------------------------------------------------------------|
| `application.command.model`   | Command objects (e.g. `CreateOrderCommand`).                        |
| `application.command.handler` | Command Handlers orchestrating domain behavior.                     |
| `application.command.dto`     | Input/Output DTOs for commands.                                     |
| `application.command.port`    | Output ports (repositories, external services, message publishers). |

---

### Query (Read Side – CQRS)

| Package                     | Responsibility                                             |
|-----------------------------|------------------------------------------------------------|
| `application.query.service` | Application services for read-only use cases.              |
| `application.query.dto`     | DTOs optimized for read operations.                        |
| `application.query.port`    | Output ports for read models, caches, search engines, etc. |

---

### Application Events

| Package                        | Responsibility                                           |
|--------------------------------|----------------------------------------------------------|
| `application.event.publisher`  | Publishes domain/application events to external systems. |
| `application.event.subscriber` | Subscribes to events and triggers follow-up use cases.   |

---

### Public API

| Package                  | Responsibility                                                        |
|--------------------------|-----------------------------------------------------------------------|
| `application.public_api` | Exposes application-level APIs for other modules or bounded contexts. |

---

### Shared

| Package                       | Responsibility                                                   |
|-------------------------------|------------------------------------------------------------------|
| `application.shared.port.out` | Shared output ports (clock, UUID generator, external providers). |

---

## Interface Layer

Handles external communication. **No business logic**.

| Package              | Responsibility                                                       |
|----------------------|----------------------------------------------------------------------|
| `interface.rest`     | REST controllers mapping HTTP requests to application DTOs.          |
| `interface.graphql`  | GraphQL resolvers mapping queries/mutations to application services. |
| `interface.internal` | Internal adapters (scheduler, batch jobs, background tasks).         |

---

## Infrastructure Layer

Contains **technical implementations and integrations**.

### Persistence

| Package                            | Responsibility                                                     |
|------------------------------------|--------------------------------------------------------------------|
| `infrastructure.persistence.write` | Write-side persistence (JPA/JDBC). Implements domain repositories. |
| `infrastructure.persistence.read`  | Read models, projections, and optimized query views.               |

---

### Messaging

| Package                             | Responsibility                                                 |
|-------------------------------------|----------------------------------------------------------------|
| `infrastructure.messaging.producer` | Message/event producers (Kafka, RabbitMQ, SNS, etc.).          |
| `infrastructure.messaging.consumer` | Message consumers converting messages into events or commands. |

---

### Configuration

| Package                        | Responsibility                                                     |
|--------------------------------|--------------------------------------------------------------------|
| `infrastructure.configuration` | Framework configuration, dependency wiring, and external settings. |

---

## Dependency Rule

```
interface → application → domain
infrastructure ─────────┘
```

* Domain depends on nothing
* Application depends only on domain
* Infrastructure depends on all, but nothing depends on it
