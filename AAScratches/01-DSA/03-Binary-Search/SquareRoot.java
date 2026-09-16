class SquareRoot {
    public static int getMySqrt(int x) {
        if (x == 0) return 0; // Square root of 0 is 0

        int left = 1;
        int right = x;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Check if mid * mid is equal to x
            if (mid == x / mid)
                return mid;
                // If mid * mid is greater than x, the result must be smaller
            else if (mid > x / mid)
                right = mid - 1;
                // If mid * mid is less than x, the result must be greater
            else
                left = mid + 1;
        }
        // At the end, right will be the integer square root of x
        return right;
    }

    public static void main(String[] args) {
        System.out.println(getMySqrt(25));
    }
}
