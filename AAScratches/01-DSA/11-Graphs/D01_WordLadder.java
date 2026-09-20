/*
 * =====================================================================
 *  Word Ladder                            LeetCode 127 | Hard  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given beginWord, endWord and a dictionary wordList, transform beginWord into
 *   endWord one letter at a time, where every intermediate word must be in
 *   wordList. Return the number of words in the shortest such sequence, counting
 *   both ends, or 0 if no sequence exists. All words have the same length and are
 *   lowercase; beginWord need not be in wordList, but endWord must be.
 *
 * EXAMPLE
 *   "hit" -> "cog", ["hot","dot","dog","lot","log","cog"]  ->  5
 *       hit -> hot -> dot -> dog -> cog   (5 words, so 4 changes)
 *   "hit" -> "cog", ["hot","dot","dog","lot","log"]        ->  0  (cog missing)
 *   "hit" -> "cog", ["hot","dog","cog"]                    ->  0  (no bridge
 *                                                                  from hot)
 *
 * APPROACH  (BFS over an implicit graph of words)
 *   1. Put the dictionary in a HashSet so membership is O(L) instead of O(n),
 *      and bail out early if endWord is not in it.
 *   2. BFS from beginWord, one level per transformation. cnt is the number of
 *      words on the path so far, so it starts at 1 for beginWord itself.
 *   3. To find a word's neighbours, do NOT compare it with every dictionary word.
 *      Instead generate them: for each of the L positions, try all 26 letters and
 *      keep the results that are in the set. That is 26*L candidates, independent
 *      of how big the dictionary is.
 *   4. Remove a word from the set the moment it is queued. That is the visited
 *      marker, and removing on enqueue (not on dequeue) stops the same word from
 *      being queued twice within one level.
 *   5. The first time endWord comes off the queue, cnt is the shortest length.
 *      Queue empty means unreachable, so return 0.
 *
 * KEY INSIGHT
 *   The graph is never built. Nodes are words, edges are "differs in one letter",
 *   and neighbours are generated on demand - which turns an O(n^2) edge build
 *   into an O(26 * L) lookup per word. BFS then gives the shortest path for free
 *   because every edge costs the same. Recognise this shape in any state-space
 *   search: puzzle boards, lock combinations, jump games. The question to ask is
 *   "what is a state, and what is one legal move?", not "where are the edges?".
 *
 * COMPLEXITY
 *   Time  O(n * L * 26)  n words, each dequeued once, each generating 26*L
 *                        candidates costing O(L) to build and hash
 *   Space O(n * L)       the set and the queue hold the dictionary
 *
 * INTERVIEW FOLLOW-UPS
 *   - Word Ladder II (LC 126): return every shortest sequence - BFS to fix the
 *     level of each word, then backtrack over those levels.
 *   - Bidirectional BFS: expand from both ends and always grow the smaller side.
 *     Roughly halves the explored depth, the usual "make it faster" answer here.
 *   - Very long words but a tiny dictionary: flip to comparing pairs directly, or
 *     bucket words by wildcard patterns such as "h*t".
 *   - Why BFS and not DFS? DFS finds a path, not the shortest one.
 *
 * RUN
 *   main() runs 4 cases (the classic ladder, endWord absent, no bridge, begin
 *   word one step away) and prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class WordLadder {

    // BFS (Breadth First Traversal), i.e. level-by-level, so the first hit is shortest
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> dictSet = new HashSet<>(wordList);
        if (!dictSet.contains(endWord)) return 0; // no ladder can ever end there

        Queue<String> que = new ArrayDeque<>();
        que.add(beginWord);
        dictSet.remove(beginWord); // never revisit the start

        int cnt = 0; // words on the path, counting beginWord
        while (!que.isEmpty()) {
            cnt++;
            int size = que.size(); // freeze this level before adding the next one

            for (int k = 0; k < size; k++) {
                String word = que.poll();
                if (word.equals(endWord)) return cnt;

                // Generate neighbours: hold every other character fixed and try
                // all 26 letters in position i.
                for (int i = 0; i < word.length(); i++) {
                    char[] arr = word.toCharArray();
                    char original = arr[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        if (ch == original) continue; // same word, not a move
                        arr[i] = ch;
                        String nextWord = new String(arr);
                        // remove on enqueue: doubles as the visited marker
                        if (dictSet.remove(nextWord)) {
                            que.add(nextWord);
                        }
                    }
                    arr[i] = original; // restore before moving to the next position
                }
            }
        }
        return 0; // the queue drained without ever reaching endWord
    }

    private static void check(String label, String beginWord, String endWord,
                              List<String> wordList, int expected) {
        int actual = new WordLadder().ladderLength(beginWord, endWord, wordList);
        System.out.println(label + " " + beginWord + " -> " + endWord
                + " dict=" + wordList + "\n         actual " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: hit -> hot -> dot -> dog -> cog, five words
        check("case 1 classic  ", "hit", "cog",
                Arrays.asList("hot", "dot", "dog", "lot", "log", "cog"), 5);

        // edge case: endWord is not in the dictionary, so the answer is 0
        check("case 2 no endWord", "hit", "cog",
                Arrays.asList("hot", "dot", "dog", "lot", "log"), 0);

        // tricky: every word is present but hot and dog differ in two letters,
        // so the chain is broken and no ladder exists
        check("case 3 no bridge", "hit", "cog",
                Arrays.asList("hot", "dog", "cog"), 0);

        // edge case: one letter apart, the shortest possible real ladder
        check("case 4 one step ", "hot", "dot",
                Arrays.asList("dot", "dog"), 2);
    }
}
