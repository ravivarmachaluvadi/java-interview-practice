class KthElementOf2SortedArrays {
    public int kthElement(int[] a, int[] b, int k) {
        // making sure m is smaller
        int m = a.length;
        int n = b.length;
        // Ensure a is smaller array for optimization
        if (m > n) {
            // Swap a and b
            return kthElement(b, a, k);
        }

        // Length of the left half
        int left = k;

        // Apply binary search
//        low and 0 same side
        int low = Math.max(0, k - n), high = Math.min(k, m);
        while (low <= high) {
            int mid1 = (low + high) >> 1;
            int mid2 = left - mid1;

            // Initialize l1, l2, r1, r2
            int l1 = (mid1 > 0) ? a[mid1 - 1] : Integer.MIN_VALUE;
            int l2 = (mid2 > 0) ? b[mid2 - 1] : Integer.MIN_VALUE;
            int r1 = (mid1 < m) ? a[mid1] : Integer.MAX_VALUE;
            int r2 = (mid2 < n) ? b[mid2] : Integer.MAX_VALUE;

            // Check if we have found the answer
            if (l1 <= r2 && l2 <= r1) {
                return Math.max(l1, l2);
            } else if (l1 > r2) {
                // Eliminate the right half
                high = mid1 - 1;
            } else {
                // Eliminate the left half
                low = mid1 + 1;
            }
        }

        // Dummy return statement
        return -1;
    }

    public static void main(String[] args) {
        KthElementOf2SortedArrays solution = new KthElementOf2SortedArrays();
        int[] a = {2, 3, 6, 7, 9};
        int[] b = {1, 4, 8, 10};
        int k = 5;
        System.out.println("Kth element is: " + solution.kthElement(a, b, k)); // Output: 6
    }
}