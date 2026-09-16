# DSA Practice Index

Every solved problem in this folder, grouped by technique. Files whose name starts with `Important` are the ones the owner starred as must-remember; they are marked with a star below. Where the source file cites a LeetCode problem, the link or number is shown.

Notes and roadmaps that go with this code:

- [DSA memory keypoints](notes/DSA_Memory_Keypoints.md) and [part II](notes/DSA_Memory_Keypoints_II.md): patterns, formulas and reminders collected while practising.
- [Classic 150 roadmap](roadmaps/Classic_150_Roadmap.md): phased list of 150 problems.
- [Atlassian question list](roadmaps/Atlassian_Question_List.md): problems reported from Atlassian rounds.
- [LeetCode 500 links](roadmaps/LeetCode_500_Links.md): the full link list.

## Topics

| # | Topic | Files | What is here |
|---|-------|-------|--------------|
| 01 | [Arrays](01-Arrays/) | 45 | Prefix sums, Kadane, cyclic sort, in-place tricks, rotations. |
| 02 | [Two-Pointers-Sliding-Window](02-Two-Pointers-Sliding-Window/) | 44 | Fixed and variable windows, opposite-end pointers, longest/shortest subarray or substring. |
| 03 | [Binary-Search](03-Binary-Search/) | 24 | Classic, rotated arrays, lower/upper bound, binary search on the answer space. |
| 04 | [Strings](04-Strings/) | 41 | Parsing, encoding, palindromes, formatting, roman numerals, word games. |
| 05 | [Hashing-Prefix-Sum](05-Hashing-Prefix-Sum/) | 27 | HashMap and HashSet counting, prefix-sum plus map, grouping and set operations. |
| 06 | [Linked-List](06-Linked-List/) | 27 | Reversal, cycle detection, merge, reorder, doubly linked lists. |
| 07 | [Stack-Queue-Monotonic](07-Stack-Queue-Monotonic/) | 29 | Expression evaluation, parentheses, monotonic stack and deque patterns. |
| 08 | [Heap-Priority-Queue](08-Heap-Priority-Queue/) | 14 | Top-K, median of stream, scheduling with a min or max heap. |
| 09 | [Trees-BST](09-Trees-BST/) | 56 | Traversals, LCA variants, BST operations, construction from traversals, path sums. |
| 10 | [Trie](10-Trie/) | 5 | Prefix trees for word search, suggestions and distinct substrings. |
| 11 | [Graphs](11-Graphs/) | 51 | BFS, DFS, topological sort, union-find, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, SCC. |
| 12 | [Dynamic-Programming](12-Dynamic-Programming/) | 44 | 1-D and 2-D DP, knapsack family, LCS and LIS, partition and matrix-chain problems. |
| 13 | [Greedy](13-Greedy/) | 27 | Jump games, stock trading, scheduling, local-choice proofs. |
| 14 | [Backtracking-Recursion](14-Backtracking-Recursion/) | 18 | Subsets, permutations, combination sum, N-Queens, word search, basic recursion. |
| 15 | [Intervals](15-Intervals/) | 12 | Merge, insert, meeting rooms, sweep line and difference arrays. |
| 16 | [Matrix](16-Matrix/) | 10 | Rotation, spiral and diagonal traversal, grid simulation. |
| 17 | [Math-Bit-Manipulation](17-Math-Bit-Manipulation/) | 38 | Number theory, primes, GCD/LCM, fast exponentiation, bit tricks, Pascal's triangle. |
| 18 | [Sorting-Searching-Algorithms](18-Sorting-Searching-Algorithms/) | 16 | Sorting algorithm implementations, quick select, KMP, Rabin-Karp, segment tree. |
| 19 | [Design-Data-Structures](19-Design-Data-Structures/) | 23 | LeetCode 'design' problems: LRU/LFU cache, min stack, hit counter, time-based KV store. |
| 20 | [Scenario-Based-Problems](20-Scenario-Based-Problems/) | 17 | Real-world style questions (Karat, Atlassian, onsite rounds): logs, votes, ratings, distances. |
| | **Total** | **568** | |

## Arrays

Prefix sums, Kadane, cyclic sort, in-place tricks, rotations.

