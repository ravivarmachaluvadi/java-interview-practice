/*
 * =====================================================================
 *  Movie Recommender (collaborative filtering)          Karat | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given a rating log: each entry is [user, movie, rating] with rating 1..5.
 *   For a target user, recommend movies that "similar" users liked but the target has
 *   not rated at all. You must define "similar" and "liked" yourself and defend them.
 *
 *   Rules chosen here (state these out loud in the interview):
 *     - "liked"   = rating >= 4.
 *     - "similar" = any OTHER user who also liked at least one movie the target liked.
 *     - a movie is recommendable only if the target has no rating for it (not even a 1).
 *     - the result is returned sorted alphabetically so the output is deterministic.
 *
 * EXAMPLE
 *   Charlie liked Lost In Translation and Inception.
 *   Bob and Franz also liked Lost In Translation, so they are Charlie's similar users.
 *   Both of them liked Mad Max, which Charlie never rated.
 *   recommendations("Charlie", log)  ->  [Mad Max]
 *
 *   recommendations("Alice", log)    ->  []
 *     Alice only liked Frozen, which nobody else rated, so she has no similar users.
 *
 * APPROACH  (two inverted indexes, then a two-hop traversal)
 *   1. Pass over the log once and build two maps:
 *        userToRatings : user  -> (movie -> rating)     "what did this user rate?"
 *        movieToLikers : movie -> set of users who rated it >= 4
 *   2. Hop 1: for every movie the TARGET liked, read movieToLikers to collect the
 *      similar users. Drop the target itself.
 *   3. Hop 2: for every similar user, walk their ratings and keep every movie they
 *      liked that the target has not rated.
 *   4. Sort the collected set and return it.
 *
 * KEY INSIGHT
 *   Recommendation here is just a two-hop walk on a bipartite graph of users and movies:
 *     target -> movies they liked -> other users who liked them -> movies THOSE users liked.
 *   The inverted index movie -> likers is what makes the middle hop O(1) instead of a
 *   rescan of the whole log. Whenever a problem says "people like you also liked X",
 *   reach for the inverted index and the two-hop pattern.
 *
 * COMPLEXITY
 *   Let R = number of log entries, and let the target have L liked movies.
 *   Time  O(R + sum of ratings of the similar users + M log M)
 *         building the indexes is one pass over R; the two hops only touch entries that
 *         are reachable from the target; M is the number of recommended movies we sort.
 *         Worst case (everyone likes everything) this degrades to O(R + M log M).
 *   Space O(R)  both indexes together store every log entry once.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rank the results instead of returning a set: score each movie by how many similar
 *     users liked it, or by average rating, then sort by score descending.
 *   - Weight similarity: a user who overlaps on 5 liked movies should count more than
 *     one who overlaps on 1. That is cosine similarity / Jaccard on the liked sets.
 *   - Exclude a movie the target rated 1-3, which we already do, versus re-recommending
 *     it later. Which is right depends on whether a low rating means "seen and disliked".
 *   - Scale: millions of users means you cannot expand every similar user online. Use
 *     min-hash / LSH to shortlist neighbours, or precompute item-item similarity offline.
 *
 * RUN
 *   main() runs 7 cases: four ordinary users (Charlie, Bob, Dennis, Franz), a user
 *   with no similar users (Alice), an unknown user, and an empty log. Each prints
 *   actual vs expected on one line.
 */

import java.util.*;

class MovieRecommender {

    private static final int LIKE_THRESHOLD = 4;

