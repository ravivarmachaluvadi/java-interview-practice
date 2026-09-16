# Hibernate / JPA Q&A

Interview-prep notes on Hibernate and JPA: the entity lifecycle, the N+1 select problem, the caching levels and cache concurrency strategies, plus a Hibernate 6 `jsonb` column mapping snippet.

## Table of Contents

1. [What is the Hibernate entity lifecycle?](#1-what-is-the-hibernate-entity-lifecycle)
2. [What is the N+1 select problem and how do you solve it?](#2-what-is-the-n1-select-problem-and-how-do-you-solve-it)
3. [What are the different types of caching available in Hibernate?](#3-what-are-the-different-types-of-caching-available-in-hibernate)
4. [How does Hibernate's first-level cache work internally?](#4-how-does-hibernates-first-level-cache-work-internally)
5. [What's the difference between session.clear(), session.evict(), and session.refresh()?](#5-whats-the-difference-between-sessionclear-sessionevict-and-sessionrefresh)
6. [What is a Second-Level Cache and how is it configured?](#6-what-is-a-second-level-cache-and-how-is-it-configured)
7. [What are the different CacheConcurrencyStrategy options in Hibernate?](#7-what-are-the-different-cacheconcurrencystrategy-options-in-hibernate)
8. [Suppose two concurrent transactions update the same cached entity: what could go wrong?](#8-suppose-two-concurrent-transactions-update-the-same-cached-entity-what-could-go-wrong)
9. [Snippet: mapping a jsonb column with Hibernate 6](#snippet-mapping-a-jsonb-column-with-hibernate-6)

## 1. What is the Hibernate entity lifecycle?

An entity is in one of four states. The first table describes each state, the second shows whether Hibernate manages it and whether a database row exists, and the third lists the `Session` methods that move an entity between states.

**Entity states:**

| State | Description |
| --- | --- |
| **Transient** | Newly created object, **not associated** with a Hibernate `Session` and **not yet saved** in DB. |
| **Persistent** | Associated with an active Hibernate `Session` — Hibernate tracks changes and synchronizes with DB during `flush()`. |
| **Detached** | Was persistent earlier, but now the `Session` is closed or the entity is evicted. Hibernate no longer tracks changes. |
| **Removed** | Marked for deletion from DB, will be deleted when `flush()` or transaction commit occurs. |

**State properties:**

| State | Managed by Hibernate? | Database row exists? |
| --- | --- | --- |
| **Transient** | No | No |
| **Persistent** | Yes | Yes |
| **Detached** | No | Yes |
| **Removed** | Yes (until delete executed) | Soon to be removed |

**State transitions:**

| Method | Transitions | Description |
| --- | --- | --- |
| `save()` / `persist()` | Transient → Persistent | Makes a new object managed and schedules `INSERT`. |
| `update()` / `merge()` | Detached → Persistent | Reattaches a detached entity or merges its state. |
| `delete()` / `remove()` | Persistent → Removed | Marks entity for deletion. |
| `evict()` | Persistent → Detached | Removes entity from session cache (no DB operation). |
| `refresh()` | Persistent → Persistent | Reloads entity state from DB. |
| `clear()` / `close()` | Persistent → Detached (for all entities) | Ends persistence context or session. |

## 2. What is the N+1 select problem and how do you solve it?

When fetching a list of entities with a lazy collection, each child causes an extra query.

**Solution:**

- Use `@EntityGraph`
- Use `JOIN FETCH` in JPQL
- Configure batch fetching

## 3. What are the different types of caching available in Hibernate?

Hibernate supports multiple cache levels:

- **First-level cache:** Built-in and mandatory per `Session`. Caches entities within the same session scope.
- **Second-level cache:** Optional, session factory–level cache. Caches entities and collections across sessions.
- **Query cache:** Optional, caches query result sets (identifiers); depends on the second-level cache.
- **Natural ID cache:** Used for caching lookups by natural identifiers (like email or username).

## 4. How does Hibernate's first-level cache work internally?

- Managed by the `PersistenceContext` inside a Hibernate `Session`.
- When you call `session.get()` or `session.find()`, Hibernate first checks this cache.
- On flush, dirty entities are synchronized with the database.
- When the session is closed or cleared, the first-level cache is destroyed.

**Follow-up:** You can explicitly clear it using `session.clear()` or evict a single entity using `session.evict(entity)`.

## 5. What's the difference between `session.clear()`, `session.evict()`, and `session.refresh()`?

- `evict(entity)`: Removes a single entity from the first-level cache.
- `clear()`: Removes all entities from the first-level cache.
- `refresh(entity)`: Reloads the entity from the DB, overwriting any cached or dirty state.

## 6. What is a Second-Level Cache and how is it configured?

- It's a shared cache across sessions, managed by the `SessionFactory`.
- Requires an external cache provider like Ehcache, Infinispan, Redis, or Caffeine.
- Enable it via configuration:

```properties
hibernate.cache.use_second_level_cache=true
hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
hibernate.cache.use_query_cache=true
```

- Annotate the entity:

```java
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
```

## 7. What are the different CacheConcurrencyStrategy options in Hibernate?

| Strategy | Description | Use Case |
| --- | --- | --- |
| `READ_ONLY` | For immutable data | Reference data |
| `NONSTRICT_READ_WRITE` | No guarantee of strong consistency | Low update frequency |
| `READ_WRITE` | Uses soft locks to maintain consistency | Medium update frequency |
| `TRANSACTIONAL` | Uses JTA and XA transactions | Distributed caching with full ACID |

## 8. Suppose two concurrent transactions update the same cached entity: what could go wrong?

Depends on the concurrency strategy:

- With `READ_WRITE`, Hibernate uses a soft-lock: the first transaction locks the cache entry until commit; others must wait.
- With `NONSTRICT_READ_WRITE`, updates are not locked → possible dirty reads.
- With `READ_ONLY`, update attempts cause an exception.

**Real-world fix:** Pick the strategy based on update frequency and consistency need — not blindly.

## Snippet: mapping a jsonb column with Hibernate 6

Transcribed from a photo the owner kept at `08-Reference/images/jpa-entity-jsonb-column-snippet.jpg`.

```java
@Entity
@Audited
@EntityListeners(EntityListener.class)
@Table(name = "BDM_APPROVAL_REQUEST")
public class ApprovalRequest {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private RequestType type;

    @Column(name = "EDIT_PARAMS", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private GqlSupplierEdit editParams;
}
```

- `@JdbcTypeCode(SqlTypes.JSON)` (Hibernate 6) maps a POJO to a PostgreSQL `jsonb` column without a custom UserType.
- `@GeneratedValue(strategy = GenerationType.UUID)` is the JPA 3.1 way to generate UUID keys.
