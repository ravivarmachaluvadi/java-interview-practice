/*
 * =====================================================================
 *  Most Frequent IP In A Log File                  Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given server log lines where each line starts with an IP address followed
 *   by a space and arbitrary text, return the IP that appears most often.
 *   Lines are well formed; the log may be empty, in which case return "".
 *   Ties are not defined by the problem - see KEY INSIGHT.
 *
 * EXAMPLE
 *   ["10.0.0.1 - GET /a", "10.0.0.1 - GET /b", "10.0.0.2 - GET /c"]  ->  "10.0.0.1"
 *   ["10.0.0.7 - only line"]  ->  "10.0.0.7"    single entry
 *   []                        ->  ""            empty log, the edge case main() runs
 *
 * APPROACH  (frequency map plus argmax scan)
 *   1. Walk the log once. For each line take the first whitespace-separated
 *      token - that is the IP.
 *   2. Count it into a HashMap<String, Integer> with getOrDefault(ip, 0) + 1.
 *   3. Walk the map's entrySet once, keeping the entry with the largest count
 *      seen so far (argmax). Return its key.
 *   4. findTopIpLexicographic() is the same scan with a deterministic tie-break,
 *      so a tied log always yields the same answer.
 *
 * KEY INSIGHT
 *   Count-then-argmax is two independent linear passes, not one clever pass -
 *   you cannot know the winner until every line is counted. The trap is the
 *   argmax comparison: a strict ">" keeps the FIRST maximum in HashMap iteration
 *   order, and that order is not the insertion order and not stable across JVM
 *   runs. If ties are possible, state a tie-break rule out loud (smallest IP,
 *   earliest seen) instead of shipping an order-dependent answer.
 *
 * COMPLEXITY
 *   Time  O(n * L)  n lines, L the cost of splitting one line to its first token
 *   Space O(k)      k distinct IP addresses held in the map
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the top k IPs, not just one - heap of size k, or a full sort.
 *   - The log does not fit in memory: stream it, or shard by hash of the IP.
 *   - Count only the last 5 minutes - sliding window over timestamped entries.
 *   - Approximate heavy hitters with Count-Min Sketch or Misra-Gries.
 *
 * RUN
 *   main() runs 4 cases (typical, single line, empty log, tie) and prints
 *   actual vs expected on one line each.
 */
import java.util.HashMap;
import java.util.Map;

class FindTopIpaddress {

    /** Most frequent leading IP. On a tie the winner depends on map iteration order. */
    public static String findTopIpaddress(String[] inputs) {
        Map<String, Integer> ipCountMap = new HashMap<>();

        for (String logEntry : inputs) {
            String ip = logEntry.split(" ")[0]; // the IP is the first token of the line
            ipCountMap.put(ip, ipCountMap.getOrDefault(ip, 0) + 1);
        }

        String topIp = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : ipCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                topIp = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return topIp;
    }

    /**
     * Same count-and-argmax, but ties break on the lexicographically smallest IP,
     * so the result no longer depends on HashMap iteration order.
     */
    public static String findTopIpLexicographic(String[] inputs) {
        Map<String, Integer> ipCountMap = new HashMap<>();
        for (String logEntry : inputs) {
            String ip = logEntry.split(" ")[0];
            ipCountMap.put(ip, ipCountMap.getOrDefault(ip, 0) + 1);
        }

        String topIp = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : ipCountMap.entrySet()) {
            int count = entry.getValue();
            String ip = entry.getKey();
            boolean better = count > maxCount
                    || (count == maxCount && !topIp.isEmpty() && ip.compareTo(topIp) < 0);
            if (better) {
                topIp = ip;
                maxCount = count;
            }
        }
        return topIp;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        String[] typical = {
                "10.0.0.1 - log entry 1 11",
                "10.0.0.1 - log entry 2 213",
                "10.0.0.2 - log entry 133132"
        };
        String[] single = {"10.0.0.7 - the only line"};
        String[] empty = {};
        String[] tied = {"10.0.0.9 - a", "10.0.0.3 - b"}; // one vote each

        print("case 1 typical", findTopIpaddress(typical), "10.0.0.1");
        print("case 2 single line", findTopIpaddress(single), "10.0.0.7");
        print("case 3 empty log (edge)", findTopIpaddress(empty), "");
        // The plain version is order-dependent on a tie, so the tie case uses the
        // deterministic variant: both IPs have one hit, smaller string wins.
        print("case 4 tie, lexicographic", findTopIpLexicographic(tied), "10.0.0.3");
    }
}
