/**
 * Problem: Track the most popular content ID by incrementing and decrementing counts,
 * returning any ID with the highest count or -1 if none exist.
 *
 * Approach: Maintain two maps:
 *   1) popularityMap (contentId → count) for quick updates.
 *   2) countMap (count → set of contentIds) as a TreeMap to retrieve the maximum count in O(log n).
 * On each increment/decrement, move the ID between sets and clean up empty entries.
 *
 * Time Complexity:
 *   increasePopularity / decreasePopularity: O(log k) where k is number of distinct counts
 *   mostPopular: O(1) (TreeMap.lastKey() + set lookup)
 *
 * Space Complexity: O(n) for storing all content IDs and their counts, plus auxiliary sets.
 */
import java.util.*;

interface MostPopular {
    void increasePopularity(Integer contentId);

    Integer mostPopular();

    void decreasePopularity(Integer contentId);
}

class PopularityTracker implements MostPopular {
    // Maps contentId -> popularity count
    private final Map<Integer, Integer> popularityMap = new HashMap<>();

    // Maps popularity -> set of contentIds with that popularity
    private final TreeMap<Integer, Set<Integer>> countMap = new TreeMap<>();

    @Override
    public void increasePopularity(Integer contentId) {
        int oldCount = popularityMap.getOrDefault(contentId, 0);
        int newCount = oldCount + 1;
        popularityMap.put(contentId, newCount);

        // Remove from old count set
        if (oldCount > 0) {
            removeFromCountMap(oldCount, contentId);
        }

        // Add to new count set
        countMap.computeIfAbsent(newCount, k -> new HashSet<>()).add(contentId);
    }

    @Override
    public void decreasePopularity(Integer contentId) {
        if (!popularityMap.containsKey(contentId)) return;

        int oldCount = popularityMap.get(contentId);
        removeFromCountMap(oldCount, contentId);

        int newCount = oldCount - 1;

        if (newCount > 0) {
            popularityMap.put(contentId, newCount);
            countMap.computeIfAbsent(newCount, k -> new HashSet<>()).add(contentId);
        } else {
            popularityMap.remove(contentId); // Remove if popularity reaches 0
        }
    }

    @Override
    public Integer mostPopular() {
        if (countMap.isEmpty()) return -1;
        int highestCount = countMap.lastKey();
        Set<Integer> mostPopularSet = countMap.get(highestCount);
        return mostPopularSet.iterator().next(); // return any one with max popularity
    }

    private void removeFromCountMap(int count, int contentId) {
        Set<Integer> set = countMap.get(count);
        if (set != null) {
            set.remove(contentId);
            if (set.isEmpty()) countMap.remove(count);
        }
    }

    // For debugging (optional)
    public void printState() {
        System.out.println("Popularity Map: " + popularityMap);
        System.out.println("Count Map: " + countMap);
        System.out.println();
    }

    public static void main(String[] args) {
        PopularityTracker popularityTracker = new PopularityTracker();

        popularityTracker.increasePopularity(7);
        popularityTracker.increasePopularity(7);
        popularityTracker.increasePopularity(8);
        System.out.println(popularityTracker.mostPopular()); // 7

        popularityTracker.increasePopularity(8);
        popularityTracker.increasePopularity(8);
        System.out.println(popularityTracker.mostPopular()); // 8

        popularityTracker.decreasePopularity(8);
        popularityTracker.decreasePopularity(8);
        System.out.println(popularityTracker.mostPopular()); // 7

        popularityTracker.decreasePopularity(7);
        popularityTracker.decreasePopularity(7);
        popularityTracker.decreasePopularity(8);
        System.out.println(popularityTracker.mostPopular()); // -1
    }
}
