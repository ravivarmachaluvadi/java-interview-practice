/**
 * Problem: Design a data structure that supports insert, remove and getRandom
 * operations in average O(1) time.
 *
 * Approach: Maintain an ArrayList for fast index access and a HashMap to map
 * values to their indices in the list. Insertion appends to the list and records
 * the index. Removal swaps the target element with the last one, updates the
 * map, removes the last entry from the list, and deletes the key from the map.
 *
 * Time Complexity: O(1) average for insert, remove, and getRandom.
 * Space Complexity: O(n), where n is the number of elements stored.
 */
import java.util.*;

class RemoveInsertGetO1 {

    List<Integer> list;
    Map<Integer, Integer> map;
    Random rand;

    public RemoveInsertGetO1() {
        list = new ArrayList<>(); // 0
        map = new HashMap<>(); //
        rand = new Random();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        list.add(val); // 1
        map.put(val, list.size() - 1); //1,1
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) return false;

        int lastVal = list.get(list.size() - 1); // 1
        int valIdx = map.get(val);// 0
        list.set(valIdx, lastVal);
        map.put(lastVal, valIdx);
        list.remove(list.size() - 1);
        map.remove(val);

        return true;
    }

    public static void main(String[] args) {
        RemoveInsertGetO1 obj = new RemoveInsertGetO1();
        System.out.println("Initial list: " + obj.list);
        System.out.println("Insert 10: " + obj.insert(10));
        System.out.println("Insert 20: " + obj.insert(20));
        System.out.println("Insert 30: " + obj.insert(30));
        System.out.println("After inserts, list: " + obj.list);
        System.out.println("Remove 20: " + obj.remove(20));
        System.out.println("After removal of 20, list: " + obj.list);
        System.out.println("Insert 40: " + obj.insert(40));
        System.out.println("Final list: " + obj.list);
    }
}

