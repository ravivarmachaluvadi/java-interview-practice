/*
 * =====================================================================
 *  Shortest Distance Between Two Words      LeetCode 243 | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a document (or a word array) and two distinct words, return the
 *   shortest distance between an occurrence of the first and an occurrence of
 *   the second. Matching is case-insensitive on raw text; return -1 when either
 *   word never appears.
 *
 *   "Distance" means two different things in interviews - ALWAYS ask which:
 *     1. Word-index distance : how many words apart (LeetCode 243).
 *     2. Character distance  : characters between the MIDPOINTS of the two
 *                              words (the Karat variant; its published answers
 *                              6 / 14 / 25 only come out with this meaning).
 *
 * EXAMPLE
 *   ["the","quick","brown","fox","quick","jumps","over","the","lazy","dog"]
 *       word1 = "quick", word2 = "the"      ->  1    indices 1 and 0 are adjacent
 *       word1 = "quick", word2 = "missing"  -> -1    edge case main() runs
 *   DOCUMENT, "and", "graphic"  ->  wordIndex 1,  midpoint distance 6
 *
 * APPROACH  (last-seen index, single pass)
 *   1. Walk the tokens left to right once, remembering only the most recent
 *      position of word1 and the most recent position of word2.
 *   2. Whenever either is updated and both have been seen, the gap between the
 *      two remembered positions is a candidate answer - keep the minimum.
 *   3. For the character variant the "position" is the word's midpoint:
 *      real start offset in the document + length / 2.
 *   4. midpointDistancePairwise() is the naive O(p*q) all-pairs version, kept
 *      as a second named method so main() can cross-check the one-pass answer.
 *
 * KEY INSIGHT
 *   You never need the full list of occurrences. Positions arrive in increasing
 *   order, so when you meet an occurrence of word1 the only occurrence of word2
 *   that can possibly be closer than anything already scored is the LAST one you
 *   saw. That single observation turns an O(p*q) all-pairs comparison into O(n)
 *   time and O(1) space. Recognise the shape: "closest pair of two tagged items
 *   in a sequence" is always last-seen bookkeeping, never a nested loop.
 *
 *   Gotcha the earlier drafts got wrong: if you tokenize with split() and
 *   advance a running offset by "length + 1", the offset drifts by one for every
 *   multi-character separator such as ", " or ". ". Use a regex Matcher and read
 *   the real m.start() offset instead.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over n tokens (pairwise variant is O(n + p*q))
 *   Space O(1)  two remembered positions (pairwise variant stores O(p + q))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Many queries on one fixed document (LC 244): pre-index word -> sorted
 *     positions, then merge the two lists with two pointers per query.
 *   - word1 equals word2 (LC 245): the else-if collapses, so track the previous
 *     occurrence of the same word instead of two separate slots.
 *   - Return the actual pair of offsets, not just the gap.
 *   - Make it phrase-aware, or ignore stop words - changes only the tokenizer.
 *
 * RUN
 *   main() runs 2 array cases (typical, missing word) and 3 document cases,
 *   each printing actual vs expected for all three distance methods.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MinimumDistanceBetweenWordsV2 {

    // -------- Approach 1: word-index distance on an array (LeetCode 243 shape) --------

    public static int wordIndexDistance(String[] words, String word1, String word2) {
        int minDistance = Integer.MAX_VALUE;
        int lastPositionWord1 = -1;
        int lastPositionWord2 = -1;

        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word1)) {
                lastPositionWord1 = i;
            } else if (words[i].equals(word2)) {
                lastPositionWord2 = i;
            } else {
                continue; // not one of ours, nothing to update
            }
            // Only the most recent occurrence of the other word can improve the answer.
            if (lastPositionWord1 != -1 && lastPositionWord2 != -1) {
                minDistance = Math.min(minDistance,
                        Math.abs(lastPositionWord1 - lastPositionWord2));
            }
        }
        return (minDistance == Integer.MAX_VALUE) ? -1 : minDistance;
    }

    // -------- Approach 2: word-index distance on raw text --------

    public static int wordIndexDistanceInText(String paragraph, String word1, String word2) {
        // \W+ splits on any run of non-word characters, so punctuation is dropped for free.
        String[] words = paragraph.toLowerCase().split("\\W+");
        return wordIndexDistance(words, word1.toLowerCase(), word2.toLowerCase());
    }

    // -------- Shared helper for the midpoint approaches --------

    private static final Pattern WORD = Pattern.compile("[A-Za-z0-9']+");

    /** Midpoint = real start offset in the document + length / 2 ("bad" at offset 0 -> 1). */
    private static List<Integer>[] collectMidpoints(String document, String word1, String word2) {
        @SuppressWarnings("unchecked")
        List<Integer>[] midpoints = new List[]{new ArrayList<>(), new ArrayList<>()};
        Matcher m = WORD.matcher(document);
        while (m.find()) {
            String word = m.group().toLowerCase();
            int midpoint = m.start() + word.length() / 2;
            if (word.equals(word1)) midpoints[0].add(midpoint);
            else if (word.equals(word2)) midpoints[1].add(midpoint);
        }
        return midpoints;
    }

    // -------- Approach 3: character distance between midpoints, all pairs --------

    public static int midpointDistancePairwise(String document, String word1, String word2) {
        List<Integer>[] mids = collectMidpoints(document, word1.toLowerCase(), word2.toLowerCase());
        int shortest = Integer.MAX_VALUE;
        for (int pos1 : mids[0]) {
            for (int pos2 : mids[1]) {
                shortest = Math.min(shortest, Math.abs(pos1 - pos2));
            }
        }
        return shortest == Integer.MAX_VALUE ? -1 : shortest;
    }

    // -------- Approach 4: character distance between midpoints, one pass --------

    public static int midpointDistanceOnePass(String document, String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        int last1 = -1, last2 = -1, shortest = Integer.MAX_VALUE;
        Matcher m = WORD.matcher(document);
        while (m.find()) {
            String word = m.group().toLowerCase();
            int midpoint = m.start() + word.length() / 2;
            if (word.equals(word1)) last1 = midpoint;
            else if (word.equals(word2)) last2 = midpoint;
            else continue;
            // Midpoints arrive in increasing order, so the nearest partner is
            // always the last one seen.
            if (last1 != -1 && last2 != -1) shortest = Math.min(shortest, Math.abs(last1 - last2));
        }
        return shortest == Integer.MAX_VALUE ? -1 : shortest;
    }

    // -------- Demo --------

    private static final String DOCUMENT =
            "In publishing and graphic design, lorem ipsum is a filler text"
          + " commonly used to demonstrate the graphic elements"
          + " lorem ipsum text has been used in typesetting since the 1960s or"
          + " earlier, when it was popularized by advertisements"
          + " for Letraset transfer sheets. It was introduced to the Information"
          + " Age in the mid-1980s by Aldus Corporation, which";

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] words = {"the", "quick", "brown", "fox", "quick",
                          "jumps", "over", "the", "lazy", "dog"};

        print("case 1 array, quick vs the", wordIndexDistance(words, "quick", "the"), 1);
        print("case 2 array, quick vs missing word (edge)",
                wordIndexDistance(words, "quick", "missing"), -1);
        System.out.println();

        // Karat sample: the published answers 6 / 14 / 25 are CHARACTER distances
        // between midpoints. The word-index answer for the same pair is different,
        // which is exactly why you must pin down the definition before coding.
        String[][] cases = {
                // word1, word2, expected word-index distance, expected midpoint distance
                {"and", "graphic", "1", "6"},
                {"transfer", "it", "2", "14"},
                {"Design", "filler", "5", "25"},
        };
        int caseNumber = 3;
        for (String[] c : cases) {
            String pair = c[0] + " vs " + c[1];
            print("case " + caseNumber + " doc, " + pair + " wordIndex",
                    wordIndexDistanceInText(DOCUMENT, c[0], c[1]), c[2]);
            print("case " + caseNumber + " doc, " + pair + " midpoint pairwise",
                    midpointDistancePairwise(DOCUMENT, c[0], c[1]), c[3]);
            print("case " + caseNumber + " doc, " + pair + " midpoint one-pass",
                    midpointDistanceOnePass(DOCUMENT, c[0], c[1]), c[3]);
            caseNumber++;
        }
    }
}
