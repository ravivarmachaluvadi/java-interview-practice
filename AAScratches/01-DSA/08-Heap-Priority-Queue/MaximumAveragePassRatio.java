import java.util.PriorityQueue;

class MaximumAveragePassRatio {
    // Class representing a single class with pass & total, and able to compute gain
    private static class ClassInfo {
        int pass;
        int total;

        public ClassInfo(int p, int t) {
            this.pass = p;
            this.total = t;
        }

        // compute the gain if we add one extra student (who passes)
        public double gain() {
            // (pass+1)/(total+1) - pass/total
            return ((double) (pass + 1) / (total + 1)) - ((double) pass / total);
        }
    }

    public static double maxAverageRatio(int[][] classes, int extraStudents) {
        int n = classes.length;
        // max‐heap by gain
        PriorityQueue<ClassInfo> pq = new PriorityQueue<>(
                (c1, c2) -> Double.compare(c2.gain(), c1.gain())
        );

        // initialize heap
        for (int[] cl : classes) {
            pq.offer(new ClassInfo(cl[0], cl[1]));
        }

        // assign extra students one by one
        for (int i = 0; i < extraStudents; i++) {
            ClassInfo best = pq.poll();
            best.pass += 1;
            best.total += 1;
            pq.offer(best);
        }

        // compute final average pass ratio
        double sum = 0.0;
        while (!pq.isEmpty()) {
            ClassInfo ci = pq.poll();
            sum += (double) ci.pass / ci.total;
        }
        return sum / n;
    }

    // main method with example tests
    public static void main(String[] args) {
        // Example 1
        int[][] classes1 = {{1, 2}, {3, 5}, {2, 2}};
        int extra1 = 2;
        double result1 = maxAverageRatio(classes1, extra1);
        System.out.printf("Example 1 result = %.5f (expected ≈ 0.78333)%n", result1);

        // Example 2
        int[][] classes2 = {{2, 4}, {3, 9}, {4, 5}, {2, 10}};
        int extra2 = 4;
        double result2 = maxAverageRatio(classes2, extra2);
        System.out.printf("Example 2 result = %.5f (expected ≈ 0.53485)%n", result2);

        // You can add additional test cases below.
    }
}
