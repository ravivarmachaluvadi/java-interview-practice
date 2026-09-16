import java.util.*;

// https://leetcode.com/problems/parallel-courses/description/

/**
 * representing a prerequisite relationship between course prevCoursei and course nextCoursei:
 * course prevCoursei has to be taken before course nextCoursei.
 * <p>
 * In one semester, you can take any number of courses as long as you have taken all
 * the prerequisites in the previous semester for the courses you are taking.
 * <p>
 * Return the minimum number of semesters needed to take all courses. If there is no
 * way to take all the courses
 */
class ParallelCourses {

    // Function to calculate the minimum number of semesters to complete all courses
    // BFS
    public static int minNumberOfSemesters(int n, List<List<Integer>> relations, int k) {
        // Build the graph (adjacency list) and calculate in-degrees
        int[] inDegree = new int[n + 1];
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (List<Integer> relation : relations) {
            int course = relation.get(0);
            int prereq = relation.get(1);
            graph.get(prereq).add(course);
            inDegree[course]++;
        }

        // Use a queue to perform topological sorting (Kahn's algorithm)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i); // Add courses with no prerequisites
            }
        }

        int semesters = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            int coursesToTake = Math.min(size, k); // Take at most k courses in this semester
            for (int i = 0; i < coursesToTake; i++) {
                int course = queue.poll();
                // For each course, reduce the in-degree of its dependent courses
                for (int next : graph.get(course)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) {
                        queue.offer(next); // Add courses that have no remaining prerequisites
                    }
                }
            }
            semesters++;
        }

        return semesters;
    }

    public static void main(String[] args) {
        // Example input:
        int n = 4;
        List<List<Integer>> relations = Arrays.asList(
                Arrays.asList(2, 1),
                Arrays.asList(3, 1),
                Arrays.asList(4, 2),
                Arrays.asList(4, 3)
        );
        int k = 2;  // You can take at most 2 courses per semester
        // Expected output: 3
        // Semester 1: take course 1 and 2
        // Semester 2: take course 3 and 4
        // Total: 3 semesters

        int result = minNumberOfSemesters(n, relations, k);
        System.out.println("Minimum number of semesters required: " + result);
    }
}
