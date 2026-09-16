# 04-Strings — must-know order

**Techniques in this topic:** Character-frequency counting with int[26] / HashMap — anagrams, missing letters, first unique, most-frequent vowel, Two pointers converging from both ends — palindrome with skips, strobogrammatic, mirrored digit checks, Read/write two-pointer over a run of equal characters — RLE, in-place string compression, run-mod-3 decomposition, Digit-by-digit arithmetic on decimal strings — carry simulation (add), partial-product grid (multiply), overflow-safe parsing (atoi), split / transform / join word pipelines — reverse words, camel case, Goat Latin, capitalisation, StringBuilder as the accumulator, plus the index tricks: indexOf(x, from), delete(i,j), reverse(), setCharAt(), Greedy table-driven and lexicographic-greedy construction — Roman numerals, largest merge, text justification, Simulation and state machines over a character stream — zigzag rows, valid number, bold-tag interval marking, The concatenation trick: s+s contains a rotation; str1+str2 == str2+str1 proves a common divisor, Hard tier is casework discipline, not exotic data structures — Manacher, valid number, number-to-words, next palindrome

| | |
|---|---|
| Problems | 41 |
| Must-know | 10 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A04_AnagramStrings.java` | int[26] frequency counting |
| 2 | `A06_PalindromeSpecial.java` | Two pointers with skips |
| 3 | `A08_AddStrings.java` | Digit carry simulation |
| 4 | `B07_FirstNonRepeatingChar.java` | Order-preserving frequency map |
| 5 | `B15_LongestCommonPrefix.java` | Shrinking prefix across array |
| 6 | `B16_RomanToInteger.java` | Right-to-left subtractive scan |
| 7 | `C01_ReverseWordsInString.java` | Trim, split on whitespace, reverse order |
| 8 | `C02_ATOI.java` | Parsing with overflow guard |
| 9 | `C03_StringCompression.java` | In-place read/write pointers |
| 10 | `D01_ImportantLongestPalindrome.java` | Expand around center, then Manacher |

## Full practice order


### A — Building blocks

- `A01_DelimiterSplit.java` — String.split on a delimiter
  - The tokenising primitive every word-level problem in this folder starts from.
- `A02_FirstCharToUpperCase.java` — split, map, join pipeline
  - Establishes the split-transform-join shape reused by every per-word transform below.
- `A03_ReverseWords.java` — StringBuilder.reverse per token
  - Adds the reverse() primitive on top of split/join before it is needed in palindrome and semordnilap work.
- `A04_AnagramStrings.java` — int[26] frequency counting **[must-know]**
  - The single most reused primitive in string interviews: increment, decrement, assert all zero.
- `A05_CharactersNotInString.java` — Frequency array read-back
  - Same array as anagram, but read in reverse — teaches that count==0 is itself an answer.
- `A06_PalindromeSpecial.java` — Two pointers with skips **[must-know]**
  - The converging-pointer primitive, with the filtering inner loops that trip people up under pressure.
- `A07_RunLengthEncodingW4A3.java` — Counting consecutive runs
  - Teaches the run-scan loop that compression, decomposition and grouping problems all assume.
- `A08_AddStrings.java` — Digit carry simulation **[must-know]**
  - The arithmetic-on-strings primitive; atoi and multiply are both extensions of this loop.

### B — Easy

- `B01_ImportantRunLengthEncodingW4A3.java` — Run-length encoding repeat
  - Duplicate of the A-tier run scan — use it as the speed drill, not as new material.
- `B02_CapitalizeFirstAndLastCharacterOfEachWord.java` — setCharAt on each word
  - First variation on the word pipeline; introduces in-place StringBuilder mutation.
- `B03_ToCamelCase.java` — Split, capitalize, concat
  - Same pipeline as above but joining with no separator, which is the common off-by-one trap.
- `B04_GoatLatin.java` — Per-word transform with index
  - Adds word position to the transform, so state now crosses the loop boundary.
- `B05_PanagramDetectorMain.java` — Set-based alphabet coverage
  - Solves CharactersNotInString with a Set instead of int[26] — the array-vs-set trade-off conversation.
- `B06_MostFrequentVowelAndConsonant.java` — Frequency array plus classification
  - Frequency counting with a predicate partition layered on top.
- `B07_FirstNonRepeatingChar.java` — Order-preserving frequency map **[must-know]**
  - Constant phone-screen question; teaches that insertion order must be preserved alongside counts.
- `B08_SortStringDescending.java` — Sorting characters with comparator
  - Sorted-string canonical form underpins anagram grouping; also the Character[] boxing gotcha.
- `B09_SemordnilapChecker.java` — Reverse plus HashSet lookup
  - Combines the reverse primitive with O(1) set lookup — the pair-finding pattern.
- `B10_StrobogrammaticNumber.java` — Two pointers with mapping table
  - Direct variation on the palindrome scan where equality is replaced by a lookup.
- `B11_PasswordStrengthChecker.java` — Single-pass boolean flags
  - Introduces flag accumulation over one scan, the shape ValidNumber later pushes to its limit.
- `B12_MinimumAlternatingBinaryString.java` — Two hypotheses counted together
  - Teaches evaluating both candidate answers in one pass instead of two — a widely reusable trick.
- `B13_CheckIfStringIsDecomposableIntoValueEqualSubstrings.java` — Run grouping with mod-3 rule
  - First problem where the run scan feeds a constraint, not just an output string.
- `B14_MergeStringsAlternately.java` — Two-pointer merge with leftovers
  - The merge skeleton that LargestMerge later upgrades with a greedy choice.
- `B15_LongestCommonPrefix.java` — Shrinking prefix across array **[must-know]**
  - Appears constantly as an opener; the trimming loop is the version to have memorised.
- `B16_RomanToInteger.java` — Right-to-left subtractive scan **[must-know]**
  - Ubiquitous, and it must precede IntegerToRoman, which assumes the value table.
- `B17_RotateString.java` — s+s contains goal
  - Introduces the doubling trick that GreatestCommonDivisorOfStrings depends on.
- `B18_LongestWordFromLetters.java` — Can-form check via letter counts
  - Applies frequency counting as a feasibility test across a dictionary, plus tie-keeping.
- `B19_RemoveAllOccurrences.java` — Repeated indexOf and delete
  - Closes the easy tier by exercising StringBuilder indexOf/delete; know the O(n) stack answer as follow-up.

### C — Medium

- `C01_ReverseWordsInString.java` — Trim, split on whitespace, reverse order **[must-know]**
  - The canonical medium warm-up, and the natural entry to the O(1)-space reverse-all-then-reverse-each follow-up.
- `C02_ATOI.java` — Parsing with overflow guard **[must-know]**
  - Builds on the digit loop from AddStrings and adds the pre-multiply overflow check interviewers actually probe.
- `C03_StringCompression.java` — In-place read/write pointers **[must-know]**
  - Turns the run scan into an in-place rewrite — the read/write pointer split that many array questions reuse.
- `C04_IntegerToRoman.java` — Greedy descending value table
  - Reverses RomanToInteger and teaches encoding the subtractive pairs into the table itself.
- `C05_ZigzagConversion.java` — Row simulation with direction flip
  - First pure simulation problem: state is a row index and a direction, not a counter.
- `C06_GreatestCommonDivisorOfStrings.java` — Concat equality plus gcd of lengths
  - Needs the doubling insight from RotateString before the two-line solution feels earned.
- `C07_MultiplyStrings.java` — Positional partial products
  - The heavy extension of AddStrings: the i+j / i+j+1 index mapping is the whole problem.
- `C08_LargestMergeOfTwoStrings.java` — Greedy lexicographic suffix compare
  - Upgrades the alternate-merge skeleton with a greedy rule, and raises the suffix-comparison cost question.
- `C09_AddBoldTagInString.java` — Boolean mark array, merge intervals
  - Closes the medium tier by combining substring search with interval merging expressed as a flag array.

### D — Hard

- `D01_ImportantLongestPalindrome.java` — Expand around center, then Manacher **[must-know]**
  - Asked everywhere; own the O(n^2) center expansion cold and treat this Manacher version as the stretch answer.
- `D02_ValidNumber.java` — State machine over character classes
  - Escalates PasswordStrengthChecker's flags into ordering rules — hard because of exhaustive casework, not algorithm.
- `D03_TextJustification.java` — Greedy line packing, space distribution
  - Classic hard: line-fitting greedy is easy, the uneven space split and last-line rule are where candidates fail.
- `D04_NumberToWordsConverter.java` — Recursive group decomposition
  - Pure decomposition and formatting discipline; sits after TextJustification as the second casework grind.
- `D05_NextPalindromeUsingSameDigits.java` — Next permutation on half, then mirror
  - Hardest here: requires seeing that only the left half is free, plus the next-permutation subroutine.

## Interview readiness

This is one of the strongest string folders I have reviewed at this level — 41 problems that genuinely span the ladder, from frequency counting and two-pointer palindromes up to a hard tier most candidates never touch (TextJustification, ValidNumber, NumberToWordsConverter, MultiplyStrings, NextPalindromeUsingSameDigits, expand-around-centre longest palindrome). Crucially, the classics that look missing here are mostly covered in sibling folders — sliding-window string work sits in 02 (MinimumWindowSubstring, LongestSubstringWithoutRepeatingCharacter, CharacterReplacement, ValidWordAbbreviation), hashing string work in 05 (GroupAnagrams, GroupShiftedStrings, FindAndReplacePattern which subsumes Isomorphic Strings and Word Pattern, RansomNote, SubdomainVisitCount), stack-based parsing in 07 (DecodeString, BasicCalculator, ValidParentheses, RemovingStarsFromAString which is Backspace String Compare, SimplifyPath), and string DP in 12 (EditDistance, LCS, PalindromePartitioning, NumDecodings, RegularExpressionMatching). So the honest gap list is short. The one structural hole is pattern matching: there is no correct KMP anywhere — SingleLoopSubstringCheckKMP.java in 18-Sorting-Searching-Algorithms is a naive scan mislabelled as KMP and it is actually wrong (it returns false for pattern "aab" in text "aaab" because j is zeroed before the i-backtrack is computed), leaving only RabinKarpAlgorithm.java as real substring search. The other real holes are narrow but high-frequency screening questions: palindrome with one allowed deletion, fixed-window anagram matching, and Word Break. Close those five or six and this folder is above the bar for any senior or staff string round; today the risk is not depth, it is losing a 20-minute phone screen to a problem that is easier than half of what he has already solved.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Valid Palindrome II — palindrome after deleting at most one character (LC 680) | Two pointers with a single branch point: on the first mismatch, recurse/verify the two candidate sub-ranges (skip left, skip right) in O(n) total, O(1) space. | PalindromeSpecial.java is only LC 125 — it skips non-letters while converging, it never handles a deletion. The one-skip branch is a different idea and is one of the most frequently asked warm-ups at Meta and Amazon, usually as the opener before a harder second question. Missing it here means the theme note claiming 'palindrome with skips' is overstated: the skip he practises is 'ignore punctuation', not 'spend one deletion'. |
| high | Implement strStr / indexOf with KMP (LC 28 plus the LPS / prefix-function build) | Build the longest-proper-prefix-suffix array for the pattern, then scan the text once without ever moving the text pointer backwards; O(n+m) time, O(m) space. | There is no correct KMP in the whole workspace. The file that claims to cover it, 18-Sorting-Searching-Algorithms\SingleLoopSubstringCheckKMP.java, has no failure function at all and is buggy: it sets j = 0 and then computes i = i - j + 1, which collapses to i + 1 and destroys the backtrack, so isSubstring("aaab", "aab") returns false. Only RabinKarpAlgorithm.java gives real substring search. 'Implement indexOf, now make it linear worst-case' is a standard staff-level probe, and the LPS array is also the key to Repeated Substring Pattern and Shortest Palindrome. |
| high | Find All Anagrams in a String / Permutation in String (LC 438, LC 567) | Fixed-size sliding window over an int[26] with a single 'matches' counter updated incrementally as characters enter and leave — O(n), not O(n) windows each compared in O(26). | Nothing in the 41 files or in 02-Two-Pointers-Sliding-Window covers it. MinimumWindowSubstring is the variable-width, at-least-covers relative; the fixed-width, exactly-equal-multiset version has a different bookkeeping trick (decrement the matches counter only when a count crosses the required value) and candidates who only know the min-window template usually write the O(26n) recompute version and then stall when asked to remove the inner loop. This is a top-20 phone-screen string question. |
| high | Word Break (LC 139) | 1-D DP over prefixes with a HashSet dictionary, dp[i] = any j < i where dp[j] && s[j..i) in dict; follow-ups are memoised recursion, a Trie to bound the inner loop, and the max-word-length cutoff. | Absent from 04-Strings, from 12-Dynamic-Programming (which has EditDistance, LCS and PalindromePartitioning but not this) and from 10-Trie. It is the canonical bridge between strings and DP and gets asked constantly at Amazon, Google and most product companies. His DP folder proves he can do interval and subsequence DP, so this is a cheap fill — but right now if the interviewer opens with Word Break there is no rehearsed template, and the naive exponential recursion is an easy trap. |
| medium | Encode and Decode Strings (LC 271) | Length-prefixed framing — write len + '#' + payload per string, then decode by reading the length and slicing exactly that many characters. No escaping, no forbidden delimiter. | DelimiterSplit.java is the naive split-on-a-separator approach, which is exactly the wrong answer here: any chosen delimiter can appear inside the payload. This is the classic staff-level string question because it is really protocol design — the interviewer wants you to reject delimiters, reason about arbitrary Unicode content, and discuss escaping versus framing. He has no problem in the set where the correct answer is 'do not use a delimiter at all'. |
| medium | Compare Version Numbers (LC 165) | Two-pointer tokenisation over '.' without allocating a split array, parsing each revision as an int, treating a missing component as 0 so '1.0' equals '1.0.0', and tolerating leading zeros. | Not present anywhere. It looks trivial and is where people lose points: unequal segment counts, leading zeros ('01' == '1'), and overflow on long revisions. His split/transform/join pipelines all assume both inputs have the same shape, so the pad-the-shorter-side case is unrehearsed. Common at Microsoft and Amazon and it shows up in real product code, which is why interviewers like it. |
| medium | One Edit Distance (LC 161) — are two strings exactly one insert, delete or replace apart | Single pass, O(1) extra space: align until the first mismatch, then compare the remaining suffixes shifted by one depending on whether the lengths are equal (replace) or differ by one (insert/delete). | EditDistance.java in 12-Dynamic-Programming is the full O(n*m) Levenshtein table, which is the wrong-altitude answer here — an interviewer asking this wants the O(1)-space linear scan and the length-difference casework. It is a long-standing Meta and Google screen question and the casework (which string is longer, the |len diff| > 1 early exit, the equal-strings-return-false detail) is exactly the kind of thing that goes wrong under time pressure. |
| low | Reverse Words in a String II — in place on a char[] with O(1) extra space (LC 186, the standard follow-up to LC 151) | Reverse the entire array, then reverse each word in place between its boundaries; the same reverse-the-whole-then-reverse-the-parts trick also gives array rotation by k. | Both existing solutions, ReverseWords.java and ReverseWordsInString.java, call split("\\s+") and rebuild with a StringBuilder, so they use O(n) auxiliary space. The interviewer's follow-up is always 'now do it without allocating' and the double-reversal idiom is the expected answer. It is a five-minute addition that converts two problems he already has into a complete answer, and the same trick reappears in array rotation, so the cost of not knowing it is paid twice. |

