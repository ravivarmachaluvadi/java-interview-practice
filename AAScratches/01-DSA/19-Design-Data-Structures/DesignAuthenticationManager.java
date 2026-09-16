import java.util.HashMap;
import java.util.Map;

// 1797. Design Authentication Manager
// https://leetcode.com/problems/design-authentication-manager/description/
class DesignAuthenticationManager {

    private final int timeToLive;
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
            // Token not present
            return;
        }
        if (expiryTime <= currentTime) {
            // Token already expired
            return;
        }
        // Renew valid token
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

    // Helper method to clean expired tokens (optional optimization)
    public void cleanup(int currentTime) {
        tokenExpiryMap.entrySet().removeIf(entry -> entry.getValue() <= currentTime);
    }

    public static void main(String[] args) {
        // Example usage
        DesignAuthenticationManager manager = new DesignAuthenticationManager(5); // TTL = 5 seconds

        // Step 1: Generate a token at time = 1
        manager.generate("token1", 1);
        System.out.println("Unexpired tokens at time 2: " + manager.countUnexpiredTokens(2)); // Expected 1

        // Step 2: Renew before expiry
        manager.renew("token1", 6); // token1 expired at 6 (1 + 5), so renewal fails
        System.out.println("Unexpired tokens at time 6: " + manager.countUnexpiredTokens(6)); // Expected 0

        // Step 3: Generate more tokens
        manager.generate("token2", 7);
        manager.generate("token3", 8);
        System.out.println("Unexpired tokens at time 10: " + manager.countUnexpiredTokens(10)); // Expected 2

        // Step 4: Renew valid token
        manager.renew("token2", 10); // token2 was generated at 7 → expired at 12, still valid
        System.out.println("Unexpired tokens at time 13: " + manager.countUnexpiredTokens(13)); // Expected 1 (only token2 renewed)

        // Step 5: After all expired
        System.out.println("Unexpired tokens at time 20: " + manager.countUnexpiredTokens(20)); // Expected 0
    }
}
