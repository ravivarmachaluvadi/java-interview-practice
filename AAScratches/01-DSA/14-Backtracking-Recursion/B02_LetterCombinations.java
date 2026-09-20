/*
 * =====================================================================
 *  Letter Combinations of a Phone Number        LeetCode 17 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of digits 2-9, return every letter combination the number
 *   could spell on an old phone keypad (2 -> "abc", 3 -> "def", ... 7 -> "pqrs",
 *   9 -> "wxyz"). The answer may be returned in any order. An empty input
 *   returns an empty list, not a list containing the empty string.
 *
 * EXAMPLE
 *   digits = "23"  ->  [ad, ae, af, bd, be, bf, cd, ce, cf]
 *   digits = ""    ->  []                (edge case main() runs)
 *   digits = "79"  ->  16 combinations   because 7 has 4 letters and 9 has 4
 *
 * APPROACH  (digit-to-letter map with StringBuilder backtracking)
 *   1. Store the keypad as a fixed array so letterMap[d] is the letters for d.
 *   2. Recurse with an index into digits and a StringBuilder holding the
 *      partial word built so far.
 *   3. When index reaches the end of digits, the word is complete -- record it.
 *   4. Otherwise loop over the letters of the current digit: append the letter,
 *      recurse on index + 1, then delete the last char to undo the choice.
 *
 * KEY INSIGHT
 *   The recursion depth is fixed at digits.length(): one digit consumed per
 *   level, and the branching factor is that digit's letter count. Because the
 *   StringBuilder is shared across the whole search, every append must be
 *   matched by a deleteCharAt on the way out -- that append / recurse / undo
 *   triple is the backtracking template you reuse everywhere.
 *
 * COMPLEXITY
 *   Time  O(4^n * n)  up to 4 branches per digit, and n work to copy each word
 *   Space O(n)        recursion stack plus the shared StringBuilder
 *                     (the output list itself is O(4^n * n) on top of that)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Write it iteratively (grow a list of prefixes one digit at a time).
 *   - Only emit combinations that are real dictionary words -- add a Trie and
 *     prune a branch as soon as the prefix is not in the Trie.
 *   - What if digits may contain 0 or 1? Decide: skip them, or return empty.
 *   - Stream the results instead of materialising 4^n strings in memory.
 *
 * RUN
 *   main() runs 3 cases (typical, empty edge case, two 4-letter digits) and
 *   prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class LetterCombinations {

    // index == the digit itself, so letterMap[7] is "pqrs"
    private static final String[] letterMap = {
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
        // an empty input has no combinations at all, not even the empty word
        if (digits == null || digits.isEmpty()) {
            return combinations;
        }
        backtrack(combinations, digits, 0, new StringBuilder());
        return combinations;
    }

    private static void backtrack(List<String> combinations, String digits, int index, StringBuilder current) {
        if (index == digits.length()) {
            combinations.add(current.toString()); // snapshot: current keeps mutating
            return;
        }

        String letters = letterMap[digits.charAt(index) - '0'];
        for (int i = 0; i < letters.length(); i++) {
            current.append(letters.charAt(i));                   // choose
            backtrack(combinations, digits, index + 1, current); // explore the next digit
            current.deleteCharAt(current.length() - 1);          // undo, so the next letter starts clean
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: typical two-digit number
        print("case 1 digits=\"23\":      ", letterCombinations("23"),
                "[ad, ae, af, bd, be, bf, cd, ce, cf]");

        // case 2: edge -- empty input must give an empty list
        print("case 2 digits=\"\":        ", letterCombinations(""), "[]");

        // case 3: tricky -- 7 and 9 have four letters each, so 4 * 4 = 16
        print("case 3 digits=\"79\" size: ", letterCombinations("79").size(), 16);
        print("case 3 first / last:     ",
                letterCombinations("79").get(0) + " / " + letterCombinations("79").get(15),
                "pw / sz");
    }
}
