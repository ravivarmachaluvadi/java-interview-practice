# Spring, Microservices, Kafka

Backend framework interview notes, plus a small Spring Boot REST service to read alongside
them.

## Contents

| Folder | Files | What is here |
|---|---|---|
| [notes](notes/) | 6 | Q&A notes: Spring Boot, Spring Security, transactions, Hibernate/JPA, microservices, Kafka. |
| [orders-springboot-project](orders-springboot-project/) | 10 | A minimal orders REST service: controller, DTO with validation, global exception handler, two tests. |
| [snippets](snippets/) | 1 | A fuller `application.properties` for an H2 + JPA setup. |
| **Total** | **17** | |

## Notes

**Read these first:** [Spring transaction management](notes/Spring_Transaction_Management.md),
[Kafka Q&A](notes/Kafka_QA.md) — they hold the two mechanisms that get probed hardest.

| File | What it covers | Key insight |
|---|---|---|
| [Spring_Boot_QA.md](notes/Spring_Boot_QA.md) | Property source precedence, auto-configuration, starters, the Spring Boot 3 migration, `@ConfigurationProperties` | Auto-config runs *after* your beans, which is why `@ConditionalOnMissingBean` lets a bean you declare win. |
| [Spring_Security_QA.md](notes/Spring_Security_QA.md) | Authentication vs authorisation, the filter chain, the lambda DSL that replaced `WebSecurityConfigurerAdapter`, JWT, OAuth 2.0 grant types | OAuth 2.0 gets you an access token (authorisation); OIDC is what authenticates the user. |
| [Spring_Transaction_Management.md](notes/Spring_Transaction_Management.md) | The proxy model, the seven propagation levels, rollback rules, isolation levels | A call that does not cross the proxy gets no transaction — silently. |
| [Hibernate_JPA_QA.md](notes/Hibernate_JPA_QA.md) | Entity lifecycle, N+1 selects, `LazyInitializationException`, first vs second level cache, `equals`/`hashCode` for entities | N+1 is one extra query per **parent**, and making the association `EAGER` guarantees it rather than fixing it. |
| [Microservices_QA.md](notes/Microservices_QA.md) | Sagas, circuit breakers, service discovery, retries, idempotency, tracing, caching, CQRS | A circuit breaker without a timeout on the call does nothing. |
| [Kafka_QA.md](notes/Kafka_QA.md) | Consumer groups, offset commit semantics, exactly-once, ordering, retention vs compaction, `acks`, tuning | Ordering is per partition, never per topic — and partition count is the ceiling on consumer parallelism. |

Every note opens with a contents table and a list of the factual corrections made to it, so you
can see what you previously had wrong without re-reading the whole file.

## orders-springboot-project

A minimal Spring Boot 3.5 REST service on Java 21: `POST /v1/api/orders` and
`GET /v1/api/orders/{id}`, with Bean Validation on the request body and a `@ControllerAdvice`
that turns every failure into the same JSON error shape.

**Layout note:** the files are stored flat in this folder, the way every other file in this
repository is, but they carry real `com.target.orders.*` package declarations. To run it, copy
them into a standard Maven layout first — `src/main/java/com/target/orders/…` for the five main
classes, `src/test/java/com/target/orders/…` for the two tests, and
`src/main/resources/application.properties` — then `mvn spring-boot:run`.

| File | Package | What it shows |
|---|---|---|
| [OrdersApplication.java](orders-springboot-project/OrdersApplication.java) | `com.target.orders` | `@SpringBootApplication` and the `main` that boots the context. |
| [OrderController.java](orders-springboot-project/OrderController.java) | `…orders.controller` | `@RestController` with a shared `/v1/api/orders` prefix, `@Valid @RequestBody`, `@PathVariable`, `ResponseEntity`. |
| [OrderDTO.java](orders-springboot-project/OrderDTO.java) | `…orders.dto` | The request/response contract, and which validation annotation to reach for: `@NotNull` vs `@NotEmpty` vs `@NotBlank`. |
| [OrderNotFoundException.java](orders-springboot-project/OrderNotFoundException.java) | `…orders.exception.exceptions` | A custom unchecked exception as the signal for a 404. |
| [ErrorResponse.java](orders-springboot-project/ErrorResponse.java) | `…orders.exception.exceptions` | The error payload: timestamp, status, message, path, field errors. |
| [GlobalExceptionHandler.java](orders-springboot-project/GlobalExceptionHandler.java) | `…orders.exception.handler` | `@ControllerAdvice` extending `ResponseEntityExceptionHandler`; specificity, not declaration order, picks the handler. |
| [OrderControllerTest.java](orders-springboot-project/OrderControllerTest.java) | `…orders.controller` | MockMvc through the real dispatch chain, so routing, JSON binding, validation and the advice all run. |
| [OrdersApplicationTests.java](orders-springboot-project/OrdersApplicationTests.java) | `com.target.orders` | The context-loads smoke test. |
| [application.properties](orders-springboot-project/application.properties) | — | H2 in-memory datasource, H2 console, JPA `ddl-auto=create-drop`, SQL logging. |
| [pom.xml](orders-springboot-project/pom.xml) | — | Spring Boot 3.5.7 parent, Java 21, starters for web, data-jpa and validation, H2, Lombok. |

## snippets

Not part of the project above — a longer `application.properties` to copy from.

| File | What is in it |
|---|---|
| [application-h2-sample.properties](snippets/application-h2-sample.properties) | H2 + JPA setup, `spring.sql.init` schema and data locations, Hibernate JDBC batching (`batch_size`, `order_inserts`, `order_updates`), parallel JUnit execution, and commented-out `generate_statistics` and basic-auth lines. |

## Related notes elsewhere in this repo

- [Distributed transactions and Saga Q&A](../04-HLD-System-Design/notes/Distributed_Transactions_Saga_QA.md) — the system-design view of what [Microservices_QA.md](notes/Microservices_QA.md) covers from the service side.
- [SQL Q&A](../06-SQL/SQL_QA.md) — joins, indexes and query plans behind the Hibernate notes.
- [Interview memory Q&A](../07-Interview-QA-Memory/Interview_Memory_QA_Part1.md) — the same topics as they were actually asked in real rounds.
