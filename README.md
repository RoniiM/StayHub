# StayHub

StayHub is a backend application inspired by Airbnb, built with **Spring Boot 4.0.7** and **Java 21**. It implements a modern, production-style REST API demonstrating clean layered architecture, scalable domain modeling, and industry best practices across the Spring ecosystem.

The application lets users register, list rental properties as hosts, browse and book accommodations as guests, and leave reviews after completed stays. It intentionally excludes features such as payments, messaging, and notifications to keep the focus on the core booking experience.

## Tech Stack

* **Java 21**
* **Spring Boot 4.0.7**
* **Spring Web (MVC)**
* **Spring Data JPA** (Hibernate)
* **Spring Security** + **JWT** (access & refresh tokens)
* **Bean Validation**
* **PostgreSQL**
* **MapStruct** — DTO mapping
* **SpringDoc OpenAPI / Swagger UI** — API documentation
* **SLF4J + Logback** — structured logging
* **Lombok**
* **Maven**

## Architecture

The project follows a layered, feature-independent package structure:

```text
com.stayhub
├── config       # Spring configuration (JPA auditing, OpenAPI, web MVC)
├── controller   # REST controllers — DTOs in, DTOs out
├── dto          # Request/response records
├── entity       # JPA entities and enums
├── exception    # Custom exceptions + centralized global exception handler
├── mapper       # MapStruct entity <-> DTO mappers
├── repository   # Spring Data JPA repositories & specifications
├── security     # JWT service/filter, Spring Security config, @CurrentUser
└── service      # Business logic interfaces, with impl/ subpackage
```

Controllers depend only on services and DTOs; services own all business rules and talk to repositories; mappers keep entity-to-DTO conversion out of the service layer; entities never leak past the service boundary.

## Domain Model

| Entity | Description |
|---|---|
| `User` | A registered account. Can hold one or more roles (`ROLE_GUEST`, `ROLE_HOST`, `ROLE_ADMIN`) and implements Spring Security's `UserDetails` directly. |
| `Property` | A listing owned by a host. Tracks `status` (`DRAFT` / `PUBLISHED` / `ARCHIVED`) and a server-maintained `averageRating` / `reviewCount`. |
| `PropertyImage` | A URL-based image belonging to a property (no file uploads). |
| `Amenity` | A predefined, reusable amenity (Wi-Fi, Pool, Parking, ...) assigned to properties many-to-many. |
| `Booking` | A reservation request with a lifecycle: `PENDING` → `CONFIRMED`/`REJECTED`/`CANCELLED` → `COMPLETED`. |
| `Review` | A 1–5 star rating + comment tied to exactly one completed booking. |

All entities extend a common `BaseEntity` (`id`, `createdAt`, `updatedAt`) populated via Spring Data JPA auditing.

## Getting Started

### Prerequisites

* JDK 21
* Maven (or use the bundled `./mvnw`)
* PostgreSQL running locally (or update the connection settings)

### Configuration

Database and JWT settings live in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stayhub
spring.datasource.username=postgres
spring.datasource.password=password

jwt.secret=<base64-encoded HMAC key>
jwt.access-token-expiration-ms=900000
jwt.refresh-token-expiration-ms=604800000
```

Hibernate is configured with `ddl-auto=update`, so the schema is created/updated automatically on startup — no manual migrations needed for local development.

### Running the application

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### API Documentation

Once running, interactive API docs are available via Swagger UI:

* Swagger UI: `http://localhost:8080/swagger-ui.html`
* OpenAPI spec (JSON): `http://localhost:8080/v3/api-docs`

Secured endpoints can be tested directly from Swagger UI — click **Authorize** and paste in a JWT access token obtained from `/api/v1/auth/login`.

### Build & Test

```bash
./mvnw clean compile      # compile
./mvnw test               # run tests
```

## Authentication & Authorization

StayHub uses **stateless JWT authentication**:

1. `POST /api/v1/auth/register` — create an account (starts with `ROLE_GUEST`, password BCrypt-hashed).
2. `POST /api/v1/auth/login` — exchange email/password for an **access token** and a **refresh token**.
3. `POST /api/v1/auth/refresh` — exchange a valid refresh token for a new access token (rotated refresh token included).
4. Send `Authorization: Bearer <accessToken>` on subsequent requests to protected endpoints.

Any authenticated guest can upgrade to a host via `POST /api/v1/users/become-host`, which adds `ROLE_HOST` alongside their existing roles — a user can act as both guest and host simultaneously.

Ownership and role checks (e.g., only a property's host may edit it, only a booking's guest may cancel it) are enforced in the service layer based on the authenticated user — never from client-supplied identifiers.

### Public endpoints

No authentication required:

* `POST /api/v1/auth/register`, `/login`, `/refresh`
* `GET /api/v1/properties`, `/api/v1/properties/search`, `/api/v1/properties/{id}`
* `GET /api/v1/properties/{id}/reviews`

Everything else requires a valid Bearer token.

## API Overview

| Resource | Endpoints |
|---|---|
| **Auth** | `POST /api/v1/auth/{register,login,refresh}` |
| **Users** | `POST /api/v1/users/become-host` |
| **Properties** | `GET/POST /api/v1/properties`, `GET/PUT/DELETE /api/v1/properties/{id}`, `PATCH /{id}/status`, `GET/POST /{id}/images`, `DELETE /{id}/images/{imageId}`, `GET/PUT /{id}/amenities`, `GET /{id}/reviews`, `GET /search` |
| **Bookings** | `POST /api/v1/bookings`, `GET /{id}`, `GET /me`, `GET /hosted`, `PATCH /{id}/{cancel,approve,reject}` |
| **Reviews** | `POST /api/v1/reviews`, `GET/PUT/DELETE /{id}` |

Full request/response schemas, validation constraints, and status codes are documented in Swagger UI.

## Key Business Rules

* Guests may only book **published** properties, and cannot book their own listings.
* Overlapping bookings (`PENDING`/`CONFIRMED`) on the same property and date range are rejected; the total price is always computed server-side (`nights × pricePerNight`).
* Booking status transitions follow a strict lifecycle (e.g., only a `PENDING` booking can be approved/rejected).
* A booking may receive at most one review, and only once it reaches `COMPLETED`.
* A property's `averageRating`/`reviewCount` are recalculated automatically after every review create/update/delete.
* Passwords are BCrypt-hashed; refresh tokens can only be used to mint new access tokens, never to authenticate a request directly.

## Logging

Structured logs (console + rolling file under `logs/`) cover authentication events, property/booking/review lifecycle changes, and system-level failures (unexpected exceptions, authentication/authorization failures), using INFO/WARN/ERROR levels as appropriate. Passwords, tokens, and other sensitive data are never logged.

## Project Status

The backend is feature-complete, covering:

* Domain modeling & persistence
* Property management (CRUD, images, amenities, search/filter/sort/paginate)
* Booking lifecycle with availability validation
* Reviews & automatic rating aggregation
* JWT authentication, refresh tokens, and role-based authorization
* MapStruct-based DTO mapping
* OpenAPI/Swagger documentation
* Structured logging and centralized exception handling
