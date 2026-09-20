/*
 * =====================================================================
 *  Meeting Rooms                                       LeetCode 252 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given meeting time intervals [start, end), decide whether one person can
 *   attend all of them. The answer is true only when no two meetings overlap.
 *   The intervals are half-open, so a meeting ending at 10 does not clash with
 *   one starting at 10.
 *
 * EXAMPLE
 *   [[0,30],[5,10]]       ->  false   0-30 is still running when 5-10 starts
 *   [[7,10],[2,4]]        ->  true    sorted they are [2,4] then [7,10]
 *   [[1,2],[2,3],[3,4]]   ->  true    back-to-back meetings are allowed
 *   []                    ->  true    nothing to attend
 *
 * APPROACH  (sort by start time, then compare adjacent pairs)
 *   1. Sort the intervals by start time.
 *   2. Scan the sorted array once, comparing each interval with the previous one.
 *   3. If prev end > current start the two overlap, so return false immediately.
 *   4. If the scan finishes with no clash, return true.
 *
 * KEY INSIGHT
 *   Once the intervals are sorted by start, an overlap can only exist between
 *   NEIGHBOURS. If interval i does not clash with i-1, it cannot clash with
 *   anything earlier either, because every earlier start is <= start of i-1 and
 *   every earlier end is <= end of i-1 in the part already checked. That turns an
 *   O(n^2) pairwise check into one scan. The predicate prev.end > cur.start is
 *   the atom every other interval problem in this folder is built on; use > and
 *   not >= only because the intervals are half-open.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates, the scan after it is O(n)
 *   Space O(1) extra  the sort is in place (O(log n) recursion stack)
 *
 * INTERVIEW FOLLOW-UPS
 *   - How many rooms are needed instead of yes/no? (Meeting Rooms II, C03_MeetingRoomsII)
 *   - What if intervals were closed so [1,2] and [2,3] clash? (use >=)
 *   - Meetings arrive one at a time: keep a TreeMap and check floor/ceiling keys.
 *   - Return the first colliding pair instead of a boolean.
 *
 * RUN
 *   main() runs 5 cases (overlap, unsorted but clean, touching, empty, nested)
 *   and prints actual vs expected on each line.
 */

import java.util.Arrays;
import java.util.Comparator;

class MeetingRooms {

    public static boolean canAttendMeetings(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return true; // no meetings, so trivially attend them all
        }

        // Sorting by start is what makes the neighbour-only check valid.
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        for (int i = 1; i < intervals.length; i++) {
            // Previous meeting ends after this one starts -> they overlap.
            // Strict > keeps [1,2] and [2,3] legal (half-open intervals).
            if (intervals[i - 1][1] > intervals[i][0]) {
                return false;
            }
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: the two meetings collide
        print("case 1 [[0,30],[5,10]]", canAttendMeetings(new int[][]{{0, 30}, {5, 10}}), false);

        // typical: unsorted input, but no clash once sorted
        print("case 2 [[7,10],[2,4]]", canAttendMeetings(new int[][]{{7, 10}, {2, 4}}), true);

        // tricky: touching endpoints must NOT count as an overlap
        print("case 3 [[1,2],[2,3],[3,4]]",
                canAttendMeetings(new int[][]{{1, 2}, {2, 3}, {3, 4}}), true);

        // edge: empty schedule
        print("case 4 []", canAttendMeetings(new int[][]{}), true);

        // tricky: one long meeting swallows the later short ones
        print("case 5 [[0,5],[1,2],[3,4],[6,7]]",
                canAttendMeetings(new int[][]{{0, 5}, {1, 2}, {3, 4}, {6, 7}}), false);
    }
}
