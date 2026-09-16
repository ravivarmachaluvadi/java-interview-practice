# 05-Hashing-Prefix-Sum — must-know order

**Techniques in this topic:** Hash map as complement lookup: the one-pass trick that collapses O(n^2) pair search to O(n) (TwoSum -> CountPairSum -> remainder buckets), Prefix sum + hash map: the subarray-sum family. The single decision that separates these problems is map-of-counts (how many subarrays) vs map-of-first-index (longest subarray), Prefix sum array for O(1) range queries: precompute once, answer q queries in O(1) each, Canonical-key grouping: derive an invariant key (sorted chars, consecutive-diff pattern, first-index normalization) and bucket by it with computeIfAbsent, Frequency maps and fixed counting arrays (int[26]) for multiset comparison, greedy batching, and frequency ordering, Set membership: dedup, set difference, fixed-window substrings, and the sequence-head trick that makes an O(n^2)-looking loop amortized O(n), Modular/remainder bucketing: group by value mod m, then pair or consume complements, Housekeeping: three near-duplicate pairs exist (PairsOfSongs x2, LongestSubarrayWithKSum x2, CountPairSum vs the PairSum class inside TwoSum.java), and ImportantLongestSubarrayWithSumKHash.java does not compile - it calls sumIndexMap.get(key) where the variable is named remSum

