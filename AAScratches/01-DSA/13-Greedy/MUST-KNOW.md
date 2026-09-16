# 13-Greedy — must-know order

**Techniques in this topic:** Sort-then-scan: establishing an order in which the locally best choice is provably globally safe (exchange argument), Custom comparators as the greedy itself - multi-key sorts and pairwise-concatenation ordering, Reachability frontier / farthest-reach scanning: Jump Game, Jump Game II and everything that reduces to them (interval covering, taps), Last-occurrence precomputation turning a string or digit problem into interval merging (Partition Labels, Maximum Swap), Prefix-sum with reset plus a separate global feasibility check (Gas Station, Non-Constructible Change), Two-pointer pairing on a sorted array with a proof that the extreme element must be paired now (Boats, Assign Cookies, Class Photos), Two-pass greedy: satisfy one direction of a constraint, then the other, then take the max (Candy), Scheduling greedies: shortest-job-first, deadline slot filling, profit-descending placement, Counting and parity arguments feeding a greedy allocation (palindrome pools, rearranging fruits), Duplicate-file recall drills: the same problem solved twice is a spaced-repetition slot, not extra material

| | |
|---|---|
| Problems | 27 |
| Must-know | 7 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `B01_BestTimeToBuyAndSellStockII.java` | Sum every positive delta |
| 2 | `C04_PartitionLabels.java` | Last index per char, merge to boundary |
| 3 | `C05_BiggestNumber.java` | Comparator on a+b vs b+a |
| 4 | `C07_CanJump.java` | Farthest-reach frontier |
| 5 | `C08_CanJumpTwo.java` | Layered frontier, count jumps |
| 6 | `C11_GasStation.java` | Running sum with reset plus global check |
| 7 | `D01_Candy.java` | Two passes, then take the max |

## Full practice order


### A — Building blocks

- `A01_ShortestJobFirst.java` — Sort ascending, accumulate wait time
  - The purest exchange argument in the folder: swapping a longer job earlier strictly increases total wait, so ascending order is optimal - this is the proof template every later file reuses.
- `A02_JobSequencing.java` — Multi-key comparator (profit desc, deadline asc)
  - Just the comparator, no scheduling - own this primitive before ZOptimalFreelancing and BiggestNumber, where the comparator IS the algorithm.

### B — Easy

- `B01_BestTimeToBuyAndSellStockII.java` — Sum every positive delta **[must-know]**
  - The canonical proof that a sum of local optima equals the global optimum; asked constantly as a warm-up and it is the cheapest place to practise saying WHY greedy is valid.
- `B02_BuyAndSellStockII.java` — Sum every positive delta
  - Identical problem to the previous file - use it as a blind recall drill a week later, not as new material.
- `B03_CanPlaceFlowers.java` — Scan and commit on a local window
  - Simplest 'take it when you can' greedy; teaches boundary handling (i==0, i==n-1) and mutating the input to record the commitment.
- `B04_AssignCookies.java` — Sort both arrays, two-pointer match
  - First problem needing TWO sorted sequences advanced together - the matching pattern that BoatsToSavePeople then makes non-trivial.
- `B05_ClassPhotos.java` — Sort both, decide roles, pairwise compare
  - Same two-sorted-arrays setup as AssignCookies, plus one global decision (which row goes in back) made from a single extreme element.
- `B06_NonConstructibleChange.java` — Sorted prefix reachability invariant
  - Introduces the reach invariant ('everything up to currentChange is constructible') in a one-line form, which is the same idea CanJump generalises to indices.
- `B07_LemonadeChange.java` — Spend the largest denomination first
  - Greedy change-making with a real safety argument (a $5 is more flexible than a $10, so hoard it) - easy code, but the first file where the wrong greedy is tempting.

### C — Medium

- `C01_BoatsToSavePeople.java` — Sorted two-pointer pairing
  - Upgrades AssignCookies with a real proof obligation: the heaviest person must board now, and pairing them with the lightest is never worse.
- `C02_MinimumHealthToBeatGame.java` — Total aggregate plus one global choice
  - Order does not matter at all - the only decision is where to spend the armor, so it isolates 'find the single best place to apply one resource' from any scanning logic.
- `C03_MaximumSwap.java` — Last-occurrence table on digits
  - Introduces the precomputed last[] array and the leftmost-improvable-position rule; do it immediately before PartitionLabels, which uses the same table for a different purpose.
- `C04_PartitionLabels.java` — Last index per char, merge to boundary **[must-know]**
  - The last-occurrence table from MaximumSwap becomes interval merging; this is the single best transferable pattern here and shows up in string-chunking and interval questions constantly.
