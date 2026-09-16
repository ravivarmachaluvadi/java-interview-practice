import java.util.*;

// sliding window or two pointer solution
class LongestUniformSubstring {

    static int[] longestUniformSubstring(String input) {

        if (input.length() == 0) {
            return new int[]{-1, 0};
        }
        int maxLength = 1, endIndex = 0, length = 1;

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == input.charAt(i - 1)) {
                length++;
                if (length > maxLength) {
                    maxLength = length;
                    endIndex = i;
                }
            } else {
                length = 1;
            }
        }
        return new int[]{endIndex - maxLength + 1, maxLength};
    }

    private static final Map<String, int[]> testCases = new HashMap<>();

    public static void main(String[] args) {
        testCases.put("", new int[]{-1, 0});
        testCases.put("10000111", new int[]{1, 4});
        testCases.put("aaabbbbbCdAA", new int[]{3, 5});

        boolean pass = true;
        for (Map.Entry<String, int[]> testCase : testCases.entrySet()) {
            String input = testCase.getKey();
            int[] expected = testCase.getValue();
            int[] result = longestUniformSubstring(input);
            if (!Arrays.equals(result, expected)) {
                System.out.printf("Test failed for input: '%s'. Expected: %s, Got: %s\n",
                        input, Arrays.toString(expected), Arrays.toString(result));
                pass = false;
            }
        }
        if (pass) {
            System.out.println("All tests pass!");
        } else {
            System.out.println("Some tests failed.");
        }
    }

}
