/*
 * =====================================================================
 *  Repeated DNA Sequences                               LeetCode 187 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A DNA string uses only the letters A, C, G, T. Return every 10-letter substring that
 *   occurs more than once, each listed once, in any order. Length is at most 10^5; strings
 *   shorter than 10 have no windows and return an empty list.
 *
 * EXAMPLE
 *   "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"  ->  [AAAAACCCCC, CCCCCAAAAA]
 *   "AAAAAAAAAAAAA"                     ->  [AAAAAAAAAA]   four identical windows, reported once
 *   "ACGT"                              ->  []             shorter than 10, no windows
 *   "ACGTACGTAC"                        ->  []             exactly 10 chars, one window, no repeat
 *
 * APPROACH  (fixed window hashing, seen vs repeats)
 *   1. Slide a 10-char window: start i runs while i + 9 < n, i.e. n - 9 windows.
 *   2. seen.add(window) returns false when the window was already present.
 *   3. On false, put the window in a second set, repeats, so each answer appears once
 *      even if it occurs three or more times.
 *   4. Return repeats as a list.
 *
 * KEY INSIGHT
 *   Set.add's boolean is a free "have I seen this before?" test - no containsKey then put.
 *   Two sets, not one, because "seen once" and "seen again" are different facts and the
 *   output must not duplicate a window that repeats many times.
 *
 * COMPLEXITY
 *   Time  O(n * 10) = O(n)   n - 9 windows, each substring + hash costs 10 chars
 *   Space O(n * 10) = O(n)   worst case every window is distinct and stored
 *
 * INTERVIEW FOLLOW-UPS
 *   - Drop the substring copies: 2 bits per letter packs a window into a 20-bit int; roll
 *     it with (hash << 2 | code) & mask so each window costs O(1) - true O(n) time and space.
 *   - General alphabet or long windows: Rabin-Karp rolling hash with a modulus.
 *   - Return the count of each repeated window: Map<String, Integer> instead of two sets.
 *   - Streaming input: same two-set logic works online, one window at a time.
 *
 * RUN
 *   main() runs 4 cases (typical, many repeats, too short, exactly 10) and prints
 *   actual vs expected. Results are sorted before printing because HashSet order is arbitrary.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RepeatedDnaSequences {

    private static final int WINDOW = 10;

    public static List<String> findRepeatedDnaSequences(String s) {
        Set<String> seen = new HashSet<>();
        Set<String> repeats = new HashSet<>();
        // Last window starts at n - WINDOW; when n < WINDOW the loop body never runs.
        for (int start = 0; start + WINDOW <= s.length(); start++) {
            String window = s.substring(start, start + WINDOW);
            if (!seen.add(window)) {      // add() is false when the window was already seen
                repeats.add(window);      // a Set here keeps each repeated window once
            }
        }
        return new ArrayList<>(repeats);
    }

    private static void print(String label, String dna, String expected) {
        List<String> actual = findRepeatedDnaSequences(dna);
        Collections.sort(actual);   // stable order for display only
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)     ", "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT",
                "[AAAAACCCCC, CCCCCAAAAA]");
        print("case 2 (many repeats)", "AAAAAAAAAAAAA", "[AAAAAAAAAA]");
        print("case 3 (too short)   ", "ACGT", "[]");
        print("case 4 (exactly 10)  ", "ACGTACGTAC", "[]");
    }
}
