# Language-Features — must-know order

**Techniques in this topic:** Compile-time binding vs runtime dispatch: static calls on null refs, varargs overload resolution, default-method diamond conflicts, Reference semantics and immutability: shallow clone, defensive copies, and lock-free CAS updates on immutable snapshots, Generics and type erasure: generic methods, and the super-type-token trick that recovers a type argument at runtime, Stream collectors: groupingBy with downstream collectors (counting, maxBy, filtering, collectingAndThen), flatMap and boxing, Ordered collections and the compareTo/equals contract: TreeMap navigation methods and the TreeSet removal trap, Reflection and JDK dynamic proxies: the mechanism behind Spring AOP, @Transactional and mocking frameworks, Everyday API correctness: java.time offset parsing to Instant, SLF4J parameterized logging with a trailing Throwable, enum constant-specific bodies

| | |
|---|---|
| Problems | 15 |
| Must-know | 4 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `C01_Streams.java` | Core stream and collector pipelines |
| 2 | `C02_Java8StreamsCollectingAndThen.java` | Downstream collectors, collectingAndThen |
| 3 | `C03_DynamicProxyDemo.java` | JDK dynamic proxy, InvocationHandler |
| 4 | `D03_ImmutablePerson.java` | Immutable snapshot with CAS retry |

## Full practice order


### A — Building blocks

- `A01_StaticNullRefWithStaticMethhodCall.java` — Static binding on null reference
  - The most primitive fact about Java calls — static methods bind to the declared type at compile time, so no NPE — and every dispatch question below assumes it.
- `A02_VarArgDemo.java` — Varargs and overload resolution
  - Extends compile-time resolution from file 1 into the three-phase overload rules and the fact that varargs is just array sugar.
- `A03_ArrayCloneExample.java` — Shallow clone, reference semantics
  - Follows naturally from varargs-as-arrays, and establishes the shallow-vs-deep copy primitive that the immutability and defensive-copy problems all depend on.
- `A04_GenericsInJava.java` — Static generic method basics
  - Introduces type parameters and inference — the prerequisite for the erasure and TypeToken problem later, and for reading collector signatures.
- `A05_EnumDemo.java` — Enum constant-specific bodies
  - Needs the override/abstract-class basics already covered, and gives the enum-as-strategy building block used in real domain modelling.
- `A06_FloorKeyExample.java` — TreeMap floorKey navigation
  - The sorted-map primitive (O(log n) predecessor lookup) that the TreeSet removal trap in tier D directly builds on.

### B — Easy

- `B01_DefaultM1InABInterfaces.java` — Default method diamond resolution
  - The first non-trivial dispatch question: it applies the tier-A binding rules to interfaces and has a single memorable answer, A.super.m1().
- `B02_StringZonedDateTimeToInstant.java` — Offset parsing to Instant
  - Self-contained java.time warm-up; teaches the formatter to OffsetDateTime to Instant chain before any time zone handling appears in harder problems.
- `B03_ExceptionLoggingDemo.java` — SLF4J parameterized exception logging
  - Easy but high-frequency in senior screens: the trailing Throwable argument is what prints the stack trace, unlike string concatenation.

### C — Medium

- `C01_Streams.java` — Core stream and collector pipelines **[must-know]**
  - The entry point to the stream tier — frequency counting, filtering to arrays, flatMap and groupingBy with maxBy — and everything in C2 assumes this vocabulary.
- `C02_Java8StreamsCollectingAndThen.java` — Downstream collectors, collectingAndThen **[must-know]**
  - Layers on C1: unwrapping maxBy's Optional via collectingAndThen and Collectors.filtering, which is the staple senior-level grouping question.
- `C03_DynamicProxyDemo.java` — JDK dynamic proxy, InvocationHandler **[must-know]**
  - Needs interfaces and generics from tier A; explains Spring AOP, @Transactional and Mockito, and sets up the reflection thinking the erasure problem needs.

### D — Hard

- `D01_TreeSetRemoveExample.java` — Comparator-equals contract trap
  - Builds on the TreeMap primitive and requires the non-obvious insight that TreeSet.remove uses compareTo, not equals — the classic Dijkstra decrease-key bug.
- `D02_GenericTypeDynamically.java` — Super type token, erasure
  - Extends the generics primitive with the insight that an anonymous subclass preserves the type argument erasure would otherwise discard.
