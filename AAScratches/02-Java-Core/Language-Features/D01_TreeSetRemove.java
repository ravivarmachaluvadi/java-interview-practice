/*
 * =====================================================================
 *  TreeSet uses compareTo, never equals          Java core | Hard
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A Node has a vertex id and a distance. equals/hashCode compare the VERTEX (two nodes
 *   are the same graph node), but compareTo compares the DISTANCE (so the set is ordered
 *   by distance, which is what Dijkstra wants). Those two answers disagree, and a sorted
 *   collection believes only compareTo. This is the classic decrease-key bug.
 *
 * WHAT YOU WILL SEE
 *   case 1  adding vertex 4 at distance 10 to a set that already holds vertex 0 at
 *           distance 10 is REJECTED as a duplicate - the set silently loses a node.
 *   case 2  remove(new Node(2, 99)) returns false even though equals() says that object
 *           is vertex 2, because no element compares equal to distance 99.
 *   case 3  the correct decrease-key: remove with the OLD distance, then add the new one.
 *   case 4  the fix - a comparator that breaks ties on vertex, so ordering agrees
 *           with equals and both traps disappear.
 *
 * HOW IT WORKS
 *   TreeSet is a TreeMap of red-black nodes. Every add, remove and contains walks the
 *   tree using compareTo (or the supplied Comparator) and stops when it reads 0. It never
 *   calls equals or hashCode. So "this element is already here" and "this is the element
 *   you asked me to delete" both mean "compareTo returned 0", nothing else.
 *
 * KEY INSIGHT
 *   A sorted collection's notion of identity IS its comparator. If compareTo can return 0
 *   for two objects that are not equals(), a TreeSet will drop one of them; if it can
 *   return non-zero for two objects that ARE equals(), you cannot remove by identity. Make
 *   the comparator a total order that ends in a unique tie-breaker (here, the vertex id).
 *   Same reasoning applies to PriorityQueue.remove, which instead uses equals and is O(n).
 *
 * GOTCHAS
 *   - The Comparable javadoc calls "consistent with equals" strongly recommended, not
 *     required - so the compiler will never warn you about this.
 *   - contains() has the same blind spot: a List would find vertex 2 by equals, the
 *     TreeSet will not.
 *   - Mutating an element's distance while it sits in the set corrupts the tree: the
 *     element is now in the wrong place and may be unreachable. Always remove, mutate,
 *     re-add.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you implement decrease-key with a PriorityQueue instead? (lazy deletion:
 *     push the new pair and skip stale pops with a seen[] or a best-distance check)
 *   - Why is PriorityQueue.remove(Object) O(n) while TreeSet.remove is O(log n)?
 *   - When is a comparator inconsistent with equals actually fine? (BigDecimal in a
 *     TreeSet: 1.0 and 1.00 collapse, which may be exactly what you want)
 *   - What breaks if compareTo is not a total order, e.g. it is not transitive?
 *
 * RUN
 *   main() runs 4 cases (silent duplicate, failed remove, correct decrease-key, fixed
 *   comparator) and prints actual vs expected.
 */

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

class TreeSetRemoveExample {

    /** A graph node: ordered by distance, but identified by vertex. */
    static class Node implements Comparable<Node> {
        final int vertex;
        final int distance;

        public Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            // Ordering is by distance only - this is the line that causes every trap below.
            return Integer.compare(this.distance, other.distance);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return vertex == node.vertex;     // identity is the vertex, not the distance
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertex);
        }

        @Override
        public String toString() {
            return "v" + vertex + ":d" + distance;
        }
    }

    /** Consistent with equals: order by distance, then break ties on the vertex id. */
    static final Comparator<Node> BY_DISTANCE_THEN_VERTEX =
            Comparator.comparingInt((Node n) -> n.distance).thenComparingInt(n -> n.vertex);

    private static TreeSet<Node> freshSet() {
        TreeSet<Node> set = new TreeSet<>();
        set.add(new Node(0, 10));
        set.add(new Node(1, 20));
        set.add(new Node(2, 15));
        set.add(new Node(3, 5));
        return set;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        TreeSet<Node> set = freshSet();
        print("initial set", set, "[v3:d5, v0:d10, v2:d15, v1:d20]");

        // ---- case 1: a distinct vertex that ties on distance is swallowed -------------
        boolean added = set.add(new Node(4, 10));   // same distance as vertex 0
        print("case 1 add(v4:d10)", added, false);
        print("case 1 size after add", set.size(), 4);
        print("case 1 set", set, "[v3:d5, v0:d10, v2:d15, v1:d20]");

        // ---- case 2: equals() says yes, the tree search says no ----------------------
        Node lookalike = new Node(2, 99);           // equals() vertex 2, wrong distance
        print("case 2 equals(v2:d15)", lookalike.equals(new Node(2, 15)), true);
        print("case 2 list contains", List.copyOf(set).contains(lookalike), true);
        print("case 2 treeSet contains", set.contains(lookalike), false);
        print("case 2 remove", set.remove(lookalike), false);
        print("case 2 size", set.size(), 4);

        // ---- case 3: decrease-key done right - remove with the OLD key, then add ------
        boolean removed = set.remove(new Node(2, 15));   // exact old distance
        set.add(new Node(2, 7));                         // reinsert at the new distance
        print("case 3 remove(v2:d15)", removed, true);
        print("case 3 set", set, "[v3:d5, v2:d7, v0:d10, v1:d20]");

        // ---- case 4: the fix - ordering consistent with equals -----------------------
        TreeSet<Node> fixed = new TreeSet<>(BY_DISTANCE_THEN_VERTEX);
        fixed.add(new Node(0, 10));
        fixed.add(new Node(1, 20));
        fixed.add(new Node(2, 15));
        fixed.add(new Node(3, 5));
        print("case 4 add(v4:d10)", fixed.add(new Node(4, 10)), true);
        print("case 4 set", fixed, "[v3:d5, v0:d10, v4:d10, v2:d15, v1:d20]");
        print("case 4 remove(v2:d99)", fixed.remove(new Node(2, 99)), false);
        print("case 4 remove(v2:d15)", fixed.remove(new Node(2, 15)), true);
        print("case 4 final", fixed, "[v3:d5, v0:d10, v4:d10, v1:d20]");
    }
}
