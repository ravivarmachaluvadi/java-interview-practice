import java.util.*;

// https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended/description/
// 1353. Maximum Number of Events That Can Be Attended
class MaximumNumberofEventsThatCanBeAttended {

    public static int maxEvents(int[][] events) {
        // Sort by start day ascending
        Arrays.sort(events, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        int n = events.length;
        int i = 0;
        int day = 0;
        int attended = 0;

        while (i < n || !minHeap.isEmpty()) {
            if (minHeap.isEmpty()) {
                // jump day forward to next event's start if no event to attend now
                day = Math.max(day, events[i][0]);
            }
            // Add all events starting today
            while (i < n && events[i][0] == day) {
                minHeap.offer(events[i][1]);
                i++;
            }
            // Remove expired events (end day < current day)
            while (!minHeap.isEmpty() && minHeap.peek() < day) {
                minHeap.poll();
            }
            // Attend one event (the one ending earliest)
            if (!minHeap.isEmpty()) {
                minHeap.poll();
                attended++;
                day++;
            }
        }
        return attended;
    }

    public static void main(String[] args) {
        int[][] events = {
                {1, 4},
                {4, 4},
                {2, 2},
                {3, 4},
                {1, 1}
        };
        int result = maxEvents(events);
        System.out.println("Maximum number of events that can be attended = " + result);
    }
}
