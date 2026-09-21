# Structured DSA Practice Roadmap (150 Questions)

An 11-phase practice roadmap of 150 classic DSA problems, numbered continuously
from 1 to 150 across the phases. The **Solved here** column links the practice
file in this repo that answers the problem, so the list doubles as a progress
tracker.

Converted from `AAScratches/_archive/original-txt/Classic_150.txt`. That folder
was deleted in commit `9474f16`; recover the original with
`git show 9474f16^:AAScratches/_archive/original-txt/Classic_150.txt`.

## At a glance

| Key | Value |
| --- | --- |
| Problems listed | 150 |
| Solved in this repo | **116** |
| Partly covered (marked `*`) | 11 |
| Not started | 23 |
| Practice files searched | 490 under `AAScratches/01-DSA/` |
| How a match was decided | the problem name, and the LeetCode number in each file's header comment |

A `*` after a link means the file covers the same technique but is not the same
problem statement. Every `*` is explained in
[Partial coverage, and why](#partial-coverage-and-why) at the end.

## Table of Contents

| Phase | Questions | Solved | Partly |
| --- | --- | --- | --- |
| [Phase 1](#phase-1-arrays--basics-15-q) | 15 | 15 | 0 |
| [Phase 2](#phase-2-strings-10-q) | 10 | 9 | 0 |
| [Phase 3](#phase-3-linked-list-10-q) | 10 | 9 | 0 |
| [Phase 4](#phase-4-stacks--queues-10-q) | 10 | 9 | 1 |
| [Phase 5](#phase-5-trees-15-q) | 15 | 14 | 0 |
| [Phase 6](#phase-6-recursion--backtracking-10-q) | 10 | 8 | 1 |
| [Phase 7](#phase-7-heaps--priority-queues-10-q) | 10 | 8 | 0 |
| [Phase 8](#phase-8-searching--sorting-10-q) | 10 | 8 | 2 |
| [Phase 9](#phase-9-dynamic-programming-25-q) | 25 | 16 | 1 |
| [Phase 10](#phase-10-graphs-20-q) | 20 | 13 | 2 |
| [Phase 11](#phase-11-advanced--miscellaneous-15-q) | 15 | 7 | 4 |

Two more sections close the file:
[Partial coverage, and why](#partial-coverage-and-why) (11 entries) and
[Still completely open](#still-completely-open) (23 entries).

---

## Phase 1: Arrays & Basics (15 Q)

**Focus:** Iteration, sliding window, prefix/suffix sums, sorting, hashing.

| # | Problem | Solved here |
| --- | --- | --- |
| 1 | Two Sum | [A02_TwoSum](../05-Hashing-Prefix-Sum/A02_TwoSum.java) |
| 2 | Best Time to Buy and Sell Stock | [B03_BestTimeToBuyAndSellStock](../01-Arrays/B03_BestTimeToBuyAndSellStock.java) |
| 3 | Contains Duplicate | [A01_ContainsDuplicate](../05-Hashing-Prefix-Sum/A01_ContainsDuplicate.java) |
| 4 | Move Zeroes | [A02_MoveZeroes](../02-Two-Pointers-Sliding-Window/A02_MoveZeroes.java) |
| 5 | Product of Array Except Self | [C05_ProductExceptSelf](../01-Arrays/C05_ProductExceptSelf.java) |
| 6 | Maximum Subarray (Kadane's Algorithm) | [C01_KadaneSAlgorithm](../01-Arrays/C01_KadaneSAlgorithm.java) |
| 7 | Merge Intervals | [A02_MergeIntervals](../15-Intervals/A02_MergeIntervals.java) |
| 8 | Missing Number | [B01_MissingNumber](../17-Math-Bit-Manipulation/B01_MissingNumber.java) |
| 9 | Find All Duplicates in an Array | [C14_FindAllDuplicatesInAnArray](../01-Arrays/C14_FindAllDuplicatesInAnArray.java) |
| 10 | Set Matrix Zeroes | [C01_SetMatrixZeroes](../16-Matrix/C01_SetMatrixZeroes.java) |
| 11 | Rotate Array | [C09_RotateArray](../01-Arrays/C09_RotateArray.java) |
| 12 | Longest Consecutive Sequence | [D01_LongestConsecutiveSequence](../05-Hashing-Prefix-Sum/D01_LongestConsecutiveSequence.java) |
| 13 | 3Sum | [C02_Three3Sum](../02-Two-Pointers-Sliding-Window/C02_Three3Sum.java) |
| 14 | Majority Element | [B09_MajorityElement](../01-Arrays/B09_MajorityElement.java) |
| 15 | Next Permutation | [C10_NextGreaterPermutation](../01-Arrays/C10_NextGreaterPermutation.java) |

## Phase 2: Strings (10 Q)

**Focus:** Hashing, frequency maps, two pointers, palindrome, substring logic.

| # | Problem | Solved here |
| --- | --- | --- |
| 16 | Valid Anagram | [A04_AnagramStrings](../04-Strings/A04_AnagramStrings.java) |
| 17 | Group Anagrams | [C05_GroupAnagrams](../05-Hashing-Prefix-Sum/C05_GroupAnagrams.java) |
| 18 | Longest Substring Without Repeating Characters | [C03_LongestSubStringWithoutRepeatingCharacter](../02-Two-Pointers-Sliding-Window/C03_LongestSubStringWithoutRepeatingCharacter.java) |
| 19 | Longest Palindromic Substring | [C12_LongestPalindrome](../12-Dynamic-Programming/C12_LongestPalindrome.java) |
| 20 | Valid Palindrome | [A06_PalindromeSpecial](../04-Strings/A06_PalindromeSpecial.java) |
| 21 | Implement strStr() / Substring Search | [D02_SingleLoopSubstringCheckKMP](../18-Sorting-Searching-Algorithms/D02_SingleLoopSubstringCheckKMP.java) |
| 22 | Longest Common Prefix | [B13_LongestCommonPrefix](../04-Strings/B13_LongestCommonPrefix.java) |
| 23 | Count and Say | -- |
| 24 | String Compression | [C03_StringCompression](../04-Strings/C03_StringCompression.java) |
| 25 | Minimum Window Substring | [D02_MinimumWindowSubstring](../02-Two-Pointers-Sliding-Window/D02_MinimumWindowSubstring.java) |

## Phase 3: Linked List (10 Q)

**Focus:** Pointers, reversing, cycle detection, merging.

| # | Problem | Solved here |
| --- | --- | --- |
| 26 | Reverse Linked List | [A02_ReverseLinkedList](../06-Linked-List/A02_ReverseLinkedList.java) |
| 27 | Merge Two Sorted Lists | [A04_MergeTwoSortedLists](../06-Linked-List/A04_MergeTwoSortedLists.java) |
| 28 | Linked List Cycle Detection | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) |
| 29 | Intersection of Two Linked Lists | [B02_IntersectionOfTwoLinkedLists](../06-Linked-List/B02_IntersectionOfTwoLinkedLists.java) |
| 30 | Remove Nth Node From End | [C02_DeleteNthNodefromEnd](../06-Linked-List/C02_DeleteNthNodefromEnd.java) |
| 31 | Reorder List | [C10_ReorderList](../06-Linked-List/C10_ReorderList.java) |
| 32 | Palindrome Linked List | [B03_PalindromeLinkedList](../06-Linked-List/B03_PalindromeLinkedList.java) |
| 33 | Add Two Numbers | [C01_AddTwoNumbers](../06-Linked-List/C01_AddTwoNumbers.java) |
| 34 | Copy List with Random Pointer | [C12_CopyRandomList](../06-Linked-List/C12_CopyRandomList.java) |
| 35 | Flatten a Multilevel Linked List | -- |

## Phase 4: Stacks & Queues (10 Q)

**Focus:** Stack simulation, monotonic stack, next greater/smaller, queue via stack.

| # | Problem | Solved here |
| --- | --- | --- |
| 36 | Valid Parentheses | [A02_ValidParentheses](../07-Stack-Queue-Monotonic/A02_ValidParentheses.java) |
| 37 | Min Stack | [A02_MinStack](../19-Design-Data-Structures/A02_MinStack.java) |
| 38 | Evaluate Reverse Polish Notation | [C03_EvaluateReversePolishNotation](../07-Stack-Queue-Monotonic/C03_EvaluateReversePolishNotation.java) |
| 39 | Daily Temperatures | [C06_DailyTemperatures](../07-Stack-Queue-Monotonic/C06_DailyTemperatures.java) |
| 40 | Next Greater Element I/II | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) `*` |
| 41 | Largest Rectangle in Histogram | [D02_LargestRectangleArea](../07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java) |
| 42 | Trapping Rain Water | [D01_TrappingRainWater](../02-Two-Pointers-Sliding-Window/D01_TrappingRainWater.java) |
| 43 | Implement Queue using Stacks | [A04_QueueUsingStacks](../07-Stack-Queue-Monotonic/A04_QueueUsingStacks.java) |
| 44 | Sliding Window Maximum | [D04_SlidingWindowMaximum](../07-Stack-Queue-Monotonic/D04_SlidingWindowMaximum.java) |
| 45 | Simplify Path | [C12_SimplifyPath](../07-Stack-Queue-Monotonic/C12_SimplifyPath.java) |

## Phase 5: Trees (15 Q)

**Focus:** Traversals, recursion, BST logic, depth/height.

| # | Problem | Solved here |
| --- | --- | --- |
| 46 | Maximum Depth of Binary Tree | [A02_HeightOfBinaryTree](../09-Trees-BST/A02_HeightOfBinaryTree.java) |
| 47 | Same Tree | [B02_SameTree](../09-Trees-BST/B02_SameTree.java) |
| 48 | Invert Binary Tree | [B01_InvertTree](../09-Trees-BST/B01_InvertTree.java) |
| 49 | Binary Tree Level Order Traversal | [A03_LevelOrderTraversal](../09-Trees-BST/A03_LevelOrderTraversal.java) |
| 50 | Symmetric Tree | [B03_SymmetricTree](../09-Trees-BST/B03_SymmetricTree.java) |
| 51 | Path Sum | [B05_HasPathSum](../09-Trees-BST/B05_HasPathSum.java) |
| 52 | Diameter of Binary Tree | [B09_DiameterOfBinaryTree](../09-Trees-BST/B09_DiameterOfBinaryTree.java) |
| 53 | Convert Sorted Array to BST | [B15_SortedArrayToBST](../09-Trees-BST/B15_SortedArrayToBST.java) |
| 54 | Lowest Common Ancestor of a BST | [C11_LowestCommonAncestorBST](../09-Trees-BST/C11_LowestCommonAncestorBST.java) |
| 55 | Validate Binary Search Tree | [C01_ValidateBST](../09-Trees-BST/C01_ValidateBST.java) |
| 56 | Construct Binary Tree from Preorder and Inorder | [C23_ConstructBinaryTreeFromINPre](../09-Trees-BST/C23_ConstructBinaryTreeFromINPre.java) |
| 57 | Binary Tree Right Side View | [C18_RightView](../09-Trees-BST/C18_RightView.java) |
| 58 | Sum Root to Leaf Numbers | -- |
| 59 | Subtree of Another Tree | [B04_IsSubtree](../09-Trees-BST/B04_IsSubtree.java) |
| 60 | Serialize and Deserialize Binary Tree | [D02_SerializeAndDeserialiseBinaryTree](../09-Trees-BST/D02_SerializeAndDeserialiseBinaryTree.java) |

## Phase 6: Recursion & Backtracking (10 Q)

**Focus:** Combinatorics, subsets, permutations, DFS generation.

| # | Problem | Solved here |
| --- | --- | --- |
| 61 | Subsets | [A04_Subsets](../14-Backtracking-Recursion/A04_Subsets.java) |
| 62 | Subsets II | [C02_SubsetsII](../14-Backtracking-Recursion/C02_SubsetsII.java) |
| 63 | Permutations | [A05_Permutations](../14-Backtracking-Recursion/A05_Permutations.java) `*` |
| 64 | Combination Sum | [C03_CombinationSum](../14-Backtracking-Recursion/C03_CombinationSum.java) |
| 65 | Combination Sum II | [C04_CombinationSumII](../14-Backtracking-Recursion/C04_CombinationSumII.java) |
| 66 | Letter Combinations of a Phone Number | [B02_LetterCombinations](../14-Backtracking-Recursion/B02_LetterCombinations.java) |
| 67 | Word Search | [C05_WordSearch](../14-Backtracking-Recursion/C05_WordSearch.java) |
| 68 | N-Queens | [D01_NQueens](../14-Backtracking-Recursion/D01_NQueens.java) |
| 69 | Generate Parentheses | [C01_GenerateParenthesis](../14-Backtracking-Recursion/C01_GenerateParenthesis.java) |
| 70 | Sudoku Solver | -- |

## Phase 7: Heaps & Priority Queues (10 Q)

**Focus:** Top-k problems, heap property, greedy merging.

| # | Problem | Solved here |
| --- | --- | --- |
| 71 | Kth Largest Element in Array | [C01_QuickSelect](../18-Sorting-Searching-Algorithms/C01_QuickSelect.java) |
| 72 | Merge K Sorted Lists | [D02_MergeKLists](../06-Linked-List/D02_MergeKLists.java) |
| 73 | Top K Frequent Elements | [C01_topKFrequent](../08-Heap-Priority-Queue/C01_topKFrequent.java) |
| 74 | Find Median from Data Stream | [D01_MedianOfStream](../08-Heap-Priority-Queue/D01_MedianOfStream.java) |
| 75 | K Closest Points to Origin | [C02_KClosestPointsToOrigin](../08-Heap-Priority-Queue/C02_KClosestPointsToOrigin.java) |
| 76 | Task Scheduler | -- |
| 77 | Reorganize String | [C04_ReorganizeString](../08-Heap-Priority-Queue/C04_ReorganizeString.java) |
| 78 | Sliding Window Median | [D02_SlidingWindowMedian](../08-Heap-Priority-Queue/D02_SlidingWindowMedian.java) |
| 79 | Minimum Cost to Connect Sticks | -- |
| 80 | Meeting Rooms II | [C03_MeetingRoomsII](../15-Intervals/C03_MeetingRoomsII.java) |

## Phase 8: Searching & Sorting (10 Q)

**Focus:** Binary search patterns, partition logic, merge/quick sort.

| # | Problem | Solved here |
| --- | --- | --- |
| 81 | Binary Search | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) `*` |
| 82 | Search Insert Position | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) `*` |
| 83 | Search in Rotated Sorted Array | [C06_SearchInRotatedSortedArray](../03-Binary-Search/C06_SearchInRotatedSortedArray.java) |
| 84 | Find Minimum in Rotated Sorted Array | [C04_MinIndexInRotatedSortedArray](../03-Binary-Search/C04_MinIndexInRotatedSortedArray.java) |
| 85 | Median of Two Sorted Arrays | [D02_MedianOfTwoSortedArrays](../03-Binary-Search/D02_MedianOfTwoSortedArrays.java) |
| 86 | Kth Largest Element (Quickselect) | [C01_QuickSelect](../18-Sorting-Searching-Algorithms/C01_QuickSelect.java) |
| 87 | Merge Sort | [A05_MergeSort](../18-Sorting-Searching-Algorithms/A05_MergeSort.java) |
| 88 | Quick Sort | [A06_QuickSort](../18-Sorting-Searching-Algorithms/A06_QuickSort.java) |
| 89 | Sort Colors (Dutch Flag) | [C08_Sort012](../01-Arrays/C08_Sort012.java) |
| 90 | Find Peak Element | [C03_FindPeakElement](../03-Binary-Search/C03_FindPeakElement.java) |

## Phase 9: Dynamic Programming (25 Q)

**Focus:** Subproblems, overlapping structure, tabulation, memoization.

| # | Problem | Solved here |
| --- | --- | --- |
| 91 | Fibonacci Number | [A01_Fibonacci](../12-Dynamic-Programming/A01_Fibonacci.java) |
| 92 | Climbing Stairs | [A02_ClimbingStairs](../12-Dynamic-Programming/A02_ClimbingStairs.java) |
| 93 | House Robber | [B03_HouseRobber](../12-Dynamic-Programming/B03_HouseRobber.java) |
| 94 | House Robber II | -- |
| 95 | Coin Change | [C08_CoinChangeMinimum](../12-Dynamic-Programming/C08_CoinChangeMinimum.java) |
| 96 | Longest Increasing Subsequence | [A08_LongestIncreasingSubsequence](../12-Dynamic-Programming/A08_LongestIncreasingSubsequence.java) |
| 97 | Longest Common Subsequence | [A07_CommonSubSequence](../12-Dynamic-Programming/A07_CommonSubSequence.java) |
| 98 | Edit Distance | [C11_EditDistance](../12-Dynamic-Programming/C11_EditDistance.java) |
| 99 | Unique Paths | [B01_UniquePaths](../12-Dynamic-Programming/B01_UniquePaths.java) |
| 100 | Minimum Path Sum | -- |
| 101 | Partition Equal Subset Sum | [A04_SubsetSumEqualsToTarget](../12-Dynamic-Programming/A04_SubsetSumEqualsToTarget.java) `*` |
| 102 | 0/1 Knapsack | [A05_Knapsack01](../12-Dynamic-Programming/A05_Knapsack01.java) |
| 103 | Decode Ways | [C01_NumDecodings](../12-Dynamic-Programming/C01_NumDecodings.java) |
| 104 | Word Break | -- |
| 105 | Palindromic Substrings | -- |
| 106 | Longest Palindromic Subsequence | [C10_LongestPalindromicSubsequence](../12-Dynamic-Programming/C10_LongestPalindromicSubsequence.java) |
| 107 | Maximum Product Subarray | [C02_MaxProductSubArray](../12-Dynamic-Programming/C02_MaxProductSubArray.java) |
| 108 | Jump Game | [C08_CanJump](../13-Greedy/C08_CanJump.java) |
| 109 | Best Time to Buy and Sell Stock with Cooldown | -- |
| 110 | Target Sum | [C07_TargetSumCountWays](../12-Dynamic-Programming/C07_TargetSumCountWays.java) |
| 111 | Distinct Subsequences | [D02_CountOfDistinctSubsequences](../12-Dynamic-Programming/D02_CountOfDistinctSubsequences.java) |
| 112 | Interleaving String | -- |
| 113 | Minimum Number of Coins | [C08_CoinChangeMinimum](../12-Dynamic-Programming/C08_CoinChangeMinimum.java) |
| 114 | Burst Balloons | -- |
| 115 | Maximum Sum Circular Subarray | -- |

## Phase 10: Graphs (20 Q)

**Focus:** BFS, DFS, topological sort, union-find, shortest path.

| # | Problem | Solved here |
| --- | --- | --- |
| 116 | Number of Islands | [B02_NumberOfIslands](../11-Graphs/B02_NumberOfIslands.java) |
| 117 | Clone Graph | -- |
| 118 | Course Schedule | [B10_CourseSchedule](../11-Graphs/B10_CourseSchedule.java) |
| 119 | Course Schedule II | [B10_CourseSchedule](../11-Graphs/B10_CourseSchedule.java) `*` |
| 120 | Pacific Atlantic Water Flow | -- |
| 121 | Word Ladder | [D01_WordLadder](../11-Graphs/D01_WordLadder.java) |
| 122 | Graph Valid Tree | -- |
| 123 | Detect Cycle in Directed Graph | [A03_CycleCheckInDirectedGraph](../11-Graphs/A03_CycleCheckInDirectedGraph.java) |
| 124 | Rotting Oranges | [B06_RottenOranges](../11-Graphs/B06_RottenOranges.java) |
| 125 | Minimum Height Trees | -- |
| 126 | Redundant Connection | -- |
| 127 | Accounts Merge | [C09_AccountsMerge](../11-Graphs/C09_AccountsMerge.java) |
| 128 | Kruskal's Algorithm (Union-Find) | [A10_KruskalAlgorithm](../11-Graphs/A10_KruskalAlgorithm.java) |
| 129 | Prim's Algorithm (MST) | [A09_PrimsAlgo](../11-Graphs/A09_PrimsAlgo.java) |
| 130 | Dijkstra's Algorithm | [A06_DijkstrasAlgoPQ](../11-Graphs/A06_DijkstrasAlgoPQ.java) |
| 131 | Bellman-Ford Algorithm | [A07_BellmanFord](../11-Graphs/A07_BellmanFord.java) |
| 132 | Floyd Warshall Algorithm | [A08_FloydWarshallAlgorithm](../11-Graphs/A08_FloydWarshallAlgorithm.java) |
| 133 | Topological Sort (Kahn's Algorithm) | [A04_ToposortDFS](../11-Graphs/A04_ToposortDFS.java) |
| 134 | Graph Bipartite Check | [B09_IsBipartite](../11-Graphs/B09_IsBipartite.java) |
| 135 | Number of Connected Components | [B03_NumberOfProvinces](../11-Graphs/B03_NumberOfProvinces.java) `*` |

## Phase 11: Advanced / Miscellaneous (15 Q)

**Focus:** Tries, design, bit manipulation, interval, math.

| # | Problem | Solved here |
| --- | --- | --- |
| 136 | Implement Trie (Prefix Tree) | [A01_Trie](../10-Trie/A01_Trie.java) |
| 137 | Word Search II | -- |
| 138 | Find Median from Data Stream | [D01_MedianOfStream](../08-Heap-Priority-Queue/D01_MedianOfStream.java) |
| 139 | LRU Cache | [C05_LRUCache](../19-Design-Data-Structures/C05_LRUCache.java) |
| 140 | LFU Cache | [D01_LFUCache](../19-Design-Data-Structures/D01_LFUCache.java) |
| 141 | Insert Delete GetRandom O(1) | [C03_InsertDeleteGetRandomOof1](../19-Design-Data-Structures/C03_InsertDeleteGetRandomOof1.java) |
| 142 | Bitwise AND of Numbers Range | -- |
| 143 | Single Number I/II/III | [A03_SingleNumber](../17-Math-Bit-Manipulation/A03_SingleNumber.java) `*` |
| 144 | Missing Number using XOR | [B01_MissingNumber](../17-Math-Bit-Manipulation/B01_MissingNumber.java) |
| 145 | Power of Two | [B06_PowerCheck](../17-Math-Bit-Manipulation/B06_PowerCheck.java) `*` |
| 146 | Count Bits | [A02_NumberOf1Bits](../17-Math-Bit-Manipulation/A02_NumberOf1Bits.java) `*` |
| 147 | Subarray Sum Equals K | [C01_CountSubarraySumEqualsK](../05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java) |
| 148 | Range Sum Query -- Immutable / Mutable | [D03_SegmentTree](../18-Sorting-Searching-Algorithms/D03_SegmentTree.java) `*` |
| 149 | Prefix Sum 2D (Matrix Range Query) | -- |
| 150 | Design Twitter | -- |

## Partial coverage, and why

These are the `*` entries. The linked file is real and worth reading and gets you
most of the way, but it is not the listed problem, so the listed problem is
counted separately from "solved" above.

| # | Problem | Nearest file | What is missing |
| --- | --- | --- | --- |
| 40 | Next Greater Element I/II | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) | Covers the plain left-to-right array form. The circular variant (LC 503) and the nums1/nums2 mapping of LC 496 are not done. |
| 63 | Permutations | [A05_Permutations](../14-Backtracking-Recursion/A05_Permutations.java) | Permutes the characters of a string and keeps duplicates. LC 46 permutes an int array of distinct values. |
| 81 | Binary Search | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) | The bounds are the binary-search template, but no file is framed as plain "find target, else -1" (LC 704). |
| 82 | Search Insert Position | [A01_LowerAndUpperBounds](../03-Binary-Search/A01_LowerAndUpperBounds.java) | `lowerBound` returns exactly the insert position LC 35 asks for, but the file is not written as that problem. |
| 101 | Partition Equal Subset Sum | [A04_SubsetSumEqualsToTarget](../12-Dynamic-Programming/A04_SubsetSumEqualsToTarget.java) | Subset-sum is the engine LC 416 runs on; the half-the-total framing is not done. |
| 119 | Course Schedule II | [B10_CourseSchedule](../11-Graphs/B10_CourseSchedule.java) | LC 207 answers yes/no. The order itself comes from `A04_ToposortDFS` (Kahn), but no file combines the two as LC 210. |
| 135 | Number of Connected Components | [B03_NumberOfProvinces](../11-Graphs/B03_NumberOfProvinces.java) | LC 547 is the same count on an adjacency matrix; LC 323 takes an edge list. |
| 143 | Single Number I/II/III | [A03_SingleNumber](../17-Math-Bit-Manipulation/A03_SingleNumber.java) | Only Single Number I (LC 136). II (LC 137) and III (LC 260) are not done. |
| 145 | Power of Two | [B06_PowerCheck](../17-Math-Bit-Manipulation/B06_PowerCheck.java) | Tests any base by division. The LC 231 bit trick `n & (n - 1) == 0` is not shown. |
| 146 | Count Bits | [A02_NumberOf1Bits](../17-Math-Bit-Manipulation/A02_NumberOf1Bits.java) | Counts bits in one number (LC 191). LC 338 wants the DP over 0..n. |
| 148 | Range Sum Query -- Immutable / Mutable | [D03_SegmentTree](../18-Sorting-Searching-Algorithms/D03_SegmentTree.java) | Range sum plus point update is the mutable version (LC 307). No immutable prefix-sum class (LC 303). |

## Still completely open

The 23 problems with no file of any kind in this repo, in roadmap order.

| # | Problem |
| --- | --- |
| 23 | Count and Say |
| 35 | Flatten a Multilevel Linked List |
| 58 | Sum Root to Leaf Numbers |
| 70 | Sudoku Solver |
| 76 | Task Scheduler |
| 79 | Minimum Cost to Connect Sticks |
| 94 | House Robber II |
| 100 | Minimum Path Sum |
| 104 | Word Break |
| 105 | Palindromic Substrings |
| 109 | Best Time to Buy and Sell Stock with Cooldown |
| 112 | Interleaving String |
| 114 | Burst Balloons |
| 115 | Maximum Sum Circular Subarray |
| 117 | Clone Graph |
| 120 | Pacific Atlantic Water Flow |
| 122 | Graph Valid Tree |
| 125 | Minimum Height Trees |
| 126 | Redundant Connection |
| 137 | Word Search II |
| 142 | Bitwise AND of Numbers Range |
| 149 | Prefix Sum 2D (Matrix Range Query) |
| 150 | Design Twitter |

