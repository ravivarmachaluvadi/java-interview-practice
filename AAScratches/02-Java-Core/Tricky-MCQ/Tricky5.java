/**
 * Problem:
 * Demonstrates how Java’s static type checking prevents calling a subclass-specific
 * method through a reference of the superclass type. The code attempts to invoke
 * Dog.makeSound() on an Animal reference, which fails at compile time because
 * makeSound() is not declared in Animal.
 *
 * Approach:
 * 1. Create an Animal reference pointing to a Dog instance.
 * 2. Attempt to call makeSound() directly – compilation error.
 * 3. Cast the reference back to Dog and invoke makeSound().
 *    The cast compiles, but if the object were not actually a Dog it would
 *    throw ClassCastException at runtime.
 *
 * Complexity:
 * Time: O(1) – constant-time operations for casting and method call.
 * Space: O(1) – no additional data structures are used.
 */

public class Tricky5 {
    public static void main(String[] args) {
        Animal a = new Dog();
//        a.makeSound();// Compilation Error
        ((Dog) a).makeSound();// Compilation Error
    }
    
class Animal {
}

class Dog extends Animal {
    void makeSound() {
        System.out.println("Dog barks");
    }
}


}
