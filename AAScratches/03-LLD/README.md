# Low-Level Design

The GoF patterns, classic LLD interview problems, and data structures built from scratch. Each pattern file maps every class to its pattern role in the header.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [Data-Structure-Implementations](Data-Structure-Implementations/) | 2 | 2 | HashMap and a dynamic array deque built from primitives. |
| [Design-Patterns/1. Creational Design Patterns](Design-Patterns/1.%20Creational%20Design%20Patterns/) | 5 | 3 | Factory (payment processor per checkout request), Builder (product search), Prototype (product variants), Abstract Factory (regional checkout kit), Singleton. |
| [Design-Patterns/2. Structural Patterns](Design-Patterns/2.%20Structural%20Patterns/) | 7 | 3 | Adapter (courier APIs), Facade (place order), Decorator (retry/fallback on an API call), Proxy (cached product catalog), Composite (product bundles), Flyweight (game forest), Bridge (notification type x channel). |
| [Design-Patterns/3. Behavioral Patterns](Design-Patterns/3.%20Behavioral%20Patterns/) | 11 | 5 | Strategy (promotions), Template (order fulfilment), Observer (back-in-stock alerts), Iterator (paged API), Command (cart undo/redo), Memento (editor undo), State (order lifecycle), Chain (ATM), Mediator (chat room), Visitor (cart tax/shipping), Interpreter (coupon rules). |
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

Factory (payment processor per checkout request), Builder (product search), Prototype (product variants), Abstract Factory (regional checkout kit), Singleton.

**Do these first:** [A01_FactoryDesignPattern.java](Design-Patterns/1.%20Creational%20Design%20Patterns/A01_FactoryDesignPattern.java), [B01_Builder.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B01_Builder.java), [D01_Singleton.java](Design-Patterns/1.%20Creational%20Design%20Patterns/D01_Singleton.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_FactoryDesignPattern.java](Design-Patterns/1.%20Creational%20Design%20Patterns/A01_FactoryDesignPattern.java) * | Factory - payment processor per checkout request | Creational / Easy | The factory owns the "code -> class" decision, and a registry of Suppliers replaces the if/else chain: a new method is one register() call. |
| [B01_Builder.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B01_Builder.java) * | Builder - product search request | Creational / Easy | Required values go in the builder's constructor, optional ones are named methods, and build() is the single gate that validates. |
| [B02_Prototype.java](Design-Patterns/1.%20Creational%20Design%20Patterns/B02_Prototype.java) | Prototype - product variants from a base listing | Creational / Easy | The copy must be DEEP for every mutable field. |
| [C01_AbstractFactoryPattern.java](Design-Patterns/1.%20Creational%20Design%20Patterns/C01_AbstractFactoryPattern.java) | Abstract Factory - regional checkout kit | Creational / Medium | One factory makes the whole family, so choosing the region once (from config, at startup) guarantees every part matches. |
| [D01_Singleton.java](Design-Patterns/1.%20Creational%20Design%20Patterns/D01_Singleton.java) * | Singleton - sealing every back door | Creational / Hard | `instance = new Singleton()` is allocate, construct, assign. |

**Worth adding next:**

- Factory Method proper (Creator hierarchy): Abstract Creator with an abstract factoryMethod() that subclasses override, where the Creator also holds the business logic that consumes the product.
- Object Pool — a bounded connection pool with borrow and return: Fixed-size pool over a BlockingQueue, acquire() with a timeout, release() that validates and resets the object before returning it, handling of the exhausted case, and a policy for objects that are never returned.

## Design-Patterns/2. Structural Patterns

Adapter (courier APIs), Facade (place order), Decorator (retry/fallback on an API call), Proxy (cached product catalog), Composite (product bundles), Flyweight (game forest), Bridge (notification type x channel).

**Do these first:** [A01_AdapterPattern.java](Design-Patterns/2.%20Structural%20Patterns/A01_AdapterPattern.java), [C01_DecoratorDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C01_DecoratorDesignPattern.java), [C02_ProxyDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C02_ProxyDesignPattern.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_AdapterPattern.java](Design-Patterns/2.%20Structural%20Patterns/A01_AdapterPattern.java) * | Adapter - courier partner APIs | Structural / Easy | An adapter implements the interface we want, holds the SDK we have, and translates in between: units, currency, parameter types, error style. |
| [B01_FacadePattern.java](Design-Patterns/2.%20Structural%20Patterns/B01_FacadePattern.java) | Facade - one placeOrder() over four services | Structural / Easy | The facade owns the workflow: one call in, many subsystem calls out. |
| [C01_DecoratorDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C01_DecoratorDesignPattern.java) * | Decorator - retry, fallback, logging on an API call | Structural / Medium | Each wrapper implements the SAME interface it wraps, so wrappers stack: new Fallback(new Retry(new Logging(remote))). |
| [C02_ProxyDesignPattern.java](Design-Patterns/2.%20Structural%20Patterns/C02_ProxyDesignPattern.java) * | Proxy - cached, admin-only product catalog | Structural / Medium | The proxy implements the same interface as the real catalog and stands in front of it, deciding whether and when the real one is touched: serve reads from a cache, refuse non-admin writes, evict on write. |
| [C03_CompositePattern.java](Design-Patterns/2.%20Structural%20Patterns/C03_CompositePattern.java) | Composite - product bundles inside an order | Structural / Medium | Product (leaf) and ProductBundle (composite) implement the same OrderItem, so a bundle can hold bundles. |
| [C04_Flyweight.java](Design-Patterns/2.%20Structural%20Patterns/C04_Flyweight.java) | Flyweight - forest of trees in a game map | Structural / Medium | What repeats (species, colour, texture) is stored once in a shared, immutable TreeType; what varies (x, y) stays in a tiny Tree. |
| [D01_BridgePattern.java](Design-Patterns/2.%20Structural%20Patterns/D01_BridgePattern.java) | Bridge - notification type x delivery channel | Structural / Hard | Two independent dimensions -> two hierarchies joined by a field. |

