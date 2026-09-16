# 08-Heap-Priority-Queue — must-know order

**Techniques in this topic:** Bounded size-k heap: keep exactly k candidates, evict the root — the single primitive behind top-K, k-closest and kth-in-stream, Two balanced heaps (max-heap for the lower half, min-heap for the upper half) to hold a running median in O(log n) per insert, Greedy driven by a heap: repeatedly take the locally best item — highest frequency, largest marginal gain, earliest end day, Regret / retroactive greedy: buffer every candidate in a heap and undo or upgrade the worst past choice when you run out of resource, Custom comparators over objects and computed keys (squared distance, marginal gain, node frequency) — plus the a-b integer-overflow trap, Streaming and sliding-window state: maintain a heap invariant per element rather than recomputing, including removal of elements leaving the window, Sort-then-heap scheduling: sort by start, heap on end — the standard interval/event greedy shape

| | |
|---|---|
| Problems | 14 |
| Must-know | 4 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_KthLargestElementInAStream.java` | Bounded size-k min-heap |
| 2 | `C01_ImportantTopKFrequent.java` | Frequency map + size-k min-heap |
| 3 | `C03_KClosestPointsToOrigin.java` | Size-k max-heap on a computed key |
| 4 | `D01_ImportantMedianOfStream.java` | Two balanced heaps for median |

## Full practice order


### A — Building blocks

- `A01_KthLargestElementInAStream.java` — Bounded size-k min-heap **[must-know]**
  - The one primitive the whole folder assumes: a min-heap capped at k whose root is the kth largest — learn this before any top-K variant.

### B — Easy

- `B01_LastStoneWeight.java` — Max-heap simulation loop
  - Pure PriorityQueue mechanics — reverseOrder for a max-heap, poll twice, push the remainder back; the warm-up that makes the API muscle memory.

### C — Medium

- `C01_ImportantTopKFrequent.java` — Frequency map + size-k min-heap **[must-know]**
  - First real application of the size-k heap: count with a HashMap, then heap the entries — the most frequently asked heap question in interviews.
- `C02_topKFrequent.java` — Frequency map + size-k min-heap
  - Same problem as the file above, so it costs nothing once that one is solid — use it purely as a rewrite-from-scratch drill.
- `C03_KClosestPointsToOrigin.java` — Size-k max-heap on a computed key **[must-know]**
  - Flips the heap direction: a max-heap of size k evicting the worst, keyed on squared distance — teaches comparators over a derived value, not the element itself.
- `C04_HuffmanCoding.java` — Merge-two-smallest greedy tree build
  - Extends poll-two-push-one from LastStoneWeight to custom Node objects and a built tree; the classic optimal-merge greedy that also shows up as merge-stones/rope-cutting.
- `C05_ReorganizeString.java` — Frequency max-heap, dual poll
  - Combines the frequency map from top-K with the two-at-a-time poll from Huffman, plus the (n+1)/2 feasibility check — same shape as Task Scheduler.
- `C06_MaximumAveragePassRatio.java` — Max-heap keyed by marginal gain
  - Introduces poll, mutate, re-offer with a double comparator: the greedy key is a delta that changes after each pick, so the heap must be re-entered.
- `C07_MaximumNumberofEventsThatCanBeAttended.java` — Sort by start, min-heap on end
  - The interval-scheduling shape — sweep the day counter, push events that opened, drop expired ends, attend the soonest-ending; needed before any hard greedy here.
- `C08_FurthestBuildingYouCanReach.java` — Regret heap of size = ladders
  - First deferred-decision greedy: tentatively ladder every climb, keep only the ladders largest in a min-heap, pay bricks for what falls out — the hardest medium and the bridge into tier D.
- `C09_FurthestBuildingExample.java` — Same problem, max-heap swap variant
  - Same problem solved by swapping the largest brick spend for a ladder instead; worth a read only after the min-heap version to see why the cleaner framing wins.

### D — Hard

- `D01_ImportantMedianOfStream.java` — Two balanced heaps for median **[must-know]**
  - The two-heap technique itself — split-and-rebalance invariant, O(log n) insert, O(1) median; a staple design-flavoured hard that unlocks every median variant.
- `D02_SlidingWindowMedian.java` — Two heaps with window removal
  - Median-of-stream plus eviction: arbitrary remove breaks the balance, so you must rebalance after every slide — only attempt once the plain two-heap version is automatic.
- `D03_MinimumRefuelingStops.java` — Retroactive greedy max-heap
  - The regret idea from Furthest Building at hard level: bank every station you pass and refuel from the largest only when you actually run dry.

## Interview readiness

This folder is genuinely strong — close to interview-ready, not a beginner set. The 14 files cover every load-bearing heap primitive: the bounded size-k heap (KthLargestElementInAStream, both TopKFrequent files, KClosestPointsToOrigin), two balanced heaps for a running median including the hard sliding-window variant with lazy deletion (ImportantMedianOfStream, SlidingWindowMedian), pure heap greedy (LastStoneWeight, ReorganizeString, MaximumAveragePassRatio, HuffmanCoding), regret/retroactive greedy (FurthestBuildingYouCanReach, MinimumRefuelingStops), and sort-then-heap scheduling (MaximumNumberofEventsThatCanBeAttended). Cross-folder coverage closes several gaps that would otherwise look alarming: MergeKLists (heap k-way merge) sits in 06-Linked-List, MeetingRoomsIII (dual free/busy heaps) and DivideIntervalsIntoMinGroups in 15-Intervals, DesignAFoodRatingSystem and StockPriceFluctuation (heap + hashmap with lazy deletion) in 19-Design-Data-Structures, DjikstrasAlgoPQ/PrimsAlgo/CheapestFlight in 11-Graphs, and HeapSort plus two QuickSelect implementations in 18-Sorting-Searching-Algorithms — so I did not flag Kth Largest Element in an Array, Merge k Sorted Lists, Meeting Rooms II, IPO (structurally identical to his MinimumRefuelingStops) or Course Schedule III (identical to FurthestBuilding) as missing. What is actually absent is a small, coherent set: no problem where the heap maintains a running aggregate (a sum) under eviction rather than just membership; no heap frontier over a 2D candidate space needing a visited-set to avoid duplicate pushes; no sweep-line-with-lazy-deletion at the hard tier; and no comparator with a string tie-break, which is where the size-k-heap inversion trap bites. Fix the three high-priority items and this folder clears a senior bar comfortably; the mediums are what separate a pass from a strong-hire at staff.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Task Scheduler (LC 621) | Max-heap by remaining count plus a cooldown queue, and the O(1) counting-formula alternative ((maxFreq-1)*(n+1) + countOfMaxFreq) | One of the three most-asked heap problems at Amazon, Meta and Uber. ReorganizeString gets him the heap-plus-cooldown simulation, but the interviewer's real follow-up is the idle-slot formula and 'now output the actual task order' — two distinct insights that nothing in this folder teaches. Currently he would simulate it correctly and then stall on the O(1)-space push. |
| high | Find K Pairs with Smallest Sums (LC 373), with Kth Smallest Element in a Sorted Matrix (LC 378) as the sibling | Heap frontier over a 2D candidate grid: push (i,j) successors with a visited set for dedup, plus binary-search-on-answer as the alternative for 378 | His only k-way merge is MergeKLists, where each popped node has exactly one successor, so dedup never arises. Here every pop has two successors and the naive push generates duplicates — the classic failure. Both problems are standard Amazon/Google phone-screen fare and the 378 binary-search-on-value follow-up is a separate skill he has no rep for. |
| high | Maximum Performance of a Team (LC 1383), with Minimum Cost to Hire K Workers (LC 857) and Maximum Subsequence Score (LC 2542) as siblings | Sort by one key descending, keep a size-k min-heap on the second key while maintaining a running sum that is decremented on every eviction | This is the one heap primitive genuinely absent from his themes: the heap holds an aggregate, not just a membership set, so every poll must also update a sum. KClosestPoints and TopKFrequent evict without any bookkeeping. The whole family is common at Amazon and Google, and the bug that fails candidates — forgetting to subtract on eviction, or fixing the sorted key at the wrong end — is only learned by doing one of these. |
| medium | Employee Free Time (LC 759) | Min-heap over the heads of k sorted interval lists, sweeping for gaps where the merged coverage drops to zero | A Google and Meta staple, and distinct from the MeetingRoomsII/DivideIntervals work he already has: the output is the complement (the gaps) across k separate schedules rather than a peak-concurrency count. It is also the cleanest test of whether he can merge k sorted streams when the elements are intervals rather than scalars. |
| medium | The Skyline Problem (LC 218) | Sweep line over sorted events with a max-heap (or TreeMap multiset) of active heights, using lazy deletion because the heap cannot remove an arbitrary interior element | The canonical hard heap problem and a real senior/staff discriminator at Meta and Google. He has lazy deletion in SlidingWindowMedian, but only for a window where removals are known in advance; Skyline forces him to reason about a multiset keyed by height with out-of-order removals and about ties between start and end events at the same x. Without a rep here, this is a 45-minute stall. |
| medium | Top K Frequent Words (LC 692) | Size-k min-heap with a composite comparator: frequency ascending but lexicographic order descending, then reverse the output | Looks like a duplicate of his two TopKFrequent files and is not. Both of his are numeric, so he has never hit the trap that when you keep a size-k min-heap, the tie-break key must be inverted relative to the final answer. Getting this backwards silently returns the wrong words and is exactly the small comparator bug that sinks an otherwise clean senior interview. |
| medium | Single-Threaded CPU (LC 1834) | Sort by enqueue time, min-heap keyed on (processing time, original index), simulate a clock that jumps forward to the next enqueue time when the heap empties | Very frequently asked at Amazon. MeetingRoomsIII gives him the dual-heap server shape but never the idle-clock jump — the failure mode is advancing time by one unit in a loop and timing out, or stalling when no task is available. The 'keep the original index as the tie-break inside the comparator' detail is also a common dropped requirement. |
| low | Design Twitter (LC 355) | Hashmap of follow sets plus per-user tweet lists, merged with a bounded size-10 heap over the k followee feeds | A design-and-heap hybrid that shows up in senior loops at Meta and Amazon, and the one place where the k-way merge has to be justified out loud against the alternative (push-on-write fan-out vs pull-on-read merge). His design folder is otherwise excellent, so this is a cheap addition, but it is a discussion he has not rehearsed rather than an algorithm he cannot write. |