| | |
|---|---|
| Problems | 27 |
| Must-know | 7 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_TwoSum.java` | Hash map complement lookup |
| 2 | `A03_CountPairSum.java` | Frequency map, count pairs not find |
| 3 | `A05_CountVowelStringsInRanges.java` | Prefix sum array, range queries |
| 4 | `C01_ImportantCountSubarraySumEqualsK.java` | Prefix sum plus count map |
| 5 | `C03_ImportantLongestSubarrayWithSumKHash.java` | Prefix sum, first-index map |
| 6 | `C05_GroupAnagrams.java` | Canonical key by sorting |
| 7 | `D01_LongestConsecutiveSequence.java` | Set plus sequence-head insight |

## Full practice order


### A — Building blocks

- `A01_ContainsDuplicate.java` — HashSet membership check
  - The simplest possible use of hashing - trade O(n) space for O(1) lookup; every other file assumes this reflex.
- `A02_TwoSum.java` — Hash map complement lookup **[must-know]**
  - The archetype: store value->index while scanning and look up target-x; unlocks every pair and subarray problem in the folder.
- `A03_CountPairSum.java` — Frequency map, count pairs not find **[must-know]**
  - Upgrades TwoSum from find-one to count-all by storing counts instead of indices - the exact shape reused by SubarraySumEqualsK and PairsOfSongs.
- `A04_RansomNote.java` — int[26] counting array
  - Teaches the fixed-alphabet counter and the decrement-and-check-negative idiom that beats a HashMap when the key space is small.
- `A05_CountVowelStringsInRanges.java` — Prefix sum array, range queries **[must-know]**
  - The other half of the folder: prefix[r+1]-prefix[l] with the n+1 offset. Own this before any prefix-sum-plus-hashing problem.

### B — Easy

- `B01_FindTheDifferenceOfTwoArrays.java` — Two sets, one-sided difference
  - First application of set membership to two collections; dedup falls out of the Set for free.
- `B02_SetDifferenceOfTwoArrays.java` — Symmetric difference plus sort
  - Same two-set scan as the previous file, merged into one sorted list - pure reinforcement.
- `B03_IntersectionOfTwoArrays.java` — Two pointers on sorted arrays
  - The deliberate non-hash contrast: O(1) extra space when input is already sorted. Know when NOT to reach for a map.
- `B04_IntersectionOfTwoArraysII.java` — Frequency map with multiplicity
  - The real interview version of intersection - duplicates must be preserved, so a count map replaces the set.
- `B05_UniqueTuples.java` — Fixed-length substrings into a set
  - Introduces the sliding fixed window plus set, including the n-k+1 loop bound people get wrong.
- `B06_RepeatedDnaSequences.java` — Window hashing, seen vs repeats
  - UniqueTuples with duplicate detection via the boolean return of Set.add; mention rolling hash as the follow-up optimisation.
- `B07_TournamentWinner.java` — Tally map with running max
  - Accumulate into a map and track the best key in the same pass - no second scan needed.
- `B08_ExponentPairs.java` — Brute-force pairs, set dedup
  - Closes the easy tier by showing the O(n^2) baseline plus a canonical string key for dedup - the cost hashing exists to avoid.

### C — Medium

- `C01_ImportantCountSubarraySumEqualsK.java` — Prefix sum plus count map **[must-know]**
  - LC 560, the centrepiece of this folder and the most-asked subarray question; seeding the map with (0,1) is the detail to internalise.
- `C02_BinarySubarraysWithSum.java` — Same template on a 0/1 array
  - Immediate reuse of the 560 template so the pattern sticks; also sets up the at-most(k)-minus-at-most(k-1) sliding-window alternative.
- `C03_ImportantLongestSubarrayWithSumKHash.java` — Prefix sum, first-index map **[must-know]**
  - The contrast that decides the whole family: putIfAbsent to keep the earliest index because longest wants the furthest-back match. Note this file does not compile - get(key) should be get(remSum).
- `C04_LongestSubarrayWithKSumWithHashing.java` — Same, clean compiling version
  - The working duplicate of the previous file; use it as the reference implementation and keep only one of the two.
- `C05_GroupAnagrams.java` — Canonical key by sorting **[must-know]**
  - Opens the grouping thread and is a constant screen question; also the place to discuss the O(k) count-signature key versus O(k log k) sorting.
- `C06_GroupShiftedStrings.java` — Consecutive-diff key mod 26
  - Same bucket-by-key skeleton as GroupAnagrams, but you must invent the invariant yourself and handle wraparound with +26.
- `C07_FindAndReplacePattern.java` — First-index normalization
  - Third grouping variant: reduces a two-map bijection check to comparing indexOf fingerprints - a neat trick worth being able to justify.
- `C08_SubdomainVisitCount.java` — Map accumulation, derived keys
  - Applies key-building to messy string parsing where one input row updates several map entries; the split("\\.") escape is the usual stumble.
- `C09_SortBasedOnFrequencyOfOccurrence.java` — Count then order by count
  - Moves from counting to ranking by count; the natural lead-in to Top-K Frequent and heap or bucket-sort follow-ups.
- `C10_MinimumRounds.java` — Counting plus greedy ceil(c/3)
  - Shows the map only sets up the real work - a per-count math argument - and includes a sort-and-group alternative to compare.
- `C11_PairsOfSongsDivisibleBy60.java` — Remainder bucket complement
  - Transfers the TwoSum complement idea into modular arithmetic with a fixed int[60] instead of a map.
- `C12_PairsOfSongsDivBy60.java` — Same, with the rem 0 edge case
  - Duplicate of the previous file that adds the (60-rem)%60 reasoning and the self-pairing C(n,2) case - the part interviewers probe.

### D — Hard

- `D01_LongestConsecutiveSequence.java` — Set plus sequence-head insight **[must-know]**
  - The non-obvious step is expanding only from numbers with no predecessor, which makes a nested loop amortized O(n); asked constantly at senior and above.
- `D02_SmallestMissingNonNegativeIntegerAfterOperations.java` — Mod-invariant classes, greedy consume
  - Needs the observation that +/- value never changes num mod value, so the answer is found by consuming remainder classes in order - build it on the remainder bucketing from the PairsOfSongs pair.

## Interview readiness

This is a genuinely strong hashing folder — stronger than most senior candidates bring. The five load-bearing patterns are all present and each has more than one representative: complement lookup (TwoSum, CountPairSum, PairsOfSongs), prefix-sum + hash map in both of its forms (ImportantCountSubarraySumEqualsK for counts, LongestSubarrayWithKSum for first-index), 1D prefix arrays for O(1) range queries (CountVowelStringsInRanges), canonical-key grouping (GroupAnagrams, GroupShiftedStrings, FindAndReplacePattern, SubdomainVisitCount), frequency maps and int[26] counting (RansomNote, MinimumRounds, SortBasedOnFrequencyOfOccurrence), and set membership including the amortized sequence-head trick (LongestConsecutiveSequence). Several neighbours that would otherwise be gaps are already covered elsewhere in the tree: Top K Frequent (08-Heap), LRU/LFU and Time-Based KV (19-Design), Copy List with Random Pointer (06-Linked-List), Minimum Window Substring and Longest Substring Without Repeating (02-Sliding-Window), Valid Anagram and First Non-Repeating Char (04-Strings). The weakness is not breadth of patterns but breadth of *form*: nearly every problem here is the 1D-array-or-string instance of its pattern. What is missing is the set of twists interviewers use to tell "has drilled the pattern" from "understands the invariant" — remapping 0/1 to -1 before applying the sum-zero machinery, taking prefix sums into modular arithmetic, lifting prefix-sum-plus-hash-map onto a DFS with backtracking, and moving prefix sums into two dimensions or inverting them into a difference array. Also count the real inventory honestly: three near-duplicate pairs (PairsOfSongs x2, LongestSubarrayWithKSum x2, CountPairSum vs the inner PairSum class in TwoSum.java) inflate 27 files into roughly 23 distinct problems, and ImportantLongestSubarrayWithSumKHash.java does not compile (it calls sumIndexMap.get(key) where the declared variable is remSum) — a file he would "revise from" the night before and find broken. Net: safe for any phone screen and most onsite hashing rounds, but one follow-up twist away from being exposed, and the four high-priority gaps below are exactly where that twist lands.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Contiguous Array (LC 525) — longest subarray with equal numbers of 0s and 1s | Remap 0 -> -1, then the problem collapses to longest subarray with sum 0: prefix sum + map-of-first-index, answer = i - firstIndex[prefix]. | He owns both halves already (LongestSubarrayWithKSumWithHashing is literally the target machinery; BinarySubarraysWithSum is the 0/1 array) but has never practised the transformation step that connects them. This is the single most common way an interviewer checks whether prefix-sum+hash was memorised or understood, and the ask is almost always phrased as 'equal counts', never as 'sum zero'. Without having done the remap once, candidates stall for several minutes on a problem they can otherwise solve instantly. Direct follow-ups (equal counts of two chars, equal vowels/consonants, LC 1930-style variants) all reduce to the same move. |
| high | Subarray Sums Divisible by K (LC 974) | Prefix sum taken modulo k, counting map keyed by remainder: two prefixes with the same remainder bound a divisible subarray. Needs ((r % k) + k) % k for negative values, and count += map.get(r) before incrementing. | He has both ingredients in isolation and has never composed them: ImportantCountSubarraySumEqualsK is prefix+count, SmallestMissingNonNegativeIntegerAfterOperations already uses ((num % value) + value) % value correctly. The composition is the actual interview question, and the insight (equal remainders, not a target remainder) is not derivable from the sum=k version by pattern-matching. Also unlocks Continuous Subarray Sum (LC 523), which is the same prefix-mod idea with a map-of-first-index and a length>=2 constraint — the two together nail down the count-vs-first-index decision he already names as a theme. |
| high | Path Sum III (LC 437) — count root-to-any-node downward paths summing to target | The same prefix-sum + counting-map as LC 560, carried down a DFS: add the running prefix on the way in, read map.get(prefix - target), and decrement the entry on the way out so sibling branches are not contaminated. | 09-Trees-BST has HasPathSum and PathSumII (plain root-to-leaf backtracking) but nothing that moves a prefix-sum map onto a tree. This is the canonical 'can you transfer the technique to a new shape' question at Amazon and Google, and the backtracking undo is a step candidates skip and then cannot debug live. Because it sits at the boundary of two folders, it is exactly the problem that falls through the cracks of a folder-by-folder study plan — he will have seen it in neither. |
| high | Range Sum Query 2D - Immutable (LC 304) — region sum of a matrix in O(1) | 2D prefix sum built once with pre[i][j] = a[i][j] + pre[i-1][j] + pre[i][j-1] - pre[i-1][j-1], then inclusion-exclusion at query time with the symmetric four-term formula. | His only prefix-array problem is one-dimensional (CountVowelStringsInRanges), and 16-Matrix has no prefix work at all. Inclusion-exclusion is not something you reliably re-derive under pressure — the sign and off-by-one on the +pre[i-1][j-1] term is the classic live-coding failure, and it is a formula you either have written once or have not. It is also the base layer for the follow-ups that actually get asked at senior level (count submatrices summing to target, maximum sum rectangle, number of submatrices with all ones), so its absence caps how far a matrix-flavoured round can go. |
| medium | 4Sum II (LC 454) — count tuples across four arrays summing to zero | Split into halves: hash every a+b sum with its multiplicity, then for every c+d look up -(c+d) and add the stored count. O(n^2) instead of O(n^4). | His complement work is all single-array and mostly existence-or-pairs; nothing here hashes a derived key from one half of the input and accumulates counts with multiplicity from the other. That meet-in-the-middle framing is the standard senior escalation of Two Sum and is asked directly at Meta and Amazon. He would likely find it given time because the complement instinct is strong, which is why this is medium rather than high — but the multiplicity bookkeeping (add the count, not one) is where people quietly return the wrong answer. |
| medium | Valid Sudoku (LC 36) | One pass over the board with hash sets over composite keys — encode 'digit d in row r', 'digit d in col c', 'digit d in box (r/3, c/3)' as distinct strings or three arrays of bitmasks — and fail on the first duplicate insert. | Absent from both 05-Hashing and 16-Matrix. It is the most common opening question in the easy-medium warm-up slot at Amazon and Microsoft, and it tests a skill adjacent to but distinct from his canonical-key grouping: building a composite key that fuses several dimensions, plus the r/3*3 + c/3 box index that people fumble live. The algorithm is not hard for him, but the box-index derivation and the choice of key encoding eat real minutes if never written before, and a slow start on a warm-up colours the rest of the loop. |
| medium | Corporate Flight Bookings (LC 1109) / Range Addition (LC 370) — apply k range updates, report the final array | Difference array, the inverse of prefix sum: diff[start] += v, diff[end+1] -= v in O(1) per update, then one prefix pass materialises the whole array in O(n). | 15-Intervals/CarPooling.java solves the same physics but with the sweep-line form — build events, sort them, sweep — which is O(n log n) and does not produce a per-index array. The difference-array form is the one interviewers want when the indices are dense and bounded, and it is the standard follow-up to any prefix-sum question ('now support updates'). Having only the sorted-events version means he answers a range-update question with an unnecessary sort and misses the O(1)-per-update point, which at staff level reads as not knowing prefix sum is invertible. |
| medium | Find All Anagrams in a String (LC 438) / Permutation in String (LC 567) | Fixed-size sliding window over an int[26] frequency map, comparing counts in O(1) amortised by maintaining a 'matched characters' counter rather than re-comparing the arrays each step. | GroupAnagrams covers the canonical-key form and MinimumWindowSubstring covers the variable-size need/have window, but the fixed-width frequency window is missing from every folder. It is the most-asked frequency-map question after Group Anagrams and is the natural bridge question between the hashing and sliding-window rounds. He can reach a correct O(n*26) solution from what he has; the gap is the matched-counter optimisation and the clean add/remove symmetry, which is what the interviewer probes after the first working version. |

