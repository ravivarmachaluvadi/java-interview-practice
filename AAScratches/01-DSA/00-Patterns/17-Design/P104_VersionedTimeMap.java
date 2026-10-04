/*
 * =====================================================================
 *  P104 Design: Versioned and Time-Windowed Data   Canonical LC 981 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 981, Time Based Key-Value Store)
 *   set(key, value, timestamp) stores a value; get(key, timestamp) returns the value set at
 *   the LARGEST timestamp <= the given one, or "" if none. Timestamps of set() only grow.
 *
 * EXAMPLE
 *   set(foo, bar, 1); get(foo, 1) = bar; get(foo, 3) = bar; set(foo, bar2, 4);
 *   get(foo, 4) = bar2; get(foo, 5) = bar2; get(foo, 0) = ""
 *
 * RECOGNIZE WHEN
 *   - "value at time t", "as of version v", "snapshot", "undo history": many versions per
 *     key, queried by the latest version <= t.
 *   - "hits / requests in the last N seconds", "allow a message once every 10 seconds":
 *     only a recent time window matters.
 *   Not this if: only the current value matters -> a plain HashMap; you need the k-th
 *   largest value in a window -> P045_TwoHeaps.
 *
 * TEMPLATE
 *   versions: key -> list of (time, value) appended in time order
 *   get(key, t): binary search the last entry with time <= t (or TreeMap.floorEntry(t))
 *   snapshot array: per index, list of (snapId, value); get = floor search on snapId
 *   sliding time window: queue of timestamps; pop the front while it is older than t - W
 *
 * APPROACH
 *   1. Because set() timestamps increase, each key's list is already sorted by time.
 *   2. get() binary searches that list for the last time <= t.
 *
 * KEY INSIGHT
 *   Never overwrite history: append versions, and let a floor search answer "as of". When
 *   writes arrive in time order, a list plus binary search beats a TreeMap; when they do
 *   not, TreeMap.floorEntry is the same idea with O(log n) inserts.
 *
 * COMPLEXITY
 *   set O(1), get O(log v) per key. Hit counter: O(1) amortised per call.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1146 Snapshot Array           per index (snapId, value) history; floor search
 *   [coded] LC 362  Design Hit Counter       queue of hit times; drop those <= t - 300
 *   [coded] LC 359  Logger Rate Limiter      map message -> next allowed time
 *           LC 1352 Product of Last K        prefix products, reset on 0
 *           LC 2034 Stock Price Fluctuation  latest price per time + TreeMap of prices
 *           LC 635  Log Storage System       timestamps as strings, compare by granularity
 *
 * PITFALLS
 *   - "" (not null) when no version is old enough.
 *   - LC 1146: several sets between two snaps keep only the last one for that snap id.
 *   - Hit counter window: "last 300 seconds" includes t - 299 .. t, so drop t - 300 and older.
 *
 * DEEP DIVE
 *   C06_TimeBasedKeyValueStore, C07_SnapshotArray, C02_DesignHitCounter,
 *   B01_LoggerRateLimiter (19-Design-Data-Structures)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class VersionedTimeMap {

    // Canonical LC 981.
    static class TimeMap {
        private final Map<String, List<Integer>> times = new HashMap<>();
        private final Map<String, List<String>> values = new HashMap<>();

        void set(String key, String value, int timestamp) {
            times.computeIfAbsent(key, k -> new ArrayList<>()).add(timestamp);
            values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        String get(String key, int timestamp) {
            List<Integer> t = times.get(key);
            if (t == null) {
                return "";
            }
            int i = lastAtMost(t, timestamp);
            return i < 0 ? "" : values.get(key).get(i);
        }
    }

    // Index of the last element <= x in a sorted list, or -1.
    static int lastAtMost(List<Integer> sorted, int x) {
        int lo = 0;
        int hi = sorted.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (sorted.get(mid) <= x) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo - 1;
    }

    // LC 1146.
    static class SnapshotArray {
        private final List<List<int[]>> history = new ArrayList<>();   // {snapId, value}
        private int snapId;

        SnapshotArray(int length) {
            for (int i = 0; i < length; i++) {
                List<int[]> h = new ArrayList<>();
                h.add(new int[]{0, 0});
                history.add(h);
            }
        }

        void set(int index, int val) {
            List<int[]> h = history.get(index);
            if (h.get(h.size() - 1)[0] == snapId) {
                h.get(h.size() - 1)[1] = val;      // same snap: overwrite
            } else {
                h.add(new int[]{snapId, val});
            }
        }

        int snap() {
            return snapId++;
        }

        int get(int index, int snap) {
            List<int[]> h = history.get(index);
            int lo = 0;
            int hi = h.size();
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (h.get(mid)[0] <= snap) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            return h.get(lo - 1)[1];
        }
    }

    // LC 362: hits in the past 300 seconds; timestamps never decrease.
    static class HitCounter {
        private final Deque<Integer> hits = new ArrayDeque<>();

        void hit(int timestamp) {
            hits.addLast(timestamp);
        }

        int getHits(int timestamp) {
            while (!hits.isEmpty() && hits.peekFirst() <= timestamp - 300) {
                hits.pollFirst();
            }
            return hits.size();
        }
    }

    // LC 359: each message may be printed at most once every 10 seconds.
    static class Logger {
        private final Map<String, Integer> nextAllowed = new HashMap<>();

        boolean shouldPrintMessage(int timestamp, String message) {
            if (timestamp < nextAllowed.getOrDefault(message, 0)) {
                return false;
            }
            nextAllowed.put(message, timestamp + 10);
            return true;
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        TimeMap tm = new TimeMap();
        tm.set("foo", "bar", 1);
        List<String> got = new ArrayList<>();
        got.add(tm.get("foo", 1));
        got.add(tm.get("foo", 3));
        tm.set("foo", "bar2", 4);
        got.add(tm.get("foo", 4));
        got.add(tm.get("foo", 5));
        check("LC 981 four reads", got, "[bar, bar, bar2, bar2]");
        check("LC 981 before any set", "[" + tm.get("foo", 0) + "]", "[]");
        check("LC 981 unknown key", "[" + tm.get("nope", 9) + "]", "[]");

        SnapshotArray sa = new SnapshotArray(3);
        sa.set(0, 5);
        int s0 = sa.snap();
        sa.set(0, 6);
        check("LC 1146 snap 0 sees 5", sa.get(0, s0), 5);
        int s1 = sa.snap();
        check("LC 1146 snap 1 sees 6", sa.get(0, s1), 6);
        check("LC 1146 untouched index", sa.get(2, s1), 0);

        HitCounter hc = new HitCounter();
        hc.hit(1);
        hc.hit(2);
        hc.hit(3);
        int a = hc.getHits(4);
        hc.hit(300);
        int b = hc.getHits(300);
        int c = hc.getHits(301);
        check("LC 362 three reads", a + "," + b + "," + c, "3,4,3");

        Logger log = new Logger();
        List<Boolean> printed = new ArrayList<>();
        printed.add(log.shouldPrintMessage(1, "foo"));
        printed.add(log.shouldPrintMessage(2, "bar"));
        printed.add(log.shouldPrintMessage(3, "foo"));
        printed.add(log.shouldPrintMessage(8, "bar"));
        printed.add(log.shouldPrintMessage(10, "foo"));
        printed.add(log.shouldPrintMessage(11, "foo"));
        check("LC 359 six messages", printed, "[true, true, false, false, false, true]");
    }
}
