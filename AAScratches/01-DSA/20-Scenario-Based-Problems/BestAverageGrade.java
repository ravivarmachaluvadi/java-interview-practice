/**
 * Problem: Given a list of student name–score pairs, find the highest average score among all students.
 *
 * Approach: Build a map from each student to a list of their scores. Then compute the average for each
 * student's score list and track the maximum average found. Return the floor of that maximum as an integer.
 *
 * Time Complexity: O(n) where n is the number of input pairs (building the map and computing averages).
 * Space Complexity: O(m + k) where m is the number of distinct students and k is the total number of scores
 * stored in the lists. The additional space is dominated by the map and its value lists.
 */
import java.util.*;

class BestAverageGrade {

    public static Integer bestAverageGrade(String[][] inputScores) {
        if (inputScores.length == 0) return 0;

        Map<String, ArrayList<Integer>> studentScoresMap = new HashMap<>();

        for (String[] scoreRow : inputScores) {
            if (scoreRow.length != 2)
                return 0; // Invalid input case

            String student = scoreRow[0];
            Integer score = Integer.parseInt(scoreRow[1]);

            studentScoresMap.putIfAbsent(student, new ArrayList<>());
            studentScoresMap.get(student).add(score);
        }

        double maxAverage = Double.MIN_VALUE;

        for (ArrayList<Integer> scores : studentScoresMap.values()) {
            int sum = 0;
            for (int score : scores)
                sum += score;

            double average = sum / (double) scores.size();
            maxAverage = Math.max(maxAverage, average);
        }
        return (int) Math.floor(maxAverage);
    }

    public static boolean doTestsPass() {
        Map<String[][], Integer> testCases = new HashMap<>();

        testCases.put(new String[][]{{"Bobby", "87"}, {"Charles", "100"}, {"Eric", "64"}, {"Charles", "22"}}, 87);

        boolean passed = true;
        for (Map.Entry<String[][], Integer> entry : testCases.entrySet()) {
            Integer actual = bestAverageGrade(entry.getKey());
            if (!actual.equals(entry.getValue())) {
                passed = false;
                System.out.println("Failed for " + Arrays.deepToString(entry.getKey()) + "\n  expected " + entry.getValue() + ", actual " + actual);
            }
        }

        return passed;
    }

    public static void main(String[] args) {
        if (doTestsPass()) {
            System.out.println("All tests pass");
        } else {
            System.out.println("Tests fail.");
        }
    }
}
