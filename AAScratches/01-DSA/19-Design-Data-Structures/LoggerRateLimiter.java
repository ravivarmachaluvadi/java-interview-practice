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
