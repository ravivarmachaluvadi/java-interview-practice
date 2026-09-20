/*
 * =====================================================================
 *  Design Snake Game                              LeetCode 353 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A snake starts at cell (0, 0) of a height x width grid, one cell long. Food appears
 *   one piece at a time at the positions in food[], in that exact order. Each move("U" |
 *   "D" | "L" | "R") shifts the head one cell; eating the current food grows the snake by
 *   one and scores a point. Return the score, or -1 once the snake leaves the board or
 *   bites its own body -- and -1 for every move after that.
 *
 * EXAMPLE
 *   width 3, height 2, food [[1,2],[0,1]]; moves R D R U L U -> 0, 0, 1, 1, 2, -1
 *   (the last move walks off the top edge)
 *   width 3, height 3, food [[1,0],[1,1]]; moves D R L -> 1, 2, -1
 *   (after growing to length 3 the snake reverses into its own neck)
 *
 * DESIGN
 *   Cell encoding     (row, col) is stored as the single int row * width + col, so the
 *                     body needs no Point class.
 *   snake: Deque      head at the FIRST end, tail at the LAST: addFirst() each move,
 *                     pollLast() when not growing. Only a deque makes both ends O(1).
 *   occupied: Set     the same cells, kept in sync, so the self-bite test is O(1).
 *   foodIndex         pieces eaten so far; food[foodIndex] is the only one on the board.
 *   score             doubles as the game-over sentinel -- once -1, move() short-circuits.
 *
 * KEY DECISIONS
 *   - Remove the tail BEFORE testing for a self-bite: the tail vacates its cell in the
 *     same move, so stepping onto the old tail cell is legal. Testing first would report
 *     a phantom collision on every tight turn.
 *   - Do not remove the tail when eating -- that is precisely what "grow by one" means.
 *   - Two structures for one list of cells, because neither a deque nor a set alone gives
 *     both ordered ends and O(1) membership.
 *
 * KEY INSIGHT
 *   The snake IS a queue of cells: the head is an enqueue, the tail a dequeue, and growing
 *   just means skipping the dequeue. Pair the deque with a hash set for O(1) membership
 *   and flatten each cell to row * width + col. The difficulty is the ORDER of the
 *   checks, not the data structures.
 *
 * COMPLEXITY
 *   Time  O(1) per move -- one push, at most one pop, constant-time set work.
 *   Space O(min(width * height, food.length + 1)), held twice (deque and set).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is stepping onto the current tail cell safe, and when is it not?
 *   - Support food appearing at random free cells instead of a fixed list.
 *   - Add the rule "the snake may not reverse straight back": track the last direction.
 *   - Wrap-around (torus) edges instead of walls: which check disappears?
 *
 * RUN
 *   main() runs 3 games (typical walkthrough, a self-bite, and a 1x1 board where the
 *   first move is fatal) and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

class DesignSnakeGame {
    private final int width;
    private final int height;
    private final int[][] food;

    private int foodIndex = 0;
    private int score = 0;                  // -1 also means "game over"

    private final Deque<Integer> snake;     // head first, tail last
    private final Set<Integer> occupied;    // same cells, for O(1) self-bite checks

    public DesignSnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        this.snake = new ArrayDeque<>();
        this.occupied = new HashSet<>();
        snake.addLast(encode(0, 0));        // the snake starts at the top-left cell
        occupied.add(encode(0, 0));
    }

    /** Packs a cell into one int so the body can live in int collections. */
    private int encode(int row, int col) {
        return row * width + col;
    }

    public int move(String direction) {
        if (score == -1) {
            return -1;                      // already dead; every later move is -1 too
        }

        int head = snake.peekFirst();
        int row = head / width;
        int col = head % width;

        switch (direction) {
            case "U" -> row--;              // row 0 is the TOP row, so up decreases row
            case "D" -> row++;
            case "L" -> col--;
            case "R" -> col++;
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        if (row < 0 || row >= height || col < 0 || col >= width) {
            score = -1;                     // walked off the board
            return -1;
        }

        // Only food[foodIndex] is on the board right now.
        boolean eating = foodIndex < food.length
                && food[foodIndex][0] == row
                && food[foodIndex][1] == col;
        if (eating) {
            score++;
            foodIndex++;
        } else {
            // Not growing: the tail vacates its cell BEFORE the collision test below,
            // which is what makes a tight turn onto the old tail cell legal.
            occupied.remove(snake.pollLast());
        }

        int newHead = encode(row, col);
        if (occupied.contains(newHead)) {
            score = -1;                     // bit its own body
            return -1;
        }

        snake.addFirst(newHead);
        occupied.add(newHead);
        return score;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // ---- case 1: typical walkthrough on a 3 x 2 board -----------------
        DesignSnakeGame game = new DesignSnakeGame(3, 2, new int[][]{{1, 2}, {0, 1}});
        print("case 1 move R", game.move("R"), 0);
        print("case 1 move D", game.move("D"), 0);
        print("case 1 move R", game.move("R"), 1);   // eats food at (1,2)
        print("case 1 move U", game.move("U"), 1);
        print("case 1 move L", game.move("L"), 2);   // eats food at (0,1)
        print("case 1 move U", game.move("U"), -1);  // off the top edge
        print("case 1 move after death", game.move("D"), -1);

        // ---- case 2: tricky -- grow to length 3, then reverse into itself --
        DesignSnakeGame biter = new DesignSnakeGame(3, 3, new int[][]{{1, 0}, {1, 1}});
        print("case 2 move D", biter.move("D"), 1);  // eats (1,0), length 2
        print("case 2 move R", biter.move("R"), 2);  // eats (1,1), length 3
        print("case 2 move L", biter.move("L"), -1); // back onto its own neck

        // ---- case 3: edge case -- 1 x 1 board, nowhere legal to go --------
        DesignSnakeGame tiny = new DesignSnakeGame(1, 1, new int[][]{});
        print("case 3 move R", tiny.move("R"), -1);
    }
}
