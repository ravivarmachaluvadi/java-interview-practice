import java.util.*;

public class ReconstructItinerary {

    public static List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        List<String> result = new LinkedList<>();

        // Build the graph
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.computeIfAbsent(from, k -> new PriorityQueue<>()).add(to);
        }

        // DFS to find the itinerary
        dfs(graph, "JFK", result);

        return result;
    }

    private static void dfs(Map<String, PriorityQueue<String>> graph, String current, List<String> result) {
        PriorityQueue<String> destinations = graph.get(current);

        // Visit all destinations in lexicographical order
        while (destinations != null && !destinations.isEmpty()) {
            String next = destinations.poll();
            dfs(graph, next, result);
        }

        // Add the current airport to the result after visiting all its destinations
        result.add(0, current);
    }

    public static void main(String[] args) {
        List<List<String>> tickets = new ArrayList<>();
        tickets.add(Arrays.asList("MUC", "LHR"));
        tickets.add(Arrays.asList("JFK", "MUC"));
        tickets.add(Arrays.asList("SFO", "SJC"));
        tickets.add(Arrays.asList("LHR", "SFO"));

        List<String> itinerary = findItinerary(tickets);

        System.out.println("Reconstructed Itinerary: " + itinerary);
    }
}
