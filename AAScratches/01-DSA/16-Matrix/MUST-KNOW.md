# 16-Matrix — must-know order

**Techniques in this topic:** In-place matrix transforms: transpose + row/column reversal as the basis for all rotations (90/180/270), Boundary-pointer traversal: shrinking top/bottom/left/right windows to walk a matrix in a non-row-major order, Diagonal indexing: the r-c and r+c invariants, and toggling direction at the four boundary cases, Two-pass marking: record state on the first sweep, apply it on the second, with the O(1)-space follow-up using row 0 / column 0 as the marker band, Monotone staircase search: starting at a corner where one move decreases and the other increases, giving O(m+n) on a sorted matrix, Grid simulation with direction vectors: a {{0,1},{1,0},{0,-1},{-1,0}} table plus (dir+1)%4 / (dir+3)%4 for turns, Coordinate hashing for O(1) obstacle/visited lookup on an unbounded or sparse grid, Separability of Manhattan distance into independent x and y problems, and the median as the minimiser of sum of absolute deviations

| | |
|---|---|
| Problems | 10 |
| Must-know | 3 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_MatrixRotate90Degree.java` | Transpose then reverse rows |
| 2 | `C01_SetMatrixZeroes.java` | Two-pass marking of rows/cols |
| 3 | `C02_SpiralTraversalOfMatrix.java` | Four shrinking boundary pointers |

## Full practice order


### A — Building blocks

- `A01_MatrixRotate90Degree.java` — Transpose then reverse rows **[must-know]**
  - The single primitive the whole folder leans on: once transpose and row/column reversal are muscle memory, 90/180/270 rotation and most in-place reshaping questions collapse into two lines.
- `A02_ToeplitzMatrix.java` — Diagonal invariant r-c constant
  - Teaches that cells on a diagonal share r-c by comparing (i,j) with (i+1,j+1); trivial on its own but it is the indexing fact DiagonalTraverse assumes you already own.

### B — Easy

- `B01_LargestInEachThreeRows.java` — Column-wise scan over row blocks
  - Pure nested-loop warm-up with no insight required — the right first file to confirm row/column index discipline before anything with pointers or direction state.
- `B02_CountNegativeNumbersInASortedMatrix.java` — Staircase walk from a corner
  - Easy as a problem but it introduces the O(m+n) corner-start walk that unlocks Search a 2D Matrix II and kth-smallest-in-sorted-matrix — learn it here where the bookkeeping is cheap.

### C — Medium

- `C01_SetMatrixZeroes.java` — Two-pass marking of rows/cols **[must-know]**
  - The gentlest medium and a constant interview appearance; it establishes record-then-apply, and the expected staff-level follow-up (drop the sets, use row 0 and column 0 as markers with a separate first-column flag) is where the real signal is.
- `C02_SpiralTraversalOfMatrix.java` — Four shrinking boundary pointers **[must-know]**
  - The canonical matrix question and the source of the top/bottom/left/right idiom, including the two guard checks that stop a single leftover row or column being emitted twice.
- `C03_DiagonalTraverse.java` — Direction toggle with boundary cases
  - Sits after Spiral because it needs the same boundary reflex plus the diagonal invariant from Toeplitz, and adds the classic trap: at a corner the right-edge check must be tested before the top-row check.
- `C04_WalkingRobotSimulation.java` — Direction vectors plus obstacle set
  - Shifts from traversing a given matrix to simulating on an implicit grid; introduces the direction table with modular turns and hashed coordinates, which is the shape of most grid-simulation follow-ups.
- `C05_ImportantWalkingRobotSimulation.java` — Annotated robot simulation replay
  - Same algorithm as the previous file with the direction-vector and turn arithmetic spelled out, so it belongs immediately after it as the explanation pass, not as new material.

### D — Hard

- `D01_BestMeetingPoint.java` — Median minimises Manhattan sum
  - Needs two non-obvious leaps — that Manhattan distance separates into independent x and y problems, and that the median (not the mean or centroid) minimises the sum of absolute deviations — so it lands last, after all the mechanical traversal work is automatic.

## Interview readiness

This folder is stronger than its size suggests, because a lot of what people file under "Matrix" already lives elsewhere in his tree and is genuinely covered — grid BFS/DFS (Number of Islands, Rotting Oranges, Flood Fill, Walls and Gates, Shortest Path in Binary Matrix, Making a Large Island) sits in 11-Graphs, grid DP (Unique Paths, Minimum Falling Path Sum, Count Square Submatrices, Triangle, Cherry Pickup) sits in 12-DP, Search a 2D Matrix sits in 03-Binary-Search, and Word Search sits in 14-Backtracking. Within the folder itself the traversal and transform half is essentially complete: he has in-place rotation via transpose+reverse, boundary-pointer spiral, the r-c/r+c diagonal invariants, two-pass marking with the O(1)-space marker-band follow-up, the monotone staircase on a sorted matrix, and direction-vector simulation with coordinate hashing. What is missing is one whole axis and a few canonical set pieces. The axis is 2D prefix sums / integral images — he has 1D prefix sum and subarray-sum-equals-K in 05-Hashing-Prefix-Sum but nothing 2D anywhere in the tree, so inclusion-exclusion over a submatrix, and the column-collapse trick that reduces a 2D submatrix-sum question to the 1D one he already knows, are both untested. The set pieces are Game of Life (encoding old and new state in the same cell), Valid Sudoku (the row/col/box triple index), and Kth Smallest in a Sorted Matrix (fusing his staircase counter with binary search on value). For a senior/staff loop I would call this about 70% ready: he would not be embarrassed on a traversal or rotation question, but a 2D-prefix-sum question or the in-place state-encoding follow-up on Game of Life would expose a real hole rather than a slow start.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Range Sum Query 2D - Immutable (LC 304) | 2D prefix sum / integral image: pre[i][j] = sum of the rectangle from (0,0) to (i-1,j-1), then any submatrix sum by inclusion-exclusion in O(1). | This is the single biggest structural hole. He has 1D prefix sums in 05-Hashing-Prefix-Sum but there is no 2D prefix sum anywhere in the whole DSA tree. It is the base primitive for Matrix Block Sum, Count Submatrices with All Ones, Max Sum Rectangle, and most 'sum over a region' follow-ups, and an interviewer probing depth on matrices will reach for it early. Without it he has no O(1) region-query tool at all. |
| high | Game of Life (LC 289) | In-place simultaneous update by encoding both the old and the new state in the same cell (2-bit trick: value 1 means was-live, bit 1 means will-be-live), then a second pass to shift. Follow-up: infinite board handled with a hash set of live coordinates. | He has two-pass marking from Set Matrix Zeroes, but that records state in a separate marker band. Game of Life is the problem where the old value must survive in the same cell while neighbours are still reading it, which is a different and less obvious idea. It is a standard Meta/Google/Amazon question, and the infinite-board follow-up is exactly the coordinate-hashing skill he already built in Walking Robot Simulation, so the payoff is high relative to effort. |
| high | Valid Sudoku (LC 36) | Single pass with three index families: row r, column c, and box (r/3)*3 + c/3, each tracked with a boolean array or bitmask. | Nothing in the folder teaches the cell-to-box index mapping, which is the one piece candidates fumble under pressure. It is a very common screen and early-onsite warm-up, and the bitmask formulation is a cheap way to show polish. It also unlocks Sudoku Solver as the natural follow-up. |
| high | Kth Smallest Element in a Sorted Matrix (LC 378) | Two accepted solutions: min-heap over row heads doing a k-way merge in O(k log m), and binary search on the value range using a staircase walk to count elements <= mid in O(m+n) per probe. | He owns both ingredients separately - the staircase from Count Negative Numbers, and heaps from 08-Heap-Priority-Queue - but has never fused them, and no file in the tree does. The binary-search-on-answer-plus-staircase-count pattern is exactly the kind of thing a staff-level interviewer uses to separate people who memorised solutions from people who compose primitives. Frequently asked at Amazon and Google. |
| medium | Number of Submatrices That Sum to Target (LC 1074) | Fix a pair of column boundaries, collapse each row to a single running sum, then reuse the 1D prefix-sum-plus-hashmap subarray-sum-equals-K solution. O(n^2 * m). | This is the canonical 'lift a 1D technique into 2D' matrix question, and he already has ImportantCountSubarraySumEqualsK in 05-Hashing-Prefix-Sum, so the only missing step is the column-collapse framing. It is a common senior-level follow-up after a 2D prefix sum question, and the same collapse pattern also solves Max Sum Rectangle and Maximum Sum of a Rectangle No Larger Than K. |
| medium | Maximal Rectangle (LC 85) | Build a per-row histogram of consecutive ones ending at that row, then run largest-rectangle-in-histogram on each row with a monotonic stack. O(m*n). | He already has ImportantLargestRectangleArea in 07-Stack-Queue-Monotonic and the square DP via CountSquareSubmatricesWithAllOnes, so the rectangle version is the one uncovered rung. The row-histogram reduction is non-obvious cold, and this is the standard hard follow-up when an interviewer wants to push past Maximal Square. Worth a rep precisely because the 1D primitive is already in place. |
| medium | Design Tic-Tac-Toe (LC 348) | O(1) move by keeping per-row, per-column, and two diagonal counters incremented by +1/-1 per player, rather than storing or rescanning the board. | Absent from both 16-Matrix and 19-Design-Data-Structures. It is the classic matrix-flavoured design question at Meta and Amazon, and the whole point - realising you never need the board at all - is a design instinct that does not appear anywhere else in his set. Cheap to learn, and it generalises to Tic-Tac-Toe on an n x n board and to Find Winner on a Tic Tac Toe Game. |
| medium | Robot Room Cleaner (LC 489) | Backtracking on an unknown grid using relative coordinates, a visited set of (x,y), the direction table with (dir+1)%4 turns, and an explicit go-back routine (turn twice, move, turn twice) to restore position and heading after each branch. | He has direction vectors and coordinate hashing from Walking Robot Simulation, but never with backtracking and never on a grid whose bounds are unknown. The state-restoration step is the part candidates get wrong, and it is a well-known Google and Meta question rather than an exotic one. This is the natural next rung on the grid-simulation theme he has already started. |

