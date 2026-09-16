import java.util.*;

/**
 * https://leetcode.com/problems/minimum-time-difference/description/
 * 539. Minimum Time Difference In Minutes
 * <p>
 * Given a list of 24-hour clock time points in "HH:MM" format, return the minimum
 * <p>
 * minutes difference between any two time-points in the list.
 * <p>
 * Example 1:
 * <p>
 * Input: timePoints = ["23:59","00:00"]
 * Output: 1
 * Example 2:
 * <p>
 * Input: timePoints = ["00:00","23:59","00:00"]
 * Output: 0
 */
class MinimumTimeDifference {
    // covert hours to minutes from 00:00 to 24:59
    public static int findMinDifference(List<String> timePoints) {
        List<Integer> minutes = new ArrayList<>();

        for (String time : timePoints) {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutesPart = Integer.parseInt(parts[1]);
            minutes.add(hours * 60 + minutesPart);
        }

        // Sort the minutes
        Collections.sort(minutes);

        // Calculate the minimum difference of adjacent elements
        int minDiff = Integer.MAX_VALUE;
        for (int i = 1; i < minutes.size(); i++)
            minDiff = Math.min(minDiff, minutes.get(i) - minutes.get(i - 1));

        // Include the circular difference between the last and first time
        minDiff = Math.min(minDiff, 1440 - minutes.get(minutes.size() - 1) + minutes.get(0));

        return minDiff; // 1
    }

    public static void main(String[] args) {
        // Example usage
        List<String> timePoints = Arrays.asList("23:59", "00:00", "12:34", "06:45");
        System.out.println("Minimum Time Difference: " + findMinDifference(timePoints));
    }
}
