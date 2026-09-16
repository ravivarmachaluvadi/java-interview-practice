# 01-Arrays — must-know order

**Techniques in this topic:** Single-pass running state: carry one or two scalars (min-so-far, max-so-far, streak length, running sum) and update the answer on every step rather than only when a run ends — this is the spine of Best Time to Buy/Sell, Kadane, Zero-Filled Subarrays and Increasing Triplet, Prefix/suffix decomposition: answer at index i = something about everything left of i combined with something about everything right of i, computed in two linear passes instead of an O(n^2) double loop (Product Except Self, Best Seat, Pivot Index, Leaders, Partition Disjoint), Index-as-hash: when values are constrained to 1..n, the array is its own hash map — encode counts by adding n, mark presence by negating, or place each value at index value-1. This is the single highest-leverage family here and it unlocks Count Frequencies, Find All Duplicates, Find All Missing, Corrupt Pair and First Missing Positive, Cyclic sort as a named, reusable routine: the swap-until-settled loop with the nums[i] == nums[correctIdx] guard that both prevents infinite swapping on duplicates and proves O(n) termination — learn it once, then four problems become a two-line post-scan, Two pointers and in-place partitioning: Dutch national flag three-way partition, alternating sign placement, and the reverse-range primitive that composes into k-rotation and next-permutation, Sliding window with a variable-size window, valid only because all elements are positive so the sum is monotone in the window bounds — the amortised O(n) argument (each index enters and leaves once) is the thing to be able to say out loud, Cycle detection on an array viewed as a functional graph: Floyd tortoise-and-hare, why the meeting point is on the cycle but is not the cycle entry, and the mandatory second phase, Counting / voting invariants: Boyer-Moore majority and the XOR cancellation algebra — both are 'the wrong elements annihilate each other' arguments, and both need a stated precondition or a verification pass, Run and shape scanning: detecting strictly increasing then strictly decreasing stretches, where the whole difficulty is plateau handling and where to capture the boundary indices (Bitonic Subarray, Longest Mountain), Edge-case discipline: trailing runs never terminating, boundaries before the first and after the last element, empty input decided explicitly rather than by overflow accident, and overflow-safe accumulators

| | |
|---|---|
| Problems | 38 |
| Must-know | 10 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `B03_BestTimeToBuyAndSellStock.java` | Running min plus best-so-far |
| 2 | `B10_MajorityElement.java` | Boyer-Moore voting |
| 3 | `C01_KadaneSAlgorithm.java` | Kadane maximum subarray |
| 4 | `C05_ProductExceptSelf.java` | Prefix and suffix products |
| 5 | `C07_Sort012.java` | Dutch national flag partition |
| 6 | `C08_RotateArray.java` | Rotate by triple reversal |
| 7 | `C09_NextGreaterPermutation.java` | Pivot, swap, reverse suffix |
| 8 | `C10_SmallestSubarraySum.java` | Variable-size sliding window |
| 9 | `C17_FindDuplicate.java` | Floyd cycle on value pointers |
| 10 | `D01_FirstMissingPositive.java` | Cyclic sort with pigeonhole bound |

## Full practice order


### A — Building blocks

- `A01_ArraySortedOrNot.java` — Adjacent-pair single scan
  - The base loop shape (compare arr[i] with arr[i+1], bail early) that every later scan is a variation of.
- `A02_MonotonicArray.java` — Two boolean flags, one pass
  - Same adjacent-pair scan as A01, but shows you can test two hypotheses at once instead of looping twice.
- `A03_MaxConsecutiveOnes.java` — Running streak with reset
  - Introduces the streak counter and the trailing-run bug; C10 is this exact loop with one line changed.
- `A04_SecondSmallest.java` — Track two extremes, one pass
  - First problem where the state is two correlated scalars and the demote-then-assign order matters.
- `A05_ThreeLargestNumbers.java` — Track three extremes, cascading
  - Generalises A03 to k=3 and makes the cascade explicit; after this you can write the k-extremes pattern from memory.
