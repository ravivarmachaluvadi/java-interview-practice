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
}