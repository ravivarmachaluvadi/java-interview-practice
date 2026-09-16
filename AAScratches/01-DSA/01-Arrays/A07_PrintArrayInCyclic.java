/**
 * Problem: Given an array of integers, print a 6x4 matrix where each row contains four consecutive
 * elements starting from successive positions in the array, wrapping around to the beginning when
 * the end is reached.
 *
 * Approach: Iterate over six rows. For each row, start at index `col` and output four elements,
 * using modulo arithmetic to wrap indices. Increment `row` and `col` after each line.
 *
 * Time Complexity: O(6 * 4) = O(1) – constant time for fixed-size array.
 * Space Complexity: O(1) – only a few integer variables are used, no additional data structures.
 */
void main() {
    int[] arr = {1, 2, 3, 4, 5, 6};

    int row = 0;
    int col = 0;
    while (row < 6) {
        int colp = col;
        for (int i = 0; i < 4; i++) {
            IO.print(arr[colp % 6]);
            colp++;
        }
        IO.println();
        row++;
        col++;
    }
}
/**
 * 1234
 * 2345
 * 3456
 * 4561
 * 5612
 * 6123
 */