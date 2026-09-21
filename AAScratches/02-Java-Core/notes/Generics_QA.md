# Generics Q&A

Interview notes on Java generics: type erasure and its consequences, overloading and overriding with generic types, and the three kinds of bounded types.

Every "this compiles" or "this fails" claim below was checked by running `javac` on Temurin JDK 21.0.12. The error text is the real compiler output, not a paraphrase.

## Contents

| # | Question |
| --- | --- |
| 1 | [Can you overload methods based only on generic type parameters?](#1-can-you-overload-methods-based-only-on-generic-type-parameters) |
| 2 | [Why does overriding `List<Number> m()` with `ArrayList<Integer> m()` fail?](#2-why-does-overriding-listnumber-m-with-arraylistinteger-m-fail) |
| 3 | [What else does erasure make impossible?](#3-what-else-does-erasure-make-impossible) |
| 4 | [The 3 generic bounded types in Java](#4-the-3-generic-bounded-types-in-java) |
| 5 | [PECS in practice](#5-pecs-in-practice) |

## 1. Can you overload methods based only on generic type parameters?

No.

```java
void process(List<String> list) { }
void process(List<Integer> list) { } // compile-time error
```

Real compiler output:

```text
error: name clash: process(List<Integer>) and process(List<String>) have the same erasure
```

Both methods erase to the same signature, so the class file would contain two identical methods:

```java
void process(List list)
```

> **Precision fix:** the original note said generics are *"erased at runtime"*.
> They are erased **at compile time** — `javac` performs the erasure and writes
> the erased signature into the `.class` file. Nothing is erased while the program
> runs; by then the type argument was never there to begin with. This is why the
> failure above is a **compile error**, not a runtime one. Saying "erased at
> runtime" invites the follow-up "then why does it not compile?", and there is no
> good answer from that position.

The mental model to keep: **the compiler checks types, then throws the checking apparatus away.** Generic type information exists only for `javac`; the JVM sees raw types.

Two exceptions worth knowing, because interviewers use them to test whether "erasure means generics are gone at runtime" is being repeated without understanding:

- **Generic type arguments in class and method signatures are retained as metadata** for reflection. `getGenericSuperclass()` and `getGenericParameterType()` can read `List<String>` from a *declared* field, parameter or supertype. What is erased is the type argument of an *instance*.
- That loophole is what makes the "super type token" trick work — `new TypeReference<List<String>>() {}` in Jackson, and Guava's `TypeToken`. They capture the type argument in an anonymous subclass's signature, where it survives.

## 2. Why does overriding `List<Number> m()` with `ArrayList<Integer> m()` fail?

```java
class A {
    List<Number> m() { return null; }
}
class B extends A {
    @Override
    ArrayList<Integer> m() { return null; }
}
```

Real compiler output:

```text
error: m() in B cannot override m() in A
  return type ArrayList<Integer> is not compatible with List<Number>
error: method does not override or implement a method from a supertype
```

> **Correction:** the original note's inline comment read *"not because of
> ArrayList but Integer ArrayList is covariant return type"*, which garbles the
> rule and names the wrong culprit. Two separate things are going on, and only
> one of them is the problem.

**Covariant return types are fine.** Since Java 5 an override may return a *subtype*. `ArrayList` is a subtype of `List`, so narrowing the container class is legal — proven by compiling and running it:

```java
class A { List<Number> m() { return null; } }
class B extends A {
    @Override
    ArrayList<Number> m() { return new ArrayList<>(); }   // compiles, runs
}
```

```text
covariant ArrayList<Number> override OK: []
```

**Generics are invariant.** That is the real failure. `Integer` is a subtype of `Number`, but `ArrayList<Integer>` is **not** a subtype of `List<Number>`. Changing `ArrayList` to `List` would not help; changing `Integer` to `Number` fixes it.

So the rule in one line: **you may narrow the class, you may not change the type argument.**

Why invariance exists at all — if `List<Integer>` were a `List<Number>`, this would compile:

```java
List<Number> nums = someListOfInteger;   // hypothetical, does not compile
nums.add(3.14);                          // a Double, into a List<Integer>
```

Arrays made exactly that mistake and are covariant, so the equivalent code compiles and blows up at runtime instead. Verified:

```java
Object[] arr = new String[1];
arr[0] = 42;        // ArrayStoreException at runtime
```

```text
ArrayStoreException (arrays covariant)
```

Generics moved that failure from runtime to compile time. That is the whole point of invariance, and it is the answer interviewers want.

## 3. What else does erasure make impossible?

Each row was compiled to confirm it really is an error, and the compiler's own message is quoted.

| You cannot | Example | Compiler says |
| --- | --- | --- |
| Create a generic array | `new T[10]` | `generic array creation` |
| Create a parameterised array | `new List<String>[5]` | `generic array creation` |
| `instanceof` an unprovable generic type | `o instanceof List<String>` where `o` is `Object` | `Object cannot be safely cast to List<String>` |
| Overload on type arguments alone | question 1 | `have the same erasure` |
| Use a type parameter in a static context | `static T field;` | `non-static type variable T cannot be referenced from a static context` |
| Catch a generic exception | `catch (T e)` | `unexpected type — required: class, found: type parameter T` |
| Have a generic type extend `Throwable` | `class E<T> extends Exception` | `a generic class may not extend java.lang.Throwable` |
| Write a lower-bounded type parameter | `class C<T super Integer>` | `> expected` — the syntax does not exist |

One nuance the table above deliberately captures, because it is a trap in both directions: **`instanceof` with a generic type is legal when the compiler can prove it.** Since Java 16 this compiles and prints `true`:

```java
ArrayList<String> ls = new ArrayList<>();
System.out.println(ls instanceof List<String>);   // true
```

It fails only when the static type makes the test unprovable:

```java
Object o = ls;
System.out.println(o instanceof List<String>);    // error
```

```text
error: Object cannot be safely cast to List<String>
```

So "you can never use generics with `instanceof`" is wrong. The accurate statement is **the cast must be statically provable**; otherwise the check would have to happen at runtime, where the type argument no longer exists.

### Heap pollution

Erasure means an unchecked write can put the wrong type into a collection, and the `ClassCastException` surfaces somewhere else entirely — at the **read**, not the write:

```java
List<String> ls = new ArrayList<>();
List raw = ls;          // raw type, unchecked
raw.add(42);            // no error here
String s = ls.get(0);   // ClassCastException here
```

```text
ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String
```

This is why the compiler emits *unchecked* warnings and why `@SuppressWarnings("unchecked")` should be applied to the narrowest possible scope. The stack trace points at innocent code — the actual bug is wherever the raw type crept in, often many frames away.

`@SafeVarargs` exists for the same reason: a generic varargs parameter is really an array, and arrays cannot hold type arguments safely.

## 4. The 3 generic bounded types in Java

### 1. Upper bounded (`extends`)

Uses the keyword `extends` — for interfaces too; there is no `implements` in a bound.

```java
<T extends Number>
<? extends Number>
```

`T` can be `Number` or any subtype (`Integer`, `Double`). Used when the structure is a **producer** — you read from it.

```java
List<? extends Number> nums;   // read as Number
```

A type parameter can have several bounds with `&`: `<T extends Number & Comparable<T>>`. At most one may be a class, and it must be listed first.

### 2. Lower bounded (`super`)

```java
<? super Integer>
```

`T` can be `Integer` or any supertype (`Number`, `Object`). Used when the structure is a **consumer** — you write into it.

```java
List<? super Integer> ints;    // can insert Integer
```

`super` is only legal on a wildcard. `<T super Integer>` on a type parameter does not exist in Java.

### 3. Unbounded (`?`)

```java
List<?>
```

The type is unknown. You can read elements as `Object` and you cannot add anything except `null`.

```java
void print(List<?> list) {
    for (Object o : list) System.out.println(o);
}
```

`List<?>` and `List<Object>` are not the same. `List<?>` accepts a `List<String>`; `List<Object>` does not — verified:

```text
error: incompatible types: ArrayList<String> cannot be converted to List<Object>
```

And raw `List` is different again: it disables generic checking altogether rather than restricting it.

### Summary

| Bound | Syntax | Means | Can add? | Can read as? |
| --- | --- | --- | --- | --- |
| Upper | `<? extends T>` | Some unknown subtype of T | Only `null` | `T` |
| Lower | `<? super T>` | Some unknown supertype of T | `T` or subtype | `Object` |
| Unbounded | `<?>` | Some unknown type | Only `null` | `Object` |

The "Only `null`" cells are the ones to be able to justify. `List<? extends Number>` might really be a `List<Integer>`, so the compiler cannot let you add a `Double` — it does not know which subtype it is. `null` is the sole value that is a member of every reference type.

Here are the four restrictions with real compiler output:

```java
List<? extends Number> prod = new ArrayList<Integer>();
prod.add(1);                                // error
List<?> unk = new ArrayList<String>();
unk.add("x");                               // error
unk.add(null);                              // OK
List<? super Integer> cons = new ArrayList<Number>();
Integer i = cons.get(0);                    // error
```

```text
error: incompatible types: int cannot be converted to CAP#1
  where CAP#1 is a fresh type-variable:
    CAP#1 extends Number from capture of ? extends Number
error: incompatible types: String cannot be converted to CAP#1
error: incompatible types: CAP#1 cannot be converted to Integer
  where CAP#1 is a fresh type-variable:
    CAP#1 extends Object super: Integer from capture of ? super Integer
```

`CAP#1` in those messages is **capture conversion** — the compiler invents a fresh, unnameable type to stand for "the one specific type this wildcard is". Recognising `CAP#1` in an error message and knowing it means "the compiler does not know which type, only that one exists" is a strong signal in an interview.

## 5. PECS in practice

**Producer Extends, Consumer Super.** If a parameter *produces* values you read, use `extends`. If it *consumes* values you write, use `super`. If it does both, use neither — an exact type.

Verified, compiled and run:

```java
import java.util.*;

public class Pecs {

    // PRODUCER - you read Numbers out of it
    static double sum(List<? extends Number> src) {
        double total = 0;
        for (Number n : src) total += n.doubleValue();
        return total;
    }

    // CONSUMER - you write Integers into it
    static void fill(List<? super Integer> dst) {
        for (int i = 1; i <= 3; i++) dst.add(i);
    }

    public static void main(String[] args) {
        System.out.println(sum(List.of(1, 2, 3)));      // 6.0
        System.out.println(sum(List.of(1.5, 2.5)));     // 4.0

        List<Number> nums = new ArrayList<>();
        fill(nums);
        System.out.println(nums);                        // [1, 2, 3]
    }
}
```

```text
6.0
4.0
[1, 2, 3]
```

Without `? extends Number` on `sum`, the `List<Double>` call would not compile — invariance again. That is the practical payoff of wildcards: they are how you get flexibility back after invariance takes it away.

The JDK's own signatures are the best worked example:

| Method | Signature | Why |
| --- | --- | --- |
| `Collections.copy` | `copy(List<? super T> dest, List<? extends T> src)` | dest consumes, src produces |
| `Collections.addAll` | `addAll(Collection<? super T> c, T... e)` | `c` consumes |
| `Stream.map` | `map(Function<? super T, ? extends R> f)` | takes a T, produces an R |
| `Comparator.comparing` | `comparing(Function<? super T, ? extends U> k)` | same shape |

`Stream.map` is the one to be able to read aloud: the function **consumes** `T` so it is `? super T`, and **produces** `R` so it is `? extends R`. Once PECS clicks, every generic signature in the JDK becomes readable.

### Example: reading a `List<?>`

```java
import java.util.ArrayList;
import java.util.List;

class Scratch {
    public static void main(String[] args) {

        List<String> list2 = new ArrayList<>();
        list2.add("Ravi");
        list2.add("Varma");
        readList(list2);
    }

    private static void readList(List<?> list2) {
        list2.forEach(System.out::println);
    }
}
```

`List<?>` is the right choice here because `readList` only reads. If it needed to add a `String`, the parameter would have to be `List<? super String>` — or just `List<String>`.
