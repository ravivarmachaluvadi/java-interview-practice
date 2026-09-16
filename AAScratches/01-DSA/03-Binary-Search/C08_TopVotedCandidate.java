import java.util.*;

class TopVotedCandidate {
    private int[] times;
    // leaders[i] = leader after vote i
    private int[] leaders;

    // persons[i] is the candidate who got the i-th vote.
    // times[i] is the time when that vote was cast.
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

            if (c > currentLeaderVotes) {
                currentLeader = p;
                currentLeaderVotes = c;
            }

            leaders[i] = currentLeader;
        }
    }

    public int q(int t) {
        int low = 0;
        int high = times.length - 1;

        if (t >= times[high]) {
            return leaders[high];
        }
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

    // Example test in main
    public static void main(String[] args) {
        int[] persons = {0, 1, 1, 0, 0, 1, 0};
        int[] times = {0, 5, 10, 15, 20, 25, 30};

        TopVotedCandidate tvc = new TopVotedCandidate(persons, times);

        int[] queryTimes = {3, 12, 25, 15, 24, 8};
        for (int qt : queryTimes) {
            System.out.println("At time " + qt + ", leader = " + tvc.q(qt));
        }
        // Expected output:
        // At time 3, leader = 0
        // At time 12, leader = 1
        // At time 25, leader = 1
        // At time 15, leader = 0
        // At time 24, leader = 0
        // At time 8, leader = 1
    }
}
