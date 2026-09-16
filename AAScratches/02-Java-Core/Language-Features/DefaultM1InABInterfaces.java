/**
 * Demonstrates how to resolve a default method conflict in Java 8+ interfaces.
 *
 * The class implements three interfaces: A, B and C. Both A and B provide
 * a default implementation of m1(), causing a compile‑time conflict.
 * The concrete class overrides m1() and explicitly delegates to A's
 * default method using the syntax A.super.m1().
 *
 * It also calls C's default method m2() without conflict because only
 * one interface defines it.
 *
 * Approach:
 * 1. Override the conflicting method in the implementing class.
 * 2. Use InterfaceName.super.method() to invoke a specific default implementation.
 * 3. Call other non‑conflicting default methods directly.
 *
 * Time Complexity: O(1) – each method call performs a constant amount of work.
 * Space Complexity: O(1) – no additional data structures are used.
 */

class DefaultM1InABInterfaces implements A, B, C {
    public static void main(String[] args) {

        DefaultM1InABInterfaces sc = new DefaultM1InABInterfaces();
        sc.m1();

        sc.m2();

    }

    @Override
    public void m1() {
        A.super.m1();
    }
}

interface A {
    default void m1() {
        System.out.println("A m1");
    }
}

interface B {
    default void m1() {
        System.out.println("B m1");
    }
}

interface C {
    default void m2() {
        System.out.println("C m2");
    }
}