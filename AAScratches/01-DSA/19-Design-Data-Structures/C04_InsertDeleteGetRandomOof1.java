/*
 * =====================================================================
 *  Insert Delete GetRandom O(1)           LeetCode 380 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a set of ints supporting insert, remove and getRandom in average O(1).
 *   insert returns false if the value is already there; remove returns false if it is
 *   absent. getRandom must return every element currently in the set with equal chance.
 *
 * EXAMPLE
 *   insert(10) -> true, insert(20) -> true, insert(10) -> false   (already present)
 *   remove(20) -> true, remove(20) -> false                       (gone already)
 *   getRandom() -> 10                                             (only member left)
 *
 * DESIGN  (array plus index map, swap-with-last deletion)
 *   List<Integer> values   the elements packed with no gaps, so a random draw is just
 *                          values.get(rand.nextInt(values.size())).
 *   Map<Integer,Integer> indexOf   value -> its position in values, so remove can find
 *                          the slot to overwrite in O(1) instead of scanning.
 *   insert     append to values, record the new index in indexOf.
 *   remove     copy the LAST element over the victim's slot, repoint that element's
 *              index, then drop the duplicated tail and delete the victim's key.
 *   getRandom  one uniform draw over [0, size).
 *
 * KEY DECISIONS
 *   - Why not delete from the middle of the list? That shifts every later element:
 *     O(n) work and every index stored in the map goes stale. Swapping with the last
 *     element touches exactly two map entries and keeps the array gap-free.
 *   - The array MUST stay gap-free: getRandom's uniformity relies on every index in
 *     [0, size) holding a live element, so tombstones are not an option.
 *   - Order inside remove matters. Do indexOf.put(lastValue, slot) BEFORE
 *     indexOf.remove(val); when the victim IS the last element the put re-adds its own
 *     key, and only the later remove clears it. Swap the two lines and a dead key stays.
 *   - values.remove(values.size() - 1) resolves to remove(int index), which is O(1).
 *     remove(Integer.valueOf(x)) would be a linear scan - a classic overload trap.
 *
 * COMPLEXITY
 *   Time  O(1) average per op   hash lookup plus an append or a tail removal
 *   Space O(n)                  each element is held once in the list, once as a key
 *
 * INTERVIEW FOLLOW-UPS
 *   - Allow duplicates (LC 381): the map value becomes a Set<Integer> of indices.
 *   - Weighted getRandom: prefix sums plus binary search, O(log n) per draw.
 *   - Thread safety: one lock is easy; concurrent readers need a stable snapshot.
 *   - Why not TreeSet / LinkedHashSet? Neither can pick a uniform element in O(1).
 *
 * RUN
 *   main() runs 3 cases: a typical insert/remove sequence, the edge case of removing
 *   the only element and re-inserting it, and a statistical check that getRandom is
 *   uniform. Every line prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class RandomizedSet {

    /** value -> index of that value inside {@code values}. */
    private final Map<Integer, Integer> indexOf;

    /** The elements, packed with no gaps so index -> element is total. */
    private final List<Integer> values;

    private final Random rand;

    public RandomizedSet() {
        indexOf = new HashMap<>();
        values = new ArrayList<>();
        rand = new Random();
    }

    /** Inserts val. Returns false (and changes nothing) if it is already present. */
    public boolean insert(int val) {
        if (indexOf.containsKey(val)) {
            return false;
        }
        values.add(val);
        indexOf.put(val, values.size() - 1);
        return true;
    }

    /** Removes val. Returns false if it was not in the set. */
    public boolean remove(int val) {
        Integer slot = indexOf.get(val);
        if (slot == null) {
            return false;
        }
        int lastValue = values.get(values.size() - 1);

        // Move the last element into the victim's slot so the array keeps no gaps.
        values.set(slot, lastValue);
        indexOf.put(lastValue, slot);

        // Drop the now-duplicated tail by INDEX (O(1)), then forget the victim.
        // If val was itself the last element, the put above re-added its key and this
        // remove is what finally clears it - hence this exact ordering.
        values.remove(values.size() - 1);
        indexOf.remove(val);
        return true;
    }

    /** Uniformly random member. Caller must not call this on an empty set. */
    public int getRandom() {
        return values.get(rand.nextInt(values.size()));
    }

    public int size() {
        return values.size();
    }

    /** Copy of the backing array, for the demo only - insertion order is an artefact. */
    public List<Integer> contents() {
        return new ArrayList<>(values);
    }
}

class InsertDeleteGetRandomOof1 {

    public static void main(String[] args) {
        // ---- case 1: typical sequence -------------------------------------------
        RandomizedSet set = new RandomizedSet();
        print("case 1 insert(10)", set.insert(10), true);
        print("case 1 insert(20)", set.insert(20), true);
        print("case 1 insert(30)", set.insert(30), true);
        print("case 1 insert(10) again", set.insert(10), false);
        print("case 1 contents", set.contents(), "[10, 20, 30]");
        print("case 1 remove(20)", set.remove(20), true);
        // 30 was the last element, so it slid into slot 1 where 20 used to sit.
        print("case 1 contents after remove", set.contents(), "[10, 30]");
        print("case 1 remove(20) again", set.remove(20), false);
        print("case 1 size", set.size(), 2);

        // ---- case 2: edge - remove the only element, then re-insert it -----------
        // This is what breaks a naive remove(): the victim is also the last element.
        RandomizedSet single = new RandomizedSet();
        print("case 2 insert(7)", single.insert(7), true);
        print("case 2 remove(7)", single.remove(7), true);
        print("case 2 size", single.size(), 0);
        print("case 2 contents", single.contents(), "[]");
        print("case 2 insert(7) again", single.insert(7), true);
        print("case 2 getRandom", single.getRandom(), 7);

        // ---- case 3: getRandom is a member, and is uniform -----------------------
        RandomizedSet three = new RandomizedSet();
        three.insert(1);
        three.insert(2);
        three.insert(3);

        int draws = 3000;
        int[] hits = new int[4];
        boolean alwaysMember = true;
        for (int i = 0; i < draws; i++) {
            int picked = three.getRandom();
            if (picked < 1 || picked > 3) {
                alwaysMember = false;
            } else {
                hits[picked]++;
            }
        }
        print("case 3 every draw is a member", alwaysMember, true);
        // Expected 1000 each; 700..1300 is roughly 11 standard deviations wide, so this
        // is a deterministic pass for a correct uniform draw.
        boolean balanced = inRange(hits[1]) && inRange(hits[2]) && inRange(hits[3]);
        print("case 3 each of 1,2,3 drawn 700..1300 times", balanced, true);
    }

    private static boolean inRange(int count) {
        return count >= 700 && count <= 1300;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
