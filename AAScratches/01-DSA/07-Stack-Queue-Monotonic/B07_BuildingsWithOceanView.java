import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/buildings-with-an-ocean-view/description/

/**
 * There are n buildings in a line. You are given an integer array heights
 * of size n that represents the heights of the buildings in the line.
 * <p>
 * The ocean is to the right of the buildings. A building has an ocean view
 * if the building can see the ocean without obstructions. Formally, a building
 * has an ocean view if all the buildings to its right have a smaller height.
 * <p>
 * Return a list of indices (0-indexed) of buildings that have an ocean view,
 * sorted in increasing order.
 */
class BuildingsWithOceanView {

    public static List<Integer> findBuildings(int[] heights) {
        List<Integer> result = new ArrayList<>();
        int maxHeight = 0;

        // Traverse the heights from right to left
        for (int i = heights.length - 1; i >= 0; i--) {
            if (heights[i] > maxHeight) {
                result.add(i);
                maxHeight = heights[i];
            }
        }
        // Reverse the result list to get the buildings in left-to-right order
        List<Integer> finalResult = new ArrayList<>();
        for (int i = result.size() - 1; i >= 0; i--)
            finalResult.add(result.get(i));
        return finalResult;
    }

    public static void main(String[] args) {
        int[] heights = {4, 2, 3, 1};
        // Example input: {4, 2, 3, 1}
        // Expected output: [0, 2, 3] (buildings with indices 0, 2, and 3 have an ocean view)

        List<Integer> buildingsWithOceanView = findBuildings(heights);
        System.out.println("Buildings with an ocean view: " + buildingsWithOceanView);
    }
}
