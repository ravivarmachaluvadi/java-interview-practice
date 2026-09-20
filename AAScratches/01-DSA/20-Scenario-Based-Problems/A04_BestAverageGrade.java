/*
 * =====================================================================
 *  Best Average Grade                                      Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of [studentName, score] string pairs, find the highest average
 *   score any single student achieved and return its floor as an int.
 *   An empty list returns 0. A malformed row (not exactly two fields) also
 *   returns 0, matching the original contract.
 *
 * EXAMPLE
 *   [["Bobby","87"],["Charles","100"],["Eric","64"],["Charles","22"]]  ->  87
 *       Bobby 87, Charles (100+22)/2 = 61, Eric 64  ->  best is 87
 *   [["Ann","-10"],["Ann","-11"]]  ->  -11   average -10.5, floor is -11
 *   []                             ->  0     edge case main() runs
 *
 * APPROACH  (group by key, then aggregate)
 *   1. Build Map<String, List<Integer>> from student name to all their scores,
 *      using putIfAbsent to create the list on first sight.
 *   2. For each student, sum the list and divide by its size as a double.
 *   3. Track the running maximum average, seeded with NEGATIVE_INFINITY so a
 *      negative average can still win.
 *   4. Return Math.floor of the winner, cast to int.
 *
 * KEY INSIGHT
 *   Group-then-aggregate is the workhorse shape behind most log and record
 *   problems: one pass to bucket rows under a key, a second pass to reduce each
 *   bucket. Upgrading a plain counter map to a map of lists is the only change.
 *   Fixed: the maximum was seeded with Double.MIN_VALUE, which is the smallest
 *   POSITIVE double (about 4.9e-324), not the most negative one. Any input whose
 *   best average was negative returned 0. Seed running maxima with
 *   Double.NEGATIVE_INFINITY (or Integer.MIN_VALUE for ints) instead.
 *
 * COMPLEXITY
 *   Time  O(n)      n rows: one pass to group, one pass over the same n scores
 *   Space O(m + n)  m distinct students plus every score held in the lists
 *
 * INTERVIEW FOLLOW-UPS
 *   - Drop the lists: keep running sum and count per student, O(m) space.
 *   - Return the student's name too, not just the value - track the argmax key.
 *   - Break ties between equal averages by name, or by number of scores.
 *   - Should the answer round, truncate toward zero, or floor? Ask before coding.
 *
 * RUN
 *   main() runs 5 cases (typical, single student, negative scores, empty,
 *   malformed row) and prints actual vs expected on one line each.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BestAverageGrade {

    public static Integer bestAverageGrade(String[][] inputScores) {
        if (inputScores.length == 0) return 0;

        // Step 1: group every score under its student.
        Map<String, List<Integer>> studentScoresMap = new HashMap<>();
        for (String[] scoreRow : inputScores) {
            if (scoreRow.length != 2) {
                return 0; // malformed row: the original contract returns 0 rather than throwing
            }
            String student = scoreRow[0];
            int score = Integer.parseInt(scoreRow[1]);

            studentScoresMap.putIfAbsent(student, new ArrayList<>());
            studentScoresMap.get(student).add(score);
        }

        // Step 2: reduce each bucket to its average and keep the largest.
        // NEGATIVE_INFINITY, not Double.MIN_VALUE - MIN_VALUE is a tiny POSITIVE
        // number, so it would beat every negative average.
        double maxAverage = Double.NEGATIVE_INFINITY;

        for (List<Integer> scores : studentScoresMap.values()) {
            int sum = 0;
            for (int score : scores) {
                sum += score;
            }
            double average = sum / (double) scores.size(); // cast first, else integer division
            maxAverage = Math.max(maxAverage, average);
        }
        return (int) Math.floor(maxAverage);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[][] typical = {{"Bobby", "87"}, {"Charles", "100"},
                              {"Eric", "64"}, {"Charles", "22"}};
        String[][] singleStudent = {{"Solo", "42"}};
        String[][] allNegative = {{"Ann", "-10"}, {"Ann", "-11"}};
        String[][] empty = {};
        String[][] malformed = {{"Bobby"}};

        print("case 1 typical", bestAverageGrade(typical), 87);
        print("case 2 single student", bestAverageGrade(singleStudent), 42);
        print("case 3 negative averages", bestAverageGrade(allNegative), -11);
        print("case 4 empty input (edge)", bestAverageGrade(empty), 0);
        print("case 5 malformed row", bestAverageGrade(malformed), 0);
    }
}
