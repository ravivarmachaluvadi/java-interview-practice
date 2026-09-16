import java.util.*;

class MovieRecommender {

    public static List<String> recommendations(String user, List<List<String>> ratings) {
        // Map: user -> (movie -> rating)
        Map<String, Map<String, Integer>> userToMovies = new HashMap<>();
        // Map: movie -> users who rated it 4 or 5
        Map<String, Set<String>> movieToUsersLiked = new HashMap<>();

        // Step 1: Build data structures
        for (List<String> entry : ratings) {
            String u = entry.get(0);
            String movie = entry.get(1);
            int rating = Integer.parseInt(entry.get(2));

            userToMovies.computeIfAbsent(u, k -> new HashMap<>()).put(movie, rating);

            if (rating >= 4) {
                movieToUsersLiked.computeIfAbsent(movie, k -> new HashSet<>()).add(u);
            }
        }

        // Step 2: Find similar users — those who liked the same movies (4 or 5)
        Set<String> similarUsers = new HashSet<>();
        Map<String, Integer> targetMovies = userToMovies.getOrDefault(user, new HashMap<>());

        for (String movie : targetMovies.keySet()) {
            if (targetMovies.get(movie) >= 4) {
                Set<String> likedUsers = movieToUsersLiked.getOrDefault(movie, new HashSet<>());
                for (String similar : likedUsers) {
                    if (!similar.equals(user)) {
                        similarUsers.add(similar);
                    }
                }
            }
        }

        // Step 3: Collect candidate recommendations
        Set<String> recommended = new HashSet<>();
        for (String simUser : similarUsers) {
            Map<String, Integer> simRatings = userToMovies.get(simUser);
            for (Map.Entry<String, Integer> entry : simRatings.entrySet()) {
                String movie = entry.getKey();
                int rating = entry.getValue();
                // recommend if simUser rated >=4 and target user hasn't rated
                if (rating >= 4 && !targetMovies.containsKey(movie)) {
                    recommended.add(movie);
                }
            }
        }

        return new ArrayList<>(recommended);
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

        System.out.println("Charlie -> " + recommendations("Charlie", ratings)); // [Mad Max]
        System.out.println("Bob -> " + recommendations("Bob", ratings)); // [Inception, Topsy-Turvy]
        System.out.println("Dennis -> " + recommendations("Dennis", ratings)); // [Lost In Translation]
        System.out.println("Alice -> " + recommendations("Alice", ratings)); // []
        System.out.println("Franz -> " + recommendations("Franz", ratings)); // [Inception, All About Eve, Topsy-Turvy]
    }
}
