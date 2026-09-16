/**
 * Demonstrates Java's method overloading by providing two distinct
 * {@code main} methods: one accepting a {@link String[]} and the other
 * accepting a {@link Character[]}. Depending on which signature is invoked,
 * the program prints either "String Main" or "Character Main".
 *
 * Approach:
 * 1. Define two overloaded {@code main} methods with different parameter types.
 * 2. Each method simply outputs a unique message indicating which overload
 *    was called.
 *
 * Time Complexity: O(1) – constant time to print the string.
 * Space Complexity: O(1) – no additional data structures are used.
 */
public class Tricky6 {
    public static void main(String[] args) {
        System.out.println("String Main");// String Main
    }

    public static void main(Character[] args) {
        System.out.println("Character Main");
    }

}