| File | LeetCode | Starred |
|------|----------|---------|
| [AlternatePositiveNegative](01-Arrays/AlternatePositiveNegative.java) |  |  |
| [ArraySortedOrNot](01-Arrays/ArraySortedOrNot.java) |  |  |
| [ArrayTransformation](01-Arrays/ArrayTransformation.java) |  |  |
| [BestSeat](01-Arrays/BestSeat.java) |  |  |
| [BestTimeToBuyAndSellStock](01-Arrays/BestTimeToBuyAndSellStock.java) | [121. best-time-to-buy-and-sell-stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) |  |
| [CheckIfAnArrayIsConsecutive](01-Arrays/CheckIfAnArrayIsConsecutive.java) |  |  |
| [CountFrequencies](01-Arrays/CountFrequencies.java) |  |  |
| [CycleLengthInArray](01-Arrays/CycleLengthInArray.java) |  |  |
| [FindAllDuplicatesInAnArray](01-Arrays/FindAllDuplicatesInAnArray.java) | [find-all-duplicates-in-an-array](https://leetcode.com/problems/find-all-duplicates-in-an-array/) |  |
| [FindAllMissedNumbers](01-Arrays/FindAllMissedNumbers.java) | [find-all-numbers-disappeared-in-an-array](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/) |  |
| [FindDuplicate](01-Arrays/FindDuplicate.java) |  |  |
| [FindDuplicateNumber](01-Arrays/FindDuplicateNumber.java) |  |  |
| [FindMissingRepeatingNumbers](01-Arrays/FindMissingRepeatingNumbers.java) |  |  |
| [FirstMiddleLast](01-Arrays/FirstMiddleLast.java) |  |  |
| [FirstMissingPositive](01-Arrays/FirstMissingPositive.java) | [41. first-missing-positive](https://leetcode.com/problems/first-missing-positive/) |  |
| [ImportantFindCorruptPair](01-Arrays/ImportantFindCorruptPair.java) |  | ⭐ |
| [ImportantFirstMissingPositive](01-Arrays/ImportantFirstMissingPositive.java) |  | ⭐ |
| [IncreasingTripletSubsequence](01-Arrays/IncreasingTripletSubsequence.java) |  |  |
| [IsSorted](01-Arrays/IsSorted.java) |  |  |
| [KadaneSAlgorithm](01-Arrays/KadaneSAlgorithm.java) |  |  |
| [KidsWithCandies](01-Arrays/KidsWithCandies.java) | [1431. kids-with-the-greatest-number-of-candies](https://leetcode.com/problems/kids-with-the-greatest-number-of-candies/) |  |
| [LeadersInAnArray](01-Arrays/LeadersInAnArray.java) |  |  |
| [LeftRotateArrayByOne](01-Arrays/LeftRotateArrayByOne.java) |  |  |
| [LexicographicallySmallestArray](01-Arrays/LexicographicallySmallestArray.java) | [2948. make-lexicographically-smallest-array-by-swapping-elements](https://leetcode.com/problems/make-lexicographically-smallest-array-by-swapping-elements/) |  |
| [LongestBitonicSubarrayProblem](01-Arrays/LongestBitonicSubarrayProblem.java) |  |  |
| [LongestMountainInArray](01-Arrays/LongestMountainInArray.java) | [longest-mountain-in-array](https://leetcode.com/problems/longest-mountain-in-array/) |  |
| [MajorityElement](01-Arrays/MajorityElement.java) | [majority-element](https://leetcode.com/problems/majority-element/) |  |
| [MaxConsecutiveOnes](01-Arrays/MaxConsecutiveOnes.java) | [max-consecutive-ones](https://leetcode.com/problems/max-consecutive-ones/) |  |
| [MaximizeDistanceToClosestPerson](01-Arrays/MaximizeDistanceToClosestPerson.java) | #849 |  |
| [MaximumSubarrayKadanes](01-Arrays/MaximumSubarrayKadanes.java) | [53. maximum-subarray](https://leetcode.com/problems/maximum-subarray/) |  |
| [MaxSubArraySum](01-Arrays/MaxSubArraySum.java) | [maximum-subarray](https://leetcode.com/problems/maximum-subarray/) |  |
| [MissingRanges](01-Arrays/MissingRanges.java) | [missing-ranges](https://leetcode.com/problems/missing-ranges/) |  |
| [MonotonicArray](01-Arrays/MonotonicArray.java) |  |  |
| [NextGreaterPermutation](01-Arrays/NextGreaterPermutation.java) |  |  |
| [NumberOfZeroFilledSubarrays](01-Arrays/NumberOfZeroFilledSubarrays.java) | [2348. number-of-zero-filled-subarrays](https://leetcode.com/problems/number-of-zero-filled-subarrays/) |  |
| [PartitionArrayintoDisjointIntervals](01-Arrays/PartitionArrayintoDisjointIntervals.java) |  |  |
| [PivotIndex](01-Arrays/PivotIndex.java) |  |  |
| [PlusOne](01-Arrays/PlusOne.java) |  |  |
| [PrintArrayInCyclic](01-Arrays/PrintArrayInCyclic.java) |  |  |
| [ProductExceptSelf](01-Arrays/ProductExceptSelf.java) |  |  |
| [RotateArray](01-Arrays/RotateArray.java) | [rotate-array](https://leetcode.com/problems/rotate-array/) |  |
| [SecondSmallest](01-Arrays/SecondSmallest.java) |  |  |
| [SmallestSubarraySum](01-Arrays/SmallestSubarraySum.java) |  |  |
| [Sort012](01-Arrays/Sort012.java) |  |  |
| [ThreeLargestNumbers](01-Arrays/ThreeLargestNumbers.java) |  |  |

## Two-Pointers-Sliding-Window

Fixed and variable windows, opposite-end pointers, longest/shortest subarray or substring.

| File | LeetCode | Starred |
|------|----------|---------|
| [CharacterReplacement](02-Two-Pointers-Sliding-Window/CharacterReplacement.java) | [424. longest-repeating-character-replacement](https://leetcode.com/problems/longest-repeating-character-replacement/) |  |
| [ContainerWithMostWater](02-Two-Pointers-Sliding-Window/ContainerWithMostWater.java) | [container-with-most-water](https://leetcode.com/problems/container-with-most-water/) |  |
| [CountPairsWhoseSumisLessthanTarget](02-Two-Pointers-Sliding-Window/CountPairsWhoseSumisLessthanTarget.java) |  |  |
| [FruitIntoBaskets](02-Two-Pointers-Sliding-Window/FruitIntoBaskets.java) | [904. fruit-into-baskets](https://leetcode.com/problems/fruit-into-baskets/) |  |
| [ImportantLongestSubarrayWithSumKTwoPointer](02-Two-Pointers-Sliding-Window/ImportantLongestSubarrayWithSumKTwoPointer.java) |  | ⭐ |
| [ImportantLongestSubStringWithoutRepeatingCharacter](02-Two-Pointers-Sliding-Window/ImportantLongestSubStringWithoutRepeatingCharacter.java) | [longest-substring-without-repeating-characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | ⭐ |
| [ImportantLongestUniformSubstring](02-Two-Pointers-Sliding-Window/ImportantLongestUniformSubstring.java) |  | ⭐ |
| [ImportantMinSubArrayExceedsSumSlidingWindow](02-Two-Pointers-Sliding-Window/ImportantMinSubArrayExceedsSumSlidingWindow.java) |  | ⭐ |
| [ImportantTrappingRainWater](02-Two-Pointers-Sliding-Window/ImportantTrappingRainWater.java) | [42. trapping-rain-water](https://leetcode.com/problems/trapping-rain-water/) | ⭐ |
| [IntersectionOfThreeSortedArrays](02-Two-Pointers-Sliding-Window/IntersectionOfThreeSortedArrays.java) |  |  |
| [IntersectionThreeArraysUnique](02-Two-Pointers-Sliding-Window/IntersectionThreeArraysUnique.java) |  |  |
| [IsSubsequence](02-Two-Pointers-Sliding-Window/IsSubsequence.java) |  |  |
| [LengthOfLongestSubstring](02-Two-Pointers-Sliding-Window/LengthOfLongestSubstring.java) | [3. longest-substring-without-repeating-characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) |  |
| [LongestSubarrayAfterDeletingOne](02-Two-Pointers-Sliding-Window/LongestSubarrayAfterDeletingOne.java) |  |  |
| [LongestSubarrayWithSumKTwoPointer](02-Two-Pointers-Sliding-Window/LongestSubarrayWithSumKTwoPointer.java) |  |  |
| [LongestSubstringWithKRepeatingChars](02-Two-Pointers-Sliding-Window/LongestSubstringWithKRepeatingChars.java) | [395. longest-substring-with-at-least-k-repeating-characters](https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/) |  |
| [LongestSubstringWithoutRepeatingCharacter](02-Two-Pointers-Sliding-Window/LongestSubstringWithoutRepeatingCharacter.java) |  |  |
| [LongestSubStringWithoutRepeatingCharacters](02-Two-Pointers-Sliding-Window/LongestSubStringWithoutRepeatingCharacters.java) | [subarray-sum-equals-k](https://leetcode.com/problems/subarray-sum-equals-k/) |  |
| [LongestUniformSubstring](02-Two-Pointers-Sliding-Window/LongestUniformSubstring.java) |  |  |
| [MaxConsecutiveGoodNums](02-Two-Pointers-Sliding-Window/MaxConsecutiveGoodNums.java) |  |  |
| [MaxConsecutiveOnesIII](02-Two-Pointers-Sliding-Window/MaxConsecutiveOnesIII.java) |  |  |
| [MaximumAverageSubarray](02-Two-Pointers-Sliding-Window/MaximumAverageSubarray.java) |  |  |
| [MaximumFrequencyOfAnElementAfterPerformingOperationsI](02-Two-Pointers-Sliding-Window/MaximumFrequencyOfAnElementAfterPerformingOperationsI.java) | [3346. maximum-frequency-of-an-element-after-performing-operations-i](https://leetcode.com/problems/maximum-frequency-of-an-element-after-performing-operations-i/) |  |
| [MaximumPointsFromCards](02-Two-Pointers-Sliding-Window/MaximumPointsFromCards.java) | [1423. maximum-points-you-can-obtain-from-cards](https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/) |  |
| [MaxProfitWithWindowSize3](02-Two-Pointers-Sliding-Window/MaxProfitWithWindowSize3.java) |  |  |
| [MergeTwoSortedArrays](02-Two-Pointers-Sliding-Window/MergeTwoSortedArrays.java) |  |  |
| [MinimumWindowSubstring](02-Two-Pointers-Sliding-Window/MinimumWindowSubstring.java) |  |  |
| [MinSubArrayExceedsSumSlidingWindow](02-Two-Pointers-Sliding-Window/MinSubArrayExceedsSumSlidingWindow.java) |  |  |
| [MinSubArrayLen](02-Two-Pointers-Sliding-Window/MinSubArrayLen.java) | [minimum-size-subarray-sum](https://leetcode.com/problems/minimum-size-subarray-sum/) |  |
| [MinSubArrayLenSum](02-Two-Pointers-Sliding-Window/MinSubArrayLenSum.java) | [minimum-size-subarray-sum](https://leetcode.com/problems/minimum-size-subarray-sum/) |  |
| [MoveElementToEnd](02-Two-Pointers-Sliding-Window/MoveElementToEnd.java) |  |  |
| [MoveZeroes](02-Two-Pointers-Sliding-Window/MoveZeroes.java) |  |  |
| [RemoveDuplicates](02-Two-Pointers-Sliding-Window/RemoveDuplicates.java) |  |  |
| [RemoveElement](02-Two-Pointers-Sliding-Window/RemoveElement.java) |  |  |
| [ReverseVowels](02-Two-Pointers-Sliding-Window/ReverseVowels.java) |  |  |
| [SetDifferenceTwoPointers](02-Two-Pointers-Sliding-Window/SetDifferenceTwoPointers.java) |  |  |
| [SortArrayByParity](02-Two-Pointers-Sliding-Window/SortArrayByParity.java) |  |  |
| [SquaresOfASortedArray](02-Two-Pointers-Sliding-Window/SquaresOfASortedArray.java) | [977. squares-of-a-sorted-array](https://leetcode.com/problems/squares-of-a-sorted-array/) |  |
| [SubarrayProductLessThanK](02-Two-Pointers-Sliding-Window/SubarrayProductLessThanK.java) | [713. subarray-product-less-than-k](https://leetcode.com/problems/subarray-product-less-than-k/) |  |
| [SweetAndSavory](02-Two-Pointers-Sliding-Window/SweetAndSavory.java) |  |  |
| [Three3Sum](02-Two-Pointers-Sliding-Window/Three3Sum.java) | [3sum](https://leetcode.com/problems/3sum/) |  |
| [TrappingRainWater](02-Two-Pointers-Sliding-Window/TrappingRainWater.java) | [trapping-rain-water](https://leetcode.com/problems/trapping-rain-water/) |  |
| [UnionOfArrays](02-Two-Pointers-Sliding-Window/UnionOfArrays.java) |  |  |
| [ValidWordAbbreviation](02-Two-Pointers-Sliding-Window/ValidWordAbbreviation.java) | [valid-word-abbreviation](https://leetcode.com/problems/valid-word-abbreviation/) |  |

## Binary-Search

Classic, rotated arrays, lower/upper bound, binary search on the answer space.

| File | LeetCode | Starred |
|------|----------|---------|
| [CapacityToShipPackagesWithinDDays](03-Binary-Search/CapacityToShipPackagesWithinDDays.java) | [capacity-to-ship-packages-within-d-days](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/) |  |
| [CuttingRibbons](03-Binary-Search/CuttingRibbons.java) | [cutting-ribbons](https://leetcode.com/problems/cutting-ribbons/) |  |
| [FindPeakElement](03-Binary-Search/FindPeakElement.java) |  |  |
| [FirstAndLastOccurrence](03-Binary-Search/FirstAndLastOccurrence.java) |  |  |
| [FloorAndCeilInSortedArray](03-Binary-Search/FloorAndCeilInSortedArray.java) |  |  |
| [ImportantLowerAndUpperBounds](03-Binary-Search/ImportantLowerAndUpperBounds.java) |  | ⭐ |
| [ImportantMedianOfTwoSortedArrays](03-Binary-Search/ImportantMedianOfTwoSortedArrays.java) |  | ⭐ |
| [ImportantSearchInRotatedSortedArray](03-Binary-Search/ImportantSearchInRotatedSortedArray.java) | [search-in-rotated-sorted-array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | ⭐ |
| [KokoEatingBananas](03-Binary-Search/KokoEatingBananas.java) |  |  |
| [KthElementOf2SortedArrays](03-Binary-Search/KthElementOf2SortedArrays.java) |  |  |
| [KthMissingPositive](03-Binary-Search/KthMissingPositive.java) | [kth-missing-positive-number](https://leetcode.com/problems/kth-missing-positive-number/) |  |
| [LowerAndUpperBounds](03-Binary-Search/LowerAndUpperBounds.java) |  |  |
| [MagneticForceBetweenTwoBalls](03-Binary-Search/MagneticForceBetweenTwoBalls.java) | [1552. magnetic-force-between-two-balls](https://leetcode.com/problems/magnetic-force-between-two-balls/) |  |
| [MedianOfTwoSortedArrays](03-Binary-Search/MedianOfTwoSortedArrays.java) |  |  |
| [MinimumCostToMakeArrayEqual](03-Binary-Search/MinimumCostToMakeArrayEqual.java) | [2448. minimum-cost-to-make-array-equal](https://leetcode.com/problems/minimum-cost-to-make-array-equal/) |  |
| [MinIndexInRotatedSortedArray](03-Binary-Search/MinIndexInRotatedSortedArray.java) |  |  |
| [RandomPickWithWeight](03-Binary-Search/RandomPickWithWeight.java) | [528. random-pick-with-weight](https://leetcode.com/problems/random-pick-with-weight/) |  |
| [Search2DMatrix](03-Binary-Search/Search2DMatrix.java) |  |  |
| [SearchInARotatedSortedArrayII](03-Binary-Search/SearchInARotatedSortedArrayII.java) |  |  |
| [SearchInRotatedSortedArray](03-Binary-Search/SearchInRotatedSortedArray.java) | [search-in-rotated-sorted-array](https://leetcode.com/problems/search-in-rotated-sorted-array/) |  |
| [SecondSmallestInRotatedArray](03-Binary-Search/SecondSmallestInRotatedArray.java) |  |  |
| [SingleNonDuplicateElement](03-Binary-Search/SingleNonDuplicateElement.java) | [540. single-element-in-a-sorted-array](https://leetcode.com/problems/single-element-in-a-sorted-array/) |  |
| [SquareRoot](03-Binary-Search/SquareRoot.java) |  |  |
| [TopVotedCandidate](03-Binary-Search/TopVotedCandidate.java) |  |  |

## Strings

Parsing, encoding, palindromes, formatting, roman numerals, word games.

| File | LeetCode | Starred |
|------|----------|---------|
| [AddBoldTagInString](04-Strings/AddBoldTagInString.java) | [616. add-bold-tag-in-string](https://leetcode.com/problems/add-bold-tag-in-string/) |  |
| [AddStrings](04-Strings/AddStrings.java) |  |  |
| [AnagramStrings](04-Strings/AnagramStrings.java) |  |  |
| [ATOI](04-Strings/ATOI.java) |  |  |
| [CapitalizeFirstAndLastCharacterOfEachWord](04-Strings/CapitalizeFirstAndLastCharacterOfEachWord.java) |  |  |
| [CharactersNotInString](04-Strings/CharactersNotInString.java) |  |  |
| [CheckIfStringIsDecomposableIntoValueEqualSubstrings](04-Strings/CheckIfStringIsDecomposableIntoValueEqualSubstrings.java) |  |  |
| [DelimiterSplit](04-Strings/DelimiterSplit.java) |  |  |
| [FirstCharToUpperCase](04-Strings/FirstCharToUpperCase.java) |  |  |
| [FirstNonRepeatingChar](04-Strings/FirstNonRepeatingChar.java) |  |  |
| [GoatLatin](04-Strings/GoatLatin.java) |  |  |
| [GreatestCommonDivisorOfStrings](04-Strings/GreatestCommonDivisorOfStrings.java) |  |  |
| [ImportantLongestPalindrome](04-Strings/ImportantLongestPalindrome.java) |  | ⭐ |
| [ImportantRunLengthEncodingW4A3](04-Strings/ImportantRunLengthEncodingW4A3.java) |  | ⭐ |
| [IntegerToRoman](04-Strings/IntegerToRoman.java) |  |  |
| [LargestMergeOfTwoStrings](04-Strings/LargestMergeOfTwoStrings.java) | [1754. largest-merge-of-two-strings](https://leetcode.com/problems/largest-merge-of-two-strings/) |  |
| [LongestCommonPrefix](04-Strings/LongestCommonPrefix.java) | [longest-common-prefix](https://leetcode.com/problems/longest-common-prefix/) |  |
| [LongestWordFromLetters](04-Strings/LongestWordFromLetters.java) |  |  |
| [MergeStringsAlternately](04-Strings/MergeStringsAlternately.java) |  |  |
| [MinimumAlternatingBinaryString](04-Strings/MinimumAlternatingBinaryString.java) | [1758. minimum-changes-to-make-alternating-binary-string](https://leetcode.com/problems/minimum-changes-to-make-alternating-binary-string/) |  |
| [MostFrequentVowelAndConsonant](04-Strings/MostFrequentVowelAndConsonant.java) | [3541. find-most-frequent-vowel-and-consonant](https://leetcode.com/problems/find-most-frequent-vowel-and-consonant/) |  |
| [MultiplyStrings](04-Strings/MultiplyStrings.java) |  |  |
| [NextPalindromeUsingSameDigits](04-Strings/NextPalindromeUsingSameDigits.java) |  |  |
| [NumberToWordsConverter](04-Strings/NumberToWordsConverter.java) |  |  |
| [PalindromeSpecial](04-Strings/PalindromeSpecial.java) |  |  |
| [PanagramDetectorMain](04-Strings/PanagramDetectorMain.java) |  |  |
| [PasswordStrengthChecker](04-Strings/PasswordStrengthChecker.java) |  |  |
| [RemoveAllOccurrences](04-Strings/RemoveAllOccurrences.java) | [1910. remove-all-occurrences-of-a-substring](https://leetcode.com/problems/remove-all-occurrences-of-a-substring/) |  |
| [ReverseWords](04-Strings/ReverseWords.java) |  |  |
| [ReverseWordsInString](04-Strings/ReverseWordsInString.java) |  |  |
| [RomanToInteger](04-Strings/RomanToInteger.java) |  |  |
| [RotateString](04-Strings/RotateString.java) | [796. rotate-string](https://leetcode.com/problems/rotate-string/) |  |
| [RunLengthEncodingW4A3](04-Strings/RunLengthEncodingW4A3.java) |  |  |
| [SemordnilapChecker](04-Strings/SemordnilapChecker.java) |  |  |
| [SortStringDescending](04-Strings/SortStringDescending.java) |  |  |
| [StringCompression](04-Strings/StringCompression.java) |  |  |
| [StrobogrammaticNumber](04-Strings/StrobogrammaticNumber.java) |  |  |
| [TextJustification](04-Strings/TextJustification.java) | [68. text-justification](https://leetcode.com/problems/text-justification/) |  |
| [ToCamelCase](04-Strings/ToCamelCase.java) |  |  |
| [ValidNumber](04-Strings/ValidNumber.java) |  |  |
| [ZigzagConversion](04-Strings/ZigzagConversion.java) |  |  |

## Hashing-Prefix-Sum

HashMap and HashSet counting, prefix-sum plus map, grouping and set operations.

| File | LeetCode | Starred |
|------|----------|---------|
| [BinarySubarraysWithSum](05-Hashing-Prefix-Sum/BinarySubarraysWithSum.java) | [930. binary-subarrays-with-sum](https://leetcode.com/problems/binary-subarrays-with-sum/) |  |
| [ContainsDuplicate](05-Hashing-Prefix-Sum/ContainsDuplicate.java) | [217. contains-duplicate](https://leetcode.com/problems/contains-duplicate/) |  |
| [CountPairSum](05-Hashing-Prefix-Sum/CountPairSum.java) |  |  |
| [CountVowelStringsInRanges](05-Hashing-Prefix-Sum/CountVowelStringsInRanges.java) |  |  |
| [ExponentPairs](05-Hashing-Prefix-Sum/ExponentPairs.java) |  |  |
| [FindAndReplacePattern](05-Hashing-Prefix-Sum/FindAndReplacePattern.java) | [890. find-and-replace-pattern](https://leetcode.com/problems/find-and-replace-pattern/) |  |
| [FindTheDifferenceOfTwoArrays](05-Hashing-Prefix-Sum/FindTheDifferenceOfTwoArrays.java) |  |  |
| [GroupAnagrams](05-Hashing-Prefix-Sum/GroupAnagrams.java) |  |  |
| [GroupShiftedStrings](05-Hashing-Prefix-Sum/GroupShiftedStrings.java) |  |  |
| [ImportantCountSubarraySumEqualsK](05-Hashing-Prefix-Sum/ImportantCountSubarraySumEqualsK.java) | [subarray-sum-equals-k](https://leetcode.com/problems/subarray-sum-equals-k/) | ⭐ |
| [ImportantLongestSubarrayWithSumKHash](05-Hashing-Prefix-Sum/ImportantLongestSubarrayWithSumKHash.java) |  | ⭐ |
| [IntersectionOfTwoArrays](05-Hashing-Prefix-Sum/IntersectionOfTwoArrays.java) |  |  |
| [IntersectionOfTwoArraysII](05-Hashing-Prefix-Sum/IntersectionOfTwoArraysII.java) | [intersection-of-two-arrays-ii](https://leetcode.com/problems/intersection-of-two-arrays-ii/) |  |
| [LongestConsecutiveSequence](05-Hashing-Prefix-Sum/LongestConsecutiveSequence.java) | [longest-consecutive-sequence](https://leetcode.com/problems/longest-consecutive-sequence/) |  |
| [LongestSubarrayWithKSumWithHashing](05-Hashing-Prefix-Sum/LongestSubarrayWithKSumWithHashing.java) |  |  |
| [MinimumRounds](05-Hashing-Prefix-Sum/MinimumRounds.java) |  |  |
| [PairsOfSongsDivBy60](05-Hashing-Prefix-Sum/PairsOfSongsDivBy60.java) | [1010. pairs-of-songs-with-total-durations-divisible-by-60](https://leetcode.com/problems/pairs-of-songs-with-total-durations-divisible-by-60/) |  |
| [PairsOfSongsDivisibleBy60](05-Hashing-Prefix-Sum/PairsOfSongsDivisibleBy60.java) |  |  |
| [RansomNote](05-Hashing-Prefix-Sum/RansomNote.java) |  |  |
| [RepeatedDnaSequences](05-Hashing-Prefix-Sum/RepeatedDnaSequences.java) | [187. repeated-dna-sequences](https://leetcode.com/problems/repeated-dna-sequences/) |  |
| [SetDifferenceOfTwoArrays](05-Hashing-Prefix-Sum/SetDifferenceOfTwoArrays.java) |  |  |
| [SmallestMissingNonNegativeIntegerAfterOperations](05-Hashing-Prefix-Sum/SmallestMissingNonNegativeIntegerAfterOperations.java) | [2598. smallest-missing-non-negative-integer-after-operations](https://leetcode.com/problems/smallest-missing-non-negative-integer-after-operations/) |  |
| [SortBasedOnFrequencyOfOccurrence](05-Hashing-Prefix-Sum/SortBasedOnFrequencyOfOccurrence.java) |  |  |
| [SubdomainVisitCount](05-Hashing-Prefix-Sum/SubdomainVisitCount.java) | [subdomain-visit-count](https://leetcode.com/problems/subdomain-visit-count/) |  |
| [TournamentWinner](05-Hashing-Prefix-Sum/TournamentWinner.java) |  |  |
| [TwoSum](05-Hashing-Prefix-Sum/TwoSum.java) | [two-sum](https://leetcode.com/problems/two-sum/) |  |
| [UniqueTuples](05-Hashing-Prefix-Sum/UniqueTuples.java) |  |  |

## Linked-List

Reversal, cycle detection, merge, reorder, doubly linked lists.

| File | LeetCode | Starred |
|------|----------|---------|
| [AddTwoNumbers](06-Linked-List/AddTwoNumbers.java) | [add-two-numbers](https://leetcode.com/problems/add-two-numbers/) |  |
| [CopyRandomList](06-Linked-List/CopyRandomList.java) | [copy-list-with-random-pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) |  |
| [DeleteHeadOfDLL](06-Linked-List/DeleteHeadOfDLL.java) |  |  |
| [DeleteNthNodefromEnd](06-Linked-List/DeleteNthNodefromEnd.java) |  |  |
| [DoublyLinkedList](06-Linked-List/DoublyLinkedList.java) |  |  |
| [DoublyLinkedListReverse](06-Linked-List/DoublyLinkedListReverse.java) |  |  |
| [FindMiddleOfLinkedList](06-Linked-List/FindMiddleOfLinkedList.java) |  |  |
| [IntersectionOfTwoLinkedLists](06-Linked-List/IntersectionOfTwoLinkedLists.java) | [160. intersection-of-two-linked-lists](https://leetcode.com/problems/intersection-of-two-linked-lists/) |  |
| [LinkedListLoopDetection](06-Linked-List/LinkedListLoopDetection.java) |  |  |
| [LinkedListLoopLength](06-Linked-List/LinkedListLoopLength.java) |  |  |
| [ListNodeReferencing](06-Linked-List/ListNodeReferencing.java) |  |  |
| [MaximumTwinSum](06-Linked-List/MaximumTwinSum.java) | [2130. maximum-twin-sum-of-a-linked-list](https://leetcode.com/problems/maximum-twin-sum-of-a-linked-list/) |  |
| [MergeInBetweenLinkedLists](06-Linked-List/MergeInBetweenLinkedLists.java) | [1669. merge-in-between-linked-lists](https://leetcode.com/problems/merge-in-between-linked-lists/) |  |
| [MergeKLists](06-Linked-List/MergeKLists.java) | [23. merge-k-sorted-lists](https://leetcode.com/problems/merge-k-sorted-lists/) |  |
| [MergeNodesinBetweenZeros](06-Linked-List/MergeNodesinBetweenZeros.java) |  |  |
| [MergeTwoSortedLists](06-Linked-List/MergeTwoSortedLists.java) |  |  |
| [OddEvenLinkedList](06-Linked-List/OddEvenLinkedList.java) | [odd-even-linked-list](https://leetcode.com/problems/odd-even-linked-list/) |  |
| [PalindromeLinkedList](06-Linked-List/PalindromeLinkedList.java) |  |  |
| [PartitionList](06-Linked-List/PartitionList.java) | [partition-list](https://leetcode.com/problems/partition-list/) |  |
| [RemoveDuplicatesInList](06-Linked-List/RemoveDuplicatesInList.java) | [83. remove-duplicates-from-sorted-list](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) |  |
| [ReorderList](06-Linked-List/ReorderList.java) |  |  |
| [ReverseLinkedList](06-Linked-List/ReverseLinkedList.java) |  |  |
| [ReverseListInKGroups](06-Linked-List/ReverseListInKGroups.java) |  |  |
| [RotateRightList](06-Linked-List/RotateRightList.java) | [rotate-list](https://leetcode.com/problems/rotate-list/) |  |
| [SortaLLof0s1sand2s](06-Linked-List/SortaLLof0s1sand2s.java) |  |  |
| [SortList](06-Linked-List/SortList.java) |  |  |
| [ZReverseLinkedListRecursive](06-Linked-List/ZReverseLinkedListRecursive.java) |  |  |

## Stack-Queue-Monotonic

Expression evaluation, parentheses, monotonic stack and deque patterns.

| File | LeetCode | Starred |
|------|----------|---------|
| [BasicCalculator](07-Stack-Queue-Monotonic/BasicCalculator.java) | [basic-calculator](https://leetcode.com/problems/basic-calculator/) |  |
| [BuildingsWithOceanView](07-Stack-Queue-Monotonic/BuildingsWithOceanView.java) | [buildings-with-an-ocean-view](https://leetcode.com/problems/buildings-with-an-ocean-view/) |  |
| [CarFleet](07-Stack-Queue-Monotonic/CarFleet.java) |  |  |
| [DailyTemperatures](07-Stack-Queue-Monotonic/DailyTemperatures.java) | #739 |  |
| [DecodeString](07-Stack-Queue-Monotonic/DecodeString.java) | [decode-string](https://leetcode.com/problems/decode-string/) |  |
| [EvalRPN](07-Stack-Queue-Monotonic/EvalRPN.java) | [evaluate-reverse-polish-notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) |  |
| [EvaluateReversePolishNotation](07-Stack-Queue-Monotonic/EvaluateReversePolishNotation.java) | [150. evaluate-reverse-polish-notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) |  |
| [ExclusiveTimeOfFunctions](07-Stack-Queue-Monotonic/ExclusiveTimeOfFunctions.java) | [exclusive-time-of-functions](https://leetcode.com/problems/exclusive-time-of-functions/) |  |
| [Find132pattern](07-Stack-Queue-Monotonic/Find132pattern.java) | [132-pattern](https://leetcode.com/problems/132-pattern/) |  |
| [ImportantAsteroidCollision](07-Stack-Queue-Monotonic/ImportantAsteroidCollision.java) | [asteroid-collision](https://leetcode.com/problems/asteroid-collision/) | ⭐ |
| [ImportantLargestRectangleArea](07-Stack-Queue-Monotonic/ImportantLargestRectangleArea.java) | [largest-rectangle-in-histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | ⭐ |
| [ImportantLongestValidParentheses](07-Stack-Queue-Monotonic/ImportantLongestValidParentheses.java) | [longest-valid-parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | ⭐ |
| [ImportantNextGreaterElements](07-Stack-Queue-Monotonic/ImportantNextGreaterElements.java) |  | ⭐ |
| [ImportantSlidingWindowMaximum](07-Stack-Queue-Monotonic/ImportantSlidingWindowMaximum.java) |  | ⭐ |
| [InfixToPrefix](07-Stack-Queue-Monotonic/InfixToPrefix.java) |  |  |
| [IsValidParentheses](07-Stack-Queue-Monotonic/IsValidParentheses.java) |  |  |
| [MinimumAddToMakeParenthesesValid](07-Stack-Queue-Monotonic/MinimumAddToMakeParenthesesValid.java) | [minimum-add-to-make-parentheses-valid](https://leetcode.com/problems/minimum-add-to-make-parentheses-valid/) |  |
| [NextGreaterElements](07-Stack-Queue-Monotonic/NextGreaterElements.java) |  |  |
| [QueueUsingStacks](07-Stack-Queue-Monotonic/QueueUsingStacks.java) |  |  |
| [RemoveAdjacentDuplicates](07-Stack-Queue-Monotonic/RemoveAdjacentDuplicates.java) |  |  |
| [RemoveKdigits](07-Stack-Queue-Monotonic/RemoveKdigits.java) |  |  |
| [RemovingStarsFromAString](07-Stack-Queue-Monotonic/RemovingStarsFromAString.java) |  |  |
| [RobotCollisions](07-Stack-Queue-Monotonic/RobotCollisions.java) | [2751. robot-collisions](https://leetcode.com/problems/robot-collisions/) |  |
| [SimplifyPath](07-Stack-Queue-Monotonic/SimplifyPath.java) | [simplify-path](https://leetcode.com/problems/simplify-path/) |  |
| [StackSortable](07-Stack-Queue-Monotonic/StackSortable.java) |  |  |
| [StackTraversal](07-Stack-Queue-Monotonic/StackTraversal.java) |  |  |
| [SumOfSubarrayMinimums](07-Stack-Queue-Monotonic/SumOfSubarrayMinimums.java) | [907. sum-of-subarray-minimums](https://leetcode.com/problems/sum-of-subarray-minimums/) |  |
| [ValidParentheses](07-Stack-Queue-Monotonic/ValidParentheses.java) |  |  |
| [VisiblePeopleInQueue](07-Stack-Queue-Monotonic/VisiblePeopleInQueue.java) | [1944. number-of-visible-people-in-a-queue](https://leetcode.com/problems/number-of-visible-people-in-a-queue/) |  |

## Heap-Priority-Queue

Top-K, median of stream, scheduling with a min or max heap.

| File | LeetCode | Starred |
|------|----------|---------|
| [FurthestBuildingExample](08-Heap-Priority-Queue/FurthestBuildingExample.java) | [1642. furthest-building-you-can-reach](https://leetcode.com/problems/furthest-building-you-can-reach/) |  |
| [FurthestBuildingYouCanReach](08-Heap-Priority-Queue/FurthestBuildingYouCanReach.java) | [1642. furthest-building-you-can-reach](https://leetcode.com/problems/furthest-building-you-can-reach/) |  |
| [HuffmanCoding](08-Heap-Priority-Queue/HuffmanCoding.java) |  |  |
| [ImportantMedianOfStream](08-Heap-Priority-Queue/ImportantMedianOfStream.java) |  | ⭐ |
| [ImportantTopKFrequent](08-Heap-Priority-Queue/ImportantTopKFrequent.java) |  | ⭐ |
| [KClosestPointsToOrigin](08-Heap-Priority-Queue/KClosestPointsToOrigin.java) |  |  |
| [KthLargestElementInAStream](08-Heap-Priority-Queue/KthLargestElementInAStream.java) | [kth-largest-element-in-a-stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) |  |
| [LastStoneWeight](08-Heap-Priority-Queue/LastStoneWeight.java) | [1046. last-stone-weight](https://leetcode.com/problems/last-stone-weight/) |  |
| [MaximumAveragePassRatio](08-Heap-Priority-Queue/MaximumAveragePassRatio.java) |  |  |
| [MaximumNumberofEventsThatCanBeAttended](08-Heap-Priority-Queue/MaximumNumberofEventsThatCanBeAttended.java) | [1353. maximum-number-of-events-that-can-be-attended](https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended/) |  |
| [MinimumRefuelingStops](08-Heap-Priority-Queue/MinimumRefuelingStops.java) | [871. minimum-number-of-refueling-stops](https://leetcode.com/problems/minimum-number-of-refueling-stops/) |  |
| [ReorganizeString](08-Heap-Priority-Queue/ReorganizeString.java) |  |  |
| [SlidingWindowMedian](08-Heap-Priority-Queue/SlidingWindowMedian.java) | [sliding-window-median](https://leetcode.com/problems/sliding-window-median/) |  |
| [topKFrequent](08-Heap-Priority-Queue/topKFrequent.java) |  |  |

## Trees-BST

Traversals, LCA variants, BST operations, construction from traversals, path sums.

| File | LeetCode | Starred |
|------|----------|---------|
| [AddRowToTree](09-Trees-BST/AddRowToTree.java) |  |  |
| [BinarySearchTreeOperations](09-Trees-BST/BinarySearchTreeOperations.java) |  |  |
| [BinarySearchTreeToGreaterSumTree](09-Trees-BST/BinarySearchTreeToGreaterSumTree.java) | [1038. binary-search-tree-to-greater-sum-tree](https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree/) |  |
| [BinaryTree](09-Trees-BST/BinaryTree.java) |  |  |
| [BinaryTreeFromString](09-Trees-BST/BinaryTreeFromString.java) |  |  |
| [BinaryTreeMaxPathSum](09-Trees-BST/BinaryTreeMaxPathSum.java) | [binary-tree-maximum-path-sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) |  |
| [BinaryTreeToLinkedList](09-Trees-BST/BinaryTreeToLinkedList.java) |  |  |
| [BinaryTreeZigzagTraversal](09-Trees-BST/BinaryTreeZigzagTraversal.java) | [binary-tree-zigzag-level-order-traversal](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/) |  |
| [BottomViewBinaryTree](09-Trees-BST/BottomViewBinaryTree.java) |  |  |
| [BranchSums](09-Trees-BST/BranchSums.java) |  |  |
| [BSTtoDLLInPlace](09-Trees-BST/BSTtoDLLInPlace.java) |  |  |
| [ConstructBinarySearchTreefromPreorderTraversal](09-Trees-BST/ConstructBinarySearchTreefromPreorderTraversal.java) | [1008. construct-binary-search-tree-from-preorder-traversal](https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/) |  |
| [ConstructBinaryTreeFromINPre](09-Trees-BST/ConstructBinaryTreeFromINPre.java) |  |  |
| [ConvertSortedListToBST](09-Trees-BST/ConvertSortedListToBST.java) |  |  |
| [CountCompleteTreeNodes](09-Trees-BST/CountCompleteTreeNodes.java) |  |  |
| [CountGoodNodes](09-Trees-BST/CountGoodNodes.java) | [count-good-nodes-in-binary-tree](https://leetcode.com/problems/count-good-nodes-in-binary-tree/) |  |
| [Cousins](09-Trees-BST/Cousins.java) | [cousins-in-binary-tree](https://leetcode.com/problems/cousins-in-binary-tree/) |  |
| [DeleteANodeInBST](09-Trees-BST/DeleteANodeInBST.java) |  |  |
| [DeleteNodeInBST](09-Trees-BST/DeleteNodeInBST.java) |  |  |
| [DeleteNodesAndReturnForest](09-Trees-BST/DeleteNodesAndReturnForest.java) |  |  |
| [DiameterOfBinaryTree](09-Trees-BST/DiameterOfBinaryTree.java) |  |  |
| [ExpressionTreeEvaluator](09-Trees-BST/ExpressionTreeEvaluator.java) |  |  |
| [FindLeavesOfBinaryTree](09-Trees-BST/FindLeavesOfBinaryTree.java) | [366. find-leaves-of-binary-tree](https://leetcode.com/problems/find-leaves-of-binary-tree/) |  |
| [FloorCeilOfBST](09-Trees-BST/FloorCeilOfBST.java) |  |  |
| [GetMinimumDifference](09-Trees-BST/GetMinimumDifference.java) |  |  |
| [HasPathSum](09-Trees-BST/HasPathSum.java) |  |  |
| [HeightOfBinaryTree](09-Trees-BST/HeightOfBinaryTree.java) |  |  |
| [ImportantSerializeAndDeserialiseBinaryTree](09-Trees-BST/ImportantSerializeAndDeserialiseBinaryTree.java) |  | ⭐ |
| [InvertTree](09-Trees-BST/InvertTree.java) | [invert-binary-tree](https://leetcode.com/problems/invert-binary-tree/) |  |
| [IsBalancedBinaryTree](09-Trees-BST/IsBalancedBinaryTree.java) |  |  |
| [IsSubtree](09-Trees-BST/IsSubtree.java) | [subtree-of-another-tree](https://leetcode.com/problems/subtree-of-another-tree/) |  |
| [LCA](09-Trees-BST/LCA.java) |  |  |
| [LevelOrderTraversal](09-Trees-BST/LevelOrderTraversal.java) | [binary-tree-level-order-traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) |  |
| [LowestCommonAncestorBST](09-Trees-BST/LowestCommonAncestorBST.java) |  |  |
| [LowestCommonAncestorIV](09-Trees-BST/LowestCommonAncestorIV.java) | [1676. lowest-common-ancestor-of-a-binary-tree-iv](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iv/) |  |
| [LowestCommonAncestorOfABinaryTreeII](09-Trees-BST/LowestCommonAncestorOfABinaryTreeII.java) | [1644. lowest-common-ancestor-of-a-binary-tree-ii](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-ii/) |  |
| [LowestCommonAncestorOfaBinaryTreeIII](09-Trees-BST/LowestCommonAncestorOfaBinaryTreeIII.java) | [1650. lowest-common-ancestor-of-a-binary-tree-iii](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/) |  |
| [MaximumDepthOfBinaryTree](09-Trees-BST/MaximumDepthOfBinaryTree.java) |  |  |
| [MaximumDifferenceBetweenNodeAndAncestor](09-Trees-BST/MaximumDifferenceBetweenNodeAndAncestor.java) | [1026. maximum-difference-between-node-and-ancestor](https://leetcode.com/problems/maximum-difference-between-node-and-ancestor/) |  |
| [MaximumSubtreeSum](09-Trees-BST/MaximumSubtreeSum.java) |  |  |
| [MaximumSumPathInBinaryTree](09-Trees-BST/MaximumSumPathInBinaryTree.java) | [124. binary-tree-maximum-path-sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) |  |
| [MaxLevelSum](09-Trees-BST/MaxLevelSum.java) | [maximum-level-sum-of-a-binary-tree](https://leetcode.com/problems/maximum-level-sum-of-a-binary-tree/) |  |
| [MinimumHeightBST](09-Trees-BST/MinimumHeightBST.java) |  |  |
| [NthLargestInBST](09-Trees-BST/NthLargestInBST.java) |  |  |
| [PathSumII](09-Trees-BST/PathSumII.java) | [path-sum-ii](https://leetcode.com/problems/path-sum-ii/) |  |
| [PrePostInorderInOneTraversal](09-Trees-BST/PrePostInorderInOneTraversal.java) |  |  |
| [RangeSumBST](09-Trees-BST/RangeSumBST.java) |  |  |
| [RecursivePostorder](09-Trees-BST/RecursivePostorder.java) |  |  |
| [ReverseOddLevelsOfBinaryTree](09-Trees-BST/ReverseOddLevelsOfBinaryTree.java) |  |  |
| [RightView](09-Trees-BST/RightView.java) |  |  |
| [SameTreeExample](09-Trees-BST/SameTreeExample.java) |  |  |
| [SmallestCommonRegion](09-Trees-BST/SmallestCommonRegion.java) | [1257. smallest-common-region](https://leetcode.com/problems/smallest-common-region/) |  |
| [SortedArrayToBST](09-Trees-BST/SortedArrayToBST.java) |  |  |
| [SumOfLeftLeaves](09-Trees-BST/SumOfLeftLeaves.java) | [sum-of-left-leaves](https://leetcode.com/problems/sum-of-left-leaves/) |  |
| [SymmetricTree](09-Trees-BST/SymmetricTree.java) |  |  |
| [ValidateBST](09-Trees-BST/ValidateBST.java) |  |  |

## Trie

Prefix trees for word search, suggestions and distinct substrings.

| File | LeetCode | Starred |
|------|----------|---------|
| [CountDistinctSubstringsUsingTrie](10-Trie/CountDistinctSubstringsUsingTrie.java) |  |  |
| [DesignAddAndSearchWordsDataStructure](10-Trie/DesignAddAndSearchWordsDataStructure.java) | [211. design-add-and-search-words-data-structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) |  |
| [LongestWordWithAllPrefixesORCompleteStringFinderTrie](10-Trie/LongestWordWithAllPrefixesORCompleteStringFinderTrie.java) |  |  |
| [SearchSuggestionsSystem](10-Trie/SearchSuggestionsSystem.java) | [1268. search-suggestions-system](https://leetcode.com/problems/search-suggestions-system/) |  |
| [Trie](10-Trie/Trie.java) |  |  |

## Graphs

BFS, DFS, topological sort, union-find, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, SCC.

| File | LeetCode | Starred |
|------|----------|---------|
| [AccountsMerge](11-Graphs/AccountsMerge.java) |  |  |
| [AlienDictionaryOrder](11-Graphs/AlienDictionaryOrder.java) |  |  |
| [AllNodesDistanceKInBinaryTree](11-Graphs/AllNodesDistanceKInBinaryTree.java) |  |  |
| [ArticulationPointInGraph](11-Graphs/ArticulationPointInGraph.java) |  |  |
| [BellmanFord](11-Graphs/BellmanFord.java) |  |  |
| [BFSandDFS](11-Graphs/BFSandDFS.java) |  |  |
| [BridgesInGraph](11-Graphs/BridgesInGraph.java) |  |  |
| [Celebrity](11-Graphs/Celebrity.java) | [find-the-celebrity](https://leetcode.com/problems/find-the-celebrity/) |  |
| [CheapestFlight](11-Graphs/CheapestFlight.java) |  |  |
| [CheckForCycleInUnDirected](11-Graphs/CheckForCycleInUnDirected.java) |  |  |
| [CourseSchedule](11-Graphs/CourseSchedule.java) | [course-schedule](https://leetcode.com/problems/course-schedule/) |  |
| [CycleCheckInDirectedGraphMain](11-Graphs/CycleCheckInDirectedGraphMain.java) |  |  |
| [DijkstraUsingSet](11-Graphs/DijkstraUsingSet.java) |  |  |
| [DisjointSetsMain](11-Graphs/DisjointSetsMain.java) |  |  |
| [DjikstrasAlgoPQ](11-Graphs/DjikstrasAlgoPQ.java) |  |  |
| [EventualSafeNodesByDFS](11-Graphs/EventualSafeNodesByDFS.java) |  |  |
| [EventualSafeNodesByDFSV2](11-Graphs/EventualSafeNodesByDFSV2.java) |  |  |
| [FindAllPeopleWithSecret](11-Graphs/FindAllPeopleWithSecret.java) |  |  |
| [FindTheCity](11-Graphs/FindTheCity.java) | [1334. find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) |  |
| [FloodFill](11-Graphs/FloodFill.java) | [flood-fill](https://leetcode.com/problems/flood-fill/) |  |
| [FloydWarshallAlgorithm](11-Graphs/FloydWarshallAlgorithm.java) |  |  |
| [GridTeleportationTraversal](11-Graphs/GridTeleportationTraversal.java) | [3552. grid-teleportation-traversal](https://leetcode.com/problems/grid-teleportation-traversal/) |  |
| [HtmlParserMain](11-Graphs/HtmlParserMain.java) |  |  |
| [IsBipartite](11-Graphs/IsBipartite.java) |  |  |
| [KahnsTopoBFS](11-Graphs/KahnsTopoBFS.java) |  |  |
| [KeysAndRooms](11-Graphs/KeysAndRooms.java) |  |  |
| [KosarajusAlgorithm](11-Graphs/KosarajusAlgorithm.java) |  |  |
| [KruskalAlgorithm](11-Graphs/KruskalAlgorithm.java) |  |  |
| [LastDayToCrossPQ](11-Graphs/LastDayToCrossPQ.java) | [1970. last-day-where-you-can-still-cross](https://leetcode.com/problems/last-day-where-you-can-still-cross/) |  |
| [MakingALargeIsland](11-Graphs/MakingALargeIsland.java) | #827 |  |
| [MinimumEdgeReversals](11-Graphs/MinimumEdgeReversals.java) | [2858. minimum-edge-reversals-so-every-node-is-reachable](https://leetcode.com/problems/minimum-edge-reversals-so-every-node-is-reachable/) |  |
| [MinimumMultiplications](11-Graphs/MinimumMultiplications.java) |  |  |
| [MinimumTimeToVisitCell](11-Graphs/MinimumTimeToVisitCell.java) | [2577. minimum-time-to-visit-a-cell-in-a-grid](https://leetcode.com/problems/minimum-time-to-visit-a-cell-in-a-grid/) |  |
| [MostStonesRemovedWithSameRowOrColumn](11-Graphs/MostStonesRemovedWithSameRowOrColumn.java) | [most-stones-removed-with-same-row-or-column](https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/) |  |
| [NumberOfEnclaves](11-Graphs/NumberOfEnclaves.java) | [1020. number-of-enclaves](https://leetcode.com/problems/number-of-enclaves/) |  |
| [NumberOfIslands](11-Graphs/NumberOfIslands.java) | [200. number-of-islands](https://leetcode.com/problems/number-of-islands/) |  |
| [NumberOfOperationsToMakeNetworkConnected](11-Graphs/NumberOfOperationsToMakeNetworkConnected.java) |  |  |
| [NumberOfProvines](11-Graphs/NumberOfProvines.java) | [547. number-of-provinces](https://leetcode.com/problems/number-of-provinces/) |  |
| [NumberOfWaysToArriveAtDestination](11-Graphs/NumberOfWaysToArriveAtDestination.java) | [number-of-ways-to-arrive-at-destination](https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/) |  |
| [OpenTheLock](11-Graphs/OpenTheLock.java) | [752. open-the-lock](https://leetcode.com/problems/open-the-lock/) |  |
| [ParallelCourses](11-Graphs/ParallelCourses.java) | [parallel-courses](https://leetcode.com/problems/parallel-courses/) |  |
| [PrimsAlgo](11-Graphs/PrimsAlgo.java) |  |  |
| [ReconstructItinerary](11-Graphs/ReconstructItinerary.java) |  |  |
| [RottenOranges](11-Graphs/RottenOranges.java) |  |  |
| [ShortestBridgeSolution](11-Graphs/ShortestBridgeSolution.java) |  |  |
| [ShortestPath](11-Graphs/ShortestPath.java) |  |  |
| [ShortestPathBinaryMatrix](11-Graphs/ShortestPathBinaryMatrix.java) | [shortest-path-in-a-binary-matrix](https://leetcode.com/problems/shortest-path-in-a-binary-matrix/) |  |
| [ShortestPathInBinaryMatrix](11-Graphs/ShortestPathInBinaryMatrix.java) | [1091. shortest-path-in-binary-matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix/) |  |
| [ToposortDFS](11-Graphs/ToposortDFS.java) |  |  |
| [WallsAndGates](11-Graphs/WallsAndGates.java) | [walls-and-gates](https://leetcode.com/problems/walls-and-gates/) |  |
| [WordLadder](11-Graphs/WordLadder.java) | [word-ladder](https://leetcode.com/problems/word-ladder/) |  |

## Dynamic-Programming

1-D and 2-D DP, knapsack family, LCS and LIS, partition and matrix-chain problems.

| File | LeetCode | Starred |
|------|----------|---------|
| [CherryPickII](12-Dynamic-Programming/CherryPickII.java) |  |  |
| [CherryPickup](12-Dynamic-Programming/CherryPickup.java) | [741. cherry-pickup](https://leetcode.com/problems/cherry-pickup/) |  |
| [ClimbingStairs](12-Dynamic-Programming/ClimbingStairs.java) |  |  |
| [CoinChange](12-Dynamic-Programming/CoinChange.java) | [coin-change](https://leetcode.com/problems/coin-change/) |  |
| [CoinChangeII](12-Dynamic-Programming/CoinChangeII.java) |  |  |
| [CoinChangeMinimum](12-Dynamic-Programming/CoinChangeMinimum.java) |  |  |
| [CommonSubSequence](12-Dynamic-Programming/CommonSubSequence.java) |  |  |
| [CountOfDistinctSubsequences](12-Dynamic-Programming/CountOfDistinctSubsequences.java) |  |  |
| [CountPalindromicSubsequences](12-Dynamic-Programming/CountPalindromicSubsequences.java) |  |  |
| [CountSquareSubmatricesWithAllOnes](12-Dynamic-Programming/CountSquareSubmatricesWithAllOnes.java) |  |  |
| [CountSubsequenceWithTargetSum](12-Dynamic-Programming/CountSubsequenceWithTargetSum.java) |  |  |
| [EditDistance](12-Dynamic-Programming/EditDistance.java) |  |  |
| [Fibonacci](12-Dynamic-Programming/Fibonacci.java) |  |  |
| [FibonacciNumber](12-Dynamic-Programming/FibonacciNumber.java) |  |  |
| [HouseRobber](12-Dynamic-Programming/HouseRobber.java) |  |  |
| [ImportantJobSchedulingMaxProfit](12-Dynamic-Programming/ImportantJobSchedulingMaxProfit.java) |  | ⭐ |
| [knapsack01](12-Dynamic-Programming/knapsack01.java) |  |  |
| [LengthOfLIS](12-Dynamic-Programming/LengthOfLIS.java) | [longest-increasing-subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) |  |
| [LengthOfLongestFibonacciSubsequence](12-Dynamic-Programming/LengthOfLongestFibonacciSubsequence.java) | [873. length-of-longest-fibonacci-subsequence](https://leetcode.com/problems/length-of-longest-fibonacci-subsequence/) |  |
| [LongestCommonSubsequence](12-Dynamic-Programming/LongestCommonSubsequence.java) |  |  |
| [LongestCommonSubsequenceTabulation](12-Dynamic-Programming/LongestCommonSubsequenceTabulation.java) |  |  |
| [LongestIncreasingSubsequence](12-Dynamic-Programming/LongestIncreasingSubsequence.java) | [300. longest-increasing-subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) |  |
| [LongestPalindrome](12-Dynamic-Programming/LongestPalindrome.java) |  |  |
| [LongestPalindromicSubsequence](12-Dynamic-Programming/LongestPalindromicSubsequence.java) | [longest-palindromic-subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) |  |
| [LongestStringChain](12-Dynamic-Programming/LongestStringChain.java) | [1048. longest-string-chain](https://leetcode.com/problems/longest-string-chain/) |  |
| [MaximumSubarraySumWithOneDeletion](12-Dynamic-Programming/MaximumSubarraySumWithOneDeletion.java) |  |  |
| [MaxProductSubArray](12-Dynamic-Programming/MaxProductSubArray.java) |  |  |
| [MCM](12-Dynamic-Programming/MCM.java) |  |  |
| [MinimumCostToCutTheStick](12-Dynamic-Programming/MinimumCostToCutTheStick.java) |  |  |
| [MinimumFallingPathSum](12-Dynamic-Programming/MinimumFallingPathSum.java) |  |  |
| [NumDecodings](12-Dynamic-Programming/NumDecodings.java) | [decode-ways](https://leetcode.com/problems/decode-ways/) |  |
| [OptimalPath](12-Dynamic-Programming/OptimalPath.java) |  |  |
| [PalindromePartitioning](12-Dynamic-Programming/PalindromePartitioning.java) | [131. palindrome-partitioning](https://leetcode.com/problems/palindrome-partitioning/) |  |
| [PrintAllSubSets](12-Dynamic-Programming/PrintAllSubSets.java) |  |  |
| [RegularExpressionMatchingMemo](12-Dynamic-Programming/RegularExpressionMatchingMemo.java) | [10. regular-expression-matching](https://leetcode.com/problems/regular-expression-matching/) |  |
| [RodCuttingProblem](12-Dynamic-Programming/RodCuttingProblem.java) |  |  |
| [RodCuttingProblemUsingMemoization](12-Dynamic-Programming/RodCuttingProblemUsingMemoization.java) |  |  |
| [StickersToSpellWord](12-Dynamic-Programming/StickersToSpellWord.java) |  |  |
| [SubsetSumEqualsToTarget](12-Dynamic-Programming/SubsetSumEqualsToTarget.java) |  |  |
| [TallestBillboard](12-Dynamic-Programming/TallestBillboard.java) |  |  |
| [TargetSumCountWays](12-Dynamic-Programming/TargetSumCountWays.java) |  |  |
| [Triangle](12-Dynamic-Programming/Triangle.java) |  |  |
| [TriangleV2](12-Dynamic-Programming/TriangleV2.java) |  |  |
| [UniquePaths](12-Dynamic-Programming/UniquePaths.java) |  |  |

## Greedy

Jump games, stock trading, scheduling, local-choice proofs.

| File | LeetCode | Starred |
|------|----------|---------|
| [AssignCookies](13-Greedy/AssignCookies.java) |  |  |
| [BestTimeToBuyAndSellStockII](13-Greedy/BestTimeToBuyAndSellStockII.java) | [122. best-time-to-buy-and-sell-stock-ii](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) |  |
| [BiggestNumber](13-Greedy/BiggestNumber.java) |  |  |
| [BoatsToSavePeople](13-Greedy/BoatsToSavePeople.java) | [881. boats-to-save-people](https://leetcode.com/problems/boats-to-save-people/) |  |
| [BuyAndSellStockII](13-Greedy/BuyAndSellStockII.java) | [best-time-to-buy-and-sell-stock-ii](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) |  |
| [Candy](13-Greedy/Candy.java) | [candy](https://leetcode.com/problems/candy/) |  |
| [CanJump](13-Greedy/CanJump.java) |  |  |
| [CanJumpTwo](13-Greedy/CanJumpTwo.java) |  |  |
| [CanPlaceFlowers](13-Greedy/CanPlaceFlowers.java) |  |  |
| [ClassPhotos](13-Greedy/ClassPhotos.java) |  |  |
| [GasStation](13-Greedy/GasStation.java) | [gas-station](https://leetcode.com/problems/gas-station/) |  |
| [JobSequencing](13-Greedy/JobSequencing.java) |  |  |
| [JumpGame](13-Greedy/JumpGame.java) |  |  |
| [JumpGameII](13-Greedy/JumpGameII.java) | [45. jump-game-ii](https://leetcode.com/problems/jump-game-ii/) |  |
| [LemonadeChange](13-Greedy/LemonadeChange.java) |  |  |
| [MaxDifferenceChangingInteger](13-Greedy/MaxDifferenceChangingInteger.java) | [1432. max-difference-you-can-get-from-changing-an-integer](https://leetcode.com/problems/max-difference-you-can-get-from-changing-an-integer/) |  |
| [MaximumPalindromesAfterOperations](13-Greedy/MaximumPalindromesAfterOperations.java) | [3035. maximum-palindromes-after-operations](https://leetcode.com/problems/maximum-palindromes-after-operations/) |  |
| [MaximumSwap](13-Greedy/MaximumSwap.java) | [maximum-swap](https://leetcode.com/problems/maximum-swap/) |  |
| [MinimumHealthToBeatGame](13-Greedy/MinimumHealthToBeatGame.java) | [minimum-health-to-beat-game](https://leetcode.com/problems/minimum-health-to-beat-game/) |  |
| [MinimumMovesToMakePalindrome](13-Greedy/MinimumMovesToMakePalindrome.java) | [2193. minimum-number-of-moves-to-make-palindrome](https://leetcode.com/problems/minimum-number-of-moves-to-make-palindrome/) |  |
| [MinimumMovesToPalindrome](13-Greedy/MinimumMovesToPalindrome.java) |  |  |
| [MinimumNumberOfTapsToOpenToWaterAGarden](13-Greedy/MinimumNumberOfTapsToOpenToWaterAGarden.java) | [1326. minimum-number-of-taps-to-open-to-water-a-garden](https://leetcode.com/problems/minimum-number-of-taps-to-open-to-water-a-garden/) |  |
| [NonConstructibleChange](13-Greedy/NonConstructibleChange.java) |  |  |
| [PartitionLabels](13-Greedy/PartitionLabels.java) | [partition-labels](https://leetcode.com/problems/partition-labels/) |  |
| [ShortestJobFirst](13-Greedy/ShortestJobFirst.java) |  |  |
| [SolutionRearrangingFruits](13-Greedy/SolutionRearrangingFruits.java) | [2561. rearranging-fruits](https://leetcode.com/problems/rearranging-fruits/) |  |
| [ZOptimalFreelancing](13-Greedy/ZOptimalFreelancing.java) |  |  |

## Backtracking-Recursion

Subsets, permutations, combination sum, N-Queens, word search, basic recursion.

| File | LeetCode | Starred |
|------|----------|---------|
| [CombinationProgram](14-Backtracking-Recursion/CombinationProgram.java) |  |  |
| [CombinationSum](14-Backtracking-Recursion/CombinationSum.java) | [combination-sum](https://leetcode.com/problems/combination-sum/) |  |
| [CombinationSumII](14-Backtracking-Recursion/CombinationSumII.java) |  |  |
| [ExpressionAddOperators](14-Backtracking-Recursion/ExpressionAddOperators.java) |  |  |
| [GenerateParenthesis](14-Backtracking-Recursion/GenerateParenthesis.java) |  |  |
| [KthPermutation](14-Backtracking-Recursion/KthPermutation.java) | [60. permutation-sequence](https://leetcode.com/problems/permutation-sequence/) |  |
| [LetterCombinations](14-Backtracking-Recursion/LetterCombinations.java) | [letter-combinations-of-a-phone-number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) |  |
| [MColoringProblem](14-Backtracking-Recursion/MColoringProblem.java) |  |  |
| [NestedListWeightSum](14-Backtracking-Recursion/NestedListWeightSum.java) | [nested-list-weight-sum](https://leetcode.com/problems/nested-list-weight-sum/) |  |
| [NQueens](14-Backtracking-Recursion/NQueens.java) | [51. n-queens](https://leetcode.com/problems/n-queens/) |  |
| [Permuataions](14-Backtracking-Recursion/Permuataions.java) |  |  |
| [RecursiveArraySum](14-Backtracking-Recursion/RecursiveArraySum.java) |  |  |
| [RecursiveFactorial](14-Backtracking-Recursion/RecursiveFactorial.java) |  |  |
| [RemoveInvalidParentheses](14-Backtracking-Recursion/RemoveInvalidParentheses.java) |  |  |
| [Subsets](14-Backtracking-Recursion/Subsets.java) | [subsets](https://leetcode.com/problems/subsets/) |  |
| [SubsetsII](14-Backtracking-Recursion/SubsetsII.java) |  |  |
| [WordPathFinder](14-Backtracking-Recursion/WordPathFinder.java) |  |  |
| [WordSearch](14-Backtracking-Recursion/WordSearch.java) | [word-search](https://leetcode.com/problems/word-search/) |  |

## Intervals

Merge, insert, meeting rooms, sweep line and difference arrays.

| File | LeetCode | Starred |
|------|----------|---------|
| [CarPooling](15-Intervals/CarPooling.java) | [1094. car-pooling](https://leetcode.com/problems/car-pooling/) |  |
| [DivideIntervalsIntoMinGroups](15-Intervals/DivideIntervalsIntoMinGroups.java) | [2406. divide-intervals-into-minimum-number-of-groups](https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/) |  |
| [InsertInterval](15-Intervals/InsertInterval.java) |  |  |
| [IntervalListIntersections](15-Intervals/IntervalListIntersections.java) | [986. interval-list-intersections](https://leetcode.com/problems/interval-list-intersections/) |  |
| [MaximumPopulationYear](15-Intervals/MaximumPopulationYear.java) | [1854. maximum-population-year](https://leetcode.com/problems/maximum-population-year/) |  |
| [MeetingRooms](15-Intervals/MeetingRooms.java) | [252. meeting-rooms](https://leetcode.com/problems/meeting-rooms/) |  |
| [MeetingRoomsII](15-Intervals/MeetingRoomsII.java) | [253. meeting-rooms-ii](https://leetcode.com/problems/meeting-rooms-ii/) |  |
| [MeetingRoomsIII](15-Intervals/MeetingRoomsIII.java) | [2402. meeting-rooms-iii](https://leetcode.com/problems/meeting-rooms-iii/) |  |
| [MergeIntervals](15-Intervals/MergeIntervals.java) |  |  |
| [MinimumArrowsToBurstBalloons](15-Intervals/MinimumArrowsToBurstBalloons.java) | [452. minimum-number-of-arrows-to-burst-balloons](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/) |  |
| [MinimumPlatforms](15-Intervals/MinimumPlatforms.java) |  |  |
| [NonOverlappingIntervals](15-Intervals/NonOverlappingIntervals.java) | [435. non-overlapping-intervals](https://leetcode.com/problems/non-overlapping-intervals/) |  |

## Matrix

Rotation, spiral and diagonal traversal, grid simulation.

| File | LeetCode | Starred |
|------|----------|---------|
| [BestMeetingPoint](16-Matrix/BestMeetingPoint.java) |  |  |
| [CountNegativeNumbersInASortedMatrix](16-Matrix/CountNegativeNumbersInASortedMatrix.java) | [1351. count-negative-numbers-in-a-sorted-matrix](https://leetcode.com/problems/count-negative-numbers-in-a-sorted-matrix/) |  |
| [DiagonalTraverse](16-Matrix/DiagonalTraverse.java) | [diagonal-traverse](https://leetcode.com/problems/diagonal-traverse/) |  |
| [ImportantWalkingRobotSimulation](16-Matrix/ImportantWalkingRobotSimulation.java) |  | ⭐ |
| [LargestInEachThreeRows](16-Matrix/LargestInEachThreeRows.java) | [find-largest-value-in-each-row-of-binary-tree](https://leetcode.com/problems/find-largest-value-in-each-row-of-binary-tree/) |  |
| [MatrixRotate90Degree](16-Matrix/MatrixRotate90Degree.java) |  |  |
| [SetMatrixZeroes](16-Matrix/SetMatrixZeroes.java) |  |  |
| [SpiralTraversalOfMatrix](16-Matrix/SpiralTraversalOfMatrix.java) |  |  |
| [ToeplitzMatrix](16-Matrix/ToeplitzMatrix.java) | [toeplitz-matrix](https://leetcode.com/problems/toeplitz-matrix/) |  |
| [WalkingRobotSimulation](16-Matrix/WalkingRobotSimulation.java) |  |  |

## Math-Bit-Manipulation

Number theory, primes, GCD/LCM, fast exponentiation, bit tricks, Pascal's triangle.

| File | LeetCode | Starred |
|------|----------|---------|
| [AddTwoFractions](17-Math-Bit-Manipulation/AddTwoFractions.java) |  |  |
| [AllDivisors](17-Math-Bit-Manipulation/AllDivisors.java) |  |  |
| [AngleBetweenHandsOfClock](17-Math-Bit-Manipulation/AngleBetweenHandsOfClock.java) | [angle-between-hands-of-a-clock](https://leetcode.com/problems/angle-between-hands-of-a-clock/) |  |
| [ArmstrongNumber](17-Math-Bit-Manipulation/ArmstrongNumber.java) |  |  |
| [BinaryExponentiation](17-Math-Bit-Manipulation/BinaryExponentiation.java) |  |  |
| [BinaryWatch](17-Math-Bit-Manipulation/BinaryWatch.java) | #401 |  |
| [CheckIfTheIthBitIsSetOrNot](17-Math-Bit-Manipulation/CheckIfTheIthBitIsSetOrNot.java) |  |  |
| [CheckPrime](17-Math-Bit-Manipulation/CheckPrime.java) |  |  |
| [DivisibleByThree](17-Math-Bit-Manipulation/DivisibleByThree.java) |  |  |
| [ExcelSheetColumnNumber](17-Math-Bit-Manipulation/ExcelSheetColumnNumber.java) |  |  |
| [FibonacciBinaryExponentiation](17-Math-Bit-Manipulation/FibonacciBinaryExponentiation.java) |  |  |
| [FibonacciCounter](17-Math-Bit-Manipulation/FibonacciCounter.java) |  |  |
| [HappyNumber](17-Math-Bit-Manipulation/HappyNumber.java) | [happy-number](https://leetcode.com/problems/happy-number/) |  |
| [ImportantFibonacciBinaryExponentiation](17-Math-Bit-Manipulation/ImportantFibonacciBinaryExponentiation.java) |  | ⭐ |
| [ImportantPascalTrianleByMath](17-Math-Bit-Manipulation/ImportantPascalTrianleByMath.java) |  | ⭐ |
| [ImportantPrimeFactors](17-Math-Bit-Manipulation/ImportantPrimeFactors.java) |  | ⭐ |
| [ImportantRepeatedNumberInFractionAfterDecimal](17-Math-Bit-Manipulation/ImportantRepeatedNumberInFractionAfterDecimal.java) |  | ⭐ |
| [JosephusProblem](17-Math-Bit-Manipulation/JosephusProblem.java) |  |  |
| [LCMOfTwoNumbers](17-Math-Bit-Manipulation/LCMOfTwoNumbers.java) |  |  |
| [MaximumAreaofLongestDiagonalRectangle](17-Math-Bit-Manipulation/MaximumAreaofLongestDiagonalRectangle.java) | [3000. maximum-area-of-longest-diagonal-rectangle](https://leetcode.com/problems/maximum-area-of-longest-diagonal-rectangle/) |  |
| [MeanMedianCalculator](17-Math-Bit-Manipulation/MeanMedianCalculator.java) |  |  |
| [MinimumBitFlipsToConvertNumber](17-Math-Bit-Manipulation/MinimumBitFlipsToConvertNumber.java) | [2220. minimum-bit-flips-to-convert-number](https://leetcode.com/problems/minimum-bit-flips-to-convert-number/) |  |
| [MinimumTimeDifference](17-Math-Bit-Manipulation/MinimumTimeDifference.java) | [minimum-time-difference](https://leetcode.com/problems/minimum-time-difference/) |  |
| [MissingNumber](17-Math-Bit-Manipulation/MissingNumber.java) |  |  |
| [NumberOf1Bits](17-Math-Bit-Manipulation/NumberOf1Bits.java) |  |  |
| [NumberOfDays](17-Math-Bit-Manipulation/NumberOfDays.java) |  |  |
| [NumberOfPerfectPairs](17-Math-Bit-Manipulation/NumberOfPerfectPairs.java) | [3649. number-of-perfect-pairs](https://leetcode.com/problems/number-of-perfect-pairs/) |  |
| [PascalTrianleByMath](17-Math-Bit-Manipulation/PascalTrianleByMath.java) |  |  |
| [PascalTrianleTwoLoops](17-Math-Bit-Manipulation/PascalTrianleTwoLoops.java) |  |  |
| [PowerCheck](17-Math-Bit-Manipulation/PowerCheck.java) |  |  |
| [PowerOfTen](17-Math-Bit-Manipulation/PowerOfTen.java) |  |  |
| [PowerOfThree](17-Math-Bit-Manipulation/PowerOfThree.java) |  |  |
| [PrimeFactors](17-Math-Bit-Manipulation/PrimeFactors.java) |  |  |
| [RepeatedNumberInFractionAfterDecimal](17-Math-Bit-Manipulation/RepeatedNumberInFractionAfterDecimal.java) |  |  |
| [SieveOfEratosthenes](17-Math-Bit-Manipulation/SieveOfEratosthenes.java) |  |  |
| [SingleNumber](17-Math-Bit-Manipulation/SingleNumber.java) | [single-number](https://leetcode.com/problems/single-number/) |  |
| [SumOfProductOfPairs](17-Math-Bit-Manipulation/SumOfProductOfPairs.java) |  |  |
| [ZScore](17-Math-Bit-Manipulation/ZScore.java) |  |  |

## Sorting-Searching-Algorithms

Sorting algorithm implementations, quick select, KMP, Rabin-Karp, segment tree.

| File | LeetCode | Starred |
|------|----------|---------|
| [BubbleSort](18-Sorting-Searching-Algorithms/BubbleSort.java) |  |  |
| [BucketSort](18-Sorting-Searching-Algorithms/BucketSort.java) |  |  |
| [CyclicSort](18-Sorting-Searching-Algorithms/CyclicSort.java) |  |  |
| [HeapSort](18-Sorting-Searching-Algorithms/HeapSort.java) |  |  |
| [ImportantMergeSort](18-Sorting-Searching-Algorithms/ImportantMergeSort.java) |  | ⭐ |
| [ImportantQuickSelect](18-Sorting-Searching-Algorithms/ImportantQuickSelect.java) |  | ⭐ |
| [ImportantQuickSort](18-Sorting-Searching-Algorithms/ImportantQuickSort.java) |  | ⭐ |
| [InsertionSort](18-Sorting-Searching-Algorithms/InsertionSort.java) |  |  |
| [MaximumGap](18-Sorting-Searching-Algorithms/MaximumGap.java) | [164. maximum-gap](https://leetcode.com/problems/maximum-gap/) |  |
| [MergeSort](18-Sorting-Searching-Algorithms/MergeSort.java) |  |  |
| [QuickSelect](18-Sorting-Searching-Algorithms/QuickSelect.java) |  |  |
| [RabinKarpAlgorithm](18-Sorting-Searching-Algorithms/RabinKarpAlgorithm.java) |  |  |
| [SegmentTree](18-Sorting-Searching-Algorithms/SegmentTree.java) |  |  |
| [SelectionSort](18-Sorting-Searching-Algorithms/SelectionSort.java) |  |  |
| [ShellSort](18-Sorting-Searching-Algorithms/ShellSort.java) |  |  |
| [SingleLoopSubstringCheckKMP](18-Sorting-Searching-Algorithms/SingleLoopSubstringCheckKMP.java) |  |  |

## Design-Data-Structures

LeetCode 'design' problems: LRU/LFU cache, min stack, hit counter, time-based KV store.

| File | LeetCode | Starred |
|------|----------|---------|
| [AllOne](19-Design-Data-Structures/AllOne.java) |  |  |
| [AllOneII](19-Design-Data-Structures/AllOneII.java) | [432. all-oone-data-structure](https://leetcode.com/problems/all-oone-data-structure/) |  |
| [AuthenticationManager](19-Design-Data-Structures/AuthenticationManager.java) | [1797. design-authentication-manager](https://leetcode.com/problems/design-authentication-manager/) |  |
| [CBTInserter](19-Design-Data-Structures/CBTInserter.java) | #919 |  |
| [DesignAFoodRatingSystem](19-Design-Data-Structures/DesignAFoodRatingSystem.java) | [2353. design-a-food-rating-system](https://leetcode.com/problems/design-a-food-rating-system/) |  |
| [DesignAStackWithIncrementOperation](19-Design-Data-Structures/DesignAStackWithIncrementOperation.java) | [1381. design-a-stack-with-increment-operation](https://leetcode.com/problems/design-a-stack-with-increment-operation/) |  |
| [DesignAuthenticationManager](19-Design-Data-Structures/DesignAuthenticationManager.java) | [1797. design-authentication-manager](https://leetcode.com/problems/design-authentication-manager/) |  |
| [DesignFileSystem](19-Design-Data-Structures/DesignFileSystem.java) | [1166. design-file-system](https://leetcode.com/problems/design-file-system/) |  |
| [DesignHitCounter](19-Design-Data-Structures/DesignHitCounter.java) |  |  |
| [DesignInMemoryFileSystem](19-Design-Data-Structures/DesignInMemoryFileSystem.java) |  |  |
| [DesignSnakeGame](19-Design-Data-Structures/DesignSnakeGame.java) | [353. design-snake-game](https://leetcode.com/problems/design-snake-game/) |  |
| [HitCounter](19-Design-Data-Structures/HitCounter.java) |  |  |
| [HitCounterMain](19-Design-Data-Structures/HitCounterMain.java) |  |  |
| [ImportantLRUCacheMain](19-Design-Data-Structures/ImportantLRUCacheMain.java) |  | ⭐ |
| [InsertDeleteGetRandomOof1](19-Design-Data-Structures/InsertDeleteGetRandomOof1.java) | #380 |  |
| [LFUCache](19-Design-Data-Structures/LFUCache.java) |  |  |
| [LoggerRateLimiter](19-Design-Data-Structures/LoggerRateLimiter.java) |  |  |
| [MinStack](19-Design-Data-Structures/MinStack.java) |  |  |
| [RandomPickIndex](19-Design-Data-Structures/RandomPickIndex.java) | [random-pick-index](https://leetcode.com/problems/random-pick-index/) |  |
| [RemoveInsertGetO1](19-Design-Data-Structures/RemoveInsertGetO1.java) |  |  |
| [SnapshotArray](19-Design-Data-Structures/SnapshotArray.java) | [1146. snapshot-array](https://leetcode.com/problems/snapshot-array/) |  |
| [StockPriceFluctuation](19-Design-Data-Structures/StockPriceFluctuation.java) | [2034. stock-price-fluctuation](https://leetcode.com/problems/stock-price-fluctuation/) |  |
| [TimeBasedKeyValueStore](19-Design-Data-Structures/TimeBasedKeyValueStore.java) |  |  |

## Scenario-Based-Problems

Real-world style questions (Karat, Atlassian, onsite rounds): logs, votes, ratings, distances.

| File | LeetCode | Starred |
|------|----------|---------|
| [BadgeAccess](20-Scenario-Based-Problems/BadgeAccess.java) |  |  |
| [BestAverageGrade](20-Scenario-Based-Problems/BestAverageGrade.java) |  |  |
| [Election](20-Scenario-Based-Problems/Election.java) |  |  |
| [ElectionWinner](20-Scenario-Based-Problems/ElectionWinner.java) |  |  |
| [FindTopIpaddress](20-Scenario-Based-Problems/FindTopIpaddress.java) |  |  |
| [HighAccessEmployees](20-Scenario-Based-Problems/HighAccessEmployees.java) | [2933. high-access-employees](https://leetcode.com/problems/high-access-employees/) |  |
| [KARAT_R1](20-Scenario-Based-Problems/KARAT_R1.java) |  |  |
| [MinimumDistanceBetweenWords](20-Scenario-Based-Problems/MinimumDistanceBetweenWords.java) |  |  |
| [MinimumDistanceBetweenWordsV2](20-Scenario-Based-Problems/MinimumDistanceBetweenWordsV2.java) |  |  |
| [MovieRecommender](20-Scenario-Based-Problems/MovieRecommender.java) |  |  |
| [NearestPlacesFinder](20-Scenario-Based-Problems/NearestPlacesFinder.java) |  |  |
| [PopularityTracker](20-Scenario-Based-Problems/PopularityTracker.java) |  |  |
| [RankTeamsByVotes](20-Scenario-Based-Problems/RankTeamsByVotes.java) | [1366. rank-teams-by-votes](https://leetcode.com/problems/rank-teams-by-votes/) |  |
| [ShortestDistance](20-Scenario-Based-Problems/ShortestDistance.java) |  |  |
| [ShortestDistanceBetweenTwoWords](20-Scenario-Based-Problems/ShortestDistanceBetweenTwoWords.java) |  |  |
| [StudentGrade](20-Scenario-Based-Problems/StudentGrade.java) |  |  |
| [TopVideos](20-Scenario-Based-Problems/TopVideos.java) |  |  |

## Same problem, several files

These files solve the same problem (matched by the LeetCode link or number inside the file, or by near-identical file names such as `X` and `ImportantX`). They are usually different approaches, an `Important` re-write, or an older attempt. Worth a review pass to keep the best one.

| Problem | Files |
|---------|-------|
| best-time-to-buy-and-sell-stock-ii | `13-Greedy/BestTimeToBuyAndSellStockII.java`<br>`13-Greedy/BuyAndSellStockII.java` |
| binary-tree-maximum-path-sum | `09-Trees-BST/BinaryTreeMaxPathSum.java`<br>`09-Trees-BST/MaximumSumPathInBinaryTree.java` |
| design-authentication-manager | `19-Design-Data-Structures/AuthenticationManager.java`<br>`19-Design-Data-Structures/DesignAuthenticationManager.java` |
| evaluate-reverse-polish-notation | `07-Stack-Queue-Monotonic/EvalRPN.java`<br>`07-Stack-Queue-Monotonic/EvaluateReversePolishNotation.java` |
| EventualSafeNodesByDFS | `11-Graphs/EventualSafeNodesByDFS.java`<br>`11-Graphs/EventualSafeNodesByDFSV2.java` |
| FibonacciBinaryExponentiation | `17-Math-Bit-Manipulation/FibonacciBinaryExponentiation.java`<br>`17-Math-Bit-Manipulation/ImportantFibonacciBinaryExponentiation.java` |
| FirstMissingPositive | `01-Arrays/FirstMissingPositive.java`<br>`01-Arrays/ImportantFirstMissingPositive.java` |
| furthest-building-you-can-reach | `08-Heap-Priority-Queue/FurthestBuildingExample.java`<br>`08-Heap-Priority-Queue/FurthestBuildingYouCanReach.java` |
| HitCounter | `19-Design-Data-Structures/DesignHitCounter.java`<br>`19-Design-Data-Structures/HitCounter.java`<br>`19-Design-Data-Structures/HitCounterMain.java` |
| longest-increasing-subsequence | `12-Dynamic-Programming/LengthOfLIS.java`<br>`12-Dynamic-Programming/LongestIncreasingSubsequence.java` |
| LongestPalindrome | `04-Strings/ImportantLongestPalindrome.java`<br>`12-Dynamic-Programming/LongestPalindrome.java` |
| LongestSubarrayWithSumKTwoPointer | `02-Two-Pointers-Sliding-Window/ImportantLongestSubarrayWithSumKTwoPointer.java`<br>`02-Two-Pointers-Sliding-Window/LongestSubarrayWithSumKTwoPointer.java` |
| LongestSubStringWithoutRepeatingCharacter | `02-Two-Pointers-Sliding-Window/ImportantLongestSubStringWithoutRepeatingCharacter.java`<br>`02-Two-Pointers-Sliding-Window/LengthOfLongestSubstring.java`<br>`02-Two-Pointers-Sliding-Window/LongestSubstringWithoutRepeatingCharacter.java` |
| LongestUniformSubstring | `02-Two-Pointers-Sliding-Window/ImportantLongestUniformSubstring.java`<br>`02-Two-Pointers-Sliding-Window/LongestUniformSubstring.java` |
| LowerAndUpperBounds | `03-Binary-Search/ImportantLowerAndUpperBounds.java`<br>`03-Binary-Search/LowerAndUpperBounds.java` |
| maximum-subarray | `01-Arrays/MaxSubArraySum.java`<br>`01-Arrays/MaximumSubarrayKadanes.java` |
| MedianOfTwoSortedArrays | `03-Binary-Search/ImportantMedianOfTwoSortedArrays.java`<br>`03-Binary-Search/MedianOfTwoSortedArrays.java` |
| MergeSort | `18-Sorting-Searching-Algorithms/ImportantMergeSort.java`<br>`18-Sorting-Searching-Algorithms/MergeSort.java` |
| minimum-size-subarray-sum | `02-Two-Pointers-Sliding-Window/MinSubArrayLen.java`<br>`02-Two-Pointers-Sliding-Window/MinSubArrayLenSum.java` |
| MinimumDistanceBetweenWords | `20-Scenario-Based-Problems/MinimumDistanceBetweenWords.java`<br>`20-Scenario-Based-Problems/MinimumDistanceBetweenWordsV2.java` |
| MinSubArrayExceedsSumSlidingWindow | `02-Two-Pointers-Sliding-Window/ImportantMinSubArrayExceedsSumSlidingWindow.java`<br>`02-Two-Pointers-Sliding-Window/MinSubArrayExceedsSumSlidingWindow.java` |
| NextGreaterElements | `07-Stack-Queue-Monotonic/ImportantNextGreaterElements.java`<br>`07-Stack-Queue-Monotonic/NextGreaterElements.java` |
| PascalTrianleByMath | `17-Math-Bit-Manipulation/ImportantPascalTrianleByMath.java`<br>`17-Math-Bit-Manipulation/PascalTrianleByMath.java` |
| PrimeFactors | `17-Math-Bit-Manipulation/ImportantPrimeFactors.java`<br>`17-Math-Bit-Manipulation/PrimeFactors.java` |
| QuickSelect | `18-Sorting-Searching-Algorithms/ImportantQuickSelect.java`<br>`18-Sorting-Searching-Algorithms/QuickSelect.java` |
| RepeatedNumberInFractionAfterDecimal | `17-Math-Bit-Manipulation/ImportantRepeatedNumberInFractionAfterDecimal.java`<br>`17-Math-Bit-Manipulation/RepeatedNumberInFractionAfterDecimal.java` |
| RunLengthEncodingW4A3 | `04-Strings/ImportantRunLengthEncodingW4A3.java`<br>`04-Strings/RunLengthEncodingW4A3.java` |
| search-in-rotated-sorted-array | `03-Binary-Search/ImportantSearchInRotatedSortedArray.java`<br>`03-Binary-Search/SearchInRotatedSortedArray.java` |
| subarray-sum-equals-k | `02-Two-Pointers-Sliding-Window/LongestSubStringWithoutRepeatingCharacters.java`<br>`05-Hashing-Prefix-Sum/ImportantCountSubarraySumEqualsK.java` |
| TopKFrequent | `08-Heap-Priority-Queue/ImportantTopKFrequent.java`<br>`08-Heap-Priority-Queue/topKFrequent.java` |
| trapping-rain-water | `02-Two-Pointers-Sliding-Window/ImportantTrappingRainWater.java`<br>`02-Two-Pointers-Sliding-Window/TrappingRainWater.java` |
| Triangle | `12-Dynamic-Programming/Triangle.java`<br>`12-Dynamic-Programming/TriangleV2.java` |
| WalkingRobotSimulation | `16-Matrix/ImportantWalkingRobotSimulation.java`<br>`16-Matrix/WalkingRobotSimulation.java` |

