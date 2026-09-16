import java.util.*;

//  "Insert Delete GetRandom O(1)" problem — also
// known as LeetCode 380: Insert Delete GetRandom O(1).
class RandomizedSet {
    // value -> index in list
    private final Map<Integer, Integer> map;
    private final List<Integer> list;
    private final Random rand;

    public RandomizedSet() {
        map = new HashMap<>();
        list = new ArrayList<>();
        rand = new Random();
    }

    // Inserts a value. Returns true if inserted
    // successfully, false if it already exists.
    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        list.add(val);
        map.put(val, list.size() - 1);
        return true;
    }

    // Removes a value. Returns true if removed
    // successfully, false if it doesn't exist.
    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }
        int index = map.get(val);
        // list.getLast
        int lastElement = list.getLast();

        // Swap the element to remove with the last element
        list.set(index, lastElement);
        map.put(lastElement, index);

        // Remove last element
        list.removeLast();
        map.remove(val);
        return true;
    }

    // Get a random element from the set
    public int getRandom() {
        int randomIndex = rand.nextInt(list.size());
        return list.get(randomIndex);
    }

    // Print the current state (for demonstration)
    public void printState() {
        System.out.println("List: " + list);
        System.out.println("Map: " + map);
    }
}

public class InsertDeleteGetRandomOof1 {
    public static void main(String[] args) {
        RandomizedSet randomizedSet = new RandomizedSet();

        System.out.println("Insert 10: " + randomizedSet.insert(10));
        System.out.println("Insert 20: " + randomizedSet.insert(20));
        System.out.println("Insert 30: " + randomizedSet.insert(30));
        randomizedSet.printState();

        System.out.println("\nRemove 20: " + randomizedSet.remove(20));
        randomizedSet.printState();

        System.out.println("\nInsert 40: " + randomizedSet.insert(40));
        randomizedSet.printState();

        System.out.println("\nRandom element: " + randomizedSet.getRandom());
        System.out.println("Random element: " + randomizedSet.getRandom());
    }
}
