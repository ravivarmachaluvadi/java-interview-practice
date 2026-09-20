/*
 * =====================================================================
 *  Find the Celebrity                              LeetCode 277 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   In a party of n people (0..n-1) a "celebrity" is someone that everybody
 *   else knows, and who knows nobody. There is at most one. You cannot see the
 *   relationships; you may only ask the oracle knows(a, b) -> "does a know b?".
 *   Return the celebrity's index, or -1 if there is none, using as few calls
 *   to knows() as you can.
 *
 * EXAMPLE
 *   n = 4, rows = who-knows-whom, 1 = knows:
 *      0: 0 1 1 0      2 knows nobody and 0, 1, 3 all know 2   ->  2
 *      1: 0 0 1 0
 *      2: 0 0 0 0
 *      3: 0 1 1 0
 *   n = 3, {{0,1,0},{1,0,0},{0,0,0}}  -> -1   (0 and 1 know each other)
 *   n = 2, {{0,1},{1,0}}              -> -1   (mutual acquaintances)
 *   n = 1, {{0}}                      ->  0   (knows nobody, vacuously known by all)
 *
 * APPROACH  (candidate elimination, then verify)
 *   1. Hold a single candidate, starting at person 0.
 *   2. Sweep i = 1..n-1 asking knows(candidate, i):
 *        - true  -> candidate knows somebody, so candidate is out; i becomes
 *                   the new candidate.
 *        - false -> i is not known by candidate, so i is out; candidate stays.
 *      Either way exactly one person is eliminated per question, so after n-1
 *      questions only one person can still possibly be the celebrity.
 *   3. Step 2 proves nothing on its own - it only narrows. Verify the survivor
 *      with a full pass: for every other i, knows(candidate, i) must be false
 *      AND knows(i, candidate) must be true. Any violation -> return -1.
 *
 * KEY INSIGHT
 *   One knows() question always kills exactly one person, which is what turns
 *   an O(n^2) scan into O(n). This is a dense-graph problem solved with no
 *   traversal at all: with n^2 possible edges, building the graph is already
 *   more expensive than the answer. The verify pass is not optional - phase 1
 *   finds the only *possible* celebrity, never a *proven* one.
 *   Cost: n-1 calls to narrow plus at most 2(n-1) to verify, so <= 3(n-1).
 *
 * COMPLEXITY
 *   Time  O(n)   n-1 elimination calls + up to 2(n-1) verification calls
 *   Space O(1)   one candidate index; the matrix here is just the test oracle
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove at most one celebrity exists: two of them would have to know each
 *     other, contradicting "knows nobody".
 *   - Stack variant: push everyone, pop two, discard the one that cannot be the
 *     celebrity, repeat. Same O(n), more space.
 *   - What if knows() is expensive/remote? Cache answers; the verify pass can
 *     reuse the n-1 answers phase 1 already paid for.
 *   - Variant: several celebrities allowed, or "knows" is not transitive.
 *
 * FIXED
 *   knows() was a stub returning false, so the demo always printed
 *   "no celebrity". It is now backed by a real knows-matrix passed in, and
 *   main() exercises four different parties.
 *
 * RUN
 *   main() runs 4 cases (celebrity present, none, mutual pair, single person)
 *   through both the O(n) elimination and an O(n^2) brute force, printing
 *   actual vs expected on each line.
 */

class Celebrity {

    /** knowsMatrix[a][b] == 1 means "a knows b". Stands in for the interviewer's oracle. */
    private final int[][] knowsMatrix;

    Celebrity(int[][] knowsMatrix) {
        this.knowsMatrix = knowsMatrix;
    }

    /** The only allowed question. In the real problem this is a black box. */
    public boolean knows(int a, int b) {
        return knowsMatrix[a][b] == 1;
    }

    public int findCelebrity(int n) {
        int candidate = 0;

        // Step 1: narrow to one candidate. Each question eliminates exactly one person.
        for (int i = 1; i < n; i++) {
            if (knows(candidate, i)) {
                candidate = i;   // candidate knows someone, so candidate is not the celebrity
            }
            // else: candidate does not know i, so i is not known by everyone -> i is out
        }

        // Step 2: the survivor is only a *possibility* until fully verified.
        for (int i = 0; i < n; i++) {
            if (i == candidate) continue;
            if (knows(candidate, i) || !knows(i, candidate)) {
                return -1;   // candidate knows someone, or someone does not know candidate
            }
        }
        return candidate;
    }

    /** O(n^2) reference: check every person against the definition directly. */
    public int findCelebrityBruteForce(int n) {
        for (int person = 0; person < n; person++) {
            boolean isCelebrity = true;
            for (int other = 0; other < n && isCelebrity; other++) {
                if (other == person) continue;
                if (knows(person, other) || !knows(other, person)) isCelebrity = false;
            }
            if (isCelebrity) return person;
        }
        return -1;
    }

    private static void runCase(String label, int[][] matrix, int expected) {
        Celebrity party = new Celebrity(matrix);
        int n = matrix.length;
        System.out.println(label + ": elimination=" + party.findCelebrity(n)
                + ", bruteForce=" + party.findCelebrityBruteForce(n)
                + "   expected " + expected + " for both");
    }

    public static void main(String[] args) {
        runCase("celebrity is 2", new int[][]{
                {0, 1, 1, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 0},
                {0, 1, 1, 0}
        }, 2);

        runCase("no celebrity  ", new int[][]{
                {0, 1, 0},
                {1, 0, 0},
                {0, 0, 0}
        }, -1);

        runCase("mutual pair   ", new int[][]{
                {0, 1},
                {1, 0}
        }, -1);

        runCase("single person ", new int[][]{
                {0}
        }, 0);
    }
}
