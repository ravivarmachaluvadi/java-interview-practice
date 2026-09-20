/*
 * =====================================================================
 *  Stock Price Fluctuation                       LeetCode 2034 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A stream of (timestamp, price) records arrives out of order, and a record for a
 *   timestamp already seen is a CORRECTION that overwrites the old price. Support
 *   update(ts, price), current() (price at the largest timestamp seen), maximum() and
 *   minimum() over the corrected prices.
 *
 * EXAMPLE
 *   update(1, 10), update(2, 5)  ->  current = 5, maximum = 10, minimum = 5
 *   update(1, 3)                 ->  maximum = 5   because ts 1 is now 3, not 10
 *   update(4, 2)                 ->  minimum = 2   and current = 2 (ts 4 is newest)
 *
 * DESIGN
 *   Price               immutable (timestamp, price) pair -- one recorded reading.
 *   timestampToPrice    HashMap ts -> its CURRENT Price. The single source of truth;
 *                       the heaps are only caches of it.
 *   minHeap / maxHeap   PriorityQueue<Price> by price. A correction pushes a new Price
 *                       and leaves the old one behind, because a heap cannot delete an
 *                       interior element in better than O(n).
 *   latestTimestamp     running max of all timestamps, so current() is O(1).
 *
 * KEY DECISIONS
 *   - Lazy deletion: update() only pushes. Cleanup is paid by the next query that trips
 *     over a stale top.
 *   - Validity test on peek: (ts, p) is live only if timestampToPrice.get(ts).price == p.
 *     Anything else was superseded -- poll it and look again.
 *   - A stale entry can never win, since the live entry for the same timestamp is also
 *     in the heap and compares at least as well.
 *
 * KEY INSIGHT
 *   You cannot cheaply remove an arbitrary element from a binary heap, so do not try.
 *   Keep an authoritative map, let the heap collect garbage, and validate the peek
 *   against the map. Each entry is polled at most once, so the waste amortizes away.
 *
 * COMPLEXITY
 *   Time  update O(log n); current O(1); maximum/minimum O(log n) amortized.
 *   Space O(n) in the number of update() calls -- stale entries linger until evicted.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not TreeMap<price, count>? True O(log n) worst case and O(distinct) space, at
 *     the cost of a decrement-and-remove on every correction.
 *   - Bound the memory when corrections are frequent (rebuild the heaps on a threshold).
 *   - What changes if timestamps can be deleted, not just corrected?
 *   - Make it thread safe for concurrent updates and queries.
 *
 * RUN
 *   main() runs 3 scenarios (typical stream, correction of an old timestamp, single
 *   record) and prints actual vs expected.
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/** One recorded reading. Immutable, so a stale copy left in a heap is harmless. */
class Price {
    final int timestamp;
    final int price;

    Price(int timestamp, int price) {
        this.timestamp = timestamp;
        this.price = price;
    }
}

class StockPrice {

    private int latestTimestamp;

    /** Source of truth: timestamp -> its latest (corrected) price. */
    private final Map<Integer, Price> timestampToPrice = new HashMap<>();

    private final PriorityQueue<Price> minHeap =
            new PriorityQueue<>(Comparator.comparingInt(p -> p.price));

    private final PriorityQueue<Price> maxHeap =
            new PriorityQueue<>(Comparator.comparingInt((Price p) -> p.price).reversed());

    public StockPrice() {
        this.latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        Price reading = new Price(timestamp, price);
        timestampToPrice.put(timestamp, reading);   // overwrites any earlier reading
        // The old reading stays in both heaps as garbage; a query will evict it.
        minHeap.add(reading);
        maxHeap.add(reading);
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timestampToPrice.get(latestTimestamp).price;
    }

    public int maximum() {
        return peekLive(maxHeap);
    }

    public int minimum() {
        return peekLive(minHeap);
    }

    /**
     * Discards stale tops until the top entry still matches the map, then returns it.
     * Amortized O(log n): each entry can be discarded only once, ever.
     */
    private int peekLive(PriorityQueue<Price> heap) {
        while (!heap.isEmpty()) {
            Price top = heap.peek();
            if (timestampToPrice.get(top.timestamp).price == top.price) {
                return top.price;
            }
            heap.poll();    // superseded by a correction
        }
        return -1;          // only reachable if no update() has happened yet
    }
}

class StockPriceFluctuation {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // ---- case 1: typical stream, no corrections yet -------------------
        StockPrice stock = new StockPrice();
        stock.update(1, 10);
        stock.update(2, 5);
        print("case 1 current", stock.current(), 5);
        print("case 1 maximum", stock.maximum(), 10);
        print("case 1 minimum", stock.minimum(), 5);

        // ---- case 2: correction of an OLD timestamp (the whole point) -----
        stock.update(1, 3);                     // ts 1 was 10, is now 3
        print("case 2 maximum", stock.maximum(), 5);    // the 10 is now garbage
        print("case 2 minimum", stock.minimum(), 3);
        print("case 2 current", stock.current(), 5);    // ts 2 is still the newest
        stock.update(4, 2);                     // newest timestamp, new global min
        print("case 2 minimum after ts4", stock.minimum(), 2);
        print("case 2 current after ts4", stock.current(), 2);

        // ---- case 3: edge case, a single record corrected twice -----------
        StockPrice single = new StockPrice();
        single.update(7, 100);
        print("case 3 max == min", single.maximum() + "/" + single.minimum(), "100/100");
        single.update(7, 50);
        print("case 3 after correction", single.maximum() + "/" + single.minimum(), "50/50");
        print("case 3 current", single.current(), 50);
    }
}
