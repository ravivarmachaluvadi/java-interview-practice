/**
 * Problem: Capitalize the first letter of each word in a given sentence.
 *
 * Approach: Split the string into words, transform each word by uppercasing its
 * first character and concatenating it with the rest of the word, then join
 * the transformed words back together with spaces.
 *
 * Time Complexity: O(n) where n is the number of characters in the input string,
 * because each character is processed a constant number of times.
 *
 * Space Complexity: O(n) for storing the array of words and the resulting string.
 */
import java.util.Arrays;
import java.util.stream.Collectors;

class FirstCharToUpperCase {
    public static void main(String[] args) {
        String str = "ravi varma";
        String capitalizedStr = Arrays
                .stream(str.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
        System.out.println(capitalizedStr); // Ravi Varma
    }
}
