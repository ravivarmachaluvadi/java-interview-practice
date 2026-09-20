/*
 * =====================================================================
 *  Stack Traversal Order                            Java core demo | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   java.util.Stack is a Vector underneath. A for-each loop walks the Vector from index 0
 *   upward, which is BOTTOM to TOP. pop() removes from the end of the Vector, which is TOP to
 *   BOTTOM. The two orders are opposite, and code that iterates a Stack expecting LIFO order
 *   is silently wrong. ArrayDeque used as a stack iterates TOP to BOTTOM, the opposite again.
 *
 * WHAT YOU WILL SEE
 *   push 1,2,3,4 onto a Stack
 *     for-each          -> [1, 2, 3, 4]   bottom to top (Vector index order)
 *     pop until empty   -> [4, 3, 2, 1]   top to bottom (true LIFO)
 *   push 1,2,3,4 onto an ArrayDeque
 *     for-each          -> [4, 3, 2, 1]   head first, and push() inserts at the head
 *   empty and single-element stacks give the same order both ways (nothing to reverse)
 *
 * HOW IT WORKS
 *   1. Stack extends Vector; push(x) is addElement(x) at index size(), so the top is the
 *      LAST index. Iterator goes 0..size-1, so it reaches the top last.
 *   2. pop() reads and removes index size-1, so it drains the top first.
 *   3. ArrayDeque.push(x) is addFirst(x); its iterator starts at the head, so the most
 *      recently pushed element comes out first. descendingIterator() gives bottom to top.
 *
 * KEY INSIGHT
 *   Iteration order of a stack is an implementation detail, not a LIFO guarantee.
 *   When order matters (building a string from leftover characters, printing a path),
 *   decide explicitly: pop() for top-first, for-each over Stack for bottom-first, and never
 *   assume the two agree. Prefer ArrayDeque over Stack in new code (no synchronization cost),
 *   but remember its for-each is top-first.
 *
 * GOTCHAS
 *   - Stack.get(i) and stack.elementAt(i) index from the BOTTOM (index 0 = oldest element).
 *   - Stack.search(x) returns a 1-based distance from the TOP, not an index.
 *   - Stack is synchronized (Vector), so every push/pop takes a lock you did not ask for.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does Java recommend ArrayDeque over Stack? (Vector locking, cleaner Deque API)
 *   - How do you print a Stack top-to-bottom without destroying it? (index from size-1 down,
 *     or ArrayDeque for-each)
 *   - Which order does B01_RemoveAdjacentDuplicates rely on when it rebuilds the string?
 *
 * RUN
 *   main() runs 5 cases (Stack for-each, Stack pop, ArrayDeque for-each, empty, single) and
 *   prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

class StackTraversal {

    // for-each over java.util.Stack walks the backing Vector from index 0: bottom to top.
    static List<String> stackForEachOrder(Stack<String> stack) {
        List<String> order = new ArrayList<>();
        for (String s : stack) {
            order.add(s);
        }
        return order;
    }

    // pop() always returns the most recently pushed element: top to bottom. Empties the stack.
    static List<String> stackPopOrder(Stack<String> stack) {
        List<String> order = new ArrayList<>();
        while (!stack.isEmpty()) {
            order.add(stack.pop());
        }
        return order;
    }

    // ArrayDeque.push() inserts at the head and the iterator starts at the head: top to bottom.
    static List<String> dequeForEachOrder(Deque<String> deque) {
        List<String> order = new ArrayList<>();
        for (String s : deque) {
            order.add(s);
        }
        return order;
    }

    static Stack<String> stackOf(String... items) {
        Stack<String> stack = new Stack<>();
        for (String item : items) {
            stack.push(item);
        }
        return stack;
    }

    static Deque<String> dequeOf(String... items) {
        Deque<String> deque = new ArrayDeque<>();
        for (String item : items) {
            deque.push(item);
        }
        return deque;
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: Stack for-each is bottom-to-top (Vector index order), NOT LIFO
        Stack<String> s1 = stackOf("1", "2", "3", "4");
        print("case 1 Stack for-each     ", stackForEachOrder(s1), "[1, 2, 3, 4]");

        // case 2: Stack pop is top-to-bottom, the true LIFO order
        print("case 2 Stack pop          ", stackPopOrder(s1), "[4, 3, 2, 1]");

        // case 3: ArrayDeque used as a stack iterates top-to-bottom (opposite of Stack)
        Deque<String> d1 = dequeOf("1", "2", "3", "4");
        print("case 3 ArrayDeque for-each", dequeForEachOrder(d1), "[4, 3, 2, 1]");

        // case 4: empty stack, both orders are empty
        print("case 4 empty for-each     ", stackForEachOrder(stackOf()), "[]");
        print("case 4 empty pop          ", stackPopOrder(stackOf()), "[]");

        // case 5: single element, both orders agree because there is nothing to reverse
        print("case 5 single for-each    ", stackForEachOrder(stackOf("x")), "[x]");
        print("case 5 single pop         ", stackPopOrder(stackOf("x")), "[x]");
    }
}
