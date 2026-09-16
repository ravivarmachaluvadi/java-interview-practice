# Interview Memory Q&A — Part 2

Numbered, mixed-topic memory notes (Spring AOP proxies, JUnit 5 parameterized tests, the 12-factor principles, deployment strategies, and a full Spring Security + JWT + load-balancer walkthrough) kept in the original order and with the original item numbers.

## 1. How Spring AOP proxies work

- If target class implements an interface → JDK Dynamic Proxy used.
- Else → CGLIB Proxy (creates subclass at runtime).
- Proxy intercepts method calls → applies advice (before, after, around).

## 2. JUnit 5 @ParameterizedTest (ValueSource, CsvSource, MethodSource)

**Example 1 — `@ValueSource`**

```java
public class MathUtils {
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }
}
```

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MathUtilsTest {

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 8, 10})
    void testIsEven_ShouldReturnTrueForEvenNumbers(int number) {
        assertTrue(MathUtils.isEven(number));
    }
}
```

**Example 2 — `@CsvSource`**

```java
public class DiscountService {
    public double calculateDiscount(double price, double discountPercent) {
        return price - (price * discountPercent / 100);
    }
}
```

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscountServiceTest {

    DiscountService service = new DiscountService();

    @ParameterizedTest
    @CsvSource({
        "100, 10, 90",
        "200, 25, 150",
        "50, 0, 50",
        "120, 50, 60"
    })
    void testCalculateDiscount(double price, double discountPercent, double expected) {
        double actual = service.calculateDiscount(price, discountPercent);
        assertEquals(expected, actual, 0.01);
    }
}
```

