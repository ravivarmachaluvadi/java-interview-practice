import java.util.*;

/**
 * Input: digits = "23"
 * <p>
 * Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
 * <p>
 * <p>
 * Input: digits = "2"
 * <p>
 * Output: ["a","b","c"]
 */
// https://leetcode.com/problems/letter-combinations-of-a-phone-number/
class LetterCombinations {
    // Mapping of digits to letters
    private static String[] letterMap = {
            "",     // 0
            "",     // 1
            "abc",  // 2
            "def",  // 3
            "ghi",  // 4
            "jkl",  // 5
            "mno",  // 6
            "pqrs", // 7
            "tuv",  // 8
            "wxyz"  // 9
    };

    public static List<String> letterCombinations(String digits) {
        List<String> combinations = new ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return combinations;
        }
        backtrack(combinations, digits, 0, new StringBuilder());
        return combinations;
    }

    private static void backtrack(List<String> combinations, String digits, int index, StringBuilder current) {
        if (index == digits.length()) {
            combinations.add(current.toString());
            return;
        }

        String letters = letterMap[digits.charAt(index) - '0'];
        for (char letter : letters.toCharArray()) {
            current.append(letter); // Add letter
            backtrack(combinations, digits, index + 1, current); // Move to the next digit
            current.deleteCharAt(current.length() - 1); // Backtrack
        }
    }

    public static void main(String[] args) {
        String num = "23";

        List<String> strings = letterCombinations(num);
        System.out.println(strings);

    }
}
