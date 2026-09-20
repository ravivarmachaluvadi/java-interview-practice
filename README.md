# Java interview practice

578 standalone Java programs covering DSA, Java core, low-level design, HLD and Spring,
organised so that reading a folder top to bottom **is** the study order.

## How to navigate

Start with the README in any top-level folder. Each one lists every file in practice order
with its problem, its difficulty and the one insight to remember, marks the must-know subset,
and names the canonical problems still missing from that topic.

| Folder | Files | Index |
|---|---|---|
| DSA | 490 | [AAScratches/01-DSA](AAScratches/01-DSA/README.md) |
| Java Core | 45 | [AAScratches/02-Java-Core](AAScratches/02-Java-Core/README.md) |
| Low-Level Design | 31 | [AAScratches/03-LLD](AAScratches/03-LLD/README.md) |
| HLD and System Design | 4 | [AAScratches/04-HLD-System-Design](AAScratches/04-HLD-System-Design/README.md) |
| Spring and Microservices | 8 | [AAScratches/05-Spring-Microservices](AAScratches/05-Spring-Microservices/README.md) |

Written revision material lives in `06-SQL`, `07-Interview-QA-Memory` and `08-Reference`.

## What a file looks like

Every file opens with the same block comment, so you can tell in ten seconds whether you need
to read the code: **PROBLEM**, **EXAMPLE**, **APPROACH**, **KEY INSIGHT**, **COMPLEXITY**,
**INTERVIEW FOLLOW-UPS**, **RUN**. The title line carries the LeetCode number, the difficulty
and `MUST-KNOW` where it applies (177 files).

`main()` runs two to six cases, always including an edge case, and prints the actual result
next to the expected one:

```
case 1 typical  : [24, 12, 8, 6]   expected [24, 12, 8, 6]
case 2 one zero : [0, 0, 9, 0, 0]  expected [0, 0, 9, 0, 0]
case 3 two elems: [3, 2]           expected [3, 2]
```

A file is healthy when both sides of every `expected` read the same. No test framework needed.

## Filename scheme

`<TIER><NN>_<Name>.java`, so alphabetical order is practice order.

| Tier | Meaning |
|---|---|
| **A** | Building block: the technique itself, or a primitive later files assume |
| **B** | Easy: standard warm-up |
| **C** | Medium: the bulk of real interview questions |
| **D** | Hard: needs a non-obvious insight |

Within a tier, `NN` runs in dependency order. **Tier is difficulty, not importance**: Kadane's
algorithm is foundational but the problem is medium, so it sits in `C` and is flagged
must-know.

## Running code

```bash
tools/runjava AAScratches/01-DSA/01-Arrays/A01_ArraySortedOrNot.java
tools/runjava A01_ArraySortedOrNot.java   # resolves by name, no path needed
tools/runjava --find TwoSum               # locate files by name fragment
tools/runjava --list-mains <file>         # which classes declare main()
```

It compiles to a temp directory and runs whichever class actually declares `main()`, which is
why the filename never has to match the class name.

**Requires JDK 21 or newer.** All 566 standalone files compile on JDK 21, except the two
Tricky MCQ files that are meant not to compile.

### Why not `java Foo.java`

The JDK single-file launcher looks for a class named after the **file**. Files here are
`<TIER><NN>_<Name>.java` while the class inside keeps its problem name, so the launcher
reports `can't find class` on almost all of them. `runjava` finds the real class instead.

### Why this cannot be an IDE project

**Do not import this as an IntelliJ, Maven or Gradle project.** That is structural, not a
misconfiguration:

- 566 of 578 files declare **no package**, so they would all land in the default package.
- Folder names like `01-Arrays` are **not valid Java identifiers**, so they cannot be packages.
- **20 class names are declared in more than one file**: `TreeNode` in 45, `ListNode` and
  `Node` in 17 each, `Solution` in 14. Many clashes are inside the *same* folder.

A project compiles a source root as one unit, so marking any folder as a source root produces
dozens of duplicate-class errors. These were always IntelliJ **scratch files**, which the IDE
compiles in isolation. To get the green run gutter back, copy the folders into
`%APPDATA%\JetBrains\<IDE>\scratches`.

## Local LLM

A model runs on this laptop for reviewing practice solutions at zero cloud cost. Setup, the
measured tuning findings, and an honest account of what it is and is not good at:
**[LOCAL-LLM.md](LOCAL-LLM.md)**.

## Verification

Every file is compiled and executed on each change.

| Result | Count |
|---|---|
| Compiles and runs clean | 564 |
| Printed values matching their stated expectation | 3,207 of 3,207 |
| Non-compiling **on purpose** (the compile error is the lesson) | 2 |
| Header only, needs external jars (Spring, Guava) | 12 |

The 12 header-only files are the Spring orders service and the WorkFlowExecutor package. They
carry a shortened header explaining their role in the project instead of a runnable example,
because a standalone `javac` has no classpath for Spring, Lombok, Jackson, JUnit or Guava.
