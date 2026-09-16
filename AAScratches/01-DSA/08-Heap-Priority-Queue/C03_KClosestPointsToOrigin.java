import java.util.*;

/**
 * Given an array of points where points[i] = [xi, yi] represents a point on the X-Y
 * plane and an integer k, return the k closest points to the origin (0, 0).
 * <p>
 * The distance between two points on the X-Y plane is the Euclidean distance
 * (i.e., √(x1 - x2)2 + (y1 - y2)2).
 * <p>
 * You may return the answer in any order. The answer is guaranteed to be unique
 * (except for the order that it is in).
 */
class KClosestPointsToOrigin {

    public static int[][] kClosest(int[][] points, int K) {
        // Max-heap to store the K closest points, ordered by their squared distance to the origin
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1]));

        // Add all points to the heap
        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > K) {
                maxHeap.poll(); // Remove the farthest point if heap size exceeds K
            }
        }

        // Prepare the result array to store the K closest points
        int[][] result = new int[K][2];
        int i = 0;
        while (!maxHeap.isEmpty()) {
            result[i++] = maxHeap.poll();
        }
        return result;
    }

    public static void main(String[] args) {
        // Example input: points array and K value
        int[][] points = {{1, 3}, {-2, 2}, {5, 8}, {0, 1}};
        int K = 2;
        // Get the K closest points to the origin
        int[][] closestPoints = kClosest(points, K);

        // Print the result
        System.out.println("K closest points to the origin:");
        for (int[] point : closestPoints) {
            System.out.println(Arrays.toString(point));
        }
    }
}
