import java.util.*;

class ElectionWinner {

    public static String findWinner(String[] votes) {
        // Step 1: Count the votes for each candidate using a HashMap
        HashMap<String, Integer> voteCount = new HashMap<>();

        for (String vote : votes) {
            voteCount.put(vote, voteCount.getOrDefault(vote, 0) + 1);
        }

        // Step 2: Find the candidate with the maximum votes
        String winner = "";
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            String candidate = entry.getKey();
            int votesReceived = entry.getValue();

            // If current candidate has more votes or in case of tie lexicographically smaller name
            if (votesReceived > maxVotes || (votesReceived == maxVotes && candidate.compareTo(winner) < 0)) {
                winner = candidate;
                maxVotes = votesReceived;
            }
        }

        return winner;
    }

    public static void main(String[] args) {
        String[] votes = {"John", "Jane", "John", "Jane", "John", "Doe", "Doe", "Jane", "Doe", "John"};

        System.out.println("Winner of the election is: " + findWinner(votes));
        System.out.println("A".compareTo("B"));
        System.out.println("B".compareTo("A"));
    }
}