- `C05_BiggestNumber.java` — Comparator on a+b vs b+a **[must-know]**
  - The exchange argument turned into code - a comparator whose correctness (and transitivity) you must defend out loud; the comparator primitive from JobSequencing now carries the whole solution.
- `C06_MaxDifferenceChangingInteger.java` — Independent digit-level max and min
  - Two separate greedies on the same input, with the leading-zero constraint as the trap; good practice at enumerating edge cases before writing code.
- `C07_CanJump.java` — Farthest-reach frontier **[must-know]**
  - The reach invariant from NonConstructibleChange applied to indices; this frontier idea is the load-bearing technique for four later files including the two hard ones.
- `C08_CanJumpTwo.java` — Layered frontier, count jumps **[must-know]**
  - Adds the currentEnd/farthest two-frontier structure - BFS levels without a queue - and the n-1 loop bound edge case; the taps problem is literally this algorithm in disguise.
- `C09_JumpGameII.java` — Layered frontier, count jumps
  - Clean second copy of CanJumpTwo - re-derive it from scratch to confirm the currentEnd update and the loop bound are actually internalised.
- `C10_JumpGame.java` — Reachability and min-jumps together
  - Holds both variants in one file, so it works as the combined recall check after the previous three; note its getMinJumps has no reachability guard, which is worth spotting.
- `C11_GasStation.java` — Running sum with reset plus global check **[must-know]**
  - Two independent arguments in one pass - total gas >= total cost proves existence, the reset proves the candidate index - and articulating why the reset is safe is exactly what separates a senior from a staff answer.
- `C12_ZOptimalFreelancing.java` — Profit-desc sort, latest free slot
  - Pays off the JobSequencing comparator: sort by profit then place each task in the LATEST free slot to keep earlier slots open - the classic deadline-scheduling greedy.

### D — Hard

- `D01_Candy.java` — Two passes, then take the max **[must-know]**
  - The insight that one constraint direction cannot be satisfied in a single scan, so you solve left-to-right and right-to-left and combine - a pattern that generalises far beyond this problem.
- `D02_MinimumNumberOfTapsToOpenToWaterAGarden.java` — Interval covering reduced to Jump Game II
  - Hard only until you see the reduction: collapse each tap into maxReachFrom[left], then run CanJumpTwo verbatim - do it right after the jump files while that shape is fresh.
- `D03_MaximumPalindromesAfterOperations.java` — Parity bitmask pool, sort by length
  - Two non-obvious steps stacked: characters are a shared pool so only global parity matters, and once you have a pool the greedy is the easy part (fill shortest words first).
- `D04_MinimumMovesToMakePalindrome.java` — Adjacent-swap matching, two pointers
  - Greedy on a swap count with a proof that is genuinely hard to state; needs the feasibility precheck and the odd-character-to-middle case that nobody gets on the first attempt.
- `D05_MinimumMovesToPalindrome.java` — Cheaper-of-two-directions variant
  - Same problem as the previous file but it searches from both ends and takes the shorter shift - study it second, as the contrast is what exposes which choices are actually forced.
- `D06_SolutionRearrangingFruits.java` — Multiset diff plus 2x-global-min proxy swap
  - Hardest file here: parity feasibility, then the insight that the cheapest global element can broker any swap at cost 2*min, then swap only half the surplus list - put it last.

## Interview readiness

