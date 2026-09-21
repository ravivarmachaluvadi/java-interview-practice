# Java 8 Streams Q&A

Interview notes on the Java 8 Streams API: laziness, short-circuiting, stateful operations, parallel-stream caveats, the real drawbacks, and a worked `Collectors.groupingBy` example.

Every output block below is from a real run on Temurin JDK 21.0.12.

## Contents

| # | Question |
| --- | --- |
| 1 | [What are the disadvantages of Java 8 streams?](#1-what-are-the-disadvantages-of-java-8-streams) |
| 2 | [What does "streams are lazy" actually mean?](#2-what-does-streams-are-lazy-actually-mean) |
| 3 | [Which operations are stateful, and why does it matter?](#3-which-operations-are-stateful-and-why-does-it-matter) |
| 4 | [What goes wrong with parallel streams?](#4-what-goes-wrong-with-parallel-streams) |
| 5 | [Example: find the maximum salary by department](#5-example-find-the-maximum-salary-by-department) |

## 1. What are the disadvantages of Java 8 streams?

| Disadvantage | Description | Mitigation |
| --- | --- | --- |
| Performance overhead | Slower for small or simple loops | Use for larger data or readability |
| Hard to debug | Stack traces are deep and lambda frames unclear | Break into steps; use `peek` to inspect |
| Readability | Long chains become hard to follow | Extract named methods |
| Side effects | Mutating shared state breaks silently | Collect instead of mutating |
| Parallel pitfalls | Overhead and race conditions | Measure before parallelising |
| Limited control | Hard to break early or carry an index | Use a loop when you need either |
| Boxing / unboxing | Allocation per element | `IntStream`, `LongStream`, `DoubleStream` |
| Checked exceptions | Lambdas cannot throw them | Wrap, or handle inside |
| **Single use** | A stream cannot be traversed twice | Re-create from the source |

That last row is missing from most people's list and is a frequent live-coding failure. A stream is consumed by its terminal operation:

```java
Stream<Integer> s = Stream.of(1, 2, 3);
s.count();
s.count();   // throws
```

```text
IllegalStateException: stream has already been operated upon or closed
```

The fix is to keep the **source** (the collection) and build a fresh stream each time — or to use a `Supplier<Stream<T>>`.

On the "performance overhead" row, be ready to qualify it. For a simple `int` loop over a small array, a `for` loop wins. For pipelines the JIT can inline, the gap usually closes. The genuinely reliable cost is **boxing**, which is why the primitive streams exist.

## 2. What does "streams are lazy" actually mean?

Nothing runs until a **terminal** operation is called. Intermediate operations only build a description of the pipeline.

```java
Stream.of(1, 2, 3)
      .peek(x -> System.out.println("peek " + x))
      .map(x -> x * 2);     // no terminal op
```

```text
(no output at all)
```

The `peek` never fires because nothing ever asked for a result. This is why a pipeline with no terminal operation is dead code — and why `peek` for logging is unreliable.

### Element-at-a-time, not stage-at-a-time

The second half of laziness matters more in interviews: elements are pushed through the **whole pipeline one at a time**, not stage by stage. Combined with short-circuiting, that means work is skipped entirely:

```java
Optional<Integer> first = Stream.of(1, 2, 3, 4, 5)
        .peek(x -> System.out.println("  saw " + x))
        .filter(x -> x % 2 == 0)
        .findFirst();
```

```text
  saw 1
  saw 2
  first even = 2
```

Elements 3, 4 and 5 were never touched. A `for` loop with a `break` does the same thing; the point is that the stream does it without you writing the `break`.

### Which operations short-circuit

| Operation | Kind | Notes |
| --- | --- | --- |
| `limit(n)` | Intermediate | Stops after n elements |
| `takeWhile(p)` | Intermediate | Java 9+ |
| `findFirst`, `findAny` | Terminal | Stops at the first match |
| `anyMatch`, `allMatch`, `noneMatch` | Terminal | Stops at the first decisive element |

Short-circuiting is what makes infinite streams usable:

```java
Stream.iterate(1, x -> x * 2).limit(5).toList();
```

```text
[1, 2, 4, 8, 16]
```

Without `limit`, that pipeline never terminates. `Stream.iterate`, `Stream.generate` and `Random.ints()` are all infinite and **must** be bounded by a short-circuiting operation.

## 3. Which operations are stateful, and why does it matter?

| Operation | Stateless / Stateful | Cost |
| --- | --- | --- |
| `filter`, `map`, `flatMap`, `peek` | Stateless | Constant memory |
| `mapToInt`, `boxed` | Stateless | Constant memory |
| `distinct` | **Stateful** | Holds seen elements |
| `sorted` | **Stateful, full barrier** | Buffers everything |
| `limit`, `skip` | **Stateful** | Cheap, but ordering-sensitive |

A **stateful** operation needs to see other elements to decide about the current one. `sorted` is the extreme case: it is a full barrier, so it must buffer the entire stream before emitting anything. Two consequences:

- `sorted` on an infinite stream never terminates, even with a `limit` **after** it. `limit` before `sorted` is fine; after it is not.
- `sorted` and `distinct` destroy the memory advantage of streaming, because the whole dataset is materialised.

Order matters for performance in the obvious direction: `filter` then `sorted` sorts fewer elements than `sorted` then `filter`, and both produce the same answer.

## 4. What goes wrong with parallel streams?

### Shared mutable state loses data silently

This is the failure to be able to describe, because it does not throw:

```java
List<Integer> unsafe = new ArrayList<>();
IntStream.range(0, 100_000).parallel().forEach(unsafe::add);
System.out.println("size = " + unsafe.size());   // expected 100000
```

```text
size = 15453
```

No exception. No warning. **Over 84% of the elements vanished** because `ArrayList.add` is not thread-safe and concurrent resizes clobbered each other. A different run gives a different number, and it can also throw `ArrayIndexOutOfBoundsException` or produce `null` holes.

The correct version lets the framework do the combining:

```java
List<Integer> safe = IntStream.range(0, 100_000).parallel().boxed().toList();
```

```text
size = 100000
```

The rule: **never mutate shared state in `forEach`.** Use `collect` or `toList`, which are built to merge per-thread partial results.

### The common ForkJoinPool is shared

All parallel streams in a JVM use `ForkJoinPool.commonPool()` by default, sized to `CPUs - 1`. A slow or blocking parallel stream starves every other parallel stream in the process. Never put blocking I/O in a parallel stream.

### Ordering costs

`forEach` on a parallel stream gives no order guarantee; `forEachOrdered` restores it but serialises the output and gives back much of the speed-up.

```java
IntStream.range(0, 8).parallel().forEachOrdered(x -> System.out.print(x + " "));
```

```text
0 1 2 3 4 5 6 7
```

Reductions are safe regardless, provided the operation is **associative**:

```java
IntStream.rangeClosed(1, 1000).parallel().sum();   // 500500, always
```

Subtraction and division are not associative, so `reduce(0, (a, b) -> a - b)` gives different answers in parallel than in serial. That is a correctness bug, not a performance one.

### When parallel actually helps

| Condition | Why |
| --- | --- |
| Large N (rule of thumb: 10k+) | Splitting must pay for itself |
| Cheap, independent, CPU-bound work | No coordination, no blocking |
| Splittable source | `ArrayList`, arrays, `IntStream.range` split well |
| No shared mutable state | Otherwise it is incorrect, not just slow |
| Measured, not assumed | The only rule that matters |

`LinkedList`, `Stream.iterate` and `BufferedReader.lines()` split badly — the framework cannot estimate size or halve the work cheaply, so parallelism adds overhead and returns nothing.

## 5. Example: find the maximum salary by department

```java
import java.util.*;
import java.util.stream.*;

class Employee {
    private String name;
    private String department;
    private double salary;

    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }
}

public class MaxSalaryByDepartment {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "HR", 4000),
                new Employee("Bob", "IT", 7000),
                new Employee("Charlie", "HR", 4500),
                new Employee("David", "IT", 8000),
                new Employee("Eve", "Finance", 6500)
        );

        Map<String, Double> maxSalaryByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                emp -> emp.map(Employee::getSalary).orElse(0.0)
                        )
                ));

        maxSalaryByDept.forEach((dept, salary) ->
                System.out.println(dept + " → " + salary)
        );
    }
}
```

Verified output:

```text
Finance → 6500.0
HR → 4500.0
IT → 8000.0
```

This compiles and is correct. Three things about it are worth being able to explain.

**Why `collectingAndThen` is there.** `Collectors.maxBy` returns an `Optional`, because a group could in principle be empty. Without the wrapper the result type is `Map<String, Optional<Employee>>`, not `Map<String, Double>`. `collectingAndThen` applies a finisher to unwrap it. Verified — `maxBy` alone really does produce `Optional` values.

**The `orElse(0.0)` is unreachable.** `groupingBy` never creates an empty group, so the `Optional` is always present. It is harmless, but a reviewer will ask, and "it cannot happen, it is just to satisfy the type" is the right answer. Returning `0.0` for a genuinely empty group would be a bug anyway, since zero is a plausible salary.

**The result map is a `HashMap`, so iteration order is unspecified.** Confirmed at runtime:

```text
map class -> java.util.HashMap
```

The output above happens to be alphabetical; do not rely on that. For a guaranteed order, pass a map factory — `groupingBy(classifier, TreeMap::new, downstream)` — or wrap the result in a `TreeMap`.

### Two simpler ways to write the same thing

`Collectors.toMap` with a merge function is the shortest, and skips the `Optional` entirely:

```java
Map<String, Double> byDept = employees.stream()
        .collect(Collectors.toMap(Employee::getDepartment, Employee::getSalary, Math::max));
```

`summarizingDouble` is the one to reach for when you want more than one statistic, since it computes count, sum, min, average and max in a single pass:

```java
Map<String, DoubleSummaryStatistics> stats = employees.stream()
        .collect(Collectors.groupingBy(Employee::getDepartment,
                 Collectors.summarizingDouble(Employee::getSalary)));
```

All three approaches were run and produce the same numbers:

```text
collectingAndThen -> {Finance=6500.0, HR=4500.0, IT=8000.0}
summarizing       -> {Finance=6500.0, HR=4500.0, IT=8000.0}
toMap merge       -> {Finance=6500.0, HR=4500.0, IT=8000.0}
```

A likely follow-up: **what if you want the whole `Employee`, not just the salary?** Drop the finisher's `map` step and keep the employee — `collectingAndThen(maxBy(cmp), Optional::get)`. And note `Collectors.toMap` throws `IllegalStateException` on a duplicate key unless you supply the merge function, which is exactly what `Math::max` is doing above.
