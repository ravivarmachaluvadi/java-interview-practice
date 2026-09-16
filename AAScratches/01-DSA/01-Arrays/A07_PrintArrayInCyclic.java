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