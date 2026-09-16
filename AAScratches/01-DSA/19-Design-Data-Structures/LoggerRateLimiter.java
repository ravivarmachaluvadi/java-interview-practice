/**
 * Problem: Implement a logger that suppresses duplicate messages within a 10‑second window.
 *
 * Approach: Keep a map from each message to the earliest timestamp at which it can be printed again.
 * When a new request arrives, compare its timestamp with the stored value; if the current time is
 * earlier, reject the message. Otherwise update the next allowed time to `timestamp + 10` and allow printing.
 *
 * Time Complexity: O(1) per call (hash map lookup/insert).
 * Space Complexity: O(n) where n is the number of distinct messages seen so far.
 */
import java.util.HashMap;
import java.util.Map;

public class LoggerRateLimiter {
    private final Map<String, Integer> messageNextAllowed;

    public LoggerRateLimiter() {
        messageNextAllowed = new HashMap<>();
    }

    public boolean shouldPrintMessage(int timestamp, String message) {
        int nextAllowed = messageNextAllowed.getOrDefault(message, 0);
        if (timestamp < nextAllowed) {
            return false;
        }
        messageNextAllowed.put(message, timestamp + 10);
        return true;
    }

    public static void main(String[] args) {
        LoggerRateLimiter logger = new LoggerRateLimiter();

        // Example as per the problem description
        System.out.println(logger.shouldPrintMessage(1, "foo"));   // true
        System.out.println(logger.shouldPrintMessage(2, "bar"));   // true
        System.out.println(logger.shouldPrintMessage(3, "foo"));   // false
        System.out.println(logger.shouldPrintMessage(8, "bar"));   // false
        System.out.println(logger.shouldPrintMessage(10, "foo"));  // false
        System.out.println(logger.shouldPrintMessage(11, "foo"));  // true
    }
}
