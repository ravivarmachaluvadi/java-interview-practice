# 3. Behavioral Patterns — must-know order

**Techniques in this topic:** Delegation to an interface vs. inheritance hooks — the two axes of varying behavior (Strategy vs. Template Method), the single most-asked LLD trade-off question, Event notification and decoupled communication — one-to-many broadcast (Observer) generalizing into an N-to-N hub (Mediator), the backbone of pub/sub, listeners and chat/notification LLD rounds, Behavior as an object — requests (Command), states (State) and snapshots (Memento) reified into first-class classes so they can be queued, swapped, logged or undone, Replacing if/else and switch chains with polymorphism — State for lifecycle machines, Chain of Responsibility for handle-or-forward pipelines (servlet filters, Spring Security, approval flows, ATM dispensers), Separating traversal and operations from the data structure — Iterator for traversal, Visitor for double dispatch over a fixed type hierarchy, Interpreter for recursive evaluation of a composite AST, Open/Closed in practice: which axis stays open — Strategy/Chain keep algorithms open, Visitor keeps operations open at the cost of freezing the type set (the trade-off staff interviewers probe)

| | |
|---|---|
| Problems | 11 |
| Must-know | 5 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_StrategyDesignPattern.java` | Interchangeable algorithms behind an interface |
| 2 | `A02_TemplateDesignPattern.java` | Final skeleton with abstract hooks |
| 3 | `A03_ObserverDesignPattern.java` | Subject registry, push notification |
| 4 | `B02_CommandDesignPattern.java` | Request encapsulated as an object |
| 5 | `C01_StateDesignPatternExample.java` | State objects own their transitions |

## Full practice order


### A — Building blocks

- `A01_StrategyDesignPattern.java` — Interchangeable algorithms behind an interface **[must-know]**
  - The root primitive of the whole folder — Command, State and Chain are all Strategy with a twist, so nothing else reads cleanly until 'inject behavior as an object' is automatic.
- `A02_TemplateDesignPattern.java` — Final skeleton with abstract hooks **[must-know]**
  - The inheritance-based counterpart to Strategy; learned immediately after it so the classic 'Strategy vs Template Method, which and why' question has a real answer rather than a definition.
- `A03_ObserverDesignPattern.java` — Subject registry, push notification **[must-know]**
  - The second primitive — register/remove/notify is assumed by Mediator later and by every event-driven LLD prompt (notifications, stock ticker, cache invalidation), so it belongs before the medium tier.

### B — Easy

- `B01_IteratorPatternExample.java` — Externalized traversal cursor
  - Easiest file here and the gentlest warm-up: a hasNext/next cursor holding its own index, and the first taste of 'pull behavior out of the collection' that Visitor pushes much further.
- `B02_CommandDesignPattern.java` — Request encapsulated as an object **[must-know]**
  - Strategy plus a bound receiver, so it must follow Strategy; easy as written (invoker + concrete commands, no undo) but must-know because it unlocks undo/redo, macros, job queues and CQRS.
- `B03_MementoDesignPattern.java` — Encapsulated state snapshot on a stack
  - Sits right after Command because the two combine into the standard undo/redo answer; on its own it is just originator/memento/caretaker with a Stack.

### C — Medium

- `C01_StateDesignPatternExample.java` — State objects own their transitions **[must-know]**
  - First real medium problem and the highest-frequency LLD machine question (order lifecycle, vending machine, elevator); assumes Strategy, then adds the non-trivial step of a state choosing its own successor.
- `C02_ChainOfResponsibilityExample.java` — Handle-or-forward handler pipeline
  - Medium because each handler must decide partial-handle vs. forward with a remainder; not marked must-know only because 12 years of servlet filters and Spring Security already teach the shape.
- `C03_MediatorDesignPatternExample.java` — Central hub replaces N-to-N links
  - Placed last in the medium tier because it is Observer generalized to many-to-many, and the real lesson — hub vs. mesh coupling, and the god-object risk — only lands once Observer and Chain are owned.

### D — Hard

- `D01_VisitorDesignPatternDemo.java` — Double dispatch via accept/visit
  - The first genuinely non-obvious insight: accept(this) exists purely to recover the concrete type, and the pattern trades a frozen type hierarchy for open-ended operations.
- `D02_InterpreterPatternExample.java` — Recursive evaluation of a composite AST
  - Capstone — the only file with no description comment and the rarest pattern; assumes the composite tree plus recursive evaluation, and is where a grammar gets mapped onto terminal/non-terminal classes.

## Interview readiness

On taxonomy this folder is complete: all 11 GoF behavioral patterns are present, one runnable, commented example each (C:\Users\Ravi Varma Chaluvadi\AppData\Roaming\JetBrains\IdeaIC2025.2\scratches\AAScratches\03-LLD\Design-Patterns\3. Behavioral Patterns), and nothing canonical is missing at the pattern level — for a screening round that asks "explain Strategy vs Template Method" or "which pattern fits this?", he is ready. The gap is depth and composition, which is exactly what separates a senior pass from a staff pass. Every file is 68-171 lines of single-pattern, single-thread, happy-path demo in a toy domain (TV remote, weather station, playlist, ATM dispenser): CommandDesignPattern.java has no undo(), no history stack and no macro command despite its own header advertising undoable operations; ObserverDesignPattern.java notifies from a plain ArrayList with no thread safety, no exception isolation and no async delivery; IteratorPatternExample.java hand-rolls its own Iterator interface and never touches java.util.Iterable, fail-fast modCount or remove(); StateDesignPatternExample.java models a linear order lifecycle with no illegal-transition handling and no state-owned data. None of the eleven files composes two patterns, and the Problems folder next door holds only ParkingLotExample.java and TicTacToe.java — so there is nowhere in the workspace where State, Strategy and Observer are made to coexist under one requirement set. In a 45-minute round the question is almost never "show me Command"; it is "design undo/redo", "design a vending machine", "design the notification fan-out" — and every one of those dies on the first follow-up about concurrency, failure, memory or illegal input rather than on the pattern itself. Roughly a weekend of work on the four high-priority items below closes most of it, because the pattern vocabulary is already there.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Undo/Redo in a text editor — Command objects that carry their own undo(), an undo stack plus a redo stack, and a MacroCommand that groups several edits into one undoable unit | Command with reversible operations + Composite Command, contrasted against Memento snapshots | CommandDesignPattern.java stops at execute() on a TV remote — there is no undo(), no history, no invoker-side stack, and MementoDesignPattern.java only pops a single undo stack with no redo. 'Design undo/redo' is the single most-asked Command question, and the whole interview lives in the follow-up: inverse-operation commands are cheap but hard to write correctly, full-state mementos are trivial but blow up memory on a large document, and the senior answer is the hybrid (commands for small edits, periodic snapshots for checkpoints) plus what happens to the redo stack when a new edit arrives. He currently has the two halves as separate toy files and has never had to reconcile them. |
| high | An event bus / notification hub that survives production — 10k subscribers, subscribe and unsubscribe while a notify is in flight, one slow or throwing listener, and listeners that outlive their owners | Observer hardened: CopyOnWriteArrayList or snapshot-then-iterate, per-listener try/catch, executor-backed async delivery, WeakReference registration, push vs pull payloads | ObserverDesignPattern.java iterates a plain ArrayList and calls update() inline, so an unsubscribe inside a callback throws ConcurrentModificationException, one listener throwing kills the rest of the fan-out, and one slow listener blocks the publisher. Every interviewer who sees a correct Observer immediately asks those three questions, and the memory-leak one (a registered listener is a strong reference, which is why Swing and Spring listener leaks exist) is the staff-level probe. He has the shape but none of the hardening, and this same machinery is what a 'design a notification service' or 'design a stock ticker' round is actually grading. |
| high | A real custom iterator — a lazy nested/flattening iterator over a list-of-lists or an in-order BST iterator, implementing java.util.Iterator<T> and java.lang.Iterable<T> so it works in a for-each, with fail-fast modCount and a working remove() | Iterator implementing the JDK contract, external vs internal iteration, lazy advancement with O(h) space | IteratorPatternExample.java declares its own Iterator interface over an ArrayList index — it never implements java.util.Iterator, so the class cannot be used in an enhanced for loop, and it has no remove(), no modCount, and nothing lazy. The asked versions of this problem are all lazy: flatten a nested list without materialising it, iterate a BST in order using O(height) space, merge or zip two iterators. The follow-ups are the JDK contract itself — what hasNext() must guarantee about side effects, why ArrayList throws ConcurrentModificationException and how modCount produces that, and why Iterator.remove() is the only safe mutation during traversal. This is also the closest behavioural pattern to a coding round, so it gets asked in both LLD and DSA interviews. |
| high | Vending machine (or ATM) as a hardened state machine — money and inventory owned by the context, guarded transitions, explicit rejection of illegal events (insert coin while DISPENSING, select while NO_MONEY), refund on cancel, and a documented transition table | State with context-owned data, plus the design decision of where transitions live (inside each state vs a central table) and Strategy for the payment mode | This is not a duplicate of StateDesignPatternExample.java: that file is a linear NEW->PAID->SHIPPED->DELIVERED walk where every event is legal and the state objects hold no data. The vending machine is the version interviewers actually name, and it fails on the parts he has never written — what an invalid event does (silently ignore, throw, or return a typed result), where the accumulated coins and stock counts live so states stay stateless and shareable, how cancel unwinds a partial transaction, and whether transitions belong in the state classes (open to new states, scattered) or in a central map (auditable, closed). The transition table is also the artifact a staff interviewer asks him to draw before any code. |
| medium | Selecting a strategy at runtime without reintroducing the if/else — a strategy registry keyed by type, self-registering implementations, and the Spring idiom of injecting List<Strategy> or Map<String, Strategy> | Strategy + registry/lookup map, ServiceLoader, Spring's collection injection and @Qualifier | StrategyDesignPattern.java news up CreditCardPayment and PayPalPayment directly in main, which sidesteps the question every interviewer asks next: Strategy removed the switch from the algorithm but something still has to pick, so where did the switch go? The expected answer is that the branch collapses into a Map<PaymentType, PaymentStrategy> built once, that Spring can populate it by injecting every bean of the interface type, and that a new strategy then ships without touching the selector — which is what makes the Open/Closed claim true rather than rhetorical. He already reasons about which axis stays open; he has not written the registry that delivers it, and this is the most common way a clean Strategy answer still loses points. |
| medium | A request pipeline / interceptor chain — ordered filters (auth, rate limit, validation, logging) where any handler can short-circuit with a response, ordering is explicit, and handlers can act both before and after the downstream call | Chain of Responsibility in its middleware form: list-driven chain assembly, short-circuit return, around-style pre/post hooks | ChainOfResponsibilityExample.java is an ATM dispenser, which is the atypical case — every handler partially handles and always forwards, so it never exercises the pattern's real decision, which is 'handle and stop'. The asked shape is the servlet/Spring Security filter chain: handlers return a result or veto the request, the chain is assembled from an ordered list rather than hand-wired setNext calls, and a handler needs to run code after the rest of the chain completes (timing, logging, response rewriting), which a naive linked chain cannot do. Since his themes already name servlet filters and Spring Security as the motivating examples, being unable to build one when asked is a visible gap. |
| medium | Elevator / lift system for one building — one composite design where State (idle/moving/doors-open), Strategy (FCFS vs SCAN/LOOK scheduling) and Observer (floor displays, door sensors) all have to coexist | Multi-pattern composition, with the pattern boundaries justified rather than assumed | Nothing in this folder or in the Problems folder (only ParkingLotExample.java and TicTacToe.java) composes two behavioural patterns, yet every senior LLD round is a system prompt, not a pattern prompt. Elevator is the canonical one because it forces the exact judgement staff interviewers grade: which varying thing is a state and which is a strategy (direction is state, request scheduling is strategy), who owns the request queue, how a pluggable scheduler is compared against a fixed one, and how displays are updated without the controller knowing about them. Knowing eleven patterns individually does not produce that decomposition under time pressure — one rehearsed composite does. |

