# 07-Stack-Queue-Monotonic — must-know order

**Techniques in this topic:** Stack as a matcher: push openers, pop on a matching closer, empty at the end (bracket family), Balance counter as the O(1)-space replacement for a stack when only one bracket type exists, Monotonic stack template: hold indices whose answer is still pending, resolve them when the current element breaks the invariant, Next-greater / next-smaller as the single primitive behind temperatures, spans, visibility and rectangle widths, Span + contribution counting: prev-smaller and next-smaller bound the subarrays where an element dominates, Monotonic stack as greedy construction under a removal budget (lexicographically smallest result), Stack as a simulation engine: collisions, robots, directional survivors, Nested-state parsing: push the enclosing context on '(' or '[', restore and merge on the closer, Operator precedence and expression evaluation (RPN, shunting-yard, sign-and-paren calculators), Monotonic deque: the two-ended upgrade that adds window eviction to the monotonic stack, Amortised O(1) reasoning: every element is pushed and popped at most once

| | |
|---|---|
| Problems | 29 |
| Must-know | 8 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_ValidParentheses.java` | Stack as bracket matcher |
| 2 | `A04_ImportantNextGreaterElements.java` | Monotonic stack template |
| 3 | `C01_DailyTemperatures.java` | Next greater, index distance |
| 4 | `C06_ImportantAsteroidCollision.java` | Stack collision simulation |
| 5 | `C09_DecodeString.java` | Twin stacks for nested state |
| 6 | `D02_ImportantLargestRectangleArea.java` | Monotonic stack, width from neighbours |
| 7 | `D04_ImportantSlidingWindowMaximum.java` | Monotonic deque with eviction |
| 8 | `D05_BasicCalculator.java` | Sign and result stack parsing |

## Full practice order


### A — Building blocks

- `A01_StackTraversal.java` — Java Stack iteration order gotcha
  - Starts here because java.util.Stack iterates bottom-to-top while pop() goes top-down — the bug that silently corrupts half the files below.
- `A02_ValidParentheses.java` — Stack as bracket matcher **[must-know]**
  - The canonical push-opener/pop-on-match primitive; every parenthesis, path, decode and calculator problem in this folder assumes it.
- `A03_IsValidParentheses.java` — Balance counter, O(1) space
  - Immediately after the stack version, so he learns when a single counter replaces it — the insight that later powers the O(1) longest-valid-parentheses pass.
- `A04_ImportantNextGreaterElements.java` — Monotonic stack template **[must-know]**
  - The one template that unlocks the entire monotonic half of the folder: hold pending indices, resolve them when a bigger element arrives, flush leftovers as -1.
- `A05_NextGreaterElements.java` — Monotonic stack, annotated repeat
  - Same algorithm as the previous file with the complexity write-up attached; use it as the spaced-repetition re-derivation, not as new material.
- `A06_QueueUsingStacks.java` — Two-stack FIFO, amortized O(1)
  - Closes the primitives tier with the amortised-cost argument he must articulate aloud, and sets up the deque thinking used in sliding-window maximum.

### B — Easy

- `B01_RemoveAdjacentDuplicates.java` — Stack collapse on equal top
  - Simplest possible compare-with-top-then-push-or-pop loop; the shape every stack-simulation problem later reuses.
- `B02_RemovingStarsFromAString.java` — In-place array as stack
  - Same collapse as the previous file but with a write-pointer instead of a Stack object — teaches that a stack is often just an index.
- `B03_MinimumAddToMakeParenthesesValid.java` — Two-sided balance counter
  - Extends the IsValidParentheses counter to track unmatched openers and closers separately; the natural second step before the hard parenthesis problems.
- `B04_EvalRPN.java` — Stack as operand accumulator
  - Introduces the stack holding values rather than symbols, plus the operand-order trap on '-' and '/'; groundwork for all expression parsing.
- `B05_EvaluateReversePolishNotation.java` — RPN with arrow-switch
  - Same problem as the previous file rewritten with Java switch expressions — a Java-fluency rep, not a new algorithm.
- `B06_StackSortable.java` — Greedy stack simulation
  - First problem where the stack models a process rather than pairing symbols, which is exactly what the collision problems in C demand.
- `B07_BuildingsWithOceanView.java` — Right-to-left running maximum
  - Ends the easy tier as the bridge into monotonic work: the degenerate case where a strictly decreasing stack collapses to one variable.

### C — Medium

- `C01_DailyTemperatures.java` — Next greater, index distance **[must-know]**
  - The purest application of the A-tier template and the single most-asked monotonic-stack question; storing indices instead of values is the whole lesson.
- `C02_VisiblePeopleInQueue.java` — Monotonic stack with counting
  - Runs the same decreasing stack right-to-left but counts pops, adding the subtle +1 for the one taller person still visible past them.
- `C03_RemoveKdigits.java` — Monotonic greedy under a budget
  - Reframes the monotonic stack as a greedy construction with a removal quota — the unlock for remove-duplicate-letters and create-maximum-number.
- `C04_CarFleet.java` — Sort, then monotonic on time
  - Teaches the transform step: sort by position, convert to arrival time, and only then the monotonic comparison becomes obvious.
- `C05_SumOfSubarrayMinimums.java` — Prev/next smaller, contribution count
  - The capstone medium: combines both monotonic directions into span counting, and the strict-vs-non-strict tie-break that stops double counting.
- `C06_ImportantAsteroidCollision.java` — Stack collision simulation **[must-know]**
  - Opens the simulation thread and appears constantly; the tricky part is the three-way outcome inside the while loop and the destroyed-flag exit.
- `C07_RobotCollisions.java` — Collision with state and reindexing
  - Directly generalises the asteroid loop by adding health, sorting by position, and restoring original index order at the end.
- `C08_SimplifyPath.java` — Token stack for path resolution
  - Moves from characters to tokens and is really an edge-case exercise — empty segments, '.', '..' at root — which is why interviewers still use it.
- `C09_DecodeString.java` — Twin stacks for nested state **[must-know]**
  - The reference model for saving and restoring enclosing context on a bracket; once this clicks, the calculator and time-accounting problems follow.
- `C10_ExclusiveTimeOfFunctions.java` — Call-frame stack with time deltas
  - Applies the nested-context idea to intervals; the prevTime bookkeeping and the +1 on end are the two places everyone loses the answer.
- `C11_InfixToPrefix.java` — Precedence stack, shunting-yard
  - Last medium because it formalises operator precedence and associativity, which is the machinery BasicCalculator improvises by hand.

### D — Hard

- `D01_ImportantLongestValidParentheses.java` — Index sentinel stack, two-pass counter
  - Entry to hard: the -1 sentinel plus i - stack.peek() introduces the distance-from-the-element-below arithmetic that the rectangle problem needs next.
- `D02_ImportantLargestRectangleArea.java` — Monotonic stack, width from neighbours **[must-know]**
  - The most reused hard technique in the folder — it drives maximal rectangle and trapping rain water — and the width formula is the exact arithmetic just learned.
- `D03_Find132pattern.java` — Right-to-left stack, popped maximum
  - Needs full monotonic fluency first: the insight is that the largest popped value is the best candidate for the '2', which no template gives you.
- `D04_ImportantSlidingWindowMaximum.java` — Monotonic deque with eviction **[must-know]**
  - Upgrades the stack to two ends by adding index-based window expiry; asked constantly and the natural sequel to the two-stack queue in tier A.
- `D05_BasicCalculator.java` — Sign and result stack parsing **[must-know]**
  - Final problem because it composes everything — nested context from DecodeString, precedence from InfixToPrefix, operand handling from RPN — into one parser.

## Interview readiness

This folder is unusually strong — close to the top decile of stack/monotonic prep I see, and well past the bar for a senior loop. The monotonic-stack core is fully built out (next-greater in both the linear and circular forms, daily temperatures, histogram, 132-pattern, span/contribution counting via subarray minimums, greedy removal under budget via RemoveKdigits), the simulation family is covered three ways (asteroids, robots, car fleet), nested-state parsing is covered (DecodeString, SimplifyPath, ExclusiveTime), and the deque upgrade is present via Sliding Window Maximum. Cross-folder checks show Trapping Rain Water sits in 02-Two-Pointers-Sliding-Window and MinStack plus Design-A-Stack-With-Increment sit in 19-Design-Data-Structures, so those are not real gaps. What is genuinely missing clusters into four pockets. First, the 2D lift of the histogram (Maximal Rectangle) — he has the primitive but not the problem that proves he can apply it, and that is the most common staff-level follow-up to a problem he already solved. Second, monotonic-greedy with a uniqueness constraint (Remove Duplicate Letters) — RemoveKdigits trained the budget variant, but the last-occurrence and already-in-stack guards are a different and harder invariant. Third, the deque beyond a single extreme: he has one deque for max, but not the paired min-and-max deques of LC 1438 or the prefix-sum deque of LC 862, which is where the "why does the sliding window break with negatives" conversation lives at staff level. Fourth, a small expression-evaluation hole — BasicCalculator here is LC 224 (parens, plus and minus only), so operator precedence inside an evaluator is untested even though InfixToPrefix shows he knows the precedence rules. Honourable mentions I left off the cap because they are one notch below: Valid Parenthesis String (678, wildcard open-count range), Score of Parentheses (856), Sum of Subarray Ranges (2104, a direct extension of SumOfSubarrayMinimums he can likely derive live) and Nested List Iterator (341). Close the four high items and this folder is interview-complete for any product company.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Remove Duplicate Letters / Smallest Subsequence of Distinct Characters (LC 316 / 1081) | Monotonic stack greedy with a last-occurrence guard and an in-stack set: pop a larger character only if it reappears later, skip characters already on the stack. | He has RemoveKdigits, which is the budget version of monotonic greedy (pop k times, no constraint on what you keep). LC 316 is the constrained version: you must keep exactly one of every distinct character, so the pop test changes from 'do I have budget left' to 'will this character appear again'. That is a different invariant, and it is the single most common monotonic-greedy question at Google and Amazon. Without it he will try to reuse the RemoveKdigits template and produce a wrong answer on input like 'cbacdcbc'. |
| high | Maximal Rectangle (LC 85) | Build a histogram of consecutive-ones heights row by row, then run Largest Rectangle in Histogram on each row's height array. | He already has ImportantLargestRectangleArea, and this is the standard staff-level extension interviewers reach for once a candidate solves the 1D version quickly — it is the same primitive applied to a matrix. The gap is not the stack code, it is recognising that the 2D problem collapses to m calls of the 1D one and maintaining the height array correctly (reset to 0 on a '0'). Having the primitive without ever having applied it is exactly the situation where a candidate stalls on the follow-up after nailing the main question. |
| high | Longest Continuous Subarray With Absolute Diff Less Than or Equal to Limit (LC 1438) | Sliding window with two monotonic deques run in parallel — one decreasing for the window max, one increasing for the window min — shrinking from the left while max minus min exceeds the limit. | ImportantSlidingWindowMaximum taught the single deque with a fixed window. This problem is the real interview form of the deque: the window is variable, and two deques must be evicted in sync from the left as the window shrinks. The tricky part is that the left pointer advances based on a condition, not a fixed size, so both deques must drop the front only when the front index falls out of the window. It is asked frequently at Amazon and Google, and a candidate who has only seen the fixed-window version usually reaches for a heap and lands on O(n log n) instead of O(n). |
| high | Minimum Remove to Make Valid Parentheses (LC 1249) | Stack of indices for unmatched openers plus a set of indices for unmatched closers, then rebuild the string skipping both; or the equivalent two-pass left-to-right and right-to-left scan. | MinimumAddToMakeParenthesesValid is the counting version — I read the file and it returns openCount plus closeCount, an int. LC 1249 asks for the resulting string, which forces index tracking and reconstruction, and it also has to leave non-bracket characters untouched. This is Meta's most frequently asked stack problem by a wide margin, and the counting solution does not transfer: he would have to invent the index-stack step live under time pressure. |
| medium | Basic Calculator II and III (LC 227 / 772) | Single-pass evaluation with a stack of terms: push for plus, push the negation for minus, and for multiply or divide pop the top and combine immediately, then sum the stack. LC 772 adds recursion or a context stack for parentheses. | The BasicCalculator.java in this folder is LC 224 — parentheses with only plus and minus, and a running sign variable, so operator precedence never appears. EvalRPN evaluates an expression where precedence has already been resolved, and InfixToPrefix converts rather than evaluates. So the one idiom he has not written is precedence handled inside an evaluator by lazy pop-and-combine. LC 227 is a very high-frequency Amazon and Meta question and LC 772 is the combined form interviewers escalate to. He has enough adjacent machinery to likely get there, which is why this is medium rather than high, but the clean single-pass form is worth drilling. |
| medium | Online Stock Span (LC 901) | Monotonic decreasing stack of (price, span) pairs in a streaming class API — pop all prices less than or equal to the current one, accumulating their spans into the answer. | DailyTemperatures and NextGreaterElements are both next-greater on a known array. Stock Span is previous-greater, arrives one element at a time, and collapses popped entries into an accumulated count so no index array exists to look back into. The count-merging step is the part candidates miss. It also doubles as a design question (the method signature is next(price)), which is how product companies like to wrap monotonic stacks so they do not look like a memorised LeetCode problem. |
| medium | Maximum Frequency Stack (LC 895) | HashMap from value to frequency plus a HashMap from frequency to a stack of values, tracking the current max frequency — push increments and appends, pop takes from the max-frequency stack and decrements. | This is the only serious stack-design gap left. MinStack and Design A Stack With Increment Operation are already in 19-Design-Data-Structures, so the auxiliary-stack idea is covered, but FreqStack requires the stack-of-stacks insight: keeping a separate stack per frequency level means the tie-break rule (most recently pushed among the most frequent) falls out for free. Asked regularly at Amazon and Bloomberg, and it is the standard way to test whether a candidate can invent a layered data structure rather than recall one. |
| medium | Shortest Subarray with Sum at Least K (LC 862) | Monotonic increasing deque over the prefix-sum array — pop from the front while the window qualifies, pop from the back while the new prefix is smaller than or equal to the tail. | This is the staff-level version of the deque, and its real value is the conversation it forces: why the ordinary sliding window works for MinSubArrayLen (which he has in folder 02) but breaks the moment negative numbers are allowed. The answer is that the window sum is no longer monotonic in the right pointer, so you need the deque on prefix sums instead. An interviewer probing depth will ask precisely this, and it is the one problem here that separates a strong senior answer from a staff one. Lower priority than the first four only because it is asked less often than they are. |

