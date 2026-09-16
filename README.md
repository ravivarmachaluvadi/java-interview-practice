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

## Running anything

**Requires JDK 25.** Several files use `java.lang.IO.println` and instance `main`
methods, which do not compile on 21.

```bash
javac -d /tmp/out path/to/A01_Something.java
java -cp /tmp/out Something          # class name, not filename
```

Most files carry a `main()` with a worked example, so they run standalone and print
labelled input and output.

**The class name is not the filename.** Renaming into tiers deliberately left class
names untouched so no cross-file reference could break — compile the file, then run
the class.

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
