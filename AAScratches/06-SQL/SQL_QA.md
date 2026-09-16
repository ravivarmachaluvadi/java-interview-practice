# SQL Interview Q&A

Answers to expect for SQL interview questions: clustered vs non-clustered and the other index types, practice queries on an employees/departments schema, and how to keep queries efficient at scale.

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

## 1. Difference between clustered and non-clustered index

**Short and clear answer (ideal for a quick response):**

A clustered index determines the physical order of data in a table — there can be only one clustered index per table because the data rows are stored in that order.

A non-clustered index, on the other hand, creates a separate structure that stores key values and pointers (row locators) to the actual data — you can have multiple non-clustered indexes per table.

| Feature | Clustered Index | Non-Clustered Index |
| --- | --- | --- |
| Data storage | Data is physically stored in the same order as the index (B-tree leaf nodes contain actual data rows). | Index is stored separately from the data. Leaf nodes contain pointers (row locators) to the actual data rows. |
| Number per table | Only **one** (because data can be sorted only one way). | Can have **many** per table (e.g., up to 999 in SQL Server). |
| Performance | Faster for **range queries** and **sorting operations** (e.g., `ORDER BY`, `BETWEEN`). | Better for **lookup queries** or **frequent filtering** on non-key columns. |
| Storage | No separate structure; data itself is the index. | Requires **additional storage** space. |
| Example use case | Clustered index on `PRIMARY KEY` or `ID` column (frequently used for joins and sorting). | Non-clustered index on `email` or `name` for faster filtering or search. |

```sql
-- Clustered Index (usually on Primary Key)
CREATE CLUSTERED INDEX idx_orders_id ON Orders(order_id);

-- Non-Clustered Index (on another column)
CREATE NONCLUSTERED INDEX idx_orders_customer ON Orders(customer_id);
```

## 2. Can we create a clustered index on non-primary columns? Pros and cons

Yes, you can create a clustered index on non-primary key columns in most relational databases (like SQL Server, PostgreSQL, and MySQL with InnoDB). But it's a design decision that comes with trade-offs.

By default:

- The primary key column is used for the clustered index.
- But you can override this and create a clustered index on a different column (or composite columns).

```sql
-- Suppose we have this table
CREATE TABLE Orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    order_date DATE,
    amount DECIMAL(10,2)
);

-- By default, the clustered index will be on order_id (the primary key).

-- But you can drop it and create one on order_date:
DROP INDEX IF EXISTS PK_Orders;  -- remove the default clustered index

CREATE CLUSTERED INDEX idx_orders_order_date ON Orders(order_date);
```

**Benefits**

| Benefit | Explanation |
| --- | --- |
| Faster range queries | Great if your queries often use `WHERE order_date BETWEEN ...` or need to fetch recent data quickly. |
| Better performance for sequential reads | Ideal for reports, analytics, or "time-series" data patterns (like logs or transactions sorted by timestamp). |
| Reduced I/O | Queries that read data in the same order as the clustering key (e.g. `ORDER BY order_date`) can avoid sorting operations. |

**Drawbacks**

| Drawback | Explanation |
| --- | --- |
| Only one clustered index allowed | You lose the benefit of clustering by the primary key (e.g., joins or lookups by `order_id` become slower). |
| All non-clustered indexes include the clustering key internally | That means larger index size and potentially higher storage + slower updates. |
| More maintenance during inserts/updates | Inserting rows out of order (e.g., random dates) can cause page splits and fragmentation, slowing performance. |
| Not ideal for frequently updated columns | If the clustering column (like `order_date`) changes often, the DB must move rows physically — very expensive. |

**Which column to cluster on**

| Situation | Recommended Clustered Index |
| --- | --- |
| Table mainly accessed by **primary key lookups** | Keep clustered index on the **primary key** |
| Table mainly accessed by **range queries (dates, time-series)** | Cluster on **that range column** (e.g., `created_at`) |
| Table mostly append-only (insert at end) | Cluster on **increasing column** (like `id` or `timestamp`) |
| Table used in frequent **joins** on a certain foreign key | Cluster on that **foreign key** (if it benefits most queries) |

## 3. Print student name, grade and marks using a grade range table

Write a query to print Student Name, Grade and Marks for each student. Grade will be defined from the grade table based on the range of mark. Sort the data based on Grade; if multiple students have the same Grade, then order them based on Mark.

| Student (cols) | grades (cols) |
| --- | --- |
| id | grade |
| name | min_mark |
| marks | max_mark |

```sql
SELECT
    s.name,
    g.grade,
    s.marks
FROM
    student s
JOIN
    grades g
ON
    s.marks BETWEEN g.min_mark AND g.max_mark
ORDER BY
    g.grade,
    s.marks;
```

## 4. Reference schema: departments and employees tables

