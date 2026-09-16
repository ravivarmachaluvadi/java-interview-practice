class Animal {
}

class Dog extends Animal {
    void makeSound() {
        System.out.println("Dog barks");
    }
}

public class Tricky5 {
    public static void main(String[] args) {
        Animal a = new Dog();
//        a.makeSound();// Compilation Error
        ((Dog) a).makeSound();// Compilation Error
    }
}
