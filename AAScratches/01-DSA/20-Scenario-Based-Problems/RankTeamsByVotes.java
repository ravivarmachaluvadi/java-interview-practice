import java.util.*;
// https://leetcode.com/problems/rank-teams-by-votes/
// 1366. Rank Teams by Votes

/**
 * 1 <= votes.length <= 1000
 * <p>
 * 1 <= votes[i].length <= 26
 * <p>
 * votes[i].length == votes[j].length for 0 <= i, j < votes.length.
 * <p>
 * votes[i][j] is an English uppercase letter.
 * <p>
 * All characters of votes[i] are unique.
 * <p>
 * All the characters that occur in votes[0] also occur in votes[j] where 1 <= j < votes.length.
 *
 */
import java.util.*;

class RankTeamsByVotes {
    public static String rankTeams(String[] votes) {
        if (votes == null || votes.length == 0) return "";

        int n = votes[0].length();
        int[][] teams = new int[26][n];

        for (String vote : votes) {
            for (int i = 0; i < n; i++) {
                teams[vote.charAt(i) - 'A'][i]++;
            }
        }

        Character[] arr = new Character[n];
        for (int i = 0; i < n; i++) {
            arr[i] = votes[0].charAt(i);
        }

        Arrays.sort(arr, (a, b) -> {
            int idxA = a - 'A', idxB = b - 'A';
            for (int i = 0; i < n; i++) {
                if (teams[idxA][i] != teams[idxB][i]) {
                    // frequency descending
                    return teams[idxB][i] - teams[idxA][i];
                }
            }
            // order of character by ascending
            return a - b;
        });

        StringBuilder sb = new StringBuilder();
        for (char c : arr) sb.append(c);
        return sb.toString();
    }

    public static void main(String[] args) {
        String[] votes1 = {"ABC", "ACB", "ABC", "ACB", "ACB"};
        System.out.println("Result → " + rankTeams(votes1));  // expected "ACB"

        String[] votes2 = {"WXYZ", "XYZW"};
        System.out.println("Result → " + rankTeams(votes2));  // expected "XWYZ"
    }
}
