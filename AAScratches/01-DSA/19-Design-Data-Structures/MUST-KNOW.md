# 19-Design-Data-Structures — must-know order

**Techniques in this topic:** HashMap + doubly linked list: the O(1) cache spine (LRU -> LFU -> AllOne bucket list), Array + index map with swap-with-last, for O(1) insert/delete/getRandom, Heap with lazy deletion: push a new version, validate against the source-of-truth map on peek, Sorted history + binary search / TreeMap.floorKey for point-in-time and versioned reads, Sliding-window queue over timestamps: hit counters, rate limiters, expiry, Trie of path segments (nested maps) for hierarchical namespaces and file systems, Augment each entry with a running aggregate, or a parallel lazy-propagation array, to keep stack ops O(1), Deque + HashSet to get ordered traversal and O(1) membership in the same structure, Bucket-by-frequency + minFreq/maxFreq pointers to avoid scanning on eviction, Amortized O(1) reasoning: what you may defer to a later call and still claim the bound

| | |
|---|---|
| Problems | 23 |
| Must-know | 6 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_MinStack.java` | Augment entry with running aggregate |
| 2 | `C01_DesignHitCounter.java` | Sliding-window queue of timestamps |
| 3 | `C04_InsertDeleteGetRandomOof1.java` | Array plus index map, swap-with-last |
| 4 | `C07_ImportantLRUCacheMain.java` | HashMap plus doubly linked list |
| 5 | `C08_TimeBasedKeyValueStore.java` | TreeMap floorKey per key |
| 6 | `D01_LFUCache.java` | Frequency buckets plus minFreq pointer |

## Full practice order


### A — Building blocks

- `A01_RandomPickIndex.java` — Uniform random index selection
  - Pure primitive — Random.nextInt(bound) with an exclusive upper bound; the randomized structures later all assume this is free and correct.
- `A02_MinStack.java` — Augment entry with running aggregate **[must-know]**
  - The archetypal design move — store (value, min-so-far) per entry so a query is O(1); every later 'make this operation constant time' problem is a variant of it.

### B — Easy

- `B01_LoggerRateLimiter.java` — Map key to next-allowed timestamp
  - Simplest possible time-window design; introduces storing an expiry instead of storing events, which the hit counter and auth problems both reuse.
- `B02_AuthenticationManager.java` — Token expiry map, linear count
  - Adds renew-only-if-still-valid on top of the logger's expiry map; the linear countUnexpired scan is the naive baseline an interviewer will ask you to beat.
- `B03_DesignAuthenticationManager.java` — Token expiry map, linear count
  - Same LC 1797 as the file above with identical logic — use it as the from-scratch rewrite drill, not as new material.

### C — Medium

- `C01_DesignHitCounter.java` — Sliding-window queue of timestamps **[must-know]**
  - First real amortized-O(1) argument in the folder: evict from the front lazily on read; this is the rate-limiter pattern staff interviews open with.
- `C02_HitCounter.java` — Sliding-window queue of timestamps
  - Same problem, Deque-based; note the boundary differs (>= WINDOW vs <=) — worth diffing against the file above to pin the off-by-one.
- `C03_HitCounterMain.java` — Sliding-window queue of timestamps
  - Third copy of the hit counter, with a runnable main — use it purely as the timed rewrite after the two above.
- `C04_InsertDeleteGetRandomOof1.java` — Array plus index map, swap-with-last **[must-know]**
  - Needs the Random primitive from tier A; teaches the swap-with-last deletion that keeps an array gap-free — a trick reused all over array-backed design.
- `C05_RemoveInsertGetO1.java` — Array plus index map, swap-with-last
  - Duplicate of LC 380 above; drill the index-bookkeeping order (update the map before shrinking the list) which is where people break it.
- `C06_DesignAStackWithIncrementOperation.java` — Lazy increment propagation array
  - Builds directly on MinStack's parallel-array idea, but defers work instead of precomputing it — the cleanest small example of lazy propagation.
- `C07_ImportantLRUCacheMain.java` — HashMap plus doubly linked list **[must-know]**
  - The single most-asked design question and the spine of the whole tier-D chain; LFU and AllOneII are unreadable until this is muscle memory.
- `C08_TimeBasedKeyValueStore.java` — TreeMap floorKey per key **[must-know]**
  - Introduces point-in-time reads — the largest timestamp <= query; constant at product companies and the prerequisite framing for SnapshotArray.
- `C09_SnapshotArray.java` — Per-index version list, binary search
  - Same floor-search idea as the time-based store but hand-rolled over an append-only version list, plus the insight that you only record on change.
- `C10_StockPriceFluctuation.java` — Two heaps with lazy deletion
  - Introduces the technique that unlocks the rest of the heap designs: never delete from the heap, validate the peek against the authoritative map instead.
- `C11_DesignAFoodRatingSystem.java` — Grouped heaps, lazy deletion
  - Applies the previous file's lazy-deletion trick per group and adds a comparator tie-break, which is where most candidates lose points.
- `C12_DesignSnakeGame.java` — Deque plus set, 1D cell encoding
  - Combines ordered traversal with O(1) membership, and adds the row*width+col encoding trick; heavier on case analysis than on new data structures.
- `C13_CBTInserter.java` — Queue of incomplete parent nodes
  - Moves the queue idea from timestamps onto a tree — the insight is that only nodes with a free child slot need to be retained.
- `C14_DesignFileSystem.java` — Trie of path segments
  - Introduces the nested-map path trie and the parent-must-exist rule; placed last in C because the hard file-system problem in D extends it directly.

### D — Hard

- `D01_LFUCache.java` — Frequency buckets plus minFreq pointer **[must-know]**
  - The direct step up from LRU: a list per frequency plus a minFreq pointer to keep eviction O(1); the standard staff-level cache follow-up.
- `D02_AllOne.java` — Frequency-to-key-set maps
  - Reuses LFU's frequency bucketing but tracks both min and max; this map-of-sets version is the approach to state first, then attack its weakness.
- `D03_AllOneII.java` — Ordered bucket doubly linked list
  - Fixes the previous file's non-constant min/max by chaining buckets in sorted order — the true O(1) answer, and the hardest pointer surgery here.
- `D04_DesignInMemoryFileSystem.java` — Directory trie with file contents
  - Extends the tier-C path trie with mkdir -p semantics, file append, and sorted ls; hard from accumulated edge cases rather than one insight.

## Interview readiness

This is one of the strongest design folders I have reviewed for a senior/staff loop — the hard, high-ceiling problems are all there and done more than once. He owns the O(1) cache spine end to end (LRU, LFU, AllOne), the array-plus-index-map randomized set, lazy-deletion heaps (StockPrice, FoodRating), versioned point-in-time reads (TimeBasedKV, SnapshotArray), timestamp windows (HitCounter, LoggerRateLimiter, AuthenticationManager) and hierarchical tries (DesignFileSystem, InMemoryFileSystem). Nothing on my "he would fail the round" list is missing at the hard end. The gaps are of two specific kinds, and both are cheap to close. First, an entire canonical family is absent from the whole DSA tree, not just this folder: iterator design. There is no BST Iterator, no Flatten Nested List Iterator, no Peeking or Zigzag Iterator anywhere. That pattern — the hasNext/next contract, doing the work lazily instead of materialising in the constructor, amortized O(1) with O(h) space, and the "what if next() is called without hasNext()" follow-up — is asked constantly at Meta, Google and Amazon, and none of his ten covered themes reach it. Second, he has the exotic structures but skipped several of the plain, extremely high-frequency ones that open real interviews: Design Twitter (composition plus k-way merge, and it is literally on his own Classic 150 roadmap), Design Underground System (the single most-asked design problem in Amazon phone screens), and a fixed-capacity ring buffer, which appears nowhere in the tree. He also has two copies of LC 380 and neither is the duplicates variant, so the standard live escalation from that question is untested. Net: he is ready for the hard half of the design bar today and would likely lose a round to an easy-looking warm-up or an iterator, not to a cache. Closing the four high-priority items below is roughly a weekend of work and would make this folder genuinely complete. Beyond the eight listed, the next tier I would add later — Design Browser History (1472), Design Tic-Tac-Toe (348, on his Atlassian list), Max Stack (716) and Design Search Autocomplete System (642) — are worth doing but each is close enough to a technique he already owns that I would not call them interview-costing.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | BST Iterator (LC 173) | Controlled inorder traversal driven by an explicit stack: push the left spine in the constructor, and on next() pop, then push the left spine of the popped node's right child. O(h) space, amortized O(1) per call. | The iterator-design family is missing from the entire DSA tree, not just this folder — I grepped 19, 09, 07 and found no BSTIterator, PeekingIterator, ZigzagIterator or NestedIterator. This is the most commonly asked member of that family and it tests something none of his ten themes cover: the hasNext/next contract, and the argument for why you may NOT flatten the tree into a list in the constructor (O(n) memory on a huge tree). His 09-Trees-BST folder has ~55 traversal problems, all of which run to completion; none of them pause and resume. The standard follow-ups (add prev(), make it O(1) space with Morris) also have no foothold in what he has done. |
| high | Design Twitter (LC 355) | Composition: HashMap<userId, Deque<Tweet>> for each user's own tweets, HashMap<userId, Set<followeeId>> for the follow graph, and a max-heap k-way merge over the followee lists (bounded to 10) to build the feed. Global monotonic counter as the timestamp. | Absent from the folder and present in his own roadmap file at 01-DSA/roadmaps/Classic_150_Roadmap.md line 150 and LeetCode_500_Links.md — so he has already flagged it and not done it. It is the canonical 'compose three structures under one API' design question at product companies, and it is the only one in this set where the interviewer expects you to reason about which operation absorbs the cost: push-model (fan out on write, cheap reads) versus pull-model (merge on read, cheap writes). He has heaps and he has hash maps, but he has never merged k sorted streams lazily behind a service-shaped API, and that merge is exactly where candidates stall. |
| high | Design Circular Queue and Circular Deque (LC 622, 641) | Fixed-capacity array ring buffer with head and tail indices advanced modulo capacity, plus an explicit size counter (or a sacrificed slot) to disambiguate full from empty. | There is no ring buffer anywhere in the tree — I searched all 20 folders for circular queue and ring buffer and got zero hits. QueueUsingStacks in 07 is a different problem. This is a frequent opener at Amazon, Microsoft and Bloomberg, and the failure is specific and brutal: head == tail means both full and empty, and candidates who have not built one before produce an off-by-one that only shows up at wraparound, in front of the interviewer. Everything he has designed so far grows without bound; nothing is capacity-bounded with wraparound. It is also the substrate for follow-ups like bounded blocking queue and Design Front Middle Back Queue. |
| medium | Design Underground System (LC 1396) | Two hash maps: one keyed by customer id holding the in-progress check-in (station, time), one keyed by the composite start->end route string holding a running (sum, count) pair so getAverageTime is O(1). | Purely a frequency argument — this is the most-asked design question in Amazon phone screens and it also shows up at Google and Uber, and it is not in the folder. The technique is adjacent to his 'augment each entry with a running aggregate' theme (StockPriceFluctuation, DesignAFoodRatingSystem), so he will not be lost, but the specific moves are untested: modelling a check-in/check-out lifecycle where an entry is provisional until a second call completes it, and building a composite key from two fields instead of using a nested map. Being asked a question everyone else has drilled and solving it a beat slower is a real scoring loss at senior level even when the answer is correct. |
| medium | Flatten Nested List Iterator (LC 341) | Stack of iterators (or stack of NestedInteger positions) unwound lazily inside hasNext(); hasNext() does the flattening work and next() stays trivial. | The second half of the missing iterator family, and a genuinely different technique from BST Iterator: here the structure is arbitrarily nested rather than a known shape, so the state you carry is a stack of partially consumed iterators. It is a Meta and Google staple. The key teaching point — that all the work belongs in hasNext() and not next(), and that flattening eagerly in the constructor defeats the purpose when the input is a stream — is the same discipline as 173 but with a second failure mode he has not seen: empty nested lists like [[],[[]]] that require advancing past several levels before you can honestly answer hasNext(). |
| medium | Insert Delete GetRandom O(1) - Duplicates allowed (LC 381) | Same array-plus-index-map spine, but the map becomes value -> Set<index>; on remove, swap the victim with the last element and repair BOTH index sets, which is the step that breaks people. | He has LC 380 twice — InsertDeleteGetRandomOof1.java and RemoveInsertGetO1.java are the same no-duplicates problem — and the duplicates variant nowhere. This matters because 381 is not a separate question in practice; it is the follow-up the interviewer asks in the last ten minutes after you finish 380 cleanly, which he will. The trap is precise: when you swap the last element into the removed slot, you must remove the OLD index of the moved element from its own set and add the new one, and you must handle the case where the victim IS the last element (removing then adding in the wrong order corrupts the set). Deriving that live under time pressure is much harder than it looks on paper. |
| medium | Design HashMap / Design HashSet from scratch (LC 706, 705) | Bucket array with separate chaining (linked list or list per bucket), a hash function plus index compression, load-factor tracking and resize/rehash to keep chains short. | Every structure in this folder is built ON a HashMap; he has never built one. At staff level interviewers periodically pull the floor out with 'now implement the map itself' to check whether the O(1) you keep claiming is understood or recited. It is also listed in his own 01-DSA/roadmaps/LeetCode_500_Links.md at line 385 and not done. The part he cannot currently answer from anything in this folder is the amortization argument for resizing — why doubling the bucket array and rehashing every entry still leaves put() at amortized O(1) — which is the same reasoning muscle as his 'amortized O(1): what you may defer' theme, but applied to memory growth rather than deferred cleanup. |
| medium | My Calendar I and II (LC 729, 731) | TreeMap<start, end> with floorKey/ceilingKey to test overlap in O(log n) for booking; My Calendar II adds a second map of double-booked intervals, or a sweep-line delta map where you reject when any running count would exceed the limit. | Genuinely absent, and it is in his Atlassian list at 01-DSA/roadmaps/Atlassian_Question_List.md line 153. His 15-Intervals folder is entirely static — MergeIntervals, InsertInterval, MeetingRooms I/II/III all take the full input up front — so he has never maintained an interval set incrementally where each booking must be accepted or rejected against everything booked so far. He does have the TreeMap.floorKey reflex from TimeBasedKeyValueStore and SnapshotArray, which makes Calendar I derivable, but Calendar II is a real step up: the 'count the overlaps without scanning' idea via a sweep-line delta map is the bridge to Calendar III and Range Module, and it is the standard Google booking-system progression. |

