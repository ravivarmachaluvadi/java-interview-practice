# AAScratches: interview practice workspace

578 standalone Java programs and 36 Markdown notes, grouped by subject. Nothing here is a
build system project: every `.java` file is an IntelliJ-style scratch file that compiles and
runs on its own.

## Map

| Folder | What is inside | Start here |
|--------|----------------|------------|
| [01-DSA](01-DSA/) | 490 solved problems in 20 technique folders, plus notes and roadmaps | [01-DSA/README.md](01-DSA/README.md) |
| [02-Java-Core](02-Java-Core/) | 45 files: concurrency, language features, IO, crypto, tricky MCQs, plus Q&A notes | [02-Java-Core/README.md](02-Java-Core/README.md) |
| [03-LLD](03-LLD/) | 31 files: the GoF patterns, parking lot, Tic-Tac-Toe, hand-built data structures, a workflow executor | [03-LLD/README.md](03-LLD/README.md) |
| [04-HLD-System-Design](04-HLD-System-Design/) | Building blocks in code, plus system design keypoints and a payment system write-up | [04-HLD-System-Design/README.md](04-HLD-System-Design/README.md) |
| [05-Spring-Microservices](05-Spring-Microservices/) | Spring Boot, Security, transactions, Hibernate, microservices and Kafka notes; a sample orders service | [05-Spring-Microservices/README.md](05-Spring-Microservices/README.md) |
| [06-SQL](06-SQL/) | SQL interview Q&A and a query scratch file | [SQL_QA.md](06-SQL/SQL_QA.md) |
| [07-Interview-QA-Memory](07-Interview-QA-Memory/) | Three mixed-topic Q&A files written from real interview rounds | [Part 1](07-Interview-QA-Memory/Interview_Memory_QA_Part1.md) |
| [08-Reference](08-Reference/) | Git workflow, maths symbols, IntelliJ plugins, work techniques, an AI-assisted development playbook | [08-Reference/README.md](08-Reference/README.md) |

## How a practice file is laid out

Every file opens with the same block comment, so you can decide whether to read on without
scrolling:

```
PROBLEM                what is being asked, and the constraints that matter
EXAMPLE                concrete input to output, including the edge case
APPROACH               numbered steps you can follow against the code below
KEY INSIGHT            the one idea to be able to recite in an interview
COMPLEXITY             time and space, with the reason
INTERVIEW FOLLOW-UPS   the variants an interviewer asks next
RUN                    what main() prints
```

The title line carries the LeetCode number and difficulty, and `MUST-KNOW` on the 177 files
worth doing first. Design pattern, LLD and HLD files swap `KEY INSIGHT` for `KEY DECISIONS`
and add a section mapping each class to its role.

`main()` runs two to six cases covering a typical input, an edge case and often a trap, and
prints the actual result next to the expected one:

```
case 1: [0, 1]   expected [0, 1]
case 2 empty: []   expected []
```

So a file is working if every line reads the same on both sides of `expected`.

## Filename scheme

`<TIER><NN>_<Name>.java`, so alphabetical order is practice order.

| Tier | Meaning |
|---|---|
| **A** | Building block: the technique itself, or a primitive later files assume |
| **B** | Easy: standard warm-up |
| **C** | Medium: the bulk of real interview questions |
| **D** | Hard: needs a non-obvious insight |

Within a tier, `NN` runs in dependency order: earlier files teach what later ones assume.
**Tier is difficulty, not importance.** Kadane's algorithm is foundational but the problem is
medium, so it sits in `C` and is flagged must-know.

## Running a file

```bash
tools/runjava AAScratches/01-DSA/01-Arrays/A01_ArraySortedOrNot.java
tools/runjava A01_ArraySortedOrNot.java     # resolves by name, no path needed
tools/runjava --find TwoSum                 # locate files
```

Do not import this as a Maven, Gradle or IntelliJ project. 566 of 578 files declare no
package, folder names like `01-Arrays` are not valid Java identifiers, and 20 class names are
declared in more than one file (`TreeNode` in 45, `ListNode` and `Node` in 17 each,
`Solution` in 14). A project compiles a source root as one unit, so marking any folder as a
source root produces dozens of duplicate-class errors. These were always scratch files, which
the IDE compiles in isolation, and that is the model to keep.

To get the green run gutter back, copy the folders into IntelliJ's scratches directory
(`%APPDATA%\JetBrains\<IDE>\scratches`).

## What does not run on its own

| Files | Why |
|---|---|
| 8 under `05-Spring-Microservices/orders-springboot-project` | Need Spring, Lombok, Jackson and JUnit on the classpath |
| 4 under `03-LLD/WorkFlowExecutor` | Need Guava, and they are a package, not scratch files |
| `B04_Tricky7.java`, `C02_Tricky3.java` | Do not compile **on purpose**: the compile error is the answer |

Those 12 Spring and WorkFlowExecutor files carry a shortened header that explains their role
in the project instead of a runnable example. Everything else compiles and runs.
