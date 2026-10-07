# Design Patterns — Key Points

A **design pattern** is a named, reusable solution to a problem that keeps coming up in object-oriented design. The 23 classic ones come from the 1994 book *Design Patterns* by Gamma, Helm, Johnson and Vlissides — the "Gang of Four", so you will hear them called **GoF patterns**.

Every pattern below has: a **definition** you can say out loud, the **variations** worth naming in an interview, one **small example**, and **use cases** (including where it appears in the JDK and Spring). Each section links to the full worked file in this folder.

## Contents

| # | Pattern | Family | Intent in one line |
| --- | --- | --- | --- |
| 1 | [Singleton](#1-singleton) | Creational | Exactly one instance, one access point |
| 2 | [Factory](#2-factory) | Creational | One place decides which class to create |
| 3 | [Abstract Factory](#3-abstract-factory) | Creational | Create a whole matching family of objects |
| 4 | [Builder](#4-builder) | Creational | Assemble a complex object step by step |
| 5 | [Prototype](#5-prototype) | Creational | Create by copying an existing object |
| 6 | [Adapter](#6-adapter) | Structural | Make an incompatible interface fit |
| 7 | [Facade](#7-facade) | Structural | One simple entry point over many services |
| 8 | [Decorator](#8-decorator) | Structural | Add behaviour by wrapping, stackable |
| 9 | [Proxy](#9-proxy) | Structural | A stand-in that controls access |
| 10 | [Composite](#10-composite) | Structural | Treat one object and a group the same way |
| 11 | [Flyweight](#11-flyweight) | Structural | Share the heavy repeated part |
| 12 | [Bridge](#12-bridge) | Structural | Split two dimensions into two hierarchies |
| 13 | [Strategy](#13-strategy) | Behavioral | Swap the algorithm at runtime |
| 14 | [Template Method](#14-template-method) | Behavioral | Fixed steps, some filled in by subclasses |
| 15 | [Observer](#15-observer) | Behavioral | Notify listeners when something changes |
| 16 | [Iterator](#16-iterator) | Behavioral | Walk elements without knowing the storage |
| 17 | [Command](#17-command) | Behavioral | A request as an object (queue, undo) |
| 18 | [Memento](#18-memento) | Behavioral | Snapshot state to restore later |
| 19 | [State](#19-state) | Behavioral | Behaviour changes with internal state |
| 20 | [Chain of Responsibility](#20-chain-of-responsibility) | Behavioral | Pass a request along a line of handlers |
| 21 | [Mediator](#21-mediator) | Behavioral | Peers talk through one coordinator |
| 22 | [Visitor](#22-visitor) | Behavioral | New operations over fixed types |
| 23 | [Interpreter](#23-interpreter) | Behavioral | Rules of a small language as objects |
| — | [Confusable pairs](#confusable-pairs) | — | The differences interviewers probe |
| — | [Spot the pattern](#spot-the-pattern-from-the-symptom) | — | Code smell → which pattern fixes it |

The three families answer three different questions:

| Family | Question it answers | Count |
| --- | --- | --- |
| Creational | How do objects get **created**? | 5 |
| Structural | How are objects **put together**? | 7 |
| Behavioral | How do objects **communicate and share work**? | 11 |

Every code block below compiles on its own with JDK 25. Lines starting with `// usage:` show how the code is called.

---

## Creational patterns

### 1. Singleton

**Definition:** Make sure a class has exactly one instance and give one global way to reach it. Strictly, it is one per class loader, not one per JVM.

**Variations:**

| Variation | How it works | Catch |
| --- | --- | --- |
| Eager | `static final X INSTANCE = new X();` | Built when the class loads, even if never used |
| Lazy, `synchronized` method | `static synchronized X getInstance()` | Thread-safe, but every call takes a lock |
| Double-checked locking | Check, lock, check again; field must be `volatile` | Without `volatile`, another thread can see a half-built object |
| Holder class (Bill Pugh) | A nested static class holds the instance | Lazy and thread-safe for free — the JVM initialises a class only once |
| Enum | `enum X { INSTANCE; }` | Safest: reflection and deserialization can't make a second copy. Can't extend another class |

**Example:**

```java
// Holder idiom: lazy and thread-safe, with no locking in your code.
final class AppConfig {
    private AppConfig() { }

    private static final class Holder {
        static final AppConfig INSTANCE = new AppConfig();
    }

    static AppConfig getInstance() {
        return Holder.INSTANCE;   // Holder loads (and builds INSTANCE) on the first call only
    }
}

// Enum singleton: the JVM guarantees exactly one INSTANCE.
enum IdGenerator {
    INSTANCE;
    private long next = 0;
    synchronized long nextId() { return ++next; }
}
// usage: AppConfig.getInstance();  IdGenerator.INSTANCE.nextId();
```

**Use cases:** app configuration, ID generator, connection-pool manager, `Runtime.getRuntime()`.

**Interview note:** Spring "singleton" beans are one per Spring container, not one per JVM. Prefer letting Spring inject one shared bean over a hand-written singleton — static access makes unit testing hard.

**In this repo:** [D01_Singleton.java](1.%20Creational%20Design%20Patterns/D01_Singleton.java) — covers every way a singleton leaks (reflection, serialization, cloning).

### 2. Factory

**Definition:** Put the "which concrete class do I create?" decision in one place, so callers ask for what they need (`"UPI"`) instead of naming a class (`new UpiProcessor()`).

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Simple Factory | One static method with a `switch` on a key | Most common in real code. Not an official GoF pattern. Adding a type means editing the `switch` |
| Factory Method (GoF) | A base class declares an abstract `create()`; each subclass returns its own product | The base class has logic that uses the product but must not know its exact type |
| Registry factory | `Map<String, Supplier<T>>` instead of a `switch` | New types are one new map entry; nothing else changes |

**Example:**

```java
import java.util.Map;
import java.util.function.Supplier;

interface PaymentProcessor { void pay(long amount); }
class UpiProcessor  implements PaymentProcessor { public void pay(long a) { System.out.println("UPI "  + a); } }
class CardProcessor implements PaymentProcessor { public void pay(long a) { System.out.println("Card " + a); } }

// Registry factory: the "code -> class" decision lives here and nowhere else.
final class PaymentFactory {
    private static final Map<String, Supplier<PaymentProcessor>> REGISTRY = Map.of(
            "UPI",  UpiProcessor::new,
            "CARD", CardProcessor::new);

    static PaymentProcessor create(String method) {
        Supplier<PaymentProcessor> supplier = REGISTRY.get(method);
        if (supplier == null) throw new IllegalArgumentException("Unknown method: " + method);
        return supplier.get();
    }
}
// usage: PaymentFactory.create("UPI").pay(499);
```

**Use cases:** payment processor per payment method, notification sender per channel, parser per file type. In the JDK: `List.of(...)`, `NumberFormat.getCurrencyInstance()`, and `Collection.iterator()` — the textbook Factory Method, because each collection returns its own kind of iterator.

**In this repo:** [A01_FactoryDesignPattern.java](1.%20Creational%20Design%20Patterns/A01_FactoryDesignPattern.java)

### 3. Abstract Factory

**Definition:** A factory that creates a whole **family** of related objects that must be used together, so the code can never mix parts from different families.

**Variations:**

| Variation | How it works | Catch |
| --- | --- | --- |
| One factory class per family | `IndiaKit`, `UsKit` each implement `CheckoutKit` | Adding a new family is one class. Adding a new product type means editing every factory |
| Chosen once from config | Pick the factory at startup (`region=IN`) and inject it everywhere | Guarantees every part used in that run matches |

**Example:**

```java
interface TaxCalculator { long tax(long amount); }
interface ShippingRule  { long fee(long amount); }

// The family: every part of a region's checkout comes from one place.
interface CheckoutKit {
    TaxCalculator taxCalculator();
    ShippingRule shippingRule();
}

final class IndiaKit implements CheckoutKit {
    public TaxCalculator taxCalculator() { return amount -> amount * 18 / 100; }        // GST 18%
    public ShippingRule shippingRule()   { return amount -> amount >= 499 ? 0 : 40; }   // free above 499
}

final class UsKit implements CheckoutKit {
    public TaxCalculator taxCalculator() { return amount -> amount * 7 / 100; }         // sales tax
    public ShippingRule shippingRule()   { return amount -> 5; }                        // flat fee
}
// usage: CheckoutKit kit = "IN".equals(region) ? new IndiaKit() : new UsKit();
//        long total = amount + kit.taxCalculator().tax(amount) + kit.shippingRule().fee(amount);
```

**Use cases:** regional checkout kits, UI themes (a dark-theme factory makes a dark `Button` *and* a dark `Checkbox`), database vendor kits (connection + SQL dialect). In the JDK: `DocumentBuilderFactory.newInstance()`.

**Factory vs Abstract Factory:** Factory Method creates **one** product and lets a subclass pick it. Abstract Factory creates a **family** of products from one object you pass around.

**In this repo:** [C01_AbstractFactoryPattern.java](1.%20Creational%20Design%20Patterns/C01_AbstractFactoryPattern.java)

### 4. Builder

**Definition:** Build a complex object through named, chainable methods, then create it in a single `build()` call that validates everything. It solves the "telescoping constructor" problem — `new Pizza(12, true, false, true, null, 3)` where nobody can tell which `true` means what.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Static nested Builder | `new SearchRequest.Builder("shoes").maxPrice(2000).build()` | The everyday version (from *Effective Java*). The built object stays immutable |
| Lombok `@Builder` | Annotation generates the builder | Less code. Validation needs extra work |
| Step builder | Each step returns a different interface, so required fields must be set in order | Compile-time safety for required fields; more boilerplate |
| GoF Builder + Director | A Director runs the same steps on different builders | Rare in Java code — know it exists |

**Example:**

```java
final class SearchRequest {
    private final String query;        // required
    private final int maxPrice;        // optional
    private final boolean inStockOnly; // optional

    private SearchRequest(Builder b) {
        this.query = b.query;
        this.maxPrice = b.maxPrice;
        this.inStockOnly = b.inStockOnly;
    }

    static final class Builder {
        private final String query;
        private int maxPrice = Integer.MAX_VALUE;
        private boolean inStockOnly = false;

        Builder(String query) { this.query = query; }                 // required -> constructor
        Builder maxPrice(int value) { this.maxPrice = value; return this; } // optional -> named method
        Builder inStockOnly() { this.inStockOnly = true; return this; }

        SearchRequest build() {                                       // the single validation gate
            if (query == null || query.isBlank()) throw new IllegalStateException("query is required");
            if (maxPrice <= 0) throw new IllegalStateException("maxPrice must be > 0");
            return new SearchRequest(this);
        }
    }
}
// usage: new SearchRequest.Builder("running shoes").maxPrice(3000).inStockOnly().build();
```

**Use cases:** request/DTO objects with many optional fields, test-data builders. In the JDK: `HttpRequest.newBuilder()`, `Stream.builder()`, `StringBuilder` (loosely).

**In this repo:** [B01_Builder.java](1.%20Creational%20Design%20Patterns/B01_Builder.java)

### 5. Prototype

**Definition:** Create a new object by copying an existing one instead of building it from scratch. Useful when setup is expensive, or when you want "same as this, with one change".

**Variations:**

| Variation | How it works | Catch |
| --- | --- | --- |
| Copy constructor / `copy()` method | `new Product(other)` or `other.copy()` | Preferred in Java — explicit, no surprises |
| `Cloneable` + `clone()` | Call `super.clone()` | Shallow by default, awkward checked exception. *Effective Java* advises against it |
| Prototype registry | A map of named templates; callers copy one, then tweak | Good for presets |
| Shallow vs deep copy | Shallow shares mutable fields; deep copies them | The bug interviewers look for: two objects sharing one `List` |

**Example:**

```java
import java.util.ArrayList;
import java.util.List;

final class Product {
    String name;
    String colour;
    List<String> tags;

    Product(String name, String colour, List<String> tags) {
        this.name = name;
        this.colour = colour;
        this.tags = tags;
    }

    // Deep copy: the new product gets its OWN tags list.
    Product copy() {
        return new Product(name, colour, new ArrayList<>(tags));
    }
}
// usage: Product red = base.copy(); red.colour = "red"; red.tags.add("sale");  // base.tags unchanged
```

**Use cases:** product variants from a base listing, spawning game enemies from a template, document templates.

**Interview note:** Spring's `prototype` **scope** is a different idea — it just means "create a new bean every time it is requested". Nothing is copied.

**In this repo:** [B02_Prototype.java](1.%20Creational%20Design%20Patterns/B02_Prototype.java)

---

## Structural patterns

### 6. Adapter

**Definition:** Make a class with the wrong interface usable where a different interface is expected, by wrapping it and translating each call. "Implement the interface we want, hold the class we have."

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Object adapter | Implements the target interface, holds the adaptee as a field | Preferred — composition works even with `final` classes |
| Class adapter | Extends the adaptee and implements the target | Rare in Java because of single inheritance |
| Two-way adapter | Implements both interfaces | During migrations, when old and new code run side by side |

**Example:**

```java
// The interface our code wants.
interface Courier { String ship(String orderId, double weightKg); }

// A third-party SDK we cannot change: grams, and different names.
final class FastShipSdk {
    String createConsignment(String ref, int grams) { return "FS-" + ref + "-" + grams; }
}

// Adapter: implements what we want, holds what we have, translates in between.
final class FastShipAdapter implements Courier {
    private final FastShipSdk sdk;
    FastShipAdapter(FastShipSdk sdk) { this.sdk = sdk; }

    @Override
    public String ship(String orderId, double weightKg) {
        return sdk.createConsignment(orderId, (int) Math.round(weightKg * 1000));
    }
}
// usage: Courier courier = new FastShipAdapter(new FastShipSdk()); courier.ship("ORD-1", 1.25);
```

**Use cases:** wrapping courier or payment-gateway SDKs, legacy code behind a new interface. In the JDK: `Arrays.asList(array)` (array → `List`), `InputStreamReader` (bytes → characters). In Spring MVC: `HandlerAdapter`.

**In this repo:** [A01_AdapterPattern.java](2.%20Structural%20Patterns/A01_AdapterPattern.java)

### 7. Facade

**Definition:** One simple entry point over a group of subsystems, so callers don't need to know how many services are involved or in what order to call them.

**Variations:**

| Variation | How it works | Catch |
| --- | --- | --- |
| Simple facade | One class, one method per use case | The common form |
| Facade plus direct access | The facade is a shortcut; advanced callers may still use subsystems | Decide on purpose whether the facade is the only door |
| One facade per module | Each module or domain gets its own facade | Stops a single facade from becoming a "god class" |

**Example:**

```java
class Inventory { void reserve(String sku)            { System.out.println("reserved " + sku); } }
class Payments  { void charge(String user, long amt)   { System.out.println("charged " + amt); } }
class Shipping  { void schedule(String sku)           { System.out.println("shipping " + sku); } }
class Notifier  { void email(String user, String msg) { System.out.println("mail: " + msg); } }

// Facade: one call in, four calls out, in the right order.
final class OrderFacade {
    private final Inventory inventory = new Inventory();
    private final Payments payments = new Payments();
    private final Shipping shipping = new Shipping();
    private final Notifier notifier = new Notifier();

    void placeOrder(String user, String sku, long amount) {
        inventory.reserve(sku);
        payments.charge(user, amount);
        shipping.schedule(sku);
        notifier.email(user, "Order placed for " + sku);
    }
}
// usage: new OrderFacade().placeOrder("ravi", "SKU-1", 999);
```

**Use cases:** a checkout `placeOrder()`, a "create account" flow, a client library over a messy API. Real examples: SLF4J (the name means *Simple Logging Facade for Java*), Spring `JdbcTemplate` over raw JDBC.

**In this repo:** [B01_FacadePattern.java](2.%20Structural%20Patterns/B01_FacadePattern.java)

### 8. Decorator

**Definition:** Add behaviour to an object by wrapping it in another object that has the **same interface**. Because every wrapper looks like the original, wrappers stack in any order, at runtime, with no subclassing.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Interface wrappers | Each decorator implements the interface and holds one delegate | The standard form |
| Abstract decorator base | A base class forwards every method; subclasses override only what they change | Large interfaces (the JDK's `FilterInputStream`) |
| Functional decorators | Wrap a lambda with another lambda (`Function.andThen`) | Single-method interfaces |

**Example:**

```java
interface PriceApi { long price(String sku); }

final class RemotePriceApi implements PriceApi {
    public long price(String sku) { return 999; }   // pretend this is an HTTP call
}

final class LoggingPriceApi implements PriceApi {
    private final PriceApi inner;
    LoggingPriceApi(PriceApi inner) { this.inner = inner; }

    public long price(String sku) {
        System.out.println("price(" + sku + ")");
        return inner.price(sku);
    }
}

final class RetryingPriceApi implements PriceApi {
    private final PriceApi inner;
    private final int attempts;
    RetryingPriceApi(PriceApi inner, int attempts) { this.inner = inner; this.attempts = attempts; }

    public long price(String sku) {
        RuntimeException last = new IllegalStateException("no attempts made");
        for (int i = 0; i < attempts; i++) {
            try { return inner.price(sku); } catch (RuntimeException e) { last = e; }
        }
        throw last;
    }
}
// usage: PriceApi api = new RetryingPriceApi(new LoggingPriceApi(new RemotePriceApi()), 3);
```

**Use cases:** adding retry, logging, metrics or fallback around a client without touching it. In the JDK: `new BufferedReader(new InputStreamReader(new FileInputStream(file)))`, `Collections.unmodifiableList(list)`, `Collections.synchronizedMap(map)`.

**In this repo:** [C01_DecoratorDesignPattern.java](2.%20Structural%20Patterns/C01_DecoratorDesignPattern.java)

### 9. Proxy

**Definition:** A stand-in object with the same interface as the real one that **controls access** to it — deciding whether, when, or how the real object gets called.

**Variations:**

| Variation | What it controls | Real example |
| --- | --- | --- |
| Virtual proxy | Creates the real object only on first use | Hibernate lazy-loaded entities |
| Protection proxy | Checks permissions before forwarding | Admin-only writes |
| Caching proxy | Answers from a cache; forwards only on a miss | Product catalog reads |
| Remote proxy | A local object that forwards calls over the network | gRPC stubs, Feign clients |
| Dynamic proxy | Generated at runtime: JDK `Proxy` (interfaces only) or CGLIB/ByteBuddy (subclass) | How Spring implements `@Transactional` and `@Cacheable` |

**Example:**

```java
import java.util.HashMap;
import java.util.Map;

interface Catalog { String find(String sku); }

final class DbCatalog implements Catalog {
    public String find(String sku) {
        System.out.println("DB hit " + sku);
        return "Product " + sku;
    }
}

// Caching proxy: same interface, sits in front, decides when the real catalog is touched.
final class CachingCatalog implements Catalog {
    private final Catalog real;
    private final Map<String, String> cache = new HashMap<>();
    CachingCatalog(Catalog real) { this.real = real; }

    public String find(String sku) {
        return cache.computeIfAbsent(sku, real::find);
    }
}
// usage: Catalog c = new CachingCatalog(new DbCatalog()); c.find("A1"); c.find("A1");  // one DB hit
```

**Use cases:** caching, lazy loading, access control, remote calls.

**Interview note — the Spring self-invocation trap:** Spring wraps your bean in a proxy. If a method in the bean calls `this.otherMethod()`, the call goes straight to the object and **skips the proxy**, so `@Transactional` on `otherMethod` does nothing.

**In this repo:** [C02_ProxyDesignPattern.java](2.%20Structural%20Patterns/C02_ProxyDesignPattern.java)

### 10. Composite

**Definition:** Treat a single object and a group of objects the same way by giving both the same interface. A group holds children of that interface — which can themselves be groups — so you get a tree.

**Variations:**

| Variation | How it works | Trade-off |
| --- | --- | --- |
| Safe | `add()` / `remove()` exist only on the group class | Leaves can't be misused, but the caller must know which is which when building |
| Transparent | `add()` / `remove()` on the shared interface; leaves throw | Caller treats everything alike, but mistakes fail at runtime |

**Example:**

```java
import java.util.ArrayList;
import java.util.List;

interface OrderItem { long price(); }

record Product(String name, long price) implements OrderItem { }   // leaf

// Composite: a bundle IS an OrderItem and HOLDS OrderItems (which can be bundles).
final class Bundle implements OrderItem {
    private final List<OrderItem> items = new ArrayList<>();
    private final int discountPercent;
    Bundle(int discountPercent) { this.discountPercent = discountPercent; }

    Bundle add(OrderItem item) { items.add(item); return this; }

    public long price() {
        long sum = items.stream().mapToLong(OrderItem::price).sum();
        return sum - sum * discountPercent / 100;
    }
}
// usage: new Bundle(10).add(new Product("Phone", 50_000))
//                      .add(new Bundle(0).add(new Product("Case", 1_000))).price();
```

**Use cases:** file systems (folders hold files and folders), product bundles, org charts, menus. In the JDK: Swing/AWT `Container` holds `Component`s. Interpreter (pattern 23) is built on Composite.

**In this repo:** [C03_CompositePattern.java](2.%20Structural%20Patterns/C03_CompositePattern.java)

### 11. Flyweight

**Definition:** When many objects repeat the same heavy data, store that data once and share it. Split the state in two:

- **Intrinsic** state — the same for many objects (shared, must be immutable).
- **Extrinsic** state — different for each object (kept small, or passed in).

**Variations:**

| Variation | How it works | Example |
| --- | --- | --- |
| Flyweight factory | A map returns the existing shared object for a key, or creates it once | Tree types in a game |
| Pre-built pool | A fixed set created up front | `Integer.valueOf` caches -128 to 127 |

**Example:**

```java
import java.util.HashMap;
import java.util.Map;

// Intrinsic: shared, immutable, heavy.
record TreeType(String species, String colour, String texture) { }

final class TreeTypes {
    private static final Map<String, TreeType> CACHE = new HashMap<>();

    static TreeType of(String species, String colour, String texture) {
        return CACHE.computeIfAbsent(species + "|" + colour + "|" + texture,
                key -> new TreeType(species, colour, texture));
    }
}

// Extrinsic: tiny, one per tree.
record Tree(int x, int y, TreeType type) { }
// usage: a million Trees across 3 species share just 3 TreeType objects.
```

**Use cases:** game maps (trees, bullets), characters in a text editor. In the JDK: the String pool, and the `Integer` cache — which is why `Integer a = 127, b = 127; a == b` is `true` but the same with `128` is `false`.

**In this repo:** [C04_Flyweight.java](2.%20Structural%20Patterns/C04_Flyweight.java)

### 12. Bridge

**Definition:** When a class varies along **two independent dimensions**, split it into two hierarchies and connect them with a field. Without it you need a class for every combination (N × M); with it you need N + M.

**Variations:** there is really one shape. What changes is how the second side is chosen — passed into the constructor (most common), or swapped at runtime with a setter.

**Example:**

```java
// Dimension 2: HOW it is delivered.
interface Channel { void send(String to, String text); }

final class EmailChannel implements Channel {
    public void send(String to, String text) { System.out.println("email " + to + ": " + text); }
}
final class SmsChannel implements Channel {
    public void send(String to, String text) { System.out.println("sms " + to + ": " + text); }
}

// Dimension 1: WHAT is sent. It HOLDS a Channel instead of extending one.
abstract class Notification {
    protected final Channel channel;
    Notification(Channel channel) { this.channel = channel; }
    abstract void deliver(String user);
}

final class OrderShipped extends Notification {
    OrderShipped(Channel channel) { super(channel); }
    void deliver(String user) { channel.send(user, "Your order has shipped"); }
}
final class PriceDrop extends Notification {
    PriceDrop(Channel channel) { super(channel); }
    void deliver(String user) { channel.send(user, "An item on your wishlist is cheaper"); }
}
// usage: new PriceDrop(new SmsChannel()).deliver("ravi");
//        adding a WhatsApp channel = 1 new class, not 1 per notification type.
```

**Use cases:** notification type × channel, shape × renderer, report × export format. Real example: JDBC — your code talks to `java.sql` interfaces, and each vendor's `Driver` is the other side.

**In this repo:** [D01_BridgePattern.java](2.%20Structural%20Patterns/D01_BridgePattern.java)

---

## Behavioral patterns

### 13. Strategy

**Definition:** Put each version of an algorithm behind one interface and choose one at runtime. The algorithm becomes a **field holding an object**, not an `if/else` branch.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Class per strategy | `class FlatDiscount implements Promotion` | The strategy has state or several methods |
| Lambda strategy | Functional interface + lambda | Single-method strategies — `Comparator` is the classic |
| Strategy registry | `Map<Key, Strategy>`, or Spring injects a `List<Strategy>` you index by key | Pick by a runtime value without a `switch` |

**Example:**

```java
interface Promotion { long apply(long cartTotal); }

final class Checkout {
    private Promotion promotion = total -> total;           // default: no discount
    void setPromotion(Promotion promotion) { this.promotion = promotion; }
    long pay(long cartTotal) { return promotion.apply(cartTotal); }
}
// usage: checkout.setPromotion(total -> total * 90 / 100);           // 10% off
//        checkout.setPromotion(total -> Math.max(0, total - 200));   // flat 200 off
```

**Use cases:** discounts and promotions, payment methods, pricing rules, rate-limiting algorithms. In the JDK: the `Comparator` you pass to `list.sort(...)`.

**In this repo:** [A01_StrategyDesignPattern.java](3.%20Behavioral%20Patterns/A01_StrategyDesignPattern.java)

### 14. Template Method

**Definition:** A base class fixes the **order of steps** in one `final` method. Subclasses fill in only the steps that differ.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Abstract steps | Subclasses must implement them | The steps that always differ |
| Hook methods | The base gives a default (often empty); subclasses may override | Optional extras, e.g. gift wrap |
| Callback form | Pass the varying step as a lambda instead of subclassing | Spring `JdbcTemplate`, `TransactionTemplate` |

**Example:**

```java
abstract class OrderFulfilment {
    // The template: the order of steps is fixed and cannot be overridden.
    final void fulfil(String orderId) {
        validate(orderId);
        pack(orderId);
        if (needsGiftWrap()) System.out.println("gift wrapping " + orderId);
        ship(orderId);
    }

    void validate(String orderId) { System.out.println("validated " + orderId); } // shared step
    abstract void pack(String orderId);                                            // varies
    abstract void ship(String orderId);                                            // varies
    boolean needsGiftWrap() { return false; }                                      // hook
}

final class DigitalOrder extends OrderFulfilment {
    void pack(String orderId) { System.out.println("generating licence key"); }
    void ship(String orderId) { System.out.println("emailing download link"); }
}
// usage: new DigitalOrder().fulfil("ORD-7");
```

**Use cases:** order fulfilment, data-import pipelines (read → validate → transform → save), report generation. In the JDK: `HttpServlet.service()` calls your `doGet()` / `doPost()`, and `AbstractList` gives you a full `List` once you write `get()` and `size()`.

**In this repo:** [A02_TemplateDesignPattern.java](3.%20Behavioral%20Patterns/A02_TemplateDesignPattern.java)

### 15. Observer

**Definition:** When one object (the **subject**) changes, it automatically tells every registered **listener**. The subject knows only the listener interface, not who is listening.

**Variations:**

| Variation | How it works | Trade-off |
| --- | --- | --- |
| Push | The event carries the data: `backInStock(sku)` | Simple; listeners get what they need |
| Pull | The event says only "I changed"; listeners ask for details | Listeners pick what they need; more calls |
| Sync vs async | Call listeners on the same thread, or hand off to an executor / event bus | Sync: one slow listener blocks the rest. Async: ordering and error handling get harder |

**Example:**

```java
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

interface StockListener { void backInStock(String sku); }

final class StockItem {
    private final String sku;
    private int quantity = 0;
    // CopyOnWriteArrayList: safe even if a listener unsubscribes while we are notifying.
    private final List<StockListener> listeners = new CopyOnWriteArrayList<>();

    StockItem(String sku) { this.sku = sku; }
    void subscribe(StockListener listener)   { listeners.add(listener); }
    void unsubscribe(StockListener listener) { listeners.remove(listener); }

    void restock(int added) {
        boolean wasEmpty = quantity == 0;
        quantity += added;
        if (wasEmpty && quantity > 0) {                 // notify only on the 0 -> positive change
            for (StockListener listener : listeners) listener.backInStock(sku);
        }
    }
}
// usage: item.subscribe(sku -> System.out.println("Email user: " + sku + " is back"));
```

**Use cases:** back-in-stock or price-drop alerts, UI event listeners, cache invalidation. In Spring: `ApplicationEventPublisher` + `@EventListener`. Kafka pub/sub is the same idea across services.

**Interview note:** `java.util.Observable` has been deprecated since Java 9 — don't use it.

**In this repo:** [A03_ObserverDesignPattern.java](3.%20Behavioral%20Patterns/A03_ObserverDesignPattern.java)

### 16. Iterator

**Definition:** A way to go through a collection's elements one at a time without knowing how they are stored or fetched.

**Variations:**

| Variation | How it works | Example |
| --- | --- | --- |
| External | The caller drives with `hasNext()` / `next()`, and can stop early | `Iterator` |
| Internal | The collection drives; you pass the action | `list.forEach(x -> ...)` |
| Fail-fast | Throws `ConcurrentModificationException` if the collection changes mid-loop | `ArrayList`, `HashMap` |
| Weakly consistent (often called "fail-safe") | Never throws; may or may not show changes made during the loop | `ConcurrentHashMap`, `CopyOnWriteArrayList` |

**Example:**

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

// Hides paging: the caller sees one stream of items, never pages.
final class PagedIterator<T> implements Iterator<T> {
    private final IntFunction<List<T>> fetchPage;   // page number -> items; an empty list means "no more"
    private final Deque<T> buffer = new ArrayDeque<>();
    private int nextPage = 0;
    private boolean exhausted = false;

    PagedIterator(IntFunction<List<T>> fetchPage) { this.fetchPage = fetchPage; }

    @Override
    public boolean hasNext() {
        if (buffer.isEmpty() && !exhausted) {
            List<T> page = fetchPage.apply(nextPage++);
            if (page.isEmpty()) exhausted = true; else buffer.addAll(page);
        }
        return !buffer.isEmpty();
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return buffer.poll();
    }
}
// usage: new PagedIterator<>(page -> ordersApi.fetch(page, 100)).forEachRemaining(this::export);
```

**Use cases:** exporting from a paged API or database, walking a tree (in-order BST iterator). In the JDK: every `for (x : collection)` loop uses an `Iterator`; `Scanner` is one too.

**In this repo:** [B01_IteratorPattern.java](3.%20Behavioral%20Patterns/B01_IteratorPattern.java)

### 17. Command

**Definition:** Turn a request into an object. The object knows how to `execute()` the request — and often how to `undo()` it — so requests can be queued, logged, retried or undone.

**Variations:**

| Variation | How it works | Example |
| --- | --- | --- |
| Undoable command | `execute()` + `undo()`; remembers what it needs to reverse | Undo/redo stacks |
| Macro command | A command holding a list of commands; undo runs them in reverse | "Apply all", grouped edits |
| Queued command | Commands go on a queue and workers run them later | `executor.submit(runnable)`, job queues |

**Example:**

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

interface Command { void execute(); void undo(); }

final class Cart { final List<String> items = new ArrayList<>(); }

final class AddItem implements Command {
    private final Cart cart;
    private final String item;
    AddItem(Cart cart, String item) { this.cart = cart; this.item = item; }

    public void execute() { cart.items.add(item); }
    public void undo()    { cart.items.remove(item); }
}

final class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    void run(Command c) { c.execute(); undoStack.push(c); redoStack.clear(); } // new action clears redo
    void undo() { if (!undoStack.isEmpty()) { Command c = undoStack.pop(); c.undo(); redoStack.push(c); } }
    void redo() { if (!redoStack.isEmpty()) { Command c = redoStack.pop(); c.execute(); undoStack.push(c); } }
}
// usage: history.run(new AddItem(cart, "shoes")); history.undo(); history.redo();
```

**Use cases:** cart or editor undo/redo, menu and button actions, job queues, retrying failed operations. In the JDK: every `Runnable` / `Callable` you hand to an `ExecutorService` is a command.

**In this repo:** [B02_CommandDesignPattern.java](3.%20Behavioral%20Patterns/B02_CommandDesignPattern.java)

### 18. Memento

**Definition:** Save an object's internal state in a snapshot so it can be restored later, without exposing that state to anyone else. Three roles:

- **Originator** — the object whose state is saved.
- **Memento** — the sealed snapshot.
- **Caretaker** — stores snapshots but never looks inside them.

**Variations:**

| Variation | How it works | Trade-off |
| --- | --- | --- |
| Full snapshot | Copy the whole state every time | Simple; heavy when the state is big |
| Incremental (diffs) | Store only what changed | Less memory; restoring means replaying changes |

**Example:**

```java
import java.util.ArrayDeque;
import java.util.Deque;

final class Editor {                                   // Originator
    private String text = "";
    private int cursor = 0;

    void type(String s) {
        text = text.substring(0, cursor) + s + text.substring(cursor);
        cursor += s.length();
    }
    String text() { return text; }

    // Memento: immutable, and only Editor can read its private fields.
    static final class Snapshot {
        private final String text;
        private final int cursor;
        private Snapshot(String text, int cursor) { this.text = text; this.cursor = cursor; }
    }

    Snapshot save()              { return new Snapshot(text, cursor); }
    void restore(Snapshot s)     { this.text = s.text; this.cursor = s.cursor; }
}

final class History {                                  // Caretaker: stores, never looks inside
    private final Deque<Editor.Snapshot> stack = new ArrayDeque<>();
    void push(Editor.Snapshot s) { stack.push(s); }
    Editor.Snapshot pop()        { return stack.pop(); }
}
// usage: history.push(editor.save()); editor.type("oops"); editor.restore(history.pop());
```

**Use cases:** editor undo, game save points, "reset form to last saved", rolling back a config change.

**Memento vs Command for undo:** Memento stores **the old state**; Command stores **how to reverse the action**. Memento is simpler to write but uses more memory.

**In this repo:** [B03_MementoDesignPattern.java](3.%20Behavioral%20Patterns/B03_MementoDesignPattern.java)

### 19. State

**Definition:** Let an object change its behaviour when its internal state changes, by moving each state's rules into its own class or enum constant. The object hands each call to its current state instead of doing `switch (status)` in every method.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Class per state | Each state is a class and decides the next state | Rich rules per state; easy to add states |
| Enum state machine | Each enum constant overrides the methods | Few, fixed states — compact |
| Transition table | `Map<State, Map<Event, State>>` | Rules come from data or config |
| Who owns transitions | The states set the next state, or the main object does | States: logic stays local. Main object: all transitions visible in one place |

**Example:**

```java
enum OrderState {
    CREATED   { OrderState next() { return PAID; }      OrderState cancel() { return CANCELLED; } },
    PAID      { OrderState next() { return SHIPPED; }   OrderState cancel() { return CANCELLED; } },
    SHIPPED   { OrderState next() { return DELIVERED; } },
    DELIVERED { OrderState next() { throw new IllegalStateException("already delivered"); } },
    CANCELLED { OrderState next() { throw new IllegalStateException("order is cancelled"); } };

    abstract OrderState next();
    OrderState cancel() { throw new IllegalStateException("cannot cancel when " + this); }
}

final class Order {
    private OrderState state = OrderState.CREATED;
    void advance() { state = state.next(); }
    void cancel()  { state = state.cancel(); }
    OrderState state() { return state; }
}
// usage: order.advance();  // CREATED -> PAID
//        order.advance();  // PAID -> SHIPPED
//        order.cancel();   // throws: cannot cancel when SHIPPED
```

**Use cases:** order lifecycle, vending machine (idle → has coin → dispensing), traffic lights, document workflow (draft → review → published), payment status.

**In this repo:** [C01_StateDesignPattern.java](3.%20Behavioral%20Patterns/C01_StateDesignPattern.java) — uses the class-per-state variation.

### 20. Chain of Responsibility

**Definition:** Pass a request along a chain of handlers. Each handler decides to handle it, pass it on, or do part of it and pass the rest. The sender doesn't know which handler will deal with it.

**Variations:**

| Variation | How it works | Example |
| --- | --- | --- |
| First match stops | One handler deals with it and the chain ends | Approval limits: manager → director → VP |
| Partial handling | Each handler does its share and passes the remainder | ATM: 2000s, then 500s, then 100s |
| Filter pipeline | Every handler runs; any one can stop the request | Servlet filters, Spring Security filter chain |

**Example:**

```java
final class NoteDispenser {
    private final int note;
    private NoteDispenser next;
    NoteDispenser(int note) { this.note = note; }

    NoteDispenser then(NoteDispenser next) { this.next = next; return next; }

    void dispense(int amount) {
        int count = amount / note;
        if (count > 0) System.out.println(count + " x " + note);
        int rest = amount % note;
        if (rest > 0) {
            if (next == null) throw new IllegalArgumentException("cannot dispense " + rest);
            next.dispense(rest);
        }
    }
}
// usage: NoteDispenser atm = new NoteDispenser(2000);
//        atm.then(new NoteDispenser(500)).then(new NoteDispenser(100));
//        atm.dispense(3700);   // 1 x 2000, 3 x 500, 2 x 100
```

**Use cases:** ATM cash dispensing, approval workflows, request validation steps, logging handlers. In Java web apps: servlet `Filter` chains and Spring Security's `SecurityFilterChain`.

**In this repo:** [C02_ChainOfResponsibility.java](3.%20Behavioral%20Patterns/C02_ChainOfResponsibility.java)

### 21. Mediator

**Definition:** Objects talk to one central **mediator** instead of to each other. Many-to-many links become one-to-many: each object knows only the mediator.

**Variations:**

| Variation | How it works | Catch |
| --- | --- | --- |
| Classic | Peers call `mediator.send(...)`; the mediator routes | The mediator can grow into a "god object" |
| Event-bus style | Peers publish events; the mediator dispatches them | Looser coupling; starts to overlap with Observer |

**Example:**

```java
import java.util.ArrayList;
import java.util.List;

final class ChatRoom {                                 // Mediator
    private final List<User> users = new ArrayList<>();
    void join(User user) { users.add(user); }

    void broadcast(User from, String message) {
        for (User user : users) if (user != from) user.receive(from.name(), message);
    }
}

final class User {                                     // Peer: knows the room, never another user
    private final String name;
    private final ChatRoom room;
    User(String name, ChatRoom room) { this.name = name; this.room = room; }

    String name() { return name; }
    void send(String message) { room.broadcast(this, message); }
    void receive(String from, String message) { System.out.println(name + " <- " + from + ": " + message); }
}
// usage: ChatRoom room = new ChatRoom(); User a = new User("a", room); room.join(a); ... a.send("hi");
```

**Use cases:** chat rooms, an air-traffic control tower, a UI form where ticking a checkbox enables a text box, coordinating several services in a workflow.

**In this repo:** [C03_MediatorDesignPattern.java](3.%20Behavioral%20Patterns/C03_MediatorDesignPattern.java)

### 22. Visitor

**Definition:** Add new operations over a fixed set of types without changing those types. Each operation is one **visitor** class with one `visit` method per type. Each type has an `accept(visitor)` method that calls the right `visit` — this two-step call is called **double dispatch**.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Classic GoF | `accept(Visitor v) { return v.visit(this); }` | Any Java version |
| Sealed types + pattern-matching `switch` (Java 21+) | `switch (line) { case Box b -> ...; case Ebook e -> ...; }` | Same benefit, no `accept` boilerplate, and the compiler checks every type is covered |

**Trade-off:** adding a new **operation** is easy (one new visitor). Adding a new **type** is hard (every visitor must change).

**Example — classic:**

```java
interface CartLine { <R> R accept(CartVisitor<R> visitor); }

record Physical(long price, double weightKg) implements CartLine {
    public <R> R accept(CartVisitor<R> visitor) { return visitor.visit(this); }
}
record Digital(long price) implements CartLine {
    public <R> R accept(CartVisitor<R> visitor) { return visitor.visit(this); }
}

interface CartVisitor<R> {
    R visit(Physical line);
    R visit(Digital line);
}

// One operation = one visitor.
final class ShippingCost implements CartVisitor<Long> {
    public Long visit(Physical line) { return Math.round(line.weightKg() * 50); }
    public Long visit(Digital line)  { return 0L; }
}
// usage: long fee = line.accept(new ShippingCost());
```

**Example — modern Java 21+:**

```java
sealed interface Line permits Box, Ebook { }
record Box(long price, double weightKg) implements Line { }
record Ebook(long price) implements Line { }

final class Tax {
    static long of(Line line) {
        return switch (line) {            // compile error if a permitted type is missing
            case Box b   -> b.price() * 18 / 100;
            case Ebook e -> e.price() * 5 / 100;
        };
    }
}
// usage: long tax = Tax.of(new Box(1_000, 2.5));
```

**Use cases:** tax / shipping / loyalty points over cart lines, compilers walking a syntax tree, exporting a model to JSON or XML. In the JDK: `Files.walkFileTree(path, fileVisitor)`.

**In this repo:** [D01_VisitorDesignPattern.java](3.%20Behavioral%20Patterns/D01_VisitorDesignPattern.java)

### 23. Interpreter

**Definition:** Represent each rule of a small language as a class, build a tree of those objects for an expression, and evaluate the tree. It is Composite (pattern 10) applied to rules.

**Variations:**

| Variation | How it works | When to use |
| --- | --- | --- |
| Hand-built tree | Code creates the rule objects directly | Rules written by developers |
| Parsed tree | A small parser turns text like `total > 1000 AND category = ELECTRONICS` into the tree | Rules stored in a DB or edited by business users |
| Use a library | SpEL, MVEL, or a rules engine such as Drools | Anything beyond a tiny grammar |

**Example:**

```java
import java.util.Map;

interface Rule { boolean eval(Map<String, Object> ctx); }

record GreaterThan(String key, long value) implements Rule {          // leaf
    public boolean eval(Map<String, Object> ctx) { return ((Number) ctx.get(key)).longValue() > value; }
}
record EqualsTo(String key, Object value) implements Rule {           // leaf
    public boolean eval(Map<String, Object> ctx) { return value.equals(ctx.get(key)); }
}
record And(Rule left, Rule right) implements Rule {                   // composite
    public boolean eval(Map<String, Object> ctx) { return left.eval(ctx) && right.eval(ctx); }
}
record Or(Rule left, Rule right) implements Rule {                    // composite
    public boolean eval(Map<String, Object> ctx) { return left.eval(ctx) || right.eval(ctx); }
}
// usage: Rule coupon = new And(new GreaterThan("total", 1000), new EqualsTo("category", "ELECTRONICS"));
//        coupon.eval(Map.of("total", 1500L, "category", "ELECTRONICS"));   // true
```

**Use cases:** coupon eligibility, feature-flag targeting ("country = IN and app version > 5"), calculators, search filters. Real examples: regular-expression engines, Spring Expression Language (`#{...}`).

**In this repo:** [D02_InterpreterPattern.java](3.%20Behavioral%20Patterns/D02_InterpreterPattern.java)

---

## Confusable pairs

These are the comparisons interviewers ask most, because the patterns look alike in code.

| Pair | The difference |
| --- | --- |
| Strategy vs State | Strategy: the **caller** picks the algorithm, and it rarely changes. State: the **object switches itself** as events happen. |
| Strategy vs Template Method | Strategy swaps the **whole** algorithm, using composition. Template Method varies **some steps**, using inheritance. |
| Decorator vs Proxy | Same structure. Decorator **adds behaviour**, and the caller builds the stack. Proxy **controls access**, and the caller usually doesn't know it's there. |
| Adapter vs Decorator vs Proxy | Adapter **changes** the interface. Decorator **keeps** it and adds behaviour. Proxy **keeps** it and controls access. |
| Adapter vs Bridge | Adapter fixes a mismatch **after the fact**. Bridge is designed **up front** so two dimensions can vary. |
| Factory Method vs Abstract Factory | Factory Method: **one** product, chosen by a subclass. Abstract Factory: a **family** of products, from one object. |
| Factory vs Builder | Factory decides **which class** to create. Builder decides **how to assemble** one complex object. |
| Facade vs Mediator | Facade: an outside caller → the subsystems, one way; the subsystems don't know it exists. Mediator: peers ↔ mediator, both ways; every peer knows it. |
| Observer vs Mediator | Observer: a subject **broadcasts** one way. Mediator: a centre **coordinates** two-way talk. |
| Command vs Memento | Both give undo. Command remembers **how to reverse** the action. Memento stores **the old state**. |
| Composite vs Decorator | Both wrap the same interface. Composite holds **many** children and combines them. Decorator holds **one** and adds to it. |

## Spot the pattern from the symptom

| You see this in code | Reach for |
| --- | --- |
| A long `if/else` on a type to choose **behaviour** | Strategy |
| A long `if/else` on a type to choose **which class to `new`** | Factory |
| A constructor with many parameters, most of them optional | Builder |
| A `status` field and a `switch (status)` in every method | State |
| A third-party API that doesn't match your interface | Adapter |
| Retry, logging or caching needed around a call you can't change | Decorator or Proxy |
| One use case that must call several services in order | Facade |
| Undo / redo | Command or Memento |
| Other parts of the system must react when something changes | Observer |
| A request that must pass several checks in sequence | Chain of Responsibility |
| Many objects all referencing each other | Mediator |
| The same steps everywhere, with a few that differ by type | Template Method |
| Class count growing as N × M combinations | Bridge |
| A tree of parts and wholes that should be handled alike | Composite |
| The same heavy data repeated across millions of objects | Flyweight |
| New operations keep being added over a fixed set of types | Visitor |
| Business rules written as small expressions | Interpreter |
