/*
 * =====================================================================
 *  P014 Two Pointers: Walk Two Sequences   Canonical LC 88 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 88, Merge Sorted Array)
 *   nums1 has length m + n: m sorted values followed by n empty slots. nums2 has n sorted
 *   values. Merge nums2 into nums1 in place so nums1 is sorted.
 *
 * EXAMPLE
 *   nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3  ->  [1,2,2,3,5,6]
 *   nums1 = [0],           m = 0, nums2 = [1],     n = 1  ->  [1]
 *
 * RECOGNIZE WHEN
 *   - Two sorted arrays / strings / lists to merge, intersect, diff or compare.
 *   - "Is s a subsequence of t", "compare after applying backspaces", "versions".
 *   - One pointer per input, each only moves forward (or only backward).
 *   Not this if: more than two inputs -> P044_KWayMerge (heap); linked lists ->
 *   P033_DummyHeadMerge; unsorted inputs -> hash them (P020_ComplementLookup).
 *
 * TEMPLATE
 *   i = 0, j = 0
 *   while i < len(a) and j < len(b):
 *       compare a[i] with b[j]; act; advance the pointer(s) whose element is "used up"
 *   drain whatever is left of a or b
 *   // in place with spare room at the end: walk BOTH from the back, write at the back
 *
 * APPROACH
 *   1. Write from the back: k = m + n - 1 is the next slot to fill with the biggest value.
 *   2. Take the bigger of nums1[i] and nums2[j]; move that pointer and k left.
 *   3. When nums2 is empty, the rest of nums1 is already in place.
 *
 * KEY INSIGHT
 *   Filling from the front would overwrite unread nums1 values; the empty slots are at
 *   the BACK, so writing largest-first can never overwrite anything still needed.
 *
 * COMPLEXITY
 *   Time O(m + n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 392  Is Subsequence           advance i only on a match; j always advances
 *   [coded] LC 350  Intersection of Arrays II  sort both; equal -> take and move both
 *   [coded] LC 844  Backspace String Compare walk from the BACK, skipping chars that a
 *                                            later '#' deletes; O(1) space
 *           LC 408  Valid Word Abbreviation  a number in abbr jumps the word pointer
 *           LC 165  Compare Version Numbers  parse one revision at a time from each side
 *           LC 2540 Minimum Common Value     first equal pair in two sorted arrays
 *           LC 986  Interval Intersections   -> P048_MergeIntervals
 *           LC 1868 Product of Two RLE Arrays  consume min(run lengths) from both runs
 *
 * PITFALLS
 *   - Forgetting to drain the leftover tail of one input.
 *   - LC 844 from the back: count pending '#' and skip that many real characters.
 *   - LC 392 follow-up (many s against one t): pre-index t's positions per char and
 *     binary search the next position.
 *
 * DEEP DIVE
 *   D03_MergeTwoSortedArrays, B02_IsSubsequence, B03_ValidWordAbbreviation
 *   (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WalkTwoSequences {

    // Canonical LC 88.
    static int[] merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;
        int j = n - 1;
        int k = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) {
                nums1[k--] = nums1[i--];
            } else {
                nums1[k--] = nums2[j--];
            }
        }
        return nums1;
    }

    // LC 392.
    static boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; j < t.length() && i < s.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
        }
        return i == s.length();
    }

    // LC 350: sort, then walk both; equal values are part of the intersection.
    static List<Integer> intersect(int[] a, int[] b) {
        Arrays.sort(a);
        Arrays.sort(b);
        List<Integer> out = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                out.add(a[i]);
                i++;
                j++;
            } else if (a[i] < b[j]) {
                i++;
            } else {
                j++;
            }
        }
        return out;
    }

    // LC 844: compare the surviving characters from the back, one at a time.
    static boolean backspaceCompare(String s, String t) {
        int i = s.length() - 1;
        int j = t.length() - 1;
        while (i >= 0 || j >= 0) {
            i = nextSurvivor(s, i);
            j = nextSurvivor(t, j);
            if (i < 0 || j < 0) {
                return i < 0 && j < 0;
            }
            if (s.charAt(i) != t.charAt(j)) {
                return false;
            }
            i--;
            j--;
        }
        return true;
    }

    // Index of the last character at or before i that no '#' deletes, or -1.
    private static int nextSurvivor(String s, int i) {
        int skip = 0;
        while (i >= 0) {
            if (s.charAt(i) == '#') {
                skip++;
            } else if (skip > 0) {
                skip--;
            } else {
                return i;
            }
            i--;
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 88 m=3 n=3",
                Arrays.toString(merge(new int[]{1, 2, 3, 0, 0, 0}, 3, new int[]{2, 5, 6}, 3)),
                "[1, 2, 2, 3, 5, 6]");
        check("LC 88 n=0", Arrays.toString(merge(new int[]{1}, 1, new int[]{}, 0)), "[1]");
        check("LC 88 m=0", Arrays.toString(merge(new int[]{0}, 0, new int[]{1}, 1)), "[1]");
        check("LC 88 nums2 all smaller",
                Arrays.toString(merge(new int[]{4, 5, 0, 0}, 2, new int[]{1, 2}, 2)),
                "[1, 2, 4, 5]");

        check("LC 392 abc in ahbgdc", isSubsequence("abc", "ahbgdc"), true);
        check("LC 392 axc in ahbgdc", isSubsequence("axc", "ahbgdc"), false);
        check("LC 392 empty s", isSubsequence("", "ahbgdc"), true);

        check("LC 350 [1,2,2,1] [2,2]",
                intersect(new int[]{1, 2, 2, 1}, new int[]{2, 2}), "[2, 2]");
        check("LC 350 [4,9,5] [9,4,9,8,4]",
                intersect(new int[]{4, 9, 5}, new int[]{9, 4, 9, 8, 4}), "[4, 9]");

        check("LC 844 ab#c vs ad#c", backspaceCompare("ab#c", "ad#c"), true);
        check("LC 844 ab## vs c#d#", backspaceCompare("ab##", "c#d#"), true);
        check("LC 844 a#c vs b", backspaceCompare("a#c", "b"), false);
        check("LC 844 bxj##tw vs bxo#j##tw", backspaceCompare("bxj##tw", "bxo#j##tw"), true);
    }
}
