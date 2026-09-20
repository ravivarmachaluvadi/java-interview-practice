# Low-Level Design

The GoF patterns, classic LLD interview problems, and data structures built from scratch. Each pattern file maps every class to its pattern role in the header.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [Data-Structure-Implementations](Data-Structure-Implementations/) | 2 | 2 | HashMap and a dynamic array deque built from primitives. |
| [Design-Patterns/1. Creational Design Patterns](Design-Patterns/1.%20Creational%20Design%20Patterns/) | 5 | 3 | Factory, Builder, Prototype, Abstract Factory, Singleton. |
| [Design-Patterns/2. Structural Patterns](Design-Patterns/2.%20Structural%20Patterns/) | 7 | 3 | Adapter, Facade, Decorator, Proxy, Composite, Flyweight, Bridge. |
| [Design-Patterns/3. Behavioral Patterns](Design-Patterns/3.%20Behavioral%20Patterns/) | 11 | 5 | Strategy, Template, Observer, Iterator, Command, Memento, State, Chain of Responsibility, Mediator, Visitor, Interpreter. |
| [Problems](Problems/) | 2 | 1 | Classic LLD interview problems: Tic-Tac-Toe, parking lot. |
| [WorkFlowExecutor](WorkFlowExecutor/) | 4 | 0 | A small workflow executor with a bounded queue (needs Guava; read, do not run). |
| **Total** | **31** | **14** | |

## Data-Structure-Implementations

HashMap and a dynamic array deque built from primitives.

**Do these first:** [A01_DynamicArrayDeque.java](Data-Structure-Implementations/A01_DynamicArrayDeque.java), [A02_HashMapImplementation.java](Data-Structure-Implementations/A02_HashMapImplementation.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_DynamicArrayDeque.java](Data-Structure-Implementations/A01_DynamicArrayDeque.java) * | Dynamic Array Deque (circular buffer) | LLD build / Medium | 1. |
| [A02_HashMapImplementation.java](Data-Structure-Implementations/A02_HashMapImplementation.java) * | HashMap from scratch (separate chaining) | LLD build / Medium | 1. |

**Worth adding next:**

- BoundedBlockingQueue<E> — implement java.util.concurrent.BlockingQueue from scratch (put, take, offer with timeout, drainTo): Circular buffer or linked nodes behind a ReentrantLock with TWO Conditions (notFull, notEmpty); await in a while loop, signal the opposite condition, awaitNanos for the timed offer/poll, restore the interrupt flag on InterruptedException. Then the comparison version: one intrinsic lock + wait/notifyAll, and why that wakes the wrong side.
- Make MyHashMap thread-safe and resizable — a ConcurrentHashMap-style map with lock striping, plus the correct resize/rehash story: Start from the existing MyHashMap: add remove(key), size(), and resize when load factor > 0.75 (rehash every entry into the new bucket array, and explain why you cannot reuse the old index). Then add concurrency in three stages — one global synchronized lock, then N stripes with the stripe chosen by the same hash, then the read-mostly version (volatile table reference, per-bucket synchronized on write, lock-free get). Discuss why size() across stripes is inherently approximate.
- In-memory cache with TTL expiry and a pluggable eviction policy (Guava/Caffeine-style): Map of key to entry carrying an expiry timestamp. Lazy expiry on read (check-then-evict inside get), plus active cleanup via either a min-heap/DelayQueue ordered by expiry time or a single scheduled sweeper thread. Layer a size-bounded eviction policy behind a Strategy interface (LRU / LFU / FIFO). Cover the cache-stampede question: what happens when a hot key expires and 500 threads miss at once (per-key lock, or a Future placeholder installed with computeIfAbsent).

## Design-Patterns/1. Creational Design Patterns

Factory, Builder, Prototype, Abstract Factory, Singleton.

