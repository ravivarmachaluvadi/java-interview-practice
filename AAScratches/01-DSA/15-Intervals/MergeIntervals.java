/**
 * Problem: Given a list of intervals with start and end points, merge all overlapping
 * intervals so that the resulting list contains only mutually disjoint intervals.
 *
 * Approach: Sort intervals by their start value. Iterate through sorted intervals,
 * keeping a list of merged intervals. For each interval, if it does not overlap
 * with the last merged interval, append it; otherwise update the end of the last
 * merged interval to the maximum of both ends.
 *
 * Time Complexity: O(n log n) due to sorting (n = number of intervals).
 * Space Complexity: O(n) for the output list and auxiliary space used by sort.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Interval {
    int start;
    int end;
}

class MergeIntervals {
    public List<Interval> merge(List<Interval> intervals) {
        // sort interval list by start value
        Collections.sort(intervals, (i1, i2) -> i1.start - i2.start);

        // merge intervals
        List<Interval> merged = new ArrayList<>();
        for (Interval interval : intervals) {
            // if list is empty or current interval does not overlap with previous, simply append it
            if (merged.isEmpty() || merged.get(merged.size() - 1).end < interval.start) {
                merged.add(interval);
            }
            // otherwise, there is overlap, so we update the end of the previous interval if necessary
            else {
                merged.get(merged.size() - 1).end = Math.max(merged.get(merged.size() - 1).end, interval.end);
            }
        }
        return merged;
    }

    public static void main(String[] args) {
        // Build example input
        List<Interval> input = new ArrayList<>();
        Interval a = new Interval(); a.start = 1; a.end = 3;
        Interval b = new Interval(); b.start = 2; b.end = 6;
        Interval c = new Interval(); c.start = 8; c.end = 10;
        Interval d = new Interval(); d.start = 15; d.end = 18;
        input.add(a); input.add(b); input.add(c); input.add(d);

        // Call merge
        MergeIntervals mi = new MergeIntervals();
        List<Interval> output = mi.merge(input);

        // Print input
        System.out.println("Input intervals:");
        for (Interval i : input) {
            System.out.println("[" + i.start + ", " + i.end + "]");
        }

        // Print output
        System.out.println("\nMerged intervals:");
        for (Interval i : output) {
            System.out.println("[" + i.start + ", " + i.end + "]");
        }
    }
}