# Java Core

Language and JVM demos, one runnable file each, plus interview Q&A notes.

## Notes

- [Core Java Q&A](notes/Core_Java_QA.md)
- [Java 9 to 21 Q&A](notes/Java_9_to_21_QA.md)
- [Java 8 Streams Q&A](notes/Java8_Streams_QA.md)
- [Garbage Collection Q&A](notes/GC_QA.md)
- [Generics Q&A](notes/Generics_QA.md)
- [Collections Q&A](notes/Collections_QA.md)

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [Concurrency](Concurrency/) | 13 | 4 | Threads, locks, producer-consumer, CompletableFuture, ThreadLocal, executors. |
| [IO](IO/) | 4 | 1 | Scanner, CSV reading and appending, basic input and output. |
| [Language-Features](Language-Features/) | 14 | 3 | Generics, enums, varargs, immutability, dynamic proxies, streams, default methods, date-time. |
| [Security-Crypto](Security-Crypto/) | 3 | 1 | AES, RSA and Caesar cipher examples. |
| [Tricky-MCQ](Tricky-MCQ/) | 11 | 0 | Output-prediction puzzles: what does this print, does it compile. |
| **Total** | **45** | **9** | |

## Concurrency

Threads, locks, producer-consumer, CompletableFuture, ThreadLocal, executors.