The practice queries in sections 6 to 16 use these two tables.

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
| dept_id | INT (FK) | Department employee belongs to |
| salary | DECIMAL(10,2) | Monthly salary |
| join_date | DATE | Date of joining |
| manager_id | INT | Employee ID of manager (self reference) |

## 5. How do you ensure database queries are efficient in large-scale applications?

- **Write optimized SQL:** I always fetch only the required columns and rows — using projections instead of `SELECT *`, and applying proper `WHERE`, `LIMIT`, and pagination clauses. I also avoid unnecessary joins and subqueries where possible.
- **Use proper indexing:** I ensure frequently filtered, joined, or sorted columns are indexed appropriately, balancing read vs. write performance. I also monitor for unused or redundant indexes.
- **Analyze execution plans:** I regularly use tools like `EXPLAIN` (in PostgreSQL/MySQL) to understand query execution paths and identify full table scans or costly joins.
- **Optimize schema design:** I use normalization to remove redundancy, but denormalize where it helps reduce complex joins for read-heavy workloads.
- **Caching:** I cache frequent queries using Redis or application-level caches to reduce repetitive database hits.
- **Connection pooling and batching:** I use efficient connection pools (like HikariCP in Spring Boot) and batch operations to minimize round-trips to the DB.
- **Monitoring and profiling:** I set up query performance metrics (e.g., slow query logs, APM tools like New Relic or Datadog) and continuously review slow queries and optimize them.
- **Sharding and partitioning (for very large datasets):** I partition large tables or use sharding strategies to keep data access efficient.

## 6. Find employees without a department

```sql
SELECT e.name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id
WHERE d.id IS NULL;
```

## 7. Second highest salary

```sql
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
OFFSET 1 ROW FETCH NEXT 1 ROW ONLY;
```

## 8. Duplicate records

```sql
SELECT name, COUNT(*)
FROM employees
GROUP BY name
HAVING COUNT(*) > 1;
```

## 9. Highest paid employee in each department

```sql
SELECT e.*
FROM employees e
JOIN (
  SELECT dept_id, MAX(salary) AS max_sal
  FROM employees
  GROUP BY dept_id
) m ON e.dept_id = m.dept_id AND e.salary = m.max_sal;
```

## 10. Delete duplicate records

```sql
DELETE FROM employees e
WHERE e.id NOT IN (
  SELECT MIN(id)
  FROM employees
  GROUP BY name, dept_id, salary
);
```

## 11. Find employees who joined in the last 3 months

```sql
SELECT *
FROM employees
WHERE join_date >= CURRENT_DATE - INTERVAL '3 months';
```

## 12. Nth highest salary using a window function

```sql
SELECT salary
FROM (
  SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
  FROM employees
) s
WHERE rnk = 3;
```

## 13. Query optimization

- Use `EXPLAIN` / `EXPLAIN ANALYZE` to inspect query plan.
- Ensure indexes exist on filtering/join columns.
- Avoid `SELECT *`.
- Use `LIMIT`, partitioning, or materialized views.
- Optimize subqueries → joins.
- Analyze statistics regularly.

## 14. Joins vs subqueries

Which is faster — JOIN or subquery?

**Answer:** Usually JOIN is faster since it's optimized internally. Subqueries may lead to temporary tables or nested execution.

## 15. Get 10 employees from 21 to 30 based on ID (OFFSET / FETCH)

```sql
SELECT *
FROM employees
ORDER BY id
OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;
```

## 16. Get 10 employees from 21 to 30 based on ID (LIMIT / OFFSET)

```sql
SELECT *
FROM employees
ORDER BY id
LIMIT 10 OFFSET 20;
```

## 17. What is a covering index?

**Answer:** A covering index contains all columns required by a query, so the DB doesn't need to access the base table (no "key lookup"). It improves performance for read-heavy queries but increases storage and update cost.

Example:

```sql
CREATE INDEX idx_orders_covering ON orders(customer_id, status, total_amount);
```

## 18. How do you handle high write throughput tables?

**Answer:**

- Use batch inserts instead of row-by-row.
- Use partitioning or sharding.
- Minimize index count (each index adds write overhead).
- Consider asynchronous writes or queue-based ingestion.

## 19. Composite index vs covering index

| Concept | Description | Example |
| --- | --- | --- |
| **Composite Index** | Multiple key columns determine sort order and filtering. | `CREATE INDEX idx ON orders(customer_id, status)` |
| **Covering Index** | Includes *all columns used by a query* (some as key, some as included). | `CREATE INDEX idx ON orders(status) INCLUDE (customer_id, amount)` |

## 20. How indexes work: index types

### 1. Single-Column Index

- **Definition:** An index on one column.
- When you create an index on a column, the database creates a sorted structure that allows fast lookups.

For example, if you have:

```sql
CREATE INDEX idx_users_email ON users(email);
```

SQL engine will internally maintain a structure that maps each email value to its corresponding row location. So when you run:

