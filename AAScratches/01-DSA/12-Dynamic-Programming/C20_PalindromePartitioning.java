/**
 * Input: s = "aab"
 * <p>
 * Output: [["a","a","b"],["aa","b"]]
 */

// 131. Palindrome Partitioning
// https://leetcode.com/problems/palindrome-partitioning/description/

import java.util.*;

class PalindromePartitioning {

    public List<List<String>> partition(String s) {
        List<List<String>> list = new ArrayList<>();
        List<String> path = new ArrayList<>();
        func(0, s, path, list);
        return list;
    }

    static void func(int index, String s, List<String> path, List<List<String>> list) {
        // base case: if we reach the end of the string
        if (index == s.length()) {
            list.add(new ArrayList<>(path));
            return;
        }

        // explore partitions from current index to the end
        for (int i = index; i < s.length(); i++) {
            if (isPalindrome(s, index, i)) {
                // pick substring that forms a palindrome
                path.add(s.substring(index, i + 1));
                func(i + 1, s, path, list);
                // backtrack
                path.remove(path.size() - 1);
            }
        }
    }

    static boolean isPalindrome(String s, int start, int end) {
        while (start < end) {
            if (s.charAt(start) != s.charAt(end))
                return false;
            start++;
            end--;
        }
        return true;
    }

    public static void main(String[] args) {
        PalindromePartitioning obj = new PalindromePartitioning();

        String s = "aab";
        List<List<String>> result = obj.partition(s);

        System.out.println("Input: " + s);
        System.out.println("Palindrome partitions:");
        for (List<String> partition : result) {
            System.out.println(partition);
        }
    }
}
