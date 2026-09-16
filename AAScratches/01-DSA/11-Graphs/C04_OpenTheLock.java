import java.util.*;

// https://leetcode.com/problems/open-the-lock/description/
// 752. Open the Lock
class OpenTheLock {
    public static int openLock(String[] deadends, String target) {
        String start = "0000";
        Set<String> dead = new HashSet<>(Arrays.asList(deadends));
        if (dead.contains(start)) return -1;
        if (start.equals(target)) return 0;

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            moves++;
            for (int i = 0; i < size; i++) {
                String curr = queue.poll();

                for (String next : getNextStates(curr)) {
                    if (visited.contains(next) || dead.contains(next)) continue;
                    if (next.equals(target)) return moves;
                    visited.add(next);
                    queue.offer(next);
                }
            }
        }

        return -1;
    }

    private static List<String> getNextStates(String s) {
        List<String> res = new ArrayList<>();
        char[] chars = s.toCharArray();

        for (int i = 0; i < 4; i++) {
            char original = chars[i];
            // turn forward
            chars[i] = original == '9' ? '0' : (char) (original + 1);
            res.add(new String(chars));
            // turn backward
            chars[i] = original == '0' ? '9' : (char) (original - 1);
            res.add(new String(chars));
            // restore
            chars[i] = original;
        }
        return res;
    }

    public static void main(String[] args) {
        String[] deadends = {"0201", "0101", "0102", "1212", "2002"};
        String target = "0202";
        int result = openLock(deadends, target);
        System.out.println("Minimum moves to unlock: " + result);
        // Expected output: 6
    }
}
