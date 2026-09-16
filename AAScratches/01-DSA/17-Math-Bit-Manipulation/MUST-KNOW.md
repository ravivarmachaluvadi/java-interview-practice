# 17-Math-Bit-Manipulation — must-know order

**Techniques in this topic:** Bit primitives: mask-and-test (1<<i), popcount, and XOR self-cancellation as the three atoms every other bit problem is built from, Digit peeling in any base: n%10 / n/=10 for decimal, n&1 / n>>=1 for binary, and base-26 for Excel columns - the same loop wearing different clothes, Square-root bounded number theory: primality, divisor pairing (i, n/i), and trial-division prime factorization, Batch vs per-number primality: Sieve of Eratosthenes when you need all primes up to N, O(sqrt n) check when you need one, GCD by Euclid as a shared primitive: LCM = a*b/gcd, fraction reduction, and modular reasoning, Square-and-multiply (binary exponentiation), then its lift to 2x2 matrix exponentiation for O(log n) Fibonacci, Cycle detection in a numeric sequence: Floyd's tortoise-and-hare (Happy Number) vs a remainder->position hash map (repeating decimal), Combinatorics without factorials: Pascal's row from the previous row, then the incremental nCr recurrence val *= (n-k)/k, Modular/circular arithmetic on clocks and calendars: degrees per minute, minutes-since-midnight with 1440 wraparound, leap-year rules, Reformulating a counting problem until it becomes sortable: absolute values plus two pointers, or binary search on the answer, Repeated-division structure tests: is n a power of 3, 10, or an arbitrary base, Duplicate 'Important*' copies mark the versions he annotated and considers canonical - study those, skim the plain twins

