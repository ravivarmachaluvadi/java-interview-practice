import java.util.*;

// https://leetcode.com/problems/subdomain-visit-count/description/
/**
 * Input: cpdomains = ["900 google.mail.com", "50 yahoo.com", "1 intel.mail.com", "5 wiki.org"]
 * <p>
 * Output: ["901 mail.com","50 yahoo.com","900 google.mail.com","5 wiki.org","5 org","1
 */
class SubdomainVisitCount {
    public static List<String> subdomainVisits(String[] cpdomains) {
        Map<String, Integer> domainCountMap = new HashMap<>();
        for (String cpdomain : cpdomains) {
            String[] parts = cpdomain.split(" ");
            int count = Integer.parseInt(parts[0]);
            String domain = parts[1];

            // \. remember this
            String[] subdomains = domain.split("\\.");
            String current = "";

            for (int i = subdomains.length - 1; i >= 0; i--) {
                current = subdomains[i] + (current.isEmpty() ? "" : "." + current);
                domainCountMap.put(current, domainCountMap.getOrDefault(current, 0) + count);
            }
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : domainCountMap.entrySet()) {
            result.add(entry.getValue() + " " + entry.getKey());
        }

        return result;
    }

    public static void main(String[] args) {
        String[] cpdomains = {
                "9001 discuss.leetcode.com",
                "50 yahoo.com",
                "1 intel.mail.com",
                "5 wiki.org"
        };

        List<String> result = subdomainVisits(cpdomains);
        System.out.println("Subdomain visit counts:");
        for (String s : result) {
            System.out.println(s);
        }
    }
}
/**
 * 9052 com
 * 9001 leetcode.com
 * 9001 discuss.leetcode.com
 * 1 intel.mail.com
 * 5 org
 * 5 wiki.org
 * 1 mail.com
 * 50 yahoo.com
 */
