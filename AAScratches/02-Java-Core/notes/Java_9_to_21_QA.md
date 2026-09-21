# Java 9 to 21 Q&A

Interview notes on the language features added between Java 9 and Java 21 — records, sealed classes, pattern matching and switch expressions — with several answers written the way a 10-year engineer would answer them.

Every snippet below was compiled and run on Temurin JDK 21.0.12. Where a snippet is deliberately invalid, the quoted text is the real `javac` error.

## Version reference — when each feature became final

Getting preview versus final right is the single easiest way to sound current, and the easiest way to sound stale. "Preview" means it needed `--enable-preview` and could still change; only the **final** column is what you can use in production.

| Feature | Preview in | **Final in** | JEP |
| --- | --- | --- | --- |
| `var` (local type inference) | — | **10** | 286 |
| Switch expressions, `yield` | 12, 13 | **14** | 361 |
| Text blocks | 13, 14 | **15** | 378 |
| Helpful NullPointerExceptions | — | **15** (on by default) | 358 |
| Pattern matching for `instanceof` | 14, 15 | **16** | 394 |
| Records | 14, 15 | **16** | 395 |
| Sealed classes | 15, 16 | **17** | 409 |
| Pattern matching for `switch` | 17, 18, 19, 20 | **21** | 441 |
| Record patterns | 19, 20 | **21** | 440 |
| Virtual threads | 19, 20 | **21** | 444 |
| Sequenced collections | — | **21** | 431 |

Two things people commonly get wrong here:

- **Records are Java 16, not 14.** Java 14 was the first *preview*.
- **Pattern matching for `switch` is Java 21, not 17.** It previewed for four consecutive releases, which is unusually long, because the `when` guard syntax changed along the way (earlier previews used `&&`).

## Table of Contents

**Records**

