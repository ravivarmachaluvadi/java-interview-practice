# 18-Sorting-Searching-Algorithms — must-know order

**Techniques in this topic:** Three O(n^2) baselines (bubble, selection, insertion) exist to give you invariant vocabulary - 'largest bubbles to the end', 'sorted prefix grows by one' - which is what you use to justify the O(n log n) sorts later, Two divide-and-conquer engines: merge (stable, O(n) extra space, predictable) and partition (in-place, expected O(n log n), adversarial worst case). Nearly every harder file here reuses one of the two, Partition reuse gives order statistics: quickselect is the same partition with one-sided recursion, and it is the expected answer for kth-largest / top-K before you reach for a heap, Non-comparison sorting: distributing by key into buckets beats the n log n bound when the key range is bounded, and the pigeonhole form of that idea is exactly what makes Maximum Gap linear, Array-as-tree indexing (children at 2i+1 / 2i+2) shows up twice - heapify and segment tree - so owning it once pays for both, Index-as-address placement (cyclic sort) replaces comparison entirely when values are a permutation of 1..n; it unlocks the whole missing-number / duplicate-number family, Searching here means string searching: rolling hash with verification (Rabin-Karp) versus failure-function backtracking (KMP). The KMP file is actually naive matching and returns a wrong answer on its own sample - that gap is the lesson, Two files are near-duplicates of each other (MergeSort/ImportantMergeSort, QuickSelect/ImportantQuickSelect) - treat the second of each pair as a re-derivation drill, not new material

