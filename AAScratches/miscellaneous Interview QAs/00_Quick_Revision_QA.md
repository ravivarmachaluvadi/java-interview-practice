# Quick Revision — Commonly Asked Questions, Simple Answers

**What this is:** the questions that come up most often in Java backend and AWS
interviews, each answered in one to three plain sentences — the version to say first
in an interview. Each answer links (→) to the detailed answer in files 01–12 for the
follow-up questions.

Questions marked **(basic)** have no detailed version elsewhere — they are simple
warm-up questions that only need the short answer.

**Docker and Kubernetes** are not repeated here: [13_Docker_QA.md](13_Docker_QA.md) and
[14_Kubernetes_QA.md](14_Kubernetes_QA.md) each open with a contents table that gives a
one-line answer per question — use those tables for quick revision.

**JavaScript and React** work the same way: teaching files
[15_JavaScript_Fundamentals.md](15_JavaScript_Fundamentals.md) and
[16_React_Fundamentals.md](16_React_Fundamentals.md) (both list online playgrounds), and
interview files [17_JavaScript_QA.md](17_JavaScript_QA.md) and
[18_React_QA.md](18_React_QA.md) with one-line answers in their contents tables.

**End-to-end microservices** (one request from the UI through the gateway, services,
Kafka and databases, plus how it's secured, deployed and monitored) are files 19–27,
again with one-line answers in each contents table:
[19 request flow and architecture](19_Microservices_Request_Flow_Architecture_QA.md),
[20 security](20_Microservices_Security_AuthN_AuthZ_QA.md),
[21 gateway, rate limiting, resilience](21_API_Gateway_Rate_Limiting_Resilience_QA.md),
[22 Kafka and events](22_Kafka_Event_Driven_Microservices_QA.md),
[23 caching and data](23_Caching_Data_Management_QA.md),
[24 patterns catalogue](24_Microservice_Patterns_Catalogue_QA.md),
[25 CI/CD](25_CI_CD_Build_Deploy_QA.md),
[26 observability](26_Observability_Logging_Monitoring_Tracing_QA.md),
[27 production scenarios](27_Microservices_Production_Scenarios_QA.md).

| Section | Questions | Detailed files |
| --- | --- | --- |
| [Spring core and Spring Boot](#spring-core-and-spring-boot) | 1–26 | [04](04_Spring_Boot_QA.md) |
| [Spring AOP](#spring-aop) | 27–35 | [01](01_Spring_AOP_QA.md) |
| [JPA and Hibernate](#jpa-and-hibernate) | 36–58 | [02](02_Hibernate_QA.md), [03](03_JPA_Spring_Data_QA.md) |
| [Spring Data JPA](#spring-data-jpa) | 59–66 | [03](03_JPA_Spring_Data_QA.md) |
| [REST and Spring Web](#rest-and-spring-web) | 67–82 | [06](06_Spring_Boot_Web_QA.md), [04](04_Spring_Boot_QA.md) |
| [Starters and Actuator](#starters-and-actuator) | 83–95 | [05](05_Spring_Boot_Starters_QA.md), [07](07_Spring_Boot_Actuator_QA.md) |
| [AWS basics](#aws-basics) | 96–103 | [09](09_AWS_Compute_IAM_Networking_QA.md), [12](12_AWS_Architecture_Practices_Scenarios_QA.md) |
| [AWS compute and networking](#aws-compute-and-networking) | 104–120 | [09](09_AWS_Compute_IAM_Networking_QA.md) |
| [AWS storage, databases, messaging](#aws-storage-databases-and-messaging) | 121–133 | [10](10_AWS_Storage_Databases_Messaging_QA.md) |
| [AWS serverless, containers, DevOps](#aws-serverless-containers-and-devops) | 134–146 | [11](11_AWS_Serverless_Containers_DevOps_QA.md) |
| [AWS architecture and scenarios](#aws-architecture-and-scenarios) | 147–158 | [12](12_AWS_Architecture_Practices_Scenarios_QA.md), [09](09_AWS_Compute_IAM_Networking_QA.md) |

---

## Spring core and Spring Boot

**1. What is Spring Boot, and how is it different from Spring?**
Spring is the framework (dependency injection, MVC, data access, security). Spring
Boot is Spring with the setup done for you — starters for dependencies,
auto-configuration, an embedded server and production tools — so you write almost no
configuration. → [04 Q1](04_Spring_Boot_QA.md#1-what-is-spring-boot-and-how-is-it-different-from-spring)

**2. What are IoC and dependency injection?**
Instead of a class creating the objects it needs with `new`, Spring creates them
(beans) and passes them in. Classes stay loosely coupled and are easy to test with
mocks. → [04 Q11](04_Spring_Boot_QA.md#11-dependency-injection-styles-and-circular-dependencies)

**3. Constructor, setter or field injection — which do you use?**
Constructor injection: dependencies can be `final`, the object can't exist without
them, and unit tests just call the constructor. Field injection hides dependencies and
needs reflection to test. → [04 Q11](04_Spring_Boot_QA.md#11-dependency-injection-styles-and-circular-dependencies)

**4. What does `@SpringBootApplication` do?**
It combines `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan`. The scan
covers the main class's package and everything below it. → [04 Q2](04_Spring_Boot_QA.md#2-what-does-springbootapplication-do)

**5. How does auto-configuration work?**
Boot reads a list of auto-configuration classes from its jars. Each one has conditions
— is a class on the classpath, is a bean missing, is a property set — and creates
beans only if they pass. If you define your own bean, Boot's default backs off.
→ [04 Q3](04_Spring_Boot_QA.md#3-how-does-auto-configuration-work)

**6. `@Component` vs `@Service` vs `@Repository` vs `@Controller`?** (basic)
All four register a bean. `@Repository` also converts database exceptions into
Spring's `DataAccessException`; `@Controller` handles web requests; `@Service` only
marks business logic.

**7. `@Bean` vs `@Component`?** (basic)
`@Component` goes on your own class and is found by scanning. `@Bean` goes on a method
in a `@Configuration` class — used for third-party classes you can't annotate, or when
creating the object needs logic.

**8. Why are `@Configuration` classes proxied?** (basic)
So that calling one `@Bean` method from another returns the same singleton instead of
creating a new object. `@Configuration(proxyBeanMethods = false)` turns this off for
faster startup.

**9. BeanFactory vs ApplicationContext?** (basic)
`ApplicationContext` is the full container: everything `BeanFactory` does plus events,
profiles and properties, internationalisation, AOP integration and eager creation of
singletons. In practice you always use `ApplicationContext`.

**10. What bean scopes exist?**
Singleton (default, one per container), prototype (new each time), and for web apps
request, session, application and websocket. A prototype injected into a singleton is
created only once — use `ObjectProvider` to get a new one each time.
→ [04 Q10](04_Spring_Boot_QA.md#10-bean-scopes-and-the-prototype-in-singleton-problem)

**11. Describe the bean lifecycle.**
Constructor → dependencies injected → `@PostConstruct` → AOP proxy created if needed →
bean in use → `@PreDestroy` on shutdown. → [04 Q9](04_Spring_Boot_QA.md#9-bean-lifecycle)

**12. Two beans of the same type — how does Spring choose?**
Mark one `@Primary` as the default, pick one with `@Qualifier("name")`, or inject all of
them as a `List` or `Map`. → [04 Q12](04_Spring_Boot_QA.md#12-several-beans-of-the-same-type)

**13. What is a circular dependency, and how do you fix it?**
Bean A needs B and B needs A. Boot refuses this by default since 2.6. Fix the design
(move shared logic into a third bean) or, as a last resort, put `@Lazy` on one side.
→ [04 Q11](04_Spring_Boot_QA.md#11-dependency-injection-styles-and-circular-dependencies)

**14. `application.properties` vs `application.yml`?** (basic)
Same settings, different format. YAML is hierarchical and can hold several profile
sections in one file. If both files exist in the same place, `.properties` wins for the
same key.

**15. How do you change the server port?**
`server.port=9090` in the config file, `--server.port=9090` on the command line, or the
`SERVER_PORT` environment variable.
→ [Spring_Boot_QA Q1](../05-Spring-Microservices/notes/Spring_Boot_QA.md#1-where-can-serverport-be-set-and-which-property-source-wins)

**16. Which configuration source wins?**
Command-line arguments beat Java system properties, which beat environment variables,
which beat config files. Among files, outside the jar beats inside, and
profile-specific beats plain. → [04 Q13](04_Spring_Boot_QA.md#13-externalized-configuration-and-precedence)

**17. `@Value` vs `@ConfigurationProperties`?**
`@Value` injects one value. `@ConfigurationProperties` binds a whole group of related
settings into a typed class or record, with validation — use it for anything with more
than one setting. → [04 Q14](04_Spring_Boot_QA.md#14-value-vs-configurationproperties)

**18. What are profiles?**
Named sets of configuration and beans per environment (dev, prod). Activate with
`spring.profiles.active=prod`; `application-prod.yml` then overrides the defaults.
→ [04 Q15](04_Spring_Boot_QA.md#15-profiles)

**19. `CommandLineRunner` vs `ApplicationRunner`?** (basic)
Both run code once the application has started. `CommandLineRunner` receives the raw
`String[]` arguments; `ApplicationRunner` receives them parsed into options and values.

**20. What does `@Transactional` do, and when does it not work?**
It runs the method in a database transaction, through a proxy. By default it rolls
back only on unchecked exceptions. It does nothing on calls from the same class
(self-invocation) or on private methods. → [04 Q19](04_Spring_Boot_QA.md#19-transactional-essentials)

**21. What does `@Async` do?**
Runs the method on another thread so the caller doesn't wait. Needs `@EnableAsync` and
a call from another bean. Configure the thread pool — Boot's default queue is
unbounded. → [04 Q20](04_Spring_Boot_QA.md#20-async-methods-and-thread-pools)

**22. How does caching work in Spring?**
`@Cacheable` stores a method's result and returns it next time for the same arguments;
`@CacheEvict` removes it. Use a real provider (Caffeine, Redis) with an expiry time.
→ [04 Q21](04_Spring_Boot_QA.md#21-caching)

**23. Which server does Boot use, and how many requests can it handle?**
Embedded Tomcat by default, with 200 worker threads; the app runs as `java -jar`.
Switch to Jetty by swapping the starter. → [04 Q24](04_Spring_Boot_QA.md#24-embedded-server-threads-and-virtual-threads)

**24. How do you test a Spring Boot application?**
`@SpringBootTest` for the full app, slices such as `@WebMvcTest` (controllers) and
`@DataJpaTest` (repositories) for one layer, `@MockitoBean` for mocks, and
Testcontainers for a real database. → [04 Q27](04_Spring_Boot_QA.md#27-testing)

**25. RestTemplate vs WebClient vs RestClient?**
`RestTemplate` is the old blocking client (maintenance mode). `WebClient` is
non-blocking (reactive). `RestClient` is the modern blocking client — use it for new
code, and always set timeouts. → [04 Q28](04_Spring_Boot_QA.md#28-http-clients)

**26. What is graceful shutdown?**
On shutdown, the server stops taking new requests but lets running ones finish (30 s by
default) before closing. → [04 Q25](04_Spring_Boot_QA.md#25-graceful-shutdown)

---

## Spring AOP

**27. What is AOP?**
A way to put code that many methods need — transactions, security, logging — in one
place (an aspect) and apply it automatically, instead of repeating it in every method.
→ [01 Q1](01_Spring_AOP_QA.md#1-what-is-aop-and-what-problem-does-it-solve)

**28. Aspect, advice, pointcut, join point?**
A **pointcut** chooses *where* (which methods), **advice** is *what* runs and *when*,
an **aspect** packages both, and a **join point** is one method call where it runs.
→ [01 Q2](01_Spring_AOP_QA.md#2-the-core-aop-terms)

**29. What advice types are there?**
`@Before`, `@AfterReturning`, `@AfterThrowing`, `@After` (always, like finally) and
`@Around` (wraps the call and can change or skip it).
→ [01 Q6](01_Spring_AOP_QA.md#6-the-five-advice-types-and-their-order)

**30. How does Spring AOP work?**
Spring wraps the bean in a **proxy**. Callers talk to the proxy, which runs the advice
and then calls the real object. → [01 Q3](01_Spring_AOP_QA.md#3-how-does-spring-aop-work-internally)

**31. JDK dynamic proxy vs CGLIB?**
A JDK proxy implements the bean's interfaces; CGLIB creates a subclass of the class.
Spring Boot uses CGLIB by default, so `final` classes and methods can't be advised.
→ [01 Q4](01_Spring_AOP_QA.md#4-jdk-dynamic-proxy-vs-cglib-proxy)

**32. Why is my `@Transactional` ignored when I call the method from the same class?**
The call uses `this`, not the proxy, so no advice runs. Move the method to another bean.
→ [01 Q10](01_Spring_AOP_QA.md#10-self-invocation-why-the-annotation-is-ignored)

**33. Spring AOP vs AspectJ?**
Spring AOP uses runtime proxies and only works on public bean methods. AspectJ changes
the bytecode itself, so it also works on private methods, self-calls and non-Spring
objects. → [01 Q5](01_Spring_AOP_QA.md#5-spring-aop-vs-aspectj)

**34. Where does Spring itself use AOP?**
`@Transactional`, `@Async`, `@Cacheable`, `@PreAuthorize`, `@Retryable` and method
validation are all proxies. → [01 Q14](01_Spring_AOP_QA.md#14-spring-features-built-on-aop)

**35. Filter vs interceptor vs aspect?**
A filter sees raw HTTP before Spring MVC; an interceptor sees the request and which
controller handles it; an aspect works on any bean method, HTTP or not.
→ [01 Q17](01_Spring_AOP_QA.md#17-filter-vs-handlerinterceptor-vs-aspect)

---

## JPA and Hibernate

**36. JPA vs Hibernate vs Spring Data JPA?**
JPA is the specification (annotations and the `EntityManager` API). Hibernate is the
implementation that does the work. Spring Data JPA generates repositories on top so you
write less code. → [03 Q1](03_JPA_Spring_Data_QA.md#1-jpa-vs-hibernate-vs-spring-data-jpa)

**37. What does a class need to be an entity?**
`@Entity`, an `@Id` field, a no-argument constructor, and it must not be `final`.
→ [03 Q3](03_JPA_Spring_Data_QA.md#3-what-makes-a-class-an-entity)

**38. What are the entity states?**
Transient (new, not tracked), managed (tracked — changes saved automatically), detached
(was tracked, no longer), removed (will be deleted).
→ [03 Q4](03_JPA_Spring_Data_QA.md#4-entity-lifecycle-states)

**39. What is the first-level cache?**
The persistence context: inside one transaction, each entity is loaded once and the
same object is returned for the same id. Always on.
→ [02 Q3](02_Hibernate_QA.md#3-the-first-level-cache)

**40. What is the second-level cache?**
An optional cache shared by all sessions, for data that is read often and rarely
changed. → [02 Q12](02_Hibernate_QA.md#12-the-second-level-cache)

**41. What is dirty checking?**
Hibernate remembers how a loaded entity looked; at commit it compares and writes an
UPDATE for anything changed. You don't call `save()` for managed entities.
→ [02 Q4](02_Hibernate_QA.md#4-dirty-checking)

**42. Lazy vs eager loading?**
Lazy loads an association only when you use it; eager loads it immediately. Make every
association lazy (to-one is eager by default) and fetch what each use case needs.
→ [03 Q7](03_JPA_Spring_Data_QA.md#7-fetch-types-and-their-defaults)

**43. What is the N+1 problem?**
One query loads N rows, then one more query runs for each row to load an association —
101 queries instead of 1 or 2. Fix with `JOIN FETCH`, `@EntityGraph`, a DTO query or
batch fetching. → [03 Q8](03_JPA_Spring_Data_QA.md#8-the-n1-select-problem)

**44. What causes `LazyInitializationException`?**
Touching a lazy association after the transaction has closed. Load the data inside the
transaction (fetch join or DTO) instead. → [02 Q8](02_Hibernate_QA.md#8-lazyinitializationexception)

**45. `get()`/`find()` vs `load()`/`getReference()`?**
`find` hits the database now and returns `null` if missing. `getReference` returns a
proxy without a query — useful when you only need it as a foreign key.
→ [02 Q6](02_Hibernate_QA.md#6-find-vs-getreference)

**46. `save` vs `persist` vs `merge`?**
`persist` makes a new object managed. `merge` copies a detached object's state into a
managed copy and returns that copy. `save` was Hibernate's own version and is removed
in Hibernate 7. → [02 Q7](02_Hibernate_QA.md#7-save-vs-persist-vs-merge-vs-update)

**47. Flush vs commit?**
Flush sends pending SQL to the database inside the transaction; commit makes it
permanent. Flushed changes can still roll back.
→ [02 Q5](02_Hibernate_QA.md#5-flush-when-it-happens-and-why-the-order-matters)

**48. What is the owning side, and what does `mappedBy` mean?**
The owning side holds the foreign key and is the only side saved to the database.
`mappedBy` marks the other side. Always set both sides in code.
→ [03 Q5](03_JPA_Spring_Data_QA.md#5-relationships-and-the-owning-side)

**49. Cascade vs `orphanRemoval`?**
Cascade passes an operation (persist, remove…) from parent to children. `orphanRemoval`
also deletes a child when it is removed from the parent's collection.
→ [03 Q10](03_JPA_Spring_Data_QA.md#10-cascade-types-and-orphanremoval)

**50. Inheritance mapping strategies?**
`SINGLE_TABLE` (one table, fastest), `JOINED` (table per class, normalised),
`TABLE_PER_CLASS` (avoid), and `@MappedSuperclass` for shared fields only.
→ [03 Q13](03_JPA_Spring_Data_QA.md#13-inheritance-mapping-strategies)

**51. ID generation strategies?**
`IDENTITY` (auto-increment; disables batch inserts), `SEQUENCE` (best for batching),
`TABLE` (avoid), `UUID`. → [02 Q14](02_Hibernate_QA.md#14-id-generators-and-their-effect-on-batching)

**52. JPQL vs native SQL vs Criteria API?**
JPQL queries entities and is portable; native SQL uses database-specific features;
Criteria builds queries in code — good for dynamic filters.
→ [03 Q17](03_JPA_Spring_Data_QA.md#17-jpql-vs-criteria-api-vs-native-sql)

**53. Optimistic vs pessimistic locking?**
Optimistic uses a `@Version` column and fails the update if someone changed the row
first — no locks held. Pessimistic locks the row (`SELECT … FOR UPDATE`) so others wait.
→ [03 Q19](03_JPA_Spring_Data_QA.md#19-optimistic-and-pessimistic-locking)

**54. What is Open Session in View?**
Keeps the database session open for the whole web request so lazy loading works in
controllers. It's on by default in Boot; turn it off — it holds connections and hides
N+1. → [02 Q9](02_Hibernate_QA.md#9-open-session-in-view)

**55. How should you write `equals`/`hashCode` for entities?**
Use a business key, or the id with a constant `hashCode`. Never Lombok `@Data` on
entities. → [02 Q18](02_Hibernate_QA.md#18-equals-hashcode-and-lombok-on-entities)

**56. `JOIN` vs `JOIN FETCH`?**
`JOIN` only filters; `JOIN FETCH` also loads the association in the same query.
→ [03 Q9](03_JPA_Spring_Data_QA.md#9-join-vs-join-fetch-and-paging-with-fetch-joins)

**57. How do you insert many rows quickly?**
Set `hibernate.jdbc.batch_size`, use `SEQUENCE` ids, and flush and clear every N rows.
→ [02 Q15](02_Hibernate_QA.md#15-jdbc-batching)

**58. Why use DTO projections?**
For read-only screens, selecting just the needed columns into a DTO is faster than
loading full entities. → [03 Q18](03_JPA_Spring_Data_QA.md#18-projections-and-dtos)

---

## Spring Data JPA

**59. `CrudRepository` vs `JpaRepository`?**
`CrudRepository` gives basic save, find and delete. `JpaRepository` adds paging,
sorting, `flush`, batch deletes and `getReferenceById`.
→ [03 Q21](03_JPA_Spring_Data_QA.md#21-how-spring-data-repositories-work)

**60. How does a repository work without an implementation?**
At startup Spring creates a proxy for the interface and builds queries from method
names. → [03 Q21](03_JPA_Spring_Data_QA.md#21-how-spring-data-repositories-work)

**61. Derived query methods vs `@Query`?**
`findByStatusAndCustomerId(...)` is parsed from the name — fine for simple queries. Use
`@Query` once the name gets long or the query is complex.
→ [03 Q22](03_JPA_Spring_Data_QA.md#22-derived-queries-vs-query-annotation)

**62. When do you need `@Modifying`?**
On any `@Query` that runs UPDATE or DELETE. It also needs a transaction.
→ [03 Q23](03_JPA_Spring_Data_QA.md#23-modifying-queries)

**63. How does pagination work?**
Pass a `Pageable` (`PageRequest.of(0, 20)`, page numbers start at 0). `Page` also runs
a count query; `Slice` doesn't. → [03 Q25](03_JPA_Spring_Data_QA.md#25-pagination-page-slice-and-keyset-scrolling)

**64. What does `save()` do?**
Calls `persist` for new entities and `merge` for existing ones. If you set ids
yourself, every save does an extra SELECT — implement `Persistable` to avoid it.
→ [03 Q24](03_JPA_Spring_Data_QA.md#24-save-persist-or-merge-and-the-isnew-trap)

**65. How do you record created/updated time and user automatically?**
`@EnableJpaAuditing` plus `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy` on the
entity. → [03 Q20](03_JPA_Spring_Data_QA.md#20-lifecycle-callbacks-and-auditing)

**66. How do you build a search with optional filters?**
Specifications — one small predicate per filter, combined only for the filters the
user sent. → [03 Q26](03_JPA_Spring_Data_QA.md#26-specifications-for-dynamic-filters)

---

## REST and Spring Web

**67. `@Controller` vs `@RestController`?**
`@Controller` returns view names (HTML pages). `@RestController` = `@Controller` +
`@ResponseBody`, so return values are written as JSON.
→ [06 Q2](06_Spring_Boot_Web_QA.md#2-controller-vs-restcontroller)

**68. `@PathVariable` vs `@RequestParam` vs `@RequestBody`?**
Path variable from the URL path (`/orders/{id}`), request param from the query string
(`?status=OPEN`), request body from the JSON body.
→ [06 Q3](06_Spring_Boot_Web_QA.md#3-mapping-requests-and-binding-parameters)

**69. `@RequestBody` vs `@ResponseBody`?** (basic)
`@RequestBody` turns incoming JSON into an object; `@ResponseBody` turns the return
value into the JSON response (`@RestController` applies it to every method).

**70. How do you return the right status code?**
Return `ResponseEntity` — e.g. `ResponseEntity.created(location).body(dto)` for 201,
or throw exceptions mapped to statuses. → [06 Q4](06_Spring_Boot_Web_QA.md#4-responseentity-and-choosing-status-codes)

**71. How do you handle exceptions globally?**
A `@RestControllerAdvice` class with `@ExceptionHandler` methods returning a
`ProblemDetail` error body. → [04 Q17](04_Spring_Boot_QA.md#17-global-exception-handling-and-problemdetail)

**72. How do you validate a request?**
Add the validation starter, put `@NotBlank`, `@Email`… on the DTO and `@Valid` on the
`@RequestBody`. Failures return 400. → [04 Q18](04_Spring_Boot_QA.md#18-validation)

**73. PUT vs PATCH vs POST, and what is idempotent?**
POST creates, PUT replaces the whole resource, PATCH changes part of it. Idempotent
means repeating the call has the same effect — GET, PUT and DELETE are; POST isn't,
so payments use an idempotency key. → [06 Q5](06_Spring_Boot_Web_QA.md#5-put-vs-patch-vs-post-and-idempotency)

**74. How does a request flow through Spring MVC?**
Filters → `DispatcherServlet` → find the controller method → interceptors → convert
JSON to arguments and validate → controller → convert result to JSON → response.
→ [04 Q16](04_Spring_Boot_QA.md#16-how-a-request-flows-through-a-spring-mvc-app)

**75. Filter vs interceptor?**
A filter runs for every request before Spring MVC (auth, CORS, logging). An interceptor
runs inside Spring MVC and knows which controller handles the request.
→ [06 Q10](06_Spring_Boot_Web_QA.md#10-registering-filters-and-interceptors)

**76. What is CORS?**
A browser rule that blocks a page on one domain from calling an API on another unless
the API allows it. Configure allowed origins in Spring (and in Spring Security).
→ [06 Q9](06_Spring_Boot_Web_QA.md#9-cors)

**77. How do you customise JSON output?**
`spring.jackson.*` properties, annotations such as `@JsonProperty` and `@JsonIgnore`,
or a customizer bean — never replace Boot's mapper with `new ObjectMapper()`.
→ [06 Q7](06_Spring_Boot_Web_QA.md#7-customizing-json-with-jackson)

**78. Why does a file upload fail above 1 MB?**
Boot's default limits are 1 MB per file and 10 MB per request; raise
`spring.servlet.multipart.*`, or upload large files straight to S3.
→ [06 Q11](06_Spring_Boot_Web_QA.md#11-file-upload-and-download)

**79. How do you version an API?**
URL path (`/v2/...`), header, media type or query parameter. Spring 7 has it built in:
`@GetMapping(version = "2")`. Version only for breaking changes.
→ [06 Q12](06_Spring_Boot_Web_QA.md#12-api-versioning)

**80. How do you paginate a REST endpoint?**
Accept a `Pageable` (`?page=0&size=20&sort=createdAt,desc`) and return a stable page
DTO, not Spring's `PageImpl`. → [06 Q13](06_Spring_Boot_Web_QA.md#13-pagination-and-sorting-in-rest)

**81. How do you test a controller?**
`@WebMvcTest` with `MockMvc`, mocking the service with `@MockitoBean`.
→ [06 Q17](06_Spring_Boot_Web_QA.md#17-testing-controllers)

**82. 401 vs 403?**
401 = not authenticated ("who are you?"). 403 = authenticated but not allowed.
→ [06 Q4](06_Spring_Boot_Web_QA.md#4-responseentity-and-choosing-status-codes)

---

## Starters and Actuator

**83. What is a starter?**
A single dependency that brings a working set of libraries at compatible versions —
e.g. `spring-boot-starter-web` brings Spring MVC, Tomcat and Jackson.
→ [05 Q1](05_Spring_Boot_Starters_QA.md#1-what-exactly-is-a-starter)

**84. What does `spring-boot-starter-test` include?**
JUnit 5, Mockito, AssertJ, Hamcrest, JsonPath, JSONassert, Awaitility and Spring's test
support. → [05 Q16](05_Spring_Boot_Starters_QA.md#16-test)

**85. How do you turn off one auto-configuration?**
`@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)` or
`spring.autoconfigure.exclude=...`. → [04 Q5](04_Spring_Boot_QA.md#5-overriding-or-excluding-auto-configuration)

**86. How do you write a custom starter?**
An auto-configuration class with conditions and `@ConditionalOnMissingBean`, listed in
`AutoConfiguration.imports`, plus a starter POM that depends on it.
→ [04 Q7](04_Spring_Boot_QA.md#7-writing-a-custom-starter)

**87. What is DevTools?**
Restarts the app automatically when code changes, for development only; it switches
itself off in a packaged jar. → [05 Q19](05_Spring_Boot_Starters_QA.md#19-devtools)

**88. What is Actuator?**
Ready-made production endpoints — health, metrics, info, log levels, thread dumps —
under `/actuator`. → [07 Q1](07_Spring_Boot_Actuator_QA.md#1-what-actuator-is-and-what-you-get)

**89. Which Actuator endpoints matter most?**
`health`, `info`, `metrics`, `prometheus`, `loggers`, `env`, `threaddump`; `heapdump`
and `shutdown` are off by default. → [07 Q2](07_Spring_Boot_Actuator_QA.md#2-the-endpoints)

**90. How do you expose and secure Actuator endpoints?**
Only `health` is exposed by default; add others with
`management.endpoints.web.exposure.include`. Put them on a separate management port
and protect them with Spring Security. → [07 Q4](07_Spring_Boot_Actuator_QA.md#4-securing-actuator)

**91. Liveness vs readiness?**
Liveness: "is the app stuck?" — failing restarts it. Readiness: "can it take traffic?"
— failing just stops traffic to it. Never put the database in liveness.
→ [07 Q6](07_Spring_Boot_Actuator_QA.md#6-liveness-and-readiness-probes)

**92. How do you add a custom health check?**
A bean implementing `HealthIndicator` (or extending `AbstractHealthIndicator`) that
returns UP or DOWN — keep it fast. → [07 Q7](07_Spring_Boot_Actuator_QA.md#7-writing-a-custom-health-indicator)

**93. How do you expose metrics?**
Micrometer records them; add `micrometer-registry-prometheus` and Prometheus scrapes
`/actuator/prometheus`. → [07 Q9](07_Spring_Boot_Actuator_QA.md#9-metrics-with-micrometer)

**94. How do you change a log level without restarting?**
POST to `/actuator/loggers/<package>` with `{"configuredLevel":"DEBUG"}` — on each
instance. → [07 Q13](07_Spring_Boot_Actuator_QA.md#13-changing-log-levels-at-runtime)

**95. How does distributed tracing work?**
Each request gets a trace id that travels in HTTP headers through every service and
appears in the logs; Micrometer Tracing with OpenTelemetry records it. Only 10% of
requests are sampled by default. → [07 Q12](07_Spring_Boot_Actuator_QA.md#12-observations-and-distributed-tracing)

---

## AWS basics

**96. What are IaaS, PaaS and SaaS?** (basic)
IaaS gives you servers and networks to manage yourself (EC2). PaaS runs your code
while the provider manages the servers (Elastic Beanstalk, and serverless such as
Lambda). SaaS is finished software you just use (Gmail, Salesforce).

**97. Region vs Availability Zone vs edge location?**
A region is a geographic area; an AZ is a separate data centre inside it; edge
locations cache content near users for CloudFront. Run production across several AZs.
→ [09 Q1](09_AWS_Compute_IAM_Networking_QA.md#1-regions-availability-zones-and-edge-locations)

**98. What is the shared responsibility model?**
AWS secures the cloud (hardware, data centres, managed-service software); you secure
what you put in it (data, IAM, network rules, your code).
→ [09 Q2](09_AWS_Compute_IAM_Networking_QA.md#2-the-shared-responsibility-model)

**99. IAM user vs group vs role vs policy?**
A user is a long-term identity; a group shares permissions among users; a role gives
temporary credentials to whoever assumes it (apps, services); a policy is the JSON
list of what's allowed. → [09 Q3](09_AWS_Compute_IAM_Networking_QA.md#3-iam-building-blocks)

**100. How should an application on AWS get its permissions?**
Through an IAM role (instance profile, ECS task role, Lambda execution role) — never
access keys in code or config. → [09 Q5](09_AWS_Compute_IAM_Networking_QA.md#5-how-applications-and-pipelines-get-credentials)

**101. If one policy allows and another denies, which wins?**
An explicit deny always wins; without any allow, the default is deny.
→ [09 Q4](09_AWS_Compute_IAM_Networking_QA.md#4-how-aws-evaluates-a-request)

**102. Horizontal vs vertical scaling?** (basic)
Vertical = a bigger machine (limited, often needs downtime). Horizontal = more machines
behind a load balancer (needs stateless apps, scales much further). AWS designs favour
horizontal.

**103. High availability vs fault tolerance vs disaster recovery; RTO and RPO?**
HA survives a failure with a short blip; fault tolerance has no interruption; DR
recovers from a big event such as a region outage. RTO = how long until you're back;
RPO = how much data you can lose.
→ [12 Q2](12_AWS_Architecture_Practices_Scenarios_QA.md#2-high-availability-fault-tolerance-and-disaster-recovery)

---

## AWS compute and networking

**104. How do you choose an EC2 instance type?**
By workload: M general purpose, C compute-heavy, R memory-heavy, T for small bursty
loads; Graviton (`g`) for better price-performance. Right-size from metrics.
→ [09 Q6](09_AWS_Compute_IAM_Networking_QA.md#6-choosing-an-ec2-instance-type)

**105. On-Demand vs Savings Plans vs Reserved vs Spot?**
On-Demand: no commitment. Savings Plans / Reserved: 1–3 year commitment for a big
discount on steady usage. Spot: up to 90% off, but AWS can reclaim it with 2 minutes'
notice. → [09 Q7](09_AWS_Compute_IAM_Networking_QA.md#7-ec2-purchasing-options)

**106. Stop vs terminate vs hibernate?** (basic)
Stop shuts the instance down but keeps its EBS disks (no compute charge; instance-store
data is lost; the public IP changes). Terminate deletes it — and the root disk by
default. Hibernate saves memory to disk so it resumes where it left off.

**107. What is an Elastic IP?** (basic)
A fixed public IPv4 address you own and can move between instances. Every public IPv4
address is billed, so release ones you don't use.

**108. How do you log in to an EC2 instance?**
Classic way: SSH with a key pair. Better: SSM Session Manager — no open port 22, no
keys, every session logged. → [09 Q20](09_AWS_Compute_IAM_Networking_QA.md#20-reaching-a-private-ec2-instance)

**109. What is an AMI?**
The disk image an instance boots from: OS plus pre-installed software. Launch templates
use it to create identical instances. → [09 Q8](09_AWS_Compute_IAM_Networking_QA.md#8-amis-user-data-and-launch-templates)

**110. EBS vs instance store vs EFS vs S3?**
EBS: a disk for one instance in one AZ. Instance store: a temporary disk lost on stop.
EFS: a shared file system for many instances. S3: object storage for files and backups.
→ [09 Q9](09_AWS_Compute_IAM_Networking_QA.md#9-ebs-vs-instance-store-vs-efs-vs-s3)

**111. ALB vs NLB?**
ALB works at HTTP level and routes by path or host — use it for web apps and APIs. NLB
works at TCP/UDP level with static IPs and very high throughput.
→ [09 Q11](09_AWS_Compute_IAM_Networking_QA.md#11-alb-vs-nlb-vs-gwlb)

**112. How does Auto Scaling work?**
An Auto Scaling group keeps between a minimum and maximum number of instances across
AZs, adding or removing them to hold a target metric (e.g. 50% CPU), and replaces
unhealthy ones. → [09 Q12](09_AWS_Compute_IAM_Networking_QA.md#12-auto-scaling-groups-and-scaling-policies)

**113. What makes a subnet public or private?**
A public subnet's route table sends `0.0.0.0/0` to an internet gateway; a private
subnet's doesn't. → [09 Q14](09_AWS_Compute_IAM_Networking_QA.md#14-vpc-basics-subnets-route-tables-gateways)

**114. Internet gateway vs NAT gateway?**
An internet gateway allows two-way internet traffic for public subnets. A NAT gateway
lets private subnets make outbound calls only, without being reachable from outside.
→ [09 Q14](09_AWS_Compute_IAM_Networking_QA.md#14-vpc-basics-subnets-route-tables-gateways)

**115. Security group vs network ACL?**
Security group: on each instance, stateful, allow rules only. NACL: on a subnet,
stateless, allow and deny rules in order. → [09 Q15](09_AWS_Compute_IAM_Networking_QA.md#15-security-groups-vs-network-acls)

**116. VPC peering vs Transit Gateway vs PrivateLink?**
Peering links two VPCs (not transitive). Transit Gateway is a hub for many VPCs.
PrivateLink exposes one service privately without connecting the networks.
→ [09 Q16](09_AWS_Compute_IAM_Networking_QA.md#16-connecting-vpcs-and-on-premises-networks)

**117. What is a VPC endpoint?**
A private connection from your VPC to an AWS service without going through the
internet or NAT. Gateway endpoints (S3, DynamoDB) are free.
→ [09 Q17](09_AWS_Compute_IAM_Networking_QA.md#17-vpc-endpoints)

**118. What Route 53 routing policies are there?**
Simple, weighted, latency, failover, geolocation, geoproximity, multivalue and IP-based.
→ [09 Q18](09_AWS_Compute_IAM_Networking_QA.md#18-route-53-routing-policies)

**119. What is CloudFront?**
AWS's CDN: caches content close to users and protects origins (S3, ALB) — faster pages
and less load on servers. → [09 Q19](09_AWS_Compute_IAM_Networking_QA.md#19-cloudfront)

**120. What is Elastic Beanstalk?** (basic)
You upload your application; Beanstalk creates and manages the EC2 instances, load
balancer and auto scaling for you. Quick to start; less control than ECS or EKS.

---

## AWS storage, databases and messaging

**121. What is S3, and what storage classes does it have?**
Object storage with eleven-nines durability. Classes range from Standard (frequent
access) through Infrequent Access to Glacier (archive); lifecycle rules move data
between them automatically. → [10 Q2](10_AWS_Storage_Databases_Messaging_QA.md#2-s3-storage-classes-and-lifecycle-rules)

**122. How do you secure S3, and what is a pre-signed URL?**
Block Public Access, least-privilege policies, encryption. A pre-signed URL gives
time-limited access to one object — used for direct browser uploads and downloads.
→ [10 Q3](10_AWS_Storage_Databases_Messaging_QA.md#3-securing-s3)

**123. What do S3 versioning and replication do?**
Versioning keeps old versions so deletes and overwrites can be undone. Replication
copies objects to another bucket or region. → [10 Q4](10_AWS_Storage_Databases_Messaging_QA.md#4-versioning-replication-and-object-lock)

**124. RDS vs Aurora?**
Both are managed relational databases. Aurora (MySQL/PostgreSQL-compatible) has faster
failover, storage replicated across 3 AZs and low-lag replicas, usually at a higher
price. → [10 Q6](10_AWS_Storage_Databases_Messaging_QA.md#6-rds-vs-aurora-vs-a-database-on-ec2)

**125. Multi-AZ vs read replica?**
Multi-AZ is for availability: a standby takes over automatically. Read replicas are for
scaling reads and are asynchronous. → [10 Q7](10_AWS_Storage_Databases_Messaging_QA.md#7-multi-az-vs-read-replicas)

**126. How do RDS backups work?**
Automatic daily backups plus logs allow restoring to any second in the last 1–35 days;
a restore creates a new database instance.
→ [10 Q8](10_AWS_Storage_Databases_Messaging_QA.md#8-backups-snapshots-and-point-in-time-recovery)

**127. What is DynamoDB?**
A serverless key-value database with millisecond latency at any scale. Items are found
by partition key (plus an optional sort key); design tables from your access patterns.
→ [10 Q10](10_AWS_Storage_Databases_Messaging_QA.md#10-dynamodb-fundamentals)

**128. DynamoDB or RDS?**
DynamoDB for simple, known access patterns at massive scale; RDS for joins, flexible
queries and transactions. → [10 Q13](10_AWS_Storage_Databases_Messaging_QA.md#13-dynamodb-or-rds)

**129. What is ElastiCache, and how do you use it?**
Managed Redis/Valkey or Memcached. The usual pattern is cache-aside: read the cache,
on a miss read the database and store the result with an expiry.
→ [10 Q14](10_AWS_Storage_Databases_Messaging_QA.md#14-elasticache-and-caching-strategies)

**130. What is SQS's visibility timeout, and what is a DLQ?**
While a consumer works on a message it is hidden for the visibility timeout; if not
deleted in time it reappears. After several failed attempts it moves to a dead-letter
queue. → [10 Q15](10_AWS_Storage_Databases_Messaging_QA.md#15-sqs-essentials)

**131. SQS standard vs FIFO?**
Standard: huge throughput, may deliver twice or out of order. FIFO: exact order per
message group and no duplicates, with lower throughput.
→ [10 Q16](10_AWS_Storage_Databases_Messaging_QA.md#16-sqs-standard-vs-fifo)

**132. SQS vs SNS vs EventBridge vs Kinesis?**
SQS is a queue (one consumer per message); SNS pushes to many subscribers; EventBridge
routes events by content; Kinesis is an ordered stream that can be replayed.
→ [10 Q20](10_AWS_Storage_Databases_Messaging_QA.md#20-choosing-a-messaging-service)

**133. What is the fan-out pattern?**
Publish once to an SNS topic; each consuming service has its own SQS queue subscribed,
so each processes independently. → [10 Q17](10_AWS_Storage_Databases_Messaging_QA.md#17-sns-and-the-fan-out-pattern)

---

## AWS serverless, containers and DevOps

**134. What is Lambda, and what are its main limits?**
Run code without servers, paying per request and duration. Up to 15 minutes per call
and 10 GB memory. → [11 Q1](11_AWS_Serverless_Containers_DevOps_QA.md#1-how-lambda-works-and-its-limits)

**135. What is a cold start, and how do you reduce it?**
The delay when Lambda starts a new environment. Reduce it with smaller packages,
SnapStart (Java), provisioned concurrency or more memory.
→ [11 Q2](11_AWS_Serverless_Containers_DevOps_QA.md#2-cold-starts)

**136. When should you not use Lambda?**
Steady high traffic (containers are cheaper), jobs over 15 minutes, or strict low
latency. → [11 Q5](11_AWS_Serverless_Containers_DevOps_QA.md#5-when-not-to-use-lambda)

**137. What does API Gateway do?**
A managed front door for APIs: routing, authentication, throttling, API keys; commonly
in front of Lambda. → [11 Q6](11_AWS_Serverless_Containers_DevOps_QA.md#6-api-gateway)

**138. ECS vs EKS, and what is Fargate?**
ECS is AWS's simpler container service; EKS is managed Kubernetes. Fargate runs
containers without you managing servers and works with both.
→ [11 Q8](11_AWS_Serverless_Containers_DevOps_QA.md#8-ecs-vs-eks-fargate-vs-ec2)

**139. ECS task role vs task execution role?**
The execution role is used by ECS to pull images and inject secrets; the task role is
what your application code uses to call AWS services.
→ [11 Q9](11_AWS_Serverless_Containers_DevOps_QA.md#9-ecs-building-blocks)

**140. What is Cognito?** (basic)
AWS's user sign-up and sign-in service. User pools handle login and issue JWT tokens;
identity pools give app users temporary AWS credentials.

**141. CloudFormation vs Terraform?**
Both define infrastructure as code. CloudFormation (and CDK) is AWS-only with state
managed by AWS; Terraform works across clouds and keeps its own state file.
→ [11 Q12](11_AWS_Serverless_Containers_DevOps_QA.md#12-infrastructure-as-code)

**142. What does a good CI/CD pipeline look like?**
Build and test → build the image once → scan → deploy to dev, then staging, then
production with canary and automatic rollback; the pipeline uses an IAM role via OIDC,
not keys. → [11 Q13](11_AWS_Serverless_Containers_DevOps_QA.md#13-a-cicd-pipeline-on-aws)

**143. Rolling vs blue/green vs canary deployment?**
Rolling replaces instances gradually; blue/green starts a full new copy and switches
traffic at once; canary sends a small share of traffic first and grows it if healthy.
→ [11 Q14](11_AWS_Serverless_Containers_DevOps_QA.md#14-deployment-strategies)

**144. CloudWatch vs CloudTrail vs Config?**
CloudWatch: how is it performing (metrics, logs, alarms). CloudTrail: who did what (API
calls). Config: how resources are configured and whether that's compliant.
→ [11 Q17](11_AWS_Serverless_Containers_DevOps_QA.md#17-cloudtrail-vs-cloudwatch-vs-config)

**145. Secrets Manager vs Parameter Store?**
Secrets Manager for credentials that need automatic rotation (paid); Parameter Store
for configuration and simple secrets (standard tier free).
→ [11 Q18](11_AWS_Serverless_Containers_DevOps_QA.md#18-secrets-manager-vs-parameter-store)

**146. What is KMS?**
AWS's encryption key service. Data is encrypted with a data key, and KMS encrypts that
data key — "envelope encryption". → [11 Q19](11_AWS_Serverless_Containers_DevOps_QA.md#19-kms-and-envelope-encryption)

---

## AWS architecture and scenarios

**147. What is the Well-Architected Framework?**
AWS's six pillars for judging a design: operational excellence, security, reliability,
performance efficiency, cost optimisation, sustainability.
→ [12 Q1](12_AWS_Architecture_Practices_Scenarios_QA.md#1-the-well-architected-framework)

**148. What are the disaster recovery strategies?**
Backup and restore (cheapest, slowest), pilot light, warm standby, and active/active
(fastest, most expensive). → [12 Q3](12_AWS_Architecture_Practices_Scenarios_QA.md#3-the-four-disaster-recovery-strategies)

**149. How would you design a highly available web app?**
Three AZs; CloudFront and an ALB in front; stateless app servers in an Auto Scaling
group or ECS; a Multi-AZ database; cache; queue for slow work; everything private
except the load balancer. → [12 Q4](12_AWS_Architecture_Practices_Scenarios_QA.md#4-designing-a-highly-available-web-application)

**150. How do you scale an app from one server to millions of users?**
Step by step: separate the database, add a load balancer and more servers, add
replicas and a cache, a CDN, queues — fixing the current bottleneck each time.
→ [12 Q5](12_AWS_Architecture_Practices_Scenarios_QA.md#5-scaling-from-one-server-to-millions-of-users)

**151. What security practices do most companies follow on AWS?**
No access keys (roles and SSO), least privilege, MFA on root, private subnets,
encryption everywhere, CloudTrail and GuardDuty turned on.
→ [12 Q7](12_AWS_Architecture_Practices_Scenarios_QA.md#7-security-practices-most-companies-follow)

**152. How do you reduce AWS costs?**
Make costs visible with tags and budgets, right-size, use Savings Plans for the
baseline and Spot for interruptible work, stop non-production at night, apply S3
lifecycle rules, avoid NAT data charges.
→ [12 Q8](12_AWS_Architecture_Practices_Scenarios_QA.md#8-cost-optimisation-practices)

**153. Serverless or containers?**
Lambda for spiky, event-driven, short tasks; containers for steady traffic,
long-running services and heavy frameworks. Most companies use both.
→ [12 Q10](12_AWS_Architecture_Practices_Scenarios_QA.md#10-serverless-or-containers)

**154. How do you migrate an application to AWS?**
The 7 Rs — usually rehost (lift and shift) first, then replatform or refactor the parts
that benefit. → [12 Q12](12_AWS_Architecture_Practices_Scenarios_QA.md#12-migrating-an-application-to-aws)

**155. I can't reach my EC2 instance — what do you check?**
Instance running, app listening, security group, network ACL, route table, public IP,
then the load balancer's health checks — layer by layer.
→ [09 Q22](09_AWS_Compute_IAM_Networking_QA.md#22-scenario-i-cant-reach-my-ec2-instance)

**156. The site is slow under load — what do you do?**
Find which layer is saturated (load balancer, app, database, cache, a downstream
service) using metrics and traces, relieve it short-term, then fix the cause.
→ [12 Q13](12_AWS_Architecture_Practices_Scenarios_QA.md#13-scenario-the-site-is-slow-under-load)

**157. Access keys leaked on GitHub — what do you do?**
Deactivate the key immediately, check CloudTrail for what it was used for, delete
anything the attacker created, then remove long-lived keys altogether.
→ [12 Q18](12_AWS_Architecture_Practices_Scenarios_QA.md#18-scenario-access-keys-leaked-on-github)

**158. An Availability Zone goes down — what happens?**
With a multi-AZ design, the load balancer and auto scaling move to healthy AZs and the
database fails over; single-AZ resources and a single NAT gateway are what break.
→ [12 Q17](12_AWS_Architecture_Practices_Scenarios_QA.md#17-scenario-an-availability-zone-goes-down)
