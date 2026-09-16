import java.util.Arrays;

// https://leetcode.com/problems/maximum-palindromes-after-operations/description/
// 3035. Maximum Palindromes After Operations
class MaximumPalindromesAfterOperations {
    public static int maxPalindromesAfterOperations(String[] words) {
        int totalChars = 0;
        int mask = 0;
        for (String w : words) {
            totalChars += w.length();
            for (char c : w.toCharArray()) {
                mask ^= 1 << (c - 'a');
            }
        }

        // subtract characters that are odd count (i.e., cannot form pairs)
        int oddCount = Integer.bitCount(mask);
        int usableChars = totalChars - oddCount;

        // sort words by length ascending
        Arrays.sort(words, (a, b) -> Integer.compare(a.length(), b.length()));

        int count = 0;
        for (String w : words) {
            int need = (w.length() / 2) * 2;
            if (usableChars >= need) {
                usableChars -= need;
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        // Examples
        String[] words1 = {"abbb", "ba", "aa"};
        System.out.println("Expected 3 → got " + maxPalindromesAfterOperations(words1));

        String[] words2 = {"abc", "ab"};
        System.out.println("Expected 2 → got " + maxPalindromesAfterOperations(words2));

        String[] words3 = {"cd", "ef", "a"};
        System.out.println("Expected 1 → got " + maxPalindromesAfterOperations(words3));

        // Additional test
        String[] words4 = {"aaa", "bbb", "cc", "d"};
        System.out.println("Got " + maxPalindromesAfterOperations(words4));
    }
}
