import java.util.*;


class TopVideos {

    public static List<String> topVideos(List<Pair<String, Integer>> videoList) {
        // Step 1: Aggregate watch rates
        Map<String, Integer> watchRates = new HashMap<>();

        for (Pair<String, Integer> video : videoList) {
            watchRates.put(video.getKey(), watchRates.getOrDefault(video.getKey(), 0) + video.getValue());
        }

        // Step 2: Sort the entries by watch rates
        List<Map.Entry<String, Integer>> sortedVideos = new ArrayList<>(watchRates.entrySet());
        sortedVideos.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Sort in descending order

        // Step 3: Extract the names of the top 10 videos
        List<String> top10Videos = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedVideos.size()); i++) {
            top10Videos.add(sortedVideos.get(i).getKey());
        }

        return top10Videos;
    }

    public static void main(String[] args) {
        // Example usage
        List<Pair<String, Integer>> videoList = new ArrayList<>();
        videoList.add(new Pair<>("abc", 10));
        videoList.add(new Pair<>("def", 15));
        videoList.add(new Pair<>("ghi", 10));
        videoList.add(new Pair<>("abc", 12));
        videoList.add(new Pair<>("xyz", 100));
        videoList.add(new Pair<>("abc", 5));
        videoList.add(new Pair<>("def", 20));

        List<String> topVideos = topVideos(videoList);
        System.out.println("Top 10 videos: " + topVideos);
    }
}

// Simple Pair class for holding video names and watch rates
class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
