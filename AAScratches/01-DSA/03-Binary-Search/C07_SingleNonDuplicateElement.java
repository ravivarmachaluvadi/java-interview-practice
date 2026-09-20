/*
 * =====================================================================
 *  Single Element in a Sorted Array                  LeetCode 540 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A sorted array where every element appears exactly twice except one
 *   element that appears once. Return that element in O(log n) time and
 *   O(1) space. The array length is always odd.
 *
 * EXAMPLE
 *   [1, 1, 2, 2, 3, 4, 4, 5, 5]  ->  3
 *   [3, 3, 7, 7, 10, 11, 11]     ->  10
 *   [1]                          ->  1    single element
 *   [1, 2, 2]                    ->  1    unique at the very start
 *   [1, 1, 2]                    ->  2    unique at the very end
 *
 * APPROACH  (index-parity invariant)
 *   1. Handle the trivial spots directly: length 1, unique at index 0, unique
 *      at index n-1. After that the unique is strictly inside, so mid-1 and
 *      mid+1 are always valid indices.
 *   2. Binary search. If arr[mid] differs from both neighbours, it is the answer.
 *   3. Otherwise arr[mid] is part of a pair. Before the unique element, pairs
 *      start at EVEN indices (0-1, 2-3, ...). After it they start at ODD indices.
 *      So: mid even and paired with mid-1, or mid odd and paired with mid+1,
 *      means the pair is "shifted" and the unique lies to the LEFT.
 *      Otherwise the pattern is still intact and the unique lies to the RIGHT.
 *
 * KEY INSIGHT
 *   The values are not what is monotone here, the pairing alignment is.
 *   Left of the unique, every pair is (even, odd); right of it, every pair is
 *   (odd, even). That is a yes/no property that flips exactly once, which is
 *   all binary search needs. Same lesson as peak element: you do not need
 *   sorted values, you need a predicate that is false...false true...true.
 *
 * COMPLEXITY
 *   Time  O(log n)  halving each step
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - XOR of all elements also works but is O(n); explain why it fails the ask
 *   - shortcut: check arr[mid] against arr[mid ^ 1]; if equal, unique is right
 *   - what if elements appear three times except one? (LC 137, bit counting)
 *
 * RUN
 *   main() runs 5 cases (typical, larger values, single element, unique at
 *   start, unique at end) and prints actual vs expected.
 */
class SingleNonDuplicateElement {

    public static int singleNonDuplicate(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0];
        if (arr[0] != arr[1]) return arr[0];
        if (arr[n - 1] != arr[n - 2]) return arr[n - 1];

        // The unique element is now strictly inside (index 1..n-2), so both
        // neighbours of mid always exist.
        int low = 0, high = n - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (arr[mid] != arr[mid - 1] && arr[mid] != arr[mid + 1]) {
                return arr[mid];
            }

            // Pairs left of the unique start at even indices. If the pair
            // containing mid starts at an odd index, the pattern has already
            // shifted, so the unique element is to the left.
            boolean pairStartsAtOddIndex = (mid % 2 == 0 && arr[mid] == arr[mid - 1])
                    || (mid % 2 == 1 && arr[mid] == arr[mid + 1]);

            if (pairStartsAtOddIndex) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1; // unreachable for valid input
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical        ",
                singleNonDuplicate(new int[]{1, 1, 2, 2, 3, 4, 4, 5, 5}), 3);
        check("case 2 larger values  ", singleNonDuplicate(new int[]{3, 3, 7, 7, 10, 11, 11}), 10);
        check("case 3 single element ", singleNonDuplicate(new int[]{1}), 1);
        check("case 4 unique at start", singleNonDuplicate(new int[]{1, 2, 2}), 1);
        check("case 5 unique at end  ", singleNonDuplicate(new int[]{1, 1, 2}), 2);
    }
}
