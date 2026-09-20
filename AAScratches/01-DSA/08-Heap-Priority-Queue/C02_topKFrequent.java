/*
 * =====================================================================
 *  Top K Frequent Elements                        LeetCode 347 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums and an integer k, return the k most frequent elements.
 *   The answer is guaranteed unique and may be returned in any order.
 *   1 <= nums.length <= 10^5, k is between 1 and the number of distinct values.
 *   Follow-up on the problem itself: must be better than O(n log n).
 *
 * EXAMPLE
 *   nums = [1, 1, 1, 2, 2, 3], k = 2   ->  [1, 2]     counts 1:3, 2:2, 3:1
 *   nums = [1], k = 1                  ->  [1]        single element
 *   nums = [4, 1, -1, 2, -1, 2, 3], k = 2  ->  [-1, 2] (any order)  both appear twice
 *
 * APPROACH  (frequency map + size-k min-heap)
 *   1. Count each value in a HashMap<value, count>.
 *   2. Walk the map entries and offer each into a min-heap ordered by count.
 *      Whenever the heap grows past k, poll: the least frequent entry so far falls out.
 *   3. What remains are the k most frequent. Poll them into the result array from the
 *      back so the array ends up most-frequent first (nice, not required).
 *
 *   Second method topKFrequentBucket (the O(n) follow-up):
 *   1. Count as before. 2. bucket[c] = list of values with count c (c <= n).
 *   3. Walk buckets from n down to 1 collecting values until k are gathered.
 *
 * KEY INSIGHT
 *   Same primitive as Kth Largest in a Stream, just keyed on a computed frequency rather
 *   than the value itself. A min-heap bounded to k gives O(n log k), which beats sorting
 *   all m distinct entries when k << m. Buckets go further: counts are bounded by n, so
 *   they can be sorted in O(n) with no comparisons at all.
 *
 * COMPLEXITY
 *   Heap    Time O(n log k)   n to count, each of m <= n entries costs O(log k)
 *           Space O(m + k)    the map plus the heap
 *   Bucket  Time O(n)         count, distribute into n+1 buckets, scan down once
 *           Space O(n)        the map plus the buckets
 *
 * INTERVIEW FOLLOW-UPS
 *   - "Better than O(n log n)?" -> bucket sort on counts, O(n).
 *   - Top k frequent words (LC 692): ties broken alphabetically, so the comparator must
 *     order by count then reversed word; a heap handles that, buckets need sorting.
 *   - Stream of values with a fixed k: keep the map and heap alive and update per element.
 *   - Quickselect on the distinct entries by count: O(m) average, in place.
 *
 * RUN
 *   main() runs 3 cases through both methods and prints actual vs expected.
 *   Results are sorted before printing because the problem allows any order.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// https://leetcode.com/problems/top-k-frequent-elements/
class TopKFrequent {

    // Approach 1: frequency map + min-heap bounded to size k.  O(n log k)
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> countOf = new HashMap<>();
        for (int num : nums) {
            countOf.merge(num, 1, Integer::sum);
        }

        // min-heap on count: the root is the least frequent of the k kept so far
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
                new PriorityQueue<>((a, b) -> Integer.compare(a.getValue(), b.getValue()));

        for (Map.Entry<Integer, Integer> entry : countOf.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();                 // drop the least frequent, keep exactly k
            }
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {      // fill from the back: most frequent ends up first
            result[i] = minHeap.poll().getKey();
        }
        return result;
    }

    // Approach 2: bucket sort on counts.  O(n) -- answers the "better than n log n" follow-up.
    public int[] topKFrequentBucket(int[] nums, int k) {
        Map<Integer, Integer> countOf = new HashMap<>();
        for (int num : nums) {
            countOf.merge(num, 1, Integer::sum);
        }

        // bucket[c] holds every value that occurs exactly c times; c can be at most nums.length
        List<List<Integer>> bucket = new ArrayList<>();
        for (int c = 0; c <= nums.length; c++) {
            bucket.add(new ArrayList<>());
        }
        for (Map.Entry<Integer, Integer> entry : countOf.entrySet()) {
            bucket.get(entry.getValue()).add(entry.getKey());
        }

        int[] result = new int[k];
        int filled = 0;
        for (int c = nums.length; c >= 1 && filled < k; c--) {   // highest count first
            for (int value : bucket.get(c)) {
                if (filled == k) break;
                result[filled++] = value;
            }
        }
        return result;
    }

    // The problem accepts any order, so sort before comparing against the expected array.
    private static int[] sorted(int[] arr) {
        int[] copy = arr.clone();
        Arrays.sort(copy);
        return copy;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(sorted(actual))
                + "   expected " + Arrays.toString(sorted(expected)));
    }

    public static void main(String[] args) {
        TopKFrequent sol = new TopKFrequent();

        int[][] inputs = {
                {1, 1, 1, 2, 2, 3},          // typical
                {1},                         // edge: single element, k == distinct count
                {4, 1, -1, 2, -1, 2, 3},     // tricky: negatives, tie between -1 and 2
        };
        int[] ks = {2, 1, 2};
        int[][] expected = {{1, 2}, {1}, {-1, 2}};

        for (int i = 0; i < inputs.length; i++) {
            String label = "case " + (i + 1) + " nums=" + Arrays.toString(inputs[i])
                    + " k=" + ks[i];
            print(label + " heap  ", sol.topKFrequent(inputs[i], ks[i]), expected[i]);
            print(label + " bucket", sol.topKFrequentBucket(inputs[i], ks[i]), expected[i]);
        }
    }
}
