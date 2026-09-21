# Collections Q&A

Interview notes on the Java Collections Framework: complexities, `HashMap` internals, null rules, and fail-fast versus fail-safe iteration.

Every table below was checked by running the code on Temurin JDK 21.0.12. The `HashMap` constants are quoted from `java.base/java/util/HashMap.java` in that JDK's `src.zip`.

## Contents

| # | Question |
| --- | --- |
| 1 | [How does `PriorityQueue` differ from `TreeSet`?](#1-how-does-priorityqueue-differ-from-treeset) |
| 2 | [What are the real time complexities?](#2-what-are-the-real-time-complexities) |
| 3 | [How does `HashMap` work internally?](#3-how-does-hashmap-work-internally) |
| 4 | [Which collections permit `null`?](#4-which-collections-permit-null) |
| 5 | [Fail-fast vs fail-safe iterators](#5-fail-fast-vs-fail-safe-iterators) |

## 1. How does `PriorityQueue` differ from `TreeSet`?

| Aspect | PriorityQueue | TreeSet |
| --- | --- | --- |
| Duplicates | Allowed | Not allowed |
| Ordering | Natural order or `Comparator` | Natural order or `Comparator` |
| Order guarantee | Only `peek`/`poll` see the minimum | Fully sorted at all times |
| Iteration / `toString()` | **Unordered** (raw heap array) | Ascending order |
| Backed by | Binary heap in an array | Red-black tree |
| `null` elements | `NullPointerException` | `NullPointerException` |

> **Correction:** the original note said PriorityQueue orders "based on comparator"
> and TreeSet is "sorted & unique", as if the ordering mechanism differed. It does
> not — both take a `Comparator` or use `Comparable`. The real difference is
> **how much order you get**: a heap only guarantees the *head* is the minimum,
> so iterating a `PriorityQueue` gives you heap-array order, not sorted order.
> This is the single most common `PriorityQueue` bug.

Proof, straight from a run:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 5, 3, 2, 4));
System.out.println(pq);                       // heap order, NOT sorted
while (!pq.isEmpty()) System.out.print(pq.poll() + " ");   // sorted, duplicates kept
System.out.println(new TreeSet<>(List.of(5, 1, 5, 3, 2, 4)));
```

```text
[1, 2, 4, 3, 5, 5]
1 2 3 4 5 5
[1, 2, 3, 4, 5]
```

Note `[1, 2, 4, 3, 5, 5]` — position 2 holds `4` and position 3 holds `3`. Printing a `PriorityQueue` and expecting a sorted list is wrong.

Complexities:

| Operation | PriorityQueue | TreeSet |
| --- | --- | --- |
| Insert | O(log n) | O(log n) |
| Find minimum | **O(1)** (`peek`) | O(log n) (`first`) |
| Remove minimum | O(log n) | O(log n) |
| `contains(x)` | **O(n)** (linear scan) | O(log n) |
| Arbitrary `remove(x)` | **O(n)** | O(log n) |

## 2. What are the real time complexities?

"Average" and "worst" differ in ways interviewers probe, so keep them separate.

### Lists

| Operation | ArrayList | LinkedList | CopyOnWriteArrayList |
| --- | --- | --- | --- |
| `get(i)` | O(1) | O(n) | O(1) |
| `add` at end | O(1) amortised | O(1) | **O(n)** (copies array) |
| `add`/`remove` at index | O(n) | O(n) to walk, O(1) to link | O(n) |
| `contains` | O(n) | O(n) | O(n) |

`ArrayList.add` is *amortised* O(1): growth allocates a new array at roughly 1.5x and copies, so a single add can be O(n). `LinkedList` is O(1) to link a node but you must walk to the index first, which is why it rarely beats `ArrayList` in practice — the walk destroys cache locality.

### Maps and sets

| Operation | HashMap / HashSet | LinkedHashMap | TreeMap / TreeSet | ConcurrentHashMap |
| --- | --- | --- | --- | --- |
| `get` average | O(1) | O(1) | O(log n) | O(1) |
| `get` worst | **O(log n)** since Java 8 | O(log n) | O(log n) | O(log n) |
| `put` average | O(1) | O(1) | O(log n) | O(1) |
| Iteration order | Unspecified | Insertion (or access) | Sorted | Unspecified |

The worst case being **O(log n) rather than O(n)** is the Java 8 change worth naming: a heavily collided bin becomes a red-black tree instead of staying a linked list. That is a degradation guard against hash-collision denial-of-service, not an optimisation for normal use.

## 3. How does `HashMap` work internally?

A `HashMap` is an array of bins. A key's hash picks a bin; entries that land in the same bin form a linked list, which may be converted to a red-black tree.

### The constants that actually matter

Quoted from `HashMap.java` in JDK 21:

| Constant | Value | Meaning |
| --- | --- | --- |
| `DEFAULT_INITIAL_CAPACITY` | `1 << 4` = **16** | Starting number of bins |
| `DEFAULT_LOAD_FACTOR` | **0.75f** | Resize when size > capacity x 0.75 |
| `TREEIFY_THRESHOLD` | **8** | Bin length at which a list becomes a tree |
| `UNTREEIFY_THRESHOLD` | **6** | Tree shrinks back to a list at this size |
| `MIN_TREEIFY_CAPACITY` | **64** | Table must be this big before any treeifying |

### The treeify rule people get wrong

Reaching 8 entries in one bin is **not enough**. `treeifyBin` first checks the table size:

```java
final void treeifyBin(Node<K,V>[] tab, int hash) {
    int n, index; Node<K,V> e;
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();                       // <- resizes INSTEAD of treeifying
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        // ... build the red-black tree
    }
}
```

So the correct statement is: **a bin treeifies when it reaches 8 entries AND the table has at least 64 bins.** Below 64 bins the map resizes instead, on the reasoning that a small table with a long bin is suffering from too few bins, not from genuinely colliding hashes.

Two asymmetries to remember:

- Treeify at **8**, untreeify at **6** — not the same number. The gap is hysteresis, so a bin hovering at the boundary does not convert back and forth on every put and remove.
- Untreeification happens during **resize**, not on every removal.

### Resize behaviour

- Resize **doubles** the capacity, so capacity is always a power of two. That lets the bin index be computed as `(n - 1) & hash` — a bitmask, far cheaper than a modulo.
- `hash()` XORs the key's hash with its own upper 16 bits (`h ^ (h >>> 16)`). With a power-of-two mask only the low bits would otherwise be used, so this "spreads" high bits down where they can affect the index.
- Since Java 8, resize **preserves relative order within a bin** and splits each bin into a "low" and "high" list. Java 7 reversed the order while rehashing, which is what made concurrent `HashMap` resize able to create an infinite loop. That specific infinite-loop bug is gone in Java 8, but `HashMap` is still **not** thread-safe — concurrent writes can still lose entries and corrupt size.
- `new HashMap<>(n)` sizes the table for `n` *bins*, not `n` entries. To hold `n` entries without resizing you need `n / 0.75 + 1`. Java 19 added `HashMap.newHashMap(n)` which does that arithmetic for you.

### Why `equals` and `hashCode` must agree

Lookup is "find the bin by hash, then walk it comparing with `equals`". If you override `equals` but not `hashCode`, two equal keys can hash to different bins and the map will happily hold both. If you mutate a key after inserting it, its hash changes and the entry becomes unreachable — a genuine, silent memory leak.

## 4. Which collections permit `null`?

This is a favourite trick question because the answers are inconsistent by design. Verified by running each case:

| Collection | `null` key | `null` value / element |
| --- | --- | --- |
| `HashMap`, `LinkedHashMap` | **Yes — exactly one** | Yes, many |
| `Hashtable` | No — `NullPointerException` | No — `NullPointerException` |
| `ConcurrentHashMap` | No — `NullPointerException` | No — `NullPointerException` |
| `TreeMap` | No — `NullPointerException` | **Yes** |
| `ArrayList`, `LinkedList` | n/a | Yes |
| `CopyOnWriteArrayList` | n/a | Yes |
| `Arrays.asList(...)` | n/a | Yes |
| `List.of` / `Map.of` / `Set.of` | No — `NullPointerException` | No — `NullPointerException` |
| `HashSet`, `LinkedHashSet` | n/a | **Yes — one** |
| `TreeSet` | n/a | No — `NullPointerException` |
| `ArrayDeque` | n/a | No — `NullPointerException` |
| `PriorityQueue` | n/a | No — `NullPointerException` |

The reasons, which is what the interviewer is really after:

- **`TreeMap` / `TreeSet`** must call `compareTo` on the key to place it. `null.compareTo` cannot work. Values are never compared, so a `null` value is fine. (A custom `Comparator` that tolerates `null` can permit `null` keys — the restriction is the comparison, not the class.)
- **`ConcurrentHashMap` / `Hashtable`** ban `null` so that `get(k) == null` unambiguously means "absent". In a concurrent map you cannot follow up with `containsKey` to disambiguate, because the answer may change between the two calls.
- **`ArrayDeque` / `PriorityQueue`** ban `null` because the queue API uses `null` as the "empty" sentinel — `poll()` returning `null` must mean the queue is empty.
- **`HashMap`** allows one `null` key by special-casing it to bin 0 with hash 0.
- **`List.of` and friends** are the modern immutable factories and reject `null` deliberately, which is a behaviour change from `Arrays.asList`. Swapping one for the other in old code is a real source of new NPEs.

## 5. Fail-fast vs fail-safe iterators

| | Fail-fast | Fail-safe (weakly consistent / snapshot) |
| --- | --- | --- |
| Examples | `ArrayList`, `HashMap`, `TreeMap`, `HashSet` | `ConcurrentHashMap`, `CopyOnWriteArrayList`, `ConcurrentLinkedQueue` |
| Detects modification | Yes — `modCount` check | No |
| On modification | Throws `ConcurrentModificationException` | Keeps iterating |
| Sees later writes | n/a | CHM: maybe. COW: never |
| Memory cost | None | COW copies the whole array per write |

Verified behaviour:

```text
ArrayList:            ConcurrentModificationException (fail-fast)
HashMap:              ConcurrentModificationException (fail-fast)
CopyOnWriteArrayList: iterated snapshot [ab], size now 4 (no CME)
ConcurrentHashMap:    no CME, size 2 (weakly consistent)
```

Three points worth making explicitly:

- **"Fail-safe" is a misnomer** and the JDK never uses the word. The javadoc says **weakly consistent**: the iterator reflects the state at some point at or since creation, and may or may not show later changes. `CopyOnWriteArrayList` is stronger — a true immutable snapshot, so it never sees later writes at all.
- **`ConcurrentModificationException` is best-effort, not a guarantee.** The javadoc is explicit: do not write code that depends on catching it. `modCount` is not volatile, so in a genuinely concurrent program the check can miss.
- **It fires on single-threaded code too.** Removing from a list inside its own for-each loop is the classic case — nothing concurrent is happening at all.

The correct single-threaded fix is the iterator's own `remove`, or `removeIf`:

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));

Iterator<String> it = list.iterator();
while (it.hasNext()) if (it.next().equals("a")) it.remove();   // [b, c]

list.removeIf(s -> s.equals("b"));                              // [c]
```

One more trap: `List.of("a").add("b")` throws `UnsupportedOperationException`, **not** `ConcurrentModificationException`. Immutability and fail-fast are unrelated mechanisms, and mixing up the two exceptions is a common slip.

## Questions to explore

> Explain, and give tricky interview questions (with answers) to ask a dev with 10 years of experience, on switch expressions.

This prompt was kept from the original note. It is about switch expressions rather than collections; that topic is covered in [Java 9 to 21 Q&A](Java_9_to_21_QA.md).
