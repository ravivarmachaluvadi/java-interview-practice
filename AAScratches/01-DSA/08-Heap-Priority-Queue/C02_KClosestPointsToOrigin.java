/*
 * =====================================================================
 *  K Closest Points to Origin                     LeetCode 973 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given points[i] = [xi, yi] on the plane and an integer k, return the k points closest
 *   to the origin (0, 0) by Euclidean distance. The answer is unique and may be returned
 *   in any order. 1 <= k <= points.length <= 10^4, coordinates in [-10^4, 10^4].
 *
 * EXAMPLE
 *   points = [[1,3], [-2,2]], k = 1            ->  [[-2,2]]        dist^2 10 vs 8
 *   points = [[3,3], [5,-1], [-2,4]], k = 2    ->  [[3,3], [-2,4]] dist^2 18, 26, 20
 *   points = [[1,3], [-2,2], [5,8], [0,1]], k = 4  ->  all four   k == n, nothing evicted
 *
 * APPROACH  (size-k max-heap on a computed key)
 *   1. Compare points by squared distance x*x + y*y; the square root is monotonic so it is
 *      never needed and we stay in exact integer arithmetic.
 *   2. Keep a MAX-heap of at most k points: its root is the farthest of the k closest so far.
 *   3. Offer every point; when the size exceeds k, poll the root (the farthest one out).
 *   4. Whatever remains is the answer; drain the heap into a k x 2 array.
 *
 * KEY INSIGHT
 *   "K closest" is "k smallest by distance", so the heap direction flips: a max-heap evicts
 *   the worst survivor. The comparator is written over a derived value (distance), not the
 *   element itself -- that is the pattern to spot: decide the key, then decide which end of
 *   the heap must hold the element you are willing to throw away.
 *
 * COMPLEXITY
 *   Time  O(n log k)   each of n points costs one heap op on a heap of size <= k
 *   Space O(k)         the heap (plus the k x 2 result)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(n) average: quickselect on squared distance (partition until the pivot lands at k).
 *   - Points arrive as a stream: the heap version already works incrementally.
 *   - k close to n: sorting all points, O(n log n), is simpler and about as fast.
 *   - Why squared distance? Avoids sqrt and floating point; coordinates <= 10^4 keep the
 *     squares within int (2 * 10^8).
 *
 * RUN
 *   main() runs 3 cases (typical k=1, typical k=2, k == n) and prints actual vs expected.
 *   Output rows are sorted before printing because the problem allows any order.
 */
import java.util.Arrays;
import java.util.PriorityQueue;

// https://leetcode.com/problems/k-closest-points-to-origin/
class KClosestPointsToOrigin {

    private static int distSq(int[] p) {
        return p[0] * p[0] + p[1] * p[1];
    }

    public static int[][] kClosest(int[][] points, int k) {
        // max-heap by squared distance: root = the farthest of the k closest kept so far
        PriorityQueue<int[]> maxHeap =
                new PriorityQueue<>((a, b) -> Integer.compare(distSq(b), distSq(a)));

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) {
                maxHeap.poll();                 // evict the farthest survivor
            }
        }

        int[][] result = new int[k][];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }

    // Any order is accepted, so sort rows (by distance, then x, then y) before comparing.
    private static int[][] sortedRows(int[][] rows) {
        int[][] copy = rows.clone();
        Arrays.sort(copy, (a, b) -> {
            if (distSq(a) != distSq(b)) return Integer.compare(distSq(a), distSq(b));
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });
        return copy;
    }

    private static void print(String label, int[][] actual, int[][] expected) {
        System.out.println(label + ": " + Arrays.deepToString(sortedRows(actual))
                + "   expected " + Arrays.deepToString(sortedRows(expected)));
    }

    public static void main(String[] args) {
        // typical: LeetCode example 1
        print("case 1 (k=1)",
                kClosest(new int[][]{{1, 3}, {-2, 2}}, 1),
                new int[][]{{-2, 2}});

        // typical: LeetCode example 2, negatives in both axes
        print("case 2 (k=2)",
                kClosest(new int[][]{{3, 3}, {5, -1}, {-2, 4}}, 2),
                new int[][]{{3, 3}, {-2, 4}});

        // edge: k == n, nothing is ever evicted
        print("case 3 (k=n)",
                kClosest(new int[][]{{1, 3}, {-2, 2}, {5, 8}, {0, 1}}, 4),
                new int[][]{{0, 1}, {-2, 2}, {1, 3}, {5, 8}});
    }
}
