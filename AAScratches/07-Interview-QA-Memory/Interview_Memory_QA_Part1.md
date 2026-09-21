# Interview Memory Q&A — Part 1

Numbered, mixed-topic memory notes (Java 21 switch, Streams collectors, Spring Boot
config, Spring Security, Spring Data JPA, GraphQL, MockMvc testing) kept in the
original order and with the original item numbers.

Several snippets here were recorded from memory straight after a round and did not
compile as written. Those carry a **Correction** line saying what was wrong, so the
distinction sticks rather than just the corrected code. Items marked *(new)* are
additions where the original recorded a question with no answer, or an answer that
stopped short of what the follow-up question would be.

## How to use this file

| # | Topic | The one thing to remember |
| --- | --- | --- |
| 1 | Diamond default methods | `A.super.m1()` names the interface. |
| 2 | Logging levels | `logging.level.<package>=DEBUG`. |
| 3 | Activating a profile | Property, CLI arg, or `SPRING_PROFILES_ACTIVE`. |
| 4 | `@Valid` vs `@Validated` | `@Validated` on the **class** enables param validation; they throw different exceptions. |
| 5 | `@RequiredArgsConstructor` | Goes on the **class**, never on a field. |
| 4b | Pattern-matching switch | `when` guards; `null` throws unless you write `case null`. |
| 5b | `Collectors.maxBy` | Gives `Map<K, Optional<V>>`. |
| 6 | `collectingAndThen` | Unwraps that `Optional` inside the grouping. |
| 7 | `Collectors.mapping` | Group to a list of one field. |
| 8 | Circuit breaker, JWT chain, `@EntityGraph` | Two `List` fetches throw `MultipleBagFetchException`. |
| 9 | GraphQL mappings | One method maps to one field — never both annotations. |
| 10 | `JpaSpecificationExecutor` | It already declares the paged `findAll`. |
| 11 | `PageRequest` | Page index is **0-based**. |
| 12 | Building a `Specification` | The lambda must **return a `Predicate`**. |
| 13 | `@ControllerAdvice` | `ResponseEntity.of(Optional.of(x))` returns **200**, not 404. |
| 14 | Controller parameters | `@RequestBody` on a `@GetMapping` does not work. |
| 15 | MockMvc test | `@MockBean` is deprecated — use `@MockitoBean`. |
| 16 | `@EntityGraph` / `JOIN FETCH` | Collection fetch + `Pageable` paginates **in memory**. |
| 17 | Bidirectional mapping | Keep both sides in sync with helper methods. |
| 18 | `@JdbcTypeCode(SqlTypes.JSON)` | Hibernate 6 native jsonb mapping. |
| 19 | *(was empty)* | Now holds the follow-ups these rounds actually asked. |

## Table of Contents

