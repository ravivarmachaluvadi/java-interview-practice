import java.util.ArrayList;
import java.util.List;
/**
 * <h2>Array Transformation</h2>
 *
 * <p>Given an array of integers, transform it so that no interior element
 * (any element except the first and last) is a strict local minimum or a
 * strict local maximum relative to its immediate neighbors.</p>
 *
 * <p>In one pass, for every interior index {@code i} where
 * {@code 1 <= i <= n-2}:</p>
 * <ul>
 *   <li>If {@code arr[i] < arr[i-1]} and {@code arr[i] < arr[i+1]}
 *       (local minimum), increment {@code arr[i]} by 1.</li>
 *   <li>If {@code arr[i] > arr[i-1]} and {@code arr[i] > arr[i+1]}
 *       (local maximum), decrement {@code arr[i]} by 1.</li>
 *   <li>Otherwise {@code arr[i]} is unchanged.</li>
 * </ul>
 *
 * <p>All updates in a pass use the values from <b>before</b> that pass began
 * (simultaneous update, not sequential). Repeat passes until one pass
 * produces no changes. The first and last elements are never modified.</p>
 *
 * <p>Returns the final, stabilized array.</p>
 *
 * <pre>
 * Example:
 *   Input:  [1, 6, 3, 4, 3, 5]
 *   Output: [1, 4, 4, 4, 4, 5]
 * </pre>
 *
 * <p>Time: {@code O(n * p)} where {@code p} is the number of passes until
 * stable.<br>
 * Space: {@code O(n)} for the temp copy per pass.</p>
 */

class ArrayTransformation {
    public List<Integer> transformArray(List<Integer> arr) {
        int n = arr.size();
        boolean changed = true;
        while (changed) {
            changed = false;
            //temp list with input arr
            List<Integer> temp = new ArrayList<>(arr);
            for (int i = 1; i < n - 1; i++) {
                if (arr.get(i) < arr.get(i - 1) && arr.get(i) < arr.get(i + 1)) {
                    // make sure you are modifying temp array
                    temp.set(i, arr.get(i) + 1);
                    changed = true;
                } else if (arr.get(i) > arr.get(i - 1) && arr.get(i) > arr.get(i + 1)) {
                    // make sure you are modifying temp array
                    temp.set(i, arr.get(i) - 1);
                    changed = true;
                }
            }
            arr = new ArrayList<>(temp); // Update the list
        }
        return arr;
    }

    public static void main(String[] args) {
        ArrayTransformation solution = new ArrayTransformation();
        List<Integer> arr = new ArrayList<>();
        arr.add(1);
        arr.add(6);
        arr.add(3);
        arr.add(4);
        arr.add(3);
        arr.add(5);
        List<Integer> result = solution.transformArray(arr);
        System.out.println(result); // Output the transformed array
    }
}
