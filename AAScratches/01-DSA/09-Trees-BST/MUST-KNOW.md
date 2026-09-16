# 09-Trees-BST — must-know order

**Techniques in this topic:** Recursive DFS contract: base case, recurse on children, combine — every tree problem is a choice of what to return and what to combine, The 'return one value, track another globally' pattern — height returned upward while a field records the best answer seen (diameter, max path sum, max subtree sum), BFS with an explicit level-size loop — unlocks level order, zigzag, right/bottom view, max level sum, cousins, Carrying state DOWN the path as parameters (max-so-far, min/max bounds, depth) versus bubbling state UP as return values, The BST invariant as a search directive: one comparison prunes half the tree — floor/ceil, range sum, LCA, insert, delete, In-order traversal of a BST yields sorted order; reverse in-order yields descending — the engine behind kth largest, greater-sum tree, min difference, BST-to-DLL, Parent-pointer / parent-map reconstruction to turn a tree into an undirected graph — distance-K, LCA with parents, smallest common region, The LCA family as one recursion with four variants: plain, existence-checked, node-set, and parent-pointer, Tree construction and reconstruction: from sorted array, sorted list, preorder+inorder, preorder-with-bounds, string, and serialized form, Structural rewiring in place — flatten to list, BST to doubly linked list, insert a row, delete into a forest, Backtracking on trees: add to path, recurse, remove — the only way to emit all root-to-leaf paths, Two-tree parallel recursion — same tree, symmetric, subtree-of-another