| # | Question |
| --- | --- |
| 1 | [Explain records. How do they differ from Lombok?](#1-explain-records-in-java-how-are-they-different-from-lombok-annotated-classes) |
| 2 | [What are the disadvantages of using records?](#2-what-are-the-disadvantages-of-using-records-in-java) |
| 3 | [Can a record extend another class?](#3-can-a-record-extend-another-class) |
| 4 | [Can you define instance fields other than the components?](#4-can-you-define-instance-fields-in-a-record-other-than-the-components) |
| 5 | [Can you define constructors in a record?](#5-can-you-define-constructors-in-a-record) |
| 6 | [Are record fields truly immutable?](#6-are-record-fields-truly-immutable) |
| 7 | [Can a record have methods other than accessors?](#7-can-a-record-have-methods-other-than-accessors) |
| 8 | [Can you use records with JPA or Hibernate?](#8-can-you-use-records-with-frameworks-like-jpa-or-hibernate) |
| 9 | [Can records form an inheritance hierarchy?](#9-trick-question-can-a-record-have-an-inheritance-hierarchy-with-other-records) |

**Sealed classes**

| # | Question |
| --- | --- |
| 10 | [Explain sealed classes](#10-explain-sealed-classes) |
| 11 | [Can an enum extend a sealed class?](#11-can-an-enum-extend-a-sealed-class) |
| 12 | [Can a record be a permitted subclass of a sealed type?](#12-can-a-record-be-a-permitted-subclass-of-a-sealed-type) |

**Pattern matching**

| # | Question |
| --- | --- |
| 13 | [Explain pattern matching](#13-explain-pattern-matching) |
| 14 | [Can you use multiple patterns in one switch case?](#14-can-you-use-multiple-patterns-in-one-switch-case) |
| 15 | [Is pattern matching syntactic sugar?](#15-is-pattern-matching-syntactic-sugar-or-does-it-affect-runtime-performance) |

**Switch expressions**

| # | Question |
| --- | --- |
| 16 | [Explain switch expressions and their latest features](#16-explain-switch-expressions-and-their-latest-features) |
| 17 | [Switch statement vs switch expression](#17-what-is-the-main-difference-between-a-switch-statement-and-a-switch-expression) |
| 18 | [What problem does a switch expression solve?](#18-what-problem-does-a-switch-expression-solve-compared-to-old-switch-statements) |
| 19 | [How does `yield` differ from `return` or `break`?](#19-how-does-the-yield-keyword-differ-from-return-or-break-in-switch) |
| 20 | [What does "exhaustive check" mean?](#20-what-does-exhaustive-check-mean) |

## 1. Explain records in Java. How are they different from Lombok-annotated classes?

Records (final in **Java 16**) are an **immutable data carrier** — they hold data without boilerplate.

Quick facts:

| Question | Answer |
| --- | --- |
| Can a record extend an abstract class? | No |
| Can a record implement interfaces? | Yes |
| Can a record be a permitted subtype of a sealed **interface**? | Yes |
| Can a record be `sealed` itself? | No — records are implicitly `final` |
| What is every record's superclass? | `java.lang.Record` |

```java
public record Employee(String id, String name, int age) {}
```

This single line automatically gives you:

- A final class (`Employee` can't be extended)
- Private final fields for each component (`id`, `name`, `age`)
- A canonical constructor (`public Employee(String id, String name, int age)`)
- Accessors (not getters, but same purpose) → `id()`, `name()`, `age()`
- `equals()`, `hashCode()`, and `toString()` implementations

Verified at runtime:

```text
Employee[id=E1, name=Ravi, age=30]
class java.lang.Record
record is final: true
```

Example with a compact constructor and a custom method:

```java
public record Employee(String id, String name, int age) {
    public Employee {
        if (age < 18) {
            throw new IllegalArgumentException("Age must be >= 18");
        }
    }
    public String greeting() {
        return "Hello, " + name;
    }
}
```

```text
compact ctor rejected: Age must be >= 18
```

### Record vs Lombok

| Aspect | Java Record | Lombok |
| --- | --- | --- |
| Boilerplate reduction | Built into the language | Annotation processing |
| Mutability | Always shallowly immutable | Mutable or immutable by annotation |
| Inheritance | Final, cannot extend | Can extend other classes |
| `equals`/`hashCode`/`toString` | Generated by the compiler | Generated by Lombok |
| Constructor | Canonical, auto-generated | All-args or no-args by config |
| Pattern matching | **Deconstructable** — `case Point(int x, int y)` | Just a POJO; no deconstruction |
| Dependency | Pure Java, none | Lombok + IDE plugin |

The row that actually decides the argument in a modern codebase is **pattern matching**. A record is deconstructable by the language — `case Circle(Point(var x, var y), double r)` — and no Lombok class can be, because deconstruction relies on the compiler knowing the canonical component order. That is a capability difference, not a style preference.

### When I still use Lombok

- Mutable entities, such as JPA entities that require setters
- Builders (`@Builder`) for objects with many optional fields
- Projects on Java 8 or 11, where records do not exist

## 2. What are the disadvantages of using records in Java?

### Cannot extend other classes

- All records implicitly extend `java.lang.Record`.
- Java has single inheritance, so no other superclass is possible.
- They can implement any number of interfaces.

```java
class Base {}
record Derived(int x) extends Base {}   // does not even parse
```

> **Precision fix:** the original note labelled this "Not allowed", which
> understates it. `extends` is not merely rejected for records — it is **not part
> of the record grammar at all**, so this is a *syntax* error, not a type error.
> The compiler fails at the `extends` token before it ever reasons about `Base`:
>
> ```text
> error: '{' expected
> record Derived(int x) extends Base {}
>                      ^
> ```
>
> Knowing it is a parse error is the difference between "records can't extend" and
> understanding that a record declaration has a fixed shape.

### The other real disadvantages

| Limitation | Consequence |
| --- | --- |
| No instance fields beyond components | State must be fully in the component list |
| Shallow immutability only | A `List` component is still mutable — see question 6 |
| No no-arg constructor | Breaks JPA and some older frameworks |
| Always final | No polymorphism except via interfaces |
| Components are public API | Renaming one is a breaking change |

## 3. Can a record extend another class?

No — records implicitly extend `java.lang.Record`, and Java doesn't support multiple inheritance, so they cannot extend any other class.

However, records can implement interfaces. Verified:

```java
public record Point(int x, int y) implements Comparable<Point> {
    public int compareTo(Point o) {
        return Integer.compare(this.x, o.x);
    }
    public double distance(Point other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}
```

```text
distance = 5.0
compareTo = -1
```

## 4. Can you define instance fields in a record other than the components?

**No.** Static fields are allowed; additional instance fields are not.

> **Correction:** the original note answered *"Yes, but only static fields can be
> added"*, which contradicts itself — a static field is not an instance field, so
> the answer to the question as asked is simply No. The example given was correct;
> the "Yes" was not.

```java
public record Employee(String id, String name) {
    static int employeeCount = 0;   // allowed
    int extra = 5;                  // NOT allowed
}
```

```text
error: field declaration must be static
        int extra = 5;
            ^
  (consider replacing field with record component)
```

The compiler's own hint — *"consider replacing field with record component"* — is the design rationale: a record's state **is** its component list, which is what lets `equals`, `hashCode`, `toString` and deconstruction patterns all be derived mechanically. A hidden extra field would silently fall outside all four.

## 5. Can you define constructors in a record?

Yes. Records support three forms — note that "override" is the wrong verb, since there is nothing to override; you are *replacing* or *augmenting* what the compiler would generate.

| Form | Signature | Use |
| --- | --- | --- |
| Canonical | All components, in order | Full control over assignment |
| Compact | No parameter list at all | Validation and normalisation |
| Non-canonical | Any other parameters | Must delegate via `this(...)` |

**Compact constructor** — the common case. Parameters are implicitly in scope, and the field assignments are appended automatically, so you assign to the *parameter* to normalise:

```java
public record Employee(String id, String name, int age) {
    public Employee {
        if (age < 18) throw new IllegalArgumentException("Invalid age");
        id = id.trim();          // assigns the parameter; the field gets the trimmed value
    }
}
```

Verified — `new Employee("  E1  ", "Ravi", 30)` produces:

```text
Employee[id=E1, name=Ravi, age=30]
```

**Explicit canonical constructor** — you must assign every field yourself:

```java
public Employee(String id, String name, int age) {
    this.id = id.trim();
    this.name = name;
    this.age = age;
}
```

You may write the compact form *or* the explicit canonical form, never both — they declare the same constructor.

**Non-canonical constructor** — must delegate to the canonical one on its first line:

```java
public record Employee(String id, String name, int age) {
    public Employee(String name) {
        this("UNKNOWN", name, 30);
    }
}
```

```text
Employee[id=UNKNOWN, name=Solo, age=30]
```

## 6. Are record fields truly immutable?

The fields are `final`, but that is **shallow** immutability. A `final` reference to a mutable object still points at something that can change.

Fix it with a defensive copy in a compact constructor:

```java
public record Team(String name, List<String> members) {
    public Team {
        members = List.copyOf(members);
    }
}
```

Verified — the record holds a snapshot, and the copy itself is unmodifiable:

```text
defensive copy held: [a]          // caller added "b" afterwards; record unaffected
copy is unmodifiable: UnsupportedOperationException
```

Two caveats on `List.copyOf`: it throws `NullPointerException` if any element is `null`, and it is a no-op if the argument is already an unmodifiable list. For full safety the accessor should also return a copy — but if the component is already unmodifiable, that is unnecessary.

## 7. Can a record have methods other than accessors?

Yes — instance methods, static methods, static factories, and nested types.

```java
public record Point(int x, int y) {
    public double distance(Point other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}
```

You can also override `toString()`, `equals()`, `hashCode()`, and any accessor. Overriding an accessor to return something other than the field is legal but a bad idea — it silently desynchronises the accessor from `equals`, which always uses the underlying field.

## 8. Can you use records with frameworks like JPA or Hibernate?

**Not as `@Entity` classes.** JPA requires a no-arg constructor, non-final fields and setters for its proxying and dirty-checking; records provide none of these.

Records are well suited to:

- DTOs
- API requests and responses
- Event models (Kafka, messaging)
- **JPA constructor projections** — this part is worth adding, because it is the case where records and JPA *do* work together:

```java
@Query("select new com.acme.EmployeeView(e.id, e.name) from Employee e")
List<EmployeeView> findViews();
```

`EmployeeView` can be a record. Jackson also supports records natively (since 2.12), so they work fine as request and response bodies without extra configuration.

## 9. Trick question: can a record have an inheritance hierarchy with other records?

No. A record cannot extend another record or any class, because it already extends `java.lang.Record` and records are implicitly `final`.

You get polymorphism through a **sealed interface** instead:

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}
```

This is the idiomatic modern Java shape — an algebraic data type. The sealed interface gives you the closed set of alternatives, the records give you the data, and pattern matching gives you exhaustive dispatch with no `default` branch.

## 10. Explain sealed classes

Sealed classes (final in **Java 17**) give you **controlled inheritance**: you declare exactly which types may extend or implement yours.

```java
sealed interface Payment permits CreditCardPayment, UpiPayment, CryptoPayment {}

final class CreditCardPayment implements Payment {}
final class UpiPayment implements Payment {}
non-sealed class CryptoPayment implements Payment {}   // reopens the hierarchy
```

The same idea with a sealed abstract class:

```java
public sealed abstract class Payment permits CreditCardPayment, UpiPayment, WalletPayment {
    public abstract void process();
}

final class CreditCardPayment extends Payment {
    @Override
    public void process() {
        System.out.println("Processing credit card payment...");
    }
}

final class UpiPayment extends Payment {
    @Override
    public void process() {
        System.out.println("Processing UPI payment...");
    }
}

final class WalletPayment extends Payment {
    @Override
    public void process() {
        System.out.println("Processing wallet payment...");
    }
}
```

Verified output:

```text
Processing credit card payment...
Processing UPI payment...
Processing wallet payment...
```

### The rules the compiler enforces

| Rule | Detail |
| --- | --- |
| Every permitted subtype must be declared | `final`, `sealed`, or `non-sealed` — no other option |
| Same module | Or same package, if in the unnamed module |
| Must actually extend | A permitted type that does not extend is an error |
| `permits` can be omitted | Only if all subtypes are in the same file |

`non-sealed` is the deliberate escape hatch: it reopens that one branch to unrestricted extension, and it is the only hyphenated keyword in Java.

The payoff is exhaustiveness. Because the compiler knows the full set of subtypes, a `switch` over a sealed type needs **no `default` branch**, and adding a new permitted subtype turns every such switch into a compile error until you handle it. That turns "did I update every dispatch site?" from a code-review question into a compiler guarantee.

## 11. Can an enum extend a sealed class?

No. Enums implicitly extend `java.lang.Enum` and cannot extend anything else — verified:

```text
Enum superclass: class java.lang.Enum
```

But an enum **can implement a sealed interface**, and is a valid permitted subtype because enums are implicitly final when they have no constant bodies:

```java
sealed interface Payment permits Card, UpiKind {}
final class Card implements Payment {}
enum UpiKind implements Payment { GPAY, PHONEPE }
```

```text
upi:GPAY
```

## 12. Can a record be a permitted subclass of a sealed type?

Yes — records are implicitly final, which satisfies the sealed-hierarchy rule. **But it must be a sealed `interface`, and the record must `implement` it.**

> **Correction — the original example did not compile.** It was written as:
>
> ```java
> public sealed class Shape permits Circle, Rectangle {}
> public record Circle(double radius) extends Shape {}       // syntax error
> public record Rectangle(double w, double h) extends Shape {} // syntax error
> ```
>
> Real compiler output:
>
> ```text
> error: '{' expected
> record Circle(double radius) extends Shape {}
>                             ^
> error: '{' expected
> record Rectangle(double w, double h) extends Shape {}
>                                     ^
> ```
>
> A record can **never** use `extends` — see question 2. So a record cannot be
> permitted by a sealed *class* at all; it can only implement a sealed
> *interface*. This matters beyond syntax: it means that when you want an
> algebraic data type made of records, **the sealed root must be an interface**.

The version that compiles and runs:

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

static String area(Shape s) {
    return switch (s) {                       // no default needed
        case Circle c    -> "circle " + Math.PI * c.radius() * c.radius();
        case Rectangle r -> "rect " + r.w() * r.h();
    };
}
```

```text
circle 3.141592653589793
rect 6.0
```

Note there is no `default` branch — the sealed interface makes the switch provably exhaustive. Remove one case and it stops compiling:

```text
error: the switch expression does not cover all possible input values
```

## 13. Explain pattern matching

Pattern matching makes type checks and the casts that follow them a single, safe step. It arrived progressively: `instanceof` patterns previewed in Java 14 and became final in **16**; `switch` patterns previewed from 17 and became final in **21**.

### Pattern matching for `instanceof` (final in Java 16)

```java
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}
```

```text
HELLO
```

`s` is scoped by *flow analysis*, not by braces — it is in scope wherever the compiler can prove the test passed. That includes after an early return, which is the idiom that removes the nesting:

```java
if (!(obj instanceof String s)) return;
System.out.println(s.toUpperCase());     // s is in scope here
```

### Pattern matching for `switch` (final in Java 21)

```java
static String format(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l    -> String.format("long %d", l);
        case String s  -> String.format("String '%s'", s);
        case null      -> "null value";
        default        -> obj.toString();
    };
}
```

```text
int 1
long 2
String 'hi'
null value
3.5
```

The `case null` label is the part worth calling out. Historically `switch` threw `NullPointerException` on a null selector; with a `case null` label it no longer does. **Without a `case null` label, a pattern switch still throws NPE** — the old behaviour is preserved for compatibility, so handling null is opt-in.

Case order matters: patterns are tested top to bottom, and the compiler rejects a case that is completely shadowed by an earlier one (a *dominance* error).

### Record patterns (final in Java 21)

Java 21 added deconstruction, which is where pattern matching starts to earn its keep. Verified:

```java
sealed interface Shape permits Circle, Rect {}
record Point(int x, int y) {}
record Circle(Point centre, double r) implements Shape {}
record Rect(Point tl, Point br) implements Shape {}

static String describe(Object o) {
    return switch (o) {
        case Circle(Point(var x, var y), double r) when r > 10 -> "big circle at " + x + "," + y;
        case Circle(Point p, double r) -> "circle r=" + r + " at " + p;
        case Rect(Point tl, Point br)  -> "rect " + tl + " to " + br;
        case null, default             -> "unknown";
    };
}
```

```text
big circle at 1,2
circle r=5.0 at Point[x=3, y=4]
rect Point[x=0, y=0] to Point[x=2, y=2]
unknown
unknown
```

Patterns nest arbitrarily — `Circle(Point(var x, var y), double r)` pulls `x` and `y` out of a record two levels deep in one line. This also works with `instanceof`:

```java
if (obj instanceof Circle(Point(var x, var y), var r))
    System.out.println("x=" + x + " y=" + y + " r=" + r);
```

```text
instanceof record pattern: x=7 y=8 r=1.0
```

### Real-world example

In one of our microservices, multiple event types arrived through Kafka — `UserCreatedEvent`, `OrderPlacedEvent`, and so on. The old code was an if-else chain:

```java
if (event instanceof UserCreatedEvent) { ... }
else if (event instanceof OrderPlacedEvent) { ... }
```

With pattern matching (Java 21):

```java
String result = switch (event) {
    case UserCreatedEvent u   -> handleUserEvent(u);
    case OrderPlacedEvent o   -> handleOrderEvent(o);
    case PaymentFailedEvent p -> handlePaymentEvent(p);
    default -> throw new IllegalArgumentException("Unknown event: " + event);
};
```

Benefits: cleaner, type-safe, no manual casting, easy to extend.

Worth adding when telling this story: if `Event` is a **sealed** interface, you can drop the `default` entirely, and then adding a new event type becomes a compile error at every dispatch site rather than a runtime exception in production. That is the version an interviewer is hoping you reach.

## 14. Can you use multiple patterns in one switch case?

**No — Java 21 has no OR patterns.** One case label may contain at most one pattern.

> **Correction — the original example does not compile.** It was written as:
>
> ```java
> case String s, null -> "String or Null";
> ```
>
> Real compiler output:
>
> ```text
> error: illegal fall-through from a pattern
>             case String s, null -> "String or Null";
>                            ^
>   (the current case label is missing a break)
> ```
>
> The original also called these "OR patterns". Java has no OR patterns — as of
> Java 21 they are not even a preview feature. The only multi-element case label
> permitted alongside a pattern is the special form **`case null, default`**.

What Java 21 **does** support:

| Form | Legal? | Example |
| --- | --- | --- |
| Guarded pattern | **Yes** | `case Integer i when i > 0 ->` |
| Multiple constants | **Yes** | `case MONDAY, FRIDAY ->` |
| `null` with `default` | **Yes** | `case null, default ->` |
| Two patterns in one label | **No** | `case Integer i, String s ->` |
| Pattern plus `null` | **No** | `case String s, null ->` |

The working version of the original example:

```java
static String guarded(Object obj) {
    return switch (obj) {
        case Integer i when i > 0 -> "Positive Integer";
        case Integer i when i < 0 -> "Negative Integer";
        case Integer i            -> "Zero";
        case String s             -> "String";
        case null                 -> "Null";
        default                   -> "Something else";
    };
}
```

```text
Positive Integer
Negative Integer
Zero
String
Null
Something else
```

Two points on guards. The `when` keyword is Java 21's syntax — earlier previews used `&&`, so older blog posts show the wrong form. And a guarded pattern **never contributes to exhaustiveness**: the compiler cannot prove `when i > 0` covers anything, which is why the unguarded `case Integer i` above is required.

## 15. Is pattern matching syntactic sugar or does it affect runtime performance?

Partly sugar, partly not — and the split is exactly what a senior interview is testing.

| Construct | How it compiles | Sugar? |
| --- | --- | --- |
| `instanceof` pattern | `instanceof` + checkcast + store | **Yes** |
| `switch` on patterns | `invokedynamic` to `SwitchBootstraps.typeSwitch` | **No** |
| Record deconstruction | Accessor calls, in order | Mostly |

> **Correction:** the original answer said pattern matching is *"compiled down to
> traditional `instanceof` checks and casts"* with *"no runtime overhead
> (identical bytecode)"*. That is true for **`instanceof` patterns** and wrong for
> **`switch` patterns**. A pattern switch compiles to an `invokedynamic` call to
> `java.lang.runtime.SwitchBootstraps.typeSwitch`, which resolves a call site
> returning the index of the matching case. That is genuinely different bytecode
> from an if-else chain, not the same bytecode rearranged.

The practical consequence is the opposite of a penalty: because the bootstrap produces an **indexed jump** rather than a linear chain of type tests, a pattern switch can scale better than a hand-written if-else chain as the number of cases grows. The first execution pays a one-time call-site linkage cost, as with lambdas.

So the accurate summary is: **pattern matching is not slower, `instanceof` patterns are free, and `switch` patterns use a different and potentially faster mechanism.** The real wins remain readability, exhaustiveness checking and the elimination of manual casts.

## 16. Explain switch expressions and their latest features

Switch expressions became final in **Java 14** (JEP 361).

### Switch as an expression (yielding a value)

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case THURSDAY, SATURDAY -> 8;
    case WEDNESDAY -> 9;
};
```

Verified over all seven constants:

```text
MONDAY -> 6      TUESDAY -> 7     WEDNESDAY -> 9    THURSDAY -> 8
FRIDAY -> 6      SATURDAY -> 8    SUNDAY -> 6
```

The arrow form eliminates fall-through and `break`. Note the switch has no `default` and still compiles — every enum constant is covered, so it is exhaustive.

### `yield` for multi-statement cases

```java
int result = switch (operation) {
    case "ADD" -> {
        int sum = a + b;
        yield sum;
    }
    case "SUBTRACT" -> a - b;
    default -> throw new IllegalArgumentException("Unknown op: " + operation);
};
```

```text
ADD 2,3 = 5
SUBTRACT 2,3 = -1
```

`yield` is a *contextual* keyword — it only has meaning inside a switch block, so existing code with a variable or method named `yield` still compiles.

### Arrow versus colon is independent of expression versus statement

A point that confuses almost everyone: these are **two separate axes**. You can write a switch *statement* with arrows (getting fall-through protection without needing a value), and a switch *expression* with colons and `yield`. The arrow is about fall-through; the expression-ness is about producing a value.

## 17. What is the main difference between a switch statement and a switch expression?

- **Switch statement:** executes code blocks, produces no value.
- **Switch expression:** evaluates to a value, so it can be assigned or returned.

```java
// switch statement
switch (day) {
    case MONDAY: System.out.println("Start"); break;
}

// switch expression
String msg = switch (day) {
    case MONDAY -> "Start";
    default -> "Other";
};
```

The consequences that follow from "it is an expression":

| | Statement | Expression |
| --- | --- | --- |
| Must be exhaustive | No | **Yes** |
| Every branch must produce a value | No | **Yes** |
| `null` selector | NPE | NPE unless `case null` |
| Can complete without matching | Yes, silently | No — compile error |

"Must be exhaustive" is the one that carries real value. A statement that silently matches nothing is a bug you find in production; an expression that might match nothing does not compile.

## 18. What problem does a switch expression solve compared to old switch statements?

- **Accidental fall-through.** The classic bug — a missing `break` silently running the next case — is impossible with arrows.
- **Multiple labels per case** — `case MONDAY, FRIDAY ->` instead of stacked empty cases.
- **Value-producing**, so no mutable accumulator variable outside the switch.
- **Definite assignment.** With the old form, a `final` variable assigned in each branch could not be proven assigned; an expression assigns it exactly once.
- **Exhaustiveness**, which turns a forgotten enum constant into a compile error.

The old pattern this replaces:

```java
int numLetters;                       // not final, must be declared first
switch (day) {
    case MONDAY: numLetters = 6; break;
    // forget a break, or a case, and it compiles anyway
    default: throw new IllegalStateException();
}
```

## 19. How does the `yield` keyword differ from `return` or `break` in switch?

`yield` supplies the value of a switch **expression**'s case block. `return` would exit the enclosing method instead, and is a compile error inside a switch expression. `break` with a value was the syntax used during preview and is no longer valid.

```java
int result = switch (op) {
    case "ADD" -> {
        int r = a + b;
        yield r;       // correct
    }
    default -> 0;
};
```

| Keyword | Exits | Valid in a switch expression? |
| --- | --- | --- |
| `yield` | The switch, with a value | **Yes** — this is its only use |
| `return` | The enclosing method | No |
| `break` | The switch (no value) | No |

### Old switch vs new switch expression

| Feature | Old switch | New switch expression |
| --- | --- | --- |
| Returns a value | No | Yes |
| Requires `break` | Yes | No |
| Arrow syntax | No | Yes |
| Fall-through | Yes | No |
| Exhaustive check | No | Yes (enums and sealed types) |
| Pattern matching support | No | Yes — **Java 21** |

> **Correction:** the original table's last row said pattern matching in switch
> was available in **"Java 17+"**. Java 17 had it as a **preview** feature
> requiring `--enable-preview`; it did not become final until **Java 21**
> (JEP 441). For a role targeting Java 17, the honest answer is "preview only".

## 20. What does "exhaustive check" mean?

In a switch **expression**, the compiler requires that every possible input is covered — by explicit cases, or by a `default`. If not, compilation fails. The old switch statement did not care and would compile with cases missing.

### Example 1 — non-exhaustive switch (compile error)

```java
enum Day { MONDAY, TUESDAY, WEDNESDAY }

String msg = switch (Day.MONDAY) {
    case MONDAY -> "Start";
    case TUESDAY -> "Second";
    // Missing WEDNESDAY!
};
```

Real compiler output:

```text
error: the switch expression does not cover all possible input values
        String msg = switch (Day.MONDAY) {
                     ^
```

To fix it, either:

```java
// Option 1: add the missing case
case WEDNESDAY -> "Midweek";
// Option 2: add a default
default -> "Other";
```

### Example 2 — sealed types are checked the same way

```java
sealed interface E permits A, B {}
record A() implements E {}
record B() implements E {}

static String f(E e) { return switch (e) { case A a -> "A"; case B b -> "B"; }; }  // OK
static String g(E e) { return switch (e) { case A a -> "A"; }; }                   // error
```

```text
error: the switch expression does not cover all possible input values
    static String g(E e) { return switch (e) { case A a -> "A"; }; }
                                  ^
```

### Prefer exhaustiveness over `default`

Adding `default` makes any switch compile, and in doing so **throws away the guarantee you wanted**. With a sealed hierarchy and no `default`, adding a new permitted subtype breaks the build at every switch that needs updating — which is the entire point. With a `default`, the new subtype silently falls into it and you find out in production.

Two related details:

- The compiler inserts a hidden `MatchException` fallback so that if a sealed hierarchy is recompiled separately and a new subtype appears at runtime, the switch fails loudly rather than returning something wrong.
- Exhaustiveness applies to switch **expressions** always, and to switch **statements** only when they use patterns. A plain old arrow statement over an enum is still allowed to be incomplete.
