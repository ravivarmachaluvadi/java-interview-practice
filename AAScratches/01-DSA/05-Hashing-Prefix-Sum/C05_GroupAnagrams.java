/*
 * =====================================================================
 *  Group Anagrams                                LeetCode 49 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of strings, group the ones that are anagrams of each other.
 *   Two strings are anagrams when they contain the same letters with the same counts.
 *   Groups may be returned in any order. Strings are lowercase a-z, up to 100 chars each.
 *
 * EXAMPLE
 *   ["eat","tea","tan","ate","nat","bat"]  ->  [[ate, eat, tea], [bat], [nat, tan]]
 *   []                                     ->  []
 *   ["a","","b",""]                        ->  [[, ], [a], [b]]   the two empty strings group
 *
 * APPROACH  (canonical key by sorting)
 *   1. For each string, sort its characters: "eat" -> "aet". Every anagram of "eat" sorts
 *      to the same "aet", so the sorted form is a canonical key for the whole group.
 *   2. map.computeIfAbsent(key, k -> new ArrayList<>()).add(str) appends the original
 *      string to its bucket, creating the bucket the first time the key is seen.
 *   3. Return map.values() as the list of groups.
 *
 *   Alternative in this file (count-signature key): count letters into int[26] and build
 *   a key like "#1#0#0...#1". O(k) per string instead of O(k log k). Same skeleton,
 *   different key function.
 *
 * KEY INSIGHT
 *   "Group equivalent things" == "map each thing to a canonical key, bucket by key".
 *   The whole problem is choosing a key function where equivalent inputs collide and
 *   non-equivalent inputs do not. Sorting is the lazy universal key; a count signature
 *   is the faster one when the alphabet is small and fixed.
 *
 * COMPLEXITY
 *   Time  O(n * k log k)  sort each of n strings of length k   (O(n * k) with count key)
 *   Space O(n * k)        every string stored once in a bucket, plus one key per group
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can the raw int[26] not be the map key? (arrays hash by identity, not content;
 *     use a String signature or List<Integer>)
 *   - Unicode input: int[26] no longer fits, fall back to the sorted-string key.
 *   - LC 242 Valid Anagram is the two-string base case; LC 438 is the sliding-window cousin.
 *
 * RUN
 *   main() runs 3 cases (typical, empty, empty-strings) through both key functions and
 *   prints actual vs expected. Groups are sorted before printing so output is stable.
 */
import java.util.*;

class GroupAnagrams {

    /** Author's approach: sorted characters as the bucket key. */
    public List<List<String>> groupAnagrams(String[] strs) {
        if (strs == null || strs.length == 0) return new ArrayList<>();
        Map<String, List<String>> groups = new HashMap<>();
        for (String str : strs) {
            groups.computeIfAbsent(sortedKey(str), k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(groups.values());
    }

    private static String sortedKey(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    /** Alternative: letter-count signature as the key, O(k) per string instead of O(k log k). */
    public List<List<String>> groupAnagramsByCount(String[] strs) {
        if (strs == null || strs.length == 0) return new ArrayList<>();
        Map<String, List<String>> groups = new HashMap<>();
        for (String str : strs) {
            groups.computeIfAbsent(countKey(str), k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(groups.values());
    }

    private static String countKey(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        StringBuilder key = new StringBuilder();
        // the '#' separator keeps counts [1,11] distinct from [11,1]
        for (int c : count) key.append('#').append(c);
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
        GroupAnagrams solution = new GroupAnagrams();
        String[] typical = {"eat", "tea", "tan", "ate", "nat", "bat"};
        String[] empty = {};
        String[] emptyStrings = {"a", "", "b", ""};

        print("case 1 sorted-key ", normalized(solution.groupAnagrams(typical)),
                "[[ate, eat, tea], [bat], [nat, tan]]");
        print("case 1 count-key  ", normalized(solution.groupAnagramsByCount(typical)),
                "[[ate, eat, tea], [bat], [nat, tan]]");
        print("case 2 sorted-key ", normalized(solution.groupAnagrams(empty)), "[]");
        print("case 2 count-key  ", normalized(solution.groupAnagramsByCount(empty)), "[]");
        print("case 3 sorted-key ", normalized(solution.groupAnagrams(emptyStrings)),
                "[[, ], [a], [b]]");
        print("case 3 count-key  ", normalized(solution.groupAnagramsByCount(emptyStrings)),
                "[[, ], [a], [b]]");
    }
}
