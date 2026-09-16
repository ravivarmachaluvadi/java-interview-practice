import java.util.*;

class AlternatePositiveNegative {

    // Classic version: works only when positives.length == negatives.length
    // (kept faithful to the original problem statement, but made safe)
    public static int[] rearrangeArray(int[] nums) {
        int n = nums.length;
        long posCount = Arrays.stream(nums).filter(x -> x >= 0).count();
        if (posCount != n - posCount) {
            throw new IllegalArgumentException(
                    "rearrangeArray requires an equal count of positive and negative numbers");
        }

        int[] result = new int[n];
        int posIndex = 0, negIndex = 1;
        for (int num : nums) {
            if (num >= 0) {
                result[posIndex] = num;
                posIndex += 2;
            } else {
                result[negIndex] = num;
                negIndex += 2;
            }
        }
        return result;
    }

    // General version: handles unequal counts of positives/negatives
    public static void rearrange(int[] arr, int n) {
        ArrayList<Integer> positive = new ArrayList<>();
        ArrayList<Integer> negative = new ArrayList<>();

        for (int num : arr) {
            if (num >= 0) {
                positive.add(num);
            } else {
                negative.add(num);
            }
        }

        int pos = 0, neg = 0, i = 0;

        while (pos < positive.size() && neg < negative.size()) {
            if (i % 2 == 0) {
                arr[i] = positive.get(pos++);
            } else {
                arr[i] = negative.get(neg++);
            }
            i++;
        }

        while (pos < positive.size()) {
            arr[i++] = positive.get(pos++);
        }

        while (neg < negative.size()) {
            arr[i++] = negative.get(neg++);
        }
    }

    public static void main(String[] args) {
        // Use an array with EQUAL counts of positives/negatives for rearrangeArray,
        // since that method assumes this invariant (like the original LeetCode problem).
        int[] original = {1, 2, 3, -4, -1, -2};
        System.out.println("Original array: " + Arrays.toString(original));

        // Work on a copy so we don't destroy 'original' before using it again
        int[] arrCopy = original.clone();
        rearrange(arrCopy, arrCopy.length);
        System.out.println("Rearranged (general method): " + Arrays.toString(arrCopy));

        // Now correctly reuse the untouched original array
        System.out.println("Original array (unchanged): " + Arrays.toString(original));

        int[] result = rearrangeArray(original);
        System.out.println("Rearranged (equal-count method): " + Arrays.toString(result));
    }
}