| | |
|---|---|
| Problems | 38 |
| Must-know | 12 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_CheckIfTheIthBitIsSetOrNot.java` | Mask and test a bit |
| 2 | `A02_NumberOf1Bits.java` | Popcount by shift and test |
| 3 | `A03_SingleNumber.java` | XOR self-cancellation |
| 4 | `A05_CheckPrime.java` | Primality to sqrt(n) |
| 5 | `A06_AllDivisors.java` | Divisor pairing i and n/i |
| 6 | `A08_ImportantPrimeFactors.java` | Trial-division factorization |
| 7 | `A09_SieveOfEratosthenes.java` | Sieve of Eratosthenes |
| 8 | `A10_LCMOfTwoNumbers.java` | Euclid GCD, LCM identity |
| 9 | `A11_BinaryExponentiation.java` | Square and multiply |
| 10 | `B01_MissingNumber.java` | Sum formula or XOR |
| 11 | `C01_HappyNumber.java` | Floyd cycle detection on digits |
| 12 | `C05_ImportantPascalTrianleByMath.java` | Incremental nCr per row |

## Full practice order


### A — Building blocks

- `A01_CheckIfTheIthBitIsSetOrNot.java` — Mask and test a bit **[must-know]**
  - The single atom - (n & (1 << i)) - that every other bit problem in this folder composes.
- `A02_NumberOf1Bits.java` — Popcount by shift and test **[must-know]**
  - Loops the rank-1 mask test across all bits; also the place to learn the n & (n-1) trick.
- `A03_SingleNumber.java` — XOR self-cancellation **[must-know]**
  - The second bit identity (x^x=0, x^0=x); unlocks Missing Number, bit flips and pair-elimination problems.
- `A04_ArmstrongNumber.java` — Digit extraction loop
  - Decimal twin of bit peeling - %10 and /=10 - the loop reused by Happy Number, digit sums and base conversion.
- `A05_CheckPrime.java` — Primality to sqrt(n) **[must-know]**
  - Introduces the sqrt bound that every divisor and factorization routine after it assumes.
- `A06_AllDivisors.java` — Divisor pairing i and n/i **[must-know]**
  - Generalizes the sqrt bound into enumeration; the pairing insight drives divisor-count and perfect-number questions.
- `A07_PrimeFactors.java` — Trial-division factorization
  - Adds divide-out-until-indivisible on top of divisor enumeration; plain twin of the annotated copy that follows.
- `A08_ImportantPrimeFactors.java` — Trial-division factorization **[must-know]**
  - The annotated canonical copy - factorization feeds GCD reasoning, divisor counting and most number-theory follow-ups.
- `A09_SieveOfEratosthenes.java` — Sieve of Eratosthenes **[must-know]**
  - Flips per-number primality into batch precomputation; the i*i start and O(n log log n) bound are standard follow-ups.
- `A10_LCMOfTwoNumbers.java` — Euclid GCD, LCM identity **[must-know]**
  - Euclid's algorithm plus LCM = a*b/gcd is the shared primitive for fractions, modular work and overflow discussion.
- `A11_BinaryExponentiation.java` — Square and multiply **[must-know]**
  - Needs the bit test from rank 1 and turns it into O(log n) power; prerequisite for modpow and matrix exponentiation.

### B — Easy

- `B01_MissingNumber.java` — Sum formula or XOR **[must-know]**
  - First application of XOR cancellation; the sum-vs-XOR trade-off (overflow) is a constant interview exchange.
- `B02_MinimumBitFlipsToConvertNumber.java` — XOR diff then popcount
  - Composes the first two A primitives directly - XOR to find differing bits, popcount to count them.
- `B03_BinaryWatch.java` — Enumerate states, filter by popcount
  - Shows popcount used as a filter over a small state space instead of bit-by-bit construction.
- `B04_PowerOfThree.java` — Repeated division test
  - Simplest form of the divide-down structure test; base case for the two files after it.
- `B05_PowerOfTen.java` — Repeated division base 10
  - Same loop, different base - makes the pattern visible before it gets generalized.
- `B06_PowerCheck.java` — Power of arbitrary base
  - Generalizes the previous two into one routine and forces the edge cases (base 1, zero, negatives).
- `B07_DivisibleByThree.java` — Digit-sum divisibility rule
  - First problem that replaces computation with a number-theory rule; needs the digit loop from A4.
- `B08_ExcelSheetColumnNumber.java` — Base-26 positional parse
  - Same digit-accumulation loop in a non-decimal base; the 1-indexed (no zero digit) quirk is the real question.
- `B09_AddTwoFractions.java` — GCD reduction of a fraction
  - First consumer of Euclid's GCD from A10; sets up the fraction long-division problems in tier C.
- `B10_FibonacciCounter.java` — Iterative Fibonacci in a range
  - Establishes the linear Fibonacci generator that tier D later replaces with matrix exponentiation.
- `B11_PascalTrianleTwoLoops.java` — Row from previous row
  - The obvious O(n^2) Pascal build; you must write this before the closed-form version in tier C means anything.
- `B12_SumOfProductOfPairs.java` — Pair enumeration, sum identity
  - Brute-force pair loop whose real lesson is the O(n) identity ((sum)^2 - sum of squares)/2.
- `B13_MeanMedianCalculator.java` — Mean and median via sort
  - Trivial statistics, but the odd/even median split and the sort cost are the follow-up hooks.
- `B14_MaximumAreaofLongestDiagonalRectangle.java` — Single-pass max with tie-break
  - Teaches comparing squared distances to avoid sqrt, plus clean secondary-key tie-breaking.
- `B15_NumberOfDays.java` — Leap-year rule lookup
  - Opens the calendar/clock group; the 4/100/400 rule is pure edge-case discipline.
- `B16_AngleBetweenHandsOfClock.java` — Degrees-per-unit, pick smaller arc
  - First circular-measure problem - the hour hand drifting 0.5 deg/min and the 360-angle fold prepare tier C's wraparound.

### C — Medium

- `C01_HappyNumber.java` — Floyd cycle detection on digits **[must-know]**
  - Combines the digit loop with tortoise-and-hare; the insight that the sequence must cycle transfers to linked lists and functional graphs.
- `C02_RepeatedNumberInFractionAfterDecimal.java` — Long division, remainder map
  - Second cycle-detection flavour - a repeated remainder, not a repeated value; plain twin of the annotated copy.
- `C03_ImportantRepeatedNumberInFractionAfterDecimal.java` — Long division, remainder map
  - The annotated canonical copy; the remainder-to-position map plus sign and overflow handling is the whole interview.
- `C04_PascalTrianleByMath.java` — Incremental nCr per row
  - Replaces the two-loop build with val *= (n-k)/k; the ordering that keeps the division exact is the non-obvious part.
- `C05_ImportantPascalTrianleByMath.java` — Incremental nCr per row **[must-know]**
  - Annotated canonical copy - one row in O(k) and one element in O(1) is the answer interviewers actually push you toward.
- `C06_MinimumTimeDifference.java` — Normalize, sort, circular wrap
  - Extends clock arithmetic to a full array: minutes-since-midnight, adjacent diffs, and the 1440 wraparound pair everyone forgets.
- `C07_ZScore.java` — Binary search on the answer
  - The one monotonic-predicate search here (H-index shaped); note the code's sort order versus its comment before trusting it.

### D — Hard

- `D01_FibonacciBinaryExponentiation.java` — 2x2 matrix exponentiation
  - Lifts square-and-multiply from scalars to matrices; only makes sense after A11 and the linear Fibonacci in B10.
- `D02_ImportantFibonacciBinaryExponentiation.java` — 2x2 matrix exponentiation
  - Annotated canonical copy - the transfer-matrix idea generalizes to any linear recurrence, which is the staff-level point.
- `D03_JosephusProblem.java` — Recurrence by index remapping
  - Needs the non-obvious step of re-indexing the survivor after each elimination: J(n,k) = (J(n-1,k)+k) % n.
- `D04_NumberOfPerfectPairs.java` — Algebraic reduction, two pointers
  - Hardest derivation here - prove signs are irrelevant, reduce to absolute values, sort, then count with a sliding pointer in O(n log n).

## Interview readiness

The number-theory half of this folder is genuinely strong — arguably deeper than most senior candidates bring. Sieve, sqrt-bounded factorization, GCD/LCM, binary exponentiation lifted to 2x2 matrix Fibonacci, Josephus, repeating-decimal cycle detection and Pascal's incremental nCr cover essentially everything a product-company interviewer would open with on the math side, and the clock/calendar/modular-arithmetic cluster covers the "estimation-flavoured" warm-ups. The bit-manipulation half is the thin one: only 6 of the 38 files touch bits at all (CheckIfTheIthBitIsSetOrNot, NumberOf1Bits, SingleNumber, MinimumBitFlips, BinaryWatch, MissingNumber), and every one of them is built from just two atoms — mask-and-test with 1<<i, and XOR self-cancellation. Three whole families are absent: isolating the lowest set bit (x & -x, n & (n-1)), counting occurrences per bit position across an array, and doing arithmetic at the bit level (add/divide without + or /). That matters because the standard interview escalation path is exactly Single Number -> Single Number II -> Single Number III, and he stops at step one; an interviewer who opens with the problem he has will follow up with the two he does not. One concrete liability to fix while he is in there: NumberOf1Bits.java uses `n = n >> 1` (arithmetic shift), so on a negative int the sign bit refills forever and the loop never terminates — LeetCode 191 explicitly treats the input as unsigned, so this is a hang, not a style nit. It needs `>>>`, or better, the `n &= (n - 1)` idiom, which is also the missing primitive behind half the gaps below. Net read: he would pass a math-heavy round comfortably today, and would be exposed in a bit-heavy one at roughly the fourth question in.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Single Number II (every element appears three times except one) | Count set bits per position mod 3, or the two-variable ones/twos state machine | This is the single most likely follow-up to SingleNumber, which he already has, and XOR self-cancellation does not generalise to it — so his existing tool fails loudly. It is the only place he learns to count occurrences per bit position across an array, a technique that also solves 'appears k times except one' for any k. Currently he has no per-bit counting anywhere in the folder. |
| high | Single Number III (exactly two elements appear once, all others twice) | XOR everything, isolate the lowest set bit with x & -x, partition the array into two buckets and XOR each | The second half of the standard Single Number escalation and the canonical introduction to x & -x (isolate lowest set bit), which appears nowhere in this folder. That primitive recurs in Fenwick/BIT trees, subset-mask enumeration and several graph problems, so its absence is not local to this topic. |
| high | Counting Bits (popcount for every number from 0 to n) | dp[i] = dp[i >> 1] + (i & 1), or dp[i] = dp[i & (i-1)] + 1 | Very frequently asked as a screen because it looks like NumberOf1Bits but punishes the O(n log n) per-number loop he currently knows. It is the bridge between bit tricks and DP, and it teaches n & (n-1) as 'clear the lowest set bit' — the idiom that would also fix the infinite-loop bug in his existing NumberOf1Bits.java. |
| high | Sum of Two Integers without using + or - | sum = a ^ b, carry = (a & b) << 1, loop until carry is zero | The canonical test of whether a candidate actually understands what addition is at the hardware level rather than having memorised tricks. It is asked at exactly the senior level he is targeting, and nothing in this folder builds arithmetic out of bit operations — his bit files only inspect bits, never compute with them. Has a real Java trap too (the carry loop must be written for two's complement). |
| medium | Reverse Bits (reverse the 32 bits of an unsigned integer) | Shift result left, OR in n & 1, shift n right with >>> for 32 fixed iterations | The standard companion to NumberOf1Bits in bit-manipulation screens, and the problem that forces the unsigned-shift discipline his current popcount gets wrong. Also sets up the 'what if you call this millions of times' follow-up (byte-level lookup table / divide-and-conquer swap of nibbles), which is a good staff-level depth probe he cannot currently take. |
| medium | Maximum XOR of Two Numbers in an Array | Insert all numbers into a binary (bitwise) trie MSB-first, then greedily walk the opposite branch for each number | The one bit problem that reliably shows up at staff level and separates candidates, because it needs both a trie and MSB-first greedy reasoning. He has a whole Trie folder (10-Trie) with none of the bitwise variants, so the substrate exists but the connection has never been made. Also the gateway to XOR-prefix / maximum-XOR-subarray follow-ups. |
| medium | Factorial Trailing Zeroes | Count factors of 5 via n/5 + n/25 + n/125 + ... (Legendre's formula) | A classic product-company math question that punishes anyone who tries to compute n! and rewards reasoning about factor multiplicity. He has prime factorisation of a single number but nothing that reasons about how often a prime divides a whole factorial, and the natural follow-up ('how many trailing zeroes in base 12?') is a clean staff-level extension of the same idea. |
| medium | Divide Two Integers (no multiplication, division or modulo) | Binary long division — repeatedly double the divisor while it fits, subtract, accumulate the shifted quotient; plus the Integer.MIN_VALUE / -1 overflow case | The best available test of overflow discipline, which is completely absent from this folder — every file here assumes results fit. The doubling loop is the additive sibling of the binary exponentiation he already knows, so it is cheap for him to learn, and the MIN_VALUE edge case is exactly the kind of thing a staff interviewer watches for him to raise unprompted. |

