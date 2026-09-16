# 03-Binary-Search — must-know order

**Techniques in this topic:** Boundary search (lower bound / upper bound): the one template that answers first-occurrence, last-occurrence, floor, ceil and insert-position, Binary search on the answer space: guess a value, write a monotone feasibility predicate, shrink toward the first/last feasible guess (square root, ribbons, Koko, ship packages, magnetic force), Rotated sorted arrays: find the pivot, then decide which half is sorted; and how duplicates destroy that invariant, Binary search without a sorted array: any locally monotone or parity-based invariant is enough (peak element, single non-duplicate), Precompute-then-search: build a prefix sum or a rolling-state array, then binary search it (weighted random pick, top voted candidate), Partitioning two sorted arrays: the O(log min(m,n)) kth-element / median technique, Unimodal (valley) functions: comparing f(mid) with f(mid+1) instead of a yes/no predicate

| | |
|---|---|
| Problems | 22 |
| Must-know | 6 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_AImportantLowerAndUpperBounds.java` | Lower/upper bound template |
| 2 | `C01_FindPeakElement.java` | Binary search on unsorted input |
| 3 | `C02_MinIndexInRotatedSortedArray.java` | Find the rotation pivot |
| 4 | `C04_ImportantSearchInRotatedSortedArray.java` | Identify the sorted half |
| 5 | `C09_KokoEatingBananas.java` | Minimum feasible answer |
| 6 | `D02_ImportantMedianOfTwoSortedArrays.java` | Median by partitioning |

## Full practice order


### A — Building blocks

- `A01_AImportantLowerAndUpperBounds.java` — Lower/upper bound template **[must-know]**
  - The single primitive every other file in this folder reuses: keep a candidate answer, shrink the window, never return -1 from the loop body.
- `A02_FirstAndLastOccurrence.java` — Two bounds for a range
  - Immediately after the template, because it is that template called twice; proves he can turn the primitive into an interview answer (LC 34).
- `A03_FloorAndCeilInSortedArray.java` — Predecessor and successor
  - Same two searches restated on values rather than indices, which is how the bound idea shows up inside larger problems.
- `A04_SquareRoot.java` — Binary search on a value range
  - First problem with no array at all; introduces searching an answer range, the prerequisite for the whole Koko/ship/ribbons family.

### B — Easy

- `B01_Search2DMatrix.java` — Flatten 2D index to 1D
  - Easiest step past the plain template: the only new idea is the div/mod index mapping..
- `B02_RandomPickWithWeight.java` — Prefix sums plus lower bound
  - First precompute-then-search problem; needs nothing beyond lower bound but teaches that the searched array is often one you build.
- `B03_KthMissingPositive.java` — Search a derived quantity
  - Next step up: the array is not what you binary search on, arr[i]-(i+1) is; the same reframing later powers the harder answer-space problems.
- `B04_CuttingRibbons.java` — Maximize x with count(x) >= k
  - The gentlest answer-space search, because the feasibility check is one summation with no simulation; the warm-up for Koko.

### C — Medium

- `C01_FindPeakElement.java` — Binary search on unsorted input **[must-know]**
  - Opens the medium tier because it breaks the assumption everything before it relied on: sortedness is not required, only a monotone decision rule.
- `C02_MinIndexInRotatedSortedArray.java` — Find the rotation pivot **[must-know]**
  - The root of the rotated-array cluster; the next three files all assume you can locate the pivot in O(log n).
- `C03_SecondSmallestInRotatedArray.java` — Pivot plus one, wrapping
  - Sits directly after pivot-finding as its cheapest consequence, and forces the modular wrap-around edge case.
- `C04_ImportantSearchInRotatedSortedArray.java` — Identify the sorted half **[must-know]**
  - The canonical rotated-array question (LC 33) and the reason pivot logic matters; one of the most asked binary search problems anywhere.
- `C05_SearchInRotatedSortedArray.java` — Identify the sorted half
  - Near-duplicate of the file above with the half-selection reasoning spelled out in comments; use it as the re-derivation pass, not a new problem.
- `C06_SearchInARotatedSortedArrayII.java` — Duplicates break the invariant
  - Must come after LC 33 because its whole lesson is which guarantee duplicates remove, degrading the search to O(n) worst case.
- `C07_SingleNonDuplicateElement.java` — Index-parity invariant
  - Closes the unsorted-invariant thread started by peak element: the pairing parity, not the values, is what stays monotone.
- `C08_TopVotedCandidate.java` — Precompute state, search timestamps
  - Last of the search-an-existing-array problems and the most design-flavoured: the binary search is trivial, the O(n) preprocessing is the interview.
- `C09_KokoEatingBananas.java` — Minimum feasible answer **[must-know]**
  - Opens the answer-space block and is the template he should recite in interviews: define bounds, write canDo(x), search for the first true.
- `C10_CapacityToShipPackagesWithinDDays.java` — Answer search plus greedy check
  - Same shape as Koko but the predicate is a greedy partition simulation, and the low bound must start at max(weights) rather than 1.
- `C11_MagneticForceBetweenTwoBalls.java` — Maximize the minimum gap
  - Last of the answer-space family because the direction flips: you search for the last feasible value, which is where people write the wrong branch.

### D — Hard

- `D01_KthElementOf2SortedArrays.java` — Partition two sorted arrays
  - Learn the general partition-and-check machinery here, where there is no even/odd median case to distract from the invariant l1<=r2 && l2<=r1.
- `D02_ImportantMedianOfTwoSortedArrays.java` — Median by partitioning **[must-know]**
  - The k=(m+n)/2 special case of the file above plus parity handling; the standard hard binary search question at senior and staff level.
- `D03_MinimumCostToMakeArrayEqual.java` — Search a unimodal function
  - Placed last because it abandons the yes/no predicate entirely and compares f(mid) with f(mid+1) to descend a convex cost curve.

## Interview readiness

This is one of the strongest binary-search folders I have reviewed for a senior/staff loop: the boundary template, binary-search-on-the-answer (five separate feasibility problems, including maximize-the-minimum), the full rotated-array family with and without duplicates, the no-sorted-array invariants (peak, single non-duplicate), precompute-then-search, the O(log min(m,n)) two-array partition, and even a unimodal/valley problem are all present, and I confirmed the obvious adjacent problems are covered in neighbouring folders rather than genuinely missing (TimeBasedKeyValueStore.java and SnapshotArray.java in 19-Design-Data-Structures cover the design-plus-timestamp-search framing, CountNegativeNumbersInASortedMatrix.java in 16-Matrix already drills the staircase walk that Search a 2D Matrix II asks for, and ImportantJobSchedulingMaxProfit.java in 12-Dynamic-Programming already uses predecessor search inside a DP). I also deliberately did not flag Split Array Largest Sum, Book Allocation/Painter's Partition, Minimum Days to Make m Bouquets or Minimum Speed to Arrive on Time, because their predicates are identical to CapacityToShipPackagesWithinDDays.java and KokoEatingBananas.java — adding them would be padding, not coverage. The one real structural hole is that every feasibility predicate he owns is boolean ("can I finish in H hours?"), and he has never written a counting predicate ("how many values are <= x?") searched over a value range rather than an index range; that single missing idea is what LC 378, LC 719 and the median-of-a-row-sorted-matrix family all rest on, and it is the most likely place a Google or Meta interviewer breaks him. The second real hole is that his LIS file (12-Dynamic-Programming/LengthOfLIS.java) is O(n^2) only, so the near-universal "now make it n log n" follow-up would land on nothing. Fix those two plus the unbounded/exponential-search trick and this folder is genuinely interview-complete; the remaining items below are polish.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Kth Smallest Element in a Sorted Matrix (LC 378) — and its twin, Median of a Row-wise Sorted Matrix | Binary search over the VALUE range (lo = matrix[0][0], hi = matrix[n-1][n-1]) with a counting predicate countLessOrEqual(x), not a boolean feasibility predicate; the count is itself computed in O(n) by a staircase walk. | This is the single missing idea in an otherwise complete folder. Every predicate he owns returns yes/no (Koko, ship packages, ribbons, magnetic force); none returns a count, and none searches a value range that is not an array index. KthElementOf2SortedArrays is partition-based and does not transfer. This problem is asked constantly at Google, Meta and Amazon, and it is the gateway to LC 668, LC 1201 and LC 719. |
| high | Longest Increasing Subsequence in O(n log n) (LC 300 follow-up / patience sorting) | Maintain a 'tails' array and use lower_bound to find the first tail >= nums[i] and overwrite it; binary search used as a subroutine inside a DP rather than as the whole algorithm. | His only LIS file is the O(n^2) DP. 'Can you do it in n log n?' is one of the most reliably asked follow-ups in the entire interview canon, and refusing it reads as a ceiling. It also unlocks Russian Doll Envelopes, Longest String Chain and Maximum Length of Pair Chain, which he has in other forms. He already has the lower-bound template in AImportantLowerAndUpperBounds — he just has never applied it to a non-array-lookup purpose. |
| medium | Search in a Sorted Array of Unknown Size / infinite sorted array (LC 702, exponential or galloping search) | Double the high bound (1, 2, 4, 8, ...) until the value at hi overshoots the target or the API returns out-of-bounds, then run an ordinary binary search on [hi/2, hi]; O(log p) where p is the target index. | Every problem in his folder hands him an explicit hi. He has never had to derive one. This is the standard Amazon/Google twist ('the array is a reader API with no length'), and it is also the honest answer whenever the answer space has no obvious upper bound. Without it he stalls on the very first line of the solution, which is a bad way to lose a phone screen over a 15-line problem. |
| medium | Find K-th Smallest Pair Distance (LC 719) | Binary search on the answer distance, where the feasibility check is a counting sliding window over the sorted array — countPairsWithDistanceAtMost(d) in O(n). | The staff-level escalation of LC 378: the predicate is a count AND the count itself requires a two-pointer sweep, so it fuses two of his folders. Interviewers use it specifically to separate people who memorised the Koko template from people who understand that the predicate is just any monotone function. He has the sliding-window half in 02-Two-Pointers-Sliding-Window and the answer-space half here, but has never combined them. |
| medium | Find K Closest Elements (LC 658) | Binary search over the WINDOW START index in [0, n-k], comparing x - arr[mid] against arr[mid+k] - x to decide which side to discard; O(log(n-k)) instead of the O(log n + k) expand-from-insertion-point approach. | He can almost certainly produce the two-pointer version (lower bound, then expand outward), but the interviewer's next sentence is always 'do it without the linear expansion'. Searching over the space of windows — where the comparison is between two elements k apart, not between an element and a target — is a template he has no analogue for anywhere in the folder. Very common at Amazon and Meta. |
| medium | Find a Peak Element II (LC 1901, 2D peak) | Binary search over columns: take the middle column, find its row-wise maximum, compare against the left and right neighbours, and discard half the columns; the invariant is that the retained half still contains a peak. O(m log n). | FindPeakElement.java gives him the 1D version, but the 2D lift is not mechanical — it requires stating and defending the invariant ('the half containing the larger neighbour must contain a peak'), which is exactly the argument a staff-level interviewer is probing for. It is also the classic trap where candidates propose searching both rows and columns and get an incorrect O(log m log n). Increasingly common at Google and Meta. |
| low | Find in Mountain Array (LC 1095) | Three chained binary searches — locate the peak, search the ascending prefix normally, then search the descending suffix with a reversed comparator — under a constrained API-call budget. | Mostly a composition of things he already has (FindPeakElement plus a standard search), so it is not a conceptual gap, but the reversed-comparator half is where people actually write the bug, and the 'you may call get() at most 100 times' framing forces him to justify the call count out loud. Worth one sitting as a correctness drill, not as new theory. Regularly asked at Amazon. |

