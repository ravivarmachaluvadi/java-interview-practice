/*
 * =====================================================================
 *  Reverse Vowels of a String                       LeetCode 345 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, reverse only the vowels (a e i o u, either case) and leave every
 *   other character where it is. Return the new string. The input may be empty or
 *   contain no vowels at all, in which case it is returned unchanged.
 *
 * EXAMPLE
 *   "hello world"  ->  "hollo werld"     vowels e,o,o become o,o,e
 *   "leetcode"     ->  "leotcede"        LeetCode sample
 *   ""             ->  ""                empty edge case
 *   "xyz"          ->  "xyz"             no vowels, nothing to swap
 *   "aA"           ->  "Aa"              case is preserved per character, just relocated
 *
 * APPROACH  (opposite ends, skip and swap)
 *   1. Convert to a char[] so we can swap in place. left = 0, right = n - 1.
 *   2. Advance left while chars[left] is not a vowel (and left < right).
 *   3. Retreat right while chars[right] is not a vowel (and left < right).
 *   4. If left < right still holds, both point at vowels: swap them, then move both
 *      inward by one.
 *   5. Repeat until the pointers meet or cross.
 *
 * KEY INSIGHT
 *   This is the converging-pointer primitive: two pointers walk toward each other and
 *   act only when both satisfy a condition. The inner loops MUST re-check left < right;
 *   dropping that guard is the classic bug (a string with no vowels would run left off
 *   the end). "Skip what does not qualify, swap what does, converge" is the pattern.
 *
 * COMPLEXITY
 *   Time  O(n)  every character is visited by at most one pointer once
 *   Space O(n)  for the char[] copy (O(1) extra beyond the output string)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reverse only the letters (LC 917): same shape, test Character.isLetter instead.
 *   - Reverse the whole string / a subrange: drop the skip step, swap every pair.
 *   - Valid Palindrome (LC 125): same converging walk, compare instead of swap.
 *   - Faster vowel test: a boolean[128] lookup or a Set instead of String.indexOf.
 *
 * RUN
 *   main() runs 5 cases (typical, LeetCode sample, empty, no vowels, mixed case)
 *   and prints actual vs expected.
 */

class ReverseVowels {

    private static final String VOWELS = "aeiouAEIOU";

    private static boolean isVowel(char c) {
        return VOWELS.indexOf(c) != -1;
    }

    public static String reverseVowels(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        char[] chars = string.toCharArray();
        int left = 0;
        int right = chars.length - 1;

        while (left < right) {
            // Move left forward until it sits on a vowel. The left < right guard is essential:
            // without it a vowel-free string would run left past the end of the array.
            while (left < right && !isVowel(chars[left])) {
                left++;
            }
            // Move right backward until it sits on a vowel, same guard
            while (left < right && !isVowel(chars[right])) {
                right--;
            }
            // Both pointers are on vowels (or have met): swap and step inward
            if (left < right) {
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;
                left++;
                right--;
            }
        }
        return new String(chars);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", reverseVowels("hello world"), "hollo werld");
        print("case 2 LC sample ", reverseVowels("leetcode"), "leotcede");
        print("case 3 empty     ", "\"" + reverseVowels("") + "\"", "\"\"");
        print("case 4 no vowels ", reverseVowels("xyz"), "xyz");
        print("case 5 mixed case", reverseVowels("aA"), "Aa");
    }
}
