/*
 * =====================================================================
 *  P046 Heap-Driven Greedy Scheduling   Canonical LC 621 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 621, Task Scheduler)
 *   Tasks are letters; each takes one unit of time. Two runs of the same letter must be at
 *   least n units apart (idle units allowed). Return the minimum total time.
 *
 * EXAMPLE
 *   [A,A,A,B,B,B], n = 2                    ->  8     A B idle A B idle A B
 *   [A,A,A,B,B,B], n = 0                    ->  6
 *   [A,A,A,A,A,A,B,C,D,E,F,G], n = 2        ->  16
 *
 * RECOGNIZE WHEN
 *   - At every step you must pick "the best available option right now" (most frequent,
 *     biggest gain, earliest deadline), and options keep changing as time passes.
 *   - Deferred decisions: "use the ladder on the biggest climbs", "refuel at the best
 *     station you already passed" -- choose in hindsight from a heap of passed options.
 *   Not this if: one sort decides everything up front -> P096_SortAndPair or
 *   P049_SortByEndGreedy.
 *
 * TEMPLATE
 *   heap of available options, ordered by the greedy key
 *   for each step / event in time order:
 *       add newly available options to the heap
 *       take the best one (or the best k), apply it, re-insert what is left of it
 *   hindsight version: push every passed option; when you are stuck, pop the best one
 *   you could have used
 *
 * APPROACH
 *   1. Count tasks; a max-heap holds the remaining counts.
 *   2. Each cycle of n + 1 slots runs up to n + 1 DIFFERENT tasks, most frequent first.
 *   3. Put back the reduced counts. A cycle is n + 1 long unless the heap is empty after it.
 *
 * KEY INSIGHT
 *   Always run the task with the most copies left, because it is the one most likely to
 *   force idle time later. The heap answers "which is most urgent now?" in O(log k). The
 *   closed form (maxCount - 1) * (n + 1) + (number of tasks with maxCount), floored at the
 *   task count, is the same greedy counted directly.
 *
 * COMPLEXITY
 *   Time O(T log 26) = O(T) for T tasks, space O(26).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 621 formula                   (maxCount - 1) * (n + 1) + numberWithMax
 *   [coded] LC 767  Reorganize String        pop the two most frequent letters per step
 *   [coded] LC 1642 Furthest Building        min-heap of climbs on ladders; when it holds
 *                                            more than `ladders`, pay its smallest in bricks
 *   [coded] LC 871  Min Refueling Stops      max-heap of fuel at passed stations; refuel
 *                                            from the best one only when you run dry
 *           LC 1353 Max Events Attended      by day: add starting events, attend the one
 *                                            ending soonest (min-heap of end days)
 *           LC 630  Course Schedule III      sort by deadline; max-heap of durations; drop
 *                                            the longest course when over time
 *           LC 1834 Single-Threaded CPU      events by enqueue time; heap by (time, index)
 *           LC 2402 Meeting Rooms III        heap of free rooms + heap of busy rooms
 *
 * PITFALLS
 *   - LC 621: the answer is at least tasks.length (no idling needed when many letters).
 *   - LC 767: impossible when the top count > (n + 1) / 2; return "".
 *   - "Hindsight" greedy: pop from the heap only when forced, not at every step.
 *
 * DEEP DIVE
 *   C04_ReorganizeString, C07_FurthestBuildingYouCanReach, D03_MinimumRefuelingStops,
 *   C06_MaximumNumberofEventsThatCanBeAttended (08-Heap-Priority-Queue)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class HeapSchedulingGreedy {

    // Canonical LC 621, simulated with a max-heap of remaining counts.
    static int leastInterval(char[] tasks, int n) {
        int[] count = new int[26];
        for (char t : tasks) {
            count[t - 'A']++;
        }
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> b - a);
        for (int c : count) {
            if (c > 0) {
                heap.offer(c);
            }
        }
        int time = 0;
        while (!heap.isEmpty()) {
            List<Integer> leftover = new ArrayList<>();
            int slots = 0;
            while (slots < n + 1 && !heap.isEmpty()) {
                int c = heap.poll();
                if (c > 1) {
                    leftover.add(c - 1);
                }
                slots++;
            }
            heap.addAll(leftover);
            time += heap.isEmpty() ? slots : n + 1;   // the last cycle needs no idle tail
        }
        return time;
    }

    // LC 621 closed form.
    static int leastIntervalFormula(char[] tasks, int n) {
        int[] count = new int[26];
        int max = 0;
        for (char t : tasks) {
            max = Math.max(max, ++count[t - 'A']);
        }
        int withMax = 0;
        for (int c : count) {
            if (c == max) {
                withMax++;
            }
        }
        return Math.max(tasks.length, (max - 1) * (n + 1) + withMax);
    }

    // LC 767: no two equal neighbours; "" if impossible.
    static String reorganizeString(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        PriorityQueue<Integer> heap = new PriorityQueue<>(
                (a, b) -> count[a] != count[b] ? count[b] - count[a] : a - b);
        for (int i = 0; i < 26; i++) {
            if (count[i] > 0) {
                heap.offer(i);
            }
        }
        StringBuilder sb = new StringBuilder();
        while (heap.size() >= 2) {
            int a = heap.poll();
            int b = heap.poll();
            sb.append((char) ('a' + a)).append((char) ('a' + b));
            if (--count[a] > 0) {
                heap.offer(a);
            }
            if (--count[b] > 0) {
                heap.offer(b);
            }
        }
        if (!heap.isEmpty()) {
            int last = heap.poll();
            if (count[last] > 1) {
                return "";
            }
            sb.append((char) ('a' + last));
        }
        return sb.toString();
    }

    // LC 1642: ladders go to the largest climbs seen so far, bricks pay for the rest.
    static int furthestBuilding(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> climbsOnLadders = new PriorityQueue<>();
        for (int i = 0; i + 1 < heights.length; i++) {
            int climb = heights[i + 1] - heights[i];
            if (climb <= 0) {
                continue;
            }
            climbsOnLadders.offer(climb);
            if (climbsOnLadders.size() > ladders) {
                bricks -= climbsOnLadders.poll();     // the smallest climb gets bricks
                if (bricks < 0) {
                    return i;
                }
            }
        }
        return heights.length - 1;
    }

    // LC 871: stations[i] = {position, fuel}.
    static int minRefuelStops(int target, int startFuel, int[][] stations) {
        PriorityQueue<Integer> passed = new PriorityQueue<>((a, b) -> b - a);
        long reach = startFuel;
        int stops = 0;
        int i = 0;
        while (reach < target) {
            while (i < stations.length && stations[i][0] <= reach) {
                passed.offer(stations[i++][1]);
            }
            if (passed.isEmpty()) {
                return -1;
            }
            reach += passed.poll();                   // refuel in hindsight at the best one
            stops++;
        }
        return stops;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        char[] six = "AAABBB".toCharArray();
        check("LC 621 heap n=2", leastInterval(six, 2), 8);
        check("LC 621 heap n=0", leastInterval(six, 0), 6);
        check("LC 621 heap n=2 many letters", leastInterval("AAAAAABCDEFG".toCharArray(), 2), 16);
        check("LC 621 formula n=2", leastIntervalFormula(six, 2), 8);
        check("LC 621 formula no idling", leastIntervalFormula("ACABDB".toCharArray(), 1), 6);

        check("LC 767 aab", reorganizeString("aab"), "aba");
        check("LC 767 aaab impossible", "[" + reorganizeString("aaab") + "]", "[]");
        check("LC 767 vvvlo", reorganizeString("vvvlo"), "vlvov");

        check("LC 1642 b=5 l=1", furthestBuilding(new int[]{4, 2, 7, 6, 9, 14, 12}, 5, 1), 4);
        check("LC 1642 b=10 l=2",
                furthestBuilding(new int[]{4, 12, 2, 7, 3, 18, 20, 3, 19}, 10, 2), 7);
        check("LC 1642 b=17 l=0", furthestBuilding(new int[]{14, 3, 19, 3}, 17, 0), 3);

        check("LC 871 already there", minRefuelStops(1, 1, new int[][]{}), 0);
        check("LC 871 cannot reach", minRefuelStops(100, 1, new int[][]{{10, 100}}), -1);
        check("LC 871 two stops",
                minRefuelStops(100, 10, new int[][]{{10, 60}, {20, 30}, {30, 30}, {60, 40}}), 2);
    }
}
