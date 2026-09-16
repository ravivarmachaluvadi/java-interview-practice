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
