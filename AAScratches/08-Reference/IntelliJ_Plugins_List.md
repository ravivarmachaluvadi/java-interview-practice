# IntelliJ Plugins List

The IntelliJ IDEA plugins the owner had installed, with the version of each.

This is a **point-in-time snapshot**, not a shopping list. The version numbers
are the ones that were installed when the list was taken; the build suffixes
(`-231`, `222.`, `2023.x`) pin several of them to the IntelliJ 2022.2 / 2023.1 /
2023.2 line, so a current IDE will offer newer builds or, in a few cases,
nothing at all.

## Where this came from

Converted from `AAScratches/_archive/original-txt/ThanksAndRegards.txt` -- the
original filename does not describe the contents. That folder was deleted in
commit `9474f16`; recover the original with:

```bash
git show 9474f16^:AAScratches/_archive/original-txt/ThanksAndRegards.txt
```

## Installed plugins

| Plugin | Version |
| --- | --- |
| .ignore | 4.5.1 |
| Atom Material Icons | 90.0.0 |
| CamelCase | 3.0.12 |
| Codota AI Autocomplete for Java and JavaScript | 4.2.10 |
| Convert YAML and Properties File | 1.0.5 |
| CSV Editor | 3.2.0-231 |
| Database Navigator | 3.3.9310.0 |
| Diffblue Cover - Create complete JUnit tests with AI | 2023.06.02-2023.1 |
| Doc-Aware Search Everywhere | 1.0.12 |
| DummyMapper (Json,Avro,GraphQL) | 2.1.1 |
| Grep Console | 12.18.211.6693.0 |
| HighlightBracketPair | 1.4.0 |
| JavaDoc | 4.0.2 |
| JPA Buddy | 2023.2.4-231 |
| Json Parser | 1.4.2 |
| Jump to Line | 0.1.13 |
| Key Promoter X | 2023.1.0 |
| POJO to JSON | 1.2.6 |
| Presentation Assistant | 1.0.10 |
| Rainbow Brackets | 2023.2.11 |
| RegexpTester | 1.0.8 |
| RoboPOJOGenerator | 2.3.3 |
| Sequence Diagram | 3.0.5 |
| Snyk Security - Code, Open Source, Container, IaC Configurations | 2.4.62 |
| Spot Profiler for Java and Kotlin | 2.0 |
| Spring Boot Assistant | 222.17.2 |
| String Manipulation | 9.9.0 |
| Tabnine AI Code Completion- JS Java Python TS Rust Go PHP & More | 1.0.17 |
| Test Data | 231.8109.91 |
| VisualVM Launcher | 1.21.211.6085.0 |

## Notes before reinstalling

| Plugin | Why it needs a second look |
| --- | --- |
| Codota AI Autocomplete **and** Tabnine | Codota renamed itself Tabnine; these two entries are the same product line and installing both is redundant. Pick Tabnine |
| Rainbow Brackets **and** HighlightBracketPair | Overlapping jobs -- both colour or highlight matching brackets |
| Everything pinned to `-231` / `222.` / `2023.x` | Built against IntelliJ 2022.2-2023.2. Let the Marketplace resolve the version for your IDE rather than pasting these numbers |

These notes are about what the list itself shows -- duplicated purpose and pinned
builds. Whether each plugin is still published, still maintained, or still free
has not been checked.
