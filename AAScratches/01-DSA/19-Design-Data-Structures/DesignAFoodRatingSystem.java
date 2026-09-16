import java.util.*;

// https://leetcode.com/problems/design-a-food-rating-system/description/
// 2353. Design a Food Rating System
// Using record to store food properties (immutable and compact)

public class DesignAFoodRatingSystem {

    private record Food(String name, String cuisine, int rating) {
    }

    // Map from cuisine → max-heap (by rating desc, then name asc)
    private final Map<String, PriorityQueue<Food>> cuisineToFoods;
    // Map from food name → current Food object
    private final Map<String, Food> nameToFood;

    public DesignAFoodRatingSystem(String[] foods, String[] cuisines, int[] ratings) {
        cuisineToFoods = new HashMap<>();
        nameToFood = new HashMap<>();

        for (int i = 0; i < foods.length; i++) {
            String name = foods[i];
            String cuisine = cuisines[i];
            Food food = new Food(name, cuisine, ratings[i]);

            nameToFood.put(name, food);
            cuisineToFoods
                    .computeIfAbsent(cuisine, k -> new PriorityQueue<>((a, b) -> {
                        int diff = b.rating - a.rating; // higher rating first
                        if (diff == 0) {
                            return a.name.compareTo(b.name); // tie-break lexicographically
                        }
                        return diff;
                    }))
                    .add(food);
        }
    }

    // O(log n) amortized due to heap add
    public void changeRating(String food, int newRating) {
        String cuisine = nameToFood.get(food).cuisine;
        Food newFood = new Food(food, cuisine, newRating);
        nameToFood.put(food, newFood);
        cuisineToFoods.get(cuisine).add(newFood); // lazy addition
    }

    // O(log n) amortized due to lazy cleanup of outdated foods
    public String highestRated(String cuisine) {
        PriorityQueue<Food> pq = cuisineToFoods.get(cuisine);
        while (!pq.isEmpty()) {
            Food top = pq.peek();
            Food current = nameToFood.get(top.name);
            // Check if this top entry is still valid
            if (top.rating == current.rating) {
                return top.name;
            } else {
                pq.poll(); // remove outdated
            }
        }
        return null;
    }

    // Example demonstration
    public static void main(String[] args) {
        String[] foods = {"kimchi", "miso", "sushi", "ramen"};
        String[] cuisines = {"korean", "japanese", "japanese", "japanese"};
        int[] ratings = {9, 12, 8, 15};

        DesignAFoodRatingSystem fr = new DesignAFoodRatingSystem(foods, cuisines, ratings);

        System.out.println(fr.highestRated("japanese")); // ramen
        fr.changeRating("ramen", 10);
        System.out.println(fr.highestRated("japanese")); // miso
        fr.changeRating("sushi", 16);
        System.out.println(fr.highestRated("japanese")); // sushi
        System.out.println(fr.highestRated("korean"));   // kimchi
    }
}