**Example 3 — `@MethodSource`**

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class StringUtilsTest {

    static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
            Arguments.of("   ", true),
            Arguments.of("", true),
            Arguments.of("abc", false),
            Arguments.of(null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void testIsBlank(String input, boolean expected) {
        boolean actual = input == null || input.trim().isEmpty();
        Assertions.assertEquals(expected, actual);
    }
}
```

**Summary**

| Feature | Benefit |
| --- | --- |
| `@ParameterizedTest` | Run the same test with multiple inputs |
| `@ValueSource` | Simple input values |
| `@CsvSource` | Multiple arguments inline |
| `@SpringBootTest` | Works seamlessly in Spring context |
| `@MethodSource` | Dynamic or complex inputs |

## 3. Explain the 12-factor principles

Mnemonic: **CDCBBPPCDDLA**

Codebase, Dependencies, Config, Backing Services, Build, Release, Run, Processes, Port Binding, Concurrency, Disposability, Dev/Prod Parity, Logs, Admin Processes.

### 1. Codebase — One codebase tracked in revision control, many deploys

- There should be one codebase per application, tracked in a version control system (like Git).
- Different environments (dev, staging, prod) are different deployments of the same codebase — not separate copies.
- **Example:** All environments use the same Git repo, but different configurations.

### 2. Dependencies — Explicitly declare and isolate dependencies

- Don't assume the system has dependencies installed.
- Always declare all dependencies in a dependency file (like `pom.xml` for Maven, `requirements.txt` for Python, or `package.json` for Node).
- Use dependency isolation tools like virtual environments or containers.
- **Example:** In Java, use Maven or Gradle to manage libraries, so builds are reproducible anywhere.

### 3. Config — Store config in the environment

- Configuration (like DB credentials, API keys) should not be hardcoded in the codebase.
- Store them in environment variables instead.
- **Example:** Use `DATABASE_URL` as an environment variable instead of writing credentials in `application.properties`.

### 4. Backing Services — Treat backing services as attached resources

- Any service your app uses (database, message queue, cache, etc.) should be treated as a replaceable resource.
- Your code shouldn't depend on where the service runs.
- **Example:** If your PostgreSQL DB is replaced by another hosted DB, only the connection URL changes, not your code.

### 5. Build, Release, Run — Strictly separate build and run stages

- **Build:** Convert code to an executable (e.g., jar, docker image).
- **Release:** Combine build + config.
- **Run:** Execute the app in the target environment.
- Keep these stages isolated for consistency.
- **Example:** You should never modify code or configs in production directly.

### 6. Processes — Execute the app as one or more stateless processes

- Your app should be stateless — no session or data stored in memory between requests.
- Store data in external systems (like DB or Redis).
- **Example:** A user session should be stored in Redis, not in local memory of a single instance.

### 7. Port Binding — Export services via port binding

- The app should be self-contained and expose its service via a port.
- Don't rely on external web servers (like Apache or Nginx).
- **Example:** A Spring Boot app exposes itself via port 8080 using an embedded Tomcat server.

### 8. Concurrency — Scale out via the process model

- Scale your app by running multiple stateless instances, not by running one large instance.
- Each process handles part of the load.
- **Example:** Run multiple instances of your app behind a load balancer instead of increasing CPU/RAM for one instance.

### 9. Disposability — Maximize robustness with fast startup and graceful shutdown

- Apps should start up fast and shut down gracefully.
- This helps with scaling and quick recovery from failures.
- **Example:** On shutdown, a Kafka consumer should commit offsets and exit cleanly, not abruptly.

### 10. Dev/Prod Parity — Keep development, staging, and production as similar as possible

- Avoid environment drift.
- Make environments consistent in terms of:
  - Dependencies
  - Configuration
  - Database versions
  - Deployment automation
- **Example:** Use Docker to ensure the same environment setup across dev, staging, and prod.

### 11. Logs — Treat logs as event streams

- The app should not manage log files.
- Instead, write logs to stdout/stderr and let the execution environment collect and manage them.
- **Example:** Kubernetes or AWS CloudWatch aggregates logs automatically from app output streams.

### 12. Admin Processes — Run admin/management tasks as one-off processes

- Run admin tasks (like migrations, data fixes, or scripts) as one-off processes using the same environment and codebase.
- **Example:** Run `java -jar app.jar --migrate` for DB migration in the same environment as production.

## 4. Explain different deployment strategies

| Strategy | Description | When to Use | Pros | Cons |
| --- | --- | --- | --- | --- |
| 1. Recreate Deployment | Shut down old version completely before starting new. | Dev/test or small apps where downtime is okay. | Simple, no version overlap. | Causes downtime. |
| 2. Rolling Deployment | Gradually replace old instances with new ones. | High-availability production systems. | No downtime, gradual rollout. | Rollback is slower. |
| 3. Blue-Green Deployment | Two environments (Blue=current, Green=new). Switch traffic when ready. | Zero-downtime, instant rollback required. | Zero downtime, fast rollback. | Double infrastructure cost. |
| 4. Canary Deployment | Release to a small subset of users first, then expand if stable. | Large-scale systems with risk-sensitive changes. | Safe, gradual rollout. | Needs routing and monitoring setup. |
| 5. Shadow Deployment | New version gets real traffic but doesn't affect users. | Performance testing under real load. | No user impact. | Complex and resource-heavy. |
| 6. A/B Testing Deployment | Multiple versions (A, B) for user testing and analytics. | UX experiments, data-driven feature validation. | Controlled comparison. | Needs analytics setup. |
| 7. Feature Toggles / Flags | Deploy code but enable/disable features dynamically. | Continuous delivery, fast rollback, experimentation. | No redeploy needed, fine control. | Adds complexity, must clean up old flags. |
| 8. Partitioned / Regional Rollouts | Deploy region-by-region or service-by-service. | Distributed systems and microservice rollouts. | Controlled, isolated risk. | Requires orchestration. |

## Q. How Spring Security works in real time with a load balancer, and how it helps scalability

Explain Spring Security: how it works in real time with an example and flow, with a load balancer, and how it helps to achieve scalability.

This is one of the most common system design + backend deep-dive topics asked in senior-level interviews (like Atlassian, Netflix, or PayPal). Let's break this down step-by-step, from Spring Security fundamentals → real-time flow → load balancer integration → scalability and statelessness.

### 1. What is Spring Security?

Spring Security is a framework that provides authentication, authorization, and protection against common attacks (CSRF, session fixation, etc.) for Spring-based applications.

It's built on a filter chain model — where each request passes through a series of filters before hitting your controller.

### 2. How Spring Security works (core flow)

Let's go through the authentication flow step-by-step.

**Example: User logs in to a microservice-based web app.** Let's say your app is `https://bookmyshow.com/login` (behind a load balancer).

#### Step 1. User sends login request

```http
POST /login
Content-Type: application/json

{
  "username": "ravi",
  "password": "password123"
}
```

#### Step 2. Security filter chain intercepts

Spring Security uses a chain of filters such as:

- `SecurityContextPersistenceFilter`
- `UsernamePasswordAuthenticationFilter`
- `ExceptionTranslationFilter`
- `FilterSecurityInterceptor`

`UsernamePasswordAuthenticationFilter` captures the credentials and hands them off to the `AuthenticationManager`.

#### Step 3. AuthenticationManager → AuthenticationProvider

`AuthenticationManager` delegates authentication to one or more `AuthenticationProvider`s.

Example:

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(myUserDetailsService)
        .passwordEncoder(passwordEncoder());
}
```

Here:

- `myUserDetailsService` loads user data (e.g., from DB)
- `passwordEncoder` validates password securely (e.g., BCrypt)

#### Step 4. On success

- A valid `Authentication` object is created.
- Stored in the `SecurityContext`.
- Optionally persisted in session (stateful) or JWT (stateless).
- Response is sent with a `Set-Cookie` (session ID) or `Authorization: Bearer <token>` header.

### 3. Stateless authentication with JWT (for scalability)

In microservice or load-balanced environments, session replication is costly — so we make the app stateless using JWTs.

**Flow with JWT:**

#### Step 1. Login

- User logs in → Spring Security authenticates credentials.
- Server generates JWT signed using private key/secret:

```json
{
  "sub": "ravi",
  "roles": ["USER"],
  "iat": 1734768000,
  "exp": 1734771600
}
```

#### Step 2. Client stores the JWT

Client stores the JWT (in browser localStorage or mobile app).

#### Step 3. For each request

Client sends JWT in header:

```http
GET /profile
Authorization: Bearer <jwt-token>
```

#### Step 4. On server side

- A `JwtAuthenticationFilter` extracts and validates token.
- Builds `Authentication` from JWT claims.
- Sets into `SecurityContext`.
- Controller executes only if authorized.

Result:

- No session.
- Any server instance can validate the token.
- Works beautifully with load balancers and horizontal scaling.

### 4. Real-time architecture with load balancer

Here's a high-level setup diagram:

```text
          ┌──────────────────────────────┐
          │         Client (App)         │
          └──────────────┬───────────────┘
                         │
                   HTTPS Requests
                         │
              ┌──────────┴──────────┐
              │     Load Balancer    │
              └──────────┬──────────┘
            ┌────────────┼────────────┐
            │             │            │
   ┌────────▼──────┐ ┌────▼────────┐ ┌─▼───────────┐
   │ Spring App #1 │ │ Spring App#2│ │ Spring App#3│
   │ (Stateless)   │ │ (Stateless) │ │ (Stateless) │
   └───────────────┘ └─────────────┘ └────────────┘
            │             │            │
            └─────────────┼────────────┘
                         ▼
                 PostgreSQL / Redis
