# Hibernate / JPA Q&A

Interview-prep notes on Hibernate and JPA: the entity lifecycle, lazy loading and
`LazyInitializationException`, the N+1 select problem, the caching levels and cache
concurrency strategies, `equals`/`hashCode` for entities, plus a Hibernate 6 `jsonb` column
mapping snippet.

## Contents

| # | Question | The one thing to remember |
| --- | --- | --- |
| 1 | [Entity lifecycle](#1-what-is-the-hibernate-entity-lifecycle) | Only a *managed* entity is dirty-checked. |
| 2 | [The N+1 select problem](#2-what-is-the-n1-select-problem-and-how-do-you-solve-it) | One query per **parent**, not per child — and `EAGER` makes it worse, not better. |
| 3 | [Caching levels](#3-what-are-the-different-types-of-caching-available-in-hibernate) | L1 is mandatory and per-session; L2 is optional and shared. |
| 4 | [First-level cache internals](#4-how-does-hibernates-first-level-cache-work-internally) | It is the persistence context, and it is why `find()` twice is one query. |
| 5 | [`clear()` / `evict()` / `refresh()`](#5-whats-the-difference-between-sessionclear-sessionevict-and-sessionrefresh) | Detach one, detach all, reload one. |
| 6 | [Second-level cache setup](#6-what-is-a-second-level-cache-and-how-is-it-configured) | `@Cacheable` here is `jakarta.persistence`, not Spring's. |
| 7 | [Cache concurrency strategies](#7-what-are-the-different-cacheconcurrencystrategy-options-in-hibernate) | Pick by write frequency and staleness tolerance. |
| 8 | [Concurrent updates to a cached entity](#8-suppose-two-concurrent-transactions-update-the-same-cached-entity-what-could-go-wrong) | The L2 cache does not give you locking you did not already have. |
| 9 | [`LazyInitializationException`](#9-what-is-lazyinitializationexception-and-how-do-you-really-fix-it) | Fix the fetch, do not open the session longer. |
| 10 | [`equals` and `hashCode`](#10-how-do-you-write-equals-and-hashcode-for-a-jpa-entity) | A generated id is null before flush — never hash on it. |
| 11 | [Snippet: `jsonb` column](#snippet-mapping-a-jsonb-column-with-hibernate-6) | `@JdbcTypeCode(SqlTypes.JSON)` replaces the custom `UserType`. |

## Corrections made to this file

| Topic | The note used to say | What is actually true |
| --- | --- | --- |
| `merge()` | "Detached → Persistent" alongside `update()` | `merge()` does **not** attach the instance you pass it. It copies the state onto a managed copy and returns that copy; your argument stays detached. `update()` (Hibernate-legacy) reattaches the same instance. Using the return value is the whole trick. |
| N+1 | "Each **child** causes an extra query" | Each **parent** causes one extra query for its association: 1 + N, where N is the number of parents fetched. |
| L2 cache config | `org.hibernate.cache.ehcache.EhCacheRegionFactory` | That class belongs to Ehcache 2, dead since Hibernate 5.3. Modern setups use the JCache bridge (`hibernate-jcache`) with Ehcache 3, or Infinispan. |
| Cache providers | "Ehcache, Infinispan, Redis, or Caffeine" | Hibernate ships integrations for Ehcache (via JCache), Infinispan and JCache generally. Redis and Hazelcast are third-party region factories (e.g. Redisson). Caffeine has no official Hibernate region factory — it reaches Hibernate only through a JCache bridge. |
| `@Cacheable` | Shown unqualified | On an entity this is `jakarta.persistence.Cacheable`. Spring's `org.springframework.cache.annotation.Cacheable` is a different annotation for a different cache; mixing them up is a classic interview trip-up. |
| Coverage | No lazy loading, no `equals`/`hashCode` | Both added — they are the two questions asked most often after N+1. |

## 1. What is the Hibernate entity lifecycle?

An entity is in one of four states. The first table describes each state, the second shows
whether Hibernate manages it and whether a database row exists, and the third lists the
methods that move an entity between states.

**Entity states:**

| State | Description |
| --- | --- |
| **Transient** (JPA: *new*) | Newly created object, **not associated** with a persistence context and **not yet saved** in the DB. |
| **Persistent** (JPA: *managed*) | Associated with an open persistence context — Hibernate tracks changes and synchronises them at `flush()`. |
| **Detached** | Was persistent earlier, but the session is closed or the entity was evicted. Changes are no longer tracked. |
| **Removed** | Marked for deletion; the `DELETE` is issued at `flush()` or commit. |

**State properties:**

| State | Managed by Hibernate? | Database row exists? |
| --- | --- | --- |
| **Transient** | No | No |
| **Persistent** | Yes | Usually — but not between `persist()` and the flush, unless an `IDENTITY` generator forced the `INSERT` immediately |
| **Detached** | No | Yes |
| **Removed** | Yes (until the delete is flushed) | Still there until flush/commit |

**State transitions:**

| Method | Transitions | Description |
| --- | --- | --- |
| `persist()` | Transient → Persistent | Makes a new object managed and schedules the `INSERT`. |
| `merge()` | Detached → *a managed copy* | Copies state onto a managed instance and **returns it**. The argument stays detached. |
| `remove()` | Persistent → Removed | Marks the entity for deletion. |
| `detach()` / `evict()` | Persistent → Detached | Drops the entity from the persistence context; no DB operation. |
| `refresh()` | Persistent → Persistent | Reloads state from the DB, discarding in-memory changes. |
| `clear()` | All Persistent → Detached | Empties the persistence context. |
| `close()` | All Persistent → Detached | Ends the session. |

**The `merge()` trap, written out:**

```java
Order detached = new Order(existingId, "UPDATED");

em.merge(detached);           // WRONG: return value dropped
detached.setStatus("X");      // changes nothing - still detached

Order managed = em.merge(detached);   // RIGHT
managed.setStatus("X");               // dirty-checked and flushed
```

**Legacy Hibernate methods.** `save()`, `update()`, `saveOrUpdate()` and `delete()` are
Hibernate's own, pre-JPA API and are deprecated in Hibernate 6 in favour of `persist()`,
`merge()` and `remove()`. Differences worth naming if asked: `save()` returns the generated
identifier and may hit the DB immediately; `persist()` returns `void` and may defer the
`INSERT` to the flush.

## 2. What is the N+1 select problem, and how do you solve it?

**The mechanism.** You run one query that returns N rows, and then Hibernate runs one more
query *per row* to resolve an association. Total: 1 + N.

```java
List<Order> orders = orderRepo.findAll();      // 1 query, returns 100 orders
for (Order o : orders) {
    o.getItems().size();                       // 100 more queries, one per ORDER
}
```

`SELECT * FROM orders` — then `SELECT * FROM items WHERE order_id = ?` one hundred times. At
10 ms per round trip that is one fast query and one second of latency.

**The counter-intuitive part:** changing the association to `FetchType.EAGER` does **not** fix
this. JPQL and `findAll()` build the SQL from the query, not from the fetch plan, so an EAGER
`@ManyToOne` is resolved by a secondary select per row anyway. EAGER guarantees N+1 happens
*always*, instead of only when you touch the association.

**Solutions, and when each one fits:**

| Fix | How | Watch out for |
| --- | --- | --- |
| `JOIN FETCH` | `select o from Order o join fetch o.items where ...` | Duplicate parents (use `distinct` or a `Set`); breaks pagination |
| `@EntityGraph` | `@EntityGraph(attributePaths = "items")` on the repository method | Same pagination caveat; cleaner than JPQL for Spring Data |
| Batch fetching | `@BatchSize(size = 50)` on the collection, or `hibernate.default_batch_fetch_size=50` globally | Turns 1+N into 1+(N/50) with an `IN (...)` per batch — the best general-purpose default |
| Subselect fetching | `@Fetch(FetchMode.SUBSELECT)` | Re-runs the original query as a subselect; good for one big collection |
| DTO projection | `select new com.x.OrderView(o.id, o.status) from Order o` | Fastest of all — you never load the entity, so nothing can lazy-load later |

**Two traps that follow the fix:**

- **`JOIN FETCH` plus pagination.** Hibernate logs `HHH000104: firstResult/maxResults specified
  with collection fetch; applying in memory` and then loads *every* matching row before
  paginating in Java. Fix by paginating the ids first, then fetching the collection for that
  page — or by using `@BatchSize` instead.
- **`MultipleBagFetchException`.** You cannot `join fetch` two `List` collections in one query
  (a "bag" has no order, so the cartesian product is unresolvable). Change one to a `Set`, or
  fetch the second with `@BatchSize`.

**How to detect it honestly:** set
`spring.jpa.properties.hibernate.generate_statistics=true`, or assert the query count in a test
with datasource-proxy. Never conclude "the N+1 is gone" from a page that feels fast — count the
statements.

## 3. What are the different types of caching available in Hibernate?

| Level | Scope | Optional? | Caches |
| --- | --- | --- | --- |
| **First-level (L1)** | One `Session` / `EntityManager` | No — always on, cannot be disabled | Entity instances by id |
| **Second-level (L2)** | `SessionFactory`, shared by all sessions | Yes | Entities, collections, natural ids |
| **Query cache** | `SessionFactory` | Yes, and it needs L2 | Query result **identifiers**, not rows — the entities are then read from L2 |
| **Natural id cache** | `SessionFactory` | Yes | Lookups by `@NaturalId` (email, username) |

The query cache is the one to be careful with: it caches ids, so a hit still needs the entities,
and its region is invalidated whenever *any* table in the query changes. On a write-heavy table
it can be slower than no cache at all.

## 4. How does Hibernate's first-level cache work internally?

- It **is** the `PersistenceContext` inside the `Session`. "First-level cache" and "persistence
  context" are two names for the same map of `EntityKey` → entity instance.
- `em.find()` / `session.get()` check that map before going to the database. Calling `find()`
  for the same id twice in one transaction issues one query and returns the **same object
  reference** — that identity guarantee is the point, more than the saved round trip.
- On `flush()`, Hibernate compares each managed entity against the snapshot it took at load
  time (dirty checking) and issues the `UPDATE`s.
- When the session is closed or cleared, it is gone. It is never shared between threads or
  requests.

**A JPQL query does not consult it for the result set.** `select o from Order o` always goes to
the database; only the *returned* rows are then reconciled against the persistence context, so
an already-managed instance wins over the freshly-read row.

**`find()` vs `getReference()` (Hibernate's `get()` vs `load()`):**

| Call | Hits the DB now? | If the row is missing |
| --- | --- | --- |
| `em.find(Order.class, id)` | Yes (unless cached) | Returns `null` |
| `em.getReference(Order.class, id)` | No — returns a proxy | Throws `EntityNotFoundException` on first access |

`getReference()` is the right call when you only need to set a foreign key
(`child.setParent(em.getReference(Parent.class, id))`) and never read the parent's fields.

**Follow-up:** clear it explicitly with `session.clear()`, or evict one entity with
`session.evict(entity)` — necessary in batch jobs, where an unbounded persistence context is a
memory leak and makes every flush slower.

## 5. What's the difference between `session.clear()`, `session.evict()`, and `session.refresh()`?

| Call | Effect | Pending changes to that entity |
| --- | --- | --- |
| `evict(entity)` / `detach(entity)` | Removes **one** entity from the persistence context | Silently discarded — never flushed |
| `clear()` | Removes **all** entities | All silently discarded |
| `refresh(entity)` | Re-reads the row and overwrites the in-memory state | Overwritten by the database values |

The shared danger: `evict` and `clear` throw nothing away noisily. Un-flushed modifications to
the evicted entities simply never reach the database.

## 6. What is a Second-Level Cache and how is it configured?

- A shared cache across sessions, owned by the `SessionFactory`.
- It requires an external provider. Hibernate ships `hibernate-jcache` (use Ehcache 3 or any
  JSR-107 provider behind it) and `hibernate-infinispan`; Redis and Hazelcast are available as
  third-party region factories.

**Modern configuration (Hibernate 5.3+ / 6.x, via JCache):**

```properties
hibernate.cache.use_second_level_cache=true
hibernate.cache.region.factory_class=jcache
hibernate.javax.cache.provider=org.ehcache.jsr107.EhcacheCachingProvider
hibernate.cache.use_query_cache=true
```

> **Correction.** The old value here was
> `org.hibernate.cache.ehcache.EhCacheRegionFactory`, the Ehcache 2 region factory. Ehcache 2
> support was removed after Hibernate 5.2; on Hibernate 6 that class does not exist and the
> app fails to start.

In Spring Boot, the equivalent properties are prefixed:

```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=jcache
```

- Then mark the entity as cacheable:

```java
@Entity
@jakarta.persistence.Cacheable                          // JPA: this entity may be cached
@org.hibernate.annotations.Cache(                       // Hibernate: how to cache it
        usage = CacheConcurrencyStrategy.READ_WRITE)
public class Country { ... }
```

Both annotations are needed in a JPA setup: `@Cacheable` opts the entity in (subject to
`jakarta.persistence.sharedCache.mode`), and Hibernate's `@Cache` chooses the concurrency
strategy. Note this `@Cacheable` is **`jakarta.persistence.Cacheable`** — importing Spring's
`@Cacheable` here does nothing for Hibernate.

**What the L2 cache is good for:** small, read-mostly reference data (countries, currencies,
product categories). It is a poor fit for hot transactional tables, where invalidation traffic
and staleness cost more than the query you saved.

## 7. What are the different CacheConcurrencyStrategy options in Hibernate?

| Strategy | How it behaves | Use case |
| --- | --- | --- |
| `READ_ONLY` | No updates allowed; modifying a cached instance throws | Immutable reference data |
| `NONSTRICT_READ_WRITE` | No locking. The entry is **invalidated after** the transaction commits, so there is a window in which a stale value is served | Rarely updated data where brief staleness is acceptable |
| `READ_WRITE` | Soft-locks the entry during the write and updates it after commit; readers see the DB value while the soft lock is held | Medium update frequency, read-committed semantics |
| `TRANSACTIONAL` | Full transactional cache; needs JTA and a transactional provider such as Infinispan | Distributed caching with XA-level guarantees |

`NONSTRICT_READ_WRITE` gives **stale** reads, not dirty reads — it never exposes uncommitted
data, it just may hand back the previous committed value for a moment.

## 8. Suppose two concurrent transactions update the same cached entity: what could go wrong?

Depends on the concurrency strategy:

- With `READ_WRITE`, Hibernate soft-locks the cache entry for the duration of the write.
  Concurrent readers are not served the cached value while the lock is held — they go to the
  database — so they never see a half-written entry.
- With `NONSTRICT_READ_WRITE`, there is no lock. Between the database commit and the cache
  invalidation, another session can still read the old value.
- With `READ_ONLY`, an update attempt throws (`UnsupportedOperationException`, "Can't write to
  a readonly object").

**The point the question is really testing:** none of this protects you from a **lost update**.
Two transactions that both read `quantity = 10`, both subtract 1, and both write 9 have lost
one decrement — with or without an L2 cache. The fix is optimistic locking with `@Version` (or
a pessimistic lock), not a cache setting:

```java
@Entity
public class Stock {
    @Id Long id;
    @Version Long version;   // UPDATE ... WHERE id = ? AND version = ?
    int quantity;            // 0 rows updated -> OptimisticLockException
}
```

**Real-world rule:** pick the strategy from update frequency and staleness tolerance, and pick
`@Version` separately for write correctness. They solve different problems.

## 9. What is `LazyInitializationException`, and how do you really fix it?

**What lazy actually is.** A lazy `@ManyToOne` is a *proxy* — a generated subclass with only
the id populated. A lazy collection is a `PersistentBag`/`PersistentSet` placeholder. Both
initialise themselves by running a query **through the session that created them**.

**The exception.** Touch either one after that session is closed, and there is no session to
query with:

```
org.hibernate.LazyInitializationException:
    could not initialize proxy [Order#42] - no Session
```

Typically: a `@Transactional` service method returns an entity, the transaction (and the
persistence context) ends at the return, and then Jackson serialises `order.getItems()` in the
controller.

**Default fetch types — worth memorising, because they are asymmetric:**

| Association | Default |
| --- | --- |
| `@OneToMany` | `LAZY` |
| `@ManyToMany` | `LAZY` |
| `@ManyToOne` | **`EAGER`** |
| `@OneToOne` | **`EAGER`** |

Almost every experienced team overrides the two `EAGER` defaults to `LAZY`. One caveat: a
nullable `@OneToOne` on the *inverse* (non-owning) side cannot be made lazy by proxying —
Hibernate must query to learn whether the row exists at all, so it stays eager unless you use
bytecode enhancement or map it as the owning side.

**Fixes, best first:**

| Fix | What it costs |
| --- | --- |
| Fetch what you need: `JOIN FETCH` or `@EntityGraph` | Nothing — this is the correct answer |
| Return a **DTO**, not the entity | Nothing; also stops accidental lazy loads at serialisation time forever |
| `Hibernate.initialize(order.getItems())` inside the transaction | Explicit but easy to forget on the next field |
| Widen the transaction to cover the work | Holds a DB connection longer |
| `spring.jpa.open-in-view=true` (Spring Boot's **default**) | Hides the bug; see below |
| Make the association `EAGER` | Worst answer — it loads the collection on *every* query, N+1 included |

**Open Session In View.** Spring Boot keeps the `EntityManager` open for the whole HTTP request
by default, which is exactly why lazy loading "works in the app but fails in tests". The cost
is that a database connection is pinned for the entire request — including view rendering and
any downstream HTTP call — and that queries fire from the view layer where nobody is counting
them. Spring Boot logs a warning about it at startup. Set `spring.jpa.open-in-view=false` and
fix the fetches properly.

## 10. How do you write `equals` and `hashCode` for a JPA entity?

**Why it matters.** Entities go into `HashSet`s (`@OneToMany Set<Item> items`), get compared
across detach/merge boundaries, and are used as map keys. The same row loaded in two sessions
gives two different Java objects, so the default identity `equals` is wrong the moment an
entity leaves its session.

**The trap with a generated id.** An IDE-generated `hashCode()` over the `@Id` field is broken
for any entity whose id is assigned at insert:

```java
Set<Order> set = new HashSet<>();
Order o = new Order();     // id == null  -> hashCode() = 0
set.add(o);                // lands in bucket 0
em.persist(o);
em.flush();                // id is now 42 -> hashCode() changes
set.contains(o);           // false. The object is lost in its own set.
```

A hash code must never change while the object is in a hash-based collection.

**The three workable options:**

| Approach | `equals` compares | `hashCode` returns | When to use |
| --- | --- | --- | --- |
| **Business / natural key** | The unique business field (order number, ISBN, email) | That field's hash | Best, whenever a genuine natural key exists |
| **Application-assigned UUID** | The UUID, set in the constructor | The UUID's hash | Best when there is no natural key — the id exists before the insert, so nothing changes |
| **Generated id + constant hash** | The id, treating two nulls as *not* equal | A **constant**, e.g. `getClass().hashCode()` | The pragmatic fallback for existing `IDENTITY`/sequence entities |

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    // instanceof, NOT getClass() - a Hibernate proxy is a generated subclass
    if (!(o instanceof Order other)) return false;
    return id != null && id.equals(other.getId());   // use the GETTER, not the field
}

@Override
public int hashCode() {
    return getClass().hashCode();   // constant: stable across the id being assigned
}
```

**Three rules that come out of that snippet:**

- Use `instanceof` (or `Hibernate.getClass(o)`), never `getClass() != o.getClass()` — a lazy
  proxy is `Order$HibernateProxy`, so the strict check says a proxy and its own entity are
  different objects.
- Read the other object's state through its **getter**, never its field. Reading a proxy's
  field directly returns `null`, because only the getter triggers initialisation.
- Never include mutable fields, and never include a lazy association — a `toString()` or
  `equals()` that walks `getItems()` triggers a query, or a `LazyInitializationException`.

**Lombok on entities:** `@Data` and `@EqualsAndHashCode` generate exactly the broken version —
all fields, lazy associations included, plus a `toString()` that walks them. If you use Lombok
on entities, restrict it to `@Getter`/`@Setter` and write `equals`, `hashCode` and `toString`
by hand.

## Snippet: mapping a `jsonb` column with Hibernate 6

Transcribed from a photo the owner kept at
[`08-Reference/images/jpa-entity-jsonb-column-snippet.jpg`](../../08-Reference/images/jpa-entity-jsonb-column-snippet.jpg).

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

- `@JdbcTypeCode(SqlTypes.JSON)` (Hibernate 6) maps a POJO to a PostgreSQL `jsonb` column
  without a custom `UserType`. On Hibernate 5 this needed a third-party type such as
  `hibernate-types`.
- `@GeneratedValue(strategy = GenerationType.UUID)` is the JPA 3.1 way to generate UUID keys.
  Because the id exists before the insert, this entity can safely hash on its id — see
  section 10.
- `@Enumerated(EnumType.STRING)` rather than the default `ORDINAL`: ordinal storage breaks the
  moment someone inserts a new constant in the middle of the enum.
- One thing to test rather than assume: whether mutating a field *inside* `editParams` is
  picked up by dirty checking depends on the mutability plan Hibernate uses for the mapped
  type. The safe habit is to **assign a new instance** (`setEditParams(updated)`) instead of
  mutating in place. Unverified for this exact mapping — confirm with
  `hibernate.generate_statistics` or by checking the emitted SQL before relying on it.
