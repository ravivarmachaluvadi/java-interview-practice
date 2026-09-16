import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

// 353. Design Snake Game
// https://leetcode.com/problems/design-snake-game/description/
class DesignSnakeGame {
    private final int width;
    private final int height;
    private final int[][] food;
    private int foodIndex = 0;
    private int score = 0;

    private final Deque<Integer> snake; // stores head to tail
    private final Set<Integer> occupied; // stores all snake body positions for quick lookup

    public DesignSnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        this.snake = new ArrayDeque<>();
        this.occupied = new HashSet<>();
        // Initial position: (0,0)
        snake.addLast(0);
        occupied.add(0);
    }

    public int move(String direction) {
        if (score == -1) {
            return -1; // game already over
        }

        // current head
        int head = snake.peekFirst();
        int headRow = head / width;
        int headCol = head % width;

        // compute new head position
        switch (direction) {
            // -- goes up not down
            case "U" -> headRow--;
            case "D" -> headRow++;
            case "L" -> headCol--;
            case "R" -> headCol++;
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        }


        // check boundary
        if (headRow < 0 || headRow >= height || headCol < 0 || headCol >= width) {
            score = -1;
            return -1;
        }

        // check if eating food
        boolean eating = false;
        if (foodIndex < food.length &&
                food[foodIndex][0] == headRow &&
                food[foodIndex][1] == headCol) {
            eating = true;
            score++;
            foodIndex++;
        }

        // remove tail if not eating
        if (!eating) {
            int tail = snake.pollLast();
            occupied.remove(tail);
        }

        int newHead = (headRow * width) + headCol;
        // check self-collision
        if (occupied.contains(newHead)) {
            score = -1;
            return -1;
        }

        // add new head
        // make sure you are adding at first
        snake.addFirst(newHead);
        occupied.add(newHead);

        return score;
    }

    // -------------------- MAIN FUNCTION --------------------
    public static void main(String[] args) {
        int width = 3;
        int height = 2;
        int[][] food = {{1, 2}, {0, 1}};
        DesignSnakeGame game = new DesignSnakeGame(width, height, food);

        System.out.println(game.move("R")); // 0
        System.out.println(game.move("D")); // 0
        System.out.println(game.move("R")); // 1 -> eats first food
        System.out.println(game.move("U")); // 1
        System.out.println(game.move("L")); // 2 -> eats second food
        System.out.println(game.move("U")); // -1 -> game over (out of bounds)
    }
}
