import java.util.*;

// Solution class containing the ZScore function
class Solution {
    // Function to calculate the maximum
    // Z-score using binary search
    public int ZScore(int[] marks, int k) {
        int n = marks.length;

        // Sort the marks in descending order
        Arrays.sort(marks);
        int left = 1, right = n, ans = 0;

        // Binary search to find the maximum Z-score
        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Check if the current mid value satisfies the condition for Z-score
            if (marks[mid - 1] >= mid * k) {
                ans = mid;  // Update the answer
                left = mid + 1;  // Try for a higher Z-score
            } else {
                right = mid - 1;  // Try for a smaller Z-score
            }
        }

        return ans;  // Return the maximum Z-score found
    }
}

class ZScore {
    public static void main(String[] args) {
        // Test case input
        int[] marks = {45, 60, 70, 80, 90, 95};
        int k = 10;

        Solution sol = new Solution();
        int result = sol.ZScore(marks, k);

        // Print result (calculated Z-score)
        System.out.println("Calculated Z-score: " + result);
    }
}
