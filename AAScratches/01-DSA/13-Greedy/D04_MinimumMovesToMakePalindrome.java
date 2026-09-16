import java.util.*;

// https://leetcode.com/problems/minimum-number-of-moves-to-make-palindrome/description/
// 2193. Minimum Number of Moves to Make Palindrome
// s can be converted to a palindrome using a finite number of moves.
class MinimumMovesToMakePalindrome {
    public static int minMovesToMakePalindrome(String s) {
        int n = s.length();
        char[] arr = s.toCharArray();
        // 1. Check counts
        int[] freq = new int[26];
        for (char c : arr) {
            freq[c - 'a']++;
        }
        int oddCount = 0;
        for (int f : freq) {
            if ((f & 1) != 0) oddCount++;
        }
        if (oddCount > 1) return -1;

        int i = 0, j = n - 1;
        int moves = 0;
        while (i < j) {
            if (arr[i] == arr[j]) {
                i++;
                j--;
            } else {
                int k = j;
                // find matching partner for arr[i] from right side
                while (k > i && arr[k] != arr[i]) {
                    k--;
                }
                if (k == i) {
                    // no matching partner, so arr[i] must go to middle
                    // swap arr[i] and arr[i+1]
                    char tmp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = tmp;
                    moves++;
                } else {
                    // found partner at k < j
                    for (int t = k; t < j; t++) {
                        // swap arr[t] and arr[t+1]
                        char tmp = arr[t];
                        arr[t] = arr[t + 1];
                        arr[t + 1] = tmp;
                        moves++;
                    }
                    i++;
                    j--;
                }
            }
        }
        return moves;
    }

    // sample main
    public static void main(String[] args) {
        String s1 = "zzazz";
        String s2 = "mbadm";
        String s3 = "leetcode";

        System.out.println("Input: " + s1 + " → Output: " + minMovesToMakePalindrome(s1));
        System.out.println("Input: " + s2 + " → Output: " + minMovesToMakePalindrome(s2));
        System.out.println("Input: " + s3 + " → Output: " + minMovesToMakePalindrome(s3));
    }
}
