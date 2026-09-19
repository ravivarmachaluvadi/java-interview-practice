/**
 * Problem:
 * Demonstrates Java's method overloading resolution when a null argument
 * is passed. The code defines two m1 methods, one accepting String and
 * another accepting Object. When sc.m1(null) is called, the compiler must
 * decide which overload to invoke.
 *
 * Approach:
 * Compile‑time overload resolution selects the most specific applicable
 * method. Since String is a subclass of Object, the String version is chosen,
 * resulting in "string" being printed.
 *
 * Complexity:
 * Time: O(1) – single method call with constant-time decision.
 * Space: O(1) – no additional data structures are used.
 */
public class Tricky9 {
    public static void main(String[] args) {
        Tricky9 sc = new Tricky9();
        sc.m1(null);
    }

    void m1(String s) {
        System.out.println("string");// string output printed
    }

    // void m1(StringBuilder s) {
    //     System.out.println("string");// string output printed
    // }

    void m1(Object s) {
        System.out.println("object");
    }

}
