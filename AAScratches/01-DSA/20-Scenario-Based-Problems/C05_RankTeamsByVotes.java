/*
 * =====================================================================
 *  Rank Teams by Votes                          LeetCode 1366 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Every voter submits a full ranking of the same teams as a string, e.g. "ACB"
 *   means A first, C second, B third. Produce the overall ranking: a team ranks above
 *   another if it received more FIRST-place votes; if those tie, compare second-place
 *   votes, then third, and so on. If a team ties on every position, order the two by
 *   team letter ascending. Teams are uppercase letters, at most 26 of them, and every
 *   vote lists exactly the same teams.
 *
 * EXAMPLE
 *   ["ABC","ACB","ABC","ACB","ACB"]  ->  "ACB"
 *     A: 5 firsts. B and C both have 0 firsts, so compare seconds: C has 3, B has 2.
 *   ["WXYZ","XYZW"]                  ->  "XWYZ"
 *     W and X each have 1 first; both have 0 seconds and 0 thirds; W has 1 fourth
 *     and X has 0, so X wins on the fourth position - the tie goes deep.
 *   ["BCA","CAB","CBA","ABC","ACB","BAC"] -> "ABC"   every team ties everywhere
 *   ["A"]                            ->  "A"         edge case main() runs
 *
 * APPROACH  (positional tally matrix, then a cascading comparator)
 *   1. Build tally[26][n]: tally[team][position] = how many voters put that team in
 *      that position. One pass over all votes, O(votes * n).
 *   2. Collect the participating teams - votes[0] already lists each exactly once.
 *   3. Sort them with a comparator that walks positions 0..n-1 and returns on the
 *      first position where the two teams differ, higher count first.
 *   4. If the loop finishes without a difference the teams are identical on every
 *      position, so fall back to letter ascending.
 *
 * KEY INSIGHT
 *   The counting is trivial; the COMPARATOR is the interview signal. A cascading
 *   tie-break is a loop over keys that returns on the first difference and only falls
 *   through to the final rule when every key matched. Write it that way and the number
 *   of tie-break levels stops mattering - 2 or 26, the code is identical. The second
 *   thing to say out loud: the comparator must be a total order (transitive, and
 *   antisymmetric), which is exactly why the letter fallback is mandatory rather than
 *   cosmetic - without it Arrays.sort can throw "Comparison method violates its
 *   general contract" and the output becomes input-order dependent.
 *   Recognise the shape next time: ElectionWinner is this problem with n = 1.
 *
 * COMPLEXITY
 *   With v = number of votes and n = number of teams (n <= 26):
 *   Time  O(v * n + n^2 log n)   tally pass, then a sort whose comparator costs O(n)
 *   Space O(26 * n) = O(n)       the tally matrix; bounded by 26 * 26 = 676 ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not return teams[b][i] - teams[a][i] as an int subtraction in general?
 *     (safe here because counts are at most 1000, but it overflows for arbitrary ints)
 *   - Why is the letter fallback required, not just nice? (total-order contract)
 *   - Votes stream in one at a time and you must answer at any moment - what changes?
 *     (keep the tally live; the sort is O(n^2 log n) per query, or maintain a TreeSet)
 *   - Weighted positions (3/2/1 points) instead of lexicographic cascade - how does
 *     the comparator collapse? (to a single summed score, and ties become common)
 *
 * RUN
 *   main() runs 5 cases (two LeetCode examples, an all-tie case, a single vote, a
 *   single team) and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Comparator;

class RankTeamsByVotes {

    private static final int ALPHABET = 26;

    public static String rankTeams(String[] votes) {
        if (votes == null || votes.length == 0) return "";
        if (votes.length == 1) return votes[0]; // one voter: their ranking IS the answer

        final int teamCount = votes[0].length();

        // tally[team letter][position] = number of voters placing that team there.
        final int[][] tally = new int[ALPHABET][teamCount];
        for (String vote : votes) {
            for (int position = 0; position < teamCount; position++) {
                tally[vote.charAt(position) - 'A'][position]++;
            }
        }

        // votes[0] lists every participating team exactly once.
        Character[] teams = new Character[teamCount];
        for (int i = 0; i < teamCount; i++) {
            teams[i] = votes[0].charAt(i);
        }

        Arrays.sort(teams, cascadingComparator(tally, teamCount));

        StringBuilder ranking = new StringBuilder(teamCount);
        for (char team : teams) ranking.append(team);
        return ranking.toString();
    }

    /**
     * Walk the positions in order and return on the FIRST position where the two teams
     * differ, more votes first. Identical on every position -> letter ascending, which
     * is what makes this a total order and keeps Arrays.sort legal.
     */
    private static Comparator<Character> cascadingComparator(int[][] tally, int teamCount) {
        return (a, b) -> {
            int teamA = a - 'A';
            int teamB = b - 'A';
            for (int position = 0; position < teamCount; position++) {
                if (tally[teamA][position] != tally[teamB][position]) {
                    // Descending on count: the team with MORE votes at this position wins.
                    return Integer.compare(tally[teamB][position], tally[teamA][position]);
                }
            }
            return Character.compare(a, b); // mandatory fallback, not cosmetic
        };
    }

    // ---------- Test harness ----------

    private static void check(String label, String[] votes, String expected) {
        System.out.println(label + ": " + rankTeams(votes) + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1 - typical: A sweeps the firsts, C beats B on second-place votes
        check("case 1", new String[]{"ABC", "ACB", "ABC", "ACB", "ACB"}, "ACB");

        // case 2 - tricky: W and X tie on positions 1..3, decided only at position 4
        check("case 2", new String[]{"WXYZ", "XYZW"}, "XWYZ");

        // case 3 - tricky: every team ties on every position, so letters decide
        check("case 3", new String[]{"BCA", "CAB", "CBA", "ABC", "ACB", "BAC"}, "ABC");

        // case 4 - edge: a single voter, so their ranking is the result verbatim
        check("case 4", new String[]{"ZMNAGUEDSJYLBOPHRQICWFXTVK"}, "ZMNAGUEDSJYLBOPHRQICWFXTVK");

        // case 5 - edge: one team only
        check("case 5", new String[]{"A", "A", "A"}, "A");

        // case 6 - edge: no votes at all
        check("case 6", new String[]{}, "");
    }
}
