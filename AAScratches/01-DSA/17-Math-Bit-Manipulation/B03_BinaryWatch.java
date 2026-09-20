/*
 * =====================================================================
 *  Binary Watch                             LeetCode 401 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A binary watch shows the hour on 4 LEDs (values 8, 4, 2, 1) and the minute
 *   on 6 LEDs (values 32, 16, 8, 4, 2, 1). Given turnedOn, the number of LEDs
 *   that are lit, return every time the watch could be showing.
 *   Hours run 0..11, minutes run 0..59, and minutes are zero-padded to 2 digits.
 *
 * EXAMPLE
 *   turnedOn = 1 -> [0:01, 0:02, 0:04, 0:08, 0:16, 0:32, 1:00, 2:00, 4:00, 8:00]
 *   turnedOn = 0 -> [0:00]                      only every LED off
 *   turnedOn = 9 -> []                          impossible; the most any valid
 *                                               time can light is 8 LEDs
 *
 * APPROACH  (enumerate every valid time, filter by popcount)
 *   1. The answer space is tiny: 12 hours * 60 minutes = 720 times. Just walk it.
 *   2. For each (hour, minute), the LEDs lit are exactly the set bits of hour
 *      plus the set bits of minute, so count them with Integer.bitCount.
 *   3. Keep the pair when that total equals turnedOn.
 *   4. Format as h:mm - the hour is not padded, the minute always is.
 *
 * KEY INSIGHT
 *   Do not try to place turnedOn lit bits into 10 LED positions and then check
 *   validity; go the other way. When the state space is small and bounded,
 *   enumerate every state and test a cheap predicate. Here popcount is the
 *   predicate, which is why this sits in the bit-manipulation family at all.
 *
 * COMPLEXITY
 *   Time  O(1)   720 fixed iterations, each doing constant work
 *   Space O(1)   ignoring the output list, which holds at most 720 strings
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it by choosing bits instead: enumerate the 1024 values of a 10-bit
 *     mask, split into hour/minute halves and discard invalid times.
 *   - Why can turnedOn never exceed 8? Hours 0..11 have at most 3 set bits
 *     (11 = 1011), minutes 0..59 at most 5 (59 = 111011).
 *   - Backtracking variant: choose which LEDs to light, prune once hour > 11.
 *   - How would you sort the output? By hour then minute, which this order
 *     already gives because the loops are nested that way.
 *
 * RUN
 *   main() runs 3 cases (typical, all-off edge, impossible count) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class BinaryWatch {

    public static List<String> readBinaryWatch(int turnedOn) {
        List<String> result = new ArrayList<>();
        for (int hour = 0; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                // LEDs lit by this time = set bits of the hour + set bits of the minute
                if (Integer.bitCount(hour) + Integer.bitCount(minute) == turnedOn) {
                    result.add(String.format("%d:%02d", hour, minute)); // minute always 2 digits
                }
            }
        }
        return result;
    }

    private static void check(int turnedOn, String expected) {
        System.out.println("turnedOn = " + turnedOn
                + "   actual " + readBinaryWatch(turnedOn)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: exactly one LED lit
        check(1, "[0:01, 0:02, 0:04, 0:08, 0:16, 0:32, 1:00, 2:00, 4:00, 8:00]");
        // edge: no LED lit, so the watch reads midnight
        check(0, "[0:00]");
        // tricky: more LEDs than any valid time can light
        check(9, "[]");
    }
}
