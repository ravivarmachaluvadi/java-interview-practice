/*
 * =====================================================================
 *  P047 Ordered Map: TreeMap floor / ceiling   Canonical LC 729 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 729, My Calendar I)
 *   book(start, end) adds the half-open event [start, end) if it does not overlap any
 *   booked event, and returns whether it was added.
 *
 * EXAMPLE
 *   book(10, 20) -> true, book(15, 25) -> false, book(20, 30) -> true
 *
 * RECOGNIZE WHEN
 *   - You keep inserting / deleting AND need "the nearest key below / above x" (floor,
 *     ceiling, lower, higher) or the smallest / largest key, all in O(log n).
 *   - Interval bookings, "is there a value within t of x among the last k", grouping
 *     consecutive values, counts that must be consumed smallest first.
 *   Not this if: you only need the min or max, never "nearest to x" -> a heap is lighter
 *   (P043_TopK); the keys are static and sorted -> binary search (P023_BoundarySearch).
 *
 * TEMPLATE
 *   TreeMap<Integer, V> map / TreeSet<Integer> set
 *   below = map.floorKey(x)     (<= x)     lower = map.lowerKey(x)    (< x)
 *   above = map.ceilingKey(x)   (>= x)     higher = map.higherKey(x)  (> x)
 *   map.firstKey(), map.lastKey(), map.headMap(x), map.tailMap(x)
 *   diff-map sweep: map.merge(start, +1), map.merge(end, -1), walk values in key order
 *
 * APPROACH
 *   1. Store booked events as start -> end in a TreeMap.
 *   2. The only events that can clash with [s, e) are the one starting just before s
 *      (floor) and the one starting at or after s (ceiling).
 *   3. Book if prev.end <= s and next.start >= e.
 *
 * KEY INSIGHT
 *   A balanced BST keeps keys sorted under updates, so "neighbours of x" are O(log n)
 *   questions. Many problems reduce to checking just the two neighbours of a new key
 *   instead of every existing key.
 *
 * COMPLEXITY
 *   O(log n) per operation for floor / ceiling / put / remove. LC 731 diff sweep: O(n) per
 *   booking.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 220  Contains Duplicate III   TreeSet of the last k values; ceiling(x - t)
 *                                            must be <= x + t
 *   [coded] LC 846  Hand of Straights        TreeMap of counts; always start a group at
 *                                            firstKey() and consume k consecutive keys
 *   [coded] LC 731  My Calendar II           diff map (+1 at start, -1 at end); reject if
 *                                            the running sum ever reaches 3
 *           LC 732  My Calendar III          the same sweep; answer = max running sum
 *           LC 352  Data Stream Intervals    floor / ceiling neighbours merge on insert
 *           LC 218  The Skyline Problem      events + TreeMap of active heights (counts)
 *           LC 2034 Stock Price Fluctuation  map time -> price + TreeMap price -> count
 *           LC 981  Time Based Key-Value     floorKey(timestamp) -> P104_VersionedTimeMap
 *
 * PITFALLS
 *   - floorKey / ceilingKey return null when nothing qualifies; check before unboxing.
 *   - TreeSet drops duplicates; keep counts in a TreeMap when values repeat.
 *   - LC 220: x - t and x + t overflow int; use long.
 *
 * DEEP DIVE
 *   C08_StockPriceFluctuation, C06_TimeBasedKeyValueStore (19-Design-Data-Structures)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

class TreeMapFloorCeiling {

    // Canonical LC 729.
    static class MyCalendar {
        private final TreeMap<Integer, Integer> events = new TreeMap<>();   // start -> end

        boolean book(int start, int end) {
            Map.Entry<Integer, Integer> prev = events.floorEntry(start);
            Map.Entry<Integer, Integer> next = events.ceilingEntry(start);
            boolean clashBefore = prev != null && prev.getValue() > start;
            boolean clashAfter = next != null && next.getKey() < end;
            if (clashBefore || clashAfter) {
                return false;
            }
            events.put(start, end);
            return true;
        }
    }

    // LC 220: indices at most indexDiff apart, values at most valueDiff apart.
    static boolean containsNearbyAlmostDuplicate(int[] nums, int indexDiff, int valueDiff) {
        TreeSet<Long> window = new TreeSet<>();
        for (int i = 0; i < nums.length; i++) {
            Long candidate = window.ceiling((long) nums[i] - valueDiff);
            if (candidate != null && candidate <= (long) nums[i] + valueDiff) {
                return true;
            }
            window.add((long) nums[i]);
            if (i >= indexDiff) {
                window.remove((long) nums[i - indexDiff]);
            }
        }
        return false;
    }

    // LC 846: split the hand into groups of groupSize consecutive cards.
    static boolean isNStraightHand(int[] hand, int groupSize) {
        TreeMap<Integer, Integer> count = new TreeMap<>();
        for (int card : hand) {
            count.merge(card, 1, Integer::sum);
        }
        while (!count.isEmpty()) {
            int first = count.firstKey();              // the smallest card must start a group
            for (int card = first; card < first + groupSize; card++) {
                Integer c = count.get(card);
                if (c == null) {
                    return false;
                }
                if (c == 1) {
                    count.remove(card);
                } else {
                    count.put(card, c - 1);
                }
            }
        }
        return true;
    }

    // LC 731: no point may be covered three times.
    static class MyCalendarTwo {
        private final TreeMap<Integer, Integer> diff = new TreeMap<>();

        boolean book(int start, int end) {
            diff.merge(start, 1, Integer::sum);
            diff.merge(end, -1, Integer::sum);
            int active = 0;
            for (int delta : diff.values()) {
                active += delta;
                if (active >= 3) {
                    diff.merge(start, -1, Integer::sum);   // undo
                    diff.merge(end, 1, Integer::sum);
                    return false;
                }
            }
            return true;
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MyCalendar cal = new MyCalendar();
        List<Boolean> got = new ArrayList<>();
        got.add(cal.book(10, 20));
        got.add(cal.book(15, 25));
        got.add(cal.book(20, 30));
        check("LC 729 three bookings", got, "[true, false, true]");
        check("LC 729 inside a gap", cal.book(5, 10), true);
        check("LC 729 covering an event", cal.book(0, 40), false);

        check("LC 220 [1,2,3,1] k=3 t=0",
                containsNearbyAlmostDuplicate(new int[]{1, 2, 3, 1}, 3, 0), true);
        check("LC 220 [1,5,9,1,5,9] k=2 t=3",
                containsNearbyAlmostDuplicate(new int[]{1, 5, 9, 1, 5, 9}, 2, 3), false);
        int[] extremes = {Integer.MIN_VALUE, Integer.MAX_VALUE};
        check("LC 220 overflow trap", containsNearbyAlmostDuplicate(extremes, 1, 1), false);

        check("LC 846 groups of 3", isNStraightHand(new int[]{1, 2, 3, 6, 2, 3, 4, 7, 8}, 3), true);
        check("LC 846 groups of 4", isNStraightHand(new int[]{1, 2, 3, 4, 5}, 4), false);

        MyCalendarTwo two = new MyCalendarTwo();
        List<Boolean> got2 = new ArrayList<>();
        int[][] books = {{10, 20}, {50, 60}, {10, 40}, {5, 15}, {5, 10}, {25, 55}};
        for (int[] b : books) {
            got2.add(two.book(b[0], b[1]));
        }
        check("LC 731 six bookings", got2, "[true, true, true, false, true, true]");
    }
}