**Do these first:** [A01_FactoryDesignPattern.java](Design-Patterns/1.%20Creational%20Design%20Patterns/A01_FactoryDesignPattern.java), [B01_Builder.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B01_Builder.java), [D01_Singleton.java](Design-Patterns/1.%20Creational%20Design%20Patterns/D01_Singleton.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_FactoryDesignPattern.java](Design-Patterns/1.%20Creational%20Design%20Patterns/A01_FactoryDesignPattern.java) * | Factory (Simple Factory / Factory Method) | Creational / Easy | The win is not "fewer new keywords", it is the direction of the dependency: the client compiles against Shape only, so adding a Rectangle touches the factory and nothing else. |
| [B01_Builder.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B01_Builder.java) * | Builder Pattern (static nested builder) | Creational / Easy | The builder is the *only* mutable thing in the design, and it is short-lived. |
| [B02_Prototype.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B02_Prototype.java) | Prototype Pattern (clone instead of construct) | Creational / Easy | super.clone() copies the object's fields bit for bit. |
| [C01_AbstractFactoryDatabase.java](Design-Patterns/1.%20Creational%20Design%20Patterns/C01_AbstractFactoryDatabase.java) | Abstract Factory - database driver families | Creational / Medium | Abstract Factory is Factory applied to a family. |
| [D01_Singleton.java](Design-Patterns/1.%20Creational%20Design%20Patterns/D01_Singleton.java) * | Singleton - sealing every back door | Creational / Hard | `instance = new Singleton()` is three steps: allocate, run the constructor, assign the reference. |

**Worth adding next:**

- Factory Method proper (Creator hierarchy) and an OCP-safe registry factory: Abstract Creator with an abstract factoryMethod() that subclasses override, where the Creator also holds the business logic that consumes the product; then refactor the if/else selector into a Map<String, Supplier<Product>> registry so new types register themselves instead of editing a switch.
- Deep copy versus shallow copy on an object with mutable nested state: An object holding a mutable field (a nested Address object and a List<String>), showing that super.clone() shares those references and mutating the copy corrupts the original; then three fixes — manual deep clone of each field, a copy constructor, and a serialization round-trip — with the trade-offs of each.
- Builder with enforced required fields, invariant validation, and immutable collections: Required fields taken in the Builder constructor rather than as with-setters; all cross-field invariants checked inside build() before the object is constructed so an invalid object can never exist; defensive copy of collection fields into List.copyOf on build; and a toBuilder() for copy-and-modify.

## Design-Patterns/2. Structural Patterns

Adapter, Facade, Decorator, Proxy, Composite, Flyweight, Bridge.

**Do these first:** [A01_AdapterPattern.java](Design-Patterns/2.%20Structural%20Patterns/A01_AdapterPattern.java), [C01_DecoratorDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C01_DecoratorDesignPattern.java), [C02_ProxyDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C02_ProxyDesignPattern.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_AdapterPattern.java](Design-Patterns/2.%20Structural%20Patterns/A01_AdapterPattern.java) * | Adapter Pattern (Wrapper) | Design Pattern / Easy | Object Adapter = "implement what the caller wants, hold what you have, translate in between". |
| [B01_FacadePattern.java](Design-Patterns/2.%20Structural%20Patterns/B01_FacadePattern.java) | Facade Pattern | Design Pattern / Easy | Facade is the degenerate case of delegation: it aggregates several collaborators, and it does NOT share an interface with any of them. |
| [C01_DecoratorDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C01_DecoratorDesignPattern.java) * | Decorator Pattern | Design Pattern / Medium | The wrapper implements the same interface as the wrappee. |
| [C02_ProxyDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C02_ProxyDesignPattern.java) * | Proxy Pattern | Design Pattern / Medium | Structurally a proxy is identical to a decorator: same interface, holds one wrappee, delegates. |
| [C03_CompositePattern.java](Design-Patterns/2.%20Structural%20Patterns/C03_CompositePattern.java) | Composite Pattern | Design Pattern / Medium | A composite is the decorator chain generalised from a line to a tree: the node holds MANY children instead of one wrappee, and because a composite is itself a Component, bundles nest to any depth. |
| [C04_Flyweight.java](Design-Patterns/2.%20Structural%20Patterns/C04_Flyweight.java) | Flyweight Pattern - forest of trees | Structural / Medium | Split the object's state in two: what repeats (intrinsic -> share it) and what varies (extrinsic -> pass it in). |
| [D01_BridgePattern.java](Design-Patterns/2.%20Structural%20Patterns/D01_BridgePattern.java) | Bridge Pattern - payment channel x payment method | Structural / Hard | The moment you feel yourself naming a class OnlineUpiPayment, you have two dimensions and you want Bridge: make one of them a field, not a supertype. |