- `A06_LeftRotateArrayByOne.java` — In-place shift with one temp
  - The minimal in-place mutation primitive; the naive baseline that B11's reversal trick has to beat.
- `A07_PrintArrayInCyclic.java` — Modulo wraparound indexing
  - Teaches index % n as the wrap operator, which is the arithmetic behind rotation and cyclic placement.
- `A08_FirstMiddleLast.java` — Digit extraction by div/mod
  - Standalone arithmetic primitive (n/100, (n/10)%10, n%10); park it here so it never costs you time later.
- `A09_CountFrequencies.java` — In-place counting via value encoding
  - The most advanced primitive in this tier: array-as-its-own-hash-map with add-n / divide-n encoding, which is the idea C05, C06, C15 and D02 all rest on.

### B — Easy

- `B01_PlusOne.java` — Right-to-left carry propagation
  - Easiest full problem in the set and the first time you scan backwards with early return; the all-nines case is the only edge.
- `B02_KidsWithCandies.java` — Find max, then compare pass
  - The simplest two-pass shape: compute a global aggregate first, then answer each index against it.
- `B03_BestTimeToBuyAndSellStock.java` — Running min plus best-so-far **[must-know]**
  - Turns A03's extreme-tracking into an answer-tracking idiom; it is the direct ancestor of Kadane and appears in some form in almost every screen.
- `B04_PivotIndex.java` — Total sum minus running prefix
  - First use of prefix sums, and the leftSum == total - leftSum - nums[i] identity that removes the second array entirely.
- `B05_LeadersInAnArray.java` — Right-to-left running max
  - The mirror of B02: scanning from the right with a suffix extreme, which C08 and C03 both reuse.
- `B06_CheckIfAnArrayIsConsecutive.java` — Min/max span plus duplicate set
  - First problem framed on the value-to-index relationship (max - min + 1 == n), the mental model cyclic sort formalises.
- `B07_AlternatePositiveNegative.java` — Two write pointers into output
  - Introduces independent write cursors and partitioning into buckets; the warm-up before the in-place three-way partition in C02.
- `B08_ArrayTransformation.java` — Simultaneous update until stable
  - Teaches read-from-snapshot-write-to-copy, the trap of sequential in-place updates, and a converge-until-no-change loop.
- `B09_MissingRanges.java` — Gap enumeration with boundaries
  - Pure edge-case discipline: before-first, between-pairs, after-last, and empty input — nothing clever, everything easy to get wrong.
- `B10_MajorityElement.java` — Boyer-Moore voting **[must-know]**
  - The first genuine invariant argument in the set (non-majority elements cancel out), and you must be able to state that the result is only a candidate without the guarantee.

### C — Medium

- `C01_KadaneSAlgorithm.java` — Kadane maximum subarray **[must-know]**
  - B02's running state applied to sums; the canonical best-subarray-ending-here recurrence that every DP-on-arrays question builds from, and the all-negatives case is the standard follow-up.
- `C02_NumberOfZeroFilledSubarrays.java` — Count subarrays ending at i
  - A05's streak counter plus one accumulator line; teaches the count-by-right-endpoint trick that converts many O(n^2) enumerations to O(n).
- `C03_IncreasingTripletSubsequence.java` — Two sentinels, O(1) space
  - Combines A03's two-extreme state with a correctness argument you must be able to defend (why a stale 'first' is harmless, why comparisons are non-strict).
- `C04_PartitionArrayintoDisjointIntervals.java` — Left max versus running max
  - Needs both B06's running max and a deferred second max; the insight that the answer only moves when the invariant breaks.
- `C05_ProductExceptSelf.java` — Prefix and suffix products **[must-know]**
  - The clean statement of two-pass decomposition, with the no-division and O(1)-extra-space constraints that make it a perennial senior-level question.
- `C06_BestSeat.java` — Left and right distance passes
  - Same prefix/suffix shape as C03 but over distances, and the unbounded-edge case is the whole difficulty.
