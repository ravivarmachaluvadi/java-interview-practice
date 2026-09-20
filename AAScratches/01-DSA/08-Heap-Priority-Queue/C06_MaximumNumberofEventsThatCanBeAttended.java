/*
 * =====================================================================
 *  Maximum Number of Events That Can Be Attended     LeetCode 1353 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   events[i] = [startDay, endDay], both inclusive. You can attend at most one
 *   event per day, on any day d with start <= d <= end. Return the maximum number
 *   of events you can attend. Up to 1e5 events, day values up to 1e5.
 *
 * EXAMPLE
 *   [[1,2],[2,3],[3,4]]              ->  3   day 1, day 2, day 3
 *   [[1,4],[4,4],[2,2],[3,4],[1,1]]  ->  4   d1 [1,1], d2 [2,2], d3 [1,4], d4 [4,4]
 *   [[1,1],[1,1],[1,1]]              ->  1   all three collide on the only day
 *   [[7,7]]                          ->  1   single event
 *
 * APPROACH  (sort by start, min-heap on end day)
 *   1. Sort events by start day.
 *   2. Walk a day counter. If no event is currently open, jump the counter
 *      forward to the next event's start day (never backwards).
 *   3. Push the end day of every event that has started by the current day.
 *   4. Pop end days that are already in the past (end < day): those expired.
 *   5. Attend the open event that ends soonest (heap top), count it, next day.
 *   6. Stop when every event has been pushed and the heap is empty.
 *
 * KEY INSIGHT
 *   On any given day, among the events you could still attend, the one that
 *   expires first is the one you lose if you do not take it now; every other
 *   choice can wait. This is "earliest deadline first", and a min-heap on end
 *   day answers "which expires first" in O(log n). Sort by start so that events
 *   become available in order; heap by end so you pick the most urgent one.
 *
 * COMPLEXITY
 *   Time  O(n log n)  sort, plus each event enters and leaves the heap once
 *   Space O(n)        the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Events carry a value and you may attend k (LC 1751): greedy breaks, use DP + binary search.
 *   - Day values up to 1e9: still fine, the counter jumps over empty gaps instead of iterating.
 *   - Why not iterate days 1..maxDay? Works for 1e5 but wastes time on gaps; the jump fixes it.
 *
 * RUN
 *   main() runs 4 cases and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.PriorityQueue;

class MaximumNumberofEventsThatCanBeAttended {

    public static int maxEvents(int[][] events) {
        Arrays.sort(events, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> openEndDays = new PriorityQueue<>(); // min-heap on end day
        int n = events.length;
        int next = 0;      // index of the next event not yet pushed
        int day = 0;
        int attended = 0;

        while (next < n || !openEndDays.isEmpty()) {
            if (openEndDays.isEmpty()) {
                // nothing open today: skip straight to the next event's start day
                day = Math.max(day, events[next][0]);
            }
            // open every event that has started by today
            while (next < n && events[next][0] <= day) {
                openEndDays.offer(events[next][1]);
                next++;
            }
            // drop events whose last day has already passed
            while (!openEndDays.isEmpty() && openEndDays.peek() < day) {
                openEndDays.poll();
            }
            // attend the open event that ends soonest, then move to the next day
            if (!openEndDays.isEmpty()) {
                openEndDays.poll();
                attended++;
                day++;
            }
        }
        return attended;
    }

    private static void print(String label, int[][] events, int expected) {
        System.out.println(label + ": " + maxEvents(events) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", new int[][]{{1, 2}, {2, 3}, {3, 4}}, 3);
        print("case 2 tricky", new int[][]{{1, 4}, {4, 4}, {2, 2}, {3, 4}, {1, 1}}, 4);
        print("case 3 all collide", new int[][]{{1, 1}, {1, 1}, {1, 1}}, 1);
        print("case 4 single event", new int[][]{{7, 7}}, 1);
    }
}
