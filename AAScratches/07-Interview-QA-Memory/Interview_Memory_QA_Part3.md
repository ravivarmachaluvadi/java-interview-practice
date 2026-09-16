# Interview Memory Q&A — Part 3

Mixed-topic memory notes (SQL clause order, nth-highest salary, duplicates, pivots, deadlocks, unions, joins, indexes, foreign keys, OOP concepts, and Spring exception-handling snippets) kept in the original order; the source had no item numbers, so they are numbered sequentially here.

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

The SQL standard defines the clause order as:

```sql
SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ... ORDER BY ... LIMIT ... OFFSET ...
```

So while `OFFSET` and `LIMIT` can be swapped in some databases, using the standard order ensures your SQL is portable and won't break when you switch engines.

## 2. Nth highest salary in SQL

```sql
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET N-1;
```

```sql
SELECT DISTINCT *
FROM (SELECT *,
         DENSE_RANK() OVER (ORDER BY salary DESC) AS rank
  FROM employees) ranked WHERE rank = N;
```

## 3. How do you detect duplicate rows in a table?

```sql
SELECT name, COUNT(*)
FROM employees
GROUP BY name
HAVING COUNT(*) > 1;
```

## 4. How do you pivot rows to columns in SQL?

Example (PostgreSQL):

```sql
SELECT department,
       SUM(CASE WHEN gender = 'M' THEN 1 ELSE 0 END) AS male_count,
       SUM(CASE WHEN gender = 'F' THEN 1 ELSE 0 END) AS female_count
FROM employees
GROUP BY department;
```

## 5. How do you handle deadlocks?

Occurs when two transactions wait on each other's lock.

Solutions:

- Access tables in same order in all transactions
- Keep transactions short
- Use lower isolation level if possible

## 6. What is the difference between UNION and UNION ALL?

- `UNION` → Removes duplicates
- `UNION ALL` → Keeps duplicates
- `UNION ALL` is faster since no deduplication.

## 7. What is the difference between INNER JOIN, LEFT JOIN, RIGHT JOIN, and FULL JOIN?

- `INNER JOIN` → Returns rows when there is a match in both tables.
- `LEFT JOIN` → Returns all rows from the left table and matched rows from the right table.
- `RIGHT JOIN` → Returns all rows from the right table and matched rows from the left table.
- `FULL JOIN` → Returns all rows when there's a match in either table.

## 8. What are indexes and their types?

Indexes speed up lookups by creating a data structure (like B-tree or hash).

Types:

- Clustered Index → physically sorts data
- Non-Clustered Index → logical reference
- Composite Index → multiple columns

## 9. What is a foreign key and what happens if the referenced row is deleted?

A foreign key ensures referential integrity between tables.

Example:

```sql
CREATE TABLE orders (
  order_id INT PRIMARY KEY,
  customer_id INT,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
```

- `ON DELETE CASCADE` → deletes child rows
- `ON DELETE SET NULL` → sets to NULL

## 10. OOP concepts: encapsulation, inheritance, polymorphism, abstraction

| Concept | Meaning | Real-world Example |
| --- | --- | --- |
| Encapsulation | Wrapping data and behavior into a single unit (class) and restricting direct access to data | Capsule containing medicine |
| Inheritance | Mechanism by which one class acquires properties and behavior of another | Child inherits traits from parent |
| Polymorphism | One name, many forms — allows methods to behave differently based on the object | Same remote button does different actions on different devices |
| Abstraction | Hiding internal details and showing only essential features | Car driver uses accelerator without knowing engine internals |

## 11. Testing an expected exception with assertThrows

```java
@Test
void shouldThrowOrderNotFoundException() {
    assertThrows(OrderNotFoundException.class, () -> {
        orderService.getOrder("123");
    });
}
```

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


    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {

        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .toList();
        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                validationErrors,
                path);
        log.error("Validation failed at {} -> {}", path, validationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                List.of(),
                path);

        log.error("Resource not found at {} -> {}", path, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleServerError(Exception exception, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(), List.of(), path);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
