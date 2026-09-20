/*
 * =====================================================================
 *  Which makeSound() runs?                            Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   Animal declares makeSound(); Dog extends Animal and overrides it.
 *   The code does:  Animal a = new Dog();  a.makeSound();
 *   Which version runs - Animal's or Dog's? And what does "a.name" read if both
 *   classes declare a field called name?
 *
 * OPTIONS  (for the a.makeSound() call)
 *   A. "Animal makes a sound"   B. "Dog barks"   C. compile error   D. ambiguous
 *
 * HOW TO REASON ABOUT IT
 *   1. The reference type (Animal) decides which methods you are ALLOWED to call.
 *   2. The object's runtime type (Dog) decides which override actually runs. The JVM
 *      looks up the method in the object's own vtable - this is dynamic dispatch.
 *   3. Fields are the opposite: they are resolved at compile time from the reference
 *      type, so Dog's "name" hides Animal's rather than overriding it.
 *
 * GOTCHAS
 *   - Only instance methods are dispatched dynamically. Fields, static methods and
 *     private methods are bound to the static (reference) type.
 *   - An override cannot narrow visibility, and it cannot add new checked exceptions.
 *   - Calling an overridable method from a constructor runs the subclass version
 *     before the subclass fields are initialised - a classic source of nulls.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Overloading vs overriding: which one is decided at compile time, and why?
 *   - What does @Override actually buy you if the code compiles without it?
 *   - Can an override return a different type? (Yes - a covariant subtype.)
 *   - How do you call the superclass version from inside the override? (super.makeSound())
 *
 * RUN
 *   main() runs 3 cases: an Animal reference holding a Dog, a plain Animal, and the
 *   field-hiding case. Each prints what actually happened, then the expected text.
 *
 * ---------------------------------------------------------------------
 * ANSWER  (stop above if you want to solve it yourself)
 * ---------------------------------------------------------------------
 *   B. "Dog barks". But "a.name" reads "animal", not "dog".
 *
 * WHY
 *   Methods are virtual in Java, so the override on the runtime object wins. Fields
 *   are not virtual - the compiler picks the field of the declared reference type.
 *   Same expression "a", two different rules.
 */
class Animal {
    String name = "animal";

    void makeSound() {
        System.out.print("Animal makes a sound");
    }
}

class Dog extends Animal {
    String name = "dog"; // hides Animal.name, it does not override it

    @Override
    void makeSound() {
        System.out.print("Dog barks");
    }
}

class Tricky2 {

    public static void main(String[] args) {
        Animal a = new Dog();
        System.out.print("case 1 (Animal ref, Dog object): ");
        a.makeSound();
        System.out.println("   expected Dog barks");

        Animal plain = new Animal();
        System.out.print("case 2 (Animal ref, Animal obj): ");
        plain.makeSound();
        System.out.println("   expected Animal makes a sound");

        // Field access is resolved from the reference type, so Animal.name wins.
        System.out.println("case 3 (field hiding)         : a.name=" + a.name
                + ", ((Dog) a).name=" + ((Dog) a).name
                + "   expected a.name=animal, ((Dog) a).name=dog");
    }
}
