/*
 * =====================================================================
 *  Student Grade From Marks                                Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a student's marks out of 100, return the letter grade using fixed
 *   cut-offs: A >= 90, B >= 70, C >= 50, D >= 35, otherwise "Fail".
 *   Marks are assumed to be in 0..100; anything below 35 fails.
 *
 * EXAMPLE
 *   marks = 85   ->  "Grade B"    85 is below 90 but at or above 70
 *   marks = 90   ->  "Grade A"    boundary value belongs to the higher band
 *   marks = 34   ->  "Fail"       falls through every threshold
 *   marks = 0    ->  "Fail"       lowest legal input, the edge case main() runs
 *
 * APPROACH  (ordered threshold ladder)
 *   1. Order the thresholds from highest to lowest: 90, 70, 50, 35.
 *   2. Walk down the ladder with else-if. The first test that succeeds wins,
 *      so each branch only has to state its own lower bound.
 *   3. The trailing else is the catch-all for everything under the last bound.
 *   4. Return the label instead of printing it, so main() can assert on it.
 *
 * KEY INSIGHT
 *   Because the ladder is ordered top-down and uses else-if, a branch never has
 *   to write "marks >= 70 && marks < 90" - the earlier branch already excluded
 *   the upper part of the range. Descending order plus else-if removes every
 *   upper bound. Reverse the order and the ladder silently collapses to one
 *   branch, which is the classic bug an interviewer plants here.
 *
 * COMPLEXITY
 *   Time  O(1)  at most four comparisons, independent of input size
 *   Space O(1)  no allocation beyond the returned label
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make the bands data-driven: an array of {cutoff, label} scanned in order.
 *   - Many bands, many lookups - use a TreeMap and floorEntry(marks) for O(log n).
 *   - Validate input: what should marks = -5 or marks = 120 do, throw or clamp?
 *   - Change to grade-on-a-curve: cut-offs become percentiles of the cohort.
 *
 * RUN
 *   main() runs 6 cases (typical, every boundary, and the low edge) and prints
 *   actual vs expected on one line each.
 */
class StudentGrade {

    /** Returns the letter grade for a mark out of 100 using descending cut-offs. */
    public String studentGrade(int marks) {
        // Ordered high to low: the first match wins, so no upper bounds are needed.
        if (marks >= 90) {
            return "Grade A";
        } else if (marks >= 70) {
            return "Grade B";
        } else if (marks >= 50) {
            return "Grade C";
        } else if (marks >= 35) {
            return "Grade D";
        } else {
            return "Fail";
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        StudentGrade obj = new StudentGrade();

        print("case 1 marks=85 (typical)", obj.studentGrade(85), "Grade B");
        print("case 2 marks=90 (boundary)", obj.studentGrade(90), "Grade A");
        print("case 3 marks=70 (boundary)", obj.studentGrade(70), "Grade B");
        print("case 4 marks=50 (boundary)", obj.studentGrade(50), "Grade C");
        print("case 5 marks=34 (just below pass)", obj.studentGrade(34), "Fail");
        print("case 6 marks=0  (edge)", obj.studentGrade(0), "Fail");
    }
}
