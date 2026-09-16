# Kafka Q&A

Interview-prep notes on Apache Kafka: exactly-once semantics and ordering, transactions and the outbox pattern, producer and consumer configuration, throughput tuning, and why Kafka is fast.

## Table of Contents

1. [How do you ensure exactly-once semantics with ordering of messages in Kafka in real time, and where and when do you use them?](#1-how-do-you-ensure-exactly-once-semantics-with-ordering-of-messages-in-kafka-in-real-time-and-where-and-when-do-you-use-them)
2. [Transaction boundaries across services: how do you guarantee consistency when writing to the DB and publishing an event?](#2-transaction-boundaries-across-services-how-do-you-guarantee-consistency-when-writing-to-the-db-and-publishing-an-event)
3. [How do you write a basic Kafka producer with a send callback?](#3-how-do-you-write-a-basic-kafka-producer-with-a-send-callback)
4. [What happens when a Kafka consumer takes longer than max.poll.interval.ms? How do you handle it?](#4-what-happens-when-a-kafka-consumer-takes-longer-than-maxpollintervalms-how-do-you-handle-it)
5. [How do you implement idempotent producers in Kafka?](#5-how-do-you-implement-idempotent-producers-in-kafka)
6. [How do you ensure exactly-once semantics (EOS) in Kafka Streams or Spring Kafka?](#6-how-do-you-ensure-exactly-once-semantics-eos-in-kafka-streams-or-spring-kafka)
7. [How do you tune Kafka consumer throughput?](#7-how-do-you-tune-kafka-consumer-throughput)
8. [How do you design a scalable Kafka-based microservice system?](#8-how-do-you-design-a-scalable-kafka-based-microservice-system)
9. [What is a Kafka transaction and why is it useful?](#9-what-is-a-kafka-transaction-and-why-is-it-useful)
10. [What is acks in the Kafka producer and what are its levels?](#10-what-is-acks-in-the-kafka-producer-and-what-are-its-levels)
11. [Why is Kafka fast?](#11-why-is-kafka-fast)

## 1. How do you ensure exactly-once semantics with ordering of messages in Kafka in real time, and where and when do you use them?

Excellent question — this one goes to the heart of designing reliable, real-time, ordered Kafka pipelines (critical for fintech, inventory, or payment systems). Let's break this down step by step.

### The Core Challenge

In Kafka, we usually aim for:

- **Exactly-once semantics (EOS)** — each message is processed once and only once (no duplicates, no loss).
- **Ordering guarantees** — messages are processed in the order they were produced.

Achieving both together in real-time streaming pipelines is tricky because failures, retries, batching, and distributed consumers can break both guarantees if not handled carefully.

### Kafka Guarantees: Quick Recap

| Guarantee | Default Support | Notes |
| --- | --- | --- |
| **At-most-once** | Simple (no retries) | Possible message loss |
| **At-least-once** | Default | Possible duplicates |
| **Exactly-once** | With idempotence & transactions | Introduced in Kafka 0.11+ |
| **Ordering** | Within a **partition** | Cross-partition order not guaranteed |

### Achieving Exactly-Once + Ordering Together

You can ensure both within a partition (not across all partitions). Here's how.

#### Producer Configuration (Exactly-Once Writes)

Enable idempotence + transactions:

```java
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
props.put(ProducerConfig.ACKS_CONFIG, "all");
props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "txn-<unique-id>");
```

Then use the transactional APIs:

```java
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();
try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("orders", key, value));
    // optionally produce to multiple topics atomically
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

**Why this matters:**

- Idempotence ensures retries don't create duplicates.
- Transactions ensure that batches of records are atomically written — either all or none.

#### Consumer Configuration (Exactly-Once Processing)

When using Kafka Streams or manual consumers + producers, ensure atomicity between consuming and producing.

**Example: Kafka Streams (best practice).** Kafka Streams enables EOS natively:

```java
Properties props = new Properties();
props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
```

This ensures:

- Read-process-write steps are transactional.
- Kafka commits offsets only after output records are successfully written.

**Example: Consumer → Process → Producer (Manual).** You must manage this carefully:

1. Consume messages.
2. Begin a transaction in the producer.
3. Process messages.
4. Send processed records using the same transactional producer.
5. Send offsets to the transaction via:

   ```java
   producer.sendOffsetsToTransaction(offsets, consumerGroupId);
   ```

6. Commit the transaction.

This ensures offsets and outputs are committed atomically → "exactly-once".

#### Ordering Guarantee

Kafka maintains strict ordering within each partition. To preserve it:

- Use a partition key (like `userId`, `accountId`, `orderId`).
- Ensure `max.in.flight.requests.per.connection=1` in the producer (so messages are not re-ordered during retries).
- Don't use multiple consumers for the same partition in one group — only one consumer will be assigned per partition by Kafka, preserving order.

### Real-Time Use Cases

You'd use exactly-once + ordered processing in systems where correctness trumps latency:

1. **Financial Transactions**
   - Debit before credit.
   - Must not double-process payments or miss one.
   - Ordering by `accountId` ensures sequential consistency.
2. **Inventory Management**
   - Stock increment/decrement in sequence.
   - Prevent negative inventory due to race conditions.
3. **Fraud Detection / Audit Pipelines**
   - Logs or events must reflect the exact sequence for legal/audit integrity.
4. **Real-Time Analytics with Derived Topics**
   - Derived topics (like "aggregated-metrics") require strict one-time updates.

### Performance Considerations

| Tradeoff | Impact |
| --- | --- |
| Throughput ↓ | Transactions and idempotence introduce overhead |
| Latency ↑ | Transaction commit adds delay |
| Complexity ↑ | Manual handling of offset + transaction requires care |

In high-throughput, low-criticality systems (e.g., clickstreams), at-least-once is acceptable. For mission-critical flows (money, stock, orders), EOS + ordering is worth it.

### Summary

| Concern | Solution |
| --- | --- |
| **Duplicates** | Enable `enable.idempotence=true` |
| **Atomicity** | Use `transactional.id` and a transactional producer |
| **Ordering** | Use a partition key, 1 in-flight request per connection |
| **Exactly-once processing** | Use `sendOffsetsToTransaction()` or Kafka Streams EOS |
| **Use Cases** | Finance, Orders, Inventory, Auditing |

## 2. Transaction boundaries across services: how do you guarantee consistency when writing to the DB and publishing an event?

- Use the Outbox pattern: write the domain data and the outbox row in the same local TX; a relay publishes to Kafka.
- For workflows, use sagas (choreography or orchestration).

## 3. How do you write a basic Kafka producer with a send callback?

```java
Properties props = new Properties();
props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
props.put(ProducerConfig.ACKS_CONFIG, "all");  // strongest durability guarantee
props.put(ProducerConfig.RETRIES_CONFIG, 3);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);

ProducerRecord<String, String> record =
        new ProducerRecord<>("orders", "order123", "Order placed");

producer.send(record, (metadata, exception) -> {
    if (exception != null) {
        exception.printStackTrace();
    } else {
        System.out.printf("Sent to partition %d, offset %d%n",
                metadata.partition(), metadata.offset());
    }
});
producer.close();
```

## 4. What happens when a Kafka consumer takes longer than `max.poll.interval.ms`? How do you handle it?

**Question intent:** Tests handling of consumer group rebalances.

**Answer:**

- If processing takes longer than `max.poll.interval.ms`, Kafka assumes the consumer is dead and triggers a rebalance.
- **Solution:** Use manual commits and asynchronous processing with careful control.

```java
props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Collections.singletonList("orders"));

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processOrder(record.value());
    }
    consumer.commitSync();  // commit after successful processing
}
```

## 5. How do you implement idempotent producers in Kafka?

- Set `enable.idempotence=true` to prevent duplicate messages during retries.
- It guarantees exactly-once delivery per partition.

**Code snippet:**

```java
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
props.put(ProducerConfig.ACKS_CONFIG, "all");
props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
```

## 6. How do you ensure exactly-once semantics (EOS) in Kafka Streams or Spring Kafka?

```java
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "txn-1234");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("orders", "123", "processed"));
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

## 7. How do you tune Kafka consumer throughput?

| Property | Description |
| --- | --- |
| `fetch.min.bytes` | Reduce overhead by fetching in larger batches |
| `max.poll.records` | Controls the number of messages per poll |
| `enable.auto.commit=false` | Avoid commit delay |
| `max.poll.interval.ms` | Increase if processing takes longer |
| `session.timeout.ms` | Adjust based on consumer heartbeat rate |

## 8. How do you design a scalable Kafka-based microservice system?

- Use event-driven communication between microservices.
- Separate command and event topics.
- Use a schema registry (Avro/Protobuf) for schema evolution.
- Implement DLT, retry, and idempotent processing.
- Use exactly-once semantics for financial or critical data.

## 9. What is a Kafka transaction and why is it useful?

- Kafka supports atomic writes across multiple partitions and topics using transactions.
- Useful for exactly-once semantics (EOS).
- Set `transactional.id` in the producer config and use:

```java
producer.beginTransaction();
producer.send(record);
producer.commitTransaction();
```

## 10. What is `acks` in the Kafka producer and what are its levels?

- `acks=0` → Fire and forget (no confirmation).
- `acks=1` → Leader acknowledgment only.
- `acks=all` or `-1` → All replicas acknowledge (strongest durability).
- Recommended for production: `acks=all`

## 11. Why is Kafka fast?

- Sequential disk writes using log-structured storage.
- Uses zero-copy I/O with the `sendfile()` system call.
- Batching of messages.
- Page cache utilization to avoid disk seeks.
- Partitioning allows parallelism.
