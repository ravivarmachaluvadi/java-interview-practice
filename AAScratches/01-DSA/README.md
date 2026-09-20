# DSA practice

Every solved problem, grouped by technique. Inside a folder the filename order **is** the practice order: `A` building blocks, `B` easy, `C` medium, `D` hard, numbered by dependency. Open any file: the header tells you the problem, the approach, the one insight to remember, and the follow-ups; `main()` runs the cases.

## Notes

- [DSA memory keypoints](notes/DSA_Memory_Keypoints.md)
- [DSA memory keypoints II](notes/DSA_Memory_Keypoints_II.md)
- [Classic 150 roadmap](roadmaps/Classic_150_Roadmap.md)
- [Atlassian question list](roadmaps/Atlassian_Question_List.md)
- [LeetCode 500 links](roadmaps/LeetCode_500_Links.md)

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Arrays](01-Arrays/) | 37 | 11 | Prefix sums, Kadane, cyclic sort, in-place tricks, rotations. |
| [02-Two-Pointers-Sliding-Window](02-Two-Pointers-Sliding-Window/) | 29 | 10 | Fixed and variable windows, opposite-end pointers, longest or shortest subarray and substring. |
| [03-Binary-Search](03-Binary-Search/) | 21 | 6 | Classic, rotated arrays, lower and upper bound, binary search on the answer space. |
| [04-Strings](04-Strings/) | 38 | 10 | Parsing, encoding, palindromes, formatting, roman numerals, word games. |
| [05-Hashing-Prefix-Sum](05-Hashing-Prefix-Sum/) | 24 | 6 | HashMap and HashSet counting, prefix sum plus map, grouping and set operations. |
| [06-Linked-List](06-Linked-List/) | 25 | 8 | Reversal, cycle detection, merge, reorder, doubly linked lists. |
| [07-Stack-Queue-Monotonic](07-Stack-Queue-Monotonic/) | 26 | 9 | Expression evaluation, parentheses, monotonic stack and deque patterns. |
| [08-Heap-Priority-Queue](08-Heap-Priority-Queue/) | 12 | 4 | Top-K, median of a stream, scheduling with a min or max heap. |
| [09-Trees-BST](09-Trees-BST/) | 52 | 15 | Traversals, LCA variants, BST operations, construction from traversals, path sums. |
| [10-Trie](10-Trie/) | 5 | 2 | Prefix trees for word search, suggestions and distinct substrings. |
| [11-Graphs](11-Graphs/) | 47 | 12 | BFS, DFS, topological sort, union-find, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, SCC. |
| [12-Dynamic-Programming](12-Dynamic-Programming/) | 36 | 11 | 1-D and 2-D DP, knapsack family, LCS and LIS, partition and matrix-chain problems. |
| [13-Greedy](13-Greedy/) | 22 | 6 | Jump games, stock trading, scheduling, local-choice proofs. |
| [14-Backtracking-Recursion](14-Backtracking-Recursion/) | 18 | 7 | Subsets, permutations, combination sum, N-Queens, word search, basic recursion. |
| [15-Intervals](15-Intervals/) | 12 | 3 | Merge, insert, meeting rooms, sweep line and difference arrays. |
| [16-Matrix](16-Matrix/) | 9 | 4 | Rotation, spiral and diagonal traversal, grid simulation. |
| [17-Math-Bit-Manipulation](17-Math-Bit-Manipulation/) | 32 | 13 | Number theory, primes, GCD and LCM, fast exponentiation, bit tricks. |
| [18-Sorting-Searching-Algorithms](18-Sorting-Searching-Algorithms/) | 14 | 5 | Sorting algorithm implementations, quick select, KMP, Rabin-Karp, segment tree. |
| [19-Design-Data-Structures](19-Design-Data-Structures/) | 18 | 6 | LeetCode design problems: LRU and LFU cache, min stack, hit counter, time-based KV store. |
| [20-Scenario-Based-Problems](20-Scenario-Based-Problems/) | 13 | 4 | Real-world style questions from Karat, Atlassian and onsite rounds: logs, votes, ratings, distances. |
| **Total** | **490** | **152** | |

## 01-Arrays

Prefix sums, Kadane, cyclic sort, in-place tricks, rotations.

