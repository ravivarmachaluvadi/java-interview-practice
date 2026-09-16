/**
 * Finds every three-digit number in a range whose middle digit equals the sum of its
 * first and last digits.
 *
 * <p>For a number {@code abc}, the digits are extracted arithmetically:
 *
 * <ul>
 *   <li>{@code a = n / 100} — hundreds</li>
 *   <li>{@code b = (n / 10) % 10} — tens</li>
 *   <li>{@code c = n % 10} — units</li>
 * </ul>
 *
 * <p>The number qualifies when {@code a + c == b}. Because {@code b} is a single
 * digit, the sum must not exceed {@code 9}, so each choice of {@code a} admits at
 * most {@code 10 - a} candidates per hundred.
 *
 * <pre>
 * Input:  101 .. 200
 * Output: [110, 121, 132, 143, 154, 165, 176, 187, 198]
 * </pre>
 *
 * <p>Runs in O(hi - lo) time with O(k) space for the {@code k} results.
 *
 * @param lo lower bound, inclusive
 * @param hi upper bound, inclusive
 * @return matching numbers in ascending order
 * @throws IllegalArgumentException if the range falls outside {@code 100..999},
 *                                  where the digit arithmetic no longer holds
 */
List<Integer> findNumbersWithMiddleDigitAsSum(int lo, int hi) {
    if (lo < 100 || hi > 999) {
        throw new IllegalArgumentException(
                "range must lie within 100..999, got " + lo + ".." + hi);
    }

    List<Integer> result = new ArrayList<>();
    for (int n = lo; n <= hi; n++) {
        int first = n / 100;
        int middle = (n / 10) % 10;
        int last = n % 10;

        if (first + last == middle) {
            result.add(n);
        }
    }
    return result;
}

void main() {
    int[][] ranges = {
            {101, 200},   // the original range
            {100, 150},   // truncated mid-run
            {300, 400},   // larger leading digit shrinks the result set
            {900, 999}    // 9 + last <= 9 leaves only one candidate
    };

    for (int[] r : ranges) {
        IO.println(r[0] + ".." + r[1] + " -> " + findNumbersWithMiddleDigitAsSum(r[0], r[1]));
    }
}