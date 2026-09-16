class Tricky1 {

    public static void main(String[] args) {
        B b = new B(); // A C B D
    }

    public static void main(char[] args) {
    }
}

class A {
    static {
        System.out.print("A");
    }

    {
        System.out.print("B");
    }
}


class B extends A {
    static {
        System.out.print("C");
    }

    {
        System.out.print("D");
    }
}
