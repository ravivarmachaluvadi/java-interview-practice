/*
 * =====================================================================
 *  Tournament Winner                                  AlgoExpert | Easy
 * =====================================================================
 *
 * PROBLEM
 *   matches[i] = [homeTeam, awayTeam] and results[i] is 1 if the home team won, 0 if the
 *   away team won. Every win is worth one point; no ties within a match. Return the team with
 *   the most points. If several teams share the top score, the first team to reach it wins.
 *
 * EXAMPLE
 *   matches = [[A,B],[B,C],[C,A]], results = [1,0,1]  ->  "TeamC"  (C beat B and A)
 *   matches = [[HTML,C#],[C#,Python],[Python,HTML]], results = [0,0,1] -> "Python"
 *   matches = [[A,B],[C,D]], results = [1,1]         ->  "A"   (tie; A reached 1 first)
 *   matches = []                                      ->  ""    (no matches, no winner)
 *
 * APPROACH  (tally map with running max)
 *   1. Walk the matches once; results[i] tells you which of the two names is the winner.
 *   2. Increment the winner's count in a HashMap<team, wins>.
 *   3. Right after the increment, compare the new count with the best seen so far and
 *      update the leader if it is strictly greater. No second pass over the map is needed.
 *
 * KEY INSIGHT
 *   Accumulate and track the maximum in the same pass. The "strictly greater" comparison is
 *   what makes ties go to the team that reached the score first. Pattern to recognise: any
 *   "who/what appears most" question is a count map plus a running best.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over n matches, O(1) map work per match
 *   Space O(k)  one map entry per distinct team, k <= 2n
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if a draw is possible (each team gets 1, a win gets 3)? Update both teams per match.
 *   - Return all tied leaders instead of the first one: collect keys whose count == max.
 *   - Millions of matches streaming in: same code, the map is the only state you keep.
 *
 * RUN
 *   main() runs 4 cases (typical, AlgoExpert sample, tie, empty) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TournamentWinner {

    private static final int HOME_TEAM_WON = 1;

    public static String tournamentWinner(List<List<String>> matches, List<Integer> results) {
        Map<String, Integer> winsByTeam = new HashMap<>();
        String bestTeam = "";
        int bestWins = 0;

        for (int i = 0; i < matches.size(); i++) {
            List<String> match = matches.get(i);
            String homeTeam = match.get(0);
            String awayTeam = match.get(1);
            String winner = results.get(i) == HOME_TEAM_WON ? homeTeam : awayTeam;

            int winnerWins = winsByTeam.getOrDefault(winner, 0) + 1;
            winsByTeam.put(winner, winnerWins);

            // strictly greater: on a tie the team that reached the score first keeps the lead
            if (winnerWins > bestWins) {
                bestWins = winnerWins;
                bestTeam = winner;
            }
        }
        return bestTeam;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)",
                tournamentWinner(
                        List.of(List.of("TeamA", "TeamB"), List.of("TeamB", "TeamC"),
                                List.of("TeamC", "TeamA")),
                        List.of(1, 0, 1)),
                "TeamC");

        print("case 2 (AlgoExpert sample)",
                tournamentWinner(
                        List.of(List.of("HTML", "C#"), List.of("C#", "Python"),
                                List.of("Python", "HTML")),
                        List.of(0, 0, 1)),
                "Python");

        print("case 3 (tie -> first to reach max)",
                tournamentWinner(
                        List.of(List.of("A", "B"), List.of("C", "D")),
                        List.of(1, 1)),
                "A");

        print("case 4 (no matches)",
                tournamentWinner(List.of(), List.of()),
                "");
    }
}
