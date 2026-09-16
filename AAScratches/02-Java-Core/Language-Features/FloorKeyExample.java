/**
 * Problem: Demonstrates how to retrieve the greatest key less than or equal to a given value
 * from a TreeMap using the floorKey method.
 *
 * Approach: Create a TreeMap, insert integer keys with string values,
 * then call floorKey on various inputs to show its behavior when no key is ≤ the argument,
 * when an intermediate key exists, and when an exact match exists.
 *
 * Time Complexity: O(log n) per floorKey lookup (TreeMap uses a Red‑Black tree).
 * Space Complexity: O(n) for storing n entries in the TreeMap. 
 */
import java.util.*;

class FloorKeyExample {
    public static void main(String[] args) {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(5, "A");
        map.put(10, "B");
        map.put(15, "C");

        System.out.println("Map: " + map);

        System.out.println("floorKey(3): " + map.floorKey(3)); // null, since all keys > 3
        System.out.println("floorKey(12): " + map.floorKey(12)); // 10 (greatest key ≤ 12)
        System.out.println("floorKey(10): " + map.floorKey(10)); // 10 (exact match)
    }
}
