import java.util.*;

// https://leetcode.com/problems/smallest-common-region/description/
// 1257. Smallest Common Region
class SmallestCommonRegion {

    /*
       The idea:
       1. Build a map of child → parent for all regions.
       2. Collect all ancestors of region1 in a set.
       3. Traverse from region2 upwards — the first common
          ancestor found in the set is the smallest common region.
    */
    public String findSmallestRegion(List<List<String>> regions, String region1, String region2) {
        HashMap<String, String> childParentMap = new HashMap<>();

        // Step 1: Build child-parent map
        for (List<String> innerList : regions) {
            String parent = innerList.get(0);
            for (int i = 1; i < innerList.size(); i++) {
                childParentMap.put(innerList.get(i), parent);
            }
        }

        // Step 2: Collect region1 and all its ancestors
        Set<String> ancestors = new HashSet<>();
        while (region1 != null) {
            ancestors.add(region1);
            region1 = childParentMap.get(region1);
        }

        // Step 3: Traverse region2's ancestors to find the first match
        while (region2 != null) {
            if (ancestors.contains(region2)) {
                return region2;
            }
            region2 = childParentMap.get(region2);
        }

        return null;
    }

    // Main method to test
    public static void main(String[] args) {
        SmallestCommonRegion solution = new SmallestCommonRegion();

        List<List<String>> regions = Arrays.asList(
                Arrays.asList("Earth", "North America", "South America"),
                Arrays.asList("North America", "United States", "Canada"),
                Arrays.asList("United States", "New York", "Boston"),
                Arrays.asList("Canada", "Ontario", "Quebec"),
                Arrays.asList("South America", "Brazil")
        );

        String region1 = "Quebec";
        String region2 = "New York";

        String result = solution.findSmallestRegion(regions, region1, region2);

        System.out.println("Smallest Common Region between " + region1 + " and " + region2 + " is: " + result);
    }
}
