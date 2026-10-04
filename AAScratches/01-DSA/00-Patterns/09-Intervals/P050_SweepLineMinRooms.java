/*
 * =====================================================================
 *  P050 Sweep Line: Maximum Overlap / Minimum Rooms   Canonical LC 253 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 253, Meeting Rooms II)
 *   Given meeting intervals [start, end), return the minimum number of rooms so every
 *   meeting has a room. A meeting ending at t frees its room for one starting at t.
 *
 * EXAMPLE
 *   [[0,30],[5,10],[15,20]]  ->  2
 *   [[7,10],[2,4]]           ->  1
 *   [[1,5],[5,10]]           ->  1      end and start at the same time share a room
 *
 * RECOGNIZE WHEN
 *   - "minimum rooms / platforms / servers / groups", "maximum number of overlapping
 *     intervals", "peak load at any moment".
 *   - The answer is the maximum, over time, of how many intervals are active.
 *   Not this if: you merge the intervals -> P048_MergeIntervals; you keep the most
 *   non-overlapping ones -> P049_SortByEndGreedy; coordinates are small integers and many
 *   ranges are added -> a difference array (P003_DifferenceArray) is simpler.
 *
 * TEMPLATE
 *   events: (start, +1) and (end, -1); sort by time, and at equal times put -1 first
 *           when an end frees the slot for a start at the same time (half-open)
 *   active = 0, best = 0
 *   for (time, delta) in events: active += delta; best = max(best, active)
 *   heap version: sort by start; min-heap of end times; pop if heap.min <= start; push end;
 *                 rooms = max heap size (or the final size)
 *
 * APPROACH
 *   1. Sort starts and ends separately.
 *   2. Walk the starts; before counting a new meeting, release every meeting that ended
 *      at or before this start.
 *   3. The most meetings active at once is the number of rooms.
 *
 * KEY INSIGHT
 *   Which meeting uses which room does not matter, only how many are running at each
 *   moment. Turning intervals into +1 / -1 events and sweeping time in order counts that
 *   directly. The tie rule at equal times encodes whether touching intervals conflict.
 *
 * COMPLEXITY
 *   Time O(n log n), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 253 with a heap               min-heap of end times = rooms in use
 *   [coded] LC 2406 Min Groups               intervals are INCLUSIVE: [1,5] and [5,10]
 *                                            conflict, so release only if end < start
 *   [coded] Minimum Platforms (GfG)          the same count with inclusive times
 *           LC 2402 Meeting Rooms III        heap of free room ids + heap of (end, room)
 *           LC 732  My Calendar III          TreeMap of +1/-1 deltas, max running sum
 *           LC 218  The Skyline Problem      events + max-heap / TreeMap of live heights
 *           LC 1094 Car Pooling              -> P003_DifferenceArray
 *           LC 1851 Min Interval per Query   sort queries; heap of intervals by length
 *
 * PITFALLS
 *   - The tie rule decides correctness: [1,5] and [5,10] need 1 room in LC 253 (half-open)
 *     but 2 groups in LC 2406 (closed).
 *   - With a heap, pop while heap.min <= start (half-open) before pushing the new end.
 *   - Sorting events by time alone is not enough; add the tie-breaker.
 *
 * DEEP DIVE
 *   C03_MeetingRoomsII, C04_DivideIntervalsIntoMinGroups, A03_MinimumPlatforms,
 *   D01_MeetingRoomsIII (15-Intervals)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;
import java.util.PriorityQueue;

class SweepLineMinRooms {

    // Canonical LC 253 with sorted starts and ends (two pointers over the two event lists).
    static int minMeetingRooms(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        int active = 0;
        int best = 0;
        int e = 0;
        for (int s = 0; s < n; s++) {
            while (e < n && ends[e] <= starts[s]) {
                active--;                          // released before this start
                e++;
            }
            active++;
            best = Math.max(best, active);
        }
        return best;
    }

    // LC 253 with a min-heap of end times.
    static int minMeetingRoomsHeap(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        PriorityQueue<Integer> ends = new PriorityQueue<>();
        for (int[] m : a) {
            if (!ends.isEmpty() && ends.peek() <= m[0]) {
                ends.poll();                       // reuse the room that frees first
            }
            ends.offer(m[1]);
        }
        return ends.size();
    }

    // LC 2406: inclusive intervals, so a group frees only if its end < the new start.
    static int minGroups(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        PriorityQueue<Integer> ends = new PriorityQueue<>();
        for (int[] iv : a) {
            if (!ends.isEmpty() && ends.peek() < iv[0]) {
                ends.poll();
            }
            ends.offer(iv[1]);
        }
        return ends.size();
    }

    // Minimum platforms: a train departing at t and one arriving at t need two platforms.
    static int minPlatforms(int[] arrive, int[] depart) {
        int[] a = arrive.clone();
        int[] d = depart.clone();
        Arrays.sort(a);
        Arrays.sort(d);
        int active = 0;
        int best = 0;
        int j = 0;
        for (int i = 0; i < a.length; i++) {
            while (j < d.length && d[j] < a[i]) {
                active--;
                j++;
            }
            active++;
            best = Math.max(best, active);
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] three = {{0, 30}, {5, 10}, {15, 20}};
        check("LC 253 events", minMeetingRooms(three), 2);
        check("LC 253 heap", minMeetingRoomsHeap(three), 2);
        check("LC 253 disjoint", minMeetingRooms(new int[][]{{7, 10}, {2, 4}}), 1);
        check("LC 253 back to back events", minMeetingRooms(new int[][]{{1, 5}, {5, 10}}), 1);
        check("LC 253 back to back heap", minMeetingRoomsHeap(new int[][]{{1, 5}, {5, 10}}), 1);
        check("LC 253 all at once", minMeetingRoomsHeap(new int[][]{{1, 4}, {2, 5}, {3, 6}}), 3);

        check("LC 2406 five intervals",
                minGroups(new int[][]{{5, 10}, {6, 8}, {1, 5}, {2, 3}, {1, 10}}), 3);
        check("LC 2406 disjoint", minGroups(new int[][]{{1, 3}, {5, 6}, {8, 10}, {11, 13}}), 1);
        check("LC 2406 touching conflicts", minGroups(new int[][]{{1, 5}, {5, 10}}), 2);

        check("platforms six trains", minPlatforms(new int[]{900, 940, 950, 1100, 1500, 1800},
                new int[]{910, 1200, 1120, 1130, 1900, 2000}), 3);
        check("platforms same minute", minPlatforms(new int[]{100, 200}, new int[]{200, 300}), 2);
    }
}
