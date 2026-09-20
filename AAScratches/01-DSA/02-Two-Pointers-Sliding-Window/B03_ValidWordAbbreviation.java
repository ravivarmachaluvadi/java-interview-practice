/*
 * =====================================================================
 *  Valid Word Abbreviation                            LeetCode 408 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a word and an abbreviation, decide whether the abbreviation is valid for the word.
 *   An abbreviation replaces one or more non-empty, non-adjacent substrings with their length.
 *   A number in the abbreviation may not have a leading zero, and it may not be zero itself.
 *
 * EXAMPLE
 *   word = "internationalization", abbr = "i12iz4n"  ->  true   (i + 12 chars + iz + 4 chars + n)
 *   word = "apple",                abbr = "a2e"      ->  false  ("pp" skipped, then "le" != "e")
 *   word = "substitution",         abbr = "s010n"    ->  false  (leading zero is illegal)
 *   word = "hi",                   abbr = "2"        ->  true   (number covers the whole word)
 *
 * APPROACH  (two-string walk with numeric jump)
 *   1. Keep one index into the word and one into the abbreviation.
 *   2. If the abbreviation character is a letter, it must equal the word character; advance both.
 *   3. If it is a digit, reject a leading '0', otherwise parse the whole run of digits into a
 *      number and jump the word index forward by that many characters.
 *   4. Valid only if BOTH indexes land exactly at the end of their strings.
 *
 * KEY INSIGHT
 *   It is the same two-pointer walk as "Is Subsequence", plus one twist: a digit run is parsed
 *   in-loop and acts as a jump instruction for the other pointer. The final "both exhausted"
 *   check is what catches an abbreviation that is too short or overshoots the word.
 *
 * COMPLEXITY
 *   Time  O(n + m)  every character of word and abbr is visited at most once
 *   Space O(1)      two indexes and one running number
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is "a0b" invalid? (a substring must be non-empty, so a zero-length skip is disallowed)
 *   - What if the abbreviation is "a12" for a 3-letter word? (jump overshoots, final check fails)
 *   - Generalisation: generate all valid abbreviations of a word (LeetCode 320, backtracking)
 *   - Minimum unique word abbreviation against a dictionary (LeetCode 411, bitmask + pruning)
 *
 * RUN
 *   main() runs 5 cases (typical, mismatch, leading zero, whole-word number, overshoot)
 *   and prints actual vs expected.
 */
class ValidWordAbbreviation {

    public static boolean validWordAbbreviation(String word, String abbr) {
        int wordIndex = 0, abbrIndex = 0;

        while (wordIndex < word.length() && abbrIndex < abbr.length()) {
            char abbrChar = abbr.charAt(abbrIndex);

            if (Character.isDigit(abbrChar)) {
                if (abbrChar == '0') {
                    return false; // leading zero (or a bare "0") is never allowed
                }
                int skip = 0;
                // parse the full run of digits, e.g. "12" -> 12, advancing abbrIndex as we go
                while (abbrIndex < abbr.length() && Character.isDigit(abbr.charAt(abbrIndex))) {
                    skip = skip * 10 + (abbr.charAt(abbrIndex) - '0');
                    abbrIndex++;
                }
                wordIndex += skip; // jump over that many word characters
            } else {
                if (word.charAt(wordIndex) != abbrChar) {
                    return false;
                }
                wordIndex++;
                abbrIndex++;
            }
        }

        // both must be fully consumed: no leftover word letters, no leftover abbr tokens
        return wordIndex == word.length() && abbrIndex == abbr.length();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical      ",
                validWordAbbreviation("internationalization", "i12iz4n"), true);
        print("case 2 mismatch     ", validWordAbbreviation("apple", "a2e"), false);
        print("case 3 leading zero ", validWordAbbreviation("substitution", "s010n"), false);
        print("case 4 whole word   ", validWordAbbreviation("hi", "2"), true);
        print("case 5 overshoot    ", validWordAbbreviation("abc", "a12"), false);
    }
}
