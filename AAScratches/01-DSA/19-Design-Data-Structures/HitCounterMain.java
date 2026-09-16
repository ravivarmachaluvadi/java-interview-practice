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
