import java.util.Stack;

class NextGreaterElements {

    private static int[] getNextGreaterElements(int[] input) {
        Stack<Integer> stack = new Stack<>();
        int n = input.length;
        int[] output = new int[n];

        stack.push(0);

        for (int i = 1; i < n; i++) {
            while (!stack.isEmpty() && input[stack.peek()] < input[i]) {
                output[stack.peek()] = input[i];
                stack.pop();
            }
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            output[stack.pop()] = -1;
        }

        return output;
    }

    private static void printArray(int[] arr) {
        for (int i : arr) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Sample test case 1
        int[] input1 = {4, 5, 2, 10, 8};
        int[] result1 = getNextGreaterElements(input1);
        System.out.println("Next greater elements for {4, 5, 2, 10, 8}: ");
        printArray(result1);

        // Sample test case 2
        int[] input2 = {3, 7, 1, 7, 6, 4};
        int[] result2 = getNextGreaterElements(input2);
        System.out.println("Next greater elements for {3, 7, 1, 7, 6, 4}: ");
        printArray(result2);

        // Sample test case 3
        int[] input3 = {6, 8, 0, 1, 3};
        int[] result3 = getNextGreaterElements(input3);
        System.out.println("Next greater elements for {6, 8, 0, 1, 3}: ");
        printArray(result3);
    }
}
