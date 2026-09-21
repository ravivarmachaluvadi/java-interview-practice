# Maths Symbols

A quick reference of the mathematical, logical and computer-science symbols that
turn up in algorithm notes, textbooks and papers, with the meaning and an example
of each.

## At a glance

| Section | Covers |
| --- | --- |
| [1. Set theory and collections](#1-set-theory-and-collections) | membership, subsets, union, the number sets |
| [2. Logic and conditions](#2-logic-and-conditions) | and/or/not, implication, quantifiers |
| [3. Maths and analysis](#3-maths-and-analysis) | comparison, sums, products, limits |
| [4. Algorithm analysis](#4-algorithm-analysis) | Big-O and friends, floor, ceiling, log |
| [5. Computer science notation](#5-computer-science-notation) | lambda, XOR, congruence, proof shorthand |
| [6. Symbols that are easy to confuse](#6-symbols-that-are-easy-to-confuse) | the pairs that get mixed up |

## 1. Set theory and collections

| Symbol | Meaning | Example / Usage |
| --- | --- | --- |
| ∈ | Is an element of | `x ∈ S` -- x is in set S |
| ∉ | Is not an element of | `x ∉ S` |
| ∋ | Contains as an element (∈ written backwards) | `S ∋ x` -- S contains x |
| ⊆ | Subset of, possibly equal | `A ⊆ B` |
| ⊂ | Subset of; **most authors mean proper subset**, some mean plain subset | `A ⊂ B`; write `⊊` when you need "proper" beyond doubt |
| ⊊ | Proper subset: inside B and definitely not equal to it | `A ⊊ B` |
| ⊄ | Not a subset of | `A ⊄ B` |
| ∪ | Union | `A ∪ B` -- everything in either set |
| ∩ | Intersection | `A ∩ B` -- only what is in both |
| ∖ | Set difference | `A ∖ B` -- in A, not in B |
| △ | Symmetric difference | `A △ B` -- in one set but not both |
| ∅ | Empty set | `S = ∅` -- the set is empty |
| \|S\| | Cardinality: how many elements | `\|{1,2,3}\| = 3` |
| ℕ ℤ ℚ ℝ ℂ | The number sets | natural, integer, rational, real, complex |

## 2. Logic and conditions

| Symbol | Meaning | Example / Usage |
| --- | --- | --- |
| ∧ | Logical AND | `p ∧ q` -- both true |
| ∨ | Logical OR (inclusive) | `p ∨ q` -- at least one true |
| ¬ | NOT | `¬p` |
| ⇒ | Implies | `p ⇒ q` -- if p then q |
| ⇔ | If and only if | `p ⇔ q` -- each implies the other |
| ∀ | For all | `∀x ∈ S, f(x) > 0` |
| ∃ | There exists | `∃x ∈ S, f(x) = 0` |
| ∄ | There does not exist | `∄x ∈ S, f(x) = 0` |
| : or \| | Such that, inside a quantifier or set builder | `∃x : x > 0`; `{x ∈ ℕ \| x is even}` |
| ⊤ / ⊥ | True / False | `⊥` also means "bottom": undefined, or a computation that never returns |

## 3. Maths and analysis

| Symbol | Meaning | Example |
| --- | --- | --- |
| ≈ | Approximately equal | `π ≈ 3.14` |
| ≠ | Not equal | `a ≠ b` |
| ≤ / ≥ | Less than or equal / greater than or equal | `a ≤ b` |
| ≪ / ≫ | Much smaller / much larger than | `n ≪ N` |
| → | Maps to, or transitions to | `f: X → Y`; `state → nextState` |
| ↦ | Sends this element to that one | `x ↦ x²` |
| ∑ | Summation | `∑ i=1..n  i` |
| ∏ | Product | `∏ i=1..n  a[i]` |
| √ | Square root | `√x` |
| ∞ | Infinity | `while (true)` is a conceptual `∞` loop |
| ∝ | Is proportional to | `time ∝ n` |
| Δ, δ | Change in a quantity (capital for finite, lower-case for infinitesimal) | `Δx = x₂ - x₁` |
| ∇ | Nabla / del: the gradient operator | `∇loss` -- the gradient of the loss, used in ML |
| ∂ | Partial derivative | `∂f/∂x` |

## 4. Algorithm analysis

These are the ones that actually appear in the COMPLEXITY line of the practice
files in this repo.

| Symbol | Meaning | Example |
| --- | --- | --- |
| O(f) | Upper bound: grows no faster than f | binary search is `O(log n)` |
| Ω(f) | Lower bound: grows at least as fast as f | comparison sorting is `Ω(n log n)` |
| Θ(f) | Tight bound: both `O(f)` and `Ω(f)` | merge sort is `Θ(n log n)` |
| o(f) | Strictly smaller order than f | `n` is `o(n²)` |
| ⌊x⌋ | Floor: round down | `⌊3.7⌋ = 3`; `mid = ⌊(lo+hi)/2⌋` is integer division |
| ⌈x⌉ | Ceiling: round up | `⌈3.2⌉ = 4`; `⌈n/k⌉` buckets hold n items |
| log n | Logarithm; **base 2 unless stated** in CS | halving n takes `log₂ n` steps |
| n! | Factorial | permutations of n items |
| \|x\| | Absolute value | `\|-4\| = 4` |

## 5. Computer science notation

| Symbol | Meaning | Example / Context |
| --- | --- | --- |
| λ | Lambda: an anonymous function | `λx. x + 1`, written `x -> x + 1` in Java |
| ⊕ | XOR (in maths, also direct sum) | `a ⊕ a = 0` is why XOR finds the single number |
| ⊻ | XOR, when ⊕ would be ambiguous | `p ⊻ q` |
| & \| ~ ^ | Java's bitwise AND, OR, NOT, XOR | `n & (n - 1)` clears the lowest set bit |
| ≡ | Congruent / identical | `a ≡ b (mod n)` -- a and b leave the same remainder |
| ∘ | Function composition | `(f ∘ g)(x) = f(g(x))` |
| ⊗ | Tensor product (Kronecker product) | deep-learning and linear-algebra papers |
| ∴ | Therefore | closing line of a proof |
| ∵ | Because | `∵ the array is sorted, binary search applies` |
| □ or ∎ | End of proof (QED) | last character of a proof |

## 6. Symbols that are easy to confuse

| These two | Look alike but | Rule of thumb |
| --- | --- | --- |
| ∋ and "such that" | `∋` means "contains as an element". Some older texts abuse it for "such that" | Write "such that" as `:` or `\|`, never `∋` |
| Δ and ∇ | `Δ` is a change in a quantity. `∇` is the gradient operator | Δ = difference, ∇ = direction of steepest ascent |
| ⊕ and ∘ | `⊕` is XOR or direct sum. `∘` is function composition | XOR combines bits, `∘` chains functions |
| ⊗ and × | `⊗` is a tensor/Kronecker product. `×` is ordinary or Cartesian product | Use `×` for `A × B` pairs |
| → and ⇒ | `→` maps or transitions. `⇒` is logical implication | `f: X → Y` never uses `⇒` |
| ⊂ and ⊆ | Many authors use `⊂` for plain subset, others for proper subset | Say which you mean, or use `⊆` and `⊊` |
| ⇒ in maths vs code | In maths it is "implies". In Kotlin, C# and JavaScript, `=>` introduces a lambda | Java's lambda arrow is `->`, not `=>` |
