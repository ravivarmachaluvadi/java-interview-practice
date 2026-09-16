import java.util.Arrays;
import java.util.Comparator;

class Task {
    int deadline;
    int profit;

    Task(int deadline, int profit) {
        this.deadline = deadline;
        this.profit = profit;
    }

    @Override
    public String toString() {
        return "Task{" +
                "deadline=" + deadline +
                ", profit=" + profit +
                '}';
    }
}

class ZOptimalFreelancing {
    public static int maximizeProfit(Task[] tasks) {
        // Sort tasks based on profit in descending order
        Arrays.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return Integer.compare(t2.profit, t1.profit); // Descending order
            }
        });

        int maxDeadline = 0;
        for (Task task : tasks) {
            maxDeadline = Math.max(maxDeadline, task.deadline);
        }

        boolean[] slots = new boolean[maxDeadline + 1]; // To keep track of free time slots
        int totalProfit = 0;

        for (Task task : tasks) {
            // Find a free time slot for this task
            for (int j = Math.min(maxDeadline, task.deadline); j > 0; j--) {
                if (!slots[j]) { // If the slot is free
                    slots[j] = true; // Mark the slot as occupied
                    totalProfit += task.profit; // Add the profit
                    break; // Move to the next task
                }
            }
        }
        return totalProfit;
    }

    public static void main(String[] args) {
        Task[] tasks = {
                new Task(2, 100),
                new Task(1, 19),
                new Task(2, 27),
                new Task(1, 25),
                new Task(3, 15)
        };
        int maxProfit = maximizeProfit(tasks);
        System.out.println("Maximum profit: " + maxProfit);
    }
}