```sql
SELECT * FROM users WHERE email = 'abc@gmail.com';
```

The database doesn't scan the whole table — it jumps directly to that record via the index.

### 2. Composite (Multi-Column) Index

- **Definition:** An index on multiple columns together.

Example:

```sql
CREATE INDEX idx_orders_customer_date ON orders(customer_id, order_date);
```

Use case: when queries use multiple columns in the `WHERE` clause:

```sql
SELECT * FROM orders WHERE customer_id = 101 AND order_date > '2025-01-01';
```

**Important rule (Prefix Rule):** the index is used only if the query filters by the first column or prefix columns.

- Works for `(customer_id)`
- Works for `(customer_id, order_date)`
- Won't work for `(order_date)` alone

### 3. Unique Index

- **Definition:** Ensures all values in the indexed column(s) are unique.

Example:

```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
```

Use case:

- Often used for natural keys like email, username, SSN.
- Ensures no duplicate values (same as `UNIQUE` constraint).

### 5. Clustered Index

- **Definition:** Determines the physical order of rows in the table.
- Each table can have only one clustered index.
- In MySQL (InnoDB), the primary key is the clustered index by default.

Example:

```sql
CREATE CLUSTERED INDEX idx_emp_id ON employees(emp_id);
```

Use case: best for columns used in range queries:

```sql
SELECT * FROM employees WHERE emp_id BETWEEN 100 AND 200;
```

Faster because data is physically ordered.

### 6. Non-Clustered Index

- **Definition:** A separate structure that points to the physical location of data.

Example:

```sql
CREATE INDEX idx_employee_name ON employees(name);
```

Use case:

- Ideal for searching by non-primary key columns.
- You can have multiple non-clustered indexes on a table.

Difference from clustered:

- Clustered: rearranges the table itself.
- Non-clustered: creates a separate lookup structure.

### 7. Full-Text Index

- **Definition:** Special type of index for text search (phrases, words, relevance).

Example:

```sql
CREATE FULLTEXT INDEX idx_articles_content ON articles(content);
```

Use case: for searching inside long text fields:

```sql
SELECT * FROM articles WHERE MATCH(content) AGAINST ('database optimization');
```

Supported by: MySQL, PostgreSQL (GIN / tsvector), SQL Server, etc.

### 8. Hash Index

- **Definition:** Uses a hash table for lookups — extremely fast for equality searches.

Example (PostgreSQL):

```sql
CREATE INDEX idx_users_id_hash ON users USING HASH(id);
```

Use case: for `=` searches, not for range queries. Example:

```sql
SELECT * FROM users WHERE id = 100;
```

Not useful for `>`, `<`, or `BETWEEN`.

### 9. Bitmap Index (mainly in Oracle, PostgreSQL)

- **Definition:** Uses bitmaps for each distinct value, very efficient for low-cardinality columns.

Example:

```sql
CREATE BITMAP INDEX idx_employee_gender ON employees(gender);
```

Use case:

- Columns with few distinct values (e.g., gender, status, active_flag).
- Useful in analytical queries (data warehouses).

### 10. Partial Index / Filtered Index

- **Definition:** Index only a subset of rows that meet a condition.

Example (PostgreSQL):

```sql
CREATE INDEX idx_active_users ON users(email) WHERE active = true;
```

Use case: when you often query by a condition like:

```sql
SELECT * FROM users WHERE active = true;
```

Saves space and maintenance overhead.

### Effect of indexes on operations

| Operation | Effect |
| --- | --- |
| **SELECT** | Much faster |
| **INSERT/UPDATE/DELETE** | Slightly slower (index needs to be updated) |
| **Storage** | More disk space used |
| **Maintenance** | Indexes must be rebuilt if data distribution changes significantly |

## 21. Do we need to create a clustered index explicitly, or is it automatic?

### 3. PostgreSQL

- **Automatic:** No.
- PostgreSQL does not automatically cluster the table by the primary key.
- A primary key automatically creates a unique B-tree index, but not clustered (physical order of rows is not changed).

**Manual clustering** — you can cluster manually:

```sql
CREATE INDEX idx_users_created_at ON users(created_at);
CLUSTER users USING idx_users_created_at;
```

This command physically reorders the table according to that index.

**Important:**

- The table does not stay clustered automatically — if you insert new rows later, they're appended.
- You can periodically re-run `CLUSTER` if you want to maintain physical order.

| Database | Clustered Index Auto-Created? | When / How | Can Create Manually? |
| --- | --- | --- | --- |
| **MySQL (InnoDB)** | Yes | On PRIMARY KEY | Not directly — only by changing PK |
| **SQL Server** | Yes | On PRIMARY KEY (by default) | Yes — specify explicitly |
| **PostgreSQL** | No | Only manually using `CLUSTER` | Yes (manual operation) |
| **Oracle** | No (different model) | Uses IOTs instead | Yes via IOT |
