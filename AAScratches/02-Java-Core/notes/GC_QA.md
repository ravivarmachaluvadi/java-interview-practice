# Garbage Collection Q&A

Interview notes on JVM garbage collection: memory layout, collector types, G1 internals, tuning flags, and production troubleshooting.

Every flag default and log excerpt below came from running Temurin JDK 21.0.12, not from memory. Where the original note was wrong, the correction is called out in a quote block so the distinction sticks.

## Table of Contents

**Fundamentals**

| # | Question |
| --- | --- |
| 1 | [What is Garbage Collection in Java?](#1-what-is-garbage-collection-in-java) |
| 2 | [What are the memory areas managed by the JVM?](#2-what-are-the-memory-areas-managed-by-the-jvm) |
| 3 | [Explain the generational structure of the JVM heap](#3-explain-the-generational-structure-of-the-jvm-heap) |
| 4 | [What are minor, major and full GCs?](#4-what-are-minor-major-and-full-gcs) |
| 5 | [Which garbage collectors exist in current Java?](#5-which-garbage-collectors-exist-in-current-java) |

**G1 and tuning**

| # | Question |
| --- | --- |
| 6 | [How does G1 GC work internally?](#6-how-does-g1-gc-work-internally) |
| 7 | [How do you tune GC performance?](#7-how-do-you-tune-gc-performance) |
| 8 | [How do you analyze a memory leak in a production JVM?](#8-how-do-you-analyze-a-memory-leak-in-a-production-jvm) |
| 9 | [Explain the GC phases of G1](#9-explain-the-gc-phases-of-g1) |
| 10 | [What are stop-the-world (STW) events?](#10-what-are-stop-the-world-stw-events) |
| 11 | [What causes frequent GC or long GC pauses?](#11-what-causes-frequent-gc-or-long-gc-pauses) |
| 12 | [How would you choose a GC algorithm?](#12-how-would-you-choose-a-gc-algorithm) |

**Production behaviour and flags**

| # | Question |
| --- | --- |
| 13 | [CPU is high and you see frequent GCs. What would you do?](#13-cpu-is-high-and-you-see-frequent-gcs-what-would-you-do) |
| 14 | [How would you monitor GC behavior in production?](#14-how-would-you-monitor-gc-behavior-in-production) |
| 15 | [What happens if you set multiple GC algorithms in flags?](#15-what-happens-if-you-set-multiple-gc-algorithms-in-flags) |
| 16 | [What does `-XX:+PrintGCDetails` or `-Xlog:gc*` do?](#16-what-does--xxprintgcdetails-or--xloggc-do) |
| 17 | [What is `-XX:+UseStringDeduplication`?](#17-what-is--xxusestringdeduplication) |
| 18 | [What is the purpose of `-XX:+UseCompressedOops`?](#18-what-is-the-purpose-of--xxusecompressedoops) |
| 19 | [What metrics do you monitor for GC overhead?](#19-what-metrics-do-you-monitor-for-gc-overhead) |

## 1. What is Garbage Collection in Java?

Garbage Collection (GC) is the process by which the JVM automatically identifies and removes objects that are no longer **reachable** from a GC root, to free heap memory.

"Unreachable", not "unused" — an object you never touch again is still live as long as a reference chain from a GC root reaches it. That distinction is the whole reason memory leaks are possible in a garbage-collected language.

GC roots are, roughly: local variables on any thread's stack, active thread objects, static fields, JNI references, and classes held by a live class loader.

**Follow-up:** What happens if GC cannot reclaim enough memory?

- The JVM throws `OutOfMemoryError`. Note it is an `Error`, not an `Exception` — the JVM is in an unrecoverable state and catching it is almost always the wrong move.
- A distinct failure is `OutOfMemoryError: GC overhead limit exceeded`, thrown when more than 98% of time goes to GC and less than 2% of the heap is recovered. That means the heap is technically not full but the process is thrashing.

## 2. What are the memory areas managed by the JVM?

| Area | Holds | Per | Heap? |
| --- | --- | --- | --- |
| Heap | Objects and arrays | JVM | **Yes** |
| Metaspace | Class metadata | JVM | **No — native** |
| Code cache | JIT-compiled machine code | JVM | No — native |
| Stack | Frames, locals, partial results | Thread | No |
| PC register | Address of current instruction | Thread | No |
| Native method stack | JNI call frames | Thread | No |

Metaspace replaced PermGen in Java 8. The point that matters: **Metaspace lives in native memory, not in the heap**, so it is not bounded by `-Xmx`. It grows until `-XX:MaxMetaspaceSize` or until the machine runs out. `OutOfMemoryError: Metaspace` is therefore a completely different problem from a heap OOM, and raising `-Xmx` will not fix it. It usually means class loader leaks — repeated redeploys, or dynamic proxy or bytecode generation in a loop.

Confirmed by `MemoryPoolMXBean` on JDK 21:

```text
  G1 Eden Space                Heap memory
  G1 Survivor Space            Heap memory
  G1 Old Gen                   Heap memory
  Metaspace                    Non-heap memory
  Compressed Class Space       Non-heap memory
  CodeHeap 'profiled nmethods' Non-heap memory
```

## 3. Explain the generational structure of the JVM heap

The **heap** is divided into:

- **Young Generation:** Eden + two Survivor spaces (S0, S1)
- **Old Generation (Tenured):** long-lived objects

> **Correction:** the original note listed **Metaspace as a third division of the
> heap**. It is not part of the heap at all — see the pool dump in question 2,
> where Metaspace reports as `Non-heap memory`. The heap has exactly two
> generations. Putting Metaspace inside the heap is a leftover mental model from
> PermGen, which genuinely *was* a heap region before Java 8.

Objects are allocated in Eden. A minor GC copies survivors into a Survivor space; each surviving collection increments the object's age, and once the age passes `MaxTenuringThreshold` (default **15**) it is promoted to Old Gen.

Two mechanisms that bypass that ladder, and are common interview follow-ups:

- **Large objects are allocated straight into Old Gen** (in G1, into humongous regions) when they do not fit in Eden. This is why a big `byte[]` can trigger old-gen pressure immediately.
- **Premature promotion** happens when Survivor space is too small: objects that would have died young get pushed into Old Gen anyway, and then only a much more expensive collection can reclaim them.

Relevant defaults verified on JDK 21:

| Flag | Default | Meaning |
| --- | --- | --- |
| `NewRatio` | 2 | Old : Young size ratio |
| `SurvivorRatio` | 8 | Eden : one Survivor ratio |
| `MaxTenuringThreshold` | 15 | Ages survived before promotion |

G1 mostly ignores `NewRatio` and sizes the young generation adaptively to hit its pause goal, so setting it while using G1 usually fights the collector rather than helping it.

## 4. What are minor, major and full GCs?

Three terms, not two — the original note collapsed the last two, which is exactly the confusion interviewers probe.

| Type | Collects | Typical cost | STW? |
| --- | --- | --- | --- |
| Minor / Young | Young Gen only | Short (ms) | Yes, always |
| Major | Old Gen | Longer | Mostly concurrent in G1/CMS |
| Full | Young + Old + Metaspace, with compaction | Longest | Yes, fully |

A **Full GC in G1 is a failure signal**, not routine operation. G1 is designed never to need one; when you see `Pause Full (G1 Evacuation Pause)` or `(Allocation Failure)` in the log, it means concurrent marking could not keep up with allocation and G1 fell back to a single-threaded-style compaction of the whole heap. That is the line to grep for in a latency incident.

## 5. Which garbage collectors exist in current Java?

| Collector | Flag | Status in Java 21 | Best for |
| --- | --- | --- | --- |
| Serial | `-XX:+UseSerialGC` | Supported | Tiny heaps, 1 CPU, containers |
| Parallel | `-XX:+UseParallelGC` | Supported | Raw throughput, batch jobs |
| G1 | `-XX:+UseG1GC` | **Default** | Balanced latency/throughput |
| ZGC | `-XX:+UseZGC` | Supported | Sub-ms pauses, huge heaps |
| Shenandoah | `-XX:+UseShenandoahGC` | Supported | Sub-ms pauses, mid heaps |
| Epsilon | `-XX:+UseEpsilonGC` | **Experimental** | Benchmarks, no-op testing |
| CMS | — | **Removed** | Nothing — it is gone |

> **Correction — two errors in the original list.**
>
> 1. It said **"CMS ... deprecated in Java 14"**. CMS was **deprecated in Java 9**
>    (JEP 291) and **removed in Java 14** (JEP 363). Deprecated and removed are
>    different events four releases apart. On JDK 21, `-XX:+UseConcMarkSweepGC`
>    does not warn — the JVM refuses to start:
>    `Unrecognized VM option 'UseConcMarkSweepGC'`.
> 2. It listed CMS under a **"(Java 17+)"** heading, which cannot be right for a
>    collector removed in 14.
>
> It also omitted **Epsilon** (JEP 318, Java 11), the no-op collector that
> allocates and never reclaims. It is experimental and needs
> `-XX:+UnlockExperimentalVMOptions` before the flag, or the JVM will not start.

Version history worth being precise about:

| Collector | Landed | Production-ready |
| --- | --- | --- |
| G1 | Java 7 (experimental) | Java 9 (default) |
| ZGC | Java 11 (experimental) | Java 15 (JEP 377) |
| Shenandoah | Java 12 (experimental) | Java 15 (JEP 379) |
| Generational ZGC | Java 21 (JEP 439) | Java 23 (default for ZGC) |
| CMS | Java 4 era | Deprecated 9, removed 14 |

On Java 21, generational ZGC is **opt-in**: `-XX:+ZGenerational` alongside `-XX:+UseZGC`. Verified on this machine — `ZGenerational` reports `false {default}` on JDK 21. (The "default from Java 23" row above is from the JEP, not checked here; no JDK 23 was available to run.)

### "Parallel is the Java 8 default" — true, but say which default

Parallel GC was the default in Java 8, and G1 has been the default since **Java 9** (JEP 248). But the default is **ergonomic, not fixed**: on a machine the JVM classifies as small, it still picks Serial. Verified on JDK 21:

```text
java -XX:ActiveProcessorCount=1 -Xmx512m   ->  [info][gc] Using Serial
java -XX:ActiveProcessorCount=2 -Xmx4g     ->  [info][gc] Using G1
```

This matters in production: a container limited to one CPU silently gets Serial GC, which is a frequent cause of "it is slow in Kubernetes but fine on my laptop".

## 6. How does G1 GC work internally?

- G1 divides the heap into equal-sized **regions** (power of two between 1MB and 32MB; ergonomically **4MB** on a default JDK 21 heap). A region is dynamically labelled Eden, Survivor, Old, or Humongous — generations are no longer contiguous address ranges.
- It marks concurrently to learn which regions hold the most garbage, then collects **the regions with the most garbage first** — that is what "Garbage First" means.
- **Mixed collections** collect all young regions plus a selection of old ones.
- It targets a soft pause goal, `-XX:MaxGCPauseMillis`, default **200ms**, and sizes each collection set to fit that budget.
- **Humongous objects** — anything larger than half a region — get their own contiguous regions and are handled specially. Lots of humongous allocations fragment the heap and are a classic cause of unexpected Full GCs.

> **Correction:** the original note said G1 **"compacts memory concurrently"**.
> It does not. G1's *marking* is concurrent; its *copying and compaction* happen
> inside a stop-the-world **evacuation pause**. The GC log says so directly —
> every collection is labelled `Pause`:
>
> ```text
> GC(0) Pause Young (Normal) (G1 Evacuation Pause) 23M->13M(128M) 4.554ms
> GC(3) Pause Young (Concurrent Start) (G1 Evacuation Pause) 88M->84M(160M) 8.439ms
> ```
>
> Concurrent compaction is the thing **ZGC and Shenandoah** do and G1 does not —
> that is the actual architectural difference between them, so attributing it to
> G1 erases the answer to the next question you will be asked.

### G1 vs ZGC, stated correctly

| | G1 | ZGC |
| --- | --- | --- |
| Marking | Concurrent | Concurrent |
| Relocation / compaction | **Stop-the-world** | **Concurrent** |
| Pause scales with | Live set of the collection set | Root set only |
| Typical pause | 10–200ms | Under 1ms |
| Heap range | Up to ~32GB comfortably | 8MB to 16TB |
| Generational | Always | Opt-in in 21, default from 23 |

ZGC's trick is **coloured pointers plus load barriers**: metadata bits are stored inside the reference itself, and a barrier on every reference load fixes up pointers to objects that have already been moved. That is how it relocates objects while the application is still running.

## 7. How do you tune GC performance?

Tune in this order — sizing first, collector second, micro-flags last.

| Lever | Flags | When |
| --- | --- | --- |
| Heap size | `-Xms`, `-Xmx` | Always first; set them equal in servers |
| Collector choice | `-XX:+UseG1GC`, `-XX:+UseZGC` | When the workload's priority is clear |
| Pause goal | `-XX:MaxGCPauseMillis` (default 200) | Latency-sensitive services |
| Marking start | `-XX:InitiatingHeapOccupancyPercent` (default 45) | Full GCs from late marking |
| Generation sizing | `-XX:NewRatio`, `-XX:SurvivorRatio` | Parallel/Serial only; fights G1 |
| Observation | `-Xlog:gc*` | Before changing anything |

Two rules that carry more weight than any individual flag:

- **Set `-Xms` equal to `-Xmx` on a server.** A growing heap causes extra Full GCs during warm-up and makes your GC logs unreadable, because the denominator keeps changing.
- **Change one flag at a time and measure.** Most "GC tuning" problems are allocation-rate problems in the application, and no flag fixes an object churn bug.

Tools: VisualVM, JConsole, GCViewer, JDK Mission Control (JMC) with Flight Recorder, Grafana over JMX or Micrometer.

## 8. How do you analyze a memory leak in a production JVM?

1. Confirm the symptom: old-gen occupancy **after** every Full GC trending upward. A sawtooth that returns to the same floor is not a leak.
2. Capture a heap dump: `jmap -dump:live,format=b,file=heap.bin <pid>`.
3. Better: set `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/dumps` **before** the incident, so you get a dump at the moment of failure without having to catch it live.
4. Analyse in Eclipse MAT — its *Leak Suspects* report and *dominator tree* do most of the work.
5. Trace the **path to GC root** for the biggest retained set. Retained size, not shallow size, is the number that matters.
6. Fix the cause. The usual suspects: static collections that only ever grow, caches with no eviction policy, unclosed resources, listeners never deregistered, `ThreadLocal`s not removed on pooled threads, and class loader leaks on redeploy.

Note `-dump:live` forces a Full GC first. On a struggling production JVM that pause is real, and the dump file will be roughly the size of the live heap — plan disk space before you run it.

## 9. Explain the GC phases of G1

The original note used the phase names from an older JDK. Modern G1 (JDK 9+) renamed them, and these are the strings you will actually grep for.

| Phase | STW? | What happens |
| --- | --- | --- |
| Pause Young (Concurrent Start) | **Yes** | Normal young collection that also marks roots |
| Concurrent Mark Cycle | No | Traces the object graph in the background |
| Pause Remark | **Yes** | Finishes marking, processes references |
| Pause Cleanup | **Yes**, very short | Accounts region liveness, frees empty regions |
| Concurrent Cleanup | No | Resets data structures |
| Pause Young (Mixed) | **Yes** | Collects young + selected old regions |

> **Correction:** the phase once called **"Initial Mark"** is now logged as
> **`Pause Young (Concurrent Start)`** — it was folded into a normal young
> collection rather than being a separate pause. Verified log excerpt:
>
> ```text
> GC(3) Pause Young (Concurrent Start) (G1 Evacuation Pause) 88M->84M(160M) 8.439ms
> GC(4) Concurrent Mark Cycle
> GC(4) Pause Remark 96M->56M(150M) 0.390ms
> GC(4) Pause Cleanup 59M->59M(150M) 0.053ms
> GC(4) Concurrent Mark Cycle 4.080ms
> ```

CMS phases are no longer worth memorising — the collector was removed in Java 14. If an interviewer asks, the honest and better answer is to name CMS's two concurrent phases (mark, sweep), then say it never compacted, which caused old-gen fragmentation and eventual `concurrent mode failure` fallbacks to a serial Full GC — and that this is precisely the problem G1 was built to solve.

## 10. What are stop-the-world (STW) events?

An STW event is a pause where the JVM brings every application thread to a **safepoint** and holds them there while it does work that cannot tolerate the heap changing underneath it.

Two things worth adding beyond the basic definition:

- **Every collector has STW phases**, including ZGC and Shenandoah. The difference is that their pauses are bounded by the size of the *root set*, not the size of the live heap, so pauses stay sub-millisecond as the heap grows.
- **Not all STW pauses are GC.** Deoptimisation, class redefinition by an agent, thread dumps and heap dumps all stop the world. Biased-locking revocation used to be a common cause, but biased locking was disabled by default in Java 15 and removed in Java 18 — on JDK 21 `-XX:+UseBiasedLocking` is an unrecognised option, so it can no longer be the answer. If pause time does not correlate with GC activity, look at `-Xlog:safepoint`.

A thread only reaches a safepoint at certain points — typically method returns and loop back-edges. A long-running counted loop with no safepoint poll can delay everyone else, which shows up as a huge "time to safepoint" with a tiny actual GC time.

## 11. What causes frequent GC or long GC pauses?

Separate the two — they have different causes and different fixes.

| Symptom | Likely cause | First check |
| --- | --- | --- |
| Frequent young GCs | High allocation rate | Allocation rate MB/s in GC log |
| Frequent Full GCs | Leak, or marking starts too late | Old-gen occupancy after Full GC |
| Long young pauses | Large live set surviving | Survivor sizing, promotion rate |
| Long Full pauses | Fragmentation, humongous objects | Humongous allocation count |
| Rising pauses over time | Memory leak | Heap dump, dominator tree |

Frequent young GC on its own is **not** a problem. A young collection's cost is proportional to what survives, not to what died, so a high allocation rate of short-lived objects is cheap. The number to worry about is **promotion rate**, not collection count.

## 12. How would you choose a GC algorithm?

| Priority | Collector | Rationale |
| --- | --- | --- |
| Throughput, pauses tolerable | Parallel | Least total CPU spent on GC |
| Balanced, general services | G1 | The default for a reason |
| Strict latency SLA (p99) | ZGC or Shenandoah | Concurrent compaction, sub-ms |
| Heap over ~32GB | ZGC | Pause independent of heap size |
| Tiny heap, 1 CPU, CLI tool | Serial | No coordination overhead |
| Benchmarking allocation | Epsilon | No collection at all |

The trade-off to name out loud: **concurrent collectors buy latency with CPU and headroom.** ZGC does its work on application-thread time via load barriers and needs spare heap to relocate into. If your service is CPU-saturated or running near `-Xmx`, moving to ZGC can make throughput worse, not better.

## 13. CPU is high and you see frequent GCs. What would you do?

1. **Confirm GC is the cause, not a symptom.** Compare "time in GC" against total CPU. If GC is 3% of CPU, the frequent GCs are a side effect of a hot loop, not the problem.
2. Read the GC log: frequency, pause duration, **cause** field, and heap occupancy after each collection.
3. Decide leak vs churn. Old-gen occupancy after Full GC rising over hours means a leak (go to question 8). Flat means churn.
4. For churn, profile allocation — JFR's *Allocation Profiling* or async-profiler in `alloc` mode names the exact line allocating the most bytes.
5. Fix the allocation: boxing in a hot loop, string concatenation, defensive copies, oversized buffers, logging that formats messages that are then discarded.
6. Only then consider heap or collector changes.

## 14. How would you monitor GC behavior in production?

- Always run with GC logging on. It is cheap, and a rotating configuration means you always have the window you need:
  `-Xlog:gc*:file=/var/log/gc.log:time,uptime,level,tags:filecount=10,filesize=50M`
- Export metrics by JMX or Micrometer to Prometheus and Grafana.
- Track: `gc_pause_seconds` (p99, not mean), `gc_collection_count`, heap used **after** collection, allocation rate, and promotion rate.
- Alert on: any Full GC in a G1 service, p99 pause over your SLA budget, and old-gen-after-Full-GC trending up across restarts.
- Use JDK Flight Recorder continuously — it is designed for production and its overhead is around 1%.

Watch the mean-versus-percentile trap: mean pause time hides exactly the outliers that break an SLA. Alert on p99.

## 15. What happens if you set multiple GC algorithms in flags?

```text
-XX:+UseParallelGC -XX:+UseG1GC
```

> **Correction — the original answer was wrong.** It said "the JVM will use the
> last one in the command line (rightmost flag wins)". It does not. The JVM
> **refuses to start.** Verified on JDK 21:
>
> ```text
> $ java -XX:+UseParallelGC -XX:+UseG1GC -version
> Error occurred during initialization of VM
> Multiple garbage collectors selected
> ```
>
> The "last flag wins" rule is real, but it applies to **repeating the same
> flag** (`-Xmx1g -Xmx2g` gives 2GB). It does not apply to selecting mutually
> exclusive collectors. This matters in practice: two different `JAVA_OPTS`
> sources each adding a GC flag does not silently pick one — it crashes the
> container at startup, which is at least a loud failure.

Only one GC algorithm can be active at a time; that part of the original note was right.

## 16. What does `-XX:+PrintGCDetails` or `-Xlog:gc*` do?

`-XX:+PrintGCDetails` is the **Java 8 flag**. In Java 9 it was replaced by the unified logging framework (JEP 158/271). It still works on JDK 21, but only as a deprecated alias:

```text
$ java -XX:+PrintGCDetails -version
[0.004s][warning][gc] -XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.
[0.008s][info][gc] Using G1
```

Use `-Xlog:gc*` on anything modern. A useful production form:

```text
-Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=10,filesize=50M
```

Reading a line:

```text
GC(0) Pause Young (Normal) (G1 Evacuation Pause) 23M->13M(128M) 4.554ms
```

| Field | Meaning |
| --- | --- |
| `GC(0)` | Collection sequence number |
| `Pause Young (Normal)` | Phase — `Pause` means stop-the-world |
| `(G1 Evacuation Pause)` | **Cause** — the most diagnostic field |
| `23M->13M` | Heap used before -> after |
| `(128M)` | Total heap capacity |
| `4.554ms` | Pause duration |

The cause field is what you triage on. `Allocation Failure` is normal; `Metadata GC Threshold` points at Metaspace, not the heap; `System.gc()` means something in your code or a library is calling it explicitly — usually NIO direct buffers, and the reason `-XX:+DisableExplicitGC` exists.

## 17. What is `-XX:+UseStringDeduplication`?

It finds `String` objects whose backing `byte[]` has identical contents and makes them share one array. It saves memory, costs some CPU, and runs on a background thread.

> **Correction:** the original note said it **"works with G1GC"**. That was true
> when the feature shipped (JEP 192, Java 8) but is out of date. It was extended
> to the other collectors in **Java 18**. Verified on JDK 21 — it really does run
> under Parallel, Serial and ZGC, not just G1:
>
> ```text
> $ java -XX:+UseParallelGC -XX:+UseStringDeduplication -Xlog:stringdedup -version
> [0.034s][info][stringdedup] Concurrent String Deduplication 589 (inspected), ...
>
> $ java -XX:+UseSerialGC   -XX:+UseStringDeduplication -Xlog:stringdedup -version
> [0.030s][info][stringdedup] Concurrent String Deduplication 580 (inspected), ...
>
> $ java -XX:+UseZGC        -XX:+UseStringDeduplication -Xlog:stringdedup -version
> [0.046s][info][stringdedup] Concurrent String Deduplication 581 (inspected), ...
> ```
>
> The "extended in Java 18" attribution is from the release notes; only the
> JDK 21 behaviour above was run here.

Use it when a heap dump shows duplicate strings are a large share of retained memory — typical in apps holding many parsed records with repeated field values. It deduplicates the `byte[]` inside the `String`, not the `String` object itself, so it does not affect `==` comparisons or identity.

It is not a substitute for `String.intern()` and does not put anything in the string pool.

## 18. What is the purpose of `-XX:+UseCompressedOops`?

It stores object references as **32-bit offsets** instead of 64-bit addresses on a 64-bit JVM, cutting reference size in half. Less memory per object, and — usually more important — better cache line utilisation.

It is **on by default**, and you almost never set it manually.

> **Correction:** the original note said to "disable it for large heaps (>32GB)".
> You do not need to — **the JVM disables it automatically**, because above ~32GB
> the offsets cannot address the heap. Verified:
>
> ```text
> java -Xmx4g   ->  UseCompressedOops = true   {ergonomic}
> java -Xmx40g  ->  UseCompressedOops = false  {default}
> ```

The practical consequence is a genuine interview favourite: **a heap just over 32GB can hold less data than a heap just under it**, because every reference doubles in size at the boundary. If you are sizing at 33GB, either drop to 31GB or go well past 40GB, because the range in between is strictly worse.

`-XX:ObjectAlignmentInBytes=16` pushes the boundary to about 64GB at the cost of more padding per object.

## 19. What metrics do you monitor for GC overhead?

| Metric | Why it matters | Warning sign |
| --- | --- | --- |
| Pause time p99 | Directly hits request latency | Above your SLA budget |
| Time in GC (% CPU) | Total tax paid for memory management | Over ~5% sustained |
| Allocation rate (MB/s) | Drives young GC frequency | Rising with flat traffic |
| **Promotion rate** (MB/s) | Drives old-gen growth and Full GCs | The best early leak signal |
| Old-gen occupancy after Full GC | Distinguishes leak from churn | Trending up over hours |
| Full GC count | In G1, should be zero | Any non-zero value |

Promotion rate is the one most people omit and the one that predicts trouble earliest. Allocation rate tells you how hard the young collector is working; **promotion rate tells you how fast pressure is reaching the old generation**, which is what eventually produces the long pauses.
