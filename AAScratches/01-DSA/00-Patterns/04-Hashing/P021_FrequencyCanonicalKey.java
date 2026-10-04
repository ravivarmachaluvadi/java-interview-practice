/*
 * =====================================================================
 *  P021 HashMap: Frequency Count + Canonical Key   Canonical LC 49 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 49, Group Anagrams)
 *   Group the strings that are anagrams of each other. Any order of groups and of the
 *   strings inside a group is accepted.
 *
 * EXAMPLE
 *   ["eat","tea","tan","ate","nat","bat"]  ->  [[ate,eat,tea],[bat],[nat,tan]]
 *   [""]                                   ->  [[]]
 *
 * RECOGNIZE WHEN
 *   - "anagram", "same letters", "can be built from", "same pattern", "same shape".
 *   - Group items that are "equal up to" reordering, renaming or shifting.
 *   - You need counts of each value to compare two collections.
 *   Not this if: the comparison is over a sliding window of a longer string ->
 *   P016_FixedWindowFrequencyMatch; you need a pair, not a group -> P020_ComplementLookup.
 *
 * TEMPLATE
 *   groups = {}
 *   for item in items:
 *       key = canonical(item)        // sorted letters, 26 counts, shift diffs, first-seen ids
 *       groups[key].append(item)
 *   compare two collections: count[x]++ for one, count[x]-- for the other, all must be 0
 *
 * APPROACH
 *   1. Two words are anagrams iff their letter counts match.
 *   2. Build a key from the 26 counts (or the sorted letters) and group by it.
 *
 * KEY INSIGHT
 *   Define a function that maps every member of an equivalence class to the SAME value and
 *   different classes to different values; then a HashMap does the grouping. Pick the key
 *   for the relation: counts for anagrams, letter-to-letter differences for shifts, and
 *   "index of first occurrence" for one-to-one renaming (isomorphism).
 *
 * COMPLEXITY
 *   Time O(n * L) with a count key (O(n * L log L) with sorting), space O(n * L).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 242  Valid Anagram            +1 for s, -1 for t, all counts must be 0
 *   [coded] LC 383  Ransom Note              counts of magazine must cover the note
 *   [coded] LC 205  Isomorphic Strings       key = first-occurrence index of each char;
 *                                            "egg" and "add" both become 0,1,1
 *   [coded] LC 290  Word Pattern             the same key over letters vs over words
 *   [coded] LC 249  Group Shifted Strings    key = (c[i] - c[0] + 26) % 26 for every i
 *           LC 890  Find and Replace Pattern LC 205 key against each word
 *           LC 1657 Close Strings            same set of letters AND same sorted counts
 *           LC 387  First Unique Character   count first, then scan for count == 1
 *           LC 1207 Unique Occurrences       set of the counts has the same size as counts
 *           LC 451  Sort Characters by Frequency  counts + buckets -> P043_TopK
 *
 * PITFALLS
 *   - Isomorphism is a BIJECTION: one map a -> b is not enough ("badc" vs "baba").
 *   - A key built by joining counts needs a separator ("1#12" vs "11#2").
 *   - Shift keys wrap around: use (diff + 26) % 26.
 *
 * DEEP DIVE
 *   C05_GroupAnagrams, C06_GroupShiftedStrings, C07_FindAndReplacePattern, A03_RansomNote
 *   (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FrequencyCanonicalKey {

    // Canonical LC 49. Key = the 26 letter counts.
    static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strs) {
            int[] count = new int[26];
            for (char c : s.toCharArray()) {
                count[c - 'a']++;
            }
            groups.computeIfAbsent(Arrays.toString(count), k -> new ArrayList<>()).add(s);
        }
        return sorted(groups.values());
    }

    // LC 242.
    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }
        for (int c : count) {
            if (c != 0) {
                return false;
            }
        }
        return true;
    }

    // LC 383: every letter of the note must be available in the magazine.
    static boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26];
        for (char c : magazine.toCharArray()) {
            count[c - 'a']++;
        }
        for (char c : ransomNote.toCharArray()) {
            if (--count[c - 'a'] < 0) {
                return false;
            }
        }
        return true;
    }

    // Canonical key for renaming: position i -> index where that symbol first appeared.
    static List<Integer> firstSeenKey(List<String> symbols) {
        Map<String, Integer> first = new HashMap<>();
        List<Integer> key = new ArrayList<>();
        for (String sym : symbols) {
            first.putIfAbsent(sym, first.size());
            key.add(first.get(sym));
        }
        return key;
    }

    // LC 205.
    static boolean isIsomorphic(String s, String t) {
        List<Integer> keyS = firstSeenKey(Arrays.asList(s.split("")));
        return keyS.equals(firstSeenKey(Arrays.asList(t.split(""))));
    }

    // LC 290: letters of the pattern vs words of s.
    static boolean wordPattern(String pattern, String s) {
        List<String> words = Arrays.asList(s.split(" "));
        List<String> letters = Arrays.asList(pattern.split(""));
        return letters.size() == words.size() && firstSeenKey(letters).equals(firstSeenKey(words));
    }

    // LC 249: key = differences to the first letter, mod 26.
    static List<List<String>> groupStrings(String[] strings) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strings) {
            StringBuilder key = new StringBuilder();
            for (int i = 1; i < s.length(); i++) {
                key.append((s.charAt(i) - s.charAt(0) + 26) % 26).append(',');
            }
            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(s);
        }
        return sorted(groups.values());
    }

    // Groups in any order are accepted; sort for a stable printout.
    static List<List<String>> sorted(Iterable<List<String>> groups) {
        List<List<String>> out = new ArrayList<>();
        for (List<String> g : groups) {
            List<String> copy = new ArrayList<>(g);
            Collections.sort(copy);
            out.add(copy);
        }
        out.sort((a, b) -> a.toString().compareTo(b.toString()));
        return out;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 49 six words",
                groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}),
                "[[ate, eat, tea], [bat], [nat, tan]]");
        check("LC 49 empty string", groupAnagrams(new String[]{""}), "[[]]");
        check("LC 49 single", groupAnagrams(new String[]{"a"}), "[[a]]");

        check("LC 242 anagram/nagaram", isAnagram("anagram", "nagaram"), true);
        check("LC 242 rat/car", isAnagram("rat", "car"), false);

        check("LC 383 a from b", canConstruct("a", "b"), false);
        check("LC 383 aa from ab", canConstruct("aa", "ab"), false);
        check("LC 383 aa from aab", canConstruct("aa", "aab"), true);

        check("LC 205 egg/add", isIsomorphic("egg", "add"), true);
        check("LC 205 foo/bar", isIsomorphic("foo", "bar"), false);
        check("LC 205 paper/title", isIsomorphic("paper", "title"), true);
        check("LC 205 badc/baba bijection trap", isIsomorphic("badc", "baba"), false);

        check("LC 290 abba / dog cat cat dog", wordPattern("abba", "dog cat cat dog"), true);
        check("LC 290 abba / dog cat cat fish", wordPattern("abba", "dog cat cat fish"), false);
        check("LC 290 abba / dog dog dog dog", wordPattern("abba", "dog dog dog dog"), false);
        check("LC 290 aaa / three words, different", wordPattern("aaa", "dog cat dog"), false);

        check("LC 249 eight strings",
                groupStrings(new String[]{"abc", "bcd", "acef", "xyz", "az", "ba", "a", "z"}),
                "[[a, z], [abc, bcd, xyz], [acef], [az, ba]]");
    }
}
