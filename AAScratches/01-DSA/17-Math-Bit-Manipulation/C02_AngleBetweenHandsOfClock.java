/*
 * =====================================================================
 *  Angle Between Hands of a Clock                   LeetCode 1344 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an hour (1..12) and a minute (0..59) on a standard analog clock,
 *   return the smaller of the two angles formed by the hour hand and the
 *   minute hand, in degrees. The answer is always in [0, 180].
 *
 * EXAMPLE
 *   hour = 3, minute = 30   ->  75.0    hour hand is at 105, minute hand at 180
 *   hour = 6, minute = 0    ->  180.0   the two hands point exactly opposite
 *   hour = 12, minute = 0   ->  0.0     edge case: 12 normalises to 0
 *   hour = 9, minute = 45   ->  22.5    hour hand has drifted 22.5 past the 9
 *
 * APPROACH  (degrees-per-unit, then fold to the smaller arc)
 *   1. Normalise hour 12 to 0, because the dial repeats every 12 hours.
 *   2. Minute hand: 360 degrees / 60 minutes = 6 degrees per minute.
 *   3. Hour hand: 360 / 12 = 30 degrees per hour, PLUS 30/60 = 0.5 degrees
 *      per minute, because the hour hand creeps forward continuously.
 *   4. Take the absolute difference of the two positions.
 *   5. Two hands cut the circle into two arcs that sum to 360, so the answer
 *      is min(diff, 360 - diff).
 *
 * KEY INSIGHT
 *   The hour hand does NOT jump on the hour - it drifts 0.5 degrees every
 *   minute. Forgetting that drift is the single most common wrong answer
 *   (it gives 90 for 3:30 instead of 75). The second half of the pattern,
 *   folding an angle with min(x, 360 - x), is the same wraparound trick that
 *   circular-array and minute-of-day problems reuse.
 *
 * COMPLEXITY
 *   Time  O(1)  a handful of arithmetic operations, no loops
 *   Space O(1)  only scalar locals
 *
 * INTERVIEW FOLLOW-UPS
 *   - At what times of day are the hands exactly overlapping? (22 times/day)
 *   - Return the angle measured clockwise from the hour hand instead.
 *   - Why is double safe here? (all values are multiples of 0.5, exact in binary)
 *   - Extend to a hand-and-second-hand clock, or to a 24-hour dial.
 *
 * RUN
 *   main() runs 4 cases (typical, opposite hands, the 12:00 edge, and a
 *   tricky one where the hour-hand drift decides the answer) and prints
 *   actual vs expected.
 */

class AngleBetweenHandsOfClock {

    private static final double DEGREES_PER_MINUTE = 6.0;        // 360 / 60
    private static final double DEGREES_PER_HOUR = 30.0;         // 360 / 12
    private static final double HOUR_HAND_DRIFT_PER_MINUTE = 0.5; // 30 / 60

    public static double angleBetweenHands(int hour, int minute) {
        // 12 o'clock sits at the same place on the dial as 0 o'clock.
        if (hour >= 12) {
            hour -= 12;
        }

        double minuteAngle = minute * DEGREES_PER_MINUTE;

        // The hour hand is partly through its hour, so add the per-minute drift.
        double hourAngle = (hour * DEGREES_PER_HOUR) + (minute * HOUR_HAND_DRIFT_PER_MINUTE);

        double angle = Math.abs(hourAngle - minuteAngle);

        // The two arcs between the hands sum to 360; we want the smaller one.
        return Math.min(angle, 360 - angle);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1  3:30 (typical)", angleBetweenHands(3, 30), 75.0);
        print("case 2  6:00 (opposite)", angleBetweenHands(6, 0), 180.0);
        print("case 3 12:00 (edge, hour wraps)", angleBetweenHands(12, 0), 0.0);
        print("case 4  9:45 (drift decides)", angleBetweenHands(9, 45), 22.5);
    }
}
