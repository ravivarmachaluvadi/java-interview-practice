/*
 * =====================================================================
 *  Semordnilap Checker                          AlgoExpert-style | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A semordnilap is a word whose reverse is a different valid word in the same
 *   list ("stressed" / "desserts"). Given an array of distinct words, return every
 *   word that has its reverse elsewhere in the array. Palindromes ("level") do not
 *   count because the reverse is the same word, not a different one.
 *
 * EXAMPLE
 *   ["desserts","stressed","diaper","repaid","drawer","reward","gateman","nametag",
 *    "deliver","reviled"]            ->  all ten words (five pairs)
 *   ["level","noon","abc"]           ->  []    palindromes and singles are excluded
 *   ["abc","cba","xyz"]              ->  [abc, cba]
 *   []                               ->  []
 *
 * APPROACH  (reverse plus HashSet lookup)
 *   1. Put every word into a HashSet for O(1) membership.
 *   2. For each word, build its reverse with StringBuilder.reverse().
 *   3. If reverse != word and the set contains the reverse, record both words.
 *   4. Return the result set; main() prints it sorted so the output is stable.
 *
 * KEY INSIGHT
 *   Pair-finding over a list: precompute the set once, then ask "is my partner
 *   present?" per element instead of comparing every pair (O(n^2)). The same
 *   shape drives two-sum. The extra rule here is the self-partner exclusion.
 *
 * COMPLEXITY
 *   Time  O(n * L)  n words of average length L: reversing and hashing each word
 *   Space O(n * L)  the lookup set and the result set
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return pairs instead of a flat set? Add [word, reverse] only when
 *     word.compareTo(reverse) < 0 so each pair appears once.
 *   - Input may contain duplicates? A duplicate is not its own semordnilap; the
 *     set collapses duplicates so the reverse check is unaffected.
 *   - Case-insensitive matching? Normalise to lower case before inserting.
 *
 * RUN
 *   main() runs 4 cases (typical, palindromes only, one pair among strangers, empty)
 *   and prints actual vs expected, with sets sorted for a stable comparison.
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

class SemordnilapChecker {

    public static Set<String> findSemordnilaps(String[] words) {
        Set<String> semordnilaps = new HashSet<>();
        // one set for O(1) "is the reverse also a word?" lookups
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));

        for (String word : words) {
            String reversed = new StringBuilder(word).reverse().toString();
            // a palindrome reverses to itself, which is not a different word
            if (!word.equals(reversed) && wordSet.contains(reversed)) {
                semordnilaps.add(word);
                semordnilaps.add(reversed);
            }
        }
        return semordnilaps;
    }

    private static void print(String label, Set<String> actual, Object expected) {
        // HashSet iteration order is unspecified; sort for a stable printout
        System.out.println(label + ": " + new TreeSet<>(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] tenWords = {
                "desserts", "stressed", "diaper", "repaid",
                "drawer", "reward", "gateman", "nametag",
                "deliver", "reviled"
        };
        print("case 1", findSemordnilaps(tenWords),
                "[deliver, desserts, diaper, drawer, gateman, nametag, "
                        + "repaid, reviled, reward, stressed]");
        print("case 2", findSemordnilaps(new String[]{"level", "noon", "abc"}), "[]");
        print("case 3", findSemordnilaps(new String[]{"abc", "cba", "xyz"}), "[abc, cba]");
        print("case 4", findSemordnilaps(new String[]{}), "[]");
    }
}
