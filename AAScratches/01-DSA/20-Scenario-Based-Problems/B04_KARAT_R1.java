/*
 * =====================================================================
 *  Scrambled Word Hidden in a Note              Karat round 1 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   You suspect students are passing the answer to a quiz around, hidden inside a
 *   note that looks like gibberish. Given a list of lowercase words and one note,
 *   return the word whose letters can all be found inside the note. The letters need
 *   not be in order or adjacent, but each letter of the note can be used only ONCE.
 *   At most one word matches; return "-" when none does.
 *
 * EXAMPLE
 *   words = [baby, referee, cat, dada, dog, bird, ax, baz]
 *   note = "ctay"             ->  "cat"    letters need not be in order
 *   note = "bcanihjsrrrferet" ->  "cat"    letters need not be adjacent
 *   note = "tbaykkjlga"       ->  "-"      only one 'a', so "baby" cannot reuse it
 *   note = "bbbblkkjbaby"     ->  "baby"   b,a,b,y all present
 *   note = "dad"              ->  "-"      "dada" needs two 'a', the note has one
 *   note = "dadaa"            ->  "dada"   two 'd' and two 'a' available
 *   note = ""                 ->  "-"      edge case main() runs
 *
 * APPROACH  (letter multiset containment)
 *   1. Count the note's letters once into an int[26] - this is the budget of letters
 *      available, and it never changes while we test candidates.
 *   2. Count each word's letters into its own int[26].
 *   3. A word fits iff for EVERY letter, wordCount[c] <= noteCount[c]. One counter
 *      exceeding its budget is enough to reject, so bail out on the first failure.
 *   4. Return the first word that fits, otherwise "-".
 *
 * KEY INSIGHT
 *   "Letters cannot be reused" is the whole problem, and it is exactly the definition
 *   of multiset (bag) containment: word is a sub-multiset of note. The moment you see
 *   that, the "not in order, not adjacent" wording becomes irrelevant noise - position
 *   never enters the computation, only counts do. Recognise the shape: the same
 *   int[26] <= int[26] test drives Ransom Note, Valid Anagram (there with equality
 *   instead of <=) and Minimum Window Substring's "have we covered the need" check.
 *
 * COMPLEXITY
 *   With W = number of words and S = max length of a word or the note:
 *   Time  O(S + W * (S + 26)) = O(W * S)  count the note once, then each word once
 *   Space O(W * 26) = O(W)  one fixed 26-slot tally per word; O(26) if built lazily
 *
 * INTERVIEW FOLLOW-UPS
 *   - Many notes against the same word list: precompute the word tallies once, then
 *     each note costs O(S + W * 26). That is why the tallies live in a list here.
 *   - Unicode or mixed case instead of a-z: swap int[26] for a HashMap<Character,
 *     Integer>, or normalise case first. The int[26] indexing silently corrupts
 *     memory-adjacent counts on any character outside a-z.
 *   - Return ALL matching words rather than the first one.
 *   - Rank the matches by length, or find the longest word hidden in the note.
 *
 * RUN
 *   main() runs the 7 official Karat notes plus 2 edge cases (empty note, a word that
 *   uses the note exactly) and prints actual vs expected on each line.
 */

import java.util.ArrayList;
import java.util.List;

class Solution {

    private static final int ALPHABET = 26;
    private static final String NOT_FOUND = "-";

    /** Returns the first word whose letters all fit inside the note, or "-". */
    private String find(String[] words, String note) {
        // Precompute one tally per word so repeated notes cost nothing extra.
        List<int[]> wordTallies = new ArrayList<>();
        for (String word : words) {
            wordTallies.add(letterCounts(word));
        }

        int[] noteTally = letterCounts(note);
        for (int i = 0; i < words.length; i++) {
            if (fitsInside(wordTallies.get(i), noteTally)) {
                return words[i];
            }
        }
        return NOT_FOUND;
    }

    /** How many of each letter a-z the text contains. */
    private int[] letterCounts(String text) {
        int[] counts = new int[ALPHABET];
        for (int i = 0; i < text.length(); i++) {
            counts[text.charAt(i) - 'a']++;
        }
        return counts;
    }

    /** True iff the word never needs more of a letter than the note can supply. */
    private boolean fitsInside(int[] wordCounts, int[] noteCounts) {
        for (int c = 0; c < ALPHABET; c++) {
            if (wordCounts[c] > noteCounts[c]) {
                return false; // one over-budget letter is enough to reject
            }
        }
        return true;
    }

    // ---------- Test harness ----------

    private static void check(Solution sol, String[] words, String note, String expected) {
        String actual = sol.find(words, note);
        System.out.println("note \"" + note + "\" -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String[] words = {"baby", "referee", "cat", "dada", "dog", "bird", "ax", "baz"};

        // The 7 cases from the original Karat prompt.
        check(sol, words, "ctay", "cat");             // out of order
        check(sol, words, "bcanihjsrrrferet", "cat"); // not adjacent
        check(sol, words, "tbaykkjlga", "-");         // letters cannot be reused
        check(sol, words, "bbbblkkjbaby", "baby");    // plenty of spare letters
        check(sol, words, "dad", "-");                // "dada" needs a second 'a'
        check(sol, words, "breadmaking", "bird");
        check(sol, words, "dadaa", "dada");           // exactly enough letters

        // Edge cases.
        check(sol, words, "", "-");                   // empty note matches nothing
        check(sol, words, "xa", "ax");                // note consumed exactly
    }
}
