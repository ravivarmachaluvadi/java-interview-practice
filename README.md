# Java interview practice

642 Java files covering DSA, Java core, low-level design, HLD and Spring — organised
so that reading a folder top to bottom *is* the study order.

## How to navigate

Start with the `MUST-KNOW.md` in any topic folder. It gives you three things:

| Section | What it is |
|---|---|
| **Must-know** | The subset you cannot walk into a senior interview without |
| **Full practice order** | Every problem in dependency order, with the technique it teaches |
| **Gaps** | Canonical problems *missing* from that folder, and why their absence costs you |

## Filename scheme

Every file is `<TIER><NN>_<Name>.java`, so alphabetical order is practice order.

| Tier | Meaning |
|---|---|
| **A** | Building block — the technique itself, or a primitive later problems assume |
| **B** | Easy — standard warm-up |
| **C** | Medium — the bulk of real interview questions |
| **D** | Hard — needs a non-obvious insight |

Within a tier, `NN` orders by dependency: earlier problems teach what later ones assume.

**Tier is difficulty, not importance.** Those are separate axes. Kadane's algorithm is
foundational but the problem is medium, so it sits in `C` and is flagged must-know.

## Layout

| Folder | Contents |
|---|---|
| `01-DSA` | 550 problems across 20 topics, arrays through scenario-based |
| `02-Java-Core` | Concurrency, IO, language features, security, tricky MCQs |
| `03-LLD` | Design patterns and low-level design problems |
| `04-HLD-System-Design` | System design code and notes |
| `05-Spring-Microservices` | A Spring Boot orders service, plus Kafka and security notes |
| `06-SQL`, `07-Interview-QA-Memory`, `08-Reference` | Written revision material |

## Running code

**Do NOT import this as an IntelliJ/Maven/Gradle project.** It will not work, and
that is structural rather than a misconfiguration:

- 628 of 643 files declare **no package**, so they all live in the default package.
- Folder names like `01-Arrays` are **not valid Java identifiers** (leading digit,
  hyphens), so they cannot be packages either.
- **20 class names are declared in multiple files** - `TreeNode` in 48, `Node` in 21,
  `Solution` in 18, `ListNode` in 17 - and many of those clashes are inside the *same*
  folder. A project compiles a source root as one unit, so marking even one folder as
  Sources Root produces dozens of duplicate-class errors.

These were always IntelliJ **scratch files**, which the IDE compiles in isolation.
That is the model to keep: 643 independent programs, not one project.

### Use the runner

```bash
tools/runjava AAScratches/01-DSA/01-Arrays/A01_ArraySortedOrNot.java
tools/runjava A02_TwoSum.java          # resolves by name, no path needed
tools/runjava --find TwoSum            # locate files
tools/runjava --list-mains <file>      # which classes have a main()
```

It compiles to a temp directory, then runs whichever class actually declares
`main()`. **588 of 643 files run this way.**

### Why not `java Foo.java`

The JDK single-file launcher looks for a class named after the **file**. Files here
are `<TIER><NN>_<Name>.java` while the class inside keeps its original name, so the
launcher reports `can't find class` on ~113 of them. `runjava` finds the real class
instead, which is worth +95 files.

### Doing it by hand

```bash
javac -d out -sourcepath <folder> <file>.java
java -cp out <ClassName>        # the CLASS name, not the filename
```

**Requires JDK 25.** Several files use `java.lang.IO.println` and `static void main`
without `public`, neither of which compiles on 21. If `JAVA_HOME` points at an older
JDK, `runjava` ignores it and picks the newest installed.

### If you want the IDE experience back

Copy the folders into IntelliJ's scratches directory
(`%APPDATA%\JetBrains\<IDE>\scratches`). Scratch files are compiled individually,
so the duplicate class names stop mattering and you get the green run gutter.

## Verification

Every file is compiled and executed with JDK 25 on each change.

| Result | Count |
|---|---|
| Compile clean | 621 / 642 |
| `main()` runs clean | 592 |
| Known failures | 21 |

The 21 are accounted for, not unexplained: 10 need external jars (lombok, gson, guava,
Spring) that a standalone `javac` has no classpath for, 2 are `Tricky-MCQ` files where
being uncompilable *is* the lesson, and the rest are genuine pre-existing bugs left
in place. A further 7 time out and 7 crash by design — concurrency demos and files
that read from `stdin`.
