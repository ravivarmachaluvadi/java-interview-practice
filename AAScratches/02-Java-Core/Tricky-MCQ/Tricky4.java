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
