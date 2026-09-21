# Interview Memory Q&A — Part 3

Mixed-topic memory notes (SQL clause order, nth-highest salary, duplicates, pivots,
deadlocks, unions, joins, indexes, foreign keys, OOP concepts, and Spring
exception-handling snippets) kept in the original order; the source had no item
numbers, so they are numbered sequentially here.

The SQL queries here were run against sample data before being written up. Where the
recorded answer returned something other than what it claimed, the section carries a
**Correction** line saying what was wrong. The deeper SQL reference lives in
[06-SQL/SQL_QA.md](../06-SQL/SQL_QA.md) — this file cross-references it rather than
repeating it.

## How to use this file

| # | Topic | The one thing to remember |
| --- | --- | --- |
| 1 | SQL clause order | The **logical** order differs from the written order. |
| 2 | Nth highest salary | `rank` is a reserved word; `SELECT DISTINCT *` does not dedupe salaries. |
| 3 | Detecting duplicates | `GROUP BY … HAVING COUNT(*) > 1`. |
| 4 | Pivot rows to columns | Conditional aggregation. |
| 5 | Deadlocks | The database picks a victim — **your app must retry**. |
| 6 | UNION vs UNION ALL | `UNION` treats two `NULL`s as duplicates. |
| 7 | Join types | `FULL JOIN` returns all rows from **both** sides. |
| 8 | Indexes and types | Knowing when an index is *skipped* is worth more. |
| 9 | Foreign keys | The default is `NO ACTION` — the delete **fails**. |
| 10 | OOP concepts | Overloading is compile-time, overriding is runtime. |
| 11 | `assertThrows` | It **returns** the exception — assert the message too. |
| 12 | `GlobalExceptionHandler` | Never put `ex.getMessage()` in a 500 response body. |

## Table of Contents

