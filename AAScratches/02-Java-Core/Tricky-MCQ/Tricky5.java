/*
 * =====================================================================
 *  Calling a subclass-only method through a parent reference   Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   class Animal { }                                  // empty
 *   class Dog extends Animal { void makeSound() { print("Dog barks"); } }
 *
 *   Animal a = new Dog();
 *   a.makeSound();            // line 1
 *   ((Dog) a).makeSound();    // line 2
 *
 *   Which of these two lines compiles, and what happens at runtime?
 *
 * OPTIONS
 *   A. Both compile; both print "Dog barks"
 *   B. Line 1 fails to compile; line 2 compiles and prints "Dog barks"
 *   C. Both compile; line 2 throws ClassCastException
 *   D. Line 1 compiles but throws NoSuchMethodError at runtime
 *
 * ---------------------------------------------------------------------
 *  ANSWER  ->  B.
 *    Line 1: compile error "cannot find symbol: method makeSound()".
 *    Line 2: compiles and prints "Dog barks".
 * ---------------------------------------------------------------------
 *
 * WHY
 *   1. The compiler resolves a call using the STATIC type of the reference.
 *      The static type of a is Animal, and Animal declares no makeSound(),
 *      so line 1 is rejected before the program ever runs. It does not matter
 *      that the object really is a Dog - the compiler is not tracking that.
 *   2. The cast (Dog) a changes the static type of the expression to Dog, so
 *      the compiler can now see makeSound() and the call is legal.
 *   3. The cast is still checked at RUNTIME. If a did not actually refer to a
 *      Dog, the JVM would throw ClassCastException (case 3 below).
 *
 * KEY INSIGHT
 *   A downcast does not change the object; it changes what the COMPILER
 *   believes about the expression, and hands the check over to the JVM. So a
 *   downcast is a promise you make to the compiler that the JVM will verify.
 *   Guard it with instanceof (or pattern matching) whenever the type is not
 *   guaranteed by the surrounding code.
 *
 * GOTCHAS
 *   - Fixed: Animal and Dog were declared as INNER (non-static) classes of
 *     Tricky5, so "new Dog()" inside static main failed with "non-static
 *     variable this cannot be referenced from a static context". They are now
 *     static nested classes, which is what the puzzle intended.
 *   - Upcasting (Dog -> Animal) is always safe and needs no cast.
 *     Downcasting (Animal -> Dog) is the direction that can fail.
 *   - Java 16+ lets you write: if (a instanceof Dog d) d.makeSound();
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if Animal declared makeSound() and Dog overrode it? Does line 1
 *     compile then, and whose body runs?
 *   - Difference between ClassCastException and a compile-time "inconvertible
 *     types" error (for example casting Animal to String)?
 *   - How does generic erasure introduce casts you never wrote?
 *
 * RUN
 *   main() runs 3 cases: the legal downcast, an instanceof-guarded call, and a
 *   downcast that fails at runtime. Each prints actual vs expected.
 */
public class Tricky5 {

    /** Parent type: deliberately declares nothing. */
    static class Animal {
    }

    /** Child type: makeSound() exists only here, not on Animal. */
    static class Dog extends Animal {
        void makeSound() {
            System.out.println("Dog barks");
        }
    }

    /** A sibling used to show what a bad downcast does. */
    static class Rabbit extends Animal {
    }

    public static void main(String[] args) {
        Animal a = new Dog();

        // The puzzle's line 1 stays commented out on purpose: it is the part
        // of the MCQ that does NOT compile.
        //     a.makeSound();   // error: cannot find symbol - method makeSound()

        // Case 1 - the cast widens what the compiler can see, and the object
        // really is a Dog, so this runs fine.
        System.out.print("case 1 (downcast then call):  ");
        ((Dog) a).makeSound();
        System.out.println("                 expected: Dog barks");

        // Case 2 - the safe form: ask first, then cast. Same output, no risk.
        String guarded = (a instanceof Dog) ? "instanceof true -> Dog barks" : "not a Dog";
        System.out.println("case 2 (instanceof guard):    " + guarded
                + "   expected instanceof true -> Dog barks");

        // Case 3 - the edge case. The compiler accepts this cast because
        // Rabbit IS-A Animal, but the object is a Dog, so the JVM rejects it.
        Animal r = new Rabbit();
        String outcome;
        try {
            Dog notReallyADog = (Dog) r;   // compiles, fails at runtime
            notReallyADog.makeSound();
            outcome = "no exception";
        } catch (ClassCastException e) {
            outcome = "ClassCastException";
        }
        System.out.println("case 3 (bad downcast):        " + outcome
                + "        expected ClassCastException");
    }
}
