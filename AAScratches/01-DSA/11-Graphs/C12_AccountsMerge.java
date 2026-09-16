import java.util.*;

class DisjointSet {
    /* To store the ranks, parents and
    sizes of different set of vertices */
    int[] rank, parent, size;

    // Constructor
    DisjointSet(int n) {
        rank = new int[n + 1];
        parent = new int[n + 1];
        size = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    // Function to find ultimate parent
    int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    // Function to implement union by rank
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

    // Function to implement union by size
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


// Solution class
class AccountsMerge {

    // Function to merge the accounts
    static List<List<String>> accountsMerge(List<List<String>> accounts) {

        int n = accounts.size();
        DisjointSet ds = new DisjointSet(n);
        // Hashmap to store the pair: {mails, node}
        Map<String, Integer> mapMailNode = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // Iterate on all the mails of the person
            for (int j = 1; j < accounts.get(i).size(); j++) {
                String mail = accounts.get(i).get(j);
                if (!mapMailNode.containsKey(mail))
                    mapMailNode.put(mail, i);
                else {
                    ds.unionBySize(i, mapMailNode.get(mail));
                }
            }
        }

        // To store the merged mails
        List<List<String>> mergedMail = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            mergedMail.add(new ArrayList<>());

        for (Map.Entry<String, Integer> entry : mapMailNode.entrySet()) {
            String mail = entry.getKey(); // Mail
            int node = ds.findUPar(entry.getValue()); // Node
            // Add the merged mail to the list
            mergedMail.get(node).add(mail);
        }

        // To return the result
        List<List<String>> ans = new ArrayList<>();

        // Iterate on all list of merged mails
        for (int i = 0; i < n; i++) {
            /* If a person has no mails,
            skip the iteration */
            if (mergedMail.get(i).isEmpty())
                continue;
            // Otherwise, add all the merged mails of person
            Collections.sort(mergedMail.get(i));
            List<String> temp = new ArrayList<>();
            temp.add(accounts.get(i).get(0));
            temp.addAll(mergedMail.get(i));
            ans.add(temp);
        }
        ans.sort(Comparator.comparing(list -> list.get(0)));
        return ans;
    }

    public static void main(String[] args) {
        int n = 4;
        List<List<String>> accounts = Arrays.asList(
                Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"),
                Arrays.asList("Mary", "mary@mail.com"),
                Arrays.asList("John", "johnnybravo@mail.com")
        );

        // Function call to merge the accounts
        List<List<String>> ans = accountsMerge(accounts);

        // Output
        System.out.println("The merged accounts are:");
        for (List<String> account : ans) {
            System.out.println(String.join(" ", account));
        }
    }
}
