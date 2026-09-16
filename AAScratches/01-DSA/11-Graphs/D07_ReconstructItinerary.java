/**
 * Reconstructs an itinerary from a list of airline tickets.
 *
 * Problem: Given a collection of directed edges (tickets) where each ticket is
 * represented as [from, to], find the unique Eulerian path that uses all tickets
 * exactly once and starts at "JFK". When multiple destinations are possible,
 * choose the lexicographically smallest one first.
 *
 * Approach: Build an adjacency list mapping each departure airport to a min-heap
 * (PriorityQueue) of arrival airports. Perform a depth‑first search from "JFK",
 * always selecting the smallest available destination. After exploring all
 * outgoing edges, prepend the current airport to the result list. This yields
 * the itinerary in correct order.
 *
 * Time Complexity: O(E log D), where E is the number of tickets and D is the
 * maximum outdegree (due to heap operations). In practice this simplifies to
 * O(E log E).
 *
 * Space Complexity: O(V + E) for the graph, plus O(V) for recursion stack and
 * result list, where V is the number of distinct airports.
 */
import java.util.*;

class ReconstructItinerary {

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
