
// 1010. Pairs of Songs With Total Durations Divisible by 60
// https://leetcode.com/problems/pairs-of-songs-with-total-durations-divisible-by-60/description/

/**
 * Constraints:
 * <p>
 * 1 <= time.length <= 6 * 104
 * <p>
 * 1 <= time[i] <= 500
 */
class PairsOfSongsDivBy60 {
    // method to compute number of pairs
    public static int numPairsDivisibleBy60(int[] time) {
        // counts of remainders mod 60
        int[] remCount = new int[60];
        int result = 0;
        for (int t : time) {
            int rem = t % 60;
            int complement = (60 - rem) % 60;  // %60 so that rem=0 → complement=0
            result += remCount[complement];
            remCount[rem]++;
        }
        return result;
    }

    // main method with example
    public static void main(String[] args) {
        int[] time1 = {30, 20, 150, 100, 40};
        System.out.println("Example 1 result: " + numPairsDivisibleBy60(time1));
        // Explanation: The pairs are (30,150), (20,100) and (20,40) → total 3

        int[] time2 = {60, 60, 60};
        System.out.println("Example 2 result: " + numPairsDivisibleBy60(time2));
        // Explanation: Each song 60 has remainder 0, so pairs = C(3,2) = 3
    }
}
