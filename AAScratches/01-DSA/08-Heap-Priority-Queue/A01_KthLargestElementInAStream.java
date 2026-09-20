/*
 * =====================================================================
 *  Kth Largest Element in a Stream           LeetCode 703 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a class that is constructed with an integer k and an initial array nums.
 *   Each call to add(val) appends val to the stream and returns the kth largest element
 *   seen so far (kth largest in sorted order, so duplicates count separately).
 *   1 <= k <= 10^4, up to 10^4 calls to add, and there are at least k elements whenever
 *   add() is asked for an answer.
 *
 * EXAMPLE
 *   k = 3, nums = [4, 5, 8, 2]
 *     add(3)  -> 4     stream {2,3,4,5,8}, 3rd largest is 4
 *     add(5)  -> 5     stream {2,3,4,5,5,8}
 *     add(10) -> 5
 *     add(9)  -> 8
 *     add(4)  -> 8
 *   k = 1, nums = []  (edge: empty start)
 *     add(-3) -> -3, add(-2) -> -2, add(-4) -> -2, add(1) -> 1
 *
 * APPROACH  (bounded size-k min-heap)
 *   1. Keep a min-heap that never holds more than k elements: the k largest so far.
 *   2. On add(val): if the heap has fewer than k elements, just offer val.
 *      Otherwise, if val is bigger than the root (the smallest of the k largest),
 *      poll the root and offer val; if val is not bigger, it cannot be in the top k.
 *   3. The root of the heap is always the kth largest. Return peek().
 *
 * KEY INSIGHT
 *   "Kth largest" == "smallest of the k largest". A min-heap capped at size k makes that
 *   smallest element sit at the root for free. Every top-K / kth-largest / k-closest
 *   problem in this folder is this one idea with a different comparator.
 *
 * COMPLEXITY
 *   Time  O(log k) per add          one poll + one offer on a heap of size k
 *   Space O(k)                      the heap never grows past k
 *
 * INTERVIEW FOLLOW-UPS
 *   - Kth smallest instead: flip to a max-heap of size k.
 *   - Whole array is given up front (LC 215): quickselect gives O(n) average.
 *   - Why not a sorted list? Insert is O(n); the heap keeps only what matters.
 *   - What if k > number of elements? Problem guarantees it will not happen; heap.peek()
 *     would return the smallest seen so far, which is not a valid answer.
 *
 * RUN
 *   main() runs 3 cases (typical, empty-start with negatives, all-equal) and prints
 *   actual vs expected.
 */
import java.util.Arrays;
import java.util.PriorityQueue;

// https://leetcode.com/problems/kth-largest-element-in-a-stream/
class KthLargestElementInAStream {
    private final PriorityQueue<Integer> minHeap;   // holds the k largest seen; root = kth largest
    private final int k;

    // Fixed: this used to be a plain method named KthLargest(...) that mimicked a constructor,
    // so callers had to do "new X(); x.KthLargest(k, nums)". It is now a real constructor.
    public KthLargestElementInAStream(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(k);
        for (int num : nums) {
            add(num);
        }
    }

    public int add(int val) {
        if (minHeap.size() < k) {
            minHeap.offer(val);                 // still filling the top-k window
        } else if (val > minHeap.peek()) {
            minHeap.poll();                     // evict the smallest of the current top k
            minHeap.offer(val);                 // val takes its place
        }
        // else: val is <= the current kth largest, so it can never be in the top k
        return minHeap.peek();
    }

    // Replays a sequence of add() calls and collects each returned kth largest.
    private static int[] replay(int k, int[] initial, int[] adds) {
        KthLargestElementInAStream stream = new KthLargestElementInAStream(k, initial);
        int[] answers = new int[adds.length];
        for (int i = 0; i < adds.length; i++) {
            answers[i] = stream.add(adds[i]);
        }
        return answers;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        // typical: LeetCode example 1
        print("case 1 (k=3, start [4,5,8,2], add 3,5,10,9,4)",
                replay(3, new int[]{4, 5, 8, 2}, new int[]{3, 5, 10, 9, 4}),
                new int[]{4, 5, 5, 8, 8});

        // edge: empty initial array, negatives, k = 1 (so answer is simply the max so far)
        print("case 2 (k=1, start [], add -3,-2,-4,1)",
                replay(1, new int[]{}, new int[]{-3, -2, -4, 1}),
                new int[]{-3, -2, -2, 1});

        // tricky: duplicates count separately; all-equal stream
        print("case 3 (k=2, start [7,7,7], add 7,8,8)",
                replay(2, new int[]{7, 7, 7}, new int[]{7, 8, 8}),
                new int[]{7, 7, 8});
    }
}