    /**
     * Returns the alphabetically sorted movies that users similar to {@code user} liked
     * and that {@code user} has not rated.
     */
    public static List<String> recommendations(String user, List<List<String>> ratings) {
        Map<String, Map<String, Integer>> userToRatings = new HashMap<>();
        Map<String, Set<String>> movieToLikers = new HashMap<>();
        buildIndexes(ratings, userToRatings, movieToLikers);

        // Everything the target has already rated, at any score. Used twice below.
        Map<String, Integer> targetRatings = userToRatings.getOrDefault(user, Map.of());

        Set<String> similarUsers = findSimilarUsers(user, targetRatings, movieToLikers);

        Set<String> recommended = new HashSet<>();
        for (String similarUser : similarUsers) {
            for (Map.Entry<String, Integer> rated : userToRatings.get(similarUser).entrySet()) {
                boolean theyLikedIt = rated.getValue() >= LIKE_THRESHOLD;
                boolean targetHasNotSeenIt = !targetRatings.containsKey(rated.getKey());
                if (theyLikedIt && targetHasNotSeenIt) {
                    recommended.add(rated.getKey());
                }
            }
        }

        // Fixed: the original returned new ArrayList<>(hashSet), so the order changed
        // between runs and the printed answer could not be compared to an expected value.
        List<String> result = new ArrayList<>(recommended);
        Collections.sort(result);
        return result;
    }

    /** One pass over the log fills both inverted indexes. */
    private static void buildIndexes(List<List<String>> ratings,
                                     Map<String, Map<String, Integer>> userToRatings,
                                     Map<String, Set<String>> movieToLikers) {
        for (List<String> entry : ratings) {
            String user = entry.get(0);
            String movie = entry.get(1);
            int score = Integer.parseInt(entry.get(2));

            userToRatings.computeIfAbsent(user, k -> new HashMap<>()).put(movie, score);

            if (score >= LIKE_THRESHOLD) {
                movieToLikers.computeIfAbsent(movie, k -> new HashSet<>()).add(user);
            }
        }
    }

    /** Hop 1: anyone who liked a movie the target liked, excluding the target. */
    private static Set<String> findSimilarUsers(String user,
                                                Map<String, Integer> targetRatings,
                                                Map<String, Set<String>> movieToLikers) {
        Set<String> similarUsers = new HashSet<>();
        for (Map.Entry<String, Integer> rated : targetRatings.entrySet()) {
            if (rated.getValue() < LIKE_THRESHOLD) continue;          // target did not like it
            for (String liker : movieToLikers.getOrDefault(rated.getKey(), Set.of())) {
                if (!liker.equals(user)) {
                    similarUsers.add(liker);
                }
            }
        }
        return similarUsers;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        List<List<String>> ratings = Arrays.asList(
                Arrays.asList("Alice", "Frozen", "5"),
                Arrays.asList("Bob", "Mad Max", "5"),
                Arrays.asList("Charlie", "Lost In Translation", "4"),
                Arrays.asList("Charlie", "Inception", "4"),
                Arrays.asList("Bob", "All About Eve", "3"),
                Arrays.asList("Bob", "Lost In Translation", "5"),
                Arrays.asList("Dennis", "All About Eve", "5"),
                Arrays.asList("Dennis", "Mad Max", "4"),
                Arrays.asList("Charlie", "Topsy-Turvy", "2"),
                Arrays.asList("Dennis", "Topsy-Turvy", "4"),
                Arrays.asList("Alice", "Lost In Translation", "1"),
                Arrays.asList("Franz", "Lost In Translation", "5"),
                Arrays.asList("Franz", "Mad Max", "5")
        );

        // typical: one similar-user hop lands on exactly one unseen movie
        print("case 1 Charlie", recommendations("Charlie", ratings), "[Mad Max]");

        // Bob rated All About Eve a 3, so Dennis liking it does NOT make it a recommendation
        print("case 2 Bob", recommendations("Bob", ratings), "[Inception, Topsy-Turvy]");

        print("case 3 Dennis", recommendations("Dennis", ratings), "[Lost In Translation]");

        // edge: Alice's only liked movie was rated by nobody else -> no similar users
        print("case 4 Alice", recommendations("Alice", ratings), "[]");

        print("case 5 Franz", recommendations("Franz", ratings),
                "[All About Eve, Inception, Topsy-Turvy]");

        // edge: a user who is not in the log at all must not blow up
        print("case 6 unknown user", recommendations("Zoe", ratings), "[]");

        // edge: empty log
        print("case 7 empty log", recommendations("Alice", List.of()), "[]");
    }
}
