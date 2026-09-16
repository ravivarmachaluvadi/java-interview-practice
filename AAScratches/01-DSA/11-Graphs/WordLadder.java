import java.util.*;

// Hash Table, String, Breadth-First Search
// https://leetcode.com/problems/word-ladder
// beginWord -> s1 -> s2 -> ... -> sk
// minimum length of shortest transformation sequence
// https://leetcode.com/problems/word-ladder/description/
class WordLadder {

    // BFS(Breadth First Traversal) or Level to Level traversing
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(endWord)) return 0;

        int cnt = 0;
        Set<String> dictSet = new HashSet<>(wordList);
        Queue<String> que = new LinkedList<>();

        que.add(beginWord);

        while (!que.isEmpty()) {
            cnt++;
            int size = que.size();

            // BFS Traversal
            for (int k = 0; k < size; k++) {
                String word = que.poll();
                if (word.equals(endWord)) return cnt;

                for (int i = 0; i < word.length(); i++) {
                    for (char ch = 'a'; ch <= 'z'; ch++) {
//keeping every other character is same and chaging one with all possibilities
                        char[] arr = word.toCharArray();
                        arr[i] = ch;
                        String nextWord = new String(arr);
                        if (dictSet.contains(nextWord)) {
                            que.add(nextWord);
                            dictSet.remove(nextWord);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        WordLadder wl = new WordLadder();
        String beginWord = "hit";
        String endWord = "cog";
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");

        int result = wl.ladderLength(beginWord, endWord, wordList);
        System.out.println("Length of shortest transformation sequence: " + result);
    }
}
