# Distributed Transactions and Saga Q&A

Twelve interview questions with expected answers on Saga, transactional outbox, idempotency, and related microservice resilience and boundary patterns.

## Table of Contents

1. [When would you NOT use Saga for distributed transactions? What alternative patterns would you consider?](#1-when-would-you-not-use-saga-for-distributed-transactions-what-alternative-patterns-would-you-consider)
2. [How do you identify when to use Choreography vs Orchestration in a Saga? Give real-world signals.](#2-how-do-you-identify-when-to-use-choreography-vs-orchestration-in-a-saga-give-real-world-signals)
3. [Explain the problem the Transactional Outbox Pattern solves and how it avoids race conditions.](#3-explain-the-problem-the-transactional-outbox-pattern-solves-and-how-it-avoids-race-conditions)
4. [How do you design idempotency for microservices? Provide patterns.](#4-how-do-you-design-idempotency-for-microservices-provide-patterns)
5. [What problem does Strangler Fig Pattern solve, and where can it fail?](#5-what-problem-does-strangler-fig-pattern-solve-and-where-can-it-fail)
6. [Explain the Bulkhead Pattern and how it differs from Circuit Breaker.](#6-explain-the-bulkhead-pattern-and-how-it-differs-from-circuit-breaker)
7. [When would you not use API Gateway Pattern? What are better alternatives?](#7-when-would-you-not-use-api-gateway-pattern-what-are-better-alternatives)
8. [What design patterns help in reducing chatty communication between microservices?](#8-what-design-patterns-help-in-reducing-chatty-communication-between-microservices)
9. [Explain the Sidecar Pattern and when it's harmful.](#9-explain-the-sidecar-pattern-and-when-its-harmful)
10. [Tricky: Can Circuit Breaker + Retry Pattern cause a global outage? Show how.](#10-tricky-can-circuit-breaker--retry-pattern-cause-a-global-outage-show-how)
11. [Which microservice patterns help maintain read consistency without distributed transactions?](#11-which-microservice-patterns-help-maintain-read-consistency-without-distributed-transactions)
12. [How do you detect wrong domain boundaries in your microservice system?](#12-how-do-you-detect-wrong-domain-boundaries-in-your-microservice-system)

---

## 1. When would you NOT use Saga for distributed transactions? What alternative patterns would you consider?

**Expected answer**

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

## 2. How do you identify when to use Choreography vs Orchestration in a Saga? Give real-world signals.

**Expected answer**

Use Choreography when:

- Few services (<5) are involved.
- The workflow is simple.
- No single place needs to understand the full workflow.
- The business flow evolves frequently → decentralization preferred.

Signals that choreography is failing:

- "Distributed spaghetti" → too many event listeners.
- Hard to understand the business process.
- Debugging requires tracing multiple services.
- A change in business flow touches many services.

Switch to Orchestration when:

- One central "brain" is needed.
- Many conditional, branching flows.
- Need visibility/audit of the entire business process.
- Need isolation of business logic from services.

## 3. Explain the problem the Transactional Outbox Pattern solves and how it avoids race conditions.

**Expected answer**

Problem:

- A service writes a DB row AND publishes an event → risk of dual-write inconsistency.

Solution:

- Write the event into the same DB transaction as the business record.
- A separate process (CDC/Debezium/polling publisher) emits the events.

How it avoids the race condition:

- Both business state and event are committed atomically.
- There is no scenario where the event is published but the DB update fails (and vice versa).

## 4. How do you design idempotency for microservices? Provide patterns.

**Expected answer**

Patterns:

- Idempotency Key stored with the request.
- Request deduplication table with status.
- Upsert (insert or update) semantics for writes.
- Versioning (optimistic concurrency).
- State machine transitions that ignore invalid transitions.
- Retry tokens embedded in message brokers.

The candidate should emphasize:

> "Idempotency is not about re-running the same code — it's about making the end state unchanged regardless of repetitions."

## 5. What problem does Strangler Fig Pattern solve, and where can it fail?

**Expected answer**

Solves:

- Migrating monolith → microservices incrementally.
- A routing layer diverts traffic to the new service gradually.

Failure scenarios:

- Hard-to-split domains with deep coupling.
- Hidden shared database constraints.
- If domain boundaries are unclear.
- If the router layer becomes a bottleneck or single point of failure.

The candidate should mention a reverse proxy like:

- API Gateway
- Envoy
- NGINX
- Service Mesh

## 6. Explain the Bulkhead Pattern and how it differs from Circuit Breaker.

**Expected answer**

- Bulkhead = isolate resources (threads, memory pools, connections) so one failure doesn't drown everything.
- Circuit Breaker = stop calling a failing system after a threshold to prevent cascading failures.

Key difference:

- Bulkhead prevents resource starvation.
- Circuit Breaker prevents continuous failing calls.

A strong answer will say:

> "Bulkhead protects capacity, Circuit Breaker protects latency and error rates."

## 7. When would you not use API Gateway Pattern? What are better alternatives?

**Expected answer**

Avoid API Gateway if:

- The system requires ultra-low latency (microseconds).
- The team is small → a gateway adds complexity.
- Services do not share cross-cutting concerns.
- Real-time streaming workloads (Kafka, gRPC).

Alternatives:

- Service Mesh for service-to-service communication.
- Direct client-to-service (BFF) for mobile/web.
- Edge proxy + CDN for static/REST workloads.

## 8. What design patterns help in reducing chatty communication between microservices?

**Expected answer**

- API Composition Pattern
- Aggregator Pattern
- CQRS + read views
- GraphQL façade layer
- Data replication through CDC
- Caching + read optimization services

Bonus point:

> "Excessive chatty communication means wrong domain boundaries."

## 9. Explain the Sidecar Pattern and when it's harmful.

**Expected answer**

A sidecar runs a helper process beside the main service for:

- Logging
- Service mesh proxies (Envoy)
- Security agents
- Config updaters

Harmful when:

- Too many sidecars → "sidecar explosion".
- Resource overhead becomes huge.
- Hard to debug failures between container boundaries.
- Increased startup times.

## 10. Tricky: Can Circuit Breaker + Retry Pattern cause a global outage? Show how.

**Expected answer**

Yes — the classic retry storm scenario.

Flow:

1. Downstream service slows.
2. Calls fail → breaker opens.
3. Clients retry aggressively.
4. Retries cause even more load.
5. Breaker resets → traffic spike → downstream dies completely.

Fix using patterns:

- Retry with exponential backoff + jitter.
- Per-client token bucket rate limiting.
- Bulkheads to restrict concurrent callers.
- Partition breakers for independence.

## 11. Which microservice patterns help maintain read consistency without distributed transactions?

**Expected answer**

- Event Sourcing
- CQRS
- Materialized views
- Read replicas
- Transactional Outbox + CDC
- Consumer-retry queues
- Exactly-once processing (idempotency)

A great candidate mentions:

> "Use asynchronous consistency where possible; choose the consistency level based on the business SLA."

## 12. How do you detect wrong domain boundaries in your microservice system?

**Expected answer**

Signals:

- Too many synchronous calls → temporal coupling.
- Constant changes across multiple services.
- Teams are stepping on each other frequently.
- Many distributed transactions (Saga explosion).
- Reads require stitching from 5+ services.

Pattern fixes:

- Event storming to realign domains.
- Consolidate services (merge them).
- Apply Domain Aggregator or API Composition.
