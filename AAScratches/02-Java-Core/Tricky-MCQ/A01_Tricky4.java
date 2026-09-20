/*
 * =====================================================================
 *  Virtual call on a subclass that does NOT override        Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   class Animal { void makeSound() { print("Animal makes a sound"); } }
 *   class Dog extends Animal { }            // empty body, no override
 *
 *   Animal a = new Dog(); a.makeSound();
 *
 *   What does this print, and does it even compile?
 *
 * OPTIONS
 *   A. Compilation error: Dog has no makeSound()
 *   B. "Animal makes a sound" C. "Dog barks"
 *   D. Runtime error: AbstractMethodError
 *
 * WHY IT LOOKS TRICKY
 *   Two different types are in play for the same expression:
 *     - the COMPILE-TIME (static) type of the variable a is Animal
 *     - the RUNTIME (dynamic) type of the object is Dog
 *   People expect one of them to "win" and cause a problem. Neither does.
 *
 * ---------------------------------------------------------------------
 *  ANSWER  ->  B.  It compiles and prints "Animal makes a sound".
 * ---------------------------------------------------------------------
 *
 * WHY
 *   1. COMPILE TIME decides IF the call is legal. The compiler looks only at
 *      the static type Animal, finds makeSound() there, and accepts the call.
 *   2. RUNTIME decides WHICH body runs. The JVM starts at the actual class
 *      (Dog) and walks up the chain looking for an override. Dog declares
 *      none, so the inherited Animal.makeSound() body is the one that runs.
 *   3. An empty subclass does not "lose" anything: Dog INHERITS makeSound(),
 *      so there is nothing missing and nothing abstract to fail on.
 *
 * KEY INSIGHT
 *   Static type = what you are ALLOWED to call.
 *   Dynamic type = WHOSE implementation actually runs.
 *   No override in the subclass simply means the inherited body is the most
 *   derived one, so the parent's code runs. Case 2 below shows the contrast:
 *   add a real override (Cat) and the dynamic type takes over.
 *
 * GOTCHAS
 *   - This rule is for INSTANCE methods only. Static methods are hidden, not
 *     overridden, and are resolved by the static type (Animal, not Dog).
 *   - Fields are never polymorphic either; they are resolved at compile time.
 *   - private and final methods are not virtual, so they cannot be overridden.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make makeSound() static in both classes. What prints now, and why?
 *   - Add a field name to both classes. Which one does a.name read?
 *   - What if Animal were abstract with an abstract makeSound()?
 *   - Can Dog widen the return type or narrow the visibility when overriding?
 *
 * RUN
 *   main() runs 2 cases (the puzzle, then the overriding contrast) and prints
 *   actual vs expected.
 */
class Tricky4 {

    // Animal/Dog/Cat are nested here on purpose: B01_Tricky2 declares its own
    // top-level Animal and Dog, and two top-level classes with the same name in
    // one folder overwrite each other's .class files.
    static class Animal {
        void makeSound() {
            System.out.println("Animal makes a sound");
        }
    }

    /** No override at all: Dog inherits Animal.makeSound() unchanged. */
    static class Dog extends Animal {
    }

    /** The contrast case: a real override, so the dynamic type wins. */
    static class Cat extends Animal {
        @Override
        void makeSound() {
            System.out.println("Cat meows");
        }
    }

    public static void main(String[] args) {
        // Case 1 - the puzzle itself. Static type Animal, dynamic type Dog,
        // no override in Dog, so Animal's body runs.
        Animal a = new Dog();
        System.out.print("case 1 (Dog, no override): ");
        a.makeSound();
        System.out.println("          expected: Animal makes a sound");

        // Case 2 - same static type, different dynamic type. Cat overrides,
        // so the JVM picks Cat's body. This is what people expected in case 1.
        Animal b = new Cat();
        System.out.print("case 2 (Cat, overrides):   ");
        b.makeSound();
        System.out.println("          expected: Cat meows");
    }
}