- `C07_Sort012.java` — Dutch national flag partition **[must-know]**
  - The three-way in-place partition with four loop-invariant regions; the reason mid does not advance on a 2 is the single most-asked follow-up in this file set.
- `C08_RotateArray.java` — Rotate by triple reversal **[must-know]**
  - Supplies the reverse-a-range primitive and the k % n normalisation that C07 composes with directly; also the cleanest O(1)-space-versus-extra-array trade-off to talk through.
- `C09_NextGreaterPermutation.java` — Pivot, swap, reverse suffix **[must-know]**
  - Builds literally on B11's reverse; a three-step algorithm nobody derives under pressure, so it must be memorised, and it recurs constantly as LeetCode 31.
- `C10_SmallestSubarraySum.java` — Variable-size sliding window **[must-know]**
  - The gateway to the entire sliding-window family, and the amortised O(n) argument despite a nested loop is exactly what an interviewer probes.
- `C11_LongestBitonicSubarrayProblem.java` — Up-then-down run scanning
  - First problem where the state is a shape rather than a value; plateau skipping and where to capture the end index are the traps.
- `C12_LongestMountainInArray.java` — Find peak, expand outward
  - The same bitonic shape as C12 attacked from the peak instead of the left edge, plus the strictness and length >= 3 constraints.
- `C13_FindAllDuplicatesInAnArray.java` — Cyclic sort placement
  - The first clean statement of the cyclic-sort loop from A02's encoding idea; the equality guard that stops infinite swaps is the point of the file.
- `C14_FindAllMissedNumbers.java` — Sign marking in place
  - The alternative in-place marking to C05's swapping — same constraints, different trade-off (values survive as absolute values, order is preserved).
- `C15_ImportantFindCorruptPair.java` — Cyclic sort, single mismatch
  - Applies C05's placement loop and then reads both the duplicate and the missing value off one wrong index; the payoff that makes cyclic sort worth memorising.
- `C16_CycleLengthInArray.java` — Floyd tortoise and hare
  - Introduces cycle detection on an array as a functional graph and the reason a second phase is required, before C04 puts it to work.
- `C17_FindDuplicate.java` — Floyd cycle on value pointers **[must-know]**
  - Needs C14's machinery plus the non-obvious reframing of nums as linked-list pointers; the O(1)-space, do-not-modify-input constraint is what makes it a standard senior question.

### D — Hard

- `D01_FirstMissingPositive.java` — Cyclic sort with pigeonhole bound **[must-know]**
  - Continues the C05/C15 thread but adds the answer-is-in-[1, n+1] argument and out-of-range skipping; the classic O(n) time, O(1) space hard that interviewers use as a ceiling test.
- `D02_FindMissingRepeatingNumbers.java` — XOR bit-partition into two groups
  - Re-solves C15's problem with a genuinely different tool — xor & -xor to isolate a differing bit and split the numbers — so it lands last, once the cyclic-sort solution is already fluent.

## Interview readiness

