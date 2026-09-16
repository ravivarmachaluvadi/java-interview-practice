# Microservices Q&A

Interview-prep notes on microservices: data consistency and sagas, resilience against slow dependencies, distributed tracing, read-your-own-write in CQRS, retry mechanisms, and caching.

## Table of Contents

1. [How do you handle data consistency across multiple microservices?](#1-how-do-you-handle-data-consistency-across-multiple-microservices)
2. [What would you do if one downstream microservice becomes very slow?](#2-what-would-you-do-if-one-downstream-microservice-becomes-very-slow)
3. [How do you debug a user request that spans multiple microservices?](#3-how-do-you-debug-a-user-request-that-spans-multiple-microservices)
4. [How would you ensure "read-your-own-write" consistency in a CQRS system?](#4-how-would-you-ensure-read-your-own-write-consistency-in-a-cqrs-system)
5. [How do you apply a retry mechanism in microservices?](#5-how-do-you-apply-a-retry-mechanism-in-microservices)
6. [Why caching, and where and how do you apply it?](#6-why-caching-and-where-and-how-do-you-apply-it)
7. [When would you NOT use Saga for distributed transactions? What alternative patterns would you consider?](#7-when-would-you-not-use-saga-for-distributed-transactions-what-alternative-patterns-would-you-consider)
8. [How do you identify when to use Choreography vs Orchestration in a Saga? Give real-world signals.](#8-how-do-you-identify-when-to-use-choreography-vs-orchestration-in-a-saga-give-real-world-signals)

## 1. How do you handle data consistency across multiple microservices?

**Trick:** Candidate must know the saga pattern, event-driven architecture, and idempotency.

**Expected answer:**

- Avoid distributed transactions (2PC is not scalable).
- Use the Saga pattern: Choreography (event-based) or Orchestration (central coordinator).
- Make operations idempotent.
- Use event sourcing + compensating transactions for rollback logic.
- Ensure eventual consistency and design retry mechanisms.

## 2. What would you do if one downstream microservice becomes very slow?

**Trick:** Tests resilience and fault tolerance.

**Expected answer:**

- Implement timeouts, retries with exponential backoff, and circuit breakers (Resilience4j/Hystrix).
- Use the bulkhead pattern to isolate failure domains.
- Implement fallbacks where possible.
- Monitor latency via distributed tracing (e.g., Zipkin, Jaeger).
- Possibly asynchronous processing via message queues (Kafka, RabbitMQ).

## 3. How do you debug a user request that spans multiple microservices?

**Trick:** Tests knowledge of distributed tracing and observability.

**Expected answer:**

- Use distributed tracing (Jaeger, Zipkin, OpenTelemetry).
- Pass trace IDs and span IDs via HTTP headers.
- Aggregate logs in the ELK Stack or Grafana Loki.
- Use centralized logging and correlation IDs.
- Combine with metrics (Prometheus) and alerting.

## 4. How would you ensure "read-your-own-write" consistency in a CQRS system?

**Trick:** This is a real-world production issue.

**Expected answer:**

- **Option 1:** Write to both read & write models synchronously (at the cost of latency).
- **Option 2:** Use session consistency — route subsequent reads to the same node or a temporary cache.
- **Option 3:** Temporarily cache the latest write result for the user until the projection updates.
- **Option 4:** Add a "last updated timestamp" in the read model; if lag is detected, fetch from the write side.

## 5. How do you apply a retry mechanism in microservices?

### Why You Need a Retry Mechanism

Microservices communicate over unreliable networks (HTTP, gRPC, Kafka, etc.). Sometimes a call might fail due to:

- Temporary network glitches
- Service overload or throttling
- Database connection timeouts
- API gateway latency

Instead of failing immediately, a retry can help recover automatically without user impact.

### Core Principles of Retry Design

| Principle | Description |
| --- | --- |
| **Idempotency** | Retried operations must not cause side effects. For example, "add to cart" should not duplicate entries. |
| **Backoff Strategy** | Add delay between retries — typically exponential (1s, 2s, 4s…). |
| **Retry Limit** | Avoid infinite loops; limit retries (e.g., max 3 attempts). |
| **Jitter** | Add randomness to avoid multiple clients retrying at the same time (thundering herd problem). |
| **Circuit Breaker Integration** | Stop retrying when a service is clearly down. |
| **Logging & Metrics** | Track retries to detect unhealthy dependencies. |

### A. Spring Retry with `@Retryable`

```java
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Retryable(
        value = { RuntimeException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public String callPaymentService() {
        return restTemplate.getForObject("http://payment-service/pay", String.class);
    }
}
```

**Explanation:**

- Retries up to 3 times
- Delay between retries: 2s, 4s, 8s
- Uses Spring Retry behind the scenes

### B. Feign Client Retry (Declarative)

If you use Spring Cloud OpenFeign for inter-service calls:

```java
@FeignClient(name = "payment-service", configuration = PaymentRetryConfig.class)
public interface PaymentClient {
    @GetMapping("/pay")
    String makePayment();
}

@Configuration
public class PaymentRetryConfig {
    @Bean
    public Retryer retryer() {
        // period = initial interval, maxPeriod = max interval, maxAttempts
        return new Retryer.Default(1000, 5000, 3);
    }
}
```

### C. Resilience4j Retry (YAML configuration)

```yaml
resilience4j.retry:
  instances:
    inventoryRetry:
      max-attempts: 3
      wait-duration: 2s
      retry-exceptions:
        - java.net.ConnectException
        - java.net.SocketTimeoutException
```

### Retry Options by Layer

| Layer | Common Tools | Notes |
| --- | --- | --- |
| **HTTP Client** | Spring Retry, Resilience4j, Feign Retry | Simple + Declarative |
| **Async Messaging** | Kafka DLQ, RabbitMQ Retry Queue | For event-driven systems |
| **Service-to-Service** | Resilience4j + Circuit Breaker | Combine with fallback |
| **Database/API** | Retry transient failures only | Use exponential backoff |

## 6. Why caching, and where and how do you apply it?

### Why Caching?

Caching improves performance by storing frequently accessed data in memory, so you don't have to repeatedly:

- Query the database,
- Call an external API, or
- Perform expensive computations.

**Benefits:**

- Faster response times
- Reduced load on the database and downstream services
- Lower latency & cost
- Better scalability

### Where to Apply Caching (Cache Layers)

| Layer | Example | Description |
| --- | --- | --- |
| **Client-side cache** | Browser cache, mobile app memory | Avoids re-fetching the same data from the backend |
| **API Gateway cache** | NGINX / CloudFront / API Gateway | Caches whole HTTP responses |
| **Service layer cache** | `@Cacheable` in Spring Boot | Avoids recomputing the same logic or DB call |
| **Database caching** | Query cache / Redis lookup | Avoids hitting the DB for the same queries |
| **Distributed cache** | Redis / Memcached | Shared among multiple instances |

### Caching Strategies

| Strategy | Use Case | Example |
| --- | --- | --- |
| **Read-through** | Application reads from cache; if missing → loads from DB | Common in Spring Cache |
| **Write-through** | Writes go to cache + DB simultaneously | For consistent data |
| **Write-behind** | Writes go to cache first, DB updated later | For high write throughput |
| **Cache-aside (Lazy loading)** | App checks cache first → if miss → fetch DB → update cache | Most popular approach |

### In-Memory vs Distributed Cache

| Type | Example | Scope | Use Case |
| --- | --- | --- | --- |
| **In-memory** | `ConcurrentHashMap`, Caffeine, Ehcache | Single instance | Small-scale or local cache |
| **Distributed** | Redis, Memcached, Hazelcast | Shared across nodes | Scalable microservices |

### Spring Boot Example: Using `@Cacheable`

```java
@SpringBootApplication
@EnableCaching
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}

@Service
public class ProductService {

    @Cacheable(value = "products", key = "#productId")
    public Product getProductById(Long productId) {
        simulateSlowService();
        return productRepository.findById(productId).orElseThrow();
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(2000); // simulate DB latency
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

- First call fetches from DB (slow)
- Subsequent calls fetch from cache (fast)
- Cache key = product ID
- Cache name = "products"

### Evict or Update Cache

```java
@CacheEvict(value = "products", key = "#product.id")
public void updateProduct(Product product) {
    productRepository.save(product);
}
```

## 7. When would you NOT use Saga for distributed transactions? What alternative patterns would you consider?

**Expected answer:**

Don't use Saga when:

- Operations need strong atomicity (classic ACID) → Saga is eventually consistent.
- A rollback step is too complex or impossible.
- The system cannot tolerate intermediate inconsistent states.
- Compensation may cause cascading failures.

Alternatives:

- 2PC (Two-Phase Commit) if the scale is small and a coordinator is acceptable.
- Transactional Outbox + Change Data Capture for reliable async updates.
- Orchestrated Saga if choreography chaos exists.
- Idempotent writes + retries for simpler flows.

## 8. How do you identify when to use Choreography vs Orchestration in a Saga? Give real-world signals.

**Expected answer:**

Use Choreography when:

- Few services (<5) are involved.
- The workflow is simple.
- No single place needs to understand the full workflow.
- The business flow evolves frequently → decentralization preferred.

Signals that choreography is failing:

- "Distributed spaghetti" → too many event listeners.
- Hard to understand the business process.
- Debugging requires tracing multiple services.
- A change in the business flow touches many services.

Switch to Orchestration when:

- One central "brain" is needed.
- Many conditional, branching flows.
- Need visibility/audit of the entire business process.
- Need isolation of business logic from services.
