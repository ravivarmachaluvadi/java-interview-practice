import java.util.*;

// https://leetcode.com/problems/meeting-rooms-iii/description/
// 2402. Meeting Rooms III
class MeetingRoomsIII {

    public static int mostBooked(int n, int[][] meetings) {
        // sort meetings by start time
        Arrays.sort(meetings, (a, b) -> Integer.compare(a[0], b[0]));

        // min-heap for idle rooms (by room index)
        PriorityQueue<Integer> idleRooms = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            idleRooms.offer(i);
        }
        // min-heap for busy rooms: each element is a pair (endTime, roomIndex)
        PriorityQueue<RoomEnd> busyRooms = new PriorityQueue<>();

        // count of meetings per room
        int[] count = new int[n];

        for (int[] mt : meetings) {
            int start = mt[0];
            int end = mt[1];
            // Free up rooms that have become available by this start time
            while (!busyRooms.isEmpty() && busyRooms.peek().endTime <= start) {
                RoomEnd re = busyRooms.poll();
                idleRooms.offer(re.roomIndex);
            }

            if (!idleRooms.isEmpty()) {
                // assign the smallest free room
                int r = idleRooms.poll();
                count[r]++;
                // room now becomes busy until `end`
                busyRooms.offer(new RoomEnd(end, r));
            } else {
                // no free room => wait for earliest free room
                RoomEnd re = busyRooms.poll();
                int r = re.roomIndex;
                long freeTime = re.endTime; // when it becomes free
                long duration = (long) end - start;
                long newEnd = freeTime + duration;
                count[r]++;
                busyRooms.offer(new RoomEnd(newEnd, r));
            }
        }

        // find the room with maximum count, tie → smallest index
        int ans = 0;
        for (int i = 1; i < n; i++) {
            if (count[i] > count[ans]) {
                ans = i;
            }
        }
        return ans;
    }

    private static class RoomEnd implements Comparable<RoomEnd> {
        long endTime;
        int roomIndex;

        public RoomEnd(long endTime, int roomIndex) {
            this.endTime = endTime;
            this.roomIndex = roomIndex;
        }

        public int compareTo(RoomEnd other) {
            if (this.endTime != other.endTime) {
                return Long.compare(this.endTime, other.endTime);
            }
            return Integer.compare(this.roomIndex, other.roomIndex);
        }
    }

    // Example main to test
    public static void main(String[] args) {
        int n = 2;
        int[][] meetings = {
                {0, 10},
                {1, 5},
                {2, 7},
                {3, 4}
        };
        int result = mostBooked(n, meetings);
        System.out.println("Most booked room: " + result);
        // Expected output: 0
        // Explanation: as per example in problem statement
    }
}
