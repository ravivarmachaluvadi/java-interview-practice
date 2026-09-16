import java.util.ArrayList;
import java.util.List;

class KeysAndRooms {

    // if all rooms visited in dfs or not ?
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        boolean[] visited = new boolean[rooms.size()];
        dfs(rooms, 0, visited);

        for (boolean roomVisited : visited) {
            if (!roomVisited) return false;
        }
        return true;
    }

    private void dfs(List<List<Integer>> rooms,
                     int room,
                     boolean[] visited) {
        visited[room] = true;
        for (int key : rooms.get(room)) {
            if (!visited[key]) {
                dfs(rooms, key, visited);
            }
        }
    }

    public static void main(String[] args) {
        KeysAndRooms solution = new KeysAndRooms();

        List<List<Integer>> rooms = new ArrayList<>();
        rooms.add(List.of(1));      // Room 0 has a key to room 1
        rooms.add(List.of(2));      // Room 1 has a key to room 2
        rooms.add(List.of(3));      // Room 2 has a key to room 3
        rooms.add(new ArrayList<>()); // Room 3 has no keys

        System.out.println("Can visit all rooms: " + solution.canVisitAllRooms(rooms)); // Output: true
    }
}
