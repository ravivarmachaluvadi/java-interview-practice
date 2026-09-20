/*
 * =====================================================================
 *  Search Suggestions System                     LeetCode 1268 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of product names and a search word, simulate an autocomplete box:
 *   after every keystroke, return at most 3 products that start with what has been
 *   typed so far, in lexicographic order. The answer is a list of lists, one entry
 *   per prefix of searchWord, so the outer list always has searchWord.length() rows.
 *
 * EXAMPLE
 *   products = [mobile, mouse, moneypot, monitor, mousepad], searchWord = "mouse"
 *     "m"     ->  [mobile, moneypot, monitor]   first 3 in sorted order
 *     "mo"    ->  [mobile, moneypot, monitor]
 *     "mou"   ->  [mouse, mousepad]             only 2 candidates survive
 *     "mous"  ->  [mouse, mousepad]
 *     "mouse" ->  [mouse, mousepad]
 *   products = [apple], searchWord = "ax"  ->  [[apple], []]   (a dead prefix)
 *
 * APPROACH  (two methods, both run from main and compared)
 *   A. suggestedProducts - sort + monotone left pointer (the author's approach)
 *     1. Sort the products once; now every set of prefix matches is a contiguous block.
 *     2. Keep a left pointer at the first product that could still match.
 *     3. For each new prefix, advance left past products that no longer match. Left
 *        never moves back, because a longer prefix can only shrink the block.
 *     4. Take up to 3 products from left, stopping early at the first non-match.
 *   B. suggestedProductsTrie - trie with a cached top-3 per node
 *     1. Insert the sorted products into a trie; at every node on a word's path,
 *        append that word to the node's list if the list holds fewer than 3.
 *        Because insertion order is sorted, the first 3 stored ARE the smallest 3.
 *     2. Walk the searchWord one letter at a time and read the cached list off each
 *        node; once the walk falls off the trie, every remaining row is empty.
 *
 * KEY INSIGHT
 *   Sorting turns "all strings with this prefix" into a contiguous range, and because
 *   prefixes only grow, the left edge of that range only moves right - one pass, not
 *   a fresh scan per keystroke. The trie stores the same answer precomputed at each
 *   node, which is what a real search box does: pay once at build time so each
 *   keystroke is O(1). Pattern: sorted array for a one-off query, trie when the same
 *   product set is queried over and over.
 *
 * COMPLEXITY
 *   Time  A: O(N*L log N) to sort, then O(N + K) for the sweep, K = searchWord length
 *         B: O(N*L) to build (each character visited once, top-3 append is O(1)),
 *            then O(K) to answer - build cost is amortised over many searches
 *   Space A: O(1) beyond the output (sort is in place)
 *         B: O(N*L) trie nodes, each holding at most 3 cached strings
 *
 * INTERVIEW FOLLOW-UPS
 *   - Trie or sorted array? Trie wins when products are fixed and queries are many,
 *     or when products change incrementally; sorting wins for a single query.
 *   - Top 3 by popularity instead of alphabetically: store a size-3 min-heap per node.
 *   - Millions of products that do not fit in memory: shard the trie by first letter.
 *   - Binary search variant: lower-bound the prefix per keystroke, O(K log N) total.
 *
 * RUN
 *   main() runs 4 cases (typical, single product, early dead prefix, one-letter edge)
 *   through BOTH methods and prints actual vs expected for each.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SearchSuggestionsSystem {

    /** Approach A: sort once, then sweep a left pointer that never moves backwards. */
    public static List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Arrays.sort(products);
        List<List<String>> result = new ArrayList<>();
        int n = products.length;
        int left = 0; // first product that might still match the current prefix

        for (int i = 0; i < searchWord.length(); i++) {
            String prefix = searchWord.substring(0, i + 1);

            // the block of matches only shrinks from the left as the prefix grows
            while (left < n && !products[left].startsWith(prefix)) {
                left++;
            }

            List<String> suggestions = new ArrayList<>();
            for (int j = left; j < Math.min(n, left + 3); j++) {
                if (!products[j].startsWith(prefix)) {
                    break; // sorted, so the first miss ends the block
                }
                suggestions.add(products[j]);
            }
            result.add(suggestions);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Approach B: trie whose every node caches the 3 smallest words below it.
    // ------------------------------------------------------------------

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        List<String> top3 = new ArrayList<>(3);
    }

    public static List<List<String>> suggestedProductsTrie(String[] products, String searchWord) {
        String[] sorted = products.clone(); // do not disturb the caller's array
        Arrays.sort(sorted);

        TrieNode root = new TrieNode();
        for (String product : sorted) {
            insert(root, product);
        }

        List<List<String>> result = new ArrayList<>();
        TrieNode node = root;
        for (int i = 0; i < searchWord.length(); i++) {
            if (node != null) {
                node = node.children[searchWord.charAt(i) - 'a'];
            }
            // once the walk falls off the trie it stays off: every later row is empty
            result.add(node == null ? new ArrayList<>() : new ArrayList<>(node.top3));
        }
        return result;
    }

    private static void insert(TrieNode root, String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
            // words arrive in sorted order, so the first 3 recorded are the smallest 3
            if (node.top3.size() < 3) {
                node.top3.add(word);
            }
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void run(int caseNo, String[] products, String searchWord, String expected) {
        print("case " + caseNo + "a sorted+pointer (" + searchWord + ")",
                suggestedProducts(products.clone(), searchWord), expected);
        print("case " + caseNo + "b trie          (" + searchWord + ")",
                suggestedProductsTrie(products, searchWord), expected);
    }

    public static void main(String[] args) {
        // typical: the classic LeetCode example, 5 products, suggestions narrow down
        run(1, new String[]{"mobile", "mouse", "moneypot", "monitor", "mousepad"}, "mouse",
                "[[mobile, moneypot, monitor], [mobile, moneypot, monitor], "
                        + "[mouse, mousepad], [mouse, mousepad], [mouse, mousepad]]");

        // edge: a single product that matches every prefix typed
        run(2, new String[]{"havana"}, "havana",
                "[[havana], [havana], [havana], [havana], [havana], [havana]]");

        // tricky: the prefix dies on the second keystroke, so the rest are empty
        run(3, new String[]{"apple"}, "ax", "[[apple], []]");

        // edge: one-letter search word over products that share it
        run(4, new String[]{"bags", "baggage", "banner", "box", "cloths"}, "b",
                "[[baggage, bags, banner]]");
    }
}
