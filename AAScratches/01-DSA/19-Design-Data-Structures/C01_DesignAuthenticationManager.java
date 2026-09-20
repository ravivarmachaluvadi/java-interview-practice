/*
 * =====================================================================
 *  Design Authentication Manager                 LeetCode 1797 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Tokens live for timeToLive seconds. generate(tokenId, currentTime) creates a token
 *   that expires at currentTime + timeToLive. renew(tokenId, currentTime) pushes the
 *   expiry out by another timeToLive, but ONLY if the token exists and has not expired.
 *   countUnexpiredTokens(currentTime) returns how many tokens are still alive.
 *   Expiry is exclusive: a token expiring at time t is already dead at time t.
 *
 * EXAMPLE
 *   ttl = 5, generate("token1", 1)      ->  token1 expires at 6
 *   countUnexpiredTokens(2)             ->  1
 *   renew("token1", 6)                  ->  ignored: 6 >= 6, the token died exactly now
 *   countUnexpiredTokens(6)             ->  0
 *
 * APPROACH  (map tokenId -> expiry timestamp)
 *   1. generate: put(tokenId, currentTime + timeToLive), overwriting any old entry.
 *   2. renew: look up the expiry; return early if absent or if expiry <= currentTime,
 *      otherwise put the fresh expiry. The two guards are the whole problem.
 *   3. countUnexpiredTokens: scan the values and count expiry > currentTime.
 *   4. cleanup is an optional sweep that drops dead entries so the map stops growing.
 *
 * KEY INSIGHT
 *   Nothing ever has to be actively expired. Store the expiry timestamp and decide
 *   liveness lazily at read time by comparing with "now". The only detail that costs
 *   candidates the question is the boundary: expiry <= now means dead, so renewing at
 *   exactly the expiry second must fail.
 *
 * COMPLEXITY
 *   Time  O(1) generate and renew;  O(n) countUnexpiredTokens, n = tokens in the map
 *   Space O(n)  one entry per token until it is cleaned up
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make count O(1): keep a LinkedHashMap in expiry order and pop the dead front,
 *     since calls arrive with non-decreasing time - amortised O(1), like LC 362.
 *   - Distributed version: Redis keys with TTL, or a lazy check plus a background sweep.
 *   - Make it thread-safe under concurrent renew: ConcurrentHashMap.computeIfPresent.
 *
 * RUN
 *   main() runs 3 cases (lifecycle, renew edge cases, expiry boundary) and prints
 *   actual vs expected on each line.
 */

import java.util.HashMap;
import java.util.Map;

class DesignAuthenticationManager {

    private final int timeToLive;

    /** tokenId -> the timestamp at which it becomes invalid (exclusive) */
    private final Map<String, Integer> tokenExpiryMap;

    public DesignAuthenticationManager(int timeToLive) {
        this.timeToLive = timeToLive;
        this.tokenExpiryMap = new HashMap<>();
    }

    public void generate(String tokenId, int currentTime) {
        tokenExpiryMap.put(tokenId, currentTime + timeToLive);
    }

    public void renew(String tokenId, int currentTime) {
        Integer expiryTime = tokenExpiryMap.get(tokenId);
        if (expiryTime == null) {
            return; // token was never generated
        }
        if (expiryTime <= currentTime) {
            return; // already expired: expiry is exclusive, so == now is too late
        }
        tokenExpiryMap.put(tokenId, currentTime + timeToLive);
    }

    public int countUnexpiredTokens(int currentTime) {
        int count = 0;
        for (int expiryTime : tokenExpiryMap.values()) {
            if (expiryTime > currentTime) {
                count++;
            }
        }
        return count;
    }

    /** Optional housekeeping: physically drop dead tokens so the map does not grow forever. */
    public void cleanup(int currentTime) {
        tokenExpiryMap.entrySet().removeIf(entry -> entry.getValue() <= currentTime);
    }

    public int size() {
        return tokenExpiryMap.size();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): generate, let it die, generate more, renew a live one.
        DesignAuthenticationManager manager = new DesignAuthenticationManager(5);
        manager.generate("token1", 1); // expires at 6
        print("case 1a: count at 2", manager.countUnexpiredTokens(2), 1);

        manager.renew("token1", 6); // 6 >= 6, so this renew is ignored
        print("case 1b: count at 6 after failed renew", manager.countUnexpiredTokens(6), 0);

        manager.generate("token2", 7); // expires at 12
        manager.generate("token3", 8); // expires at 13
        print("case 1c: count at 10", manager.countUnexpiredTokens(10), 2);

        manager.renew("token2", 10); // still alive (12 > 10), so expiry moves to 15
        print("case 1d: count at 13 (token3 just died)", manager.countUnexpiredTokens(13), 1);
        print("case 1e: count at 20", manager.countUnexpiredTokens(20), 0);

        // Case 2 (edge): renewing a token that was never generated is a silent no-op.
        DesignAuthenticationManager edge = new DesignAuthenticationManager(3);
        edge.renew("ghost", 1);
        print("case 2a: count at 1 after renewing a ghost", edge.countUnexpiredTokens(1), 0);
        print("case 2b: map size (ghost not inserted)", edge.size(), 0);

        // Case 3 (tricky): renew one second before expiry works, exactly at expiry does not.
        DesignAuthenticationManager boundary = new DesignAuthenticationManager(5);
        boundary.generate("a", 0); // dies at 5
        boundary.renew("a", 4);    // 5 > 4, so it now dies at 9
        print("case 3a: count at 8", boundary.countUnexpiredTokens(8), 1);
        boundary.generate("b", 0); // dies at 5
        boundary.renew("b", 5);    // 5 <= 5, ignored
        print("case 3b: count at 6", boundary.countUnexpiredTokens(6), 1);
        boundary.cleanup(6);       // drops the dead token b
        print("case 3c: map size after cleanup(6)", boundary.size(), 1);
    }
}
