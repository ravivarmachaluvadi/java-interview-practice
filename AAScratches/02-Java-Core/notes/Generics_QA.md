# Generics Q&A

Interview notes on Java generics: type erasure, overloading and overriding with generic types, and the three kinds of bounded types.

## 1. Can you overload methods based only on generic type parameters?

No. Because generics are erased at runtime.

Example:

```java
void process(List<String> list) { }
void process(List<Integer> list) { } // compile-time error
```

Erased signatures become:

```java
void process(List list)
```

## 2. Why does overriding `List<Number> m()` with `ArrayList<Integer> m()` fail to compile?

```java
class A {
    List<Number> m() { return null; }
}
class B extends A {
    // not because of ArrayList but Integer ArrayList is covariant return type
    @Override
    ArrayList<Integer> m() { return null; }  // 'm()' in 'B' clashes with 'm()' in 'A'; incompatible return type
}
```

## 3. The 3 generic bounded types in Java

### 1. Upper bounded type (`extends`)

Uses the keyword `extends` (even for interfaces).

Syntax:

```java
<T extends Number>
<? extends Number>
```

Meaning: `T` can be `Number` or any subclass (`Integer`, `Double`, etc.). Used when the type is a **producer** (PECS rule).

Example:

```java
List<? extends Number> nums; // Can read as Number
```

### 2. Lower bounded type (`super`)

Uses the keyword `super`.

Syntax:

```java
<? super Integer>
```

Meaning: `T` can be `Integer` or any superclass of `Integer` (`Number`, `Object`). Used when the type is a **consumer**.

Example:

```java
List<? super Integer> ints; // Can insert Integer
```

### 3. Unbounded type (`?`)

Syntax:

```java
List<?>
```

Meaning: the type is unknown; accepts any type. You cannot add anything except `null`.

Example:

```java
void print(List<?> list) {
    for (Object o : list) System.out.println(o);
}
```

### Summary

| Bound Type | Syntax | Meaning | Can Add? | Can Read? |
| --- | --- | --- | --- | --- |
| **Upper Bound** | `<? extends T>` | Any subtype of T | No | Yes, as T |
| **Lower Bound** | `<? super T>` | Any supertype of T | Yes, T or subtype | Yes, as Object |
| **Unbounded** | `<?>` | Unknown type | No | Yes, as Object |

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
