public class Tricky9 {
    public static void main(String[] args) {
        Tricky9 sc = new Tricky9();
        sc.m1(null);
    }

    void m1(String s) {
        System.out.println("string");// string output printed
    }

    void m1(Object s) {
        System.out.println("object");
    }

}
