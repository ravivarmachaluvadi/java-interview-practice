/**
 * Demonstrates Java's method overloading with variable‑argument (varargs) parameters.
 *
 * The program defines two overloaded methods:
 * - {@code varArgDemo(Integer...)} accepts any number of Integer objects.
 * - {@code varArgDemo(String...)} accepts any number of String objects.
 *
 * In {@link #main(String...)} it creates an {@link java.util.ArrayList} of Integers,
 * a fixed‑size array of Strings, and then calls the appropriate overloaded method
 * based on the argument type. The last call passes a single empty string literal,
 * which also matches the String vararg overload.
 *
 * Approach:
 * 1. Define two varargs methods with distinct parameter types (Integer[] vs String[]).
 * 2. Call them with various arguments to trigger compile‑time resolution of the correct
 *    overload.
 *
 * Time Complexity: O(1) per call – no processing is performed inside the methods.
 * Space Complexity: O(1) – only method parameters are stored; no additional data structures.
 */
import java.util.ArrayList;
import java.util.List;

class VarArgDemo {
    public static void main(String... args) {
        List<Integer> list = new ArrayList<>();
        String[] list1 = new String[10];

        varArgDemo(list);
        varArgDemo(list1);
        varArgDemo(list1);
        varArgDemo("");

    }

    private static void varArgDemo(Integer... list) {
    }

    private static void varArgDemo(String... list) {
    }
}
