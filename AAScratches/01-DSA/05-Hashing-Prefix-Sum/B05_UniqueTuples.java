/**
 * Problem: Extract all unique substrings (tuples) of a given length from an input string.
 *
 * Approach: Iterate over the string, taking each consecutive slice of the specified
 * length and adding it to a HashSet which automatically removes duplicates.
 *
 * Time Complexity: O(n), where n is the length of the input string,
 * since each character is processed once.
 *
 * Space Complexity: O(k), where k is the number of unique substrings generated,
 * as they are stored in the HashSet. */
import java.util.Arrays;
import java.util.HashSet;

class UniqueTuples {

    public static HashSet<String> uniqueTuples(String inputString, int length) {
        if (inputString == null) return null;
        int inputStringLen = inputString.length();
        HashSet<String> hs = new HashSet<>();
        for (int i = 0; i < (inputStringLen - length + 1); i++)
            hs.add(inputString.substring(i, (i + length)));
        return hs;
    }

    public static void main(String[] args) {

        String input = "aab";
        String input1 = "abbccde";

        HashSet<String> result = uniqueTuples(input, 2);
        HashSet<String> result1 = uniqueTuples(input1, 2);

        if ((result.contains("aa") && result.contains("ab"))
                && (result1.containsAll(Arrays.asList("ab", "bb", "bc", "cc", "cd", "de"))
                && result1.size() == 6)) {
            System.out.println("Test passed.");
        } else {
            System.out.println("Test failed.");
        }
    }
}
