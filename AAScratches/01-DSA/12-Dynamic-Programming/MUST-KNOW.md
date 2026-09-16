# 12-Dynamic-Programming — must-know order

**Techniques in this topic:** 1D linear DP: the Fibonacci recurrence, and the ladder naive recursion to memo to tabulation to O(1) rolling space, Pick / not-pick recursion over an index, and converting that recursion tree into an (index, target) DP table, 0/1 knapsack family: subset sum, count subsets with a target, target sum with signs, partition into two halves, Unbounded knapsack family: rod cutting and both coin change forms (minimum coins vs. number of ways) - the 'stay at the same index' transition, Grid path DP: unique paths, min falling path, triangle, square submatrices, and the two-agent 3D lockstep variants, Two-string DP on an (i, j) grid: LCS, edit distance, distinct subsequences, regex matching - all the same table with a different transition, LIS family: O(n^2) DP over pairs, plus partial-order variants (string chain, Fibonacci subsequence), Kadane-style running state: max subarray product, max sum with one deletion - carry two states instead of one, Interval / partition DP (MCM shape): choose a split point k, add sentinels, and price the merge, State compression: DP keyed on a difference, on a character-frequency signature, or on a fixed-length window instead of an index

| | |
|---|---|
| Problems | 44 |
| Must-know | 12 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A03_ClimbingStairs.java` | Fibonacci as O(1) rolling DP |
| 2 | `A04_PrintAllSubSets.java` | Pick / not-pick recursion tree |
| 3 | `A07_knapsack01.java` | 0/1 knapsack (index, capacity) |
| 4 | `B01_UniquePaths.java` | Grid counting DP |
| 5 | `B03_HouseRobber.java` | Non-adjacent 1D DP |
| 6 | `C02_MaxProductSubArray.java` | Prefix/suffix running product |
| 7 | `C09_CoinChangeMinimum.java` | Unbounded knapsack, minimise |
| 8 | `C13_LongestCommonSubsequenceTabulation.java` | LCS DP table |
| 9 | `C15_EditDistance.java` | Three-way transition on LCS grid |
| 10 | `C16_LongestPalindrome.java` | Longest palindromic substring |
| 11 | `C17_LengthOfLIS.java` | O(n^2) LIS DP |
| 12 | `C21_MCM.java` | Interval DP, split at k |

## Full practice order


### A — Building blocks

- `A01_Fibonacci.java` — Naive recursion, overlapping subproblems
  - The entry point: an exponential recursion that visibly recomputes the same state, which is the only motivation DP ever needs.
- `A02_FibonacciNumber.java` — Same recurrence, memo drill
  - A duplicate of the previous file on purpose - use it to run the full ladder (memo, tabulation, two variables) on a recurrence you already trust.
- `A03_ClimbingStairs.java` — Fibonacci as O(1) rolling DP **[must-know]**
  - The first time the recurrence is hidden inside a word problem, and the file already shows the two-variable space optimisation every later 1D problem reuses.
- `A04_PrintAllSubSets.java` — Pick / not-pick recursion tree **[must-know]**
  - The single most reused skeleton in this folder - every subset, knapsack and subsequence file below is this recursion with a parameter bolted on.
- `A05_CountSubsequenceWithTargetSum.java` — Pick/not-pick carrying a target
  - Adds the second state dimension to the previous recursion: now the branch returns a count, not a printout, which is what makes it memoisable.
- `A06_SubsetSumEqualsToTarget.java` — Boolean subset sum
  - Same skeleton again with OR instead of addition - do it right after the counting version so you see that the transition, not the structure, is what changes.
- `A07_knapsack01.java` — 0/1 knapsack (index, capacity) **[must-know]**
  - The canonical (index, capacity) state; once you own this template, subset sum, target sum and partition problems are all re-labellings of it.
- `A08_RodCuttingProblem.java` — Unbounded choice, brute force
  - Shows the one structural change from 0/1: an item can be reused, so the recursion stays at the same index instead of moving on.
- `A09_RodCuttingProblemUsingMemoization.java` — Unbounded knapsack memoised
  - Turns the previous file into a real DP table and is the exact template both coin change problems in tier C are built on.
- `A10_CommonSubSequence.java` — Two-string brute force recursion
  - The two-pointer-over-two-strings recursion in its rawest form - the shape behind LCS, edit distance and regex matching later.
- `A11_LongestIncreasingSubsequence.java` — Take/skip with a prev constraint
  - Introduces a conditional take (only if larger than the last picked value), the extra state that makes LIS harder than plain subsets.

### B — Easy

- `B01_UniquePaths.java` — Grid counting DP **[must-know]**
  - Opens the grid dimension: dp[i][j] built from the cell above and the cell left, the base every grid problem in tier C inherits.
- `B02_OptimalPath.java` — Grid max-path accumulation
  - Same grid traversal as unique paths but maximising a value instead of counting, and it does it in place - the space trick used again in Triangle.
- `B03_HouseRobber.java` — Non-adjacent 1D DP **[must-know]**
  - The standard warm-up that appears constantly: max(skip, take + dp[i-2]), and the file already includes the O(1) rolling version to recite.

### C — Medium

- `C01_NumDecodings.java` — 1D DP with validity checks
  - First step past pure Fibonacci - same two-term recurrence, but the zero and >26 edge cases are what interviewers actually score here.
- `C02_MaxProductSubArray.java` — Prefix/suffix running product **[must-know]**
  - The Kadane family entry and a constant interview question; sign flips and zeros break the naive running-max, which is the whole lesson.
- `C03_MaximumSubarraySumWithOneDeletion.java` — Two-state Kadane
  - Sits right after the product version because it is the same running scan with a second state (deleted / not deleted) carried alongside.
- `C04_MinimumFallingPathSum.java` — Grid min path, three moves
  - First grid problem where a cell has three predecessors and the answer is a min over the whole last row, not a single corner.
- `C05_TriangleV2.java` — Bottom-up on a triangular grid
  - Teaches bottom-up direction on a variable-width grid using one 1D array - the cleanest form of the pattern, so take it before the variant.
- `C06_Triangle.java` — Rolling-array space optimisation
  - The same problem as the previous file, kept so you can practise collapsing a 2D table to two rows and then to one.
- `C07_CountSquareSubmatricesWithAllOnes.java` — Grid DP storing a size
  - The state stops being a path cost and becomes 'largest square ending here' - the min-of-three-neighbours trick that also solves Maximal Square.
- `C08_TargetSumCountWays.java` — Signs reframed as subset sum
  - The first real re-labelling exercise: +/- assignment becomes count-subsets-with-sum (totalSum - target)/2, straight off the tier A template.
- `C09_CoinChangeMinimum.java` — Unbounded knapsack, minimise **[must-know]**
  - The most asked unbounded-knapsack problem; needs the memoised rod-cutting transition plus a clean infinity sentinel for unreachable amounts.
- `C10_CoinChangeII.java` — Unbounded knapsack, count ways
  - Placed after the minimise version so the contrast is sharp: identical recursion, but sum instead of min, and combinations not permutations.
- `C11_CoinChange.java` — Count ways with modulo
  - Despite the filename and header, the code counts ways - useful as a third pass on the same recurrence, and as a lesson in reading code over comments.
- `C12_LongestCommonSubsequence.java` — LCS recursion (i, j)
  - The recursive form first: match means 1 + diagonal, mismatch means max of two moves. Memoise it yourself before opening the tabulated file.
- `C13_LongestCommonSubsequenceTabulation.java` — LCS DP table **[must-know]**
  - This is the version you write in an interview, and the worked table in the comment is the artefact you draw on the whiteboard to explain it.
- `C14_LongestPalindromicSubsequence.java` — LCS with the reversed string
  - First payoff from owning LCS: reverse the string and call LCS, or solve it as an interval recursion - both framings are worth saying out loud.
- `C15_EditDistance.java` — Three-way transition on LCS grid **[must-know]**
  - Same table as LCS with insert/delete/replace transitions; a perennial question and the one that proves you really understand the (i, j) grid.
- `C16_LongestPalindrome.java` — Longest palindromic substring **[must-know]**
  - Asked constantly. The file gives Manacher's O(n), but know expand-around-centre as the interview answer and Manacher as the follow-up.
- `C17_LengthOfLIS.java` — O(n^2) LIS DP **[must-know]**
  - Converts the tier A brute force into the real DP, and is the base you extend to the O(n log n) patience/binary-search version interviewers push for.
- `C18_LongestStringChain.java` — LIS over a partial order
  - LIS where 'comes before' means 'is a predecessor string'; teaches sorting by the DP dimension and memoising in a HashMap instead of an array.
- `C19_LengthOfLongestFibonacciSubsequence.java` — DP keyed on a pair of indices
  - The state becomes a pair (i, j), not a single index - the last step before the multi-dimensional states in tier D.
- `C20_PalindromePartitioning.java` — Partition enumeration with backtracking
  - Introduces cutting a string at every valid position, which is exactly the front-partition idea that interval DP then optimises.
- `C21_MCM.java` — Interval DP, split at k **[must-know]**
  - The archetype for every interval problem: loop k across (i, j), price the merge, recurse both sides. Nothing in tier D's interval half works without it.

### D — Hard

- `D01_MinimumCostToCutTheStick.java` — MCM with sentinel padding
  - The gentlest hard interval problem - MCM's recurrence plus the non-obvious step of sorting the cuts and padding with 0 and n.
- `D02_CountOfDistinctSubsequences.java` — Counting on the LCS grid
  - First hard two-string problem: on a match you must add both branches (use it, skip it), which is the counting variant of the LCS transition.
- `D03_RegularExpressionMatchingMemo.java` — Two-string matching with '*'
  - Same (i, j) grid again, but '*' branches into zero-or-more, and the base cases are where nearly everyone fails - do it after distinct subsequences.
- `D04_CountPalindromicSubsequences.java` — Fixed-length window as state
  - Pushes state design: the state is (index, first char, second char, length) rather than a range, which is a genuinely non-obvious modelling leap.
- `D05_ImportantJobSchedulingMaxProfit.java` — Weighted interval scheduling
  - Sort by end time, then binary search for the last compatible job - the first problem here where the DP only works after the right ordering.
- `D06_TallestBillboard.java` — DP keyed on a difference
  - The insight is keying the table on the difference between two sums rather than on the sums themselves; a strong staff-level signal if you find it.
- `D07_StickersToSpellWord.java` — Memo on a frequency signature
  - State compression: the key is a character-count array serialised to a string (bitmask in the classic form) instead of an index.
- `D08_CherryPickII.java` — Two agents in lockstep, 3D DP
  - The multi-agent idea in its clearer form - both robots share a row, so the state is (row, col1, col2) with nine joint transitions.
- `D09_CherryPickup.java` — Two traversals as one walk
  - The hardest here: it needs the reduction of a there-and-back trip into two simultaneous forward walks indexed by step count, plus blocked-cell handling.

## Interview readiness

This is one of the stronger DP folders I've seen for interview prep: 44 files covering nearly every template an interviewer draws on — 1D linear, pick/not-pick, 0/1 and unbounded knapsack, grid and 3D lockstep grids, two-string (i,j) tables including regex and distinct subsequences, LIS with partial-order variants, Kadane with two carried states, interval/MCM partition, and even the exotic tail (TallestBillboard, StickersToSpellWord, CherryPickup II). The gaps are not whole missing families so much as three specific things that get asked constantly and that this folder cannot answer today. First, the *front-partition* template — dp[i] = best over a cut j, then recurse on the suffix — is absent: the only Palindrome Partitioning file here is the LC 131 backtracking version, and Word Break does not exist anywhere in the 20-folder workspace, so the single most-asked DP-on-strings question would be a cold start. Second, the buy/sell state-machine family is entirely missing from DP (only greedy Stock I/II live in 01-Arrays and 13-Greedy), so "index + holding + transactions left" — the standard way interviewers push a candidate from a 1D array DP into multi-dimensional state — has never been practiced. Third, everything here stops at computing the *value*: LIS exists only as brute force plus O(n²) with no patience-sorting version, and no file anywhere walks a filled table backwards to reconstruct the actual answer, which is the usual senior follow-up ("now print the subsequence, not its length"). Close those three and the remaining gaps (Burst Balloons' reverse framing, longest common substring, bitmask-over-subsets) are polish rather than risk. Two mechanical notes while he's in here: HouseRobber.java declares rob(int[]) twice and will not compile, and its space-optimized copy has `if (nums == null && nums.length == 0)` where the `&&` should be `||` — an NPE waiting on an empty array, and exactly the kind of thing an interviewer notices when a candidate pastes a "known" solution.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Word Break (LC 139) | Front-partition DP over string prefixes: dp[i] = OR over j of (dict contains s[j..i) AND dp[j]), with a HashSet or trie for lookup; O(n^2) substrings. | Not present anywhere in the 20-folder workspace — I grepped all of 01-DSA. It is arguably the single most-asked DP-on-strings question at product companies (Amazon, Google, Meta phone screens), and it is the gateway to Word Break II and to the whole 'partition a sequence at a cut point' family. His folder has the interval/MCM shape (split at k inside a range) but nothing with the prefix-cut shape, so he would have to derive the recurrence live under time pressure. |
| high | Best Time to Buy and Sell Stock III / IV, plus Cooldown and Transaction Fee (LC 123 / 188 / 309 / 714) | State-machine DP: dp[index][holding][transactionsLeft], then collapse to O(k) rolling variables; cooldown adds a skip-a-day transition, fee adjusts the sell edge. | The only stock files in the workspace are the greedy one-pass versions in 01-Arrays and 13-Greedy — there is no DP formulation at all. This is the canonical way an interviewer escalates: start at Stock II (greedy), then say 'at most two transactions' and watch whether the candidate reaches for an extra state dimension. He has practiced adding a second carried value (MaxProductSubArray, MaximumSubarraySumWithOneDeletion) but never a boolean state plus a counter, and never the k-transaction generalization. |
| high | Longest Increasing Subsequence in O(n log n) (LC 300 follow-up, and Russian Doll Envelopes LC 354) | Patience sorting / tails array: maintain the smallest tail for each length and binary-search the replacement position; length of tails is the answer. | LengthOfLIS.java is O(n^2) only and LongestIncreasingSubsequence.java is brute-force recursion — no binary-search version exists. 'Can you do better than O(n^2)?' is the standard follow-up on essentially every LIS ask, and Russian Doll Envelopes (sort by width, LIS on height with the descending-height tiebreak) is unsolvable in the time limit without it. This is a 20-line addition that removes a very likely mid-interview stall. |
| high | Palindrome Partitioning II — minimum cuts (LC 132) | Front-partition DP with precomputed isPalindrome[i][j]: dp[i] = 1 + min over j of dp[j+1] where s[i..j] is a palindrome; O(n^2). | PalindromePartitioning.java in this folder is the LC 131 backtracking enumeration, which is a different problem — it does not build a DP table at all. LC 132 is the standard escalation from 131 and the textbook front-partition template (same skeleton as Partition Array for Maximum Sum, LC 1043). He has the MCM split-at-k shape but not the prefix-cut shape, so this and Word Break together close the same structural hole from two angles. |
| medium | Longest Common Substring (contiguous), and Shortest Common Supersequence / printing the actual LCS (LC 1092) | Two changes to the LCS table he already has: on mismatch reset dp to 0 and track a global max (substring), and walk the completed table backwards from (n,m) to reconstruct the string itself. | Every two-string file here returns a length or a count; nothing reconstructs an answer from a filled table. 'Now print the subsequence, not just its length' is a routine senior follow-up, and traceback is a distinct skill from filling the table. Longest Common Substring is the classic trap — a candidate who pattern-matches to LCS and forgets that the answer no longer lives at dp[n][m] gets a wrong answer confidently, which reads worse than not knowing. |
| medium | Burst Balloons (LC 312) | Interval DP with sentinels where you choose the LAST balloon burst in the interval, not the first, so the two subproblems stay independent. | He has MCM and Minimum Cost to Cut a Stick, so the mechanics (sentinels, loop over k, memo on (i,j)) are already muscle memory — but Burst Balloons is the one where the obvious framing (which balloon to burst first) produces subproblems that are not independent, and the candidate has to invert the ordering. That reframing insight is the entire question and is exactly what a staff-level interviewer is probing. Lower priority only because the surrounding template is already solid. |
| medium | Interleaving String (LC 97) | Two-pointer grid DP where the third string's index is derived (k = i + j) rather than stored, reducing an apparent 3D state to a 2D table. | A frequent Meta/Amazon onsite question and a good test of state-space reasoning: the naive reading suggests three indices, and the insight is recognizing one is redundant. His two-string grid work is otherwise strong, so this is a short addition, but the 'prove a dimension is derivable' move is the kind of thing that separates a memorized template from actual DP fluency. |
| medium | Partition to K Equal Sum Subsets (LC 698) / bitmask DP over subsets | DP keyed on a bitmask of used elements: dp[mask] = remainder of the current bucket, with pruning (sort descending, early exit when sum % k != 0). | He has state-compressed DP on a frequency signature (StickersToSpellWord) and on a difference (TallestBillboard), so he is close — but never a mask over element subsets, which is the standard encoding when n <= 20 and assignment/grouping is involved (task assignment, Shortest Path Visiting All Nodes, smallest-sufficient-team). It surfaces in senior rounds as a 'can you beat exponential backtracking?' follow-up. Lowest of the eight because plain backtracking with pruning often passes, so failing to reach bitmask rarely sinks the loop on its own. |

