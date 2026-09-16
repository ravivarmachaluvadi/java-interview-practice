# 1. Creational Design Patterns — must-know order

**Techniques in this topic:** Hide `new` behind an interface: the client names what it wants, never the concrete class, Factory Method raised one level into Abstract Factory, so a whole family of products switches together, Telescoping constructors replaced by a static nested Builder: fluent chaining, final fields, object immutable once built, Copy semantics in Java: Cloneable, super.clone(), shallow vs deep copy, and reference inequality after cloning, One-instance guarantees under concurrency: lazy init, double-checked locking, and why volatile is load-bearing under the JMM, Sealing the back doors that break a singleton: reflection, deserialization (readResolve), clone(), subclassing — and the enum alternative

| | |
|---|---|
| Problems | 6 |
| Must-know | 3 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_FactoryDesignPattern.java` | Factory method behind an interface |
| 2 | `B01_BuilderExample.java` | Static nested builder, fluent chaining |
| 3 | `D01_Singleton.java` | Double-checked locking, sealed back doors |

## Full practice order


### A — Building blocks

- `A01_FactoryDesignPattern.java` — Factory method behind an interface **[must-know]**
  - The root idea of the whole folder: the client asks ShapeFactory for a Shape and never calls `new`; every later file assumes this decoupling.
- `A02_CloneExample.java` — Cloneable and super.clone() mechanics
  - The copy primitive itself — implement Cloneable, delegate to Object.clone(), prove original != clone; Prototype is unreadable without owning this first.

### B — Easy

- `B01_BuilderExample.java` — Static nested builder, fluent chaining **[must-know]**
  - Mechanically easy but appears in nearly every LLD round, and drills the private-constructor plus static-nested-class idiom that Singleton reuses.
- `B02_PrototypeExample.java` — Clone-based creation via a Prototype interface
  - Applies the A2 primitive as a named pattern; easy here only because every field is an immutable String, which is exactly where the deep-copy follow-up begins.

### C — Medium

- `C01_AbstractFactoryDatabaseExample.java` — Families of related products
  - Medium because it is Factory applied twice — one factory returning a matched Connection plus QueryExecutor pair — and it only lands once A1 is solid.

### D — Hard

- `D01_Singleton.java` — Double-checked locking, sealed back doors **[must-know]**
  - Hard for reasons invisible in the code: why volatile (JMM reordering of allocate/construct/assign) and the three bypass routes — reflection, deserialization, clone() — which is why enum is the clean answer.

## Interview readiness

All five GoF creational patterns are physically present, which puts this folder ahead of most candidates' notes — but the files are 48-147 lines of happy-path demo, and at senior/staff level the interview is never about the happy path. Three of the six files would break under the first follow-up. FactoryDesignPattern.java is Simple Factory mislabelled as Factory: a single concrete ShapeFactory with an if/else chain that returns null on a miss, so there is no Creator hierarchy and no answer to "add a new Shape without editing the factory." CloneExample and PrototypeExample both contain only String fields, which means super.clone() is safe purely by accident and the shallow-versus-deep distinction — the entire reason Prototype gets asked — is never demonstrated; put one List or one mutable Address inside and the code is wrong. BuilderExample has no validation in build() and carries a comment saying "optional: create Builder constructor with mandatory attributes" that was never done, so "what happens if firstName is null" and "how do you build a subclass with a builder" both land on nothing. Singleton.java is genuinely strong — double-checked locking with volatile, the holder idiom, and reflection/clone/readResolve guards all present and correctly annotated — and is the one file that would survive a staff-level grilling, though no enum singleton actually appears in code despite being listed as a covered theme. The net read: he can name and draw every creational pattern, which passes a screen, and he can defend exactly one of them, which is what fails an onsite. The highest-leverage fixes are cheap — they are deepenings of files that already exist rather than new territory — and Object Pool is the one genuinely absent canonical problem, notable because it is the creational pattern that doubles as a concurrency question.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Factory Method proper (Creator hierarchy) and an OCP-safe registry factory | Abstract Creator with an abstract factoryMethod() that subclasses override, where the Creator also holds the business logic that consumes the product; then refactor the if/else selector into a Map<String, Supplier<Product>> registry so new types register themselves instead of editing a switch. | What he has is Simple Factory, not Factory Method. ShapeFactory is one concrete class with an if/else chain that returns null for unknown input. The two questions that always follow are 'add a Hexagon without modifying ShapeFactory' and 'what is the difference between Factory Method and Abstract Factory' — the first needs the registry refactor and the second needs the Creator-subclass structure, and neither exists in the folder. The null return is also a standalone red flag an interviewer will pick at. |
| high | Deep copy versus shallow copy on an object with mutable nested state | An object holding a mutable field (a nested Address object and a List<String>), showing that super.clone() shares those references and mutating the copy corrupts the original; then three fixes — manual deep clone of each field, a copy constructor, and a serialization round-trip — with the trade-offs of each. | This is the most reliably asked question in the entire creational block, and both of his copy files dodge it: CloneExample and PrototypeExample contain only final String fields, so super.clone() happens to be correct and the bug never appears. He can currently state that shallow and deep copy differ but cannot demonstrate the failure or write the fix. The standard probe is 'you cloned it, now mutate the list on the copy and print the original' — his code cannot answer it because there is no list. |
| high | Builder with enforced required fields, invariant validation, and immutable collections | Required fields taken in the Builder constructor rather than as with-setters; all cross-field invariants checked inside build() before the object is constructed so an invalid object can never exist; defensive copy of collection fields into List.copyOf on build; and a toBuilder() for copy-and-modify. | His BuilderExample has zero validation and a leftover comment admitting mandatory-field handling was skipped. The first follow-up to any builder answer is 'what if the caller never sets firstName' and the second is 'where do you validate, and why there and not in the setters' — the answer is build(), because that is the single choke point where the object is complete. He also has no collection field anywhere, so the defensive-copy point that makes the immutability claim actually true is untested. He currently claims immutability that his code would not deliver with a List field. |
| high | Object Pool — a bounded connection pool with borrow and return | Fixed-size pool over a BlockingQueue, acquire() with a timeout, release() that validates and resets the object before returning it, handling of the exhausted case, and a policy for objects that are never returned. | This is the one canonical creational problem genuinely absent from the folder rather than merely shallow, and it is asked often at senior level precisely because it is creational plus concurrency in one question — it tests whether he understands why expensive object creation gets amortized, and simultaneously tests BlockingQueue, timeouts, and resource-leak reasoning. Given he has Spring and database work in his background, 'how does HikariCP work' or 'implement a connection pool' is a very likely ask, and there is nothing in this folder to build the answer from. |
| medium | Static factory methods and instance caching (Effective Java Item 1) | Named creators such as of(), valueOf(), getInstance() replacing public constructors; returning a cached or interned instance instead of a new one; returning a subtype or interface the caller does not name; and the explicit comparison against the Factory pattern. | There is a specific Java interview question — 'what is the difference between a static factory method and the Factory design pattern' — that catches people who only learned GoF, and nothing in this folder addresses it. The answer is that a static factory is a language-level replacement for a constructor on the same class, while Factory Method is a structural delegation to a subclass. The caching angle also unlocks the Integer.valueOf and Boolean.valueOf questions, which are standard Java-fundamentals follow-ups, and it connects cleanly to the Flyweight file he already has next door. |
| medium | Prototype Registry — a manager holding pre-configured prototypes | A Map<String, Prototype> of expensively-built, fully-configured template objects, where the client fetches by key and clones rather than constructing; used for document templates, game entities, or pre-loaded config objects. | His PrototypeExample clones one ad-hoc object, which shows the mechanism but not the motivation — it never explains why anyone would choose Prototype over just calling a constructor. The registry is the answer: when the configured state is expensive to assemble and you need many near-identical copies. Without it, the honest answer to 'when would you actually use Prototype in production' is thin, and that is the question that separates pattern recall from pattern judgement. |
| medium | Generic recursive builder for a class hierarchy (Effective Java Item 2) | abstract static class Builder<T extends Builder<T>> with an abstract self() returning T, so that a subclass builder can inherit the parent's with-methods and still chain fluently without casting. | The near-guaranteed follow-up to any builder answer is 'now I subclass User into AdminUser with an extra field — does your builder still chain?' With his current static nested Builder it does not, because withFirstName() returns the parent Builder type and the subclass methods drop off the chain. The self-type trick is the standard fix and it is exactly the kind of Java-specific depth a staff-level Java interviewer uses to separate candidates who read Effective Java from ones who memorised a diagram. |
| medium | Replacing a singleton with dependency injection for testability | Take a class that calls Singleton.getInstance() internally, show that it cannot be unit tested because the dependency cannot be substituted, then refactor to constructor injection with the singleton lifecycle owned by the container rather than the class; contrast a hand-rolled singleton with a Spring singleton-scoped bean (one instance per container, not per JVM). | He has the strongest singleton file in the folder but only covers how to build one, never when not to. The standard senior-level pivot after a good singleton answer is 'fine — now how do you test a class that depends on it', and the expected answer is that you do not, you inject it. The Spring contrast matters too: a singleton-scoped bean is per-ApplicationContext while a static getInstance() is per-classloader, and conflating the two is a common and visible mistake in Spring-heavy interviews. |

