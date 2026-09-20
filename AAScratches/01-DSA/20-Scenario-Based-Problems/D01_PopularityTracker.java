/*
 * =====================================================================
 *  Popularity Tracker (running max under an interface)   Design | Hard  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Implement the MostPopular interface for a content service. Content IDs can be
 *   promoted (increasePopularity) or demoted (decreasePopularity) in any order, and at
 *   any moment mostPopular() must return an ID with the highest count, or -1 when no
 *   content has a positive count. A count that falls to 0 means the ID is gone.
 *   Ties may be broken arbitrarily: any ID holding the maximum is a valid answer.
 *
 * EXAMPLE
 *   increase(7), increase(7), increase(8)   ->  mostPopular() = 7   (7 has 2, 8 has 1)
 *   increase(8), increase(8)                ->  mostPopular() = 8   (8 has 3, 7 has 2)
 *   decrease(8), decrease(8)                ->  mostPopular() = 7   (8 back to 1)
 *   decrease everything to zero             ->  mostPopular() = -1
 *
 * DESIGN  (count map + count-to-IDs bucket TreeMap)
 *   popularityMap : HashMap<Integer, Integer>
 *       contentId -> its current count. The forward lookup, O(1).
 *   countMap      : TreeMap<Integer, Set<Integer>>
 *       count -> the set of IDs currently sitting at that count. This is the inverted
 *       index, and because it is a TreeMap its last key is the running maximum.
 *
 *   Every update is the same three-step move:
 *       1. read the old count from popularityMap
 *       2. pull the ID out of its old bucket, deleting the bucket if it becomes empty
 *       3. write the new count and drop the ID into the new bucket
 *   Deleting empty buckets is what keeps lastKey() honest - a leftover empty set at a
 *   high count would make mostPopular() report a maximum nobody holds any more.
 *
 * KEY DECISIONS
 *   - Why two maps and not one? A single ID->count map forces an O(n) scan on every
 *     mostPopular() call. The inverted index turns the running maximum into a lookup.
 *   - Why TreeMap and not a PriorityQueue? A heap cannot cheaply remove an arbitrary
 *     element when a count changes, so it accumulates stale entries. A TreeMap keyed by
 *     count supports exact removal and still exposes the maximum.
 *   - Why a Set per count and not a List? Removal must be O(1); a List removal is O(size).
 *   - Count 0 deletes the ID entirely, so "not tracked" and "count zero" are one state.
 *     decreasePopularity on an unknown ID is a silent no-op rather than a negative count.
 *
 * COMPLEXITY
 *   increasePopularity / decreasePopularity : O(log d), d = number of DISTINCT counts,
 *       from the TreeMap insert/remove. The HashMap and HashSet work is O(1).
 *   mostPopular : O(log d) for TreeMap.lastKey(), then O(1) to take an element from the
 *       bucket. (The original header claimed O(1) here; lastKey() is a tree descent.
 *       A LinkedHashMap-of-buckets doubly-linked-list design is the true O(1) variant.)
 *   Space : O(n) for n tracked IDs - each ID appears once in each map.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make every operation O(1): replace the TreeMap with a doubly linked list of count
 *     buckets in ascending order. Counts only ever move by one, so the next bucket is
 *     always the neighbour. This is exactly LeetCode 432 "All O(1) Data Structure".
 *   - Return ALL IDs at the maximum, or the k most popular, instead of one.
 *   - Break ties deterministically (smallest ID, or most recently promoted): swap the
 *     HashSet bucket for a TreeSet, or for a LinkedHashSet to get insertion order.
 *   - This is the skeleton of an LFU cache: LFU evicts from the LOWEST bucket
 *     (firstKey) instead of reading the highest.
 *   - Thread safety: the two maps must move together, so guard both with one lock or
 *     make the whole move a single atomic section. Two concurrent maps are not enough.
 *   - Gotcha: mostPopular() returns Integer, so == between TWO boxed Integers compares
 *     references and fails outside the -128..127 cache - use .equals() or .intValue().
 *     Against an int literal it is safe: "result == -1" unboxes the Integer and compares
 *     numerically, which is exactly what case 7 below relies on.
 *
 * RUN
 *   main() runs 7 cases: the empty tracker, the typical promote/demote sequence, a
 *   no-op decrease on an unknown ID, everything back at zero, and a tie on a second
 *   tracker. Each prints actual vs expected.
 */

