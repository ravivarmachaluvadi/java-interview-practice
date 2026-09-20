/*
 * =====================================================================
 *  Meeting Rooms III                                   LeetCode 2402 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   There are n rooms numbered 0..n-1 and a list of meetings [start, end) with all starts
 *   distinct. Meetings are processed in increasing order of start time. A meeting takes the
 *   lowest-numbered free room; if every room is busy it WAITS, keeps its original duration, and
 *   starts the moment the earliest-finishing room frees up (ties go to the lower room number).
 *   Return the room that held the most meetings, smallest index on a tie.
 *   Delays stack, so end times can exceed int range: do the arithmetic in long.
 *
 * EXAMPLE
 *   n=2, [[0,10],[1,5],[2,7],[3,4]]              ->  0
 *        room0 takes [0,10); room1 takes [1,5); [2,7) waits for room1 at t=5 -> [5,10);
 *        [3,4) then waits for the earliest free room, room0 at t=10 -> [10,11).
 *        counts = [2, 2] -> tie -> answer 0
 *   n=3, [[1,20],[2,10],[3,5],[4,9],[6,8]]       ->  1
 *   n=1, [[0,10],[1,5],[2,7]]                    ->  0   everything queues behind room 0
 *
 * DESIGN (classes and why)
 *   RoomEnd        one busy room as (endTime as long, roomIndex), Comparable so a
 *                  PriorityQueue orders by endTime then by roomIndex - exactly the
 *                  tie-break the problem asks for.
 *   idleRooms      PriorityQueue<Integer>, a min-heap of free room NUMBERS, so "lowest
 *                  numbered free room" is one poll().
 *   busyRooms      PriorityQueue<RoomEnd>, a min-heap of occupied rooms keyed by when they
 *                  free up, so "earliest room to free" is one poll().
 *   count[]        meetings held per room; scanned once at the end.
 *
 * KEY DECISIONS
 *   1. Sort meetings by start time so they can be handed out in the order the problem defines.
 *   2. Before placing each meeting, drain busyRooms of every room whose endTime <= start back
 *      into idleRooms. A room that ends exactly at `start` is free at `start` (half-open ends).
 *   3. Free room available -> take the smallest index, push (end, room) onto busyRooms.
 *   4. No free room -> poll the earliest-ending busy room, keep the duration (end - start) and
 *      restart it at that room's free time: newEnd = freeTime + duration. The room stays busy,
 *      so it goes straight back into busyRooms and never visits idleRooms.
 *   5. endTime is a long. With n=1 and 100000 chained meetings the delays accumulate past
 *      Integer.MAX_VALUE; an int here silently overflows and wrecks the heap order.
 *   Note: mostBooked() sorts the caller's array in place, which is fine for a scratch file.
 *
 * COMPLEXITY
 *   Time  O(m log m + m log n)  sort of m meetings, then O(log n) heap work per meeting
 *   Space O(n)                  the two heaps together hold exactly n rooms, plus count[]
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does the delayed meeting keep its ORIGINAL duration rather than its original end?
 *   - Return the busiest room's meeting count, or the total time each room was occupied.
 *   - Why not one heap? (A free room has no end time to sort by, and index order differs.)
 *   - n is small, say n <= 100: can you drop idleRooms and linearly scan the rooms instead?
 *
 * RUN
 *   main() runs 4 cases (two LeetCode examples, the single-room edge case, and a tie-break
 *   case) and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.PriorityQueue;

class MeetingRoomsIII {

    public static int mostBooked(int n, int[][] meetings) {
        // Meetings must be handed out in start order; starts are distinct so no tie-break needed.
        Arrays.sort(meetings, (a, b) -> Integer.compare(a[0], b[0]));

        // Free rooms, smallest index first.
        PriorityQueue<Integer> idleRooms = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            idleRooms.offer(i);
        }
        // Occupied rooms as (endTime, roomIndex), earliest to free first.
        PriorityQueue<RoomEnd> busyRooms = new PriorityQueue<>();

        int[] count = new int[n];   // meetings held per room

        for (int[] meeting : meetings) {
            int start = meeting[0];
            int end = meeting[1];

            // Anything finishing at or before `start` is free again: ends are exclusive.
            while (!busyRooms.isEmpty() && busyRooms.peek().endTime <= start) {
                idleRooms.offer(busyRooms.poll().roomIndex);
            }

            if (!idleRooms.isEmpty()) {
                int room = idleRooms.poll();            // lowest-numbered free room
                count[room]++;
                busyRooms.offer(new RoomEnd(end, room));
            } else {
                // Everything is busy: wait for the earliest-freeing room (lowest index on a tie),
                // then run for the meeting's original duration starting at that moment.
                RoomEnd earliest = busyRooms.poll();
                long duration = (long) end - start;
                long delayedEnd = earliest.endTime + duration;
                count[earliest.roomIndex]++;
                busyRooms.offer(new RoomEnd(delayedEnd, earliest.roomIndex));
            }
        }

        int best = 0;   // strict > keeps the smallest index on a tie
        for (int i = 1; i < n; i++) {
            if (count[i] > count[best]) {
                best = i;
            }
        }
        return best;
    }

    /** One occupied room: when it frees up, and which room it is. */
    private static class RoomEnd implements Comparable<RoomEnd> {
        final long endTime;     // long: stacked delays can exceed Integer.MAX_VALUE
        final int roomIndex;

        RoomEnd(long endTime, int roomIndex) {
            this.endTime = endTime;
            this.roomIndex = roomIndex;
        }

        @Override
        public int compareTo(RoomEnd other) {
            if (this.endTime != other.endTime) {
                return Long.compare(this.endTime, other.endTime);
            }
            return Integer.compare(this.roomIndex, other.roomIndex);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 tie on 2 rooms ",
                mostBooked(2, new int[][]{{0, 10}, {1, 5}, {2, 7}, {3, 4}}), 0);
        print("case 2 three rooms    ",
                mostBooked(3, new int[][]{{1, 20}, {2, 10}, {3, 5}, {4, 9}, {6, 8}}), 1);
        print("case 3 single room    ",
                mostBooked(1, new int[][]{{0, 10}, {1, 5}, {2, 7}}), 0);
        print("case 4 idle then reuse",
                mostBooked(4, new int[][]{{0, 10}, {1, 2}, {12, 14}, {13, 15}}), 0);
    }
}
