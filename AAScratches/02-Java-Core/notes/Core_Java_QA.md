# Core Java Q&A

Interview notes on core Java internals: how the JVM loads classes, and the different ways an object can be created.

Verified against Temurin JDK 21.0.12 — the outputs below are copied from real runs, not from memory.

## Contents

| # | Question |
| --- | --- |
| 1 | [What is a Class Loader?](#1-what-is-a-class-loader) |
| 2 | [What is the class loader hierarchy?](#2-what-is-the-class-loader-hierarchy) |
| 3 | [What is the parent delegation model?](#3-what-is-the-parent-delegation-model) |
| 4 | [In how many ways can we create an object in Java?](#4-in-how-many-ways-can-we-create-an-object-in-java) |

## 1. What is a Class Loader?

A `ClassLoader` is the part of the JVM that finds a class's bytecode and defines it as a runtime `Class` object, on demand, the first time the class is actively used.

You never load classes manually — Java does it automatically and lazily.

"Lazily" is worth being precise about in an interview: loading is triggered by *active use* (first `new`, first static field read or write, first static method call, or reflection). Merely declaring a variable of a type does not load it.

## 2. What is the class loader hierarchy?

> **Correction — this is the single most out-of-date answer in most people's notes.**
> The **Extension ClassLoader** and the `$JAVA_HOME/jre/lib/ext` directory were
> **removed in Java 9** by the module system (JEP 220). `rt.jar` was removed at
> the same time — there is no `jre/lib/rt.jar` and no `jre` directory at all in a
> modern JDK. If you say "Bootstrap, Extension, Application" in an interview for a
> Java 17 or 21 role, you are describing Java 8.

Modern Java (9 and later) has three built-in loaders:

| Loader | Loads | Source | `getClassLoader()` returns |
| --- | --- | --- | --- |
| Bootstrap | Core platform classes (`java.base` etc.) | Runtime image (`jrt:/`) | `null` |
| Platform | Remaining JDK modules (`java.sql`, `java.xml.crypto`) | Runtime image | `ClassLoaders$PlatformClassLoader` |
| System / Application | Your application and its dependencies | Class path, module path | `ClassLoaders$AppClassLoader` |
| Custom | Whatever you implement | Anything (DB, network, bytes) | Your subclass |

Two naming points that trip people up:

- **System** and **Application** class loader are the same thing — `ClassLoader.getSystemClassLoader()` returns the application loader.
- **Bootstrap is written in native code**, so it has no Java object. Asking a core class for its loader returns `null`, not an object. `null` means bootstrap; it does not mean "no loader".

### Verified output on JDK 21

```java
public class ClassLoaderDemo {
    public static void main(String[] args) {
        System.out.println("String  loader: " + String.class.getClassLoader());
        System.out.println("app     loader: " + ClassLoaderDemo.class.getClassLoader());
        ClassLoader sys = ClassLoader.getSystemClassLoader();
        System.out.println("system  : " + sys);
        System.out.println("parent  : " + sys.getParent());
        System.out.println("gparent : " + sys.getParent().getParent());
        System.out.println("platform: " + ClassLoader.getPlatformClassLoader());
    }
}
```

```text
String  loader: null
app     loader: jdk.internal.loader.ClassLoaders$AppClassLoader@105be200
system  : jdk.internal.loader.ClassLoaders$AppClassLoader@105be200
parent  : jdk.internal.loader.ClassLoaders$PlatformClassLoader@7ad041f3
gparent : null
platform: jdk.internal.loader.ClassLoaders$PlatformClassLoader@7ad041f3
```

The chain is `App -> Platform -> null (bootstrap)`. There is no fourth link where "Extension" used to sit.

A good follow-up to be ready for — *which loader owns which JDK class?* It depends on the module, and it is easy to check:

| Class | Module | Loader |
| --- | --- | --- |
| `java.lang.String` | `java.base` | Bootstrap (`null`) |
| `javax.xml.catalog.Catalog` | `java.xml` | Bootstrap (`null`) |
| `javax.sql.DataSource` | `java.sql` | Platform |
| Your `Main` | unnamed / your module | Application |

### What changed in Java 9

| Java 8 | Java 9+ |
| --- | --- |
| Bootstrap, Extension, Application | Bootstrap, Platform, System (Application) |
| `$JAVA_HOME/jre/lib/rt.jar` | Runtime image, `jrt:/` — no `rt.jar`, no `jre/` |
| `$JAVA_HOME/jre/lib/ext` + `java.ext.dirs` | Removed; upgrade with `--upgrade-module-path` |
| `-Xbootclasspath/p:` to prepend | Removed; use `--patch-module` |

## 3. What is the parent delegation model?

When asked for a class, a loader **delegates upward first**: it asks its parent, which asks its parent, up to bootstrap. Only if every ancestor fails does the loader try to find the class itself.

Why it exists:

- **Security.** You cannot replace `java.lang.String` with your own version by putting it on the class path — bootstrap always wins, because it is asked first.
- **Uniqueness.** A class's runtime identity is `(name, defining loader)`. Delegation keeps core types loaded once, so `String` from two places is still the same `Class`.

That identity rule is the source of the classic puzzle: **the same `.class` file loaded by two different loaders produces two incompatible types**, and assigning one to the other throws `ClassCastException` even though the fully-qualified names match. This is why app servers and plugin systems hit "`Foo` cannot be cast to `Foo`".

Delegation is a convention, not a law — it is implemented in `ClassLoader.loadClass`, and containers such as Tomcat and OSGi deliberately override it to search locally first so a web app can ship its own version of a library.

## 4. In how many ways can we create an object in Java?

Interviewers usually want five or six. Be aware that the usual list mixes two different things: **JVM mechanisms** that actually produce an instance, and **API styles** that are just a wrapper over one of those mechanisms. Saying so is what separates a good answer from a memorised one.

| # | Way | Underlying mechanism | Runs a constructor? |
| --- | --- | --- | --- |
| 1 | `new` | `new` bytecode | Yes |
| 2 | Reflection (`Constructor.newInstance`) | `new` + invoke, via reflection | Yes |
| 3 | `clone()` | Native memory copy | **No** |
| 4 | Deserialization | Native allocation | **No** (for the `Serializable` part) |
| 5 | Factory / builder methods | Wrapper over `new` (may return a cached instance) | Usually |
| 6 | Library / framework (Jackson, Gson, Kryo) | Reflection, or `Unsafe.allocateInstance` | Depends |

Only **three** are genuinely distinct at the JVM level: `new` (including reflection, which ends in the same bytecode), `clone`, and allocation without a constructor (deserialization, `Unsafe`).

### 1. Using the `new` keyword (most common)

```java
MyClass obj = new MyClass();
```

### 2. Using reflection

```java
MyClass obj = (MyClass) Class.forName("MyClass").newInstance();   // deprecated since Java 9
```

`Class.newInstance()` is deprecated because it **propagates checked exceptions thrown by the constructor without declaring them**, defeating compile-time exception checking. Use:

```java
MyClass obj = MyClass.class.getDeclaredConstructor().newInstance();
```

`Constructor.newInstance()` wraps any constructor exception in `InvocationTargetException`, which is the behaviour you want.

Note `Class.forName("MyClass")` only works with an unqualified name if the class really is in the default package; normally you pass the fully-qualified name.

### 3. Using the `Constructor` class from Reflection

```java
Constructor<MyClass> constructor = MyClass.class.getConstructor(int.class);
MyClass obj = constructor.newInstance(7);
```

`getConstructor` finds only `public` constructors; `getDeclaredConstructor` finds any, and needs `setAccessible(true)` for non-public ones.

### 4. Using the `clone()` method

The class must implement `Cloneable` and override `clone()`. Without `Cloneable`, `super.clone()` throws `CloneNotSupportedException`.

```java
MyClass obj2 = (MyClass) obj1.clone();
```

`Object.clone()` is a **shallow** copy — object fields are shared, not duplicated — and **no constructor runs**, so any invariant enforced in the constructor is bypassed.

### 5. Using Deserialization

The object is created without calling its constructor.

```java
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("file.ser"));
MyClass obj = (MyClass) ois.readObject();
```

Precisely: the no-arg constructor of the **first non-serializable superclass** runs; constructors of the serializable classes themselves do not. This is why deserialization can produce objects in states a constructor would have rejected, and is the root of a whole class of security bugs.

### 6. Using Factory Methods

```java
Number n = Integer.valueOf(10);
MyClass obj = MyClass.create();
```

A factory is not a separate JVM mechanism, but it can avoid allocation entirely: `Integer.valueOf` returns a cached instance for `-128..127`, which is why `Integer.valueOf(127) == Integer.valueOf(127)` is `true` and `Integer.valueOf(128) == Integer.valueOf(128)` is `false`.

### 7. Using frameworks and copying libraries

Jackson, Gson and Kryo create objects internally — usually by reflection, and sometimes via `sun.misc.Unsafe.allocateInstance`, which allocates without running any constructor at all.

```java
MyClass obj = new Gson().fromJson(json, MyClass.class);
```

### Also worth mentioning

These produce objects without an explicit `new` in your source, and are easy marks in an interview:

- **String literals** — `"abc"` comes from the string pool; no allocation on repeat.
- **Autoboxing** — `Integer i = 5;` compiles to `Integer.valueOf(5)`.
- **Array creation** — `new int[10]` creates an object (arrays are objects).
- **Lambdas and method references** — an instance is spun up at runtime by `invokedynamic`.
- **String concatenation** — builds a new `String` each time it is evaluated.
