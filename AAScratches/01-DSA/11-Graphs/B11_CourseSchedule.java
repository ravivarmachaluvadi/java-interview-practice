import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Depth-First Search, Breadth-First Search, Graph, Topological Sort
// https://leetcode.com/problems/course-schedule
class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Create an adjacency list for the graph
        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adjList.add(new ArrayList<>());
        }

        // In-degree array to count prerequisites
        int[] inDegree = new int[numCourses];

        // Populate the adjacency list and in-degree array
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int preReq = prerequisite[1];
            adjList.get(preReq).add(course);
            inDegree[course]++;
        }

        // Queue to process courses with no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        // Count of processed courses
        int processedCourses = 0;

        // Process the graph
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processedCourses++;

            // Reduce in-degree of dependent courses
            for (int dependent : adjList.get(course)) {
                inDegree[dependent]--;
                if (inDegree[dependent] == 0) {
                    queue.offer(dependent);
                }
            }
        }

        // If all courses were processed, return true, otherwise false
        return processedCourses == numCourses;
    }

    public static void main(String[] args) {
        CourseSchedule scheduler = new CourseSchedule();
        int numCourses = 4;
        int[][] prerequisites = {{1,0},{2,0},{3,1},{3,2}};
        System.out.println("Input:");
        System.out.println("numCourses = " + numCourses);
        System.out.print("prerequisites = [");
        for (int i = 0; i < prerequisites.length; i++) {
            System.out.print("[" + prerequisites[i][0] + "," + prerequisites[i][1] + "]");
            if (i < prerequisites.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        boolean result = scheduler.canFinish(numCourses, prerequisites);
        System.out.println("\nOutput:");
        System.out.println("Can finish all courses? " + result);
    }
}