**Do these first:** [A01_TwoThreadsToSingleValue.java](Concurrency/A01_TwoThreadsToSingleValue.java), [A04_ThreadInterrupt.java](Concurrency/A04_ThreadInterrupt.java), [C03_WriterReader.java](Concurrency/C03_WriterReader.java), [D02_CustomFutureThreadPool.java](Concurrency/D02_CustomFutureThreadPool.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_TwoThreadsToSingleValue.java](Concurrency/A01_TwoThreadsToSingleValue.java) * | Two Threads, One Counter: atomic vs plain int | Java Core / Concurrency | "It is a single line of Java" says nothing about atomicity. |
| [A02_StaticValChat.java](Concurrency/A02_StaticValChat.java) | Which Monitor Are You Locking? | Java Core / Concurrency | Locks live on objects, not on code. |
| [A03_ThreadLocal.java](Concurrency/A03_ThreadLocal.java) | ThreadLocal: per-thread state, and how it leaks in a pool | Java Core / Concurrency | ThreadLocal solves sharing by not sharing: confine the state to one thread. |
| [A04_ThreadInterrupt.java](Concurrency/A04_ThreadInterrupt.java) * | Stopping a Thread the Only Legal Way: interrupt | Java Core / Concurrency | interrupt() is a request, not a kill. |
| [A05_CountDownLatch.java](Concurrency/A05_CountDownLatch.java) | CountDownLatch + PriorityBlockingQueue | Java Core / Concurrency | A latch counts DOWN once and can never be reset - it is a one-shot gate, which is exactly why it is the right tool for "wait for startup / wait for N results" and the wrong tool for a repeating rendezvous (that is Cyclic |
| [B01_CompletableFutureExceptionHandling.java](Concurrency/B01_CompletableFutureExceptionHandling.java) | CompletableFuture exception handling | Java Core / Concurrency / Easy | An async failure is a completion state, not a thrown exception at the call site. |
| [B02_ThreadGroupManagement.java](Concurrency/B02_ThreadGroupManagement.java) | ThreadGroup: bulk interrupt of a set of threads | Java Core / Concurrency / Easy | Interrupt is a request, not a kill. |
| [C01_ProducerConsumer.java](Concurrency/C01_ProducerConsumer.java) | Producer-Consumer on a shared queue | Java Core / Concurrency / Medium | wait() must always sit inside a `while` that re-tests the condition, never an `if`. |
| [C02_OddEvenPrinter.java](Concurrency/C02_OddEvenPrinter.java) | Odd / Even printer: two threads, strict alternation | Java Core / Concurrency / Medium | The turn is not a variable you flip, it is a predicate over shared state: value % 2 == myParity. |
| [C03_WriterReader.java](Concurrency/C03_WriterReader.java) * | Writer / Reader handoff with two Conditions | Java Core / Concurrency / Medium | One lock can own several wait queues. |
| [C04_MultiCSVProcessor.java](Concurrency/C04_MultiCSVProcessor.java) | Aggregate stats across many CSV files | Concurrency / Medium | Parallel aggregation is safe when the per-task result is a value you can FOLD, and the fold has a neutral element. |
| [D01_NThreadsKNumbers.java](Concurrency/D01_NThreadsKNumbers.java) | Print 1..K in order using N threads | Concurrency / Hard | Two rules make every "ordered handoff between threads" problem work, and they are both about the WAKE-UP, not the sleep: - notifyAll, not notify. |
| [D02_CustomFutureThreadPool.java](Concurrency/D02_CustomFutureThreadPool.java) * | Build a thread pool from scratch | Concurrency / Hard | - poll(timeout) rather than take(), so a worker can notice the shutdown flag. |

**Worth adding next:**

- Deadlock: reproduce it with two locks taken in opposite order, then fix it (global lock ordering, and tryLock with timeout plus backoff). Extend to Dining Philosophers.: Lock-ordering deadlock, ReentrantLock.tryLock(timeout), livelock vs deadlock, jstack/ThreadMXBean.findDeadlockedThreads for detection
- Readers-writers: a concurrent key-value store or config cache where many readers run in parallel and a writer gets exclusive access.: ReentrantReadWriteLock (read lock shared, write lock exclusive), lock downgrading, fair vs unfair and writer starvation, StampedLock optimistic reads as the follow-up
- Make this cache thread-safe: a memoizing cache in front of a slow computation, then bound it as an LRU.: ConcurrentHashMap.computeIfAbsent for atomic check-then-act, why get-then-put is a race that duplicates expensive work, CompletableFuture-valued map as the JCiP Memoizer, Collections.synchronizedMap vs ConcurrentHashMap, LinkedHashMap access-order LRU plus the lock

## IO

Scanner, CSV reading and appending, basic input and output.

**Do these first:** [C01_CsvReader.java](IO/C01_CsvReader.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_InputOutput.java](IO/A01_InputOutput.java) | Read a number from standard input | Java Core / IO / Easy | Scanner is a token stream, not a line reader. |
| [A02_Scanner.java](IO/A02_Scanner.java) | Scanner token loop, and the next()/nextInt()/nextLine() trap | Java Core / IO / Easy | Token reads and line reads measure the input differently, so they cannot be mixed carelessly. |
| [C01_CsvReader.java](IO/C01_CsvReader.java) * | Stream a CSV and process rows on a thread pool | Java Core / IO / Medium | The reader must stay single-threaded and sequential - a file has one cursor - while the per-record work fans out. |
| [D01_CsvAppender.java](IO/D01_CsvAppender.java) | Append records to one CSV from many threads | Java Core / IO / Hard | A file is a single append cursor, so writes are serialised no matter what you do. |

**Worth adding next:**

- Top-K frequent words in a large file (word-frequency counter with a bounded-memory follow-up): Stream with BufferedReader, split/normalise tokens, HashMap<String,Integer> counts, then a size-K min-heap for the top K. Follow-up: when the distinct-key set exceeds heap, shard by hash(word) % N into N spill files, count each shard independently, merge the per-shard top-Ks.
- Sort a file larger than RAM (external merge sort): Read a fixed-size chunk, sort it in memory, write it to a temp file, repeat; then k-way merge the runs using a PriorityQueue of one BufferedReader-backed cursor per run, writing the merged output as a stream. Discuss run size vs merge fan-in, and deleting temp files in a finally/try-with-resources.
- Charset-correct read and write: byte streams vs character streams: Replace FileReader/FileWriter with InputStreamReader/OutputStreamWriter over a FileInputStream/FileOutputStream with an explicit StandardCharsets.UTF_8, or Files.newBufferedReader(path, UTF_8). Show why FileReader uses the platform default charset, why a multi-byte UTF-8 character must never be split across a manually-managed byte[] buffer, and when you must stay on InputStream (binary) rather than Reader (text).

## Language-Features

Generics, enums, varargs, immutability, dynamic proxies, streams, default methods, date-time.

**Do these first:** [C01_Streams.java](Language-Features/C01_Streams.java), [C02_DynamicProxy.java](Language-Features/C02_DynamicProxy.java), [D03_ImmutablePerson.java](Language-Features/D03_ImmutablePerson.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_StaticNullRefWithStaticMethodCall.java](Language-Features/A01_StaticNullRefWithStaticMethodCall.java) | Static method call on a null reference | Java language / Easy | Static = bound at compile time to the declared type. |
| [A02_VarArgDemo.java](Language-Features/A02_VarArgDemo.java) | Varargs and overload resolution | Java language / Easy | Varargs is array sugar resolved at compile time. |
| [A03_ArrayClone.java](Language-Features/A03_ArrayClone.java) | Array clone() is a shallow copy | Java language / Easy | clone(), Arrays.copyOf() and System.arraycopy() all copy one level. |
| [A04_GenericsInJava.java](Language-Features/A04_GenericsInJava.java) | Static generic methods and type inference | Java language / Easy | Generics are a compile-time contract. |
| [A05_EnumDemo.java](Language-Features/A05_EnumDemo.java) | Enums with constant-specific bodies | Java language / Easy | A constant-specific body is an anonymous subclass. |
| [A06_FloorKey.java](Language-Features/A06_FloorKey.java) | TreeMap floorKey and friends | Java Core / Easy | Reach for TreeMap the moment a problem says "nearest", "previous", "next", "last value before time T" or "which bucket does this fall into". |
| [B01_DefaultM1InABInterfaces.java](Language-Features/B01_DefaultM1InABInterfaces.java) | Default method diamond: A.super.m1() | Java Core / Easy | Java has no multiple inheritance of state, but default methods give it multiple inheritance of behaviour - so it needs one tie-break rule. |
| [B02_StringZonedDateTimeToInstant.java](Language-Features/B02_StringZonedDateTimeToInstant.java) | Parsing an offset date-time string into an Instant | Java Core / Easy | Pick the java.time type by how much the text actually tells you. |
| [B03_ExceptionLogging.java](Language-Features/B03_ExceptionLogging.java) | Logging an exception so the stack trace survives | Java Core / Easy | The exception argument is positional, not typed magic: it must be the last argument AND unmatched by any "{}". |
| [C01_Streams.java](Language-Features/C01_Streams.java) * | Stream and Collector recipes | Java Core / Medium | A stream pipeline is source -> intermediate ops -> one terminal op, and the whole shape of the result is decided by the terminal collector. |
| [C02_DynamicProxy.java](Language-Features/C02_DynamicProxy.java) * | JDK Dynamic Proxy (InvocationHandler) | Java core / Medium | The proxy is typed by INTERFACES, not by the target object. |
| [D01_TreeSetRemove.java](Language-Features/D01_TreeSetRemove.java) | TreeSet uses compareTo, never equals | Java core / Hard | A sorted collection's notion of identity IS its comparator. |
| [D02_GenericTypeDynamically.java](Language-Features/D02_GenericTypeDynamically.java) | Super type token: picking Response<T> at runtime | Java core / Hard | Erasure removes the type argument from OBJECTS, not from CLASS DECLARATIONS. |
| [D03_ImmutablePerson.java](Language-Features/D03_ImmutablePerson.java) * | Immutable object + AtomicReference CAS retry | Java core / Hard | CAS turns a read-modify-write into an atomic "publish only if nothing changed". |

**Worth adding next:**

- EqualsHashCodeContractDemo — put an object into a HashMap/HashSet, mutate a field that hashCode() uses, then show get()/contains() returns null/false while the entry is still visible when iterating; plus the override-equals-but-not-hashCode variant and the getClass() vs instanceof symmetry question.: The equals/hashCode contract and hash-bucket placement: hash is computed once at insertion and never recomputed, so a mutated key lands in the wrong bucket forever.
- ConcurrentModificationDemo — remove from a List while iterating with an enhanced for loop to trigger ConcurrentModificationException, then fix it three ways (Iterator.remove, removeIf, collect-then-removeAll); in the same file show Arrays.asList(...).add() throwing UnsupportedOperationException, List.of(null) throwing NPE, and a subList being a live view whose parent mutation invalidates it.: modCount / fail-fast iterators, and the three different 'list-like' contracts hiding behind the List interface (fully mutable, fixed-size view, truly immutable).
- StringIdentityAndCache — compare literals, new String(), runtime-concatenated strings, compile-time constant expressions and intern() with both == and equals(); then the parallel Integer case: Integer.valueOf(127) == Integer.valueOf(127) is true but 128 is false, and Integer vs int comparison silently unboxing.: String constant pool, compile-time constant folding, String.intern(), and the Integer autoboxing cache (-128..127) with the NPE-on-unboxing trap.

## Security-Crypto

AES, RSA and Caesar cipher examples.

**Do these first:** [A02_AES.java](Security-Crypto/A02_AES.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_CaesarCipher.java](Security-Crypto/A01_CaesarCipher.java) | Caesar Cipher - encrypt and decrypt | Easy | Mapping a letter to 0..25 turns the alphabet into a ring, and a shift cipher is addition on that ring. |
| [A02_AES.java](Security-Crypto/A02_AES.java) * | AES symmetric encryption with the JCE | Medium | The mode of operation, not AES itself, decides whether the output leaks anything. |
| [C01_RSA.java](Security-Crypto/C01_RSA.java) | RSA asymmetric encryption, and why it needs AES | Medium | RSA is a key-transport mechanism, not a data-encryption mechanism. |

**Worth adding next:**

- AES-GCM authenticated encryption with a random per-message IV: Cipher.getInstance("AES/GCM/NoPadding") with GCMParameterSpec(128, iv), a fresh 12-byte SecureRandom nonce per message, IV prepended to the ciphertext, plus a tamper test that flips one byte and shows AEADBadTagException on decrypt. Alongside it, an ECB-vs-CBC-vs-GCM comparison that encrypts a repeating plaintext under ECB and prints the identical ciphertext blocks.
- Password storage: salted hashing with PBKDF2 (and BCrypt/Argon2 as the comparison): SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256") with a per-user 16-byte SecureRandom salt and a high iteration count, storing algorithm+iterations+salt+hash in one encoded string, and verifying with MessageDigest.isEqual for constant-time comparison. Contrast against BCrypt's built-in salt and cost factor.
- HMAC request/webhook signing and verification: Mac.getInstance("HmacSHA256"), init with a SecretKeySpec shared secret, sign a canonical string (timestamp + body), verify on the receiving side with MessageDigest.isEqual rather than String.equals, and reject stale timestamps to block replay.

## Tricky-MCQ

Output-prediction puzzles: what does this print, does it compile.

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Tricky4.java](Tricky-MCQ/A01_Tricky4.java) | Virtual call on a subclass that does NOT override | Tricky MCQ / Easy | Static type = what you are ALLOWED to call. |
| [B01_Tricky2.java](Tricky-MCQ/B01_Tricky2.java) | Which makeSound() runs? | Tricky MCQ / Easy |  |
| [B02_Tricky5.java](Tricky-MCQ/B02_Tricky5.java) | Calling a subclass-only method through a parent reference | Tricky MCQ / Easy | A downcast does not change the object; it changes what the COMPILER believes about the expression, and hands the check over to the JVM. |
| [B03_Tricky6.java](Tricky-MCQ/B03_Tricky6.java) | Two main() methods in one class | Tricky MCQ / Easy | "Entry point" is a launcher contract, not a language rule. |
| [B04_Tricky7.java](Tricky-MCQ/B04_Tricky7.java) | Redeclaring a local variable in a nested block | Tricky MCQ / Easy | Locals cannot shadow locals. |
| [B05_Tricky8.java](Tricky-MCQ/B05_Tricky8.java) | concat() without assigning the result | Tricky MCQ / Easy | Every String "mutator" - concat, substring, trim, replace, toUpperCase, strip - returns a NEW String and leaves the receiver untouched. |
| [B06_Tricky10.java](Tricky-MCQ/B06_Tricky10.java) | Prefix vs postfix increment | Tricky MCQ / Easy |  |
| [C01_Tricky1.java](Tricky-MCQ/C01_Tricky1.java) | Static vs instance initializer order | Tricky MCQ / Medium |  |
| [C02_Tricky3.java](Tricky-MCQ/C02_Tricky3.java) | Can a static method override an instance method? | Tricky MCQ / Medium |  |
| [C03_Tricky9.java](Tricky-MCQ/C03_Tricky9.java) | Overload resolution when the argument is null | Tricky MCQ / Medium |  |
| [C04_CompilationCheck_PM.java](Tricky-MCQ/C04_CompilationCheck_PM.java) | Pattern matching for switch: which case wins? | Tricky MCQ / Medium |  |

`*` = must-know. Run any file with `tools/runjava <file>` from the repo root, or open it as an IntelliJ scratch.
