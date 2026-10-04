/*
 * =====================================================================
 *  P110 Segment Tree (with Lazy Propagation)   Canonical Range Min Query | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: Range Minimum Query with point updates; LeetCode 307 is the sum version)
 *   Support update(i, value) and min(l, r) over an array, both in O(log n).
 *
 * EXAMPLE
 *   [5, 2, 6, 3, 8, 1, 7]: min(0, 6) = 1, min(1, 3) = 2; update(5, 10); min(0, 6) = 2
 *
 * RECOGNIZE WHEN
 *   - Range queries for an operation that is NOT invertible (min, max, gcd) with updates,
 *     so prefix-sum tricks and Fenwick trees do not apply.
 *   - RANGE updates ("add 5 to every index in [l, r]") mixed with range queries: lazy
 *     propagation.
 *   - Segment tree over VALUES (coordinates) for "best answer among values < x".
 *   Not this if: only sums with point updates -> P109_FenwickAndMergeCounting (shorter);
 *   no updates -> a sparse table or prefix sums; updates first, queries after ->
 *   P003_DifferenceArray.
 *
 * TEMPLATE
 *   tree over [lo, hi], node stores combine(left child, right child); array size 4n
 *   query(node, lo, hi, l, r):
 *       if [lo, hi] outside [l, r]: return identity
 *       if [lo, hi] inside  [l, r]: return tree[node]
 *       push(node) (lazy); split at mid; combine both halves
 *   point update: descend to the leaf, set it, recombine on the way up
 *   lazy range add: tag a fully covered node (tree += add * length, lazy += add) and stop;
 *                   push the tag to the children only when you descend later
 *
 * APPROACH
 *   1. Build bottom-up: each internal node is the min of its two children.
 *   2. A query splits [l, r] into O(log n) fully covered nodes.
 *   3. An update changes one leaf and the O(log n) ancestors above it.
 *
 * KEY INSIGHT
 *   Any interval is a union of O(log n) canonical segments (the tree's nodes), so any
 *   ASSOCIATIVE operation can be answered from precomputed segments. Lazy tags postpone
 *   range updates until a query actually needs to look inside a segment.
 *
 * COMPLEXITY
 *   Build O(n); query and update O(log n); space O(4n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Range add + range sum (lazy)     tags carry the pending add per node
 *           LC 307  Range Sum Mutable        combine = +, identity 0
 *           LC 699  Falling Squares          range max with range ASSIGN (coordinate compress)
 *           LC 2407 Longest Increasing Subsequence II  tree over values: best dp in a range
 *           LC 715  Range Module             interval set; a dynamic segment tree or TreeMap
 *           LC 732  My Calendar III          range add + global max (or a TreeMap sweep)
 *
 * PITFALLS
 *   - Size the array 4n, not 2n, for the recursive layout.
 *   - Return the IDENTITY for out-of-range parts (+infinity for min, 0 for sum).
 *   - Lazy: push tags down before visiting children, in both update and query.
 *
 * DEEP DIVE
 *   D03_SegmentTree (18-Sorting-Searching-Algorithms)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class SegmentTree {

    // Canonical: range minimum with point updates.
    static class MinTree {
        private final int n;
        private final int[] tree;

        MinTree(int[] a) {
            n = a.length;
            tree = new int[4 * n];
            build(a, 1, 0, n - 1);
        }

        private void build(int[] a, int node, int lo, int hi) {
            if (lo == hi) {
                tree[node] = a[lo];
                return;
            }
            int mid = (lo + hi) >>> 1;
            build(a, 2 * node, lo, mid);
            build(a, 2 * node + 1, mid + 1, hi);
            tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
        }

        void update(int i, int value) {
            update(1, 0, n - 1, i, value);
        }

        private void update(int node, int lo, int hi, int i, int value) {
            if (lo == hi) {
                tree[node] = value;
                return;
            }
            int mid = (lo + hi) >>> 1;
            if (i <= mid) {
                update(2 * node, lo, mid, i, value);
            } else {
                update(2 * node + 1, mid + 1, hi, i, value);
            }
            tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
        }

        int min(int l, int r) {
            return min(1, 0, n - 1, l, r);
        }

        private int min(int node, int lo, int hi, int l, int r) {
            if (r < lo || hi < l) {
                return Integer.MAX_VALUE;          // identity for min
            }
            if (l <= lo && hi <= r) {
                return tree[node];
            }
            int mid = (lo + hi) >>> 1;
            return Math.min(min(2 * node, lo, mid, l, r), min(2 * node + 1, mid + 1, hi, l, r));
        }
    }

    // Range add + range sum with lazy propagation.
    static class LazySumTree {
        private final int n;
        private final long[] sum;
        private final long[] lazy;

        LazySumTree(int[] a) {
            n = a.length;
            sum = new long[4 * n];
            lazy = new long[4 * n];
            build(a, 1, 0, n - 1);
        }

        private void build(int[] a, int node, int lo, int hi) {
            if (lo == hi) {
                sum[node] = a[lo];
                return;
            }
            int mid = (lo + hi) >>> 1;
            build(a, 2 * node, lo, mid);
            build(a, 2 * node + 1, mid + 1, hi);
            sum[node] = sum[2 * node] + sum[2 * node + 1];
        }

        // Hand a pending add down to both children.
        private void push(int node, int lo, int hi) {
            if (lazy[node] != 0) {
                int mid = (lo + hi) >>> 1;
                apply(2 * node, lo, mid, lazy[node]);
                apply(2 * node + 1, mid + 1, hi, lazy[node]);
                lazy[node] = 0;
            }
        }

        private void apply(int node, int lo, int hi, long add) {
            sum[node] += add * (hi - lo + 1);
            lazy[node] += add;
        }

        void add(int l, int r, long value) {
            add(1, 0, n - 1, l, r, value);
        }

        private void add(int node, int lo, int hi, int l, int r, long value) {
            if (r < lo || hi < l) {
                return;
            }
            if (l <= lo && hi <= r) {
                apply(node, lo, hi, value);        // fully covered: tag and stop
                return;
            }
            push(node, lo, hi);
            int mid = (lo + hi) >>> 1;
            add(2 * node, lo, mid, l, r, value);
            add(2 * node + 1, mid + 1, hi, l, r, value);
            sum[node] = sum[2 * node] + sum[2 * node + 1];
        }

        long sum(int l, int r) {
            return sum(1, 0, n - 1, l, r);
        }

        private long sum(int node, int lo, int hi, int l, int r) {
            if (r < lo || hi < l) {
                return 0;
            }
            if (l <= lo && hi <= r) {
                return sum[node];
            }
            push(node, lo, hi);
            int mid = (lo + hi) >>> 1;
            return sum(2 * node, lo, mid, l, r) + sum(2 * node + 1, mid + 1, hi, l, r);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MinTree mt = new MinTree(new int[]{5, 2, 6, 3, 8, 1, 7});
        check("RMQ whole array", mt.min(0, 6), 1);
        check("RMQ [1,3]", mt.min(1, 3), 2);
        mt.update(5, 10);
        check("RMQ after update", mt.min(0, 6), 2);
        check("RMQ [4,6]", mt.min(4, 6), 7);
        check("RMQ single cell", mt.min(2, 2), 6);

        LazySumTree lt = new LazySumTree(new int[]{1, 2, 3, 4, 5});
        check("lazy sum before", lt.sum(0, 4), 15L);
        lt.add(1, 3, 10);
        check("lazy sum after add 10 on [1,3]", lt.sum(0, 4), 45L);
        check("lazy single cell", lt.sum(1, 1), 12L);
        check("lazy partial overlap", lt.sum(3, 4), 19L);
        lt.add(0, 0, -1);
        check("lazy second add", lt.sum(0, 2), 25L);
    }
}
