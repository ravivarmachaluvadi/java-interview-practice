/*
 * =====================================================================
 *  Maximum Population Year                            LeetCode 1854 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   logs[i] = [birth, death] means a person is alive from year birth through
 *   year death - 1 inclusive (the death year does NOT count). Return the earliest
 *   year with the largest population. Constraints: 1 <= logs.length <= 100 and
 *   1950 <= birth < death <= 2050, so the timeline is only 101 years wide.
 *
 * EXAMPLE
 *   [[1993,1999],[2000,2010]]              ->  1993  both years peak at 1, take first
 *   [[1950,1961],[1960,1971],[1970,1981]]  ->  1960  two people overlap in 1960
 *   [[2005,2020],[2000,2015],[2010,2015]]  ->  2010  three alive from 2010
 *   [[1950,1951]]                          ->  1950  single person, single year
 *
 * APPROACH 1  (difference array over the bounded year range)  maximumPopulation
 *   1. Allocate delta[101], where index y means the year 1950 + y.
 *   2. For each log do delta[birth - 1950]++ and delta[death - 1950]--.
 *   3. Sweep left to right accumulating a running sum; that sum is the population
 *      alive in that year.
 *   4. Track the first year whose running sum is strictly greater than the best
 *      seen, which automatically returns the EARLIEST peak year.
 *
 * APPROACH 2  (sorted two-pointer sweep)  maximumPopulationV2
 *   1. Split the logs into a births array and a deaths array and sort each one.
 *   2. Walk both with two pointers, handling the earlier event first: a birth
 *      strictly before the next death increments, otherwise a death decrements.
 *   3. Record the year whenever the live count sets a new maximum.
 *   This is exactly the Minimum Platforms sweep (A03) with the years as times.
 *
 * KEY INSIGHT
 *   A person alive over [birth, death) is a +1 at birth and a -1 at death, so the
 *   population in any year is a PREFIX SUM of those deltas. When the domain is
 *   small and bounded, bucket the deltas into an array and the whole problem
 *   becomes O(range) with no sorting at all; when it is not bounded, sort the
 *   events instead. Comparing births[i] < deaths[j] with a strict < is what makes
 *   a death in year d land before a birth in year d, matching the half-open rule.
 *
 * COMPLEXITY
 *   Time  approach 1 O(n + R), R = 101 years   approach 2 O(n log n) for the sorts
 *   Space approach 1 O(R) for the delta array  approach 2 O(n) for the two arrays
 *
 * INTERVIEW FOLLOW-UPS
 *   - Years unbounded (say any long): use approach 2, or a TreeMap of deltas.
 *   - Return the whole peak range, not just the first year of it.
 *   - Each log carries a household size: weight the deltas (Car Pooling, C05).
 *   - Answer many "population in year Y" queries: precompute the prefix sums once.
 *
 * RUN
 *   main() runs 5 cases (three LeetCode samples, a single log, a tie) through both
 *   methods and prints actual vs expected.
 */

import java.util.Arrays;

class MaximumPopulationYear {

    /**
     * Approach 1: bucket +1 / -1 deltas into a 101-slot timeline, then prefix sum.
     */
    public static int maximumPopulation(int[][] logs) {
        // Index i of the array stands for the year 1950 + i; deaths at 2050 land
        // on index 100, so 101 slots is exactly enough.
        int[] delta = new int[101];

        for (int[] log : logs) {
            delta[log[0] - 1950]++; // born: alive from this year on
            delta[log[1] - 1950]--; // died: no longer alive from this year on
        }

        int maxPopulation = 0;
        int bestYearOffset = 0;
        int currentPopulation = 0;
        for (int i = 0; i < delta.length; i++) {
            currentPopulation += delta[i]; // running prefix sum == people alive in year i
            // Strictly greater, so the EARLIEST year wins any tie.
            if (currentPopulation > maxPopulation) {
                maxPopulation = currentPopulation;
                bestYearOffset = i;
            }
        }
        return bestYearOffset + 1950;
    }

    /**
     * Approach 2: the Minimum Platforms sweep - sort births and deaths separately
     * and walk them in chronological order. Works for unbounded year ranges too.
     */
    public static int maximumPopulationV2(int[][] logs) {
        if (logs.length == 0) {
            return -1; // no data; the LeetCode constraints forbid this input
        }

        int[] births = new int[logs.length];
        int[] deaths = new int[logs.length];
        for (int i = 0; i < logs.length; i++) {
            births[i] = logs[i][0];
            deaths[i] = logs[i][1];
        }

        Arrays.sort(births);
        Arrays.sort(deaths);

        int maxPopulation = 0;
        int currentPopulation = 0;
        int maxYear = births[0];

        int i = 0; // next birth
        int j = 0; // next death
        // The peak can only be reached on a birth, so stopping when the births
        // run out is safe - the deaths left over only shrink the population.
        while (i < logs.length) {
            if (births[i] < deaths[j]) {
                // Strict <: someone dying in year d is already gone when another
                // person is born in year d, so deaths at the same year go first.
                currentPopulation++;
                if (currentPopulation > maxPopulation) {
                    maxPopulation = currentPopulation;
                    maxYear = births[i];
                }
                i++;
            } else {
                currentPopulation--;
                j++;
            }
        }
        return maxYear;
    }

    private static void print(String label, int[][] logs, int expected) {
        System.out.println(label
                + ": delta=" + maximumPopulation(logs)
                + " sweep=" + maximumPopulationV2(logs)
                + "   expected " + expected + " from both");
    }

    public static void main(String[] args) {
        // typical: two disjoint lives, both years peak at 1, earliest wins
        print("case 1", new int[][]{{1993, 1999}, {2000, 2010}}, 1993);

        // typical: a chain where exactly two lives overlap
        print("case 2", new int[][]{{1950, 1961}, {1960, 1971}, {1970, 1981}}, 1960);

        // typical: three people alive from 2010
        print("case 3", new int[][]{{2005, 2020}, {2000, 2015}, {2010, 2015}}, 2010);

        // edge: a single person alive for exactly one year, at the range start
        print("case 4", new int[][]{{1950, 1951}}, 1950);

        // tricky: a death and a birth in the same year must not stack to 2
        print("case 5", new int[][]{{1950, 1960}, {1960, 1970}}, 1950);
    }
}