**Worth adding next:**

- Annotation-driven dynamic proxy: implement @Transactional / @Retry / @Audit using java.lang.reflect.Proxy + InvocationHandler, then explain the CGLIB subclass variant: JDK dynamic proxy vs CGLIB subclass proxy; InvocationHandler.invoke dispatch; proxy-creation factory; the self-invocation problem (an internal this.method() call bypasses the proxy); why final and private methods can't be advised; interface-based vs class-based proxying trade-offs
- Design an in-memory file system: mkdir, addFile, ls, du (recursive size), find by name/glob, and path resolution from a string like /a/b/c.txt: Composite with a mutation API; the GoF safety-vs-transparency decision (do add/remove live on Component so clients are uniform, or only on Directory so leaves can't be misused?); parent back-pointers; recursive accumulators vs an explicit stack; iterator over a tree; guarding against cycles when symlinks/hard links enter
- Build a java.io-style stream decorator stack (Buffered -> Gzip -> Counting -> Encrypting over a base stream), then produce an inventory of structural patterns in the JDK and Spring and defend each mapping: Decorator over a real abstract base class rather than a toy interface; wrapping semantics for close()/flush() propagation; JDK/Spring mapping — java.io streams and Collections.unmodifiableList (Decorator), Arrays.asList and InputStreamReader (Adapter), java.awt.Container and Spring Security filter chain (Composite), SLF4J and JdbcTemplate (Facade), Integer.valueOf cache and String pool (Flyweight), JDBC DriverManager/Driver and SLF4J-over-bindings (Bridge), Hibernate lazy-loading proxies (Proxy)

## Design-Patterns/3. Behavioral Patterns

Strategy, Template, Observer, Iterator, Command, Memento, State, Chain of Responsibility, Mediator, Visitor, Interpreter.

**Do these first:** [A01_StrategyDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A01_StrategyDesignPattern.java), [A02_TemplateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A02_TemplateDesignPattern.java), [A03_ObserverDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A03_ObserverDesignPattern.java), [B02_CommandDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B02_CommandDesignPattern.java), [C01_StateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C01_StateDesignPattern.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_StrategyDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A01_StrategyDesignPattern.java) * | Strategy Design Pattern | LLD / Easy | Behaviour becomes a field. |
| [A02_TemplateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A02_TemplateDesignPattern.java) * | Template Method Design Pattern | LLD / Easy | Inverted control: the base class calls down into the subclass ("don't call us, we'll call you"), which is why every framework you use is built this way. |
| [A03_ObserverDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A03_ObserverDesignPattern.java) * | Observer Design Pattern | LLD / Easy | The subject broadcasts to an interface it does not own instances of. |
| [B01_IteratorPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B01_IteratorPattern.java) | Iterator Design Pattern | LLD / Easy | Externalising the cursor is what lets several traversals run over the same collection at once - each createIterator() call returns a fresh, independent position. |
| [B02_CommandDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B02_CommandDesignPattern.java) * | Command Design Pattern | LLD / Easy | Command is Strategy with the receiver already bound in. |
| [B03_MementoDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B03_MementoDesignPattern.java) | Memento Pattern - text editor undo | Behavioral | The Caretaker holds the state but does not read it; the Originator reads it but does not hold it. |
| [C01_StateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C01_StateDesignPattern.java) * | State Pattern - e-commerce order lifecycle | Behavioral | The transition table lives INSIDE the states, not in the context. |
| [C02_ChainOfResponsibility.java](Design-Patterns/3.%20Behavioral%20Patterns/C02_ChainOfResponsibility.java) | Chain of Responsibility - ATM cash dispenser | Behavioral | This is the PARTIAL-handling flavour of the chain: a handler does part of the work and forwards the remainder, rather than handling all or nothing. |
| [C03_MediatorDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C03_MediatorDesignPattern.java) | Mediator Pattern - chat room | Behavioral | No colleague holds a reference to another colleague. |
| [D01_VisitorDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/D01_VisitorDesignPattern.java) | Visitor Pattern - bonus and appraisal over employees | Behavioral | accept(v) { v.visit(this); } looks pointless until you see why it is there. |
| [D02_InterpreterPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/D02_InterpreterPattern.java) | Interpreter Pattern - a tiny arithmetic evaluator | Behavioral / Hard | interpret() is recursive by construction - a non-terminal asks its children for their values and never inspects their type. |

**Worth adding next:**

- Undo/Redo in a text editor — Command objects that carry their own undo(), an undo stack plus a redo stack, and a MacroCommand that groups several edits into one undoable unit: Command with reversible operations + Composite Command, contrasted against Memento snapshots
- An event bus / notification hub that survives production — 10k subscribers, subscribe and unsubscribe while a notify is in flight, one slow or throwing listener, and listeners that outlive their owners: Observer hardened: CopyOnWriteArrayList or snapshot-then-iterate, per-listener try/catch, executor-backed async delivery, WeakReference registration, push vs pull payloads
- A real custom iterator — a lazy nested/flattening iterator over a list-of-lists or an in-order BST iterator, implementing java.util.Iterator<T> and java.lang.Iterable<T> so it works in a for-each, with fail-fast modCount and a working remove(): Iterator implementing the JDK contract, external vs internal iteration, lazy advancement with O(h) space

## Problems

Classic LLD interview problems: Tic-Tac-Toe, parking lot.

**Do these first:** [C02_ParkingLot.java](Problems/C02_ParkingLot.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [C01_TicTacToe.java](Problems/C01_TicTacToe.java) | Design Tic-Tac-Toe (n x n board) | LeetCode 348 / Medium | 1. |
| [C02_ParkingLot.java](Problems/C02_ParkingLot.java) * | Design a Parking Lot | LLD / Medium | 1. |

**Worth adding next:**

- Elevator / Lift Control System (multi-car): Split fleet-level ElevatorController from per-car Elevator; each car holds a direction + state machine (IDLE/MOVING_UP/MOVING_DOWN/DOORS_OPEN) and a sorted set of stops; dispatch policy behind an interface (nearest-car, then SCAN/LOOK) so the interviewer can swap it.
- Movie Ticket Booking / Seat Reservation (BookMyShow style): Two-phase reservation: a temporary hold with a TTL plus an expiry sweep, then an idempotent confirm; lock granularity chosen per show rather than globally; booking state machine (HELD -> CONFIRMED -> CANCELLED / EXPIRED) with payment as a separate concern.
- Splitwise / Expense Sharing: Split strategy as a first-class validated object (EQUAL / EXACT / PERCENTAGE, shares must sum to the total), a balance ledger keyed by an ordered user pair with a signed amount, and a settlement pass that simplifies debts down to a minimal transfer set.

## WorkFlowExecutor

A small workflow executor with a bounded queue (needs Guava; read, do not run).

| File | Problem | Level | Key insight |
|---|---|---|---|
| [GeneralUtil.java](WorkFlowExecutor/GeneralUtil.java) | GeneralUtil - random key generator | Spring project helper / header-only |  |
| [LimitQueue.java](WorkFlowExecutor/LimitQueue.java) | LimitQueue - blocking work queue | Packaged helper / header-only |  |
| [Procedure.java](WorkFlowExecutor/Procedure.java) | Procedure - unit of work contract | Packaged helper / header-only |  |
| [WorkFlowExecutor.java](WorkFlowExecutor/WorkFlowExecutor.java) | WorkFlowExecutor - key-affinity ordered execution | Spring @Component / header-only |  |

`*` = must-know. Run any file with `tools/runjava <file>` from the repo root, or open it as an IntelliJ scratch.
