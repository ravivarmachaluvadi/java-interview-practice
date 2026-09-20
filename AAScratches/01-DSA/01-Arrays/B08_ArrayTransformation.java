/*
 * =====================================================================
 *  Array Transformation                             LeetCode 1243 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of integers, repeatedly apply one "day" of changes until nothing changes:
 *   every interior element that is strictly smaller than both neighbours goes up by 1, and
 *   every interior element strictly larger than both neighbours goes down by 1. All changes
 *   in a day use the values from the start of that day. The first and last elements never
 *   move. Return the final, stable list.
 *
 * EXAMPLE
 *   arr = [1, 6, 3, 4, 3, 5]  ->  [1, 4, 4, 4, 4, 5]
 *     day 1: [1, 5, 4, 3, 4, 5]   day 2: [1, 4, 4, 4, 4, 5]   day 3: no change, stop
 *   arr = [6, 2, 3, 4]         ->  [6, 3, 3, 4]      (one day, then stable)
 *   arr = [2, 1, 2, 1, 2]      ->  [2, 2, 2, 2, 2]   (two days: the peak and valleys meet)
 *   arr = [5]                  ->  [5]               (no interior elements)
 *
 * APPROACH  (simultaneous update until stable)
 *   1. Loop while the previous day changed something.
 *   2. Copy the current list into `next`. Read neighbours from `current`, write into `next`,
 *      so every comparison in a day sees the values from the start of that day.
 *   3. For i in 1..n-2: strict local min -> next[i] = cur + 1; strict local max -> cur - 1.
 *   4. After the pass, `current = next` and repeat. Return `current` when a pass changes
 *      nothing.
 *
 * KEY INSIGHT
 *   Read from a snapshot, write to a copy. Updating in place would let the change at i
 *   leak into the comparison at i+1 within the same day and give a different answer.
 *   The same "double buffer" shape shows up in Game of Life, cellular automata and any
 *   converge-until-no-change loop.
 *
 * COMPLEXITY
 *   Time  O(n * p)  n work per day, p days until stable (p <= max value range)
 *   Space O(n)      one copy of the list per day
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not update in place? Show [1, 6, 3, 4, 3, 5]: in-place changes to index 1 alter
 *     the check at index 2 on the same day.
 *   - Can you avoid the full copy? Yes: keep only the previous left neighbour in a variable
 *     and update in place, since each cell only reads i-1 and i+1.
 *   - Is termination guaranteed? Yes: every change moves a value strictly toward its
 *     neighbours, and the sum of |arr[i] - arr[i+1]| strictly decreases each day.
 *
 * RUN
 *   main() runs 4 cases (typical, short, alternating, single) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class ArrayTransformation {

    public static List<Integer> transformArray(List<Integer> arr) {
        List<Integer> current = new ArrayList<>(arr);   // never mutate the caller's list
        int n = current.size();
        boolean changed = true;
        while (changed) {
            changed = false;
            List<Integer> next = new ArrayList<>(current); // write here, read from current
            for (int i = 1; i < n - 1; i++) {
                int left = current.get(i - 1);
                int mid = current.get(i);
                int right = current.get(i + 1);
                if (mid < left && mid < right) {          // strict local minimum
                    next.set(i, mid + 1);
                    changed = true;
                } else if (mid > left && mid > right) {   // strict local maximum
                    next.set(i, mid - 1);
                    changed = true;
                }
            }
            current = next;
        }
        return current;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical    ", transformArray(List.of(1, 6, 3, 4, 3, 5)),
                "[1, 4, 4, 4, 4, 5]");
        print("case 2 short      ", transformArray(List.of(6, 2, 3, 4)), "[6, 3, 3, 4]");
        print("case 3 alternating", transformArray(List.of(2, 1, 2, 1, 2)), "[2, 2, 2, 2, 2]");
        print("case 4 single     ", transformArray(List.of(5)), "[5]");
    }
}
