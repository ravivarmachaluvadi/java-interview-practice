/*
 * =====================================================================
 *  Sentence to camelCase                                        classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence with words separated by one or more whitespace characters (and
 *   possibly leading/trailing whitespace), return it as a single camelCase identifier:
 *   the first word all lower-case, every later word Capitalised, no separators.
 *
 * EXAMPLE
 *   "convert this sentence to camel case"  ->  "convertThisSentenceToCamelCase"
 *   "  Convert  THIS sentence "            ->  "convertThisSentence"   trims, lower-cases
 *   "hello"                                ->  "hello"
 *   ""                                     ->  ""
 *
 * APPROACH  (split, capitalize, concat)
 *   1. trim() then split("\\s+") so leading, trailing and repeated whitespace never
 *      produce empty tokens.
 *   2. Seed a StringBuilder with words[0].toLowerCase().
 *   3. For i from 1: append toUpperCase(charAt(0)) then substring(1).toLowerCase().
 *   4. No separator is appended between words, that is what makes it camelCase.
 *
 * KEY INSIGHT
 *   It is the same split-transform-join pipeline as the other word problems, except the
 *   join separator is "" and the FIRST word is treated differently from the rest. Starting
 *   the loop at index 1 (not 0) is the off-by-one that decides camelCase vs PascalCase.
 *   substring(1) on a one-letter word is "", so no length check is needed.
 *
 * COMPLEXITY
 *   Time  O(n)  every character is split, cased and appended once
 *   Space O(n)  the word array and the builder
 *
 * INTERVIEW FOLLOW-UPS
 *   - PascalCase: start the loop at 0 and drop the special first word.
 *   - snake_case / kebab-case: same loop, join with "_" or "-" and keep everything lower.
 *   - Reverse direction: split camelCase back into words by scanning for upper-case chars.
 *   - Words containing digits or punctuation: decide whether "2fa" gets capitalised.
 *
 * RUN
 *   main() runs 4 cases (typical, messy whitespace and case, single word, empty) and prints
 *   actual vs expected.
 */
class ToCamelCase {

    static String toCamelCase(String sentence) {
        // trim + \\s+ so repeated or leading/trailing spaces never yield empty tokens
        String[] words = sentence.trim().split("\\s+");
        StringBuilder camelCase = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) { // starts at 1: first word stays lower
            String word = words[i];
            camelCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }
        return camelCase.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"convert this sentence to camel case\"",
                toCamelCase("convert this sentence to camel case"),
                "convertThisSentenceToCamelCase");
        print("case 2 \"  Convert  THIS sentence \"", toCamelCase("  Convert  THIS sentence "),
                "convertThisSentence");
        print("case 3 \"hello\"", toCamelCase("hello"), "hello");
        print("case 4 \"\"", toCamelCase(""), "");
    }
}
