/*
 * =====================================================================
 *  Reconstruct Itinerary                             LeetCode 332 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of airline tickets [from, to], build the one itinerary that
 *   starts at "JFK" and uses every ticket exactly once. The input always admits
 *   at least one such itinerary; if several exist, return the one that is
 *   smallest when read as a single list of strings.
 *
 * EXAMPLE
 *   [[MUC,LHR],[JFK,MUC],[SFO,SJC],[LHR,SFO]] -> [JFK, MUC, LHR, SFO, SJC]
 *   [[JFK,SFO],[JFK,ATL],[SFO,ATL],[ATL,JFK],[ATL,SFO]]
 *                                             -> [JFK, ATL, JFK, SFO, ATL, SFO]
 *   [[JFK,KUL],[JFK,NRT],[NRT,JFK]]           -> [JFK, NRT, JFK, KUL]
 *        the greedy "always fly to the smallest city" answer would start
 *        JFK -> KUL and strand you there with tickets still unused
 *
 * APPROACH  (Hierholzer's algorithm for an Eulerian path)
 *   1. Build airport -> min-heap of its outgoing destinations. The heap makes
 *      "smallest unused destination" an O(log d) poll, and polling also consumes
 *      the ticket, so no separate used[] array is needed.
 *   2. DFS from "JFK". At each airport, keep polling its heap and recursing
 *      until the heap is empty - that is, until the airport has no ticket left.
 *   3. Only THEN prepend the airport to the front of the answer (post-order).
 *   4. The list built by those prepends is the itinerary, start to finish.
 *
 * KEY INSIGHT
 *   Greedy-forward fails because one branch can be a dead end. Hierholzer turns
 *   that into a feature: the first airport that runs out of tickets must be the
 *   END of the route, so appending airports on the way OUT of the recursion and
 *   reversing (here: prepending) puts every dead-end branch in the right place.
 *   Recognise it whenever a problem says "use every edge exactly once" - that is
 *   an Eulerian path, not a Hamiltonian one, and Eulerian paths are cheap.
 *
 * COMPLEXITY
 *   Time  O(E log E)   each of the E tickets is pushed and polled once
 *   Space O(V + E)     graph, plus recursion depth up to E on a single chain
 *
 * INTERVIEW FOLLOW-UPS
 *   - When does an Eulerian path exist at all? (connected, and at most one node
 *     with out-in = 1 as the start and one with in-out = 1 as the end)
 *   - Why prepend instead of append-then-reverse, and does it matter? (same thing)
 *   - Rewrite the DFS iteratively with an explicit stack to survive deep inputs.
 *   - Eulerian path vs Hamiltonian path: one is linear, the other is NP-hard.
 *
 * RUN
 *   main() runs 4 cases (simple chain, lexicographic tie-break, the dead-end
 *   trap, and a single ticket) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class ReconstructItinerary {

    private static final String START = "JFK";

    public static List<String> findItinerary(List<List<String>> tickets) {
        // Min-heap per airport: polling gives the smallest unused destination.
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> ticket : tickets) {
            graph.computeIfAbsent(ticket.get(0), k -> new PriorityQueue<>()).add(ticket.get(1));
        }

        LinkedList<String> itinerary = new LinkedList<>();
        dfs(graph, START, itinerary);
        return itinerary;
    }

    private static void dfs(Map<String, PriorityQueue<String>> graph, String current,
                            LinkedList<String> itinerary) {
        PriorityQueue<String> destinations = graph.get(current);

        // Burn every ticket leaving this airport, smallest destination first.
        while (destinations != null && !destinations.isEmpty()) {
            dfs(graph, destinations.poll(), itinerary);
        }

        // Post-order: we only know where this airport belongs once it is stranded.
        // addFirst is O(1) on a LinkedList, so the whole build stays linear.
        itinerary.addFirst(current);
    }

    /** Builds the ticket list from flat "FROM", "TO" pairs to keep main() readable. */
    private static List<List<String>> tickets(String... fromToPairs) {
        List<List<String>> tickets = new ArrayList<>();
        for (int i = 0; i < fromToPairs.length; i += 2) {
            tickets.add(Arrays.asList(fromToPairs[i], fromToPairs[i + 1]));
        }
        return tickets;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // One unbranched chain: only one order is possible.
        print("case 1 (simple chain) ",
                findItinerary(tickets("MUC", "LHR", "JFK", "MUC", "SFO", "SJC", "LHR", "SFO")),
                "[JFK, MUC, LHR, SFO, SJC]");

        // Two choices at JFK and at ATL; the min-heap picks the smaller each time.
        print("case 2 (lexicographic)",
                findItinerary(tickets("JFK", "SFO", "JFK", "ATL", "SFO", "ATL",
                        "ATL", "JFK", "ATL", "SFO")),
                "[JFK, ATL, JFK, SFO, ATL, SFO]");

        // The trap: KUL sorts before NRT but is a dead end, so it must come last.
        print("case 3 (dead-end trap)",
                findItinerary(tickets("JFK", "KUL", "JFK", "NRT", "NRT", "JFK")),
                "[JFK, NRT, JFK, KUL]");

        // Edge case: a single ticket.
        print("case 4 (one ticket)   ",
                findItinerary(tickets("JFK", "ATL")),
                "[JFK, ATL]");
    }
}
