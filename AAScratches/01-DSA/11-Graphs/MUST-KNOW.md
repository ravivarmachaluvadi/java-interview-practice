# 11-Graphs — must-know order

**Techniques in this topic:** Traversal primitives: BFS queue + DFS recursion, visited arrays, adjacency list vs matrix, Grid-as-graph: 4/8-direction flood fill, component counting, boundary-DFS complement trick, Multi-source BFS for simultaneous spread and distance fill (rotten oranges / walls and gates shape), Cycle detection: parent-check in undirected graphs, recursion-stack (pathVis) in directed graphs, Topological sort both ways: DFS finish-stack and Kahn's indegree BFS, plus cycle detection as a by-product, Union-Find with path compression and union by size/rank, including encoding non-integer entities (emails, rows/columns) as DSU nodes, Weighted shortest paths: Dijkstra with PQ/TreeSet, Bellman-Ford for negative edges and negative-cycle detection, Floyd-Warshall for all-pairs, Minimum spanning trees: Prim (PQ) vs Kruskal (sort + DSU), Implicit/state-space graphs where nodes are words, lock combinations or modular values rather than given edges, Dijkstra with an extra state dimension (stops, parity, ways-count) instead of plain distance, Advanced connectivity: Kosaraju SCC, Tarjan bridges and articulation points, Specialist patterns: 0-1 BFS deque, Hierholzer Eulerian path, rerooting DP, reverse-time simulation, concurrent BFS crawling

