# Concurrency — must-know order

**Techniques in this topic:** Data races, atomics and CAS - why ++ is three operations and what AtomicInteger actually fixes, Memory visibility: volatile guarantees visibility, not atomicity, Monitor locks: which object synchronized actually locks (instance vs class), and lock scope under an executor, wait/notify/notifyAll on a monitor: predicate loops, spurious wakeups, lost wakeups, ReentrantLock + multiple Condition queues for targeted signalling instead of notifyAll, Thread ordering and turn-taking puzzles (odd/even, N-threads modulo sequencing), Producer-consumer and bounded buffers, including BlockingQueue as the built-in answer, Coordination primitives: CountDownLatch, BlockingQueue, PriorityBlockingQueue, timed await, Cooperative cancellation: the interrupt flag, InterruptedException, and restoring the flag, Thread-confined state with ThreadLocal, and the leak risk when threads are pooled, Executors and Futures - and building your own pool from a queue + workers + FutureTask, CompletableFuture pipelines: supplyAsync, exceptionally fallbacks, fan-out/fan-in aggregation

| | |
|---|---|
| Problems | 15 |
| Must-know | 5 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_TwoThreadsToSingleValue.java` | Data race vs atomic counter |
| 2 | `A05_ThreadInterruptDemo.java` | Cooperative cancellation via interrupt |
| 3 | `C02_ProducerConsumerAIVersion.java` | Bounded buffer, while-guard wait |
| 4 | `C04_WriterReader.java` | ReentrantLock with two Conditions |
| 5 | `D02_CustomFutureThreadPool.java` | Build an executor from scratch |

## Full practice order


### A — Building blocks

- `A01_TwoThreadsToSingleValue.java` — Data race vs atomic counter **[must-know]**
  - The entry point to everything else: two threads, one counter, atomic lands on 0 and the plain int does not - you cannot reason about any later file without seeing read-modify-write break first.
- `A02_IncrementByTwhoThreads.java` — volatile is visibility, not atomicity
  - Immediately after the race is visible, this kills the most common wrong fix - volatile on the shared array still loses updates, which motivates locks and CAS.
- `A03_StaticValChat.java` — Monitor identity and lock scope
  - Once you accept you need a lock, the next question is which lock - static vs instance state under one class-object monitor, plus executor shutdown and awaitTermination.
- `A04_ThreadLocalDemo.java` — Thread-confined state
  - The other side of shared mutable state: avoid sharing entirely. Belongs here because it uses a pool, which sets up the reuse-and-leak discussion later.
- `A05_ThreadInterruptDemo.java` — Cooperative cancellation via interrupt **[must-know]**
  - Cancellation is a primitive, not an afterthought - the interrupt flag, InterruptedException from sleep, and clean exit are assumed by the worker loop in the custom pool.
- `A06_CountDownLatchDemo.java` — Latch coordination + blocking queue
  - Closes the primitives tier by introducing wait-for-N-workers and a thread-safe queue - the two ingredients the producer-consumer and fan-out problems later rebuild by hand.

### B — Easy

- `B01_CompletableFutureExceptionHandling.java` — Async pipeline with fallback
  - The simplest async warm-up: supplyAsync, exceptionally, join - three calls you must be fluent in before the CSV fan-out problem in tier C.
- `B02_ThreadGroupManagement.java` — Bulk interrupt over a ThreadGroup
  - An easy applied follow-on to the interrupt primitive; ranked last in the tier because ThreadGroup is legacy API you should know only well enough to say why you would use an ExecutorService instead.

### C — Medium

- `C01_ProducerConsumer.java` — wait/notify on a shared buffer
  - The canonical first wait/notify problem - and worth doing in its flawed single-slot form first so the corrected version below has something concrete to correct.
- `C02_ProducerConsumerAIVersion.java` — Bounded buffer, while-guard wait **[must-know]**
  - The version you must be able to write under pressure: capacity bound, while not if around wait, notifyAll - it is the single most-asked concurrency coding question and every turn-taking problem after it reuses this shape.
- `C03_OddEvenPrinter.java` — Two-thread strict alternation
  - Same predicate-plus-wait machinery as the bounded buffer, now used for ordering rather than capacity - the standard bridge into sequencing questions.
- `C04_WriterReader.java` — ReentrantLock with two Conditions **[must-know]**
  - The upgrade from notifyAll to signalling exactly the right waiters; owning separate condition queues is what makes N-way sequencing and any real bounded buffer tractable.
- `C05_MultiCSVProcessor.java` — CompletableFuture fan-out/fan-in
  - The applied capstone of the async strand - parallelise per-file work on a bounded pool, join all futures, merge partial stats - and the shape senior Java interviews actually hand you.

### D — Hard

- `D01_NthreadsKnumbrs.java` — N-thread modulo sequencing
  - Generalising odd/even to N threads needs the non-obvious parts: a modulo turn predicate, notifyAll not notify, and a termination rule that does not leave threads parked forever.
- `D02_CustomFutureThreadPool.java` — Build an executor from scratch **[must-know]**
  - The staff/principal favourite: workers blocking on a queue, FutureTask carrying result and exception back to the caller, and the shutdown-and-interrupt story - it assumes every primitive above at once.

## Interview readiness

This is a strong, genuinely above-average folder for the "make threads take turns" half of Java concurrency and a weak one for the "make a shared service safe under load" half. The 15 files cover the classic teaching sequence properly and with real depth: a visible data race against AtomicInteger, the instance-vs-class monitor question, wait/notify with a predicate, ReentrantLock with two Condition queues for targeted signalling, the odd/even and N-threads-modulo ordering puzzles, producer-consumer, CountDownLatch with a timed await, cooperative interrupt, ThreadLocal, a hand-rolled pool built from a BlockingQueue plus FutureTask, and a CompletableFuture fan-out/fan-in. Someone who can whiteboard all of that will clear a mid-level Java screen comfortably. The gap is that every problem here is a small synchronization exercise with a fixed, known set of threads, and none of them is the thing a staff interviewer actually reaches for, which is a shared component under concurrent load: no deadlock anywhere in the workspace (no two-lock inversion, no tryLock-with-timeout, no lock ordering), no ReentrantReadWriteLock or StampedLock (WriterReader looks like readers-writers but is a one-reader/one-writer ping-pong, so the read-mostly cache question is untouched), no Semaphore at all, no ConcurrentHashMap or computeIfAbsent in any Java-Core file, no CyclicBarrier or Phaser, and no CAS retry loop written by hand. The CompletableFuture work also stops at supplyAsync plus exceptionally plus a blocking join fan-in; thenCompose, thenCombine, allOf and timeouts appear nowhere, which matters a lot for a Spring/microservices-flavoured loop. Practically: he is interview-ready for the puzzle round and would likely stall on "here is a cache/pool/rate limiter, make it thread-safe and now tell me why it deadlocks." Closing the first five gaps below would move this folder from solid-mid to staff-credible, and they are mostly 40-to-80-line files in the same style as what is already there. Note that double-checked-locking singleton does not belong on the missing list: it already exists, correctly done with volatile plus reflection and serialization guards, at 03-LLD\Design-Patterns\1. Creational Design Patterns\Singleton.java, and it should stay there rather than be duplicated.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Deadlock: reproduce it with two locks taken in opposite order, then fix it (global lock ordering, and tryLock with timeout plus backoff). Extend to Dining Philosophers. | Lock-ordering deadlock, ReentrantLock.tryLock(timeout), livelock vs deadlock, jstack/ThreadMXBean.findDeadlockedThreads for detection | This is the single most-asked Java concurrency question after producer-consumer, and the word 'deadlock' does not appear in a single .java file anywhere in the workspace. The standard ask is 'write code that deadlocks' followed immediately by 'now fix it without removing either lock' - and the expected answer (impose a total order on lock acquisition, e.g. by System.identityHashCode when the two accounts are symmetric) is not something you derive on the spot. The bank-transfer variant (transfer(a,b) racing transfer(b,a)) is the usual framing at product companies. He has written plenty of two-lock-free code but never a two-lock hold-and-wait, so he has no muscle memory for the fix. |
| high | Readers-writers: a concurrent key-value store or config cache where many readers run in parallel and a writer gets exclusive access. | ReentrantReadWriteLock (read lock shared, write lock exclusive), lock downgrading, fair vs unfair and writer starvation, StampedLock optimistic reads as the follow-up | WriterReader.java has the right filename but is not this problem - it is a strict one-reader/one-writer alternation using two Conditions, which is a turn-taking puzzle. ReadWriteLock and StampedLock appear nowhere in the workspace. The interview version is 'this cache is read 95% of the time and synchronized is killing throughput, fix it', and the expected answer is a read/write lock plus a discussion of why you cannot upgrade a read lock to a write lock (instant deadlock) but can downgrade. Without this he will reach for synchronized or a single ReentrantLock and lose the read-concurrency point entirely. |
| high | Make this cache thread-safe: a memoizing cache in front of a slow computation, then bound it as an LRU. | ConcurrentHashMap.computeIfAbsent for atomic check-then-act, why get-then-put is a race that duplicates expensive work, CompletableFuture-valued map as the JCiP Memoizer, Collections.synchronizedMap vs ConcurrentHashMap, LinkedHashMap access-order LRU plus the lock | ConcurrentHashMap and computeIfAbsent do not appear in any 02-Java-Core file (only in DSA solutions, used single-threaded). This is the most common 'real code' concurrency question at product companies because it has a clean wrong answer everyone gives first: synchronize the whole method, or use get-then-put and not notice two threads both compute the same value. The staff-level follow-ups - why computeIfAbsent must not do long or recursive work while holding the bin lock, and why you store a Future rather than the value so the second caller waits instead of recomputing - are exactly the depth he is being screened for. |
| high | Semaphore-bounded resource pool (N database connections, M concurrent callers) and a thread-safe token-bucket rate limiter. | Semaphore acquire/release with try-finally, tryAcquire with timeout for fast-fail, permits vs a queue, refill arithmetic under a lock or via AtomicLong CAS | Semaphore appears nowhere in the workspace - it is the one core java.util.concurrent primitive he has zero exposure to, and it is the standard answer to any 'limit concurrency to N' question. The pairing matters at senior level: CountDownLatch (which he has) is one-shot and counts down to a gate, Semaphore is reusable and counts permits, and interviewers ask him to distinguish them. The rate limiter is the applied form and is a near-guaranteed question for a product company doing anything API-facing; the failure mode he should be able to name is releasing outside a finally so a thrown exception permanently leaks a permit. |
| medium | Async orchestration: call service A, feed its result into B, call C in parallel, combine all three, apply a per-call timeout and a fallback. | thenCompose vs thenApply (flattening a nested future), thenCombine, allOf plus join for a typed fan-in, orTimeout/completeOnTimeout, supplying an explicit Executor instead of leaking onto ForkJoinPool.commonPool | MultiCSVProcessor does fan-out with supplyAsync and then blocks on join per future, and CompletableFutureExceptionHandling stops at exceptionally. thenCompose, thenCombine, allOf and orTimeout appear in zero files. thenApply-vs-thenCompose is one of the most frequently asked Java 8+ questions and the giveaway wrong answer is returning CompletableFuture<CompletableFuture<T>>. The other point he currently cannot make is that supplyAsync without an executor runs on the common pool, so a blocking HTTP call there starves every parallel stream in the JVM - a standard senior follow-up in any Spring or microservices loop. |
| medium | Shut down a pool cleanly: drain in-flight work, cancel what is stuck, and enforce a per-task timeout. | shutdown vs shutdownNow and the List<Runnable> it returns, awaitTermination in a two-phase loop, Future.cancel(true) vs get(timeout), invokeAll/invokeAny with a timeout, why shutdownNow only interrupts and cannot stop an uninterruptible task | StaticValChat busy-spins on isTerminated() to wait for completion, which is the tell that this is not internalised. He has awaitTermination elsewhere (CsvAppender, CsvReader, WorkFlowExecutor), so he is not starting from zero - hence medium, not high. But the specific things that get probed are the ones his code does not show: that shutdownNow hands back the queued tasks that never ran so you can log or requeue them, that the canonical pattern is shutdown then awaitTermination then shutdownNow then awaitTermination again, and that Future.cancel(true) only sets the interrupt flag so a task that ignores interrupts runs forever. This is asked as a follow-up to almost any executor question, which he will definitely be asked given CustomFutureThreadPool is on his list. |
| medium | Implement getAndIncrement yourself, then a lock-free stack with an AtomicReference head. | compareAndSet retry loop, why the loop body must re-read the current value each attempt, AtomicReference for linked nodes, the ABA problem and AtomicStampedReference, LongAdder for high-contention counting | He has covered the theme 'AtomicInteger fixes the ++ race' but only as an API consumer - he calls getAndIncrement and getAndDecrement and has never written a do/while CAS loop. The standard staff follow-up to his own TwoThreadsToSingleValue is 'fine, now implement getAndIncrement without a lock', and the answer is a five-line retry loop that is hard to produce cold. The lock-free stack extends it to a linked structure and sets up ABA, and LongAdder is the practical answer to 'CAS under heavy contention spins and burns CPU - what do you use in a real counter?'. This is the difference between knowing the class and understanding CAS. |
| medium | Phase synchronization: N workers process a chunk, all wait at a barrier, a merge step runs, then the next round begins. | CyclicBarrier with a barrier action, reuse across rounds, BrokenBarrierException, and as the deeper follow-up implementing your own CountDownLatch or Semaphore from wait/notify | CyclicBarrier and Phaser appear nowhere. 'CountDownLatch vs CyclicBarrier' is a stock question and his CountDownLatchDemo gives him only half the answer - he can say a latch counts down once and cannot be reused, but has never written the reusable barrier or hit BrokenBarrierException when one participant dies and the others hang forever. Iterative simulations and multi-round aggregation are the natural framing. The build-your-own-latch variant is the highest-signal version for staff level: it forces the generation counter and the while-loop predicate, and it ties directly back to the wait/notify work he has already done rather than being new material. |

