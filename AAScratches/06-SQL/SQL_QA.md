# SQL Interview Q&A

Answers to expect for SQL interview questions: clustered vs non-clustered and the
other index types, practice queries on an employees/departments schema, and how to
keep queries efficient at scale.

Every query in sections 3 and 6–16 has been run against the sample data in
[section 4](#4-reference-schema-departments-and-employees-tables). Where the
original answer returned something other than what it claimed, the section carries
a **Correction** line saying what was wrong, so the distinction sticks rather than
just the new words.

## How to use this file

| # | Question | The one thing to remember |
| --- | --- | --- |
| 1 | Clustered vs non-clustered index | Clustered *is* the table's row order; one per table. |
| 2 | Clustered index on a non-PK column | Only SQL Server really lets you choose. InnoDB and PostgreSQL do not. |
| 3 | Student / grade range join | A range `JOIN` silently drops marks that fall in no band. |
| 4 | Reference schema + sample data | All traced results below come from this data. |
| 5 | Keeping queries efficient at scale | Index, measure with `EXPLAIN`, cache, partition. |
| 6 | Employees without a department | `LEFT JOIN … WHERE right IS NULL` is the anti-join idiom. |
| 7 | Second highest salary | `OFFSET … FETCH` is standard; MySQL needs `LIMIT/OFFSET`. |
| 8 | Duplicate records | `GROUP BY key HAVING COUNT(*) > 1`. |
| 9 | Highest paid per department | The `MAX` self-join drops rows whose group key is `NULL`. |
| 10 | Delete duplicates | `ROW_NUMBER()` is portable; `NOT IN` on the same table fails in MySQL. |
| 11 | Joined in the last 3 months | Date maths is dialect-specific. |
| 12 | Nth highest via window function | `DENSE_RANK()` returns one row **per employee**, not per salary. |
| 13 | Query optimization | Measure first; index second. |
| 14 | Joins vs subqueries | Modern optimizers flatten most subqueries — the old "JOIN is faster" is stale. |
| 15–16 | Rows 21–30 | `OFFSET` counts *positions*, not `id` values. |
| 17 | Covering index | "Covering" is a property of an index **and** a query, not of the index alone. |
| 18 | High write throughput | Batch, partition, and cut index count. |
| 19 | Composite vs covering | `INCLUDE` exists in SQL Server and PostgreSQL 11+, not MySQL. |
| 20 | Index types | Bitmap indexes are Oracle — PostgreSQL does **not** have them. |
| 21 | Is the clustered index automatic? | InnoDB and SQL Server yes; PostgreSQL and Oracle no. |
| 22 | LEFT JOIN turning into an INNER JOIN | *(new)* A `WHERE` on the right table kills the outer join. |
| 23 | GROUP BY and HAVING rules | *(new)* `WHERE` filters rows, `HAVING` filters groups. |
| 24 | NULL comparison semantics | *(new)* `NOT IN` + a `NULL` returns zero rows. |
| 25 | Window function family | *(new)* `ROW_NUMBER` vs `RANK` vs `DENSE_RANK` on ties. |
| 26 | When an index is **not** used | *(new)* Wrap the column in a function and the index is gone. |
| 27 | DELETE vs TRUNCATE vs DROP | *(new)* Three different things get removed. |
| 28 | Normal forms 1NF–BCNF | *(new)* One fact in one place. |

## Table of Contents

- [1. Difference between clustered and non-clustered index](#1-difference-between-clustered-and-non-clustered-index)
- [2. Can we create a clustered index on non-primary columns? Pros and cons](#2-can-we-create-a-clustered-index-on-non-primary-columns-pros-and-cons)
- [3. Print student name, grade and marks using a grade range table](#3-print-student-name-grade-and-marks-using-a-grade-range-table)
- [4. Reference schema: departments and employees tables](#4-reference-schema-departments-and-employees-tables)
- [5. How do you ensure database queries are efficient in large-scale applications?](#5-how-do-you-ensure-database-queries-are-efficient-in-large-scale-applications)
- [6. Find employees without a department](#6-find-employees-without-a-department)
- [7. Second highest salary](#7-second-highest-salary)
- [8. Duplicate records](#8-duplicate-records)
- [9. Highest paid employee in each department](#9-highest-paid-employee-in-each-department)
- [10. Delete duplicate records](#10-delete-duplicate-records)
- [11. Find employees who joined in the last 3 months](#11-find-employees-who-joined-in-the-last-3-months)
- [12. Nth highest salary using a window function](#12-nth-highest-salary-using-a-window-function)
- [13. Query optimization](#13-query-optimization)
- [14. Joins vs subqueries](#14-joins-vs-subqueries)
- [15. Get 10 employees from 21 to 30 based on ID (OFFSET / FETCH)](#15-get-10-employees-from-21-to-30-based-on-id-offset--fetch)
- [16. Get 10 employees from 21 to 30 based on ID (LIMIT / OFFSET)](#16-get-10-employees-from-21-to-30-based-on-id-limit--offset)
- [17. What is a covering index?](#17-what-is-a-covering-index)
- [18. How do you handle high write throughput tables?](#18-how-do-you-handle-high-write-throughput-tables)
- [19. Composite index vs covering index](#19-composite-index-vs-covering-index)
- [20. How indexes work: index types](#20-how-indexes-work-index-types)
- [21. Do we need to create a clustered index explicitly, or is it automatic?](#21-do-we-need-to-create-a-clustered-index-explicitly-or-is-it-automatic)
- [22. When does a LEFT JOIN silently become an INNER JOIN? (new)](#22-when-does-a-left-join-silently-become-an-inner-join-new)
- [23. GROUP BY and HAVING rules (new)](#23-group-by-and-having-rules-new)
- [24. NULL comparison semantics (new)](#24-null-comparison-semantics-new)
- [25. Window functions: ROW_NUMBER vs RANK vs DENSE_RANK (new)](#25-window-functions-row_number-vs-rank-vs-dense_rank-new)
- [26. When is an index NOT used? (new)](#26-when-is-an-index-not-used-new)
- [27. DELETE vs TRUNCATE vs DROP (new)](#27-delete-vs-truncate-vs-drop-new)
- [28. Normal forms: 1NF to BCNF (new)](#28-normal-forms-1nf-to-bcnf-new)

## 1. Difference between clustered and non-clustered index

**Short and clear answer (ideal for a quick response):**

A clustered index determines the physical order of data in a table — there can be
only one clustered index per table because the data rows are stored in that order.

A non-clustered index, on the other hand, creates a separate structure that stores
key values and pointers (row locators) to the actual data — you can have multiple
non-clustered indexes per table.

| Feature | Clustered Index | Non-Clustered Index |
| --- | --- | --- |
| Data storage | Data is physically stored in index order (B-tree leaf nodes contain the actual data rows). | Index is stored separately. Leaf nodes hold the key plus a row locator. |
| Number per table | Only **one** (data can be sorted only one way). | **Many** per table (up to 999 in SQL Server). |
| Performance | Faster for **range queries** and **sorting** (`ORDER BY`, `BETWEEN`). | Better for **selective lookups** on non-key columns. |
| Storage | No separate structure; the data *is* the index. | Requires **additional storage**. |
| Lookup cost | One traversal reaches the row. | May need a second step — a *key lookup* / *bookmark lookup* — to fetch columns not in the index. |

```sql
-- SQL Server syntax. If order_id is already the PRIMARY KEY, SQL Server has
-- ALREADY built a clustered index on it and this statement fails.
CREATE CLUSTERED INDEX idx_orders_id ON Orders(order_id);

-- Non-clustered index on another column
CREATE NONCLUSTERED INDEX idx_orders_customer ON Orders(customer_id);
```

**Correction —** the original said non-clustered indexes are simply "better for
lookup queries". The missing half is *why* they can be slower: if the query asks
for a column the index does not hold, the engine must jump back to the table for
every matching row. That second step is the key lookup, and it is the reason
covering indexes (section 17) exist.

**Also worth knowing:** in InnoDB a non-clustered ("secondary") index leaf stores
the **primary key value**, not a physical row pointer. So every secondary-index
lookup that needs extra columns costs a second B-tree descent through the clustered
index. That is why a wide primary key makes every secondary index on the table
bigger and slower.

## 2. Can we create a clustered index on non-primary columns? Pros and cons

**Correction — this was the biggest error in the original file.** It claimed you
can choose a clustered index column "in most relational databases (like SQL Server,
PostgreSQL, and MySQL with InnoDB)". That is wrong for two of the three, and it
contradicted this file's own table in section 21.

| Engine | Can you cluster on an arbitrary non-PK column? | What actually happens |
| --- | --- | --- |
| **SQL Server** | **Yes** — this is the real answer. | Drop the PK's clustered index, create a clustered index elsewhere, keep the PK as a non-clustered unique index. |
| **MySQL / InnoDB** | **No.** | InnoDB clusters on the PK. With no PK it uses the first `UNIQUE NOT NULL` index; failing that, a hidden 6-byte row id. You influence it only by choosing the PK. |
| **PostgreSQL** | **No.** | PostgreSQL has no clustered index at all. `CLUSTER` is a one-off rewrite (see section 21) and the order decays on the next insert. |
| **Oracle** | Partly. | Use an Index-Organized Table (IOT), which is organised by the primary key. |

So the honest interview answer is: *"In SQL Server, yes — and it is a real design
decision. In InnoDB and PostgreSQL the question doesn't apply the same way,
because you don't get to pick a clustering column."*

**SQL Server example, with the correct statements:**

```sql
CREATE TABLE Orders (
    order_id    INT PRIMARY KEY,   -- gets the clustered index by default
    customer_id INT,
    order_date  DATE,
    amount      DECIMAL(10,2)
);

-- WRONG: a PK-backed index cannot be dropped with DROP INDEX.
-- DROP INDEX IF EXISTS PK_Orders;

-- RIGHT: drop the constraint, then re-add the PK as non-clustered.
ALTER TABLE Orders DROP CONSTRAINT PK_Orders;
ALTER TABLE Orders
    ADD CONSTRAINT PK_Orders PRIMARY KEY NONCLUSTERED (order_id);

CREATE CLUSTERED INDEX idx_orders_order_date ON Orders(order_date);
```

**Correction —** the original used `DROP INDEX IF EXISTS PK_Orders;`. SQL Server
rejects that: an index that backs a `PRIMARY KEY` or `UNIQUE` constraint can only
be removed by dropping the constraint.

**Benefits**

| Benefit | Explanation |
| --- | --- |
| Faster range queries | Rows for `WHERE order_date BETWEEN …` sit on adjacent pages, so fewer page reads. |
| Cheaper sequential reads | Ideal for reports and time-series patterns (logs, transactions by timestamp). |
| Sorts disappear | `ORDER BY order_date` is already satisfied by the data order — no sort operator. |

**Drawbacks**

| Drawback | Explanation |
| --- | --- |
| You only get one | Clustering by date means `order_id` lookups now cost a key lookup. |
| Every non-clustered index grows | Non-clustered leaves carry the clustering key, so a wide clustering key inflates *all* other indexes. |
| Page splits on insert | Inserting out of order (random dates) splits pages and fragments the table. |
| Bad for volatile columns | If the clustering column changes, the row physically moves — expensive, and it updates every secondary index. |

**Which column to cluster on**

| Situation | Recommended clustering key |
| --- | --- |
| Mostly primary-key lookups | Keep it on the **primary key**. |
| Mostly range scans over dates | Cluster on **that range column** (e.g. `created_at`). |
| Append-only table | Cluster on a **monotonically increasing** column (`id`, `timestamp`) to avoid splits. |
| Heavy joins on one foreign key | Cluster on that **foreign key**, if most queries benefit. |

## 3. Print student name, grade and marks using a grade range table

Write a query to print Student Name, Grade and Marks for each student. Grade is
defined by the grades table based on the range of the mark. Sort by Grade; if
several students share a Grade, order them by Mark.

| Table | Columns |
| --- | --- |
| `student` | `id`, `name`, `marks` |
| `grades` | `grade`, `min_mark`, `max_mark` |

```sql
SELECT s.name,
       g.grade,
       s.marks
FROM student s
JOIN grades g
  ON s.marks BETWEEN g.min_mark AND g.max_mark
ORDER BY g.grade,
         s.marks;
```

**Traced against this sample data:**

```sql
-- student
(1,'Ravi',75) (2,'Sita',91) (3,'Tara',58) (4,'Umesh',103)
-- grades
('A',90,100) ('B',70,89) ('C',50,69)
```

Actual result — **3 rows, not 4**:

| name | grade | marks |
| --- | --- | --- |
| Sita | A | 91 |
| Ravi | B | 75 |
| Tara | C | 58 |

**Correction —** the query is written correctly, but the question says "for each
student" and this returns only the students whose marks land inside a band.
Umesh (103) falls outside every range, so the `INNER JOIN` **silently drops him**.
That is exactly the kind of thing an interviewer probes. Say it out loud, and offer
the `LEFT JOIN` variant, which returns all 4 rows with `grade = NULL` for Umesh:

```sql
SELECT s.name,
       g.grade,
       s.marks
FROM student s
LEFT JOIN grades g
  ON s.marks BETWEEN g.min_mark AND g.max_mark
ORDER BY g.grade,
         s.marks;
```

Two more things to mention unprompted:

- `BETWEEN` is **inclusive** on both ends, so the grade bands must not overlap. If
  `('B',70,89)` and `('C',60,75)` both existed, Ravi would appear **twice** — a
  range join can multiply rows.
- Ordering by `g.grade` is alphabetical on the grade letter. If grades were
  `A, B, …, F` that happens to be the right order; for grades like `10, 9, 8` you
  would need to order by `g.min_mark DESC` instead.

## 4. Reference schema: departments and employees tables

Sections 6 to 16 use these two tables. The sample rows below are the exact data
every "actual result" in this file was traced against.

**Table: departments**

| Column | Type | Description |
| --- | --- | --- |
| id | INT (PK) | Unique department ID |
| name | VARCHAR(50) | Department name |
| location | VARCHAR(50) | City where the department is located |

**Table: employees**

| Column | Type | Description |
| --- | --- | --- |
| id | INT (PK) | Unique employee ID |
| name | VARCHAR(50) | Employee name |
| dept_id | INT (FK) | Department the employee belongs to; nullable |
| salary | DECIMAL(10,2) | Monthly salary |
| join_date | DATE | Date of joining |
| manager_id | INT | Employee ID of manager (self reference) |

**Sample data used for every traced result below**

```sql
INSERT INTO departments VALUES
 (1,'Engineering','Bangalore'), (2,'Sales','Mumbai'),
 (3,'HR','Delhi'),              (4,'Legal','Pune'),
 (5,'Finance','Chennai');       -- Finance has no employees

INSERT INTO employees VALUES
 (1,'Asha',   1,  90000,'2025-01-10', NULL),
 (2,'Bhanu',  1,  90000,'2025-02-11', 1),
 (3,'Chetan', 1,  70000,'2024-06-01', 1),
 (4,'Divya',  4,  50000,'2023-03-03', NULL),
 (5,'Esha', NULL, 120000,'2025-07-20',NULL),  -- no department
 (6,'Farid',  9,  60000,'2025-08-02', NULL),  -- dept 9 does not exist
 (7,'Gita',   2,  80000,'2024-11-15', NULL),  -- 7 and 8 are duplicates
 (8,'Gita',   2,  80000,'2024-11-15', NULL),
 (9,'Hari',   3,  40000,'2022-01-01', NULL);
```

The three deliberately awkward rows — Esha with a `NULL` department, Farid pointing
at a department that does not exist, and the Gita pair — are what expose the bugs in
sections 9, 10 and 12.

## 5. How do you ensure database queries are efficient in large-scale applications?

- **Write optimized SQL:** fetch only the required columns and rows — projections
  instead of `SELECT *`, with proper `WHERE`, `LIMIT` and pagination. Avoid
  unnecessary joins and subqueries.
- **Use proper indexing:** index the columns that are frequently filtered, joined or
  sorted, balancing read gain against write cost. Drop unused and redundant indexes
  — an index on `(a)` is redundant if `(a, b)` already exists.
- **Analyze execution plans:** use `EXPLAIN` / `EXPLAIN ANALYZE` to see the real
  plan and spot full table scans, nested-loop joins over large inputs, and sorts
  that spill to disk.
- **Optimize schema design:** normalize to remove redundancy, then denormalize
  selectively where it removes a hot join on a read-heavy path.
- **Caching:** cache frequent queries in Redis or an application-level cache. Be
  explicit about invalidation — a stale cache is a correctness bug, not a
  performance one.
- **Connection pooling and batching:** use a pool such as HikariCP and batch writes
  to cut round-trips.
- **Keyset pagination for deep pages:** `OFFSET 100000` still reads and discards
  100,000 rows. `WHERE id > :last_seen_id ORDER BY id LIMIT 10` does not. See
  section 15.
- **Monitoring and profiling:** slow-query logs plus an APM tool (New Relic,
  Datadog); review the top queries by total time, not just by slowest single run.
- **Sharding and partitioning:** partition very large tables so that most queries
  touch one partition, and shard when a single node can no longer hold the working
  set.

## 6. Find employees without a department

```sql
SELECT e.id, e.name, e.dept_id
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id
WHERE d.id IS NULL;
```

Actual result — **2 rows**:

| id | name | dept_id |
| --- | --- | --- |
| 5 | Esha | NULL |
| 6 | Farid | 9 |

This query is correct, and it is worth knowing *why* it is correct when section 22
shows nearly the same shape being a bug. The `LEFT JOIN` keeps every employee and
fills `d.*` with `NULL` where no department matched. `WHERE d.id IS NULL` then keeps
exactly the rows that failed to match. This is the standard **anti-join** idiom.

The rule: a `WHERE` on the right table breaks an outer join *unless* the test is
`IS NULL`, which is the one predicate that is true for the NULL-filled rows.

Note that it returns two different kinds of "no department" — Esha, whose `dept_id`
is genuinely `NULL`, and Farid, whose `dept_id = 9` points at a department that does
not exist. If you only want the first kind, `WHERE e.dept_id IS NULL` needs no join
at all. Say which one you mean.

## 7. Second highest salary

```sql
-- Standard SQL / SQL Server 2012+ / PostgreSQL / Oracle 12c+
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY;
```

```sql
-- MySQL and SQLite do not support OFFSET ... FETCH
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET 1;
```

Actual result on the sample data: **90000** (Esha's 120000 is highest; 90000 is
second, shared by Asha and Bhanu, collapsed to one row by `DISTINCT`).

**Correction —** the original gave only the `OFFSET … FETCH` form without saying
which engines accept it. In MySQL that statement is a syntax error, so reciting it
for a MySQL shop is a visible miss. Name the dialect when you answer.

Two edge cases the interviewer will ask about, both traced:

- If **every** salary is identical, both forms return **zero rows**, not `NULL`.
  `SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) …)` returns
  a single `NULL` row instead. Know which behaviour the caller wants.
- `DISTINCT` is what makes this the second highest *salary*. Without it you get the
  second highest *row*, which on this data is still 90000 but for a different
  reason — say which question you are answering.

## 8. Duplicate records

```sql
SELECT name, COUNT(*)
FROM employees
GROUP BY name
HAVING COUNT(*) > 1;
```

Actual result: **1 row** — `('Gita', 2)`.

This finds duplicate *names*. Real duplicate detection is usually on a composite
business key, and usually you want the offending rows, not just the counts:

```sql
-- the full duplicate rows, not just the key and a count
SELECT *
FROM (
    SELECT e.*,
           COUNT(*) OVER (PARTITION BY name, dept_id, salary) AS dup_count
    FROM employees e
) t
WHERE dup_count > 1;
```

Remember that `GROUP BY` puts **all `NULL`s in one group** — unlike `=`, which
never matches `NULL` to `NULL`. So two rows with `dept_id IS NULL` *are* grouped
together here. That is the opposite of the `NOT IN` behaviour in section 24, and
mixing the two up is a common slip.

## 9. Highest paid employee in each department

```sql
SELECT e.*
FROM employees e
JOIN (
    SELECT dept_id, MAX(salary) AS max_sal
    FROM employees
    GROUP BY dept_id
) m ON e.dept_id = m.dept_id
   AND e.salary  = m.max_sal;
```

Actual result — **7 rows**:

| id | name | dept_id | salary |
| --- | --- | --- | --- |
| 1 | Asha | 1 | 90000 |
| 2 | Bhanu | 1 | 90000 |
| 4 | Divya | 4 | 50000 |
| 6 | Farid | 9 | 60000 |
| 7 | Gita | 2 | 80000 |
| 8 | Gita | 2 | 80000 |
| 9 | Hari | 3 | 40000 |

**Correction — this query silently loses a row.** Esha (id 5, salary 120000, the
highest paid person in the company) does **not** appear. Her `dept_id` is `NULL`,
the subquery produces a `NULL` group, and the join condition
`e.dept_id = m.dept_id` evaluates to `UNKNOWN` for `NULL = NULL` — so it never
matches. Any `GROUP BY` value that is `NULL` cannot be joined back this way.

The window-function version has no such hole, and needs only one pass over the
table instead of two:

```sql
SELECT id, name, dept_id, salary
FROM (
    SELECT e.*,
           RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk
    FROM employees e
) t
WHERE rnk = 1;
```

That returns **8 rows** — the same 7 plus Esha.

Two more points to raise yourself:

- Both versions return **all tied employees** (Asha and Bhanu, Gita and Gita). If
  you want exactly one row per department, use `ROW_NUMBER()` instead of `RANK()`
  and add a tie-breaker to the `ORDER BY`, e.g. `ORDER BY salary DESC, id`.
- Farid is listed as the top earner of department 9, which does not exist. Neither
  version filters orphan foreign keys; add `JOIN departments d ON e.dept_id = d.id`
  if the department must be real.

## 10. Delete duplicate records

**Portable version — this is the one to write:**

```sql
WITH ranked AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY name, dept_id, salary
               ORDER BY id
           ) AS rn
    FROM employees
)
DELETE FROM employees
WHERE id IN (SELECT id FROM ranked WHERE rn > 1);
```

The original version, for reference:

```sql
DELETE FROM employees
WHERE id NOT IN (
    SELECT MIN(id)
    FROM employees
    GROUP BY name, dept_id, salary
);
```

Traced: it deletes exactly **one row, id 8** (the second Gita), leaving 8 rows.
The intent is right. Three corrections to the way it was written:

| Problem in the original | What actually happens | Fix |
| --- | --- | --- |
| `DELETE FROM employees e` with an alias | Syntax error in MySQL and SQLite. PostgreSQL allows it. | Drop the alias, or use MySQL's `DELETE e FROM employees e`. |
| Subquery reads the same table being deleted | **MySQL error 1093** — "You can't specify target table 'employees' for update in FROM clause". | Wrap it: `… NOT IN (SELECT id FROM (SELECT MIN(id) AS id FROM employees GROUP BY …) x)`. |
| `NOT IN` against a subquery | Safe *here* only because `MIN(id)` over a `NOT NULL` primary key can never be `NULL`. Change it to `MIN(manager_id)` and the whole `DELETE` quietly deletes nothing. | Prefer `NOT EXISTS`, or the `ROW_NUMBER()` form above. See section 24. |

One thing that is **not** a bug, though it looks like one: `GROUP BY name, dept_id,
salary` groups rows where `dept_id IS NULL` together correctly, because `GROUP BY`
treats `NULL`s as equal. Traced with Gita's `dept_id` set to `NULL` on both rows —
it still correctly deletes one of them.

Always run the `SELECT` form first and read the rows you are about to destroy:

```sql
SELECT id, name, dept_id, salary
FROM employees e
WHERE e.id NOT IN (
    SELECT MIN(id) FROM employees GROUP BY name, dept_id, salary
);
```

## 11. Find employees who joined in the last 3 months

```sql
-- PostgreSQL
SELECT *
FROM employees
WHERE join_date >= CURRENT_DATE - INTERVAL '3 months'
  AND join_date <= CURRENT_DATE;
```

```sql
-- MySQL
SELECT *
FROM employees
WHERE join_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
  AND join_date <= CURDATE();
```

```sql
-- SQL Server
SELECT *
FROM employees
WHERE join_date >= DATEADD(MONTH, -3, CAST(GETDATE() AS DATE))
  AND join_date <= CAST(GETDATE() AS DATE);
```

**Correction —** the original gave only the PostgreSQL form and labelled it
generically. `INTERVAL '3 months'` is a syntax error in MySQL and SQL Server.

The upper bound matters: without `join_date <= CURRENT_DATE` the query also returns
future-dated rows, which exist in most HR systems for people who have signed but not
started.

The important performance point: keep the column bare on the left-hand side. This
version can use an index on `join_date`. The moment you write
`WHERE MONTHS_BETWEEN(CURRENT_DATE, join_date) <= 3` or
`WHERE YEAR(join_date) = 2025`, the column is wrapped in a function and the index is
no longer usable — see section 26.

## 12. Nth highest salary using a window function

```sql
SELECT DISTINCT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) s
WHERE rnk = 3;
```

Actual result: **1 row — 80000**.

**Correction — the original omitted `DISTINCT` and returned 2 rows, both 80000.**
The window function is evaluated per *row*, so every employee earning the 3rd
highest salary produces a row. Asked for "the Nth highest salary", returning it
twice is a wrong answer. Two rows came back because Gita appears twice at 80000.

Pick the ranking function to match the question:

| You want | Function | Why |
| --- | --- | --- |
| 3rd highest **distinct salary** | `DENSE_RANK()` | Ties share a rank and no rank numbers are skipped. |
| 3rd highest **salary position**, skipping past ties | `RANK()` | Two people tied at rank 2 means the next rank is 4, so `rnk = 3` returns nothing. |
| The 3rd **row**, one row guaranteed | `ROW_NUMBER()` | Never ties, but which of two equal salaries you get is arbitrary without a tie-breaker. |

Section 25 shows all three side by side on this data.

Avoid naming the alias `rank`. `RANK` is a reserved word in MySQL 8 and SQL Server,
so `AS rank` is a syntax error there unless you quote it. `rnk` avoids the issue
entirely.

## 13. Query optimization

- Measure before you change anything: `EXPLAIN` / `EXPLAIN ANALYZE`. `EXPLAIN`
  shows the *estimated* plan; `EXPLAIN ANALYZE` actually runs it and shows real row
  counts. A large gap between estimated and actual rows means stale statistics.
- Index the columns used in `WHERE`, `JOIN` and `ORDER BY` — and check section 26
  for the ways a query stops an existing index from being used.
- Avoid `SELECT *`: it defeats covering indexes and ships columns nobody reads.
- Use `LIMIT`, partitioning, or materialized views to bound the work.
- Rewrite a subquery as a join **only if the plan shows it helps** — see section 14.
- Keep statistics fresh (`ANALYZE`, `UPDATE STATISTICS`) so the optimizer's row
  estimates are believable.
- Prefer keyset pagination over deep `OFFSET`.

## 14. Joins vs subqueries

Which is faster — `JOIN` or subquery?

**Correction — the original answer ("JOIN is usually faster; subqueries create
temporary tables") is out of date and would be challenged.** It describes MySQL 5.5
and earlier. Modern optimizers (PostgreSQL, SQL Server, Oracle, MySQL 8) *unnest*
most `IN` and `EXISTS` subqueries into semi-joins and often produce a plan
identical to the hand-written join. Saying "JOIN is faster" flatly invites the
follow-up "did you check the plan?" — and the honest answer is no.

The answer that holds up:

| Construct | Row-count behaviour | When it wins |
| --- | --- | --- |
| `JOIN` | **Can multiply rows** if the right side has several matches. | You need columns from both tables. |
| `IN` / `EXISTS` (semi-join) | Returns each left row **at most once**, whatever the match count. | You only need to test existence. |
| `NOT EXISTS` | Safe with `NULL`s. | Anti-join — "rows with no match". |
| `NOT IN` | **Returns nothing** if the subquery yields a single `NULL`. | Avoid unless the column is `NOT NULL`. |
| Correlated subquery in `SELECT` | Runs conceptually once per outer row. | Rarely — a window function or a `LEFT JOIN` on an aggregate is usually better. |

So the real difference is **semantic, not performance**: a `JOIN` used as an
existence test duplicates rows and needs a `DISTINCT` to clean up, which costs a
sort. `EXISTS` never had the problem in the first place.

## 15. Get 10 employees from 21 to 30 based on ID (OFFSET / FETCH)

```sql
-- Standard SQL / SQL Server 2012+ / PostgreSQL / Oracle 12c+
SELECT *
FROM employees
ORDER BY id
OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;
```

**Correction — this returns rows 21 to 30 by *position*, not employees with `id`
between 21 and 30.** The heading conflates the two. Traced on a table with a gap in
the ids, `LIMIT 3 OFFSET 2` returned ids 4, 5 and 6 — not ids 3, 4 and 5. Once any
row is deleted the two readings diverge. If you actually want the id range, that is
a different query:

```sql
SELECT * FROM employees WHERE id BETWEEN 21 AND 30 ORDER BY id;
```

Two points to volunteer:

- `ORDER BY` is **mandatory** for `OFFSET … FETCH` in SQL Server, and without a
  deterministic `ORDER BY` in any engine, page 2 may repeat or skip rows that page 1
  already showed. Order by something unique.
- `OFFSET 20` is cheap; `OFFSET 200000` is not — the engine still generates and
  discards every skipped row. For deep pagination use a keyset:
  `WHERE id > :last_id ORDER BY id FETCH NEXT 10 ROWS ONLY`.

## 16. Get 10 employees from 21 to 30 based on ID (LIMIT / OFFSET)

```sql
-- MySQL, PostgreSQL, SQLite
SELECT *
FROM employees
ORDER BY id
LIMIT 10 OFFSET 20;
```

Same query as section 15 in the MySQL/PostgreSQL dialect, and the same two caveats
apply. MySQL also accepts the older positional form `LIMIT 20, 10` — note the
arguments are **offset first, then count**, which is the reverse of the
`LIMIT 10 OFFSET 20` reading and an easy way to return the wrong page.

## 17. What is a covering index?

**Answer:** an index is *covering* for a query when it contains every column that
query needs, so the engine answers entirely from the index and never touches the
table. SQL Server calls the avoided step a *key lookup*; PostgreSQL calls the
successful case an *index-only scan*; MySQL's `EXPLAIN` shows `Using index`.

**Correction —** "covering" is not a property of an index on its own, which is how
the original phrased it. The same index covers one query and not another. Saying
"I created a covering index" without naming the query it covers is the giveaway
that the concept is half-understood.

```sql
CREATE INDEX idx_orders_covering
    ON orders(customer_id, status, total_amount);
```

That index covers:

```sql
SELECT status, total_amount FROM orders WHERE customer_id = 7;
```

…and does **not** cover this, because `order_date` is not in the index:

```sql
SELECT status, order_date FROM orders WHERE customer_id = 7;
```

Costs: more storage, and every `INSERT`/`UPDATE` that touches any of the three
columns must maintain the index. PostgreSQL adds one more condition — an index-only
scan also needs the visibility map to be current, so it can silently stop happening
after a burst of writes until `VACUUM` runs.

## 18. How do you handle high write throughput tables?

- **Batch inserts** instead of row-by-row, and use multi-row `INSERT` or `COPY` /
  `LOAD DATA` for bulk paths.
- **Partition or shard** so writes spread across partitions rather than contending
  on one hot page.
- **Minimize index count** — every index is an extra structure to maintain on each
  write. This is the single biggest lever.
- **Watch the insert hot spot:** a monotonically increasing clustered key puts every
  insert on the same last page. That is good for fragmentation and bad for
  concurrency; a random UUID key is the reverse. Neither is free.
- **Asynchronous or queue-based ingestion** (Kafka → batched consumer) so the write
  spike is absorbed outside the database.
- **Loosen durability deliberately if the data allows it** — group commit, or
  `innodb_flush_log_at_trx_commit = 2` — and be explicit that this trades a window
  of data loss for throughput.
- **Keep transactions short** so locks are held briefly and the undo/WAL stays small.

## 19. Composite index vs covering index

| Concept | Description | Example |
| --- | --- | --- |
| **Composite index** | Several **key** columns. Order matters: it fixes the sort order and which prefixes are usable. | `CREATE INDEX idx ON orders(customer_id, status)` |
| **Covering index** | Holds every column a given query needs, so no table access is required. | `CREATE INDEX idx ON orders(status) INCLUDE (customer_id, amount)` |

They are not alternatives — a composite index is often *also* covering. The
difference is the role of the extra columns: key columns can be searched and
sorted on; `INCLUDE` columns are stored in the leaf only, so they satisfy the
`SELECT` list but cannot be used for seeking.

**Correction —** the original gave the `INCLUDE` example with no dialect note.
`INCLUDE` exists in **SQL Server** and **PostgreSQL 11+**. **MySQL has no
`INCLUDE`** — to get the same effect you add the column as a trailing key column:
`CREATE INDEX idx ON orders(status, customer_id, amount)`.

## 20. How indexes work: index types

**Correction —** the original numbering skipped 4 and jumped from 3 to 5. The types
are renumbered 1–9 here with no content removed.

| # | Type | Best for | Note |
| --- | --- | --- | --- |
| 1 | Single-column | Equality and range on one column | The baseline case. |
| 2 | Composite | Multi-column filters | Leftmost-prefix rule applies. |
| 3 | Unique | Natural keys | `NULL` handling differs by engine. |
| 4 | Clustered | Range scans, PK lookups | One per table; SQL Server / InnoDB. |
| 5 | Non-clustered | Selective lookups on other columns | May need a key lookup. |
| 6 | Full-text | Word and phrase search in long text | `MATCH … AGAINST` / `tsvector`. |
| 7 | Hash | Equality only | Useless for `>`, `<`, `BETWEEN`, `ORDER BY`. |
| 8 | Bitmap | Low-cardinality columns in a warehouse | **Oracle.** Not PostgreSQL. |
| 9 | Partial / filtered | A hot subset of rows | Smaller and cheaper to maintain. |

### 1. Single-column index

An index on one column. The database keeps a sorted B-tree of the values, so a
lookup is a short tree descent instead of a scan of every row.

```sql
CREATE INDEX idx_users_email ON users(email);
```

```sql
SELECT * FROM users WHERE email = 'abc@example.com';
```

The engine descends the B-tree to the matching key, reads the row locator, and
fetches the row — a handful of page reads instead of the whole table. Note the
second step: because this is `SELECT *`, the index alone cannot answer the query.

### 2. Composite (multi-column) index

An index on several columns together, sorted by the first column, then the second
within each first value, and so on.

```sql
CREATE INDEX idx_orders_customer_date
    ON orders(customer_id, order_date);
```

```sql
SELECT * FROM orders
WHERE customer_id = 101
  AND order_date > '2025-01-01';
```

**Leftmost-prefix rule:** the index supports a *seek* only on a leading prefix of
its columns.

| Filter | Can it seek on `(customer_id, order_date)`? |
| --- | --- |
| `customer_id = ?` | Yes — leading column. |
| `customer_id = ? AND order_date > ?` | Yes — full prefix, the ideal case. |
| `order_date > ?` alone | **No seek** — `order_date` is not the leading column. |

**Correction —** the original said the index "won't work" for `order_date` alone.
That is too absolute. The engine cannot *seek*, but it can still choose a full
**index scan** — reading the whole index instead of the whole table — which is often
much cheaper because the index is narrower. MySQL 8.0.13+ can also use an **index
skip scan** when the leading column has few distinct values. So the honest phrasing
is "no seek", not "won't be used".

### 3. Unique index

Guarantees no two rows share the indexed value, and gives the optimizer the
knowledge that a match returns at most one row.

```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
```

Used for natural keys such as email or username. A `UNIQUE` constraint is
implemented by a unique index, but the two are not identical — a constraint is part
of the logical model and can be targeted by a foreign key; a bare unique index is a
physical object.

**`NULL` handling differs, and it gets asked:**

| Engine | Rows with `NULL` in a unique column |
| --- | --- |
| PostgreSQL, Oracle, MySQL | **Many allowed** — two `NULL`s are not "equal", so they do not conflict. |
| SQL Server | **Only one** allowed. Use a filtered unique index to get the other behaviour. |

### 4. Clustered index

Determines the physical order of rows. One per table. In MySQL (InnoDB) the primary
key is the clustered index by default.

```sql
-- SQL Server syntax only. MySQL has no CREATE CLUSTERED INDEX statement -
-- in InnoDB you change the clustering by changing the PRIMARY KEY.
CREATE CLUSTERED INDEX idx_emp_id ON employees(emp_id);
```

Best for range queries, because the matching rows are physically adjacent:

```sql
SELECT * FROM employees WHERE emp_id BETWEEN 100 AND 200;
```

### 5. Non-clustered index

A separate structure holding the key plus a row locator.

```sql
CREATE INDEX idx_employee_name ON employees(name);
```

Ideal for searching by non-primary-key columns, and you can have many per table.
The difference from clustered: a clustered index *is* the table's row order; a
non-clustered index is a lookup structure beside it.

### 6. Full-text index

For word, phrase and relevance search inside long text — the thing `LIKE '%word%'`
cannot do with an index.

```sql
-- MySQL
CREATE FULLTEXT INDEX idx_articles_content ON articles(content);

SELECT * FROM articles
WHERE MATCH(content) AGAINST ('database optimization');
```

```sql
-- PostgreSQL equivalent: GIN index over a tsvector
CREATE INDEX idx_articles_content
    ON articles USING GIN (to_tsvector('english', content));

SELECT * FROM articles
WHERE to_tsvector('english', content)
      @@ plainto_tsquery('english', 'database optimization');
```

### 7. Hash index

A hash table — one probe for an equality match, and nothing else.

```sql
-- PostgreSQL
CREATE INDEX idx_users_id_hash ON users USING HASH(id);
```

```sql
SELECT * FROM users WHERE id = 100;
```

Useless for `>`, `<`, `BETWEEN`, `ORDER BY` or prefix matching, because hashing
destroys ordering. In MySQL, `MEMORY` tables default to hash indexes while InnoDB
builds its *adaptive hash index* automatically — you do not create it.

### 8. Bitmap index (Oracle)

A bitmap per distinct value, so filters on several low-cardinality columns can be
combined with cheap bitwise AND/OR.

```sql
-- Oracle
CREATE BITMAP INDEX idx_employee_gender ON employees(gender);
```

Good for columns with few distinct values (gender, status, active flag) in
read-mostly analytical tables. Terrible for OLTP: a single-row update locks a whole
bitmap segment, blocking many rows at once.

**Correction —** the original heading said "mainly in Oracle, PostgreSQL".
**PostgreSQL has no bitmap index type** and `CREATE BITMAP INDEX` is a syntax error
there. The confusion comes from PostgreSQL's *bitmap heap scan* / *bitmap index
scan*, which are runtime **execution strategies** that build a throwaway bitmap from
ordinary B-tree indexes. Same word, different thing — and claiming PostgreSQL has
bitmap indexes is the kind of detail a DBA interviewer will catch.

### 9. Partial index / filtered index

Indexes only the rows matching a condition, so it is smaller and cheaper to
maintain.

```sql
-- PostgreSQL
CREATE INDEX idx_active_users ON users(email) WHERE active = true;
```

```sql
-- SQL Server calls it a filtered index
CREATE INDEX idx_active_users ON users(email) WHERE active = 1;
```

The planner uses it only when it can prove the query's predicate implies the
index's predicate — so `WHERE active = true AND email = ?` uses it, and
`WHERE email = ?` alone does not. **MySQL has no partial indexes** (its
`INDEX(col(10))` "prefix index" is a different feature: indexing the first N
characters of a string).

### Effect of indexes on operations

| Operation | Effect |
| --- | --- |
| **SELECT** | Much faster — when the query lets the index be used (section 26). |
| **INSERT / UPDATE / DELETE** | Slower; every index on an affected column must be maintained. |
| **Storage** | More disk, and more memory competing for the buffer pool. |
| **Maintenance** | Statistics must stay fresh, and heavily updated indexes fragment. |

## 21. Do we need to create a clustered index explicitly, or is it automatic?

**Correction —** the original jumped straight to a heading numbered "3. PostgreSQL"
with no 1 or 2. All four engines are covered here.

| Database | Clustered index auto-created? | When / how | Can you create it manually? |
| --- | --- | --- | --- |
| **MySQL (InnoDB)** | Yes | On the `PRIMARY KEY`; else first `UNIQUE NOT NULL`; else a hidden row id. | Not directly — only by changing the PK. |
| **SQL Server** | Yes | On the `PRIMARY KEY` by default. | Yes — `PRIMARY KEY NONCLUSTERED` plus an explicit `CREATE CLUSTERED INDEX`. |
| **PostgreSQL** | No | No clustered index exists as a concept. | Only the one-off `CLUSTER` command. |
| **Oracle** | No | Heap tables by default. | Via an Index-Organized Table (IOT). |

### MySQL (InnoDB)

Automatic and unavoidable. Every InnoDB table is clustered on something; you only
choose *what* by choosing the primary key. This is why a random UUID primary key
hurts an InnoDB write path — inserts land at random points in the clustered index
and split pages.

### SQL Server

`PRIMARY KEY` implies `CLUSTERED` unless you say otherwise, so it is automatic by
default but fully overridable — see section 2 for the correct statements.

### PostgreSQL

- **Automatic:** no. PostgreSQL does not cluster the table by the primary key.
- A primary key creates a unique B-tree index, but the heap row order is unchanged.

Manual, one-time clustering:

```sql
CREATE INDEX idx_users_created_at ON users(created_at);
CLUSTER users USING idx_users_created_at;
```

This physically rewrites the table in index order. Important caveats:

- The table does **not stay** clustered. New and updated rows go wherever there is
  free space, so the order decays.
- `CLUSTER` takes an `ACCESS EXCLUSIVE` lock and rewrites the whole table — it is
  not something you run casually on a live production table.
- Re-run it periodically if you want to maintain the ordering.

### Oracle

Heap-organised by default. To get PK-ordered storage, declare an Index-Organized
Table:

```sql
CREATE TABLE orders (
    order_id   INT PRIMARY KEY,
    order_date DATE
) ORGANIZATION INDEX;
```

## 22. When does a LEFT JOIN silently become an INNER JOIN? (new)

*New section — this was not in the original file, and it is the single most common
join mistake in interviews.*

A `LEFT JOIN` produces `NULL`-filled rows where the right table has no match. Any
`WHERE` condition on a right-table column then tests those `NULL`s, and almost every
comparison against `NULL` is `UNKNOWN`, which `WHERE` discards. The unmatched rows
vanish and the outer join has quietly become an inner join.

**The bug**, traced on the section 4 sample data:

```sql
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id
WHERE d.location = 'Bangalore';
```

Returns **3 rows** — Asha, Bhanu, Chetan. Every employee outside Engineering is
gone, including Esha and Farid who have no department at all. The `LEFT` keyword had
no effect whatsoever.

**The fix — move the condition into the `ON` clause:**

```sql
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d
       ON e.dept_id = d.id
      AND d.location = 'Bangalore';
```

Returns **9 rows** — every employee, with `d.name` filled in for the three in
Bangalore and `NULL` for the other six.

**The rule**

| Clause | Runs | Effect on a LEFT JOIN |
| --- | --- | --- |
| `ON` | **While** matching, before `NULL`-filling | Decides *what counts as a match*. Preserves all left rows. |
| `WHERE` | **After** the join is built | Filters the finished result. Drops `NULL`-filled rows unless the test is `IS NULL`. |

The one safe `WHERE` on a right-table column is `IS NULL`, because it is the only
predicate that is *true* for a `NULL`-filled row. That is the deliberate anti-join
of section 6 — the same shape, used on purpose.

Quick self-check: if you find yourself writing `LEFT JOIN … WHERE right.col = 'x'`,
you almost certainly meant either the `ON` version above, or a plain `INNER JOIN`.

## 23. GROUP BY and HAVING rules (new)

*New section.*

**Logical order of evaluation** — this explains every rule below:

```text
FROM / JOIN  ->  WHERE  ->  GROUP BY  ->  HAVING  ->  SELECT
             ->  DISTINCT  ->  ORDER BY  ->  LIMIT / OFFSET
```

| Rule | Why | Consequence |
| --- | --- | --- |
| `WHERE` cannot reference an aggregate | `WHERE` runs **before** groups exist | `WHERE COUNT(*) > 1` is an error. Use `HAVING`. |
| `HAVING` filters groups | It runs **after** `GROUP BY` | `HAVING COUNT(*) > 1` is the duplicate-finding idiom. |
| Every `SELECT` column must be grouped or aggregated | A group is one output row | `SELECT dept_id, name … GROUP BY dept_id` is ambiguous — which name? |
| `SELECT` aliases are not visible to `WHERE` | `SELECT` runs after `WHERE` | Repeat the expression, or wrap it in a subquery / CTE. |
| `SELECT` aliases **are** visible to `ORDER BY` | `ORDER BY` runs after `SELECT` | `SELECT salary*12 AS annual … ORDER BY annual` works. |

**Filter rows first, groups second.** Putting a row-level condition in `HAVING`
still gives the right answer but makes the engine build groups it is about to throw
away:

```sql
-- prefer this
SELECT dept_id, COUNT(*)
FROM employees
WHERE salary > 50000      -- row filter, before grouping
GROUP BY dept_id
HAVING COUNT(*) > 1;      -- group filter, after grouping
```

**Strictness differs by engine, and this trips people up.** PostgreSQL and Oracle
reject a bare non-aggregated column in the `SELECT` list. MySQL rejects it too under
`ONLY_FULL_GROUP_BY`, which has been **on by default since MySQL 5.7** — so old
tutorial queries that used to run now fail. SQLite accepts it and picks an arbitrary
row (verified). Never rely on that.

Two more behaviours worth stating:

- `GROUP BY` puts **all `NULL`s in a single group**, even though `NULL = NULL` is
  not true. `GROUP BY` uses "not distinct from", not `=`.
- `HAVING` without `GROUP BY` treats the whole table as one group.
  `SELECT COUNT(*) FROM employees HAVING COUNT(*) > 5` returns one row (verified:
  `9`) when the table has more than 5 rows, and no rows otherwise.
- `COUNT(*)` counts rows; `COUNT(col)` counts **non-`NULL`** values. On the sample
  data: `COUNT(*) = 9`, `COUNT(dept_id) = 8`, `COUNT(DISTINCT dept_id) = 5`
  (verified). Three different numbers from the same column.

## 24. NULL comparison semantics (new)

*New section. `NULL` means "unknown", not "empty" and not "zero", and every rule
below follows from that.*

| Expression | Result | Note |
| --- | --- | --- |
| `NULL = NULL` | `UNKNOWN` | Not `TRUE`. `WHERE` treats `UNKNOWN` as "discard". |
| `NULL <> 1` | `UNKNOWN` | So `WHERE dept_id <> 1` **excludes** `NULL` rows. |
| `col IS NULL` | `TRUE` / `FALSE` | The only correct null test. |
| `NULL + 1`, `'a' \|\| NULL` | `NULL` | Arithmetic and concatenation propagate `NULL`. |
| `COUNT(col)` | Skips `NULL`s | But `COUNT(*)` counts the rows. |
| `SUM` / `AVG` | Skip `NULL`s | `AVG` divides by the non-null count, not the row count. |
| `GROUP BY`, `DISTINCT`, `UNION` | Treat `NULL`s as **equal** | Deliberately different from `=`. |

**Verified on the section 4 sample data:**

- `WHERE dept_id = NULL` returns **0 rows**; `WHERE dept_id IS NULL` returns **1**.
- `WHERE dept_id <> 1` returns **5 rows**, not 6 — Esha's `NULL` is excluded even
  though her department is obviously not 1.

**The `NOT IN` trap — the one that actually costs people offers:**

```sql
-- Which departments have no employees?
SELECT d.name
FROM departments d
WHERE d.id NOT IN (SELECT dept_id FROM employees);
```

Returns **0 rows**. The correct answer is Finance. It fails because `employees`
contains one `NULL` `dept_id` (Esha), so `5 NOT IN (1,2,3,4,9,NULL)` expands to
`5<>1 AND … AND 5<>NULL`, and that trailing `UNKNOWN` makes the whole `AND` chain
`UNKNOWN` for **every** row. `NOT IN` over a nullable column can only ever return
zero rows or error — never a partial answer, which is why the bug looks like "the
data is fine, the query just finds nothing".

Both fixes return Finance (verified):

```sql
-- preferred: NOT EXISTS is null-safe by construction
SELECT d.name
FROM departments d
WHERE NOT EXISTS (
    SELECT 1 FROM employees e WHERE e.dept_id = d.id
);
```

```sql
-- or exclude the NULLs explicitly
SELECT d.name
FROM departments d
WHERE d.id NOT IN (
    SELECT dept_id FROM employees WHERE dept_id IS NOT NULL
);
```

Note that plain `IN` is **not** affected the same way — `IN` with a `NULL` in the
list still returns the rows that match something, it just never returns `FALSE` for
the rest. Only the negation breaks.

**Useful null-handling functions**

| Function | Engines | Does |
| --- | --- | --- |
| `COALESCE(a, b, c)` | all | First non-`NULL` argument. |
| `NULLIF(a, b)` | all | `NULL` if `a = b`, else `a`. Handy to avoid divide-by-zero. |
| `IFNULL(a, b)` | MySQL | Two-argument `COALESCE`. |
| `ISNULL(a, b)` | SQL Server | Two-argument `COALESCE`. |
| `a IS NOT DISTINCT FROM b` | PostgreSQL | `=` that treats `NULL = NULL` as true. |

## 25. Window functions: ROW_NUMBER vs RANK vs DENSE_RANK (new)

*New section, extracted from the confusion in sections 12 and Part 3.*

A window function computes a value **per row** over a set of related rows, without
collapsing them the way `GROUP BY` does. That is the whole difference: `GROUP BY`
returns one row per group; a window function returns every row plus an extra column.

Run on the section 4 sample data, ordered by salary descending — **verified output**:

| salary | ROW_NUMBER | RANK | DENSE_RANK |
| --- | --- | --- | --- |
| 120000 | 1 | 1 | 1 |
| 90000 | 2 | 2 | 2 |
| 90000 | 3 | 2 | 2 |
| 80000 | 4 | 4 | 3 |
| 80000 | 5 | 4 | 3 |
| 70000 | 6 | 6 | 4 |
| 60000 | 7 | 7 | 5 |
| 50000 | 8 | 8 | 6 |
| 40000 | 9 | 9 | 7 |

Read the two tied pairs and the whole family becomes obvious:

- **`ROW_NUMBER()`** never ties. Which of the two 90000 rows gets 2 and which gets 3
  is **arbitrary** unless you add a tie-breaker (`ORDER BY salary DESC, id`).
- **`RANK()`** gives ties the same number then **skips**: after two rows at rank 2,
  the next is 4. So `WHERE rnk = 3` returns **nothing** here — a silent empty result.
- **`DENSE_RANK()`** gives ties the same number and **does not skip**: 1, 2, 2, 3.
  This is the one for "Nth highest distinct salary".

Anatomy of the clause:

```sql
SELECT name,
       dept_id,
       salary,
       DENSE_RANK() OVER (
           PARTITION BY dept_id          -- restart numbering per department
           ORDER BY     salary DESC      -- ranking order within the partition
       ) AS salary_rank
FROM employees;
```

Other functions on the same `OVER (…)` mechanism:

| Function | Returns |
| --- | --- |
| `LAG(col, 1)` / `LEAD(col, 1)` | The previous / next row's value — month-over-month deltas. |
| `SUM(col) OVER (ORDER BY …)` | A running total. |
| `NTILE(4)` | Bucket number, for quartiles. |
| `FIRST_VALUE` / `LAST_VALUE` | First / last value in the window frame. |

Two rules that get tested:

- A window function is evaluated **after** `WHERE`, `GROUP BY` and `HAVING`, so you
  **cannot filter on its alias** in the same query. Wrap it in a subquery or CTE —
  which is exactly why section 12 needs the nested `SELECT`.
- Window functions are available in PostgreSQL 8.4+, SQL Server 2012+, Oracle 8i+,
  **MySQL 8.0+** and SQLite 3.25+. On MySQL 5.7 they do not exist, which is why the
  self-join form in section 9 still shows up in older codebases.

## 26. When is an index NOT used? (new)

*New section. Knowing when an index is ignored is worth more in an interview than
listing index types.*

| Query pattern | Why the index is skipped | Fix |
| --- | --- | --- |
| `WHERE YEAR(join_date) = 2025` | The column is wrapped in a function, so the indexed values no longer match what is compared. | Rewrite as a range: `join_date >= '2025-01-01' AND join_date < '2026-01-01'`. Or build an expression index. |
| `WHERE name LIKE '%gita'` | A leading wildcard has no prefix to seek on. | `LIKE 'gita%'` works. For true substring search use a full-text index. |
| `WHERE phone = 9876543210` on a `VARCHAR` column | Implicit type conversion makes the engine cast the column, which is a function call in disguise. | Compare like with like: `phone = '9876543210'`. |
| `WHERE a = 1 OR b = 2` | One index cannot serve both sides. | Index both and let the engine merge, or `UNION` two indexed queries. |
| Filter on a non-leading composite column | Leftmost-prefix rule (section 20.2). | Reorder the index, or add one that leads with that column. |
| Predicate matches most of the table | A full scan is genuinely cheaper than index lookups plus row fetches. | Nothing to fix — this is the optimizer being right. |
| Stale statistics | The optimizer's row estimate is wrong, so it costs the plan wrongly. | `ANALYZE` / `UPDATE STATISTICS`. |
| `WHERE status != 'ACTIVE'` | Negation usually matches too much to be worth seeking. | Often fine. A partial index on the rare values can help. |
| Tiny table | The whole table fits in one or two pages. | Nothing to fix. |

Always confirm with `EXPLAIN` rather than assuming. The giveaway words are `Seq
Scan` (PostgreSQL), `type: ALL` (MySQL) and `Table Scan` / `Clustered Index Scan`
(SQL Server) where you expected a seek.

The general principle behind the first three rows: **keep the indexed column bare on
one side of the comparison.** The moment it is wrapped in a function, a cast, or a
concatenation, the B-tree ordering no longer corresponds to what you are comparing,
and the index cannot be used.

## 27. DELETE vs TRUNCATE vs DROP (new)

*New section.*

| Aspect | `DELETE` | `TRUNCATE` | `DROP` |
| --- | --- | --- | --- |
| Removes | Selected rows | All rows | Rows **and** the table definition |
| Command family | DML | DDL | DDL |
| `WHERE` clause | Yes | No | No |
| Speed on a big table | Slow — row by row, fully logged | Fast — deallocates pages | Fast |
| Rollback | Yes | PostgreSQL & SQL Server: yes. **MySQL/InnoDB: no** — it implicitly commits. | Same split as `TRUNCATE` |
| Triggers fired | Yes (per row) | No | No |
| Identity / auto-increment | Not reset | Reset to the seed | Gone with the table |

Three things to add when asked:

- `TRUNCATE` normally **cannot run** on a table referenced by a foreign key from
  another table. PostgreSQL lets you say `TRUNCATE … CASCADE`; MySQL just refuses.
- `DELETE FROM t` with no `WHERE` and `TRUNCATE TABLE t` leave the same empty table,
  but `DELETE` writes an undo/redo record per row — on millions of rows that is the
  difference between seconds and hours, and it can blow up the transaction log.
- After a large `DELETE`, PostgreSQL does not return the space to the OS until
  `VACUUM` runs, and the table stays physically large. `TRUNCATE` does free it.

There is no `TRUNCATE ... WHERE`. If someone offers you one, that is the trick.

## 28. Normal forms: 1NF to BCNF (new)

*New section. The one-line version: store one fact in exactly one place, and make
every non-key column depend on the key, the whole key, and nothing but the key.*

| Form | Requirement | Violation looks like | Fix |
| --- | --- | --- | --- |
| **1NF** | Every column holds a single atomic value; no repeating groups. | `phone_numbers = '999,888,777'` in one column, or `item1`, `item2`, `item3` columns. | Split into rows in a child table. |
| **2NF** | 1NF **and** no non-key column depends on only *part* of a composite key. | In `order_items(order_id, product_id, qty, product_name)`, `product_name` depends on `product_id` alone. | Move `product_name` to `products`. |
| **3NF** | 2NF **and** no non-key column depends on another non-key column (no transitive dependency). | `employees(id, dept_id, dept_name)` — `dept_name` depends on `dept_id`, not on `id`. | Move `dept_name` to `departments`. |
| **BCNF** | 3NF **and** every determinant is a candidate key. | Overlapping candidate keys where a non-key attribute determines part of a key. | Decompose further. Rare in practice. |

The practical anomalies these prevent, which is what the interviewer is really
asking about:

| Anomaly | Example with `employees(id, dept_id, dept_name)` |
| --- | --- |
| **Update** | Renaming a department means updating every employee row; miss one and the data contradicts itself. |
| **Insert** | You cannot record a new department until it has at least one employee. |
| **Delete** | Deleting the last employee of a department erases the department entirely. |

**When to denormalize deliberately:** read-heavy paths where a join is measurably
hot, reporting and analytics tables, and cached aggregates such as `order_count` on
a customer row. The trade is always the same — faster reads for the obligation to
keep the duplicate in step, which you should do with a trigger, a materialized view
or an explicit background job, never by hoping the application remembers.

Most OLTP schemas target 3NF. Going beyond BCNF (4NF, 5NF) almost never comes up
outside a database-theory interview.
