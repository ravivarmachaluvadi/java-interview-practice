/*
 * =====================================================================
 *  Design a Food Rating System                   LeetCode 2353 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Parallel arrays foods, cuisines and ratings describe n dishes; each food name is
 *   unique and belongs to exactly one cuisine. Support changeRating(food, newRating) and
 *   highestRated(cuisine), which returns the best-rated food of that cuisine, breaking
 *   ties by the lexicographically smaller NAME.
 *
 * EXAMPLE
 *   foods = [kimchi, miso, sushi, ramen], cuisines = [korean, japanese x3],
 *   ratings = [9, 12, 8, 15]
 *   highestRated("japanese")          ->  ramen   (15 is the best)
 *   changeRating("ramen", 10)         ->  miso now leads with 12
 *   miso and sushi both 16            ->  miso, because "miso" < "sushi"
 *
 * DESIGN
 *   Food              immutable record (name, cuisine, rating) -- one snapshot of a dish.
 *   nameToFood        HashMap name -> its CURRENT Food. The single source of truth.
 *   cuisineToFoods    HashMap cuisine -> PriorityQueue<Food>, one heap PER CUISINE, so a
 *                     query never looks at dishes of another cuisine.
 *   Heap ordering     rating descending, then name ascending -- the tie-break is a
 *                     property of the structure, not of the query.
 *
 * KEY DECISIONS
 *   - Lazy deletion: changeRating() pushes a new snapshot and leaves the old one in the
 *     heap, since a PriorityQueue cannot remove an interior element in better than O(n).
 *   - Validity test on peek: a snapshot is live only if
 *     nameToFood.get(top.name()).rating() == top.rating(). Otherwise poll and retry.
 *   - Integer.compare / String.compareTo instead of subtraction, so the comparator stays
 *     correct for ratings near Integer.MAX_VALUE.
 *
 * KEY INSIGHT
 *   Group first, then apply lazy deletion inside each group: partitioning by cuisine
 *   turns one global "find the best" into many small ones. The two-level comparator is
 *   where candidates drop points -- state the tie-break out loud before writing the heap.
 *
 * COMPLEXITY
 *   Time  constructor O(n log n); changeRating O(log n); highestRated O(log n) amortized.
 *   Space O(n + c) for n dishes and c changeRating calls, since stale snapshots linger.
 *
 * INTERVIEW FOLLOW-UPS
 *   - TreeSet<Food> per cuisine with eager removal: true O(log n) worst case, O(n) space.
 *   - Return the top k foods of a cuisine instead of just the best.
 *   - Bound memory when ratings churn: rebuild a heap past 2x the live dish count.
 *   - Make changeRating and highestRated safe under concurrent callers.
 *
 * RUN
 *   main() runs 3 scenarios (typical, a rating tie that exercises the name tie-break,
 *   and a single-dish cuisine) and prints actual vs expected.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class DesignAFoodRatingSystem {

    /** One snapshot of a dish. A record is immutable, so stale copies are harmless. */
    private record Food(String name, String cuisine, int rating) {
    }

    /** cuisine -> max-heap of snapshots (rating desc, then name asc). */
    private final Map<String, PriorityQueue<Food>> cuisineToFoods = new HashMap<>();

    /** food name -> its current snapshot. This is the authority the heaps are checked against. */
    private final Map<String, Food> nameToFood = new HashMap<>();

    public DesignAFoodRatingSystem(String[] foods, String[] cuisines, int[] ratings) {
        for (int i = 0; i < foods.length; i++) {
            Food food = new Food(foods[i], cuisines[i], ratings[i]);
            nameToFood.put(food.name(), food);
            heapFor(food.cuisine()).add(food);
        }
    }

    /** Lazily creates the per-cuisine heap with the two-level ordering. */
    private PriorityQueue<Food> heapFor(String cuisine) {
        return cuisineToFoods.computeIfAbsent(cuisine, k -> new PriorityQueue<>((a, b) -> {
            int byRating = Integer.compare(b.rating(), a.rating());   // higher rating first
            if (byRating != 0) {
                return byRating;
            }
            return a.name().compareTo(b.name());                      // tie -> smaller name
        }));
    }

    /** O(log n): push a new snapshot, leave the old one behind as garbage. */
    public void changeRating(String food, int newRating) {
        String cuisine = nameToFood.get(food).cuisine();
        Food updated = new Food(food, cuisine, newRating);
        nameToFood.put(food, updated);
        heapFor(cuisine).add(updated);
    }

    /** O(log n) amortized: drop superseded snapshots, then trust the top. */
    public String highestRated(String cuisine) {
        PriorityQueue<Food> heap = cuisineToFoods.get(cuisine);
        if (heap == null) {
            return null;    // cuisine never seen
        }
        while (!heap.isEmpty()) {
            Food top = heap.peek();
            Food live = nameToFood.get(top.name());
            if (top.rating() == live.rating()) {
                return top.name();
            }
            heap.poll();    // this snapshot was superseded by a changeRating
        }
        return null;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] foods = {"kimchi", "miso", "sushi", "ramen"};
        String[] cuisines = {"korean", "japanese", "japanese", "japanese"};
        int[] ratings = {9, 12, 8, 15};
        DesignAFoodRatingSystem system = new DesignAFoodRatingSystem(foods, cuisines, ratings);

        // ---- case 1: typical -- best dish, then a downgrade of the leader ----
        print("case 1 japanese", system.highestRated("japanese"), "ramen");
        system.changeRating("ramen", 10);           // 15 -> 10, ramen loses the lead
        print("case 1 after downgrade", system.highestRated("japanese"), "miso");
        system.changeRating("sushi", 16);           // 8 -> 16, the laggard takes over
        print("case 1 after upgrade", system.highestRated("japanese"), "sushi");

        // ---- case 2: tie on rating -> lexicographically smaller name wins ----
        system.changeRating("miso", 16);            // miso and sushi are both 16 now
        print("case 2 tie 16/16", system.highestRated("japanese"), "miso");

        // ---- case 3: edge case -- a cuisine with exactly one dish ----
        print("case 3 korean", system.highestRated("korean"), "kimchi");
        system.changeRating("kimchi", 1);           // still the only korean dish
        print("case 3 korean downgraded", system.highestRated("korean"), "kimchi");
        print("case 3 unknown cuisine", system.highestRated("thai"), "null");
    }
}
