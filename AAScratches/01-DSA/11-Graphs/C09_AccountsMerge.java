/*
 * =====================================================================
 *  Accounts Merge                           LeetCode 721 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Each account is a list whose first element is a person's name and whose
 *   remaining elements are emails. Two accounts belong to the same person if
 *   they share at least one email (names alone prove nothing - many people are
 *   called John). Merge them, and return each person as [name, sorted emails].
 *
 * EXAMPLE
 *   [["John","johnsmith@m","john_newyork@m"],
 *    ["John","johnsmith@m","john00@m"],
 *    ["Mary","mary@m"], ["John","johnnybravo@m"]]
 *   -> [[John, john00@m, john_newyork@m, johnsmith@m], [John, johnnybravo@m],
 *       [Mary, mary@m]]                    the first two share johnsmith@m
 *
 * APPROACH  (DSU over account indices, with a mail -> index map)
 *   1. Give every ACCOUNT an index 0..n-1 and build a DSU over those indices.
 *      Accounts are the nodes; a shared email is the edge.
 *   2. Scan every email of every account, keeping map mailToAccount.
 *      First sighting of a mail: remember which account claimed it.
 *      Later sighting: union this account with the account that claimed it.
 *   3. After the scan, each DSU component is one real person. Re-walk the map
 *      and drop every mail into a bucket keyed by findUPar(claiming account).
 *   4. For each non-empty bucket, sort its mails and prefix the name taken from
 *      that root account. Sort the answer by name to make the output stable.
 *
 * KEY INSIGHT
 *   DSU needs integer nodes, and the entities here are strings. The whole trick
 *   is the indirection: union ACCOUNT INDICES, not emails, and let a hash map
 *   carry each email back to the first index that owned it. That mapping step -
 *   turn arbitrary entities into dense integers, then union - is what makes
 *   union-find usable on real data, and it reappears in stones, islands and
 *   every "merge things that touch" question.
 *
 * COMPLEXITY
 *   Time  O(M log M + M * alpha(n))   M = total emails; the log M is the sorts.
 *   Space O(M + n)                    the mail map, the buckets, the DSU arrays.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it with plain DFS over a mail-to-mail graph; compare the memory use.
 *   - Accounts stream in forever: which structure survives (DSU does, DFS does not)?
 *   - Merge on phone numbers OR emails - does anything about the DSU change?
 *   - How do you keep the output deterministic when two people share a name?
 *
 * RUN
 *   main() runs 3 cases: the classic merge, a single lonely account, and a
 *   three-account chain where A-B and B-C force all three together.
 */

import java.util.*;

class DisjointSet {
    /* Ranks, parents and sizes for every node in the structure */
    int[] rank, parent, size;

    DisjointSet(int n) {
        rank = new int[n + 1];
        parent = new int[n + 1];
        size = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /** Ultimate parent, flattening the chain on the way back up. */
    int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    // Union by rank: hang the shallower tree under the deeper one
    void unionByRank(int u, int v) {
        int ulp_u = findUPar(u);
        int ulp_v = findUPar(v);
        if (ulp_u == ulp_v) return;
        if (rank[ulp_u] < rank[ulp_v]) {
            parent[ulp_u] = ulp_v;
        } else if (rank[ulp_v] < rank[ulp_u]) {
            parent[ulp_v] = ulp_u;
        } else {
            parent[ulp_v] = ulp_u;
            rank[ulp_u]++;
        }
    }

    // Union by size: hang the smaller set under the larger one
    void unionBySize(int u, int v) {
        int ulp_u = findUPar(u);
        int ulp_v = findUPar(v);
        if (ulp_u == ulp_v) return;
        if (size[ulp_u] < size[ulp_v]) {
            parent[ulp_u] = ulp_v;
            size[ulp_v] += size[ulp_u];
        } else {
            parent[ulp_v] = ulp_u;
            size[ulp_u] += size[ulp_v];
        }
    }
}

class AccountsMerge {

    static List<List<String>> accountsMerge(List<List<String>> accounts) {

        int n = accounts.size();
        DisjointSet ds = new DisjointSet(n);

        // mail -> index of the first account that claimed it
        Map<String, Integer> mailToAccount = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // index 0 is the name, so the mails start at 1
            for (int j = 1; j < accounts.get(i).size(); j++) {
                String mail = accounts.get(i).get(j);
                if (!mailToAccount.containsKey(mail)) {
                    mailToAccount.put(mail, i);
                } else {
                    // seen before: this account is the same person as that one
                    ds.unionBySize(i, mailToAccount.get(mail));
                }
            }
        }

        /* Bucket every mail under the ROOT of the account that claimed it,
           so one bucket ends up holding one real person's whole mailbox. */
        List<List<String>> mergedMail = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            mergedMail.add(new ArrayList<>());
        }
        for (Map.Entry<String, Integer> entry : mailToAccount.entrySet()) {
            int root = ds.findUPar(entry.getValue());
            mergedMail.get(root).add(entry.getKey());
        }

        List<List<String>> ans = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // non-root accounts were absorbed, so their bucket is empty
            if (mergedMail.get(i).isEmpty()) continue;

            Collections.sort(mergedMail.get(i));
            List<String> person = new ArrayList<>();
            person.add(accounts.get(i).get(0));   // every merged account shares this name
            person.addAll(mergedMail.get(i));
            ans.add(person);
        }

        // not required by the problem, but keeps the printed output stable
        ans.sort(Comparator.comparing(list -> list.get(0)));
        return ans;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {

        List<List<String>> classic = Arrays.asList(
                Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"),
                Arrays.asList("Mary", "mary@mail.com"),
                Arrays.asList("John", "johnnybravo@mail.com")
        );
        print("case 1 classic",
                accountsMerge(classic),
                "[[John, john00@mail.com, john_newyork@mail.com, johnsmith@mail.com],"
                        + " [John, johnnybravo@mail.com], [Mary, mary@mail.com]]");

        // edge: one account, one mail, nothing to merge
        List<List<String>> lonely = Arrays.asList(
                Arrays.asList("Alice", "alice@mail.com")
        );
        print("case 2 single ",
                accountsMerge(lonely),
                "[[Alice, alice@mail.com]]");

        /* tricky: account 0 and 1 share y@m, account 1 and 2 share z@m, so all
           three are one person even though 0 and 2 share nothing directly. */
        List<List<String>> chain = Arrays.asList(
                Arrays.asList("A", "x@m", "y@m"),
                Arrays.asList("A", "y@m", "z@m"),
                Arrays.asList("A", "z@m", "w@m")
        );
        print("case 3 chain  ",
                accountsMerge(chain),
                "[[A, w@m, x@m, y@m, z@m]]");
    }
}
