/*
 * =====================================================================
 *  Top K Videos by Total Watch Time            Karat / phone screen | Easy
 * =====================================================================
 *
 * PROBLEM
 *   You are given a stream of watch events, each a (videoName, watchTime) pair.
 *   The same video appears many times. Return the names of the 10 videos with the
 *   highest TOTAL watch time, best first. Ties are broken by name ascending so the
 *   answer is deterministic. Fewer than 10 distinct videos -> return all of them.
 *
 * EXAMPLE
 *   [(abc,10) (def,15) (ghi,10) (abc,12) (xyz,100) (abc,5) (def,20)]
 *     totals: xyz=100, def=35, abc=27, ghi=10  ->  [xyz, def, abc, ghi]
 *   [(b,10) (a,10) (c,3)]  ->  [a, b, c]        a and b tie at 10, name breaks it
 *   []                     ->  []               empty stream, empty answer
 *
 * APPROACH  (group-by-key, then top-k)
 *   1. Fold the events into Map<videoName, totalWatchTime>. One pass, O(n).
 *   2. topVideosBySort: copy the entry set to a list, sort by total descending
 *      (name ascending on a tie), take the first k. O(m log m) for m distinct videos.
 *   3. topVideosByHeap: keep a size-k MIN-heap ordered by the SAME rule. Push every
 *      entry; when the heap exceeds k, evict its head (the current worst). At the end
 *      the heap holds the best k; drain it and reverse. O(m log k).
 *   4. main() runs both and asserts they agree - identical comparator, identical answer.
 *
 * KEY INSIGHT
 *   Top-k is two independent decisions: the aggregation (a HashMap fold) and the
 *   selection (sort vs heap). Sorting is O(m log m) and gives you the full ranking;
 *   a size-k min-heap is O(m log k) and gives you only the k winners. Because k = 10
 *   here, the heap is the answer an interviewer wants once m gets large or the input
 *   is a stream that does not fit in memory. The min-heap is counter-intuitive on
 *   purpose: to keep the LARGEST k you must be able to cheaply discard the smallest
 *   one you are holding, so the heap head is the weakest survivor.
 *   Fixed: the original comparator sorted on total only, so videos with equal totals
 *   came out in HashMap iteration order - a different answer on a different JVM run.
 *
 * COMPLEXITY
 *   Time  O(n + m log m) sort form, O(n + m log k) heap form; n events, m distinct
 *   Space O(m)  the totals map dominates; the heap itself is only O(k)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why a min-heap and not a max-heap for top-k? (evict the worst in O(log k))
 *   - The events arrive as an unbounded stream - what changes? (heap, or count-min
 *     sketch plus heap when even the distinct-video map will not fit)
 *   - Top 10 per hour with old events expiring - how? (bucket by time window)
 *   - If m is huge but k is tiny, is Quickselect better? (O(m) average, but it
 *     destroys the order and needs the whole array in memory)
 *
 * RUN
 *   main() runs 3 cases (typical, tie-break, empty) through both implementations
 *   and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class TopVideos {

    private static final int DEFAULT_K = 10;

    /**
     * Best total watch time first; ties broken by name ascending.
     * Used by BOTH implementations so their answers are directly comparable.
     */
    private static final Comparator<Map.Entry<String, Integer>> BEST_FIRST =
            Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                      .thenComparing(Map.Entry::getKey);

    // ---------- Step 1: aggregation, shared by both approaches ----------

    /** Fold the raw events into video -> total watch time. O(n). */
    private static Map<String, Integer> totalWatchTimePerVideo(List<Pair<String, Integer>> events) {
        Map<String, Integer> totals = new HashMap<>();
        for (Pair<String, Integer> event : events) {
            totals.merge(event.getKey(), event.getValue(), Integer::sum);
        }
        return totals;
    }

    // ---------- Approach 1: sort the whole thing ----------

    /** The problem as asked: top 10. */
    public static List<String> topVideos(List<Pair<String, Integer>> events) {
        return topVideosBySort(events, DEFAULT_K);
    }

    /** O(m log m). Simple, and it hands you the full ranking if you ever need it. */
    public static List<String> topVideosBySort(List<Pair<String, Integer>> events, int k) {
        List<Map.Entry<String, Integer>> ranked =
                new ArrayList<>(totalWatchTimePerVideo(events).entrySet());
        ranked.sort(BEST_FIRST);

        List<String> top = new ArrayList<>();
        for (int i = 0; i < Math.min(k, ranked.size()); i++) {
            top.add(ranked.get(i).getKey());
        }
        return top;
    }

    // ---------- Approach 2: bounded min-heap ----------

    /**
     * O(m log k). The heap is ordered WORST-first (BEST_FIRST reversed), so its head is
     * always the weakest video currently held and can be evicted in O(log k).
     */
    public static List<String> topVideosByHeap(List<Pair<String, Integer>> events, int k) {
        if (k <= 0) return new ArrayList<>();

        PriorityQueue<Map.Entry<String, Integer>> worstFirst =
                new PriorityQueue<>(BEST_FIRST.reversed());

        for (Map.Entry<String, Integer> entry : totalWatchTimePerVideo(events).entrySet()) {
            worstFirst.offer(entry);
            if (worstFirst.size() > k) {
                worstFirst.poll(); // drop the weakest survivor, never the strongest
            }
        }

        // Draining a worst-first heap yields ascending order, so reverse it.
        List<String> top = new ArrayList<>();
        while (!worstFirst.isEmpty()) {
            top.add(worstFirst.poll().getKey());
        }
        Collections.reverse(top);
        return top;
    }

    // ---------- Test harness ----------

    private static List<Pair<String, Integer>> events(Object... nameThenTime) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        for (int i = 0; i < nameThenTime.length; i += 2) {
            list.add(new Pair<>((String) nameThenTime[i], (Integer) nameThenTime[i + 1]));
        }
        return list;
    }

    private static void check(String label, List<Pair<String, Integer>> input,
                              int k, String expected) {
        List<String> bySort = topVideosBySort(input, k);
        List<String> byHeap = topVideosByHeap(input, k);
        System.out.println(label + " sort: " + bySort + "   expected " + expected);
        System.out.println(label + " heap: " + byHeap + "   expected " + expected
                + "   (agrees with sort: " + bySort.equals(byHeap) + ", expected true)");
    }

    public static void main(String[] args) {
        // case 1 - typical: repeated videos, k larger than the number of distinct videos
        check("case 1", events(
                "abc", 10, "def", 15, "ghi", 10, "abc", 12,
                "xyz", 100, "abc", 5, "def", 20), 10, "[xyz, def, abc, ghi]");

        // case 2 - tricky: a and b tie at 10, so the name tie-break decides
        check("case 2", events("b", 10, "a", 10, "c", 3), 10, "[a, b, c]");

        // case 3 - edge: no events at all
        check("case 3", events(), 10, "[]");

        // case 4 - edge: k smaller than the number of distinct videos
        check("case 4", events("p", 1, "q", 9, "r", 5), 2, "[q, r]");

        // the method as the interviewer asked for it (top 10, no k argument)
        System.out.println("case 5: " + topVideos(events("solo", 7)) + "   expected [solo]");
    }
}

/** Minimal pair holder so the file stays dependency-free
 *  (javafx.util.Pair is not on the JDK path). */
class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
