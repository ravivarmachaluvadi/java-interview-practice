/*
 * =====================================================================
 *  P095 Greedy Reachability (Jump Game)   Canonical LC 55 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 55, Jump Game)
 *   nums[i] is the MAXIMUM jump length from index i. Starting at index 0, can you reach the
 *   last index?
 *
 * EXAMPLE
 *   [2, 3, 1, 1, 4]  ->  true
 *   [3, 2, 1, 0, 4]  ->  false    every path lands on the 0 at index 3
 *   [0]              ->  true     already at the end
 *
 * RECOGNIZE WHEN
 *   - Each position lets you reach a RANGE ahead (jump up to k, a tap watering [i-r, i+r],
 *     a clip covering [s, e]); ask "can we reach / cover the end?" or "fewest steps".
 *   - A DP over positions would work, but only the farthest reach matters.
 *   Not this if: moves go both ways or land on exact cells -> BFS (P067_BfsShortestPath,
 *   LC 1306 below); jumps carry costs or scores -> DP (P042_MonotonicDeque for LC 1696).
 *
 * TEMPLATE
 *   can reach:   far = 0; for i in 0..n-1: if i > far: return false; far = max(far, i + a[i])
 *   min jumps:   jumps = 0, curEnd = 0, far = 0
 *                for i in 0..n-2: far = max(far, i + a[i])
 *                                 if i == curEnd: jumps++; curEnd = far   // must jump now
 *   cover [0, n] with fewest intervals: the same "BFS levels" loop over ranges
 *
 * APPROACH
 *   1. Walk left to right, tracking the farthest index reachable so far.
 *   2. If the walk ever reaches an index beyond that, you are stuck.
 *
 * KEY INSIGHT
 *   You never need to know HOW you reach an index, only how far any reachable index can
 *   send you. For the fewest jumps, the indices reachable in exactly j jumps form one
 *   contiguous window, so the algorithm is BFS by levels without a queue.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 45   Jump Game II             count levels: jump when i reaches curEnd
 *   [coded] LC 1306 Jump Game III            exact jumps i +- a[i], both directions -> BFS
 *   [coded] LC 1326 Min Taps to Water Garden turn each tap into a range, store the farthest
 *                                            reach per left end, then LC 45
 *           LC 1024 Video Stitching          LC 1326 with clips
 *           LC 1345 Jump Game IV             BFS; jump to any index with the same value
 *           LC 1871 Jump Game VII            reachable count in a sliding window
 *
 * PITFALLS
 *   - LC 45: loop to n - 2; jumping "from" the last index would add one too many.
 *   - LC 1326: the answer is -1 if a gap is never covered (far does not move past curEnd).
 *   - A 0 at index 0 with n > 1 is unreachable for everything after it.
 *
 * DEEP DIVE
 *   C08_CanJump, C09_JumpGameII, D02_MinimumNumberOfTapsToOpenToWaterAGarden (13-Greedy)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class Reachability {

    // Canonical LC 55.
    static boolean canJump(int[] nums) {
        int far = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > far) {
                return false;                      // index i can never be reached
            }
            far = Math.max(far, i + nums[i]);
        }
        return true;
    }

    // LC 45: the end is guaranteed reachable.
    static int jump(int[] nums) {
        int jumps = 0;
        int curEnd = 0;                            // last index reachable with `jumps` jumps
        int far = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            far = Math.max(far, i + nums[i]);
            if (i == curEnd) {
                jumps++;
                curEnd = far;
            }
        }
        return jumps;
    }

    // LC 1306: from i jump to i + a[i] or i - a[i]; can you reach any 0?
    static boolean canReach(int[] a, int start) {
        boolean[] seen = new boolean[a.length];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        seen[start] = true;
        while (!queue.isEmpty()) {
            int i = queue.poll();
            if (a[i] == 0) {
                return true;
            }
            for (int j : new int[]{i + a[i], i - a[i]}) {
                if (j >= 0 && j < a.length && !seen[j]) {
                    seen[j] = true;
                    queue.add(j);
                }
            }
        }
        return false;
    }

    // LC 1326: tap i waters [i - ranges[i], i + ranges[i]]; cover [0, n] with fewest taps.
    static int minTaps(int n, int[] ranges) {
        int[] reach = new int[n + 1];              // reach[l] = farthest right end starting at l
        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            reach[left] = Math.max(reach[left], Math.min(n, i + ranges[i]));
        }
        int taps = 0;
        int curEnd = 0;
        int far = 0;
        for (int i = 0; i < n; i++) {
            far = Math.max(far, reach[i]);
            if (i == curEnd) {
                if (far <= i) {
                    return -1;                     // nothing extends past i: a dry gap
                }
                taps++;
                curEnd = far;
            }
        }
        return taps;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 55 [2,3,1,1,4]", canJump(new int[]{2, 3, 1, 1, 4}), true);
        check("LC 55 [3,2,1,0,4]", canJump(new int[]{3, 2, 1, 0, 4}), false);
        check("LC 55 [0]", canJump(new int[]{0}), true);
        check("LC 55 [0,1] stuck at start", canJump(new int[]{0, 1}), false);

        check("LC 45 [2,3,1,1,4]", jump(new int[]{2, 3, 1, 1, 4}), 2);
        check("LC 45 [2,3,0,1,4]", jump(new int[]{2, 3, 0, 1, 4}), 2);
        check("LC 45 [0]", jump(new int[]{0}), 0);

        int[] a = {4, 2, 3, 0, 3, 1, 2};
        check("LC 1306 start 5", canReach(a, 5), true);
        check("LC 1306 start 0", canReach(a, 0), true);
        check("LC 1306 no path", canReach(new int[]{3, 0, 2, 1, 2}, 2), false);

        check("LC 1326 one tap", minTaps(5, new int[]{3, 4, 1, 1, 0, 0}), 1);
        check("LC 1326 dry", minTaps(3, new int[]{0, 0, 0, 0}), -1);
        check("LC 1326 n=7 three taps", minTaps(7, new int[]{1, 2, 1, 0, 2, 1, 0, 1}), 3);
    }
}