| | |
|---|---|
| Problems | 56 |
| Must-know | 15 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_RecursivePostorder.java` | DFS traversal orders |
| 2 | `A02_HeightOfBinaryTree.java` | Recursive height, 1 + max(children) |
| 3 | `A04_LevelOrderTraversal.java` | BFS with level-size loop |
| 4 | `A06_BinarySearchTreeOperations.java` | BST insert, search, in-order |
| 5 | `B09_DiameterOfBinaryTree.java` | Return height, track global best |
| 6 | `B13_RightView.java` | Last node per level |
| 7 | `B16_LowestCommonAncestorBST.java` | Split point via value compare |
| 8 | `C01_ValidateBST.java` | Min/max bounds propagation |
| 9 | `C02_NthLargestInBST.java` | Reverse in-order with counter |
| 10 | `C11_PathSumII.java` | Backtracking to emit all paths |
| 11 | `C12_LCA.java` | Postorder null-propagation LCA |
| 12 | `C17_BinaryTree.java` | Parent map plus BFS from target |
| 13 | `C21_ConstructBinaryTreeFromINPre.java` | Preorder + inorder with index map |
| 14 | `D01_BinaryTreeMaxPathSum.java` | Return best arm, track best bend |
| 15 | `D03_ImportantSerializeAndDeserialiseBinaryTree.java` | Serialize/deserialize with null markers |

## Full practice order


### A — Building blocks

- `A01_RecursivePostorder.java` — DFS traversal orders **[must-know]**
  - The three visit orders are the alphabet of this entire folder; postorder (children before parent) is the one every aggregation problem uses.
- `A02_HeightOfBinaryTree.java` — Recursive height, 1 + max(children) **[must-know]**
  - The first problem where the recursion returns a computed value rather than printing; it is the literal subroutine inside diameter, balance, and complete-node counting.
- `A03_MaximumDepthOfBinaryTree.java` — Max depth via recursion
  - The same primitive as HeightOfBinaryTree restated — use it as a 60-second re-derivation check, not as new material.
- `A04_LevelOrderTraversal.java` — BFS with level-size loop **[must-know]**
  - The queue plus `int levelSize = queue.size()` idiom is the second engine in the folder; every view, zigzag and per-level problem is this loop with one line changed.
- `A05_BranchSums.java` — Root-to-leaf accumulation
  - Introduces passing a running total down the recursion and only acting at a leaf — the shape HasPathSum and PathSumII both inherit.
- `A06_BinarySearchTreeOperations.java` — BST insert, search, in-order **[must-know]**
  - Establishes the two BST facts the whole BST half of the folder depends on: one comparison discards half the tree, and in-order emits sorted order.
- `A07_PrePostInorderInOneTraversal.java` — Iterative stack with node state
  - Last in the foundations because it assumes you already know all three orders; it shows the explicit stack that replaces recursion when depth is a risk.

### B — Easy

- `B01_InvertTree.java` — Swap children recursively
  - Simplest problem that mutates structure rather than reading it; a two-line warm-up before the harder rewiring problems.
- `B02_SameTreeExample.java` — Parallel two-tree recursion
  - Introduces walking two trees in lockstep with matched null handling — the helper that SymmetricTree and IsSubtree both call.
- `B03_SymmetricTree.java` — Mirrored parallel recursion
  - SameTree with the child pairing crossed (left-vs-right); teaches that the pairing, not the traversal, is the variable.
- `B04_IsSubtree.java` — SameTree run at every node
  - Composes the previous two: an outer traversal wrapping an inner comparison, and the first O(n*m) cost discussion.
- `B05_HasPathSum.java` — Root-to-leaf target sum
  - BranchSums with early exit; the leaf-detection condition here is the one people get wrong under pressure, so nail it before PathSumII.
- `B06_SumOfLeftLeaves.java` — Leaf test from the parent
  - Forces the realization that 'left leaf' is a property only the parent can see — your first taste of context that cannot live in the child call.
- `B07_ExpressionTreeEvaluator.java` — Postorder evaluation
  - Concrete payoff for postorder: operands at leaves, operators combining children — the mental model for all bottom-up aggregation.
- `B08_MaximumSubtreeSum.java` — Postorder sum with global max
  - The gentlest instance of 'return the subtree value, record the best seen' — deliberately placed one step before Diameter, which uses the identical skeleton.
- `B09_DiameterOfBinaryTree.java` — Return height, track global best **[must-know]**
  - The single most leveraged pattern in tree interviews: what you return up is not what you are answering. Owning this makes LC124 a variation, not a new problem.
- `B10_IsBalancedBinaryTree.java` — Height with -1 sentinel
  - Same height recursion, now carrying a failure signal upward to short-circuit — the encoding trick that avoids a second traversal.
- `B11_Cousins.java` — Depth plus parent identity
  - First problem needing two facts per node at once; solvable by BFS or DFS, so use it to compare the two engines side by side.
- `B12_MaxLevelSum.java` — Per-level BFS aggregate
  - A direct application of the level-size loop with an accumulator — confirms the BFS template is automatic before the view problems.
- `B13_RightView.java` — Last node per level **[must-know]**
  - Asked constantly, and this file gives both solutions — BFS last-of-level and DFS right-first-record-once-per-depth — which is exactly the follow-up interviewers push for.
- `B14_RangeSumBST.java` — BST-guided pruning
  - First problem where the BST property is used to skip subtrees rather than to find a node; the pruning conditions are the whole lesson.
- `B15_FloorCeilOfBST.java` — Descent tracking best candidate
  - Teaches remembering a candidate while continuing to descend — the pattern behind predecessor/successor and insert-position questions.
- `B16_LowestCommonAncestorBST.java` — Split point via value compare **[must-know]**
  - The whole answer is 'walk down until the two values straddle the node'; must be instant, and it is the contrast case that makes the general LC236 recursion memorable.
- `B17_GetMinimumDifference.java` — In-order with previous pointer
  - Introduces the stateful `prev` carried across in-order visits — the exact mechanism reused by the greater-sum tree and BST-to-DLL conversions.
- `B18_SortedArrayToBST.java` — Mid as root, divide and conquer
  - The inverse of in-order: picking the midpoint guarantees balance, which is the prerequisite idea for the sorted-list version in tier C.
- `B19_MinimumHeightBST.java` — Balanced BST from sorted array
  - Same construction as the previous file under a different name; a quick repetition rep, not new ground.

### C — Medium

- `C01_ValidateBST.java` — Min/max bounds propagation **[must-know]**
  - Opens tier C because it corrects the near-universal wrong answer (compare only with children) and installs range-passing, which BST-from-preorder reuses verbatim.
- `C02_NthLargestInBST.java` — Reverse in-order with counter **[must-know]**
  - The kth-smallest/largest question shows up in nearly every BST round; the counter-plus-early-stop skeleton generalizes to any order statistic.
- `C03_BinarySearchTreeToGreaterSumTree.java` — Reverse in-order running sum
  - Reverse in-order again, now mutating values with a carried accumulator — proves the traversal order is the algorithm.
- `C04_BSTtoDLLInPlace.java` — In-order rewiring with prev
  - Combines the `prev` pointer from GetMinimumDifference with pointer surgery; a classic where candidates lose the head node.
- `C05_DeleteNodeInBST.java` — Delete via in-order successor
  - The three-case deletion (no child, one child, two children) is the first real BST invariant-maintenance problem and the hardest BST code to write cleanly.
- `C06_DeleteANodeInBST.java` — Iterative delete with parent relink
  - Same deletion with explicit parent tracking instead of recursion — read it straight after the recursive version to see the O(1)-space trade.
- `C07_ConstructBinarySearchTreefromPreorderTraversal.java` — Build with bounds and index pointer
  - Directly inverts ValidateBST: the same (lower, upper) window now constructs instead of checks, plus a shared mutable index.
- `C08_ConvertSortedListToBST.java` — Slow/fast midpoint, then recurse
  - The array construction from tier B with random access removed; the half-open [start, end) boundary is the subtle part worth drilling.
- `C09_CountGoodNodes.java` — Carry max-so-far downward
  - The cleanest example of state flowing down as a parameter rather than up as a return — the counterpart to the Diameter pattern.
- `C10_MaximumDifferenceBetweenNodeAndAncestor.java` — Carry path min and max down
  - CountGoodNodes with two carried values; shows that widening the downward state costs nothing and answers a harder question.
- `C11_PathSumII.java` — Backtracking to emit all paths **[must-know]**
  - Add-recurse-remove is the technique that unlocks every 'return all paths/combinations' follow-up; forgetting the remove is the classic bug to internalize.
- `C12_LCA.java` — Postorder null-propagation LCA **[must-know]**
  - LC236 is a permanent interview fixture, and this four-line recursion is the base case for the four LCA variants that follow it here.
- `C13_LowestCommonAncestorOfABinaryTreeII.java` — LCA with existence flags
  - First variant: the base recursion is unsafe when a node may be absent, so you add found-flags — teaches why LC236's assumption matters.
- `C14_LowestCommonAncestorIV.java` — LCA of a node set
  - Generalizes the same recursion from two targets to a set; confirms you understood the recursion rather than memorized it.
- `C15_LowestCommonAncestorOfaBinaryTreeIII.java` — Parent pointers, two-runner meet
  - Drops the root entirely and becomes linked-list intersection — the pivot from tree thinking to graph thinking that the next two problems need.
- `C16_SmallestCommonRegion.java` — LCA over a child-to-parent map
  - The LCA idea applied to an arbitrary hierarchy with no tree object at all; the build-a-parent-map step feeds directly into the next problem.
- `C17_BinaryTree.java` — Parent map plus BFS from target **[must-know]**
  - All-nodes-distance-K is the canonical 'a tree is a graph if you add parent edges' problem and shows up constantly at senior and staff level.
- `C18_BinaryTreeZigzagTraversal.java` — Level order with alternating direction
  - The BFS template plus one toggle; worth doing for the reverse-at-the-end versus insert-at-front discussion, not for the difficulty.
- `C19_BottomViewBinaryTree.java` — Horizontal distance map with BFS
  - Introduces the horizontal-distance coordinate, which converts top/bottom/vertical view questions into a map keyed by column.
- `C20_ReverseOddLevelsOfBinaryTree.java` — Paired symmetric DFS swap
  - Reuses SymmetricTree's mirrored pairing to mutate instead of compare; the non-obvious bit is that DFS can do a level operation without BFS.
- `C21_ConstructBinaryTreeFromINPre.java` — Preorder + inorder with index map **[must-know]**
  - The reconstruction question that appears everywhere, and the value-to-index map that turns it from O(n^2) to O(n) is the exact optimization interviewers probe for.
- `C22_BinaryTreeFromString.java` — Recursive descent parsing
  - Construction from a textual grammar rather than traversal arrays; the shared mutable index here is the same device as in the deserializer ahead.
- `C23_BinaryTreeToLinkedList.java` — In-place flatten to preorder
  - Pointer surgery where the recursion must return a tail so the caller can splice — a step up in rewiring difficulty from BST-to-DLL.
- `C24_FindLeavesOfBinaryTree.java` — Height as a bucket index
  - The insight that a node's height IS its removal round turns an O(n^2) peel-the-leaves simulation into one pass; a memorable reframing exercise.
- `C25_DeleteNodesAndReturnForest.java` — Postorder delete collecting roots
  - Combines deletion with a set lookup and the subtlety of when a child becomes a new root; requires deleting bottom-up to be correct.
- `C26_AddRowToTree.java` — Depth-targeted structural insert
  - Closes tier C: depth tracking plus pointer rewiring plus the d==1 new-root edge case that most candidates miss.

### D — Hard

- `D01_BinaryTreeMaxPathSum.java` — Return best arm, track best bend **[must-know]**
  - The hard-tier payoff for the Diameter pattern, with two extra insights — clamp negative contributions at zero, and the path you report is not the value you return.
- `D02_MaximumSumPathInBinaryTree.java` — Max path sum, restated
  - Identical LC124 solution under a second filename; use it as a cold re-solve to verify the previous file actually stuck.
- `D03_ImportantSerializeAndDeserialiseBinaryTree.java` — Serialize/deserialize with null markers **[must-know]**
  - Placed after construction because it is construction without the luxury of two traversals — you must design the format so null placeholders make it unambiguous. A staple design-flavoured hard question.
- `D04_CountCompleteTreeNodes.java` — Perfect-subtree shortcut, O(log^2 n)
  - Last because it is the purest 'beat the obvious O(n)' insight in the folder: compare leftmost and rightmost depths, and half the recursion collapses to 2^h - 1.

## Interview readiness

This is one of the more complete tree folders I've seen — 56 files covering essentially every canonical family: both traversal orders (recursive and single-pass iterative), the full BFS level-size toolkit (level order, zigzag, right/left/bottom view, max level sum, cousins, reverse odd levels), the "return height, record answer globally" trio (diameter, max path sum, max subtree sum), the whole LCA family including parent-pointer and node-set variants, all five reconstruction sources, in-place rewiring (flatten, BST-to-DLL, add row, delete-to-forest), and serialize/deserialize. All Nodes Distance K is present too — it's hiding inside BinaryTree.java, not missing. He would pass a standard 45-minute tree round today. The real exposure is not the opening question, it's the follow-up: every gap I found sits in a variant an interviewer reaches for *after* the obvious solution lands. He can BFS any level pattern but has never done one in O(1) space (next pointers) or with positional indices (max width); he can walk every root-to-leaf path but has never counted arbitrary downward paths with a prefix-sum map; every bottom-up recursion in the folder returns a single scalar, so a two-state DP return (House Robber III) or a designed struct return (largest BST subtree) is untested; and he has the inorder-with-prev machinery in four places but never the repair case (Recover BST) or the paused/iterator framing. Close those and the folder is staff-ready rather than senior-ready.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Path Sum III (count all downward paths summing to target, LC 437) | Running root-path prefix sum in a HashMap<sum, count>, incremented on the way down and decremented on the way back up (backtracking on the map itself) | He has HasPathSum and PathSumII, but both are root-to-leaf. This is the only tree problem that imports the prefix-sum-hashmap trick from arrays, and the O(n^2) 'start DFS from every node' answer is the expected first attempt — the interviewer's whole point is getting him to O(n). Extremely common at Amazon/Microsoft, and the un-decrement bug is the classic failure. |
| high | Populating Next Right Pointers in Each Node II (LC 117) | Level linking in O(1) extra space: walk the current level using the next pointers already built, stitching the child level with a dummy head and a tail pointer | Every one of his ~8 BFS problems uses a queue. The near-universal follow-up to any level-order question is 'now do it without the queue', and this is the problem that teaches it. The version that matters is II (non-perfect tree) — the perfect-tree version I hides the hard part. |
| high | Binary Search Tree Iterator (LC 173) | Controlled/paused in-order traversal as an object: a stack holding the left spine, next() popping and pushing the right child's left spine, amortized O(1) time in O(h) space | PrePostInorderInOneTraversal gives him the stack mechanics but not the design framing — stopping a traversal mid-flight and resuming it on demand. Product companies ask this constantly because it extends naturally ('add prev()', 'now merge two BSTs using two iterators', 'why is next() amortized O(1)?'). Without practice he will reach for 'flatten to a list in the constructor', which fails the O(h) space constraint. |
| medium | Maximum Width of Binary Tree (LC 662) | BFS carrying a positional index per node (left = 2i, right = 2i+1), width = last index minus first index on each level, with per-level re-normalisation to avoid integer overflow | Nothing in the folder assigns positions to nodes — bottom view uses horizontal distance, which is a different axis and never overflows. The overflow guard is the exact thing staff-level interviewers probe for, and 'count the nulls in between' is the wrong instinct this problem is designed to catch. |
| medium | House Robber III (LC 337) | Tree DP returning a pair of states per node — best if this node is taken, best if it is skipped — combined at the parent | Every bottom-up recursion he has written returns one number (height, sum, boolean). This is the canonical problem where the return contract has to be a pair, and it is the gateway to the whole family (Binary Tree Cameras, Distribute Coins, tree colouring). His own stated theme is 'choosing what to return and what to combine' — this is the case where the choice is non-obvious. |
| medium | Recover Binary Search Tree (LC 99) | In-order traversal tracking a prev pointer, capturing first and second violation candidates, then swapping values — with the adjacent-swap special case where only one violation appears | He has the in-order-with-prev engine in four places (ValidateBST, GetMinimumDifference, GreaterSumTree, NthLargest) but never the repair variant. The adjacent-swap edge case is where almost everyone fails, and the O(1)-space Morris follow-up makes it a natural senior question. Low cost to add given the machinery is already familiar. |
| medium | Vertical Order Traversal of a Binary Tree (LC 987 / 314) | Group nodes by column, ordered by row, with same-cell ties broken by node value — a TreeMap of columns plus a sort or priority queue inside each cell | BottomViewBinaryTree already gives him the horizontal-distance map, so this is partially covered — but bottom view keeps one value per column, while the asked version emits every value in a defined order. The (col, row, value) tie-break is the actual difficulty and is what separates a passing answer from a wrong one; a plain DFS here silently produces the wrong ordering. |
| medium | Largest BST Subtree (LC 333) | One post-order pass returning a composite per subtree — isBST, min, max, size — and combining children into the parent's verdict | This is the test of whether he can design a return type rather than reuse a known one. He has IsBalanced (bool + height) and MaximumSubtreeSum (sum), but never a four-field contract, and the trap is the O(n^2) 'validate every subtree independently' answer. Good discriminator at staff level because the naive solution looks correct and passes small tests. |