- [1. Calling a specific interface's default method when two interfaces define it](#1-calling-a-specific-interfaces-default-method-when-two-interfaces-define-it)
- [2. Logging levels in application.properties](#2-logging-levels-in-applicationproperties)
- [3. Activating a Spring profile](#3-activating-a-spring-profile)
- [4. @Valid vs @Validated](#4-valid-vs-validated)
- [5. Constructor injection with Lombok @RequiredArgsConstructor](#5-constructor-injection-with-lombok-requiredargsconstructor)
- [4. Pattern-matching switch with a when guard](#4-pattern-matching-switch-with-a-when-guard)
- [5. Group employees by department and pick the highest-paid (Collectors.maxBy)](#5-group-employees-by-department-and-pick-the-highest-paid-collectorsmaxby)
- [6. Group by department and map to the highest-paid employee's name (collectingAndThen)](#6-group-by-department-and-map-to-the-highest-paid-employees-name-collectingandthen)
- [7. Group by department to a list of employee names (Collectors.mapping)](#7-group-by-department-to-a-list-of-employee-names-collectorsmapping)
- [8. Resilience4j circuit breaker, stateless JWT SecurityFilterChain and @EntityGraph for N+1](#8-resilience4j-circuit-breaker-stateless-jwt-securityfilterchain-and-entitygraph-for-n1)
- [9. GraphQL @QueryMapping / @MutationMapping with @Argument](#9-graphql-querymapping--mutationmapping-with-argument)
- [10. JpaRepository with JpaSpecificationExecutor and a paged findAll](#10-jparepository-with-jpaspecificationexecutor-and-a-paged-findall)
- [11. PageRequest with Sort](#11-pagerequest-with-sort)
- [12. Building a JPA Specification with predicates and a LEFT join](#12-building-a-jpa-specification-with-predicates-and-a-left-join)
- [13. @ControllerAdvice global exception handler and orElseThrow in the service](#13-controlleradvice-global-exception-handler-and-orelsethrow-in-the-service)
- [14. Controller method with @RequestBody, @PathVariable and @RequestParam](#14-controller-method-with-requestbody-pathvariable-and-requestparam)
- [15. MockMvc integration test with custom headers, params and a JSON body](#15-mockmvc-integration-test-with-custom-headers-params-and-a-json-body)
- [16. Fetching child collections with @EntityGraph and JOIN FETCH](#16-fetching-child-collections-with-entitygraph-and-join-fetch)
- [17. Bidirectional @OneToMany / @ManyToOne mapping](#17-bidirectional-onetomany--manytoone-mapping)
- [18. Entity with @Enumerated(EnumType.STRING) and a jsonb column via @JdbcTypeCode](#18-entity-with-enumeratedenumtypestring-and-a-jsonb-column-via-jdbctypecode)
- [19. Follow-up questions these rounds asked (new)](#19-follow-up-questions-these-rounds-asked-new)

## 1. Calling a specific interface's default method when two interfaces define it

Default method in two interfaces A, B and class C implements both A, B — how to
make a call?

```java
interface A { default void m1() { System.out.println("A"); } }
interface B { default void m1() { System.out.println("B"); } }

class C implements A, B {
    @Override
    public void m1() {
        A.super.m1();   // pick A's version explicitly
    }
}
```

The compiler **forces** the override. If `C` does not override `m1()`, the code
fails to compile with "inherits unrelated defaults for m1() from types A and B" —
Java does not pick a winner for you. `A.super.m1()` is the only syntax that names
the interface, and it is legal only inside a class that directly implements `A`.

Two related rules the follow-up usually goes to:

| Conflict | Who wins |
| --- | --- |
| Class method vs interface default | **Class wins.** A concrete superclass method always beats a default. |
| Sub-interface vs super-interface default | **Most specific interface wins**, no error. |
| Two unrelated interfaces | **Compile error** — you must override. |

## 2. Logging levels in application.properties

Shorthand noted in the source: `llr`

```properties
# Root logging level
logging.level.root=INFO
# Package-specific logging level
logging.level.com.example.myapp=DEBUG
# Specific class logging level
logging.level.com.example.myapp.service.UserService=TRACE
```

The most specific prefix wins, so the three lines above coexist. Levels, least to
most verbose: `OFF < ERROR < WARN < INFO < DEBUG < TRACE < ALL`. Setting a level
enables that level **and everything less verbose**.

## 3. Activating a Spring profile

```properties
# application.properties
spring.profiles.active=dev
```

**Correction —** the original recorded only the bare property name with no value
and no alternatives. In practice you almost never hardcode it in
`application.properties`, because the whole point is that it varies per
environment. The ways that matter, in increasing order of precedence:

| Method | Syntax | Typical use |
| --- | --- | --- |
| Property file | `spring.profiles.active=dev` | A local default. |
| Environment variable | `SPRING_PROFILES_ACTIVE=prod` | Containers and Kubernetes. |
| JVM system property | `-Dspring.profiles.active=prod` | Running a jar. |
| Command-line argument | `--spring.profiles.active=prod` | Overrides all of the above. |
| Test annotation | `@ActiveProfiles("test")` | Integration tests. |

Three points that come up as follow-ups:

- Profile-specific files are `application-{profile}.properties` (or `.yml`) and are
  layered **on top of** plain `application.properties`, not instead of it.
- You can activate several at once: `--spring.profiles.active=prod,metrics`.
- Since Spring Boot 2.4, `spring.profiles.active` **cannot be set inside a
  profile-specific document** — the framework rejects it, because a profile turning
  on another profile made the resolution order unpredictable. Use
  `spring.profiles.group.prod=metrics,audit` instead.

## 4. @Valid vs @Validated

**Correction — the original answer was wrong in both halves.** It said `@Valid` is
"for Model or DTO validation" and `@Validated` is "to validate attribute path, URL
path or query params". The second half describes a *consequence* of one specific
use of `@Validated`, not what the annotation is, and the split implied they are two
flavours of the same thing. They come from different places and do different jobs.

| | `@Valid` | `@Validated` |
| --- | --- | --- |
| Comes from | Jakarta Bean Validation (`jakarta.validation`) — a JSR standard | Spring (`org.springframework.validation.annotation`) |
| Validation groups | **No** | **Yes** — `@Validated(OnCreate.class)` |
| Cascades into nested objects | **Yes** — put it on a field to validate the object inside | **No** — it is not a cascade annotation and cannot go on a field |
| On a class | Not applicable | Enables **method-level validation** via an AOP proxy |
| Exception on failure | `MethodArgumentNotValidException` → 400 | `ConstraintViolationException` → 500 unless you handle it |

**`@Valid` on a request body** — validates the DTO and cascades into its fields:

```java
@PostMapping("/movies")
public ResponseEntity<MovieDTO> create(@Valid @RequestBody MovieDTO movie) { ... }

public class MovieDTO {
    @NotBlank private String title;

    @Valid                       // cascade: without this, Director is NOT validated
    private DirectorDTO director;
}
```

**`@Validated` on the class** — the only way to make constraints on `@RequestParam`
and `@PathVariable` fire at all:

```java
@RestController
@Validated                       // on the CLASS, not the parameter
public class MovieController {

    @GetMapping("/movies")
    public List<MovieDTO> search(@RequestParam @NotBlank String heroName,
                                 @RequestParam @Min(1) int page) { ... }
}
```

Without the class-level `@Validated`, `@NotBlank` and `@Min` on those parameters are
**silently ignored** — no proxy is created, so nothing checks them. That silence is
what makes this a good interview question.

**`@Validated` for groups** — the same DTO validated differently on create and
update:

```java
public interface OnCreate {}
public interface OnUpdate {}

public class MovieDTO {
    @Null(groups = OnCreate.class)      // must be absent when creating
    @NotNull(groups = OnUpdate.class)   // must be present when updating
    private UUID id;
}

@PostMapping public void create(@Validated(OnCreate.class) @RequestBody MovieDTO m) {}
@PutMapping  public void update(@Validated(OnUpdate.class) @RequestBody MovieDTO m) {}
```

**The practical takeaway:** use `@Valid` on request bodies and nested fields, and
`@Validated` on the class when you need parameter validation or groups. Because the
two throw different exceptions, a `@ControllerAdvice` that only handles
`MethodArgumentNotValidException` will let `ConstraintViolationException` through as
a 500 — handle both (see section 13).

## 5. Constructor injection with Lombok @RequiredArgsConstructor

**Correction — the original put the annotation on the field. It belongs on the
class.** As recorded it would not compile: `@RequiredArgsConstructor` has
`@Target(ElementType.TYPE)`.

```java
// WRONG - as originally recorded
// @RequiredArgsConstructor
// private final EmployeeRepository employeeRepository;

// RIGHT
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    // Lombok generates:
    // public EmployeeService(EmployeeRepository r, DepartmentRepository d) { ... }
}
```

`@RequiredArgsConstructor` generates a constructor taking every `final` field and
every `@NonNull` non-final field, in declaration order. Since Spring 4.3 a class
with exactly one constructor needs no `@Autowired`, so this is the whole wiring.

Why constructor injection over `@Autowired` on fields:

| Benefit | Why |
| --- | --- |
| Fields can be `final` | Genuinely immutable dependencies; no accidental reassignment. |
| Fails fast | A missing bean is a startup error, not a `NullPointerException` in production. |
| Testable without Spring | `new EmployeeService(mockRepo, mockDeptRepo)` — no reflection, no container. |
| Exposes bloat | A constructor with nine arguments is visibly wrong; nine `@Autowired` fields hide it. |

One catch worth knowing: constructor injection turns a **circular dependency** into
a startup failure rather than silently resolving it. That is a feature — but if you
hit it, the fix is to break the cycle, not to switch back to field injection.

## 4. Pattern-matching switch with a when guard

*(Item number 4 is used a second time in the source; kept as recorded.)*

```java
static String check(Object o) {
    return switch (o) {
        case String s            -> "string";
        case Integer i when i > 10 -> "big int";  // 'when', not '&&'
        default                  -> "other";
    };
}
```

Correct as recorded, and the `when` note is the right thing to flag — a guard uses
the contextual keyword `when`, not `&&` or `if`. Pattern matching for `switch` was
finalised in **Java 21** (JEP 441), after three preview rounds in 17–20, so on
Java 17 this needs `--enable-preview`.

Four rules the follow-up goes to:

- **`null` throws unless you handle it.** A classic `switch` throws
  `NullPointerException` on a null selector, and a pattern switch keeps that
  behaviour — `default` does **not** catch null. You must write `case null ->` (or
  `case null, default ->`) to handle it. This is the most commonly missed detail.
- **Order matters, and the compiler enforces it.** A guarded case must come *before*
  the unguarded case for the same type, and a broader pattern before a narrower one
  is a compile error ("this case label is dominated by a preceding case label").
  Here there is no unguarded `case Integer`, so `check(5)` falls through to
  `default` and returns `"other"` — worth tracing out loud, because it looks like it
  should return `"big int"`.
- **Exhaustiveness is required** for a switch *expression* (one that returns a
  value). With `Object` as the selector, only `default` can make it exhaustive. With
  a `sealed` interface you can drop `default` and let the compiler verify you
  covered every permitted subtype — which then fails the build if someone adds one.
- **Record patterns** (also final in Java 21, JEP 440) destructure in the same
  position: `case Point(int x, int y) when x == y -> "diagonal";`.

## 5. Group employees by department and pick the highest-paid (Collectors.maxBy)

*(Item number 5 is used a second time in the source; kept as recorded.)*

```java
Map<String, Optional<Employee>> result = emplList.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
        ));
```

The recorded snippet was correct but left the result type off, and that type is the
whole point of the follow-up question: **the value is `Optional<Employee>`, not
`Employee`**, because `maxBy` has to describe an empty input.

Inside `groupingBy`, that `Optional` can never actually be empty — a group only
exists because at least one element landed in it. So the `Optional` is pure noise,
and section 6 is how you get rid of it.

## 6. Group by department and map to the highest-paid employee's name (collectingAndThen)

```java
Map<String, String> result = emplList.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.collectingAndThen(
            Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
            optEmp -> optEmp.map(Employee::getName).orElse(null)
        )
    ));
```

Correct as recorded. `collectingAndThen` wraps a collector with a finisher that runs
once per group, which is what unwraps the `Optional` from section 5.

Two refinements worth saying out loud:

- Because the group is never empty, `.orElseThrow()` is a better finisher than
  `.orElse(null)` — it turns "impossible" into a loud failure instead of a `null`
  sitting in a map, which is a `NullPointerException` waiting to happen at the call
  site. Use `orElse(null)` only if you have a real reason.
- `Collectors.toMap` with a merge function does the same job in one step and avoids
  the `Optional` entirely:

```java
Map<String, String> result = emplList.stream()
    .collect(Collectors.toMap(
        Employee::getDepartment,
        Employee::getName,
        (a, b) -> a));           // merge on duplicate key
```

…but note that `toMap` throws `NullPointerException` if a *value* is null, whereas
`groupingBy` tolerates it. That asymmetry is a favourite trick question.

## 7. Group by department to a list of employee names (Collectors.mapping)

```java
Map<String, List<String>> result = emplList.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
```

Correct as recorded. `mapping` transforms each element **before** it reaches the
downstream collector — that is how you get `List<String>` rather than
`List<Employee>` without a second pass.

The sibling collectors share the same shape and are worth memorising as a set:

| Downstream | Produces |
| --- | --- |
| `Collectors.toList()` | `Map<K, List<V>>` — the default. |
| `Collectors.toSet()` | `Map<K, Set<V>>` — deduplicated. |
| `Collectors.counting()` | `Map<K, Long>`. |
| `Collectors.summingDouble(...)` | `Map<K, Double>`. |
| `Collectors.joining(", ")` | `Map<K, String>`. |
| `Collectors.partitioningBy(pred)` | `Map<Boolean, List<V>>` — always both keys, even when one list is empty. |

One gotcha: `groupingBy` throws `NullPointerException` if the **classifier** returns
null — so an employee with a null department blows up the whole stream. Guard with
`Optional.ofNullable(e.getDepartment()).orElse("UNASSIGNED")` if that is possible.

## 8. Resilience4j circuit breaker, stateless JWT SecurityFilterChain and @EntityGraph for N+1

```java
// --- Circuit breaker (Resilience4j) ---
@CircuitBreaker(name = "catalog", fallbackMethod = "fallback")
public Product getProduct(String id) { ... }

// The fallback must take the SAME parameters PLUS a Throwable, and return
// the same type. A mismatch fails at runtime, not compile time.
public Product fallback(String id, Throwable t) {
    return Product.unavailable(id);
}
```

```java
// --- SecurityFilterChain (stateless JWT, Spring Security 6) ---
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

```java
// --- EntityGraph to kill N+1 ---
@EntityGraph(attributePaths = {"items", "customer"})
List<Order> findByStatus(Status status);
```

All three were recorded correctly. The notes each one needs:

- **Circuit breaker:** the states are `CLOSED` → `OPEN` → `HALF_OPEN`. It opens when
  the failure rate over a sliding window exceeds the threshold, waits, then lets a
  few trial calls through in `HALF_OPEN`. The `@CircuitBreaker` annotation is an AOP
  proxy, so calling `getProduct` from **inside the same class** bypasses it entirely
  — the same self-invocation trap as `@Transactional`.
- **`csrf.disable()`** is correct **only** because this is a stateless token API
  with no cookie-based session. On a browser app with session cookies, disabling
  CSRF is a real vulnerability, and interviewers ask precisely to see whether you
  know which case you are in.
- **`hasRole("ADMIN")`** checks for the authority `ROLE_ADMIN` — the prefix is added
  for you. `hasAuthority("ADMIN")` does not add it. Mixing these up is the usual
  cause of "my role check always fails".
- **`@EntityGraph` with two collections throws.** Here `items` is a collection and
  `customer` is a `@ManyToOne`, so this is fine. If both were `List` collections,
  Hibernate throws `MultipleBagFetchException: cannot simultaneously fetch multiple
  bags`. The fixes are to make one a `Set`, or to fetch the second collection in a
  separate query. See section 16 for the pagination half of this problem.

## 9. GraphQL @QueryMapping / @MutationMapping with @Argument

**Correction — the recorded snippet had two syntax errors and one design error.**
It was missing the closing `)` on `@Argument("supplierId"` and the closing `)` of
the parameter list, and it stacked `@MutationMapping` and `@QueryMapping` on the
same method. A method maps to exactly **one** schema field; a query and a mutation
are different fields in different root types, so they need different methods.

```java
@Controller
public class ProductGraphQLController {

    // maps to:  type Query { product(supplierId: ID!): ProductResponse }
    @QueryMapping
    public GqlProductResponse product(@Argument("supplierId") UUID supplierId) {
        return productService.findBySupplier(supplierId);
    }

    // maps to:  type Mutation { updateProduct(input: ProductInput!): ProductResponse }
    @MutationMapping
    public GqlProductResponse updateProduct(@Argument("input") GqlProductInput input) {
        return productService.update(input);
    }
}
```

Points worth volunteering:

- The **method name is the schema field name** by default. `@QueryMapping("product")`
  lets you override it when the Java name differs.
- `@Argument` matches the schema argument name. If the parameter name survives
  compilation (`-parameters`, which Spring Boot's Maven plugin enables), the explicit
  string is optional — but stating it is safer and self-documenting.
- The class needs `@Controller`, not `@RestController`. There is no HTTP response
  body being written; the GraphQL engine handles serialisation.
- The N+1 problem is worse in GraphQL than in REST, because the client chooses the
  shape. `@SchemaMapping` plus a `DataLoader` (batch loading) is the standard answer
  to "how do you stop a nested field firing one query per parent?"

## 10. JpaRepository with JpaSpecificationExecutor and a paged findAll

```java
public interface SupplierSearchRepository
        extends JpaRepository<SupplierSearch, UUID>,
                JpaSpecificationExecutor<SupplierSearch> {
}
```

**Correction — the recorded version redeclared a method it already inherits, and
added an annotation it does not need.**

- `Page<SupplierSearch> findAll(Specification<SupplierSearch> spec, Pageable pageable)`
  is **already declared by `JpaSpecificationExecutor`**. Repeating it is harmless
  but signals not knowing what the interface gives you.
- `@Repository` is **not required** on a Spring Data repository interface. Spring
  Data registers the proxy and applies exception translation regardless. It is
  needed only on your own hand-written `@Component` DAO classes.

What `JpaSpecificationExecutor` actually adds:

| Method | Returns |
| --- | --- |
| `findAll(Specification)` | `List<T>` |
| `findAll(Specification, Pageable)` | `Page<T>` |
| `findAll(Specification, Sort)` | `List<T>` |
| `findOne(Specification)` | `Optional<T>` |
| `count(Specification)` | `long` |
| `exists(Specification)` | `boolean` |

Use it when the filter combination is decided at runtime — a search screen where any
subset of ten fields may be filled in. If the queries are fixed, derived query
methods or `@Query` are simpler and easier to read.

## 11. PageRequest with Sort

**Correction — the recorded snippet was missing a closing parenthesis and the
semicolon, so it did not compile.**

```java
PageRequest pageRequest = PageRequest.of(
        pageNumber,                 // 0-BASED: page 0 is the first page
        pageSize,
        Sort.by(Sort.Direction.ASC, request.getSortedBy()));
```

The things that actually go wrong with this in production:

- **`pageNumber` is 0-based.** If the UI sends a 1-based page number and you pass it
  straight through, every response is shifted by one page and page 1's data is never
  seen. Convert at the boundary.
- **`request.getSortedBy()` is an entity property name, not a column name.** Passing
  a user-supplied string here throws `PropertyReferenceException` for anything that
  is not a real property — so validate it against an allow-list rather than trusting
  the client.
- **Sorting must be deterministic** or pages overlap. If `sortedBy` is a non-unique
  column, append a unique tiebreaker:

```java
Sort sort = Sort.by(Sort.Direction.ASC, request.getSortedBy())
                .and(Sort.by(Sort.Direction.ASC, "id"));
PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
```

`Page` runs a second `COUNT` query to populate `getTotalElements()`. If you do not
need the total, return `Slice` instead and skip that count — a real saving on large
tables.

## 12. Building a JPA Specification with predicates and a LEFT join

**Correction — the recorded snippet had four errors and would not compile.**

| Recorded | Problem |
| --- | --- |
| `CriteriaQuery<?> query1 = query.distinct(true);` | `query` is undefined — the lambda parameter is named `criterQuery`. |
| `root.join(suppliesOffers, LEFT)` | `suppliesOffers` is an undefined identifier; the attribute name needs quotes. |
| `LEFT` | Unqualified; it is `JoinType.LEFT`. |
| lambda body | Never **returns a `Predicate`**, which is the whole contract of `Specification`. |

```java
public static Specification<SupplierSearch> buildSupplierSpecification(
        RequestFilter filter) {

    return (root, query, cb) -> {

        // distinct is needed because the LEFT JOIN to a collection
        // multiplies parent rows
        query.distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")),
                                   "%" + filter.getName().toLowerCase() + "%"));
        }

        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        if (filter.getOfferCode() != null) {
            Join<SupplierSearch, SupplierOfferSearch> offers =
                    root.join("supplierOffers", JoinType.LEFT);
            predicates.add(cb.equal(offers.get("code"), filter.getOfferCode()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}
```

Notes that earn marks:

- **`cb.and()` on an empty array returns a conjunction that is always true**, so an
  empty filter correctly returns everything. You do not need a null guard.
- **Join inside the `if`, not outside.** An unconditional `LEFT JOIN` on a
  collection forces a join and a `DISTINCT` on every single search, even when nobody
  filtered on offers.
- **`query.distinct(true)` breaks pagination counts.** When this spec runs through
  `findAll(spec, pageable)`, Spring Data reuses it for the `COUNT` query, where a
  `JOIN` plus `distinct` is wasteful and can produce a wrong total. Guard it with
  `if (Long.class != query.getResultType())` to skip the join work on the count pass.
- Compose small specs with `Specification.where(a).and(b).or(c)` rather than growing
  one giant lambda — that is the real reason to use the Criteria API over `@Query`.

## 13. @ControllerAdvice global exception handler and orElseThrow in the service

**Correction — the recorded handler had two bugs, and the second one means it
returned the wrong HTTP status.**

```java
// WRONG - as originally recorded
// @ExceptionHandler(exception = MovieNotFoundException.class)
// public ResponseEntity<?> movieNotFound(MovieNotFoundException ex) {
//     return ResponseEntity.of(Optional.of(ex.getMessage()));
// }
```

| Bug | What happens |
| --- | --- |
| `@ExceptionHandler(exception = ...)` | **Does not compile.** The annotation has no `exception` attribute — it is `value` (plus `produces` in Spring 6.2), so the type goes in unnamed. |
| `ResponseEntity.of(Optional.of(msg))` | **Returns 200 OK**, not 404. `ResponseEntity.of` returns 200 when the `Optional` is *present* and 404 only when it is *empty*. Wrapping a message in `Optional.of` guarantees the present branch — so the "not found" handler reports success. |

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse movieNotFound(MovieNotFoundException ex,
                                       HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                 ex.getMessage(),
                                 request.getRequestURI());
    }

    // equivalent, returning ResponseEntity explicitly
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> movieNotFoundAlt(MovieNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ErrorResponse(404, ex.getMessage(), null));
    }
}
```

```java
// logic in the service
Movie movie = movieRepository.findById(uuid)
        .orElseThrow(() -> new MovieNotFoundException("movie not found with " + uuid));
```

Three things to add:

- `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`. With plain
  `@ControllerAdvice` a method returning a non-`ResponseEntity` object is treated as
  a **view name**, which is almost never what you want in an API.
- `ResponseEntity.of(Optional)` genuinely is useful — but in the *controller*, for
  the "found or 404" pattern: `return ResponseEntity.of(movieRepository.findById(id));`.
  That is where the empty branch can actually occur.
- Handler methods are matched **most-specific-first**, so a
  `@ExceptionHandler(Exception.class)` catch-all coexists with specific handlers.
  Make sure the catch-all logs the throwable and does **not** put
  `ex.getMessage()` in the response body — that leaks SQL fragments and internal
  paths to the caller.

Remember from section 4: `@Valid` failures throw `MethodArgumentNotValidException`
while class-level `@Validated` failures throw `ConstraintViolationException`. A
handler for only the first lets the second escape as a 500.

## 14. Controller method with @RequestBody, @PathVariable and @RequestParam

**Correction — the recorded method does not work as written.**

```java
// WRONG - as originally recorded
// @GetMapping(path = "/{id}")
// public ResponseEntity<List<MovieDTO>> getMovies(@RequestBody MovieDTO movie,
//                                                 @PathVariable UUID id,
//                                                 @RequestParam String heroName) {
//     return ResponseEntity.ok(movieService.getMovies());
// }
```

| Bug | What happens |
| --- | --- |
| `@RequestBody` on a `@GetMapping` | `@RequestBody` is `required = true` by default, so a normal GET — which carries no body — fails with **400 Bad Request**. Many clients, proxies and caches strip a GET body outright. |
| `id` and `heroName` are bound but never used | The method returns every movie regardless of the path variable and the filter. Silently ignoring input is worse than rejecting it. |

Use POST when you need a body, and GET with parameters when you do not:

```java
// GET with path variable + query parameter, no body
@GetMapping("/movies/{id}")
public ResponseEntity<MovieDTO> getMovie(
        @PathVariable UUID id,
        @RequestParam(required = false) String heroName) {
    return ResponseEntity.ok(movieService.getMovie(id, heroName));
}

// POST when a request body is genuinely needed
@PostMapping("/movies/search")
public ResponseEntity<List<MovieDTO>> search(
        @Valid @RequestBody MovieSearchDTO criteria) {
    return ResponseEntity.ok(movieService.search(criteria));
}
```

The binding annotations, side by side:

| Annotation | Reads from | Required by default |
| --- | --- | --- |
| `@PathVariable` | A `{placeholder}` in the URL path | Yes |
| `@RequestParam` | Query string or form body | **Yes** — a missing param is a 400 |
| `@RequestBody` | The HTTP body, deserialised by Jackson | Yes |
| `@RequestHeader` | A header | Yes |

`@RequestParam` being required by default surprises people. For an optional filter
use `@RequestParam(required = false)`, a `defaultValue`, or `Optional<String>`.

## 15. MockMvc integration test with custom headers, params and a JSON body

```java
package com.real.interview.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.real.interview.dto.MovieDTO;
import com.real.interview.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean                 // replaces @MockBean, deprecated in Boot 3.4
    private MovieService movieService;

    @Test
    void testGetMovies_WithHeaders_ReturnsMovieListSuccessfully() throws Exception {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String heroName = "Superman";

        MovieDTO movie1 = new MovieDTO();
        movie1.setTitle("Man of Steel");

        MovieDTO movie2 = new MovieDTO();
        movie2.setTitle("Batman v Superman");

        List<MovieDTO> mockResponse = List.of(movie1, movie2);
        Mockito.when(movieService.getMovies()).thenReturn(mockResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer dummy-jwt-token");
        headers.add("X-Request-Id", UUID.randomUUID().toString());
        headers.add("Accept-Language", "en-US");

        // Act
        MvcResult result = mockMvc.perform(get("/movies/{id}", movieId)
                        .headers(headers)
                        .param("heroName", heroName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String jsonResponse = result.getResponse().getContentAsString();
        List<MovieDTO> responseList = objectMapper.readValue(
                jsonResponse, new TypeReference<List<MovieDTO>>() {});

        assertThat(responseList)
                .isNotNull()
                .hasSize(2)
                .extracting(MovieDTO::getTitle)
                .containsExactly("Man of Steel", "Batman v Superman");

        Mockito.verify(movieService, Mockito.times(1)).getMovies();
    }
}
```

**Corrections applied to the recorded test:**

| Recorded | Corrected | Why |
| --- | --- | --- |
| `@MockBean` | `@MockitoBean` | `@MockBean` is **deprecated since Spring Boot 3.4** in favour of `org.springframework.test.context.bean.override.mockito.MockitoBean`. Reciting the deprecated one dates you. |
| `@SpringBootTest` + `@AutoConfigureMockMvc` | `@WebMvcTest(MovieController.class)` | `@SpringBootTest` starts the **whole** context — every repository, the datasource, the JPA layer. A slice test loads only the web layer and is seconds faster. Use `@SpringBootTest` when you genuinely want the full wiring. |
| `.content(...)` on a `get(...)` | removed | Matches the section 14 fix: a GET carries no body. |
| `new TypeReference<>() {}` | `new TypeReference<List<MovieDTO>>() {}` | Jackson reads the generic type **reflectively at runtime**. The explicit form is guaranteed to carry `List<MovieDTO>`; the diamond form depends on the compiler emitting the inferred signature. Spell it out. |

Two more points about the test itself:

- It never asserts anything about the headers or `heroName` — because the controller
  ignored them. A test that passes regardless of its inputs is not testing them. Add
  `Mockito.verify(movieService).getMovie(movieId, heroName)` once the controller
  actually uses them.
- `containsExactly` asserts order as well as content. Use
  `containsExactlyInAnyOrder` if the endpoint makes no ordering guarantee, otherwise
  the test breaks the day someone adds a sort.

## 16. Fetching child collections with @EntityGraph and JOIN FETCH

```java
// 1. Entity graph on a derived query
@EntityGraph(attributePaths = {"employees"})
@Query("SELECT d FROM Department d WHERE d.id = :id")
Optional<Department> findWithEmployees(@Param("id") Long id);

// 2. Explicit JOIN FETCH in JPQL
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
Optional<Department> findDepartmentWithEmployees(@Param("id") Long id);

// 3. Overriding an inherited method to add a graph
@EntityGraph(attributePaths = {"employees"})
List<Department> findAll();
```

All three were recorded correctly. Both solve N+1: without them, loading 100
departments and touching `getEmployees()` on each fires 1 + 100 queries.

| Approach | Choose it when |
| --- | --- |
| `@EntityGraph` | Declarative; reuses an existing derived method; easy to apply to `findAll`. |
| `JOIN FETCH` | You need the join in a query you are writing anyway, or need `WHERE` conditions on the joined side. |

Four caveats, in the order they bite:

- **Pagination silently breaks.** Combining a collection fetch with `Pageable` makes
  Hibernate log `HHH000104: firstResult/maxResults specified with collection fetch;
  applying in memory` — it loads **every** matching row and paginates in Java. On a
  large table that is an out-of-memory incident, not a slow query. Fix it with a
  two-query approach: page the IDs first, then fetch the collection for those IDs.
- **Two collections throw.** Fetching two `List` associations in one query raises
  `MultipleBagFetchException`. Change one to a `Set`, or split into two queries.
- **`DISTINCT` is no longer needed for entity queries in Hibernate 6.** A join fetch
  multiplies rows in SQL, and Hibernate 5 needed `SELECT DISTINCT d` to collapse the
  duplicate `Department` objects. Hibernate 6 (Spring Boot 3) de-duplicates entity
  results automatically. If you are on Boot 2 / Hibernate 5, you still need it.
- **`@EntityGraph` only widens fetching, never narrows it.** An association mapped
  `FetchType.EAGER` is still loaded even if you leave it out of `attributePaths`,
  unless you use `type = EntityGraphType.FETCH`. Map associations `LAZY` by default
  and opt in per query.

## 17. Bidirectional @OneToMany / @ManyToOne mapping

```java
@Entity
public class Department {

    @OneToMany(mappedBy = "department",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    // keep both sides in sync - the owning side is what actually gets persisted
    public void addEmployee(Employee e) {
        employees.add(e);
        e.setDepartment(this);
    }

    public void removeEmployee(Employee e) {
        employees.remove(e);
        e.setDepartment(null);
    }
}

@Entity
public class Employee {

    // the MANY side owns the relationship - the FK column lives here
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```

Correct as recorded. The helper methods are the addition, and they matter: JPA
persists the relationship from the **owning** side only. `mappedBy = "department"`
declares `Department.employees` to be the *inverse* side, so adding to that list
without also calling `e.setDepartment(this)` writes nothing to the database — the
row appears in memory and vanishes on reload.

| Setting | Effect |
| --- | --- |
| `mappedBy` | Marks the inverse side. The other side owns the FK. |
| `cascade = ALL` | Persist/merge/remove on the parent propagates to children. |
| `orphanRemoval = true` | Removing a child from the list **deletes the row**. Different from `CascadeType.REMOVE`, which only fires when the *parent* is deleted. |
| `fetch = LAZY` | The default for `@OneToMany`, but **`@ManyToOne` defaults to `EAGER`** — so set it explicitly, as above. |

Two more that come up:

- `@ManyToOne` being eager by default is a leading cause of accidental N+1 and of
  loading half the object graph for one lookup. Make every association lazy and opt
  in with `@EntityGraph`.
- Always implement `equals`/`hashCode` on entities from a **business key**, not from
  the generated `id` — a not-yet-persisted entity has a null id, so id-based hashing
  changes after the flush and breaks any `Set` or `Map` holding it.

## 18. Entity with @Enumerated(EnumType.STRING) and a jsonb column via @JdbcTypeCode

```java
@Entity
class ApprovalRequestEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private RequestType type;

    @Column(name = "edit_params", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private GqlSupplierEdit editParams;
}
```

Correct as recorded, for Hibernate 6 with PostgreSQL. The two points behind it:

- **`EnumType.STRING` vs `EnumType.ORDINAL`.** `ORDINAL` is the JPA default and it
  stores the enum's **position**, so inserting a new constant in the middle of the
  enum silently re-maps every existing row. Always write `EnumType.STRING`. The cost
  is a wider column and the need for a migration when a constant is renamed.
- **`@JdbcTypeCode(SqlTypes.JSON)`** is Hibernate 6's built-in JSON mapping — it
  replaces the third-party `hibernate-types` library (`@Type(JsonBinaryType.class)`)
  that Hibernate 5 projects needed. The object is serialised with Jackson.

The trade-off to state unprompted: a `jsonb` column is schema-less, so the database
cannot enforce anything about its contents and you cannot join on it. Query it with
PostgreSQL's JSON operators and add a GIN index if you filter on it often. Use it
for genuinely variable payloads, not to avoid designing a table.

## 19. Follow-up questions these rounds asked (new)

*The original item 19 was an empty heading with no question and no answer. Rather
than leave the gap, these are the follow-ups that the topics above lead into — they
are new content, not recovered from the source.*

**Q. How do you make `@Transactional` work on a method called from within the same
class?**

You cannot, as written. `@Transactional` is applied by an AOP proxy, and a
`this.method()` call never leaves the object, so the proxy is not involved and no
transaction starts. The same applies to `@Cacheable`, `@Async` and
`@CircuitBreaker`. Fixes, best first: move the method to a separate bean and inject
it; or self-inject the proxy; or use `TransactionTemplate` explicitly. Also note
`@Transactional` is ignored on `private`, `final` and `static` methods.

**Q. What is the difference between `JpaRepository`, `CrudRepository` and
`PagingAndSortingRepository`?**

`CrudRepository` is the base (save, find, delete). `PagingAndSortingRepository`
adds `findAll(Pageable)` and `findAll(Sort)`. `JpaRepository` extends both and adds
JPA-specific methods — `flush()`, `saveAndFlush()`, `deleteAllInBatch()`, and
`List` instead of `Iterable` return types. Use `JpaRepository` unless you are
deliberately keeping the persistence technology swappable.

**Q. `save()` vs `saveAndFlush()`?**

`save()` puts the entity in the persistence context; the SQL runs when the context
flushes, normally at transaction commit. `saveAndFlush()` forces the SQL
immediately. You need the second one when subsequent code in the same transaction
reads through native SQL, or when you want a constraint violation to surface at that
line rather than at commit.

**Q. What does `@Transactional(readOnly = true)` buy you?**

It sets the JDBC connection read-only, which lets the database skip some locking,
and it tells Hibernate to skip dirty checking and the flush — a measurable saving on
a query that loads many entities. Put it on read-only service methods as a default.

**Q. How do you handle a `LazyInitializationException`?**

It means a lazy association was touched after the persistence context closed —
almost always in the controller or during serialisation. Fix it by loading what you
need inside the transaction (`@EntityGraph` or `JOIN FETCH`, section 16) and mapping
to a DTO before returning. Do **not** fix it with `spring.jpa.open-in-view=true`,
which is on by default and is exactly the setting that hides N+1 problems until
production.
