import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Problem: shortest distance between two given words in a text (Karat "shortest distance" question).
 *
 * Two different meanings of "distance" show up in interviews - always ask which one is wanted:
 *   1. Word-index distance   : how many words apart (LeetCode 243 "Shortest Word Distance").
 *   2. Character distance    : characters between the MIDPOINTS of the two words (the Karat variant;
 *                              its sample answers 6 / 14 / 25 only work with this meaning).
 *
 * Approaches:
 *   wordIndexDistance        - one pass over a word array, track last index of each word. O(n) time, O(1) space.
 *   wordIndexDistanceInText  - same idea on a raw paragraph: tokenize, case-insensitive.
 *   midpointDistancePairwise - collect every midpoint of each word, compare all pairs. O(p*q) after O(n) scan.
 *   midpointDistanceOnePass  - track the most recent midpoint of each word. O(n) time, O(1) extra space.
 *
 * Gotcha (this is what the earlier drafts got wrong): if you tokenize with split() and advance a running
 * offset by "length + 1", the offset drifts by one for every multi-character separator such as ", " or ". ".
 * Use a regex Matcher and read the real start offset instead.
 */
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
                continue;
            }
            // Only the most recent occurrence of the other word can improve the answer.
            if (lastPositionWord1 != -1 && lastPositionWord2 != -1) {
                minDistance = Math.min(minDistance, Math.abs(lastPositionWord1 - lastPositionWord2));
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
            // Midpoints arrive in increasing order, so the nearest partner is always the last one seen.
            if (last1 != -1 && last2 != -1) shortest = Math.min(shortest, Math.abs(last1 - last2));
        }
        return shortest == Integer.MAX_VALUE ? -1 : shortest;
    }

    // -------- Demo --------

    private static final String DOCUMENT =
            "In publishing and graphic design, lorem ipsum is a filler text commonly used to demonstrate the graphic elements"
          + " lorem ipsum text has been used in typesetting since the 1960s or earlier, when it was popularized by advertisements"
          + " for Letraset transfer sheets. It was introduced to the Information Age in the mid-1980s by Aldus Corporation, which";

    public static void main(String[] args) {
        String[] words = {"the", "quick", "brown", "fox", "quick", "jumps", "over", "the", "lazy", "dog"};
        System.out.println("wordIndexDistance(array, quick, the)     = " + wordIndexDistance(words, "quick", "the") + "  (expected 1)");
        System.out.println("wordIndexDistance(array, quick, missing) = " + wordIndexDistance(words, "quick", "missing") + " (expected -1)");
        System.out.println();

        // Karat sample: expected 6, 14, 25 - these are CHARACTER distances between midpoints.
        String[][] cases = {{"and", "graphic", "6"}, {"transfer", "it", "14"}, {"Design", "filler", "25"}};
        for (String[] c : cases) {
            System.out.printf("%-10s %-8s expected %-3s | wordIndex %-3d | midpointPairwise %-3d | midpointOnePass %-3d%n",
                    c[0], c[1], c[2],
                    wordIndexDistanceInText(DOCUMENT, c[0], c[1]),
                    midpointDistancePairwise(DOCUMENT, c[0], c[1]),
                    midpointDistanceOnePass(DOCUMENT, c[0], c[1]));
        }
    }
}
