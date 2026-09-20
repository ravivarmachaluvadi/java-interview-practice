/*
 * =====================================================================
 *  Unique Tuples (distinct substrings of length k)          Classic warm-up | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string and a length k, return the set of distinct substrings of exactly length k.
 *   A null input returns null. If k is larger than the string there are no windows, so the
 *   result is the empty set. k <= 0 is treated as invalid and returns the empty set.
 *
 * EXAMPLE
 *   "aab",     k = 2  ->  {aa, ab}
 *   "abbccde", k = 2  ->  {ab, bb, bc, cc, cd, de}
 *   "aaaa",    k = 2  ->  {aa}          three windows, all the same
 *   "ab",      k = 5  ->  {}            k longer than the string, zero windows
 *   "abc",     k = 3  ->  {abc}         k equals the length, exactly one window
 *
 * APPROACH  (fixed-size sliding window into a HashSet)
 *   1. There are n - k + 1 windows; the last one starts at index n - k.
 *   2. For each start i, cut substring(i, i + k) and add it to a HashSet.
 *   3. The set silently drops repeats, so its contents are the answer.
 *
 * KEY INSIGHT
 *   Fixed-length windows are counted by n - k + 1, not n - k; off-by-one here drops the last
 *   window. The Set does the deduplication, the loop does the enumeration - keep the two jobs
 *   separate and this generalises to k-mers, n-grams and DNA sequences (see B06).
 *
 * COMPLEXITY
 *   Time  O(n * k)  n - k + 1 windows, each substring copy and hash costs O(k)
 *   Space O(n * k)  worst case every window is distinct and stored
 *
 * INTERVIEW FOLLOW-UPS
 *   - Which windows repeat, not which exist: use the boolean return of Set.add (B06).
 *   - Make it O(n): rolling hash (Rabin-Karp) so each window's hash comes from the previous.
 *   - Count occurrences of each window: Map<String, Integer> instead of Set.
 *   - Preserve first-seen order: LinkedHashSet.
 *
 * RUN
 *   main() runs 5 cases (typical, all-equal, k too long, k == n, second typical) and prints
 *   actual vs expected. Results are sorted before printing because HashSet order is arbitrary.
 */
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

class UniqueTuples {

    public static Set<String> uniqueTuples(String input, int length) {
        if (input == null) return null;
        Set<String> tuples = new HashSet<>();
        if (length <= 0) return tuples;

        int windowCount = input.length() - length + 1;   // may be <= 0 when length > input
        for (int start = 0; start < windowCount; start++) {
            tuples.add(input.substring(start, start + length));
        }
        return tuples;
    }

    private static void print(String label, String input, int k, String expected) {
        Set<String> actual = uniqueTuples(input, k);
        Object shown = actual == null ? null : new TreeSet<>(actual);   // stable order for display
        System.out.println(label + ": " + shown + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)   ", "aab", 2, "[aa, ab]");
        print("case 2 (typical)   ", "abbccde", 2, "[ab, bb, bc, cc, cd, de]");
        print("case 3 (all equal) ", "aaaa", 2, "[aa]");
        print("case 4 (k > n)     ", "ab", 5, "[]");
        print("case 5 (k == n)    ", "abc", 3, "[abc]");
    }
}
