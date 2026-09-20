/*
 * =====================================================================
 *  Online Election (Top Voted Candidate)             LeetCode 911 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   persons[i] is the candidate who received the i-th vote, cast at time
 *   times[i] (times strictly increasing). Build a structure that answers
 *   q(t): who is leading at time t, counting only votes cast at or before t.
 *   In case of a tie, the candidate who received a vote MOST RECENTLY wins.
 *   Queries always satisfy t >= times[0].
 *
 * EXAMPLE
 *   persons = [0,1,1,0,0,1,0], times = [0,5,10,15,20,25,30]
 *     q(3)  -> 0    only vote at t=0 counts
 *     q(12) -> 1    votes 0,1,1: candidate 1 leads 2-1
 *     q(25) -> 1    3-3 tie, candidate 1 voted most recently
 *     q(15) -> 0    2-2 tie, candidate 0 voted most recently
 *     q(24) -> 0    candidate 0 leads 3-2
 *     q(8)  -> 1    1-1 tie, candidate 1 voted most recently
 *   persons = [0], times = [0]: q(0) -> 0, q(100) -> 0   single vote
 *   persons = [0,1], times = [0,1]: q(1) -> 1            tie goes to latest
 *
 * APPROACH  (precompute state, binary search on timestamps)
 *   1. Constructor: walk the votes once, keep a count per candidate and the
 *      current leader. Record leaders[i] = leader after the i-th vote.
 *      Use count >= leaderCount (not >) so a tie hands the lead to the
 *      candidate just voted for.
 *   2. q(t): binary search times[] for the LAST index with times[i] <= t
 *      (upper-bound minus one), then return leaders[that index].
 *
 * KEY INSIGHT
 *   The leader at time t depends only on the prefix of votes up to t, and the
 *   prefix is fixed once the arrays are given. So precompute the answer for
 *   every prefix in O(n), then each query reduces to "find my prefix", which
 *   is a plain upper-bound search on the sorted timestamps. Preprocessing is
 *   the real interview content; the search is the template.
 *
 *   Fixed: original used count > leaderCount, which gave ties to the OLDER
 *   leader and contradicted the problem statement (and the author's own
 *   expected output for q(25), q(15), q(8)).
 *
 * COMPLEXITY
 *   Time  O(n) constructor, O(log n) per query
 *   Space O(n) for the leaders array and the count map
 *
 * INTERVIEW FOLLOW-UPS
 *   - votes arrive online and queries interleave: keep a running max, but
 *     queries about the past now need the snapshot array anyway
 *   - many candidates and a "top k at time t" query: per-prefix heap snapshot
 *     is too big; discuss persistent structures or offline query sorting
 *   - why is a HashMap fine here but an int[] would be faster? candidate ids
 *     are bounded by n, so an int[n] count array avoids boxing
 *
 * RUN
 *   main() runs 9 checks across 3 setups (LC example with 6 queries, single
 *   vote, two-way tie) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class TopVotedCandidate {
    private final int[] times;
    // leaders[i] = who is leading immediately after the i-th vote
    private final int[] leaders;

    public TopVotedCandidate(int[] persons, int[] times) {
        this.times = times;
        int n = persons.length;
        leaders = new int[n];

        Map<Integer, Integer> count = new HashMap<>();
        int currentLeader = -1;
        int currentLeaderVotes = 0;

        for (int i = 0; i < n; i++) {
            int p = persons[i];
            int c = count.getOrDefault(p, 0) + 1;
            count.put(p, c);

            // ">=" not ">": on a tie the most recently voted candidate wins.
            if (c >= currentLeaderVotes) {
                currentLeader = p;
                currentLeaderVotes = c;
            }
            leaders[i] = currentLeader;
        }
    }

    public int q(int t) {
        // Find the last index with times[index] <= t. When the loop ends,
        // high sits on that index (t >= times[0] is guaranteed, so high >= 0).
        int low = 0;
        int high = times.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (times[mid] <= t) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return leaders[high];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // LeetCode example 1
        TopVotedCandidate lc = new TopVotedCandidate(
                new int[]{0, 1, 1, 0, 0, 1, 0},
                new int[]{0, 5, 10, 15, 20, 25, 30});
        check("case 1 q(3)  ", lc.q(3), 0);
        check("case 1 q(12) ", lc.q(12), 1);
        check("case 1 q(25) ", lc.q(25), 1);
        check("case 1 q(15) ", lc.q(15), 0);
        check("case 1 q(24) ", lc.q(24), 0);
        check("case 1 q(8)  ", lc.q(8), 1);

        // Edge: a single vote, queried at and long after it
        TopVotedCandidate single = new TopVotedCandidate(new int[]{0}, new int[]{0});
        check("case 2 q(0)  ", single.q(0), 0);
        check("case 2 q(100)", single.q(100), 0);

        // Tricky: two-way tie, most recent vote wins
        TopVotedCandidate tie = new TopVotedCandidate(new int[]{0, 1}, new int[]{0, 1});
        check("case 3 q(1)  ", tie.q(1), 1);
    }
}
