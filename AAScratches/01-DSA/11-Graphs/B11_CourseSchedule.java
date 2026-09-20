/*
 * =====================================================================
 *  Course Schedule                      LeetCode 207 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   There are numCourses courses labelled 0..numCourses-1. Each pair
 *   prerequisites[i] = [course, preReq] means preReq must be taken before course.
 *   Return true if every course can be finished, i.e. some order exists that
 *   respects all the pairs. Duplicate pairs are allowed; the list may be empty.
 *
 * EXAMPLE
 *   numCourses = 4, prereqs = [[1,0],[2,0],[3,1],[3,2]]  ->  true   (0,1,2,3)
 *   numCourses = 2, prereqs = [[1,0],[0,1]]              ->  false  (0 and 1 wait
 *                                                                    on each other)
 *   numCourses = 3, prereqs = []                         ->  true   (edge case)
 *
 * APPROACH  (Kahn's algorithm - topological sort by in-degree)
 *   1. Build a directed edge preReq -> course, so an edge points from the thing
 *      you must do first to the thing it unlocks.
 *   2. inDegree[c] = how many prerequisites course c is still waiting on.
 *   3. Push every course with inDegree 0 into a queue: those can start now.
 *   4. Pop a course, count it as taken, and decrement the in-degree of everything
 *      it unlocks. A course whose in-degree hits 0 joins the queue.
 *   5. If the count of popped courses equals numCourses, every course was
 *      reachable in some valid order -> true. Otherwise the leftovers all sit on
 *      a cycle and can never reach in-degree 0 -> false.
 *
 * KEY INSIGHT
 *   "Can all tasks be scheduled?" is the same question as "is this directed graph
 *   acyclic?", and Kahn's answers it by counting: exactly the vertices that are
 *   NOT on (or downstream of) a cycle ever get dequeued. The count check at the
 *   end is the cycle detector - no extra visited/recursion-stack bookkeeping.
 *   Recognise this whenever the input is a list of "B depends on A" pairs.
 *
 * COMPLEXITY
 *   Time  O(V + E)  V = numCourses, E = prerequisites.length; each vertex is
 *                   queued at most once and each edge relaxed once
 *   Space O(V + E)  adjacency list, in-degree array and queue
 *
 * INTERVIEW FOLLOW-UPS
 *   - Course Schedule II (LC 210): return the actual order - collect the popped
 *     courses instead of just counting them.
 *   - Do it with DFS instead: three-colour marking, grey-on-grey means a cycle.
 *   - Course Schedule IV (LC 1462): answer reachability queries - transitive
 *     closure, or a BFS per course.
 *   - What if the pairs can name a course >= numCourses? Validate input first.
 *
 * RUN
 *   main() runs 4 cases (a DAG, a 2-cycle, no prerequisites, a self-loop) and
 *   prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // adjacency list: preReq -> every course it unlocks
        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adjList.add(new ArrayList<>());
        }

        // inDegree[c] = number of prerequisites course c is still waiting on
        int[] inDegree = new int[numCourses];

        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int preReq = prerequisite[1];
            adjList.get(preReq).add(course);
            inDegree[course]++;
        }

        // courses with nothing to wait for can be started immediately
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int processedCourses = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processedCourses++;

            // taking this course removes one blocker from each dependent
            for (int dependent : adjList.get(course)) {
                if (--inDegree[dependent] == 0) {
                    queue.offer(dependent);
                }
            }
        }

        // anything left un-processed is stuck on a cycle
        return processedCourses == numCourses;
    }

    private static void check(String label, int numCourses, int[][] prereqs, boolean expected) {
        boolean actual = new CourseSchedule().canFinish(numCourses, prereqs);
        System.out.println(label + " n=" + numCourses
                + " prereqs=" + Arrays.deepToString(prereqs)
                + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: a diamond-shaped DAG, 0 unlocks 1 and 2, both unlock 3
        check("case 1 dag      ", 4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}, true);

        // tricky: 1 needs 0 and 0 needs 1, so neither ever reaches in-degree 0
        check("case 2 cycle    ", 2, new int[][]{{1, 0}, {0, 1}}, false);

        // edge case: no prerequisites at all, every course starts immediately
        check("case 3 no edges ", 3, new int[][]{}, true);

        // tricky: a self-loop is a cycle of length 1 - course 1 blocks itself
        check("case 4 self-loop", 3, new int[][]{{1, 1}, {2, 0}}, false);
    }
}
