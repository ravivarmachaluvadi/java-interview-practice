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

