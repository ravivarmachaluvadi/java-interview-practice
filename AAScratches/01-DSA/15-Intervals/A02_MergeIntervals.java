/*
 * =====================================================================
 *  Merge Intervals                              LeetCode 56 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a collection of intervals, merge every group that overlaps and return
 *   the resulting list of mutually disjoint intervals. The input is in no
 *   particular order. Intervals that merely touch (end == next start) are merged
 *   here, which is what LeetCode 56 expects.
 *
 * EXAMPLE
 *   [[1,3],[2,6],[8,10],[15,18]]  ->  [[1,6],[8,10],[15,18]]   1-3 and 2-6 overlap
 *   [[1,4],[4,5]]                 ->  [[1,5]]                  touching still merges
 *   [[5,10],[1,20],[2,3]]         ->  [[1,20]]                 one interval swallows all
 *   []                            ->  []                       nothing to merge
 *
 * APPROACH  (sort by start, fold into an accumulator)
 *   1. Sort the intervals by start value.
 *   2. Walk the sorted list keeping a result list whose last entry is the
 *      interval currently being built.
 *   3. If the current interval starts after the last result ends, it cannot touch
 *      anything already merged, so append a COPY of it and move on.
 *   4. Otherwise they overlap, so stretch the last result's end to
 *      max(last.end, current.end). The start never needs updating: sorting
 *      guarantees last.start is already the smaller one.
 *
 * KEY INSIGHT
 *   Sorting by start turns a global "which intervals overlap which" question into
 *   a local one: each interval only ever has to be compared with the single
 *   interval being accumulated. The whole pattern is three lines - sort, compare
 *   against the last kept interval, extend its end - and Insert Interval, Non
 *   Overlapping Intervals and Interval List Intersections are all variations of
 *   this same fold.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates, the merge pass is a single O(n) walk
 *   Space O(n)        output list (plus O(n) for the sort's own scratch space)
 *
 * INTERVIEW FOLLOW-UPS
 *   - The list is already sorted and you insert one new interval: LeetCode 57 (C01_InsertInterval).
 *   - Return the total covered length, or the gaps between the merged blocks.
 *   - Intervals arrive as a stream: keep a TreeMap keyed by start and merge the
 *     floor and ceiling neighbours on each insert.
 *   - Only merge intervals that overlap by at least k units.
 *
 * FIXES APPLIED
 *   Fixed: merge() appended the caller's Interval objects and then mutated their
 *          end field, silently corrupting the input list. It now appends copies.
 *   Fixed: the comparator used (i1.start - i2.start), which overflows for large
 *          or negative starts. Replaced with Integer.compare.
 *
 * RUN
 *   main() runs 4 cases (typical, touching, nested, empty), prints actual vs
 *   expected, and re-prints the input to show it was not mutated.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Interval {
    int start;
    int end;

    Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}

class MergeIntervals {

    public List<Interval> merge(List<Interval> intervals) {
        // Sort by start value. Integer.compare avoids the overflow that a plain
        // subtraction comparator hits when the starts are far apart.
        Collections.sort(intervals, (i1, i2) -> Integer.compare(i1.start, i2.start));

        List<Interval> merged = new ArrayList<>();
        for (Interval interval : intervals) {
            Interval last = merged.isEmpty() ? null : merged.get(merged.size() - 1);

            // No overlap with the interval being accumulated -> start a new one.
            // A copy is added so that stretching its end later cannot write back
            // into the caller's list.
            if (last == null || last.end < interval.start) {
                merged.add(new Interval(interval.start, interval.end));
            } else {
                // Overlap: only the end can grow. Sorting already fixed the start.
                last.end = Math.max(last.end, interval.end);
            }
        }
        return merged;
    }

    private static List<Interval> intervalsOf(int[][] pairs) {
        List<Interval> list = new ArrayList<>();
        for (int[] pair : pairs) {
            list.add(new Interval(pair[0], pair[1]));
        }
        return list;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MergeIntervals solver = new MergeIntervals();

        // typical: one overlapping pair, two isolated intervals
        List<Interval> input1 = intervalsOf(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}});
        print("case 1", solver.merge(input1), "[[1, 6], [8, 10], [15, 18]]");
        // merge() sorts the list in place, but it must not change any endpoint
        print("case 1 input after merge", input1, "[[1, 3], [2, 6], [8, 10], [15, 18]]");

        // tricky: intervals that only touch still merge under LeetCode 56 rules
        print("case 2", solver.merge(intervalsOf(new int[][]{{1, 4}, {4, 5}})), "[[1, 5]]");

        // tricky: unsorted input where one interval contains the others
        print("case 3",
                solver.merge(intervalsOf(new int[][]{{5, 10}, {1, 20}, {2, 3}})), "[[1, 20]]");

        // edge: empty input
        print("case 4", solver.merge(intervalsOf(new int[][]{})), "[]");
    }
}
