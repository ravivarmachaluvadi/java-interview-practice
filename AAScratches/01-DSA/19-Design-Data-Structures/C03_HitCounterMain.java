/**
 * Problem: Implement a simple hit counter that records hits with timestamps and can return the number of hits in the past five minutes (300 seconds).
 *
 * Approach: Use a FIFO queue to store timestamps of each hit. On recording a hit, enqueue the timestamp.
 * When querying getHits(timestamp), dequeue all timestamps older than timestamp - 300, then return the queue size.
 *
 * Time Complexity:
 *   - hit(): O(1) amortized
 *   - getHits(): O(k) where k is the number of outdated hits removed (worst-case O(n))
 *
 * Space Complexity: O(n), where n is the total number of hits stored within the last five minutes.
 */
import java.util.LinkedList;
import java.util.Queue;

class HitCounter {
    private Queue<Integer> hits;

    public HitCounter() {
        hits = new LinkedList<>();
    }

    // Record a hit at the given timestamp.
    public void hit(int timestamp) {
        hits.offer(timestamp);
    }

    // Return the number of hits in the past 5 minutes.
    public int getHits(int timestamp) {
        while (!hits.isEmpty() && hits.peek() <= timestamp - 300) {
            hits.poll(); // Remove outdated hits.
        }
        return hits.size();
    }
}

class HitCounterMain {
    public static void main(String[] args) {
        HitCounter hitCounter = new HitCounter();

        // Example usage
        hitCounter.hit(1);
        hitCounter.hit(2);
        hitCounter.hit(3);
        System.out.println("Hits at time 4: " + hitCounter.getHits(4)); // Output: 3
        hitCounter.hit(300);
        System.out.println("Hits at time 300: " + hitCounter.getHits(300)); // Output: 4
        System.out.println("Hits at time 301: " + hitCounter.getHits(301)); // Output: 3
    }
}
