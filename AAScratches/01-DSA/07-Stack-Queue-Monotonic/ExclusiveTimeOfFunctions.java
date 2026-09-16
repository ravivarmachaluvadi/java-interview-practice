import java.util.*;

// https://leetcode.com/problems/exclusive-time-of-functions/description/
/**
 * You are given a list logs, where logs[i] represents the ith log message formatted as a string
 * "{function_id}:{"start" | "end"}:{timestamp}". For example, "0:start:3"
 * <p>
 * A function's exclusive time is the sum of execution times for all function calls in the program.
 * For example,if a function is called twice, one call executing for 2 time units and another call
 * executing for 1 time unit, the exclusive time is 2 + 1 = 3.
 */
class ExclusiveTimeOfFunctions {

    public static int[] exclusiveTime(int n, List<String> logs) {
        int[] result = new int[n];
        // stack maintains ids of functions
        Stack<Integer> stack = new Stack<>();
        int prevTime = 0;

        for (String log : logs) {
            String[] parts = log.split(":");
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            int time = Integer.parseInt(parts[2]);
            if (type.equals("start")) {
                if (!stack.isEmpty()) {
                    // Add time to the prev function at the top of the stack
                    // as it's in execution from prevTime to currTime
                    result[stack.peek()] += time - prevTime;
                }
                // Push the current function ID onto the stack as it's started
                stack.push(id);
                prevTime = time;
            } else {
                // pop stack as its ended
                result[stack.pop()] += time - prevTime + 1;
                // Update the previous time to the end time + 1
                prevTime = time + 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // "{function_id}:{"start" | "end"}:{timestamp}"
        List<String> logs = Arrays.asList("0:start:0", "1:start:2", "1:end:5", "0:end:6");
        // Expected output: [3, 4]
        // Function 0 runs from time 0 to 2 and then from time 5 to 6 (total 3 units of time).
        // Function 1 runs from time 2 to 5 (total 4 units of time).

        int n = 2; // Number of functions
        int[] result = exclusiveTime(n, logs);
        System.out.println("Exclusive time of each function: " + Arrays.toString(result));
    }
}
