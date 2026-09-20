/*
 * =====================================================================
 *  Static vs instance initializer order            Tricky MCQ | Medium
 * =====================================================================
 *
 * QUESTION
 *   Class A has a static block printing "A" and an instance block printing "B".
 *   Class B extends A, with a static block printing "C" and an instance block
 *   printing "D". main() does "new B()". What is printed, in what order?
 *   Bonus: the class also declares main(char[] args) - does that even compile?
 *
 * OPTIONS
 *   A. ABCD        B. ACBD        C. ACDB        D. CABD
 *
 * HOW TO REASON ABOUT IT
 *   1. Touching B forces the JVM to initialize B, which first initializes its
 *      superclass A. Static blocks run once per class, superclass first: A then C.
 *   2. Then the object is built. B's constructor implicitly calls super() first,
 *      so A's instance block runs before B's: B then D.
 *   3. Static initialization happens once; a second "new B()" only re-runs the
 *      instance blocks.
 *
 * GOTCHAS
 *   - Instance initializer blocks run as part of the CONSTRUCTOR, immediately after
 *     super() and before the constructor body - not at "new" time in source order.
 *   - main(char[]) is an ordinary overload, so it compiles fine. The JVM only ever
 *     calls main(String[]); the char[] version is unreachable from the command line.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Where do field initializers (int x = 5;) run relative to instance blocks?
 *   - What if A's constructor calls an overridden method - why is that dangerous?
 *   - When exactly is a class initialized? (First instance, static member access,
 *     reflection; NOT a static final compile-time constant.)
 *
 * RUN
 *   main() runs 3 cases: first "new B()", a second "new B()" (statics already done),
 *   and "new A()". Each line prints the characters actually emitted, then the expected.
 *
 * ---------------------------------------------------------------------
 * ANSWER  (stop above if you want to solve it yourself)
 * ---------------------------------------------------------------------
 *   B. ACBD
 *
 * WHY
 *   Static side, superclass first: A ("A") then B ("C"). Then the object side, again
 *   superclass first because super() runs before the subclass body: A ("B") then
 *   B ("D"). The two phases never interleave - all static work finishes first.
 */
class Tricky1 {

    public static void main(String[] args) {
        System.out.print("case 1 (first new B()) : ");
        new B();
        System.out.println("   expected ACBD");

        System.out.print("case 2 (second new B()): ");
        new B(); // statics already ran once, so only the instance blocks print
        System.out.println("     expected BD");

        System.out.print("case 3 (new A())       : ");
        new A(); // A was initialized back in case 1, so no "A" this time
        System.out.println("      expected B");
    }

    /** Legal overload of main - it compiles, but the JVM never calls it. */
    public static void main(char[] args) {
        System.out.println("never invoked by the JVM");
    }
}

class A {
    static {
        System.out.print("A");
    }

    {
        System.out.print("B");
    }
}

class B extends A {
    static {
        System.out.print("C");
    }

    {
        System.out.print("D");
    }
}
