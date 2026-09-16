# DSA and Problem-Solving Memory: Keywords and Keypoints

A grouped reference of DSA keypoints, code snippets, formulas and one-liners converted from `DSA_Memory.txt`; the original item numbers (for example `[-33]` or `[45]`) are kept in every heading so each entry can be traced back to the source notes.

## Table of Contents

1. [Problem-solving approach](#1-problem-solving-approach)
2. [Coding habits and common mistakes](#2-coding-habits-and-common-mistakes)
3. [Arrays, prefix sums and cyclic sort](#3-arrays-prefix-sums-and-cyclic-sort)
4. [Two pointers and sliding window](#4-two-pointers-and-sliding-window)
5. [Binary search](#5-binary-search)
6. [Sorting, comparators, selection and heaps](#6-sorting-comparators-selection-and-heaps)
7. [Linked lists](#7-linked-lists)
8. [Strings](#8-strings)
9. [Matrix and grid](#9-matrix-and-grid)
10. [Binary trees and BST](#10-binary-trees-and-bst)
11. [Graphs: traversal, cycles and topological sort](#11-graphs-traversal-cycles-and-topological-sort)
12. [Graphs: shortest paths, MST, DSU and SCC](#12-graphs-shortest-paths-mst-dsu-and-scc)
13. [Recursion, backtracking and subsets](#13-recursion-backtracking-and-subsets)
14. [Dynamic programming](#14-dynamic-programming)
15. [Math, number theory and bit tricks](#15-math-number-theory-and-bit-tricks)
16. [Java specifics](#16-java-specifics)
17. [Miscellaneous](#17-miscellaneous)

---

## 1. Problem-solving approach

### Brute force to better than best

Brute Force -> (Sorting) Better -> Best -> Better than Best

### Think from easy to hard

- Thinking from easy to hard, simple to complex, small to large.
- Constraints will be getting added making it more complex, so new logic will be added.

### Constraints narrow the scope

- Constraints -> Narrow down the scope, define limits.

### Rephrase the problem

- Rephrase the problem and constraints as many times as needed to understand it fully.
- Like multiple sentence-version representations of the same problem.

### Logic is just ifs, elses and loops

Problem-Solving with Algorithms and Data Structures:

It is so simple. What is logic? Nothing but ifs and elses and loops. That is all what logic is, and using the right data structures and algorithmic approaches with the help of a grasp of the problem.

### Read, rephrase, focus on constraints

- Read through the problem carefully and multiple times and rephrase the problem with the rules involved.
- Focus on constraints and edge cases.
- Implement logic that follows constraints.
- Constraints and the rules which control the logic to be implemented.
- Constraints VS Logic (apply intuition and common sense).
- Logic VS Constraints (intuition: the ability to understand something intrinsic instinctively and intuitively without the need for conscious reasoning).

### Break the problem down into smaller parts

Break down the problem into smaller, manageable parts.

- 1 Dimension
  - 0 elements
  - 1 element
  - 2 elements
  - 3 elements
  - 4 elements -- repeat the process until the problem is solved.
- 2 Dimension (grid or matrix)
  - Solve only a column (left-most or right-most).
  - Solve only a row (first or last).
  - Solve single row, then move to multi-row thinking.
  - Use solved row results to solve a new row addition with usage of the solved or previous dp row: `dp[i][j] = grid[i][j] + max(dp[i-1][j], dp[i][j-1])`
  - 1 x 1
  - 2 x 2
  - 3 x 3
  - 4 x 4

---

## 2. Coding habits and common mistakes

### [-12] While, not if

- Inside, sometimes it's `while` not `if` -- it will be a common mistake.
- Generally when we use a for-i loop it means we are exploiting the `i` value.

### [-9] Basic building blocks

- if / else-if
- if / if
- for loop with `i`
- while loop
- swap
- find mid in binary search
- find min value or index
- find max value or index
- binary tree: check left, check right

### [-8] Variable naming pairs

- `low`, `left`, `start`
- `high`, `right`, `end`
- `high = arr.length - 1;`

### [-7] Edge cases

Edge cases like start and end, length, null etc.

### [-6] Identify, then process

- Identify, Process.
- After identification perform action.

### [-2] Check places of i and j

Check places of `i`, `j` as `i`, `j` -- swapping them is a very common mistake in coding.

### [52] Start the inner loop at j = i + 1

Remember `j = i + 1` when we don't want to include elements that are on the right side of `i`, not on the left side of `i`.

---

## 3. Arrays, prefix sums and cyclic sort

### [-33] Cyclic sort

```java
public static void cyclicSort(int[] nums) {
    int i = 0;
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;
        if (nums[i] != nums[correctIndex]) {
            swap(nums, i, correctIndex);
        } else {
            i++;
        }
    }
}
```

### [-32] Merge two sorted arrays in place (gap method)

```java
int m = arr1.length;
int n = arr2.length;

int length = m + n;
// Calculate the initial gap
int gap = (length / 2) + (length % 2);

while (gap > 0) {
    int left = 0;
    int right = left + gap;
    while (right < length) {
        // Case 1: arr1 and arr2 comparison
        if (left < m && right >= m) {
            swapIfGreater(left, right - m, arr1, arr2);
        }
        // Case 2: arr2 and arr2 comparison
        else if (left >= m) {
            swapIfGreater(left - m, right - m, arr2, arr2);
        }
        // Case 3: arr1 and arr1 comparison
        else {
            swapIfGreater(left, right, arr1, arr1);
        }
        left++;
        right++;
    }
    if (gap == 1)
        break; // When gap is 1, we can stop further halving

    // Reduce gap for the next iteration
    gap = (gap / 2) + (gap % 2); // (gap+1)/2;
}
```

### [-27] Majority element (Boyer-Moore voting)

```java
int candidate = nums[0];
int count = 0;
// Boyer-Moore Voting Algorithm
for (int num : nums) {
    if (count == 0) candidate = num;
    count += (num == candidate) ? 1 : -1;
}
return candidate;
```

### [-26] Sort colors (Dutch national flag)

```java
low = 0, mid = 0, high = n - 1
case 0:
    swap(nums, low, mid);
    low++, mid++
case 1: // If the element is 1
    mid++;
    break;
case 2: // If the element is 2
    swap(nums, mid, high);
    high--;
```

### [-21] Kth missing positive number

```java
public int findKthPositive(int[] arr, int k) {
    int missingCount = 0;
    int expectedNum = 1;
    int index = 0;
    while (missingCount < k) {
        if (index < arr.length && arr[index] == expectedNum)
            index++;
        else
            missingCount++;

        if (missingCount == k)
            return expectedNum;
        expectedNum++;
    }
    return -1;
}
```

### [-15] Next permutation (lexicographical order)

- Find an adjacent pair from right to left such that `arr[i] > arr[i+1]`.
- Swap `a[i]` with the first larger value from the right and then reverse from `i+1` to the end.

### [-13] Count subarrays with sum equal to K (prefix-sum map)

```java
/**
 * Input: nums = [1,2,3], k = 3
 * <p>
 * Output: 2
 */
class ImportantCountSubarraySumEqualsK {
    public static int subarraySum(int[] nums, int k) {
        int count = 0;
        Map<Integer, Integer> preSumCountMap = new HashMap<>();
        int preSum = 0;
        for (int i = 0; i < nums.length; i++) {
            preSum += nums[i];
            if (preSum == k) count++;
            int remSum = preSum - k;
            if (preSumCountMap.containsKey(preSum - k)) {
                // remember we update count with count from map of remSum
                // not in map
                count += preSumCountMap.get(remSum);
            }
            // update count of preSum in map not remSum
            preSumCountMap.put(preSum, preSumCountMap.getOrDefault(preSum, 0) + 1);
        }
        return count;
    }
    public static void main(String[] args) {
        System.out.println(subarraySum(new int[]{1, 2, 3}, 3));
    }
}
```

### [3] Prefix sum

```text
25 = 25
25 + 10 = 35
35 - 10 = 25
```

### [16] Left rotate an array by k steps

Example: `arr = [1, 2, 3, 4, 5, 6, 7]`, `k = 2`

- Normalize k -> do `k = k % n` (since rotating by n brings the array back).
- Reverse the first k elements: `reverse(arr, 0, k - 1);`
- Reverse the remaining n-k elements: `reverse(arr, k, n);`
- Reverse the whole array: `reverse(arr, 0, n);`

### [17] Right rotate an array by k steps

- Normalize k -> `k = k % n`.
- Reverse the last k elements: `reverse(arr, n - k + 1, n);`
- Reverse the first n-k elements: `reverse(arr, 0, n - k);`
- Reverse the whole array: `reverse(arr, 0, n);`

### [18] If / else-if ladder for set operations on arrays

- IF ELSEIF LADDER
- Intersection of two or more arrays
- Union of two or more arrays
- Un-common of two or more arrays
- (For uneven / all possible ways, two sets and sorting `ansList` can help.)

### [21] Cyclic sort solution (find duplicate and missing)

```java
// If a number is not in its correct place, swap it with the number at its correct position.
// [1, 2, 3, 5, 2]
// i is currentIndex
int correctIdx = nums[i] - 1;
// nums[i] = 2, nums[correctIdx] = 2
if (nums[i] != nums[correctIdx]) {
    swap(nums, i, correctIdx);
}

for (int j = 0; j < nums.length; j++) {
    if (nums[j] != j + 1) {
        duplicated = nums[j];
        missing = j + 1;
    }
}
```

### [34] Can place flowers

```java
// canPlaceFlowers
//              [0 , 0 , 0]
if (flowerbed[i] == 0 &&
    (i == 0 || flowerbed[i - 1] == 0) &&
    (i == flowerbed.length - 1 || flowerbed[i + 1] == 0)) {
    flowerbed[i] = 1; // Place a flower here
    count++;
```

### [37] Use long for the prefix-sum remainder

```java
long rem = preSum - target;
```

### [38] Cyclic sort: correct index of x is x - 1

For an array containing 1 to n, the correct index of a number x is `x - 1`.

```java
// Cyclic sort
// currVal to correctIdx means correctIdx = currVal - 1
// value at correctIdx to currentIdx means to the currentVal idx
// nums[i] -> currVal
int correctIdx = nums[i] - 1;
if (nums[correctIdx] != nums[i]) {
    int temp = nums[correctIdx];
    nums[correctIdx] = nums[i];
    nums[i] = temp;
}
```

---

## 4. Two pointers and sliding window

### [-29] Longest substring without repeating characters

```java
// ImportantLongestSubStringWithoutRepeatingCharacter
public static void main(String[] args) {
    String string = "abcddcba";
    Map<Character, Integer> map = new HashMap<>();
    int maxLength = 0;
    for (int left = 0, right = 0; right < string.length(); right++) {
        char c = string.charAt(right);
        if (map.containsKey(c))
            left = Math.max(map.get(c) + 1, left);

        if (maxLength < right - left + 1)
            maxLength = right - left + 1;
        // Remember update or insert latest
        // index for current character in map
        map.put(c, right);
    }
    System.out.println(maxLength);
}
```

### [45] Max cyclic sum of window size k (max score from cards)

```java
// maxCyclicSum of window size k
private static int maxScore(int[] cards, int k) {
    int n = cards.length;
    int sum = 0;
    // summing last k elements
    for (int i = n - k; i < n; i++) {
        sum += cards[i];
    }
    Integer max = sum;
    // as i = 1 is initial setting doing compensation as n-1-k+i
    for (int i = 1; i <= k; i++) {
        sum += cards[i-1] - cards[n-1-k+i];
        max = Math.max(max, sum);
        Math.max(max, sum);
    }
    return max;
}
```

### [-20] Sliding window maximum (monotonic deque)

```java
// SlidingWindowMaximum
public int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || k <= 0) return new int[0];
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new LinkedList<>();
    for (int i = 0; i < n; i++) {
        if (!deque.isEmpty() && deque.peekFirst() < i - k + 1)
            deque.pollFirst();
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i])
            deque.pollLast();
        deque.offer(i);
        if (i >= k - 1)
            result[i - k + 1] = nums[deque.peek()];
    }
    return result;
}
```

### [-11] Window index formula for window size k

Another variation of window size k, where `n` is the size of the array and `k` is the size of the window:

```text
Window.i = A[i : i+k-1] for i in [0, n-k]
So:
Window 1    -> indices 0 ... k-1
Window 2    -> indices 1 ... k
.....
Last window -> indices n-k ... n-1
```

### [1] Start index from end index and max length

```text
StartIndex = EndIndex - MaxLength + 1;
EndIndex
```

---

## 5. Binary search

### [-28] Median of two sorted arrays

```java
private static double findMedianSortedArrays(int[] arr1, int[] arr2) {

    int m = arr1.length;
    int n = arr2.length;
    int totalLength = m + n;
    int halfLength = (m + n + 1) / 2;
    int low = 0, high = m;

    while (low <= high) {
        int mid1 = (low + high) / 2;
        int mid2 = halfLength - mid1; // REMEMBER

        // Use appropriate values for left and right partitions
        int l1 = (mid1 > 0) ? arr1[mid1 - 1] : Integer.MIN_VALUE;
        int l2 = (mid2 > 0) ? arr2[mid2 - 1] : Integer.MIN_VALUE;
        int r1 = (mid1 < m) ? arr1[mid1] : Integer.MAX_VALUE;
        int r2 = (mid2 < n) ? arr2[mid2] : Integer.MAX_VALUE;

        if (l1 <= r2 && l2 <= r1) {
            if (totalLength % 2 == 1)
                return Math.max(l1, l2);
            return (Math.max(l1, l2) + Math.min(r1, r2)) / 2.0;
        }
        if (l1 > r2) high = mid1 - 1;
        else low = mid1 + 1;
    }
    return 0.0;
}
```

### [4] Kth element of two sorted arrays (search bounds)

- `m` is the size of array A and `n` is the size of array B.
- Assume `a1 = 3`, `a2 = 5` and `k = 7`; then `m1 = 1`, `m2 = 7 > n`, so an out-of-bounds exception.

```java
// KthElementOf2SortedArrays
int low = Math.max(0, k - n), high = Math.min(k, m);
```

### [11] Overflow-safe mid

```java
int mid = low + (high - low) / 2;
```

### [12] First and last occurrence (like floor and ceil)

- Use the `high` index and move towards the left to find the first occurrence.
- Use the `low` index and move towards the right to find the last occurrence.

### [18.1] Moving left and right using the mid pointer

- `midValue` and `targetValue`: in binary search, if `midVal < targetValue` it means `targetVal` is on the right of `midVal`, so ignore the left half: `left = mid + 1` (move `left` to the right of `mid`).
- Vice versa: `right = mid - 1`.
- Here `left` moving to the right or `right` moving to the left uses the mid pointer.

### [24] Binary search on a 2D matrix

```java
int rows = matrix.length;
int cols = matrix[0].length;
int left = 0;
int right = rows * cols - 1;
int mid = left + (right - left) / 2;
// no of cols = record or row size
int midValue = matrix[mid / cols][mid % cols];
```

### [48] Lower bound and upper bound

- Lower Bound (LB) -> the first position in the array where the target (or a condition) could appear.
  - `lower_bound` -> first index where `arr[i] >= target`
- Upper Bound (UB) -> the position just after the last occurrence of the target (or where the condition fails).
  - `upper_bound` -> first index where `arr[i] > target`

---

## 6. Sorting, comparators, selection and heaps

### [-31] Min-heap of map entries ordered by frequency

```java
PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
        (a, b) -> a.getValue() - b.getValue() // Sort by frequency (map values)
);
```

### [-30] Quickselect with partition

```java
private static int quickSelect(int low, int high, int k, int[] arr) {
    int partitionIndex = partition(low, high, arr);
    if (partitionIndex == k) return arr[k];
    else if (partitionIndex < k)
        return quickSelect(partitionIndex + 1, high, k, arr);

    return quickSelect(low, partitionIndex - 1, k, arr);
}

private static int partition(int low, int high, int[] arr) {
    int pivotValue = arr[high];
    int partitionIndex = low;
    for (int j = partitionIndex; j < high; j++) {
        if (arr[j] < pivotValue) {
            int lessThanPivot = arr[j];
            arr[j] = arr[partitionIndex];
            arr[partitionIndex] = lessThanPivot;
            // values left side of partitionIndex
            // are smaller than pivotValue
            partitionIndex++;
        }
    }
    int temp = arr[partitionIndex];
    arr[partitionIndex] = arr[high];
    arr[high] = temp;
    return partitionIndex;
}
```

### [-19] Min-heap and max-heap behaviour

- minHeap -> when `poll` is executed the min value will be removed and returned.
- maxHeap -> when `poll` is executed the max value will be removed and returned.
- For a sorted array -> the left half of values in a maxHeap and the right half can be stored in a minHeap.

### [-17] Comparator semantics

- `a - b == 0` -> means maintain insertion order.
- `a.compareTo(b) < 0` -> a comes before b.
- `a.compareTo(b) > 0` -> a comes after b.

```java
Collections.sort(names, (a, b) -> b.compareTo(a)); // descending

public int compareTo(Edge b) {
    return this.weight - b.weight;
}
```

### [-14] Heap sort child indices

```java
// Heap Sort
int largest = i;       // Initialize largest as root
int left = 2 * i + 1;  // left = 2*i + 1
int right = 2 * i + 2; // right = 2*i + 2
```

### [-1] Ascending vs descending comparators

- Comparing `a(o1)`, `b(o2)` as `a - b` for ascending order.
- Comparing `a(o1)`, `b(o2)` as `b - a` for descending order.

### [26] Sort intervals by their end

```java
int[][] points -> [[1,2],[3,4],[5,6],[7,8]]
// sort by ends of intervals
Arrays.sort(points, (o1, o2) -> o1[1] - o2[1]);
Arrays.sort(points, Comparator.comparingInt(a -> a[1]));
```

### [35] Quick sort

```java
void quickSort(int[] array, int low, int high) {
    if (low < high) {
        int partitionIndex = partition(array, low, high);
        quickSort(array, low, partitionIndex - 1);
        quickSort(array, partitionIndex + 1, high);
    }
}
```

Partition -> move the selected pivot to the right place; all left are smaller, all right are sorted but necessarily in the right place; the pivot is placed in the right place.

### [42] Max-heap and min-heap for the two halves

```java
// max heap
// maxHeap for left half sorted part
// minHeap for right half sorted part
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap
```

### [49] Merge sort

```java
private int[] mergeSort(int left, int right, int[] nums) {
    if (left == right) {
        // Base case, single element array either left or right
        return new int[]{nums[right]};
    }

    int mid = (left + right) / 2;
    int[] leftPart = mergeSort(left, mid, nums);
    int[] rightPart = mergeSort(mid + 1, right, nums);

    return merge(leftPart, rightPart);
}
```

---

## 7. Linked lists

### [-23] Delete Nth node from end

```java
// Delete Nth node from end
for (int i = 0; i < N; i++) fastp = fastp.next;
if (fastp == null) return head.next;
while (fastp.next != null) {
    fastp = fastp.next;
    slowp = slowp.next;
}
```

### [32] Find the middle of a linked list and reverse a list

```java
// here prev is pointer before mid of linked list
// generally used for sorting rest no need for prev one
ListNode prev = null;
ListNode slow = head;
ListNode fast = head;
while (fast != null && fast.next != null) {
    prev = slow;
    slow = slow.next;
    fast = fast.next.next;
}
// to find mid of linkedList

private static ListNode reverseList(ListNode head) {
    ListNode prev = null;
    while (head != null) {
        ListNode nextNode = head.next;
        head.next = prev;
        prev = head;
        head = nextNode;
    }
    return prev;
}
```

### [55] DeleteNthNodeFromEnd

DeleteNthNodeFromEnd (title only in the source; see item [-23] above for the snippet).

---

## 8. Strings

### [0] ASCII values of A and a

- `A = 65`, `a = 97`
- `A + 32 = a`

### [23] Find every occurrence of a word

```java
while ((start = s.indexOf(word, start)) != -1)
```

### [33] Join strings with a delimiter

```java
String joined = String.join(" ", List.of("abc", "def"));
```

### [39] ATOI overflow check

```java
// ATOI
// by 10 as we have to multiply by 10, -intVal as we do +intVal
// (Integer.MAX_VALUE - intVal) / 10) remember
```

### [44] Text justification line-fit condition

```java
// TextJustification
currentLineLength + word.length() + currentLine.size() > maxWidth
```

### [50] String.join on a list

```java
List<String> strings = List.of("abc", "def");
String joined = String.join(",", strings);
System.out.println(joined);
```

---

## 9. Matrix and grid

### [6] Transpose and rotate a matrix

```java
// Perform Transpose
for (int i = 0; i < n; i++) {
    // remember -> int j = i + 1
    for (int j = i + 1; j < n; j++)
        // now swap mat[i][j] with mat[j][i]
```

- 90 Degree -> Transpose the matrix and reverse each row.
- 180 Degree -> Reverse each column and reverse each row (inverse also works).
- 270 Degree -> Transpose the matrix and reverse each column.

### [22] Row and column neighbours

- `noOfRows = n`; the row variable may start from 0 or n.
- If starting from n to 0, then:
  - `row` = current row
  - `row + 1` = below row
  - `row - 1` = above row
- Same kind for column:
  - may be the left column
  - may be the right column

---

## 10. Binary trees and BST

### [-25] Height of a binary tree

```java
class HeightOfBinaryTree {
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
```

### [-4] Complete binary tree

1. All levels except possibly the last are completely filled.
2. The last level is filled from left to right without gaps.

### [2] Think of three nodes

If it is a binary tree problem, think of three nodes and write the logic; now repeat the same logic for the left child (or left tree) and the right child (or right tree).

### [9] Traversal orders

- Post order -> left, right, root (process the left subtree, process the right subtree, derive the result from both results).
- Pre order -> root, left, right (process the possibility with the current node and traverse for left, right).
- In order -> left, root, right [in a BST it gives a sorted array].
  - In-order traversal always gives the nodes in sorted (ascending) order because of the BST.
  - Here left is the prev node, root is the current node for BST to DLL.

### [10] Floor and ceil in a BST

- Floor of x = the greatest value in the BST <= x.
- Ceil of x = the smallest value in the BST >= x.

### [25] Count nodes of a complete binary tree

```java
if (leftHeight == rightHeight) {
    // If the heights are equal, it is a perfect binary tree
    return (1 << leftHeight) - 1; // 2^height - 1
} else {
    // Otherwise, recursively count nodes in left and right subtrees
    return 1 + countNodes(root.left) + countNodes(root.right);
}
```

---

## 11. Graphs: traversal, cycles and topological sort

### DFS and BFS

DFS and BFS (keyword listed in the source preamble).

### [58] Visited vs explored; BFS (level order traversal)

- visited -> just come across the vertex.
- explored -> all adjacent nodes of the vertex also visited.
- BFS (level order traversal):
  - Use a Queue and `vis[]`.
  - `queue.add(0); vis[0] = true`
  - Use a for loop initialized from the queue size to complete a level.

### [59] Rotten oranges (multi-source BFS on a grid)

```java
// Rotten oranges
// 4 directions
int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
// Directions: right, down, left, up
// int[] directions = {0, 1, 0, -1, 0};

// remember fresh > 0 also
while (!queue.isEmpty() && fresh > 0) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        int[] point = queue.poll();
        for (int[] d : dirs) {
            int x = point[0] + d[0];
            int y = point[1] + d[1];
            // out of bounds with or condition is more performant
            // than all are in bounds with and condition
            if (x < 0 || y < 0 || x >= rows || y >= cols || grid[x][y] != 1)
                continue;
            grid[x][y] = 2;  // rot it
            queue.offer(new int[]{x, y});
            fresh--;  // one less fresh
        }
    }
    minutes++;
}
```

### [60] Cycle detection in an undirected graph

- My parent and my adjacent node shouldn't be the same.
- Detecting a cycle in an undirected graph (bi-directional graph).
- In BFS: same reasoning, using a queue with `(node, parent)`.
- `u --> v`, here u is the parent of v.
- `1->2`, `2->1`
- Cycle in an undirected graph: `parent != vNode`
- `1->2->1` (parent -> node -> adjNode), `(parent != adjNode)` return true.
- A path `1 -> 2 -> 1` is not considered a cycle.
- A real cycle would be something like `1 -> 2 -> 3 -> 1`.

```java
private boolean dfs(int node,
                    int parent,
                    boolean[] vis,
                    ArrayList<ArrayList<Integer>> adj) {
    vis[node] = true;
    for (int adjacentNode : adj.get(node)) {
        if (!vis[adjacentNode]) {
            if (dfs(adjacentNode, node, vis, adj)) return true;
        }
        else if (adjacentNode != parent) return true;
    }
    return false;
}
```

### [61] Cycle detection in a directed graph

In directed graphs we will use the same logic along with `pathVisited`.

### [62] Bipartite colouring

```text
col = 1
(constant(1) variable(col))
newCol = 1 - col --> 0
col = 1 - 0
```

### [63] Topological sort

- Topological sorting only exists in a Directed Acyclic Graph (DAG).
- If the nodes of a graph are connected through directed edges and the graph does not contain a cycle, it is called a directed acyclic graph (DAG).
- The topological sorting of a directed acyclic graph is nothing but the linear ordering of vertices such that if there is an edge between node u and v (`u -> v`), node u appears before v in that ordering.

### [64] Topological sort using DFS

- Perform DFS on the graph.
- `1->2->3->4->5`: the stack contains `(5,4,3,2,1)` after DFS, with 1 at the top.
- When a node finishes (all its neighbors are visited), push it onto a stack (or add it to the front of a list).
- At the end, the stack/list will have the topological order.
- Time Complexity: O(V + E) (DFS traversal).

### [65] Kahn's algorithm

- Compute in-degrees: for every node in the graph, count the number of incoming edges (in-degree).
- Initialize a queue: put all nodes with in-degree = 0 into a queue.
- Process nodes in the queue. While the queue is not empty:
  - Pop a node from the queue.
  - Add it to the topological order result list.
  - For each neighbor (outgoing edge) of this node: reduce that neighbor's in-degree by 1 (because one dependency is removed). If the in-degree becomes 0, push it into the queue.
- Check for a cycle:
  - After processing, if the result list contains all nodes -> valid topological sort.
  - If not all nodes are included -> the graph has a cycle (no valid topo order).

### [74] Eventual safe nodes

- A node with zero out-degree is called a terminal node.
- A node is called eventually safe if starting from it, every possible path leads to a terminal node (a node with no outgoing edges).
- EventualSafeNodesByDFS: do DFS with `vis`, `pathVis`, `safe` boolean arrays; if not part of a cycle then update the node as a safe node.
- We track states:
  - 0 -> unvisited
  - 1 -> visiting (currently in recursion stack -> cycle)
  - 2 -> safe (already proven safe)

### [75] Building an adjacency list from edges

You will be given edges using which you can create a graph:

```java
List<List<Integer>> adjList = new ArrayList<>();
List<List<int[]>> adjList = new ArrayList<>(n);
```

### [76] Converting (i, j) to a node index

- `rowSize = n`
- Converting `rowIndex = i`, `colIndex = j` to a node:

```java
int node = i * rowSize + j;
```

---

## 12. Graphs: shortest paths, MST, DSU and SCC

### [66] Dijkstra's algorithm (using a priority queue)

- Dijkstra's algorithm finds the shortest path from a source node to all other nodes in a weighted graph. [Shortest Path Algorithm]
- Can't handle negative weights.
- Initialize `dist[]` with infinity, and `dist[src] = 0`.
- Push `(0, src)` into a min-heap (PriorityQueue in Java). Format: `(distance, node)`.
- While the PQ is not empty:
  - Pop the node u with the smallest distance.
  - For each neighbor `(v, weight)` of u: if `dist[u] + weight < dist[v]` -> update `dist[v]` -> push `(dist[v], v)` into the PQ.
- Continue until the PQ is empty.
- Complexity:
  - PQ operations -> O(log V)
  - Time Complexity: O((V + E) log V)
  - Space Complexity: O(V + E)

### [67] Bellman-Ford algorithm

- O(VE)
- Used for Single Source Shortest Path and works with negative weights, unlike Dijkstra.
- Initialization: create a `dist[]` array of size V.
  - `dist[src] = 0` (distance to itself is 0)
  - `dist[v] = infinity` (for all other vertices initially)
- Relax all edges (V-1) times:
  - For i = 1 to V-1, for all edges `(u, v, w)` with weight w: if `dist[u] + w < dist[v]` -> update `dist[v] = dist[u] + w`.
- Check for negative weight cycles: one more relaxation round for all edges.
  - For every edge `(u, v)` with weight w: if `dist[u] + w < dist[v]`, then a negative weight cycle exists (since distances can still be reduced).

### [68] Floyd-Warshall algorithm

- `matrix[i][j] = (int) (1e9);` -> `(int)` type casting needed.
- Used to find the shortest paths between all pairs of vertices in a weighted graph (can handle positive and negative weights, but no negative cycles).
- Build a distance matrix `dist[V][V]`, where:
  - `dist[i][j]` = weight of edge (i -> j) if it exists.
  - `dist[i][i] = 0` (distance from a node to itself is zero).
  - `dist[i][j]` = infinity (large number) if no direct edge exists.
  - `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])`
- Run three loops:
  - Outer loop (k) -> iterate over each vertex k as an intermediate.
  - Middle loop (i) -> pick a source vertex.
  - Inner loop (j) -> pick a destination vertex.
- Time Complexity: O(V^3)
- Space Complexity: O(V^2)

### [69] Disjoint set with path compression

You initialize from 0 to v, so your DSU supports indices `[0..v]` (v+1 elements).

```java
int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);
    }
    return parent[x];
}

public int findUPar(int node) {
    if (node == parent.get(node))
        return node;
    // find ultimate parent for my parent
    int ulp = findUPar(parent.get(node));
    parent.set(node, ulp);
    return parent.get(node);
}
```

### [70] Disjoint Set Union (DSU) uses

1. Cycle detection in an undirected graph
2. Minimum Spanning Tree (MST) -- Kruskal's algorithm
3. Connected components

A property of a minimum spanning tree: if V is the number of vertices and E is the number of edges, then `Emst (no of edges) = V - 1`.

### [71] Kruskal's algorithm

- Better for sparse graphs (fewer edges) since sorting edges is cheaper.
- O(E log E)
- Sort all edges by weight:
  - Pick the lightest edge first (greedy choice).
  - Sorting takes O(E log E).
- Initialize DSU (Disjoint Set Union).
- Iterate all edges, check the ultimate parent of u and v; if not the same, union by size and add the weight.
- Return as the minimum spanning tree sum.

### [72] Prim's algorithm

- More efficient for dense graphs (many edges) since it works with an adjacency list and PQ.
- It's Dijkstra's modified version.

### [73] Accounts merge

```java
// Hashmap to store the pair: {mails, node}
Map<String, Integer> mapMailNode = new HashMap<>();
if (!mapMailNode.containsKey(mail))
    mapMailNode.put(mail, i);
else {
    ds.unionBySize(i, mapMailNode.get(mail));
}
```

### [77] Kosaraju's algorithm (strongly connected components)

- A graph algorithm used to find Strongly Connected Components (SCCs) in a directed graph.
- In a directed graph, an SCC is a maximal subgraph where every vertex is reachable from every other vertex in that subgraph.
- Example: in a graph, if A -> B and B -> A, then {A, B} forms an SCC.
- Steps:
  - a. do stackDFS
  - b. do reverse graph
  - c. use the stack and do visitDFS
  - While doing c, count components.

### [78] Tarjan's algorithm to find all bridges

- BridgesInGraph -- BIDIRECTIONAL GRAPH (critical connections/edges) in a graph.
- Discovery Time (`disc[]`): the time when a node is first visited during DFS.
- Lowest Reachable Time (`low[]`): the earliest discovered vertex that a node can reach (including back edges).
- Bridge condition: an edge `(u, v)` is a bridge if `low[v] > disc[u]`.
  - Meaning: if v (and its descendants) cannot reach back to u or its ancestors, removing `(u, v)` disconnects the graph.
- DFS:
  - Recurse: `dfs(v, u)`.
  - After return: update `low[u] = min(low[u], low[v])`.
  - Bridge check: if `low[v] > disc[u]`, then `(u, v)` is a bridge.
  - Else (back edge case): update `low[u] = min(low[u], disc[v])`.

### [79] Articulation point

```java
low[node] = Math.min(low[node], low[it]);
if (low[it] >= tin[node] && parent != -1) {
    mark[node] = true;
}
child++;
```

---

## 13. Recursion, backtracking and subsets

### [-22] N-Queens bookkeeping arrays

```java
int[] leftRow = new int[n];
int[] upperDiagonal = new int[2 * n - 1];
int[] lowerDiagonal = new int[2 * n - 1];
board[row][col] = 'Q';
leftRow[row] = 1;
lowerDiagonal[row + col] = 1;
upperDiagonal[length - 1 + col - row] = 1;
```

### [-16] Recursion: pick and not-pick

- Recursion: left (not-pick), right (pick).
- min or max -> `Math.max(left, right)`
- count of all ways -> `left + right`

### [7] Recursion with pick and non-pick

- Recursion with pick and non-pick -> count possible ways, min, max etc.
- CountSubsequenceWithTargetSum

### [53] Subsets

```java
/**
 * Example 1:
 * <p>
 * Input: nums = [1,2,3]
 * <p>
 */
```

- pick, notPick, conditional pick
- pick all -> gives a subset
- pick half -> gives a subset
- pick none -> gives a subset

Pick first, then not-pick:

```java
private static void subsets(int[] ints, int i, List<Integer> subset, List<List<Integer>> subsets) {
    if (i == ints.length) {
        subsets.add(new ArrayList<>(subset));
        return;
    }
    subset.add(ints[i]);
    subsets(ints, i + 1, subset, subsets);
    subset.remove(subset.size() - 1);
    subsets(ints, i + 1, subset, subsets);
}
```

Output: `[[1, 2, 3], [1, 2], [1, 3], [1], [2, 3], [2], [3], []]`

Not-pick first, then pick:

```java
private static void subsets(int[] ints, int i, List<Integer> subset, List<List<Integer>> subsets) {
    if (i == ints.length) {
        subsets.add(new ArrayList<>(subset));
        return;
    }
    subsets(ints, i + 1, subset, subsets);
    subset.add(ints[i]);
    subsets(ints, i + 1, subset, subsets);
    subset.remove(subset.size() - 1);
}
```

Output: `[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]`

### [57] N-Queens diagonals

- Row incremented from the for loop; col incremented from the recursion call.
- UPPER DIAGONAL (top-left to bottom-right): `INDEX = (COL - ROW) + (N - 1)`
- Lower diagonal (bottom-left to top-right): `col + row`

```java
int[] leftRow = new int[n];
int[] upperDiagonal = new int[2 * n - 1];
int[] lowerDiagonal = new int[2 * n - 1];
```

### [81] Recursion principles

1. Define a clear BASE CASE -> prevents infinite recursion.
2. Reduce the problem -> recursion must MOVE TOWARD the BASE CASE.
3. Decide whether you need return values (like Fibonacci) or accumulating results (like DFS/backtracking).
4. Think: "If I had the solution for a smaller input, how do I build the answer for the current input?"

### [83] Subsets vs subsequences

`[1, 2, 3]`

Sub-sets (order does not matter):

```text
[]
[1], [2], [3]
[1,2], [1,3], [2,3]
[1,2,3]
```

Sub-sequences (order matters):

```text
[]
[1], [2], [3]
[1,2], [1,3], [2,3]
[1,2,3]   ([3,2,1]) -> not a sub-sequence
```

---

## 14. Dynamic programming

### Introduction to dynamic programming

1. Correctness of brute force and efficiency of greedy algorithms.

### [28] Longest common subsequence

```text
dp[i][j] = 1 + dp[i-1][j-1]
dp[i][j] = max(dp[i-1][j], dp[i][j-1])
```

### [29] Edit distance (horse, ros)

```text
dp[i][j] = dp[i-1][j-1]
dp[i][j] = 1 + min(
    dp[i-1][j],    // delete from X
    dp[i][j-1],    // insert into X
    dp[i-1][j-1]   // replace
)
```

### [30] Unique paths

```text
dp[i][j] = dp[i-1][j] + dp[i][j-1]
```

### [80] Subset sum equals target

```java
// SubsetSumEqualsToTarget
private boolean func(int ind, int target, int[] arr) {
    if (target == 0) return true;
    if (ind == 0) return arr[0] == target;
    // we will add to list and remove from list
    // if we want to capture answer/s
    boolean notTaken = func(ind - 1, target, arr);
    boolean taken = false;
    int currVal = arr[ind];
    if (currVal <= target)
        taken = func(ind - 1, target - currVal, arr);
    return notTaken || taken;
    // return (int)(dp[ind][T] = (notTaken + taken) % MOD);
}
```

### [82] Partition DP

```text
Partition DP
Base case with return
            k -> i to j try partition at every possible index
            such that left and right partitions possible
            (i, j, arr) -> initially i = 0, j = n-1 (may change)
    (i, k, arr)        (k+1, j)    -> try all partitions
    return best possible 2 partitions
make sure at least either side of a single edge case partition possible
```

---

## 15. Math, number theory and bit tricks

### [-24] Binary exponentiation

```java
// Function to calculate b^p using binary exponentiation
public static long binaryExponentiation(long b, long p) {
    long result = 1;
    long base = b;
    while (p > 0) {
        // If p is odd, multiply the current base to the result
        if ((p & 1) == 1) {
            result *= base;
        }
        // Square the base and halve the exponent
        base *= base;
        p >>= 1;  // equivalent to p = p / 2
    }
    return result;
}
```

### [8] LCM and GCD

LCM -> Least Common Multiple of two numbers n1, n2: `(n1 * n2) / GCD`

```java
private static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
}

private int GCD(int n1, int n2) {
    while (n1 > 0 && n2 > 0) {
        if (n1 > n2)
            n1 = n1 % n2;
        else
            n2 = n2 % n1;
    }
    if (n1 == 0) return n2;
    return n1;
}
```

### [14] XOR properties

- `a ^ 0 = a` (XOR == `^`)
- `a ^ a = 0`
- `(a ^ b) ^ b = a`
- `a ^ 111...111 = ~a` (ones complement)
- XOR is reversible:
  - `c = a ^ b`
  - `a = c ^ b`
  - `b = c ^ a`
- `1 ^ 0 ^ 1 ^ 1 = 1` (since three 1's -> odd)

### [15] Missing number

Missing Number: `(n * (n + 1)) / 2`, XOR

### [20] Binary watch: count set bits

`Integer.bitCount()` == number of set bits (1 bits) in the binary representation.

### [36] Pascal's triangle row

- Initial setting `ans = 1` and `rowNum`.
- Decreasing numerator from `rowNum - col` to `col`.
- Increasing denominator from `col` to `< rowNum`.

```java
private static List<Integer> generateRow(int rowNumber) {
    List<Integer> pascalRow = new ArrayList<>();
    pascalRow.add(1);
    int ans = 1;
    for (int col = 1; col < rowNumber; col++) {
        ans *= (rowNumber - col);
        ans /= col;
        pascalRow.add(ans);
    }
    return pascalRow;
}
```

### [40] Maximum swap

```java
// Maximum Swap
for (int i = 0; i < digits.length; i++) {
    // key condition to swap d > digits[i] - '0'
    for (int d = 9; d > digits[i] - '0'; d--)
```

### [41] Missing and repeating numbers: differentiating bit

```java
// Step 2: Find the differentiating bit number:
int number = (xr & -xr);
```

### [46] Clock angles

- The minutes needle covers 6 degrees in a minute.
- The hours needle covers 30 degrees in an hour.
- The hours needle covers 0.5 degrees in a minute (30/60).
- 30 degrees per hour / 60 minutes per hour = 0.5 degrees.
- `hoursAngle - minutesAngle` covered.

### [54] Binary exponentiation (repeated)

Same code as item [-24].

```java
public static long binaryExponentiation(long b, long p) {
    long result = 1;
    long base = b;

    while (p > 0) {
        // If p is odd, multiply the current base to the result
        if ((p & 1) == 1) {
            result *= base;
        }
        // Square the base and halve the exponent
        base *= base;
        p >>= 1;  // equivalent to p = p / 2
    }
    return result;
}
```

### [56] Mean / median of an array

meanMedian of arr: `Arrays.sort(arr);` n is the length of arr.

```java
if (n % 2 == 1) {
    return array[n / 2]; // Odd case
} else {
    return (array[(n / 2) - 1] + array[n / 2]) / 2.0; // Even case
}
```

```text
0 1 2 3 4 (5) 6 7 8 9 -> total 10 elements
10/2 = 5, left of 5 we have 5 elements and on right of 5 we have 4 elements
```

---

## 16. Java specifics

### Calling an interface default method

`A.super.m1();` -- `m1` is a default method of interface A.

### [27] Interval list to 2D array, and List to int[]

```java
int[][] intervals
List<int[]> result = new ArrayList<>();
return result.toArray(new int[result.size()][]);
// list to int array
List<Integer> list = Arrays.asList(7, 8, 9);
int[] array = list.stream()
      .mapToInt(Integer::intValue)
      .toArray();
```

### [-5] Creating a standalone Map.Entry

Creating only a key/value pair object of `Map.Entry`. Here `SimpleEntry` is a static inner class inside `AbstractMap`.

```java
new AbstractMap.SimpleEntry<>(first.getKey(), first.getValue() - 1)
```

### [-18] Correct usage of wait and notify

```text
inside if (condition matched) {
    // manipulation of shared data
    notifyAll or notify
}
else {
    // you got lock and condition not matched
    // please continue waiting
    obj.wait();
    // if any logic is here will be executed
}
```

For printing even and odd numbers a while loop with a `maxVal` condition is needed, else it won't work; same case for Producer and Consumer.

### [-10] Math.log10, Math.log and substring

```java
sout((int) Math.log10(12345)); // 4
sout((int) Math.log10(10000)); // 4
Math.ceil(Math.log(123))
Math.floor(Math.log(123))
str.substring(startIdx, endIdx) // endIdx is exclusive
```

### [-3] 1e9 equals one billion

```java
System.out.println(1e9 == 1000_000_000) // true
```

### [5] Narrowing cast to byte

```java
byte b = (byte) 10000;
// 2^8 = 256
// b = (byte) (10000 % 256); 16
```

### [13] Integer cache and ==

```java
Integer i = 1000;
int j = 1000;
Integer k = 1000;
i == j // true
i == k // false
```

### [43] Stack iteration order

```java
Stack<String> stack = new Stack<>();
stack.add("1");
stack.add("2");
stack.add("3");
stack.add("4");
// use for loop 1,2,3,4,
// use while loop with pop call 4,3,2,1
```

### [47] Random

```java
Random rand = new Random();
rand.nextInt(bound) // must be positive
// Value between zero (inclusive) and bound (exclusive)
// to generate random between range
rand.nextInt(1, 10) // 1 is inclusive and 10 is exclusive
// 1,7 -> rolling dice
```

### [51] computeIfAbsent for grouping

```java
map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
```

---

## 17. Miscellaneous

### [31] Rolling variables

```text
prevPrev = a
prev = b
current = c
```
