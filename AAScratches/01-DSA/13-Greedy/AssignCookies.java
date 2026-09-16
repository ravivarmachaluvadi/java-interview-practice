/**
 * Problem: Given arrays representing the greed factor of each child and the size of available cookies,
 * determine the maximum number of children that can be satisfied by assigning at most one cookie per child
 * such that a child's greed is less than or equal to the cookie's size.
 *
 * Approach: Sort both arrays. Use two pointers to iterate through cookies and greed values.
 * For each cookie, if it satisfies the current child (greed <= cookieSize), assign it and move to next child.
 * Continue until all children or cookies are processed.
 *
 * Time Complexity: O(n log n + m log m) due to sorting, where n = greed.length and m = cookieSize.length.
 * Space Complexity: O(1) auxiliary space (in-place sort).
 */
import java.util.Arrays;

class AssignCookies {
    public static int findContentChildren(int[] greed, int[] cookieSize) {
        int n = greed.length;
        int m = cookieSize.length;
        Arrays.sort(greed);
        Arrays.sort(cookieSize);
        int l = 0;
        int r = 0;

        while (l < m && r < n) {
            if (greed[r] <= cookieSize[l]) {
                r++;
            }
            l++;
        }
        return r;
    }

    public static void main(String[] args) {
        int[] greed = {1, 5, 3, 3, 4};
        int[] cookieSize = {4, 2, 1, 2, 1, 3};

        System.out.print("Array Representing Greed: ");
        for (int i = 0; i < greed.length; i++) {
            System.out.print(greed[i] + " ");
        }
        System.out.println();

        System.out.print("Array Representing Cookie Size: ");
        for (int i = 0; i < cookieSize.length; i++) {
            System.out.print(cookieSize[i] + " ");
        }

        int ans = findContentChildren(greed, cookieSize);

        System.out.println();
        System.out.println("No. of kids assigned cookies " + ans);
        System.out.println();
    }
}
