/*
 * =====================================================================
 *  Can a static method override an instance method?  Tricky MCQ | Medium
 * =====================================================================
 *
 * QUESTION
 *   Animal declares an INSTANCE method makeSound().
 *   Dog extends Animal and declares a STATIC method with the same signature.
 *   main() then does:  Animal a = new Dog();  a.makeSound();
 *   Does this compile? If it does, what does it print?
 *
 * OPTIONS
 *   A. prints "Dog barks"                B. prints "Animal makes a sound"
 *   C. compile error in Dog              D. runtime ClassCastException
 *
 * THIS FILE IS MEANT NOT TO COMPILE
 *   Do not "fix" it - the compile error IS the lesson. javac reports:
 *     error: makeSound() in Dog cannot override makeSound() in Animal
 *       overriding method is static
 *   So running this file fails by design, and that is the expected result.
 *
 * HOW TO REASON ABOUT IT
 *   1. An inherited method name must keep one kind of binding in the whole hierarchy.
 *   2. Instance methods are dispatched on the runtime object; static methods are bound
 *      at compile time from the class name. The two rules cannot coexist for one name.
 *   3. So the compiler rejects the subclass declaration outright - it never gets as far
 *      as deciding what a.makeSound() would do.
 *
 * GOTCHAS
 *   - The mirror case is equally illegal: an instance method in the subclass cannot
 *     override a static method in the superclass.
 *   - static-over-static IS legal, but it is HIDING, not overriding. Animal a = new Dog();
 *     a.makeSound() would then run ANIMAL's version, because the reference type decides.
 *   - @Override on a static method is always a compile error, which is a cheap way to
 *     catch this mistake early.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What is the difference between hiding and overriding, in one sentence each?
 *   - Can you override a private method? (No - it is not inherited, you just declare a
 *     new unrelated method.) A final method? (No.)
 *   - Why can't static methods be abstract?
 *
 * RUN
 *   Nothing runs. Compilation fails on the "static void makeSound()" line in Dog, and
 *   that failure is the expected output of this file.
 *
 * ---------------------------------------------------------------------
 * ANSWER  (stop above if you want to solve it yourself)
 * ---------------------------------------------------------------------
 *   C. Compile error: "Static method 'makeSound()' in 'Dog' cannot override instance
 *   method 'makeSound()' in 'Animal'".
 *
 * WHY
 *   Overriding replaces a virtual method that is looked up on the object at runtime.
 *   A static method has no object to look it up on, so it cannot stand in for one.
 *   Java refuses the declaration rather than silently changing the dispatch rule.
 */
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    // Compile error (on purpose):
    // Static method 'makeSound()' in 'Dog' cannot override instance method in 'Animal'
    static void makeSound() {
        System.out.println("Dog barks");
    }
}

public class Tricky3 {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.makeSound(); // never reached - the file does not compile
    }
}
