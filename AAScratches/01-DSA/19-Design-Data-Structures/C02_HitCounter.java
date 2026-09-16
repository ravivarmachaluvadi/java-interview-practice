/**
 * Problem: Count the number of hits received in the last five minutes (300 seconds)
 * for a web server, given timestamps in increasing order.
 *
 * Approach: Store each hit timestamp in a FIFO queue. When querying getHits,
 * remove all timestamps older than 5 minutes from the current time and return
 * the remaining queue size. This keeps only relevant hits in memory.
 *
 * Time Complexity:
 *   - hit(): O(1) amortized
 *   - getHits(): O(k), where k is the number of stale entries removed (≤ total hits)
 *
 * Space Complexity: O(n), where n is the maximum number of hits within any 5‑minute window. */
import java.util.Deque;
import java.util.LinkedList;

class HitCounter {
    private final Deque<Integer> queue;
    private static final int WINDOW = 300; // 300 seconds = 5 minutes

    /**
     * Initialize your data structure here.
     */
    public HitCounter() {
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
        // Remove stale hits (older than 300 seconds before timestamp)
        while (!queue.isEmpty() && timestamp - queue.peekFirst() >= WINDOW) {
            queue.pollFirst();
        }
        return queue.size();
    }

    // Example main to demonstrate usage
    public static void main(String[] args) {
        HitCounter counter = new HitCounter();

        counter.hit(1);
        counter.hit(2);
        counter.hit(3);
        System.out.println("getHits(4) → " + counter.getHits(4)); // expected 3

        counter.hit(300);
        System.out.println("getHits(300) → " + counter.getHits(300)); // expected 4
        System.out.println("getHits(301) → " + counter.getHits(301)); // expected 3
    }
}
