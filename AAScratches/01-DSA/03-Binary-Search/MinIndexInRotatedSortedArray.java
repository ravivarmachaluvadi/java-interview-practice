class MinIndexInRotatedSortedArray {
    public static void main(String[] args) {
        int[] arr = {4, 5, 7, 8, 9, 0, 1, 2, 3};
        System.out.println(getMinIndex(arr));
    }

    private static int getMinIndex(int[] arr) {
        int low = 0, high = arr.length - 1;
        int mid = (low + high) / 2;
        while (low < high) {
            if (arr[low] < arr[high]) return low;
            if (arr[mid] < arr[mid + 1] && arr[mid] < arr[mid - 1]) {
                return mid;
            }
            // identifying sorted part using mid pointer
            if (arr[mid] < arr[high]) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

}
