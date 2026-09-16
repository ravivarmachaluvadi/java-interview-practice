import java.util.Arrays;

class MeanMedianCalculator {
    public static void main(String[] args) {
        int[] array = {5, 3, 8, 1, 2}; // Example unsorted array
        double mean = calculateMean(array);
        double median = calculateMedian(array);
        System.out.println("Mean: " + mean);
        System.out.println("Median: " + median);
    }

    public static double calculateMean(int[] array) {
        double sum = 0;
        for (int num : array)
            sum += num;
        return sum / array.length;
    }

    public static double calculateMedian(int[] array) {
        Arrays.sort(array); // Sort the array
        int n = array.length;
        if (n % 2 == 1) {
            return array[n / 2]; // Odd case
        } else {
            return (array[(n / 2) - 1] + array[n / 2]) / 2.0; // Even case
        }
    }
}
