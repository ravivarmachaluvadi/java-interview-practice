/*
 * =====================================================================
 *  Remove Invalid Parentheses                       LeetCode 301 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of '(', ')' and lowercase letters, remove the MINIMUM number
 *   of parentheses so the string becomes valid, and return every distinct string
 *   you can reach with that minimum number of removals.
 *   Letters are never removed. "" counts as valid, so an answer always exists.
 *
 * EXAMPLE
 *   "()())()"  ->  ["(())()", "()()()"]   one removal is enough
 *   "(a)())()" ->  ["(a())()", "(a)()()"]  letters just ride along
 *   ")("       ->  [""]                    edge case: everything must go
 *   "abc"      ->  ["abc"]                 edge case: already valid, zero removals
 *
 * APPROACH  (BFS by removal count - shortest path over strings)
 *   1. Treat each string as a graph node; an edge deletes exactly one paren.
 *      Level 0 is the input, level 1 is "one removal", and so on.
 *   2. BFS from the input with a visited set so the same string is never queued
 *      twice (this is what removes duplicate answers).
 *   3. Check every string popped from the queue with isValid(). The first valid
 *      one sits at the minimum removal count, so set foundValid = true.
 *   4. Once foundValid is set, stop EXPANDING but keep POPPING: the rest of that
 *      level is still in the queue and those strings are equally good answers.
 *
 * KEY INSIGHT
 *   "Minimum number of removals" is a shortest-path question, and BFS finds
 *   shortest paths - so the level where the first valid string appears is the
 *   whole answer set. Stopping expansion instead of stopping the loop is the
 *   detail people get wrong.
 *   Why nothing deeper can sneak in: a valid string has equal '(' and ')', so at
 *   the winning level the paren count is even; one level deeper it is odd and can
 *   never balance. Parity, not luck, makes the early exit safe.
 *
 * COMPLEXITY
 *   Time  O(2^n * n)  worst case every subset of n parens is generated, and each
 *                     isValid / substring pass costs O(n)
 *   Space O(2^n)      the queue plus the visited set
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return only ONE valid result: a single left-to-right pass that drops unmatched
 *     ')' then unmatched '(' does it in O(n) time and O(1) extra space.
 *   - Count the minimum removals only (LeetCode 921 / 1249) - a counter, no search.
 *   - Why is DFS with a precomputed (misplacedOpen, misplacedClose) pair faster here?
 *   - Extend to several bracket types - then a stack replaces the integer balance.
 *
 * RUN
 *   main() runs 4 cases (typical, letters, worst case, already valid) and prints
 *   the sorted result against the expected list.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class RemoveInvalidParentheses {

    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        if (s == null) {
            return result;
        }

        Queue<String> queue = new LinkedList<>(); // BFS frontier
        Set<String> visited = new HashSet<>();    // dedupes identical strings

        queue.add(s);
        visited.add(s);

        // Flips the moment we reach the first (i.e. shallowest) valid string.
        boolean foundValid = false;

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (isValid(current)) {
                result.add(current);
                foundValid = true;
            }

            // Keep draining the queue so the rest of THIS level is still checked,
            // but never create a deeper level once the minimum has been found.
            if (foundValid) {
                continue;
            }

            // One child per removable parenthesis.
            for (int i = 0; i < current.length(); i++) {
                if (current.charAt(i) == '(' || current.charAt(i) == ')') {
                    // substring's second index is exclusive, so this drops char i
                    String next = current.substring(0, i) + current.substring(i + 1);
                    if (visited.add(next)) { // add() returns false if already seen
                        queue.add(next);
                    }
                }
            }
        }
        return result;
    }

    private static boolean isValid(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // A negative balance means a ')' arrived with no '(' waiting for it;
            // that can never be repaired by anything later in the string.
            // A positive balance is fine mid-string - a ')' may still show up.
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }

    /**
     * BFS order depends on queue order, so sort before printing. Results are also
     * quoted, otherwise the single empty-string answer would print as "[]" and be
     * indistinguishable from "no answers at all".
     */
    private static String sortedAndQuoted(String s) {
        List<String> out = removeInvalidParentheses(s);
        Collections.sort(out);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < out.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('"').append(out.get(i)).append('"');
        }
        return sb.append(']').toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"()())()\"", sortedAndQuoted("()())()"), "[\"(())()\", \"()()()\"]");
        print("case 2 \"(a)())()\"", sortedAndQuoted("(a)())()"), "[\"(a())()\", \"(a)()()\"]");
        print("case 3 \")(\"  (all parens removed)", sortedAndQuoted(")("), "[\"\"]");
        print("case 4 \"abc\" (already valid)", sortedAndQuoted("abc"), "[\"abc\"]");
    }
}
