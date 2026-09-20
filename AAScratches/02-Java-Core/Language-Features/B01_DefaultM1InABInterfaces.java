/*
 * =====================================================================
 *  Default method diamond: A.super.m1()          Java Core | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Since Java 8 an interface may ship a default method body. If a class
 *   implements two interfaces that BOTH provide a default m1(), the compiler
 *   refuses to guess which one you meant - the class must override m1() and,
 *   if it wants one of the inherited bodies, call it as A.super.m1().
 *
 * WHAT YOU WILL SEE
 *   DefaultM1InABInterfaces implements A, B, C
 *     m1()  -> "A m1"   the override delegates with A.super.m1()
 *     m2()  -> "C m2"   only C declares it, so no conflict, no override needed
 *   PicksB implements A, B
 *     m1()  -> "B m1"   same conflict, resolved the other way
 *   MostSpecificWins implements A, D   (D extends A and redeclares m1)
 *     m1()  -> "D m1"   NOT a conflict: D is more specific than A, so D wins
 *
 * HOW IT WORKS
 *   1. The compiler collects every default m1() reachable from the class.
 *   2. It drops any interface that a more specific interface already overrides
 *      (that is the MostSpecificWins case: D extends A, so A's body is out).
 *   3. If two unrelated candidates survive, that is an error the class must
 *      resolve by overriding m1() itself.
 *   4. Inside that override, <Interface>.super.<method>() names exactly one body.
 *      The named interface must be a DIRECT superinterface of this class.
 *
 * KEY INSIGHT
 *   Java has no multiple inheritance of state, but default methods give it
 *   multiple inheritance of behaviour - so it needs one tie-break rule.
 *   The rule, in order: a class body beats any interface; a more specific
 *   interface beats a less specific one; anything still tied is a compile error
 *   you settle with A.super.m1(). Recite it in that order.
 *
 * GOTCHAS
 *   - Delete the override below and the file stops compiling:
 *     "class DefaultM1InABInterfaces inherits unrelated defaults for m1()".
 *   - A.super.m1() is legal only inside the class that directly implements A,
 *     and only for a default method - not for an abstract or static one.
 *   - Interface static methods are never inherited, so they never collide.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if a superclass also declares m1()? (class always wins over interface)
 *   - Can a default method be made abstract again by a subinterface? (yes:
 *     redeclare it with no body, and implementers must supply one)
 *   - Why were default methods added at all? (to add stream() to Collection
 *     without breaking every existing implementation)
 *   - Difference between a default method and an abstract class? (no fields,
 *     no constructor, no state - and a class can have many interfaces)
 *
 * RUN
 *   main() runs 3 cases (the conflict resolved to A, resolved to B, and the
 *   more-specific-interface case) and prints actual vs expected.
 */

class DefaultM1InABInterfaces implements A, B, C {

    // Without this override the class does not compile: A and B both offer m1().
    @Override
    public String m1() {
        return A.super.m1();   // name exactly one inherited body
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        DefaultM1InABInterfaces picksA = new DefaultM1InABInterfaces();

        // Typical: conflict resolved in favour of A.
        print("A.super.m1()            : ", picksA.m1(), "A m1");

        // No conflict at all - only C declares m2(), so it is inherited as is.
        print("inherited m2()          : ", picksA.m2(), "C m2");

        // Same diamond, opposite choice: nothing about A makes it the default.
        print("B.super.m1()            : ", new PicksB().m1(), "B m1");

        // Edge: D extends A and redeclares m1, so D is more specific and wins
        // automatically - this class needs no override.
        print("most specific wins      : ", new MostSpecificWins().m1(), "D m1");
    }
}

/** Same A+B diamond, resolved the other way, to show the choice is arbitrary. */
class PicksB implements A, B {
    @Override
    public String m1() {
        return B.super.m1();
    }
}

/** D redeclares m1, so A's body is pruned before any conflict can arise. */
class MostSpecificWins implements A, D {
    // Deliberately empty: D's m1 overrides A's, so there is nothing to resolve.
}

interface A {
    default String m1() {
        return "A m1";
    }
}

interface B {
    default String m1() {
        return "B m1";
    }
}

interface C {
    default String m2() {
        return "C m2";
    }
}

interface D extends A {
    @Override
    default String m1() {
        return "D m1";
    }
}
