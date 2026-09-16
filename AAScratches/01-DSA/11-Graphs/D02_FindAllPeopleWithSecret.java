import java.util.*;

/**
 * person xi and person yi have a meeting at timei
 * <p>
 * Input: n = 6, meetings = [[1,2,5],[2,3,8],[1,5,10]], firstPerson = 1
 * <p>
 * Output: [0,1,2,3,5]
 */
class FindAllPeopleWithSecret {

    // Function to find all people with secret
    public static List<Integer> findAllPeople(int[][] meetings, int firstPerson) {
        // Create an adjacency list to store the meeting pairs
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        
        // Add the firstPerson as someone who knows the secret
        Set<Integer> peopleWithSecret = new HashSet<>();
        peopleWithSecret.add(0);
        peopleWithSecret.add(firstPerson);

        // Add meetings to the graph
        for (int[] meeting : meetings) {
            int person1 = meeting[0];
            int person2 = meeting[1];
            graph.putIfAbsent(person1, new HashSet<>());
            graph.putIfAbsent(person2, new HashSet<>());
            graph.get(person1).add(person2);
            graph.get(person2).add(person1);
        }

        // Use BFS to spread the secret
        Queue<Integer> queue = new LinkedList<>();
        queue.add(firstPerson);

        while (!queue.isEmpty()) {
            int currentPerson = queue.poll();
            for (int neighbor : graph.getOrDefault(currentPerson, new HashSet<>())) {
                if (!peopleWithSecret.contains(neighbor)) {
                    peopleWithSecret.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        // Return the list of people who know the secret
        return new ArrayList<>(peopleWithSecret);
    }

    public static void main(String[] args) {
        int n = 6;
        int[][] meetings = {
                {1, 2, 5},
                {2, 3, 8},
                {1, 5, 10}
        };
        int firstPerson = 1;

        List<Integer> result = findAllPeople(meetings, firstPerson);
        System.out.println("People who know the secret: " + result);
    }
}
