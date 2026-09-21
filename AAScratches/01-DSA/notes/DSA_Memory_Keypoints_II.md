# DSA Memory Keypoints II

A second grouped set of DSA keypoints and code snippets converted from `DSA_Memory_II.txt`; the original item numbers (for example `[-4]` or `[7]`) are kept in every heading so each entry can be traced back to the source notes.

Every snippet marked **verified** below was extracted to a scratch file, compiled with `tools/runjava` on the repo JDK, and run against the cases shown.

## How to read this file

| # | Section | What it covers | Items |
|---|---|---|---|
| 1 | [Arrays, prefix sums and cyclic sort](#1-arrays-prefix-sums-and-cyclic-sort) | Sign-marking, cyclic sort, prefix-sum map | 3 |
| 2 | [Sliding window](#2-sliding-window) | The window that never shrinks | 1 |
| 3 | [Stacks](#3-stacks) | Monotonic stack with a sentinel flush | 1 |
| 4 | [Binary trees](#4-binary-trees) | Build, max path sum, diameter | 3 |
| 5 | [Recursion and dynamic programming](#5-recursion-and-dynamic-programming) | Unbounded knapsack, factorial | 2 |
| 6 | [Math and formulas](#6-math-and-formulas) | Ceiling of half, running average, GCD/LCM | 3 |
| 7 | [Java specifics](#7-java-specifics) | `break` scope, deque orientation, `split` | 4 |
| A | [Corrections applied in this pass](#appendix-a-corrections-applied-in-this-pass) | Every claim that was wrong, and what it is now | 11 |

## Table of Contents

1. [Arrays, prefix sums and cyclic sort](#1-arrays-prefix-sums-and-cyclic-sort)
2. [Sliding window](#2-sliding-window)
3. [Stacks](#3-stacks)
4. [Binary trees](#4-binary-trees)
5. [Recursion and dynamic programming](#5-recursion-and-dynamic-programming)
6. [Math and formulas](#6-math-and-formulas)
7. [Java specifics](#7-java-specifics)
- [Appendix A: corrections applied in this pass](#appendix-a-corrections-applied-in-this-pass)

Companion file: [`DSA_Memory_Keypoints.md`](DSA_Memory_Keypoints.md).

---

## 1. Arrays, prefix sums and cyclic sort

### [5] Find all disappeared numbers (sign marking, not cyclic sort)

Practice file: [`../01-Arrays/B10_FindAllMissedNumbers.java`](../01-Arrays/B10_FindAllMissedNumbers.java)

**Verified.** `[4,3,2,7,8,2,3,1]` -> `[5, 6]`.

**Correction — the `// Cyclic Sort` label was wrong.** This algorithm never swaps anything and never sorts. It marks "value `v` was seen" by flipping the sign of the element at index `v - 1`, then reads the still-positive slots as the missing numbers. Cyclic sort (item [6] below, and item [-33] in the companion file) is the *other* O(n)/O(1) technique for this family; they are worth keeping distinct because sign marking leaves the array order intact while cyclic sort destroys it.

```java
// Sign marking: use the array itself as the "seen" set
public static List<Integer> findDisappearedNumbers(int[] nums) {
    int n = nums.length;
    ArrayList<Integer> list = new ArrayList<>();
    // use Math.abs on the value, because earlier steps may
    // already have flipped this slot's sign
    for (int i = 0; i < n; i++) {
        // Get the index for the number nums[i]
        int correctIndex = Math.abs(nums[i]) - 1;
        if (nums[correctIndex] > 0) {
            nums[correctIndex] = -nums[correctIndex];
        }
    }
    for (int j = 0; j < n; j++) {
        // If the number is still positive,
        // it means the number (j+1) is missing
        if (nums[j] > 0) {
            list.add(j + 1);
        }
    }
    return list;
}
```

**Correction — the comment "use a for-i loop not a for-each loop, won't work" is false.** A for-each version was written and run side by side with this one and returned the identical `[5, 6]`. It works because `Math.abs` undoes any sign flip an earlier step applied, and a for-each reads the same elements in the same order that `nums[i]` would. What genuinely breaks the algorithm is **dropping `Math.abs`** — and that breaks the indexed loop just as badly as the for-each. The `Math.abs` is the load-bearing part, not the loop form.

The `if (nums[correctIndex] > 0)` guard matters for a different reason: a value that appears twice would otherwise be flipped back to positive on its second visit and reported as missing.

Requires every value in `[1, n]`. O(n) time, O(1) extra space (the output list does not count).

### [6] Find all duplicates (cyclic sort)

Practice file: [`../01-Arrays/C14_FindAllDuplicatesInAnArray.java`](../01-Arrays/C14_FindAllDuplicatesInAnArray.java)

**Verified.** `[4,3,2,7,8,2,3,1]` -> `[3, 2]` (order depends on final positions, not on input order).

```java
public static List<Integer> findDuplicates(int[] nums) {
    List<Integer> duplicates = new ArrayList<>();
    int i = 0;
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;
        if (nums[correctIndex] == nums[i])
            i++;                            // this slot is settled, move on
        else
            swap(nums, correctIndex, i);    // send the value home, retry index i
    }
    for (int j = 0; j < nums.length; j++) {
        if (nums[j] != j + 1)
            duplicates.add(nums[j]);
    }
    return duplicates;
}
```

Note the loop does **not** advance `i` after a swap — the value that just arrived at index `i` has not been placed yet. It still terminates in O(n) because every swap puts one value on its permanent index.

Compare by **value** (`nums[correctIndex] == nums[i]`), not by index. With duplicates present, an index test swaps two equal values back and forth forever.

If the result order must be ascending, sort the output or use the sign-marking variant from [5], which emits in index order.

### [7] Count subarrays with sum equal to K (prefix-sum map)

Practice file: [`../05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java`](../05-Hashing-Prefix-Sum/C01_CountSubarraySumEqualsK.java)

**Verified.** `[1,2,3], k=3` -> `2`; `[1,1,1], k=2` -> `2`; `[0,0,0], k=0` -> `6`; `[1,-1,0], k=0` -> `3`.

```java
// C01_CountSubarraySumEqualsK
public static int subarraySum(int[] nums, int k) {
    int count = 0;
    Map<Integer, Integer> preSumCountMap = new HashMap<>();
    int preSum = 0;
    for (int i = 0; i < nums.length; i++) {
        preSum += nums[i];
        if (preSum == k) count++;     // the subarray that starts at index 0
        int remSum = preSum - k;
        if (preSumCountMap.containsKey(remSum)) {
            // add the COUNT stored for remSum, not 1:
            // remSum may have occurred many times
            count += preSumCountMap.get(remSum);
        }
        // store preSum, never remSum
        preSumCountMap.put(preSum, preSumCountMap.getOrDefault(preSum, 0) + 1);
    }
    return count;
}
```

Same entry as item [-13] in the companion file, which carries the full explanation. Two reminders:

- Handle the "starts at index 0" case **once** — either with the `if (preSum == k)` line above, or by seeding the map with `put(0, 1)`. Doing both double-counts.
- A `HashMap` is needed rather than a sliding window only because `nums` may contain negatives, so the prefix sum is not monotonic. Make `preSum` a `long` when `n * max|nums[i]|` can pass 2^31 - 1.

---

## 2. Sliding window

### [3] Longest repeating character replacement

Practice file: [`../02-Two-Pointers-Sliding-Window/C07_CharacterReplacement.java`](../02-Two-Pointers-Sliding-Window/C07_CharacterReplacement.java)

**Verified.** `"ABAB", k=2` -> `4`; `"AABABBA", k=1` -> `4`; `"ABBB", k=2` -> `4`.

```java
public static int characterReplacement(String s, int k) {
    int maxLength = 0;
    int n = s.length();
    int[] frequencyMap = new int[26];       // uppercase A-Z only
    int left = 0, right = 0, maxfreq = 0;
    while (right < n) {
        char currentChar = s.charAt(right);
        if (maxfreq < ++frequencyMap[currentChar - 'A'])
            maxfreq = frequencyMap[currentChar - 'A'];

        int currentWindowLength = right - left + 1;
        // the window is valid when the characters we would have to
        // replace - everything that is not the most frequent character -
        // number at most k
        if (currentWindowLength - maxfreq <= k) {
            maxLength = Math.max(maxLength, currentWindowLength);
        } else {
            // not valid: slide left forward by one
            frequencyMap[s.charAt(left++) - 'A']--;
        }
        right++;
    }
    return maxLength;
}
```

Three things that look like bugs and are not:

| Looks wrong | Why it is fine |
|---|---|
| `maxfreq` is never decreased when `left` advances | the window only ever grows or slides, so a stale `maxfreq` can only make the window look *invalid*, never falsely valid — the recorded maximum stays correct |
| The window never actually shrinks | on an invalid step `left` and `right` both advance, so the size holds; the best size already seen is what is reported |
| `maxLength` is only updated in the valid branch | the invalid branch returns to a size already recorded, so nothing is lost |

`currentWindowLength - maxfreq` is the number of characters in the window that are not the most frequent one, i.e. exactly how many replacements this window would cost.

The `new int[26]` with `- 'A'` restricts this to **uppercase A-Z** (which LeetCode 424 guarantees). For mixed case use `new int[128]` indexed by the raw `char`, or a `HashMap`.

O(n) time, O(1) space.

---

## 3. Stacks

### [8] Largest rectangle in a histogram (monotonic stack)

Practice file: [`../07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java`](../07-Stack-Queue-Monotonic/D02_LargestRectangleArea.java)

**Verified.** `[2,1,5,6,2,3]` -> `10`; `[2,4]` -> `4`.

```java
// D02_LargestRectangleArea
public static void main(String[] args) {
    int[] arr = {2, 1, 5, 6, 2, 3};
    Deque<Integer> stack = new ArrayDeque<>();   // holds INDICES, increasing by height
    int maxArea = 0;
    int n = arr.length;

    for (int i = 0; i <= n; i++) {               // note <= n: one extra sentinel pass
        while (!stack.isEmpty() && (i == n || arr[stack.peek()] >= arr[i])) {
            int height = arr[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, width * height);
        }
        stack.push(i);
    }
    System.out.println(maxArea);
}
```

The source note used `java.util.Stack`; `ArrayDeque` is the faster choice and is the one to reach for by default (see item [43] in the companion file for why `Stack` is legacy). Behaviour is identical here.

The three parts that carry the algorithm:

1. **`i <= n` with the `i == n` short-circuit.** The extra iteration flushes everything still on the stack, so bars that are never popped by a shorter neighbour still get measured. `i == n` is written **first** in the `||` so `arr[i]` is never evaluated out of bounds.
2. **`width = i - stack.peek() - 1`.** After popping, the new stack top is the nearest **strictly smaller** bar on the left and `i` is the nearest smaller-or-equal bar on the right, so the popped bar extends over everything strictly between them. When the stack empties, the bar reaches all the way back to index 0, so the width is just `i`.
3. **`>=` rather than `>`.** Popping equal heights is safe: the earlier bar of an equal pair computes a width that is too small, but the last bar of that group computes the full span, and only the maximum is kept.

The stack holds indices, not heights, because the width calculation needs positions. O(n) time — each index is pushed once and popped once — and O(n) space.

---

## 4. Binary trees

### [1] Build a minimum-height BST from a sorted array

Practice file: [`../09-Trees-BST/B15_SortedArrayToBST.java`](../09-Trees-BST/B15_SortedArrayToBST.java)

**Verified.** Built from `[1..7]`, an in-order walk returns `[1, 2, 3, 4, 5, 6, 7]`.

```java
// Recursive method to build the minimum-height BST
private TreeNode buildBST(int[] sortedArray, int start, int end) {
    if (start > end) return null;

    int mid = start + (end - start) / 2;  // overflow-safe; choose the middle as root
    TreeNode node = new TreeNode(sortedArray[mid]);

    // Recursively build the left and right subtrees
    node.left = buildBST(sortedArray, start, mid - 1);
    node.right = buildBST(sortedArray, mid + 1, end);
    return node;
}
```

`(start + end) / 2` as the source note had it is correct for any realistic array and only overflows past ~1.07 billion elements — `start + (end - start) / 2` costs nothing and is what an interviewer listens for.

Taking the middle element as the root is what forces the minimum height: each side gets half the remaining values, so the height is `ceil(log2(n+1))`. Any other pivot makes one subtree deeper. Call it as `buildBST(a, 0, a.length - 1)`; the `start > end` base case handles both the empty array and the empty-subtree case. O(n) time, O(log n) stack.

### [2] Binary tree maximum path sum

Practice file: [`../09-Trees-BST/D01_BinaryTreeMaxPathSum.java`](../09-Trees-BST/D01_BinaryTreeMaxPathSum.java)

**Verified**, including the all-negative case that exposes the seeding trap below.

```java
private int calculateMaxPathSum(TreeNode node) {
    if (node == null) return 0;

    // a negative branch contributes nothing: clamp it to 0 instead of using it
    int leftSum = Math.max(calculateMaxPathSum(node.left), 0);
    int rightSum = Math.max(calculateMaxPathSum(node.right), 0);

    // the best path THROUGH this node, using both children
    int currentMax = node.val + leftSum + rightSum;
    maxSum = Math.max(maxSum, currentMax);

    // what this node can offer its PARENT: one branch only
    return node.val + Math.max(leftSum, rightSum);
}
```

The one idea to hold on to: **the value recorded and the value returned are different.** A path through a node may use both children, but a path handed upward can only continue through one of them — a parent cannot join two branches of its child without creating a fork.

`maxSum` must be seeded with `Integer.MIN_VALUE`, not `0`. **Verified:** on a single node holding `-3`, a `MIN_VALUE` seed returns `-3` (correct — the path must contain at least one node), while a `0` seed returns `0`, which is not a path in the tree at all.

Note that clamping the children to 0 is not the same as allowing an empty path — `node.val` is always included, so an all-negative tree still returns its least-negative node. O(n) time, O(h) stack.

### [9] Diameter of a binary tree

Practice file: [`../09-Trees-BST/B09_DiameterOfBinaryTree.java`](../09-Trees-BST/B09_DiameterOfBinaryTree.java)

**Verified.** On a 6-node complete tree this returns `4` edges (the path `4 -> 2 -> 1 -> 3 -> 6`).

```java
private int findDia(Node root) {
    if (root == null) return 0;

    int leftDepth = findDia(root.left);
    int rightDepth = findDia(root.right);
    int currDia = leftDepth + rightDepth;   // edges through this node
    maxDia = Math.max(maxDia, currDia);

    return 1 + Math.max(leftDepth, rightDepth);   // depth handed to the parent
}
```

Same record-versus-return split as item [2]: the method is **named** `findDia` but what it **returns** is the depth; the diameter only ever reaches the caller through the `maxDia` field. Renaming it `depth` in your own code removes a real source of confusion.

The unit is **edges**, which is what LeetCode 543 asks for: `leftDepth + rightDepth` with no `+ 1`. If a problem wants the node count instead, add 1. A single node has diameter 0.

O(n) — one post-order pass, not a depth computation per node, which would be O(n^2).

---

## 5. Recursion and dynamic programming

### [4] Count ways to make coin change (memoized)

Practice file: [`../12-Dynamic-Programming/C09_CoinChangeII.java`](../12-Dynamic-Programming/C09_CoinChangeII.java)

**Correction — the source snippet did not compile.** The method is declared `countWaysToMakeCoinChangeUtil` but all three recursive calls named `countWaysToMakeChangeUtil` (no "Coin"), which is an unresolved symbol. Names unified below.

**Verified** after the fix: `coins=[1,2,5], amount=5` -> `4`; `coins=[2], amount=3` -> `0`; `coins=[1,2,3], amount=4` -> `4`.

```java
static long countWaysToMakeCoinChangeUtil(int[] arr, int ind, int T, long[][] dp) {
    // Base case: If the current index is 0
    if (ind == 0) {
        // If T is divisible by the first element of the array, return 1, else return 0
        if (T % arr[0] == 0)
            return 1;
        else
            return 0;
    }

    // If the result for this subproblem has already been calculated, return it
    if (dp[ind][T] != -1)
        return dp[ind][T];

    // Calculate the number of ways without taking the current element
    long notTaken = countWaysToMakeCoinChangeUtil(arr, ind - 1, T, dp);

    // Initialize the number of ways taking the current element as 0
    long taken = 0;

    // If the current element is less than or equal to T, calculate 'taken'
    if (arr[ind] <= T)
        taken = countWaysToMakeCoinChangeUtil(arr, ind, T - arr[ind], dp);

    // Store the result in the dp array and return it
    return dp[ind][T] = notTaken + taken;
}
```

The caller has to do two things the snippet does not show, and both are common omissions:

```java
long[][] dp = new long[coins.length][amount + 1];
for (long[] row : dp) Arrays.fill(row, -1);              // -1 means "not computed"
return countWaysToMakeCoinChangeUtil(coins, coins.length - 1, amount, dp);
```

| Detail | Why it matters |
|---|---|
| `taken` recurses with **`ind`**, not `ind - 1` | coins are unbounded — the same coin may be reused |
| `notTaken` recurses with `ind - 1` | this coin is retired, so combinations are counted once and permutations are not |
| dp filled with `-1` | `0` is a legitimate answer, so it cannot double as "not computed" |
| `arr[0] != 0` | a zero coin would divide by zero in the base case |
| `long` return type | the count grows past `int` quickly |

Counting **combinations** (this code) versus **permutations** is decided entirely by loop/recursion order: retiring the coin index before moving on is what stops `1+2` and `2+1` being counted twice.

O(n * amount) time and space.

### [10] Recursive factorial

Practice file: [`../14-Backtracking-Recursion/A01_RecursiveFactorial.java`](../14-Backtracking-Recursion/A01_RecursiveFactorial.java)

**Verified.** `factorial(20)` -> `2432902008176640000`, which is exact.

```java
class RecursiveFactorial {
    public long factorial(int n) {
        // Base case: factorial of 0 or 1 is 1
        if (n <= 1) return 1;
        // Recursive case: n * factorial of n-1
        return n * factorial(n - 1);
    }
}
```

The multiplication is safe from a subtler bug than it looks: `n` is an `int` and `factorial(n - 1)` is a `long`, so binary numeric promotion widens `n` to `long` and the arithmetic happens in 64 bits. No intermediate `int` truncation.

What it does **not** survive is the range of `long` itself:

| n | Result | Status |
|---|---|---|
| 20 | 2432902008176640000 | exact, and the largest that fits |
| 21 | -4249290049419214848 | **silently wrong** — wrapped past `Long.MAX_VALUE` |
| negative | 1 | wrong; `n <= 1` swallows it, so validate or throw |

Above 20 you need `BigInteger`, or the problem wants the answer modulo something. Java has no tail-call optimisation, so recursion depth is bounded by the stack — irrelevant at n <= 20, but worth saying if asked to generalise.

---

## 6. Math and formulas

### [-4] Ceiling of half

```text
(m + n + 1) / 2  ==  (m + n) / 2 + (m + n) % 2
```

Both spell `ceil((m + n) / 2)` in integer arithmetic. True for non-negative values; for negatives, Java's `/` truncates toward zero and `%` keeps the sign of the dividend, so neither form is a ceiling any more.

This is the expression behind `halfLength` in "median of two sorted arrays" (item [-28] of the companion file) — the `+ 1` is what puts the extra element on the **left** side for odd totals, which is why the odd case reads the answer from the left partition alone.

### [-2] Incremental (running) average

Practice file: [`../19-Design-Data-Structures/C09_DesignAFoodRatingSystem.java`](../19-Design-Data-Structures/C09_DesignAFoodRatingSystem.java)

```java
public class RatingAverage {
    private long count = 0;
    private double average = 0.0;

    public void addRating(int rating) {
        count++;
        average += (rating - average) / count; // Incremental update formula
    }

    public double getAverage() {
        return average;
    }
}
```

Why it works:

```text
newAverage = oldAverage + (newValue - oldAverage) / newCount
```

`count++` must come **before** the update — the divisor is the count *including* the new value. Swapping those two lines divides by one too few and inflates every average.

Why prefer it over keeping a running sum: the sum can overflow (or, for doubles, lose precision) after enough values, while the incremental form keeps the magnitude bounded by the data itself. `(rating - average)` is a `double` because `average` is, so the division is floating point even though `count` is a `long` — no integer-division trap here.

### [0] GCD and LCM

Practice file: [`../17-Math-Bit-Manipulation/A09_LCMOfTwoNumbers.java`](../17-Math-Bit-Manipulation/A09_LCMOfTwoNumbers.java)

```java
int gcd = GCD(n1, n2);
int lcm = n1 / gcd * n2;      // NOT (n1 * n2) / gcd
```

`(n1 * n2) / gcd` overflows `int` once the product passes 2^31 - 1 — around `46341 * 46341`. Dividing first cannot overflow and cannot lose anything, because the gcd divides `n1` exactly by definition.

`gcd(a, 0) == a`, so `lcm(0, x)` would divide by zero once both arguments are 0; special-case it. See item [8] in the companion file for the two GCD implementations.

---

## 7. Java specifics

### [-3] break only exits the inner loop

**Verified.** Output: `i=1,j=1  i=1,j=2  i=2,j=1  i=2,j=2  i=3,j=1  i=3,j=2  Done`

```java
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 3; j++) {
        System.out.println("i = " + i + ", j = " + j);
        if (j == 2) break;  // breaks only inner loop
    }
}
System.out.println("Done");
```

The outer loop runs all three times; only the inner one is cut short, so six lines print, not two.

| Need | Write |
|---|---|
| Leave the inner loop | `break` |
| Leave both loops | label the outer loop and `break outer;` |
| Skip to the next outer iteration | `continue outer;` |
| Leave the method entirely | `return` |

```java
outer:
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 3; j++) {
        if (j == 2) break outer;   // now only i=1,j=1 prints
    }
}
```

The label goes immediately before the loop it names, and `break label` jumps to just **after** that loop — it is not a `goto`.

### [-1] Deque orientation

In a Deque (short for Double-Ended Queue):

- first (or "front") -> Left side
- last (or "rear" / "back") -> Right side

So if you visualize the deque like this:

```text
[ front | ... | back ]
[ first | ... | last ]
[ left  | ... | right ]
```

The method names follow the same left/right picture:

| Operation | Front (left) | Back (right) |
|---|---|---|
| add | `addFirst` / `offerFirst` / `push` | `addLast` / `offerLast` / `offer` / `add` |
| remove | `removeFirst` / `pollFirst` / `pop` | `removeLast` / `pollLast` |
| inspect | `peekFirst` / `peek` | `peekLast` |

Two traps worth memorising, because they cause silent wrong answers rather than errors:

- The unqualified methods split sides: `add`/`offer`/`peek`/`poll` all act on the **back for adding** and the **front for removing** (queue semantics), while `push`/`pop` both act on the **front** (stack semantics). Mixing `push` with `poll` gives you a queue, not a stack.
- `ArrayDeque` iterates **front to back**, i.e. in pop order when used as a stack — the opposite of `java.util.Stack`, which iterates in insertion order (item [43] of the companion file).

`ArrayDeque` rejects `null` elements outright; `LinkedList` accepts them, which is why a `null` sentinel silently works with one and throws with the other.

### [11] String.split drops trailing empty strings

**Verified**, including the fix.

```java
String str = "null,";
String[] split = str.split(",");
System.out.println(Arrays.toString(split));       // [null]      length 1
System.out.println(Arrays.toString(str.split(",", -1)));  // [null, ]   length 2
System.out.println(Arrays.toString("a,,b,,".split(",")));  // [a, , b]  length 3
```

Two separate things are going on in that first line, and they are easy to conflate:

1. **The trailing empty string is gone.** `split(regex)` uses a limit of 0, which discards *all* trailing empty strings. `split(regex, -1)` keeps every one of them. Note that **interior** empties are always kept — `"a,,b,,"` keeps the gap between `a` and `b` and drops only the two at the end.
2. **`[null]` is the printed form of the string `"null"`,** not a null array element. The array holds one non-null `String` whose content happens to be the four characters `n-u-l-l`. Nothing here is actually null.

This matters any time you parse a fixed-width CSV row: `"a,b,"` gives 2 fields with the default and 3 with `-1`, and code that indexes the last field will throw on the default. Reach for `split(regex, -1)` whenever the field count is supposed to be fixed.

`split` takes a **regex**, so a literal `.` or `|` must be escaped (`split("\\.")`, `split("\\|")`). See [`../04-Strings/A01_DelimiterSplit.java`](../04-Strings/A01_DelimiterSplit.java).

### [12] (empty in the source)

Item `12)` exists as a marker in the source file but has no content. Kept so the numbering stays traceable.

---

## Appendix A: corrections applied in this pass

| Item | What it said | What is true |
|---|---|---|
| [5] | labelled `// Cyclic Sort` | it is **sign marking** — nothing is swapped or sorted |
| [5] | "use a for-i loop not a for-each loop, won't work" | a for-each returns the identical result (verified); `Math.abs` is what carries it, not the loop form |
| [4] | calls `countWaysToMakeChangeUtil` | does not compile — the method is `countWaysToMakeCoinChangeUtil` |
| [4] | dp usage shown without setup | dp must be pre-filled with `-1`, since `0` is a real answer |
| [2] | `maxSum` seeding not stated | must be `Integer.MIN_VALUE`; a `0` seed returns 0 for an all-negative tree (verified) |
| [10] | no range given | exact only to `n = 20`; `factorial(21)` silently wraps negative (verified) |
| [0] | `lcm = (n1 * n2) / gcd` | overflows `int`; use `n1 / gcd * n2` |
| [9] | method named `findDia` | it returns the **depth**; the diameter escapes via the `maxDia` field |
| [1] | `mid = (start + end) / 2` | fine in practice, but `start + (end - start) / 2` is the overflow-safe form |
| [8] | used `java.util.Stack` | `ArrayDeque` is the faster, non-legacy choice (identical behaviour here) |
| [11] | `// [null]` unexplained | two separate facts: trailing empties are dropped, and `[null]` is the *string* `"null"` |
