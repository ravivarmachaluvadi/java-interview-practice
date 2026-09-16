import java.util.Collections;
import java.util.PriorityQueue;

class ImportantMedianOfStream {
    private PriorityQueue<Integer> left = new PriorityQueue<>(Collections.reverseOrder());
    private PriorityQueue<Integer> right = new PriorityQueue<>();

    public void addNum(int num) {
        if (left.isEmpty() || left.peek() > num) left.offer(num);
        else right.offer(num);

        if (left.size() - right.size() > 1) right.offer(left.poll());
        else if (left.size() < right.size()) left.offer(right.poll());
    }

    public double findMedian() {
        if (left.size() == right.size())
            return (left.peek() + right.peek()) / 2.0;
        else
            return (double) left.peek();
    }
}
