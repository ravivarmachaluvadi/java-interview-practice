/**
 * Problem: Given arrival and departure times of trains at a station,
 * determine the minimum number of platforms needed so that no train has to wait.
 *
 * Approach: Sort both arrival and departure arrays. Use two pointers to
 * simulate time progression: if an arrival is earlier or equal to the next
 * departure, increment platform count; otherwise decrement it. Track the
 * maximum platforms used at any moment.
 *
 * Time Complexity: O(n log n) due to sorting of the two arrays.
 * Space Complexity: O(1) auxiliary space (in-place sort and constant variables).
 */
import java.util.*;

class MinimumPlatforms {

    private static int findPlatform(int[] arr, int[] dep, int n) {
        Arrays.sort(arr);
        Arrays.sort(dep);
        int minPlatforms = 0;  // stores the answer
        int platForms = 0;     // current platforms in use

        int i = 0, j = 0;
        // process all trains
        while (i < n && j < n) {
            if (arr[i] <= dep[j]) {
                platForms++;   // new train arrives
                i++;
                minPlatforms = Math.max(minPlatforms, platForms);
            } else {
                platForms--;   // train departs
                j++;
            }
        }

        // no need for extra loops, since arrivals == departures == n
        return minPlatforms;
    }

    public static void main(String[] args) {

        int[] arr = {900, 945, 955, 1100, 1500, 1800};
        int[] dep = {920, 1200, 1130, 1150, 1900, 2000};
        int n = arr.length;
        int totalCount = findPlatform(arr, dep, n);
        System.out.println("Minimum number of Platforms required " + totalCount);
    }
}