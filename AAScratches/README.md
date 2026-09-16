# AAScratches: interview practice workspace

Everything practised for interviews, grouped by subject. Code files are standalone Java scratch programs (no build system, no packages); notes are Markdown converted from the original text files.

## Map

| Folder | What is inside | Start here |
|--------|----------------|------------|
| [01-DSA](01-DSA/) | 540+ solved problems in 20 technique folders, plus DSA notes and roadmaps | [01-DSA/README.md](01-DSA/README.md) |
| [02-Java-Core](02-Java-Core/) | Concurrency, language features, IO, crypto demos, tricky MCQs, Java Q&A notes | [02-Java-Core/README.md](02-Java-Core/README.md) |
| [03-LLD](03-LLD/) | 23 design patterns, parking lot, Tic-Tac-Toe, hand-built data structures, workflow executor | [03-LLD/README.md](03-LLD/README.md) |
| [04-HLD-System-Design](04-HLD-System-Design/) | System design keypoints, payment system, Saga Q&A, building-block demos | [04-HLD-System-Design/README.md](04-HLD-System-Design/README.md) |
| [05-Spring-Microservices](05-Spring-Microservices/) | Spring Boot, Security, transactions, Hibernate, microservices, Kafka notes; a sample Spring Boot project | [05-Spring-Microservices/README.md](05-Spring-Microservices/README.md) |
| [06-SQL](06-SQL/) | SQL interview Q&A and a query scratch file | [SQL_QA.md](06-SQL/SQL_QA.md) |
| [07-Interview-QA-Memory](07-Interview-QA-Memory/) | Three mixed-topic "memory based" Q&A files from real interview rounds | [Part 1](07-Interview-QA-Memory/Interview_Memory_QA_Part1.md) |
| [08-Reference](08-Reference/) | PDFs, git workflow, maths symbols, IntelliJ plugin list, work techniques, screenshots, CTCI tracker | [Git_Commands.md](08-Reference/Git_Commands.md) |
| `_archive/` | The original `.txt` files exactly as they were, plus `reorg-manifest.txt` (every old path to new path) | delete once you are happy with the Markdown versions |
| `_personal/` | Password file and a personal-details form. Not converted, not indexed. Keep out of any git repo | |

## Conventions

- **Topic folders are numbered** (`01-Arrays` ... `20-Scenario-Based-Problems`) so they sort in learning order.
- **`Important*` file names** are the owner's own "must remember" flag. They were kept as-is and are starred in the DSA index.
- **Same problem, several files** (for example `TrappingRainWater` and `ImportantTrappingRainWater`) were kept side by side. The DSA index lists these groups so they can be pruned later.
- **Notes are Markdown** with a table of contents, numbered questions, fenced code and narrow tables, so they render on GitHub and in any Markdown viewer.
- Two files were renamed to avoid clashes: `Triangle.java` (root) became `12-Dynamic-Programming/TriangleV2.java`, and `LongestCommonSubsequence copy.java` became `LongestCommonSubsequenceTabulation.java`.

## Running a file

Each Java file is self-contained. From the file's folder:

```bash
java FileName.java
```

The Spring Boot project under `05-Spring-Microservices/orders-springboot-project/` is a normal Maven project (`./mvnw spring-boot:run`).

## Suggested next clean-ups (not done)

- Decide which of the duplicate solutions to keep (see "Same problem, several files" in the DSA index).
- Move `_personal/` out of this folder before pushing anything to git.
- Delete `_archive/original-txt/` after spot-checking the Markdown versions.
