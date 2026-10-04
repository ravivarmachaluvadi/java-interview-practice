/*
 * =====================================================================
 *  P096 Greedy: Sort, Then Pair or Order   Canonical LC 455 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 455, Assign Cookies)
 *   Child i is content with a cookie of size >= g[i]; each child gets at most one cookie.
 *   Return the most children you can make content.
 *
 * EXAMPLE
 *   g [1,2,3], s [1,1]    ->  1
 *   g [1,2],   s [1,2,3]  ->  2
 *
 * RECOGNIZE WHEN
 *   - Match items from two groups (children and cookies, people and boats, workers and
 *     jobs) or order items so that a local comparison decides the best arrangement.
 *   - Sorting makes the right choice obvious: "smallest that still fits", "heaviest with
 *     the lightest", "by the difference of two costs", "which concatenation is bigger".
 *   Not this if: picks interact through a capacity in complex ways -> DP
 *   (P084_ZeroOneKnapsack); intervals -> P049_SortByEndGreedy.
 *
 * TEMPLATE
 *   sort one or both lists by the key that makes a local decision safe
 *   two pointers walk them: match when compatible, otherwise skip the item that can never
 *   be matched
 *   for orderings: sort with a comparator that compares two items directly (exchange)
 *
 * APPROACH
 *   1. Sort greed factors and cookie sizes.
 *   2. Give the smallest cookie that satisfies the least greedy child still waiting; a
 *      cookie too small for that child is too small for everyone after, so skip it.
 *
 * KEY INSIGHT
 *   Exchange argument: if an optimal answer uses a bigger cookie where a smaller one would
 *   do, swapping them never hurts. After sorting, "smallest that fits" is the safe local
 *   choice, and two pointers apply it in one pass.
 *
 * COMPLEXITY
 *   Time O(n log n) for sorting, then O(n). Space O(1) besides the sort.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 881  Boats to Save People     sort; heaviest boards with the lightest if they
 *                                            fit, else alone (two pointers from both ends)
 *   [coded] LC 1029 Two City Scheduling      sort by costA - costB; first half to A
 *   [coded] LC 870  Advantage Shuffle        sort both; beat each b with the smallest a
 *                                            that wins, else sacrifice the smallest a
 *   [coded] LC 179  Largest Number           comparator: x + y vs y + x as strings
 *           LC 2410 Max Matching Players     LC 455 with trainers
 *           LC 1710 Max Units on a Truck     sort boxes by units, fill greedily
 *           LC 948  Bag of Tokens            sort; play face up with the smallest, face down
 *                                            with the largest
 *
 * PITFALLS
 *   - LC 179: an all-zero input must print "0", not "000".
 *   - LC 870: remember original indices of b, or the output is in the wrong order.
 *   - Prove (or at least test) the greedy; plausible greedies are often wrong.
 *
 * DEEP DIVE
 *   B02_AssignCookies, C02_BoatsToSavePeople, C06_BiggestNumber, B03_ClassPhotos (13-Greedy)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class SortAndPair {

    // Canonical LC 455.
    static int findContentChildren(int[] g, int[] s) {
        int[] greed = g.clone();
        int[] size = s.clone();
        Arrays.sort(greed);
        Arrays.sort(size);
        int child = 0;
        for (int cookie = 0; cookie < size.length && child < greed.length; cookie++) {
            if (size[cookie] >= greed[child]) {
                child++;                           // smallest cookie that fits this child
            }
        }
        return child;
    }

    // LC 881: each boat holds at most 2 people and `limit` weight.
    static int numRescueBoats(int[] people, int limit) {
        int[] p = people.clone();
        Arrays.sort(p);
        int light = 0;
        int heavy = p.length - 1;
        int boats = 0;
        while (light <= heavy) {
            if (p[light] + p[heavy] <= limit) {
                light++;                           // lightest rides along
            }
            heavy--;                               // heaviest always leaves
            boats++;
        }
        return boats;
    }

    // LC 1029: 2n people, n to each city, minimise the total cost.
    static int twoCitySchedCost(int[][] costs) {
        int[][] c = costs.clone();
        Arrays.sort(c, (x, y) -> Integer.compare(x[0] - x[1], y[0] - y[1]));
        int n = c.length / 2;
        int total = 0;
        for (int i = 0; i < c.length; i++) {
            total += i < n ? c[i][0] : c[i][1];    // most "A-favoured" people go to A
        }
        return total;
    }

    // LC 870: permute a to maximise how many a[i] > b[i].
    static int[] advantageCount(int[] a, int[] b) {
        int n = a.length;
        int[] sortedA = a.clone();
        Arrays.sort(sortedA);
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (x, y) -> Integer.compare(b[x], b[y]));
        int[] out = new int[n];
        int lo = 0;
        int hi = n - 1;
        for (int k = n - 1; k >= 0; k--) {         // the strongest b first
            int idx = order[k];
            if (sortedA[hi] > b[idx]) {
                out[idx] = sortedA[hi--];          // our strongest beats it
            } else {
                out[idx] = sortedA[lo++];          // cannot win: sacrifice the weakest
            }
        }
        return out;
    }

    // LC 179.
    static String largestNumber(int[] nums) {
        String[] s = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            s[i] = String.valueOf(nums[i]);
        }
        Arrays.sort(s, (x, y) -> (y + x).compareTo(x + y));
        if (s[0].equals("0")) {
            return "0";
        }
        return String.join("", s);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 455 g=[1,2,3] s=[1,1]",
                findContentChildren(new int[]{1, 2, 3}, new int[]{1, 1}), 1);
        check("LC 455 g=[1,2] s=[1,2,3]",
                findContentChildren(new int[]{1, 2}, new int[]{1, 2, 3}), 2);
        check("LC 455 no cookies", findContentChildren(new int[]{1}, new int[]{}), 0);

        check("LC 881 limit 3", numRescueBoats(new int[]{3, 2, 2, 1}, 3), 3);
        check("LC 881 limit 5", numRescueBoats(new int[]{3, 5, 3, 4}, 5), 4);
        check("LC 881 pairs fit", numRescueBoats(new int[]{1, 2}, 3), 1);

        check("LC 1029 four people",
                twoCitySchedCost(new int[][]{{10, 20}, {30, 200}, {400, 50}, {30, 20}}), 110);
        check("LC 1029 six people", twoCitySchedCost(new int[][]{{259, 770}, {448, 54}, {926, 667},
                {184, 139}, {840, 118}, {577, 469}}), 1859);

        check("LC 870 [2,7,11,15] vs [1,10,4,11]",
                Arrays.toString(advantageCount(new int[]{2, 7, 11, 15}, new int[]{1, 10, 4, 11})),
                "[2, 11, 7, 15]");
        int[] ours = {12, 24, 8, 32};
        int[] theirs = {13, 25, 32, 11};
        check("LC 870 [12,24,8,32] vs [13,25,32,11]",
                Arrays.toString(advantageCount(ours, theirs)), "[24, 32, 8, 12]");

        check("LC 179 [10,2]", largestNumber(new int[]{10, 2}), "210");
        check("LC 179 [3,30,34,5,9]", largestNumber(new int[]{3, 30, 34, 5, 9}), "9534330");
        check("LC 179 all zeros", largestNumber(new int[]{0, 0}), "0");
    }
}
