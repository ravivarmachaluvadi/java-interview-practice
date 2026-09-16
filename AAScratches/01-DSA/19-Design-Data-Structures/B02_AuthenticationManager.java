import java.util.HashMap;
import java.util.Map;

// https://leetcode.com/problems/design-authentication-manager/description/
// 1797. Design Authentication Manager
class AuthenticationManager {
    private final int timeToLive;
    private final Map<String, Integer> tokenExpiryMap;

    public AuthenticationManager(int timeToLive) {
        this.timeToLive = timeToLive;
        this.tokenExpiryMap = new HashMap<>();
    }

    public void generate(String tokenId, int currentTime) {
        tokenExpiryMap.put(tokenId, currentTime + timeToLive);
    }

    public void renew(String tokenId, int currentTime) {
        Integer expiryTime = tokenExpiryMap.get(tokenId);
        if (expiryTime == null) {
            // token not present → nothing to do
            return;
        }
        if (expiryTime <= currentTime) {
            // token already expired at or before currentTime → cannot renew
            return;
        }
        // valid token → renew by setting new expiry
        tokenExpiryMap.put(tokenId, currentTime + timeToLive);
    }

    public int countUnexpiredTokens(int currentTime) {
        int count = 0;
        for (Integer expiryTime : tokenExpiryMap.values()) {
            if (expiryTime > currentTime) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        // Example from description / custom test
        AuthenticationManager mgr = new AuthenticationManager(5);  // timeToLive = 5 seconds

        mgr.renew("aaa", 1);      // nothing happens, "aaa" does not exist
        mgr.generate("aaa", 2);   // token "aaa" generated at time 2 → expires at time 7
        System.out.println(mgr.countUnexpiredTokens(6));  // expect 1 (only "aaa" is valid at time 6)

        mgr.generate("bbb", 7);   // token "bbb" at time 7 → expires at time 12
        mgr.renew("aaa", 8);      // "aaa" expired at time 7 → at time 8 cannot renew → ignored
        mgr.renew("bbb", 10);     // "bbb" expires at 12 > 10 → valid → renewed to expire at 10+5=15

        System.out.println(mgr.countUnexpiredTokens(15)); // at time 15: "bbb" expiry =15 so expired by this time → expect 0
    }
}
