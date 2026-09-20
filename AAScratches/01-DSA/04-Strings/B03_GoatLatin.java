/*
 * =====================================================================
 *  Goat Latin                                        LeetCode 824 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence of words separated by single spaces, convert each word to
 *   "Goat Latin": a word starting with a vowel keeps its letters; otherwise its
 *   first letter moves to the end. Then append "ma", then append one 'a' per
 *   1-based word position (first word gets "a", second "aa", ...). Words contain
 *   only letters, in either case.
 *
 * EXAMPLE
 *   "I speak Goat Latin"  ->  "Imaa peaksmaaa oatGmaaaa atinLmaaaaa"
 *   "The quick brown fox"  ->  "heTmaa uickqmaaa rownbmaaaa oxfmaaaaa"
 *   "a"                    ->  "amaa"          (single vowel word, position 1)
 *   ""                     ->  ""              (no words, nothing to emit)
 *
 * APPROACH  (per-word transform with index)
 *   1. Split the sentence on single spaces into words.
 *   2. For word i (0-based): if its first char is a vowel keep it, else rotate the
 *      first char to the end (substring(1) + charAt(0)).
 *   3. Append "ma", then append (i + 1) copies of 'a'.
 *   4. Append the word plus a space to the result; trim the trailing space at the end.
 *
 * KEY INSIGHT
 *   The only state that crosses the loop boundary is the word index. Everything
 *   else is a pure per-word transform, so the pattern is split -> map(word, i) ->
 *   join. Recognise this whenever the output for a token depends on its position.
 *
 * COMPLEXITY
 *   Time  O(n + w^2)  n = total characters, w = word count; the trailing 'a's add
 *                     1 + 2 + ... + w characters, which dominates for many words.
 *   Space O(n + w^2)  the output string itself.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not build the 'a' suffix with a String in a loop? Repeated concat is O(k^2).
 *   - What if words could be separated by multiple spaces? Use split("\\s+") and
 *     guard against empty tokens.
 *   - Can you do it with one StringBuilder and no per-word builder? Yes; append
 *     directly and use a "first word" flag instead of trimming.
 *
 * RUN
 *   main() runs 4 cases (typical, mixed case, single letter, empty) and prints
 *   actual vs expected.
 */
class GoatLatin {

    private static final String VOWELS = "aeiouAEIOU";

    public static String toGoatLatin(String sentence) {
        if (sentence.isEmpty()) {
            return "";
        }
        String[] words = sentence.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            StringBuilder goatWord = new StringBuilder();

            if (startsWithVowel(word)) {
                goatWord.append(word);
            } else {
                // rotate the first letter to the end
                goatWord.append(word.substring(1)).append(word.charAt(0));
            }

            goatWord.append("ma");

            // one 'a' per 1-based word position
            for (int j = 0; j <= i; j++) {
                goatWord.append('a');
            }

            result.append(goatWord).append(' ');
        }

        // drop the trailing space added after the last word
        return result.toString().trim();
    }

    private static boolean startsWithVowel(String word) {
        return VOWELS.indexOf(word.charAt(0)) >= 0;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1", toGoatLatin("I speak Goat Latin"),
                "Imaa peaksmaaa oatGmaaaa atinLmaaaaa");
        print("case 2", toGoatLatin("The quick brown fox"),
                "heTmaa uickqmaaa rownbmaaaa oxfmaaaaa");
        print("case 3", toGoatLatin("a"), "amaa");
        print("case 4", "[" + toGoatLatin("") + "]", "[]");
    }
}
