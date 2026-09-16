/**
 * Problem:
 * Demonstrates that Java Strings are immutable. The code attempts to concatenate
 * two strings using {@code a.concat(b)} but does not assign the result back to
 * {@code a}, so printing {@code a} still shows the original value.
 *
 * Approach:
 * Create two identical string literals, call {@link String#concat(String)}
 * on one of them without reassigning, and print the original variable.
 * The output confirms that the original string remains unchanged.
 *
 * Time Complexity:
 * O(1) – the concatenation is not stored; only a method call occurs.
 *
 * Space Complexity:
 * O(1) – no additional data structures are created beyond the existing strings.
 */
public class Tricky8 {
    public static void main(String[] args) {
        String a = "abc";
        String b = "abc";

        a.concat(b);
        System.out.println(a); // abc
    }
}
