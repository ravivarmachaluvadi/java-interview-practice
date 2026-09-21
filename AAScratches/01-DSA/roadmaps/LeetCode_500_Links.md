# LeetCode 500 Links

500 LeetCode problems collected for practice. The **Solved here** column links
the practice file in this repo that answers each one.

Converted from the comment block in
`AAScratches/_archive/original-txt/Leetcode500Links.java`. That folder was deleted
in commit `9474f16`; recover the original with
`git show 9474f16^:AAScratches/_archive/original-txt/Leetcode500Links.java`.

## At a glance

| Key | Value |
| --- | --- |
| Problems listed | 500 (no duplicates) |
| Solved in this repo | **234** |
| Partly covered (marked `*`) | 7 |
| Not started | 259 |
| Practice files searched | 490 under `AAScratches/01-DSA/` |
| How a match was decided | the problem name, and the LeetCode number in each file's header comment |

A `*` after a link means the file covers the same technique but is not the same
problem statement -- see [Partial coverage, and why](#partial-coverage-and-why).
Links point at `leetcode.com/problems/<slug>`, the canonical problem URL.

## Sections

| Range | Solved | Partly | Open |
| --- | --- | --- | --- |
| [1-50](#problems-1-50) | 48 | 1 | 1 |
| [51-100](#problems-51-100) | 36 | 1 | 13 |
| [101-150](#problems-101-150) | 25 | 0 | 25 |
| [151-200](#problems-151-200) | 24 | 0 | 26 |
| [201-250](#problems-201-250) | 19 | 1 | 30 |
| [251-300](#problems-251-300) | 19 | 1 | 30 |
| [301-350](#problems-301-350) | 20 | 0 | 30 |
| [351-400](#problems-351-400) | 14 | 2 | 34 |
| [401-450](#problems-401-450) | 15 | 0 | 35 |
| [451-500](#problems-451-500) | 14 | 1 | 35 |

---

## Problems 1-50

| # | Problem | Solved here |
| --- | --- | --- |
| 1 | [Two Sum](https://leetcode.com/problems/two-sum) | [A02_TwoSum](../05-Hashing-Prefix-Sum/A02_TwoSum.java) |
| 2 | [Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters) | [C03_LongestSubStringWithoutRepeatingCharacter](../02-Two-Pointers-Sliding-Window/C03_LongestSubStringWithoutRepeatingCharacter.java) |
| 3 | [Maximum Subarray](https://leetcode.com/problems/maximum-subarray) | [C01_KadaneSAlgorithm](../01-Arrays/C01_KadaneSAlgorithm.java) |
| 4 | [Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water) | [D01_TrappingRainWater](../02-Two-Pointers-Sliding-Window/D01_TrappingRainWater.java) |
| 5 | [Add Two Numbers](https://leetcode.com/problems/add-two-numbers) | [C01_AddTwoNumbers](../06-Linked-List/C01_AddTwoNumbers.java) |
| 6 | [3Sum](https://leetcode.com/problems/3sum) | [C02_Three3Sum](../02-Two-Pointers-Sliding-Window/C02_Three3Sum.java) |
| 7 | [Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring) | [C12_LongestPalindrome](../12-Dynamic-Programming/C12_LongestPalindrome.java) |
| 8 | [Median Of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays) | [D02_MedianOfTwoSortedArrays](../03-Binary-Search/D02_MedianOfTwoSortedArrays.java) |
| 9 | [Container With Most Water](https://leetcode.com/problems/container-with-most-water) | [C01_ContainerWithMostWater](../02-Two-Pointers-Sliding-Window/C01_ContainerWithMostWater.java) |
| 10 | [Best Time To Buy And Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock) | [B03_BestTimeToBuyAndSellStock](../01-Arrays/B03_BestTimeToBuyAndSellStock.java) |
| 11 | [Search In Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array) | [C06_SearchInRotatedSortedArray](../03-Binary-Search/C06_SearchInRotatedSortedArray.java) |
| 12 | [Number Of Islands](https://leetcode.com/problems/number-of-islands) | [B02_NumberOfIslands](../11-Graphs/B02_NumberOfIslands.java) |
| 13 | [Lru Cache](https://leetcode.com/problems/lru-cache) | [C05_LRUCache](../19-Design-Data-Structures/C05_LRUCache.java) |
| 14 | [Merge Intervals](https://leetcode.com/problems/merge-intervals) | [A02_MergeIntervals](../15-Intervals/A02_MergeIntervals.java) |
| 15 | [Generate Parentheses](https://leetcode.com/problems/generate-parentheses) | [C01_GenerateParenthesis](../14-Backtracking-Recursion/C01_GenerateParenthesis.java) |
| 16 | [Find The Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number) | [C15_FindDuplicate](../01-Arrays/C15_FindDuplicate.java) |
| 17 | [Product Of Array Except Self](https://leetcode.com/problems/product-of-array-except-self) | [C05_ProductExceptSelf](../01-Arrays/C05_ProductExceptSelf.java) |
| 18 | [Valid Parentheses](https://leetcode.com/problems/valid-parentheses) | [A02_ValidParentheses](../07-Stack-Queue-Monotonic/A02_ValidParentheses.java) |
| 19 | [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k) | [C01_CountSubarraySumEqualsK](../05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java) |
| 20 | [House Robber](https://leetcode.com/problems/house-robber) | [B03_HouseRobber](../12-Dynamic-Programming/B03_HouseRobber.java) |
| 21 | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence) | [A08_LongestIncreasingSubsequence](../12-Dynamic-Programming/A08_LongestIncreasingSubsequence.java) |
| 22 | [Reverse Linked List](https://leetcode.com/problems/reverse-linked-list) | [A02_ReverseLinkedList](../06-Linked-List/A02_ReverseLinkedList.java) |
| 23 | [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray) | [C02_MaxProductSubArray](../12-Dynamic-Programming/C02_MaxProductSubArray.java) |
| 24 | [Merge K Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists) | [D02_MergeKLists](../06-Linked-List/D02_MergeKLists.java) |
| 25 | [Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists) | [A04_MergeTwoSortedLists](../06-Linked-List/A04_MergeTwoSortedLists.java) |
| 26 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs) | [A02_ClimbingStairs](../12-Dynamic-Programming/A02_ClimbingStairs.java) |
| 27 | [Coin Change](https://leetcode.com/problems/coin-change) | [C08_CoinChangeMinimum](../12-Dynamic-Programming/C08_CoinChangeMinimum.java) |
| 28 | [Jump Game](https://leetcode.com/problems/jump-game) | [C08_CanJump](../13-Greedy/C08_CanJump.java) |
| 29 | [Word Break](https://leetcode.com/problems/word-break) | -- |
| 30 | [Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring) | [D02_MinimumWindowSubstring](../02-Two-Pointers-Sliding-Window/D02_MinimumWindowSubstring.java) |
| 31 | [Find First And Last Position Of Element In Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array) | [A02_FirstAndLastOccurrence](../03-Binary-Search/A02_FirstAndLastOccurrence.java) |
| 32 | [Permutations](https://leetcode.com/problems/permutations) | [A05_Permutations](../14-Backtracking-Recursion/A05_Permutations.java) `*` |
| 33 | [Combination Sum](https://leetcode.com/problems/combination-sum) | [C03_CombinationSum](../14-Backtracking-Recursion/C03_CombinationSum.java) |
| 34 | [Letter Combinations Of A Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number) | [B02_LetterCombinations](../14-Backtracking-Recursion/B02_LetterCombinations.java) |
| 35 | [Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree) | [C01_ValidateBST](../09-Trees-BST/C01_ValidateBST.java) |
| 36 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum) | [D04_SlidingWindowMaximum](../07-Stack-Queue-Monotonic/D04_SlidingWindowMaximum.java) |
| 37 | [Lowest Common Ancestor Of A Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree) | [C12_LCA](../09-Trees-BST/C12_LCA.java) |
| 38 | [Symmetric Tree](https://leetcode.com/problems/symmetric-tree) | [B03_SymmetricTree](../09-Trees-BST/B03_SymmetricTree.java) |
| 39 | [Next Permutation](https://leetcode.com/problems/next-permutation) | [C10_NextGreaterPermutation](../01-Arrays/C10_NextGreaterPermutation.java) |
| 40 | [Sort Colors](https://leetcode.com/problems/sort-colors) | [C08_Sort012](../01-Arrays/C08_Sort012.java) |
| 41 | [Remove Nth Node From End Of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list) | [C02_DeleteNthNodefromEnd](../06-Linked-List/C02_DeleteNthNodefromEnd.java) |
| 42 | [Single Number](https://leetcode.com/problems/single-number) | [A03_SingleNumber](../17-Math-Bit-Manipulation/A03_SingleNumber.java) |
| 43 | [Largest Rectangle In Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram) | [D02_LargestRectangleArea](../07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java) |
| 44 | [First Missing Positive](https://leetcode.com/problems/first-missing-positive) | [D01_FirstMissingPositive](../01-Arrays/D01_FirstMissingPositive.java) |
| 45 | [Course Schedule](https://leetcode.com/problems/course-schedule) | [B10_CourseSchedule](../11-Graphs/B10_CourseSchedule.java) |
| 46 | [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum) | [D01_BinaryTreeMaxPathSum](../09-Trees-BST/D01_BinaryTreeMaxPathSum.java) |
| 47 | [Word Search](https://leetcode.com/problems/word-search) | [C05_WordSearch](../14-Backtracking-Recursion/C05_WordSearch.java) |
| 48 | [Subsets](https://leetcode.com/problems/subsets) | [A04_Subsets](../14-Backtracking-Recursion/A04_Subsets.java) |
| 49 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence) | [D01_LongestConsecutiveSequence](../05-Hashing-Prefix-Sum/D01_LongestConsecutiveSequence.java) |
| 50 | [Kth Largest Element In An Array](https://leetcode.com/problems/kth-largest-element-in-an-array) | [C01_QuickSelect](../18-Sorting-Searching-Algorithms/C01_QuickSelect.java) |

## Problems 51-100

| # | Problem | Solved here |
| --- | --- | --- |
| 51 | [Group Anagrams](https://leetcode.com/problems/group-anagrams) | [C05_GroupAnagrams](../05-Hashing-Prefix-Sum/C05_GroupAnagrams.java) |
| 52 | [Move Zeroes](https://leetcode.com/problems/move-zeroes) | [A02_MoveZeroes](../02-Two-Pointers-Sliding-Window/A02_MoveZeroes.java) |
| 53 | [Intersection Of Two Linked Lists](https://leetcode.com/problems/intersection-of-two-linked-lists) | [B02_IntersectionOfTwoLinkedLists](../06-Linked-List/B02_IntersectionOfTwoLinkedLists.java) |
| 54 | [Majority Element](https://leetcode.com/problems/majority-element) | [B09_MajorityElement](../01-Arrays/B09_MajorityElement.java) |
| 55 | [Unique Paths](https://leetcode.com/problems/unique-paths) | [B01_UniquePaths](../12-Dynamic-Programming/B01_UniquePaths.java) |
| 56 | [Rotate Image](https://leetcode.com/problems/rotate-image) | [A01_MatrixRotate90Degree](../16-Matrix/A01_MatrixRotate90Degree.java) |
| 57 | [Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list) | [B03_PalindromeLinkedList](../06-Linked-List/B03_PalindromeLinkedList.java) |
| 58 | [Edit Distance](https://leetcode.com/problems/edit-distance) | [C11_EditDistance](../12-Dynamic-Programming/C11_EditDistance.java) |
| 59 | [Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching) | [D03_RegularExpressionMatchingMemo](../12-Dynamic-Programming/D03_RegularExpressionMatchingMemo.java) |
| 60 | [Construct Binary Tree From Preorder And Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal) | [C23_ConstructBinaryTreeFromINPre](../09-Trees-BST/C23_ConstructBinaryTreeFromINPre.java) |
| 61 | [Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree) | [B01_InvertTree](../09-Trees-BST/B01_InvertTree.java) |
| 62 | [Rotate Array](https://leetcode.com/problems/rotate-array) | [C09_RotateArray](../01-Arrays/C09_RotateArray.java) |
| 63 | [Decode String](https://leetcode.com/problems/decode-string) | [C13_DecodeString](../07-Stack-Queue-Monotonic/C13_DecodeString.java) |
| 64 | [Min Stack](https://leetcode.com/problems/min-stack) | [A02_MinStack](../19-Design-Data-Structures/A02_MinStack.java) |
| 65 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements) | [C01_topKFrequent](../08-Heap-Priority-Queue/C01_topKFrequent.java) |
| 66 | [Copy List With Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer) | [C12_CopyRandomList](../06-Linked-List/C12_CopyRandomList.java) |
| 67 | [Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses) | [D01_LongestValidParentheses](../07-Stack-Queue-Monotonic/D01_LongestValidParentheses.java) |
| 68 | [Path Sum Iii](https://leetcode.com/problems/path-sum-iii) | -- |
| 69 | [Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal) | [A03_LevelOrderTraversal](../09-Trees-BST/A03_LevelOrderTraversal.java) |
| 70 | [Diameter Of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree) | [B09_DiameterOfBinaryTree](../09-Trees-BST/B09_DiameterOfBinaryTree.java) |
| 71 | [Jump Game Ii](https://leetcode.com/problems/jump-game-ii) | [C09_JumpGameII](../13-Greedy/C09_JumpGameII.java) |
| 72 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum) | -- |
| 73 | [Binary Tree Inorder Traversal](https://leetcode.com/problems/binary-tree-inorder-traversal) | [A06_PrePostInorderInOneTraversal](../09-Trees-BST/A06_PrePostInorderInOneTraversal.java) |
| 74 | [Word Ladder](https://leetcode.com/problems/word-ladder) | [D01_WordLadder](../11-Graphs/D01_WordLadder.java) |
| 75 | [Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees) | -- |
| 76 | [Linked List Cycle](https://leetcode.com/problems/linked-list-cycle) | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) `*` |
| 77 | [Maximal Square](https://leetcode.com/problems/maximal-square) | -- |
| 78 | [Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix) | [B13_LongestCommonPrefix](../04-Strings/B13_LongestCommonPrefix.java) |
| 79 | [Best Time To Buy And Sell Stock Ii](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii) | [C01_BestTimeToBuyAndSellStockII](../13-Greedy/C01_BestTimeToBuyAndSellStockII.java) |
| 80 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum) | -- |
| 81 | [Search A 2D Matrix Ii](https://leetcode.com/problems/search-a-2d-matrix-ii) | -- |
| 82 | [Reverse Integer](https://leetcode.com/problems/reverse-integer) | -- |
| 83 | [Search Insert Position](https://leetcode.com/problems/search-insert-position) | -- |
| 84 | [Flatten Binary Tree To Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list) | [C25_BinaryTreeToLinkedList](../09-Trees-BST/C25_BinaryTreeToLinkedList.java) |
| 85 | [Implement Trie Prefix Tree](https://leetcode.com/problems/implement-trie-prefix-tree) | [A01_Trie](../10-Trie/A01_Trie.java) |
| 86 | [Daily Temperatures](https://leetcode.com/problems/daily-temperatures) | [C06_DailyTemperatures](../07-Stack-Queue-Monotonic/C06_DailyTemperatures.java) |
| 87 | [Partition Labels](https://leetcode.com/problems/partition-labels) | [C05_PartitionLabels](../13-Greedy/C05_PartitionLabels.java) |
| 88 | [Perfect Squares](https://leetcode.com/problems/perfect-squares) | -- |
| 89 | [Decode Ways](https://leetcode.com/problems/decode-ways) | [C01_NumDecodings](../12-Dynamic-Programming/C01_NumDecodings.java) |
| 90 | [Find Median From Data Stream](https://leetcode.com/problems/find-median-from-data-stream) | [D01_MedianOfStream](../08-Heap-Priority-Queue/D01_MedianOfStream.java) |
| 91 | [Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle) | -- |
| 92 | [Task Scheduler](https://leetcode.com/problems/task-scheduler) | -- |
| 93 | [Reverse Nodes In K Group](https://leetcode.com/problems/reverse-nodes-in-k-group) | [D01_ReverseListInKGroups](../06-Linked-List/D01_ReverseListInKGroups.java) |
| 94 | [Spiral Matrix](https://leetcode.com/problems/spiral-matrix) | [C02_SpiralTraversalOfMatrix](../16-Matrix/C02_SpiralTraversalOfMatrix.java) |
| 95 | [Target Sum](https://leetcode.com/problems/target-sum) | [C07_TargetSumCountWays](../12-Dynamic-Programming/C07_TargetSumCountWays.java) |
| 96 | [Find All Numbers Disappeared In An Array](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array) | [B10_FindAllMissedNumbers](../01-Arrays/B10_FindAllMissedNumbers.java) |
| 97 | [Course Schedule Ii](https://leetcode.com/problems/course-schedule-ii) | -- |
| 98 | [Merge Two Binary Trees](https://leetcode.com/problems/merge-two-binary-trees) | -- |
| 99 | [Serialize And Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree) | [D02_SerializeAndDeserialiseBinaryTree](../09-Trees-BST/D02_SerializeAndDeserialiseBinaryTree.java) |
| 100 | [Linked List Cycle Ii](https://leetcode.com/problems/linked-list-cycle-ii) | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) |

## Problems 101-150

| # | Problem | Solved here |
| --- | --- | --- |
| 101 | [House Robber Iii](https://leetcode.com/problems/house-robber-iii) | -- |
| 102 | [Sort List](https://leetcode.com/problems/sort-list) | [C11_SortList](../06-Linked-List/C11_SortList.java) |
| 103 | [Find All Anagrams In A String](https://leetcode.com/problems/find-all-anagrams-in-a-string) | -- |
| 104 | [Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view) | [C18_RightView](../09-Trees-BST/C18_RightView.java) |
| 105 | [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings) | -- |
| 106 | [Maximum Depth Of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree) | [A02_HeightOfBinaryTree](../09-Trees-BST/A02_HeightOfBinaryTree.java) |
| 107 | [Rotting Oranges](https://leetcode.com/problems/rotting-oranges) | [B06_RottenOranges](../11-Graphs/B06_RottenOranges.java) |
| 108 | [Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes) | [C01_SetMatrixZeroes](../16-Matrix/C01_SetMatrixZeroes.java) |
| 109 | [Convert Sorted Array To Binary Search Tree](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree) | [B15_SortedArrayToBST](../09-Trees-BST/B15_SortedArrayToBST.java) |
| 110 | [Search A 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix) | [C01_Search2DMatrix](../03-Binary-Search/C01_Search2DMatrix.java) |
| 111 | [Find Minimum In Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array) | [C04_MinIndexInRotatedSortedArray](../03-Binary-Search/C04_MinIndexInRotatedSortedArray.java) |
| 112 | [Populating Next Right Pointers In Each Node](https://leetcode.com/problems/populating-next-right-pointers-in-each-node) | -- |
| 113 | [Remove Duplicates From Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array) | [A03_RemoveDuplicates](../02-Two-Pointers-Sliding-Window/A03_RemoveDuplicates.java) |
| 114 | [Kth Smallest Element In A Bst](https://leetcode.com/problems/kth-smallest-element-in-a-bst) | -- |
| 115 | [Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum) | [C11_SmallestSubarraySum](../01-Arrays/C11_SmallestSubarraySum.java) |
| 116 | [Find All Duplicates In An Array](https://leetcode.com/problems/find-all-duplicates-in-an-array) | [C14_FindAllDuplicatesInAnArray](../01-Arrays/C14_FindAllDuplicatesInAnArray.java) |
| 117 | [Burst Balloons](https://leetcode.com/problems/burst-balloons) | -- |
| 118 | [Counting Bits](https://leetcode.com/problems/counting-bits) | -- |
| 119 | [All Nodes Distance K In Binary Tree](https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree) | [C19_BinaryTree](../09-Trees-BST/C19_BinaryTree.java) |
| 120 | [Swap Nodes In Pairs](https://leetcode.com/problems/swap-nodes-in-pairs) | -- |
| 121 | [Reverse Linked List Ii](https://leetcode.com/problems/reverse-linked-list-ii) | -- |
| 122 | [Word Search Ii](https://leetcode.com/problems/word-search-ii) | -- |
| 123 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence) | [A07_CommonSubSequence](../12-Dynamic-Programming/A07_CommonSubSequence.java) |
| 124 | [Kth Smallest Element In A Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix) | -- |
| 125 | [4Sum](https://leetcode.com/problems/4sum) | -- |
| 126 | [Best Time To Buy And Sell Stock Iii](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii) | -- |
| 127 | [Best Time To Buy And Sell Stock With Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown) | -- |
| 128 | [Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning) | [C15_PalindromePartitioning](../12-Dynamic-Programming/C15_PalindromePartitioning.java) |
| 129 | [Reorder List](https://leetcode.com/problems/reorder-list) | [C10_ReorderList](../06-Linked-List/C10_ReorderList.java) |
| 130 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs) | -- |
| 131 | [Max Area Of Island](https://leetcode.com/problems/max-area-of-island) | -- |
| 132 | [Count Of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self) | -- |
| 133 | [3Sum Closest](https://leetcode.com/problems/3sum-closest) | -- |
| 134 | [Binary Tree Zigzag Level Order Traversal](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal) | [C20_BinaryTreeZigzagTraversal](../09-Trees-BST/C20_BinaryTreeZigzagTraversal.java) |
| 135 | [Find Peak Element](https://leetcode.com/problems/find-peak-element) | [C03_FindPeakElement](../03-Binary-Search/C03_FindPeakElement.java) |
| 136 | [Insert Delete Getrandom O1](https://leetcode.com/problems/insert-delete-getrandom-o1) | [C03_InsertDeleteGetRandomOof1](../19-Design-Data-Structures/C03_InsertDeleteGetRandomOof1.java) |
| 137 | [Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree) | [B10_IsBalancedBinaryTree](../09-Trees-BST/B10_IsBalancedBinaryTree.java) |
| 138 | [Palindrome Number](https://leetcode.com/problems/palindrome-number) | -- |
| 139 | [N Queens](https://leetcode.com/problems/n-queens) | [D01_NQueens](../14-Backtracking-Recursion/D01_NQueens.java) |
| 140 | [K Closest Points To Origin](https://leetcode.com/problems/k-closest-points-to-origin) | [C02_KClosestPointsToOrigin](../08-Heap-Priority-Queue/C02_KClosestPointsToOrigin.java) |
| 141 | [Odd Even Linked List](https://leetcode.com/problems/odd-even-linked-list) | [C05_OddEvenLinkedList](../06-Linked-List/C05_OddEvenLinkedList.java) |
| 142 | [Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees) | -- |
| 143 | [Same Tree](https://leetcode.com/problems/same-tree) | [B02_SameTree](../09-Trees-BST/B02_SameTree.java) |
| 144 | [Lowest Common Ancestor Of A Binary Search Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree) | -- |
| 145 | [Shortest Unsorted Continuous Subarray](https://leetcode.com/problems/shortest-unsorted-continuous-subarray) | -- |
| 146 | [Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator) | -- |
| 147 | [Longest Increasing Path In A Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix) | -- |
| 148 | [Queue Reconstruction By Height](https://leetcode.com/problems/queue-reconstruction-by-height) | -- |
| 149 | [Path Sum](https://leetcode.com/problems/path-sum) | [B05_HasPathSum](../09-Trees-BST/B05_HasPathSum.java) |
| 150 | [Delete Node In A Bst](https://leetcode.com/problems/delete-node-in-a-bst) | [C05_DeleteNodeInBST](../09-Trees-BST/C05_DeleteNodeInBST.java) |

## Problems 151-200

| # | Problem | Solved here |
| --- | --- | --- |
| 151 | [Subtree Of Another Tree](https://leetcode.com/problems/subtree-of-another-tree) | [B04_IsSubtree](../09-Trees-BST/B04_IsSubtree.java) |
| 152 | [Missing Number](https://leetcode.com/problems/missing-number) | [B01_MissingNumber](../17-Math-Bit-Manipulation/B01_MissingNumber.java) |
| 153 | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence) | [C10_LongestPalindromicSubsequence](../12-Dynamic-Programming/C10_LongestPalindromicSubsequence.java) |
| 154 | [Evaluate Division](https://leetcode.com/problems/evaluate-division) | -- |
| 155 | [House Robber Ii](https://leetcode.com/problems/house-robber-ii) | -- |
| 156 | [Pascals Triangle](https://leetcode.com/problems/pascals-triangle) | [B11_PascalTriangleTwoLoops](../17-Math-Bit-Manipulation/B11_PascalTriangleTwoLoops.java) |
| 157 | [Clone Graph](https://leetcode.com/problems/clone-graph) | -- |
| 158 | [Coin Change 2](https://leetcode.com/problems/coin-change-2) | -- |
| 159 | [Happy Number](https://leetcode.com/problems/happy-number) | [B16_HappyNumber](../17-Math-Bit-Manipulation/B16_HappyNumber.java) |
| 160 | [Number Of Provinces](https://leetcode.com/problems/number-of-provinces) | [B03_NumberOfProvinces](../11-Graphs/B03_NumberOfProvinces.java) |
| 161 | [Single Element In A Sorted Array](https://leetcode.com/problems/single-element-in-a-sorted-array) | [C08_SingleNonDuplicateElement](../03-Binary-Search/C08_SingleNonDuplicateElement.java) |
| 162 | [Gas Station](https://leetcode.com/problems/gas-station) | [C10_GasStation](../13-Greedy/C10_GasStation.java) |
| 163 | [Unique Binary Search Trees Ii](https://leetcode.com/problems/unique-binary-search-trees-ii) | -- |
| 164 | [Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops) | [C12_CheapestFlight](../11-Graphs/C12_CheapestFlight.java) |
| 165 | [Middle Of The Linked List](https://leetcode.com/problems/middle-of-the-linked-list) | [A03_FindMiddleOfLinkedList](../06-Linked-List/A03_FindMiddleOfLinkedList.java) |
| 166 | [Remove K Digits](https://leetcode.com/problems/remove-k-digits) | [C07_RemoveKdigits](../07-Stack-Queue-Monotonic/C07_RemoveKdigits.java) |
| 167 | [Sudoku Solver](https://leetcode.com/problems/sudoku-solver) | -- |
| 168 | [Surrounded Regions](https://leetcode.com/problems/surrounded-regions) | -- |
| 169 | [Count Complete Tree Nodes](https://leetcode.com/problems/count-complete-tree-nodes) | [C29_CountCompleteTreeNodes](../09-Trees-BST/C29_CountCompleteTreeNodes.java) |
| 170 | [Remove Invalid Parentheses](https://leetcode.com/problems/remove-invalid-parentheses) | [D03_RemoveInvalidParentheses](../14-Backtracking-Recursion/D03_RemoveInvalidParentheses.java) |
| 171 | [Partition To K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets) | -- |
| 172 | [Two Sum Ii Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted) | -- |
| 173 | [First Unique Character In A String](https://leetcode.com/problems/first-unique-character-in-a-string) | [B05_FirstNonRepeatingChar](../04-Strings/B05_FirstNonRepeatingChar.java) |
| 174 | [Largest Number](https://leetcode.com/problems/largest-number) | [C06_BiggestNumber](../13-Greedy/C06_BiggestNumber.java) |
| 175 | [Count Primes](https://leetcode.com/problems/count-primes) | -- |
| 176 | [Squares Of A Sorted Array](https://leetcode.com/problems/squares-of-a-sorted-array) | [B01_SquaresOfASortedArray](../02-Two-Pointers-Sliding-Window/B01_SquaresOfASortedArray.java) |
| 177 | [Permutations Ii](https://leetcode.com/problems/permutations-ii) | -- |
| 178 | [Word Break Ii](https://leetcode.com/problems/word-break-ii) | -- |
| 179 | [Combination Sum Ii](https://leetcode.com/problems/combination-sum-ii) | [C04_CombinationSumII](../14-Backtracking-Recursion/C04_CombinationSumII.java) |
| 180 | [Triangle](https://leetcode.com/problems/triangle) | [C05_Triangle](../12-Dynamic-Programming/C05_Triangle.java) |
| 181 | [Remove Linked List Elements](https://leetcode.com/problems/remove-linked-list-elements) | -- |
| 182 | [Remove Duplicates From Sorted List Ii](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii) | -- |
| 183 | [Valid Sudoku](https://leetcode.com/problems/valid-sudoku) | -- |
| 184 | [Path Sum Ii](https://leetcode.com/problems/path-sum-ii) | [C10_PathSumII](../09-Trees-BST/C10_PathSumII.java) |
| 185 | [Convert Sorted List To Binary Search Tree](https://leetcode.com/problems/convert-sorted-list-to-binary-search-tree) | [C07_ConvertSortedListToBST](../09-Trees-BST/C07_ConvertSortedListToBST.java) |
| 186 | [Insert Interval](https://leetcode.com/problems/insert-interval) | [C01_InsertInterval](../15-Intervals/C01_InsertInterval.java) |
| 187 | [Wildcard Matching](https://leetcode.com/problems/wildcard-matching) | -- |
| 188 | [Subsets Ii](https://leetcode.com/problems/subsets-ii) | [C02_SubsetsII](../14-Backtracking-Recursion/C02_SubsetsII.java) |
| 189 | [Unique Paths Ii](https://leetcode.com/problems/unique-paths-ii) | -- |
| 190 | [Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum) | -- |
| 191 | [Reorganize String](https://leetcode.com/problems/reorganize-string) | [C04_ReorganizeString](../08-Heap-Priority-Queue/C04_ReorganizeString.java) |
| 192 | [Permutation In String](https://leetcode.com/problems/permutation-in-string) | -- |
| 193 | [Majority Element Ii](https://leetcode.com/problems/majority-element-ii) | -- |
| 194 | [Construct Binary Tree From Inorder And Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal) | -- |
| 195 | [Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets) | -- |
| 196 | [Top K Frequent Words](https://leetcode.com/problems/top-k-frequent-words) | -- |
| 197 | [Island Perimeter](https://leetcode.com/problems/island-perimeter) | -- |
| 198 | [Remove Duplicates From Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list) | [B01_RemoveDuplicatesInList](../06-Linked-List/B01_RemoveDuplicatesInList.java) |
| 199 | [Add Binary](https://leetcode.com/problems/add-binary) | -- |
| 200 | [01 Matrix](https://leetcode.com/problems/01-matrix) | -- |

## Problems 201-250

| # | Problem | Solved here |
| --- | --- | --- |
| 201 | [Valid Anagram](https://leetcode.com/problems/valid-anagram) | [A04_AnagramStrings](../04-Strings/A04_AnagramStrings.java) |
| 202 | [Multiply Strings](https://leetcode.com/problems/multiply-strings) | [C07_MultiplyStrings](../04-Strings/C07_MultiplyStrings.java) |
| 203 | [First Bad Version](https://leetcode.com/problems/first-bad-version) | -- |
| 204 | [Design Add And Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure) | [C02_DesignAddAndSearchWordsDataStructure](../10-Trie/C02_DesignAddAndSearchWordsDataStructure.java) |
| 205 | [Next Greater Element Ii](https://leetcode.com/problems/next-greater-element-ii) | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) `*` |
| 206 | [Basic Calculator Ii](https://leetcode.com/problems/basic-calculator-ii) | -- |
| 207 | [Accounts Merge](https://leetcode.com/problems/accounts-merge) | [C09_AccountsMerge](../11-Graphs/C09_AccountsMerge.java) |
| 208 | [Valid Palindrome Ii](https://leetcode.com/problems/valid-palindrome-ii) | -- |
| 209 | [Delete Node In A Linked List](https://leetcode.com/problems/delete-node-in-a-linked-list) | -- |
| 210 | [Interval List Intersections](https://leetcode.com/problems/interval-list-intersections) | [C02_IntervalListIntersections](../15-Intervals/C02_IntervalListIntersections.java) |
| 211 | [Rotate List](https://leetcode.com/problems/rotate-list) | [C06_RotateRightList](../06-Linked-List/C06_RotateRightList.java) |
| 212 | [Sum Root To Leaf Numbers](https://leetcode.com/problems/sum-root-to-leaf-numbers) | -- |
| 213 | [Dungeon Game](https://leetcode.com/problems/dungeon-game) | -- |
| 214 | [Powx N](https://leetcode.com/problems/powx-n) | -- |
| 215 | [Max Consecutive Ones Iii](https://leetcode.com/problems/max-consecutive-ones-iii) | [C04_MaxConsecutiveOnesIII](../02-Two-Pointers-Sliding-Window/C04_MaxConsecutiveOnesIII.java) |
| 216 | [Intersection Of Two Arrays Ii](https://leetcode.com/problems/intersection-of-two-arrays-ii) | [B04_IntersectionOfTwoArraysII](../05-Hashing-Prefix-Sum/B04_IntersectionOfTwoArraysII.java) |
| 217 | [Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters) | -- |
| 218 | [Recover Binary Search Tree](https://leetcode.com/problems/recover-binary-search-tree) | -- |
| 219 | [Is Graph Bipartite](https://leetcode.com/problems/is-graph-bipartite) | [B09_IsBipartite](../11-Graphs/B09_IsBipartite.java) |
| 220 | [Contiguous Array](https://leetcode.com/problems/contiguous-array) | -- |
| 221 | [Find K Closest Elements](https://leetcode.com/problems/find-k-closest-elements) | -- |
| 222 | [Reverse String](https://leetcode.com/problems/reverse-string) | -- |
| 223 | [Range Sum Of Bst](https://leetcode.com/problems/range-sum-of-bst) | [B12_RangeSumBST](../09-Trees-BST/B12_RangeSumBST.java) |
| 224 | [Sort Characters By Frequency](https://leetcode.com/problems/sort-characters-by-frequency) | -- |
| 225 | [Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement) | [C07_CharacterReplacement](../02-Two-Pointers-Sliding-Window/C07_CharacterReplacement.java) |
| 226 | [Binary Tree Postorder Traversal](https://leetcode.com/problems/binary-tree-postorder-traversal) | [A01_RecursivePostorder](../09-Trees-BST/A01_RecursivePostorder.java) |
| 227 | [Single Number Ii](https://leetcode.com/problems/single-number-ii) | -- |
| 228 | [Ugly Number Ii](https://leetcode.com/problems/ugly-number-ii) | -- |
| 229 | [Reconstruct Itinerary](https://leetcode.com/problems/reconstruct-itinerary) | [D07_ReconstructItinerary](../11-Graphs/D07_ReconstructItinerary.java) |
| 230 | [The Skyline Problem](https://leetcode.com/problems/the-skyline-problem) | -- |
| 231 | [Single Number Iii](https://leetcode.com/problems/single-number-iii) | -- |
| 232 | [Is Subsequence](https://leetcode.com/problems/is-subsequence) | [B02_IsSubsequence](../02-Two-Pointers-Sliding-Window/B02_IsSubsequence.java) |
| 233 | [Network Delay Time](https://leetcode.com/problems/network-delay-time) | -- |
| 234 | [Binary Tree Paths](https://leetcode.com/problems/binary-tree-paths) | -- |
| 235 | [Longest Substring With At Least K Repeating Characters](https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters) | [C10_LongestSubstringWithKRepeatingChars](../02-Two-Pointers-Sliding-Window/C10_LongestSubstringWithKRepeatingChars.java) |
| 236 | [Minimum Depth Of Binary Tree](https://leetcode.com/problems/minimum-depth-of-binary-tree) | -- |
| 237 | [Best Time To Buy And Sell Stock With Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee) | -- |
| 238 | [Non Decreasing Array](https://leetcode.com/problems/non-decreasing-array) | -- |
| 239 | [Interleaving String](https://leetcode.com/problems/interleaving-string) | -- |
| 240 | [Game Of Life](https://leetcode.com/problems/game-of-life) | -- |
| 241 | [Combinations](https://leetcode.com/problems/combinations) | -- |
| 242 | [Binary Tree Preorder Traversal](https://leetcode.com/problems/binary-tree-preorder-traversal) | [A06_PrePostInorderInOneTraversal](../09-Trees-BST/A06_PrePostInorderInOneTraversal.java) |
| 243 | [All Paths From Source To Target](https://leetcode.com/problems/all-paths-from-source-to-target) | -- |
| 244 | [Plus One](https://leetcode.com/problems/plus-one) | [B01_PlusOne](../01-Arrays/B01_PlusOne.java) |
| 245 | [Best Time To Buy And Sell Stock Iv](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv) | -- |
| 246 | [Increasing Triplet Subsequence](https://leetcode.com/problems/increasing-triplet-subsequence) | [C03_IncreasingTripletSubsequence](../01-Arrays/C03_IncreasingTripletSubsequence.java) |
| 247 | [Trim A Binary Search Tree](https://leetcode.com/problems/trim-a-binary-search-tree) | -- |
| 248 | [Backspace String Compare](https://leetcode.com/problems/backspace-string-compare) | -- |
| 249 | [Maximum Length Of Repeated Subarray](https://leetcode.com/problems/maximum-length-of-repeated-subarray) | -- |
| 250 | [Capacity To Ship Packages Within D Days](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days) | [C12_CapacityToShipPackagesWithinDDays](../03-Binary-Search/C12_CapacityToShipPackagesWithinDDays.java) |

## Problems 251-300

| # | Problem | Solved here |
| --- | --- | --- |
| 251 | [Implement Strstr](https://leetcode.com/problems/implement-strstr) | -- |
| 252 | [Word Ladder Ii](https://leetcode.com/problems/word-ladder-ii) | -- |
| 253 | [Subarray Product Less Than K](https://leetcode.com/problems/subarray-product-less-than-k) | [C08_SubarrayProductLessThanK](../02-Two-Pointers-Sliding-Window/C08_SubarrayProductLessThanK.java) |
| 254 | [Contains Duplicate](https://leetcode.com/problems/contains-duplicate) | [A01_ContainsDuplicate](../05-Hashing-Prefix-Sum/A01_ContainsDuplicate.java) |
| 255 | [Flatten A Multilevel Doubly Linked List](https://leetcode.com/problems/flatten-a-multilevel-doubly-linked-list) | -- |
| 256 | [Populating Next Right Pointers In Each Node Ii](https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii) | -- |
| 257 | [Maximum Binary Tree](https://leetcode.com/problems/maximum-binary-tree) | -- |
| 258 | [Minimum Remove To Make Valid Parentheses](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses) | -- |
| 259 | [Distribute Coins In Binary Tree](https://leetcode.com/problems/distribute-coins-in-binary-tree) | -- |
| 260 | [Critical Connections In A Network](https://leetcode.com/problems/critical-connections-in-a-network) | -- |
| 261 | [Basic Calculator](https://leetcode.com/problems/basic-calculator) | [D05_BasicCalculator](../07-Stack-Queue-Monotonic/D05_BasicCalculator.java) |
| 262 | [Redundant Connection](https://leetcode.com/problems/redundant-connection) | -- |
| 263 | [Jewels And Stones](https://leetcode.com/problems/jewels-and-stones) | -- |
| 264 | [Non Overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals) | [C06_NonOverlappingIntervals](../15-Intervals/C06_NonOverlappingIntervals.java) |
| 265 | [Flood Fill](https://leetcode.com/problems/flood-fill) | [B01_FloodFill](../11-Graphs/B01_FloodFill.java) |
| 266 | [Zigzag Conversion](https://leetcode.com/problems/zigzag-conversion) | [C05_ZigzagConversion](../04-Strings/C05_ZigzagConversion.java) |
| 267 | [Maximum Width Of Binary Tree](https://leetcode.com/problems/maximum-width-of-binary-tree) | -- |
| 268 | [Permutation Sequence](https://leetcode.com/problems/permutation-sequence) | [D02_KthPermutation](../14-Backtracking-Recursion/D02_KthPermutation.java) |
| 269 | [Construct Binary Search Tree From Preorder Traversal](https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal) | [C06_ConstructBinarySearchTreefromPreorderTraversal](../09-Trees-BST/C06_ConstructBinarySearchTreefromPreorderTraversal.java) |
| 270 | [Different Ways To Add Parentheses](https://leetcode.com/problems/different-ways-to-add-parentheses) | -- |
| 271 | [Search In Rotated Sorted Array Ii](https://leetcode.com/problems/search-in-rotated-sorted-array-ii) | [C07_SearchInARotatedSortedArrayII](../03-Binary-Search/C07_SearchInARotatedSortedArrayII.java) |
| 272 | [Add Two Numbers Ii](https://leetcode.com/problems/add-two-numbers-ii) | -- |
| 273 | [Two Sum Iv Input Is A Bst](https://leetcode.com/problems/two-sum-iv-input-is-a-bst) | -- |
| 274 | [Convert Bst To Greater Tree](https://leetcode.com/problems/convert-bst-to-greater-tree) | -- |
| 275 | [Partition List](https://leetcode.com/problems/partition-list) | [C03_PartitionList](../06-Linked-List/C03_PartitionList.java) |
| 276 | [Sqrtx](https://leetcode.com/problems/sqrtx) | -- |
| 277 | [Isomorphic Strings](https://leetcode.com/problems/isomorphic-strings) | -- |
| 278 | [Number Of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence) | -- |
| 279 | [Binary Search](https://leetcode.com/problems/binary-search) | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) `*` |
| 280 | [Valid Parenthesis String](https://leetcode.com/problems/valid-parenthesis-string) | -- |
| 281 | [Repeated Substring Pattern](https://leetcode.com/problems/repeated-substring-pattern) | -- |
| 282 | [Remove Element](https://leetcode.com/problems/remove-element) | [A01_RemoveElement](../02-Two-Pointers-Sliding-Window/A01_RemoveElement.java) |
| 283 | [Combination Sum Iv](https://leetcode.com/problems/combination-sum-iv) | -- |
| 284 | [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences) | [D02_CountOfDistinctSubsequences](../12-Dynamic-Programming/D02_CountOfDistinctSubsequences.java) |
| 285 | [Asteroid Collision](https://leetcode.com/problems/asteroid-collision) | [C11_AsteroidCollision](../07-Stack-Queue-Monotonic/C11_AsteroidCollision.java) |
| 286 | [Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset) | -- |
| 287 | [Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow) | -- |
| 288 | [132 Pattern](https://leetcode.com/problems/132-pattern) | [C10_Find132pattern](../07-Stack-Queue-Monotonic/C10_Find132pattern.java) |
| 289 | [Delete And Earn](https://leetcode.com/problems/delete-and-earn) | -- |
| 290 | [Sum Of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums) | [C09_SumOfSubarrayMinimums](../07-Stack-Queue-Monotonic/C09_SumOfSubarrayMinimums.java) |
| 291 | [Count Square Submatrices With All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones) | [C06_CountSquareSubmatricesWithAllOnes](../12-Dynamic-Programming/C06_CountSquareSubmatricesWithAllOnes.java) |
| 292 | [Palindrome Partitioning Ii](https://leetcode.com/problems/palindrome-partitioning-ii) | -- |
| 293 | [Sum Of Left Leaves](https://leetcode.com/problems/sum-of-left-leaves) | [B06_SumOfLeftLeaves](../09-Trees-BST/B06_SumOfLeftLeaves.java) |
| 294 | [Hamming Distance](https://leetcode.com/problems/hamming-distance) | -- |
| 295 | [Flatten Nested List Iterator](https://leetcode.com/problems/flatten-nested-list-iterator) | -- |
| 296 | [Score Of Parentheses](https://leetcode.com/problems/score-of-parentheses) | -- |
| 297 | [Longest String Chain](https://leetcode.com/problems/longest-string-chain) | [C13_LongestStringChain](../12-Dynamic-Programming/C13_LongestStringChain.java) |
| 298 | [Valid Palindrome](https://leetcode.com/problems/valid-palindrome) | [A06_PalindromeSpecial](../04-Strings/A06_PalindromeSpecial.java) |
| 299 | [Longest Univalue Path](https://leetcode.com/problems/longest-univalue-path) | -- |
| 300 | [Letter Case Permutation](https://leetcode.com/problems/letter-case-permutation) | -- |

## Problems 301-350

| # | Problem | Solved here |
| --- | --- | --- |
| 301 | [Binary Tree Level Order Traversal Ii](https://leetcode.com/problems/binary-tree-level-order-traversal-ii) | -- |
| 302 | [Find Duplicate Subtrees](https://leetcode.com/problems/find-duplicate-subtrees) | -- |
| 303 | [Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values) | -- |
| 304 | [Lfu Cache](https://leetcode.com/problems/lfu-cache) | [D01_LFUCache](../19-Design-Data-Structures/D01_LFUCache.java) |
| 305 | [Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes) | -- |
| 306 | [Maximum Profit In Job Scheduling](https://leetcode.com/problems/maximum-profit-in-job-scheduling) | [D05_JobSchedulingMaxProfit](../12-Dynamic-Programming/D05_JobSchedulingMaxProfit.java) |
| 307 | [Add Strings](https://leetcode.com/problems/add-strings) | [A08_AddStrings](../04-Strings/A08_AddStrings.java) |
| 308 | [Pairs Of Songs With Total Durations Divisible By 60](https://leetcode.com/problems/pairs-of-songs-with-total-durations-divisible-by-60) | [C10_PairsOfSongsDivBy60](../05-Hashing-Prefix-Sum/C10_PairsOfSongsDivBy60.java) |
| 309 | [Implement Queue Using Stacks](https://leetcode.com/problems/implement-queue-using-stacks) | [A04_QueueUsingStacks](../07-Stack-Queue-Monotonic/A04_QueueUsingStacks.java) |
| 310 | [Maximum Xor Of Two Numbers In An Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array) | -- |
| 311 | [Roman To Integer](https://leetcode.com/problems/roman-to-integer) | [B14_RomanToInteger](../04-Strings/B14_RomanToInteger.java) |
| 312 | [Find The Town Judge](https://leetcode.com/problems/find-the-town-judge) | -- |
| 313 | [Find K Pairs With Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums) | -- |
| 314 | [Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array) | [D03_MergeTwoSortedArrays](../02-Two-Pointers-Sliding-Window/D03_MergeTwoSortedArrays.java) |
| 315 | [Remove All Adjacent Duplicates In String](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string) | [B01_RemoveAdjacentDuplicates](../07-Stack-Queue-Monotonic/B01_RemoveAdjacentDuplicates.java) |
| 316 | [Reverse Words In A String](https://leetcode.com/problems/reverse-words-in-a-string) | [C01_ReverseWordsInString](../04-Strings/C01_ReverseWordsInString.java) |
| 317 | [4Sum Ii](https://leetcode.com/problems/4sum-ii) | -- |
| 318 | [Average Of Levels In Binary Tree](https://leetcode.com/problems/average-of-levels-in-binary-tree) | -- |
| 319 | [Find Pivot Index](https://leetcode.com/problems/find-pivot-index) | [B04_PivotIndex](../01-Arrays/B04_PivotIndex.java) |
| 320 | [How Many Numbers Are Smaller Than The Current Number](https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number) | -- |
| 321 | [Integer To Roman](https://leetcode.com/problems/integer-to-roman) | [C04_IntegerToRoman](../04-Strings/C04_IntegerToRoman.java) |
| 322 | [Keys And Rooms](https://leetcode.com/problems/keys-and-rooms) | [B04_KeysAndRooms](../11-Graphs/B04_KeysAndRooms.java) |
| 323 | [Power Of Two](https://leetcode.com/problems/power-of-two) | -- |
| 324 | [Subarrays With K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers) | -- |
| 325 | [Predict The Winner](https://leetcode.com/problems/predict-the-winner) | -- |
| 326 | [Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray) | -- |
| 327 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number) | [A01_Fibonacci](../12-Dynamic-Programming/A01_Fibonacci.java) |
| 328 | [Reverse Bits](https://leetcode.com/problems/reverse-bits) | -- |
| 329 | [Delete Nodes And Return Forest](https://leetcode.com/problems/delete-nodes-and-return-forest) | [C27_DeleteNodesAndReturnForest](../09-Trees-BST/C27_DeleteNodesAndReturnForest.java) |
| 330 | [Shortest Subarray With Sum At Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k) | -- |
| 331 | [Find Minimum In Rotated Sorted Array Ii](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii) | -- |
| 332 | [Word Pattern](https://leetcode.com/problems/word-pattern) | -- |
| 333 | [Arithmetic Slices](https://leetcode.com/problems/arithmetic-slices) | -- |
| 334 | [Ones And Zeroes](https://leetcode.com/problems/ones-and-zeroes) | -- |
| 335 | [Unique Paths Iii](https://leetcode.com/problems/unique-paths-iii) | -- |
| 336 | [Range Sum Query Mutable](https://leetcode.com/problems/range-sum-query-mutable) | -- |
| 337 | [Vertical Order Traversal Of A Binary Tree](https://leetcode.com/problems/vertical-order-traversal-of-a-binary-tree) | -- |
| 338 | [Candy](https://leetcode.com/problems/candy) | [D01_Candy](../13-Greedy/D01_Candy.java) |
| 339 | [Remove Duplicates From Sorted Array Ii](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii) | -- |
| 340 | [Number Of Matching Subsequences](https://leetcode.com/problems/number-of-matching-subsequences) | -- |
| 341 | [Koko Eating Bananas](https://leetcode.com/problems/koko-eating-bananas) | [C11_KokoEatingBananas](../03-Binary-Search/C11_KokoEatingBananas.java) |
| 342 | [Open The Lock](https://leetcode.com/problems/open-the-lock) | [C03_OpenTheLock](../11-Graphs/C03_OpenTheLock.java) |
| 343 | [Serialize And Deserialize Bst](https://leetcode.com/problems/serialize-and-deserialize-bst) | -- |
| 344 | [Combination Sum Iii](https://leetcode.com/problems/combination-sum-iii) | -- |
| 345 | [Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs) | -- |
| 346 | [Verifying An Alien Dictionary](https://leetcode.com/problems/verifying-an-alien-dictionary) | -- |
| 347 | [Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation) | [C03_EvaluateReversePolishNotation](../07-Stack-Queue-Monotonic/C03_EvaluateReversePolishNotation.java) |
| 348 | [Trapping Rain Water Ii](https://leetcode.com/problems/trapping-rain-water-ii) | -- |
| 349 | [Maximum Points You Can Obtain From Cards](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards) | [C09_MaximumPointsFromCards](../02-Two-Pointers-Sliding-Window/C09_MaximumPointsFromCards.java) |
| 350 | [Cousins In Binary Tree](https://leetcode.com/problems/cousins-in-binary-tree) | [B11_Cousins](../09-Trees-BST/B11_Cousins.java) |

## Problems 351-400

| # | Problem | Solved here |
| --- | --- | --- |
| 351 | [Divide Two Integers](https://leetcode.com/problems/divide-two-integers) | -- |
| 352 | [Delete Operation For Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings) | -- |
| 353 | [Minimum Number Of Arrows To Burst Balloons](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons) | [C07_MinimumArrowsToBurstBalloons](../15-Intervals/C07_MinimumArrowsToBurstBalloons.java) |
| 354 | [Subarray Sums Divisible By K](https://leetcode.com/problems/subarray-sums-divisible-by-k) | -- |
| 355 | [Count Binary Substrings](https://leetcode.com/problems/count-binary-substrings) | -- |
| 356 | [Restore Ip Addresses](https://leetcode.com/problems/restore-ip-addresses) | -- |
| 357 | [Spiral Matrix Ii](https://leetcode.com/problems/spiral-matrix-ii) | -- |
| 358 | [Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack) | -- |
| 359 | [Integer Break](https://leetcode.com/problems/integer-break) | -- |
| 360 | [Sort Array By Parity](https://leetcode.com/problems/sort-array-by-parity) | [A05_SortArrayByParity](../02-Two-Pointers-Sliding-Window/A05_SortArrayByParity.java) |
| 361 | [Binary Tree Cameras](https://leetcode.com/problems/binary-tree-cameras) | -- |
| 362 | [Wiggle Subsequence](https://leetcode.com/problems/wiggle-subsequence) | -- |
| 363 | [Jump Game Iii](https://leetcode.com/problems/jump-game-iii) | -- |
| 364 | [Number Of 1 Bits](https://leetcode.com/problems/number-of-1-bits) | [A02_NumberOf1Bits](../17-Math-Bit-Manipulation/A02_NumberOf1Bits.java) |
| 365 | [Insert Into A Binary Search Tree](https://leetcode.com/problems/insert-into-a-binary-search-tree) | [A05_BinarySearchTreeOperations](../09-Trees-BST/A05_BinarySearchTreeOperations.java) `*` |
| 366 | [Maximum Product Of Three Numbers](https://leetcode.com/problems/maximum-product-of-three-numbers) | -- |
| 367 | [Expression Add Operators](https://leetcode.com/problems/expression-add-operators) | [D04_ExpressionAddOperators](../14-Backtracking-Recursion/D04_ExpressionAddOperators.java) |
| 368 | [Excel Sheet Column Title](https://leetcode.com/problems/excel-sheet-column-title) | -- |
| 369 | [Reverse Pairs](https://leetcode.com/problems/reverse-pairs) | -- |
| 370 | [Sum Of Distances In Tree](https://leetcode.com/problems/sum-of-distances-in-tree) | -- |
| 371 | [Two City Scheduling](https://leetcode.com/problems/two-city-scheduling) | -- |
| 372 | [Remove All Adjacent Duplicates In String Ii](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string-ii) | -- |
| 373 | [Range Sum Query 2D Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable) | -- |
| 374 | [2 Keys Keyboard](https://leetcode.com/problems/2-keys-keyboard) | -- |
| 375 | [Intersection Of Two Arrays](https://leetcode.com/problems/intersection-of-two-arrays) | [B03_IntersectionOfTwoArrays](../05-Hashing-Prefix-Sum/B03_IntersectionOfTwoArrays.java) |
| 376 | [Validate Stack Sequences](https://leetcode.com/problems/validate-stack-sequences) | -- |
| 377 | [Longest Palindrome](https://leetcode.com/problems/longest-palindrome) | -- |
| 378 | [Search In A Binary Search Tree](https://leetcode.com/problems/search-in-a-binary-search-tree) | [A05_BinarySearchTreeOperations](../09-Trees-BST/A05_BinarySearchTreeOperations.java) `*` |
| 379 | [Minimum Number Of Refueling Stops](https://leetcode.com/problems/minimum-number-of-refueling-stops) | [D03_MinimumRefuelingStops](../08-Heap-Priority-Queue/D03_MinimumRefuelingStops.java) |
| 380 | [Valid Triangle Number](https://leetcode.com/problems/valid-triangle-number) | -- |
| 381 | [All Possible Full Binary Trees](https://leetcode.com/problems/all-possible-full-binary-trees) | -- |
| 382 | [Reverse Words In A String Iii](https://leetcode.com/problems/reverse-words-in-a-string-iii) | -- |
| 383 | [Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number) | [B08_ExcelSheetColumnNumber](../17-Math-Bit-Manipulation/B08_ExcelSheetColumnNumber.java) |
| 384 | [Cherry Pickup](https://leetcode.com/problems/cherry-pickup) | [D09_CherryPickup](../12-Dynamic-Programming/D09_CherryPickup.java) |
| 385 | [Design Hashmap](https://leetcode.com/problems/design-hashmap) | -- |
| 386 | [Binary Tree Pruning](https://leetcode.com/problems/binary-tree-pruning) | -- |
| 387 | [Sum Of Two Integers](https://leetcode.com/problems/sum-of-two-integers) | -- |
| 388 | [Online Stock Span](https://leetcode.com/problems/online-stock-span) | -- |
| 389 | [Convert Binary Number In A Linked List To Integer](https://leetcode.com/problems/convert-binary-number-in-a-linked-list-to-integer) | -- |
| 390 | [Binary Search Tree To Greater Sum Tree](https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree) | [C03_BinarySearchTreeToGreaterSumTree](../09-Trees-BST/C03_BinarySearchTreeToGreaterSumTree.java) |
| 391 | [Max Consecutive Ones](https://leetcode.com/problems/max-consecutive-ones) | [A03_MaxConsecutiveOnes](../01-Arrays/A03_MaxConsecutiveOnes.java) |
| 392 | [Running Sum Of 1D Array](https://leetcode.com/problems/running-sum-of-1d-array) | -- |
| 393 | [Maximum Swap](https://leetcode.com/problems/maximum-swap) | [C04_MaximumSwap](../13-Greedy/C04_MaximumSwap.java) |
| 394 | [Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome) | -- |
| 395 | [Most Stones Removed With Same Row Or Column](https://leetcode.com/problems/most-stones-removed-with-same-row-or-column) | [C10_MostStonesRemovedWithSameRowOrColumn](../11-Graphs/C10_MostStonesRemovedWithSameRowOrColumn.java) |
| 396 | [Maximum Difference Between Node And Ancestor](https://leetcode.com/problems/maximum-difference-between-node-and-ancestor) | [C09_MaximumDifferenceBetweenNodeAndAncestor](../09-Trees-BST/C09_MaximumDifferenceBetweenNodeAndAncestor.java) |
| 397 | [Frog Jump](https://leetcode.com/problems/frog-jump) | -- |
| 398 | [Pascals Triangle Ii](https://leetcode.com/problems/pascals-triangle-ii) | -- |
| 399 | [Deepest Leaves Sum](https://leetcode.com/problems/deepest-leaves-sum) | -- |
| 400 | [Shortest Bridge](https://leetcode.com/problems/shortest-bridge) | [C02_ShortestBridge](../11-Graphs/C02_ShortestBridge.java) |

## Problems 401-450

| # | Problem | Solved here |
| --- | --- | --- |
| 401 | [Bitwise And Of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range) | -- |
| 402 | [Super Egg Drop](https://leetcode.com/problems/super-egg-drop) | -- |
| 403 | [Contains Duplicate Iii](https://leetcode.com/problems/contains-duplicate-iii) | -- |
| 404 | [Robot Bounded In Circle](https://leetcode.com/problems/robot-bounded-in-circle) | -- |
| 405 | [Increasing Order Search Tree](https://leetcode.com/problems/increasing-order-search-tree) | -- |
| 406 | [Find Common Characters](https://leetcode.com/problems/find-common-characters) | -- |
| 407 | [Shortest Distance To A Character](https://leetcode.com/problems/shortest-distance-to-a-character) | -- |
| 408 | [Can Place Flowers](https://leetcode.com/problems/can-place-flowers) | [B01_CanPlaceFlowers](../13-Greedy/B01_CanPlaceFlowers.java) |
| 409 | [Contains Duplicate Ii](https://leetcode.com/problems/contains-duplicate-ii) | -- |
| 410 | [Number Of Good Pairs](https://leetcode.com/problems/number-of-good-pairs) | -- |
| 411 | [Minimum Add To Make Parentheses Valid](https://leetcode.com/problems/minimum-add-to-make-parentheses-valid) | [C02_MinimumAddToMakeParenthesesValid](../07-Stack-Queue-Monotonic/C02_MinimumAddToMakeParenthesesValid.java) |
| 412 | [Maximum Length Of Pair Chain](https://leetcode.com/problems/maximum-length-of-pair-chain) | -- |
| 413 | [Possible Bipartition](https://leetcode.com/problems/possible-bipartition) | -- |
| 414 | [Split Array Into Consecutive Subsequences](https://leetcode.com/problems/split-array-into-consecutive-subsequences) | -- |
| 415 | [Making A Large Island](https://leetcode.com/problems/making-a-large-island) | [D03_MakingALargeIsland](../11-Graphs/D03_MakingALargeIsland.java) |
| 416 | [Shortest Path In Binary Matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix) | [B08_ShortestPathInBinaryMatrix](../11-Graphs/B08_ShortestPathInBinaryMatrix.java) |
| 417 | [Count Good Nodes In Binary Tree](https://leetcode.com/problems/count-good-nodes-in-binary-tree) | [C08_CountGoodNodes](../09-Trees-BST/C08_CountGoodNodes.java) |
| 418 | [Shuffle The Array](https://leetcode.com/problems/shuffle-the-array) | -- |
| 419 | [Sliding Window Median](https://leetcode.com/problems/sliding-window-median) | [D02_SlidingWindowMedian](../08-Heap-Priority-Queue/D02_SlidingWindowMedian.java) |
| 420 | [Kth Missing Positive Number](https://leetcode.com/problems/kth-missing-positive-number) | [B01_KthMissingPositive](../03-Binary-Search/B01_KthMissingPositive.java) |
| 421 | [Peak Index In A Mountain Array](https://leetcode.com/problems/peak-index-in-a-mountain-array) | -- |
| 422 | [Next Greater Node In Linked List](https://leetcode.com/problems/next-greater-node-in-linked-list) | -- |
| 423 | [Minimum Swaps To Make Sequences Increasing](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing) | -- |
| 424 | [Wiggle Sort Ii](https://leetcode.com/problems/wiggle-sort-ii) | -- |
| 425 | [Smallest Range Covering Elements From K Lists](https://leetcode.com/problems/smallest-range-covering-elements-from-k-lists) | -- |
| 426 | [Integer To English Words](https://leetcode.com/problems/integer-to-english-words) | -- |
| 427 | [Partition Array For Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum) | -- |
| 428 | [Maximum Gap](https://leetcode.com/problems/maximum-gap) | [D01_MaximumGap](../18-Sorting-Searching-Algorithms/D01_MaximumGap.java) |
| 429 | [Find Mode In Binary Search Tree](https://leetcode.com/problems/find-mode-in-binary-search-tree) | -- |
| 430 | [Longest Arithmetic Subsequence](https://leetcode.com/problems/longest-arithmetic-subsequence) | -- |
| 431 | [Last Stone Weight Ii](https://leetcode.com/problems/last-stone-weight-ii) | -- |
| 432 | [Last Stone Weight](https://leetcode.com/problems/last-stone-weight) | [B01_LastStoneWeight](../08-Heap-Priority-Queue/B01_LastStoneWeight.java) |
| 433 | [Find Bottom Left Tree Value](https://leetcode.com/problems/find-bottom-left-tree-value) | -- |
| 434 | [Regions Cut By Slashes](https://leetcode.com/problems/regions-cut-by-slashes) | -- |
| 435 | [Beautiful Arrangement](https://leetcode.com/problems/beautiful-arrangement) | -- |
| 436 | [Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum) | [C04_MinimumFallingPathSum](../12-Dynamic-Programming/C04_MinimumFallingPathSum.java) |
| 437 | [Diagonal Traverse](https://leetcode.com/problems/diagonal-traverse) | [C03_DiagonalTraverse](../16-Matrix/C03_DiagonalTraverse.java) |
| 438 | [Longest Continuous Subarray With Absolute Diff Less Than Or Equal To Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-diff-less-than-or-equal-to-limit) | -- |
| 439 | [Number Complement](https://leetcode.com/problems/number-complement) | -- |
| 440 | [Valid Perfect Square](https://leetcode.com/problems/valid-perfect-square) | -- |
| 441 | [Arranging Coins](https://leetcode.com/problems/arranging-coins) | -- |
| 442 | [Next Greater Element Iii](https://leetcode.com/problems/next-greater-element-iii) | -- |
| 443 | [Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort) | -- |
| 444 | [Search Suggestions System](https://leetcode.com/problems/search-suggestions-system) | [C03_SearchSuggestionsSystem](../10-Trie/C03_SearchSuggestionsSystem.java) |
| 445 | [Flipping An Image](https://leetcode.com/problems/flipping-an-image) | -- |
| 446 | [Kth Largest Element In A Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream) | [A01_KthLargestElementInAStream](../08-Heap-Priority-Queue/A01_KthLargestElementInAStream.java) |
| 447 | [Max Sum Of Rectangle No Larger Than K](https://leetcode.com/problems/max-sum-of-rectangle-no-larger-than-k) | -- |
| 448 | [Time Based Key Value Store](https://leetcode.com/problems/time-based-key-value-store) | [C06_TimeBasedKeyValueStore](../19-Design-Data-Structures/C06_TimeBasedKeyValueStore.java) |
| 449 | [Insertion Sort List](https://leetcode.com/problems/insertion-sort-list) | -- |
| 450 | [String Compression](https://leetcode.com/problems/string-compression) | [C03_StringCompression](../04-Strings/C03_StringCompression.java) |

## Problems 451-500

| # | Problem | Solved here |
| --- | --- | --- |
| 451 | [Maximum Depth Of N Ary Tree](https://leetcode.com/problems/maximum-depth-of-n-ary-tree) | -- |
| 452 | [Factorial Trailing Zeroes](https://leetcode.com/problems/factorial-trailing-zeroes) | -- |
| 453 | [Car Pooling](https://leetcode.com/problems/car-pooling) | [C05_CarPooling](../15-Intervals/C05_CarPooling.java) |
| 454 | [Find Largest Value In Each Tree Row](https://leetcode.com/problems/find-largest-value-in-each-tree-row) | -- |
| 455 | [Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence) | -- |
| 456 | [Number Of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum) | -- |
| 457 | [Design Twitter](https://leetcode.com/problems/design-twitter) | -- |
| 458 | [Array Nesting](https://leetcode.com/problems/array-nesting) | -- |
| 459 | [Stone Game](https://leetcode.com/problems/stone-game) | -- |
| 460 | [Degree Of An Array](https://leetcode.com/problems/degree-of-an-array) | -- |
| 461 | [Find The Difference](https://leetcode.com/problems/find-the-difference) | -- |
| 462 | [Minimum Absolute Difference In Bst](https://leetcode.com/problems/minimum-absolute-difference-in-bst) | [B14_GetMinimumDifference](../09-Trees-BST/B14_GetMinimumDifference.java) |
| 463 | [Count Negative Numbers In A Sorted Matrix](https://leetcode.com/problems/count-negative-numbers-in-a-sorted-matrix) | [B02_CountNegativeNumbersInASortedMatrix](../16-Matrix/B02_CountNegativeNumbersInASortedMatrix.java) |
| 464 | [Course Schedule Iii](https://leetcode.com/problems/course-schedule-iii) | -- |
| 465 | [Smallest Subtree With All The Deepest Nodes](https://leetcode.com/problems/smallest-subtree-with-all-the-deepest-nodes) | -- |
| 466 | [Reshape The Matrix](https://leetcode.com/problems/reshape-the-matrix) | -- |
| 467 | [Toeplitz Matrix](https://leetcode.com/problems/toeplitz-matrix) | [A02_ToeplitzMatrix](../16-Matrix/A02_ToeplitzMatrix.java) |
| 468 | [Knight Probability In Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard) | -- |
| 469 | [Find K Th Smallest Pair Distance](https://leetcode.com/problems/find-k-th-smallest-pair-distance) | -- |
| 470 | [My Calendar I](https://leetcode.com/problems/my-calendar-i) | -- |
| 471 | [Minimum Ascii Delete Sum For Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings) | -- |
| 472 | [Shortest Path In A Grid With Obstacles Elimination](https://leetcode.com/problems/shortest-path-in-a-grid-with-obstacles-elimination) | -- |
| 473 | [Range Sum Query Immutable](https://leetcode.com/problems/range-sum-query-immutable) | -- |
| 474 | [Check Completeness Of A Binary Tree](https://leetcode.com/problems/check-completeness-of-a-binary-tree) | -- |
| 475 | [Maximize Distance To Closest Person](https://leetcode.com/problems/maximize-distance-to-closest-person) | [C06_BestSeat](../01-Arrays/C06_BestSeat.java) |
| 476 | [Implement Stack Using Queues](https://leetcode.com/problems/implement-stack-using-queues) | -- |
| 477 | [Can I Win](https://leetcode.com/problems/can-i-win) | -- |
| 478 | [Custom Sort String](https://leetcode.com/problems/custom-sort-string) | -- |
| 479 | [Brick Wall](https://leetcode.com/problems/brick-wall) | -- |
| 480 | [Next Greater Element I](https://leetcode.com/problems/next-greater-element-i) | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) `*` |
| 481 | [Sum Of Root To Leaf Binary Numbers](https://leetcode.com/problems/sum-of-root-to-leaf-binary-numbers) | -- |
| 482 | [Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states) | [C06_EventualSafeNodesByDFS](../11-Graphs/C06_EventualSafeNodesByDFS.java) |
| 483 | [Reveal Cards In Increasing Order](https://leetcode.com/problems/reveal-cards-in-increasing-order) | -- |
| 484 | [Concatenated Words](https://leetcode.com/problems/concatenated-words) | -- |
| 485 | [Boats To Save People](https://leetcode.com/problems/boats-to-save-people) | [C02_BoatsToSavePeople](../13-Greedy/C02_BoatsToSavePeople.java) |
| 486 | [Construct Binary Tree From Preorder And Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal) | -- |
| 487 | [Count Number Of Teams](https://leetcode.com/problems/count-number-of-teams) | -- |
| 488 | [Repeated Dna Sequences](https://leetcode.com/problems/repeated-dna-sequences) | [C04_RepeatedDnaSequences](../05-Hashing-Prefix-Sum/C04_RepeatedDnaSequences.java) |
| 489 | [Substring With Concatenation Of All Words](https://leetcode.com/problems/substring-with-concatenation-of-all-words) | -- |
| 490 | [Bus Routes](https://leetcode.com/problems/bus-routes) | -- |
| 491 | [Find And Replace Pattern](https://leetcode.com/problems/find-and-replace-pattern) | [C07_FindAndReplacePattern](../05-Hashing-Prefix-Sum/C07_FindAndReplacePattern.java) |
| 492 | [Swim In Rising Water](https://leetcode.com/problems/swim-in-rising-water) | -- |
| 493 | [Number Of Operations To Make Network Connected](https://leetcode.com/problems/number-of-operations-to-make-network-connected) | [C08_NumberOfOperationsToMakeNetworkConnected](../11-Graphs/C08_NumberOfOperationsToMakeNetworkConnected.java) |
| 494 | [Sort An Array](https://leetcode.com/problems/sort-an-array) | [A05_MergeSort](../18-Sorting-Searching-Algorithms/A05_MergeSort.java) |
| 495 | [Path With Maximum Gold](https://leetcode.com/problems/path-with-maximum-gold) | -- |
| 496 | [Set Mismatch](https://leetcode.com/problems/set-mismatch) | [B11_FindCorruptPair](../01-Arrays/B11_FindCorruptPair.java) |
| 497 | [Split Linked List In Parts](https://leetcode.com/problems/split-linked-list-in-parts) | -- |
| 498 | [Longest Mountain In Array](https://leetcode.com/problems/longest-mountain-in-array) | [C13_LongestMountainInArray](../01-Arrays/C13_LongestMountainInArray.java) |
| 499 | [Rotate String](https://leetcode.com/problems/rotate-string) | [B15_RotateString](../04-Strings/B15_RotateString.java) |
| 500 | [Unique Email Addresses](https://leetcode.com/problems/unique-email-addresses) | -- |

## Partial coverage, and why

| # | Problem | Nearest file | What is missing |
| --- | --- | --- | --- |
| 32 | Permutations | [A05_Permutations](../14-Backtracking-Recursion/A05_Permutations.java) | Permutes a string and keeps duplicate outputs; LC 46 permutes distinct ints. |
| 76 | Linked List Cycle | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) | The file solves the harder LC 142 (entry node); LC 141 only wants true/false. |
| 205 | Next Greater Element Ii | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) | Plain array scan, no wrap-around. |
| 279 | Binary Search | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) | Lower/upper bound rather than the plain "return the index or -1" form. |
| 365 | Insert Into A Binary Search Tree | [A05_BinarySearchTreeOperations](../09-Trees-BST/A05_BinarySearchTreeOperations.java) | BST insert exists as a building block, not as the LC 701 problem. |
| 378 | Search In A Binary Search Tree | [A05_BinarySearchTreeOperations](../09-Trees-BST/A05_BinarySearchTreeOperations.java) | BST search exists as a building block, not as the LC 700 problem. |
| 480 | Next Greater Element I | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) | Single array; LC 496 maps answers from nums2 back onto nums1. |

