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
