class CountFrequencies {
    // O(n) time and O(1) space
    // Cyclic sorting technique involved
    public static void countFrequenciesEfficient(int[] input) {

        int n = input.length;
        for (int i = 0; i < n; i++)
            input[i]--;

        for (int i = 0; i < n; i++) {
            int valIndex = input[i] % n;
            input[valIndex] += n;
        }

        for (int i = 0; i < n; i++) {
            System.out.println((i + 1) + " " + input[i] / n);
            // Change the element back to original value
            input[i] = input[i] % n + 1;
        }
    }

    public static void main(String[] args) {
        int[] input = {3, 3, 3, 5, 5};
        countFrequenciesEfficient(input);
        System.out.println();
        countFrequenciesEfficient(new int[]{10, 5, 10, 15, 10, 5});

    }
}
