/*
 * =====================================================================
 *  Gas Station                              LeetCode 134 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   n gas stations sit on a circle. Station i holds gas[i] fuel, and driving
 *   from station i to i+1 burns cost[i]. You start with an empty tank at some
 *   station and must complete the full loop, never letting the tank go negative.
 *   Return a valid starting index, or -1 if no start works. If one exists it is
 *   unique, so any valid index is THE answer.
 *
 * EXAMPLE
 *   gas = [1,2,3,4,5], cost = [3,4,5,1,2]  ->  3   start at 3, tank never dips
 *   gas = [2,3,4],     cost = [3,4,3]      ->  -1  total gas 9 < total cost 10
 *   gas = [5],         cost = [4]          ->  0   single station, 5 >= 4
 *   gas = [3,1,1],     cost = [1,2,2]      ->  0   surplus +2, -1, -1 survives
 *
 * APPROACH  (running sum with reset, plus one global feasibility check)
 *   Work with the per-station surplus gas[i] - cost[i].
 *   1. Accumulate totalSurplus over every station. If it ends negative, the loop
 *      is impossible from ANY start - the circle simply lacks fuel. Return -1.
 *   2. Separately carry fuelLeftInTank, the surplus since the current candidate
 *      start. Whenever it goes negative at station i, the candidate start (and
 *      every station between it and i) fails, so set candidate = i + 1 and reset
 *      the tank to 0.
 *   3. If step 1 says a solution exists, the last surviving candidate is it.
 *
 * KEY INSIGHT
 *   Two independent arguments in one pass, and you must be able to state both.
 *   (a) EXISTENCE: total gas >= total cost is necessary, and on a circle it is
 *       also sufficient, so it alone decides between "an answer" and -1.
 *   (b) THE RESET IS SAFE: if the tank dies somewhere between start s and station
 *       i, then no station in (s, i] can be the answer either. Any such station t
 *       was entered with a non-negative tank, so starting at t from empty gives a
 *       prefix that is no better - it dies at i too. That lets you skip the whole
 *       block instead of restarting the scan at s+1, turning O(n^2) into O(n).
 *   Pattern: "scan, and when the running value goes bad, discard the entire
 *   prefix" - the same discard argument as Kadane's maximum subarray.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass; the reset skips candidates instead of rescanning them
 *   Space O(1)  four running integers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the reset: why can no station strictly inside the failed block work?
 *   - Why is total gas >= total cost sufficient, not just necessary, on a circle?
 *   - Return ALL valid starts (uniqueness dropped) - what changes?
 *   - Overflow: with values near Integer.MAX_VALUE, which sums need long?
 *
 * RUN
 *   main() runs 4 cases (typical, impossible, single station, tight loop) and
 *   prints actual vs expected.
 */
class GasStation {

    /**
     * Returns a starting index that completes the circuit, or -1 if none exists.
     */
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalSurplus = 0;       // surplus over the whole circle -> decides feasibility
        int fuelLeftInTank = 0;     // surplus since the current candidate start
        int startingIndex = 0;      // current candidate

        for (int i = 0; i < gas.length; i++) {
            int surplus = gas[i] - cost[i];
            totalSurplus += surplus;
            fuelLeftInTank += surplus;

            // Tank died on the leg out of station i: the candidate start and every
            // station up to i are all dead ends, so jump the candidate past i.
            if (fuelLeftInTank < 0) {
                startingIndex = i + 1;
                fuelLeftInTank = 0;
            }
        }
        // Enough fuel on the circle as a whole => the surviving candidate is valid.
        return totalSurplus >= 0 ? startingIndex : -1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: the answer sits after the worst deficit
        print("case 1", canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}), 3);

        // impossible: total gas 9 < total cost 10
        print("case 2", canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3}), -1);

        // edge: a single station that can just afford its own leg
        print("case 3", canCompleteCircuit(new int[]{5}, new int[]{4}), 0);

        // tricky: index 0 works even though the tank dips to exactly 0 at the end
        print("case 4", canCompleteCircuit(new int[]{3, 1, 1}, new int[]{1, 2, 2}), 0);
    }
}
