/*
 * =====================================================================
 *  Static generic methods and type inference    Java language | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A generic method declares its own type parameter before the return type,
 *   as in "static <T> T echo(T value)". The class itself does not have to be
 *   generic, and the caller never has to spell T out - javac infers it from
 *   the arguments.
 *
 * WHAT YOU WILL SEE
 *   echo(123)            -> 123          T inferred as Integer (autoboxed)
 *   echo("I am string")  -> I am string  T inferred as String
 *   echo(25.89)          -> 25.89        T inferred as Double
 *   larger("apple", "pear")  -> pear     bounded T extends Comparable<T>
 *   Runtime class of T inside the method: Integer, String, Double - the VALUE
 *   still knows its class, even though T itself is erased.
 *
 *   Fixed: the original used new Integer(123) and new Double(25.89). Those
 *   constructors are deprecated for removal since Java 9; autoboxing or
 *   valueOf() is the modern form and it caches small values.
 *
 * HOW IT WORKS
 *   1. <T> before the return type introduces the type parameter.
 *   2. javac infers T at the call site from the static type of the argument.
 *   3. After compilation T is ERASED to its bound (Object here), so there is
 *      one method in the bytecode, not one per type.
 *   4. Erasure removes the type ARGUMENT, not the object: value.getClass()
 *      still reports Integer, because the object carries its own class.
 *   5. larger() shows a bound: <T extends Comparable<T>> is what lets the body
 *      call compareTo at all.
 *
 * KEY INSIGHT
 *   Generics are a compile-time contract. They buy type safety and remove
 *   casts at the call site; at run time the type argument is gone. Any time
 *   you need T at run time you must pass a Class<T> token or capture it in a
 *   subclass - which is exactly the super-type-token trick.
 *
 * GOTCHAS
 *   - You cannot write new T(), new T[10], or T.class inside the method.
 *   - instanceof List<String> does not compile; only List<?> does.
 *   - A generic method can be called explicitly: NonGenericClass.<String>echo("x").
 *
 * INTERVIEW FOLLOW-UPS
 *   - What is type erasure, and why did Java choose it over reified generics?
 *   - When do you need <T extends Comparable<? super T>> instead of Comparable<T>?
 *   - Explain PECS: when is List<? extends T> right, and when List<? super T>?
 *   - How do you get the real type argument back at run time?
 *
 * RUN
 *   main() runs 4 cases (Integer, String, Double, bounded comparison) and
 *   prints actual vs expected.
 */
class NonGenericClass {

    /** <T> is declared by the METHOD, so the enclosing class need not be generic. */
    static <T> T echo(T value) {
        T copy = value;   // T behaves like Object at run time, but is type-checked here
        return copy;
    }

    /** The runtime class of the value survives erasure; the type argument does not. */
    static <T> String runtimeClassOf(T value) {
        return value.getClass().getSimpleName();
    }

    /** A bound is what makes a method call on T legal inside the body. */
    static <T extends Comparable<T>> T larger(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }
}

class GenericsInJava {

    public static void main(String[] args) {
        print("case 1: Integer argument", NonGenericClass.echo(123), 123);
        print("case 1: runtime class", NonGenericClass.runtimeClassOf(123), "Integer");

        print("case 2: String argument", NonGenericClass.echo("I am string"), "I am string");
        print("case 2: runtime class", NonGenericClass.runtimeClassOf("I am string"), "String");

        print("case 3: Double argument", NonGenericClass.echo(25.89), 25.89);
        print("case 3: runtime class", NonGenericClass.runtimeClassOf(25.89), "Double");

        // Edge case: a bounded type parameter, and an explicit type argument at the call site.
        print("case 4: larger of two strings", NonGenericClass.larger("apple", "pear"), "pear");
        print("case 4: explicit type argument", NonGenericClass.<String>echo("explicit"),
                "explicit");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
