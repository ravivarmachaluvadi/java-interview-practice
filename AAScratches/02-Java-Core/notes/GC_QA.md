# Garbage Collection Q&A

Interview notes on JVM garbage collection: memory layout, collector types, G1 internals, tuning flags, and production troubleshooting.

## Table of Contents

1. [What is Garbage Collection in Java?](#1-what-is-garbage-collection-in-java)
2. [What are the memory areas managed by the JVM?](#2-what-are-the-memory-areas-managed-by-the-jvm)
3. [Explain the generational structure of the JVM heap](#3-explain-the-generational-structure-of-the-jvm-heap)
4. [What are minor and major GCs?](#4-what-are-minor-and-major-gcs)
5. [What are the different types of garbage collectors available in the JVM?](#5-what-are-the-different-types-of-garbage-collectors-available-in-the-jvm)
6. [How does G1 GC work internally?](#6-how-does-g1-gc-work-internally)
7. [How do you tune GC performance?](#7-how-do-you-tune-gc-performance)
8. [How do you analyze a memory leak in a production JVM?](#8-how-do-you-analyze-a-memory-leak-in-a-production-jvm)
9. [Explain the GC phases of G1 or CMS](#9-explain-the-gc-phases-of-g1-or-cms)
10. [What are stop-the-world (STW) events?](#10-what-are-stop-the-world-stw-events)
11. [What causes frequent GC or long GC pauses?](#11-what-causes-frequent-gc-or-long-gc-pauses)
12. [How would you choose a GC algorithm for your system?](#12-how-would-you-choose-a-gc-algorithm-for-your-system)
13. [Suppose CPU is high and you see frequent GCs. What would you do?](#13-suppose-cpu-is-high-and-you-see-frequent-gcs-what-would-you-do)
14. [How would you monitor GC behavior in production?](#14-how-would-you-monitor-gc-behavior-in-production)
15. [What happens if you set multiple GC algorithms in flags?](#15-what-happens-if-you-set-multiple-gc-algorithms-in-flags)

**GC behavior and troubleshooting**

16. [What does `-XX:+PrintGCDetails` or `-Xlog:gc*` do? How do you interpret it?](#16-what-does--xxprintgcdetails-or--xloggc-do-how-do-you-interpret-it)
17. [What is `-XX:+UseStringDeduplication` and when should you use it?](#17-what-is--xxusestringdeduplication-and-when-should-you-use-it)
18. [What is the purpose of `-XX:+UseCompressedOops` and when might you disable it?](#18-what-is-the-purpose-of--xxusecompressedoops-and-when-might-you-disable-it)
19. [If you want to analyze GC overhead, what metrics do you monitor?](#19-if-you-want-to-analyze-gc-overhead-what-metrics-do-you-monitor)

## 1. What is Garbage Collection in Java?

Garbage Collection (GC) is the process by which the JVM automatically identifies and removes objects that are no longer reachable to free up heap memory.

It helps prevent memory leaks and reduces manual memory management overhead.

**Follow-up:** What happens if GC cannot reclaim enough memory?

- The JVM throws `OutOfMemoryError`.

## 2. What are the memory areas managed by the JVM?

- **Heap** – for object storage (Young Gen + Old Gen).
- **Metaspace** (since Java 8) – for class metadata.
- **Stack** – for method calls and local variables (per thread).
- **PC Register & Native Method Stack** – for execution tracking and native calls.

## 3. Explain the generational structure of the JVM heap

The heap is divided into:

- **Young Generation:** Eden + 2 Survivor spaces (S0, S1)
- **Old Generation:** long-lived objects
- **Metaspace:** class metadata

Objects are first allocated in Eden; surviving objects move to Survivor and then eventually to Old Gen if they persist across collections.

## 4. What are minor and major GCs?

- **Minor GC:** cleans up the Young Gen; happens more frequently and quickly.
- **Major (Full) GC:** cleans the entire heap (Young + Old); more expensive and can cause longer stop-the-world pauses.

## 5. What are the different types of garbage collectors available in the JVM?

(Java 17+)

- **Serial GC:** single-threaded, suitable for small heaps (client apps).
- **Parallel GC (Throughput Collector):** multi-threaded, high throughput, default till Java 8.
- **CMS (Concurrent Mark Sweep):** low latency, deprecated in Java 14.
- **G1 (Garbage First):** default since Java 9, balances latency and throughput.
- **ZGC:** ultra-low pause collector for very large heaps.
- **Shenandoah:** low-pause collector like ZGC, from Red Hat.

## 6. How does G1 GC work internally?

- G1 divides the heap into regions (rather than contiguous spaces).
- Performs mixed collections: collects both young and old regions incrementally.
- Uses pause time goals (`-XX:MaxGCPauseMillis`).
- Performs concurrent marking to identify live objects.
- Compacts memory concurrently, reducing fragmentation and pauses.

## 7. How do you tune GC performance?

- Tune heap sizes (`-Xms`, `-Xmx`).
- Adjust new generation size (`-XX:NewRatio`, `-XX:NewSize`, `-XX:SurvivorRatio`).
- Select GC collector (`-XX:+UseG1GC`, `-XX:+UseZGC`, etc.).
- Define GC pause time goals (`-XX:MaxGCPauseMillis`).
- Analyze GC logs (`-Xlog:gc*` in Java 9+).
- Use monitoring tools like VisualVM, JConsole, GCViewer, Grafana, JMC.

## 8. How do you analyze a memory leak in a production JVM?

- Detect high GC frequency / `OutOfMemoryError`.
- Capture a heap dump (`jmap -dump:live,format=b,file=heap.bin <pid>`).
- Analyze in Eclipse MAT or VisualVM.
- Identify objects with high retention or GC roots references.
- Fix code (e.g., static references, unclosed resources, caches not cleared).

## 9. Explain the GC phases of G1 or CMS

Expected answer (G1):

- **Initial Mark:** stop-the-world, mark roots.
- **Concurrent Mark:** background marking of reachable objects.
- **Remark:** stop-the-world final marking.
- **Cleanup:** identify regions to reclaim, optional compaction.

## 10. What are stop-the-world (STW) events?

When the JVM pauses all application threads to allow GC to perform certain operations like marking, reference processing, or compaction.

Even concurrent collectors have short STW phases.

## 11. What causes frequent GC or long GC pauses?

- Excessive object creation / short-lived objects.
- Inadequate heap size.
- Memory leaks.
- Fragmented old gen.
- Large objects (directly promoted).
- Improper GC tuning or wrong collector choice.

## 12. How would you choose a GC algorithm for your system?

Depends on priorities:

- **Throughput priority:** Parallel GC.
- **Low latency:** G1 or ZGC.
- **Huge heap (>100GB):** ZGC or Shenandoah.
- **Legacy or simple app:** Serial GC.

## 13. Suppose CPU is high and you see frequent GCs. What would you do?

- Check GC logs → frequency, pause time, reason.
- Verify heap usage trend.
- Increase heap or tune allocation ratio.
- Check for object churn (profiling with YourKit, JVisualVM).
- Possibly switch GC algorithm if mismatch.

## 14. How would you monitor GC behavior in production?

- Enable GC logging (`-Xlog:gc*`).
- Track metrics via JMX, Prometheus, or Grafana.
- Observe `gc_pause_time`, `gc_count`, and `heap_usage_after_gc`.
- Configure alerts for long GC pauses or OOMs.

## 15. What happens if you set multiple GC algorithms in flags?

```text
-XX:+UseParallelGC -XX:+UseG1GC
```

The JVM will use the last one in the command line (rightmost flag wins). Only one GC algorithm can be active at once.

## 16. What does `-XX:+PrintGCDetails` or `-Xlog:gc*` do? How do you interpret it?

Understand the phases (Young GC, Mixed GC, Full GC), time spent, memory before/after, and the GC cause.

## 17. What is `-XX:+UseStringDeduplication` and when should you use it?

Works with G1GC — deduplicates identical `String` instances in the heap to save memory at the cost of some CPU.

## 18. What is the purpose of `-XX:+UseCompressedOops` and when might you disable it?

- Uses 32-bit object pointers in a 64-bit JVM → saves heap space.
- Disable for large heaps (>32GB) or debugging memory layouts.

## 19. If you want to analyze GC overhead, what metrics do you monitor?

- GC pause times
- Frequency
- Allocation rate
- Old gen occupancy
- Promotion rate
- Time in GC (% of total CPU)
