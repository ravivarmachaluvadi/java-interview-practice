/*
 * =====================================================================
 *  Disjoint Set Union (Union-Find)        building block | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Maintain a collection of disjoint sets over elements 0..n and support two
 *   operations as fast as possible: union(u, v) merges the sets containing u
 *   and v, and findUPar(x) returns the representative ("ultimate parent") of
 *   x's set. Two elements are connected exactly when their representatives
 *   are equal. There is no "un-union" - DSU only ever merges.
 *
 * EXAMPLE
 *   n = 7, then union 1-2, 2-3, 4-5, 6-7, 5-6
 *     sets are {1,2,3} and {4,5,6,7}  ->  find(3) == find(7) ? false
 *   union 3-7
 *     one set {1..7}                  ->  find(3) == find(7) ? true, size 7
 *
 * DESIGN  (two arrays and two optimisations)
 *   parent[i] - who i points at; a root points at itself and represents the set.
 *   size[i]   - number of elements in the tree rooted at i (valid at roots only).
 *   rank[i]   - an upper bound on that tree's height (the alternative to size).
 *
 *   findUPar(x) walks up to the root, then PATH COMPRESSION rewires every node
 *   on that walk to point straight at the root, so the next find on any of them
 *   is one hop. unionBySize(u, v) attaches the SMALLER tree under the larger root, so no
 *   element's depth grows unnecessarily. unionByRank does the same using height
 *   instead of element count, bumping the rank only when both ranks are equal.
 *
 * KEY DECISIONS
 *   - Arrays are sized n+1 so both 0-based and 1-based problems work unchanged.
 *   - Always union the ROOTS, never the raw arguments - unionBySize(u, v) calls
 *     findUPar first. Forgetting this is the classic DSU bug.
 *   - Size vs rank: same complexity class; size wins in interviews because
 *     size[root] is itself an answer (largest component, component count).
 *   - unionBySize returns early when both roots already match, which is exactly
 *     the "this edge closes a cycle" signal Kruskal's algorithm needs.
 *
 * COMPLEXITY
 *   Time  O(alpha(n)) amortised per find/union with BOTH optimisations, where
 *         alpha is the inverse Ackermann function - under 5 for any real n, so
 *         effectively constant. Path compression alone is O(log n); union by
 *         size alone is O(log n); together they give near O(1).
 *   Space O(n) for parent, size and rank.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Kruskal's MST: sort edges, union each one whose roots differ
 *     (A10_KruskalAlgorithm).
 *   - Number of connected components / redundant connection - count roots.
 *   - Accounts Merge (C09_AccountsMerge): map strings to indices first, then
 *     this is unchanged.
 *   - Why can DSU not undo a union, and what do you use when you must (rollback
 *     DSU without path compression, or an offline / small-to-large approach)?
 *
 * RUN
 *   main() runs 3 cases: the merge sequence above, a self-union no-op edge case,
 *   and the same sequence through unionByRank. Each prints actual vs expected.
 */
import java.util.*;

class DisjointSet {
    List<Integer> parent = new ArrayList<>();
    List<Integer> size = new ArrayList<>();
    List<Integer> rank = new ArrayList<>();

    /** Elements 0..n, each starting alone in its own set. */
    public DisjointSet(int n) {
        for (int i = 0; i <= n; i++) {
            parent.add(i);   // every element is its own root
            size.add(1);
            rank.add(0);
        }
    }

    /** Root of node's set, flattening the path on the way back out. */
    public int findUPar(int node) {
        if (node == parent.get(node)) return node;   // a root points at itself

        int ulp = findUPar(parent.get(node));
        parent.set(node, ulp);   // path compression: point straight at the root
        return ulp;
    }

    /** Attach the smaller tree under the larger root. */
    public void unionBySize(int u, int v) {
        int ulpU = findUPar(u);          // union the ROOTS, not the arguments
        int ulpV = findUPar(v);
        // already together - in Kruskal's, this edge would close a cycle
        if (ulpU == ulpV) return;

        if (size.get(ulpU) < size.get(ulpV)) {
            parent.set(ulpU, ulpV);
            size.set(ulpV, size.get(ulpV) + size.get(ulpU));
        } else {
            parent.set(ulpV, ulpU);
            size.set(ulpU, size.get(ulpU) + size.get(ulpV));
        }
    }

    /** Same merge, keyed on tree height instead of element count. */
    public void unionByRank(int u, int v) {
        int ulpU = findUPar(u);
        int ulpV = findUPar(v);
        if (ulpU == ulpV) return;

        if (rank.get(ulpU) < rank.get(ulpV)) {
            parent.set(ulpU, ulpV);
        } else if (rank.get(ulpV) < rank.get(ulpU)) {
            parent.set(ulpV, ulpU);
        } else {
            // equal heights: either may win, but the winner grows one level taller
            parent.set(ulpV, ulpU);
            rank.set(ulpU, rank.get(ulpU) + 1);
        }
    }

    /** Number of elements in the set containing node (needs unionBySize to be meaningful). */
    public int componentSize(int node) {
        return size.get(findUPar(node));
    }

    public boolean connected(int u, int v) {
        return findUPar(u) == findUPar(v);
    }
}

class DisjointSetsMain {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: typical - build {1,2,3} and {4,5,6,7}, then join them
        DisjointSet ds = new DisjointSet(7);
        ds.unionBySize(1, 2);
        ds.unionBySize(2, 3);
        ds.unionBySize(4, 5);
        ds.unionBySize(6, 7);
        ds.unionBySize(5, 6);

        print("case 1a connected(3, 7) before the merge", ds.connected(3, 7), false);
        print("case 1b componentSize(3)                ", ds.componentSize(3), 3);
        print("case 1c componentSize(7)                ", ds.componentSize(7), 4);

        ds.unionBySize(3, 7);
        print("case 1d connected(3, 7) after the merge ", ds.connected(3, 7), true);
        print("case 1e componentSize(1) now everything ", ds.componentSize(1), 7);
        print("case 1f element 0 was never touched     ", ds.componentSize(0), 1);

        // case 2: edge case - merging an element with itself changes nothing
        DisjointSet solo = new DisjointSet(3);
        solo.unionBySize(2, 2);
        print("case 2a connected(2, 2) is always       ", solo.connected(2, 2), true);
        print("case 2b componentSize(2) after self-union", solo.componentSize(2), 1);
        print("case 2c connected(1, 2) still separate  ", solo.connected(1, 2), false);

        // case 3: the same merges through unionByRank - same connectivity answers
        DisjointSet byRank = new DisjointSet(7);
        byRank.unionByRank(1, 2);
        byRank.unionByRank(2, 3);
        byRank.unionByRank(4, 5);
        byRank.unionByRank(6, 7);
        byRank.unionByRank(5, 6);
        print("case 3a rank: connected(3, 7) before    ", byRank.connected(3, 7), false);
        byRank.unionByRank(3, 7);
        print("case 3b rank: connected(3, 7) after     ", byRank.connected(3, 7), true);
        print("case 3c rank: connected(1, 4)           ", byRank.connected(1, 4), true);
    }
}
