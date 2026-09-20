/*
 * =====================================================================
 *  Maximum Average Pass Ratio                        LeetCode 1792 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   classes[i] = [pass, total]. You have extraStudents brilliant students who will
 *   pass any class you put them in. Assign all of them so that the average of
 *   pass/total across all classes is as large as possible. Answers within 1e-5
 *   of the true value are accepted.
 *
 * EXAMPLE
 *   classes = [[1,2],[3,5],[2,2]], extra = 2  ->  0.78333
 *     both extras go to [1,2]:  (3/4 + 3/5 + 2/2) / 3
 *   classes = [[2,4],[3,9],[4,5],[2,10]], extra = 4  ->  0.53485
 *   classes = [[2,2],[1,1]], extra = 3  ->  1.00000   full classes cannot improve
 *   classes = [[1,4]], extra = 0  ->  0.25000          nothing to assign
 *
 * APPROACH  (max-heap keyed by marginal gain)
 *   1. For each class compute gain = (pass+1)/(total+1) - pass/total: how much the
 *      ratio improves if one more passing student joins.
 *   2. Put every class in a max-heap ordered by that gain.
 *   3. For each extra student: poll the class with the largest gain, add the
 *      student (pass++, total++), and re-offer it. Its gain is now smaller, so it
 *      sinks and another class may come to the top.
 *   4. Sum pass/total over all classes and divide by the number of classes.
 *
 * KEY INSIGHT
 *   For one class, each added student gives a smaller gain than the previous one
 *   (diminishing returns), so taking the single best marginal gain each time is
 *   globally optimal. The heap key CHANGES after each pick, so the item has to
 *   leave and re-enter: poll, mutate, re-offer. Never mutate an object's sort key
 *   while it is still inside a PriorityQueue; the heap will silently go stale.
 *
 * COMPLEXITY
 *   Time  O((n + extra) log n)  n initial offers, then extra poll/offer pairs
 *   Space O(n)                  one heap entry per class
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not the class with the lowest ratio? [1,10] gains 0.082, [1,2] gains 0.167.
 *   - Store gain as a field so the comparator does not recompute two divisions per compare.
 *   - n and extra both up to 1e5: this is fine; anything O(n * extra) times out.
 *
 * RUN
 *   main() runs 4 cases and prints actual vs expected to 5 decimals.
 */

import java.util.PriorityQueue;

class MaximumAveragePassRatio {

    /** One class: how many pass, how many total, and the gain from one more passing student. */
    private static class ClassInfo {
        int pass;
        int total;

        ClassInfo(int pass, int total) {
            this.pass = pass;
            this.total = total;
        }

        // (pass+1)/(total+1) - pass/total : improvement from adding one passing student
        double gain() {
            return ((double) (pass + 1) / (total + 1)) - ((double) pass / total);
        }
    }

    public static double maxAverageRatio(int[][] classes, int extraStudents) {
        // max-heap by marginal gain
        PriorityQueue<ClassInfo> byGain = new PriorityQueue<>(
                (c1, c2) -> Double.compare(c2.gain(), c1.gain())
        );
        for (int[] cl : classes) {
            byGain.offer(new ClassInfo(cl[0], cl[1]));
        }

        // hand out students one at a time, always to the class that improves most
        for (int i = 0; i < extraStudents; i++) {
            ClassInfo best = byGain.poll();
            best.pass++;
            best.total++;
            byGain.offer(best); // its gain shrank, so it must be re-positioned
        }

        double sum = 0.0;
        for (ClassInfo ci : byGain) {
            sum += (double) ci.pass / ci.total;
        }
        return sum / classes.length;
    }

    private static void print(String label, int[][] classes, int extra, String expected) {
        double actual = maxAverageRatio(classes, extra);
        System.out.println(label + ": " + String.format("%.5f", actual)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", new int[][]{{1, 2}, {3, 5}, {2, 2}}, 2, "0.78333");
        print("case 2 typical", new int[][]{{2, 4}, {3, 9}, {4, 5}, {2, 10}}, 4, "0.53485");
        print("case 3 all full", new int[][]{{2, 2}, {1, 1}}, 3, "1.00000");
        print("case 4 no extras", new int[][]{{1, 4}}, 0, "0.25000");
    }
}
