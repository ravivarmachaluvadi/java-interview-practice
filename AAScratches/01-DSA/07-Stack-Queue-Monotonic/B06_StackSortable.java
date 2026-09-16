import java.util.Stack;

class StackSortable {

    // check given one is rotated sorted array
    public static boolean isStackSortable(int[] A) {
        Stack<Integer> stack = new Stack<>();
        int n = A.length;
        // Start with the smallest expected element
        int expected = 1;

        for (int i = 0; i < n; i++) {
            stack.push(A[i]);

            // Try to pop elements from the stack in increasing order
            while (!stack.isEmpty() && stack.peek() == expected) {
                stack.pop();  // Pop the expected element
                expected++;   // Increment the expected value
            }
        }

        // If we managed to pop all elements in increasing order, it is sortable
        return expected == n + 1;
    }

    public static void main(String[] args) {
        int[] A1 = {4, 1, 2, 3};  // Stack sortable: true (valid permutation)
        int[] A2 = {4, 3, 1, 2};  // Not stack sortable: false (invalid permutation)
        int[] A3 = {1, 2, 3, 4};  // Not stack sortable: false (invalid permutation)

        System.out.println("Is A1 stack sortable? " + isStackSortable(A3)); // Output: true
        System.out.println("Is A1 stack sortable? " + isStackSortable(A1)); // Output: true
        System.out.println("Is A2 stack sortable? " + isStackSortable(A2)); // Output: false
    }
}