**Do these first:** [B03_BestTimeToBuyAndSellStock.java](01-Arrays/B03_BestTimeToBuyAndSellStock.java), [B09_MajorityElement.java](01-Arrays/B09_MajorityElement.java), [B11_FindCorruptPair.java](01-Arrays/B11_FindCorruptPair.java), [C01_KadaneSAlgorithm.java](01-Arrays/C01_KadaneSAlgorithm.java), [C05_ProductExceptSelf.java](01-Arrays/C05_ProductExceptSelf.java), [C08_Sort012.java](01-Arrays/C08_Sort012.java), [C09_RotateArray.java](01-Arrays/C09_RotateArray.java), [C10_NextGreaterPermutation.java](01-Arrays/C10_NextGreaterPermutation.java), [C11_SmallestSubarraySum.java](01-Arrays/C11_SmallestSubarraySum.java), [C15_FindDuplicate.java](01-Arrays/C15_FindDuplicate.java), [D01_FirstMissingPositive.java](01-Arrays/D01_FirstMissingPositive.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_ArraySortedOrNot.java](01-Arrays/A01_ArraySortedOrNot.java) | Array Sorted Or Not | GfG / Easy | "Sorted" is a property of every adjacent pair, so one pass over the pairs settles it and you can bail out on the first bad pair. |
| [A02_MonotonicArray.java](01-Arrays/A02_MonotonicArray.java) | Monotonic Array | LeetCode 896 / Easy | Instead of scanning once for "is it increasing?" and again for "is it decreasing?", carry both hypotheses through a single scan and let the data falsify them. |
| [A03_MaxConsecutiveOnes.java](01-Arrays/A03_MaxConsecutiveOnes.java) | Max Consecutive Ones | LeetCode 485 / Easy | Update the best answer after every step, not only when a run ends. |
| [A04_SecondSmallest.java](01-Arrays/A04_SecondSmallest.java) | Second Smallest Element | GfG / Easy | Two rules make this work. |
| [A05_ThreeLargestNumbers.java](01-Arrays/A05_ThreeLargestNumbers.java) | Three Largest Numbers | AlgoExpert / Easy | Insert num into a tiny sorted list of size 3, shifting the displaced values down. |
| [A06_LeftRotateArrayByOne.java](01-Arrays/A06_LeftRotateArrayByOne.java) | Left Rotate Array By One | Warm-up / Easy | Shifting left overwrites each slot with the value to its right, so the only value ever lost is the first one; a single temp is enough to save it. |
| [A07_PrintArrayInCyclic.java](01-Arrays/A07_PrintArrayInCyclic.java) | Print Array In Cyclic Order | Warm-up / Easy | index % n is the "wrap" operator: it makes a flat array behave like a ring. |
| [A08_FirstMiddleLast.java](01-Arrays/A08_FirstMiddleLast.java) | First, Middle and Last Digit | Warm-up / Easy | n / 10^k discards the k lowest digits and n % 10 reads the lowest one, so "digit k from the right" is always (n / 10^k) % 10. |
| [A09_CountFrequencies.java](01-Arrays/A09_CountFrequencies.java) | Count Frequencies In Place | Warm-up / Medium | When values are bounded by the array length, the array can be its own hash map (index = value), and one slot can hold two numbers at once because (original + k * n) % n == original and (original + k * n) / n == k. |
| [A10_CycleLengthInArray.java](01-Arrays/A10_CycleLengthInArray.java) | Cycle Length in an Array (Functional Graph) | Floyd building block / Medium | The Phase 1 meeting point lies on the cycle, but the number of steps taken to get there is NOT the cycle length: it is a multiple of it that also depends on the tail ([1, 2, 3, 4, 3] meets after 4 steps although the cycl |
| [B01_PlusOne.java](01-Arrays/B01_PlusOne.java) | Plus One | LeetCode 66 / Easy | Adding one can only ripple through a run of trailing 9s, so the scan stops at the first non-9 digit; the early return makes the common case O(1). |
| [B02_KidsWithCandies.java](01-Arrays/B02_KidsWithCandies.java) | Kids With the Greatest Number of Candies | LeetCode 1431 / Easy | The question for each index depends only on one global aggregate (the max). |
| [B03_BestTimeToBuyAndSellStock.java](01-Arrays/B03_BestTimeToBuyAndSellStock.java) * | Best Time to Buy and Sell Stock | LeetCode 121 / Easy | At each day the only past information that matters is the cheapest price so far. |
| [B04_PivotIndex.java](01-Arrays/B04_PivotIndex.java) | Find Pivot Index | LeetCode 724 / Easy | You never need a suffix-sum array. |
| [B05_LeadersInAnArray.java](01-Arrays/B05_LeadersInAnArray.java) | Leaders in an Array | GeeksforGeeks / Easy | "Greater than everything to my right" only needs ONE number: the max of the suffix. |
| [B06_CheckIfAnArrayIsConsecutive.java](01-Arrays/B06_CheckIfAnArrayIsConsecutive.java) | Check If An Array Is Consecutive | GeeksforGeeks / Easy | n distinct integers whose span is exactly n MUST be every integer in [min, max]; nothing else fits (pigeonhole). |
| [B07_ArrayTransformation.java](01-Arrays/B07_ArrayTransformation.java) | Array Transformation | LeetCode 1243 / Easy | Read from a snapshot, write to a copy. |
| [B08_MissingRanges.java](01-Arrays/B08_MissingRanges.java) | Missing Ranges | LeetCode 163 / Easy | There is no clever idea here; the whole problem is edge-case discipline. |
| [B09_MajorityElement.java](01-Arrays/B09_MajorityElement.java) * | Majority Element | LeetCode 169 / Easy | Every mismatch cancels one occurrence of the candidate against one non-candidate. |
| [B10_FindAllMissedNumbers.java](01-Arrays/B10_FindAllMissedNumbers.java) | Find All Numbers Disappeared in an Array | LeetCode 448 / Easy | The array is its own bitmap: the sign of slot v - 1 is a free boolean "v seen", and the magnitude still carries the original value so later reads are never confused. |
| [B11_FindCorruptPair.java](01-Arrays/B11_FindCorruptPair.java) * | Find the Corrupt Pair (Set Mismatch) | LeetCode 645 / Easy | After cyclic sort, a SINGLE wrong index tells you both answers: what is sitting there (the extra copy) and what should be (the absent value). |
| [C01_KadaneSAlgorithm.java](01-Arrays/C01_KadaneSAlgorithm.java) * | Maximum Subarray (Kadane) | LeetCode 53 / Medium | The only decision at each index is: extend the previous best subarray, or start a new one here. |
| [C02_NumberOfZeroFilledSubarrays.java](01-Arrays/C02_NumberOfZeroFilledSubarrays.java) | Number of Zero-Filled Subarrays | LeetCode 2348 / Medium | Do not enumerate subarrays (O(n^2)). |
| [C03_IncreasingTripletSubsequence.java](01-Arrays/C03_IncreasingTripletSubsequence.java) | Increasing Triplet Subsequence | LeetCode 334 / Medium | Correctness rests on 'second' alone. |
| [C04_PartitionArrayintoDisjointIntervals.java](01-Arrays/C04_PartitionArrayintoDisjointIntervals.java) | Partition Array into Disjoint Intervals | LeetCode 915 / Medium | The answer only moves when the invariant "all of right >= leftMax" breaks, and when it breaks at i the new left part is the whole prefix [0..i], whose max we were already carrying as runningMax. |
| [C05_ProductExceptSelf.java](01-Arrays/C05_ProductExceptSelf.java) * | Product of Array Except Self | LeetCode 238 / Medium | "Everything except i" = "everything left of i" x "everything right of i". |
| [C06_BestSeat.java](01-Arrays/C06_BestSeat.java) | Best Seat (Maximize Distance to Closest Person) | LeetCode 849 / Medium | "Nearest on either side" is two one-directional questions, each answerable by a running "last seen" index in one pass. |
| [C07_AlternatePositiveNegative.java](01-Arrays/C07_AlternatePositiveNegative.java) | Rearrange Array Elements by Sign | LeetCode 2149 / Medium | Two independent write cursors let one read pass split the input into two interleaved streams with no swapping at all. |
| [C08_Sort012.java](01-Arrays/C08_Sort012.java) * | Sort Colors (Sort 0s, 1s, 2s) | LeetCode 75 / Medium | mid does NOT advance after swapping in a 2, because the value that came from nums[high] has never been examined. |
| [C09_RotateArray.java](01-Arrays/C09_RotateArray.java) * | Rotate Array | LeetCode 189 / Medium | A right rotation by k moves the last k elements to the front, in their original order. |
| [C10_NextGreaterPermutation.java](01-Arrays/C10_NextGreaterPermutation.java) * | Next Permutation | LeetCode 31 / Medium | The next permutation changes the sequence as far to the right as possible: keep the longest descending suffix, bump the element just before it by the smallest possible amount, then make the suffix as small as possible. |
| [C11_SmallestSubarraySum.java](01-Arrays/C11_SmallestSubarraySum.java) * | Minimum Size Subarray Sum | LeetCode 209 / Medium | Because every element is positive, growing the window only increases the sum and shrinking only decreases it. |
| [C12_LongestBitonicSubarrayProblem.java](01-Arrays/C12_LongestBitonicSubarrayProblem.java) | Longest Bitonic Subarray | GFG / Techie Delight (not LC) / Medium | The state being tracked is a SHAPE (up, then down), not a value. |
| [C13_LongestMountainInArray.java](01-Arrays/C13_LongestMountainInArray.java) | Longest Mountain in Array | LeetCode 845 / Medium | Anchor on the peak, not on the left edge. |
| [C14_FindAllDuplicatesInAnArray.java](01-Arrays/C14_FindAllDuplicatesInAnArray.java) | Find All Duplicates in an Array | LeetCode 442 / Medium | The guard is "nums[home] == nums[i]", not "home == i". |
| [C15_FindDuplicate.java](01-Arrays/C15_FindDuplicate.java) * | Find the Duplicate Number | LeetCode 287 / Medium | Values in [1, n] are valid indices into an array of length n + 1, so the array IS a functional graph (each node has one out-edge). |
| [D01_FirstMissingPositive.java](01-Arrays/D01_FirstMissingPositive.java) * | First Missing Positive | LeetCode 41 / Hard | The array can be used as its own hash set: value v belongs at index v - 1. |

**Worth adding next:**

- Maximum Sum Circular Subarray (LC 918): Kadane run twice: normal max subarray, plus total sum minus the minimum subarray for the wrap-around case, with the all-negative array handled as an explicit special case
- Count Inversions in an Array (and its harder twin, Reverse Pairs, LC 493): Modified merge sort: count cross-pairs during the merge step, O(n log n)
- Majority Element II — all elements appearing more than n/3 times (LC 229): Generalised Boyer-Moore with two candidates and two counters, plus a mandatory second verification pass

## 02-Two-Pointers-Sliding-Window

Fixed and variable windows, opposite-end pointers, longest or shortest subarray and substring.

**Do these first:** [A02_MoveZeroes.java](02-Two-Pointers-Sliding-Window/A02_MoveZeroes.java), [A07_LongestUniformSubstring.java](02-Two-Pointers-Sliding-Window/A07_LongestUniformSubstring.java), [C01_ContainerWithMostWater.java](02-Two-Pointers-Sliding-Window/C01_ContainerWithMostWater.java), [C02_Three3Sum.java](02-Two-Pointers-Sliding-Window/C02_Three3Sum.java), [C03_LongestSubStringWithoutRepeatingCharacter.java](02-Two-Pointers-Sliding-Window/C03_LongestSubStringWithoutRepeatingCharacter.java), [C04_MaxConsecutiveOnesIII.java](02-Two-Pointers-Sliding-Window/C04_MaxConsecutiveOnesIII.java), [C07_CharacterReplacement.java](02-Two-Pointers-Sliding-Window/C07_CharacterReplacement.java), [C08_SubarrayProductLessThanK.java](02-Two-Pointers-Sliding-Window/C08_SubarrayProductLessThanK.java), [D01_TrappingRainWater.java](02-Two-Pointers-Sliding-Window/D01_TrappingRainWater.java), [D02_MinimumWindowSubstring.java](02-Two-Pointers-Sliding-Window/D02_MinimumWindowSubstring.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_RemoveElement.java](02-Two-Pointers-Sliding-Window/A01_RemoveElement.java) | Remove Element | LeetCode 27 / Easy | writeIndex <= readIndex always, so writing never clobbers something not yet read. |
| [A02_MoveZeroes.java](02-Two-Pointers-Sliding-Window/A02_MoveZeroes.java) * | Move Zeroes | LeetCode 283 / Easy | Same write-pointer primitive as Remove Element, plus one extra step: because the problem wants the removed values to still exist at the end, fill the dead tail with them. |
| [A03_RemoveDuplicates.java](02-Two-Pointers-Sliding-Window/A03_RemoveDuplicates.java) | Remove Duplicates from Sorted Array | LeetCode 26 / Easy | The write pointer compares against the LAST KEPT element instead of a fixed constant (as in Remove Element). |
| [A04_ReverseVowels.java](02-Two-Pointers-Sliding-Window/A04_ReverseVowels.java) | Reverse Vowels of a String | LeetCode 345 / Easy | This is the converging-pointer primitive: two pointers walk toward each other and act only when both satisfy a condition. |
| [A05_SortArrayByParity.java](02-Two-Pointers-Sliding-Window/A05_SortArrayByParity.java) | Sort Array By Parity | LeetCode 905 / Easy | This is the converging pair from Reverse Vowels turned into a PARTITION: instead of skipping non-matching items, each pointer stops at the first item on the wrong side and the two wrong items are swapped. |
| [A06_UnionOfArrays.java](02-Two-Pointers-Sliding-Window/A06_UnionOfArrays.java) | Union of Two Sorted Arrays | GeeksforGeeks / Easy | Because both inputs are sorted, the merged stream is sorted too, so a duplicate can only ever be equal to the LAST value appended. |
| [A07_LongestUniformSubstring.java](02-Two-Pointers-Sliding-Window/A07_LongestUniformSubstring.java) * | Longest Uniform Substring | Classic / Easy | Keep two counters: "current stretch" and "best stretch seen". |
| [A08_MaximumAverageSubarray.java](02-Two-Pointers-Sliding-Window/A08_MaximumAverageSubarray.java) | Maximum Average Subarray I | LeetCode 643 / Easy | A window of fixed width never needs to be re-summed: each slide changes exactly two elements (one enters, one leaves), so the sum updates in O(1). |
| [B01_SquaresOfASortedArray.java](02-Two-Pointers-Sliding-Window/B01_SquaresOfASortedArray.java) | Squares of a Sorted Array | LeetCode 977 / Easy | When you know where the LARGEST elements are (the two ends) but not where the smallest is, fill the answer backwards from the biggest slot. |
| [B02_IsSubsequence.java](02-Two-Pointers-Sliding-Window/B02_IsSubsequence.java) | Is Subsequence | LeetCode 392 / Easy | Greedy matching is safe: taking the EARLIEST possible occurrence of each s character leaves the most of t available for the rest. |
| [B03_ValidWordAbbreviation.java](02-Two-Pointers-Sliding-Window/B03_ValidWordAbbreviation.java) | Valid Word Abbreviation | LeetCode 408 / Easy | It is the same two-pointer walk as "Is Subsequence", plus one twist: a digit run is parsed in-loop and acts as a jump instruction for the other pointer. |
| [B04_IntersectionOfThreeSortedArrays.java](02-Two-Pointers-Sliding-Window/B04_IntersectionOfThreeSortedArrays.java) | Intersection of Three Sorted Arrays | LeetCode 1213 / Easy | This is the two-array merge walk with one more pointer. |
| [B05_CountPairsWhoseSumIsLessThanTarget.java](02-Two-Pointers-Sliding-Window/B05_CountPairsWhoseSumIsLessThanTarget.java) | Count Pairs Whose Sum is Less than Target | LeetCode 2824 / Easy | One comparison settles a whole block of pairs. |
| [B06_SweetAndSavory.java](02-Two-Pointers-Sliding-Window/B06_SweetAndSavory.java) | Sweet and Savory (closest pair sum) | AlgoExpert variant / Easy | "Closest to target" is pair search plus one running best. |
| [C01_ContainerWithMostWater.java](02-Two-Pointers-Sliding-Window/C01_ContainerWithMostWater.java) * | Container With Most Water | LeetCode 11 / Medium | Moving inward always loses width, so the only hope of a bigger area is a taller limiting wall. |
| [C02_Three3Sum.java](02-Two-Pointers-Sliding-Window/C02_Three3Sum.java) * | 3Sum | LeetCode 15 / Medium | Sorting turns "find a pair with a given sum" into a two-pointer walk, and it also makes duplicates adjacent so they can be skipped in O(1) per step. |
| [C03_LongestSubStringWithoutRepeatingCharacter.java](02-Two-Pointers-Sliding-Window/C03_LongestSubStringWithoutRepeatingCharacter.java) * | Longest Substring Without Repeating Characters | LeetCode 3 / Medium | Instead of shrinking the window one character at a time until the duplicate leaves (the Set version, also shown below), remember WHERE each character was last seen and jump left there in one hop. |
| [C04_MaxConsecutiveOnesIII.java](02-Two-Pointers-Sliding-Window/C04_MaxConsecutiveOnesIII.java) * | Max Consecutive Ones III | LeetCode 1004 / Medium | Reframe "flip up to k zeros" as "a window may contain at most k zeros". |
| [C05_LongestSubarrayAfterDeletingOne.java](02-Two-Pointers-Sliding-Window/C05_LongestSubarrayAfterDeletingOne.java) | Longest Subarray of 1's After Deleting One Element | LeetCode 1493 / Medium | This is the k = 1 case of "longest window with at most k zeros", with an off-by-one trap: the deletion is mandatory, so the answer is always the window length minus one, never the raw length. |
| [C06_FruitIntoBaskets.java](02-Two-Pointers-Sliding-Window/C06_FruitIntoBaskets.java) | Fruit Into Baskets | LeetCode 904 / Medium | This is the "at most K violations" window where the thing being counted is DISTINCTNESS, so a single counter is not enough: you need a frequency map whose size() tells you how many distinct types are present. |
| [C07_CharacterReplacement.java](02-Two-Pointers-Sliding-Window/C07_CharacterReplacement.java) * | Longest Repeating Character Replacement | LeetCode 424 / Medium | maxFreq is deliberately never decreased when left moves. |
| [C08_SubarrayProductLessThanK.java](02-Two-Pointers-Sliding-Window/C08_SubarrayProductLessThanK.java) * | Subarray Product Less Than K | LeetCode 713 / Medium | The window measures a COUNT, not a length. |
| [C09_MaximumPointsFromCards.java](02-Two-Pointers-Sliding-Window/C09_MaximumPointsFromCards.java) | Maximum Points You Can Obtain from Cards | LeetCode 1423 / Medium | "Take from both ends" is not a choice problem, it is a window problem. |
| [C10_LongestSubstringWithKRepeatingChars.java](02-Two-Pointers-Sliding-Window/C10_LongestSubstringWithKRepeatingChars.java) | Longest Substring with At Least K Repeating Characters | LeetCode 395 / Medium | The condition "every char appears >= k times" is NOT monotonic: growing a window can make it valid, invalid, then valid again (add one 'c' to "aabb" and it breaks; add a second 'c' and it heals). |
| [C11_MaximumFrequencyOfAnElementAfterPerformingOperationsI.java](02-Two-Pointers-Sliding-Window/C11_MaximumFrequencyOfAnElementAfterPerformingOperationsI.java) | Maximum Frequency of an Element After Performing Operations I | LeetCode 3346 / Medium | The window view ("which sorted elements fit within 2k of each other") works, but the cleaner mental model is an interval sweep: every element votes for a range of targets and the answer is the best-voted target, adjusted |
| [D01_TrappingRainWater.java](02-Two-Pointers-Sliding-Window/D01_TrappingRainWater.java) * | Trapping Rain Water | LeetCode 42 / Hard | You normally need BOTH the left max and the right max for a bar, which is why the two-array prefix/suffix solution exists. |
| [D02_MinimumWindowSubstring.java](02-Two-Pointers-Sliding-Window/D02_MinimumWindowSubstring.java) * | Minimum Window Substring | LeetCode 76 / Hard | Never compare the two maps to test validity; that costs O(alphabet) per move. |
| [D03_MergeTwoSortedArrays.java](02-Two-Pointers-Sliding-Window/D03_MergeTwoSortedArrays.java) | Merge Two Sorted Arrays In Place (O(1) space) | LeetCode 88 variant / Hard | The naive O(1)-space merge (insertion into the right place) is O(m * n). |
| [D04_MaxProfitWithWindowSize3.java](02-Two-Pointers-Sliding-Window/D04_MaxProfitWithWindowSize3.java) | Max Profit, Buy Within Previous K Days | Custom (Sliding Window Minimum) / Hard | A plain window cannot answer "minimum of the last k values" in O(1) after the minimum leaves the window; you would rescan. |

**Worth adding next:**

- Find All Anagrams in a String (LC 438) / Permutation in String (LC 567): Fixed-size window over a string with a 26-slot frequency array and a `matches` counter updated in O(1) per slide — add right char, remove left char, compare counts incrementally rather than re-scanning the map each step.
- Valid Palindrome II (LC 680): Converging pointers with one allowed mismatch: on the first mismatch, branch into a helper that checks isPalindrome(left+1, right) OR isPalindrome(left, right-1). The 'at most one deletion' idea solved by branching rather than by a window counter.
- Subarrays with K Different Integers (LC 992), with Count Number of Nice Subarrays (LC 1248) as the drill: exactly(K) = atMost(K) - atMost(K-1): run the same grow-while-valid window twice and subtract, because 'exactly K' is not directly monotonic and so has no single valid window.

## 03-Binary-Search

Classic, rotated arrays, lower and upper bound, binary search on the answer space.

**Do these first:** [A01_LowerAndUpperBounds.java](03-Binary-Search/A01_LowerAndUpperBounds.java), [C03_FindPeakElement.java](03-Binary-Search/C03_FindPeakElement.java), [C04_MinIndexInRotatedSortedArray.java](03-Binary-Search/C04_MinIndexInRotatedSortedArray.java), [C06_SearchInRotatedSortedArray.java](03-Binary-Search/C06_SearchInRotatedSortedArray.java), [C11_KokoEatingBananas.java](03-Binary-Search/C11_KokoEatingBananas.java), [D02_MedianOfTwoSortedArrays.java](03-Binary-Search/D02_MedianOfTwoSortedArrays.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_LowerAndUpperBounds.java](03-Binary-Search/A01_LowerAndUpperBounds.java) * | Lower Bound and Upper Bound | (building block / Easy) | The predicate "arr[i] >= target" is false...false, true...true over a sorted array. |
| [A02_FirstAndLastOccurrence.java](03-Binary-Search/A02_FirstAndLastOccurrence.java) | Find First and Last Position of Element in Sorted Array | LeetCode 34 / Medium | This is the lower/upper bound template (A01_LowerAndUpperBounds) called twice. |
| [A03_FloorAndCeilInSortedArray.java](03-Binary-Search/A03_FloorAndCeilInSortedArray.java) | Floor and Ceil in a Sorted Array | (GfG classic / Easy) | Same lower/upper bound template as A01_LowerAndUpperBounds, but the answer stored is the element's value rather than its index. |
| [A04_SquareRoot.java](03-Binary-Search/A04_SquareRoot.java) | Sqrt(x) | LeetCode 69 / Easy | There is no array. |
| [B01_KthMissingPositive.java](03-Binary-Search/B01_KthMissingPositive.java) | Kth Missing Positive Number | LeetCode 1539 / Easy | You do not binary search the array values, you binary search arr[i] - (i + 1), a monotone quantity you derive from the index. |
| [C01_Search2DMatrix.java](03-Binary-Search/C01_Search2DMatrix.java) | Search a 2D Matrix | LeetCode 74 / Medium | A row-major matrix with ordered row boundaries IS a sorted array with a different addressing scheme. |
| [C02_RandomPickWithWeight.java](03-Binary-Search/C02_RandomPickWithWeight.java) | Random Pick with Weight | LeetCode 528 / Medium | Turn weights into intervals on a number line. |
| [C03_FindPeakElement.java](03-Binary-Search/C03_FindPeakElement.java) * | Find Peak Element | LeetCode 162 / Medium | Binary search does not need a sorted array, only a decision rule that is guaranteed to keep a valid answer inside the half you keep. |
| [C04_MinIndexInRotatedSortedArray.java](03-Binary-Search/C04_MinIndexInRotatedSortedArray.java) * | Min Index in Rotated Sorted Array | LeetCode 153 / Medium | In a rotated array exactly one half of any window is sorted. |
| [C05_SecondSmallestInRotatedArray.java](03-Binary-Search/C05_SecondSmallestInRotatedArray.java) | Second Smallest in Rotated Sorted Array | LeetCode 153 variant / Medium | A rotated sorted array is still sorted if you read it cyclically starting at the pivot. |
| [C06_SearchInRotatedSortedArray.java](03-Binary-Search/C06_SearchInRotatedSortedArray.java) * | Search in Rotated Sorted Array | LeetCode 33 / Medium | You cannot decide a direction by comparing target with arr[mid] alone, because the array is not globally sorted. |
| [C07_SearchInARotatedSortedArrayII.java](03-Binary-Search/C07_SearchInARotatedSortedArrayII.java) | Search in Rotated Sorted Array II | LeetCode 81 / Medium | LC 33 relies on "arr[low] <= arr[mid] implies left half sorted". |
| [C08_SingleNonDuplicateElement.java](03-Binary-Search/C08_SingleNonDuplicateElement.java) | Single Element in a Sorted Array | LeetCode 540 / Medium | The values are not what is monotone here, the pairing alignment is. |
| [C09_TopVotedCandidate.java](03-Binary-Search/C09_TopVotedCandidate.java) | Online Election (Top Voted Candidate) | LeetCode 911 / Medium | The leader at time t depends only on the prefix of votes up to t, and the prefix is fixed once the arrays are given. |
| [C10_CuttingRibbons.java](03-Binary-Search/C10_CuttingRibbons.java) | Cutting Ribbons | LeetCode 1891 / Medium | The feasibility predicate is monotone in x (true, true, ..., false, false), so binary search finds the boundary. |
| [C11_KokoEatingBananas.java](03-Binary-Search/C11_KokoEatingBananas.java) * | Koko Eating Bananas | LeetCode 875 / Medium | When the question is "the minimum X such that something is possible", do not search the input; search the range of possible answers with a yes/no feasibility check. |
| [C12_CapacityToShipPackagesWithinDDays.java](03-Binary-Search/C12_CapacityToShipPackagesWithinDDays.java) | Capacity To Ship Packages Within D Days | LeetCode 1011 / Medium | Same template as Koko (minimum feasible answer), with two twists: the lower bound is max(weights) rather than 1, and the predicate is a greedy simulation instead of a formula. |
| [C13_MagneticForceBetweenTwoBalls.java](03-Binary-Search/C13_MagneticForceBetweenTwoBalls.java) | Magnetic Force Between Two Balls | LeetCode 1552 / Medium | "Maximize the minimum" is the mirror of "minimize the maximum". |
| [D01_KthElementOf2SortedArrays.java](03-Binary-Search/D01_KthElementOf2SortedArrays.java) | K-th Element of Two Sorted Arrays | GFG classic / Hard | Binary search on how many elements the smaller array contributes to the first k. |
| [D02_MedianOfTwoSortedArrays.java](03-Binary-Search/D02_MedianOfTwoSortedArrays.java) * | Median of Two Sorted Arrays | LeetCode 4 / Hard | The median is the boundary of a partition that splits the combined data into two equal halves with every left value <= every right value. |
| [D03_MinimumCostToMakeArrayEqual.java](03-Binary-Search/D03_MinimumCostToMakeArrayEqual.java) | Minimum Cost to Make Array Equal | LeetCode 2448 / Hard | Not every binary search needs a yes/no predicate. |

**Worth adding next:**

- Kth Smallest Element in a Sorted Matrix (LC 378) — and its twin, Median of a Row-wise Sorted Matrix: Binary search over the VALUE range (lo = matrix[0][0], hi = matrix[n-1][n-1]) with a counting predicate countLessOrEqual(x), not a boolean feasibility predicate; the count is itself computed in O(n) by a staircase walk.
- Longest Increasing Subsequence in O(n log n) (LC 300 follow-up / patience sorting): Maintain a 'tails' array and use lower_bound to find the first tail >= nums[i] and overwrite it; binary search used as a subroutine inside a DP rather than as the whole algorithm.

## 04-Strings

Parsing, encoding, palindromes, formatting, roman numerals, word games.

**Do these first:** [A04_AnagramStrings.java](04-Strings/A04_AnagramStrings.java), [A06_PalindromeSpecial.java](04-Strings/A06_PalindromeSpecial.java), [A07_RunLengthEncodingW4A3.java](04-Strings/A07_RunLengthEncodingW4A3.java), [A08_AddStrings.java](04-Strings/A08_AddStrings.java), [B05_FirstNonRepeatingChar.java](04-Strings/B05_FirstNonRepeatingChar.java), [B13_LongestCommonPrefix.java](04-Strings/B13_LongestCommonPrefix.java), [B14_RomanToInteger.java](04-Strings/B14_RomanToInteger.java), [C01_ReverseWordsInString.java](04-Strings/C01_ReverseWordsInString.java), [C02_ATOI.java](04-Strings/C02_ATOI.java), [C03_StringCompression.java](04-Strings/C03_StringCompression.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_DelimiterSplit.java](04-Strings/A01_DelimiterSplit.java) | Split a String on a Delimiter | Building block / Easy | split() is regex-based and trims trailing empties. |
| [A02_FirstCharToUpperCase.java](04-Strings/A02_FirstCharToUpperCase.java) | Capitalize the First Letter of Each Word | Building block / Easy | Most per-word transforms are exactly this shape: split -> transform each token -> join. |
| [A03_ReverseWords.java](04-Strings/A03_ReverseWords.java) | Reverse Each Word, Keep Word Order | LeetCode 557 / Easy | "Reverse" is the third primitive after split and join. |
| [A04_AnagramStrings.java](04-Strings/A04_AnagramStrings.java) * | Valid Anagram | LeetCode 242 / Easy | Increment for one side, decrement for the other, assert all zero. |
| [A05_CharactersNotInString.java](04-Strings/A05_CharactersNotInString.java) | Letters Missing From a String (Pangram Check) | Building block / Easy | The same int[26] as the anagram check, but READ the other way: a count of zero is itself the answer. |
| [A06_PalindromeSpecial.java](04-Strings/A06_PalindromeSpecial.java) * | Valid Palindrome (letters only) | LeetCode 125 variant / Easy | Converging pointers do the palindrome check in one pass with O(1) extra space; the only subtlety is the two inner "skip" loops, which must be bounded by left < right or they walk off the end on inputs like "!!!". |
| [A07_RunLengthEncodingW4A3.java](04-Strings/A07_RunLengthEncodingW4A3.java) * | Run-Length Encoding | classic / Easy | The inner while loop consumes a whole run and leaves i on its LAST character, so the outer loop's own i++ lands exactly on the next run's first character. |
| [A08_AddStrings.java](04-Strings/A08_AddStrings.java) * | Add Strings | LeetCode 415 / Easy | Treat a missing digit as 0 and keep looping while ANY of (digits left in a, digits left in b, carry) is non-zero. |
| [B01_CapitalizeFirstAndLastCharacterOfEachWord.java](04-Strings/B01_CapitalizeFirstAndLastCharacterOfEachWord.java) | Capitalize First and Last Character of Each Word | classic / Easy | Strings are immutable, so per-word edits need a StringBuilder; setCharAt is the in-place mutation that avoids rebuilding the word with substring concatenation. |
| [B02_ToCamelCase.java](04-Strings/B02_ToCamelCase.java) | Sentence to camelCase | classic / Easy | It is the same split-transform-join pipeline as the other word problems, except the join separator is "" and the FIRST word is treated differently from the rest. |
| [B03_GoatLatin.java](04-Strings/B03_GoatLatin.java) | Goat Latin | LeetCode 824 / Easy | The only state that crosses the loop boundary is the word index. |
| [B04_MostFrequentVowelAndConsonant.java](04-Strings/B04_MostFrequentVowelAndConsonant.java) | Find Most Frequent Vowel and Consonant | LeetCode 3541 / Easy | Counting and classifying are two separate passes: the frequency array is the reusable primitive, the vowel predicate is just a partition layered on top. |
| [B05_FirstNonRepeatingChar.java](04-Strings/B05_FirstNonRepeatingChar.java) * | First Unique Character in a String | LeetCode 387 / Easy | Two passes: count first, then re-scan in original order. |
| [B06_SortStringDescending.java](04-Strings/B06_SortStringDescending.java) | Sort String in Descending Order | Practice / Easy | A sorted string is the canonical form of an anagram class, so "sort the chars" shows up inside group-anagrams and friends. |
| [B07_SemordnilapChecker.java](04-Strings/B07_SemordnilapChecker.java) | Semordnilap Checker | AlgoExpert-style / Easy | Pair-finding over a list: precompute the set once, then ask "is my partner present?" per element instead of comparing every pair (O(n^2)). |
| [B08_StrobogrammaticNumber.java](04-Strings/B08_StrobogrammaticNumber.java) | Strobogrammatic Number | LeetCode 246 / Easy | This is the palindrome two-pointer scan with equality replaced by a lookup: instead of s[left] == s[right], we need rotate(s[left]) == s[right]. |
| [B09_PasswordStrengthChecker.java](04-Strings/B09_PasswordStrengthChecker.java) | Password Strength Checker | GeeksforGeeks / Easy | Accumulate facts in one pass, decide at the end. |
| [B10_MinimumAlternatingBinaryString.java](04-Strings/B10_MinimumAlternatingBinaryString.java) | Minimum Changes To Make Alternating Binary String | LeetCode 1758 / Easy | When the answer must be one of a tiny, fixed set of candidates, score all candidates in the same pass rather than looping once per candidate. |
| [B11_CheckIfStringIsDecomposableIntoValueEqualSubstrings.java](04-Strings/B11_CheckIfStringIsDecomposableIntoValueEqualSubstrings.java) | Check if String Is Decomposable Into Value-Equal Substrings |  | Reduce a run's length to a remainder mod 3, then track a single boolean "already used the 2". |
| [B12_MergeStringsAlternately.java](04-Strings/B12_MergeStringsAlternately.java) | Merge Strings Alternately | LeetCode 1768 / Easy | This is the merge step of merge sort with "alternate" in place of "pick the smaller": a shared loop while both sides have input, then drain the tails. |
| [B13_LongestCommonPrefix.java](04-Strings/B13_LongestCommonPrefix.java) * | Longest Common Prefix | LeetCode 14 / Easy | The common prefix can only get shorter as you see more strings, never longer. |
| [B14_RomanToInteger.java](04-Strings/B14_RomanToInteger.java) * | Roman to Integer | LeetCode 13 / Easy | Scanning right to left turns "look ahead to decide" into "look back", which needs no bounds check: a symbol is subtracted exactly when it is smaller than the one to its right. |
| [B15_RotateString.java](04-Strings/B15_RotateString.java) | Rotate String | LeetCode 796 / Easy | Doubling the string turns "is a rotation of" into "is a substring of". |
| [B16_GreatestCommonDivisorOfStrings.java](04-Strings/B16_GreatestCommonDivisorOfStrings.java) | Greatest Common Divisor of Strings | LeetCode 1071 / Easy | Two strings share a repeating unit if and only if str1+str2 equals str2+str1. |
| [B17_LongestWordFromLetters.java](04-Strings/B17_LongestWordFromLetters.java) | Longest Word From Letters | Custom / Easy | Frequency counting works as a feasibility test, not just a comparison: "can A be built from B" is "for every char, count_A <= count_B". |
| [C01_ReverseWordsInString.java](04-Strings/C01_ReverseWordsInString.java) * | Reverse Words in a String | LeetCode 151 / Medium | "Reverse the whole thing, then reverse each piece" is the pattern: reversing twice at two different granularities moves the pieces without scrambling the letters. |
| [C02_ATOI.java](04-Strings/C02_ATOI.java) * | String to Integer (atoi) | LeetCode 8 / Medium | You cannot detect int overflow after it happens (the value silently wraps), so rearrange the inequality result * 10 + d > MAX into result > (MAX - d) / 10, which is safe to evaluate in int. |
| [C03_StringCompression.java](04-Strings/C03_StringCompression.java) * | String Compression | LeetCode 443 / Medium | write never overtakes read: a run of k chars occupies k cells but is encoded in at most 1 + digits(k) <= k cells, so writing into the same array is safe. |
| [C04_IntegerToRoman.java](04-Strings/C04_IntegerToRoman.java) | Integer to Roman | LeetCode 12 / Medium | Greedy is only correct because the subtractive pairs are in the table: 900 sits between 1000 and 500, so 1994 takes M then CM and never tries to build 900 as D + C + C + C + C. |
| [C05_ZigzagConversion.java](04-Strings/C05_ZigzagConversion.java) | Zigzag Conversion | LeetCode 6 / Medium | Do not compute column positions; the zigzag is just a row index bouncing between 0 and numRows - 1. |
| [C06_RemoveAllOccurrences.java](04-Strings/C06_RemoveAllOccurrences.java) | Remove All Occurrences of a Substring | LeetCode 1910 / Medium | The naive loop is correct because it always re-searches from index 0, so any occurrence created by the join is found. |
| [C07_MultiplyStrings.java](04-Strings/C07_MultiplyStrings.java) | Multiply Strings | LeetCode 43 / Medium | The whole problem is the index mapping: digit i of num1 times digit j of num2 contributes to result positions i + j and i + j + 1 (counting from the left in an array of length m + n). |
| [C08_LargestMergeOfTwoStrings.java](04-Strings/C08_LargestMergeOfTwoStrings.java) | Largest Merge of Two Strings | LeetCode 1754 / Medium | Comparing only the next character is wrong on ties. |
| [C09_AddBoldTagInString.java](04-Strings/C09_AddBoldTagInString.java) | Add Bold Tag in String | LeetCode 616 / Medium | Do not try to insert tags while searching: overlapping matches would produce nested or broken tags. |
| [D01_ValidNumber.java](04-Strings/D01_ValidNumber.java) | Valid Number | LeetCode 65 / Hard | There is no algorithm here, only ordering rules: dot before e, a digit before e, a sign only at a group start, a digit somewhere in every group. |
| [D02_TextJustification.java](04-Strings/D02_TextJustification.java) | Text Justification | LeetCode 68 / Hard | Greedy packing is optimal here (deferring a word never lets more fit later), so all the difficulty is bookkeeping: gaps = words - 1, the quotient goes to every gap, the remainder goes to the leftmost gaps, and two except |
| [D03_NumberToWordsConverter.java](04-Strings/D03_NumberToWordsConverter.java) | Number to Words (Indian system) | LeetCode 273 variant / Hard | Every group is "a number below 100 (or 1000) plus a unit word", so one small helper does all the wording and the top level is only arithmetic on group boundaries. |
| [D04_NextPalindromeUsingSameDigits.java](04-Strings/D04_NextPalindromeUsingSameDigits.java) | Next Palindrome Using Same Digits | Educative / GfG / Hard | A palindrome is fully determined by its left half, so "next palindrome with the same digits" is exactly "next permutation of the left half". |

**Worth adding next:**

- Valid Palindrome II — palindrome after deleting at most one character (LC 680): Two pointers with a single branch point: on the first mismatch, recurse/verify the two candidate sub-ranges (skip left, skip right) in O(n) total, O(1) space.
- Implement strStr / indexOf with KMP (LC 28 plus the LPS / prefix-function build): Build the longest-proper-prefix-suffix array for the pattern, then scan the text once without ever moving the text pointer backwards; O(n+m) time, O(m) space.
- Find All Anagrams in a String / Permutation in String (LC 438, LC 567): Fixed-size sliding window over an int[26] with a single 'matches' counter updated incrementally as characters enter and leave — O(n), not O(n) windows each compared in O(26).

## 05-Hashing-Prefix-Sum

HashMap and HashSet counting, prefix sum plus map, grouping and set operations.

**Do these first:** [A02_TwoSum.java](05-Hashing-Prefix-Sum/A02_TwoSum.java), [A04_CountVowelStringsInRanges.java](05-Hashing-Prefix-Sum/A04_CountVowelStringsInRanges.java), [C01_CountSubarraySumEqualsK.java](05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java), [C03_LongestSubarrayWithSumKHash.java](05-Hashing-Prefix-Sum/C03_LongestSubarrayWithSumKHash.java), [C05_GroupAnagrams.java](05-Hashing-Prefix-Sum/C05_GroupAnagrams.java), [D01_LongestConsecutiveSequence.java](05-Hashing-Prefix-Sum/D01_LongestConsecutiveSequence.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_ContainsDuplicate.java](05-Hashing-Prefix-Sum/A01_ContainsDuplicate.java) | Contains Duplicate | LeetCode 217 / Easy | Trade O(n) memory for O(1) average lookup. |
| [A02_TwoSum.java](05-Hashing-Prefix-Sum/A02_TwoSum.java) * | Two Sum | LeetCode 1 / Easy | Instead of searching for a partner (O(n) per element), ask the map whether the partner has ALREADY been seen (O(1)). |
| [A03_RansomNote.java](05-Hashing-Prefix-Sum/A03_RansomNote.java) | Ransom Note | LeetCode 383 / Easy | When the key space is small and fixed (26 letters), an int[26] beats a HashMap: no boxing, no hashing, cache friendly. |
| [A04_CountVowelStringsInRanges.java](05-Hashing-Prefix-Sum/A04_CountVowelStringsInRanges.java) * | Count Vowel Strings in Ranges | LeetCode 2559 / Medium | Precompute once, answer many. |
| [B01_FindTheDifferenceOfTwoArrays.java](05-Hashing-Prefix-Sum/B01_FindTheDifferenceOfTwoArrays.java) | Find the Difference of Two Arrays | LeetCode 2215 / Easy | "In A but not in B" is a set-membership question, so build a set of B and ask contains() once per element of A. |
| [B02_SetDifferenceOfTwoArrays.java](05-Hashing-Prefix-Sum/B02_SetDifferenceOfTwoArrays.java) | Set Difference of Two Arrays (symmetric difference) | Related: LeetCode 2215 / Easy | "In exactly one array" is two one-sided differences glued together (A\B then B\A). |
| [B03_IntersectionOfTwoArrays.java](05-Hashing-Prefix-Sum/B03_IntersectionOfTwoArrays.java) | Intersection of Two Sorted Arrays | LeetCode 349 (sorted variant) / Easy | Sorted order lets you discard the smaller head with certainty, so every step retires one element and the walk is linear with O(1) extra space. |
| [B04_IntersectionOfTwoArraysII.java](05-Hashing-Prefix-Sum/B04_IntersectionOfTwoArraysII.java) | Intersection of Two Arrays II | LeetCode 350 / Easy | A Set answers "seen?"; a count map answers "how many are still unclaimed?". |
| [B05_UniqueTuples.java](05-Hashing-Prefix-Sum/B05_UniqueTuples.java) | Unique Tuples (distinct substrings of length k) | Classic warm-up / Easy | Fixed-length windows are counted by n - k + 1, not n - k; off-by-one here drops the last window. |
| [B06_TournamentWinner.java](05-Hashing-Prefix-Sum/B06_TournamentWinner.java) | Tournament Winner | AlgoExpert / Easy | Accumulate and track the maximum in the same pass. |
| [B07_ExponentPairs.java](05-Hashing-Prefix-Sum/B07_ExponentPairs.java) | Exponent Pairs (a^b == b^a) | Classic puzzle / Easy | Take the log of both sides: a^b == b^a  <=>  ln(a)/a == ln(b)/b. |
| [B08_SortBasedOnFrequencyOfOccurrence.java](05-Hashing-Prefix-Sum/B08_SortBasedOnFrequencyOfOccurrence.java) | Sort Array by Frequency of Occurrence | LeetCode 1636 variant / Easy | Counting only sets up the problem; the answer is a ranking by count. |
| [C01_CountSubarraySumEqualsK.java](05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java) * | Subarray Sum Equals K | LeetCode 560 / Medium | Turn "sum of a range" into "difference of two prefix sums", then the question becomes "how many earlier prefixes equal P(i) - k", which a HashMap answers in O(1). |
| [C02_BinarySubarraysWithSum.java](05-Hashing-Prefix-Sum/C02_BinarySubarraysWithSum.java) | Binary Subarrays With Sum | LeetCode 930 / Medium | Same prefix-sum + map skeleton as LC 560, unchanged; a 0/1 array is just a special case. |
| [C03_LongestSubarrayWithSumKHash.java](05-Hashing-Prefix-Sum/C03_LongestSubarrayWithSumKHash.java) * | Longest Subarray With Sum K | LeetCode 325 / Medium | This is the sibling of LC 560 and the contrast decides the whole family: COUNT problems store a count per prefix (put / merge); LONGEST problems store the first index per prefix (putIfAbsent). |
| [C04_RepeatedDnaSequences.java](05-Hashing-Prefix-Sum/C04_RepeatedDnaSequences.java) | Repeated DNA Sequences | LeetCode 187 / Medium | Set.add's boolean is a free "have I seen this before?" test - no containsKey then put. |
| [C05_GroupAnagrams.java](05-Hashing-Prefix-Sum/C05_GroupAnagrams.java) * | Group Anagrams | LeetCode 49 / Medium | "Group equivalent things" == "map each thing to a canonical key, bucket by key". |
| [C06_GroupShiftedStrings.java](05-Hashing-Prefix-Sum/C06_GroupShiftedStrings.java) | Group Shifted Strings | LeetCode 249 / Medium | Shifting adds the same constant to every letter, so the differences between adjacent letters are invariant under shift. |
| [C07_FindAndReplacePattern.java](05-Hashing-Prefix-Sum/C07_FindAndReplacePattern.java) | Find and Replace Pattern | LeetCode 890 / Medium | Replacing each letter with the index of its first occurrence turns any string into a canonical fingerprint that is the same for all strings of the same letter-shape. |
| [C08_SubdomainVisitCount.java](05-Hashing-Prefix-Sum/C08_SubdomainVisitCount.java) | Subdomain Visit Count | LeetCode 811 / Medium | One input row updates several map keys. |
| [C09_MinimumRounds.java](05-Hashing-Prefix-Sum/C09_MinimumRounds.java) | Minimum Rounds to Complete All Tasks | LeetCode 2244 / Medium | The hash map only sets up the real question: "given c identical items, how few groups of 2 or 3?" Using as many 3s as possible is optimal, and any leftover of 1 can be fixed by borrowing from one triple (3+1 -> 2+2), so  |
| [C10_PairsOfSongsDivBy60.java](05-Hashing-Prefix-Sum/C10_PairsOfSongsDivBy60.java) | Pairs of Songs With Total Durations Divisible by 60 | LeetCode 1010 / Medium | This is Two Sum with the key mapped through mod 60: instead of "have I seen target - x", ask "how many x' with (x + x') % 60 == 0 have I seen". |
| [D01_LongestConsecutiveSequence.java](05-Hashing-Prefix-Sum/D01_LongestConsecutiveSequence.java) * | Longest Consecutive Sequence | LeetCode 128 / Medium | The nested loop looks O(n^2) but is amortized O(n): each value is walked over at most once, because only the head of each run starts a walk and the walks are disjoint. |
| [D02_SmallestMissingNonNegativeIntegerAfterOperations.java](05-Hashing-Prefix-Sum/D02_SmallestMissingNonNegativeIntegerAfterOperations.java) | Smallest Missing Non-negative Integer After Operations | LeetCode 2598 / Medium | Spot the invariant first: +/- value cannot move an element out of its residue class. |

**Worth adding next:**

- Contiguous Array (LC 525) — longest subarray with equal numbers of 0s and 1s: Remap 0 -> -1, then the problem collapses to longest subarray with sum 0: prefix sum + map-of-first-index, answer = i - firstIndex[prefix].
- Subarray Sums Divisible by K (LC 974): Prefix sum taken modulo k, counting map keyed by remainder: two prefixes with the same remainder bound a divisible subarray. Needs ((r % k) + k) % k for negative values, and count += map.get(r) before incrementing.
- Path Sum III (LC 437) — count root-to-any-node downward paths summing to target: The same prefix-sum + counting-map as LC 560, carried down a DFS: add the running prefix on the way in, read map.get(prefix - target), and decrement the entry on the way out so sibling branches are not contaminated.

## 06-Linked-List

Reversal, cycle detection, merge, reorder, doubly linked lists.

**Do these first:** [A02_ReverseLinkedList.java](06-Linked-List/A02_ReverseLinkedList.java), [A03_FindMiddleOfLinkedList.java](06-Linked-List/A03_FindMiddleOfLinkedList.java), [A04_MergeTwoSortedLists.java](06-Linked-List/A04_MergeTwoSortedLists.java), [B03_PalindromeLinkedList.java](06-Linked-List/B03_PalindromeLinkedList.java), [C02_DeleteNthNodefromEnd.java](06-Linked-List/C02_DeleteNthNodefromEnd.java), [C07_LinkedListLoopDetection.java](06-Linked-List/C07_LinkedListLoopDetection.java), [D01_ReverseListInKGroups.java](06-Linked-List/D01_ReverseListInKGroups.java), [D02_MergeKLists.java](06-Linked-List/D02_MergeKLists.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_ListNodeReferencing.java](06-Linked-List/A01_ListNodeReferencing.java) | ListNode Referencing | Building block / Easy | "prev = curr" and "curr = curr.next" move handles. |
| [A02_ReverseLinkedList.java](06-Linked-List/A02_ReverseLinkedList.java) * | Reverse Linked List | LeetCode 206 / Easy | Three handles, one save-before-overwrite. |
| [A03_FindMiddleOfLinkedList.java](06-Linked-List/A03_FindMiddleOfLinkedList.java) * | Middle of the Linked List | LeetCode 876 / Easy | Two pointers moving at a 2:1 ratio meet the halfway point without counting. |
| [A04_MergeTwoSortedLists.java](06-Linked-List/A04_MergeTwoSortedLists.java) * | Merge Two Sorted Lists | LeetCode 21 / Easy | The sentinel removes the "is this the first node?" special case, so the loop body is identical for every node. |
| [A05_DeleteHeadOfDLL.java](06-Linked-List/A05_DeleteHeadOfDLL.java) | Delete Head of a Doubly Linked List | Building block / Easy | A doubly linked node has TWO links, so a correct removal is two writes, not one. |
| [A06_DoublyLinkedListReverse.java](06-Linked-List/A06_DoublyLinkedListReverse.java) | Reverse a Doubly Linked List | GfG classic / Easy | On a doubly linked list, reversal is nothing more than swapping the two pointers in every node. |
| [B01_RemoveDuplicatesInList.java](06-Linked-List/B01_RemoveDuplicatesInList.java) | Remove Duplicates from Sorted List | LeetCode 83 / Easy | At each step there are exactly two choices: unlink or advance, never both. |
| [B02_IntersectionOfTwoLinkedLists.java](06-Linked-List/B02_IntersectionOfTwoLinkedLists.java) | Intersection of Two Linked Lists | LeetCode 160 / Easy | After the shared node, both lists are identical, so the tails have the same length. |
| [B03_PalindromeLinkedList.java](06-Linked-List/B03_PalindromeLinkedList.java) * | Palindrome Linked List | LeetCode 234 / Easy | Reversing the second half turns "compare index i with n-1-i" into a forward walk of two lists. |
| [C01_AddTwoNumbers.java](06-Linked-List/C01_AddTwoNumbers.java) | Add Two Numbers | LeetCode 2 / Medium | Put the carry INTO the loop condition. |
| [C02_DeleteNthNodefromEnd.java](06-Linked-List/C02_DeleteNthNodefromEnd.java) * | Remove Nth Node From End of List | LeetCode 19 / Medium | Two pointers with a constant gap of N: when the leader hits the end, the follower is exactly N from the end. |
| [C03_PartitionList.java](06-Linked-List/C03_PartitionList.java) | Partition List | LeetCode 86 / Medium | Reuse the existing nodes: you are not building a new list, you are re-threading the old one into two chains and joining them. |
| [C04_SortListOf0s1s2s.java](06-Linked-List/C04_SortListOf0s1s2s.java) | Sort a Linked List of 0s, 1s and 2s | GfG / Striver / Medium | This is Partition List (C03_PartitionList) with three buckets instead of two. |
| [C05_OddEvenLinkedList.java](06-Linked-List/C05_OddEvenLinkedList.java) | Odd Even Linked List | LeetCode 328 / Medium | The two pointers leapfrog each other; every assignment reads a pointer that has NOT yet been overwritten this round, so no temp variable is needed. |
| [C06_RotateRightList.java](06-Linked-List/C06_RotateRightList.java) | Rotate List | LeetCode 61 / Medium | Rotating right by k is the same as "cut after node length - k and swap the two pieces". |
| [C07_LinkedListLoopDetection.java](06-Linked-List/C07_LinkedListLoopDetection.java) * | Linked List Cycle II (loop entry node) | LeetCode 142 / Medium | Let the distance head -> entry be a, entry -> meeting point be b, and the loop length be L. |
| [C08_LinkedListLoopLength.java](06-Linked-List/C08_LinkedListLoopLength.java) | Length of Loop in Linked List | GeeksforGeeks / Easy-Medium | The meeting point does not tell you WHERE the loop starts (that needs the second phase from C07_LinkedListLoopDetection), but any node inside a ring is enough to measure the ring: walk around once and count. |
| [C09_MaximumTwinSum.java](06-Linked-List/C09_MaximumTwinSum.java) | Maximum Twin Sum of a Linked List | LeetCode 2130 / Medium | You cannot index a linked list, so "pair node i with node n-1-i" is done by reversing the second half and walking both halves forward. |
| [C10_ReorderList.java](06-Linked-List/C10_ReorderList.java) | Reorder List | LeetCode 143 / Medium | The target order is "first half forwards" zipped with "second half backwards". |
| [C11_SortList.java](06-Linked-List/C11_SortList.java) | Sort List | LeetCode 148 / Medium | Merge sort needs only two things a linked list is good at: splitting by walking to the middle, and merging by relinking. |
| [C12_CopyRandomList.java](06-Linked-List/C12_CopyRandomList.java) | Copy List with Random Pointer | LeetCode 138 / Medium | The hard part is that random can point forwards to a node whose copy does not exist yet. |
| [C13_MergeNodesInBetweenZeros.java](06-Linked-List/C13_MergeNodesInBetweenZeros.java) | Merge Nodes in Between Zeros | LeetCode 2181 / Medium | You do not need a new list. |
| [C14_MergeInBetweenLinkedLists.java](06-Linked-List/C14_MergeInBetweenLinkedLists.java) | Merge In Between Linked Lists | LeetCode 1669 / Medium | You only ever need two nodes from list1: the one just BEFORE the gap (index a-1) and the one just AFTER it (index b+1). |
| [D01_ReverseListInKGroups.java](06-Linked-List/D01_ReverseListInKGroups.java) * | Reverse Nodes in k-Group | LeetCode 25 / Hard | Per window you must hold exactly three references: the previous window's tail (to connect in), the window's k-th node (becomes its head), and the next window's head (to continue). |
| [D02_MergeKLists.java](06-Linked-List/D02_MergeKLists.java) * | Merge k Sorted Lists | LeetCode 23 / Hard | You only ever need to compare the FRONT of each list, so keep exactly k candidates in a heap and let it pick the minimum in O(log k). |

**Worth adding next:**

- Remove Duplicates from Sorted List II (LC 82): Dummy head + prev pointer, skip an entire run of equal values rather than just the trailing copies
- Add Two Numbers II (LC 445): Carry propagation from the least significant end when digits are stored most-significant-first — two stacks, or reverse-add-reverse, with the front-insertion build
- Flatten a Multilevel Doubly Linked List (LC 430): DFS over a list using an explicit stack, splicing a child list inline while maintaining both next and prev pointers

## 07-Stack-Queue-Monotonic

Expression evaluation, parentheses, monotonic stack and deque patterns.

**Do these first:** [A02_ValidParentheses.java](07-Stack-Queue-Monotonic/A02_ValidParentheses.java), [A03_NextGreaterElements.java](07-Stack-Queue-Monotonic/A03_NextGreaterElements.java), [C06_DailyTemperatures.java](07-Stack-Queue-Monotonic/C06_DailyTemperatures.java), [C11_AsteroidCollision.java](07-Stack-Queue-Monotonic/C11_AsteroidCollision.java), [C13_DecodeString.java](07-Stack-Queue-Monotonic/C13_DecodeString.java), [D01_LongestValidParentheses.java](07-Stack-Queue-Monotonic/D01_LongestValidParentheses.java), [D02_LargestRectangleArea.java](07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java), [D04_SlidingWindowMaximum.java](07-Stack-Queue-Monotonic/D04_SlidingWindowMaximum.java), [D05_BasicCalculator.java](07-Stack-Queue-Monotonic/D05_BasicCalculator.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_StackTraversal.java](07-Stack-Queue-Monotonic/A01_StackTraversal.java) | Stack Traversal Order | Java core demo / Easy | Iteration order of a stack is an implementation detail, not a LIFO guarantee. |
| [A02_ValidParentheses.java](07-Stack-Queue-Monotonic/A02_ValidParentheses.java) * | Valid Parentheses | LeetCode 20 / Easy | A closing bracket must match the MOST RECENT unmatched opener, and "most recent" is exactly what a stack top is. |
| [A03_NextGreaterElements.java](07-Stack-Queue-Monotonic/A03_NextGreaterElements.java) * | Next Greater Element (array) | LeetCode 496 family / Easy | An element that is smaller than the newcomer can never be anyone's "next greater", so it can be resolved and discarded right now. |
| [A04_QueueUsingStacks.java](07-Stack-Queue-Monotonic/A04_QueueUsingStacks.java) | Implement Queue using Stacks | LeetCode 232 / Easy | Two reversals make a forward order: a stack reverses on the way in, a second stack reverses again on the way out. |
| [B01_RemoveAdjacentDuplicates.java](07-Stack-Queue-Monotonic/B01_RemoveAdjacentDuplicates.java) | Remove All Adjacent Duplicates In String | LeetCode 1047 / Easy | One pass is enough because the stack top always holds the character that is CURRENTLY adjacent to the new one, even after earlier removals. |
| [C01_RemovingStarsFromAString.java](07-Stack-Queue-Monotonic/C01_RemovingStarsFromAString.java) | Removing Stars From a String | LeetCode 2390 / Medium | "Delete the nearest thing to the left" is a stack pop. |
| [C02_MinimumAddToMakeParenthesesValid.java](07-Stack-Queue-Monotonic/C02_MinimumAddToMakeParenthesesValid.java) | Minimum Add to Make Parentheses Valid | LeetCode 921 / Medium | Valid Parentheses (LC 20) only needs one balance counter and a "did it ever go negative" check. |
| [C03_EvaluateReversePolishNotation.java](07-Stack-Queue-Monotonic/C03_EvaluateReversePolishNotation.java) | Evaluate Reverse Polish Notation | LeetCode 150 / Medium | Postfix needs no precedence rules and no parentheses: the stack IS the evaluation order. |
| [C04_StackSortable.java](07-Stack-Queue-Monotonic/C04_StackSortable.java) | Stack Sortable Permutation | GeeksforGeeks / Medium | The stack models a process, not a pairing of symbols. |
| [C05_BuildingsWithOceanView.java](07-Stack-Queue-Monotonic/C05_BuildingsWithOceanView.java) | Buildings With an Ocean View | LeetCode 1762 / Medium | "Can see past everything on one side" only depends on the maximum on that side, so scan from that side and carry one number. |
| [C06_DailyTemperatures.java](07-Stack-Queue-Monotonic/C06_DailyTemperatures.java) * | Daily Temperatures | LeetCode 739 / Medium | This is Next Greater Element, but the answer is a DISTANCE, so store indices, not values. |
| [C07_RemoveKdigits.java](07-Stack-Queue-Monotonic/C07_RemoveKdigits.java) | Remove K Digits | LeetCode 402 / Medium | Greedy: the leftmost "peak" (a digit followed by a smaller one) is always the right thing to remove first, because it lowers the most significant position that can be lowered. |
| [C08_CarFleet.java](07-Stack-Queue-Monotonic/C08_CarFleet.java) | Car Fleet | LeetCode 853 / Medium | Position order plus arrival time turns a physics question into a monotonic scan. |
| [C09_SumOfSubarrayMinimums.java](07-Stack-Queue-Monotonic/C09_SumOfSubarrayMinimums.java) | Sum of Subarray Minimums | LeetCode 907 / Medium | Flip the question: instead of "what is the min of each subarray", ask "how many subarrays is each element the min of". |
| [C10_Find132pattern.java](07-Stack-Queue-Monotonic/C10_Find132pattern.java) | 132 Pattern | LeetCode 456 / Medium | Fix the "3" as the element being scanned and ask: what is the largest "2" that already has a bigger element to its right? |
| [C11_AsteroidCollision.java](07-Stack-Queue-Monotonic/C11_AsteroidCollision.java) * | Asteroid Collision | LeetCode 735 / Medium | Only "right-mover on the stack, left-mover arriving" can collide, so one while loop with a three-way branch covers every case. |
| [C12_SimplifyPath.java](07-Stack-Queue-Monotonic/C12_SimplifyPath.java) | Simplify Path | LeetCode 71 / Medium | Work on tokens, not characters: split first, then the stack only ever sees whole names. |
| [C13_DecodeString.java](07-Stack-Queue-Monotonic/C13_DecodeString.java) * | Decode String | LeetCode 394 / Medium | '[' is "save and enter", ']' is "restore and merge". |
| [C14_ExclusiveTimeOfFunctions.java](07-Stack-Queue-Monotonic/C14_ExclusiveTimeOfFunctions.java) | Exclusive Time of Functions | LeetCode 636 / Medium | Every log line is a boundary: whatever is on top of the stack owns the time between the previous boundary and this one. |
| [C15_InfixToPrefix.java](07-Stack-Queue-Monotonic/C15_InfixToPrefix.java) | Infix to Prefix Conversion | GfG classic / Medium | Prefix of E == reverse(postfix(reverse(E) with brackets swapped)). |
| [D01_LongestValidParentheses.java](07-Stack-Queue-Monotonic/D01_LongestValidParentheses.java) * | Longest Valid Parentheses | LeetCode 32 / Hard | Store INDICES, not characters. |
| [D02_LargestRectangleArea.java](07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java) * | Largest Rectangle in Histogram | LeetCode 84 / Hard | Every bar is the limiting height of exactly one maximal rectangle, and that rectangle spans from the previous shorter bar to the next shorter bar. |
| [D03_VisiblePeopleInQueue.java](07-Stack-Queue-Monotonic/D03_VisiblePeopleInQueue.java) | Number of Visible People in a Queue | LeetCode 1944 / Hard | Person i sees every shorter person up to and including the first taller one. |
| [D04_SlidingWindowMaximum.java](07-Stack-Queue-Monotonic/D04_SlidingWindowMaximum.java) * | Sliding Window Maximum | LeetCode 239 / Hard | A smaller element to the LEFT of a bigger one is dead: it leaves the window first and is never the max while the bigger one lives. |
| [D05_BasicCalculator.java](07-Stack-Queue-Monotonic/D05_BasicCalculator.java) * | Basic Calculator | LeetCode 224 / Hard | A '(' does not need recursion: it only needs to remember two things, the outer running total and the sign that was waiting to be applied. |
| [D06_RobotCollisions.java](07-Stack-Queue-Monotonic/D06_RobotCollisions.java) | Robot Collisions | LeetCode 2751 / Hard | This is Asteroid Collision with two extras: mutable state (health decrements instead of binary win/lose) and a transform step (sort by position first, restore original order at the end). |

**Worth adding next:**

- Remove Duplicate Letters / Smallest Subsequence of Distinct Characters (LC 316 / 1081): Monotonic stack greedy with a last-occurrence guard and an in-stack set: pop a larger character only if it reappears later, skip characters already on the stack.
- Maximal Rectangle (LC 85): Build a histogram of consecutive-ones heights row by row, then run Largest Rectangle in Histogram on each row's height array.
- Longest Continuous Subarray With Absolute Diff Less Than or Equal to Limit (LC 1438): Sliding window with two monotonic deques run in parallel — one decreasing for the window max, one increasing for the window min — shrinking from the left while max minus min exceeds the limit.

## 08-Heap-Priority-Queue

Top-K, median of a stream, scheduling with a min or max heap.

**Do these first:** [A01_KthLargestElementInAStream.java](08-Heap-Priority-Queue/A01_KthLargestElementInAStream.java), [C01_topKFrequent.java](08-Heap-Priority-Queue/C01_topKFrequent.java), [C02_KClosestPointsToOrigin.java](08-Heap-Priority-Queue/C02_KClosestPointsToOrigin.java), [D01_MedianOfStream.java](08-Heap-Priority-Queue/D01_MedianOfStream.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_KthLargestElementInAStream.java](08-Heap-Priority-Queue/A01_KthLargestElementInAStream.java) * | Kth Largest Element in a Stream | LeetCode 703 / Easy | "Kth largest" == "smallest of the k largest". |
| [B01_LastStoneWeight.java](08-Heap-Priority-Queue/B01_LastStoneWeight.java) | Last Stone Weight | LeetCode 1046 / Easy | The rule "always act on the two largest" is exactly what a max-heap serves in O(log n). |
| [C01_topKFrequent.java](08-Heap-Priority-Queue/C01_topKFrequent.java) * | Top K Frequent Elements | LeetCode 347 / Medium | Same primitive as Kth Largest in a Stream, just keyed on a computed frequency rather than the value itself. |
| [C02_KClosestPointsToOrigin.java](08-Heap-Priority-Queue/C02_KClosestPointsToOrigin.java) * | K Closest Points to Origin | LeetCode 973 / Medium | "K closest" is "k smallest by distance", so the heap direction flips: a max-heap evicts the worst survivor. |
| [C03_HuffmanCoding.java](08-Heap-Priority-Queue/C03_HuffmanCoding.java) | Huffman Coding | Classic greedy (GfG) / Medium | The two rarest symbols should be the deepest leaves, and merging them makes a pseudo- symbol that is treated exactly like any other -- so the greedy is just "poll two, push one" on a heap, the Last Stone Weight loop with |
| [C04_ReorganizeString.java](08-Heap-Priority-Queue/C04_ReorganizeString.java) | Reorganize String | LeetCode 767 / Medium | Always placing the two most frequent remaining characters next to each other stops the most frequent one from ever being forced beside itself. |
| [C05_MaximumAveragePassRatio.java](08-Heap-Priority-Queue/C05_MaximumAveragePassRatio.java) | Maximum Average Pass Ratio | LeetCode 1792 / Medium | For one class, each added student gives a smaller gain than the previous one (diminishing returns), so taking the single best marginal gain each time is globally optimal. |
| [C06_MaximumNumberofEventsThatCanBeAttended.java](08-Heap-Priority-Queue/C06_MaximumNumberofEventsThatCanBeAttended.java) | Maximum Number of Events That Can Be Attended | LeetCode 1353 / Medium | On any given day, among the events you could still attend, the one that expires first is the one you lose if you do not take it now; every other choice can wait. |
| [C07_FurthestBuildingYouCanReach.java](08-Heap-Priority-Queue/C07_FurthestBuildingYouCanReach.java) | Furthest Building You Can Reach | LeetCode 1642 / Medium | You cannot know up front which climbs deserve a ladder, so make a tentative choice and take it back later ("regret" greedy). |
| [D01_MedianOfStream.java](08-Heap-Priority-Queue/D01_MedianOfStream.java) * | Find Median from Data Stream | LeetCode 295 / Hard | The median only depends on the boundary between the lower and upper halves, and a heap gives O(1) access to exactly one boundary element. |
| [D02_SlidingWindowMedian.java](08-Heap-Priority-Queue/D02_SlidingWindowMedian.java) | Sliding Window Median | LeetCode 480 / Hard | The median-of-stream two-heap trick still works with eviction, as long as every add AND every remove is followed by a rebalance. |
| [D03_MinimumRefuelingStops.java](08-Heap-Priority-Queue/D03_MinimumRefuelingStops.java) | Minimum Number of Refueling Stops | LeetCode 871 / Hard | You do not have to decide at a station whether to stop there. |

**Worth adding next:**

- Task Scheduler (LC 621): Max-heap by remaining count plus a cooldown queue, and the O(1) counting-formula alternative ((maxFreq-1)*(n+1) + countOfMaxFreq)
- Find K Pairs with Smallest Sums (LC 373), with Kth Smallest Element in a Sorted Matrix (LC 378) as the sibling: Heap frontier over a 2D candidate grid: push (i,j) successors with a visited set for dedup, plus binary-search-on-answer as the alternative for 378
- Maximum Performance of a Team (LC 1383), with Minimum Cost to Hire K Workers (LC 857) and Maximum Subsequence Score (LC 2542) as siblings: Sort by one key descending, keep a size-k min-heap on the second key while maintaining a running sum that is decremented on every eviction

## 09-Trees-BST

Traversals, LCA variants, BST operations, construction from traversals, path sums.

**Do these first:** [A01_RecursivePostorder.java](09-Trees-BST/A01_RecursivePostorder.java), [A02_HeightOfBinaryTree.java](09-Trees-BST/A02_HeightOfBinaryTree.java), [A03_LevelOrderTraversal.java](09-Trees-BST/A03_LevelOrderTraversal.java), [A05_BinarySearchTreeOperations.java](09-Trees-BST/A05_BinarySearchTreeOperations.java), [B09_DiameterOfBinaryTree.java](09-Trees-BST/B09_DiameterOfBinaryTree.java), [C01_ValidateBST.java](09-Trees-BST/C01_ValidateBST.java), [C02_NthLargestInBST.java](09-Trees-BST/C02_NthLargestInBST.java), [C10_PathSumII.java](09-Trees-BST/C10_PathSumII.java), [C11_LowestCommonAncestorBST.java](09-Trees-BST/C11_LowestCommonAncestorBST.java), [C12_LCA.java](09-Trees-BST/C12_LCA.java), [C18_RightView.java](09-Trees-BST/C18_RightView.java), [C19_BinaryTree.java](09-Trees-BST/C19_BinaryTree.java), [C23_ConstructBinaryTreeFromINPre.java](09-Trees-BST/C23_ConstructBinaryTreeFromINPre.java), [D01_BinaryTreeMaxPathSum.java](09-Trees-BST/D01_BinaryTreeMaxPathSum.java), [D02_SerializeAndDeserialiseBinaryTree.java](09-Trees-BST/D02_SerializeAndDeserialiseBinaryTree.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_RecursivePostorder.java](09-Trees-BST/A01_RecursivePostorder.java) * | Binary Tree Postorder Traversal | LeetCode 145 / Easy | The three DFS orders differ only in WHERE the "visit" line sits relative to the two recursive calls: before both = preorder, between = inorder, after both = postorder. |
| [A02_HeightOfBinaryTree.java](09-Trees-BST/A02_HeightOfBinaryTree.java) * | Maximum Depth (Height) of Binary Tree | LeetCode 104 / Easy | This is the first tree recursion that RETURNS a computed value instead of just visiting. |
| [A03_LevelOrderTraversal.java](09-Trees-BST/A03_LevelOrderTraversal.java) * | Binary Tree Level Order Traversal | LeetCode 102 / Medium | A plain BFS queue mixes two levels together, so you cannot tell where a level ends. |
| [A04_BranchSums.java](09-Trees-BST/A04_BranchSums.java) | Branch Sums (all root-to-leaf sums) | AlgoExpert classic / Easy | The running total travels DOWN as a parameter, not UP as a return value. |
| [A05_BinarySearchTreeOperations.java](09-Trees-BST/A05_BinarySearchTreeOperations.java) * | Binary Search Tree: insert, search, in-order | Building block / Easy | 1. |
| [A06_PrePostInorderInOneTraversal.java](09-Trees-BST/A06_PrePostInorderInOneTraversal.java) | Preorder, Inorder and Postorder in One Traversal | GFG / Medium | Recursion "remembers where it was" through the call stack; the state number is that memory made explicit. |
| [B01_InvertTree.java](09-Trees-BST/B01_InvertTree.java) | Invert Binary Tree | LeetCode 226 / Easy | Inverting a tree is "swap the two children" applied at every node. |
| [B02_SameTree.java](09-Trees-BST/B02_SameTree.java) | Same Tree | LeetCode 100 / Easy | Walk the two trees in lockstep and compare position by position. |
| [B03_SymmetricTree.java](09-Trees-BST/B03_SymmetricTree.java) | Symmetric Tree | LeetCode 101 / Easy | This is SameTree with the child pairing crossed. |
| [B04_IsSubtree.java](09-Trees-BST/B04_IsSubtree.java) | Subtree of Another Tree | LeetCode 572 / Easy | An outer traversal picks a candidate start node, an inner comparison checks it fully. |
| [B05_HasPathSum.java](09-Trees-BST/B05_HasPathSum.java) | Path Sum | LeetCode 112 / Easy | Pass the REMAINING target down instead of the running sum, so a leaf needs one equality check. |
| [B06_SumOfLeftLeaves.java](09-Trees-BST/B06_SumOfLeftLeaves.java) | Sum of Left Leaves | LeetCode 404 / Easy | "Left leaf" is a fact a node cannot know about itself: it sees its own children, not which side of its parent it hangs from. |
| [B07_ExpressionTreeEvaluator.java](09-Trees-BST/B07_ExpressionTreeEvaluator.java) | Evaluate Expression Tree | Classic (AlgoExpert) / Easy | This is postorder traversal made concrete: children first, parent last, because a parent's value is a function of its children's values. |
| [B08_MaximumSubtreeSum.java](09-Trees-BST/B08_MaximumSubtreeSum.java) | Maximum Subtree Sum | Classic (GfG) / Easy | Two different things happen at each node: what you RETURN upward (this subtree's sum, which the parent needs) and what you RECORD globally (the best answer so far). |
| [B09_DiameterOfBinaryTree.java](09-Trees-BST/B09_DiameterOfBinaryTree.java) * | Diameter of Binary Tree | LeetCode 543 / Easy | What you return up is NOT what you are answering. |
| [B10_IsBalancedBinaryTree.java](09-Trees-BST/B10_IsBalancedBinaryTree.java) | Balanced Binary Tree | LeetCode 110 / Easy | The naive answer calls height() at every node and re-walks each subtree: O(n^2). |
| [B11_Cousins.java](09-Trees-BST/B11_Cousins.java) | Cousins in Binary Tree | LeetCode 993 / Easy | "Cousin" needs two facts about each node at once: its depth and who its parent is. |
| [B12_RangeSumBST.java](09-Trees-BST/B12_RangeSumBST.java) | Range Sum of BST | LeetCode 938 / Easy | This is the first problem where the BST property is used to SKIP work rather than to locate one node. |
| [B13_FloorCeilOfBST.java](09-Trees-BST/B13_FloorCeilOfBST.java) | Floor and Ceil in a BST | GFG / Easy | You never visit the whole tree. |
| [B14_GetMinimumDifference.java](09-Trees-BST/B14_GetMinimumDifference.java) | Minimum Absolute Difference in BST | LeetCode 530 / Easy | Do not think "tree", think "sorted stream". |
| [B15_SortedArrayToBST.java](09-Trees-BST/B15_SortedArrayToBST.java) | Convert Sorted Array to Height-Balanced BST | LeetCode 108 / Easy | This is the inverse of an in-order traversal. |
| [C01_ValidateBST.java](09-Trees-BST/C01_ValidateBST.java) * | Validate Binary Search Tree | LeetCode 98 / Medium | The near-universal wrong answer is to compare each node only with its two children. |
| [C02_NthLargestInBST.java](09-Trees-BST/C02_NthLargestInBST.java) * | Nth Largest Element in a BST | LeetCode 230 variant / Medium | The BST's sorted order is free via in-order traversal; an order statistic (kth smallest/largest) is just "visit in sorted order and count". |
| [C03_BinarySearchTreeToGreaterSumTree.java](09-Trees-BST/C03_BinarySearchTreeToGreaterSumTree.java) | Binary Search Tree to Greater Sum Tree | LeetCode 1038 / Medium | The traversal order IS the algorithm. |
| [C04_BSTtoDLLInPlace.java](09-Trees-BST/C04_BSTtoDLLInPlace.java) | BST to Sorted Doubly Linked List (in place) | LeetCode 426 variant (non-circular) / Medium | "In-order + prev pointer" is the reusable skeleton (same as GetMinimumDifference); here the action performed on (prev, current) is pointer surgery instead of a comparison. |
| [C05_DeleteNodeInBST.java](09-Trees-BST/C05_DeleteNodeInBST.java) | Delete Node in a BST | LeetCode 450 / Medium | - Successor (min of right) vs predecessor (max of left): either is correct; always picking one side is what slowly unbalances a textbook BST. |
| [C06_ConstructBinarySearchTreefromPreorderTraversal.java](09-Trees-BST/C06_ConstructBinarySearchTreefromPreorderTraversal.java) | Construct BST from Preorder Traversal | LeetCode 1008 / Medium | This is ValidateBST run backwards. |
| [C07_ConvertSortedListToBST.java](09-Trees-BST/C07_ConvertSortedListToBST.java) | Convert Sorted List to Binary Search Tree | LeetCode 109 / Medium | Middle of a sorted sequence as root gives balance for free, because each half is at most half the size. |
| [C08_CountGoodNodes.java](09-Trees-BST/C08_CountGoodNodes.java) | Count Good Nodes in Binary Tree | LeetCode 1448 / Medium | This is the mirror image of the Diameter pattern. |
| [C09_MaximumDifferenceBetweenNodeAndAncestor.java](09-Trees-BST/C09_MaximumDifferenceBetweenNodeAndAncestor.java) | Maximum Difference Between Node and Ancestor | LeetCode 1026 / Medium | Count Good Nodes (C08_CountGoodNodes) carries one value down; this carries two, and the cost is the same. |
| [C10_PathSumII.java](09-Trees-BST/C10_PathSumII.java) * | Path Sum II | LeetCode 113 / Medium | Add - recurse - remove is the whole technique behind every "return all paths / combinations / subsets" question. |
| [C11_LowestCommonAncestorBST.java](09-Trees-BST/C11_LowestCommonAncestorBST.java) * | Lowest Common Ancestor of a BST | LeetCode 235 / Medium | In a BST "both are in the left subtree" is decided by comparing values, not by searching. |
| [C12_LCA.java](09-Trees-BST/C12_LCA.java) * | Lowest Common Ancestor of a Binary Tree | LeetCode 236 / Medium | The return value is deliberately overloaded: it means "p or q" low in the tree and "the LCA" once the split has happened - and the code never has to tell the two apart, because once a node sees two non-null children it c |
| [C13_LowestCommonAncestorOfABinaryTreeII.java](09-Trees-BST/C13_LowestCommonAncestorOfABinaryTreeII.java) | Lowest Common Ancestor of a Binary Tree II | LeetCode 1644 / Medium | LC236 is only correct because it ASSUMES both nodes exist: it returns early the moment it sees p, never checking whether q is really below. |
| [C14_LowestCommonAncestorIV.java](09-Trees-BST/C14_LowestCommonAncestorIV.java) | Lowest Common Ancestor of a Binary Tree IV | LeetCode 1676 / Medium | The LC236 recursion never actually used "two": it asks "does my left subtree contain a target? |
| [C15_LowestCommonAncestorOfaBinaryTreeIII.java](09-Trees-BST/C15_LowestCommonAncestorOfaBinaryTreeIII.java) | Lowest Common Ancestor of a Binary Tree III | LeetCode 1650 / Medium | - Compare by identity (==), never by value. |
| [C16_SmallestCommonRegion.java](09-Trees-BST/C16_SmallestCommonRegion.java) | Smallest Common Region | LeetCode 1257 / Medium | There is no tree object here at all - only names. |
| [C17_MaxLevelSum.java](09-Trees-BST/C17_MaxLevelSum.java) | Maximum Level Sum of a Binary Tree | LeetCode 1161 / Medium | The level-size snapshot idiom (int n = queue.size() before the inner loop) is what turns a plain BFS into a "per level" BFS. |
| [C18_RightView.java](09-Trees-BST/C18_RightView.java) * | Binary Tree Right Side View | LeetCode 199 / Medium | Two different engines, one idea: pick exactly one node per depth. |
| [C19_BinaryTree.java](09-Trees-BST/C19_BinaryTree.java) * | All Nodes Distance K in Binary Tree | LeetCode 863 / Medium | A binary tree is a graph the moment you add parent edges, and "distance k" on a graph is plain BFS. |
| [C20_BinaryTreeZigzagTraversal.java](09-Trees-BST/C20_BinaryTreeZigzagTraversal.java) | Binary Tree Zigzag Level Order Traversal | LeetCode 103 / Medium | The traversal does NOT change direction. |
| [C21_BottomViewBinaryTree.java](09-Trees-BST/C21_BottomViewBinaryTree.java) | Bottom View of a Binary Tree | GfG / Medium | Adding one coordinate - the horizontal distance - turns a tree question into a grouping question: "give me one value per column". |
| [C22_ReverseOddLevelsOfBinaryTree.java](09-Trees-BST/C22_ReverseOddLevelsOfBinaryTree.java) | Reverse Odd Levels of Binary Tree | LeetCode 2415 / Medium | You do not need BFS to do a per-level operation. |
| [C23_ConstructBinaryTreeFromINPre.java](09-Trees-BST/C23_ConstructBinaryTreeFromINPre.java) * | Construct Binary Tree from Preorder and Inorder | LeetCode 105 / Medium | Preorder tells you WHO the root is, inorder tells you WHERE the split is. |
| [C24_BinaryTreeFromString.java](09-Trees-BST/C24_BinaryTreeFromString.java) | Construct Binary Tree from String | LeetCode 536 / Medium | The grammar is self-similar, so the parser is just the grammar rule written as a method: read a token, then recurse for each bracketed group. |
| [C25_BinaryTreeToLinkedList.java](09-Trees-BST/C25_BinaryTreeToLinkedList.java) | Flatten Binary Tree to Linked List | LeetCode 114 / Medium | The caller needs something the natural return value does not give it: the LAST node of the flattened subtree, so it can splice the next chunk on. |
| [C26_FindLeavesOfBinaryTree.java](09-Trees-BST/C26_FindLeavesOfBinaryTree.java) | Find Leaves of Binary Tree | LeetCode 366 / Medium | Do not simulate the peeling. |
| [C27_DeleteNodesAndReturnForest.java](09-Trees-BST/C27_DeleteNodesAndReturnForest.java) | Delete Nodes And Return Forest | LeetCode 1110 / Medium | Two directions of information meet in one traversal: "is my parent gone?" flows DOWN as a parameter, and "did I survive?" flows UP as the return value. |
| [C28_AddRowToTree.java](09-Trees-BST/C28_AddRowToTree.java) | Add One Row to Tree | LeetCode 623 / Medium | The work happens at depth d-1, not at depth d, because only a parent can re-point a child reference. |
| [C29_CountCompleteTreeNodes.java](09-Trees-BST/C29_CountCompleteTreeNodes.java) | Count Complete Tree Nodes | LeetCode 222 / Medium | Comparing the leftmost and rightmost depths is a two-pointer test for "is this subtree perfect", and a perfect subtree is counted by arithmetic instead of traversal. |
| [D01_BinaryTreeMaxPathSum.java](09-Trees-BST/D01_BinaryTreeMaxPathSum.java) * | Binary Tree Maximum Path Sum | LeetCode 124 / Hard | What you return up is not what you are answering: you return one arm so the parent can build its own path, and you record two arms joined in a side field. |
| [D02_SerializeAndDeserialiseBinaryTree.java](09-Trees-BST/D02_SerializeAndDeserialiseBinaryTree.java) * | Serialize and Deserialize Binary Tree | LeetCode 297 / Hard | A single traversal (preorder OR level order) is enough to rebuild a tree as long as every null child is recorded. |

**Worth adding next:**

- Path Sum III (count all downward paths summing to target, LC 437): Running root-path prefix sum in a HashMap<sum, count>, incremented on the way down and decremented on the way back up (backtracking on the map itself)
- Populating Next Right Pointers in Each Node II (LC 117): Level linking in O(1) extra space: walk the current level using the next pointers already built, stitching the child level with a dummy head and a tail pointer
- Binary Search Tree Iterator (LC 173): Controlled/paused in-order traversal as an object: a stack holding the left spine, next() popping and pushing the right child's left spine, amortized O(1) time in O(h) space

## 10-Trie

Prefix trees for word search, suggestions and distinct substrings.

**Do these first:** [A01_Trie.java](10-Trie/A01_Trie.java), [C02_DesignAddAndSearchWordsDataStructure.java](10-Trie/C02_DesignAddAndSearchWordsDataStructure.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Trie.java](10-Trie/A01_Trie.java) * | Implement Trie (Prefix Tree) | LeetCode 208 / Medium | search and startsWith are the SAME traversal; the only difference is the last line (isEnd() vs true). |
| [C01_LongestWordWithAllPrefixesORCompleteStringFinderTrie.java](10-Trie/C01_LongestWordWithAllPrefixesORCompleteStringFinderTrie.java) | Longest Word With All Prefixes (Complete String) | LeetCode 1858 / Medium | This is startsWith() with one extra assertion moved inside the loop. |
| [C02_DesignAddAndSearchWordsDataStructure.java](10-Trie/C02_DesignAddAndSearchWordsDataStructure.java) * | Design Add and Search Words Data Structure | LeetCode 211 / Medium | Exact trie search is a loop because each character leaves you exactly one choice. |
| [C03_SearchSuggestionsSystem.java](10-Trie/C03_SearchSuggestionsSystem.java) | Search Suggestions System | LeetCode 1268 / Medium | Sorting turns "all strings with this prefix" into a contiguous range, and because prefixes only grow, the left edge of that range only moves right - one pass, not a fresh scan per keystroke. |
| [D01_CountDistinctSubstringsUsingTrie.java](10-Trie/D01_CountDistinctSubstringsUsingTrie.java) | Count Distinct Substrings Using a Trie | Coding Ninjas / Hard | Every substring of s is a PREFIX of some suffix of s. |

**Worth adding next:**

- Word Search II (LC 212): Build a trie of the dictionary, then DFS the grid once carrying a trie node alongside the (r,c) cursor; prune the moment the child link is null, and delete/null-out leaf nodes after a word is found to shrink the trie as you go.
- Maximum XOR of Two Numbers in an Array (LC 421): Binary trie: 32-level, 2-slot children, insert each number MSB-first, then for each number greedily walk toward the opposite bit at every level to maximise XOR. O(32n) instead of O(n^2).
- Design Search Autocomplete System (LC 642): Trie where each node stores a map of sentence -> hot-degree (or a bounded top-3 heap), streaming input character by character, '#' terminating and inserting/incrementing the new sentence, ties broken by ASCII order.

## 11-Graphs

BFS, DFS, topological sort, union-find, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, SCC.

**Do these first:** [A01_BFSandDFS.java](11-Graphs/A01_BFSandDFS.java), [A03_CycleCheckInDirectedGraph.java](11-Graphs/A03_CycleCheckInDirectedGraph.java), [A05_DisjointSets.java](11-Graphs/A05_DisjointSets.java), [A06_DijkstrasAlgoPQ.java](11-Graphs/A06_DijkstrasAlgoPQ.java), [B02_NumberOfIslands.java](11-Graphs/B02_NumberOfIslands.java), [B06_RottenOranges.java](11-Graphs/B06_RottenOranges.java), [B10_CourseSchedule.java](11-Graphs/B10_CourseSchedule.java), [C01_AllNodesDistanceKInBinaryTree.java](11-Graphs/C01_AllNodesDistanceKInBinaryTree.java), [C09_AccountsMerge.java](11-Graphs/C09_AccountsMerge.java), [C12_CheapestFlight.java](11-Graphs/C12_CheapestFlight.java), [D01_WordLadder.java](11-Graphs/D01_WordLadder.java), [D02_AlienDictionaryOrder.java](11-Graphs/D02_AlienDictionaryOrder.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_BFSandDFS.java](11-Graphs/A01_BFSandDFS.java) * | BFS and DFS traversal of a graph | GFG / Easy | The only difference between the two is the container: a FIFO queue gives level order (BFS), the call stack gives depth first (DFS). |
| [A02_CheckForCycleInUnDirected.java](11-Graphs/A02_CheckForCycleInUnDirected.java) | Detect Cycle in an Undirected Graph | GFG classic / Medium | Undirected cycle detection = "visited neighbour that is not my parent". |
| [A03_CycleCheckInDirectedGraph.java](11-Graphs/A03_CycleCheckInDirectedGraph.java) * | Detect Cycle in a Directed Graph | GFG classic / Medium | "Already visited" is NOT a cycle in a directed graph - a diamond 0->1->3, 0->2->3 revisits 3 with no cycle anywhere. |
| [A04_ToposortDFS.java](11-Graphs/A04_ToposortDFS.java) | Topological Sort of a DAG | GFG classic / Medium | Two ways to say "emit a vertex only once nothing forces it later". |
| [A05_DisjointSets.java](11-Graphs/A05_DisjointSets.java) * | Disjoint Set Union (Union-Find) | building block / Medium | - Arrays are sized n+1 so both 0-based and 1-based problems work unchanged. |
| [A06_DijkstrasAlgoPQ.java](11-Graphs/A06_DijkstrasAlgoPQ.java) * | Dijkstra's Shortest Path | GFG / LC 743 style / Medium | Dijkstra is BFS where the queue is replaced by a min-heap keyed on distance so far. |
| [A07_BellmanFord.java](11-Graphs/A07_BellmanFord.java) | Bellman-Ford: Shortest Path With Negative Edges | GfG / Medium | Dijkstra freezes a node the moment it leaves the heap, which a later negative edge could invalidate - so it is simply wrong on negative weights. |
| [A08_FloydWarshallAlgorithm.java](11-Graphs/A08_FloydWarshallAlgorithm.java) | Floyd-Warshall: All-Pairs Shortest Path | GfG / Medium | The whole algorithm is one DP question asked n times: "does routing through node k beat what I already have?" Because k is the outer loop, every pair is re-checked once per waypoint, so nothing is missed even with negati |
| [A09_PrimsAlgo.java](11-Graphs/A09_PrimsAlgo.java) | Prim's Algorithm: Minimum Spanning Tree | GfG / Medium | It is the Dijkstra loop with one word changed: Dijkstra keys the heap by dist[u] + w (cost of the whole path from the source), Prim keys it by w alone (cost of this one edge). |
| [A10_KruskalAlgorithm.java](11-Graphs/A10_KruskalAlgorithm.java) | Kruskal's Algorithm: MST With Union-Find | GfG / Medium | "Would this edge create a cycle?" is the only question Kruskal asks, and DSU answers it in near-constant time - that is why the two are always taught together. |
| [A11_KosarajusAlgorithm.java](11-Graphs/A11_KosarajusAlgorithm.java) | Kosaraju: Strongly Connected Components | GfG / Hard | Reversing the edges keeps every SCC intact (a reversed cycle is the same cycle) but destroys every one-way bridge BETWEEN components. |
| [A12_BridgesInGraph.java](11-Graphs/A12_BridgesInGraph.java) | Bridges in a Graph (Critical Connections) | LeetCode 1192 / Hard | A tree edge u-v is a bridge exactly when v's subtree has no back edge climbing to u or higher. |
| [A13_ArticulationPointInGraph.java](11-Graphs/A13_ArticulationPointInGraph.java) | Articulation Points (Cut Vertices) | GfG / Hard | Same tin/low machinery as bridges, with two differences worth reciting: the comparison is >= instead of > (the child may reach u itself and u is still the only way past), and the DFS root needs the two-children rule beca |
| [B01_FloodFill.java](11-Graphs/B01_FloodFill.java) | Flood Fill | LeetCode 733 / Easy | A grid is a graph in disguise: each cell is a node and the 4 deltas are its edges. |
| [B02_NumberOfIslands.java](11-Graphs/B02_NumberOfIslands.java) * | Number of Islands | LeetCode 200 / Medium | "Count the components" = outer scan that starts one traversal per unvisited source, plus an inner traversal that consumes the whole component. |
| [B03_NumberOfProvinces.java](11-Graphs/B03_NumberOfProvinces.java) | Number of Provinces | LeetCode 547 / Medium | This is Number of Islands with the grid replaced by an explicit graph - the same "outer loop counts, inner traversal marks" skeleton. |
| [B04_KeysAndRooms.java](11-Graphs/B04_KeysAndRooms.java) | Keys and Rooms | LeetCode 841 / Medium | Nothing here needs a second traversal or a counter reset: a single DFS from the source paints exactly the reachable set, and the answer is just "did the paint cover everything". |
| [B05_NumberOfEnclaves.java](11-Graphs/B05_NumberOfEnclaves.java) | Number of Enclaves | LeetCode 1020 / Medium | Do not search from each land cell to see if it escapes; that repeats work and needs per-island bookkeeping. |
| [B06_RottenOranges.java](11-Graphs/B06_RottenOranges.java) * | Rotting Oranges | LeetCode 994 / Medium | Seeding the queue with every source at once makes ordinary BFS compute all sources' shortest distances simultaneously - it behaves as if a virtual super-source sat one step behind all of them. |
| [B07_WallsAndGates.java](11-Graphs/B07_WallsAndGates.java) | Walls and Gates | LeetCode 286 / Medium | The first time BFS reaches a room, it arrives along a shortest path from the closest gate - so the first value written is already the minimum and never needs updating. |
| [B08_ShortestPathInBinaryMatrix.java](11-Graphs/B08_ShortestPathInBinaryMatrix.java) | Shortest Path in Binary Matrix | LeetCode 1091 / Medium | Mark visited when you ENQUEUE, not when you dequeue. |
| [B09_IsBipartite.java](11-Graphs/B09_IsBipartite.java) | Is Graph Bipartite? | LeetCode 785 / Medium | Bipartite is exactly "no odd-length cycle". |
| [B10_CourseSchedule.java](11-Graphs/B10_CourseSchedule.java) * | Course Schedule | LeetCode 207 / Medium | "Can all tasks be scheduled?" is the same question as "is this directed graph acyclic?", and Kahn's answers it by counting: exactly the vertices that are NOT on (or downstream of) a cycle ever get dequeued. |
| [C01_AllNodesDistanceKInBinaryTree.java](11-Graphs/C01_AllNodesDistanceKInBinaryTree.java) * | All Nodes Distance K in Binary Tree | LeetCode 863 / Medium | A tree only stores downward edges, which is why "distance k" looks hard: the answer can be above the target. |
| [C02_ShortestBridge.java](11-Graphs/C02_ShortestBridge.java) | Shortest Bridge | LeetCode 934 / Medium | Two traversals with two different jobs: DFS answers "which cells are one island" (connectivity), BFS answers "how far away is the other one" (shortest distance). |
| [C03_OpenTheLock.java](11-Graphs/C03_OpenTheLock.java) | Open the Lock | LeetCode 752 / Medium | BFS does not need an adjacency list. |
| [C04_MinimumMultiplications.java](11-Graphs/C04_MinimumMultiplications.java) | Minimum Multiplications to Reach End | GFG / Striver / Medium | "Mod 100000" is not an arithmetic detail, it is the bound that makes the state space finite: there are only 100000 distinct values, so the search must terminate. |
| [C05_Celebrity.java](11-Graphs/C05_Celebrity.java) | Find the Celebrity | LeetCode 277 / Medium | One knows() question always kills exactly one person, which is what turns an O(n^2) scan into O(n). |
| [C06_EventualSafeNodesByDFS.java](11-Graphs/C06_EventualSafeNodesByDFS.java) | Find Eventual Safe States | LeetCode 802 / Medium | Safe is the exact complement of "on a cycle or able to reach one", so the cycle detector you already own answers this with one extra array. |
| [C07_ParallelCourses.java](11-Graphs/C07_ParallelCourses.java) | Parallel Courses | LeetCode 1136 / Medium | The only change from plain Kahn's is freezing the queue size before draining it: that snapshot is exactly one semester's worth of courses, so the loop count is the answer. |
| [C08_NumberOfOperationsToMakeNetworkConnected.java](11-Graphs/C08_NumberOfOperationsToMakeNetworkConnected.java) | Number of Operations to Make Network Connected | LeetCode 1319 / Medium | You never have to work out WHICH cable to move. |
| [C09_AccountsMerge.java](11-Graphs/C09_AccountsMerge.java) * | Accounts Merge | LeetCode 721 / Medium | DSU needs integer nodes, and the entities here are strings. |
| [C10_MostStonesRemovedWithSameRowOrColumn.java](11-Graphs/C10_MostStonesRemovedWithSameRowOrColumn.java) | Most Stones Removed with Same Row or Column | LeetCode 947 / Medium | The removal order never matters: a connected group always collapses to exactly one survivor, so "maximise removals" is just "count the groups". |
| [C11_ShortestPath.java](11-Graphs/C11_ShortestPath.java) | Shortest Path in a Weighted Undirected Graph | GFG / Medium | The path costs nothing extra to recover: one int per node, written at the exact moment a shorter route is found, is enough to replay the whole route backwards. |
| [C12_CheapestFlight.java](11-Graphs/C12_CheapestFlight.java) * | Cheapest Flights Within K Stops | LeetCode 787 / Medium | Plain Dijkstra is unsafe here: it finalises a city the first time it is popped, but the cheapest way into a city may use too many flights while a pricier way stays inside the budget. |
| [C13_FindTheCity.java](11-Graphs/C13_FindTheCity.java) | Find the City With the Smallest Number of Neighbors | LeetCode 1334 / Medium | Floyd-Warshall's loop order is the whole algorithm: after the k-th outer pass, dist[i][j] is the best route allowed to pass only through cities 0..k. |
| [C14_NumberOfWaysToArriveAtDestination.java](11-Graphs/C14_NumberOfWaysToArriveAtDestination.java) | Number of Ways to Arrive at Destination | LeetCode 1976 / Medium | The counts are correct only because weights are strictly positive. |
| [C15_GridTeleportationTraversal.java](11-Graphs/C15_GridTeleportationTraversal.java) | Grid Teleportation Traversal | LeetCode 3552 / Medium | A deque is a two-bucket priority queue. |
| [D01_WordLadder.java](11-Graphs/D01_WordLadder.java) * | Word Ladder | LeetCode 127 / Hard | The graph is never built. |
| [D02_AlienDictionaryOrder.java](11-Graphs/D02_AlienDictionaryOrder.java) * | Alien Dictionary (order of letters) | LeetCode 269 / Hard | The sort is the easy half; the interview is testing whether you can BUILD the graph. |
| [D03_MakingALargeIsland.java](11-Graphs/D03_MakingALargeIsland.java) | Making A Large Island | LeetCode 827 / Hard | - Roots go into a HashSet. |
| [D04_FindAllPeopleWithSecret.java](11-Graphs/D04_FindAllPeopleWithSecret.java) | Find All People With the Secret | LeetCode 2092 / Hard | Union-find has no notion of time, so time is imposed from outside in two moves: process timestamps in order, and undo any merge that did not end up touching person 0. |
| [D05_MinimumTimeToVisitCell.java](11-Graphs/D05_MinimumTimeToVisitCell.java) | Minimum Time to Visit a Cell in a Grid | LeetCode 2577 / Hard | Waiting is free but only in steps of 2, because the only way to idle is to walk to a neighbour and back. |
| [D06_LastDayToCrossPQ.java](11-Graphs/D06_LastDayToCrossPQ.java) | Last Day Where You Can Still Cross | LeetCode 1970 / Hard | Swap "sum of weights" for "min of weights" and Dijkstra still works, because min is also monotone: extending a path can never raise its bottleneck. |
| [D07_ReconstructItinerary.java](11-Graphs/D07_ReconstructItinerary.java) | Reconstruct Itinerary | LeetCode 332 / Hard | Greedy-forward fails because one branch can be a dead end. |
| [D08_MinimumEdgeReversals.java](11-Graphs/D08_MinimumEdgeReversals.java) | Minimum Edge Reversals So Every Node Is Reachable | LeetCode 2858 / Hard | Moving the root by one edge changes the answer by exactly +/-1, because that single edge is the only one whose "points away from the root" status flips. |
| [D09_HtmlParser.java](11-Graphs/D09_HtmlParser.java) | Web Crawler Multithreaded | LeetCode 1242 / Hard | The BFS is the easy half; the interview is really about when to STOP. |

**Worth adding next:**

- Clone Graph (LC 133): DFS or BFS over an adjacency-object graph with a HashMap<Node,Node> old->new memo, cloning before recursing to survive cycles
- Path with Minimum Effort (LC 1631), and its twin Swim in Rising Water (LC 778): Bottleneck / minimax path: Dijkstra where the relaxation is max(dist[u], w) instead of dist[u]+w; alternates are binary search on the answer + BFS feasibility, or sort edges and union until source and sink connect
- Evaluate Division (LC 399): Build a bidirectional weighted graph a->b = k, b->a = 1/k, then DFS/BFS accumulating the product; the stronger answer is weighted union-find storing the ratio to parent and compressing it

## 12-Dynamic-Programming

1-D and 2-D DP, knapsack family, LCS and LIS, partition and matrix-chain problems.

**Do these first:** [A01_Fibonacci.java](12-Dynamic-Programming/A01_Fibonacci.java), [A02_ClimbingStairs.java](12-Dynamic-Programming/A02_ClimbingStairs.java), [A05_Knapsack01.java](12-Dynamic-Programming/A05_Knapsack01.java), [B01_UniquePaths.java](12-Dynamic-Programming/B01_UniquePaths.java), [B03_HouseRobber.java](12-Dynamic-Programming/B03_HouseRobber.java), [C02_MaxProductSubArray.java](12-Dynamic-Programming/C02_MaxProductSubArray.java), [C08_CoinChangeMinimum.java](12-Dynamic-Programming/C08_CoinChangeMinimum.java), [C11_EditDistance.java](12-Dynamic-Programming/C11_EditDistance.java), [C12_LongestPalindrome.java](12-Dynamic-Programming/C12_LongestPalindrome.java), [C16_MCM.java](12-Dynamic-Programming/C16_MCM.java), [D05_JobSchedulingMaxProfit.java](12-Dynamic-Programming/D05_JobSchedulingMaxProfit.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_Fibonacci.java](12-Dynamic-Programming/A01_Fibonacci.java) * | Fibonacci Number | LeetCode 509 / Easy | The naive recursion is slow not because recursion is slow but because it recomputes the SAME state over and over: the subproblems overlap. |
| [A02_ClimbingStairs.java](12-Dynamic-Programming/A02_ClimbingStairs.java) * | Climbing Stairs | LeetCode 70 / Easy | Count by the LAST move, not the first. |
| [A03_CountSubsequenceWithTargetSum.java](12-Dynamic-Programming/A03_CountSubsequenceWithTargetSum.java) | Count Subsequences With Target Sum | Classic / Easy | Every subsequence problem is the same two-way branch - take this element or do not - and the only design choice is what you carry in the state. |
| [A04_SubsetSumEqualsToTarget.java](12-Dynamic-Programming/A04_SubsetSumEqualsToTarget.java) | Subset Sum Equals Target | Classic / Easy | This is A03_CountSubsequenceWithTargetSum with the transition swapped: counting used +, deciding uses OR. |
| [A05_Knapsack01.java](12-Dynamic-Programming/A05_Knapsack01.java) * | 0/1 Knapsack | Classic / Medium | Greedy by value-per-weight is wrong for 0/1 knapsack because a locally dense item can block a better pair - the second example above is exactly that trap, and it is the reason the problem needs DP at all. |
| [A06_RodCuttingProblem.java](12-Dynamic-Programming/A06_RodCuttingProblem.java) | Rod Cutting Problem | Classic DP / Medium | 0/1 knapsack and unbounded knapsack share one skeleton; the only edit is the index in the TAKE branch. |
| [A07_CommonSubSequence.java](12-Dynamic-Programming/A07_CommonSubSequence.java) | Longest Common Subsequence | LeetCode 1143 / Medium | On a match the two characters are consumed together and there is never a reason to pair them with anything else; on a mismatch at least one of the two characters is useless, so try dropping each. |
| [A08_LongestIncreasingSubsequence.java](12-Dynamic-Programming/A08_LongestIncreasingSubsequence.java) | Longest Increasing Subsequence | LeetCode 300 / Medium | The DP state must be "LIS ENDING at i", not "LIS in the first i elements". |
| [B01_UniquePaths.java](12-Dynamic-Programming/B01_UniquePaths.java) * | Unique Paths | LeetCode 62 / Medium | Count paths by their LAST move. |
| [B02_OptimalPath.java](12-Dynamic-Programming/B02_OptimalPath.java) | Optimal Path - collect the most rocks | Grid DP / Medium | Fix the direction of travel, then sweep in the order that guarantees every predecessor is already final. |
| [B03_HouseRobber.java](12-Dynamic-Programming/B03_HouseRobber.java) * | House Robber | LeetCode 198 / Medium | "Take or skip, and taking forbids the previous index" is the whole pattern. |
| [C01_NumDecodings.java](12-Dynamic-Programming/C01_NumDecodings.java) | Decode Ways | LeetCode 91 / Medium | It is Climbing Stairs with two gates on the steps: step of size 1 is only allowed when the digit is not '0', step of size 2 only when the pair is 10-26. |
| [C02_MaxProductSubArray.java](12-Dynamic-Programming/C02_MaxProductSubArray.java) * | Maximum Product Subarray | LeetCode 152 / Medium | Plain Kadane fails because "largest so far" is not enough state: the most negative product is a candidate for the largest one the moment a negative arrives. |
| [C03_MaximumSubarraySumWithOneDeletion.java](12-Dynamic-Programming/C03_MaximumSubarraySumWithOneDeletion.java) | Maximum Subarray Sum With One Deletion | LeetCode 1186 / Medium | "At most one X allowed" becomes a second parallel Kadane lane: one lane for the budget unspent, one for the budget spent, with the only crossing being the step where you spend it. |
| [C04_MinimumFallingPathSum.java](12-Dynamic-Programming/C04_MinimumFallingPathSum.java) | Minimum Falling Path Sum | LeetCode 931 / Medium | This is the first grid problem where a cell has three predecessors instead of two and the answer is a min over an entire edge of the grid. |
| [C05_Triangle.java](12-Dynamic-Programming/C05_Triangle.java) | Triangle (minimum top-to-bottom path sum) | LeetCode 120 / Medium | Going bottom-up removes the boundary handling entirely: top-down would need to ask "does the parent cell exist?" at both edges of every row, while bottom-up only ever reads dp[col] and dp[col + 1], which are always insid |
| [C06_CountSquareSubmatricesWithAllOnes.java](12-Dynamic-Programming/C06_CountSquareSubmatricesWithAllOnes.java) | Count Square Submatrices With All Ones | LeetCode 1277 / Medium | Summing dp is the whole trick: you never enumerate squares, you count corners. |
| [C07_TargetSumCountWays.java](12-Dynamic-Programming/C07_TargetSumCountWays.java) | Target Sum (count +/- assignments) | LeetCode 494 / Medium | Sign problems are subset problems in disguise. |
| [C08_CoinChangeMinimum.java](12-Dynamic-Programming/C08_CoinChangeMinimum.java) * | Coin Change (fewest coins) | LeetCode 322 / Medium | Staying at the same index after taking an item is what makes a knapsack unbounded - that single character (ind instead of ind - 1) separates this from 0/1 knapsack and from rod cutting it separates nothing at all, they a |
| [C09_CoinChangeII.java](12-Dynamic-Programming/C09_CoinChangeII.java) | Coin Change II (count the ways) | LeetCode 518 / Medium | Compare this with C08_CoinChangeMinimum line by line: identical recursion, with min() swapped for +. |
| [C10_LongestPalindromicSubsequence.java](12-Dynamic-Programming/C10_LongestPalindromicSubsequence.java) | Longest Palindromic Subsequence | LeetCode 516 / Medium | A palindromic subsequence of s is exactly a common subsequence of s and its reverse, so LPS(s) == LCS(s, reverse(s)). |
| [C11_EditDistance.java](12-Dynamic-Programming/C11_EditDistance.java) * | Edit Distance (Levenshtein) | LeetCode 72 / Medium | It is the LCS table with a different transition. |
| [C12_LongestPalindrome.java](12-Dynamic-Programming/C12_LongestPalindrome.java) * | Longest Palindromic Substring | LeetCode 5 / Medium | Manacher's is expand-around-centre with the redundant comparisons deleted. |
| [C13_LongestStringChain.java](12-Dynamic-Programming/C13_LongestStringChain.java) | Longest String Chain | LeetCode 1048 / Medium | This is Longest Increasing Subsequence where "comes before" means "is a predecessor string". |
| [C14_LengthOfLongestFibonacciSubsequence.java](12-Dynamic-Programming/C14_LengthOfLongestFibonacciSubsequence.java) | Length of Longest Fibonacci Subsequence | LeetCode 873 / Medium | Two consecutive terms determine the entire rest of the sequence, so the state is a PAIR of indices, not one index. |
| [C15_PalindromePartitioning.java](12-Dynamic-Programming/C15_PalindromePartitioning.java) | Palindrome Partitioning | LeetCode 131 / Medium | "Cut the string at every valid position and recurse on the rest" is the front-partition pattern. |
| [C16_MCM.java](12-Dynamic-Programming/C16_MCM.java) * | Matrix Chain Multiplication | GFG / Hard | Do not try to decide the FIRST multiplication. |
| [D01_MinimumCostToCutTheStick.java](12-Dynamic-Programming/D01_MinimumCostToCutTheStick.java) | Minimum Cost to Cut a Stick | LeetCode 1547 / Hard | Two ideas stacked. |
| [D02_CountOfDistinctSubsequences.java](12-Dynamic-Programming/D02_CountOfDistinctSubsequences.java) | Distinct Subsequences | LeetCode 115 / Hard | This is the LCS grid with the transition changed from max to SUM. |
| [D03_RegularExpressionMatchingMemo.java](12-Dynamic-Programming/D03_RegularExpressionMatchingMemo.java) | Regular Expression Matching | LeetCode 10 / Hard | Always look AHEAD for the '*', never at the current pattern character. |
| [D04_CountPalindromicSubsequences.java](12-Dynamic-Programming/D04_CountPalindromicSubsequences.java) | Count Palindromic Subsequences | LeetCode 2484 / Hard | The modelling leap is refusing to make the state a range. |
| [D05_JobSchedulingMaxProfit.java](12-Dynamic-Programming/D05_JobSchedulingMaxProfit.java) * | Maximum Profit in Job Scheduling | LeetCode 1235 / Hard | The DP only becomes possible after the right ordering. |
| [D06_TallestBillboard.java](12-Dynamic-Programming/D06_TallestBillboard.java) | Tallest Billboard | LeetCode 956 / Hard | Do not track the two sums; track their DIFFERENCE and the height of the shorter side. |
| [D07_StickersToSpellWord.java](12-Dynamic-Programming/D07_StickersToSpellWord.java) | Stickers to Spell Word | LeetCode 691 / Hard | When the "index" of a classic DP has no meaning, invent a state out of the multiset of what remains and hash it. |
| [D08_CherryPickII.java](12-Dynamic-Programming/D08_CherryPickII.java) | Cherry Pickup II | LeetCode 1463 / Hard | Two agents do NOT need two independent DPs. |
| [D09_CherryPickup.java](12-Dynamic-Programming/D09_CherryPickup.java) | Cherry Pickup | LeetCode 741 / Hard | A there-and-back trip is two forward trips. |

**Worth adding next:**

- Word Break (LC 139): Front-partition DP over string prefixes: dp[i] = OR over j of (dict contains s[j..i) AND dp[j]), with a HashSet or trie for lookup; O(n^2) substrings.
- Best Time to Buy and Sell Stock III / IV, plus Cooldown and Transaction Fee (LC 123 / 188 / 309 / 714): State-machine DP: dp[index][holding][transactionsLeft], then collapse to O(k) rolling variables; cooldown adds a skip-a-day transition, fee adjusts the sell edge.
- Longest Increasing Subsequence in O(n log n) (LC 300 follow-up, and Russian Doll Envelopes LC 354): Patience sorting / tails array: maintain the smallest tail for each length and binary-search the replacement position; length of tails is the answer.

## 13-Greedy

Jump games, stock trading, scheduling, local-choice proofs.

**Do these first:** [C01_BestTimeToBuyAndSellStockII.java](13-Greedy/C01_BestTimeToBuyAndSellStockII.java), [C05_PartitionLabels.java](13-Greedy/C05_PartitionLabels.java), [C06_BiggestNumber.java](13-Greedy/C06_BiggestNumber.java), [C08_CanJump.java](13-Greedy/C08_CanJump.java), [C10_GasStation.java](13-Greedy/C10_GasStation.java), [D01_Candy.java](13-Greedy/D01_Candy.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_ShortestJobFirst.java](13-Greedy/A01_ShortestJobFirst.java) | Shortest Job First: average waiting time | Classic OS problem / Easy | A job of length L placed at position k delays every one of the (n - k) jobs behind it by L. |
| [A02_JobSequencing.java](13-Greedy/A02_JobSequencing.java) | Job Sequencing With Deadlines | Classic greedy / Medium | Two independent greedy decisions stacked. |
| [B01_CanPlaceFlowers.java](13-Greedy/B01_CanPlaceFlowers.java) | Can Place Flowers | LeetCode 605 / Easy | Planting at the LEFTMOST legal plot is never worse. |
| [B02_AssignCookies.java](13-Greedy/B02_AssignCookies.java) | Assign Cookies | LeetCode 455 / Easy | Feed the least greedy child with the smallest cookie that satisfies them. |
| [B03_ClassPhotos.java](13-Greedy/B03_ClassPhotos.java) | Class Photos | AlgoExpert / Easy | Once both rows are sorted, the i-th shortest must stand in front of the i-th shortest; any other pairing only puts a taller person in front, so if sorted order fails, every order fails. |
| [B04_NonConstructibleChange.java](13-Greedy/B04_NonConstructibleChange.java) | Non-Constructible Change | AlgoExpert / Easy | Track a contiguous reachable range [1 .. |
| [B05_LemonadeChange.java](13-Greedy/B05_LemonadeChange.java) | Lemonade Change | LeetCode 860 / Easy | When a $20 arrives, both change options cost one $5, but the 10+5 option spends a bill that can ONLY ever pay a $20, while 5+5+5 burns two extra $5 bills that could each have served a future $10 customer. |
| [C01_BestTimeToBuyAndSellStockII.java](13-Greedy/C01_BestTimeToBuyAndSellStockII.java) * | Best Time to Buy and Sell Stock II | LeetCode 122 / Medium | Any hold from day b to day s earns prices[s] - prices[b], which telescopes into the sum of the daily deltas inside that window. |
| [C02_BoatsToSavePeople.java](13-Greedy/C02_BoatsToSavePeople.java) | Boats to Save People | LeetCode 881 / Medium | The heaviest person has the fewest possible partners, so decide their seat first. |
| [C03_MinimumHealthToBeatGame.java](13-Greedy/C03_MinimumHealthToBeatGame.java) | Minimum Health to Beat Game | LeetCode 2214 / Medium | Strip away the ordering: nothing here is a scheduling problem, because every level is paid for regardless of sequence. |
| [C04_MaximumSwap.java](13-Greedy/C04_MaximumSwap.java) | Maximum Swap | LeetCode 670 / Medium | Two greedy choices stack. |
| [C05_PartitionLabels.java](13-Greedy/C05_PartitionLabels.java) * | Partition Labels | LeetCode 763 / Medium | The cut is legal exactly when the scan index catches up to the farthest obligation we have accumulated. |
| [C06_BiggestNumber.java](13-Greedy/C06_BiggestNumber.java) * | Largest Number (Biggest Number) | LeetCode 179 / Medium | This is the exchange argument written as code. |
| [C07_MaxDifferenceChangingInteger.java](13-Greedy/C07_MaxDifferenceChangingInteger.java) | Max Difference You Can Get From Changing an Integer | LeetCode 1432 / Medium | The two halves never interact, so solve them separately instead of searching all 100 (x, y) pairs. |
| [C08_CanJump.java](13-Greedy/C08_CanJump.java) * | Jump Game | LeetCode 55 / Medium | You never have to decide how far to jump. |
| [C09_JumpGameII.java](13-Greedy/C09_JumpGameII.java) | Jump Game II | LeetCode 45 / Medium | You never decide WHICH index to jump to - only WHEN the current level runs out. |
| [C10_GasStation.java](13-Greedy/C10_GasStation.java) * | Gas Station | LeetCode 134 / Medium | Two independent arguments in one pass, and you must be able to state both. |
| [C11_MaximumPalindromesAfterOperations.java](13-Greedy/C11_MaximumPalindromesAfterOperations.java) | Maximum Palindromes After Operations | LeetCode 3035 / Medium | Two non-obvious steps stacked. |
| [D01_Candy.java](13-Greedy/D01_Candy.java) * | Candy | LeetCode 135 / Hard | One scan cannot do it: walking left to right you cannot know that the child ahead of you is about to start a long descent, which would force your count up retroactively. |
| [D02_MinimumNumberOfTapsToOpenToWaterAGarden.java](13-Greedy/D02_MinimumNumberOfTapsToOpenToWaterAGarden.java) | Minimum Number of Taps to Open to Water a Garden | LeetCode 1326 / Hard | The reduction is the whole problem. |
| [D03_MinimumMovesToMakePalindrome.java](13-Greedy/D03_MinimumMovesToMakePalindrome.java) | Minimum Number of Moves to Make Palindrome | LeetCode 2193 / Hard | Fix the outermost pair first and never revisit it. |
| [D04_RearrangingFruits.java](13-Greedy/D04_RearrangingFruits.java) | Rearranging Fruits | LeetCode 2561 / Hard | Two stacked ideas. |

**Worth adding next:**

- Task Scheduler (LC 621): Frequency counting plus the idle-slot formula max(n+1)*(maxFreq-1)+countOfMaxFreq, len(tasks)); heap variant for the follow-up asking for the actual schedule
- Valid Parenthesis String (LC 678): Track a range [lo, hi] of possible open-paren counts in one pass, clamping lo at 0 and failing when hi goes negative
- Hand of Straights / Divide Array in Sets of K Consecutive (LC 846 / 1296): Count map or TreeMap; the smallest remaining value must start a group, so consume k consecutive values from it and repeat

## 14-Backtracking-Recursion

Subsets, permutations, combination sum, N-Queens, word search, basic recursion.

**Do these first:** [A04_Subsets.java](14-Backtracking-Recursion/A04_Subsets.java), [A05_Permutations.java](14-Backtracking-Recursion/A05_Permutations.java), [C01_GenerateParenthesis.java](14-Backtracking-Recursion/C01_GenerateParenthesis.java), [C02_SubsetsII.java](14-Backtracking-Recursion/C02_SubsetsII.java), [C03_CombinationSum.java](14-Backtracking-Recursion/C03_CombinationSum.java), [C05_WordSearch.java](14-Backtracking-Recursion/C05_WordSearch.java), [D01_NQueens.java](14-Backtracking-Recursion/D01_NQueens.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_RecursiveFactorial.java](14-Backtracking-Recursion/A01_RecursiveFactorial.java) | Recursive Factorial | Building block / Easy | Every recursion is two things and nothing more: a base case that returns without calling itself, and a recursive case that moves strictly closer to that base case. |
| [A02_RecursiveArraySum.java](14-Backtracking-Recursion/A02_RecursiveArraySum.java) | Recursive Array Sum | Building block / Easy | The index parameter IS the loop counter, moved from the loop header into the call. |
| [A03_CombinationProgram.java](14-Backtracking-Recursion/A03_CombinationProgram.java) | Cartesian Product Of A List Of Lists | Building block / Easy | add -> recurse -> remove is the backtracking heartbeat: one mutable list is reused by the whole tree, and each branch undoes exactly what it did. |
| [A04_Subsets.java](14-Backtracking-Recursion/A04_Subsets.java) * | Subsets (Power Set) | LeetCode 78 / Medium | A subset is one yes/no decision per element, so the recursion tree is a binary tree of depth n with 2^n leaves - the count falls straight out of the shape. |
| [A05_Permutations.java](14-Backtracking-Recursion/A05_Permutations.java) * | String Permutations | LeetCode 46 (variant) / Medium | Permutations differ from subsets in what the loop means. |
| [B01_NestedListWeightSum.java](14-Backtracking-Recursion/B01_NestedListWeightSum.java) | Nested List Weight Sum | LeetCode 339 / Medium | This is the gentlest form of recursion: state flows only downward, so there is nothing to undo. |
| [B02_LetterCombinations.java](14-Backtracking-Recursion/B02_LetterCombinations.java) | Letter Combinations of a Phone Number | LeetCode 17 / Medium | The recursion depth is fixed at digits.length(): one digit consumed per level, and the branching factor is that digit's letter count. |
| [B03_WordPathFinder.java](14-Backtracking-Recursion/B03_WordPathFinder.java) | Word Path Finder (right / down only) | Easy | Because the moves only ever increase x or y, the search can never revisit a cell -- so this version needs no visited set at all. |
| [C01_GenerateParenthesis.java](14-Backtracking-Recursion/C01_GenerateParenthesis.java) * | Generate Parentheses | LeetCode 22 / Medium | The validity rule is not something you check at the end -- it is encoded in the branch conditions, so every leaf the recursion reaches is already a valid answer. |
| [C02_SubsetsII.java](14-Backtracking-Recursion/C02_SubsetsII.java) * | Subsets II | LeetCode 90 / Medium | The dedupe rule is i > start, NOT i > 0. |
| [C03_CombinationSum.java](14-Backtracking-Recursion/C03_CombinationSum.java) * | Combination Sum | LeetCode 39 / Medium | The single character that decides "reuse allowed" vs "use once" is the index passed down: i means the current element may be chosen again, i + 1 means move on. |
| [C04_CombinationSumII.java](14-Backtracking-Recursion/C04_CombinationSumII.java) | Combination Sum II | LeetCode 40 / Medium | "i > start", not "i > 0". |
| [C05_WordSearch.java](14-Backtracking-Recursion/C05_WordSearch.java) * | Word Search | LeetCode 79 / Medium | The visited set is the board itself. |
| [C06_MColoringProblem.java](14-Backtracking-Recursion/C06_MColoringProblem.java) | M-Coloring Problem | Classic backtracking / Medium | Nodes already coloured are the only constraint that matters, so isSafe never has to look ahead - it only checks the past. |
| [D01_NQueens.java](14-Backtracking-Recursion/D01_NQueens.java) * | N-Queens | LeetCode 51 / Hard | Every diagonal has a constant, so membership is an array lookup instead of a scan. |
| [D02_KthPermutation.java](14-Backtracking-Recursion/D02_KthPermutation.java) | Permutation Sequence | LeetCode 60 / Hard | Fixing the first digit fixes a contiguous BLOCK of (n-1)! |
| [D03_RemoveInvalidParentheses.java](14-Backtracking-Recursion/D03_RemoveInvalidParentheses.java) | Remove Invalid Parentheses | LeetCode 301 / Hard | "Minimum number of removals" is a shortest-path question, and BFS finds shortest paths - so the level where the first valid string appears is the whole answer set. |
| [D04_ExpressionAddOperators.java](14-Backtracking-Recursion/D04_ExpressionAddOperators.java) | Expression Add Operators | LeetCode 282 / Hard | '*' binds tighter than the operator that already consumed the previous term, so you cannot just apply it to the running total. |

**Worth adding next:**

- Permutations II (LeetCode 47) — permutations of an array containing duplicates: Duplicate control at permutation level: sort + a boolean used[] array with the skip `i>0 && nums[i]==nums[i-1] && !used[i-1]`, or a per-level HashSet. Critically, the swap-based template cannot use the sort+skip rule because swapping destroys sortedness.
- Partition to K Equal Sum Subsets (LeetCode 698): Assignment backtracking — place each number into one of k buckets, with the pruning set that makes it pass: early exit if sum % k != 0 or max > target, sort descending, skip a bucket whose running sum equals a bucket already tried, and only start a fresh bucket once per level. Bitmask memo as the follow-up.
- Word Search II (LeetCode 212) — find all dictionary words in a grid: Trie-backed grid backtracking: build a Trie of the word list, DFS the board once carrying a Trie node instead of restarting per word, null out the word at the terminal node to dedupe, and prune exhausted leaf nodes as you unwind.

## 15-Intervals

Merge, insert, meeting rooms, sweep line and difference arrays.

**Do these first:** [A02_MergeIntervals.java](15-Intervals/A02_MergeIntervals.java), [A03_MinimumPlatforms.java](15-Intervals/A03_MinimumPlatforms.java), [C01_InsertInterval.java](15-Intervals/C01_InsertInterval.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_MeetingRooms.java](15-Intervals/A01_MeetingRooms.java) | Meeting Rooms | LeetCode 252 / Easy | Once the intervals are sorted by start, an overlap can only exist between NEIGHBOURS. |
| [A02_MergeIntervals.java](15-Intervals/A02_MergeIntervals.java) * | Merge Intervals | LeetCode 56 / Medium | Sorting by start turns a global "which intervals overlap which" question into a local one: each interval only ever has to be compared with the single interval being accumulated. |
| [A03_MinimumPlatforms.java](15-Intervals/A03_MinimumPlatforms.java) * | Minimum Number of Platforms | GFG classic / Medium | The answer is the maximum number of intervals alive at the same instant, and that peak is found without ever pairing a start with its own end. |
| [B01_MaximumPopulationYear.java](15-Intervals/B01_MaximumPopulationYear.java) | Maximum Population Year | LeetCode 1854 / Easy | A person alive over [birth, death) is a +1 at birth and a -1 at death, so the population in any year is a PREFIX SUM of those deltas. |
| [C01_InsertInterval.java](15-Intervals/C01_InsertInterval.java) * | Insert Interval | LeetCode 57 / Medium | Sortedness means the intervals touching the new one form ONE contiguous run, so the list splits cleanly into three phases and each element is visited once. |
| [C02_IntervalListIntersections.java](15-Intervals/C02_IntervalListIntersections.java) | Interval List Intersections | LeetCode 986 / Medium | Intersection is max-of-starts, min-of-ends, valid only while start <= end. |
| [C03_MeetingRoomsII.java](15-Intervals/C03_MeetingRoomsII.java) | Meeting Rooms II | LeetCode 253 / Medium | This is Minimum Platforms (A03_MinimumPlatforms) wearing a different name - rooms, platforms, groups and "max overlap depth" are all the same count. |
| [C04_DivideIntervalsIntoMinGroups.java](15-Intervals/C04_DivideIntervalsIntoMinGroups.java) | Divide Intervals Into Minimum Number of Groups | LeetCode 2406 / Medium | The whole problem is a renaming. |
| [C05_CarPooling.java](15-Intervals/C05_CarPooling.java) | Car Pooling | LeetCode 1094 / Medium | This is the +1/-1 concurrency sweep from Minimum Platforms with weights attached: the "+1 per meeting" becomes "+k per trip". |
| [C06_NonOverlappingIntervals.java](15-Intervals/C06_NonOverlappingIntervals.java) | Non-overlapping Intervals | LeetCode 435 / Medium | Sort by END, not by start. |
| [C07_MinimumArrowsToBurstBalloons.java](15-Intervals/C07_MinimumArrowsToBurstBalloons.java) | Minimum Number of Arrows to Burst Balloons | LeetCode 452 / Medium | Placing the arrow at the earliest end coordinate is never worse than placing it anywhere else: any balloon a later position could hit, the earliest end also hits, because every surviving balloon must still be open at tha |
| [D01_MeetingRoomsIII.java](15-Intervals/D01_MeetingRoomsIII.java) | Meeting Rooms III | LeetCode 2402 / Hard | 1. |

**Worth adding next:**

- Employee Free Time (LC 759): Merge k sorted interval lists (heap or flatten-and-sort), then return the COMPLEMENT — the gaps between merged blocks, excluding the infinite ends.
- My Calendar I / II / III (LC 729 / 731 / 732): Online interval insertion with a balanced BST (Java TreeMap): floorKey/ceilingKey to detect overlap in O(log n) for I, a second 'double-booked' list or delta map for II, and a +1/-1 delta TreeMap with a running prefix max for III.
- Number of Flowers in Full Bloom (LC 2251), with Minimum Interval to Include Each Query (LC 1851) as the harder twin: Offline query processing: sort starts and ends into two arrays and, for each query point, binary search both (count of started minus count of ended); or sort queries and intervals together and sweep with a heap for 1851.

## 16-Matrix

Rotation, spiral and diagonal traversal, grid simulation.

**Do these first:** [A01_MatrixRotate90Degree.java](16-Matrix/A01_MatrixRotate90Degree.java), [C01_SetMatrixZeroes.java](16-Matrix/C01_SetMatrixZeroes.java), [C02_SpiralTraversalOfMatrix.java](16-Matrix/C02_SpiralTraversalOfMatrix.java), [C04_WalkingRobotSimulation.java](16-Matrix/C04_WalkingRobotSimulation.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_MatrixRotate90Degree.java](16-Matrix/A01_MatrixRotate90Degree.java) * | Rotate Image (rotate an n x n matrix in place) | LeetCode 48 / Medium | Every square-matrix rotation is built from two cheap primitives, transpose and reverse. |
| [A02_ToeplitzMatrix.java](16-Matrix/A02_ToeplitzMatrix.java) | Toeplitz Matrix | LeetCode 766 / Easy | Cells sit on the same top-left-to-bottom-right diagonal exactly when i - j is the same for both. |
| [B01_LargestInEachThreeRows.java](16-Matrix/B01_LargestInEachThreeRows.java) | Largest Value Per Column, In Each Block Of Three Rows | Easy (not a LeetCode problem) | Seed a running maximum from real data (the first element you will scan), never from a convenient literal like 0. |
| [B02_CountNegativeNumbersInASortedMatrix.java](16-Matrix/B02_CountNegativeNumbersInASortedMatrix.java) | Count Negative Numbers in a Sorted Matrix | LeetCode 1351 / Easy | In a doubly sorted matrix, start at a corner where the two sort directions DISAGREE (bottom-left or top-right). |
| [C01_SetMatrixZeroes.java](16-Matrix/C01_SetMatrixZeroes.java) * | Set Matrix Zeroes | LeetCode 73 / Medium | Record then apply. |
| [C02_SpiralTraversalOfMatrix.java](16-Matrix/C02_SpiralTraversalOfMatrix.java) * | Spiral Matrix | LeetCode 54 / Medium | A spiral is not a direction-and-turn simulation, it is four walls closing in. |
| [C03_DiagonalTraverse.java](16-Matrix/C03_DiagonalTraverse.java) | Diagonal Traverse | LeetCode 498 / Medium | The whole problem is the ORDER of the two boundary checks at a corner. |
| [C04_WalkingRobotSimulation.java](16-Matrix/C04_WalkingRobotSimulation.java) * | Walking Robot Simulation | LeetCode 874 / Medium | Two reusable pieces. |
| [D01_BestMeetingPoint.java](16-Matrix/D01_BestMeetingPoint.java) | Best Meeting Point | LeetCode 296 / Hard | The median, not the mean or the centroid, minimises the sum of absolute deviations. |

**Worth adding next:**

- Range Sum Query 2D - Immutable (LC 304): 2D prefix sum / integral image: pre[i][j] = sum of the rectangle from (0,0) to (i-1,j-1), then any submatrix sum by inclusion-exclusion in O(1).
- Game of Life (LC 289): In-place simultaneous update by encoding both the old and the new state in the same cell (2-bit trick: value 1 means was-live, bit 1 means will-be-live), then a second pass to shift. Follow-up: infinite board handled with a hash set of live coordinates.
- Valid Sudoku (LC 36): Single pass with three index families: row r, column c, and box (r/3)*3 + c/3, each tracked with a boolean array or bitmask.

## 17-Math-Bit-Manipulation

Number theory, primes, GCD and LCM, fast exponentiation, bit tricks.

**Do these first:** [A01_CheckIfTheIthBitIsSetOrNot.java](17-Math-Bit-Manipulation/A01_CheckIfTheIthBitIsSetOrNot.java), [A02_NumberOf1Bits.java](17-Math-Bit-Manipulation/A02_NumberOf1Bits.java), [A03_SingleNumber.java](17-Math-Bit-Manipulation/A03_SingleNumber.java), [A05_CheckPrime.java](17-Math-Bit-Manipulation/A05_CheckPrime.java), [A06_AllDivisors.java](17-Math-Bit-Manipulation/A06_AllDivisors.java), [A07_PrimeFactors.java](17-Math-Bit-Manipulation/A07_PrimeFactors.java), [A08_SieveOfEratosthenes.java](17-Math-Bit-Manipulation/A08_SieveOfEratosthenes.java), [A09_LCMOfTwoNumbers.java](17-Math-Bit-Manipulation/A09_LCMOfTwoNumbers.java), [A10_BinaryExponentiation.java](17-Math-Bit-Manipulation/A10_BinaryExponentiation.java), [B01_MissingNumber.java](17-Math-Bit-Manipulation/B01_MissingNumber.java), [B11_PascalTriangleTwoLoops.java](17-Math-Bit-Manipulation/B11_PascalTriangleTwoLoops.java), [B16_HappyNumber.java](17-Math-Bit-Manipulation/B16_HappyNumber.java), [C01_RepeatedNumberInFractionAfterDecimal.java](17-Math-Bit-Manipulation/C01_RepeatedNumberInFractionAfterDecimal.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_CheckIfTheIthBitIsSetOrNot.java](17-Math-Bit-Manipulation/A01_CheckIfTheIthBitIsSetOrNot.java) * | Check If The i-th Bit Is Set Or Not | Building block / Easy | Compare against != 0, never == 1. |
| [A02_NumberOf1Bits.java](17-Math-Bit-Manipulation/A02_NumberOf1Bits.java) * | Number of 1 Bits (Hamming Weight) | LeetCode 191 / Easy | Use >>> (logical shift), never >> (arithmetic shift). |
| [A03_SingleNumber.java](17-Math-Bit-Manipulation/A03_SingleNumber.java) * | Single Number | LeetCode 136 / Easy | XOR is a self-inverse operation: applying a value twice undoes it. |
| [A04_ArmstrongNumber.java](17-Math-Bit-Manipulation/A04_ArmstrongNumber.java) | Armstrong Number (Narcissistic Number) | Building block / Easy | %10 and /=10 are the decimal twin of "test the low bit, then shift". |
| [A05_CheckPrime.java](17-Math-Bit-Manipulation/A05_CheckPrime.java) * | Check Prime | Building block / Easy | Divisors come in pairs: if i divides n then so does n / i, and one member of every pair is <= sqrt(n). |
| [A06_AllDivisors.java](17-Math-Bit-Manipulation/A06_AllDivisors.java) * | All Divisors of a Number | Building block / Easy | Divisors are symmetric around sqrt(n). |
| [A07_PrimeFactors.java](17-Math-Bit-Manipulation/A07_PrimeFactors.java) * | Distinct Prime Factors | Building block / Easy | Dividing n down as you go is what makes plain trial division correct AND fast: composite candidates can never divide the shrunken n, so the first divisor you meet is always prime. |
| [A08_SieveOfEratosthenes.java](17-Math-Bit-Manipulation/A08_SieveOfEratosthenes.java) * | Sieve of Eratosthenes | Building block / Easy | Flip the question. |
| [A09_LCMOfTwoNumbers.java](17-Math-Bit-Manipulation/A09_LCMOfTwoNumbers.java) * | GCD and LCM of Two Numbers | Building block / Easy | Euclid works because any common divisor of a and b also divides a % b, so the set of common divisors never changes while the numbers shrink fast. |
| [A10_BinaryExponentiation.java](17-Math-Bit-Manipulation/A10_BinaryExponentiation.java) * | Binary Exponentiation (Fast Power) | Building block / Easy | Halving the exponent is the same as squaring the base: b^p = (b*b)^(p/2) when p is even, and b * b^(p-1) when p is odd. |
| [B01_MissingNumber.java](17-Math-Bit-Manipulation/B01_MissingNumber.java) * | Missing Number | LeetCode 268 / Easy | You do not need to find the missing number - you compute what the total should be and let the present numbers cancel themselves out. |
| [B02_MinimumBitFlipsToConvertNumber.java](17-Math-Bit-Manipulation/B02_MinimumBitFlipsToConvertNumber.java) | Minimum Bit Flips to Convert Number | LeetCode 2220 / Easy | XOR is a "difference detector" for bits, and popcount turns that difference into a count. |
| [B03_BinaryWatch.java](17-Math-Bit-Manipulation/B03_BinaryWatch.java) | Binary Watch | LeetCode 401 / Easy | Do not try to place turnedOn lit bits into 10 LED positions and then check validity; go the other way. |
| [B04_PowerOfThree.java](17-Math-Bit-Manipulation/B04_PowerOfThree.java) | Power of Three | LeetCode 326 / Easy | "Is n a power of b" is the same question as "does n factor into nothing but b". |
| [B05_PowerOfTen.java](17-Math-Bit-Manipulation/B05_PowerOfTen.java) | Power of Ten | not on LeetCode / Easy | This is the same "divide out the base until nothing is left" loop as PowerOfThree, just with base 10 - which makes the shared pattern visible before B06_PowerCheck generalises it to any base. |
| [B06_PowerCheck.java](17-Math-Bit-Manipulation/B06_PowerCheck.java) | Power Of An Arbitrary Base | no LeetCode id / Easy | "Is x a power of b" is the same question as "does x factor into nothing but b". |
| [B07_DivisibleByThree.java](17-Math-Bit-Manipulation/B07_DivisibleByThree.java) | Can These Digits Form A Multiple Of 3 | no LeetCode id / Easy | 10 leaves remainder 1 when divided by 3, so every power of 10 does too: 100 = 99 + 1, 1000 = 999 + 1, and so on. |
| [B08_ExcelSheetColumnNumber.java](17-Math-Bit-Manipulation/B08_ExcelSheetColumnNumber.java) | Excel Sheet Column Number | LeetCode 171 / Easy | This is ordinary positional number parsing in base 26, with one twist: the alphabet is 1-indexed. |
| [B09_AddTwoFractions.java](17-Math-Bit-Manipulation/B09_AddTwoFractions.java) | Add Two Fractions (lowest terms) | no LeetCode id / Easy | Fraction arithmetic is two separate steps that beginners fuse: combine, then normalise. |
| [B10_FibonacciCounter.java](17-Math-Bit-Manipulation/B10_FibonacciCounter.java) | Count Fibonacci Numbers In A Range | no LeetCode id / Easy | Fibonacci numbers grow exponentially - roughly phi^n, phi = 1.618 - so only 47 of them fit in a signed 32-bit int (F(46) = 1836311903 is the last). |
| [B11_PascalTriangleTwoLoops.java](17-Math-Bit-Manipulation/B11_PascalTriangleTwoLoops.java) * | Pascal's Triangle | LeetCode 118 / Easy | The triangle is the binomial coefficients laid out in a grid, so you have a choice: reuse the row above (simple, needs O(n) memory of history) or derive a row from nothing with the ratio C(n,k) = C(n,k-1) * (n-k+1) / k. |
| [B12_SumOfProductOfPairs.java](17-Math-Bit-Manipulation/B12_SumOfProductOfPairs.java) | Sum of Product of All Pairs | Easy | Any "sum over all pairs" question should make you try squaring the total first. |
| [B13_MeanMedianCalculator.java](17-Math-Bit-Manipulation/B13_MeanMedianCalculator.java) | Mean and Median of an Array | Easy | The median only needs the element at rank n/2, not a fully ordered array. |
| [B14_MaximumAreaOfLongestDiagonalRectangle.java](17-Math-Bit-Manipulation/B14_MaximumAreaOfLongestDiagonalRectangle.java) | Maximum Area of Longest Diagonal Rectangle | LeetCode 3000 / Easy | Never call Math.sqrt here. |
| [B15_NumberOfDays.java](17-Math-Bit-Manipulation/B15_NumberOfDays.java) | Number of Days in a Month | Easy | The leap rule is three nested exceptions, and the order matters: year % 400 == 0 is a leap year; otherwise year % 100 == 0 is NOT; otherwise year % 4 == 0 is. |
| [B16_HappyNumber.java](17-Math-Bit-Manipulation/B16_HappyNumber.java) * | Happy Number | LeetCode 202 / Easy | The sequence ALWAYS ends in a cycle, so "does it loop?" is not the question - the question is which cycle. |
| [C01_RepeatedNumberInFractionAfterDecimal.java](17-Math-Bit-Manipulation/C01_RepeatedNumberInFractionAfterDecimal.java) * | Fraction to Recurring Decimal | LeetCode 166 / Medium | Do not look for a repeated DIGIT - look for a repeated REMAINDER. |
| [C02_AngleBetweenHandsOfClock.java](17-Math-Bit-Manipulation/C02_AngleBetweenHandsOfClock.java) | Angle Between Hands of a Clock | LeetCode 1344 / Medium | The hour hand does NOT jump on the hour - it drifts 0.5 degrees every minute. |
| [C03_MinimumTimeDifference.java](17-Math-Bit-Manipulation/C03_MinimumTimeDifference.java) | Minimum Time Difference | LeetCode 539 / Medium | Sorting turns "compare all pairs" into "compare neighbours", but sorting a CIRCULAR quantity always leaves exactly one pair unchecked - the two ends. |
| [C04_ZScore.java](17-Math-Bit-Manipulation/C04_ZScore.java) | Maximum Z-Score (H-index shaped) | Medium | The predicate "z is achievable" is monotone: as z grows, the bar z*k rises while the z-th largest mark falls, so once it fails it fails forever. |
| [C05_JosephusProblem.java](17-Math-Bit-Manipulation/C05_JosephusProblem.java) | Josephus Problem | Classic (LeetCode 1823) / Medium | After one elimination the problem is the SAME problem one size smaller -- only the seat labels moved. |
| [D01_NumberOfPerfectPairs.java](17-Math-Bit-Manipulation/D01_NumberOfPerfectPairs.java) | Number of Perfect Pairs | LeetCode 3649 / Medium | The whole problem is one line of algebra: a perfect pair is just max(/a/, /b/) <= 2 * min(/a/, /b/). |

**Worth adding next:**

- Single Number II (every element appears three times except one): Count set bits per position mod 3, or the two-variable ones/twos state machine
- Single Number III (exactly two elements appear once, all others twice): XOR everything, isolate the lowest set bit with x & -x, partition the array into two buckets and XOR each
- Counting Bits (popcount for every number from 0 to n): dp[i] = dp[i >> 1] + (i & 1), or dp[i] = dp[i & (i-1)] + 1

## 18-Sorting-Searching-Algorithms

Sorting algorithm implementations, quick select, KMP, Rabin-Karp, segment tree.

**Do these first:** [A04_CyclicSort.java](18-Sorting-Searching-Algorithms/A04_CyclicSort.java), [A05_MergeSort.java](18-Sorting-Searching-Algorithms/A05_MergeSort.java), [A06_QuickSort.java](18-Sorting-Searching-Algorithms/A06_QuickSort.java), [A07_HeapSort.java](18-Sorting-Searching-Algorithms/A07_HeapSort.java), [C01_QuickSelect.java](18-Sorting-Searching-Algorithms/C01_QuickSelect.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_BubbleSort.java](18-Sorting-Searching-Algorithms/A01_BubbleSort.java) | Bubble Sort | Classic / Easy | The loop invariant is the whole algorithm: after pass i, the last i elements hold the i largest values, already in final position. |
| [A02_SelectionSort.java](18-Sorting-Searching-Algorithms/A02_SelectionSort.java) | Selection Sort | Classic / Easy | Fixed(ish) number of writes: exactly one swap per pass, no matter how scrambled the input. |
| [A03_InsertionSort.java](18-Sorting-Searching-Algorithms/A03_InsertionSort.java) | Insertion Sort | Classic / Easy | The sorted prefix is the invariant, and the early break is the payoff: work is proportional to the number of inversions, not to n^2. |
| [A04_CyclicSort.java](18-Sorting-Searching-Algorithms/A04_CyclicSort.java) * | Cyclic Sort | Classic / Easy | Each swap puts at least one value permanently in its final slot, so there are at most n swaps across the whole run even though the loop can revisit i. |
| [A05_MergeSort.java](18-Sorting-Searching-Algorithms/A05_MergeSort.java) * | Merge Sort | LeetCode 912 / Medium | Merging two sorted arrays is linear, and halving bottoms out in log n levels, so the total is n work per level times log n levels. |
| [A06_QuickSort.java](18-Sorting-Searching-Algorithms/A06_QuickSort.java) * | Quick Sort (Lomuto partition) | LeetCode 912 / Medium | Partition is the reusable primitive, not the sort. |
| [A07_HeapSort.java](18-Sorting-Searching-Algorithms/A07_HeapSort.java) * | Heap Sort (in-place, max-heap) | LeetCode 912 / Medium | A max-heap sorts ascending because each extracted maximum is parked at the END of the array, so the sorted suffix grows leftwards and never collides with the heap. |
| [A08_ShellSort.java](18-Sorting-Searching-Algorithms/A08_ShellSort.java) | Shell Sort (gapped insertion sort) | Medium | Insertion sort is slow only because each element moves one step at a time; a value stranded at the far end needs n shifts. |
| [A09_BucketSort.java](18-Sorting-Searching-Algorithms/A09_BucketSort.java) | Bucket Sort (floats in [0, 1]) | Medium | Comparison sorts cannot beat O(n log n), but this one is not comparing across the whole array - it is using the VALUE as an address, the way counting sort and radix sort do. |
| [C01_QuickSelect.java](18-Sorting-Searching-Algorithms/C01_QuickSelect.java) * | Quickselect - kth smallest / kth largest | LeetCode 215 / Medium | Quick sort recurses into both halves; quickselect throws one half away. |
| [C02_RabinKarpAlgorithm.java](18-Sorting-Searching-Algorithms/C02_RabinKarpAlgorithm.java) | Rabin-Karp: find all occurrences of a pattern | classic string search / Medium | A rolling hash turns "compare n characters at every position" into O(1) per position: leaving and entering characters are the only difference between neighbouring windows. |
| [D01_MaximumGap.java](18-Sorting-Searching-Algorithms/D01_MaximumGap.java) | Maximum Gap | LeetCode 164 / Hard | You do not need the sorted order, only the largest hole in it. |
| [D02_SingleLoopSubstringCheckKMP.java](18-Sorting-Searching-Algorithms/D02_SingleLoopSubstringCheckKMP.java) | Substring search in one pass (KMP) | LeetCode 28 / Easy-to-Hard | The characters already matched are themselves part of the pattern, so the pattern can tell you how far to slide after a failure - no need to re-read the text. |
| [D03_SegmentTree.java](18-Sorting-Searching-Algorithms/D03_SegmentTree.java) | Segment Tree: range sum + point update | hand-built data structure / Hard | 1. |

**Worth adding next:**

- Count Inversions in an Array (and its Reverse Pairs variant, LC 493): Modified merge sort — count cross-pairs during the merge step, in O(n log n)
- Implement KMP correctly — build the LPS/prefix function, then use it (Implement strStr, Repeated Substring Pattern, Shortest Palindrome): Failure-function preprocessing in O(m), then a single non-backtracking scan of the text in O(n)
- Harden QuickSort/QuickSelect: randomized (or median-of-three) pivot plus three-way Dutch-flag partition — LC 912 Sort an Array, kth-largest on an array with heavy duplicates: Random pivot selection for expected O(n log n) / O(n), and a <, =, > three-way split so runs of equal keys are consumed in one pass

## 19-Design-Data-Structures

LeetCode design problems: LRU and LFU cache, min stack, hit counter, time-based KV store.

**Do these first:** [A02_MinStack.java](19-Design-Data-Structures/A02_MinStack.java), [C02_DesignHitCounter.java](19-Design-Data-Structures/C02_DesignHitCounter.java), [C03_InsertDeleteGetRandomOof1.java](19-Design-Data-Structures/C03_InsertDeleteGetRandomOof1.java), [C05_LRUCache.java](19-Design-Data-Structures/C05_LRUCache.java), [C06_TimeBasedKeyValueStore.java](19-Design-Data-Structures/C06_TimeBasedKeyValueStore.java), [D01_LFUCache.java](19-Design-Data-Structures/D01_LFUCache.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_RandomPickIndex.java](19-Design-Data-Structures/A01_RandomPickIndex.java) | Random Pick Index | LeetCode 398 / Medium | Reservoir sampling turns "choose uniformly from a stream of unknown length" into O(1) space: keep the k-th item with probability 1/k. |
| [A02_MinStack.java](19-Design-Data-Structures/A02_MinStack.java) * | Min Stack | LeetCode 155 / Medium | The minimum "so far" is a prefix property of the stack, and a stack only ever grows or shrinks at one end - so caching the answer per entry is enough, and popping automatically restores the previous answer. |
| [B01_LoggerRateLimiter.java](19-Design-Data-Structures/B01_LoggerRateLimiter.java) | Logger Rate Limiter | LeetCode 359 / Easy | Store the deadline, not the history. |
| [C01_DesignAuthenticationManager.java](19-Design-Data-Structures/C01_DesignAuthenticationManager.java) | Design Authentication Manager | LeetCode 1797 / Medium | Nothing ever has to be actively expired. |
| [C02_DesignHitCounter.java](19-Design-Data-Structures/C02_DesignHitCounter.java) * | Design Hit Counter | LeetCode 362 / Medium | A time window over non-decreasing timestamps is a queue: arrivals go in at one end, expiries leave from the other, and you never have to search the middle. |
| [C03_InsertDeleteGetRandomOof1.java](19-Design-Data-Structures/C03_InsertDeleteGetRandomOof1.java) * | Insert Delete GetRandom O(1) | LeetCode 380 / Medium | - Why not delete from the middle of the list? |
| [C04_DesignAStackWithIncrementOperation.java](19-Design-Data-Structures/C04_DesignAStackWithIncrementOperation.java) | Design a Stack With Increment Operation | LeetCode 1381 / Medium | - Why not just loop over the bottom k cells? |
| [C05_LRUCache.java](19-Design-Data-Structures/C05_LRUCache.java) * | LRU Cache | LeetCode 146 / Medium | - Why a doubly linked list? |
| [C06_TimeBasedKeyValueStore.java](19-Design-Data-Structures/C06_TimeBasedKeyValueStore.java) * | Time Based Key-Value Store | LeetCode 981 / Medium | - Why a TreeMap and not a HashMap? |
| [C07_SnapshotArray.java](19-Design-Data-Structures/C07_SnapshotArray.java) | Snapshot Array | LeetCode 1146 / Medium | - Why not copy the whole array on snap()? |
| [C08_StockPriceFluctuation.java](19-Design-Data-Structures/C08_StockPriceFluctuation.java) | Stock Price Fluctuation | LeetCode 2034 / Medium | - Lazy deletion: update() only pushes. |
| [C09_DesignAFoodRatingSystem.java](19-Design-Data-Structures/C09_DesignAFoodRatingSystem.java) | Design a Food Rating System | LeetCode 2353 / Medium | - Lazy deletion: changeRating() pushes a new snapshot and leaves the old one in the heap, since a PriorityQueue cannot remove an interior element in better than O(n). |
| [C10_DesignSnakeGame.java](19-Design-Data-Structures/C10_DesignSnakeGame.java) | Design Snake Game | LeetCode 353 / Medium | - Remove the tail BEFORE testing for a self-bite: the tail vacates its cell in the same move, so stepping onto the old tail cell is legal. |
| [C11_CBTInserter.java](19-Design-Data-Structures/C11_CBTInserter.java) | Complete Binary Tree Inserter | LeetCode 919 / Medium | - insert() loops rather than assuming the front is free: it pops full nodes (both children present), enqueuing their children, until the front has a free slot. |
| [C12_DesignFileSystem.java](19-Design-Data-Structures/C12_DesignFileSystem.java) | Design File System | LeetCode 1166 / Medium | - One node per path SEGMENT, not one map entry per full path string. |
| [D01_LFUCache.java](19-Design-Data-Structures/D01_LFUCache.java) * | LFU Cache | LeetCode 460 / Hard | 1. |
| [D02_AllOne.java](19-Design-Data-Structures/D02_AllOne.java) | All O`one Data Structure | LeetCode 432 / Hard | 1. |
| [D03_DesignInMemoryFileSystem.java](19-Design-Data-Structures/D03_DesignInMemoryFileSystem.java) | Design In-Memory File System | LeetCode 588 / Hard | 1. |

**Worth adding next:**

- BST Iterator (LC 173): Controlled inorder traversal driven by an explicit stack: push the left spine in the constructor, and on next() pop, then push the left spine of the popped node's right child. O(h) space, amortized O(1) per call.
- Design Twitter (LC 355): Composition: HashMap<userId, Deque<Tweet>> for each user's own tweets, HashMap<userId, Set<followeeId>> for the follow graph, and a max-heap k-way merge over the followee lists (bounded to 10) to build the feed. Global monotonic counter as the timestamp.
- Design Circular Queue and Circular Deque (LC 622, 641): Fixed-capacity array ring buffer with head and tail indices advanced modulo capacity, plus an explicit size counter (or a sacrificed slot) to disambiguate full from empty.

## 20-Scenario-Based-Problems

Real-world style questions from Karat, Atlassian and onsite rounds: logs, votes, ratings, distances.

**Do these first:** [A02_FindTopIpaddress.java](20-Scenario-Based-Problems/A02_FindTopIpaddress.java), [B01_MinimumDistanceBetweenWordsV2.java](20-Scenario-Based-Problems/B01_MinimumDistanceBetweenWordsV2.java), [C01_HighAccessEmployees.java](20-Scenario-Based-Problems/C01_HighAccessEmployees.java), [D01_PopularityTracker.java](20-Scenario-Based-Problems/D01_PopularityTracker.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_StudentGrade.java](20-Scenario-Based-Problems/A01_StudentGrade.java) | Student Grade From Marks | Easy | Because the ladder is ordered top-down and uses else-if, a branch never has to write "marks >= 70 && marks < 90" - the earlier branch already excluded the upper part of the range. |
| [A02_FindTopIpaddress.java](20-Scenario-Based-Problems/A02_FindTopIpaddress.java) * | Most Frequent IP In A Log File | Easy | Count-then-argmax is two independent linear passes, not one clever pass - you cannot know the winner until every line is counted. |
| [A03_ElectionWinner.java](20-Scenario-Based-Problems/A03_ElectionWinner.java) | Election Winner With Lexicographic Tie-Break | Easy | The tie-break turns an order-dependent scan into a deterministic one. |
| [A04_BestAverageGrade.java](20-Scenario-Based-Problems/A04_BestAverageGrade.java) | Best Average Grade | Easy | Group-then-aggregate is the workhorse shape behind most log and record problems: one pass to bucket rows under a key, a second pass to reduce each bucket. |
| [B01_MinimumDistanceBetweenWordsV2.java](20-Scenario-Based-Problems/B01_MinimumDistanceBetweenWordsV2.java) * | Shortest Distance Between Two Words | LeetCode 243 / Easy | You never need the full list of occurrences. |
| [B02_TopVideos.java](20-Scenario-Based-Problems/B02_TopVideos.java) | Top K Videos by Total Watch Time | Karat / phone screen / Easy | Top-k is two independent decisions: the aggregation (a HashMap fold) and the selection (sort vs heap). |
| [B03_KARAT_R1.java](20-Scenario-Based-Problems/B03_KARAT_R1.java) | Scrambled Word Hidden in a Note | Karat round 1 / Easy | "Letters cannot be reused" is the whole problem, and it is exactly the definition of multiset (bag) containment: word is a sub-multiset of note. |
| [C01_HighAccessEmployees.java](20-Scenario-Based-Problems/C01_HighAccessEmployees.java) * | High-Access Employees / Badge Access | LeetCode 2933 / Medium | Once the times are sorted, "three scans inside one hour" collapses to a single subtraction. |
| [C02_NearestPlacesFinder.java](20-Scenario-Based-Problems/C02_NearestPlacesFinder.java) | K Nearest Places to a Coordinate | Scenario / "find me nearby" / Medium | The distance key is EXPENSIVE - Haversine costs several trig calls - and a comparator is invoked O(n log n) times, so computing the distance inside the comparator evaluates it roughly n log n times instead of n. |
| [C03_RankTeamsByVotes.java](20-Scenario-Based-Problems/C03_RankTeamsByVotes.java) | Rank Teams by Votes | LeetCode 1366 / Medium | The counting is trivial; the COMPARATOR is the interview signal. |
| [C04_MovieRecommender.java](20-Scenario-Based-Problems/C04_MovieRecommender.java) | Movie Recommender (collaborative filtering) | Karat / Medium | Recommendation here is just a two-hop walk on a bipartite graph of users and movies: target -> movies they liked -> other users who liked them -> movies THOSE users liked. |
| [D01_PopularityTracker.java](20-Scenario-Based-Problems/D01_PopularityTracker.java) * | Popularity Tracker (running max under an interface) | Design / Hard | - Why two maps and not one? |
| [D02_Election.java](20-Scenario-Based-Problems/D02_Election.java) | Election / Josephus Problem | LC 1823 / Medium | Do not simulate the circle - collapse it. |

**Worth adding next:**

- Common Ancestor (Karat, 3 parts): given parent/child pairs, (1) list individuals with zero parents and with exactly one parent, (2) do two individuals share any common ancestor, (3) find the earliest/furthest ancestor of an individual: Build a directed graph from edge pairs (child->parents), degree counting for part 1, BFS/DFS upward + ancestor-set intersection for part 2, BFS with level tracking for part 3
- Longest Common Continuous Subarray / browsing history (Karat): given two users' ordered page-visit lists, return the longest contiguous run they share; follow-up, do it across many users and return the pair with the longest shared run: DP grid over two sequences (or rolling-hash / suffix comparison), then all-pairs over users for the follow-up
- Student Course Pairs (Karat): given (student, course) pairs, output every pair of students with the courses they have in common; follow-up, return the pair sharing the most courses: Group student->set(courses), enumerate all student pairs, set intersection; or invert to course->students and count co-occurrences per pair with a composite key

`*` = must-know. Run any file with `tools/runjava <file>` from the repo root, or open it as an IntelliJ scratch.
