# Hibernate — Interview Q&A

This file covers what Hibernate **adds to** or **does underneath** JPA: sessions,
caching, dirty checking, flushing, fetching, batching and the Hibernate-only
annotations. Questions about the JPA specification itself — entity states,
relationships, cascades, N+1, JPQL, locking, Spring Data — are in
[03_JPA_Spring_Data_QA.md](03_JPA_Spring_Data_QA.md).

Version facts were checked against the jars in the local Maven repository:

| Spring Boot | Hibernate ORM | Jakarta Persistence (JPA) |
| --- | --- | --- |
| 3.5.7 | 6.6.33 | 3.1.0 |
| 4.0.3 | 7.2.4 | 3.2.0 |

Older, longer write-ups on caching, `LazyInitializationException` and
`equals`/`hashCode`: [Hibernate_JPA_QA.md](../05-Spring-Microservices/notes/Hibernate_JPA_QA.md).

## Weight legend

| Mark | What it means | How the answer is written |
| --- | --- | --- |
| ★★★ | Asked in almost every Java backend round, with follow-ups | Answer, mechanism, code, traps |
| ★★ | Asked often | Answer and one example or table |
| ★ | Occasional, or a senior-level bonus | Two to four lines |

## Contents

| # | Question | Weight | One-line answer |
| --- | --- | --- | --- |
| 1 | [Hibernate vs JPA](#1-what-is-hibernate-and-how-does-it-relate-to-jpa) | ★★★ | JPA is the contract, Hibernate the engine |
| 2 | [SessionFactory vs Session](#2-sessionfactory-vs-session) | ★★★ | One per DB and thread-safe vs one per transaction and not |
| 3 | [First-level cache](#3-the-first-level-cache) | ★★★ | Per-session map of id → managed instance; always on |
| 4 | [Dirty checking](#4-dirty-checking) | ★★★ | Compares each entity with a snapshot at flush; no `update()` needed |
| 5 | [Flush](#5-flush-when-it-happens-and-why-the-order-matters) | ★★★ | Sends SQL before commit and before overlapping queries; inserts before deletes |
| 6 | [find vs getReference](#6-find-vs-getreference) | ★★★ | Loads now vs proxy with no SQL |
| 7 | [save, persist, merge, update](#7-save-vs-persist-vs-merge-vs-update) | ★★★ | Use persist, merge, remove — the rest are gone in Hibernate 7 |
| 8 | [LazyInitializationException](#8-lazyinitializationexception) | ★★★ | Lazy data touched after the session closed; fetch it in the query |
| 9 | [Open Session in View](#9-open-session-in-view) | ★★ | On by default in Boot; turn it off |
| 10 | [FetchType vs FetchMode](#10-fetchtype-vs-fetchmode-and-batch-fetching) | ★★★ | When vs how; `@BatchSize` turns N into N/size |
| 11 | [MultipleBagFetchException](#11-multiplebagfetchexception) | ★★ | Two `List` fetch joins; split into two queries |
| 12 | [Second-level cache](#12-the-second-level-cache) | ★★★ | Shared per SessionFactory; only for read-mostly data |
| 13 | [Query cache](#13-the-query-cache) | ★★ | Any write to the table wipes it |
| 14 | [ID generators and batching](#14-id-generators-and-their-effect-on-batching) | ★★★ | IDENTITY disables insert batching; SEQUENCE + pooled doesn't |
| 15 | [JDBC batching](#15-jdbc-batching) | ★★★ | `batch_size`, `order_inserts`, flush + clear every N |
| 16 | [Bulk updates and deletes](#16-bulk-updates-and-deletes) | ★★ | One SQL; skips the persistence context, callbacks, cascades |
| 17 | [Lazy proxies](#17-how-lazy-proxies-work-and-where-they-bite) | ★★ | Runtime subclass; `getClass()` and `final` break it |
| 18 | [equals, hashCode, Lombok](#18-equals-hashcode-and-lombok-on-entities) | ★★ | Business key or id with constant hashCode; never `@Data` |
| 19 | [DynamicUpdate](#19-dynamicupdate-and-dynamicinsert) | ★ | UPDATE only changed columns |
| 20 | [Hibernate-only annotations](#20-hibernate-only-annotations-worth-knowing) | ★★ | `@NaturalId`, `@Formula`, `@SQLRestriction`, `@Filter`… |
| 21 | [Soft delete](#21-soft-delete) | ★★ | `@SoftDelete`, or `@SQLDelete` + `@SQLRestriction` |
| 22 | [StatelessSession, large data](#22-statelesssession-and-processing-large-data) | ★★ | No context, no dirty checking — for bulk work |
| 23 | [Seeing SQL, catching N+1](#23-seeing-the-sql-and-catching-n1-in-tests) | ★★ | `org.hibernate.SQL` logger + statement-count tests |
| 24 | [Hibernate 6 and 7 changes](#24-what-changed-in-hibernate-6-and-7) | ★★ | Jakarta, new query engine, removed legacy API |

---

## 1. What is Hibernate and how does it relate to JPA?

**Weight:** ★★★

**Answer:** Hibernate is an **ORM** (object-relational mapper). It maps Java classes
to tables, objects to rows and object references to foreign keys. It generates the
SQL, tracks changes, loads associations lazily and caches data.

- **JPA (Jakarta Persistence)** is a *specification*: the annotations in
  `jakarta.persistence.*` and the `EntityManager` API. It is a contract, not running
  code.
- **Hibernate** is the most used *implementation* of that contract (EclipseLink is
  another). It also has its own native API (`Session`) and features beyond the
  specification: `@BatchSize`, `@NaturalId`, `@SoftDelete`, filters,
  `StatelessSession`, multi-tenancy, Envers auditing.
- **Spring Data JPA** sits on top and generates repositories that call the
  `EntityManager`.

**Rule to state:** code against JPA by default. Use a Hibernate-only feature
deliberately, and know that you did.

---

## 2. SessionFactory vs Session

**Weight:** ★★★

| | `SessionFactory` (JPA: `EntityManagerFactory`) | `Session` (JPA: `EntityManager`) |
| --- | --- | --- |
| How many | One per database, built at startup | One per unit of work — in Spring, one per transaction |
| Thread-safe | Yes | **No** — never share between threads |
| Cost to create | Expensive: reads all mappings, builds the metamodel | Cheap |
| Holds | Second-level cache, metamodel, connection provider, statistics | First-level cache (persistence context), pending SQL, the JDBC connection while in use |

- Since Hibernate 6, `Session` extends `EntityManager` and `SessionFactory` extends
  `EntityManagerFactory`. Get the native one with `entityManager.unwrap(Session.class)`.
- **Spring detail:** the `EntityManager` you inject into a singleton is a shared,
  thread-safe **proxy**. Each call is routed to the real `EntityManager` bound to the
  current thread's transaction. That is why injecting it into a singleton is safe.

---

## 3. The first-level cache

**Weight:** ★★★

**Answer:** the first-level cache *is* the persistence context: a map inside each
`Session`, keyed by (entity type, id), holding the managed instance. It is always on
and cannot be turned off. It lives as long as the `Session` — in Spring, the
transaction (or the whole request, if Open Session in View is on).

**What it gives you:**

1. At most one SELECT per id per session for `find()`.
2. **Identity:** two lookups of the same id return the *same Java object*.
3. Repeatable reads at the application level, inside one session.

```java
@Transactional
public void demo() {
    Order a = em.find(Order.class, 1L);   // SELECT
    Order b = em.find(Order.class, 1L);   // no SQL — served from the context
    assert a == b;                        // same instance
}
```

**Trap 1 — queries don't refresh managed entities.** A JPQL query always runs SQL.
But for any row whose id is already in the context, Hibernate returns the existing
instance and **throws away the fresh column values**. If another transaction changed
the row, you still see your old copy. Use `em.refresh(entity)` to re-read it.

**Trap 2 — memory in batch jobs.** Loading a million entities in one transaction keeps
all of them, plus their snapshots, in memory. That leads to `OutOfMemoryError`, and
every flush gets slower because dirty checking scans them all. Flush and clear every
N rows (Q15), or use a `StatelessSession` (Q22).

---

## 4. Dirty checking

**Weight:** ★★★

**Answer:** when Hibernate loads an entity, it keeps a copy of its column values (the
*loaded state*). At flush, it compares every managed entity with its copy. Any
difference becomes an UPDATE. You never call `update()` or `save()` for a managed
entity.

```java
@Transactional
public void rename(long id, String name) {
    Customer c = customerRepository.findById(id).orElseThrow();
    c.setName(name);       // no save() — the UPDATE is sent at commit
}
```

**Details interviewers probe:**

- The default UPDATE sets **all** columns. The SQL string is then identical every
  time, which helps statement caching and batching. `@DynamicUpdate` sends only the
  changed columns (Q19).
- **Cost:** number of managed entities × number of fields, on **every** flush — and a
  flush can happen before each query (Q5). A large persistence context makes every
  query in that transaction slower.
- **Read-only optimisation:** `@Transactional(readOnly = true)` makes Spring set the
  Hibernate session to read-only and the flush mode to `MANUAL`. No snapshots are
  kept and no dirty check runs. `@Immutable` entities get the same benefit.
- **Alternative:** bytecode enhancement with dirty tracking — the entity records which
  fields changed, so Hibernate need not compare.

**Trap:** changing a managed entity "temporarily" — for example masking a field before
returning it from the controller — gets written to the database at commit. Map to a
DTO instead.

---

## 5. Flush: when it happens and why the order matters

**Weight:** ★★★

**Flush vs commit:** a flush **sends** the pending INSERT/UPDATE/DELETE statements to
the database *inside* the current transaction. A commit makes them permanent and
visible to others. A flushed change can still be rolled back.

**When it happens (default `FlushMode.AUTO`):**

1. Before the transaction commits.
2. Before a query that might read the pending changes. For JPQL/HQL, Hibernate checks
   whether the query's tables overlap the pending changes. For native SQL it cannot
   tell which tables are read, so it flushes everything first.
3. When you call `flush()` yourself.

| FlushMode | Flushes |
| --- | --- |
| `AUTO` (default) | Before commit and before overlapping queries |
| `COMMIT` | Only at commit — queries may miss your own pending changes |
| `MANUAL` | Only when you call `flush()` — used for read-only transactions |
| `ALWAYS` | Before every query |

**The order of statements inside a flush is not your call order.** Hibernate runs,
roughly: inserts → updates → collection changes → **deletes last**.

**The classic bug that follows from it:**

```java
@Transactional
public void replaceCode(PromoCode old) {
    promoRepository.delete(old);                          // DELETE queued
    promoRepository.save(new PromoCode(old.getCode()));   // INSERT queued
}   // flush runs the INSERT first → unique constraint violation
```

Fix: call `promoRepository.flush()` right after the delete, or update the existing
row instead of deleting and re-inserting it.

---

## 6. find vs getReference

**Weight:** ★★★

| | `find()` (Hibernate also has `get()`) | `getReference()` (old Hibernate: `load()`) |
| --- | --- | --- |
| SQL | Immediately, unless the entity is already in the context | None, until you touch a field other than the id |
| Returns | The real entity, or `null` | A proxy (or the real instance if already loaded) |
| Row missing | Returns `null` | `EntityNotFoundException` on first access |
| Use when | You need the data | You only need it as a foreign key |

```java
@Transactional
public void addComment(long postId, String text) {
    Post post = em.getReference(Post.class, postId);   // no SELECT
    em.persist(new Comment(post, text));                // INSERT with post_id set
}
```

- Spring Data: `getReferenceById()` — it replaced the deprecated `getOne()` and
  `getById()`.
- Hibernate 7 removed `Session.load(Class, id)` (checked in the 7.2.4 jar: only
  `load(Object, Object)`, which fills an instance you pass in, remains). `get()` and
  `find()` both still exist.

---

## 7. save vs persist vs merge vs update

**Weight:** ★★★

| Method | From | Takes | What it does |
| --- | --- | --- | --- |
| `persist(e)` | JPA | A new entity | Makes **that instance** managed; INSERT at flush (IDENTITY ids: immediately). A detached entity throws "detached entity passed to persist". Returns nothing |
| `merge(e)` | JPA | New or detached | Copies the state onto a managed instance (SELECTs it if needed) and **returns that managed copy**. The argument stays detached |
| `remove(e)` | JPA | A managed entity | DELETE at flush |
| `save(e)` | Hibernate | A new entity | Like persist, but returns the id. **Removed in Hibernate 7** |
| `update(e)` | Hibernate | A detached entity | Re-attaches **that instance**. Fails with `NonUniqueObjectException` if another instance with the same id is already in the session. **Removed in Hibernate 7** |
| `saveOrUpdate(e)` | Hibernate | Either | save if new, update if detached. **Removed in Hibernate 7** |

The Hibernate 7 removals of `save`, `update`, `saveOrUpdate` and `delete` were checked
in the 7.2.4 jar; they were deprecated in 6. Use `persist`, `merge` and `remove`.

**The merge trap:**

```java
Customer detached = request.toEntity();     // has an id
em.merge(detached);
detached.setTier("GOLD");                   // LOST — detached is not managed

Customer managed = em.merge(detached);      // correct: keep the return value
managed.setTier("GOLD");                    // tracked, saved at flush
```

Spring Data's `save()` chooses between `persist` and `merge` for you. Its "is this
entity new?" rule has its own trap — see
[JPA Q24](03_JPA_Spring_Data_QA.md#24-save-persist-or-merge-and-the-isnew-trap).

---

## 8. LazyInitializationException

**Weight:** ★★★

**What you see:**

```text
could not initialize proxy [com.shop.Customer#42] - no Session
failed to lazily initialize a collection of role: com.shop.Order.items ... - no Session
```

**Mechanism:** a lazy association is a proxy (or a `PersistentCollection`) that needs
an open session to run its SELECT. The code touches it after the session has closed —
usually in the controller, during JSON serialization, in `toString()`, or in an
`@Async` thread.

```java
@Transactional(readOnly = true)
public Order get(long id) {
    return orderRepository.findById(id).orElseThrow();
}

// In the controller, after the transaction has ended:
order.getItems().size();     // LazyInitializationException (when OSIV is off)
```

**Correct fixes:**

| Fix | When to use it |
| --- | --- |
| Load what the use case needs in the query: `JOIN FETCH` or `@EntityGraph` | You need the entities |
| Build a DTO inside the transactional service and return that | API responses — the best default |
| `Hibernate.initialize(order.getItems())` inside the transaction | A quick fix; still one extra query |

**Fixes that only hide it:**

- `FetchType.EAGER` — loads the data everywhere, even where it is not needed, and
  causes N+1 elsewhere.
- Open Session in View (Q9).
- `hibernate.enable_lazy_load_no_trans=true` — opens a temporary session for each
  lazy access. That is N+1 in disguise, with no transactional consistency.

---

## 9. Open Session in View

**Weight:** ★★

**What it is:** `OpenEntityManagerInViewInterceptor` keeps the `EntityManager` open
for the whole HTTP request, so lazy loading works in controllers and during JSON
serialization.

**Boot default:** `spring.jpa.open-in-view=true` (checked in Boot 3.5.7 and 4.0.3),
with a warning logged at startup saying so.

**Why to turn it off (`spring.jpa.open-in-view=false`):**

1. Once the `EntityManager` has a database connection, it keeps it until the request
   ends — through serialization and slow downstream calls. Under load the connection
   pool runs out.
2. Lazy loads in the controller or serializer run outside any transaction, one query
   each. N+1 hides in the web layer where nobody looks for it.
3. Missing fetch plans stay hidden until production traffic shows them.

With it off, `LazyInitializationException` shows up during development. That is the
point: it tells you to fix the fetch plan in the service.

---

## 10. FetchType vs FetchMode and batch fetching

**Weight:** ★★★

- **FetchType** (JPA) says *when*: `LAZY` or `EAGER`.
- **FetchMode** (Hibernate `@Fetch`) says *how*: `JOIN`, `SELECT` or `SUBSELECT`.

**JPA defaults:** `@ManyToOne` and `@OneToOne` are EAGER; `@OneToMany`,
`@ManyToMany` and `@ElementCollection` are LAZY. **Senior rule:** make every
association LAZY and decide the fetch plan per query.

**EAGER does not mean JOIN.** For a JPQL query, an EAGER `@ManyToOne` is loaded by a
separate SELECT per row — N+1 — even if you never use the field.

**Hibernate tools that soften N+1 for lazy associations** (100 orders, each with a
lazy `customer`):

| Setting | What it does | Statements |
| --- | --- | --- |
| Nothing | One SELECT per proxy touched | 1 + 100 |
| `@BatchSize(size = 25)` on the entity or collection | Loads up to 25 pending proxies with `WHERE id IN (...)` | 1 + 4 |
| `hibernate.default_batch_fetch_size=25` | The same, for every association | 1 + 4 |
| `@Fetch(FetchMode.SUBSELECT)` on a collection | Loads every owner's collection with one query that repeats the original as a subquery | 1 + 1 |
| `JOIN FETCH` or an entity graph | One joined query | 1 |

Batch fetching is a **safety net**, not a fetch plan. The full N+1 answer is in
[JPA Q8](03_JPA_Spring_Data_QA.md#8-the-n1-select-problem).

---

## 11. MultipleBagFetchException

**Weight:** ★★

**Message:** `cannot simultaneously fetch multiple bags`. A *bag* is a `List` mapped
without `@OrderColumn` — unordered, duplicates allowed.

**Cause:** `JOIN FETCH` of two `List` collections in one query produces a cartesian
product. An order with 5 items and 10 payments returns 50 rows. For bags Hibernate
can't tell real duplicates from join duplicates, so it refuses.

**Wrong fix:** change both to `Set`. The exception goes away, but the cartesian
product stays — 50 rows per order, which gets very slow on real data.

**Right fix:** one collection per query, in the same transaction. The second query
fills the other collection on the **same managed instances**:

```java
List<Order> orders = em.createQuery("""
        select o from Order o
        left join fetch o.items
        where o.id in :ids""", Order.class)
    .setParameter("ids", ids)
    .getResultList();

em.createQuery("""
        select o from Order o
        left join fetch o.payments
        where o in :orders""", Order.class)
    .setParameter("orders", orders)
    .getResultList();      // fills o.payments on the orders above
```

Or put `@BatchSize` on the second collection and let it load lazily in batches.

---

## 12. The second-level cache

**Weight:** ★★★

**Answer:** a cache owned by the `SessionFactory`, shared by all sessions and threads
(per JVM, unless the provider is distributed).

- **What it stores:** entity state in "disassembled" form — arrays of column values
  keyed by id — not the Java objects.
- **Collection caches store only the element ids.** The element entity must be cached
  too, or every element is loaded from the database one by one.
- **Lookup order for `find()`:** first-level cache → second-level cache → database.
- **Not used by** JPQL/HQL or native queries. They always go to the database (only the
  query cache, Q13, helps there).

**Setup in Boot** (JCache provider such as Ehcache 3; needs the `hibernate-jcache`
module plus the provider jar):

```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=jcache
spring.jpa.properties.jakarta.persistence.sharedCache.mode=ENABLE_SELECTIVE
```

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Country { ... }
```

| Concurrency strategy | Use for | On update |
| --- | --- | --- |
| `READ_ONLY` | Reference data that never changes | Updating throws |
| `NONSTRICT_READ_WRITE` | Rarely changed; brief staleness acceptable | Entry invalidated after commit; short stale window |
| `READ_WRITE` | Read-mostly data that must stay consistent | Entry soft-locked during the update |
| `TRANSACTIONAL` | JTA with a transactional cache (Infinispan) | Cache joins the XA transaction |

**When NOT to use it:**

- Write-heavy data — you pay invalidation cost for few hits.
- Data also written by other services or SQL scripts. The cache never hears about
  those writes and serves stale rows.
- Several app instances with a local, non-replicated cache. Each node serves its own
  stale copy.

**Senior answer:** in microservices, prefer explicit service-level caching (Spring
Cache with Redis or Caffeine) with a deliberate TTL and eviction. Keep the
second-level cache for immutable reference data.

---

## 13. The query cache

**Weight:** ★★

- **Stores:** query text + parameter values → the list of result ids (or scalar values).
- **Invalidation:** any insert, update or delete on any table the query reads
  invalidates **every** cached query on that table. On a table written every second,
  the hit rate is near zero and you still pay the bookkeeping.
- **Needs the entities in the second-level cache too**, or each returned id becomes a
  separate SELECT.
- **Enable:** `hibernate.cache.use_query_cache=true`, then per query the hint
  `org.hibernate.cacheable=true` (Spring Data:
  `@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))`).
- **Good for:** read-mostly lookups with repeated parameters — country lists,
  configuration tables.

---

## 14. ID generators and their effect on batching

**Weight:** ★★★

| Strategy | How the id is obtained | JDBC insert batching | Notes |
| --- | --- | --- | --- |
| `IDENTITY` | Database auto-increment; known only after the INSERT | **Disabled** — each INSERT runs at once to get its id | Usual on MySQL; simplest |
| `SEQUENCE` | `nextval` before the INSERT | Works | Pooled optimizer: one `nextval` per `allocationSize` ids (JPA default 50) |
| `TABLE` | A row in a table, locked and incremented | Works, but the row lock is a bottleneck | Avoid |
| `UUID` | Generated in Java | Works | Random v4 values scatter B-tree inserts; time-ordered v7 appends |
| `AUTO` | Hibernate picks | Depends | On MySQL it may pick a table-backed sequence — be explicit |

**Details:**

- Keep `allocationSize` equal to the database sequence's `INCREMENT BY`, or ids can
  collide or jump.
- Hibernate 6 names default sequences per entity (`order_seq`), not one shared
  `hibernate_sequence`.
- Time-ordered UUIDs: Hibernate 7 has `@UuidGenerator(style = Style.VERSION_7)`.
  Checked: 7.2.4 has `VERSION_6` and `VERSION_7`; 6.6.33 has only `AUTO`, `RANDOM`
  and `TIME`. JPA 3.1+ also has `@GeneratedValue(strategy = GenerationType.UUID)`.

**Recommendation to give:** PostgreSQL or Oracle → `SEQUENCE` with the pooled
optimizer. MySQL → `IDENTITY` is acceptable; if insert batching matters, generate
time-ordered UUIDs in the application.

---

## 15. JDBC batching

**Weight:** ★★★

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Driver rewrites a batch into multi-row statements:
# PostgreSQL: jdbc:postgresql://host/shop?reWriteBatchedInserts=true
# MySQL:      jdbc:mysql://host/shop?rewriteBatchedStatements=true
```

- `order_inserts` groups statements by table. Without it, persisting
  Order, Item, Order, Item… breaks the batch at every table switch.
- Batching does **not** apply to inserts with `IDENTITY` ids (Q14).

**The batch loop — flush to send, clear to free memory:**

```java
@Transactional
public void importProducts(List<ProductRow> rows) {
    int batchSize = 50;
    for (int i = 0; i < rows.size(); i++) {
        em.persist(rows.get(i).toEntity());
        if ((i + 1) % batchSize == 0) {
            em.flush();   // send this batch
            em.clear();   // drop managed entities → memory stays flat
        }
    }
}
```

- Spring Data `saveAll()` just loops over `save()`. Batching still depends on the
  settings above, and with assigned ids every `save()` becomes a `merge` with an extra
  SELECT ([JPA Q24](03_JPA_Spring_Data_QA.md#24-save-persist-or-merge-and-the-isnew-trap)).
- **Prove it works:** Hibernate statistics or a datasource-proxy log should show
  batches of 50, not single statements.

---

## 16. Bulk updates and deletes

**Weight:** ★★

```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("update Product p set p.price = p.price * 1.1 where p.category = :c")
int raisePrices(@Param("c") String category);
```

- One SQL statement, whatever the number of rows.
- **It bypasses:** the persistence context (managed instances keep their old values —
  hence `clearAutomatically`), dirty checking, cascades, lifecycle callbacks and
  auditing listeners, orphan removal, and the `@Version` increment (unless you write
  `update versioned` in HQL).
- **Second-level cache:** Hibernate invalidates the affected entity region.
- **Not bulk:** a Spring Data derived `deleteByCategory(..)` loads every matching
  entity and calls `remove()` on each — N DELETE statements, but callbacks and
  cascades do run.

---

## 17. How lazy proxies work and where they bite

**Weight:** ★★

**Mechanism:** for a lazy `@ManyToOne`, Hibernate creates a runtime **subclass** of
the entity (generated with ByteBuddy) that holds only the id. The first call to a
getter other than `getId()` runs the SELECT.

**Where it bites:**

- **`final` entity class or methods:** the subclass can't be made, so the lazy
  to-one effectively loads eagerly.
- **`getClass()`:** a proxy's class is not `Customer.class`. An `equals` that compares
  `getClass()` fails. Use `instanceof`, or `Hibernate.getClass(obj)`.
- **Reading fields directly:** in `equals`, `other.name` reads the proxy's own field,
  which is `null`. Always use getters on the other object.
- **Inheritance:** a proxy of abstract `Payment` can't be cast to `CardPayment`. Use
  `Hibernate.unproxy(payment)` and then check the type.
- `@Proxy(lazy = false)` was removed in Hibernate 7 (checked in the 7.2.4 jar).

---

## 18. equals, hashCode and Lombok on entities

**Weight:** ★★

**Answer:** base equality on a stable business key (`@NaturalId`, such as an email or
ISBN) if one exists. Otherwise use the id, with a **constant** `hashCode`:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order other)) return false;   // instanceof: proxy-safe
    return id != null && id.equals(other.getId());   // getter: proxy-safe
}

@Override
public int hashCode() {
    return Order.class.hashCode();                    // never changes
}
```

**Why a constant hashCode:** the id is `null` before persist and set after. If
`hashCode` used the id, an entity added to a `HashSet` before persist would sit in the
wrong bucket afterwards and `contains()` would return `false`.

**Lombok:** never put `@Data`, `@EqualsAndHashCode` or `@ToString` on entities.

- They include every field, lazy collections too — calling them triggers loading.
- Mutable fields in `hashCode` move the entity between buckets.
- `toString` on both sides of a bidirectional association recurses until
  `StackOverflowError`.

Use `@Getter`, `@Setter`, hand-written `equals`/`hashCode`, and
`@ToString(onlyExplicitlyIncluded = true)` if needed.

---

## 19. DynamicUpdate and DynamicInsert

**Weight:** ★

- By default an UPDATE lists every column. `@DynamicUpdate` lists only the changed
  ones. The SQL is built at each flush, so there is no cached statement — a small CPU
  cost.
- Worth it for: wide tables where you change one column; not overwriting columns that
  others update concurrently; versionless optimistic locking with
  `@OptimisticLocking(type = OptimisticLockType.DIRTY)`.
- `@DynamicInsert` leaves out null columns so the database defaults apply.

---

## 20. Hibernate-only annotations worth knowing

**Weight:** ★★

| Annotation | What it does | Note |
| --- | --- | --- |
| `@NaturalId` | Marks a business key; load with `session.bySimpleNaturalId(Book.class).load(isbn)` | Cached by id in L1 and L2 |
| `@Formula("(select count(*) from review r where r.book_id = id)")` | Read-only property computed by SQL | Runs on every load — keep it cheap |
| `@SQLRestriction("deleted = false")` | A fixed WHERE added for an entity or collection | Replaces `@Where`, removed in Hibernate 7 |
| `@Filter` + `@FilterDef` | A WHERE you switch on per session, with parameters (tenant, date) | `session.enableFilter("tenant").setParameter(...)` |
| `@Immutable` | Never updated; no dirty checking | Views, reference data |
| `@BatchSize` | Batch-loads lazy proxies or collections | Q10 |
| `@JdbcTypeCode(SqlTypes.JSON)` | Maps a field to a json/jsonb column | Hibernate 6+ |
| `@SoftDelete` | Soft delete in one annotation | Q21 |
| `@CreationTimestamp`, `@UpdateTimestamp` | Fills timestamps automatically | Or Spring Data auditing |

`@Where` being absent from 7.2.4 and `@SQLRestriction` present in both 6.6.33 and
7.2.4 were checked in the jars.

---

## 21. Soft delete

**Weight:** ★★

**Option A — Hibernate's annotation** (present in 6.6 and 7.2):

```java
@Entity
@SoftDelete(columnName = "deleted")   // Hibernate owns this boolean column
public class Customer { ... }
```

`remove()` becomes `UPDATE customer SET deleted = true`, and every load and query
adds `deleted = false` automatically.

**Option B — the older, explicit way:**

```java
@Entity
@SQLDelete(sql = "update customer set deleted = true where id = ?")
@SQLRestriction("deleted = false")
public class Customer {
    private boolean deleted;
    ...
}
```

**Trade-offs to mention:**

- **Unique constraints:** a deleted customer's email blocks a new sign-up with the
  same email. Use a partial unique index (`WHERE deleted = false`).
- **Native SQL and reports** don't get the filter — they must remember it.
- **The table only grows.** Plan archiving.

---

## 22. StatelessSession and processing large data

**Weight:** ★★

- `StatelessSession` has no persistence context: no first-level cache, no dirty
  checking, no cascades, no lazy loading, no second-level cache. Each `insert`,
  `update` or `delete` call is direct SQL.
- Use it for imports, ETL and jobs that touch millions of rows.

**Reading large results without running out of memory:**

- Spring Data `Stream<T>` or Hibernate `ScrollableResults`, inside a read-only
  transaction, with a fetch-size hint. Detach each entity after processing it.
- **The driver must really stream.** MySQL needs `fetchSize = Integer.MIN_VALUE` or
  `useCursorFetch=true`. PostgreSQL needs a fetch size and auto-commit off (which a
  transaction gives you).
- For pure bulk work, consider skipping the ORM: `JdbcClient` or
  `JdbcTemplate.batchUpdate`.

---

## 23. Seeing the SQL and catching N+1 in tests

**Weight:** ★★

| Goal | How |
| --- | --- |
| See the SQL | `logging.level.org.hibernate.SQL=DEBUG` — not `show_sql`, which prints to stdout and bypasses the logger |
| See bound parameter values | `logging.level.org.hibernate.orm.jdbc.bind=TRACE` (Hibernate 6+) |
| Statement counts and timings per session | `spring.jpa.properties.hibernate.generate_statistics=true` |
| Slow queries | `hibernate.log_slow_query=<milliseconds>` |
| Prove batching, count statements in tests | datasource-proxy or p6spy; assert counts with a validator such as Hypersistence Utils' `SQLStatementCountValidator` |

**N+1 regression test idea:** load 10 orders with their items through the service and
assert the statement count is 2, not 11. The test fails the day someone removes the
fetch join.

---

## 24. What changed in Hibernate 6 and 7

**Weight:** ★★

**Hibernate 6 (Spring Boot 3.x):**

- `javax.persistence` → `jakarta.persistence`.
- A new query engine (SQM). HQL supports much more SQL, and results are read by
  column position (faster).
- A new type system: `@JdbcTypeCode`, `@JavaType`, `@JdbcType` replace the old
  `@Type`/`@TypeDef` custom types.
- Per-entity default sequences (`<entity>_seq`).
- Duplicate parents in fetch-join results are removed automatically — `distinct` is
  no longer needed for that.
- `save`, `update`, `saveOrUpdate` and `load` deprecated.

**Hibernate 7 (Spring Boot 4.x)** — each item checked in the 7.2.4 and
`jakarta.persistence-api` 3.2.0 jars:

- Implements Jakarta Persistence 3.2: `FindOption` and `LockOption` for `find()`,
  `EntityManagerFactory.runInTransaction(..)` and `callInTransaction(..)`,
  programmatic `PersistenceConfiguration`.
- Removed from `Session`: `save`, `update`, `saveOrUpdate`, `delete`,
  `load(Class, id)`.
- Removed annotations: `@Where`, `@Proxy`, `@LazyCollection`, `@LazyToOne`.
- `@UuidGenerator(style = VERSION_7)` for time-ordered UUIDs.
