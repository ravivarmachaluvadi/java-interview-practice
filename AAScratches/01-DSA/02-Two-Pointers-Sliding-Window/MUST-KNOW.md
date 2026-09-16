# 02-Two-Pointers-Sliding-Window — must-know order

**Techniques in this topic:** Write-pointer compaction: one slow pointer marks where the next kept element goes while a fast pointer scans (RemoveElement, MoveZeroes, MoveElementToEnd, RemoveDuplicates), Converging pointers from opposite ends: swap, partition, or evaluate a pair and discard the provably-worse side (ReverseVowels, SortArrayByParity, SquaresOfASortedArray, ContainerWithMostWater, TrappingRainWater), Sorted-pair search: sort, then converge; counting a whole block at once with count += (high - low), and skipping duplicates (CountPairs, SweetAndSavory, Three3Sum), Parallel merge walk over two or three sorted sequences, advancing only the smallest pointer (UnionOfArrays, SetDifference, both Intersection files, MergeTwoSortedArrays), Two pointers across two different strings advancing at different rates (IsSubsequence, ValidWordAbbreviation), Fixed-size window with a rolling sum: add right, subtract left (MaximumAverageSubarray, MaximumPointsFromCards), Variable window, shrink-while-invalid: the core template for minimum-length answers (MinSubArrayExceedsSum x2), Variable window, grow-while-valid: the core template for maximum-length answers (LongestSubarrayWithSumK, LongestSubstringWithoutRepeating), The 'at most K violations' abstraction: one counter of the thing you are allowed to break (MaxConsecutiveOnesIII, LongestSubarrayAfterDeletingOne, FruitIntoBaskets, CharacterReplacement), Frequency maps inside a window, including the formed/required counter for multiset containment (FruitIntoBaskets, CharacterReplacement, MinimumWindowSubstring), Counting rather than measuring: every valid window contributes (right - left + 1) subarrays (SubarrayProductLessThanK, CountPairs), Monotonic deque to get the window min/max in O(1) amortized (MaxProfitWithWindowSize3), Knowing when the window does NOT apply, and what replaces it: prefix-sum hash map for negatives, divide and conquer, difference-array sweep (LongestSubarrayWithSumK V2, LongestSubstringWithKRepeatingChars, MaximumFrequencyAfterOperations)

