/*
 * =====================================================================
 *  Koko Eating Bananas                         LeetCode 875 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   piles[i] bananas sit in n piles; the guards return in h hours (h >= n). Each hour Koko
 *   picks one pile and eats up to k bananas from it (a pile smaller than k still costs the
 *   whole hour). Return the smallest integer speed k that finishes every pile within h hours.
 *
 * EXAMPLE
 *   piles = [3, 6, 7, 11], h = 8       ->  4     hours at k=4: 1+2+2+3 = 8, k=3 needs 10
 *   piles = [30, 11, 23, 4, 20], h = 5 ->  30    one pile per hour, so k = max pile
 *   piles = [30, 11, 23, 4, 20], h = 6 ->  23    one spare hour lets 30 be split in two
 *   piles = [1], h = 1                 ->  1     single pile, single hour
 *
 * APPROACH  (binary search on the answer: minimum feasible k)
 *   1. The answer lies in [1, max(piles)]: speed 1 always works given enough hours, and
 *      going faster than the largest pile cannot save any more hours.
 *   2. Write the predicate canFinish(k): sum of ceil(pile / k) over all piles <= h.
 *   3. The predicate is monotone: if k works, every larger k works too. So the answer
 *      space looks like F F F T T T and we want the first T.
 *   4. Binary search: if canFinish(mid) then right = mid (mid may be the answer), else
 *      left = mid + 1. Stop when left == right; that is the first T.
 *
 * KEY INSIGHT
 *   When the question is "the minimum X such that something is possible", do not search the
 *   input; search the range of possible answers with a yes/no feasibility check. Three parts
 *   to recite: define the bounds, write canDo(x), find the first true. Every problem in the
 *   Koko / ship-capacity / split-array family is this template with a different canDo.
 *
 * COMPLEXITY
 *   Time  O(n log M)  M = max pile; each of the log M probes scans all n piles
 *   Space O(1)        a handful of counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is left = 1 and not 0? Speed 0 never finishes, and it would divide by zero.
 *   - Why "right = mid" rather than "right = mid - 1"? Because mid itself may be the answer;
 *     the loop condition left < right guarantees termination.
 *   - Capacity To Ship Packages (LC 1011), Split Array Largest Sum (LC 410): same template,
 *     the predicate becomes a greedy partition simulation.
 *   - Fixed: the hour counter is now a long. With h and piles up to 1e9 and 1e4 piles, the
 *     total hours at speed 1 can exceed Integer.MAX_VALUE and wrap negative.
 *
 * RUN
 *   main() runs 4 cases (typical, k = max pile, one spare hour, single pile) and prints
 *   actual vs expected.
 */

class KokoEatingBananas {

    public static int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = 1;
        for (int pile : piles) {
            right = Math.max(right, pile); // faster than the biggest pile buys nothing
        }

        // Answer space is F F F T T T; find the first T.
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canFinish(piles, mid, h)) {
                right = mid;      // mid works, but a slower speed might too
            } else {
                left = mid + 1;   // mid is too slow
            }
        }
        return left;
    }

    /** True when eating at 'speed' bananas per hour clears every pile within h hours. */
    private static boolean canFinish(int[] piles, int speed, int h) {
        long hours = 0; // long: sum can exceed int range at low speeds with large piles
        for (int pile : piles) {
            hours += ceilDiv(pile, speed);
            if (hours > h) {
                return false; // early exit, the remaining piles only add more
            }
        }
        return true;
    }

    /** ceil(pile / speed) without the (pile + speed - 1) overflow risk. */
    private static int ceilDiv(int pile, int speed) {
        int hours = pile / speed;
        if (pile % speed != 0) {
            hours++;
        }
        return hours;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical",        minEatingSpeed(new int[]{3, 6, 7, 11}, 8), 4);
        print("case 2 h == n",         minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5), 30);
        print("case 3 one spare hour", minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 6), 23);
        print("case 4 single pile",    minEatingSpeed(new int[]{1}, 1), 1);
    }
}
