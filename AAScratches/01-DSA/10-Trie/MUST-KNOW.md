# 10-Trie — must-know order

**Techniques in this topic:** Trie node layout: 26-slot children array + isEnd flag, and the containsKey/get/put/setEnd primitive API that every other file reuses verbatim, Insert / search / startsWith traversal in O(L) — the distinction between 'a path exists' (prefix) and 'a path exists AND isEnd is true' (whole word), Reading the isEnd flag at every node along a path, not just the last one — the trick behind 'every prefix must also be a word', DFS + backtracking over trie children to handle wildcards, which turns exact lookup into pattern matching, Prefix-driven top-k retrieval for autocomplete, and the sorted-array + moving-left-pointer alternative that beats a trie when products are static, Inserting every suffix of a string so that each newly created node equals one distinct substring — counting nodes as a proxy for counting strings, Trie vs HashSet trade-off: the trie only pays off when the query is prefix-shaped or shares structure across queries

| | |
|---|---|
| Problems | 5 |
| Must-know | 2 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A01_Trie.java` | Trie insert, search, startsWith |
| 2 | `C02_DesignAddAndSearchWordsDataStructure.java` | Wildcard DFS over trie children |

## Full practice order


### A — Building blocks

- `A01_Trie.java` — Trie insert, search, startsWith **[must-know]**
  - Defines the node shape and the three traversal primitives every other file in the folder copies; nothing else here is readable until this is muscle memory.

### C — Medium

- `C01_LongestWordWithAllPrefixesORCompleteStringFinderTrie.java` — Check isEnd at every node
  - Gentlest step past raw insert/search — same walk as startsWith, except you assert the end-flag at each node instead of only the last, plus a lexicographic tie-break.
- `C02_DesignAddAndSearchWordsDataStructure.java` — Wildcard DFS over trie children **[must-know]**
  - Takes the exact-match walk from Trie.java and makes it branch: '.' fans out over all 26 children with backtracking, the technique that unlocks word-search and pattern-matching trie problems.
- `C03_SearchSuggestionsSystem.java` — Prefix top-k autocomplete
  - Applies prefix search to a product requirement (3 suggestions per keystroke); the file uses sort + a monotone left pointer, so it also forces the trie-vs-sorted-array trade-off discussion an interviewer will push on.

### D — Hard

- `D01_CountDistinctSubstringsUsingTrie.java` — Insert all suffixes, count nodes
  - Needs the non-obvious leap that distinct substrings = prefixes of suffixes, so each newly allocated node is exactly one distinct substring — a counting argument, not a traversal, and it assumes fluent insert mechanics.

## Interview readiness

The five files here (385 lines total) cover the trie *as a standalone data structure* well: Trie.java establishes a clean containsKey/get/put/setEnd node API, DesignAddAndSearchWordsDataStructure adds wildcard DFS+backtracking, LongestWordWithAllPrefixes exercises reading isEnd at every node on the path, CountDistinctSubstrings covers the insert-every-suffix trick, and SearchSuggestionsSystem even shows the sorted-array alternative that beats a trie for static data — that last file is a genuinely senior instinct. But the folder stops exactly where senior/staff interviews start. Every file uses the same 26-slot lowercase-only node with a single boolean and no payload, and the trie is never *combined* with another structure. I verified across all 20 DSA folders (grep for `TrieNode`/`new Trie(` finds zero hits outside 10-Trie) that there is no Word Search II, no bitwise/binary trie, no trie-with-counts or erase, and no ranked autocomplete anywhere in the repo. The practical consequence: he can pass a 20-minute "implement a trie" screen comfortably, and would very likely stall on the two things that are actually asked at his level — the grid-DFS-pruned-by-trie problem (Word Search II) and the binary trie for max-XOR, which shares almost no muscle memory with the 26-ary version. Closing the four high-priority gaps below would take this folder from "solid fundamentals" to interview-ready; the folder is currently about 55-60% complete for a senior/staff product-company bar.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Word Search II (LC 212) | Build a trie of the dictionary, then DFS the grid once carrying a trie node alongside the (r,c) cursor; prune the moment the child link is null, and delete/null-out leaf nodes after a word is found to shrink the trie as you go. | This is the single most-asked hard trie question and the clearest demonstration of *why* a trie exists — it converts O(words x cells x 4^L) into one traversal. He already has WordSearch.java (LC 79) in 14-Backtracking-Recursion and Trie.java here, but has never fused them, and the fusion is the entire interview. The leaf-pruning follow-up ('your solution TLEs on 3x10^4 words, fix it') is where candidates without reps fail. |
| high | Maximum XOR of Two Numbers in an Array (LC 421) | Binary trie: 32-level, 2-slot children, insert each number MSB-first, then for each number greedily walk toward the opposite bit at every level to maximise XOR. O(32n) instead of O(n^2). | Every file in this folder assumes a 26-slot lowercase-letter node. A bitwise trie has a different arity, a different key (bits, not chars), fixed depth, and no isEnd flag at all — almost none of his existing muscle memory transfers. This is a very common senior-level question precisely because it tests whether the candidate understands a trie as a *general prefix index* rather than a word-storage gadget. Currently he would almost certainly not reach it unprompted. |
| high | Design Search Autocomplete System (LC 642) | Trie where each node stores a map of sentence -> hot-degree (or a bounded top-3 heap), streaming input character by character, '#' terminating and inserting/incrementing the new sentence, ties broken by ASCII order. | SearchSuggestionsSystem covers the static, unranked, sorted-array version — this is the dynamic, ranked, stateful one, and it is a different problem. It is the canonical product-company trie design question (Amazon/Google), and it forces the two things his folder never does: putting a payload on a trie node, and maintaining a top-k ranking incrementally rather than scanning. At staff level the follow-ups (memory blowup of storing all sentences at every node, cache the top-3 vs recompute) are the actual signal. |
| high | Implement Trie II — countWordsEqualTo, countWordsStartingWith, erase (LC 1804) | Add two integer counters to each node (endsHere and passesThrough); insert increments both along the path, erase decrements and frees nodes whose passesThrough drops to zero. Prefix count is read off a single node in O(L). | This is the near-guaranteed follow-up to the 'implement a trie' warm-up he already has: 'now support delete' and 'now tell me how many words start with this prefix'. His TrieNode has only links[26] and a boolean flag, so neither is answerable without redesigning the node under time pressure. Delete is fiddly (you cannot just clear the isEnd flag, and you must not free a node another word passes through) and it is the exact spot where an otherwise clean trie answer falls apart. |
| medium | Stream of Characters (LC 1032) | Insert every dictionary word REVERSED into the trie; keep the queried characters in a bounded deque/StringBuilder and, on each new char, walk backwards from the newest character through the reversed trie, stopping at the longest word length. | The suffix-matching inversion — indexing reversed words so a streaming query becomes a prefix walk — is a distinct trick from anything here. His CountDistinctSubstrings inserts forward suffixes of one string; this reverses whole dictionary words, which is the opposite direction and is not obvious if you have not seen it. It is a common Amazon/Google design-flavoured trie question and pairs naturally with a bounded-buffer discussion. |
| medium | Replace Words (LC 648) | Insert all dictionary roots, then for each word in the sentence walk the trie and return at the FIRST node where isEnd is true (shortest root wins); fall back to the original word if the walk dies. | LongestWordWithAllPrefixes trains him to check isEnd at every node and require all of them true; this trains the opposite reflex — stop at the first true one. Same primitive, inverted stopping condition, and interviewers use it as the 15-minute warm-up before escalating to Word Search II. Cheap to add and it hardens the distinction between 'longest match' and 'shortest match' traversals, which is the root of most trie off-by-one bugs. |
| medium | Concatenated Words (LC 472) / Word Break II | Insert all words into a trie, then for each candidate run a DFS from index 0 that, at each trie node with isEnd, branches: either restart at the root (word boundary) or keep walking. Memoize on start index to avoid exponential blowup. | Trie + DP segmentation is a whole third mode of using a trie (the trie replaces the HashSet lookup inside a DP recurrence) and it is absent from the entire repo — I checked 12-Dynamic-Programming and there is no WordBreak.java either, so he has no exposure to string segmentation at all. Word Break is asked constantly; Concatenated Words is its Amazon-favourite hard sibling. The memoization-on-start-index insight is the part that gets probed. |
| low | Map Sum Pairs (LC 677) | Store an integer value on each node representing the sum of all keys passing through it; on insert, compute the delta against the key's previous value and propagate that delta down the path so re-inserting an existing key corrects rather than double-counts. | The cheapest way to learn 'a trie node can carry an aggregate, not just a flag' — the idea underneath every real ranked-autocomplete and prefix-analytics system. The subtlety worth one rep is the overwrite case: insert("apple",3) then insert("apple",2) must yield 2, not 5, which naive implementations get wrong. Lower priority only because Design Search Autocomplete above teaches the same lesson at higher difficulty; skip this one if time is short. |

