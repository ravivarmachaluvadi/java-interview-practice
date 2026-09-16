import java.util.*;

// https://leetcode.com/problems/stock-price-fluctuation/description/
// 2034. Stock Price Fluctuation
class Price {
    int timestamp, price;

    public Price(int timestamp, int price) {
        this.timestamp = timestamp;
        this.price = price;
    }
}

class StockPrice {

    int latestTimestamp;
    Map<Integer, Price> timestampToPricesMap;

    // Min-heap for minimum price
    PriorityQueue<Price> minPQ = new PriorityQueue<>(
            Comparator.comparingInt(a -> a.price)
    );

    // Max-heap for maximum price
    PriorityQueue<Price> maxPQ = new PriorityQueue<>(
            (a, b) -> Integer.compare(b.price, a.price)
    );

    public StockPrice() {
        this.latestTimestamp = 0;
        this.timestampToPricesMap = new HashMap<>();
    }

    public void update(int timestamp, int price) {
        Price stockPrice = new Price(timestamp, price);
        timestampToPricesMap.put(timestamp, stockPrice);
        // Add to both heaps
        minPQ.add(stockPrice);
        maxPQ.add(stockPrice);
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timestampToPricesMap.get(latestTimestamp).price;
    }

    public int maximum() {
        // Remove outdated entries until top is valid
        while (!maxPQ.isEmpty()) {
            Price top = maxPQ.peek();
            if (timestampToPricesMap.get(top.timestamp).price == top.price) {
                return top.price;
            }
            maxPQ.poll();
        }
        return -1; // should not happen
    }

    public int minimum() {
        // Remove outdated entries until top is valid
        while (!minPQ.isEmpty()) {
            Price top = minPQ.peek();
            if (timestampToPricesMap.get(top.timestamp).price == top.price) {
                return top.price;
            }
            minPQ.poll();
        }
        return -1; // should not happen
    }
}

class StockPriceFluctuation {
    public static void main(String[] args) {
        StockPrice stockPrice = new StockPrice();

        stockPrice.update(1, 10);   // timestamp 1 -> price 10
        stockPrice.update(2, 5);    // timestamp 2 -> price 5
        System.out.println("Current Price: " + stockPrice.current());  // 5
        System.out.println("Maximum Price: " + stockPrice.maximum());  // 10
        System.out.println("Minimum Price: " + stockPrice.minimum());  // 5

        stockPrice.update(1, 3);    // update timestamp 1 -> price 3
        System.out.println("Maximum Price after update: " + stockPrice.maximum());  // 5
        System.out.println("Current Price: " + stockPrice.current());  // 5

        stockPrice.update(4, 2);    // new timestamp 4 -> price 2
        System.out.println("Minimum Price after adding 2: " + stockPrice.minimum());  // 2
    }
}
