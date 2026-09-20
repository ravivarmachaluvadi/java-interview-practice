/*
 * =====================================================================
 *  Election Winner With Lexicographic Tie-Break            Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of ballots, each a candidate's name, return the candidate
 *   with the most votes. If several candidates tie on votes, the winner is the
 *   lexicographically smallest name. An empty ballot box returns "".
 *
 * EXAMPLE
 *   ["John","Jane","John","Jane","John","Doe","Doe","Jane","Doe","John"] -> "John"
 *       John 4, Jane 3, Doe 3 - clear majority, no tie-break needed
 *   ["Bob","Alice"]  ->  "Alice"   one vote each, smaller name wins
 *   []               ->  ""        edge case main() runs
 *
 * APPROACH  (count votes, then argmax with a tie-break)
 *   1. Tally every ballot into a HashMap<String, Integer>.
 *   2. Scan the entry set keeping the best candidate seen so far.
 *   3. A candidate replaces the current best when it has strictly more votes,
 *      OR it has exactly the same votes and a lexicographically smaller name.
 *   4. Return the surviving best. "" falls out naturally for an empty input.
 *
 * KEY INSIGHT
 *   The tie-break turns an order-dependent scan into a deterministic one. With
 *   only "votes > max" the answer is whichever tied candidate HashMap iteration
 *   happens to reach first, which is not insertion order and can change between
 *   runs. Adding "|| (votes == max && name.compareTo(best) < 0)" makes the scan
 *   a true argmin/argmax over the pair (-votes, name), so the result is the same
 *   no matter what order the entries arrive in. Recognise this shape: any
 *   "pick the best, ties broken by X" is one comparison over a compound key.
 *
 * COMPLEXITY
 *   Time  O(n + k)  n ballots to count, k distinct candidates to scan
 *   Space O(k)      the tally map
 *
 * INTERVIEW FOLLOW-UPS
 *   - Tie-break on earliest first vote instead of name - keep a first-seen index.
 *   - Return the full ranking, not just the winner - sort entries by the same key.
 *   - Streaming ballots with a running winner at any moment - keep max as you count.
 *   - Ranked-choice voting: eliminate the last-placed candidate and redistribute.
 *
 * RUN
 *   main() runs 4 cases (typical, two-way tie, all votes equal, empty) and
 *   prints actual vs expected on one line each.
 */
import java.util.HashMap;
import java.util.Map;

class ElectionWinner {

    public static String findWinner(String[] votes) {
        // Step 1: tally the ballots.
        HashMap<String, Integer> voteCount = new HashMap<>();
        for (String vote : votes) {
            voteCount.put(vote, voteCount.getOrDefault(vote, 0) + 1);
        }

        // Step 2: argmax over (votes desc, name asc). The compound comparison is
        // what makes the result independent of HashMap iteration order.
        String winner = "";
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            String candidate = entry.getKey();
            int votesReceived = entry.getValue();

            boolean beatsCurrent = votesReceived > maxVotes
                    || (votesReceived == maxVotes && candidate.compareTo(winner) < 0);
            if (beatsCurrent) {
                winner = candidate;
                maxVotes = votesReceived;
            }
        }

        return winner;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        String[] clearWinner = {"John", "Jane", "John", "Jane", "John",
                "Doe", "Doe", "Jane", "Doe", "John"};
        String[] twoWayTie = {"Bob", "Alice", "Bob", "Alice"};     // 2 each -> smaller name
        String[] allDistinct = {"Zoe", "Adam", "Mia"};             // 1 each -> smaller name
        String[] noBallots = {};

        print("case 1 clear winner", findWinner(clearWinner), "John");
        print("case 2 two-way tie", findWinner(twoWayTie), "Alice");
        print("case 3 every vote unique", findWinner(allDistinct), "Adam");
        print("case 4 empty ballot box (edge)", findWinner(noBallots), "");
    }
}
