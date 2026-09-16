import java.util.*;

/**
 * Function to check if there is a subset of
 * <p>
 * arr with sum equal to 'target' using recursion
 */
class SubsetSumEqualsToTarget {
    private boolean func(int ind, int target, int[] arr) {
        if (target == 0) return true;

        if (ind == 0) return arr[0] == target;

        boolean notTaken = func(ind - 1, target, arr);

        boolean taken = false;
        if (arr[ind] <= target)
            taken = func(ind - 1, target - arr[ind], arr);

        return notTaken || taken;
    }

    public boolean isSubsetSum(int[] arr, int target) {
        return func(arr.length - 1, target, arr);
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4};
        int target = 4;

        SubsetSumEqualsToTarget sol = new SubsetSumEqualsToTarget();
        if (sol.isSubsetSum(arr, target))
            System.out.println("Subset with the given target found");
        else
            System.out.println("Subset with the given target not found");
    }
}
