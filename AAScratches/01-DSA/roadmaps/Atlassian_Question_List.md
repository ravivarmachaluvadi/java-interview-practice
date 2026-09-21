# Atlassian Question List

207 LeetCode problems tagged for Atlassian interview practice, kept in the
original order (most-reported first). Each entry keeps its LeetCode number, and
the **Solved here** column links the practice file in this repo that answers it.

Converted from `AAScratches/_archive/original-txt/AtlassianQs.txt`. That folder
was deleted in commit `9474f16`; recover the original with
`git show 9474f16^:AAScratches/_archive/original-txt/AtlassianQs.txt`.

## At a glance

| Key | Value |
| --- | --- |
| Problems listed | 207 |
| Solved in this repo | **141** |
| Partly covered (marked `*`) | 6 |
| Not started | 60 |
| Practice files searched | 490 under `AAScratches/01-DSA/` |
| How a match was decided | the LeetCode number written in each file's header comment |

Three entries (104, 117, 151) are **SQL** problems on LeetCode, so no Java file
will ever match them; they are marked in the table. A `*` after a link means the
file covers the same technique but is not the same problem statement -- see
[Partial coverage, and why](#partial-coverage-and-why).

## Sections

| Range | Solved | Partly | Open |
| --- | --- | --- | --- |
| [1-50](#questions-1-50) | 47 | 0 | 3 |
| [51-100](#questions-51-100) | 39 | 3 | 8 |
| [101-150](#questions-101-150) | 36 | 3 | 11 |
| [151-200](#questions-151-200) | 19 | 0 | 31 |
| [201-207](#questions-201-207) | 0 | 0 | 7 |

---

## Questions 1-50

| # | LC | Problem | Solved here |
| --- | --- | --- | --- |
| 1 | 1 | Two Sum | [A02_TwoSum](../05-Hashing-Prefix-Sum/A02_TwoSum.java) |
| 2 | 42 | Trapping Rain Water | [D01_TrappingRainWater](../02-Two-Pointers-Sliding-Window/D01_TrappingRainWater.java) |
| 3 | 146 | LRU Cache | [C05_LRUCache](../19-Design-Data-Structures/C05_LRUCache.java) |
| 4 | 121 | Best Time to Buy and Sell Stock | [B03_BestTimeToBuyAndSellStock](../01-Arrays/B03_BestTimeToBuyAndSellStock.java) |
| 5 | 3 | Longest Substring Without Repeating Characters | [C03_LongestSubStringWithoutRepeatingCharacter](../02-Two-Pointers-Sliding-Window/C03_LongestSubStringWithoutRepeatingCharacter.java) |
| 6 | 56 | Merge Intervals | [A02_MergeIntervals](../15-Intervals/A02_MergeIntervals.java) |
| 7 | 20 | Valid Parentheses | [A02_ValidParentheses](../07-Stack-Queue-Monotonic/A02_ValidParentheses.java) |
| 8 | 14 | Longest Common Prefix | [B13_LongestCommonPrefix](../04-Strings/B13_LongestCommonPrefix.java) |
| 9 | 2 | Add Two Numbers | [C01_AddTwoNumbers](../06-Linked-List/C01_AddTwoNumbers.java) |
| 10 | 4 | Median of Two Sorted Arrays | [D02_MedianOfTwoSortedArrays](../03-Binary-Search/D02_MedianOfTwoSortedArrays.java) |
| 11 | 200 | Number of Islands | [B02_NumberOfIslands](../11-Graphs/B02_NumberOfIslands.java) |
| 12 | 560 | Subarray Sum Equals K | [C01_CountSubarraySumEqualsK](../05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java) |
| 13 | 11 | Container With Most Water | [C01_ContainerWithMostWater](../02-Two-Pointers-Sliding-Window/C01_ContainerWithMostWater.java) |
| 14 | 2561 | Rearranging Fruits | [D04_RearrangingFruits](../13-Greedy/D04_RearrangingFruits.java) |
| 15 | 15 | 3Sum | [C02_Three3Sum](../02-Two-Pointers-Sliding-Window/C02_Three3Sum.java) |
| 16 | 118 | Pascal's Triangle | [B11_PascalTriangleTwoLoops](../17-Math-Bit-Manipulation/B11_PascalTriangleTwoLoops.java) |
| 17 | 49 | Group Anagrams | [C05_GroupAnagrams](../05-Hashing-Prefix-Sum/C05_GroupAnagrams.java) |
| 18 | 875 | Koko Eating Bananas | [C11_KokoEatingBananas](../03-Binary-Search/C11_KokoEatingBananas.java) |
| 19 | 68 | Text Justification | [D02_TextJustification](../04-Strings/D02_TextJustification.java) |
| 20 | 53 | Maximum Subarray | [C01_KadaneSAlgorithm](../01-Arrays/C01_KadaneSAlgorithm.java) |
| 21 | 1650 | Lowest Common Ancestor of a Binary Tree III | [C15_LowestCommonAncestorOfaBinaryTreeIII](../09-Trees-BST/C15_LowestCommonAncestorOfaBinaryTreeIII.java) |
| 22 | 253 | Meeting Rooms II | [C03_MeetingRoomsII](../15-Intervals/C03_MeetingRoomsII.java) |
| 23 | 31 | Next Permutation | [C10_NextGreaterPermutation](../01-Arrays/C10_NextGreaterPermutation.java) |
| 24 | 347 | Top K Frequent Elements | [C01_topKFrequent](../08-Heap-Priority-Queue/C01_topKFrequent.java) |
| 25 | 2235 | Add Two Integers | -- |
| 26 | 239 | Sliding Window Maximum | [D04_SlidingWindowMaximum](../07-Stack-Queue-Monotonic/D04_SlidingWindowMaximum.java) |
| 27 | 48 | Rotate Image | [A01_MatrixRotate90Degree](../16-Matrix/A01_MatrixRotate90Degree.java) |
| 28 | 128 | Longest Consecutive Sequence | [D01_LongestConsecutiveSequence](../05-Hashing-Prefix-Sum/D01_LongestConsecutiveSequence.java) |
| 29 | 236 | Lowest Common Ancestor of a Binary Tree | [C12_LCA](../09-Trees-BST/C12_LCA.java) |
| 30 | 588 | Design In-Memory File System | [D03_DesignInMemoryFileSystem](../19-Design-Data-Structures/D03_DesignInMemoryFileSystem.java) |
| 31 | 23 | Merge k Sorted Lists | [D02_MergeKLists](../06-Linked-List/D02_MergeKLists.java) |
| 32 | 88 | Merge Sorted Array | [D03_MergeTwoSortedArrays](../02-Two-Pointers-Sliding-Window/D03_MergeTwoSortedArrays.java) |
| 33 | 33 | Search in Rotated Sorted Array | [C06_SearchInRotatedSortedArray](../03-Binary-Search/C06_SearchInRotatedSortedArray.java) |
| 34 | 21 | Merge Two Sorted Lists | [A04_MergeTwoSortedLists](../06-Linked-List/A04_MergeTwoSortedLists.java) |
| 35 | 432 | All O`one Data Structure | [D02_AllOne](../19-Design-Data-Structures/D02_AllOne.java) |
| 36 | 540 | Single Element in a Sorted Array | [C08_SingleNonDuplicateElement](../03-Binary-Search/C08_SingleNonDuplicateElement.java) |
| 37 | 1792 | Maximum Average Pass Ratio | [C05_MaximumAveragePassRatio](../08-Heap-Priority-Queue/C05_MaximumAveragePassRatio.java) |
| 38 | 443 | String Compression | [C03_StringCompression](../04-Strings/C03_StringCompression.java) |
| 39 | 189 | Rotate Array | [C09_RotateArray](../01-Arrays/C09_RotateArray.java) |
| 40 | 71 | Simplify Path | [C12_SimplifyPath](../07-Stack-Queue-Monotonic/C12_SimplifyPath.java) |
| 41 | 135 | Candy | [D01_Candy](../13-Greedy/D01_Candy.java) |
| 42 | 2901 | Longest Unequal Adjacent Groups Subsequence II | -- |
| 43 | 1944 | Number of Visible People in a Queue | [D03_VisiblePeopleInQueue](../07-Stack-Queue-Monotonic/D03_VisiblePeopleInQueue.java) |
| 44 | 348 | Design Tic-Tac-Toe | -- |
| 45 | 242 | Valid Anagram | [A04_AnagramStrings](../04-Strings/A04_AnagramStrings.java) |
| 46 | 1268 | Search Suggestions System | [C03_SearchSuggestionsSystem](../10-Trie/C03_SearchSuggestionsSystem.java) |
| 47 | 224 | Basic Calculator | [D05_BasicCalculator](../07-Stack-Queue-Monotonic/D05_BasicCalculator.java) |
| 48 | 34 | Find First and Last Position of Element in Sorted Array | [A02_FirstAndLastOccurrence](../03-Binary-Search/A02_FirstAndLastOccurrence.java) |
| 49 | 362 | Design Hit Counter | [C02_DesignHitCounter](../19-Design-Data-Structures/C02_DesignHitCounter.java) |
| 50 | 322 | Coin Change | [C08_CoinChangeMinimum](../12-Dynamic-Programming/C08_CoinChangeMinimum.java) |

## Questions 51-100

| # | LC | Problem | Solved here |
| --- | --- | --- | --- |
| 51 | 790 | Domino and Tromino Tiling | -- |
| 52 | 79 | Word Search | [C05_WordSearch](../14-Backtracking-Recursion/C05_WordSearch.java) |
| 53 | 410 | Split Array Largest Sum | -- |
| 54 | 981 | Time Based Key-Value Store | [C06_TimeBasedKeyValueStore](../19-Design-Data-Structures/C06_TimeBasedKeyValueStore.java) |
| 55 | 10 | Regular Expression Matching | [D03_RegularExpressionMatchingMemo](../12-Dynamic-Programming/D03_RegularExpressionMatchingMemo.java) |
| 56 | 155 | Min Stack | [A02_MinStack](../19-Design-Data-Structures/A02_MinStack.java) |
| 57 | 1091 | Shortest Path in Binary Matrix | [B08_ShortestPathInBinaryMatrix](../11-Graphs/B08_ShortestPathInBinaryMatrix.java) |
| 58 | 17 | Letter Combinations of a Phone Number | [B02_LetterCombinations](../14-Backtracking-Recursion/B02_LetterCombinations.java) |
| 59 | 41 | First Missing Positive | [D01_FirstMissingPositive](../01-Arrays/D01_FirstMissingPositive.java) |
| 60 | 692 | Top K Frequent Words | [C01_topKFrequent](../08-Heap-Priority-Queue/C01_topKFrequent.java) `*` |
| 61 | 1166 | Design File System | [C12_DesignFileSystem](../19-Design-Data-Structures/C12_DesignFileSystem.java) |
| 62 | 122 | Best Time to Buy and Sell Stock II | [C01_BestTimeToBuyAndSellStockII](../13-Greedy/C01_BestTimeToBuyAndSellStockII.java) |
| 63 | 217 | Contains Duplicate | [A01_ContainsDuplicate](../05-Hashing-Prefix-Sum/A01_ContainsDuplicate.java) |
| 64 | 359 | Logger Rate Limiter | [B01_LoggerRateLimiter](../19-Design-Data-Structures/B01_LoggerRateLimiter.java) |
| 65 | 904 | Fruit Into Baskets | [C06_FruitIntoBaskets](../02-Two-Pointers-Sliding-Window/C06_FruitIntoBaskets.java) |
| 66 | 300 | Longest Increasing Subsequence | [A08_LongestIncreasingSubsequence](../12-Dynamic-Programming/A08_LongestIncreasingSubsequence.java) |
| 67 | 1326 | Minimum Number of Taps to Open to Water a Garden | [D02_MinimumNumberOfTapsToOpenToWaterAGarden](../13-Greedy/D02_MinimumNumberOfTapsToOpenToWaterAGarden.java) |
| 68 | 2484 | Count Palindromic Subsequences | [D04_CountPalindromicSubsequences](../12-Dynamic-Programming/D04_CountPalindromicSubsequences.java) |
| 69 | 986 | Interval List Intersections | [C02_IntervalListIntersections](../15-Intervals/C02_IntervalListIntersections.java) |
| 70 | 297 | Serialize and Deserialize Binary Tree | [D02_SerializeAndDeserialiseBinaryTree](../09-Trees-BST/D02_SerializeAndDeserialiseBinaryTree.java) |
| 71 | 45 | Jump Game II | [C09_JumpGameII](../13-Greedy/C09_JumpGameII.java) |
| 72 | 735 | Asteroid Collision | [C11_AsteroidCollision](../07-Stack-Queue-Monotonic/C11_AsteroidCollision.java) |
| 73 | 2858 | Minimum Edge Reversals So Every Node Is Reachable | [D08_MinimumEdgeReversals](../11-Graphs/D08_MinimumEdgeReversals.java) |
| 74 | 131 | Palindrome Partitioning | [C15_PalindromePartitioning](../12-Dynamic-Programming/C15_PalindromePartitioning.java) |
| 75 | 354 | Russian Doll Envelopes | -- |
| 76 | 12 | Integer to Roman | [C04_IntegerToRoman](../04-Strings/C04_IntegerToRoman.java) |
| 77 | 503 | Next Greater Element II | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) `*` |
| 78 | 2402 | Meeting Rooms III | [D01_MeetingRoomsIII](../15-Intervals/D01_MeetingRoomsIII.java) |
| 79 | 139 | Word Break | -- |
| 80 | 69 | Sqrt(x) | [A04_SquareRoot](../03-Binary-Search/A04_SquareRoot.java) |
| 81 | 28 | Find the Index of the First Occurrence in a String | [D02_SingleLoopSubstringCheckKMP](../18-Sorting-Searching-Algorithms/D02_SingleLoopSubstringCheckKMP.java) |
| 82 | 658 | Find K Closest Elements | -- |
| 83 | 151 | Reverse Words in a String | [C01_ReverseWordsInString](../04-Strings/C01_ReverseWordsInString.java) |
| 84 | 1752 | Check if Array Is Sorted and Rotated | -- |
| 85 | 1146 | Snapshot Array | [C07_SnapshotArray](../19-Design-Data-Structures/C07_SnapshotArray.java) |
| 86 | 1642 | Furthest Building You Can Reach | [C07_FurthestBuildingYouCanReach](../08-Heap-Priority-Queue/C07_FurthestBuildingYouCanReach.java) |
| 87 | 66 | Plus One | [B01_PlusOne](../01-Arrays/B01_PlusOne.java) |
| 88 | 3552 | Grid Teleportation Traversal | [C15_GridTeleportationTraversal](../11-Graphs/C15_GridTeleportationTraversal.java) |
| 89 | 494 | Target Sum | [C07_TargetSumCountWays](../12-Dynamic-Programming/C07_TargetSumCountWays.java) |
| 90 | 39 | Combination Sum | [C03_CombinationSum](../14-Backtracking-Recursion/C03_CombinationSum.java) |
| 91 | 240 | Search a 2D Matrix II | [C01_Search2DMatrix](../03-Binary-Search/C01_Search2DMatrix.java) `*` |
| 92 | 741 | Cherry Pickup | [D09_CherryPickup](../12-Dynamic-Programming/D09_CherryPickup.java) |
| 93 | 67 | Add Binary | -- |
| 94 | 27 | Remove Element | [A01_RemoveElement](../02-Two-Pointers-Sliding-Window/A01_RemoveElement.java) |
| 95 | 148 | Sort List | [C11_SortList](../06-Linked-List/C11_SortList.java) |
| 96 | 1432 | Max Difference You Can Get From Changing an Integer | [C07_MaxDifferenceChangingInteger](../13-Greedy/C07_MaxDifferenceChangingInteger.java) |
| 97 | 912 | Sort an Array | [A05_MergeSort](../18-Sorting-Searching-Algorithms/A05_MergeSort.java) |
| 98 | 16 | 3Sum Closest | -- |
| 99 | 143 | Reorder List | [C10_ReorderList](../06-Linked-List/C10_ReorderList.java) |
| 100 | 1970 | Last Day Where You Can Still Cross | [D06_LastDayToCrossPQ](../11-Graphs/D06_LastDayToCrossPQ.java) |

## Questions 101-150

| # | LC | Problem | Solved here |
| --- | --- | --- | --- |
| 101 | 234 | Palindrome Linked List | [B03_PalindromeLinkedList](../06-Linked-List/B03_PalindromeLinkedList.java) |
| 102 | 881 | Boats to Save People | [C02_BoatsToSavePeople](../13-Greedy/C02_BoatsToSavePeople.java) |
| 103 | 752 | Open the Lock | [C03_OpenTheLock](../11-Graphs/C03_OpenTheLock.java) |
| 104 | 177 | Nth Highest Salary | -- (SQL) |
| 105 | 977 | Squares of a Sorted Array | [B01_SquaresOfASortedArray](../02-Two-Pointers-Sliding-Window/B01_SquaresOfASortedArray.java) |
| 106 | 605 | Can Place Flowers | [B01_CanPlaceFlowers](../13-Greedy/B01_CanPlaceFlowers.java) |
| 107 | 44 | Wildcard Matching | -- |
| 108 | 137 | Single Number II | [A03_SingleNumber](../17-Math-Bit-Manipulation/A03_SingleNumber.java) `*` |
| 109 | 3000 | Maximum Area of Longest Diagonal Rectangle | [B14_MaximumAreaOfLongestDiagonalRectangle](../17-Math-Bit-Manipulation/B14_MaximumAreaOfLongestDiagonalRectangle.java) |
| 110 | 2353 | Design a Food Rating System | [C09_DesignAFoodRatingSystem](../19-Design-Data-Structures/C09_DesignAFoodRatingSystem.java) |
| 111 | 1307 | Verbal Arithmetic Puzzle | -- |
| 112 | 353 | Design Snake Game | [C10_DesignSnakeGame](../19-Design-Data-Structures/C10_DesignSnakeGame.java) |
| 113 | 2034 | Stock Price Fluctuation | [C08_StockPriceFluctuation](../19-Design-Data-Structures/C08_StockPriceFluctuation.java) |
| 114 | 141 | Linked List Cycle | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) `*` |
| 115 | 103 | Binary Tree Zigzag Level Order Traversal | [C20_BinaryTreeZigzagTraversal](../09-Trees-BST/C20_BinaryTreeZigzagTraversal.java) |
| 116 | 1922 | Count Good Numbers | -- |
| 117 | 180 | Consecutive Numbers | -- (SQL) |
| 118 | 909 | Snakes and Ladders | -- |
| 119 | 547 | Number of Provinces | [B03_NumberOfProvinces](../11-Graphs/B03_NumberOfProvinces.java) |
| 120 | 713 | Subarray Product Less Than K | [C08_SubarrayProductLessThanK](../02-Two-Pointers-Sliding-Window/C08_SubarrayProductLessThanK.java) |
| 121 | 930 | Binary Subarrays With Sum | [C02_BinarySubarraysWithSum](../05-Hashing-Prefix-Sum/C02_BinarySubarraysWithSum.java) |
| 122 | 229 | Majority Element II | [B09_MajorityElement](../01-Arrays/B09_MajorityElement.java) `*` |
| 123 | 3541 | Find Most Frequent Vowel and Consonant | [B04_MostFrequentVowelAndConsonant](../04-Strings/B04_MostFrequentVowelAndConsonant.java) |
| 124 | 383 | Ransom Note | [A03_RansomNote](../05-Hashing-Prefix-Sum/A03_RansomNote.java) |
| 125 | 1048 | Longest String Chain | [C13_LongestStringChain](../12-Dynamic-Programming/C13_LongestStringChain.java) |
| 126 | 1797 | Design Authentication Manager | [C01_DesignAuthenticationManager](../19-Design-Data-Structures/C01_DesignAuthenticationManager.java) |
| 127 | 3649 | Number of Perfect Pairs | [D01_NumberOfPerfectPairs](../17-Math-Bit-Manipulation/D01_NumberOfPerfectPairs.java) |
| 128 | 252 | Meeting Rooms | [A01_MeetingRooms](../15-Intervals/A01_MeetingRooms.java) |
| 129 | 1242 | Web Crawler Multithreaded | [D09_HtmlParser](../11-Graphs/D09_HtmlParser.java) |
| 130 | 435 | Non-overlapping Intervals | [C06_NonOverlappingIntervals](../15-Intervals/C06_NonOverlappingIntervals.java) |
| 131 | 366 | Find Leaves of Binary Tree | [C26_FindLeavesOfBinaryTree](../09-Trees-BST/C26_FindLeavesOfBinaryTree.java) |
| 132 | 3035 | Maximum Palindromes After Operations | [C11_MaximumPalindromesAfterOperations](../13-Greedy/C11_MaximumPalindromesAfterOperations.java) |
| 133 | 24 | Swap Nodes in Pairs | -- |
| 134 | 235 | Lowest Common Ancestor of a Binary Search Tree | [C11_LowestCommonAncestorBST](../09-Trees-BST/C11_LowestCommonAncestorBST.java) |
| 135 | 2598 | Smallest Missing Non-negative Integer After Operations | [D02_SmallestMissingNonNegativeIntegerAfterOperations](../05-Hashing-Prefix-Sum/D02_SmallestMissingNonNegativeIntegerAfterOperations.java) |
| 136 | 40 | Combination Sum II | [C04_CombinationSumII](../14-Backtracking-Recursion/C04_CombinationSumII.java) |
| 137 | 1552 | Magnetic Force Between Two Balls | [C13_MagneticForceBetweenTwoBalls](../03-Binary-Search/C13_MagneticForceBetweenTwoBalls.java) |
| 138 | 211 | Design Add and Search Words Data Structure | [C02_DesignAddAndSearchWordsDataStructure](../10-Trie/C02_DesignAddAndSearchWordsDataStructure.java) |
| 139 | 523 | Continuous Subarray Sum | -- |
| 140 | 153 | Find Minimum in Rotated Sorted Array | [C04_MinIndexInRotatedSortedArray](../03-Binary-Search/C04_MinIndexInRotatedSortedArray.java) |
| 141 | 1669 | Merge In Between Linked Lists | [C14_MergeInBetweenLinkedLists](../06-Linked-List/C14_MergeInBetweenLinkedLists.java) |
| 142 | 3217 | Delete Nodes From Linked List Present in Array | -- |
| 143 | 226 | Invert Binary Tree | [B01_InvertTree](../09-Trees-BST/B01_InvertTree.java) |
| 144 | 1676 | Lowest Common Ancestor of a Binary Tree IV | [C14_LowestCommonAncestorIV](../09-Trees-BST/C14_LowestCommonAncestorIV.java) |
| 145 | 1366 | Rank Teams by Votes | [C03_RankTeamsByVotes](../20-Scenario-Based-Problems/C03_RankTeamsByVotes.java) |
| 146 | 142 | Linked List Cycle II | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) |
| 147 | 956 | Tallest Billboard | [D06_TallestBillboard](../12-Dynamic-Programming/D06_TallestBillboard.java) |
| 148 | 1760 | Minimum Limit of Balls in a Bag | -- |
| 149 | 1929 | Concatenation of Array | -- |
| 150 | 802 | Find Eventual Safe States | [C06_EventualSafeNodesByDFS](../11-Graphs/C06_EventualSafeNodesByDFS.java) |

## Questions 151-200

| # | LC | Problem | Solved here |
| --- | --- | --- | --- |
| 151 | 1141 | User Activity for the Past 30 Days I | -- (SQL) |
| 152 | 1910 | Remove All Occurrences of a Substring | [C06_RemoveAllOccurrences](../04-Strings/C06_RemoveAllOccurrences.java) |
| 153 | 729 | My Calendar I | -- |
| 154 | 1257 | Smallest Common Region | [C16_SmallestCommonRegion](../09-Trees-BST/C16_SmallestCommonRegion.java) |
| 155 | 3346 | Maximum Frequency of an Element After Performing Operations I | [C11_MaximumFrequencyOfAnElementAfterPerformingOperationsI](../02-Two-Pointers-Sliding-Window/C11_MaximumFrequencyOfAnElementAfterPerformingOperationsI.java) |
| 156 | 450 | Delete Node in a BST | [C05_DeleteNodeInBST](../09-Trees-BST/C05_DeleteNodeInBST.java) |
| 157 | 874 | Walking Robot Simulation | [C04_WalkingRobotSimulation](../16-Matrix/C04_WalkingRobotSimulation.java) |
| 158 | 1644 | Lowest Common Ancestor of a Binary Tree II | [C13_LowestCommonAncestorOfABinaryTreeII](../09-Trees-BST/C13_LowestCommonAncestorOfABinaryTreeII.java) |
| 159 | 703 | Kth Largest Element in a Stream | [A01_KthLargestElementInAStream](../08-Heap-Priority-Queue/A01_KthLargestElementInAStream.java) |
| 160 | 2089 | Find Target Indices After Sorting Array | -- |
| 161 | 1010 | Pairs of Songs With Total Durations Divisible by 60 | [C10_PairsOfSongsDivBy60](../05-Hashing-Prefix-Sum/C10_PairsOfSongsDivBy60.java) |
| 162 | 2559 | Count Vowel Strings in Ranges | [A04_CountVowelStringsInRanges](../05-Hashing-Prefix-Sum/A04_CountVowelStringsInRanges.java) |
| 163 | 1974 | Minimum Time to Type Word Using Special Typewriter | -- |
| 164 | 2448 | Minimum Cost to Make Array Equal | [D03_MinimumCostToMakeArrayEqual](../03-Binary-Search/D03_MinimumCostToMakeArrayEqual.java) |
| 165 | 1160 | Find Words That Can Be Formed by Characters | -- |
| 166 | 2933 | High-Access Employees | [C01_HighAccessEmployees](../20-Scenario-Based-Problems/C01_HighAccessEmployees.java) |
| 167 | 1381 | Design a Stack With Increment Operation | [C04_DesignAStackWithIncrementOperation](../19-Design-Data-Structures/C04_DesignAStackWithIncrementOperation.java) |
| 168 | 1863 | Sum of All Subset XOR Totals | -- |
| 169 | 2193 | Minimum Number of Moves to Make Palindrome | [D03_MinimumMovesToMakePalindrome](../13-Greedy/D03_MinimumMovesToMakePalindrome.java) |
| 170 | 1123 | Lowest Common Ancestor of Deepest Leaves | -- |
| 171 | 2751 | Robot Collisions | [D06_RobotCollisions](../07-Stack-Queue-Monotonic/D06_RobotCollisions.java) |
| 172 | 1334 | Find the City With the Smallest Number of Neighbors at a Threshold Distance | [C13_FindTheCity](../11-Graphs/C13_FindTheCity.java) |
| 173 | 1043 | Partition Array for Maximum Sum | -- |
| 174 | 846 | Hand of Straights | -- |
| 175 | 2406 | Divide Intervals Into Minimum Number of Groups | [C04_DivideIntervalsIntoMinGroups](../15-Intervals/C04_DivideIntervalsIntoMinGroups.java) |
| 176 | 3149 | Find the Minimum Cost Array Permutation | -- |
| 177 | 1941 | Check if All Characters Have Equal Number of Occurrences | -- |
| 178 | 2574 | Left and Right Sum Differences | -- |
| 179 | 821 | Shortest Distance to a Character | -- |
| 180 | 506 | Relative Ranks | -- |
| 181 | 2577 | Minimum Time to Visit a Cell In a Grid | [D05_MinimumTimeToVisitCell](../11-Graphs/D05_MinimumTimeToVisitCell.java) |
| 182 | 1310 | XOR Queries of a Subarray | -- |
| 183 | 664 | Strange Printer | -- |
| 184 | 2948 | Make Lexicographically Smallest Array by Swapping Elements | -- |
| 185 | 2485 | Find the Pivot Integer | -- |
| 186 | 1348 | Tweet Counts Per Frequency | -- |
| 187 | 757 | Set Intersection Size At Least Two | -- |
| 188 | 3026 | Maximum Good Subarray Sum | -- |
| 189 | 2376 | Count Special Integers | -- |
| 190 | 3211 | Generate Binary Strings Without Adjacent Zeros | -- |
| 191 | 2220 | Minimum Bit Flips to Convert Number | [B02_MinimumBitFlipsToConvertNumber](../17-Math-Bit-Manipulation/B02_MinimumBitFlipsToConvertNumber.java) |
| 192 | 2501 | Longest Square Streak in an Array | -- |
| 193 | 2039 | The Time When the Network Becomes Idle | -- |
| 194 | 2530 | Maximal Score After Applying K Operations | -- |
| 195 | 3168 | Minimum Number of Chairs in a Waiting Room | -- |
| 196 | 1382 | Balance a Binary Search Tree | -- |
| 197 | 911 | Online Election | [C09_TopVotedCandidate](../03-Binary-Search/C09_TopVotedCandidate.java) |
| 198 | 951 | Flip Equivalent Binary Trees | -- |
| 199 | 1220 | Count Vowels Permutation | -- |
| 200 | 2093 | Minimum Cost to Reach City With Discounts | -- |

## Questions 201-207

| # | LC | Problem | Solved here |
| --- | --- | --- | --- |
| 201 | 2263 | Make Array Non-decreasing or Non-increasing | -- |
| 202 | 2463 | Minimum Total Distance Traveled | -- |
| 203 | 2959 | Number of Possible Sets of Closing Branches | -- |
| 204 | 2975 | Maximum Square Area by Removing Fences From a Field | -- |
| 205 | 2976 | Minimum Cost to Convert String I | -- |
| 206 | 3014 | Minimum Number of Pushes to Type Word I | -- |
| 207 | 3167 | Better Compression of String | -- |

## Partial coverage, and why

| # | LC | Problem | Nearest file | What is missing |
| --- | --- | --- | --- | --- |
| 60 | 692 | Top K Frequent Words | [C01_topKFrequent](../08-Heap-Priority-Queue/C01_topKFrequent.java) | LC 347 ranks numbers by count. LC 692 ranks words and breaks ties alphabetically. |
| 77 | 503 | Next Greater Element II | [A03_NextGreaterElements](../07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) | The file scans a plain array. LC 503 wraps around the end of the array. |
| 91 | 240 | Search a 2D Matrix II | [C01_Search2DMatrix](../03-Binary-Search/C01_Search2DMatrix.java) | LC 74 treats the matrix as one sorted array. LC 240 rows and columns are sorted independently, which needs the staircase walk. |
| 108 | 137 | Single Number II | [A03_SingleNumber](../17-Math-Bit-Manipulation/A03_SingleNumber.java) | LC 136 (every other value twice) is done. LC 137 has them three times, which XOR alone does not solve. |
| 114 | 141 | Linked List Cycle | [C07_LinkedListLoopDetection](../06-Linked-List/C07_LinkedListLoopDetection.java) | The file solves the harder LC 142 (return the entry node); LC 141 only asks true/false, so it is covered in substance but not written as that problem. |
| 122 | 229 | Majority Element II | [B09_MajorityElement](../01-Arrays/B09_MajorityElement.java) | Boyer-Moore for the strictly-more-than-n/2 element. LC 229 wants every element appearing more than n/3 times, which needs two counters. |

## Still completely open

The 60 entries with no file of any kind in this repo, in list order, so the
highest-numbered entries are the least frequently reported. The three SQL
problems are included and marked.

| # | LC | Problem |
| --- | --- | --- |
| 25 | 2235 | Add Two Integers |
| 42 | 2901 | Longest Unequal Adjacent Groups Subsequence II |
| 44 | 348 | Design Tic-Tac-Toe |
| 51 | 790 | Domino and Tromino Tiling |
| 53 | 410 | Split Array Largest Sum |
| 75 | 354 | Russian Doll Envelopes |
| 79 | 139 | Word Break |
| 82 | 658 | Find K Closest Elements |
| 84 | 1752 | Check if Array Is Sorted and Rotated |
| 93 | 67 | Add Binary |
| 98 | 16 | 3Sum Closest |
| 104 | 177 | Nth Highest Salary *(SQL)* |
| 107 | 44 | Wildcard Matching |
| 111 | 1307 | Verbal Arithmetic Puzzle |
| 116 | 1922 | Count Good Numbers |
| 117 | 180 | Consecutive Numbers *(SQL)* |
| 118 | 909 | Snakes and Ladders |
| 133 | 24 | Swap Nodes in Pairs |
| 139 | 523 | Continuous Subarray Sum |
| 142 | 3217 | Delete Nodes From Linked List Present in Array |
| 148 | 1760 | Minimum Limit of Balls in a Bag |
| 149 | 1929 | Concatenation of Array |
| 151 | 1141 | User Activity for the Past 30 Days I *(SQL)* |
| 153 | 729 | My Calendar I |
| 160 | 2089 | Find Target Indices After Sorting Array |
| 163 | 1974 | Minimum Time to Type Word Using Special Typewriter |
| 165 | 1160 | Find Words That Can Be Formed by Characters |
| 168 | 1863 | Sum of All Subset XOR Totals |
| 170 | 1123 | Lowest Common Ancestor of Deepest Leaves |
| 173 | 1043 | Partition Array for Maximum Sum |
| 174 | 846 | Hand of Straights |
| 176 | 3149 | Find the Minimum Cost Array Permutation |
| 177 | 1941 | Check if All Characters Have Equal Number of Occurrences |
| 178 | 2574 | Left and Right Sum Differences |
| 179 | 821 | Shortest Distance to a Character |
| 180 | 506 | Relative Ranks |
| 182 | 1310 | XOR Queries of a Subarray |
| 183 | 664 | Strange Printer |
| 184 | 2948 | Make Lexicographically Smallest Array by Swapping Elements |
| 185 | 2485 | Find the Pivot Integer |
| 186 | 1348 | Tweet Counts Per Frequency |
| 187 | 757 | Set Intersection Size At Least Two |
| 188 | 3026 | Maximum Good Subarray Sum |
| 189 | 2376 | Count Special Integers |
| 190 | 3211 | Generate Binary Strings Without Adjacent Zeros |
| 192 | 2501 | Longest Square Streak in an Array |
| 193 | 2039 | The Time When the Network Becomes Idle |
| 194 | 2530 | Maximal Score After Applying K Operations |
| 195 | 3168 | Minimum Number of Chairs in a Waiting Room |
| 196 | 1382 | Balance a Binary Search Tree |
| 198 | 951 | Flip Equivalent Binary Trees |
| 199 | 1220 | Count Vowels Permutation |
| 200 | 2093 | Minimum Cost to Reach City With Discounts |
| 201 | 2263 | Make Array Non-decreasing or Non-increasing |
| 202 | 2463 | Minimum Total Distance Traveled |
| 203 | 2959 | Number of Possible Sets of Closing Branches |
| 204 | 2975 | Maximum Square Area by Removing Fences From a Field |
| 205 | 2976 | Minimum Cost to Convert String I |
| 206 | 3014 | Minimum Number of Pushes to Type Word I |
| 207 | 3167 | Better Compression of String |

