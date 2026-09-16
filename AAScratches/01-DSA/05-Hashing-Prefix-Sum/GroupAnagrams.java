/**
 * Problem: Group a list of strings into collections where each collection contains
 * strings that are anagrams of one another.
 *
 * Approach: For every string, sort its characters to produce a canonical key.
 * Use a hash map from this sorted key to a list of original strings. After processing
 * all strings, return the collection of lists stored in the map.
 *
 * Time Complexity: O(n * k log k) where n is number of strings and k is average string length,
 * due to sorting each string.
 * Space Complexity: O(n * k) for storing the hash map entries and result lists.
 */
import java.util.*;

class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        if (strs == null || strs.length == 0) return new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            // Sort the string to form a key
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String key = new String(charArray);

            // Add the original string to the correct group
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        GroupAnagrams solution = new GroupAnagrams();
        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = solution.groupAnagrams(strs);

        System.out.println(result);
    }
}

