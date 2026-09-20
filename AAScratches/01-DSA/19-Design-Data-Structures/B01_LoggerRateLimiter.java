/*
 * =====================================================================
 *  Logger Rate Limiter                              LeetCode 359 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Build a logger that prints a message at most once every 10 seconds. Calls arrive
 *   as shouldPrintMessage(timestamp, message) with non-decreasing timestamps (seconds).
 *   Return true if the message may be printed now, false if it is still suppressed.
 *   Each distinct message has its own independent 10-second window.
 *
 * EXAMPLE
 *   (1,  "foo")  ->  true    first time seen, next "foo" allowed at 11
 *   (2,  "bar")  ->  true    a different message has its own window
 *   (3,  "foo")  ->  false   3 < 11
 *   (10, "foo")  ->  false   10 < 11, one second short
 *   (11, "foo")  ->  true    boundary is inclusive: >= nextAllowed prints
 *
 * APPROACH  (map each key to its next-allowed timestamp)
 *   1. Keep HashMap<message, nextAllowedTimestamp>.
 *   2. On a call, read nextAllowed (default 0, so an unseen message always prints).
 *   3. If timestamp < nextAllowed, return false and change nothing - a suppressed
 *      message must NOT extend its own window, or a chatty caller is silenced forever.
 *   4. Otherwise store timestamp + 10 and return true.
 *
 * KEY INSIGHT
 *   Store the deadline, not the history. One int per message answers "may I print?"
 *   in O(1), while a list of past timestamps would cost time and memory for nothing.
 *   Recognise this whenever a window only needs its next boundary, not its contents -
 *   and note the contrast with the hit counter, which must keep events because it
 *   has to COUNT them, not just gate them.
 *
 * COMPLEXITY
 *   Time  O(1)  one hash lookup plus one hash insert per call
 *   Space O(d)  d = number of distinct messages ever seen (this map never shrinks)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unbounded memory: evict stale keys with an LRU cache or a periodic sweep.
 *   - Timestamps arriving out of order? Store max(existing, timestamp + 10).
 *   - Allow N messages per window instead of one -> token bucket or a per-key queue.
 *   - Make it thread-safe: ConcurrentHashMap plus an atomic compare-and-set on the value.
 *
 * RUN
 *   main() runs 3 cases: the LeetCode sequence, the exact boundary, and time 0.
 */

import java.util.HashMap;
import java.util.Map;

class LoggerRateLimiter {

    private static final int WINDOW_SECONDS = 10;

    /** message -> the earliest timestamp at which it may be printed again */
    private final Map<String, Integer> messageNextAllowed;

    public LoggerRateLimiter() {
        messageNextAllowed = new HashMap<>();
    }

    public boolean shouldPrintMessage(int timestamp, String message) {
        int nextAllowed = messageNextAllowed.getOrDefault(message, 0);
        if (timestamp < nextAllowed) {
            return false; // still inside the window; deliberately do not push the deadline out
        }
        messageNextAllowed.put(message, timestamp + WINDOW_SECONDS);
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): the LeetCode example, two interleaved messages.
        LoggerRateLimiter logger = new LoggerRateLimiter();
        print("case 1a: (1, foo)", logger.shouldPrintMessage(1, "foo"), true);
        print("case 1b: (2, bar)", logger.shouldPrintMessage(2, "bar"), true);
        print("case 1c: (3, foo)", logger.shouldPrintMessage(3, "foo"), false);
        print("case 1d: (8, bar)", logger.shouldPrintMessage(8, "bar"), false);
        print("case 1e: (10, foo)", logger.shouldPrintMessage(10, "foo"), false);
        print("case 1f: (11, foo)", logger.shouldPrintMessage(11, "foo"), true);

        // Case 2 (edge): a rejected call must not restart the clock.
        LoggerRateLimiter boundary = new LoggerRateLimiter();
        print("case 2a: (100, hi)", boundary.shouldPrintMessage(100, "hi"), true);
        for (int t = 101; t < 110; t++) {
            boundary.shouldPrintMessage(t, "hi"); // nine suppressed calls in a row
        }
        print("case 2b: (110, hi) after 9 rejects", boundary.shouldPrintMessage(110, "hi"), true);

        // Case 3 (edge): timestamp 0, where the map's default of 0 has to behave.
        LoggerRateLimiter zero = new LoggerRateLimiter();
        print("case 3a: (0, start)", zero.shouldPrintMessage(0, "start"), true);
        print("case 3b: (9, start)", zero.shouldPrintMessage(9, "start"), false);
        print("case 3c: (10, start)", zero.shouldPrintMessage(10, "start"), true);
    }
}
