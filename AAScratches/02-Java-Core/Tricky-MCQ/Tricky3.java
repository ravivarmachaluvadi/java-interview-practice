/**
 * This program demonstrates the rule that a static method in a subclass
 * cannot override an instance method from its superclass. The compiler
 * rejects the code with an error: "Static method 'makeSound()' in 'Dog'
 * cannot override instance method 'makeSound()' in 'Animal'".
 *
 * Approach:
 * 1. Define an instance method makeSound() in Animal.
 * 2. Attempt to declare a static method with the same signature in Dog.
 * 3. Compile-time error occurs, preventing execution.
 *
 * Time Complexity: O(1) – only compile-time checks are performed.
 * Space Complexity: O(1) – no additional data structures are used.
 */
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    // Compilation Error
    // Static method 'makeSound()' in 'Dog' cannot override instance method 'makeSound()' in 'Animal'
    static void makeSound() {
        System.out.println("Dog barks");
    }
}

public class Tricky3 {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.makeSound();// Dog barks
    }
}
