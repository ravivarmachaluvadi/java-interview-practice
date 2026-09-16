/**
 * Demonstrates polymorphism in Java by overriding the makeSound() method.
 *
 * A reference of type Animal points to an instance of Dog, and calling
 * makeSound() invokes the overridden method in Dog, printing "Dog barks".
 *
 * Approach:
 * 1. Define a base class with a virtual method.
 * 2. Extend it and override that method.
 * 3. Use a superclass reference to call the overridden method at runtime.
 *
 * Time Complexity: O(1) – single method call.
 * Space Complexity: O(1) – constant additional space.
 */
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    void makeSound() {
        System.out.println("Dog barks");
    }
}

class Tricky2 {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.makeSound();// Dog barks
    }
}
