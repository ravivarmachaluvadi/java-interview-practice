/**
 * Problem: Demonstrates Java inheritance and polymorphism by calling a method on an
 * object whose compile-time type is the superclass (Animal) but runtime type is a subclass (Dog).
 *
 * Approach: Create a Dog instance, assign it to an Animal reference, then invoke makeSound().
 * The overridden method in Animal is called because Dog does not override it.
 *
 * Time Complexity: O(1) – single method call.
 * Space Complexity: O(1) – constant auxiliary space.
 */
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {

}

public class Tricky4 {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.makeSound();// Animal makes a sound
    }
}