- [1. Standard SQL clause order](#1-standard-sql-clause-order)
- [2. Nth highest salary in SQL](#2-nth-highest-salary-in-sql)
- [3. How do you detect duplicate rows in a table?](#3-how-do-you-detect-duplicate-rows-in-a-table)
- [4. How do you pivot rows to columns in SQL?](#4-how-do-you-pivot-rows-to-columns-in-sql)
- [5. How do you handle deadlocks?](#5-how-do-you-handle-deadlocks)
- [6. What is the difference between UNION and UNION ALL?](#6-what-is-the-difference-between-union-and-union-all)
- [7. What is the difference between INNER JOIN, LEFT JOIN, RIGHT JOIN, and FULL JOIN?](#7-what-is-the-difference-between-inner-join-left-join-right-join-and-full-join)
- [8. What are indexes and their types?](#8-what-are-indexes-and-their-types)
- [9. What is a foreign key and what happens if the referenced row is deleted?](#9-what-is-a-foreign-key-and-what-happens-if-the-referenced-row-is-deleted)
- [10. OOP concepts: encapsulation, inheritance, polymorphism, abstraction](#10-oop-concepts-encapsulation-inheritance-polymorphism-abstraction)
- [11. Testing an expected exception with assertThrows](#11-testing-an-expected-exception-with-assertthrows)
- [12. GlobalExceptionHandler extending ResponseEntityExceptionHandler](#12-globalexceptionhandler-extending-responseentityexceptionhandler)

## 1. Standard SQL clause order

**Correction — the recorded answer was wrong on two counts.** It said the SQL
standard defines the order as `SELECT … FROM … WHERE … GROUP BY … HAVING …
ORDER BY … LIMIT … OFFSET`, and that "`OFFSET` and `LIMIT` can be swapped in some
databases".

1. **`LIMIT` is not in the SQL standard at all.** It is a MySQL/PostgreSQL/SQLite
   extension. The standard clause is `OFFSET n ROWS FETCH FIRST m ROWS ONLY`, with
   `OFFSET` **before** `FETCH`.
2. **They cannot be freely swapped.** MySQL accepts only `LIMIT m OFFSET n` (or the
   positional `LIMIT n, m`) — writing `OFFSET` first is a syntax error. PostgreSQL
   happens to accept either order, which is probably where the note came from.

**Written order** (what you type):

```sql
SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ... ORDER BY ...
```

…then `LIMIT m OFFSET n` (MySQL, PostgreSQL, SQLite) or
`OFFSET n ROWS FETCH NEXT m ROWS ONLY` (standard, SQL Server, Oracle 12c+,
PostgreSQL).

**Logical order** (what the engine actually does) — this is the part the question is
really after, and it explains every rule that follows:

```text
1. FROM / JOIN      build the working set
2. WHERE            filter rows
3. GROUP BY         collapse into groups
4. HAVING           filter groups
5. SELECT           evaluate expressions and window functions, apply aliases
6. DISTINCT         de-duplicate
7. ORDER BY         sort
8. LIMIT / OFFSET   slice
```

Three consequences worth stating unprompted:

| Consequence | Why |
| --- | --- |
| `WHERE` cannot reference an aggregate | Step 2 runs before groups exist at step 3. Use `HAVING`. |
| `WHERE` cannot use a `SELECT` alias | Step 5 runs after step 2, so the alias does not exist yet. |
| `ORDER BY` **can** use a `SELECT` alias | Step 7 runs after step 5. |

A caveat on that middle row: the standard forbids the alias in `WHERE` and
PostgreSQL, MySQL, SQL Server and Oracle all reject it. SQLite accepts it as an
extension (verified) — do not let that habit form, because the same query will fail
everywhere else.

## 2. Nth highest salary in SQL

**Form 1 — `LIMIT` / `OFFSET`:**

```sql
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET 2;          -- N = 3, so OFFSET is N-1
```

`N-1` must be computed by the caller. MySQL does **not** allow an expression there,
so `LIMIT 1 OFFSET ?-1` is a syntax error — bind the already-decremented value.

**Form 2 — window function:**

```sql
SELECT DISTINCT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 3;
```

**Correction — the recorded window version had two defects, both traced.**

| Recorded | Problem |
| --- | --- |
| `AS rank` | **`RANK` is a reserved word** in MySQL 8 and SQL Server. `AS rank` is a syntax error there unless quoted (`` `rank` `` / `[rank]`). Rename to `rnk`. |
| `SELECT DISTINCT * FROM (SELECT *, …)` | Returned **2 rows**, not one salary. `SELECT *` includes the primary key, so `DISTINCT` can never collapse two employees on the same salary. |

Traced on data where two employees share the 3rd-highest salary of 80000:
`SELECT DISTINCT *` returned both employee rows; `SELECT DISTINCT salary` returned
the single value 80000.

**Pick the ranking function deliberately** — this is the real question behind the
question:

| You want | Function | Behaviour on ties |
| --- | --- | --- |
| Nth highest **distinct salary** | `DENSE_RANK()` | Ties share a rank, no gaps: 1, 2, 2, 3. |
| Nth **ranking position** | `RANK()` | Ties share, then skip: 1, 2, 2, 4 — so `rnk = 3` returns nothing. |
| The Nth **row**, exactly one | `ROW_NUMBER()` | Never ties, but which tied row wins is arbitrary without a tiebreaker. |

Full worked comparison with verified output in
[06-SQL/SQL_QA.md section 25](../06-SQL/SQL_QA.md#25-window-functions-row_number-vs-rank-vs-dense_rank-new).

Edge case both forms share: if every salary is identical, **both return zero rows**
(verified), not `NULL`. If the caller needs a row regardless, use the correlated
`MAX` form instead.

## 3. How do you detect duplicate rows in a table?

```sql
SELECT name, COUNT(*)
FROM employees
GROUP BY name
HAVING COUNT(*) > 1;
```

Correct as recorded. Two extensions the follow-up asks for:

**Duplicates on a composite business key**, which is what real de-duplication uses:

```sql
SELECT name, dept_id, salary, COUNT(*)
FROM employees
GROUP BY name, dept_id, salary
HAVING COUNT(*) > 1;
```

**The full duplicate rows, not just the key and a count:**

```sql
SELECT *
FROM (
    SELECT e.*,
           COUNT(*) OVER (PARTITION BY name, dept_id, salary) AS dup_count
    FROM employees e
) t
WHERE dup_count > 1;
```

One `NULL` fact that surprises people: `GROUP BY` puts **all `NULL`s in one group**,
so two rows with `dept_id IS NULL` *are* counted as matching here — even though
`NULL = NULL` is never true in a `WHERE` clause. `GROUP BY`, `DISTINCT` and `UNION`
all use "not distinct from"; `=` does not.

Deleting the duplicates is a different query — see
[06-SQL/SQL_QA.md section 10](../06-SQL/SQL_QA.md#10-delete-duplicate-records),
which covers the MySQL error 1093 trap.

## 4. How do you pivot rows to columns in SQL?

Conditional aggregation — portable across every engine:

```sql
SELECT department,
       SUM(CASE WHEN gender = 'M' THEN 1 ELSE 0 END) AS male_count,
       SUM(CASE WHEN gender = 'F' THEN 1 ELSE 0 END) AS female_count
FROM employees
GROUP BY department;
```

Correct as recorded, and the right default answer because it works everywhere. Three
additions:

```sql
-- PostgreSQL: the FILTER clause is cleaner and does the same thing
SELECT department,
       COUNT(*) FILTER (WHERE gender = 'M') AS male_count,
       COUNT(*) FILTER (WHERE gender = 'F') AS female_count
FROM employees
GROUP BY department;
```

- `COUNT(CASE WHEN … THEN 1 END)` works too — note **no `ELSE`**, because `COUNT`
  skips `NULL`s. Mixing this up by writing `ELSE 0` under `COUNT` counts every row
  and silently returns the wrong number.
- Rows where `gender` is `NULL` land in neither column. Add a third
  `SUM(CASE WHEN gender IS NULL THEN 1 ELSE 0 END)` if they must be accounted for.
- Engine-specific operators exist — SQL Server's `PIVOT`, PostgreSQL's `crosstab()`
  from the `tablefunc` extension — but all of them still need the target columns
  known at query-writing time. **True dynamic pivots require building the SQL
  string**, which is the honest answer to "what if the values aren't known in
  advance?"

## 5. How do you handle deadlocks?

A deadlock occurs when two transactions each hold a lock the other needs, so neither
can proceed. The database **detects** this, picks a victim, and rolls it back with an
error — it does not hang forever.

**Correction — the recorded answer was incomplete in the way that matters most.** It
listed three prevention tips but never mentioned that **the application must catch
the deadlock error and retry**. Since the database has already rolled the victim
back, doing nothing means the user sees a failure for a transaction that would
succeed on a second attempt. Retry is the single most important part of the answer.

| Measure | What it does |
| --- | --- |
| **Retry the victim** | The database rolled it back; re-run it, typically 3 attempts with a short randomised backoff. Non-negotiable. |
| Consistent lock ordering | If every transaction touches tables (and rows) in the same order, a cycle cannot form. The strongest prevention. |
| Keep transactions short | Less time holding locks, less overlap. Never wait on a user, an HTTP call or a file read inside a transaction. |
| Index the filter columns | An unindexed `UPDATE … WHERE` scans and locks far more rows than it changes. A missing index is a very common hidden cause. |
| Lower the isolation level | In InnoDB, `READ COMMITTED` avoids most gap locks and measurably reduces deadlocks. It does **not** eliminate them, and it permits non-repeatable reads — a correctness trade, not a free win. |
| Lock explicitly and early | `SELECT … FOR UPDATE` on the rows you will modify, in the agreed order, rather than upgrading a shared lock later. |

Two distinctions worth raising:

- **A deadlock is not a lock-wait timeout.** A deadlock is a detected cycle and
  fails immediately; a lock-wait timeout is one transaction waiting too long on a
  lock that will eventually free. Different errors, different fixes — the timeout
  usually means a transaction is too long, not that there is a cycle.
- To diagnose, read the engine's own report: `SHOW ENGINE INNODB STATUS` in MySQL,
  `pg_locks` plus the deadlock entry in the PostgreSQL log, or the deadlock graph in
  SQL Server Extended Events. It names both transactions and the exact locks.

## 6. What is the difference between UNION and UNION ALL?

| | `UNION` | `UNION ALL` |
| --- | --- | --- |
| Duplicates | Removed | Kept |
| Cost | Higher — needs a sort or hash to de-duplicate | Lower — straight concatenation |
| Default choice | Only when duplicates are genuinely possible and wrong | **Prefer this** when you know rows cannot collide |

Correct as recorded. What it omitted:

- Both require the branches to have the **same number of columns** with compatible
  types. Column names come from the **first** branch.
- `ORDER BY` applies to the **whole result**, not a branch, and must be written once
  at the very end.
- `UNION` de-duplicates using `NULL`-equal semantics: two rows that are both `NULL`
  **are** treated as duplicates and collapsed to one (verified), even though
  `NULL = NULL` is never true in a `WHERE` clause. Same rule as `GROUP BY` and
  `DISTINCT`.

The sibling operators, which the follow-up usually reaches for:

| Operator | Returns | Notes |
| --- | --- | --- |
| `INTERSECT` | Rows in **both** results | De-duplicates, like `UNION`. |
| `EXCEPT` (`MINUS` in Oracle) | Rows in the first but not the second | MySQL 8.0.31+ supports `EXCEPT`; older MySQL has neither. |

## 7. What is the difference between INNER JOIN, LEFT JOIN, RIGHT JOIN, and FULL JOIN?

**Correction — the recorded description of `FULL JOIN` was wrong.** It said "returns
all rows when there's a match in either table", which describes something closer to
an inner join. A `FULL OUTER JOIN` returns **every row from both tables**, matched
where possible and `NULL`-filled where not — including rows that match nothing on
either side.

| Join | Returns | Unmatched rows |
| --- | --- | --- |
| `INNER JOIN` | Only rows with a match on both sides | Dropped from both sides |
| `LEFT JOIN` | All left rows + matching right rows | Left kept, right columns `NULL` |
| `RIGHT JOIN` | All right rows + matching left rows | Right kept, left columns `NULL` |
| `FULL OUTER JOIN` | **All rows from both tables** | Both kept, the missing side `NULL` |
| `CROSS JOIN` | Every combination (Cartesian product) | No `ON` clause; N × M rows |
| Self join | A table joined to itself under two aliases | E.g. employee → manager |

Three practical notes:

- **MySQL has no `FULL OUTER JOIN`.** Emulate it with
  `SELECT … LEFT JOIN … UNION SELECT … RIGHT JOIN …`.
- `RIGHT JOIN` is rare in real code — most teams rewrite it as a `LEFT JOIN` with
  the tables swapped, because reading a query where "all rows" refers to the second
  table is harder.
- An accidental `CROSS JOIN` from a forgotten `ON` clause is a classic runaway
  query: two 10,000-row tables produce 100 million rows.

### The trap: a LEFT JOIN that silently becomes an INNER JOIN

This is the most common join mistake, and it is worth being able to demonstrate
rather than just name. A `LEFT JOIN` `NULL`-fills unmatched rows; a `WHERE` on a
right-table column then tests those `NULL`s, gets `UNKNOWN`, and discards them:

```sql
-- BUG: the LEFT has no effect
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id
WHERE d.location = 'Bangalore';
```

Traced on 9 employees, this returned **3 rows** — only the Bangalore staff. Every
employee without a department disappeared.

```sql
-- FIX: move the condition into the ON clause
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d
       ON e.dept_id = d.id
      AND d.location = 'Bangalore';
```

Returned **9 rows** — all employees, with `d.name` filled for the three in Bangalore
and `NULL` for the rest.

The rule: `ON` decides **what counts as a match** and preserves the outer side;
`WHERE` filters the **finished** result and destroys it. The one safe `WHERE` on a
right-table column is `IS NULL`, which is the deliberate anti-join used to find
"rows with no match". Full walkthrough in
[06-SQL/SQL_QA.md section 22](../06-SQL/SQL_QA.md#22-when-does-a-left-join-silently-become-an-inner-join-new).

## 8. What are indexes and their types?

An index is a separate sorted data structure (usually a B-tree, sometimes a hash)
that lets the engine find rows without scanning the table.

| Type | What it does |
| --- | --- |
| Clustered | Defines the physical row order. One per table. |
| Non-clustered | A separate structure holding the key plus a row locator. |
| Composite | Several columns; only a **leftmost prefix** can be seeked on. |
| Unique | Enforces uniqueness and tells the optimizer a match returns one row. |
| Covering | Holds every column a given query needs, so the table is never touched. |
| Partial / filtered | Indexes only the rows matching a condition. |
| Full-text | Word and phrase search inside long text. |
| Hash | Equality only — useless for ranges or `ORDER BY`. |

Each type with syntax, dialect notes and the trade-offs is in
[06-SQL/SQL_QA.md section 20](../06-SQL/SQL_QA.md#20-how-indexes-work-index-types).

### When the index is NOT used — the higher-value half

Listing index types is table stakes. Knowing why an index you created is being
ignored is what the follow-up probes:

| Pattern | Why it is skipped |
| --- | --- |
| `WHERE YEAR(join_date) = 2025` | The column is wrapped in a function, so the indexed values no longer match what is compared. Use a date range instead. |
| `WHERE name LIKE '%gita'` | A leading wildcard leaves no prefix to seek on. `'gita%'` is fine. |
| `WHERE phone = 9876543210` on a `VARCHAR` | Implicit type conversion casts the column — a function call in disguise. |
| Filtering on a non-leading composite column | Leftmost-prefix rule. A full index scan may still happen; a seek will not. |
| The predicate matches most of the table | A full scan is genuinely cheaper. The optimizer is right. |
| Stale statistics | The row estimate is wrong, so the plan is costed wrongly. Run `ANALYZE`. |

The unifying principle: **keep the indexed column bare on one side of the
comparison.** Confirm with `EXPLAIN` rather than assuming — `Seq Scan`
(PostgreSQL), `type: ALL` (MySQL) and `Table Scan` (SQL Server) are the giveaways.

And the cost side, which a good answer volunteers: every index slows `INSERT`,
`UPDATE` and `DELETE`, consumes disk, and competes for buffer-pool memory. An
unused index is pure overhead.

## 9. What is a foreign key and what happens if the referenced row is deleted?

A foreign key enforces referential integrity: a value in the child column must exist
in the parent's primary key or unique column (or be `NULL`).

```sql
CREATE TABLE orders (
  order_id    INT PRIMARY KEY,
  customer_id INT,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
```

**Correction — the recorded answer listed only `CASCADE` and `SET NULL`, and so
never stated the default.** That default *is* the answer to "what happens if the
referenced row is deleted": with no `ON DELETE` clause, the behaviour is
`NO ACTION` / `RESTRICT`, and **the `DELETE` fails with a constraint violation**.
The parent row is protected. Saying only "it cascades" describes a behaviour you
have to explicitly ask for.

| `ON DELETE` option | What happens to the child rows | Notes |
| --- | --- | --- |
| `NO ACTION` | **The parent delete is rejected.** | **The default.** Checked at the end of the statement, so deferrable. |
| `RESTRICT` | The parent delete is rejected. | Checked immediately; cannot be deferred. |
| `CASCADE` | Child rows are deleted too. | Convenient and dangerous — one delete can cascade through several tables. |
| `SET NULL` | The FK column is set to `NULL`. | The column must be nullable. |
| `SET DEFAULT` | The FK column is set to its column default. | That default must itself exist in the parent, or the statement fails. |

`ON UPDATE` takes the same options and governs what happens when the parent's key
value changes.

Two things that separate a good answer:

- **MySQL/InnoDB automatically creates an index on the child FK column; PostgreSQL
  does not.** In PostgreSQL an unindexed FK makes every parent delete scan the whole
  child table to check for references — a classic cause of a delete that gets slower
  as the system grows. Index child FK columns yourself.
- **`TRUNCATE` normally cannot run on a table referenced by a foreign key.**
  PostgreSQL offers `TRUNCATE … CASCADE`; MySQL simply refuses. See
  [06-SQL/SQL_QA.md section 27](../06-SQL/SQL_QA.md#27-delete-vs-truncate-vs-drop-new).

## 10. OOP concepts: encapsulation, inheritance, polymorphism, abstraction

| Concept | Meaning | Real-world example |
| --- | --- | --- |
| Encapsulation | Wrapping data and behaviour into one unit and restricting direct access to the data | Capsule containing medicine |
| Inheritance | One class acquires the properties and behaviour of another | Child inherits traits from a parent |
| Polymorphism | One name, many forms — behaviour depends on the actual object | The same remote button does different things on different devices |
| Abstraction | Hiding internal details, exposing only essential features | A driver uses the accelerator without knowing the engine internals |

Correct as recorded. The follow-up is always "show me in Java", so pair each with its
mechanism:

| Concept | Java mechanism | Gets asked as |
| --- | --- | --- |
| Encapsulation | `private` fields + public getters/setters | "Why not just make it public?" — because you cannot add validation or change the representation later. |
| Inheritance | `extends` (one class), `implements` (many interfaces) | "Why single inheritance for classes?" — to avoid the diamond problem. |
| Polymorphism | **Overriding** (runtime) and **overloading** (compile-time) | "Which is which?" — see below. |
| Abstraction | `abstract class`, `interface` | "Abstract class vs interface?" — see below. |

**Overloading vs overriding** — the distinction that actually gets tested:

| | Overloading | Overriding |
| --- | --- | --- |
| Also called | Compile-time / static polymorphism | Runtime / dynamic polymorphism |
| Signature | Same name, **different** parameters | Same name, **same** parameters |
| Resolved | At compile time, by the **declared** type | At runtime, by the **actual object** |
| Return type | May differ freely | Must be the same or a subtype (covariant) |
| Access modifier | Any | Cannot be more restrictive than the parent's |

**Abstract class vs interface** — the other guaranteed follow-up:

| | Abstract class | Interface |
| --- | --- | --- |
| State | Can hold instance fields | Only `public static final` constants |
| Constructor | Yes | No |
| Multiple inheritance | One only | Many |
| Methods | Abstract and concrete | Abstract, `default`, `static`, `private` (Java 9+) |
| Use when | Classes share state and a common base | Classes share a capability but nothing else |

Since Java 8, interfaces can carry `default` method bodies, which is why the
"interfaces can't have implementations" answer is now wrong — and why the diamond
conflict in [Part 1 section 1](Interview_Memory_QA_Part1.md#1-calling-a-specific-interfaces-default-method-when-two-interfaces-define-it)
exists at all.

## 11. Testing an expected exception with assertThrows

```java
@Test
void shouldThrowOrderNotFoundException() {
    OrderNotFoundException ex = assertThrows(
            OrderNotFoundException.class,
            () -> orderService.getOrder("123"));

    assertEquals("Order not found with id 123", ex.getMessage());
}
```

Correct as recorded, with one addition that matters: **`assertThrows` returns the
thrown exception**, so you can assert on its message, cause or fields. Discarding
the return value means the test passes for *any* `OrderNotFoundException`, including
one thrown for a completely different reason.

Points worth having ready:

- `assertThrows` passes if the thrown exception is the given type **or a subtype**.
  Use `assertThrowsExactly` when the exact class matters.
- This replaced JUnit 4's `@Test(expected = …)`, which could not tell you *where* in
  the method the exception came from — a setup line throwing the same type made the
  test pass for the wrong reason. The lambda scopes the assertion to one call.
- The sibling is `assertDoesNotThrow(() -> …)` for the happy path.
- Keep the lambda to the single call under test. Wrapping three statements
  reintroduces exactly the ambiguity JUnit 4 had.

## 12. GlobalExceptionHandler extending ResponseEntityExceptionHandler

```java
package com.target.orders.exception.handler;

import com.target.orders.exception.exceptions.ErrorResponse;
import com.target.orders.exception.exceptions.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override                                   // was missing
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : "
                        + fieldError.getDefaultMessage())
                .toList();
        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                validationErrors,
                path);
        log.error("Validation failed at {} -> {}", path, validationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
            OrderNotFoundException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of(), path);

        log.warn("Resource not found at {} -> {}", path, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(
            Exception exception, HttpServletRequest request) {

        String path = request.getRequestURI();

        // log the THROWABLE (stack trace), return a GENERIC message
        log.error("Unhandled exception at {}", path, exception);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please contact support.",
                List.of(),
                path);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

**Corrections applied to the recorded version:**

| Recorded | Problem | Fix |
| --- | --- | --- |
| `handleMethodArgumentNotValid` with no `@Override` | The signature changed in Spring 6 (`HttpStatus` → `HttpStatusCode`). Without `@Override`, a signature that does not match compiles fine and simply **never runs** — you get the framework's default 400 body and never notice. | Add `@Override`. |
| `handleServerError` returned `exception.getMessage()` in the body | **Leaks internals to the caller** — SQL fragments, file paths, class names. A standard security finding. | Return a generic message. |
| `handleServerError` did not log at all | A 500 with no log entry is undebuggable. | `log.error("...", path, exception)` — pass the throwable **last and unformatted** so SLF4J prints the stack trace. |
| `ResponseEntity<?>` | Loses type safety and makes the response shape unclear to readers and to OpenAPI generators. | `ResponseEntity<ErrorResponse>`. |

Three more things worth knowing about this class:

- **Extending `ResponseEntityExceptionHandler` is what lets you customise Spring's
  own exceptions** — `MethodArgumentNotValidException`,
  `HttpMessageNotReadableException`, `HttpRequestMethodNotSupportedException` and
  about a dozen more. Without it, those produce the default error body.
- Handlers are matched **most specific first**, so the `Exception.class` catch-all
  does not shadow the specific handlers or the inherited ones. It is the safety net,
  not a competitor.
- `MethodArgumentNotValidException` covers `@Valid` on a `@RequestBody`. Constraints
  on `@RequestParam` / `@PathVariable` under a class-level `@Validated` throw
  **`ConstraintViolationException` instead**, which this class does not handle — so
  those surface as 500s. Add a handler for it. See
  [Part 1 section 4](Interview_Memory_QA_Part1.md#4-valid-vs-validated).
