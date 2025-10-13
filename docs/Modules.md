# Project Modules Overview

> TL;DR: one runnable app (`:backend`), the rest are lean libraries with clear boundaries. Quarkus extensions live in **`:backend`**. Libraries expose APIs & beans, not runtime wiring.

```
root
├─ backend/       ← the Quarkus app (only module that runs)
├─ api/           ← HTTP layer (JAX-RS resources, API DTOs/mappers)
├─ domain/        ← business logic (services, rules, aggregations)
├─ persistence/   ← database entities & repositories
├─ integration/   ← external systems (FRC APIs, webhooks, adapters)
├─ analytics/     ← metrics, ranking, heuristics/ML
└─ common/        ← shared types & utilities (no CDI)
```

---

## :backend — Application (Quarkus runtime)

**Purpose:** The only runnable module. Wires up Quarkus, brings in extensions, loads config, and depends on all other modules.

* **Contains:** `application.properties` (or YAML), Quarkus extensions, optional bootstrap code.
* **Depends on:** `:api`, `:domain`, `:persistence`, `:integration`, `:analytics`, `:common`.
* **Add here:** All Quarkus extensions (REST, DB, Flyway, Health, Micrometer, OIDC, WebSockets, etc.).
* **Don’t put here:** Core business logic; keep it in `:domain` and friends.

> Rule: **Only this module applies `id("io.quarkus")` and declares Quarkus extensions.**

---

## :api — HTTP Interface

**Purpose:** Defines how clients talk to us (REST endpoints, request/response DTOs, mappers).

* **Contains:** JAX-RS resources (`@Path`), DTOs tailored for the API surface, mapping from domain types.
* **Depends on:** `:domain`, `:common`.
* **CDI:** Yes (add `META-INF/beans.xml`).
* **Avoid:** DB access, heavy logic. Call `:domain` services instead.

> Think “controller” layer: thin, stateless, and boring (by design).

---

## :domain — Business Logic

**Purpose:** The brain. Encapsulates rules, calculations, and use-cases (e.g., compute team stats, validate scouting inputs).

* **Contains:** Services (`@ApplicationScoped`), domain models, rule engines, orchestrations.
* **Depends on:** `:common`. (May call repositories via interfaces or directly if you keep it simple.)
* **CDI:** Yes (add `META-INF/beans.xml`).
* **Avoid:** HTTP, DB specifics, transport-level details.

> If it smells like “what the product *does*,” it lives here.

---

## :persistence — Data Access

**Purpose:** Owns the database schema mapping and repository access.

* **Contains:** JPA/Panache entities, repositories, database mappers.
* **Depends on:** `:common`.
* **CDI:** Yes (add `META-INF/beans.xml`).
* **Runtime:** The actual ORM/JDBC/Flyway extensions are provided by `:backend`.
* **Avoid:** Business decisions or API concerns.

> Swap DB tech with minimal blast radius.

---

## :integration — External Adapters

**Purpose:** Talk to the outside world that isn’t your DB (FRC APIs, auth providers, messaging, file stores).

* **Contains:** HTTP clients, schedulers/pollers, adapters, data import/export pipelines.
* **Depends on:** `:common` (and optionally `:domain` for shapes).
* **CDI:** Optional; add `META-INF/beans.xml` if you define beans.
* **Avoid:** Core logic; this is wiring and translation.

> Ports & adapters: keep vendor quirks contained here.

---

## :analytics — Rankings & Insights

**Purpose:** Derived metrics, heuristics, predictions (e.g., OPR, ELO-style ranks, match outcome estimates).

* **Contains:** Computation services, feature extraction, light ML/heuristics.
* **Depends on:** `:common` (and optionally `:domain` for inputs).
* **CDI:** Optional; add `META-INF/beans.xml` if you define beans.
* **Avoid:** Transport concerns and DB plumbing.

> If it crunches numbers or forecasts, it belongs here.

---

## :common — Shared Types & Utilities

**Purpose:** Cross-cutting, dependency-free building blocks.

* **Contains:** DTOs used across layers, error types, tiny utilities, constants.
* **Depends on:** Nothing.
* **CDI:** **No** (no `beans.xml`).
* **Avoid:** Anything that needs Quarkus/Jakarta APIs or runtime.

> Keep it small and clean—this module should never drag runtime deps around.

---

## Cross-Module Rules of the Road

* **Extensions live in `:backend` only.** Libraries compile against APIs with `compileOnly` if needed (e.g., `jakarta.enterprise:cdi-api`, `jakarta.ws.rs:jakarta.ws.rs-api`).
* **CDI discovery:** Add `META-INF/beans.xml` in any library that defines beans; pure DTO/util modules don’t need it.
* **Dependency direction (no cycles):**

  ```
  backend  ->  api  ->  domain
  backend  ->  persistence
  backend  ->  integration
  backend  ->  analytics
  backend  ->  common
  (common has no downstream deps)
  ```
* **Where stuff goes:**

    * HTTP endpoints → `:api`
    * Business rules / use-cases → `:domain`
    * Entities & repositories → `:persistence`
    * External data (FRC API clients, schedulers) → `:integration`
    * Metrics/heuristics/predictions → `:analytics`
    * Shared DTOs/utilities → `:common`
    * Quarkus runtime config & extensions → `:backend`

---

## Example Request Flow

```
Client → :api (TeamResource)
      → :domain (TeamService)
      → :persistence (TeamRepository / Panache)
      ← :domain (apply rules/aggregations)
      ← :api (map to response DTO)
      ← Client
```

---

## Testing Guidance

* **Unit tests** in each library module (fast, no Quarkus boot).
* **Integration tests** with `@QuarkusTest` in a dedicated test module or in `:backend` (so Quarkus augmentation runs once).
* Keep fixtures/test data near the module that uses them.

---

## Config & Migrations

* App config: `backend/src/main/resources/application.properties` (use `%dev`, `%test`, `%prod` profiles).
* Flyway migrations: `backend/src/main/resources/db/migration` (or keep here and point Flyway to it).

---

## Naming Hints

* Resources: `*Resource.kt` (e.g., `TeamResource`)
* Services: `*Service.kt` (e.g., `TeamService`)
* Entities: singular nouns (e.g., `Team`)
* Repositories: `*Repository.kt` (e.g., `TeamRepository`)
* API DTOs vs Shared DTOs: prefer API-specific DTOs in `:api`, cross-layer DTOs in `:common`.

---

## What to Avoid

* Putting Quarkus extensions in library modules (bloats builds, confuses augmentation).
* Mixing JSON stacks (pick **Kotlinx** or **Jackson**, not both unless you’re sure).
* Business logic in controllers or repositories.
* Cyclic dependencies (“why is everything depending on everything?” → pain).
