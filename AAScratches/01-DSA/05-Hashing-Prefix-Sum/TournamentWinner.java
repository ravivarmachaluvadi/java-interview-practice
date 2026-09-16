/**
 * Determines the winner of a round‑robin tournament.
 *
 * Given a list of matches (each match is a pair of team names) and a parallel list of results
 * where 1 indicates the home team won and 0 indicates the away team won, this method returns
 * the team that has won the most matches. If multiple teams tie for the highest number of wins,
 * the first one encountered in the iteration order is returned.
 *
 * The algorithm iterates once over all matches, updating a hash map that tracks each team's win count.
 * After each update it checks whether this team now holds the maximum score seen so far.
 *
 * Time Complexity: O(n) where n is the number of matches.
 * Space Complexity: O(k) for storing scores of k distinct teams (k ≤ n).
 */
import java.util.HashMap;
import java.util.List;

class TournamentWinner {
    public static String tournamentWinner(List<List<String>> matches,
                                          List<Integer> results) {
        HashMap<String, Integer> scores = new HashMap<>();
        String currentBestTeam = "";
        int maxScore = 0;

        for (int i = 0; i < matches.size(); i++) {
            List<String> match = matches.get(i);
            String homeTeam = match.get(0);
            String awayTeam = match.get(1);

            // Determine the winner based on the result
            String winningTeam = results.get(i) == 1 ? homeTeam : awayTeam;

            // Update the score for the winning team
            scores.put(winningTeam, scores.getOrDefault(winningTeam, 0) + 1);

            // Check if this team has the highest score so far
            if (scores.get(winningTeam) > maxScore) {
                maxScore = scores.get(winningTeam);
                currentBestTeam = winningTeam;
            }
        }

        return currentBestTeam;
    }

    public static void main(String[] args) {
        List<List<String>> matches = List.of(
                List.of("TeamA", "TeamB"),
                List.of("TeamB", "TeamC"),
                List.of("TeamC", "TeamA")
        );
        List<Integer> results = List.of(1, 0, 1);

        System.out.println(tournamentWinner(matches, results));  // Output: TeamC
    }
}

