# DSA Memory Keypoints II

A second grouped set of DSA keypoints and code snippets converted from `DSA_Memory_II.txt`; the original item numbers (for example `[-4]` or `[7]`) are kept in every heading so each entry can be traced back to the source notes.

---

## 1. Arrays, prefix sums and cyclic sort

### [5] Find all disappeared numbers (cyclic sort, negation marking)

```java
// Cyclic Sort
public static List<Integer> findDisappearedNumbers(int[] nums) {
    int n = nums.length;
    ArrayList<Integer> list = new ArrayList<>();
    // use fori loop not for each loop, won't work
    // use currVal to get currVal-1 as correctIdx
    // and make value at correctIdx as negative
    for (int i = 0; i < n; i++) {
        // Get the index for the number nums[i]
        int correctIndex = Math.abs(nums[i]) - 1;
        if (nums[correctIndex] > 0) {
            nums[correctIndex] = -nums[correctIndex];
        }
    }
    for (int j = 0; j < n; j++) {
        // If the number is still positive,
        // it means the number (i+1) is missing
        if (nums[j] > 0) {
            list.add(j + 1);
        }
    }
    return list;
}
```

### [6] Find all duplicates (cyclic sort)

```java
public static List<Integer> findDuplicates(int[] nums) {
    List<Integer> duplicates = new ArrayList<>();
    int i = 0;
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;
        if (nums[correctIndex] == nums[i])
            i++;
        else
            swap(nums, correctIndex, i);
    }
    for (int j = 0; j < nums.length; j++) {
        if (nums[j] != j + 1)
            duplicates.add(nums[j]);
    }
    return duplicates;
}
```

### [7] Count subarrays with sum equal to K (prefix-sum map)

```java
// ImportantCountSubarraySumEqualsK
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
        // update count of preSum in map
        preSumCountMap.put(preSum, preSumCountMap.getOrDefault(preSum, 0) + 1);
    }
    return count;
}
```

---

## 2. Sliding window

### [3] Longest repeating character replacement

```java
public static int characterReplacement(String s, int k) {
    int maxLength = 0;
    int n = s.length();
    int[] frequencyMap = new int[26];
    int left = 0, right = 0, maxfreq = 0;
    while (right < n) {
        char currentChar = s.charAt(right);
        if (maxfreq < ++frequencyMap[currentChar - 'A'])
            maxfreq = frequencyMap[currentChar - 'A'];

        int currentWindowLength = right - left + 1;
        // remember currentWindowLength - maxfreq <= k
        // if currChar is maxFrequency char from currWindowLen-maxFreq <= k
        // means we can replace complement value with max freq char as count <= k
        // so by doing this we may get maxLength
        if (currentWindowLength - maxfreq <= k) {
            maxLength = Math.max(maxLength, currentWindowLength);
        } else {
            // If the window is not valid, shrink the window by moving left
            frequencyMap[s.charAt(left++) - 'A']--;
        }
        right++;
    }
    return maxLength;
}
```

---

## 3. Stacks

### [8] Largest rectangle in a histogram (monotonic stack)

```java
// ImportantLargestRectangleArea
public static void main(String[] args) {
    int[] arr = {2, 1, 5, 6, 2, 3};
    Stack<Integer> stack = new Stack<>();
    int maxArea = 0;
    int n = arr.length;

    for (int i = 0; i <= n; i++) {
        while (!stack.isEmpty() && (i == n || arr[stack.peek()] >= arr[i])) {
            int height = arr[stack.pop()];
            int width = stack.empty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, width * height);
        }
        stack.push(i);
    }
    System.out.println(maxArea);
}
```

---

## 4. Binary trees

### [1] Build a minimum-height BST from a sorted array

```java
// Recursive method to build the MinimumHeightBST
private TreeNode buildBST(int[] sortedArray, int start, int end) {
    if (start > end) return null;

    int mid = (start + end) / 2; // Choose the middle element as root
    TreeNode node = new TreeNode(sortedArray[mid]);

    // Recursively build the left and right subtrees
    node.left = buildBST(sortedArray, start, mid - 1);
    node.right = buildBST(sortedArray, mid + 1, end);
    return node;
}
```

### [2] Binary tree maximum path sum

```java
private int calculateMaxPathSum(TreeNode node) {
    if (node == null) return 0;

    int leftSum = Math.max(calculateMaxPathSum(node.left), 0);
    int rightSum = Math.max(calculateMaxPathSum(node.right), 0);

    int currentMax = node.val + leftSum + rightSum;
    maxSum = Math.max(maxSum, currentMax);
    return node.val + Math.max(leftSum, rightSum);
}
```

### [9] Diameter of a binary tree

```java
private int findDia(Node root) {
    if (root == null) return 0;

    int leftDepth = findDia(root.left);
    int rightDepth = findDia(root.right);
    int currDia = leftDepth + rightDepth;
    maxDia = Math.max(maxDia, currDia);

    return 1 + Math.max(leftDepth, rightDepth);
}
```

---

## 5. Recursion and dynamic programming

### [4] Count ways to make coin change (memoized)

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
    long notTaken = countWaysToMakeChangeUtil(arr, ind - 1, T, dp);

    // Initialize the number of ways taking the current element as 0
    long taken = 0;

    // If the current element is less than or equal to T, calculate 'taken'
    if (arr[ind] <= T)
        taken = countWaysToMakeChangeUtil(arr, ind, T - arr[ind], dp);

    // Store the result in the dp array and return it
    return dp[ind][T] = notTaken + taken;
}
```

### [10] Recursive factorial

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

---

## 6. Math and formulas

### [-4] Ceiling of half

```text
(m + n + 1) / 2 == (m + n) / 2 + (m + n) % 2;
```

### [-2] Incremental (running) average

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

### [0] GCD and LCM

```java
int gcd = GCD(n1, n2);
int lcm = (n1 * n2) / gcd;
```

---

## 7. Java specifics

### [-3] break only exits the inner loop

```java
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 3; j++) {
        System.out.println("i = " + i + ", j = " + j);
        if (j == 2) break;  // breaks only inner loop
    }
}
System.out.println("Done");
```

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

### [11] String.split drops trailing empty strings

```java
String str = "null,";
String[] split = str.split(",");
System.out.println(Arrays.toString(split)); // [null]
```

### [12] (empty in the source)

Item `12)` exists as a marker in the source file but has no content.
