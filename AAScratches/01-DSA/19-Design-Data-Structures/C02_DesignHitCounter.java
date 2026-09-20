/*
 * =====================================================================
 *  Design Hit Counter                    LeetCode 362 | Medium MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Count the hits received in the past 5 minutes (300 seconds). hit(timestamp)
 *   records one hit; getHits(timestamp) returns how many hits landed in the window
 *   (timestamp - 300, timestamp]. Calls arrive with non-decreasing timestamps, and
 *   several hits may share the same second.
 *
 * EXAMPLE
 *   hit(1), hit(2), hit(3)  ->  getHits(4)   = 3    nothing has aged out yet
 *   hit(300)                ->  getHits(300) = 4    the hit at second 1 is still in range
 *                               getHits(301) = 3    second 1 is now exactly 300 old: dropped
 *
 * APPROACH  (sliding window over a FIFO queue of timestamps)
 *   1. hit: append the timestamp to the back of a deque. O(1), no cleanup.
 *   2. getHits: first evict from the FRONT while head <= timestamp - 300, because
 *      the oldest hits are always the first ones stored.
 *   3. The remaining size is the answer.
 *   4. Eviction is lazy - work is done on read, and each timestamp is pushed once
 *      and popped at most once, so the cost is amortised O(1) per hit.
 *
 * KEY INSIGHT
 *   A time window over non-decreasing timestamps is a queue: arrivals go in at one
 *   end, expiries leave from the other, and you never have to search the middle.
 *   Contrast with the logger rate limiter, which only needs the next deadline - here
 *   you must keep the events themselves because the question is "how many".
 *
 * COMPLEXITY
 *   Time  hit O(1);  getHits O(k) for the k expired entries, amortised O(1) per hit
 *   Space O(n)  n = hits inside one 300-second window
 *
 * INTERVIEW FOLLOW-UPS
 *   - Huge hit rate? Use 300 fixed buckets (BucketHitCounter below): O(1) hit,
 *     O(300) getHits, and memory bounded no matter the traffic.
 *   - Out-of-order or concurrent timestamps -> synchronise, or per-second atomic counters.
 *   - Window granularity in milliseconds or a 24-hour window -> buckets, not a queue.
 *   - Distributed counting -> per-node buckets summed by a coordinator, or HyperLogLog
 *     when distinct users, not raw hits, are what matters.
 *
 * RUN
 *   main() runs 3 cases against BOTH implementations: the LeetCode sequence, the
 *   exact 300-second boundary, and an empty counter.
 */

import java.util.ArrayDeque;
import java.util.Deque;

class DesignHitCounter {

    private static final int WINDOW = 300; // 300 seconds = 5 minutes

    /** Hit timestamps, oldest at the front - so expiry is always a pollFirst. */
    private final Deque<Integer> queue;

    public DesignHitCounter() {
        queue = new ArrayDeque<>();
    }

    /** Record a hit at the given timestamp (seconds). */
    public void hit(int timestamp) {
        queue.offerLast(timestamp);
    }

    /** Hits within the last 300 seconds, i.e. in (timestamp - 300, timestamp]. */
    public int getHits(int timestamp) {
        // Drop everything that is 300 seconds old or older; the front is always the oldest.
        while (!queue.isEmpty() && queue.peekFirst() <= timestamp - WINDOW) {
            queue.pollFirst();
        }
        return queue.size();
    }

    /**
     * Follow-up variant: fixed memory no matter how many hits arrive per second.
     * Slot i holds the timestamp that last wrote to it plus that second's count;
     * a slot whose timestamp is older than the window is simply ignored on read.
     */
    static class BucketHitCounter {
        private final int[] slotTime = new int[WINDOW];
        private final int[] slotHits = new int[WINDOW];

        public void hit(int timestamp) {
            int idx = timestamp % WINDOW;
            if (slotTime[idx] != timestamp) {
                slotTime[idx] = timestamp; // the slot belonged to an older second: reset it
                slotHits[idx] = 1;
            } else {
                slotHits[idx]++;
            }
        }

        public int getHits(int timestamp) {
            int total = 0;
            for (int i = 0; i < WINDOW; i++) {
                if (timestamp - slotTime[i] < WINDOW) {
                    total += slotHits[i];
                }
            }
            return total;
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical) and Case 2 (boundary): same sequence through both designs.
        DesignHitCounter queueCounter = new DesignHitCounter();
        BucketHitCounter bucketCounter = new BucketHitCounter();
        for (int t : new int[]{1, 2, 3}) {
            queueCounter.hit(t);
            bucketCounter.hit(t);
        }
        print("case 1a: queue  getHits(4)", queueCounter.getHits(4), 3);
        print("case 1b: bucket getHits(4)", bucketCounter.getHits(4), 3);

        queueCounter.hit(300);
        bucketCounter.hit(300);
        print("case 2a: queue  getHits(300)", queueCounter.getHits(300), 4);
        print("case 2b: bucket getHits(300)", bucketCounter.getHits(300), 4);
        print("case 2c: queue  getHits(301) drops second 1", queueCounter.getHits(301), 3);
        print("case 2d: bucket getHits(301) drops second 1", bucketCounter.getHits(301), 3);

        // Case 3 (edge): never hit, plus many hits in one second, plus a window that
        // has fully drained.
        DesignHitCounter empty = new DesignHitCounter();
        BucketHitCounter emptyBucket = new BucketHitCounter();
        print("case 3a: queue  getHits(1) with no hits", empty.getHits(1), 0);
        print("case 3b: bucket getHits(1) with no hits", emptyBucket.getHits(1), 0);

        for (int i = 0; i < 5; i++) {
            empty.hit(10);
            emptyBucket.hit(10);
        }
        print("case 3c: queue  5 hits in second 10", empty.getHits(10), 5);
        print("case 3d: bucket 5 hits in second 10", emptyBucket.getHits(10), 5);
        print("case 3e: queue  getHits(310) all aged out", empty.getHits(310), 0);
        print("case 3f: bucket getHits(310) all aged out", emptyBucket.getHits(310), 0);
    }
}
