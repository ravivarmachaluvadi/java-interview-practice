/**
 * Problem: Job Sequencing with deadlines. Each job is [id, deadline, profit]; a job takes one
 *          unit of time and must finish by its deadline. Pick and order jobs to maximise profit.
 *
 * Approaches:
 *   1. greedyWithSlotArray   - sort by profit desc, put each job in the latest free slot <= deadline.
 *                              O(n log n + n*D), D = max deadline.
 *   2. greedyWithDisjointSet - same greedy, but "latest free slot <= d" is answered by a DSU in
 *                              near O(1), so total is O(n log n + D). Worth knowing for large D.
 */
import java.util.Arrays;

class JobSequencing {

    /**
     * Shared step: order jobs by profit descending. When profits tie, earlier deadline first -
     * it does not change the total profit, but makes the chosen set deterministic and
     * matches the ordering most references (and interviewers) expect.
     */
    private static int[][] sortedByProfitDesc(int[][] jobs) {
        int[][] copy = Arrays.copyOf(jobs, jobs.length);
        Arrays.sort(copy, (a, b) -> a[2] != b[2]
                ? Integer.compare(b[2], a[2])   // profit desc
                : Integer.compare(a[1], b[1])); // deadline asc on tie
        return copy;
    }

    private static int maxDeadline(int[][] jobs) {
        int max = 0;
        for (int[] job : jobs) max = Math.max(max, job[1]);
        return max;
    }

    // ---------------------------------------------------------------- Approach 1: boolean slot array
    /**
     * Why "latest free slot": taking the latest slot keeps the earlier ones free for jobs
     * that have tighter deadlines and are still to come in the sorted order.
     */
    static int greedyWithSlotArray(int[][] jobs) {
        int[][] sorted = sortedByProfitDesc(jobs);
        boolean[] slotTaken = new boolean[maxDeadline(jobs) + 1]; // slots are 1..D
        int totalProfit = 0;

        for (int[] job : sorted) {
            for (int slot = job[1]; slot > 0; slot--) {   // scan backwards from the deadline
                if (!slotTaken[slot]) {
                    slotTaken[slot] = true;
                    totalProfit += job[2];
                    break;                                // a job is placed at most once
                }
            }
        }
        return totalProfit;
    }

    // ---------------------------------------------------------------- Approach 2: disjoint set
    /**
     * parent[s] = the largest free slot <= s (0 means "none left").
     * Placing a job in slot s unions s with s-1, so the next lookup for s skips straight to s-1
     * (or further, via path compression) without rescanning taken slots.
     */
    static int greedyWithDisjointSet(int[][] jobs) {
        int[][] sorted = sortedByProfitDesc(jobs);
        int d = maxDeadline(jobs);
        int[] parent = new int[d + 1];
        for (int i = 0; i <= d; i++) parent[i] = i;      // every slot initially free

        int totalProfit = 0;
        for (int[] job : sorted) {
            int freeSlot = find(parent, job[1]);
            if (freeSlot > 0) {                           // 0 = no free slot on or before deadline
                parent[freeSlot] = freeSlot - 1;          // "taking" the slot = point it at the previous one
                totalProfit += job[2];
            }
        }
        return totalProfit;
    }

    private static int find(int[] parent, int s) {
        if (parent[s] != s) parent[s] = find(parent, parent[s]); // path compression
        return parent[s];
    }

    // ---------------------------------------------------------------- main
    public static void main(String[] args) {
        int[][][] inputs = {
                // {id, deadline, profit}  -> expected 90 (job 1 in slot 4, job 3 in slot 1)
                {{1, 4, 50}, {2, 1, 10}, {3, 1, 40}, {4, 1, 30}},
                // -> expected 142 (100 in slot 2, 27 in slot 1, 15 in slot 3)
                {{1, 2, 100}, {2, 1, 19}, {3, 2, 27}, {4, 1, 25}, {5, 3, 15}}
        };

        for (int[][] jobs : inputs) {
            System.out.println("Jobs " + Arrays.deepToString(jobs));
            System.out.println("  slot array   : " + greedyWithSlotArray(jobs));
            System.out.println("  disjoint set : " + greedyWithDisjointSet(jobs));
        }
    }
}
