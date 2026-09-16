# Spring Transaction Management

Interview-prep notes on the seven `@Transactional` propagation levels in Spring: what each one does when a transaction already exists (or does not), with an example and typical usage.

The seven propagation levels covered:

1. [REQUIRED](#1-required-default)
2. [SUPPORTS](#2-supports)
3. [MANDATORY](#3-mandatory)
4. [REQUIRES_NEW](#4-requires_new)
5. [NOT_SUPPORTED](#5-not_supported)
6. [NEVER](#6-never)
7. [NESTED](#7-nested)

## 1. REQUIRED (Default)

Joins the existing transaction if one exists; otherwise starts a new one.

**Behavior:**

- If a transaction exists → reuse it.
- If none exists → start a new transaction.

**Example:**

```java
@Transactional(propagation = Propagation.REQUIRED)
public void methodA() {
    // joins existing or starts new transaction
}
```

**Usage:** Most common choice — ensures everything runs in one transaction unless otherwise specified.

## 2. SUPPORTS

Executes within a transaction if one exists; otherwise runs non-transactionally.

**Behavior:**

- If a transaction exists → participate in it.
- If none exists → just run normally, no new transaction is started.

**Example:**

```java
@Transactional(propagation = Propagation.SUPPORTS)
public void methodB() {
    // optional transaction
}
```

**Usage:** For read-only methods that can optionally run inside or outside a transaction.

## 3. MANDATORY

Requires an existing transaction — throws an exception if none exists.

**Behavior:**

- If a transaction exists → join it.
- If none → throw `TransactionRequiredException`.

**Example:**

```java
@Transactional(propagation = Propagation.MANDATORY)
public void methodC() {
    // must be called inside a transaction
}
```

**Usage:** For methods that should never be called independently (e.g., internal DB operations).

## 4. REQUIRES_NEW

Always starts a new transaction; suspends any existing one.

**Behavior:**

- If a transaction exists → suspend it, start a new one.
- Always executes in its own independent transaction.

**Example:**

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void methodD() {
    // new independent transaction
}
```

**Usage:** Useful for audit logging, sending notifications, or partial commits even if the main transaction fails.

## 5. NOT_SUPPORTED

Does not support transactions — suspends any existing one.

**Behavior:**

- If a transaction exists → suspend it.
- Always run non-transactionally.

**Example:**

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public void methodE() {
    // runs outside any transaction
}
```

**Usage:** For operations that must not be part of a transaction (e.g., long-running queries, reporting).

## 6. NEVER

Must not run inside a transaction — throws an exception if one exists.

**Behavior:**

- If a transaction exists → throw `IllegalTransactionStateException`.
- Otherwise → run non-transactionally.

**Example:**

```java
@Transactional(propagation = Propagation.NEVER)
public void methodF() {
    // strictly non-transactional
}
```

**Usage:** For components that should never participate in a transaction (e.g., calling an external service that manages its own consistency).

## 7. NESTED

Creates a nested transaction within an existing one.

**Behavior:**

- If a transaction exists → create a savepoint (nested transaction).
- If none exists → behaves like REQUIRED.

**Example:**

```java
@Transactional(propagation = Propagation.NESTED)
public void methodG() {
    // nested transaction with savepoint
}
```

**Usage:**

- If the inner transaction fails → only roll back to the savepoint; the outer transaction can still continue.
- Useful for partial rollbacks within a larger transaction.
