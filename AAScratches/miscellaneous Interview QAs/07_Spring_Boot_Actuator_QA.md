# Spring Boot Actuator — Interview Q&A

**What Actuator is:** `spring-boot-starter-actuator` adds production endpoints to an
application — health checks, metrics, build info, live log levels, thread and heap
dumps — plus Micrometer for metrics and tracing. Interviewers use it to test whether
you have actually run a service in production: Kubernetes probes, securing endpoints,
metrics that don't explode, and diagnosing a live incident.

**Version note:** every default quoted below was checked in the **Spring Boot 4.1.1**
jars in the local Maven repository (`spring-boot-actuator`,
`spring-boot-actuator-autoconfigure`, `spring-boot-health`,
`spring-boot-micrometer-*`). The Boot 3 actuator jars are not in the local
repository, so Boot 3 differences are marked "not checked".

Related: [04 Q23](04_Spring_Boot_QA.md#23-actuator-and-health-probes) (short version) ·
[06 Q15](06_Spring_Boot_Web_QA.md#15-tomcat-limits-and-what-happens-under-overload)
(overload) · [08 Q12](08_Spring_Cloud_AWS_QA.md#12-observability-on-aws) (CloudWatch)

## Weight legend

| Mark | What it means | How the answer is written |
| --- | --- | --- |
| ★★★ | Asked in almost every Java backend round, with follow-ups | Answer, mechanism, code, traps |
| ★★ | Asked often | Answer and one example or table |
| ★ | Occasional, or a senior-level bonus | Two to four lines |

## Contents

| # | Question | Weight | One-line answer |
| --- | --- | --- | --- |
| 1 | [What you get](#1-what-actuator-is-and-what-you-get) | ★★ | Endpoints + Micrometer, under `/actuator` |
| 2 | [The endpoints](#2-the-endpoints) | ★★★ | 20+ endpoints; know which leak secrets |
| 3 | [Exposure vs access](#3-exposure-vs-access) | ★★★ | Reachable vs allowed; only `health` exposed by default |
| 4 | [Securing Actuator](#4-securing-actuator) | ★★★ | Separate port + Spring Security + masked values |
| 5 | [How health works](#5-how-the-health-endpoint-works) | ★★★ | Worst status wins; DOWN → HTTP 503 |
| 6 | [Liveness and readiness](#6-liveness-and-readiness-probes) | ★★★ | Liveness restarts, readiness removes from traffic |
| 7 | [Custom health indicator](#7-writing-a-custom-health-indicator) | ★★ | Fast, cached, never in liveness |
| 8 | [Info endpoint](#8-the-info-endpoint) | ★ | Build and git info: "what version is running?" |
| 9 | [Micrometer metrics](#9-metrics-with-micrometer) | ★★★ | SLF4J for metrics; tags make them dimensional |
| 10 | [Custom metrics, cardinality](#10-custom-metrics-and-the-cardinality-trap) | ★★★ | Never tag with ids |
| 11 | [Percentiles and SLOs](#11-percentiles-histograms-and-slos) | ★★ | Histograms aggregate across pods; percentiles don't |
| 12 | [Observations and tracing](#12-observations-and-distributed-tracing) | ★★★ | Only 10% of traces sampled by default |
| 13 | [Runtime log levels](#13-changing-log-levels-at-runtime) | ★★ | Per instance only |
| 14 | [Diagnosing incidents](#14-diagnosing-production-problems-with-actuator) | ★★★ | Symptom → endpoint → what to look for |
| 15 | [Custom endpoint](#15-writing-a-custom-endpoint) | ★★ | `@Endpoint` + `@ReadOperation` / `@WriteOperation` |
| 16 | [httpexchanges, auditevents, startup](#16-httpexchanges-auditevents-and-startup) | ★ | Each needs a bean before it shows anything |
| 17 | [SBOM endpoint](#17-the-sbom-endpoint) | ★ | "Are we affected by this CVE?" |
| 18 | [Production configuration](#18-a-production-configuration) | ★★ | One YAML block to adapt |
| 19 | [Overhead and pitfalls](#19-actuator-overhead-and-pitfalls) | ★★ | Slow health checks restart pods |

---

## 1. What Actuator is and what you get

**Weight:** ★★

| Area | What it gives you |
| --- | --- |
| Health | `/actuator/health`, plus Kubernetes liveness and readiness groups |
| Metrics | Micrometer meters for HTTP, JVM, DB pool, executors; export to Prometheus, OTLP, CloudWatch… |
| Tracing | Micrometer Tracing with OpenTelemetry or Brave |
| Diagnostics | Thread dump, heap dump, loggers, environment, beans, conditions, mappings |
| Info | Build version, git commit |

All HTTP endpoints sit under `/actuator` (`management.endpoints.web.base-path`, checked)
and are also available over JMX.

---

## 2. The endpoints

**Weight:** ★★★

Endpoint classes found in the 4.1.1 jars:

| Endpoint id | Shows | Can leak secrets? | Needs |
| --- | --- | --- | --- |
| `health` | Up/down status and components | No (details hidden by default) | — |
| `info` | Build, git, custom info | Rarely | Build-info / git files (Q8) |
| `metrics` | List and values of meters | No | — |
| `prometheus` | All metrics in Prometheus text format | No | `micrometer-registry-prometheus` |
| `loggers` | Log levels; change them live | No | — |
| `env` | Every property and where it came from | **Yes** (values masked by default) | — |
| `configprops` | `@ConfigurationProperties` values | **Yes** (values masked by default) | — |
| `beans` | Every bean and its dependencies | No | — |
| `conditions` | Which auto-configurations matched and why | No | — |
| `mappings` | Every URL mapping | Reveals the API surface | — |
| `threaddump` | Stack of every thread | Rarely | — |
| `heapdump` | A full heap dump file | **Yes** — everything in memory | Off by default in 4.1.1 |
| `scheduledtasks` | `@Scheduled` jobs | No | — |
| `caches` | Cache managers and caches | No | — |
| `flyway` | Applied migrations | No | Flyway |
| `httpexchanges` | Recent requests and responses | Headers may contain tokens | An `HttpExchangeRepository` bean (Q16) |
| `auditevents` | Security audit events | Usernames | An `AuditEventRepository` bean |
| `startup` | Startup steps with durations | No | `BufferingApplicationStartup` (Q16) |
| `logfile` | The log file | Possibly | `logging.file.name` or `.path` |
| `sbom` | Software bill of materials | No | Q17 |
| `shutdown` | Shuts the app down (POST) | — | Off by default |

Other endpoints appear when their technology is present (Liquibase, Quartz, Spring
Session and others).

---

## 3. Exposure vs access

**Weight:** ★★★

Two separate switches:

- **Exposure** — can the endpoint be *reached* over HTTP (or JMX)?
- **Access** — are its operations *allowed* at all? Values: `NONE`, `READ_ONLY`,
  `UNRESTRICTED` (enum checked in 4.1.1). Boot 3.4 introduced `access`, replacing the
  older `management.endpoint.<id>.enabled`.

**Defaults (checked in 4.1.1):**

| Setting | Default |
| --- | --- |
| `management.endpoints.web.exposure.include` | `health` only |
| `management.endpoints.jmx.exposure.include` | `health` only |
| `management.endpoint.shutdown.access` | `none` |
| `management.endpoint.heapdump.access` | `none` |
| `management.endpoint.health.access`, `…env.access` | `unrestricted` |

**Example:**

```properties
management.endpoints.web.exposure.include=health,info,prometheus,loggers
# nothing may change state...
management.endpoints.access.default=read-only
# ...except log levels
management.endpoint.loggers.access=unrestricted
```

**Trap:** `exposure.include=*` in production exposes `env`, `heapdump` (if access
allows it), `threaddump` and `mappings` to anyone who can reach the port.

---

## 4. Securing Actuator

**Weight:** ★★★

**Layer 1 — a separate port** that the load balancer or ingress never routes:

```properties
management.server.port=8081
```

Kubernetes probes and Prometheus reach the pod directly on 8081; the internet only
reaches 8080. Actuator then runs on its own embedded server with its own threads, so
it still answers when the main pool is exhausted (but see Q6 for why that can hide
problems).

**Layer 2 — Spring Security** for everything except health:

```java
@Bean
@Order(1)
SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
    http.securityMatcher(EndpointRequest.toAnyEndpoint())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
            .anyRequest().hasRole("OPS"))
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

**Layer 3 — masked values.** `env` and `configprops` show `******` for every value by
default (`show-values=never`, checked). Set `when-authorized` to reveal values only to
authenticated users with the configured roles.

**Never expose `heapdump` publicly.** A heap dump contains everything in memory:
database passwords, tokens, customer data.

---

## 5. How the health endpoint works

**Weight:** ★★★

**Mechanism:** health is built from **health contributors** — one per dependency Boot
finds on the classpath (`db`, `diskSpace`, `ping`, `redis`, `mongo`, …) plus your own.
Each returns a status; the endpoint aggregates them.

**Aggregation — the worst status wins**, in this order: `DOWN`, `OUT_OF_SERVICE`, `UP`,
`UNKNOWN`.

| Aggregate status | HTTP status |
| --- | --- |
| `UP`, `UNKNOWN` | 200 |
| `DOWN`, `OUT_OF_SERVICE` | **503** |

Load balancers and Kubernetes read the HTTP status, not the JSON.

**Details:** `management.endpoint.health.show-details` defaults to `never` (checked), so
the public response is just `{"status":"UP"}`. With `when-authorized` or `always`:

```json
{
  "status": "DOWN",
  "components": {
    "db":        { "status": "UP", "details": { "database": "PostgreSQL" } },
    "diskSpace": { "status": "UP" },
    "redis":     { "status": "DOWN", "details": { "error": "Connection refused" } }
  }
}
```

**Controls:**

- Turn one indicator off: `management.health.redis.enabled=false` (the `db` one is
  `management.health.db.enabled`, checked).
- Custom groups: `management.endpoint.health.group.critical.include=db,redis` →
  `/actuator/health/critical`.

**Trap:** a non-critical dependency (a recommendations cache) going down makes the
whole health `DOWN` → 503 → the load balancer pulls every instance. Leave
non-critical dependencies out of the health groups that drive routing.

---

## 6. Liveness and readiness probes

**Weight:** ★★★

| | Liveness | Readiness |
| --- | --- | --- |
| Question | Is the process broken beyond repair? | Can it serve traffic right now? |
| Failing means | Kubernetes **kills and restarts** the pod | Pod **removed** from the Service; no restart |
| URL | `/actuator/health/liveness` | `/actuator/health/readiness` |
| Default contents | `livenessState` | `readinessState` |
| Include the database? | **Never** | Only if the app is useless without it |

**Why liveness must not check the database:** if the database has a short outage,
every pod fails liveness at once and Kubernetes restarts them all. The restarts don't
fix the database, and now the whole service is cold-starting together — a small outage
becomes a big one.

**Where the states come from:** Boot tracks `LivenessState` (`CORRECT` / `BROKEN`) and
`ReadinessState` (`ACCEPTING_TRAFFIC` / `REFUSING_TRAFFIC`). Readiness becomes
`ACCEPTING_TRAFFIC` after startup runners finish, and `REFUSING_TRAFFIC` when shutdown
begins. Your code can change them:

```java
// e.g. after detecting an unrecoverable state such as a corrupted local cache
AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
```

**Enabling:** in 4.1.1, `management.endpoint.health.probes.enabled` is `true` by default
(checked). In Boot 3 the probe groups were switched on automatically only on
Kubernetes (not checked here).

**Adding a dependency to readiness:**

```properties
management.endpoint.health.group.readiness.include=readinessState,db
```

**Kubernetes:**

```yaml
startupProbe:                         # gives slow starts time before liveness applies
  httpGet: { path: /actuator/health/liveness, port: 8081 }
  periodSeconds: 5
  failureThreshold: 30                # up to 150 s to start
livenessProbe:
  httpGet: { path: /actuator/health/liveness, port: 8081 }
  periodSeconds: 10
  failureThreshold: 3
readinessProbe:
  httpGet: { path: /actuator/health/readiness, port: 8081 }
  periodSeconds: 5
  failureThreshold: 2
```

**Senior detail — separate management port:** with probes on port 8081, the main port
8080 can be completely stuck (all 200 threads blocked) while liveness still answers
UP. `management.endpoint.health.probes.add-additional-paths=true` (default `false`,
checked) also serves `/livez` and `/readyz` on the **main** port, so the probe tests
the server that real traffic uses.

---

## 7. Writing a custom health indicator

**Weight:** ★★

```java
@Component
class PaymentGatewayHealthIndicator extends AbstractHealthIndicator {

    private final PaymentGatewayClient gateway;

    PaymentGatewayHealthIndicator(PaymentGatewayClient gateway) {
        this.gateway = gateway;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        if (gateway.ping(Duration.ofMillis(500))) {
            builder.up();
        } else {
            builder.down().withDetail("gateway", "no response within 500 ms");
        }
    }
}
```

- The bean name minus `HealthIndicator` becomes the component name: `paymentGateway`.
- **Package:** Boot 3 — `org.springframework.boot.actuate.health`; Boot 4 —
  `org.springframework.boot.health.contributor` (checked in `spring-boot-health`
  4.1.1).

**Rules:**

- **Fast, with a timeout.** Probes run every few seconds on every pod. A check that
  takes 10 s makes the probe time out.
- **Cache** expensive checks for a short time, and refresh in the background.
- **Never** put it in the liveness group (Q6).
- `AbstractHealthIndicator` turns an exception into `DOWN` with the error as a detail.

---

## 8. The info endpoint

**Weight:** ★

- Answers "which version is running in production?" in one call.
- **Build info:** the `build-info` goal of `spring-boot-maven-plugin` writes
  `META-INF/build-info.properties` (artifact, version, build time).
- **Git info:** `git-commit-id-maven-plugin` writes `git.properties`; Actuator shows the
  branch and commit (`management.info.git.mode=simple` by default, checked).
- **Defaults (checked):** `build` and `git` contributors on; `env`, `java`, `os`,
  `process` off. With `management.info.env.enabled=true`, any `info.*` property is
  shown — e.g. `info.app.team=payments`.

---

## 9. Metrics with Micrometer

**Weight:** ★★★

**What Micrometer is:** a facade for metrics, the way SLF4J is for logging. You record
through one API; a *registry* exports to the monitoring system you use.

| Meter | Measures | Example |
| --- | --- | --- |
| `Counter` | Something that only goes up | Orders placed |
| `Gauge` | A current value read on demand | Queue size, cache entries |
| `Timer` | Count + total time + max (and optional histogram) | Payment call duration |
| `DistributionSummary` | Distribution of non-time values | Order amount, payload size |
| `LongTaskTimer` | Tasks still running and for how long | A nightly batch job in progress |

**Dimensional:** a meter has **tags**. One meter `http.server.requests` with tags
`method`, `uri`, `status`, `outcome` and `exception` can be sliced any way at query
time.

**Built-in meters you should name in an interview:**

| Meter | Tells you |
| --- | --- |
| `http.server.requests` | Request rate, errors, latency per endpoint |
| `jvm.memory.used`, `jvm.gc.pause` | Heap pressure, GC pauses |
| `jvm.threads.live`, `jvm.threads.states` | Thread leaks, blocked threads |
| `hikaricp.connections.active` / `.pending` | DB pool saturation — pending > 0 means waiting |
| `executor.active`, `executor.queued` | `@Async` pool saturation |
| `process.cpu.usage`, `system.cpu.usage` | CPU |
| `logback.events` | Error-log rate per level |

**Export:** Prometheus **pulls** from `/actuator/prometheus` (add
`micrometer-registry-prometheus`). OTLP, CloudWatch, Datadog and others **push** on a
fixed interval.

---

## 10. Custom metrics and the cardinality trap

**Weight:** ★★★

```java
@Component
class OrderMetrics {

    private final Counter ordersPlaced;
    private final Timer paymentTimer;

    OrderMetrics(MeterRegistry registry) {
        this.ordersPlaced = Counter.builder("orders.placed")
                .description("Orders successfully placed")
                .tag("channel", "web")
                .register(registry);
        this.paymentTimer = Timer.builder("payments.duration")
                .publishPercentileHistogram()
                .register(registry);
    }

    void orderPlaced() { ordersPlaced.increment(); }

    <T> T timePayment(Supplier<T> call) { return paymentTimer.record(call); }
}
```

**Annotation style:** `@Timed`, `@Counted` and `@Observed` need AOP and
`management.observations.annotations.enabled=true` — it defaults to `false`
(checked), the usual reason "my `@Timed` produces nothing".

**The cardinality trap:** every distinct combination of tag values is a separate time
series, held in the app's memory and stored by the monitoring system.

```java
// DON'T: one time series per customer — millions of series
Counter.builder("orders.placed").tag("customerId", id).register(registry);
```

- Never tag with ids, emails, raw URLs or free text. Use small, fixed sets: status,
  channel, region, outcome.
- That is why `http.server.requests` tags the URI **template** (`/orders/{id}`), not the
  real path.
- Cap a tag with a `MeterFilter`:

```java
@Bean
MeterFilter limitUriTags() {
    return MeterFilter.maximumAllowableTags(
            "http.server.requests", "uri", 100, MeterFilter.deny());
}
```

On paid systems (CloudWatch, Datadog) each series costs money, so cardinality becomes
a bill as well as a memory problem.

---

## 11. Percentiles, histograms and SLOs

**Weight:** ★★

**Why not averages:** 99 requests at 50 ms and one at 10 s average to about 150 ms —
which hides the user who waited 10 s. Track p95 and p99.

| Option | How | Combine across pods? |
| --- | --- | --- |
| Client-side percentiles | `.publishPercentiles(0.95, 0.99)` | **No** — each pod computes its own; averaging percentiles is wrong |
| Histogram buckets | `.publishPercentileHistogram()` | **Yes** — buckets are summed, then the percentile is computed |
| SLO buckets | `management.metrics.distribution.slo.http.server.requests=100ms,500ms` | Yes — "what share of requests met 500 ms?" |

Turn histograms on for HTTP:
`management.metrics.distribution.percentiles-histogram.http.server.requests=true`
(property name checked).

Prometheus p99 per endpoint across all pods:

```text
histogram_quantile(0.99,
  sum by (le, uri) (rate(http_server_requests_seconds_bucket[5m])))
```

---

## 12. Observations and distributed tracing

**Weight:** ★★★

**Observation API:** instrument code once; registered handlers turn it into a metric,
a trace span, and log correlation at the same time.

```java
Observation.createNotStarted("order.place", observationRegistry)
        .lowCardinalityKeyValue("channel", "web")
        .observe(() -> orderService.place(request));
```

**Micrometer Tracing** is a facade over a tracer — OpenTelemetry
(`micrometer-tracing-bridge-otel`, present locally) or Brave. Boot 4 adds
`spring-boot-starter-opentelemetry` and `spring-boot-starter-zipkin` (checked in the
4.0.3 BOM).

**How a trace crosses services:** the trace id travels in the W3C `traceparent` HTTP
header (and in Kafka headers). Each service adds its spans to the same trace, and puts
`traceId` / `spanId` into the logging MDC, so logs from five services can be joined.

**The sampling surprise:** `management.tracing.sampling.probability` defaults to
**0.1** (checked) — only 10% of requests are traced. "My trace is missing" is usually
this.

- Development: `1.0`.
- Production: keep head sampling low and use **tail sampling** in an OpenTelemetry
  Collector — keep every trace with an error or high latency, drop most of the rest.

**Context across threads:** tracing context is thread-local. It reaches `@Async`
methods, executors and Reactor only through context propagation (Micrometer's
`context-propagation` library, or a `TaskDecorator`).

---

## 13. Changing log levels at runtime

**Weight:** ★★

```text
GET  /actuator/loggers/com.shop.payment
POST /actuator/loggers/com.shop.payment    {"configuredLevel": "DEBUG"}
POST /actuator/loggers/com.shop.payment    {"configuredLevel": null}    ← reset
```

- No restart needed — useful in the middle of an incident.
- **Per instance only.** With 10 pods behind a load balancer, the call changes one
  random pod. Call each pod directly, or use a central mechanism (config server
  refresh, Spring Cloud Bus).
- DEBUG in production is expensive and may log personal data — reset it afterwards.
- It changes state, so protect it (Q4).

---

## 14. Diagnosing production problems with Actuator

**Weight:** ★★★ — "Your service is slow in production. What do you do?"

| Symptom | Endpoint or meter | What to look for |
| --- | --- | --- |
| High CPU | `threaddump`, taken 3 times a few seconds apart | The same `RUNNABLE` stack in every dump — a hot loop or regex |
| Requests hang | `threaddump` | Many threads `WAITING` in `HikariPool.getConnection` (pool exhausted) or in a socket read (slow dependency) |
| DB pool exhausted | `hikaricp.connections.pending` | Pending > 0 for a sustained time; check for long transactions |
| Memory keeps growing | `jvm.memory.used`, `jvm.gc.pause` | Old generation not dropping after full GCs → leak; then a heap dump |
| One endpoint slow | `http.server.requests` by `uri`, plus traces | p99 for that URI; the slowest span in the trace |
| Error spike | `http.server.requests` with `outcome=SERVER_ERROR`, `logback.events` | Which URI and exception |
| Is my config applied? | `env`, `configprops` | Which property source won |
| Bean not created | `conditions` | The negative match and its reason |
| Slow startup | `startup` | The slowest bean initializations |
| Job didn't run | `scheduledtasks` | Is it registered, and with what schedule |

**Heap dumps in production:** off by default in 4.1.1 (`heapdump` access `none`,
checked). The file is as large as the heap, the JVM pauses while it is written, and it
contains secrets. Prefer `jcmd <pid> GC.heap_dump` on an instance already taken out of
traffic.

---

## 15. Writing a custom endpoint

**Weight:** ★★

```java
@Component
@Endpoint(id = "features")
public class FeaturesEndpoint {

    private final Map<String, Boolean> features = new ConcurrentHashMap<>();

    @ReadOperation                       // GET /actuator/features
    public Map<String, Boolean> all() {
        return features;
    }

    @ReadOperation                       // GET /actuator/features/{name}
    public Boolean one(@Selector String name) {
        return features.get(name);
    }

    @WriteOperation                      // POST /actuator/features/{name}
                                         // body: {"enabled": true}
    public void set(@Selector String name, boolean enabled) {
        features.put(name, enabled);
    }
}
```

- Expose it: `management.endpoints.web.exposure.include=health,features`.
- **Why not a `@RestController`?** An endpoint automatically gets Actuator's port,
  exposure rules, access control, security matcher (`EndpointRequest`) and JMX
  support.
- `@DeleteOperation` maps to HTTP DELETE; `@WebEndpoint` makes it HTTP-only.

---

## 16. httpexchanges, auditevents and startup

**Weight:** ★

- **`httpexchanges`** — recent requests and responses. Recording is on
  (`management.httpexchanges.recording.enabled=true`, checked), but nothing is stored
  until you define an `HttpExchangeRepository` bean, such as
  `InMemoryHttpExchangeRepository` (keeps the last 100). Development only — use
  tracing in production.
- **`auditevents`** — Spring Security login success and failure events. Needs an
  `AuditEventRepository` bean.
- **`startup`** — startup steps with durations. Needs buffering:

```java
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ShopApplication.class);
    app.setApplicationStartup(new BufferingApplicationStartup(2048));
    app.run(args);
}
```

---

## 17. The SBOM endpoint

**Weight:** ★

- A **software bill of materials** lists every library (and version) inside the app.
- `/actuator/sbom` (endpoint class present in 4.1.1; added in Boot 3.3) serves a
  CycloneDX SBOM that the `cyclonedx-maven-plugin` generates at build time.
- **Why it matters:** when a new CVE is announced, you can ask every running service
  "do you contain library X at version Y?" instead of reading build files.

---

## 18. A production configuration

**Weight:** ★★

```yaml
management:
  server:
    port: 8081                        # never routed by the ingress
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,loggers
    access:
      default: read-only
  endpoint:
    loggers:
      access: unrestricted
    health:
      show-details: when-authorized
      probes:
        add-additional-paths: true    # /livez and /readyz on the main port too
      group:
        readiness:
          include: readinessState,db
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
  tracing:
    sampling:
      probability: 0.1                # tail-sample in the collector
```

---

## 19. Actuator overhead and pitfalls

**Weight:** ★★

| Pitfall | What happens | Fix |
| --- | --- | --- |
| Slow dependency in a probe | Probe times out → restarts or traffic removal | Timeouts, caching; keep liveness internal-only |
| Database in liveness | DB blip → every pod restarts | Database only in readiness, if at all |
| Non-critical dependency in health | 503 everywhere for a minor outage | Leave it out of routing groups |
| High-cardinality tags | Memory growth, huge monitoring bill | No ids in tags; `MeterFilter` caps |
| `exposure.include=*` | Secrets and internals reachable | Explicit list + separate port + security |
| Heap dump in production | Long pause, huge file, secrets | `access=none`; take dumps out of traffic |
| Sampling left at 0.1 in dev | "Tracing doesn't work" | `1.0` locally; tail sampling in production |
| `loggers` changed on one pod | Only one of N pods logs DEBUG | Call each pod, or a central mechanism |
