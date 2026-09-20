/*
 * =====================================================================
 *  Subdomain Visit Count                          LeetCode 811 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Each input row is "<count> <domain>", e.g. "9001 discuss.leetcode.com". A visit to a
 *   domain also counts as a visit to every parent domain: "leetcode.com" and "com".
 *   Return one "<total> <domain>" row per distinct domain or subdomain, in any order.
 *
 * EXAMPLE
 *   ["9001 discuss.leetcode.com","50 yahoo.com","1 intel.mail.com","5 wiki.org"]
 *     -> 1 intel.mail.com, 1 mail.com, 5 org, 5 wiki.org, 50 yahoo.com,
 *        9001 discuss.leetcode.com, 9001 leetcode.com, 9052 com
 *   []  ->  []
 *   ["900 google.mail.com","50 yahoo.com","1 intel.mail.com","5 wiki.org"]
 *     -> ... 900 google.mail.com, 901 mail.com, 951 com    (two rows share mail.com)
 *
 * APPROACH  (map accumulation over derived keys)
 *   1. Split each row on the space: parts[0] is the count, parts[1] the full domain.
 *   2. Split the domain on "." (regex, so it must be written "\\.").
 *   3. Walk the labels right to left, growing the suffix: "com", "leetcode.com",
 *      "discuss.leetcode.com". Add the count to the map entry for each suffix.
 *   4. Emit "value key" for every map entry.
 *
 * KEY INSIGHT
 *   One input row updates several map keys. The trick is to derive the set of keys
 *   (all suffixes of the domain) from the row and accumulate into each with getOrDefault.
 *   Building suffixes right to left avoids substring/indexOf juggling.
 *
 * COMPLEXITY
 *   Time  O(n * L)  n rows, each split and re-joined over its L labels
 *   Space O(n * L)  one map entry per distinct suffix across all rows
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why "\\." and not "."? split takes a regex; "." alone matches every char.
 *   - Building the suffix by string concatenation is O(L^2) per row; use lastIndexOf on
 *     the full domain to get each suffix as a substring instead.
 *   - Deterministic output: use a TreeMap, or sort the result, if order matters to the caller.
 *
 * RUN
 *   main() runs 3 cases (typical, empty, shared-parent) and prints actual vs expected.
 *   Results are sorted before printing so HashMap order cannot change the output.
 */
import java.util.*;

class SubdomainVisitCount {

    public static List<String> subdomainVisits(String[] cpdomains) {
        Map<String, Integer> visits = new HashMap<>();
        for (String cpdomain : cpdomains) {
            String[] parts = cpdomain.split(" ");
            int count = Integer.parseInt(parts[0]);
            String domain = parts[1];

            String[] labels = domain.split("\\.");   // regex: "." alone would match everything
            String suffix = "";
            // right to left: "com", then "leetcode.com", then "discuss.leetcode.com"
            for (int i = labels.length - 1; i >= 0; i--) {
                suffix = suffix.isEmpty() ? labels[i] : labels[i] + "." + suffix;
                visits.put(suffix, visits.getOrDefault(suffix, 0) + count);
            }
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : visits.entrySet()) {
            result.add(entry.getValue() + " " + entry.getKey());
        }
        return result;
    }

    /** Sorted copy so HashMap iteration order cannot change the printed output. */
    private static List<String> sorted(List<String> rows) {
        List<String> copy = new ArrayList<>(rows);
        Collections.sort(copy);
        return copy;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] typical = {"9001 discuss.leetcode.com", "50 yahoo.com", "1 intel.mail.com",
                "5 wiki.org"};
        String[] empty = {};
        String[] sharedParent = {"900 google.mail.com", "50 yahoo.com", "1 intel.mail.com",
                "5 wiki.org"};

        print("case 1 typical      ", sorted(subdomainVisits(typical)),
                "[1 intel.mail.com, 1 mail.com, 5 org, 5 wiki.org, 50 yahoo.com, "
                        + "9001 discuss.leetcode.com, 9001 leetcode.com, 9052 com]");
        print("case 2 empty        ", sorted(subdomainVisits(empty)), "[]");
        print("case 3 shared parent", sorted(subdomainVisits(sharedParent)),
                "[1 intel.mail.com, 5 org, 5 wiki.org, 50 yahoo.com, "
                        + "900 google.mail.com, 901 mail.com, 951 com]");
    }
}
