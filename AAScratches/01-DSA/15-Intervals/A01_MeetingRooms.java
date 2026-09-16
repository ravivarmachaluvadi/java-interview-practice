import java.util.Arrays;
import java.util.Comparator;

// https://leetcode.com/problems/meeting-rooms/description/
// 252. Meeting Rooms
class MeetingRooms {
    public static boolean canAttendMeetings(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return true;  // no meetings, so trivially can attend all
        }
        // Sort by start time
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        // Check for overlaps
        for (int i = 1; i < intervals.length; i++) {
            // if previous ending greater than current starting then return false
            if (intervals[i - 1][1] > intervals[i][0]) {
                // previous meeting ends after the next meeting starts → overlap
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Example 1: overlapping meetings
        int[][] intervals1 = {{0, 30}, {5, 10}};
        System.out.println("Example1: " + canAttendMeetings(intervals1));  // expected: false

        // Example 2: non-overlapping meetings
        int[][] intervals2 = {{7, 10}, {2, 4}};
        System.out.println("Example2: " + canAttendMeetings(intervals2));  // expected: true

        // Example 3: back-to-back meetings (allowed)
        int[][] intervals3 = {{1, 2}, {2, 3}, {3, 4}};
        System.out.println("Example3: " + canAttendMeetings(intervals3));  // expected: true

        // Example 4: more mix
        int[][] intervals4 = {{0, 5}, {1, 2}, {3, 4}, {6, 7}};
        System.out.println("Example4: " + canAttendMeetings(intervals4));  // expected: false (0-5 overlaps 1-2)
    }
}
