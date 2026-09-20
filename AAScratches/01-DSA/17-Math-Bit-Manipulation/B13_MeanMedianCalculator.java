/*
 * =====================================================================
 *  Mean and Median of an Array                            Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return its arithmetic mean and its median. The mean
 *   is the sum divided by the count. The median is the middle value once the
 *   data is sorted; when the count is even it is the average of the two central
 *   values, so the median can be a fraction even for integer input.
 *
 * EXAMPLE
 *   [5, 3, 8, 1, 2]  ->  mean 3.8,  median 3.0   (sorted: 1 2 [3] 8 -> 1 2 3 5 8)
 *   [5, 3, 8, 1]     ->  mean 4.25, median 4.0   (sorted: 1 [3 5] 8 -> (3+5)/2)
 *   [7]              ->  mean 7.0,  median 7.0   (single element)
 *
 * APPROACH  (sum for the mean, sort for the median)
 *   1. Mean: accumulate into a double and divide by length. Divide once at the
 *      end, not per element, to keep rounding error small.
 *   2. Median: sort a COPY of the input so the caller's array is untouched.
 *   3. Odd length n: the answer is copy[n / 2].
 *   4. Even length n: average copy[n/2 - 1] and copy[n/2], dividing by 2.0 so
 *      the halves are not lost to integer division.
 *
 * KEY INSIGHT
 *   The median only needs the element at rank n/2, not a fully ordered array.
 *   Sorting is the easy O(n log n) way to get it, but quickselect finds the same
 *   element in O(n) average time, and a two-heap structure maintains it in
 *   O(log n) per insert for a stream. Recognise "middle value" as a selection
 *   problem, not a sorting problem.
 *
 * COMPLEXITY
 *   Time  O(n) for the mean, O(n log n) for the median - the sort dominates
 *   Space O(n) because the input is copied before sorting (O(1) if sorted in place)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Find the median in O(n) average time with quickselect.
 *   - Median of a running stream (LeetCode 295: max-heap plus min-heap).
 *   - Median of two sorted arrays in O(log(m+n)) (LeetCode 4).
 *   - Why does summing into a double still drift, and when would you use
 *     Kahan summation or a long accumulator instead?
 *
 * NOTE ON THE ORIGINAL
 *   Fixed: calculateMedian sorted the caller's array in place, so the argument
 *   came back reordered - a surprise side effect. It now sorts a copy.
 *   Fixed: an empty array produced NaN / an out-of-bounds read; it now reports
 *   the bad input.
 *
 * RUN
 *   main() runs 4 cases (odd length, even length, single element, empty) and
 *   prints actual vs expected, plus proof the input is left unsorted.
 */
import java.util.Arrays;

class MeanMedianCalculator {

    public static void main(String[] args) {
        int[] oddCount = {5, 3, 8, 1, 2};
        System.out.println("case 1 mean  [5, 3, 8, 1, 2]: " + calculateMean(oddCount)
                + "   expected 3.8");
        System.out.println("case 1 median[5, 3, 8, 1, 2]: " + calculateMedian(oddCount)
                + "   expected 3.0");
        // the caller's array must still be in its original order
        System.out.println("case 1 input after the calls: " + Arrays.toString(oddCount)
                + "   expected [5, 3, 8, 1, 2]");

        int[] evenCount = {5, 3, 8, 1};
        System.out.println("case 2 mean  [5, 3, 8, 1]   : " + calculateMean(evenCount)
                + "   expected 4.25");
        System.out.println("case 2 median[5, 3, 8, 1]   : " + calculateMedian(evenCount)
                + "   expected 4.0");

        int[] single = {7};
        System.out.println("case 3 mean  [7]            : " + calculateMean(single)
                + "   expected 7.0");
        System.out.println("case 3 median[7]            : " + calculateMedian(single)
                + "   expected 7.0");

        try {
            calculateMean(new int[0]);
            System.out.println("case 4 empty array          : no exception"
                    + "   expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("case 4 empty array          : IllegalArgumentException"
                    + "   expected IllegalArgumentException");
        }
    }

    public static double calculateMean(int[] array) {
        requireNonEmpty(array);
        double sum = 0;
        for (int num : array)
            sum += num;
        return sum / array.length;          // one division at the end
    }

    public static double calculateMedian(int[] array) {
        requireNonEmpty(array);
        int[] sorted = array.clone();       // never reorder the caller's data
        Arrays.sort(sorted);

        int n = sorted.length;
        if (n % 2 == 1) {
            return sorted[n / 2];                                   // odd: exact middle
        }
        return (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;           // even: /2.0 keeps the half
    }

    private static void requireNonEmpty(int[] array) {
        if (array == null || array.length == 0)
            throw new IllegalArgumentException("mean and median need at least one element");
    }
}
