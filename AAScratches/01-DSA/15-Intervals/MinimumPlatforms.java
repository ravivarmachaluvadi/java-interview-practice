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