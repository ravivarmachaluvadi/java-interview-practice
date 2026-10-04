/*
 * =====================================================================
 *  P048 Intervals: Sort by Start and Merge   Canonical LC 56 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 56, Merge Intervals)
 *   Merge all overlapping intervals and return the non-overlapping intervals that cover
 *   exactly the same points.
 *
 * EXAMPLE
 *   [[1,3],[2,6],[8,10],[15,18]]  ->  [[1,6],[8,10],[15,18]]
 *   [[1,4],[4,5]]                 ->  [[1,5]]        touching counts as overlapping
 *   [[1,4],[2,3]]                 ->  [[1,4]]        the trap: one inside the other
 *
 * RECOGNIZE WHEN
 *   - Ranges / meetings / bookings and the question is about their UNION: merge, insert,
 *     "do any overlap", "total covered length", "free time".
 *   - Two sorted lists of ranges to intersect.
 *   Not this if: you must keep as many as possible or remove the fewest -> sort by END
 *   (P049_SortByEndGreedy); you need how many overlap at once -> P050_SweepLineMinRooms.
 *
 * TEMPLATE
 *   sort by start
 *   merged = [first]
 *   for [s, e] in the rest:
 *       last = merged.back
 *       if s <= last.end: last.end = max(last.end, e)     // overlap: extend
 *       else:             merged.append([s, e])           // gap: new block
 *
 * APPROACH
 *   1. Sort by start, so any interval that overlaps the current block comes right after it.
 *   2. Extend the block's end with max(); otherwise start a new block.
 *
 * KEY INSIGHT
 *   After sorting by start, an interval can only overlap the LAST merged block, never an
 *   earlier one, so one linear pass finishes the job. Use max() for the end: a short
 *   interval inside a long one must not shrink it.
 *
 * COMPLEXITY
 *   Time O(n log n) for the sort, then O(n). Space O(n) for the output.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 57   Insert Interval          input already sorted: copy those ending before,
 *                                            merge the overlapping run, copy the rest; O(n)
 *   [coded] LC 252  Meeting Rooms            sort by start; any start < previous end fails
 *   [coded] LC 986  Interval Intersections   two pointers: overlap = [max starts, min ends];
 *                                            advance whichever interval ends first
 *           LC 759  Employee Free Time       merge everyone's intervals, report the gaps
 *           LC 228  Summary Ranges           merge runs of consecutive integers
 *           LC 1288 Remove Covered Intervals sort start asc, end DESC; count new max ends
 *           Total covered length             merge, then sum (end - start)
 *
 * PITFALLS
 *   - Touching intervals: does [1,4] + [4,5] merge? Here yes (s <= end); check the
 *     problem's definition of overlap.
 *   - Comparator a[0] - b[0] overflows for extreme values; use Integer.compare.
 *   - LC 986: after taking an overlap, advance the one with the smaller END.
 *
 * DEEP DIVE
 *   A02_MergeIntervals, C01_InsertInterval, A01_MeetingRooms,
 *   C02_IntervalListIntersections (15-Intervals)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MergeIntervals {

    // Canonical LC 56.
    static int[][] merge(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] cur : a) {
            if (!merged.isEmpty() && cur[0] <= merged.get(merged.size() - 1)[1]) {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.max(last[1], cur[1]);
            } else {
                merged.add(new int[]{cur[0], cur[1]});
            }
        }
        return merged.toArray(new int[0][]);
    }

    // LC 57: intervals are sorted and non-overlapping.
    static int[][] insert(int[][] intervals, int[] add) {
        List<int[]> out = new ArrayList<>();
        int i = 0;
        int n = intervals.length;
        while (i < n && intervals[i][1] < add[0]) {
            out.add(intervals[i++]);                 // entirely before
        }
        int start = add[0];
        int end = add[1];
        while (i < n && intervals[i][0] <= end) {
            start = Math.min(start, intervals[i][0]);   // overlapping run
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        out.add(new int[]{start, end});
        while (i < n) {
            out.add(intervals[i++]);                 // entirely after
        }
        return out.toArray(new int[0][]);
    }

    // LC 252: can one person attend every meeting?
    static boolean canAttendMeetings(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        for (int i = 1; i < a.length; i++) {
            if (a[i][0] < a[i - 1][1]) {
                return false;
            }
        }
        return true;
    }

    // LC 986.
    static int[][] intervalIntersection(int[][] first, int[][] second) {
        List<int[]> out = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < first.length && j < second.length) {
            int lo = Math.max(first[i][0], second[j][0]);
            int hi = Math.min(first[i][1], second[j][1]);
            if (lo <= hi) {
                out.add(new int[]{lo, hi});
            }
            if (first[i][1] < second[j][1]) {
                i++;
            } else {
                j++;
            }
        }
        return out.toArray(new int[0][]);
    }

    static String show(int[][] a) {
        return Arrays.deepToString(a).replace(" ", "");
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 56 four intervals", show(merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}})),
                "[[1,6],[8,10],[15,18]]");
        check("LC 56 touching", show(merge(new int[][]{{1, 4}, {4, 5}})), "[[1,5]]");
        check("LC 56 contained", show(merge(new int[][]{{1, 4}, {2, 3}})), "[[1,4]]");
        check("LC 56 unsorted input",
                show(merge(new int[][]{{8, 10}, {1, 3}, {2, 6}})), "[[1,6],[8,10]]");

        check("LC 57 [2,5]",
                show(insert(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5})), "[[1,5],[6,9]]");
        int[][] five = {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}};
        check("LC 57 [4,8] spans three",
                show(insert(five, new int[]{4, 8})), "[[1,2],[3,10],[12,16]]");
        check("LC 57 into empty", show(insert(new int[][]{}, new int[]{5, 7})), "[[5,7]]");

        check("LC 252 overlapping",
                canAttendMeetings(new int[][]{{0, 30}, {5, 10}, {15, 20}}), false);
        check("LC 252 disjoint", canAttendMeetings(new int[][]{{7, 10}, {2, 4}}), true);
        check("LC 252 back to back", canAttendMeetings(new int[][]{{1, 5}, {5, 8}}), true);

        int[][] a = {{0, 2}, {5, 10}, {13, 23}, {24, 25}};
        int[][] b = {{1, 5}, {8, 12}, {15, 24}, {25, 26}};
        check("LC 986 two lists", show(intervalIntersection(a, b)),
                "[[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]");
        check("LC 986 one empty",
                show(intervalIntersection(new int[][]{{1, 3}, {5, 9}}, new int[][]{})), "[]");
    }
}
