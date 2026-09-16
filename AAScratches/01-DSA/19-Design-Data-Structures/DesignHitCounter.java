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
