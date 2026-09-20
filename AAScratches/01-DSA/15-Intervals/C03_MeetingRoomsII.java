/*
 * =====================================================================
 *  Meeting Rooms II                                   LeetCode 253 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given meeting intervals [start, end), return the minimum number of rooms
 *   needed so that no two meetings share a room at the same instant.
 *   Intervals are half-open: a meeting ending at 10 frees the room for one
 *   starting at 10. The answer is simply the peak number of meetings running
 *   at the same moment.
 *
 * EXAMPLE
 *   [[0,30],[5,10],[15,20]]  ->  2   ([5,10] and [15,20] reuse one room)
 *   [[7,10],[2,4]]           ->  1   (they never overlap)
 *   [[1,5],[2,6],[4,7],[8,9]]->  3   (at time 4 all three are running)
 *   []                       ->  0
 *
 * APPROACH  (chronological sweep over two independently sorted arrays)
 *   1. Split the intervals into a starts[] array and an ends[] array.
 *   2. Sort each array on its own. Pairing is deliberately thrown away - we only
 *      care WHEN something begins or finishes, not which meeting it was.
 *   3. Walk starts[] with i and ends[] with j, both from 0.
 *   4. If starts[i] < ends[j] a meeting begins while the earliest-finishing one
 *      is still running, so open a new room: rooms++.
 *   5. Otherwise that earliest meeting has already finished, so its room is
 *      recycled by this start: advance j and do NOT count a room.
 *   6. After all n starts are consumed, rooms is the peak concurrency.
 *
 * KEY INSIGHT
 *   This is Minimum Platforms (A03) wearing a different name - rooms, platforms,
 *   groups and "max overlap depth" are all the same count. Decoupling starts from
 *   ends is the trick: once sorted separately, time can be walked forward and the
 *   running count of open intervals IS the answer. Because rooms only ever goes
 *   up here, the final value is already the maximum - no separate max tracking.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the two sorts dominate; the sweep itself is O(n).
 *   Space O(n)        the starts[] and ends[] copies.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Meeting Rooms I (LC 252): can ONE room serve everything? Same sweep, ask
 *     whether the peak ever reaches 2.
 *   - Heap variant: sort by start, keep a min-heap of end times, pop every end
 *     <= the current start; heap size is the answer. Needed for Meeting Rooms III
 *     because that one must know WHICH room, not just how many.
 *   - If intervals were closed [start, end], a meeting ending at 10 would clash
 *     with one starting at 10: change the test to starts[i] <= ends[j].
 *   - Which meetings occupy the peak? Track the sweep position where rooms grew.
 *
 * RUN
 *   main() runs 4 cases (typical, no overlap, triple overlap, empty) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class MeetingRoomsII {

    public static int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        // Sorted independently: we care about the timeline, not about pairing.
        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int j = 0; // pointer into ends[]: the earliest meeting still running

        for (int i = 0; i < n; i++) {
            if (starts[i] < ends[j]) {
                // Nothing has finished yet, so this start needs a fresh room.
                rooms++;
            } else {
                // The earliest meeting already ended: reuse its room, free of charge.
                j++;
            }
        }

        return rooms;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1: typical - one long meeting plus two that share a room.
        print("case 1 (typical)", minMeetingRooms(new int[][]{{0, 30}, {5, 10}, {15, 20}}), 2);

        // Case 2: edge - disjoint meetings, out of order, one room suffices.
        print("case 2 (no overlap)", minMeetingRooms(new int[][]{{7, 10}, {2, 4}}), 1);

        // Case 3: tricky - three meetings all alive at time 4.
        print("case 3 (triple overlap)", minMeetingRooms(new int[][]{{1, 5}, {2, 6}, {4, 7}, {8, 9}}), 3);

        // Case 4: edge - no meetings at all.
        print("case 4 (empty)", minMeetingRooms(new int[][]{}), 0);
    }
}