```

Key points:

- Load Balancer (NGINX / AWS ELB) routes traffic.
- Each Spring Boot instance has its own Spring Security FilterChain.
- All servers share the same secret key for JWT signature verification.
- No sticky sessions required.
- Scaling horizontally is easy — just add more instances.

### 5. Example JWT-based Spring Security config

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/login", "/auth/register").permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

`JwtAuthenticationFilter`:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired private JwtService jwtService;
    @Autowired private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
        FilterChain chain)
            throws IOException, ServletException {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(req, res);
    }
}
```

### 6. How Spring Security helps scalability

| Feature | Stateful | Stateless (JWT) |
| --- | --- | --- |
| Session Storage | Server-side (per node) | None |
| Load Balancer | Needs sticky sessions | Any node can serve |
| Scale-out | Difficult | Easy (stateless microservices) |
| Security Context | Stored in session | Built per request |
| Performance | Slightly slower (session lookup) | Fast (token decode only) |

### 7. Real-world integrations

| Use Case | How Spring Security Fits |
| --- | --- |
| API Gateway | Validate JWT before forwarding to backend |
| Microservices | Each service validates token independently |
| OAuth2 Login | Integrate Google, GitHub, or SSO provider |
| Role-based Access | Use `@PreAuthorize("hasRole('ADMIN')")` |
| CSRF Protection | Enabled for web forms, disabled for APIs |

### 8. Summary — real-time flow with load balancer

```text
[Client] → [Load Balancer] → [Spring Boot Instance (Security FilterChain)]
          → [JwtAuthenticationFilter validates token]
          → [Controller executes if authorized]
          → [Response returned to Client]
```

- **Scalability:** Achieved by keeping services stateless (JWT-based).
- **Security:** Managed by filter chain and token validation.
- **Flexibility:** Works with multiple instances behind LB without sticky sessions.

---

The source file ends with an empty `Q:` placeholder; no further question was recorded.