This is one of the more complete array folders I would expect to see from a senior candidate, and the wider tree closes most of what is not here. The index-as-hash and cyclic-sort families — the two things that actually break people in a 45-minute array round — are covered to their end state (duplicates, missing numbers, corrupt pair, find-the-duplicate by Floyd, first missing positive, missing-and-repeating together), and the core single-pass, prefix/suffix, Dutch-flag, reverse-range and shape-scanning techniques are all present with the edge-case discipline that separates a pass from a strong pass. Checking the sibling folders matters here: Two Sum, 3Sum, Trapping Rain Water, Container With Most Water, Merge Intervals, Subarray Sum Equals K, Longest Consecutive Sequence, Sliding Window Maximum, Quickselect, Search in Rotated Sorted Array and the matrix classics all already exist under 02, 03, 05, 07, 08, 15, 16 and 18, so the usual "canonical array problem" checklist is genuinely satisfied and I am not counting those as gaps. What remains is the follow-up layer rather than the first question: he can open almost any array round confidently, but three of his strongest problems (Kadane, Boyer-Moore majority, merge sort) have a standard second-question variant he has not written — circular Kadane, the n/3 two-counter generalisation, and merge-sort-as-a-counter for inversions. Divide-and-conquer counting is the one whole technique family with zero representation anywhere in the tree. Close those and the array topic is done; leave them and the likely failure mode is stalling on the interviewer's "now what if..." rather than on the problem itself.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Maximum Sum Circular Subarray (LC 918) | Kadane run twice: normal max subarray, plus total sum minus the minimum subarray for the wrap-around case, with the all-negative array handled as an explicit special case | He has C01_KadaneSAlgorithm, and 'what if the array wraps around?' is the single most common live follow-up to it. The wrap case is not an extension of the same loop — it needs the complement argument (the best wrapping subarray is everything except the worst non-wrapping one) and it silently returns 0 on an all-negative array unless that branch is written. Nothing in the tree covers the circular variant. |
| high | Count Inversions in an Array (and its harder twin, Reverse Pairs, LC 493) | Modified merge sort: count cross-pairs during the merge step, O(n log n) | This is the only array technique family with no representation anywhere in the workspace — merge sort exists in 18-Sorting-Searching but purely as a sort, never as a counting device. 'Count pairs i<j with a[i]>a[j] faster than O(n^2)' is a standard senior/staff question at product companies, and it is the gateway to Count of Smaller Numbers After Self. Without practising it he has no answer to any counting problem that is not solvable by a hash map or a prefix sum. |
| high | Majority Element II — all elements appearing more than n/3 times (LC 229) | Generalised Boyer-Moore with two candidates and two counters, plus a mandatory second verification pass | B05_MajorityElement establishes the single-counter voting invariant, and interviewers routinely push straight to the n/3 version to see whether the candidate understood the cancellation algebra or memorised the loop. The two-candidate version has three real traps: the order of the if-branches (check both candidate matches before either zero-counter branch), the at-most-two-answers bound, and the fact that verification is not optional here because a majority is no longer guaranteed. |
| medium | Shortest Unsorted Continuous Subarray (LC 581) | Two scans carrying a running max left-to-right and a running min right-to-left; the last violation index in each direction gives the boundaries | It sits exactly at the intersection of two themes he already owns (prefix/suffix decomposition and running scalars) but the answer is a pair of boundary indices, not a value, and the sorted-already case must return 0 rather than a negative length. It is a common 20-minute screen question and a clean test of whether the prefix/suffix idea is understood or only pattern-matched. |
| medium | Maximum Sum of Two Non-Overlapping Subarrays (LC 1031), or equivalently Best Time to Buy and Sell Stock III | Compose prefix and suffix passes with Kadane: best answer ending at or before i from the left, best starting at or after i+1 from the right, then split at every i | He has prefix/suffix decomposition and he has Kadane, but never the composition of the two, which is the actual staff-level move — 'best single X' becomes 'best two disjoint X' by fixing the split point. 12-Dynamic-Programming has the one-deletion variant but not the two-subarray split, so the technique is unpractised. |
| medium | H-Index (LC 274) | Counting-sort style bucket by capped citation count, then a suffix scan for the largest h with at least h papers; O(n) time, O(n) space, no sorting | A frequently asked question at product companies where the sorted O(n log n) answer is expected in two minutes and the O(n) bucket answer is what distinguishes a senior. It also introduces value-bucketing with a cap, which is a different use of the array-as-table idea from the index-as-hash family he already knows, and nothing in the folder covers it. |
| low | Maximum Product of Three Numbers (LC 628) | Track the three largest and the two smallest in one pass; answer is max(top3 product, two-smallest times largest) | A04_ThreeLargestNumbers already builds the three-maxima scan, so this is a small addition, but the negative-number case (two large negatives multiply to a large positive) is a trap that catches candidates who reach for the three-largest answer reflexively. Cheap to add and it turns an existing solution into a complete one. |

