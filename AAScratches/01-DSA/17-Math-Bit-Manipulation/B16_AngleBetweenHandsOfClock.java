
// https://leetcode.com/problems/angle-between-hands-of-a-clock/

/**
 * Constraints:
 * <p>
 * 1 <= hour <= 12
 * 0 <= minutes <= 59
 * <p>
 * We want the positions (in degrees) of the hour hand and minute hand, then take the absolute
 * <p>
 * difference, and finally ensure the result is ≤ 180° (the “smaller” angle).
 */
class AngleBetweenHandsOfClock {

    public static double angleBetweenHands(int hour, int minute) {
        // Normalize the hour to a 12-hour format
        if (hour >= 12) hour -= 12;

        // Calculate the angle of the minute hand
        // (6 degrees per minute)
        double minuteAngle = minute * 6;

        // Hour hand
        // hour angle is 30 degress
        //The hour hand moves 360° per 12 hours → 30° per hour. But it also moves
        //continuously as the minutes pass (i.e. it doesn’t jump only at the hour).
        //In one hour it moves 30°, so in one minute it moves  30/60=0.5 degrees.
        double hourAngle = (hour * 30) + (minute * 0.5);

        // Calculate the difference between the two angles
        // from pivot or base of zero angle
        double angle = Math.abs(hourAngle - minuteAngle);

        // The angle should be the smaller of the two possible angles (angle or 360 - angle)
        return Math.min(angle, 360 - angle);
    }

    public static void main(String[] args) {
        // Test cases
        int hour1 = 3, minute1 = 30;
        int hour2 = 6, minute2 = 0;
        int hour3 = 12, minute3 = 0;
        int hour4 = 9, minute4 = 45;

        System.out.println("Angle at 3:30 is: " + angleBetweenHands(hour1, minute1) + " degrees"); // 75
        System.out.println("Angle at 6:00 is: " + angleBetweenHands(hour2, minute2) + " degrees"); // 180
        System.out.println("Angle at 12:00 is: " + angleBetweenHands(hour3, minute3) + " degrees"); // 0.0
        System.out.println("Angle at 9:45 is: " + angleBetweenHands(hour4, minute4) + " degrees"); // 22.5
    }
}
