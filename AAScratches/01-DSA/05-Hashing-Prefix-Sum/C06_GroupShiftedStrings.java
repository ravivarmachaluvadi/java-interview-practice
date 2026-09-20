/*
 * =====================================================================
 *  Group Shifted Strings                          LeetCode 249 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A "shift" moves every letter of a string forward by one, wrapping z -> a
 *   ("abc" -> "bcd", "xyz" -> "yza"). Given an array of lowercase strings, group the
 *   strings that are shifts of one another. Groups may be returned in any order.
 *
 * EXAMPLE
 *   ["abc","bcd","acef","aef","xyz","az","ba","a","z"]
 *       ->  [[a, z], [abc, bcd, xyz], [acef], [aef], [az, ba]]
 *   []                          ->  []
 *   ["ab","yz","za","zy"]       ->  [[ab, yz, za], [zy]]   "za" wraps: z -> a is +1 mod 26
 *
 * APPROACH  (consecutive-difference key, mod 26)
 *   1. For each string build a key from the gaps between neighbouring letters:
 *      diff = s[i] - s[i-1]; if diff < 0 add 26 so that z -> a counts as +1, not -25.
 *   2. Append each diff plus a "," separator, so "1,1," (abc) is distinct from "11," (al).
 *   3. Bucket the original string under that key with computeIfAbsent, return map.values().
 *   Single-letter strings have no gaps, so they all share the empty key "" and group together.
 *
 * KEY INSIGHT
 *   Shifting adds the same constant to every letter, so the differences between adjacent
 *   letters are invariant under shift. Hash on the invariant, not on the letters.
 *   Same bucket-by-canonical-key skeleton as Group Anagrams; the work is inventing the
 *   invariant and remembering the wraparound (+26).
 *
 * COMPLEXITY
 *   Time  O(n * k)  one pass over each of n strings of length k to build its key
 *   Space O(n * k)  every string stored once, keys up to ~3k chars each
 *
 * INTERVIEW FOLLOW-UPS
 *   - Alternative key: shift the string so its first letter is 'a' ("bcd" -> "abc").
 *   - Why the separator? Without it, diffs 1,11 and 11,1 both become "111".
 *   - Uppercase or mixed alphabet: the modulus changes from 26 to the alphabet size.
 *
 * RUN
 *   main() runs 3 cases (typical, empty, wraparound) and prints actual vs expected.
 *   Groups are sorted before printing so HashMap order cannot change the output.
 */
import java.util.*;

class GroupShiftedStrings {

    public static List<List<String>> groupStrings(String[] strings) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String str : strings) {
            groups.computeIfAbsent(shiftKey(str), k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(groups.values());
    }

    /** Gaps between neighbouring letters, normalised to 0..25 so z -> a reads as +1. */
    private static String shiftKey(String str) {
        StringBuilder key = new StringBuilder();
        for (int i = 1; i < str.length(); i++) {
            int diff = str.charAt(i) - str.charAt(i - 1);
            if (diff < 0) diff += 26;   // wraparound: 'a' - 'z' = -25 becomes +1
            key.append(diff).append(',');
        }
        return key.toString();
    }

    /** Sorts each group and the list of groups so HashMap iteration order cannot change output. */
    private static List<List<String>> normalized(List<List<String>> groups) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> group : groups) {
            List<String> sorted = new ArrayList<>(group);
            Collections.sort(sorted);
            copy.add(sorted);
        }
        copy.sort(Comparator.comparing(Object::toString));
        return copy;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] typical = {"abc", "bcd", "acef", "aef", "xyz", "az", "ba", "a", "z"};
        String[] empty = {};
        String[] wraparound = {"ab", "yz", "za", "zy"};

        print("case 1 typical   ", normalized(groupStrings(typical)),
                "[[a, z], [abc, bcd, xyz], [acef], [aef], [az, ba]]");
        print("case 2 empty     ", normalized(groupStrings(empty)), "[]");
        print("case 3 wraparound", normalized(groupStrings(wraparound)), "[[ab, yz, za], [zy]]");
    }
}
