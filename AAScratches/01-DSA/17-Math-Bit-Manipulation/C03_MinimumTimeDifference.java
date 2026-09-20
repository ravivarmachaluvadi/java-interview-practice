/*
 * =====================================================================
 *  Minimum Time Difference                        LeetCode 539 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of 24-hour clock times as "HH:MM" strings, return the
 *   smallest difference in minutes between any two of them. The clock is
 *   circular, so 23:59 and 00:00 are 1 minute apart, not 1439. Duplicate
 *   times are allowed and make the answer 0.
 *
 * EXAMPLE
 *   ["23:59", "00:00", "12:34", "06:45"]  ->  1     the midnight wrap pair
 *   ["00:00", "23:59", "00:00"]           ->  0     duplicates
 *   ["01:00", "13:00"]                    ->  720   the largest gap possible
 *   ["00:00", "04:00", "22:00"]           ->  120   22:00 -> 00:00 beats 00:00 -> 04:00
 *
 * APPROACH  (normalise to minutes, sort, then check the wraparound pair)
 *   1. Convert each "HH:MM" to minutes since midnight: HH * 60 + MM, giving a
 *      plain integer in [0, 1439].
 *   2. Sort. After sorting, the closest pair on a line must be adjacent, so a
 *      single scan of neighbouring differences covers every non-wrapping pair.
 *   3. The clock is a circle, not a line, so also measure the one pair the
 *      sort split apart: last to first, going forward through midnight, which
 *      is 1440 - last + first.
 *   4. Return the smallest of all of those.
 *
 * KEY INSIGHT
 *   Sorting turns "compare all pairs" into "compare neighbours", but sorting a
 *   CIRCULAR quantity always leaves exactly one pair unchecked - the two ends.
 *   Adding that single wraparound comparison is what makes the O(n log n)
 *   scan correct. The pigeonhole guard is the other half: with more than 1440
 *   times, two must land on the same minute, so the answer is 0 without any work.
 *
 * COMPLEXITY
 *   Time  O(n log n)  dominated by the sort (O(n) if you bucket into 1440 slots)
 *   Space O(n)        the list of normalised minute values
 *
 * INTERVIEW FOLLOW-UPS
 *   - Replace the sort with a boolean[1440] bucket array for true O(n).
 *   - Why is the wraparound pair always last-to-first, never any other pair?
 *   - Return the two times that achieve the minimum, not just the difference.
 *   - Same problem with seconds ("HH:MM:SS") or with a 12-hour AM/PM format.
 *
 * RUN
 *   main() runs 4 cases (wrap wins, duplicates, maximum gap, wrap beats an
 *   adjacent pair) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MinimumTimeDifference {

    private static final int MINUTES_PER_DAY = 24 * 60; // 1440

    public static int findMinDifference(List<String> timePoints) {
        // Pigeonhole: more times than minutes in a day means two collide.
        if (timePoints.size() > MINUTES_PER_DAY) {
            return 0;
        }

        List<Integer> minutes = new ArrayList<>();
        for (String time : timePoints) {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutesPart = Integer.parseInt(parts[1]);
            minutes.add(hours * 60 + minutesPart); // minutes since midnight
        }

        Collections.sort(minutes);

        // After sorting, the closest non-wrapping pair is adjacent.
        int minDiff = Integer.MAX_VALUE;
        for (int i = 1; i < minutes.size(); i++) {
            minDiff = Math.min(minDiff, minutes.get(i) - minutes.get(i - 1));
        }

        // The one pair the sort separated: last -> first, forward through midnight.
        int last = minutes.get(minutes.size() - 1);
        int first = minutes.get(0);
        minDiff = Math.min(minDiff, MINUTES_PER_DAY - last + first);

        return minDiff;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 wrap pair wins",
                findMinDifference(Arrays.asList("23:59", "00:00", "12:34", "06:45")), 1);
        print("case 2 duplicates (edge)",
                findMinDifference(Arrays.asList("00:00", "23:59", "00:00")), 0);
        print("case 3 two times, largest gap",
                findMinDifference(Arrays.asList("01:00", "13:00")), 720);
        print("case 4 wrap beats adjacent",
                findMinDifference(Arrays.asList("00:00", "04:00", "22:00")), 120);
    }
}
