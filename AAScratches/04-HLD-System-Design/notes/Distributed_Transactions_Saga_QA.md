# Distributed Transactions and Saga Q&A

Twelve interview questions with expected answers on Saga, transactional outbox, idempotency, and related microservice resilience and boundary patterns.

Blocks marked **Correction** flag a statement that was wrong or out of date in an
earlier version of these notes, and say what the right distinction is.

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

Reference tables used across several answers:

- [Quick recall: the one line to say for each question](#quick-recall-the-one-line-to-say-for-each-question)
- [What each protocol actually does on failure](#what-each-protocol-actually-does-on-failure)
- [Delivery guarantees vocabulary](#delivery-guarantees-vocabulary)

---

## Quick recall: the one line to say for each question

| # | Topic | The sentence to lead with |
| - | ----- | ------------------------- |
| 1 | Saga: when not to | Saga buys availability by giving up atomicity and isolation |
| 2 | Choreography vs orchestration | Choreography for few services and stable flows; orchestration for branching and audit |
| 3 | Transactional outbox | Turns two writes into one commit; delivery is at-least-once, not exactly-once |
| 4 | Idempotency | Store the key in the same transaction as the effect it guards |
| 5 | Strangler Fig | Route slices of traffic to the new service; the shared database is what blocks you |
| 6 | Bulkhead vs breaker | Bulkhead protects capacity, breaker protects latency and error rate |
| 7 | API gateway: when not to | Skip it when there are no shared cross-cutting concerns to centralise |
| 8 | Chatty services | Composition, CQRS read views, CDC replication — or fix the boundary |
| 9 | Sidecar | One helper process per workload; the cost is per-pod, so it multiplies |
| 10 | Breaker + retry outage | Retries multiply across layers; backoff, jitter and a retry budget stop it |
| 11 | Read consistency | Async consistency with idempotent consumers, chosen against a business SLA |
| 12 | Wrong boundaries | Chatty sync calls and Saga explosion are boundary smells, not plumbing smells |

---

## What each protocol actually does on failure

This is the table interviewers actually probe. "It rolls back" is the wrong answer
for three of these four rows.

| Protocol | On a participant failure | On a coordinator failure | Consistency reached |
| -------- | ------------------------ | ------------------------ | ------------------- |
| 2PC | Coordinator broadcasts abort; everyone rolls back | **Blocks.** Participants that voted YES hold locks, in-doubt, until it returns | Atomic, strongly consistent |
| 3PC | Aborts, with a timeout path that lets participants decide | Participants can time out and decide themselves | Atomic only if the network never partitions |
| Saga | Runs compensating transactions for the steps already committed | Orchestrator resumes from its persisted log | Eventually consistent |
| TCC | Cancel is called on every reserved resource | Recovery job re-drives Confirm or Cancel to completion | Eventually consistent |

Two distinctions worth saying out loud:

- **Rollback is not compensation.** A rollback erases a transaction that was never
  visible. A compensation is a *new* business transaction that offsets one that was
  already committed and already visible to other readers. You cannot un-send an
  email; you send an apology.
- **Saga gives up isolation, not just atomicity.** Between step 2 committing and
  step 4 compensating, other transactions can read the intermediate state (a dirty
  read at the business level). The standard countermeasures are semantic lock (a
  pending/reserved status flag), commutative updates, ordering the steps so the
  most-likely-to-fail one runs first, re-reading the value before acting on it, and
  versioning records so a compensation can detect that someone else moved.

---

## Delivery guarantees vocabulary

| Guarantee | What it really means | What you must build |
| --------- | -------------------- | ------------------- |
| At-most-once | Fire and forget; loss is possible | Nothing, but you accept loss |
| At-least-once | Delivered, possibly more than once | Idempotent consumers, always |
| Exactly-once *within one system* | Kafka can do this across Kafka topics and Kafka offsets in one transaction | Transactional producer + `read_committed` consumer |
| Exactly-once *end to end* | Does not exist as a transport guarantee across process and database boundaries | At-least-once delivery + idempotent effects |

> **Correction — "exactly-once processing" is not a thing you can buy.**
> Kafka's exactly-once semantics (idempotent producer plus transactions) is real,
> but its scope is data inside Kafka: atomic writes across partitions plus the
> consumer offset commit, in one cluster. It cannot make a Kafka write and a
> PostgreSQL write atomic, because the database is not in the transaction. The
> practical pattern is: at-least-once delivery, plus a dedupe key on the consumer
> side that makes reprocessing a no-op. Anywhere these notes previously said
> "exactly-once processing", read it as "at-least-once delivery with idempotent
> consumers".

---

## 1. When would you NOT use Saga for distributed transactions? What alternative patterns would you consider?

**Expected answer**

Don't use Saga when:

- Operations need strong atomicity (classic ACID) → Saga is eventually consistent.
- A rollback step is too complex or impossible.
- The system cannot tolerate intermediate inconsistent states.
- Compensation may cause cascading failures.

Add the two that candidates usually miss:

- **The operation is not compensatable at all.** Money already paid out to an
  external party, a physical shipment already dispatched, a message already sent to
  a customer. If the only compensation is "call a human", Saga is hiding a manual
  process rather than automating one.
- **All the participants live in one database.** Then you do not have a distributed
  transaction; you have one local transaction, and reaching for Saga is pure cost.

Alternatives:

- 2PC (Two-Phase Commit) if the scale is small and a coordinator is acceptable.
- Transactional Outbox + Change Data Capture for reliable async updates.
- Orchestrated Saga if choreography chaos exists.
- Idempotent writes + retries for simpler flows.

> **Correction — say *why* 2PC needs "small scale", or the answer sounds memorised.**
> 2PC is a **blocking** protocol. Once a participant has voted YES in the prepare
> phase it must hold its locks and wait; if the coordinator crashes before sending
> the decision, that participant is stuck "in doubt" and cannot unilaterally commit
> or abort, because it has no way to know what the others voted. Locks stay held,
> and everything touching those rows queues behind them. That is the real cost, and
> it is why 2PC is confined to a single trusted, low-latency boundary (one
> datacentre, an XA transaction across two local resource managers) rather than
> across services over the public internet. Systems that do need atomic multi-shard
> commit at scale run 2PC *on top of* a consensus-replicated coordinator log
> (Spanner, CockroachDB, YugabyteDB), so coordinator failure is survivable.

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

Follow-up you should pre-empt — *"isn't the orchestrator a single point of failure?"*

- Not if its state is durable. The orchestrator's job is to persist "which step am I
  on" before each call, so any instance can pick the workflow back up after a crash.
  That is exactly what Temporal, Netflix Conductor, AWS Step Functions and Camunda
  sell: a replicated log of workflow state, not a clever process.
- It *is* a single point of *coupling*: every flow change goes through one codebase
  and one team's deploy. That is the real trade-off to name, not availability.

## 3. Explain the problem the Transactional Outbox Pattern solves and how it avoids race conditions.

**Expected answer**

Problem:

- A service writes a DB row AND publishes an event → risk of dual-write inconsistency.
- Both orderings fail somewhere: commit-then-publish loses the event if the process
  dies in between; publish-then-commit emits an event for a transaction that then
  rolls back. There is no ordering of two independent writes that is safe, which is
  why the pattern exists at all.

Solution:

- Write the event into the same DB transaction as the business record.
- A separate process (CDC/Debezium/polling publisher) emits the events.

How it avoids the race condition:

- Both business state and event are committed atomically — one commit, one atomic
  unit, so the event row exists if and only if the business row does.

> **Correction — the outbox removes *loss*, not *duplication*.**
> An earlier version of this note said "there is no scenario where the event is
> published but the DB update fails (and vice versa)". The first half is right; the
> second half is not. Concretely: the relay reads outbox row 42, publishes it to
> Kafka successfully, then crashes before it can mark row 42 as sent. On restart it
> reads row 42 again and publishes a second copy. The broker's own retries add more.
> So the outbox gives you **at-least-once** delivery — the event is guaranteed to be
> published *eventually*, never lost, but possibly more than once and possibly after
> a visible lag. Every consumer of an outbox stream must therefore be idempotent,
> keyed on the event id. Say this in an interview and you are immediately ahead of
> the candidate who calls the outbox "exactly-once".

Two operational details worth naming:

- **Ordering** is per-partition, not global. If you need per-aggregate ordering, key
  the outbox message by the aggregate id so all its events land on one partition.
- **CDC vs polling**: Debezium reads the write-ahead log, so it adds no query load
  and preserves commit order; a polling publisher is simpler to operate but adds
  query load and needs an index on `(sent, created_at)` to avoid a scan every tick.

## 4. How do you design idempotency for microservices? Provide patterns.

**Expected answer**

Patterns:

- Idempotency Key stored with the request.
- Request deduplication table with status.
- Upsert (insert or update) semantics for writes.
- Versioning (optimistic concurrency).
- State machine transitions that ignore invalid transitions.
- Retry tokens / producer sequence numbers carried by the message broker.

The candidate should emphasize:

> "Idempotency is not about re-running the same code — it's about making the end state unchanged regardless of repetitions."

> **Correction — two of these patterns are only idempotent if you implement them a
> specific way, and the note did not say which.**
>
> - **The idempotency key must be inserted in the same transaction as the effect it
>   guards**, under a UNIQUE constraint. If you check "have I seen this key?" first
>   and write the effect second, two concurrent retries both pass the check and both
>   apply the effect. The database's uniqueness check is the lock; a `SELECT` is not.
> - **Upsert is idempotent only for absolute writes, not for deltas.** `SET balance
>   = 500` is idempotent. `SET balance = balance + 100` is not, however you spell it,
>   and it stays not-idempotent inside an `INSERT ... ON CONFLICT DO UPDATE`. Delta
>   writes need the dedupe table, not upsert semantics.

Practical shape of an idempotent endpoint:

| Concern | Choice to state |
| ------- | --------------- |
| Key source | Client-supplied UUID in an `Idempotency-Key` header |
| Storage | Dedupe table, UNIQUE on the key, written in the business transaction |
| Concurrent retry | Second insert violates the constraint → return the stored response |
| In-flight retry | Row exists with status `IN_PROGRESS` → return 409, let the client retry |
| Expiry | TTL of 24–72h, long enough to outlive every retry window upstream |

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
- **The migration stalls half-done** — the most common real failure. Both systems
  now exist, both must be maintained, and the will to finish evaporates once the
  painful 20% is all that is left. Name an end date and a decommission owner.

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

Concrete failure a bulkhead prevents, worth having ready: one slow downstream
dependency occupies every thread in a shared pool, so requests that never touch that
dependency also time out. The service is "down" because of a feature 95% of users
were not using. A per-dependency pool caps the blast radius at that one feature.

## 7. When would you not use API Gateway Pattern? What are better alternatives?

**Expected answer**

Avoid API Gateway if:

- The system requires ultra-low latency (microseconds).
- The team is small → a gateway adds complexity.
- Services do not share cross-cutting concerns.
- Real-time streaming workloads (Kafka, gRPC).

Alternatives:

- Service Mesh for service-to-service communication.
- Backend-for-Frontend (BFF) per client type, for mobile/web.
- Edge proxy + CDN for static/REST workloads.

> **Correction — a BFF is not "direct client-to-service".**
> The note previously read "Direct client-to-service (BFF)", which fuses two
> opposite designs. *Direct client-to-service* means the client holds N base URLs and
> calls each service itself — no intermediary, so no shared auth, no aggregation, and
> every client changes when a service moves. A *BFF* is the opposite: it is an extra
> hop, one gateway **per client type** (one for iOS, one for web), that aggregates and
> reshapes responses for that client's screens. You pick BFF precisely when a single
> shared gateway is the wrong shape because each client needs a different payload.
> They are alternatives to each other, not the same option.

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

Trade-off to state, so the answer is not just a list: every one of these buys fewer
calls by holding a **second copy** of somebody else's data. The bill arrives as
staleness and as a new failure mode (the replica falls behind, the cache serves a
value the owner has already changed). Pick the one whose staleness window the
business can actually tolerate, and say what that window is.

## 9. Explain the Sidecar Pattern and when it's harmful.

**Expected answer**

A sidecar runs a helper process beside the main service for:

- Logging
- Service mesh proxies (Envoy)
- Security agents
- Config updaters

Harmful when:

- Too many sidecars → "sidecar explosion".
- Resource overhead becomes huge — the cost is *per pod*, so 1,000 pods means 1,000
  copies of the proxy's CPU and memory floor, not one.
- Hard to debug failures between container boundaries.
- Increased startup times, and a startup-ordering hazard: the app can begin calling
  out before the proxy is ready unless you use native sidecar containers.

Current state of the art, worth mentioning:

- Kubernetes added **native sidecar containers** (init containers with
  `restartPolicy: Always`), which fixes the startup-order and job-never-terminates
  problems that made the pattern fiddly for years.
- Service meshes now offer a **sidecar-less / ambient** mode, where a per-node proxy
  handles mTLS and L4 for every pod on the node, and a shared L7 proxy is added only
  where L7 policy is actually needed. That directly targets the per-pod overhead
  above. Use this as the "what would you do differently today" answer.

## 10. Tricky: Can Circuit Breaker + Retry Pattern cause a global outage? Show how.

**Expected answer**

Yes — the classic retry storm scenario.

Flow:

1. Downstream service slows.
2. Calls fail → breaker opens.
3. Clients retry aggressively.
4. Retries cause even more load.
5. Breaker resets → traffic spike → downstream dies completely.

The multiplier is the part to draw, because it is the part that surprises people:

```text
Client retries 3x -> Gateway retries 3x -> Service A retries 3x -> Service B
    1 request            3 requests            9 requests           27 calls
```

Retries compose multiplicatively, so three innocuous-looking layers of "just retry
three times" turn one user request into 27 calls on the struggling dependency,
exactly when it has the least capacity to serve them.

Fix using patterns:

- Retry with exponential backoff + jitter. Jitter matters as much as backoff —
  without it every client retries on the same tick and you rebuild the spike.
- Per-client token bucket rate limiting.
- Bulkheads to restrict concurrent callers.
- Partition breakers for independence.
- **Retry at one layer only**, and let the others fail fast. This is the fix that
  kills the multiplier.
- **Retry budget**: allow retries only while they stay under a few percent of total
  traffic, then stop retrying entirely. Caps amplification at a known ceiling.
- Half-open should admit a **small number of probe requests**, not the full backlog.
  A breaker that flips straight from open to closed recreates step 5 by design.

## 11. Which microservice patterns help maintain read consistency without distributed transactions?

**Expected answer**

- Event Sourcing
- CQRS
- Materialized views
- Read replicas
- Transactional Outbox + CDC
- Consumer-retry queues
- At-least-once delivery with idempotent consumers

A great candidate mentions:

> "Use asynchronous consistency where possible; choose the consistency level based on the business SLA."

> **Correction — the last bullet used to read "Exactly-once processing (idempotency)".**
> Those two are not synonyms, and putting them in one bullet hides the actual
> mechanism. There is no exactly-once delivery across a process boundary; what you
> build is at-least-once delivery plus consumer-side deduplication, which produces an
> exactly-once *effect*. See [Delivery guarantees vocabulary](#delivery-guarantees-vocabulary).
> The distinction matters in the follow-up, because it tells you where the dedupe
> state has to live: in your consumer's database, not in the broker.

Read-replica caveat to have ready: a replica is asynchronously behind, so
"write then immediately read" can return the old value. Route a user's reads to the
primary for a few seconds after their own write (read-your-writes), or carry the
write's log position on the request and wait for the replica to reach it.

## 12. How do you detect wrong domain boundaries in your microservice system?

**Expected answer**

Signals:

- Too many synchronous calls → temporal coupling.
- Constant changes across multiple services.
- Teams are stepping on each other frequently.
- Many distributed transactions (Saga explosion).
- Reads require stitching from 5+ services.
- One service cannot be deployed without deploying another in the same window —
  the sharpest test of all, because it is binary and observable in the deploy log.

Pattern fixes:

- Event storming to realign domains.
- Consolidate services (merge them).
- Apply Domain Aggregator or API Composition.