import java.util.*;

interface MostPopular {
    void increasePopularity(Integer contentId);

    Integer mostPopular();

    void decreasePopularity(Integer contentId);
}

class PopularityTracker implements MostPopular {

    /** contentId -> its current popularity count. Only positive counts are stored. */
    private final Map<Integer, Integer> popularityMap = new HashMap<>();

    /** popularity count -> the IDs currently at that count. Sorted, so lastKey() is the max. */
    private final TreeMap<Integer, Set<Integer>> countMap = new TreeMap<>();

    @Override
    public void increasePopularity(Integer contentId) {
        int oldCount = popularityMap.getOrDefault(contentId, 0);
        int newCount = oldCount + 1;

        if (oldCount > 0) {
            removeFromBucket(oldCount, contentId);   // oldCount == 0 means no bucket exists yet
        }
        popularityMap.put(contentId, newCount);
        addToBucket(newCount, contentId);
    }

    @Override
    public void decreasePopularity(Integer contentId) {
        Integer oldCount = popularityMap.get(contentId);
        if (oldCount == null) return;                // untracked ID: nothing to demote

        removeFromBucket(oldCount, contentId);
        int newCount = oldCount - 1;

        if (newCount > 0) {
            popularityMap.put(contentId, newCount);
            addToBucket(newCount, contentId);
        } else {
            popularityMap.remove(contentId);         // count 0 means the ID stops existing
        }
    }

    @Override
    public Integer mostPopular() {
        if (countMap.isEmpty()) return -1;
        int highestCount = countMap.lastKey();
        return countMap.get(highestCount).iterator().next();   // any ID at the max is valid
    }

    private void addToBucket(int count, Integer contentId) {
        countMap.computeIfAbsent(count, k -> new HashSet<>()).add(contentId);
    }

    /** Removes the ID from its bucket and deletes the bucket when it empties. */
    private void removeFromBucket(int count, Integer contentId) {
        Set<Integer> bucket = countMap.get(count);
        if (bucket == null) return;
        bucket.remove(contentId);
        // An empty bucket left behind would make lastKey() report a phantom maximum.
        if (bucket.isEmpty()) countMap.remove(count);
    }

    /** Handy while debugging a failing sequence: shows both maps at once. */
    public String state() {
        return "counts=" + popularityMap + " buckets=" + countMap;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        PopularityTracker tracker = new PopularityTracker();

        // edge: nothing tracked yet
        print("case 1 empty tracker", tracker.mostPopular(), "-1");

        tracker.increasePopularity(7);
        tracker.increasePopularity(7);
        tracker.increasePopularity(8);
        print("case 2 after 7,7,8", tracker.mostPopular(), "7");

        tracker.increasePopularity(8);
        tracker.increasePopularity(8);
        print("case 3 8 overtakes 7", tracker.mostPopular(), "8");

        tracker.decreasePopularity(8);
        tracker.decreasePopularity(8);
        print("case 4 8 demoted twice", tracker.mostPopular(), "7");

        // edge: demoting an ID that was never tracked must be a silent no-op
        tracker.decreasePopularity(999);
        print("case 5 unknown ID ignored", tracker.state(),
                "counts={7=2, 8=1} buckets={1=[8], 2=[7]}");

        tracker.decreasePopularity(7);
        tracker.decreasePopularity(7);
        tracker.decreasePopularity(8);
        print("case 6 everything at zero", tracker.mostPopular(), "-1");

        // tricky: a genuine tie - either ID is a correct answer, so check membership
        PopularityTracker tied = new PopularityTracker();
        tied.increasePopularity(4);
        tied.increasePopularity(9);
        Integer winner = tied.mostPopular();
        boolean valid = winner == 4 || winner == 9;
        print("case 7 tie is valid", valid + " (returned " + winner + ")", "true");
    }
}
