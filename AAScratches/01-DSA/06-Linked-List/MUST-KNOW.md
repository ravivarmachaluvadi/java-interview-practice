# 06-Linked-List — must-know order

**Techniques in this topic:** Dummy/sentinel head: build a new chain as you walk, so the head needs no special case (merge, add-two-numbers, partition, sort-0s1s2s), Fast/slow two pointers: middle of list, Floyd cycle detect + cycle start + cycle length, and the fixed-gap variant for nth-from-end, In-place reversal as a reusable subroutine: iterative prev/curr/next, the recursive framing, and the windowed k-group version, Split-and-splice: peel one list into two or three independent chains, then relink the tails (partition, odd/even, Dutch flag on a list), Composition pattern - find middle, reverse the second half, walk both halves together (palindrome, twin sum, reorder list), Multi-list merging: two-pointer merge scaling up to a min-heap over k heads, and merge sort applied to a list instead of an array, Length arithmetic: count, normalise k, close the loop or advance the longer list by the difference (rotate right, intersection), Cloning with structural aliasing: a map from original node to copy, with the O(1)-space interleave-and-split as the follow-up, Reference semantics in Java - reassigning a variable never moves the node it pointed at; this is the source of most off-by-one pointer bugs

| | |
|---|---|
| Problems | 26 |
| Must-know | 8 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_ReverseLinkedList.java` | Iterative prev/curr/next reversal |
| 2 | `A04_FindMiddleOfLinkedList.java` | Slow/fast pointer midpoint |
| 3 | `A05_MergeTwoSortedLists.java` | Dummy-head two-pointer merge |
| 4 | `C02_DeleteNthNodefromEnd.java` | Fixed-gap two pointers |
| 5 | `C07_LinkedListLoopDetection.java` | Floyd cycle detect and entry point |
| 6 | `C10_ImpPalindromeLinkedList.java` | Middle, reverse half, compare, restore |
| 7 | `D01_ReverseListInKGroups.java` | Windowed reversal with relink |
| 8 | `D02_MergeKLists.java` | Min-heap over k list heads |

## Full practice order


### A — Building blocks

- `A01_ListNodeReferencing.java` — Java reference semantics demo
  - Not a problem at all - it establishes that a variable is a handle, not the node, which is the mental model every pointer rewiring below depends on.
- `A02_ReverseLinkedList.java` — Iterative prev/curr/next reversal **[must-know]**
  - The single most reused subroutine in the folder - palindrome, twin sum, reorder, k-groups and DLL reverse all call it or inline it.
- `A03_ZReverseLinkedListRecursive.java` — Recursive list reversal
  - Same primitive expressed recursively; teaches the head.next.next = head unwind trick that later recursive list work assumes.
- `A04_FindMiddleOfLinkedList.java` — Slow/fast pointer midpoint **[must-know]**
  - The other universal primitive - once you own the loop guard fast != null && fast.next != null, cycle detection, palindrome, reorder and merge sort all fall out.
- `A05_MergeTwoSortedLists.java` — Dummy-head two-pointer merge **[must-know]**
  - Introduces the sentinel head and is literally the merge step inside SortList and the two-list case of MergeKLists.
- `A06_DeleteHeadOfDLL.java` — Doubly linked list node detach
  - The minimal pointer-surgery drill on a two-way node - detach both directions before returning, which is the discipline LRU-cache style questions demand.
- `A07_DoublyLinkedListReverse.java` — Swap prev/next per node
  - Closes the primitives tier by showing reversal on a DLL is just a swap, once the single-list reversal and the DLL detach above are second nature.

### B — Easy

- `B01_RemoveDuplicatesInList.java` — Skip-forward on sorted list
  - Gentlest application of the reference model - decide whether to advance or to unlink, and never advance on the iteration you unlinked.
- `B02_MergeNodesInBetweenZeros.java` — Accumulate segment, reuse node
  - Adds segment boundaries to the same single walk, and reuses existing nodes instead of allocating - a cheap rehearsal for in-place compaction.
- `B03_MergeInBetweenLinkedLists.java` — Index-walk and splice a list in
  - First problem that holds two cut points at once; trivial once you can count to a-1 and to b, but it is where off-by-one errors start.
- `B04_IntersectionOfTwoLinkedLists.java` — Length difference then walk together
  - Introduces length arithmetic across two lists, which RotateRightList then reuses; also the setup for the elegant pointer-swap follow-up an interviewer will ask for.

### C — Medium

- `C01_AddTwoNumbers.java` — Dummy head with carry propagation
  - The first real use of the sentinel from tier A, plus the loop condition l1 != null || l2 != null || carry != 0 that handles the trailing carry in one place.
- `C02_DeleteNthNodefromEnd.java` — Fixed-gap two pointers **[must-know]**
  - Generalises slow/fast from a 2:1 ratio to an arbitrary N-node gap - the idea behind every sliding window on a list, and an interview staple.
- `C03_PartitionList.java` — Two dummy chains, stable relink
  - First split-and-splice: two sentinels collect two chains in one pass, and you must null-terminate the second chain or you build a cycle.
- `C04_SortaLLof0s1sand2s.java` — Dutch flag with three chains
  - Direct generalisation of PartitionList to three buckets; the only new work is the join, including the case where the middle chain is empty.
- `C05_OddEvenLinkedList.java` — Weave two chains in place
  - Same split-and-splice idea without sentinels, so you must interleave both pointer advances correctly - the tightest pointer choreography so far.
- `C06_RotateRightList.java` — Close into a ring, cut at length-k
  - Turns the length arithmetic from IntersectionOfTwoLinkedLists into a deliberate temporary cycle, and forces the k % length normalisation.
- `C07_LinkedListLoopDetection.java` — Floyd cycle detect and entry point **[must-know]**
  - The non-obvious second phase - reset one pointer to head and step both by one - is asked constantly and you must be able to justify why it lands on the entry node.
- `C08_LinkedListLoopLength.java` — Count from the meeting node
  - The natural follow-up once Floyd's meeting point exists; cheap to learn here and it confirms you understood what the meeting point actually is.
- `C09_MaximumTwinSum.java` — Middle, reverse half, pair walk
  - The simplest composition of the two tier-A primitives - guaranteed even length, no restore required, so it is the right warm-up before palindrome.
- `C10_ImpPalindromeLinkedList.java` — Middle, reverse half, compare, restore **[must-know]**
  - Same composition as twin sum but with odd-length handling and restoring the list afterwards - the O(1)-space version is what separates a senior answer from a stack-based one.
- `C11_ReorderList.java` — Middle, reverse half, interleave
  - Hardest member of the middle-plus-reverse family - you must cut at the middle, reverse, then alternate-merge while saving both next pointers before overwriting either.
- `C12_SortList.java` — Merge sort on a linked list
  - Combines the split (getMid must keep prev to unlink) with the tier-A merge; the O(n log n) with O(log n) stack answer senior interviews expect.
- `C13_CopyRandomList.java` — Deep copy via node-to-copy map
  - Capstone of the medium tier - the map version is easy, but the expected O(1)-space follow-up is interleave-then-split, which needs the weaving from OddEvenLinkedList.

### D — Hard

- `D01_ReverseListInKGroups.java` — Windowed reversal with relink **[must-know]**
  - Takes the tier-A reversal and makes you bookkeep three boundaries per window plus the leave-the-tail-alone rule - the classic hard test of raw pointer control.
- `D02_MergeKLists.java` — Min-heap over k list heads **[must-know]**
  - Scales the tier-A merge to k lists and is where the interview turns into a complexity discussion - heap O(N log k) versus pairwise divide and conquer.

## Interview readiness

This is one of the stronger linked-list folders I'd expect to see: 26 problems covering essentially the entire Blind-75 / Grind-169 core (reverse iterative + recursive, middle, Floyd cycle detect with entry node and cycle length, nth-from-end, palindrome, intersection, merge two and merge k, merge sort on a list, copy-with-random-pointer, reorder, rotate, k-group reversal, partition, odd/even, Dutch flag on a list), and the pattern vocabulary he's built — dummy head, fast/slow, reverse-as-a-subroutine, split-and-splice, find-middle-reverse-walk-together — is exactly the right set of primitives. He would pass the large majority of linked-list rounds today. The gaps are not in the headline problems; they are in the second tier, and they cluster in three places. First, the "II" variants that are the versions an interviewer actually escalates to after the easy one (delete ALL duplicates rather than the extras, add two numbers with digits in forward order, reverse a sublist by index) — he has the easy sibling of each, which is precisely the setup for a follow-up he hasn't drilled. Second, doubly and circular lists: his DLL work is two thin files (delete head, reverse) and he has zero practice inserting into a circular sorted list, so an edge-case-heavy pointer problem in that shape would be new territory under time pressure. Third, using a list as the carrier for a technique from another topic (prefix-sum over nodes, monotonic stack over nodes), which is the staff-level flavour of this topic. The realistic failure mode is not "he can't reverse a list" — it is thirty-five minutes in, when the interviewer says "now delete every node that has a duplicate" or "now the digits are most-significant-first and you may not reverse the input."

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Remove Duplicates from Sorted List II (LC 82) | Dummy head + prev pointer, skip an entire run of equal values rather than just the trailing copies | He has LC 83 (keep one copy), which is the easy sibling and the standard warm-up an interviewer escalates from. LC 82 is a different algorithm: you must hold a prev pointer before the run, detect the run length, and splice past all of it, and the two failure cases (duplicates at the head, a run that ends at the tail) are exactly what the dummy node exists for. This is the single most likely follow-up he currently cannot write clean on the first pass. |
| high | Add Two Numbers II (LC 445) | Carry propagation from the least significant end when digits are stored most-significant-first — two stacks, or reverse-add-reverse, with the front-insertion build | AddTwoNumbers.java is LC 2 with digits already reversed, which is the toy version. LC 445 is the one that gets asked because it forces a real decision: reverse the inputs (mutating the caller's lists) versus push both lists onto stacks and build the result by inserting at the front. The front-insertion build with a running carry is a distinct motion he has not practised, and the interviewer's stated constraint is usually 'without modifying the input lists', which kills the reverse shortcut. |
| high | Flatten a Multilevel Doubly Linked List (LC 430) | DFS over a list using an explicit stack, splicing a child list inline while maintaining both next and prev pointers | His doubly-linked-list coverage is two thin files (delete head, reverse), and nothing in the folder requires maintaining prev correctly across a splice. LC 430 is an Amazon/Microsoft staple and is unforgiving: forget to null out node.child, or forget to set prev on the spliced-in head or on the node after the tail, and it silently produces a corrupt list that only fails on the reverse traversal. It is also the cheapest way to turn his DLL section from a gap into real coverage. |
| medium | Reverse Linked List II (LC 92) | Reverse a window given 1-indexed left/right bounds — walk to the node before left, reverse right-left+1 nodes, relink both ends | ReverseListInKGroups covers the harder windowed-reversal mechanics, so he can almost certainly derive this, which is why it is medium and not high. The risk is narrower: the index arithmetic (left-1 steps to the predecessor, right-left+1 nodes to flip) and the case left == 1 where the predecessor is the dummy, are a classic off-by-one trap under time pressure. It is asked often enough in its own right that he should have written it once. |
| medium | Insert into a Sorted Circular Linked List (LC 708) | One full traversal of a circular list with a pivot check, plus termination on returning to the start | Nothing in the folder inserts into a circular structure — his only circular exposure is detecting a cycle and closing the loop in RotateRightList. This problem is almost entirely edge cases: empty list, single node pointing at itself, all values equal (which makes the loop non-terminating unless you stop when you return to head), and the value falling outside the range at the pivot between max and min. It is a long-standing Meta favourite and is famous for looking trivial and then failing on four hidden cases. |
| medium | Remove Zero Sum Consecutive Nodes from Linked List (LC 1171) | Running prefix sum over nodes with a HashMap from prefix value to node, splicing out the span between two equal prefixes | This is the staff-level flavour of the topic: it takes a technique he already owns from 05-Hashing-Prefix-Sum and makes him apply it to a structure where he cannot index backwards. The subtlety is that repeated zero-sum spans require overwriting the map entry (or a second pass) so the last occurrence wins, and the dummy node is mandatory because the whole list can sum to zero. Nothing else in the folder makes him combine a hash map with pointer surgery. |
| medium | Design Linked List (LC 707) | Full list API from scratch — get(i), addAtHead, addAtTail, addAtIndex, deleteAtIndex — ideally doubly linked with a size counter and sentinel head/tail | He has solved problems on lists but never built one, and 'implement a doubly linked list with insert and delete at an index' is a standard senior warm-up, often as the first ten minutes before a harder question. Sentinel head and tail nodes remove every null check, and that is a design point an interviewer explicitly probes. It also underpins the LRU cache he already has in 19-Design-Data-Structures, so writing it closes the gap between using a DLL and being able to justify its internals. |
| low | Remove Nodes From Linked List (LC 2487) | Drop every node that has a strictly greater value somewhere to its right — reverse the list and keep a running max, or a monotonic stack over nodes | He has monotonic-stack work in 07-Stack-Queue-Monotonic and reversal here, but has never combined them, and the elegant solution (reverse, sweep keeping a running maximum, reverse back) is a genuine insight rather than a template. Low priority because it is newer and less universal than the others, but it has been showing up in Google and Amazon phone screens and it is the natural test of whether his reversal subroutine is truly reusable. |

