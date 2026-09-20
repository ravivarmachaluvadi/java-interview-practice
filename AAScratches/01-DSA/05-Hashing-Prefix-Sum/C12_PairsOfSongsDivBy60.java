/*
 * =====================================================================
 *  Pairs of Songs With Total Durations Divisible by 60   LeetCode 1010 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   time[i] is the length of song i in seconds. Count the pairs (i, j) with i < j such that
 *   (time[i] + time[j]) % 60 == 0. 1 <= time.length <= 6*10^4 and 1 <= time[i] <= 500,
 *   so the O(n^2) brute force is too slow.
 *
 * EXAMPLE
 *   time = [30, 20, 150, 100, 40]  ->  3    (30,150) (20,100) (20,40)
 *   time = [60, 60, 60]            ->  3    all rem 0, C(3,2) = 3 self-pairs
 *   time = [30, 30, 30, 30]        ->  6    all rem 30, C(4,2) = 6 self-pairs
 *   time = [10]                    ->  0    no pair possible
 *
 * APPROACH  (remainder bucket counting, two-sum on mod 60)
 *   1. Only t % 60 matters, so keep an int[60] of how many earlier songs had each remainder.
 *   2. For each song with remainder rem, the partner must have remainder (60 - rem) % 60.
 *      The % 60 turns "complement of 0" from 60 into 0.
 *   3. Add remCount[complement] to the answer (all earlier songs that pair with this one),
 *      then remCount[rem]++.
 *   Counting earlier songs only means each pair is counted exactly once with i < j.
 *
 * KEY INSIGHT
 *   This is Two Sum with the key mapped through mod 60: instead of "have I seen
 *   target - x", ask "how many x' with (x + x') % 60 == 0 have I seen". The two edge
 *   buckets rem 0 and rem 30 pair with THEMSELVES; the on-the-fly count handles that
 *   automatically because we add before incrementing (giving C(k,2) over the run).
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, O(1) work per song
 *   Space O(60)  fixed bucket array, effectively O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count pairs with sum divisible by any k: same code with int[k] (or a map if k is big).
 *   - Count first, then combine: sum over rem 1..29 of cnt[r]*cnt[60-r] plus C(cnt[0],2)
 *     and C(cnt[30],2). Same answer; be able to derive the self-pair cases explicitly.
 *   - Return the pairs themselves rather than a count: store index lists per bucket.
 *   - Why does the on-the-fly method never double count? Each pair is credited only when
 *     the later index is processed.
 *
 * RUN
 *   main() runs 4 cases (typical, rem 0 self-pairs, rem 30 self-pairs, single element)
 *   and prints actual vs expected.
 */
class PairsOfSongsDivBy60 {

    public static int numPairsDivisibleBy60(int[] time) {
        int[] remCount = new int[60]; // remCount[r] = how many earlier songs had t % 60 == r
        int pairs = 0;
        for (int t : time) {
            int rem = t % 60;
            int complement = (60 - rem) % 60; // %60 so that rem 0 pairs with rem 0, not 60
            pairs += remCount[complement];    // every earlier partner forms one new pair
            remCount[rem]++;
        }
        return pairs;
    }

    private static void print(String label, int[] time, int expected) {
        System.out.println(label + ": " + numPairsDivisibleBy60(time) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical         ", new int[]{30, 20, 150, 100, 40}, 3);
        print("case 2 rem 0 self-pairs", new int[]{60, 60, 60}, 3);
        print("case 3 rem 30 self-pair", new int[]{30, 30, 30, 30}, 6);
        print("case 4 single element  ", new int[]{10}, 0);
    }
}