| | |
|---|---|
| Problems | 16 |
| Must-know | 5 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A04_CyclicSort.java` | Value-to-index placement |
| 2 | `A05_ImportantMergeSort.java` | Divide and conquer plus merge |
| 3 | `A07_ImportantQuickSort.java` | Lomuto partition, recurse both sides |
| 4 | `A08_HeapSort.java` | Sift-down heapify, in-place sort |
| 5 | `C01_ImportantQuickSelect.java` | One-sided partition recursion |

## Full practice order


### A — Building blocks

- `A01_BubbleSort.java` — Adjacent swap, bubble maximum right
  - Cheapest possible introduction to the swap primitive and a loop invariant; everything after this assumes you can state what the inner loop guarantees
- `A02_SelectionSort.java` — Select extreme, place at boundary
  - Same invariant framing as bubble but with an explicit 'find the extreme' helper, which is the mental model heapify replaces later (note: this file's getMaxVal never updates maxVal, so it is actually broken)
- `A03_InsertionSort.java` — Shift into sorted prefix
  - Introduces the growing-sorted-prefix idea and early-exit on sorted input; ShellSort is literally this loop with a gap, so it must come first
- `A04_CyclicSort.java` — Value-to-index placement **[must-know]**
  - First file that abandons comparison for addressing; O(n) placement when values are a permutation of 1..n, and it is the whole missing-number/duplicate/first-missing-positive family in one loop
- `A05_ImportantMergeSort.java` — Divide and conquer plus merge **[must-know]**
  - The merge step is the single most reused primitive in this folder and beyond (count inversions, sort list, merge k lists, external sort), and it is the stable O(n log n) baseline you compare everything against
- `A06_MergeSort.java` — Merge sort, safer base case
  - Near-identical re-derivation of the previous file; its left >= right base case is the one to keep, so use it as a recall drill rather than new material
- `A07_ImportantQuickSort.java` — Lomuto partition, recurse both sides **[must-know]**
  - Partition is the second great primitive - it must be solid before quickselect, Dutch-national-flag or sort-colors make sense, and the pivot-choice discussion is the standard staff-level follow-up
- `A08_HeapSort.java` — Sift-down heapify, in-place sort **[must-know]**
  - Introduces array-as-tree indexing (2i+1 / 2i+2) and O(n) build-heap, which is what top-K, streaming median and the SegmentTree layout later assume

### B — Easy

- `B01_ShellSort.java` — Gapped insertion sort
  - Direct extension of InsertionSort - one extra gap loop - and the cheapest way to see why moving elements far in one step breaks the O(n^2) shift cost
- `B02_BucketSort.java` — Distribute by key into buckets
  - First non-comparison sort: teaches the bounded-key-range assumption and the bucket-index formula that Maximum Gap depends on in tier D

### C — Medium

- `C01_ImportantQuickSelect.java` — One-sided partition recursion **[must-know]**
  - Reuses the partition you just learned to get kth-smallest in expected O(n); this is LeetCode 215 territory and the expected first answer for kth-largest and top-K at senior level
- `C02_QuickSelect.java` — Quickselect re-derivation
  - Same algorithm written a second time with a differently-named partition - use it to practise the expected-linear recurrence argument, not to learn anything new
- `C03_RabinKarpAlgorithm.java` — Rolling hash with verification
  - Opens the string-searching half of the folder and teaches hash-then-verify, which generalises to repeated-substring and plagiarism-style problems; needed before you can appreciate why KMP exists

### D — Hard

- `D01_MaximumGap.java` — Pigeonhole bucketing by gap size
  - The first genuinely non-obvious file: it builds directly on BucketSort but needs the insight that the answer must exceed the average gap, so only bucket min/max matter
- `D02_SingleLoopSubstringCheckKMP.java` — Linear substring search, failure function
  - Follows Rabin-Karp as the other linear-time answer; ranked hard because the prefix-function insight is the whole problem - and this file's shortcut reset (i = i - j + 1 after j is already zeroed) makes it report 'not found' on its own sample
- `D03_SegmentTree.java` — Range query, point update tree
  - Heaviest file and last for a reason: it assumes heapify's array-as-tree indexing plus merge sort's recursive halving, and adds the no-overlap / full-overlap / partial-overlap case split

## Interview readiness

As a library of sorting *engines* this folder is close to complete — he has the three O(n^2) baselines, both divide-and-conquer engines, heapsort, shellsort, bucket sort, quickselect, cyclic sort, a segment tree and a rolling-hash matcher, which is more coverage than most senior candidates carry. The problem is that interviews almost never ask "implement merge sort"; they ask a disguised question whose answer *is* merge sort, and that layer is almost entirely absent here. There is not one problem in the folder that consumes a sorting engine to compute something else — no inversion counting, no comparator-defined order, no non-comparison sort beyond bucket-sort-for-floats-in-[0,1). Two files are also actively working against him: the KMP file is naive matching with a dead line that never rewinds the text pointer (it returns the wrong answer on its own sample), and both quicksort and quickselect use a fixed last-element Lomuto pivot with no randomization and no three-way split, which means the single most predictable follow-up in any partition question — "what happens on already-sorted input, or an array of all 5s?" — currently has no answer in his code. Searching itself is not a gap: 03-Binary-Search is genuinely strong (rotated arrays, binary-search-on-answer, median of two sorted arrays), and heap/interval/linked-list folders already cover top-K, k-way merge and sort-then-sweep. Net: he is ready for "write a sort and state its complexity" and not yet ready for a staff-level partition interrogation or a merge-sort-in-disguise counting problem. Closing gaps 1-3 below is roughly a weekend and moves him from "knows the algorithms" to "uses them as tools".

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Count Inversions in an Array (and its Reverse Pairs variant, LC 493) | Modified merge sort — count cross-pairs during the merge step, in O(n log n) | This is the canonical test of whether merge sort is a memorized routine or a usable tool. He has merge sort twice and not one problem that consumes it, so the whole 'count something while merging' move is untrained. Reverse Pairs adds the subtlety that the counting pass must run before (and separately from) the merge, which is exactly where candidates who only pattern-matched the inversion version fall apart. |
| high | Implement KMP correctly — build the LPS/prefix function, then use it (Implement strStr, Repeated Substring Pattern, Shortest Palindrome) | Failure-function preprocessing in O(m), then a single non-backtracking scan of the text in O(n) | The existing SingleLoopSubstringCheckKMP.java is not KMP — it is naive matching, and the reset branch does `j = 0;` before `i = i - j + 1;`, which collapses to `i++`, so the text pointer never rewinds and the file returns a wrong answer on its own sample while claiming O(n+m). If he is asked to implement KMP he has nothing to fall back on, and worse, he may reproduce this file and then be unable to defend the complexity claim. Rabin-Karp gives him one string-search answer, but 'derive the LPS array for ababaca' is asked directly and the prefix function also unlocks the two standard follow-ups. |
| high | Harden QuickSort/QuickSelect: randomized (or median-of-three) pivot plus three-way Dutch-flag partition — LC 912 Sort an Array, kth-largest on an array with heavy duplicates | Random pivot selection for expected O(n log n) / O(n), and a <, =, > three-way split so runs of equal keys are consumed in one pass | Both partition files pick array[high] unconditionally and there is no call to Random anywhere in the folder. That means sorted or reverse-sorted input degrades to O(n^2), and an all-equal array degrades to O(n^2) even with a random pivot unless the partition is three-way. 'What is the worst case and how would you avoid it?' follows essentially every quicksort or quickselect answer, so this is the gap most likely to be probed and least likely to be survivable right now. He has Dutch-flag as a standalone problem (C02_Sort012.java in 01-Arrays) but has never wired it into a partition, which is the form interviewers want. |
| medium | Counting Sort and LSD Radix Sort (and the H-Index shape that uses counting) | Stable distribution by key: counting sort with a prefix-sum offset table, applied digit-by-digit for radix | His only non-comparison sort is BucketSort.java, hardcoded for floats in [0,1) with an O(n^2) preallocation. The standard follow-up 'can you beat n log n for 10 million 32-bit integers?' expects counting or radix, and radix specifically requires understanding that the per-digit subsort must be stable — a property he can currently only assert about merge sort, never having implemented a stable distribution pass. Maximum Gap already gave him the pigeonhole intuition, so this is a short build on existing ground rather than new theory. |
| medium | Largest Number (LC 179), plus the general comparator-defined ordering pattern | Custom comparator — order two strings by comparing a+b against b+a — and an argument for why that comparator is transitive | There is no Comparator in this folder at all. Comparator-defined order is the most common real-world sorting question at product companies, and Largest Number is its cleanest test because the correct comparator is non-obvious and the transitivity proof is exactly the kind of reasoning a staff-level interviewer digs into. For a Java candidate it also opens the adjacent question he should be able to field — why Arrays.sort uses dual-pivot quicksort for primitives but TimSort for objects, and what 'Comparison method violates its general contract!' actually means. |
| medium | Merge Sorted Array in place (LC 88) | Two pointers filling backward from the end of the larger array, so no extra buffer is needed | Very high frequency as a phone-screen or warm-up, and it is genuinely absent — MergeTwoSortedLists.java in 06-Linked-List is a different shape (pointer rewiring, no in-place constraint, no overwrite hazard). His merge sort merges into a fresh temp array, so the specific insight that filling forward destroys unread data and filling backward does not has never been exercised. Cheap to close, embarrassing to fumble. |
| medium | Count of Smaller Numbers After Self (LC 315) | Merge sort carrying original indices, or a Fenwick/segment tree over value ranks | This is the problem that connects his SegmentTree.java to his MergeSort.java, and right now those two files are isolated from each other. It is a common senior-level hard because it admits two valid solutions from different families, and the follow-up 'which would you pick and why' is a real trade-off conversation (offline divide-and-conquer versus an online counting structure, plus coordinate compression). Distinct from plain inversion counting: returning a per-index answer forces index carrying through the merge, which the scalar version never teaches. |
| low | External / k-way merge sort — sort a 10 GB file with 1 GB of RAM | Chunk, sort in memory, spill to disk, then k-way merge the runs with a min-heap of size k | The standard staff-level extension of merge sort in a DSA round: it checks whether he understands that merge is streaming and needs only one element per run resident. He has the pieces — heaps in 08 and MergeKLists.java in 06 give him the k-way merge core — but never assembled under a memory bound, and the numeric part (how many runs, how large a buffer, why merge in passes when k exceeds available buffers) is where unprepared candidates stall. Lower priority because it is usually a discussion rather than a coded solution. |

