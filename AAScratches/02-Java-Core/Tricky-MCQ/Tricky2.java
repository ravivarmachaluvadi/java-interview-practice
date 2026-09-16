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