| | |
|---|---|
| Problems | 36 |
| Must-know | 10 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_MoveZeroes.java` | Compact then fill tail |
| 2 | `C01_ContainerWithMostWater.java` | Greedy converge, drop shorter wall |
| 3 | `C02_Three3Sum.java` | Fix one index, converge on the rest |
| 4 | `C03_ImportantMinSubArrayExceedsSumSlidingWindow.java` | Variable window, shrink while valid |
| 5 | `C06_ImportantLongestSubStringWithoutRepeatingCharacter.java` | Last-seen index map, jump left |
| 6 | `C07_MaxConsecutiveOnesIII.java` | At most K violations window |
| 7 | `C10_CharacterReplacement.java` | Window minus max frequency <= K |
| 8 | `C11_SubarrayProductLessThanK.java` | Count subarrays ending at right |
| 9 | `D01_ImportantTrappingRainWater.java` | Converging pointers with running maxima |
| 10 | `D02_MinimumWindowSubstring.java` | Window with formed/required counter |

## Full practice order


### A — Building blocks

- `A01_RemoveElement.java` — Slow write pointer, fast scan
  - The single cleanest statement of the write-pointer primitive: keep what passes a test, overwrite in place.
- `A02_MoveZeroes.java` — Compact then fill tail **[must-know]**
  - Same primitive as RemoveElement plus the tail-fill step; this exact shape shows up inside dozens of harder problems.
- `A03_MoveElementToEnd.java` — Compact against arbitrary target
  - Generalises MoveZeroes from the literal 0 to any value, so it is the same drill with the constant removed.
- `A04_RemoveDuplicates.java` — Write pointer on sorted array
  - First case where the write pointer compares against the last kept element rather than a fixed constant.
- `A05_ReverseVowels.java` — Opposite ends, skip and swap
  - Introduces the second primitive, converging pointers, with the inner left<right guards that beginners drop.
- `A06_SortArrayByParity.java` — In-place two-way partition
  - Uses the converging pair from ReverseVowels to partition, which is the step before Dutch-national-flag and quicksort partitioning.
- `A07_UnionOfArrays.java` — Merge walk, advance smaller
  - Moves from one pointer pair inside one array to one pointer per array; every intersection/difference file below reuses this loop.
- `A08_LongestUniformSubstring.java` — Run-length scan with best-so-far
  - The minimal 'track a current stretch, compare to the best' bookkeeping that every longest-window problem later assumes.
- `A09_ImportantLongestUniformSubstring.java` — Run-length scan returning value
  - Same scan as the previous file but returns the substring, adding the end-of-loop final-run check people always forget.
- `A10_MaximumAverageSubarray.java` — Fixed-size rolling sum
  - The fixed-width window primitive (add right, subtract left) and the gateway to every variable window in tier C.

### B — Easy

- `B01_SquaresOfASortedArray.java` — Converge inward, fill from back
  - First problem where the converging pair produces output instead of swapping, and teaches filling a result array backwards.
- `B02_IsSubsequence.java` — Two pointers, two strings
  - Simplest case of two pointers advancing at different rates over two different sequences.
- `B03_ValidWordAbbreviation.java` — Two-string walk with numeric jump
  - Same two-string walk as IsSubsequence plus in-loop integer parsing and leading-zero validation; a common phone-screen string warm-up.
- `B04_SetDifferenceTwoPointers.java` — Merge walk emitting non-matches
  - Direct inversion of UnionOfArrays: emit on mismatch, skip on match, then drain both leftovers.
- `B05_IntersectionOfThreeSortedArrays.java` — Three-pointer merge walk
  - Extends the two-array merge to three pointers, where the 'advance the smallest' rule has to be spelled out as a chain of comparisons.
- `B06_IntersectionThreeArraysUnique.java` — Three-pointer merge, skip duplicates
  - Same three-pointer walk with explicit duplicate skipping, which is the exact habit Three3Sum needs later.
- `B07_CountPairsWhoseSumIsLessThanTarget.java` — Sort plus converging pair, block count
  - First use of the counting insight, count += (high - low): one comparison settles a whole block of pairs.
- `B08_SweetAndSavory.java` — Closest pair sum to target
  - Adds 'track the best so far' to pair search; the file's brute force is the baseline you must be able to replace with sort plus converging pointers.

### C — Medium

- `C01_ContainerWithMostWater.java` — Greedy converge, drop shorter wall **[must-know]**
  - The first problem requiring an exchange argument for why discarding a side is safe, and the direct prerequisite for Trapping Rain Water.
- `C02_Three3Sum.java` — Fix one index, converge on the rest **[must-know]**
  - Combines sorting, an outer loop, the converging pair and duplicate skipping; it is the pattern kSum and 3Sum-Closest are built from.
- `C03_ImportantMinSubArrayExceedsSumSlidingWindow.java` — Variable window, shrink while valid **[must-know]**
  - The canonical minimum-length template with the amortised O(n) argument; every remaining window problem is a variation on this loop.
- `C04_MinSubArrayExceedsSumSlidingWindow.java` — Variable window, shrink while valid
  - The same LeetCode 209 problem without the explanatory header, so it works as the recall drill immediately after the annotated version.
- `C05_ImportantLongestSubarrayWithSumKTwoPointer.java` — Max-length window plus prefix-sum map
  - Flips the template from minimum to maximum length, and shows exactly where the window breaks on negatives and the prefix-sum hash map takes over.
- `C06_ImportantLongestSubStringWithoutRepeatingCharacter.java` — Last-seen index map, jump left **[must-know]**
  - The most-asked sliding window in the industry, and it teaches jumping left in one hop instead of shrinking one step at a time.
- `C07_MaxConsecutiveOnesIII.java` — At most K violations window **[must-know]**
  - Names the abstraction that unlocks the whole next group: carry one counter of the rule you are allowed to break.
- `C08_LongestSubarrayAfterDeletingOne.java` — At most one zero, minus one
  - The K=1 instance of the previous file, plus the off-by-one trap that one element must always be deleted.
- `C09_FruitIntoBaskets.java` — At most two distinct, count map
  - Swaps the single counter for a frequency map keyed on distinctness, which generalises straight to at-most-K-distinct.
- `C10_CharacterReplacement.java` — Window minus max frequency <= K **[must-know]**
  - Hardest member of the at-most-K family: needs the running max-frequency trick and the insight that the window never has to shrink.
- `C11_SubarrayProductLessThanK.java` — Count subarrays ending at right **[must-know]**
  - Shifts the window from measuring a length to counting (right - left + 1) per step, the key to the whole exactly-K-equals-atMost(K)-minus-atMost(K-1) family.
- `C12_MaximumPointsFromCards.java` — Fixed window on the complement
  - Rewards recognising that taking from both ends is the same as sliding one fixed window over the middle or around the array.

### D — Hard

- `D01_ImportantTrappingRainWater.java` — Converging pointers with running maxima **[must-know]**
  - Extends Container With Most Water's exchange argument into the non-obvious claim that the shorter side's answer is already fully determined.
- `D02_MinimumWindowSubstring.java` — Window with formed/required counter **[must-know]**
  - The hardest standard variable window, and the payoff of the whole tier C ladder: multiset containment tracked in O(1) per move.
- `D03_MergeTwoSortedArrays.java` — In-place merge via gap sequence
  - Takes the merge walk from tier A to the O(1)-space version, where the Shell-style gap sequence is a genuinely non-obvious leap.
- `D04_MaxProfitWithWindowSize3.java` — Monotonic deque window minimum
  - Introduces the one structure the plain window cannot replace, and is the local form of Sliding Window Maximum.
- `D05_LongestSubstringWithKRepeatingChars.java` — Divide and conquer on bad chars
  - The essential counterexample: the validity condition is not monotonic, so the window fails and you must split on disqualified characters instead.
- `D06_MaximumFrequencyOfAnElementAfterPerformingOperationsI.java` — Difference-array sweep over targets
  - Last because it abandons the window entirely for an interval sweep, which only reads as natural once every window template above is automatic.

## Interview readiness

This is one of the strongest two-pointer/sliding-window folders I have reviewed at this level: all five load-bearing templates are present and each has more than one representative — write-pointer compaction, converging pointers (including both hard ones, Trapping Rain Water and Container With Most Water), the sorted-converge pair/triplet search with duplicate skipping, the shrink-while-invalid and grow-while-valid window skeletons, the "at most K violations" abstraction, the formed/required multiset counter (Minimum Window Substring), window counting via (right - left + 1), a monotonic deque, and — rarest of all — three problems that teach when the window does NOT apply (negatives, divide-and-conquer, difference-array sweep). Cross-topic coverage closes several apparent holes: Valid Palindrome (04-Strings/PalindromeSpecial), Sort Colors (01-Arrays/C02_Sort012), Sliding Window Maximum (07-Stack-Queue-Monotonic), Partition Labels and Boats To Save People (13-Greedy), and all the fast/slow linked-list problems (06-Linked-List), so none of those count as gaps. What is genuinely missing clusters in four places: fixed-window frequency matching on strings (the anagram-window family, absent from every folder), the exactly-K = atMost(K) - atMost(K-1) identity (his Binary Subarrays With Sum is solved with a prefix-sum hash map, so he has never written the subtraction), windows whose cost is computed from the window rather than counted (sort + cost = k*nums[r] - windowSum), and the O(1)-space reverse two-pointer string walk. Two quality issues inside the folder are worth fixing even though the problem names exist: SweetAndSavory is implemented as a brute-force O(n*m) double loop, which defeats the whole point of the sorted-converge pattern it is supposed to teach, and MergeTwoSortedArrays uses the Shell-sort gap method rather than the O(m+n) backward write-pointer merge that LC 88 actually wants — if an interviewer asks "merge sorted array in place," the gap method is a strange, slower answer to reach for. Close the four gaps below and this folder is comfortably at senior/staff bar for this topic.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Find All Anagrams in a String (LC 438) / Permutation in String (LC 567) | Fixed-size window over a string with a 26-slot frequency array and a `matches` counter updated in O(1) per slide — add right char, remove left char, compare counts incrementally rather than re-scanning the map each step. | This is the single most-asked sliding-window string problem after Longest Substring Without Repeating, and nothing in any of his 20 folders covers it — AnagramStrings (04-Strings) is the whole-string 26-array check with no window, and GroupAnagrams is a hashing problem. Minimum Window Substring gives him the formed/required idea but for a VARIABLE window with containment semantics; the anagram window is fixed-size with EXACT equality, and the O(1) incremental match-count update is a different piece of bookkeeping that people routinely get wrong under pressure (they re-compare the full array each slide and turn an O(n) solution into O(26n) while narrating it as O(n)). |
| high | Valid Palindrome II (LC 680) | Converging pointers with one allowed mismatch: on the first mismatch, branch into a helper that checks isPalindrome(left+1, right) OR isPalindrome(left, right-1). The 'at most one deletion' idea solved by branching rather than by a window counter. | He has plain Valid Palindrome (04-Strings/PalindromeSpecial, the alphanumeric-skip version) but not the one-deletion follow-up, which is a Meta/Amazon phone-screen staple and the far more common ask of the two. It is also the canonical place where 'allow one violation' is handled by a one-time branch instead of the shrink-window counter he already knows from LongestSubarrayAfterDeletingOne — candidates who only own the window template often try to force a window onto it and stall. Cheap to add, disproportionately likely to appear. |
| high | Subarrays with K Different Integers (LC 992), with Count Number of Nice Subarrays (LC 1248) as the drill | exactly(K) = atMost(K) - atMost(K-1): run the same grow-while-valid window twice and subtract, because 'exactly K' is not directly monotonic and so has no single valid window. | He has the atMost counting half (SubarrayProductLessThanK, CountPairsWhoseSumIsLessThanTarget) but has never written the subtraction that converts it into an exact count — and his one 'exactly' problem, BinarySubarraysWithSum in 05-Hashing-Prefix-Sum, is solved with a prefix-sum hash map, so the identity has never been exercised. This is the standard senior-level follow-up after any atMost-style window ('now count the subarrays with exactly K distinct / exactly K odd numbers'), and without the identity a candidate either goes O(n^2) or tries to maintain one window for an exactly-K condition, which cannot work. Grep confirms no `atMost` helper anywhere in the DSA tree. |
| high | Frequency of the Most Frequent Element (LC 1838) | Sort, then a grow-while-valid window whose cost is DERIVED from the window rather than counted: cost = nums[right] * windowSize - windowSum must stay <= k. Shrink when the derived cost exceeds the budget. | Every 'at most K violations' problem he owns counts discrete violations with a ++ counter (MaxConsecutiveOnesIII, CharacterReplacement, FruitIntoBaskets). This one computes the constraint arithmetically from the window contents, which is the step candidates miss. Note his MaximumFrequencyOfAnElementAfterPerformingOperationsI is LC 3346 solved with a difference-array sweep — a different problem and a different technique, so this is not a duplicate. It is also the natural bridge to the whole sort-then-window family (Minimum Difference Between Highest and Lowest of K Scores, Maximum Number of Tasks / budget windows) and gets asked a lot at Amazon and Google. |
| medium | Backspace String Compare (LC 844) | Two pointers walking both strings from the RIGHT with a lazy skip counter, so the comparison runs in O(1) extra space instead of building two stacks. | His two-strings-at-different-rates pair (IsSubsequence, ValidWordAbbreviation) both walk left-to-right; nothing in the folder walks two strings backward, and nothing covers 'defer the deletions instead of materializing them'. The interview shape is fixed: the stack answer is accepted in two minutes and then the interviewer asks for O(1) space, which is exactly the part he has not practised. Common Meta/Google phone screen, and the right-to-left skip loop with its boundary conditions is genuinely fiddly to write cold. |
| medium | Shortest Subarray with Sum at Least K (LC 862) | Prefix sums plus a monotonic increasing deque — the correct replacement for the shrinking window once negative numbers are allowed. | He already owns both halves separately: MinSubArrayExceedsSum (the positive-only shrinking window) and MaxProfitWithWindowSize3 (a monotonic deque), and his notes already record that windows break on negatives. This problem is precisely the synthesis, and it is the standard staff-level follow-up an interviewer reaches for after he nails Minimum Size Subarray Sum in three minutes — 'now the array can contain negatives.' Being able to say why the window fails is good; being able to produce the deque-over-prefix-sums answer is what separates the senior signal from the strong-mid one. |
| medium | Minimum Window Subsequence (LC 727) | Two-pointer forward scan to find a covering window, then a backward scan from that endpoint to tighten the start — order-preserving containment, unlike the frequency-map containment of Minimum Window Substring. | MinimumWindowSubstring covers multiset containment where order does not matter; this one requires the characters of t to appear IN ORDER inside the window, which kills the frequency-map approach entirely and needs the forward-then-shrink-backward walk (or the DP). It is a long-running Meta favourite and the obvious 'and what if order matters?' twist on a problem he has already solved, so it is a realistic live follow-up rather than a separate ask. |
| low | Find K Closest Elements (LC 658) | Converging pointers that shrink the full range from both ends until exactly k elements remain, discarding whichever end is farther from x — plus the O(log n) binary-search-on-window-start variant. | Genuinely absent: KClosestPointsToOrigin in 08-Heap-Priority-Queue is the heap problem on points, not this. It is a frequent Amazon/Google mid-round question and a good test of the 'discard the provably-worse side' instinct he has from ContainerWithMostWater applied to a shrink-to-size-k goal, with a tie-break rule (prefer the smaller element) that trips people up. Lower priority than the rest because if he reasons well he can likely derive the two-pointer version live; the binary-search follow-up is the part he would have to think about. |

