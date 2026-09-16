/**
 * Demonstrates how a static generic method can accept any object type.
 *
 * The program calls {@code NonGenericClass.genericMethod} with an Integer,
 * a String, and a Double, printing each value to the console.
 *
 * Approach:
 * 1. Define a static generic method that takes a parameter of type T.
 * 2. Assign the parameter to another variable of the same generic type.
 * 3. Print the variable using {@code System.out.println}.
 *
 * Time Complexity: O(1) per call – only constant‑time operations are performed.
 * Space Complexity: O(1) – no additional data structures are created beyond
 * the method parameters and local variables.
 */
class NonGenericClass {
    static <T> void genericMethod(T t1) {
        T t2 = t1;

        System.out.println(t2);
    }
}

public class GenericsInJava {
    public static void main(String[] args) {
        NonGenericClass.genericMethod(new Integer(123));     //Passing Integer type as an argument

        NonGenericClass.genericMethod("I am string");        //Passing String type as an argument

        NonGenericClass.genericMethod(new Double(25.89));    //Passing Double type as an argument
    }
}
