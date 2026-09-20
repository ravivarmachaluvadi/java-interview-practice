/*
 * =====================================================================
 *  Exclusive Time of Functions                        LeetCode 636 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A single-threaded CPU runs n functions (ids 0..n-1). Logs are "id:start:t" or "id:end:t",
 *   in time order. "start:t" means the function began at the START of unit t; "end:t" means it
 *   finished at the END of unit t (so a call with start:2 end:5 ran 4 units). Calls nest like a
 *   call stack and may be recursive. Return each function's exclusive time: the units it ran
 *   itself, excluding time spent in functions it called.
 *
 * EXAMPLE
 *   n=2  [0:start:0, 1:start:2, 1:end:5, 0:end:6]                       ->  [3, 4]
 *        fn 0 runs 0-1 and 6 (3 units), fn 1 runs 2-5 (4 units)
 *   n=1  [0:start:0, 0:start:2, 0:end:5, 0:start:6, 0:end:6, 0:end:7]  ->  [8]   recursion
 *   n=2  [0:start:0, 0:end:0, 1:start:1, 1:end:1]                       ->  [1, 1] 1-unit calls
 *
 * APPROACH  (call-frame stack with time deltas)
 *   Keep a stack of running function ids and prevTime = the first time unit not yet charged.
 *   1. On "start:t": the function on top of the stack ran from prevTime up to (not including) t,
 *      so charge it t - prevTime. Push the new id. prevTime = t.
 *   2. On "end:t": the top function ran from prevTime through t inclusive, so charge it
 *      t - prevTime + 1 and pop. prevTime = t + 1 (unit t is fully used up).
 *   3. The array of charges is the answer.
 *
 * KEY INSIGHT
 *   Every log line is a boundary: whatever is on top of the stack owns the time between the
 *   previous boundary and this one. The only subtlety is the inclusive/exclusive convention:
 *   start is exclusive of t (charge up to t), end is inclusive of t (charge through t, then
 *   move prevTime past it). Getting the "+1" and "prevTime = t + 1" right is the whole problem.
 *
 * COMPLEXITY
 *   Time  O(L)  one pass over L log lines; string split is constant work per line
 *   Space O(n + depth)  the result array plus the call stack at its deepest nesting
 *
 * INTERVIEW FOLLOW-UPS
 *   - Inclusive time instead (time including callees): record start time per frame
 *   - Multi-core: logs per core, or interleaved with a core id, need one stack per core
 *   - What breaks if the logs are not sorted by time? How would you validate them?
 *
 * RUN
 *   main() runs 3 cases (nested calls, recursion, back-to-back 1-unit calls) and prints
 *   actual vs expected.
 */
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

// https://leetcode.com/problems/exclusive-time-of-functions/description/
class ExclusiveTimeOfFunctions {

    public static int[] exclusiveTime(int n, List<String> logs) {
        int[] exclusive = new int[n];
        Stack<Integer> callStack = new Stack<>();   // ids of functions currently running
        int prevTime = 0;                           // first time unit not yet charged to anyone

        for (String log : logs) {
            String[] parts = log.split(":");        // "{id}:{start|end}:{time}"
            int id = Integer.parseInt(parts[0]);
            boolean isStart = parts[1].equals("start");
            int time = Integer.parseInt(parts[2]);

            if (isStart) {
                // The caller (top of stack) ran from prevTime up to, but not including, time.
                if (!callStack.isEmpty()) {
                    exclusive[callStack.peek()] += time - prevTime;
                }
                callStack.push(id);
                prevTime = time;
            } else {
                // "end:t" means it finished at the END of unit t, so unit t itself counts: +1.
                exclusive[callStack.pop()] += time - prevTime + 1;
                prevTime = time + 1;                // unit t is fully consumed
            }
        }
        return exclusive;
    }

    private static void print(String label, int n, List<String> logs, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(exclusiveTime(n, logs))
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        print("case 1 nested calls ", 2,
                Arrays.asList("0:start:0", "1:start:2", "1:end:5", "0:end:6"),
                new int[]{3, 4});
        print("case 2 recursion    ", 1,
                Arrays.asList("0:start:0", "0:start:2", "0:end:5",
                              "0:start:6", "0:end:6", "0:end:7"),
                new int[]{8});
        print("case 3 1-unit calls ", 2,
                Arrays.asList("0:start:0", "0:end:0", "1:start:1", "1:end:1"),
                new int[]{1, 1});
    }
}