This is one of the stronger greedy folders I've reviewed and it is close to interview-ready. Verified on disk: all 27 files match the stated list, and the adjacent folders cover greedy problems that would otherwise look like glaring holes — 15-Intervals has NonOverlappingIntervals, MinimumArrowsToBurstBalloons, MeetingRoomsII, MinimumPlatforms and DivideIntervalsIntoMinGroups (so classic activity-selection / sort-by-end-time is covered), 07-Stack-Queue-Monotonic has RemoveKdigits (monotonic-stack greedy) and 08-Heap-Priority-Queue has ReorganizeString, HuffmanCoding, MinimumRefuelingStops, FurthestBuildingYouCanReach and MaximumNumberofEventsThatCanBeAttended (heap-driven and retroactive greedy). BiggestNumber is confirmed to be Largest Number with the a+b vs b+a comparator, so pairwise-concatenation ordering is genuinely done. Two caveats on how ready it actually is. First, five of the 27 files are deliberate duplicates (CanJump/JumpGame, CanJumpTwo/JumpGameII, BestTimeToBuyAndSellStockII/BuyAndSellStockII, MinimumMovesToMakePalindrome/MinimumMovesToPalindrome, JobSequencing/ZOptimalFreelancing — I read ZOptimalFreelancing and it is profit-descending deadline slot filling, i.e. the same problem), so distinct coverage is closer to 22 problems than 27; that is fine as spaced repetition but it means the reachability-frontier theme is over-weighted relative to everything else. Second, the gaps are not random — they cluster into five greedy shapes that are entirely absent rather than thinly covered: frequency-driven scheduling with a cooldown, greedy over a range of possible states rather than a single counter, anchor-and-consume grouping over a count map, sort-then-insert-at-position, and the regret/eviction heap where you take everything and undo the worst prior choice when you become infeasible. Of those, the first three are asked often enough at product companies that a miss is a real risk; the rest are upside. Close the four high-priority items and this folder is above the bar for senior and staff greedy rounds.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Task Scheduler (LC 621) | Frequency counting plus the idle-slot formula max(n+1)*(maxFreq-1)+countOfMaxFreq, len(tasks)); heap variant for the follow-up asking for the actual schedule | Meta, Amazon and Uber staple that comes up constantly. Nothing in the folder covers frequency-driven scheduling under a cooldown constraint - the closest is JobSequencing, which is slot filling by deadline, not by repetition distance. The interviewer almost always pushes from the O(n log n) heap simulation to the O(n) bucket formula and then asks him to prove the formula, which he cannot currently rehearse from anything on disk. |
| high | Valid Parenthesis String (LC 678) | Track a range [lo, hi] of possible open-paren counts in one pass, clamping lo at 0 and failing when hi goes negative | Very high frequency at Meta and Amazon. He has MinimumAddToMakeParenthesesValid and ValidParentheses in 07-Stack-Queue-Monotonic, but those are single-counter problems. The wildcard version needs greedy over an interval of states rather than one number, which is a distinct idea he has zero exposure to, and the naive stack or DP answer is a visible downgrade in the room. |
| high | Hand of Straights / Divide Array in Sets of K Consecutive (LC 846 / 1296) | Count map or TreeMap; the smallest remaining value must start a group, so consume k consecutive values from it and repeat | Common Google and Amazon onsite. The whole folder has no anchor-and-consume greedy - every counting problem he has (Candy pools, RearrangingFruits) is an allocation argument, not a sequential grouping one. The correctness argument (the minimum element has no smaller partner, so it can only be a group head) is exactly the kind of exchange proof a staff interviewer probes, and it does not transfer from anything he has done. |
| high | Queue Reconstruction by Height (LC 406) | Sort by height descending and k ascending, then insert each person at index k; taller-first means every later insertion is invisible to the already-placed people | Classic Amazon and Google greedy. His comparator theme covers multi-key sorts, but this problem's payoff is the insert-at-position invariant, not the sort, and that step is genuinely non-obvious under time pressure. Without having seen it he will most likely reach for an O(n^2) simulation and never find the ordering argument. |
| medium | Two City Scheduling (LC 1029) | Sort by the cost difference costA - costB (opportunity cost), send the first n to A and the rest to B | Frequent at Amazon and Bloomberg, and the cleanest interview vehicle for the exchange argument on a derived key rather than a raw value. His sort-then-scan theme means he could plausibly stumble onto it, which is why this is medium rather than high, but 'sort by the delta, not the value' is a reflex worth owning outright since it also unlocks assignment-flavoured variants. |
| medium | Course Schedule III (LC 630) | Sort by deadline, greedily take every course into a max-heap of durations, and when total time exceeds the deadline evict the longest course taken so far | The regret / replacement-heap pattern - commit optimistically, then undo the worst earlier decision - is absent from the entire folder. MinimumRefuelingStops in 08-Heap is the nearest relative but it adds rather than evicts. Asked at Google and Uber, and it is the archetype behind a family of 'maximize count under a resource budget' questions, so one rep here generalises widely. |
| medium | Fractional Knapsack | Sort by value-per-weight descending, take whole items until the capacity runs out, then take a fraction of the next | He has knapsack01 in 12-Dynamic-Programming and JobSequencing here, so this is the missing third leg of the textbook greedy trio. It is the standard verbal probe in Indian product-company interviews: 'why is greedy optimal here but wrong for 0/1?' Failing to articulate that the divisibility of the item is what makes the exchange argument valid reads as pattern-matching without understanding, which is exactly what a staff-level bar screens out. |
| low | Wiggle Subsequence (LC 376) | Count direction changes in one pass, tracking up/down run lengths, for an O(n) answer instead of O(n^2) DP | Shows up periodically and is the cleanest example of a problem that looks like DP but collapses to a greedy scan. Lower priority because his existing scan-based work transfers reasonably well and it is not a frequent first-round pick, but it is a cheap rep that sharpens the 'is this DP or is it greedy?' decision he will have to make live. |