**Worth adding next:**

- Annotation-driven dynamic proxy: implement @Transactional / @Retry / @Audit using java.lang.reflect.Proxy + InvocationHandler, then explain the CGLIB subclass variant: JDK dynamic proxy vs CGLIB subclass proxy; InvocationHandler.invoke dispatch; proxy-creation factory; the self-invocation problem (an internal this.method() call bypasses the proxy); why final and private methods can't be advised; interface-based vs class-based proxying trade-offs
- Design an in-memory file system: mkdir, addFile, ls, du (recursive size), find by name/glob, and path resolution from a string like /a/b/c.txt: Composite with a mutation API; the GoF safety-vs-transparency decision (do add/remove live on Component so clients are uniform, or only on Directory so leaves can't be misused?); parent back-pointers; recursive accumulators vs an explicit stack; iterator over a tree; guarding against cycles when symlinks/hard links enter
- Build a java.io-style stream decorator stack (Buffered -> Gzip -> Counting -> Encrypting over a base stream), then produce an inventory of structural patterns in the JDK and Spring and defend each mapping: Decorator over a real abstract base class rather than a toy interface; wrapping semantics for close()/flush() propagation; JDK/Spring mapping — java.io streams and Collections.unmodifiableList (Decorator), Arrays.asList and InputStreamReader (Adapter), java.awt.Container and Spring Security filter chain (Composite), SLF4J and JdbcTemplate (Facade), Integer.valueOf cache and String pool (Flyweight), JDBC DriverManager/Driver and SLF4J-over-bindings (Bridge), Hibernate lazy-loading proxies (Proxy)

## Design-Patterns/3. Behavioral Patterns

Strategy (promotions), Template (order fulfilment), Observer (back-in-stock alerts), Iterator (paged API), Command (cart undo/redo), Memento (editor undo), State (order lifecycle), Chain (ATM), Mediator (chat room), Visitor (cart tax/shipping), Interpreter (coupon rules).

**Do these first:** [A01_StrategyDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A01_StrategyDesignPattern.java), [A02_TemplateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A02_TemplateDesignPattern.java), [A03_ObserverDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A03_ObserverDesignPattern.java), [B02_CommandDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B02_CommandDesignPattern.java), [C01_StateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C01_StateDesignPattern.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_StrategyDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A01_StrategyDesignPattern.java) * | Strategy - checkout promotions | Behavioral / Easy | The promotion is a field holding an object, not a branch. |
| [A02_TemplateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A02_TemplateDesignPattern.java) * | Template Method - order fulfilment | Behavioral / Easy | The base class owns the order of steps in one final method; subclasses fill in only the steps that differ. |
| [A03_ObserverDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/A03_ObserverDesignPattern.java) * | Observer - back-in-stock alerts | Behavioral / Easy | The subject keeps a list of listeners behind one interface and calls them on the event it owns - here the 0 -> positive transition, not every restock. |
| [B01_IteratorPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B01_IteratorPattern.java) | Iterator - paged orders API export | Behavioral / Easy | The iterator hides HOW elements are fetched. |
| [B02_CommandDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B02_CommandDesignPattern.java) * | Command - shopping cart undo / redo | Behavioral / Easy | Each action becomes an object that knows how to execute() AND undo() itself, remembering whatever it needs to reverse (the removed price, the old coupon). |
| [B03_MementoDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/B03_MementoDesignPattern.java) | Memento - text editor undo | Behavioral / Easy | The editor (Originator) makes a sealed snapshot of itself; the history (Caretaker) stores snapshots but never reads them. |
| [C01_StateDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C01_StateDesignPattern.java) * | State - e-commerce order lifecycle | Behavioral / Medium | Each state is a class that knows its own rules AND names its successor via order.setState(). |
| [C02_ChainOfResponsibility.java](Design-Patterns/3.%20Behavioral%20Patterns/C02_ChainOfResponsibility.java) | Chain of Responsibility - ATM cash dispenser | Behavioral / Medium | Each handler pays as many of its own notes as fit and forwards the remainder to the next (2000 -> 500 -> 200 -> 100) - partial handling, not all-or-nothing. |
| [C03_MediatorDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/C03_MediatorDesignPattern.java) | Mediator - chat room | Behavioral / Medium | No user references another user. |
| [D01_VisitorDesignPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/D01_VisitorDesignPattern.java) | Visitor - tax, shipping, points over cart lines | Behavioral / Hard | Each calculation becomes one visitor class with a visit() per line type. |
| [D02_InterpreterPattern.java](Design-Patterns/3.%20Behavioral%20Patterns/D02_InterpreterPattern.java) | Interpreter - coupon eligibility rules | Behavioral / Hard | Turn the rule text into a tree of small objects, one class per grammar element: Compare is a leaf, And / Or hold two sub-rules. |

**Worth adding next:**

- MacroCommand: group several commands into one undoable unit: A composite command whose execute() runs its children in order and whose undo() reverses them in reverse order; contrast with Memento snapshots.
- An event bus / notification hub that survives production — 10k subscribers, subscribe and unsubscribe while a notify is in flight, one slow or throwing listener, and listeners that outlive their owners: Executor-backed async delivery, WeakReference registration, push vs pull payloads (A03_ObserverDesignPattern already covers CopyOnWriteArrayList and per-listener try/catch).
- Fail-fast iterator with a working remove() over your own collection: modCount checked on every next(), ConcurrentModificationException on outside changes, remove() allowed once per next(); an in-order BST iterator in O(h) space.

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
