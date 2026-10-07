# Spring Boot Starters — Interview Q&A

A **starter** is a dependency you add to get a whole feature working: the libraries,
compatible versions, and Spring Boot's ready-made configuration for them. This file
goes through the starters interviewers ask about. For each one it says what the
starter brings, what Boot configures for you, the defaults that matter, and the
questions that usually follow.

Three starters have their own deep-dive files:
[06 Web](06_Spring_Boot_Web_QA.md) · [07 Actuator](07_Spring_Boot_Actuator_QA.md) ·
[08 AWS](08_Spring_Cloud_AWS_QA.md). JPA and Hibernate are in
[02](02_Hibernate_QA.md) and [03](03_JPA_Spring_Data_QA.md).

Version facts were checked against the BOMs and jars in the local Maven repository:
Spring Boot 3.5.7, 4.0.3 and 4.1.1.

## Weight legend

| Mark | What it means | How the answer is written |
| --- | --- | --- |
| ★★★ | Asked in almost every Java backend round, with follow-ups | Answer, mechanism, code, traps |
| ★★ | Asked often | Answer and one example or table |
| ★ | Occasional, or a senior-level bonus | Two to four lines |

## Contents

| # | Topic | Weight | The one thing to remember |
| --- | --- | --- | --- |
| 1 | [What a starter is](#1-what-exactly-is-a-starter) | ★★★ | A POM with no code; versions come from one BOM |
| 2 | [Why a jar changes behaviour](#2-why-does-adding-a-jar-change-how-the-app-behaves) | ★★★ | `@ConditionalOnClass` reacts to the classpath |
| 3 | [Boot 4 starter changes](#3-which-starters-changed-in-boot-4) | ★★ | aop → aspectj; new kafka, flyway, restclient starters |
| 4 | [web / webmvc](#4-web-and-webmvc) | ★★★ | Spring MVC + Tomcat + Jackson |
| 5 | [webflux](#5-webflux) | ★★ | Netty + Reactor; MVC wins if both are present |
| 6 | [validation](#6-validation) | ★★ | Not included in web since Boot 2.3 |
| 7 | [jdbc](#7-jdbc) | ★★ | HikariCP, pool size 10 by default |
| 8 | [data-jpa](#8-data-jpa) | ★★★ | Never `ddl-auto=update` in production |
| 9 | [security](#9-security) | ★★★ | Adding it locks every endpoint |
| 10 | [oauth2 resource server](#10-security-oauth2-resource-server) | ★★ | One `issuer-uri` property validates JWTs |
| 11 | [actuator](#11-actuator) | ★★★ | Only `health` exposed by default |
| 12 | [cache](#12-cache) | ★★ | Default cache has no TTL and no size limit |
| 13 | [data-redis](#13-data-redis) | ★★ | `RedisTemplate` uses JDK serialization by default |
| 14 | [kafka](#14-kafka) | ★★ | A starter only from Boot 4; Boot 3 uses `spring-kafka` |
| 15 | [flyway, liquibase](#15-flyway-and-liquibase) | ★★ | Migrations run before JPA starts |
| 16 | [test](#16-test) | ★★★ | JUnit 5, Mockito, AssertJ, Awaitility, JsonPath… |
| 17 | [aspectj](#17-aspectj) | ★ | Only needed for your own `@Aspect` classes |
| 18 | [docker-compose, testcontainers](#18-docker-compose-and-testcontainers) | ★★ | Services start for you; `@ServiceConnection` wires them |
| 19 | [devtools](#19-devtools) | ★ | Restart on change; off in a packaged jar |
| 20 | [Spring Cloud AWS starters](#20-spring-cloud-aws-starters) | ★★ | Separate project, separate BOM |

---

## 1. What exactly is a starter?

**Weight:** ★★★

**Answer:** a starter is a Maven/Gradle artifact that contains **no code** — only a
list of dependencies. Adding `spring-boot-starter-web` pulls in Spring MVC, an embedded
Tomcat, Jackson for JSON, and logging, all at versions known to work together.

**Where the versions come from:** the `spring-boot-dependencies` BOM (bill of
materials). You get it through `spring-boot-starter-parent`, or by importing the BOM
in `<dependencyManagement>`. That is why you write starters without a `<version>`.

**How it connects to auto-configuration:** the starter puts libraries on the
classpath. Boot's auto-configuration classes check the classpath and create the
beans those libraries need (next question).

**Follow-up — how do you see what a starter brought in?**
`mvn dependency:tree` (Maven) or `gradle dependencies` (Gradle).

**Follow-up — how do you override one library version?** Set the matching property
in your POM, e.g. `<hibernate.version>…</hibernate.version>`. Do it only for a
specific reason, such as a security fix, because the BOM's versions are tested
together.

---

## 2. Why does adding a jar change how the app behaves?

**Weight:** ★★★

**Mechanism:** auto-configuration classes are guarded by conditions such as
`@ConditionalOnClass(DataSource.class)`. When a new jar puts that class on the
classpath, the condition becomes true and Boot creates beans.

**Concrete example:** add `spring-boot-starter-data-jpa` without configuring a
database, and startup fails with "Failed to configure a DataSource". JPA's
auto-configuration now wants a database, and no URL was given.

**Ways to control it:**

| Want | Do |
| --- | --- |
| See which auto-configurations ran and why | Start with `--debug`, or Actuator `/actuator/conditions` |
| Switch one off | `spring.autoconfigure.exclude=…` or `@SpringBootApplication(exclude = …)` |
| Replace Boot's bean | Define your own bean of the same type — Boot backs off |

Full mechanism: [04 Q3](04_Spring_Boot_QA.md#3-how-does-auto-configuration-work).

---

## 3. Which starters changed in Boot 4?

**Weight:** ★★

Checked by comparing the 3.5.7 and 4.0.3 BOMs. The 3.5.7 BOM lists 55 starters; the
4.0.3 BOM lists 162, because Boot 4 adds a starter per technology and a matching
`-test` starter for most of them.

| Change | Boot 3.5 | Boot 4.0 |
| --- | --- | --- |
| Renamed | `spring-boot-starter-aop` | `spring-boot-starter-aspectj` |
| Added next to the old name | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| Renamed (old name still listed) | `spring-boot-starter-oauth2-resource-server` | `spring-boot-starter-security-oauth2-resource-server` |
| Removed | `spring-boot-starter-undertow` | — |
| New starters | add `spring-kafka`, `flyway-core` yourself | `spring-boot-starter-kafka`, `-flyway`, `-liquibase`, `-restclient`, `-webclient`, `-jackson`, `-opentelemetry`, `-zipkin` |
| New test starters | one `spring-boot-starter-test` | plus `-webmvc-test`, `-data-jpa-test`, `-kafka-test`, … |
| Migration aid | — | `spring-boot-starter-classic` and `spring-boot-starter-test-classic` |

**Why Boot 4 did this:** auto-configuration was split into one module per technology
(`spring-boot-jdbc`, `spring-boot-hibernate`, `spring-boot-webmvc`…). Each starter now
brings exactly its own module, so an application loads less auto-configuration it
doesn't use.

---

## 4. web and webmvc

**Weight:** ★★★ — full detail in [06_Spring_Boot_Web_QA.md](06_Spring_Boot_Web_QA.md).

- **Brings:** Spring MVC, embedded Tomcat, Jackson.
- **Auto-configures:** `DispatcherServlet` at `/`, JSON message converters, the
  `/error` endpoint, static resource handling, multipart upload support.
- **Defaults that matter** (checked in 3.5.7 and 4.0.3 metadata):

| Property | Default |
| --- | --- |
| `server.tomcat.threads.max` | 200 |
| `server.tomcat.max-connections` | 8192 |
| `server.tomcat.accept-count` | 100 |
| `spring.servlet.multipart.max-file-size` / `max-request-size` | 1MB / 10MB |
| `server.max-http-request-header-size` | 8KB |

**Q: What happens if you add both `-web` and `-webflux`?** The app runs as a servlet
(MVC) app on Tomcat. WebFlux is then just a library — useful for `WebClient`. Force
the type with `spring.main.web-application-type=reactive` if you really want reactive.

---

## 5. webflux

**Weight:** ★★

- **Brings:** Spring WebFlux, Reactor (`Mono`, `Flux`), Reactor Netty as the server.
- **Model:** a few event-loop threads handle many connections, as long as **nothing
  blocks**. One blocking JDBC call on an event-loop thread stalls every request that
  thread serves.
- **Use it when:** streaming (server-sent events), very high fan-out to other
  services, or a fully non-blocking stack (R2DBC, reactive Mongo/Redis drivers).
- **Don't use it** to get "more concurrency" in a JDBC/JPA app. Virtual threads in MVC
  (`spring.threads.virtual.enabled=true`) give that with plain blocking code
  ([04 Q24](04_Spring_Boot_QA.md#24-embedded-server-threads-and-virtual-threads)).

---

## 6. validation

**Weight:** ★★

- **Brings:** Hibernate Validator (the Bean Validation implementation).
- **Trap:** it has **not** been part of the web starter since Boot 2.3. Without it,
  `@Valid` and `@NotBlank` compile but nothing is validated.
- **Auto-configures:** a `Validator` bean and method validation for `@Validated`
  classes.

Details: [04 Q18](04_Spring_Boot_QA.md#18-validation).

---

## 7. jdbc

**Weight:** ★★

- **Brings:** Spring JDBC and **HikariCP** (Boot's default connection pool).
- **Auto-configures:** `DataSource` from `spring.datasource.url/username/password`,
  `JdbcTemplate`, `NamedParameterJdbcTemplate`, `JdbcClient`, and a transaction manager.
- **Hikari defaults:** pool size **10** (checked: `DEFAULT_POOL_SIZE = 10` in HikariCP
  7.0.2), connection timeout 30 s.

**Q: How big should the pool be?** Smaller than people expect. A database only runs as
many queries in parallel as it has cores and disks; extra connections just queue
inside the database. Start near `cores × 2`, then measure. Remember the total:
**pool size × number of app instances** must stay under the database's
`max_connections`. Ten pods × 50 connections = 500.

**Q: `JdbcClient` vs `JdbcTemplate`?** `JdbcClient` (Spring 6.1) is a fluent API over
the same engine:

```java
Optional<Order> order = jdbcClient
        .sql("select id, status from orders where id = :id")
        .param("id", id)
        .query(Order.class)
        .optional();
```

---

## 8. data-jpa

**Weight:** ★★★ — full detail in [02](02_Hibernate_QA.md) and [03](03_JPA_Spring_Data_QA.md).

- **Brings:** Spring Data JPA, Hibernate, Spring JDBC, HikariCP.
- **Auto-configures:** `DataSource`, `EntityManagerFactory`, `JpaTransactionManager`,
  repository proxies for every `JpaRepository` interface found under the main class's
  package.

**Defaults that cause production issues:**

| Setting | Default | Why it matters |
| --- | --- | --- |
| `spring.jpa.open-in-view` | `true` (checked 3.5.7, 4.0.3) | Holds a DB connection for the whole HTTP request — turn it off |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` for an embedded DB with no migration tool, otherwise `none` | Never use `update` in production |

**Q: Why not `ddl-auto=update` in production?** It never drops or renames columns, it
can't migrate data, it isn't reviewed or versioned, and with several instances
starting at once they race on the schema. Use Flyway or Liquibase (Q15) and set
`ddl-auto=validate` so Hibernate checks the schema matches the entities.

---

## 9. security

**Weight:** ★★★ — more in [Spring_Security_QA.md](../05-Spring-Microservices/notes/Spring_Security_QA.md).

**What happens the moment you add it:**

- **Every** endpoint requires authentication — even ones you didn't think about.
- Boot creates one user named `user` and logs a random password at startup
  ("Using generated security password: …").
- Form login and HTTP Basic are switched on; CSRF protection is on.

**Customizing:** define a `SecurityFilterChain` bean (the old
`WebSecurityConfigurerAdapter` was removed in Spring Security 6):

```java
@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable());   // only for stateless token APIs
    return http.build();
}
```

**Q: Why is disabling CSRF fine here?** CSRF attacks ride on cookies the browser sends
automatically. A stateless API that only accepts a bearer token in the
`Authorization` header has no such cookie. If the API uses session cookies, keep CSRF
on.

---

## 10. security-oauth2-resource-server

**Weight:** ★★

- **Name:** `spring-boot-starter-oauth2-resource-server` in Boot 3;
  `spring-boot-starter-security-oauth2-resource-server` in Boot 4.
- **Minimum config:**
  `spring.security.oauth2.resourceserver.jwt.issuer-uri=https://idp.example.com/realms/shop`

**Q: How is a JWT validated?**

1. The signature is checked with the issuer's public keys, fetched from its JWKS
   endpoint (found through the issuer's discovery document, or set directly with
   `jwk-set-uri`).
2. `exp` and `nbf` (time window) and `iss` (issuer) are checked.
3. `aud` (audience) is checked if you set `…jwt.audiences`.

No call to the identity provider is made per request — only the cached keys are used.
That is why a revoked token stays valid until it expires: keep access tokens
short-lived.

**Q: Where do roles come from?** By default each entry in the `scope` / `scp` claim
becomes an authority with the `SCOPE_` prefix. For a `roles` claim, set
`…jwt.authorities-claim-name=roles` and `…jwt.authority-prefix=ROLE_` (both properties
checked in 4.1.1), or write a `JwtAuthenticationConverter`.

---

## 11. actuator

**Weight:** ★★★ — full detail in [07_Spring_Boot_Actuator_QA.md](07_Spring_Boot_Actuator_QA.md).

- **Brings:** production endpoints (health, metrics, info, loggers…) and Micrometer.
- **Default exposure over HTTP:** only `health` (checked in 4.1.1).
- **Default access:** `shutdown` and `heapdump` are off (`access = none`, checked in
  4.1.1).

---

## 12. cache

**Weight:** ★★

- **Brings:** Spring's cache abstraction (`@Cacheable`, `@CachePut`, `@CacheEvict`).
- **Auto-configures** a `CacheManager` for whichever provider it finds: Caffeine,
  Redis, JCache, Hazelcast…
- **Trap:** with no provider on the classpath, it falls back to a `ConcurrentHashMap`
  with **no expiry and no size limit** — a memory leak in production.

Details: [04 Q21](04_Spring_Boot_QA.md#21-caching).

---

## 13. data-redis

**Weight:** ★★

- **Brings:** Spring Data Redis with the **Lettuce** client (thread-safe, one shared
  connection, non-blocking underneath).
- **Auto-configures:** `RedisConnectionFactory`, `RedisTemplate<Object, Object>` and
  `StringRedisTemplate`.

**The classic trap:** the auto-configured `RedisTemplate` uses **JDK serialization**.
Keys and values in Redis then look like `\xac\xed\x00\x05t\x00\x07order:1` — unreadable
from `redis-cli`, other languages, or after a class change. Use `StringRedisTemplate`,
or configure `StringRedisSerializer` for keys and a JSON serializer for values.

**Common uses to mention:** a cache with TTL, rate-limit counters (`INCR` +
`EXPIRE`), distributed locks (Redisson or ShedLock), session storage
(`spring-boot-starter-session-data-redis` in Boot 4).

---

## 14. kafka

**Weight:** ★★ — more in [Kafka_QA.md](../05-Spring-Microservices/notes/Kafka_QA.md).

- **Boot 3:** there is **no** Kafka starter. Add `org.springframework.kafka:spring-kafka`
  and Boot's auto-configuration picks it up. **Boot 4:** `spring-boot-starter-kafka`
  (both checked in the BOMs).
- **Auto-configures:** `KafkaTemplate`, the listener container factory behind
  `@KafkaListener`, and a `KafkaAdmin` that creates any `NewTopic` beans you declare.
- **Key properties:** `spring.kafka.bootstrap-servers`, `spring.kafka.consumer.group-id`,
  `spring.kafka.consumer.auto-offset-reset`, key/value serializers.

**Q: Who commits offsets?** Spring's listener container, not the Kafka client: Spring
Kafka turns off the client's auto-commit and commits after the listener returns
(acknowledgement mode `BATCH` by default). So a record that throws is not committed,
and the error handler decides about retries and the dead-letter topic.

---

## 15. flyway and liquibase

**Weight:** ★★

- **Boot 3:** add `flyway-core` (plus the database module, e.g.
  `flyway-database-postgresql`, for Flyway 10 and later). **Boot 4:**
  `spring-boot-starter-flyway` or `spring-boot-starter-liquibase`.
- **What Boot does:** runs pending migrations at startup, **before** the JPA
  `EntityManagerFactory` is created, so Hibernate always sees the migrated schema.
- **Flyway files:** `src/main/resources/db/migration/V1__create_orders.sql`,
  `V2__add_status.sql`. Applied migrations are recorded in `flyway_schema_history`;
  editing an applied file fails the checksum check.

**Q: Several pods start at once — do they all run the migration?** No. Flyway takes a
database lock, so one instance migrates and the others wait. For long migrations,
many teams run them as a separate job (a Kubernetes Job, or a pipeline step) before
the deployment, instead of inside application startup.

**Q: How do you rename a column with zero downtime?** Expand and contract: add the new
column, write to both, backfill, switch reads, then drop the old column in a later
release. Old and new app versions run side by side during a rolling deploy, so every
migration must work with both.

---

## 16. test

**Weight:** ★★★ — test slices and mocks in [04 Q27](04_Spring_Boot_QA.md#27-testing).

**What it brings** (checked in the 3.5.7, 4.0.3 and 4.1.1 POMs):

| Library | Used for |
| --- | --- |
| JUnit Jupiter (JUnit 5) | The test framework |
| Spring Test, Spring Boot Test | Test contexts, slices, `MockMvc` |
| Mockito (+ JUnit integration) | Mocks |
| AssertJ, Hamcrest | Assertions |
| JsonPath, JSONassert | Assertions on JSON |
| Awaitility | Waiting for async results without `Thread.sleep` |
| XMLUnit | Assertions on XML |

**Boot 4:** each technology also has its own test starter
(`spring-boot-starter-webmvc-test`, `-data-jpa-test`, `-kafka-test`…) that brings the
matching test slice.

**Q: Testing an async consumer without sleeping?**

```java
await().atMost(Duration.ofSeconds(10))
       .untilAsserted(() -> assertThat(orderRepository.findById(id))
               .hasValueSatisfying(o -> assertThat(o.getStatus()).isEqualTo(PAID)));
```

---

## 17. aspectj

**Weight:** ★

- **Name:** `spring-boot-starter-aop` in Boot 3; `spring-boot-starter-aspectj` in Boot 4.
- Brings `aspectjweaver`, needed for **your own** `@Aspect` classes. `@Transactional`,
  `@Cacheable` and `@Async` work without it.

Details: [01 Q15](01_Spring_AOP_QA.md#15-enabling-aop-in-spring-boot).

---

## 18. docker-compose and testcontainers

**Weight:** ★★

- **`spring-boot-docker-compose`** (Boot 3.1+, add as a development-only dependency):
  when you run the app locally, Boot finds `compose.yaml`, runs `docker compose up`,
  and connects the app to the containers it started — no datasource URL to write.
- **`spring-boot-testcontainers`** + `@ServiceConnection` (Boot 3.1+): in tests, Boot
  reads the URL and credentials from a Testcontainers container and configures the
  matching connection (database, Kafka, Redis…).

**Q: Why prefer Testcontainers over H2 for repository tests?** H2 is a different
database. SQL dialect, JSON columns, locking, and constraint behaviour differ, so
tests can pass on H2 and fail on PostgreSQL. A real PostgreSQL in a container tests
what production runs.

---

## 19. devtools

**Weight:** ★

- Automatic restart when classes change. Two class loaders are used: libraries load
  once, your code reloads — faster than a cold start.
- LiveReload for the browser; development-friendly defaults such as template caching
  off.
- **Switched off automatically** when the app runs as a packaged jar (`java -jar`),
  which Boot treats as production. Still, declare it as optional /
  `developmentOnly` so it never ships.

---

## 20. Spring Cloud AWS starters

**Weight:** ★★ — full detail in [08_Spring_Cloud_AWS_QA.md](08_Spring_Cloud_AWS_QA.md).
AWS itself (EC2, VPC, S3, RDS, Lambda…) is covered in files 09–12, starting with
[09_AWS_Compute_IAM_Networking_QA.md](09_AWS_Compute_IAM_Networking_QA.md).

- Not part of Spring Boot: a separate project (`io.awspring.cloud`) with its own BOM,
  `spring-cloud-aws-dependencies`.
- Starters: `spring-cloud-aws-starter-s3`, `-sqs`, `-sns`, `-ses`, `-dynamodb`,
  `-secrets-manager`, `-parameter-store`, `-imds`.
- **Match the version to Boot:** Spring Cloud AWS 3.4.x ↔ Boot 3.5.x; 4.x ↔ Boot 4.0.x
  (from the project's compatibility table).
