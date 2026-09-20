/*
 * =====================================================================
 *  Kth Missing Positive Number                      LeetCode 1539 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   arr is strictly increasing and holds positive integers. Return the k-th positive
 *   integer that does NOT appear in arr. Constraints are small (n, k <= 1000) so a
 *   linear scan passes, but the binary search is what the interviewer wants.
 *
 * EXAMPLE
 *   arr = [2, 3, 4, 7, 11], k = 5  ->  9    missing: 1, 5, 6, 8, 9, ...
 *   arr = [1, 2, 3, 4],     k = 2  ->  6    every missing number lies past the end
 *   arr = [5],              k = 3  ->  3    missing: 1, 2, 3 all before the first element
 *
 * APPROACH  (search a derived quantity)
 *   1. If nothing were missing, arr[i] would equal i + 1. So the count of missing
 *      numbers before arr[i] is missing(i) = arr[i] - (i + 1), and it never decreases.
 *   2. Binary search for the first index whose missing(i) >= k. Call it left.
 *      Every index before it has fewer than k missing numbers.
 *   3. The answer sits in the gap just before arr[left]: exactly `left` array
 *      elements are smaller than it, so the k-th missing number is left + k.
 *
 * KEY INSIGHT
 *   You do not binary search the array values, you binary search arr[i] - (i + 1),
 *   a monotone quantity you derive from the index. When the "answer" is not an
 *   element of the input, look for a monotone function of the index instead.
 *
 * COMPLEXITY
 *   Time  O(log n)  binary search over indices  (linear version: O(n + k))
 *   Space O(1)      a few integers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why left + k? Answer = k missing numbers + `left` present numbers before it.
 *   - What if arr may contain duplicates or be unsorted? Sort/dedupe first, or use a set.
 *   - Streaming version: keep a running count of missing numbers as you read values.
 *
 * RUN
 *   main() runs 3 cases through both the linear scan and the binary search and
 *   prints actual vs expected.
 */

class KthMissingPositive {

    // Linear scan: walk the number line 1, 2, 3, ... and count what is not in arr.
    public int findKthPositive(int[] arr, int k) {
        int missingCount = 0;
        int candidate = 1;
        int index = 0;

        while (true) {
            if (index < arr.length && arr[index] == candidate) {
                index++;               // candidate is present, move past it in arr
            } else {
                missingCount++;        // candidate is missing
                if (missingCount == k) {
                    return candidate;
                }
            }
            candidate++;
        }
    }

    // Binary search on missing(i) = arr[i] - (i + 1), which is non-decreasing.
    public int findKthPositiveBS(int[] arr, int k) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int missingBeforeMid = arr[mid] - (mid + 1);
            if (missingBeforeMid < k) {
                left = mid + 1;        // not enough missing yet, answer is further right
            } else {
                right = mid - 1;       // k-th missing is at or before this gap
            }
        }
        // left = number of array elements smaller than the answer
        return left + k;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        KthMissingPositive s = new KthMissingPositive();

        int[] a1 = {2, 3, 4, 7, 11};
        print("case 1 linear  [2,3,4,7,11] k=5", s.findKthPositive(a1, 5), 9);
        print("case 1 binary  [2,3,4,7,11] k=5", s.findKthPositiveBS(a1, 5), 9);

        int[] a2 = {1, 2, 3, 4};
        print("case 2 linear  [1,2,3,4] k=2 (past end)", s.findKthPositive(a2, 2), 6);
        print("case 2 binary  [1,2,3,4] k=2 (past end)", s.findKthPositiveBS(a2, 2), 6);

        int[] a3 = {5};
        print("case 3 linear  [5] k=3 (all before first)", s.findKthPositive(a3, 3), 3);
        print("case 3 binary  [5] k=3 (all before first)", s.findKthPositiveBS(a3, 3), 3);
    }
}
