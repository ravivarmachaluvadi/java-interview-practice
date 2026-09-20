/*
 * =====================================================================
 *  GeneralUtil - random key generator          Spring project helper | header-only
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   One static helper in the order-management (gom) codebase: return a pseudo-random
 *   integer inside [lowerBound, upperBound], already converted to a String.
 *
 *   Its only caller in this folder is WorkFlowExecutor.submitTask(). When a task is
 *   submitted with no order number, the executor calls
 *   generateRandomNumber(1000000000, 100000000) to mint a throwaway key, so the task
 *   still gets a partition of its own instead of colliding with a null key.
 *
 * WHAT TO NOTICE
 *   - The argument order reads backwards: the signature is (upperBound, lowerBound),
 *     so the LARGER number comes first. Swap them at a call site and the range goes
 *     negative - you get nonsense, not an exception.
 *
 *   - The formula is the standard inclusive form:
 *         lower + (int)(random * (upper - lower + 1))
 *     Both ends are reachable. The int cast truncates, which is what makes the top
 *     end reachable without ever exceeding it.
 *
 *   - (upperBound - lowerBound + 1) is int arithmetic. With a span near
 *     Integer.MAX_VALUE it overflows and the method silently returns garbage.
 *
 *   - Math.random() is thread-safe but is neither unique nor secure. Used as a
 *     workflow key that matters: two keyless submissions can draw the same number
 *     and end up serialised behind one executor for no reason. Prefer
 *     UUID.randomUUID() when uniqueness matters, SecureRandom when secrecy does.
 *
 *   - It returns String rather than int purely because the caller's key type is
 *     String; no formatting or padding is applied.
 *
 *   - The main() at the bottom is a scratch harness, not part of the Spring app.
 *     Its output is random by definition, so there is no fixed expected value to
 *     assert against - only the bounds can be checked.
 */
package com.tgt.gom.util;

public class GeneralUtil {

    /**
     * Generate a random number based on upper & lower bounds
     *
     * @param upperBound max value for the random number
     * @param lowerBound min value for the random number
     * @return random number
     */
    public static String generateRandomNumber(int upperBound, int lowerBound) {
        return String.valueOf((int) (Math.random() * ((upperBound - lowerBound) + 1)) + lowerBound);
    }

    public static void main(String[] args) {
        int upper = 100;
        int lower = 50;
        System.out.println("Input: upperBound=" + upper + ", lowerBound=" + lower);
        String result = GeneralUtil.generateRandomNumber(upper, lower);
        System.out.println("Output: random number = " + result);
    }
}
