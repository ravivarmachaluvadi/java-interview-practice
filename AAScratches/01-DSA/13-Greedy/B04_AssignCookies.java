/*
 * =====================================================================
 *  Assign Cookies                                     LeetCode 455 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   greed[i] is the smallest cookie size that will satisfy child i. cookieSize[j] is the
 *   size of cookie j. Each child gets at most one cookie and each cookie goes to at most
 *   one child; child i is content if the cookie they get has size >= greed[i]. Return the
 *   maximum number of content children.
 *
 *   Fixed: the original file had a stray character after the method's closing brace, so it
 *   did not compile. The algorithm itself was correct.
 *
 * EXAMPLE
 *   greed = [1,5,3,3,4], cookies = [4,2,1,2,1,3]  ->  3   (1<-1, 3<-3, 3<-4)
 *   greed = [1,2,3],     cookies = [1,1]          ->  1   only the greed-1 child is fed
 *   greed = [1,2],       cookies = []             ->  0   no cookies at all (edge case)
 *   greed = [10,9,8],    cookies = [5,6,7]        ->  0   every cookie is too small
 *
 * APPROACH  (sort both, two pointers advanced together)
 *   1. Sort greed ascending and cookieSize ascending.
 *   2. Point child at the least greedy child and cookie at the smallest cookie.
 *   3. If the current cookie fits the current child, hand it over and advance BOTH.
 *      Otherwise the cookie is too small for even the least greedy child left, so it is
 *      useless to everyone - advance only the cookie pointer and throw it away.
 *   4. Stop when either list runs out; child is the answer.
 *
 * KEY INSIGHT
 *   Feed the least greedy child with the smallest cookie that satisfies them. Spending a
 *   bigger cookie on that child can only take away a cookie some other child needed, so
 *   the smallest fitting cookie is never worse - the exchange argument. Equivalently: a
 *   cookie too small for the neediest-remaining-threshold child is too small for everyone
 *   still unserved, which is exactly why it is safe to discard it on the spot.
 *   Pattern: two sorted sequences walked by one pointer each, advancing the one that
 *   cannot possibly be used later.
 *
 * COMPLEXITY
 *   Time  O(n log n + m log m)  the two sorts dominate; the match pass is O(n + m)
 *   Space O(1) auxiliary if you sort in place (Java's dual-pivot quicksort on int[] is
 *              in place, so no extra array beyond the recursion stack)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not match the greediest child first? It works too, walking both arrays from the
 *     right; state which end you are sweeping and stay consistent.
 *   - Maximise total satisfaction (sum of cookie sizes handed out), not the head count -
 *     that changes the objective and the greedy with it.
 *   - Streaming cookies that arrive one at a time: keep the children in a TreeMap and
 *     serve the largest greed the arriving cookie can cover.
 *   - Point out that sorting mutates the caller's arrays if they are reused afterwards.
 *
 * RUN
 *   main() runs 4 cases (typical, short supply, no cookies, all too small) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class AssignCookies {

    /** NOTE: sorts both input arrays in place. */
    public static int findContentChildren(int[] greed, int[] cookieSize) {
        Arrays.sort(greed);
        Arrays.sort(cookieSize);

        int child = 0;    // index of the least greedy child not yet fed
        int cookie = 0;   // index of the smallest cookie not yet used

        while (child < greed.length && cookie < cookieSize.length) {
            if (greed[child] <= cookieSize[cookie]) {
                child++;      // this cookie satisfies them; move on to the next child
            }
            // either way the cookie is spent: it was used, or it fits nobody who is left
            cookie++;
        }
        return child;         // children fed so far
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] greeds = {{1, 5, 3, 3, 4}, {1, 2, 3}, {1, 2}, {10, 9, 8}};
        int[][] cookies = {{4, 2, 1, 2, 1, 3}, {1, 1}, {}, {5, 6, 7}};
        int[] expected = {3, 1, 0, 0};

        for (int i = 0; i < greeds.length; i++) {
            String label = "case " + (i + 1) + " greed " + Arrays.toString(greeds[i])
                    + ", cookies " + Arrays.toString(cookies[i]);
            // clone: findContentChildren sorts whatever it is handed
            print(label, findContentChildren(greeds[i].clone(), cookies[i].clone()), expected[i]);
        }
    }
}
