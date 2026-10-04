/*
 * =====================================================================
 *  P041 Stack Simulation: Collisions, Paths, Call Stacks   Canonical LC 735 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 735, Asteroid Collision)
 *   Each asteroid's absolute value is its size and its sign its direction (+ right,
 *   - left), all at the same speed. When two meet, the smaller explodes; equal sizes both
 *   explode. Return the asteroids left after all collisions.
 *
 * EXAMPLE
 *   [5, 10, -5]      ->  [5, 10]
 *   [8, -8]          ->  []
 *   [10, 2, -5]      ->  [10]           -5 destroys 2, then loses to 10
 *   [-2, -1, 1, 2]   ->  [-2, -1, 1, 2] moving apart: no collisions
 *
 * RECOGNIZE WHEN
 *   - A new item interacts with the MOST RECENT surviving item(s) and may cancel several
 *     of them: collisions, "..", undo, nested function calls with start/end timestamps.
 *   - Simulating the process step by step is the intended solution.
 *   Not this if: the interaction is only "next greater" -> P037_NextGreaterElement.
 *
 * TEMPLATE
 *   stack = []
 *   for item in items:
 *       while stack and item interacts with stack.top:
 *           resolve: pop the top, and/or destroy the item (then stop)
 *       if item survived: push(item)
 *   the stack (bottom -> top) is the final state
 *
 * APPROACH
 *   1. Only a left-mover arriving after a right-mover on the stack can collide.
 *   2. While that holds, compare sizes: pop smaller tops; on a tie pop and destroy; if
 *      the top is bigger, the new asteroid is destroyed.
 *
 * KEY INSIGHT
 *   Survivors to the left can only be hit by something arriving from the right, in
 *   reverse order of arrival: last in, first hit. That is a stack, and each asteroid is
 *   pushed and popped at most once.
 *
 * COMPLEXITY
 *   Time O(n), space O(n). LC 853: O(n log n) for the sort.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 71   Simplify Path            split on '/'; ".." pops, "." and "" skip
 *   [coded] LC 853  Car Fleet                sort by position descending; a car that
 *                                            arrives no later than the fleet ahead joins it
 *   [coded] LC 636  Exclusive Time           stack of running function ids; pause the top
 *                                            on start, credit time on end (end is inclusive)
 *           LC 2751 Robot Collisions         LC 735 with health that drops by 1 on a win
 *           LC 1598 Crawler Log Folder       a depth counter is enough
 *           LC 946  Validate Stack Sequences push in order, pop while top == next popped
 *
 * PITFALLS
 *   - LC 735: two left-movers or a left then right pair never collide.
 *   - LC 636: an end timestamp covers its whole unit: duration = end - start + 1.
 *   - LC 853: compare arrival TIMES as doubles (or cross-multiply), not distances.
 *
 * DEEP DIVE
 *   C11_AsteroidCollision, C12_SimplifyPath, C08_CarFleet, C14_ExclusiveTimeOfFunctions,
 *   D06_RobotCollisions (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

class StackSimulation {

    // Canonical LC 735.
    static int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int a : asteroids) {
            boolean alive = true;
            while (alive && a < 0 && !stack.isEmpty() && stack.peek() > 0) {
                int top = stack.peek();
                if (top < -a) {
                    stack.pop();                   // the right-mover explodes; keep checking
                } else {
                    if (top == -a) {
                        stack.pop();               // both explode
                    }
                    alive = false;
                }
            }
            if (alive) {
                stack.push(a);
            }
        }
        int[] out = new int[stack.size()];
        Iterator<Integer> it = stack.descendingIterator();   // bottom -> top
        for (int i = 0; i < out.length; i++) {
            out[i] = it.next();
        }
        return out;
    }

    // LC 71.
    static String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        for (String part : path.split("/")) {
            if (part.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!part.isEmpty() && !part.equals(".")) {
                stack.push(part);
            }
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append('/').append(it.next());
        }
        return sb.length() == 0 ? "/" : sb.toString();
    }

    // LC 853.
    static int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (x, y) -> position[y] - position[x]);    // closest to target first
        int fleets = 0;
        double slowestAhead = 0;                   // arrival time of the fleet in front
        for (int i : order) {
            double time = (double) (target - position[i]) / speed[i];
            if (time > slowestAhead) {
                fleets++;                          // cannot catch up: a new fleet
                slowestAhead = time;
            }
        }
        return fleets;
    }

    // LC 636: logs "id:start|end:timestamp".
    static int[] exclusiveTime(int n, List<String> logs) {
        int[] total = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        int prev = 0;                              // start of the current unpaid stretch
        for (String log : logs) {
            String[] p = log.split(":");
            int id = Integer.parseInt(p[0]);
            int time = Integer.parseInt(p[2]);
            if (p[1].equals("start")) {
                if (!stack.isEmpty()) {
                    total[stack.peek()] += time - prev;
                }
                stack.push(id);
                prev = time;
            } else {
                total[stack.pop()] += time - prev + 1;
                prev = time + 1;
            }
        }
        return total;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 735 [5,10,-5]",
                Arrays.toString(asteroidCollision(new int[]{5, 10, -5})), "[5, 10]");
        check("LC 735 [8,-8]", Arrays.toString(asteroidCollision(new int[]{8, -8})), "[]");
        check("LC 735 [10,2,-5]", Arrays.toString(asteroidCollision(new int[]{10, 2, -5})), "[10]");
        check("LC 735 moving apart", Arrays.toString(asteroidCollision(new int[]{-2, -1, 1, 2})),
                "[-2, -1, 1, 2]");

        check("LC 71 /home/", simplifyPath("/home/"), "/home");
        check("LC 71 double slash", simplifyPath("/home//foo/"), "/home/foo");
        check("LC 71 dot-dot",
                simplifyPath("/home/user/Documents/../Pictures"), "/home/user/Pictures");
        check("LC 71 above root", simplifyPath("/../"), "/");
        check("LC 71 three dots is a name", simplifyPath("/.../a/../b/c/../d/./"), "/.../b/d");

        check("LC 853 five cars",
                carFleet(12, new int[]{10, 8, 0, 5, 3}, new int[]{2, 4, 1, 1, 3}), 3);
        check("LC 853 one car", carFleet(10, new int[]{3}, new int[]{3}), 1);
        check("LC 853 all merge", carFleet(100, new int[]{0, 2, 4}, new int[]{4, 2, 1}), 1);

        check("LC 636 two functions", Arrays.toString(exclusiveTime(2,
                List.of("0:start:0", "1:start:2", "1:end:5", "0:end:6"))), "[3, 4]");
        List<String> recursive = List.of("0:start:0", "0:start:2", "0:end:5",
                "0:start:6", "0:end:6", "0:end:7");
        check("LC 636 recursion", Arrays.toString(exclusiveTime(1, recursive)), "[8]");
    }
}