| | |
|---|---|
| Problems | 51 |
| Must-know | 13 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_BFSandDFS.java` | BFS queue and DFS recursion |
| 2 | `A03_CycleCheckInDirectedGraphMain.java` | Directed cycle via recursion stack |
| 3 | `A05_KahnsTopoBFS.java` | Indegree BFS topological sort |
| 4 | `A06_DisjointSetsMain.java` | Union-Find, path compression, union by size |
| 5 | `A07_DjikstrasAlgoPQ.java` | Dijkstra with a priority queue |
| 6 | `B02_NumberOfIslands.java` | Count components in a grid |
| 7 | `B06_RottenOranges.java` | Multi-source BFS with levels |
| 8 | `B11_CourseSchedule.java` | Kahn's applied to prerequisites |
| 9 | `C01_AllNodesDistanceKInBinaryTree.java` | Parent map turns tree into graph |
| 10 | `C03_WordLadder.java` | Implicit graph BFS over words |
| 11 | `C10_AlienDictionaryOrder.java` | Derive edges, then topo sort |
| 12 | `C12_AccountsMerge.java` | DSU over string-to-index mapping |
| 13 | `C15_CheapestFlight.java` | Shortest path with a stop limit |

## Full practice order


### A — Building blocks

- `A01_BFSandDFS.java` — BFS queue and DFS recursion **[must-know]**
  - Every other file in the folder is a variation on these two loops; nothing else makes sense first.
- `A02_CheckForCycleInUnDirected.java` — Undirected cycle via parent tracking
  - First real use of traversal state: visited plus the parent you came from, in both BFS and DFS form.
- `A03_CycleCheckInDirectedGraphMain.java` — Directed cycle via recursion stack **[must-know]**
  - The vis + pathVis pair is the primitive behind course schedule, safe nodes and every DAG question.
- `A04_ToposortDFS.java` — DFS finish-time stack ordering
  - Directly extends the directed DFS above; gives the ordering intuition Kahn's then re-derives iteratively.
- `A05_KahnsTopoBFS.java` — Indegree BFS topological sort **[must-know]**
  - The version interviewers expect, and the one that doubles as cycle detection by comparing output size to V.
- `A06_DisjointSetsMain.java` — Union-Find, path compression, union by size **[must-know]**
  - Second core primitive of the folder; Kruskal and six later problems are thin wrappers over it.
- `A07_DjikstrasAlgoPQ.java` — Dijkstra with a priority queue **[must-know]**
  - Weighted shortest path baseline; assumes the BFS loop and swaps the queue for a min-heap.
- `A08_DijkstraUsingSet.java` — Dijkstra with TreeSet decrease-key
  - Same algorithm as the PQ version, kept only to show the stale-entry-free variant and its trade-off.
- `A09_BellmanFord.java` — V-1 edge relaxations, negative cycle
  - Learn it right after Dijkstra so the reason Dijkstra fails on negative edges is concrete.
- `A10_FloydWarshallAlgorithm.java` — All-pairs shortest path DP
  - The third shortest-path tool; completes the single-source vs all-pairs decision you will be asked to justify.
- `A11_PrimsAlgo.java` — MST by growing from a PQ
  - Reuses the Dijkstra heap loop with a weight-only key, so it belongs immediately after the shortest-path trio.
- `A12_KruskalAlgorithm.java` — Sort edges plus DSU union
  - Needs both DSU and the MST idea, so it sits after each of them; also the cleanest proof DSU is worth owning.
- `A13_KosarajusAlgorithm.java` — SCC via two passes and transpose
  - Builds directly on the DFS finish-stack from ToposortDFS; first of the advanced connectivity trio.
- `A14_BridgesInGraph.java` — Tarjan bridges with tin/low
  - Introduces discovery time and low-link, the machinery the articulation variant then reuses.
- `A15_ArticulationPointInGraph.java` — Cut vertices with tin/low
  - Same tin/low pass as bridges but with the root special case and >= instead of >; learn it second.

### B — Easy

- `B01_FloodFill.java` — Grid DFS with delta arrays
  - The smallest possible grid-as-graph problem and where the delRow/delCol idiom is introduced.
- `B02_NumberOfIslands.java` — Count components in a grid **[must-know]**
  - Flood fill plus an outer scan; the single most-asked graph warm-up and the template for every later grid problem.
- `B03_NumberOfProvines.java` — Connected components, adjacency matrix
  - Same counting loop as islands but on an explicit matrix, which teaches matrix-to-adjacency-list conversion.
- `B04_KeysAndRooms.java` — Reachability from a single source
  - Plain DFS on an adjacency list with a did-we-reach-everything check; no grid geometry to distract.
- `B05_NumberOfEnclaves.java` — Boundary DFS, count the complement
  - First grid problem with a twist: eliminate from the border, then count what is left.
- `B06_RottenOranges.java` — Multi-source BFS with levels **[must-know]**
  - Introduces seeding the queue with every source and counting levels as elapsed time - reused constantly.
- `B07_WallsAndGates.java` — Multi-source BFS distance fill
  - Identical machinery to rotten oranges, writing distances into the grid instead of counting minutes.
- `B08_ShortestPathInBinaryMatrix.java` — 8-direction BFS shortest path
  - Turns grid BFS into a shortest-path answer and forces you to mark visited at enqueue time.
- `B09_ShortestPathBinaryMatrix.java` — Grid BFS shortest path (duplicate)
  - Near-duplicate of the previous file; use it only as a self-test, noting its direction array covers just 4 moves.
- `B10_IsBipartite.java` — Two-colouring during traversal
  - One new idea on top of plain DFS - carry a colour and reject same-colour neighbours.
- `B11_CourseSchedule.java` — Kahn's applied to prerequisites **[must-know]**
  - The canonical first application of topological sort; easy once Kahn's is owned, and asked everywhere.

### C — Medium

- `C01_AllNodesDistanceKInBinaryTree.java` — Parent map turns tree into graph **[must-know]**
  - Teaches the reframing move - add back-edges so a tree can be BFS'd in all directions - which many follow-ups assume.
- `C02_ShortestBridgeSolution.java` — DFS to mark, then BFS to expand
  - First problem that chains two traversals; needs both the island DFS and the multi-source BFS from tier B.
- `C03_WordLadder.java` — Implicit graph BFS over words **[must-know]**
  - The gateway to state-space search: nodes are generated on the fly, not given as edges.
- `C04_OpenTheLock.java` — BFS over lock states
  - Same implicit-graph shape as Word Ladder with a deadend set, so it consolidates the pattern cheaply.
- `C05_MinimumMultiplications.java` — BFS over modular values
  - Third implicit-graph rep - numbers mod 100000 as nodes - which proves the pattern is about state, not strings.
- `C06_Celebrity.java` — Candidate elimination on knows()
  - A dense-graph question solved without traversal at all; useful contrast once BFS/DFS feels automatic.
- `C07_EventualSafeNodesByDFS.java` — Cycle detection reused as safety
  - Direct reuse of vis + pathVis, adding a check[] result array - the smallest step up from directed cycle detection.
- `C08_EventualSafeNodesByDFSV2.java` — Three-state DFS memoisation
  - Same problem re-expressed as 0/1/2 states; teaches folding visited, in-stack and answer into one array.
- `C09_ParallelCourses.java` — Topological sort in levels
  - Adds level-by-level processing to Kahn's, the standard answer to minimum-semesters style follow-ups.
- `C10_AlienDictionaryOrder.java` — Derive edges, then topo sort **[must-know]**
  - The hard part is building the graph from adjacent word pairs; the sort itself is already known by now.
- `C11_NumberOfOperationsToMakeNetworkConnected.java` — DSU components and spare edges
  - Easiest real DSU application: count components, compare extra edges to components-1.
- `C12_AccountsMerge.java` — DSU over string-to-index mapping **[must-know]**
  - Teaches mapping non-integer entities onto DSU indices, the step that makes union-find usable on real data.
- `C13_MostStonesRemovedWithSameRowOrColumn.java` — Rows and columns as DSU nodes
  - Extends the same mapping trick with an offset encoding; hardest of the medium DSU set, so it comes last.
- `C14_ShortestPath.java` — Dijkstra with parent reconstruction
  - First weighted problem that must return the path, not just the cost - the parent-array habit you need later.
- `C15_CheapestFlight.java` — Shortest path with a stop limit **[must-know]**
  - The classic trap: an extra constraint dimension breaks plain Dijkstra, so you order by stops instead of cost.
- `C16_FindTheCity.java` — Floyd-Warshall then threshold count
  - Pure application of all-pairs distance; its value is recognising when n is small enough to justify O(n^3).

### D — Hard

- `D01_MakingALargeIsland.java` — DSU on grid plus best flip
  - First hard one because it only combines things already owned: grid DSU, component sizes, dedupe of neighbours.
- `D02_FindAllPeopleWithSecret.java` — Time-grouped connectivity spread
  - Needs meetings processed in time order with per-timestamp grouping - DSU knowledge alone is not enough.
- `D03_NumberOfWaysToArriveAtDestination.java` — Dijkstra carrying a path count
  - Opens the Dijkstra-variant block: maintain ways[] alongside dist[] and know when to reset versus add.
- `D04_MinimumTimeToVisitCell.java` — Dijkstra with parity waiting
  - Same heap loop, but the insight that you can bounce between two cells to burn time is genuinely non-obvious.
- `D05_GridTeleportationTraversal.java` — 0-1 BFS with a deque
  - Introduces the deque front/back trick for zero-cost edges (portals), a distinct tool from the PQ.
- `D06_LastDayToCrossPQ.java` — Max-min path over flood times
  - Closes the weighted block: a best-bottleneck search where the heap key is a day, not a distance.
- `D07_ReconstructItinerary.java` — Hierholzer Eulerian path
  - A standalone algorithm nothing else here teaches - post-order append plus a min-heap for lexicographic order.
- `D08_MinimumEdgeReversals.java` — Rerooting DP on a tree
  - Two-pass rerooting is the most advanced idea in the folder and assumes comfort with DFS accumulation.
- `D09_HtmlParserMain.java` — Concurrent BFS web crawler
  - Last because it is judged on thread pool, shared visited set and termination - a design answer wearing a BFS costume.

## Interview readiness

This is the strongest-looking folder in the set and is close to interview-complete on algorithms: every core engine is present and usually in two forms (BFS/DFS, both topological sorts, Dijkstra with PQ and TreeSet, Bellman-Ford, Floyd-Warshall, Prim and Kruskal, DSU with compression, Kosaraju, bridges, articulation points), plus genuinely senior extras most candidates skip — Hierholzer, rerooting, 0-1 BFS, reverse-time simulation. The remaining gaps are not algorithm gaps, they are modeling gaps, and they cluster in five specific holes: (1) no problem where the graph is an object graph rather than an index array, so deep-copy/reference semantics have never been exercised; (2) no bottleneck/minimax path — every shortest-path file minimizes a sum, never a maximum edge, so the max-relaxation Dijkstra / binary-search+BFS / incremental-Kruskal family is entirely absent; (3) DSU is unweighted only — no weight-on-parent-pointer variant, and no directed DSU edge case; (4) no memoized DFS over an implicit DAG, so graph-flavoured DP has no representative here; (5) no state-as-bitmask and no all-shortest-paths enumeration, meaning every search either tracks a scalar distance or one parent pointer. In a 45-minute loop he would clear any "which algorithm is this" question comfortably, but he could get stalled by an easy-looking Clone Graph phone screen or by an interviewer who asks him to minimize the worst step instead of the total — and being stalled on an easy problem reads much worse at staff level than being slow on a hard one. Two to three sessions closes this.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Clone Graph (LC 133) | DFS or BFS over an adjacency-object graph with a HashMap<Node,Node> old->new memo, cloning before recursing to survive cycles | Every one of his 50 files uses int-indexed adjacency lists or grids, so he has never traversed a graph made of object references. This is the single most common graph phone screen at product companies and looks trivially easy, which is exactly why failing it is fatal - it tests deep vs shallow copy, cycle-safety via the map (you must insert the clone BEFORE recursing on neighbours), and the null/single-node edges. Follow-ups are Copy List with Random Pointer and serialize/deserialize, both of which reuse the same map trick. |
| high | Path with Minimum Effort (LC 1631), and its twin Swim in Rising Water (LC 778) | Bottleneck / minimax path: Dijkstra where the relaxation is max(dist[u], w) instead of dist[u]+w; alternates are binary search on the answer + BFS feasibility, or sort edges and union until source and sink connect | All nine of his weighted-path files minimize a sum of edges. Nothing in the folder minimizes the maximum edge on a path, which is a different relaxation and a different proof of correctness. This family is asked constantly (it also covers Trapping Rain Water II and Minimum Cost to Reach City With Discounts) and the giveaway of an unprepared candidate is writing plain Dijkstra and getting a wrong answer on a grid with one huge step. The three-way alternative (heap vs binary search vs Kruskal-style union) is also a strong staff-level discussion. |
| high | Evaluate Division (LC 399) | Build a bidirectional weighted graph a->b = k, b->a = 1/k, then DFS/BFS accumulating the product; the stronger answer is weighted union-find storing the ratio to parent and compressing it | His DSU set is large (AccountsMerge, MostStonesRemoved, MakingALargeIsland, NetworkConnected) but every one of them is unweighted - find() only answers 'same component?'. He has never carried a value along the parent pointer, and weighted DSU path compression is a genuinely tricky piece of code to derive live. The problem is also the canonical 'model this messy input as a graph' question: the interviewer's real test is whether he sees string equations as weighted edges at all. Asked heavily at Google, Meta and Amazon. |
| high | Longest Increasing Path in a Matrix (LC 329) | DFS with memoization over an implicit DAG (edges only to strictly-larger neighbours guarantee acyclicity); the alternative framing is Kahn's peeling by outdegree | There is no memoized graph traversal in the folder at all except EventualSafeNodesByDFSV2, and none that returns a computed value rather than a boolean. This is the bridge between his Graphs folder and his DP folder, and neither side currently has it - I checked 12-Dynamic-Programming too. The key insight he has not had to state out loud is why memoization is even legal here (the strict-increase constraint makes the graph a DAG, so no cycle can poison a cached value), and interviewers explicitly probe that. Very common at Google and Amazon. |
| medium | Word Ladder II (LC 126) | Level-by-level BFS building a parent map for every node at its minimum depth, then DFS backtracking over that map to enumerate all shortest paths | He has Word Ladder I and he has single-parent reconstruction in ShortestPath.java, but reconstructing ALL optimal paths is a different data structure (list of parents, not one parent) and a different visited rule - you must not mark a node visited until the whole level is done, or you lose valid paths. This is the standard hard follow-up to a problem he already solved, so it is the single likeliest place an interviewer will push him beyond what he has practiced. Same shape covers All Paths From Source to Target and Cheapest Path enumeration. |
| medium | Redundant Connection (LC 684) and Redundant Connection II (LC 685) | DSU where the first union that fails identifies the cycle edge; the directed version requires first finding a node with two parents and testing each candidate edge for removal | 684 is the most canonical single DSU application there is and is absent despite five other DSU files - it is the one that makes the 'union returns false means cycle' idiom explicit. 685 is the real value: it forces case analysis (node with two parents but no cycle, cycle but no two-parent node, both at once) that none of his current union-find problems require. Cheap to add given he already has the DSU template, and it directly hardens the cycle-detection story he will be asked to compare against his DFS parent-check version. |
| medium | Minimum Height Trees (LC 310) | Topological peeling on an UNDIRECTED graph: repeatedly strip all degree-1 leaves layer by layer until 1 or 2 centroid nodes remain | His Kahn's implementation peels by indegree on a directed graph; peeling by degree-1 on an undirected tree is a distinct idiom and the folder has no tree-centroid or tree-diameter problem at all. The trap is that the brute force (BFS from every node, O(n^2)) is obvious and the O(n) insight is not, and the answer is provably at most 2 nodes - interviewers ask him to justify that. Also the gateway to tree diameter and 'sum of distances in tree', which pairs with the rerooting DP he already has. |
| medium | Shortest Path Visiting All Nodes (LC 847) | BFS over a compound state (node, bitmask of visited set) with a visited[n][1<<n] table and all nodes seeded into the queue as simultaneous sources | Grepping the folder found zero uses of bitmask state. He has extra-dimension search (stops in CheapestFlight, parity/ways in NumberOfWaysToArriveAtDestination), so the concept of a widened state is there, but never a subset as part of the state, and never multi-source seeding combined with it. This is the standard senior-tier probe for 'can you tell me what a node actually is in your search space', and revisiting nodes being legal here breaks the plain-visited habit every other file in the folder reinforces. Lower frequency than the items above, which is why it is medium and last. |

