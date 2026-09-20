/*
 * =====================================================================
 *  Smallest Common Region                          LeetCode 1257 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given a list of region lists. In each list the FIRST name is a region and every
 *   name after it is a direct sub-region of that first name. The whole input therefore forms
 *   one hierarchy (a tree) with a single outermost region at the top.
 *   Given two region names, return the smallest region that contains both of them.
 *   A region is considered to contain itself.
 *
 * EXAMPLE
 *   regions = [[Earth, North America, South America],
 *              [North America, United States, Canada],
 *              [United States, New York, Boston],
 *              [Canada, Ontario, Quebec],
 *              [South America, Brazil]]
 *   ("Quebec", "New York")  ->  "North America"   both sit under it, nothing smaller does
 *   ("Earth",  "Quebec")    ->  "Earth"           an ancestor is its own answer
 *   ("Quebec", "Quebec")    ->  "Quebec"          a region contains itself
 *   ("Quebec", "Brazil")    ->  "Earth"           only the top region covers both
 *
 * APPROACH  (LCA over a child -> parent map)
 *   1. Walk every input row once and record child -> parent for each sub-region.
 *      The top region never appears as a child, so map.get(top) is null: that is our stop mark.
 *   2. Climb from region1 to the top, putting region1 and every ancestor into a HashSet.
 *   3. Climb from region2 to the top. The FIRST name already in that set is the answer,
 *      because we meet the set at the deepest point where the two upward paths join.
 *
 * KEY INSIGHT
 *   There is no tree object here at all - only names. Build the parent map and the problem
 *   collapses into the classic "lowest common ancestor with parent pointers", which is the
 *   same as finding where two linked lists merge. Recognise the pattern: whenever a hierarchy
 *   is given as edges or rows, a child -> parent map plus one upward walk usually solves it.
 *
 * COMPLEXITY
 *   Time  O(n + h)  n = total names across all rows to build the map, h = height of the walks
 *   Space O(n)      the parent map, plus the ancestor set which is at most h entries
 *
 * INTERVIEW FOLLOW-UPS
 *   - Answer many queries on the same hierarchy: precompute depths, or binary lifting O(log h).
 *   - Solve it in O(1) extra space: measure both depths, drop the deeper one, then walk together.
 *   - What if the two regions are in disconnected hierarchies? Both walks end at different tops,
 *     the set is never hit, and the method returns null.
 *   - Same trick on LeetCode 1650 (LCA III) where nodes carry real parent pointers.
 *
 * RUN
 *   main() runs 4 cases (typical, ancestor-of-the-other, same region twice, only-the-root)
 *   and prints actual vs expected, then repeats 3 of them against the no-extra-set version.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SmallestCommonRegion {

    public String findSmallestRegion(List<List<String>> regions, String region1, String region2) {
        Map<String, String> childToParent = buildChildToParent(regions);

        // Every ancestor of region1, including region1 itself.
        Set<String> ancestorsOfRegion1 = new HashSet<>();
        for (String node = region1; node != null; node = childToParent.get(node)) {
            ancestorsOfRegion1.add(node);
        }

        // First name on region2's upward path that region1 also passes through.
        for (String node = region2; node != null; node = childToParent.get(node)) {
            if (ancestorsOfRegion1.contains(node)) {
                return node;
            }
        }
        return null; // only possible if the two names are not in the same hierarchy
    }

    private Map<String, String> buildChildToParent(List<List<String>> regions) {
        Map<String, String> childToParent = new HashMap<>();
        for (List<String> row : regions) {
            String parent = row.get(0);          // first name in a row is the container
            for (int i = 1; i < row.size(); i++) {
                childToParent.put(row.get(i), parent);
            }
        }
        return childToParent;
    }

    /**
     * Same answer without the HashSet: level the two paths, then step up in lockstep.
     * Shown because "now do it in O(1) extra space" is the standard follow-up.
     */
    public String findSmallestRegionNoSet(List<List<String>> regions,
                                          String region1, String region2) {
        Map<String, String> childToParent = buildChildToParent(regions);

        int depth1 = depthOf(region1, childToParent);
        int depth2 = depthOf(region2, childToParent);

        String a = region1;
        String b = region2;
        while (depth1 > depth2) {            // drop the deeper one until both are level
            a = childToParent.get(a);
            depth1--;
        }
        while (depth2 > depth1) {
            b = childToParent.get(b);
            depth2--;
        }
        while (a != null && !a.equals(b)) {  // now they must meet at the same step
            a = childToParent.get(a);
            b = childToParent.get(b);
        }
        return a;
    }

    private int depthOf(String region, Map<String, String> childToParent) {
        int depth = 0;
        for (String node = region;
             childToParent.get(node) != null;
             node = childToParent.get(node)) {
            depth++;
        }
        return depth;
    }

    public static void main(String[] args) {
        SmallestCommonRegion solution = new SmallestCommonRegion();

        List<List<String>> regions = new ArrayList<>(Arrays.asList(
                Arrays.asList("Earth", "North America", "South America"),
                Arrays.asList("North America", "United States", "Canada"),
                Arrays.asList("United States", "New York", "Boston"),
                Arrays.asList("Canada", "Ontario", "Quebec"),
                Arrays.asList("South America", "Brazil")
        ));

        print("case 1 typical     ",
                solution.findSmallestRegion(regions, "Quebec", "New York"),
                "North America");
        print("case 2 ancestor    ",
                solution.findSmallestRegion(regions, "Earth", "Quebec"),
                "Earth");
        print("case 3 same region ",
                solution.findSmallestRegion(regions, "Quebec", "Quebec"),
                "Quebec");
        print("case 4 only the top",
                solution.findSmallestRegion(regions, "Quebec", "Brazil"),
                "Earth");

        System.out.println("-- follow-up version with no extra set --");
        print("case 5 typical     ",
                solution.findSmallestRegionNoSet(regions, "Quebec", "New York"),
                "North America");
        print("case 6 ancestor    ",
                solution.findSmallestRegionNoSet(regions, "Earth", "Quebec"),
                "Earth");
        print("case 7 only the top",
                solution.findSmallestRegionNoSet(regions, "Quebec", "Brazil"),
                "Earth");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
