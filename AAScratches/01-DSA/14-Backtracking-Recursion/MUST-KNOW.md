# 14-Backtracking-Recursion — must-know order

**Techniques in this topic:** Recursion primitives: base case, index-driven recursion, and carrying state (depth, running sum) down the call stack instead of using loops, The two canonical enumeration templates: pick/not-pick (binary choice per index) and the start-index for-loop (choose the next element from i..n) — every combination/subset problem here is one of these two, Backtracking as mutate-recurse-undo: append then remove, mark the cell then restore it, set the constraint flag then clear it — the undo is the whole technique, Duplicate handling: sort first, then skip nums[i]==nums[i-1] when i>start, so equal values are never chosen twice at the same recursion depth, Pruning before recursing (target <= 0, open/close counters, isSafe checks, O(1) row/diagonal arrays) — this is what separates a passing answer from a TLE, Grid/graph DFS with a visited marker that is set on entry and restored on exit, including the in-place sentinel trick for char boards, When enumeration is the wrong answer: counting with factorials (kth permutation) and BFS by removal-count (minimum removals) replace brute-force search, Hard string DFS: splitting a string into segments while tracking extra state (previous operand for '*' precedence, leading-zero rules)

| | |
|---|---|
| Problems | 18 |
| Must-know | 7 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A04_Subsets.java` | Pick / not-pick recursion |
| 2 | `A05_Permuataions.java` | Swap-in-place, recurse, swap back |
| 3 | `C01_GenerateParenthesis.java` | Counter-based pruning of an invalid branch |
| 4 | `C02_SubsetsII.java` | Sort, then skip duplicates at the same depth |
| 5 | `C03_CombinationSum.java` | Start index with reuse plus target pruning |
| 6 | `C05_WordSearch.java` | 4-directional DFS with in-place visited marker |
| 7 | `D01_NQueens.java` | O(1) safety via row and diagonal arrays |

## Full practice order


### A — Building blocks

- `A01_RecursiveFactorial.java` — Base case plus single recursive call
  - The smallest possible recursion: one base case, one self-call — the shape every other file repeats.
- `A02_RecursiveArraySum.java` — Index-driven recursion over an array
  - Introduces the index parameter that drives every subsequent template; a loop rewritten as recursion.
- `A03_CombinationProgram.java` — Cartesian product by depth
  - First multi-branch recursion: one choice per level, fixed depth — the simplest tree of choices before pick/not-pick appears.
- `A04_Subsets.java` — Pick / not-pick recursion **[must-know]**
  - The power-set template and the add-recurse-remove undo; literally every combination file below is a variation of it.
- `A05_Permuataions.java` — Swap-in-place, recurse, swap back **[must-know]**
  - The second core template (ordering rather than selection) and the cleanest demonstration that backtracking means restoring state.

### B — Easy

- `B01_NestedListWeightSum.java` — Recursion carrying a depth parameter
  - Easiest applied recursion: no choices to undo, just state passed downward — a gentle first non-toy problem.
- `B02_LetterCombinations.java` — Digit-to-letter map with StringBuilder backtracking
  - CombinationProgram with a lookup table and a StringBuilder undo; the standard warm-up phone-keypad question.
- `B03_WordPathFinder.java` — Grid DFS with only two moves
  - Introduces grid recursion and recording a path, but right/down only means no cycles and no visited set — the safe rehearsal for WordSearch.

### C — Medium

- `C01_GenerateParenthesis.java` — Counter-based pruning of an invalid branch **[must-know]**
  - First problem where the constraint lives in counters, not the array: generate only valid states instead of filtering afterwards.
- `C02_SubsetsII.java` — Sort, then skip duplicates at the same depth **[must-know]**
  - Converts Subsets to the start-index loop form and adds the i>start dedupe rule that CombinationSumII and every ...II follow-up reuses.
- `C03_CombinationSum.java` — Start index with reuse plus target pruning **[must-know]**
  - Teaches passing i (not i+1) to allow repeats and pruning on remaining target — one of the most frequently asked backtracking questions.
- `C04_CombinationSumII.java` — No-reuse plus duplicate skipping
  - Combines the dedupe rule from SubsetsII with the target pruning from CombinationSum; makes sense only after both.
- `C05_WordSearch.java` — 4-directional DFS with in-place visited marker **[must-know]**
  - The staple grid-backtracking interview question: mark the cell with a sentinel, explore, restore — a step up from WordPathFinder's two moves.
- `C06_MColoringProblem.java` — isSafe check over adjacency, assign and undo
  - Moves the undo pattern onto a graph with an explicit feasibility check — the direct conceptual rehearsal for N-Queens.

### D — Hard

- `D01_NQueens.java` — O(1) safety via row and diagonal arrays **[must-know]**
  - The canonical hard backtracking problem; the diagonal indexing trick turns MColoring's O(n) isSafe into O(1) and unlocks Sudoku-style solvers.
- `D02_KthPermutation.java` — Factorial number system, no recursion
  - The anti-backtracking lesson: needs Permutations and factorial first, then shows how counting replaces generating all n! candidates.
- `D03_RemoveInvalidParentheses.java` — BFS by removal count, stop at first valid level
  - Assumes the validity-balance check from GenerateParenthesis and adds minimum-removals search with a visited set for dedupe.
- `D04_ExpressionAddOperators.java` — Segment DFS tracking the previous operand
  - The apex: string splitting plus operator choice plus carrying the last multiplied term for precedence, leading zeros and overflow — assumes fluency in everything above.

## Interview readiness

This is one of the stronger folders in the set: both canonical enumeration templates (pick/not-pick in Subsets.java, start-index loop in CombinationSum/SubsetsII), duplicate suppression, pruning, constraint satisfaction on two fronts (NQueens plus MColoringProblem), grid DFS with in-place sentinels (WordSearch, WordPathFinder), and two genuinely hard string-DFS problems (ExpressionAddOperators, RemoveInvalidParentheses) are all present and correctly written — that is already above the bar for most mid-level loops, and the hard pair plus KthPermutation shows he knows when enumeration is the wrong answer. Two apparent gaps are not gaps: Palindrome Partitioning (LC 131) is solved, just filed in 12-Dynamic-Programming\PalindromePartitioning.java as a pure backtracking solution, and root-to-leaf path backtracking lives in 09-Trees-BST\PathSumII.java. What is genuinely missing clusters into four shapes he has never written: duplicate handling inside permutations (his Permuataions.java is the swap template, and the sort-plus-skip trick he uses everywhere else is invalid under swapping, so the standard "now the input has duplicates" follow-up would break him live), memoized backtracking that returns and caches result lists rather than printing them, assignment-into-k-buckets search rather than choose-a-sequence search, and fusing a Trie into a grid DFS — which is exactly the senior-level scaling follow-up to the WordSearch he already has. Fix those four and the folder is staff-ready; also be ready to answer, verbally, the two follow-ups this folder never forces: the exact time complexity of each template, and what to do when recursion depth is the constraint (explicit stack).

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Permutations II (LeetCode 47) — permutations of an array containing duplicates | Duplicate control at permutation level: sort + a boolean used[] array with the skip `i>0 && nums[i]==nums[i-1] && !used[i-1]`, or a per-level HashSet. Critically, the swap-based template cannot use the sort+skip rule because swapping destroys sortedness. | This is the single most common follow-up to Permutations, and his existing Permuataions.java uses the swap template, so the duplicate rule he has practised in SubsetsII and CombinationSumII does not transfer. He would very likely produce a wrong answer (duplicate outputs) or freeze mid-interview when the interviewer says 'the input is [1,1,2]'. |
| high | Partition to K Equal Sum Subsets (LeetCode 698) | Assignment backtracking — place each number into one of k buckets, with the pruning set that makes it pass: early exit if sum % k != 0 or max > target, sort descending, skip a bucket whose running sum equals a bucket already tried, and only start a fresh bucket once per level. Bitmask memo as the follow-up. | Every template he owns picks the next element of one growing sequence. This one assigns each element to one of k groups, which is a structurally different recursion he has never written, and it is unsolvable in time limits without the pruning discussion — exactly the discriminator interviewers use at senior level. Matchsticks to Square and Fair Distribution of Cookies are the same template, so one problem covers three. |
| high | Word Search II (LeetCode 212) — find all dictionary words in a grid | Trie-backed grid backtracking: build a Trie of the word list, DFS the board once carrying a Trie node instead of restarting per word, null out the word at the terminal node to dedupe, and prune exhausted leaf nodes as you unwind. | He has WordSearch.java and a full 10-Trie folder but has never fused them, and the fusion is the point. This is the standard senior/staff escalation: 'now do it for 10,000 words' — answering 'run Word Search 10,000 times' is a fail, and the leaf-pruning detail is what separates a hire from a strong hire. |
| high | Word Break II (LeetCode 140) — return every sentence the string can be segmented into | DFS over split points where the recursive call RETURNS the list of suffix segmentations, memoized in a Map<Integer, List<String>> (or Map<String,...>) so each suffix is solved once. | Nothing in the workspace covers memoized backtracking — recursion that returns and caches collections rather than mutating a shared accumulator. There is no Word Break of any form in 12-Dynamic-Programming either. This is the canonical bridge between backtracking and DP, and the interviewer's whole script is the TLE trap on strings like 'aaaa...aaab', which he currently has no rehearsed answer for. |
| medium | Sudoku Solver (LeetCode 37) | In-place constraint backtracking that short-circuits: try 1-9 in the first empty cell, isValid against row, column and the 3x3 box (index r/3*3 + c/3), recurse, and propagate `return true` up the stack on the first full solution instead of enumerating all of them. Follow-up: O(1) row/col/box bitmask validity. | He has the pieces — MColoringProblem is the boolean-returning constraint solver and NQueens is the O(1) safety-check enumerator — so this is derivable rather than new, but it is asked often enough by name (Amazon, Microsoft, Salesforce) that the box-index arithmetic and the boolean propagation chain should be muscle memory, not something he reconstructs under a 35-minute clock. |
| medium | Restore IP Addresses (LeetCode 93) | Fixed-depth string segmentation: recurse over cut positions with an exact segment count of 4, prune on remaining-length bounds, and enforce per-segment validity (value <= 255, no leading zero unless the segment is exactly "0"). | It is the friendly cousin of ExpressionAddOperators, which he has, so the mechanism is covered — but this specific problem is a very common phone-screen and on-site warm-up, and the leading-zero and length-pruning rules are the exact places candidates lose points. Cheap to add, and it makes the string-segmentation family fully rehearsed rather than only practised at hard difficulty. |

