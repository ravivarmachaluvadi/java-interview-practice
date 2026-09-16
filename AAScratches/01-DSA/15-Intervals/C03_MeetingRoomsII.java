import java.util.Arrays;

// https://leetcode.com/problems/meeting-rooms-ii/description/
// 253. Meeting Rooms II

/**
 * Given an array of meeting time intervals intervals where
 * <p>
 * intervals[i] = [starti, endi], return the minimum number
 * <p>
 * of conference rooms required.
 */
class MeetingRoomsII {
    public static int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int j = 0;  // pointer for end times

        for (int i = 0; i < n; i++) {
            if (starts[i] < ends[j]) {
                // need a new room
                rooms++;
            } else {
                // one meeting ended before this one starts → reuse room
                j++;
            }
        }

        return rooms;
    }

    public static void main(String[] args) {
        int[][] intervals1 = {{0, 30}, {5, 10}, {15, 20}};
        System.out.println("Expected: 2, Got: " + minMeetingRooms(intervals1));

        int[][] intervals2 = {{7, 10}, {2, 4}};
        System.out.println("Expected: 1, Got: " + minMeetingRooms(intervals2));

        int[][] intervals3 = {{1, 5}, {2, 6}, {4, 7}, {8, 9}};
        System.out.println("Additional Example => Expected: 3, Got: " + minMeetingRooms(intervals3));
    }
}
