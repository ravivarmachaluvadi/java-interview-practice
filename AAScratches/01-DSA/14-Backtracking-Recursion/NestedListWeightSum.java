import java.util.*;

// LeetCode link: https://leetcode.com/problems/nested-list-weight-sum/

/**
 * Interface representing NestedInteger. This is typically provided by LeetCode.
 * You can assume it works as described.
 */
interface NestedInteger {
    boolean isInteger();
    Integer getInteger();
    List<NestedInteger> getList();
}

class NestedListWeightSum {

    // Method to calculate the weighted sum of the nested list
    public int depthSum(List<NestedInteger> nestedList) {
        return helper(nestedList, 1);
    }

    // Recursive helper method
    private int helper(List<NestedInteger> list, int depth) {
        int sum = 0;
        for (NestedInteger ni : list) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * depth;
            } else {
                sum += helper(ni.getList(), depth + 1);
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        // Example input: [[1,1],2,[1,1]]
        // Example output: 10

        // Mock implementation of NestedInteger for testing
        class NestedIntegerImpl implements NestedInteger {
            private Integer value;
            private List<NestedInteger> list;

            public NestedIntegerImpl(Integer value) {
                this.value = value;
                this.list = null;
            }

            public NestedIntegerImpl(List<NestedInteger> list) {
                this.list = list;
                this.value = null;
            }

            @Override
            public boolean isInteger() {
                return value != null;
            }

            @Override
            public Integer getInteger() {
                return value;
            }

            @Override
            public List<NestedInteger> getList() {
                return list;
            }
        }

        // Test case
        NestedListWeightSum solver = new NestedListWeightSum();

        List<NestedInteger> nestedList = new ArrayList<>();
        List<NestedInteger> sublist1 = new ArrayList<>();
        sublist1.add(new NestedIntegerImpl(1));
        sublist1.add(new NestedIntegerImpl(1));
        nestedList.add(new NestedIntegerImpl(sublist1));
        nestedList.add(new NestedIntegerImpl(2));
        List<NestedInteger> sublist2 = new ArrayList<>();
        sublist2.add(new NestedIntegerImpl(1));
        sublist2.add(new NestedIntegerImpl(1));
        nestedList.add(new NestedIntegerImpl(sublist2));

        int result = solver.depthSum(nestedList);
        System.out.println("Weighted Sum: " + result); // Output: 10
    }
}
