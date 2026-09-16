
class DefaultM1InABInterfaces implements A, B, C {
    public static void main(String[] args) {

        DefaultM1InABInterfaces sc = new DefaultM1InABInterfaces();
        sc.m1();

        sc.m2();

    }

    @Override
    public void m1() {
        A.super.m1();
    }
}

interface A {
    default void m1() {
        System.out.println("A m1");
    }
}

interface B {
    default void m1() {
        System.out.println("B m1");
    }
}

interface C {
    default void m2() {
        System.out.println("C m2");
    }
}