- `D03_ImmutablePerson.java` — Immutable snapshot with CAS retry **[must-know]**
  - The hardest and most staff-relevant: it combines defensive copying from A3 with a lock-free compareAndSet retry loop, so it belongs last.

## Interview readiness

This folder is unusually strong on the "second-order" Java topics that separate a senior candidate from a mid-level one — compile-time vs runtime dispatch, type erasure and the super-type-token trick, JDK dynamic proxies as the mechanism under Spring AOP, stream collectors beyond the basics, and lock-free updates on immutable snapshots. That is a genuinely impressive spread and it would carry him through the deeper half of a staff-level language round. The problem is that it is strong at the top and thin at the bottom: the fifteen problems here are heavily weighted toward exotica and mechanism, while several of the highest-frequency questions in any Java interview have no artifact at all. There is nothing on the equals/hashCode contract as it plays out in a HashMap (TreeSetRemoveExample covers the compareTo/equals divergence in a sorted set, which is a different mechanism), nothing on ConcurrentModificationException or the Arrays.asList / List.of mutation traps, nothing on string interning or the Integer cache, and his two generics files are a trivial <T> passthrough plus a Gson TypeToken — no wildcards, no PECS, no array covariance. He also has zero record or sealed-type code, only Q&A notes, which is a real exposure in 2026 when "model this value type" is a routine warm-up. The net effect is an odd risk profile: he is more likely to lose the interview in the first ten minutes on a question he considers beneath him than in the last twenty on proxies and erasure. Closing the five high-priority gaps below would take a few hours and would move this folder from "impressive but spiky" to genuinely interview-complete.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | EqualsHashCodeContractDemo — put an object into a HashMap/HashSet, mutate a field that hashCode() uses, then show get()/contains() returns null/false while the entry is still visible when iterating; plus the override-equals-but-not-hashCode variant and the getClass() vs instanceof symmetry question. | The equals/hashCode contract and hash-bucket placement: hash is computed once at insertion and never recomputed, so a mutated key lands in the wrong bucket forever. | This is the single most-asked question in a Java interview and there is no artifact for it. TreeSetRemoveExample does contain a correct equals/hashCode pair, but it is exercising the compareTo/equals divergence in a sorted (red-black tree) structure — a completely different mechanism from hash bucketing. An interviewer probing the HashMap side would get nothing from his prep, and the follow-up ('why is a mutable key dangerous, what makes String a good key') is where senior candidates are separated from mid-level ones. |
| high | ConcurrentModificationDemo — remove from a List while iterating with an enhanced for loop to trigger ConcurrentModificationException, then fix it three ways (Iterator.remove, removeIf, collect-then-removeAll); in the same file show Arrays.asList(...).add() throwing UnsupportedOperationException, List.of(null) throwing NPE, and a subList being a live view whose parent mutation invalidates it. | modCount / fail-fast iterators, and the three different 'list-like' contracts hiding behind the List interface (fully mutable, fixed-size view, truly immutable). | CME is a top-three interview question and a top-three production bug, and nothing in this folder touches it. Arrays.asList appears four times across Streams.java and Java8StreamsCollectingAndThen.java but purely as a data producer — he has never been forced to confront that the thing it returns is a fixed-size view, not an ArrayList. Being asked 'you called add() on this and it blew up in prod, why' with no prior exposure is a plausible and avoidable loss. |
| high | StringIdentityAndCache — compare literals, new String(), runtime-concatenated strings, compile-time constant expressions and intern() with both == and equals(); then the parallel Integer case: Integer.valueOf(127) == Integer.valueOf(127) is true but 128 is false, and Integer vs int comparison silently unboxing. | String constant pool, compile-time constant folding, String.intern(), and the Integer autoboxing cache (-128..127) with the NPE-on-unboxing trap. | Tricky8.java touches String immutability only in the narrow sense of 'concat() without reassignment does nothing' — it never gets near identity, pooling or interning, and nothing anywhere touches the Integer cache. This pair is the classic opening whiteboard question at product companies precisely because it is cheap to ask and instantly reveals whether someone understands reference vs value semantics. There is no artifact here to fall back on. |
| high | WildcardsAndPECS — write copy(List<? extends T> src, List<? super T> dst) and show why List<String> cannot be assigned to List<Object>; contrast with array covariance by storing an Integer into an Object[] that is really a String[] and catching ArrayStoreException; add a method pair that fails to compile due to erasure signature collision (List<String> vs List<Integer>). | Producer-Extends / Consumer-Super, generic invariance vs array covariance, and erasure-driven signature clashes. | His two generics artifacts are GenericsInJava.java (a trivial static <T> void genericMethod(T t1) that just prints) and GenericTypeDynamically.java (Gson TypeToken). Neither has a single wildcard. 'Why is List<String> not a List<Object> when String[] is an Object[]?' is a standard senior-level question, and PECS is the expected answer for any API-design follow-up. He clearly understands erasure at a deep level from the super-type-token work, which makes this gap especially cheap to close and especially costly to leave open — an interviewer who sees the TypeToken sophistication will ask the wildcard question expecting a strong answer. |
| high | RecordsAndSealedTypes — reimplement the ImmutablePerson case as a record with a compact constructor that validates and defensively copies the list, show what the auto-generated equals/hashCode/toString actually do (and that the copy is still needed because records are only shallowly immutable), then model a small closed hierarchy with a sealed interface plus permits and switch over it exhaustively without a default branch. | Records as nominal tuples: compact constructors, shallow immutability, auto-generated members; sealed hierarchies and exhaustiveness checking in switch. | There is no record or sealed code anywhere in 02-Java-Core — only notes/Java_9_to_21_QA.md, which is prose, not practice. CompilationCheck_PM.java does pattern-matching switch on types but over an open Object, so it gets none of the sealed/exhaustiveness benefit. Asked to model an immutable value type in 2026 he would reach for the 2015-era ImmutablePerson pattern, which reads as dated for a senior/staff candidate. The exhaustive-switch-over-sealed-interface idiom in particular is now a routine follow-up to any 'how would you represent these states' question. |
| medium | TryWithResourcesSemantics — a resource whose close() throws while the try body also throws, showing which exception propagates and recovering the other via getSuppressed(); two resources in one statement proving close order is reverse of declaration; and a finally block containing return that silently swallows an in-flight exception. | Suppressed exceptions, reverse close ordering, and the control-flow rule that a return/throw in finally discards a pending exception. | He uses try-with-resources correctly in IO/CsvReader.java, IO/CsvAppender.java and Concurrency/MultiCSVProcessor.java, so the syntax is not the gap — the semantics are. 'Both the body and close() throw; which one does the caller see?' is a standard staff-level question because masking the real exception behind a close() failure is a genuine production debugging story. Nothing in his files demonstrates getSuppressed or the finally-return trap, so this is unverified rather than known-weak — but it is a plausible ask he currently has no evidence for. |
| medium | OptionalCorrectness — a small refactor exercise turning isPresent()/get() chains into map/filter/flatMap/orElseGet; demonstrate that orElse() evaluates its argument eagerly even when the Optional is present (with an expensive or side-effecting call) while orElseGet() does not; and show why Optional is wrong as a field or method parameter. | Optional as a return-type-only combinator: eager vs lazy defaults, flatMap for nested Optionals, and the anti-patterns. | Optional appears only incidentally in his code as the output of Collectors.maxBy in Streams.java and Java8StreamsCollectingAndThen.java — he has never written Optional-handling logic as the subject of a problem. The orElse-vs-orElseGet eager-evaluation distinction is a favourite code-review question at product companies because it is a real latency bug hiding in idiomatic-looking code, and 'should this field be Optional?' is a standard API-design follow-up. |
| medium | ComparatorContractAndChaining — build a multi-key sort with comparing().thenComparing().reversed() and handle nulls via Comparator.nullsFirst/nullsLast; then deliberately write an inconsistent/non-transitive comparator and sort a list large enough (>32 elements) to make TimSort throw 'Comparison method violates its general contract!'. | Comparator composition, null-safe ordering, and the transitivity/consistency requirements that TimSort actively verifies at scale. | He uses Comparator only as a one-line inline argument (Comparator.comparingDouble(Employee::getSalary)) and has never composed one or handled nulls in an ordering. Multi-key sorting with a null-safe tiebreaker comes up in most Java coding rounds. The contract-violation failure is the natural senior-level extension and pairs directly with the compareTo/equals theme he already started in TreeSetRemoveExample — notably, it only manifests on larger inputs, which makes it a great 'it passed in test and failed in prod' story an interviewer can probe. |

