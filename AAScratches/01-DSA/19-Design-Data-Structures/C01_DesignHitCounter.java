/**
 * Problem: Implement a hit counter that records the number of hits received in the past five minutes (300 seconds).
 *
 * Approach: Store each hit timestamp in a FIFO queue. When querying getHits, remove timestamps older than
 *          timestamp - 300 from the front of the queue and return the remaining size.
 *
 * Time Complexity:
 *   hit()      : O(1)
 *   getHits()  : O(k) where k is the number of expired hits (amortized O(1) per hit over time).
 *
 * Space Complexity: O(n), where n is the maximum number of hits stored within any 5‑minute window.
 */
import java.util.Deque;
import java.util.LinkedList;

class DesignHitCounter {
    private final Deque<Integer> queue;
    private static final int WINDOW = 300; // 300 seconds = 5 minutes

    /**
     * Initialize your data structure here.
     */
    public DesignHitCounter() {
        queue = new LinkedList<>();
    }

    /**
     * Record a hit at given timestamp (in seconds).
     */
    public void hit(int timestamp) {
        queue.offerLast(timestamp);
    }

    /**
     * Return the number of hits in the past 5 minutes from the given timestamp.
     */
    public int getHits(int timestamp) {
        while (!queue.isEmpty() && queue.peekFirst() <= timestamp - WINDOW) {
            queue.pollFirst();
        }
        return queue.size();
    }

    // Example main to demonstrate usage
    public static void main(String[] args) {
        DesignHitCounter counter = new DesignHitCounter();

        counter.hit(1);
        counter.hit(2);
        counter.hit(3);
        System.out.println("getHits(4) → " + counter.getHits(4)); // expected 3

        counter.hit(300);
        System.out.println("getHits(300) → " + counter.getHits(300)); // expected 4
        System.out.println("getHits(301) → " + counter.getHits(301)); // expected 3
    }
}
