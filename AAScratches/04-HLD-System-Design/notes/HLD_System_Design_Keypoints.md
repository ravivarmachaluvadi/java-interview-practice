# HLD System Design Keypoints

A grouped, cleaned-up reference of the keywords, acronyms, numbers and keypoints from my system-design interview memory dump (originally titled "System Design Interview — Keywords and Keypoints").

## Table of Contents

1. [Interview approach](#interview-approach)
2. [Compliance and regulatory acronyms](#compliance-and-regulatory-acronyms)
3. [CAP theorem and PACELC](#cap-theorem-and-pacelc)
4. [Back-of-envelope numbers and powers of two](#back-of-envelope-numbers-and-powers-of-two)
5. [Architecture vocabulary and glossary](#architecture-vocabulary-and-glossary)
6. [Infrastructure terminology: server, node, instance and friends](#infrastructure-terminology-server-node-instance-and-friends)
7. [Data centres, availability zones and regions](#data-centres-availability-zones-and-regions)
8. [Storage and databases](#storage-and-databases)
9. [ACID, BASE and isolation levels](#acid-base-and-isolation-levels)
10. [Consistency, replication and quorum](#consistency-replication-and-quorum)
11. [Distributed transactions](#distributed-transactions)
12. [Caching, locality and CDNs](#caching-locality-and-cdns)
13. [Messaging, webhooks and notifications](#messaging-webhooks-and-notifications)
14. [Load balancing, API gateways and networking](#load-balancing-api-gateways-and-networking)
15. [Security, cryptography and encoding](#security-cryptography-and-encoding)
16. [Specific system designs and data-model choices](#specific-system-designs-and-data-model-choices)
17. [Data structures and algorithms](#data-structures-and-algorithms)
18. [Performance primitives: mmap and LMAX Disruptor](#performance-primitives-mmap-and-lmax-disruptor)
19. [Miscellaneous](#miscellaneous)

---

## Interview approach

### Five-step method

1. Ask N number of questions
2. Clarify requirements to resolve assumptions
3. Reason out loud
4. Break down the problem
5. Make trade-offs

### Requirement-gathering questions to ask

- What are the features that we want to fulfill as part of this system?
- What are the non-functional requirements of this system?
- What is the scale of this system?
- What are the constraints of this system?
- What are the trade-offs we are making in this system?
- "I think I have gathered all the requirements, do you think I have missed anything?"

### Evolve the design

- Start simple to complex with evolutionary design.
- Thinking from easy to hard, simple to complex, small to large.
- Easy to medium to large scale.

### Justify choices

- Pick (meaning: from the available options) a few wrong choices and explain why they are wrong.
- Then pick the best choice and explain why it is the best choice.

### Constraints and rephrasing

- Constraints -> narrow down the scope, define limits. (Repeated three times in the original notes — clearly meant to be remembered.)
- Rephrase the problem and constraints as many times as needed to understand it fully.

### Requirement vocabulary

- Functional Requirements -> what the system should do
- Non-Functional Requirements -> how the system performs (e.g., scalability, reliability)
- Clarifications -> ask questions to understand requirements
- Trade-offs -> balance between competing factors (e.g., speed vs. cost)

---

## Compliance and regulatory acronyms

- PII -> Personally Identifiable Information
- PHI -> Protected Health Information
- PCI -> Payment Card Industry
- GDPR -> General Data Protection Regulation (EU)
- Compliance -> Adhering to laws, regulations, and policies
- Governance -> Overall management and oversight of policies and procedures

---

## CAP theorem and PACELC

### PACELC

PACELC - Partition tolerance, Consistency, Eventual consistency, Latency, Consistency

```text
If Partition (P)
 └── Choose Availability (A) or Consistency (C)
Else (E)
 └── Choose Latency (L) or Consistency (C)
```

### CAP Theorem (Distributed Systems)

- CA - Consistency and Availability (Single Node): if the network fails then the system becomes unavailable and also inconsistent
- CP - Consistency and Partition Tolerance (HBase, MongoDB): if the network fails then the system becomes unavailable
- AP - Availability and Partition Tolerance (Cassandra, CouchDB): if the network fails then the system becomes inconsistent

---

## Back-of-envelope numbers and powers of two

### Basic identities

- 2^10 = 1024 ≈ 1000 = 10^3 → 1 KB
- a^b * a^c = a^(b+c)
- 2^20 = 2^10 * 2^10 ≈ 1,000 * 1,000 = 10^3 * 10^3 → 1 MB

### Capacity-estimation vocabulary

- DAU -> Daily Active Users
- MAU -> Monthly Active Users
- Read/Write Ratio
- Desired Consistency (tunable consistency with R quorum and W quorum)
- Back Of The Envelope Estimation

### Powers of two (small)

| Power of two | Value |
| ------------ | ----- |
| 2^10 | 1024 |
| 2^9 | 512 |
| 2^8 | 256 |
| 2^7 | 128 |
| 2^6 | 64 |
| 2^5 | 32 |
| 2^4 | 16 |
| 2^3 | 8 |
| 2^2 | 4 |
| 2^1 | 2 |
| 2^0 | 1 |

### Powers of two to byte sizes

| Power of two | Approximate decimal value | Power of ten | Unit | Name of the number |
| ------------ | ------------------------- | ------------ | ---- | ------------------ |
| 2^10 | 1,000 | 10^3 | 1 KB (KiloBytes) | Thousand |
| 2^20 | 1,000,000 | 10^6 | 1 MB (MegaBytes) | Million |
| 2^30 | 1,000,000,000 | 10^9 | 1 GB (GigaBytes) | Billion |
| 2^40 | 1,000,000,000,000 | 10^12 | 1 TB (TeraBytes) | Trillion |
| 2^50 | 1,000,000,000,000,000 | 10^15 | 1 PB (PetaBytes) | Quadrillion |
| 2^60 | 1,000,000,000,000,000,000 | 10^18 | 1 EB (ExaBytes) | Quintillion |
| 2^70 | 1,000,000,000,000,000,000,000 | 10^21 | 1 ZB (ZettaBytes) | Sextillion |
| 2^80 | 1,000,000,000,000,000,000,000,000 | 10^24 | 1 YB (YottaBytes) | Septillion |

### Time

- 1 day = 24 hours = 1440 minutes = 86,400 seconds = 86,400,000 milliseconds
- Approximation: 100,000 seconds = 10^5 seconds = 10^5 * 10^3 milliseconds = 10^8 milliseconds per day

---

## Architecture vocabulary and glossary

### Stateless vs stateful services

- Stateless Services
- Stateful Services — example: applications which keep session states in the app memory, or persistent websocket connections

### Architecture principles

- Decentralized System -> No single point of control, multiple nodes share the responsibility
- Monolithic System -> Single unified unit, all components tightly coupled
- Decoupling -> Reducing dependencies between components
- Modularity -> Dividing a system into smaller parts (modules) that can be independently created and maintained
- Abstraction -> Hiding complex implementation details and exposing only necessary parts
- Decoupling VS Referential Integrity

### Core glossary

The original list opens with the note "(server or node)" — see [Infrastructure terminology](#infrastructure-terminology-server-node-instance-and-friends) for that table.

| Term | Meaning |
| ---- | ------- |
| SPOF | Single Point of Failure |
| Bottle Neck | Limits the performance of the entire system |
| Latency | Time taken to process a request |
| Throughput | Number of requests processed in a given time |
| Scalability | Ability to handle increased load by adding resources |
| Availability | Percentage of time the system is operational |
| Reliability | Ability to perform consistently over time even in the face of failures |
| Maintainability | Ease of making changes to the system |
| Consistency | All nodes see the same data at the same time |
| Partition Tolerance | System continues to operate despite network partitions across nodes |
| Fault Tolerance | System continues to operate despite failures of some components |
| Redundancy | Duplication of critical components to increase reliability |
| Load Balancing | Distributing incoming requests across multiple servers |
| Caching | Storing frequently accessed data in memory for faster access |
| Database Sharding | Splitting a database into smaller, more manageable pieces |
| Replication | Copying data across multiple servers for redundancy and performance |
| Indexing | Creating data structures to improve query performance |
| Message Queues | Asynchronous communication between components |
| CDN (Content Delivery Network) | Distributing static content to servers closer to users for faster access |
| Microservices | Breaking down a monolithic application into smaller, independent services |
| API Gateway | A single entry point for all client requests to microservices |
| Service Discovery | Automatically detecting and connecting to services in a distributed system |
| Monitoring | Tracking system performance and health |
| Logging | Recording system events for debugging and analysis |
| Alerting | Notifying when system performance degrades or failures occur |
| Disaster Recovery | Planning for and recovering from catastrophic failures |
| Data Backup | Regularly copying data to prevent loss |
| Data Consistency Models | Strong, Eventual, Causal, Read-your-writes, Monotonic Reads/Writes |
| CAP Theorem | Consistency, Availability, Partition Tolerance (choose two) |
| ACID Properties | Atomicity, Consistency, Isolation, Durability (for databases) |
| BASE Properties | Basically Available, Soft state, Eventual consistency (for NoSQL databases) |
| OAuth | Open standard for access delegation |
| JWT (JSON Web Token) | Compact, URL-safe means of representing claims to be transferred between two parties |
| SSL/TLS | Protocols for secure communication over a computer network |
| DNS (Domain Name System) | Translates domain names to IP addresses |
| HTTP/HTTPS | Protocols for transferring hypertext requests and information on the internet |
| SSE | Server-Sent Events, unidirectional communication from server to client |
| WebSockets | Persistent full-duplex communication channels over a single TCP connection |
| gRPC | High-performance, open-source universal RPC framework |
| GraphQL | Query language for APIs and a runtime for executing those queries |
| RESTful APIs | Architectural style for designing networked applications |

---

## Infrastructure terminology: server, node, instance and friends

Prompt from the original notes: "server or node — what else?"

| Term | Meaning | Common Context |
| --------- | ---------------------------------------------------------------------------------- | ------------------------------------------- |
| Server | A machine (physical or virtual) that provides services or resources to clients. | Web servers, app servers, database servers. |
| Node | Any active participant in a network or cluster (can be a server, client, or peer). | Distributed systems, clusters, blockchains. |
| Instance | A running copy of a virtual machine or service. | Cloud (AWS EC2, GCP Compute Engine). |
| Host | A machine (physical or virtual) with an address on a network. | Networking, Kubernetes. |
| Container | A lightweight isolated runtime for apps. | Docker, Kubernetes. |
| Pod | A group of one or more containers managed as a single unit. | Kubernetes. |
| Agent | A process that runs on nodes to perform tasks or report data. | Monitoring, orchestration tools. |
| Broker | A mediator that routes messages or data between components. | Kafka, RabbitMQ. |
| Replica | A copy of data or service instance for availability or scaling. | Databases, Kubernetes, replication setups. |
| Shard | A partitioned subset of data across nodes. | Distributed databases (Cassandra, MongoDB). |
| Cluster | A group of nodes working together as one system. | Hadoop, Kubernetes, Cassandra, etc. |

---

## Data centres, availability zones and regions

### Data center to data center (within an AZ)

- Inside a single AZ, there may be multiple physical data centers.
- These are even closer: a few hundred meters to a few kilometers apart.
- Connected with redundant, high-speed fiber (100 Gbps or more).

### Why these distances matter

- Within AZ → ultra-low latency, ideal for local failover. Example: us-east-1a, us-east-1b, us-east-1c
- Between AZs → synchronous replication possible (<2 ms latency).
- Between Regions → used for disaster recovery, but usually asynchronous.

### Rule of thumb (for big clouds like AWS, Azure, GCP)

| Hop | Typical distance |
| --- | ---------------- |
| Data center to data center (inside AZ) | < 5 km |
| AZ to AZ (inside a region) | 30–100 km |
| Region to Region | hundreds–thousands km |

---

## Storage and databases

### Cassandra data modelling: partition key and clustering columns

```sql
CREATE TABLE orders (
  user_id TEXT,
  order_id UUID,
  order_date TIMESTAMP,
  product TEXT,
  PRIMARY KEY ((user_id), order_id)
);
```

Here:

- `user_id` → Partition Key
- `order_id` → Clustering Column
- Together → Primary Key `((user_id, order_id))`

Concept:

- The PARTITION KEY decides which node(s) in the Cassandra cluster will store the row.
- Clustering Columns are the data sorting key within a partition. Clustering columns define the order of rows inside a partition — like how an index in a sorted file works.

### Write path and read path

Two keywords to remember for any storage discussion:

- Write Path
- Read Path

### Technology catalogue by data-store type

| Category | Examples |
| -------- | -------- |
| Time Series Databases | InfluxDB, TimescaleDB, Prometheus; Twitter uses MetricsDB; Amazon offers Timestream |
| Graph Databases | Neo4j, Amazon Neptune, ArangoDB |
| Search Engines | Elasticsearch, Apache Solr, Algolia |
| Message Brokers | Apache Kafka, RabbitMQ, Amazon SQS, Google Pub/Sub |
| Wide Column Stores | Apache Cassandra, HBase, ScyllaDB, Google Bigtable |
| Key-Value Stores | Redis, DynamoDB, Memcached |
| Relational Databases | MySQL, PostgreSQL, Oracle DB, SQL Server |
| Document Databases | MongoDB, CouchDB, Amazon DocumentDB |
| DBs for aggregated queries | Druid, ClickHouse, Apache Pinot (used for real-time analytics like top-N queries, time-series analysis) |
| Real-time analytics | Apache Flink, Apache Spark, Apache Storm |
| Blob storage | Amazon S3 (Simple Storage Service), Google Cloud Storage, Azure Blob Storage — example use cases: storing images, videos, backups, logs |

### Metadata databases or services

- MetaData DBs or services (keyword to remember when designing file-storage or media systems)

### Normalization and denormalization

- Normalization -> Organizing data to reduce redundancy (avoid duplicates) and improve data integrity
- Denormalization -> Reducing the number of joins by duplicating data or storing related data together

---

## ACID, BASE and isolation levels

### ACID = Atomicity + Consistency + Isolation + Durability

#### 1. Atomicity

- "All or nothing" — a transaction must be treated as a single unit of work.
- Either all its operations succeed or none do.

#### 2. Consistency

- A transaction must move the database from one valid state to another valid state.
- All integrity constraints, foreign keys, unique constraints, and business rules must remain satisfied.

Real-time example:

- Let's say your system has a rule: the sum of balances in Account A and B must always equal ₹10,000.
- If a transaction tries to transfer money but causes the total to become ₹9,900 or ₹10,100, it violates consistency.

#### 3. Isolation

- When multiple transactions occur concurrently, each one should behave as if it's executed alone.
- No transaction should see partial results of another uncommitted transaction.

Real-time example:

- Two users transferring money at the same time:
  - T1: Transfers ₹1000 from Account A to B
  - T2: Reads Account A's balance while T1 hasn't committed yet
- If T2 reads the reduced balance before T1 commits, it might lead to wrong reports or double deductions.

SQL defines four isolation levels (in order of increasing isolation strength):

| Isolation Level | What It Prevents | Example Issue |
| --------------- | ---------------- | ------------- |
| Read Uncommitted | Nothing | Dirty reads |
| Read Committed | Dirty reads | Still allows non-repeatable reads |
| Repeatable Read | Non-repeatable reads | Still allows phantom reads |
| Serializable | Phantom reads | Fully isolated (like transactions run sequentially) |

#### 4. Durability

- Once a transaction is committed, its changes are permanent, even if the system crashes immediately afterward.
- That's possible because the RDBMS writes all transaction data to durable storage (e.g., disk or WAL in PostgreSQL) before confirming the commit.

#### ACID in a bank transfer

Let's visualize ACID in a bank transfer:

| Step | Property | What Happens |
| ---- | -------- | ------------ |
| 1 | Atomicity | Both debit and credit must succeed or both fail. |
| 2 | Consistency | No account balance can go negative. Total balance remains same. |
| 3 | Isolation | Parallel transactions do not interfere or read partial data. |
| 4 | Durability | After commit, data survives crashes. |

### What is BASE?

BASE = Basically Available, Soft State, Eventually Consistent

It describes the approach taken by NoSQL and distributed databases to ensure high availability and partition tolerance (as per the CAP theorem), often at the cost of immediate consistency.

#### 1. Basically Available

- The system guarantees availability — i.e., the system will always respond to a request (success or failure) even if some parts of it are down.
- It may not always return up-to-date data, but it won't hang or crash.

#### 2. Soft State

- The system's state can change over time, even without new inputs, because of asynchronous replication and background reconciliation.

#### 3. Eventually Consistent

- If no new updates are made, all replicas in the system will eventually converge to the same value.
- That is, after some delay, all nodes will agree on the same data — even though they might differ temporarily.

### How BASE is implemented in NoSQL

| Property | Mechanism in NoSQL |
| ------------------------- | ----------------------------------------------------- |
| Basically Available | Data replication, partitioning, and fallback replicas |
| Soft State | Background sync, hinted handoff, gossip protocols |
| Eventually Consistent | Read repair, vector clocks, quorum reads/writes |

For example, in Cassandra:

- You can choose consistency levels like ONE, QUORUM, or ALL.
- This lets you tune between availability and consistency as per use case.

### ACID vs BASE comparison

| Property | ACID (RDBMS) | BASE (NoSQL) |
| ----------------- | ------------------------------------------- | --------------------------------------- |
| Focus | Consistency and correctness | Availability and scalability |
| Consistency | Immediate and strong | Eventual (maybe temporary stale) |
| State | Stable | Soft / changing |
| Availability | May sacrifice availability during partition | Prioritizes availability |
| Typical Use Cases | Banking, payments, inventory control | Social media, IoT, shopping carts |
| System Type | Centralized or smaller distributed systems | Highly distributed systems |
| Examples | PostgreSQL, MySQL, Oracle | Cassandra, DynamoDB, MongoDB, Couchbase |

So a BASE system is eventually consistent — not immediately consistent like ACID databases.

In short:

- ACID → "Consistency NOW" (banking, transactions)
- BASE → "Availability NOW, consistency LATER" (web-scale NoSQL apps)

---

## Consistency, replication and quorum

### Replication techniques

- Master-Slave, Master-Master, Multi-Master
- Hard copy vs Erasure Encoding

### Erasure encoding

Instead of simply replicating data (e.g., keeping 3 full copies), erasure encoding splits data into smaller fragments and adds redundant parity fragments. Using these fragments, the system can reconstruct the original data even if some fragments are lost.

Key idea:

- Break data into k data blocks.
- Compute m parity blocks using mathematical functions (often XOR or Reed-Solomon).
- Store a total of k + m blocks across nodes.
- If up to m blocks are lost, the original data can still be reconstructed.

Libraries exist in Java for production use:

- Jerasure (C, but bindings exist)
- Backblaze Reed-Solomon Java implementation
- Apache Commons Math + Hadoop EC

### Checksum or hashing

- Checksum or hashing -> data integrity verification
- Algorithms: md5, sha1, sha256, HMAC, CRC32

### Quorum consensus (redundant distributed systems)

- N = Total number of nodes
- W = Write considered successful when acknowledged by W nodes
- R = Read considered successful when acknowledged by R nodes
- The coordinator must wait for W nodes to acknowledge the write; the same for R nodes to acknowledge the read.
- W + R > N ensures strong consistency: at least one node that has the latest write is read.

| Setting | Effect |
| ------- | ------ |
| W = 1 and R = N | System optimized for writes |
| W = N and R = 1 | System optimized for reads |
| W = N/2 and R = N/2 | System optimized for both reads and writes |
| W + R <= N | System is eventually consistent (weak consistency) |

Depending on the use case we can choose the values of W and R to achieve the desired consistency level.

### Strong consistency via distributed locking

- Strong consistency is usually achieved by using distributed locking mechanisms like Zookeeper, etcd, Consul.
- This works by forcing a replica not to accept new reads/writes until every replica has agreed on the latest write.
- This approach is not ideal for high-throughput systems and for highly available systems because it could block new operations.

---

## Distributed transactions

Examples of distributed transaction implementations:

### Two-Phase Commit (2PC)

- 2PC is a protocol that ensures all participants in a distributed transaction either commit or abort the transaction together.
- It consists of two phases: the prepare phase and the commit phase.
- In the prepare phase, the coordinator asks all participants if they are ready to commit.
- If all participants respond positively, the coordinator sends a commit message in the commit phase.
- If any participant responds negatively or fails to respond, the coordinator sends an abort message.

### Three-Phase Commit (3PC)

- 3PC is an extension of 2PC that adds an additional phase to reduce the chances of blocking in case of failures.
- It consists of three phases: the canCommit phase, the preCommit phase, and the doCommit phase.
- In the canCommit phase, the coordinator asks all participants if they can commit.
- If all participants respond positively, the coordinator sends a preCommit message in the preCommit phase.
- Participants acknowledge the preCommit message and prepare to commit.
- Finally, in the doCommit phase, the coordinator sends a doCommit message to all participants to finalize the transaction.
- If any participant fails to respond or encounters an error, the coordinator can send an abort message.

### TC/C (Try Confirm/Cancel)

- TC/C is a pattern that separates the transaction into three distinct steps: try, confirm, and cancel.
- In the try step, the system attempts to perform the necessary operations for the transaction.
- If all operations succeed, the system proceeds to the confirmation step, where it finalizes the transaction.
- If any operation fails during the try step, the system enters the cancel step, where it rolls back any changes made during the try step.
- This pattern is particularly useful in scenarios where operations can be performed independently and can be rolled back if needed.

### Saga Pattern

- Saga is a pattern for managing long-lived transactions in a distributed system.
- It breaks down a transaction into a series of smaller, independent steps called sagas.
- Each saga step performs a specific operation and can be compensated if it fails.
- If a saga step fails, the system executes compensating actions to undo the effects of the previous successful steps.
- **This pattern is useful for scenarios where transactions span multiple services and need to be eventually consistent.**

### Event Sourcing with CQRS

- Event Sourcing is a pattern where state changes are captured as a sequence of events.
- Instead of storing the current state, the system stores a log of all events that led to the current state.
- CQRS (Command Query Responsibility Segregation) separates the read and write operations into different models.
- In a distributed transaction, commands are sent to the write model, which generates events.
- These events are then processed by the read model to update the state.
- This approach allows for better scalability and flexibility in handling distributed transactions.

### Event Sourcing (for distributed transactions)

- In this approach, the system uses event sourcing to capture state changes and ensure eventual consistency.
- When a distributed transaction is initiated, events are generated for each operation.
- If any operation fails, compensating events are created to undo the effects of the previous successful operations.
- The system processes these events asynchronously, ensuring that all services eventually reach a consistent state.
- This approach is particularly useful in scenarios where strong consistency is not required, and eventual consistency is acceptable.

---

## Caching, locality and CDNs

### Cache technologies

- Examples of Caches -> Redis, Memcached, Varnish, AWS ElasticCache

### Locality of reference

- Spatial Locality -> Data near to recently accessed data is likely to be accessed soon
- Temporal Locality -> Recently accessed data is likely to be accessed again soon
- Locality of Reference -> Temporal Locality, Spatial Locality

### CDNs

- CDNs -> Akamai, Cloudflare, Amazon CloudFront

---

## Messaging, webhooks and notifications

For the list of message brokers (Apache Kafka, RabbitMQ, Amazon SQS, Google Pub/Sub) see the [technology catalogue](#technology-catalogue-by-data-store-type).

### Webhooks

A webhook is a way for one system to automatically send real-time data or notifications to another system as soon as an event happens, without the second system having to repeatedly check (poll) for updates.

It's like a "reverse API call":

- With an API, your app keeps asking: "Anything new?"
- With a webhook, the other system tells you: "Hey! Something happened, here's the data."

```java
@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        String event = (String) payload.get("event");
        if ("payment_success".equals(event)) {
            // process payment success
            System.out.println("Payment Success: " + payload);
        } else if ("payment_failed".equals(event)) {
            // handle failure
            System.out.println("Payment Failed: " + payload);
        }
        // Must return 200 so provider knows webhook was received
        return ResponseEntity.ok("Webhook received");
    }
}
```

### Notification channels

- Push Notifications -> Server Sent Events, APNS (Apple Push Notification Service), FCM (Firebase Cloud Messaging)
- Mail Notifications -> Third-party services (SendGrid, Amazon SES, Mailgun)
- SMS Notifications -> Third-party services (Twilio, Nexmo, Plivo)

---

## Load balancing, API gateways and networking

### Load balancers and API gateways

- Examples of Load Balancers -> Nginx, HAProxy, AWS ELB, Google Cloud Load Balancing
- Examples of API Gateways -> Kong, Apigee, AWS API Gateway, NGINX API Gateway, Kubernetes Ingress + API Gateway (like Ambassador, Istio, or Traefik)

### The OSI (Open Systems Interconnection) Model

Mnemonic: **APS TNDP**

| Layer | Name | Examples |
| ----- | ---- | -------- |
| 1 | Application Layer | HTTP, FTP, SMTP |
| 2 | Presentation Layer | Encryption, Compression |
| 3 | Session Layer | API, Sockets |
| 4 | Transport Layer | TCP, UDP |
| 5 | Network Layer | IP, ICMP |
| 6 | Data Link Layer | Ethernet, MAC |
| 7 | Physical Layer | Cables, Hubs |

(Numbered top-down from Application to Physical, as in the original notes.)

### Network protocols

| Protocol | Expansion | Used for |
| -------- | --------- | -------- |
| SMTP | Simple Mail Transfer Protocol | Sending emails |
| IMAP | Internet Message Access Protocol | Retrieving emails from a mail server but not deleting them in the server |
| POP3 | Post Office Protocol 3 | Retrieving emails from a mail server and deleting them in the server |
| FTP | File Transfer Protocol | Transferring files over a network |
| SFTP | Secure File Transfer Protocol | Transferring files over a network securely |
| SSH | Secure Shell | Secure remote login and other secure network services over an insecure network |
| TLS | Transport Layer Security | To check authenticity and integrity of the server; securing communications over a computer network |
| SSL | Secure Sockets Layer | Securing communications over a computer network (deprecated, replaced by TLS) |
| HTTP | Hypertext Transfer Protocol | Transferring hypertext requests and information on the internet |
| HTTPS | Hypertext Transfer Protocol Secure | Secure communication over a computer network within HTTP; https = http + ssl/tls |
| TCP | Transmission Control Protocol | Reliable, ordered, and error-checked delivery of data between applications |
| UDP | User Datagram Protocol | Low-latency and loss-tolerating connections between applications |

---

## Security, cryptography and encoding

### Acronym expansions

| Acronym | Expansion |
| ------- | --------- |
| MD5 | Message Digest Algorithm 5 |
| RSA | Rivest-Shamir-Adleman |
| SHA | Secure Hash Algorithm |
| HMAC | Hash-based Message Authentication Code |
| AES | Advanced Encryption Standard |
| DES | Data Encryption Standard |
| 3DES | Triple Data Encryption Standard |
| RC4 | Rivest Cipher 4 |
| ECC | Elliptic Curve Cryptography |
| UTF | Unicode Transformation Format |
| ASCII | American Standard Code for Information Interchange |
| PCI-DSS | Payment Card Industry Data Security Standard |
| CRC32 | Cyclic Redundancy Check 32 |

### RSA vs ECC key sizes

| Algorithm | Key Size (bits) | Equivalent Security |
| --------- | --------------- | ------------------- |
| RSA | 2048 | Yes |
| ECC | 256 | Yes — same security level |

### Algorithm families

- Examples of Symmetric Encryption -> AES, DES, 3DES, Blowfish, RC4, ChaCha20
- Examples of Asymmetric Encryption -> RSA, ECC (Elliptic Curve Cryptography)
- Examples of Hashing Algorithms -> MD5, SHA-1, SHA-256, SHA-512, HMAC (Hash-based Message Authentication Code)
- Examples of Encoding Schemes -> Base62, Base64, URL encoding, ASCII, UTF-8, UTF-16, Hexadecimal, Unicode
- Examples of Erasure Encoding Libraries -> Jerasure, Backblaze Reed-Solomon Java implementation, Apache Commons Math + Hadoop EC
- Examples of Checksum Algorithms -> CRC32, Adler-32, MD5, SHA-1, SHA-256

### ASCII, Unicode and UTF-8

ASCII:

- Year: 1960s
- Range: 0–127
- Limitation: Only supports English characters.
- Stored as 1 byte per character, but only 7 bits used.

Unicode:

- 1 byte to 4 bytes
- Year: 1990s
- Purpose: Represent all characters from all languages (human + symbols + emojis).

UTF-8 (Unicode Transformation Format — 8-bit):

- Purpose: Store Unicode text efficiently using variable-length encoding.
- Invented: 1992
- UTF-8 is an encoding scheme that maps Unicode code points → bytes.
- Backward compatible with ASCII: if your text is all ASCII, UTF-8 uses exactly the same bytes — no wasted space.
- Efficient for English but flexible for any language.
- Think of UTF-8 as a compression strategy that can represent the entire Unicode dictionary.

| Range | Bytes Used | Example |
| ------------------ | ---------- | ------------------------------------------ |
| U+0000 – U+007F | 1 byte | Basic ASCII (A → 65 → 0x41) |
| U+0080 – U+07FF | 2 bytes | Latin, Greek |
| U+0800 – U+FFFF | 3 bytes | Most languages (Devanagari, Chinese, etc.) |
| U+10000 – U+10FFFF | 4 bytes | Emojis, rare symbols |

---

## Specific system designs and data-model choices

### Snowflake ID generation

Twitter Snowflake (64-bit ID) — Incremental, Unique, Time-ordered.

| Bits | Field | Notes |
| ---- | ----- | ----- |
| 1 bit | Unused | Always 0 |
| 41 bits | Timestamp (in milliseconds) | Custom epoch |
| 5 bits | Data Center ID | 2^5 = 32 |
| 5 bits | Machine ID | 2^5 = 32 |
| 12 bits | Sequence Number (per machine per millisecond) | 2^12 = 4096 |

### Data-model choices for common problems

- Social Media Friendships -> Graph (Undirected); store each edge as (lowerId, higherId)
- Web Page Links -> Graph (Directed)
- Message chats -> Cassandra (Wide Column Store)

---

## Data structures and algorithms

### Algorithms

- DFS and BFS

### Data structures

| Data Structure | Used for |
| -------------- | -------- |
| Trie | Efficient retrieval and storing of strings, such as in autocomplete systems |
| Inverted Index | Search engines, to map keywords to their locations in documents |
| Bloom Filter | Probabilistic membership testing, such as in caching and databases |
| Skip List | Leader boards and in-memory databases for fast search, insertion, and deletion |
| Suffix Tree | Fast substring searches in strings, such as in bioinformatics |
| Quad Tree | Spatial indexing, such as in geographic information systems (GIS) |
| R-Tree | Spatial indexing of multidimensional data, such as in GIS and CAD |
| B+ Tree | Databases and file systems for efficient data retrieval |
| Merkle Tree | Blockchain and distributed systems for data integrity verification |
| Hash Table | Fast data retrieval based on key-value pairs |

---

## Performance primitives: mmap and LMAX Disruptor

### mmap

- mmap is a memory-mapped file I/O mechanism that allows files or devices to be mapped into the memory space of a process.
- It enables applications to access files on disk as if they were part of the process's memory, allowing for efficient file I/O operations.
- mmap can improve performance by reducing the number of system calls and enabling direct memory access to file data.
- It is commonly used in scenarios where large files need to be accessed or when multiple processes need to share data.
- mmap can be used for inter-process communication (IPC) by mapping the same file into the memory space of multiple processes, allowing them to share data.
- mmap can also be used for implementing shared memory regions, where multiple processes can read and write to the same memory area.
- mmap is available in various programming languages, including C, C++, Python, and Java, through language-specific libraries or bindings.
- mmap is commonly used in operating systems like Linux, Unix, and Windows, and is supported by various file systems.

Further uses of mmap:

- Memory-mapped databases, where the database files are mapped into memory for fast access and manipulation of data.
- Memory-mapped I/O devices, where device registers are mapped into the memory space of a process for direct access.
- Memory-mapped files in embedded systems, where file I/O operations need to be efficient and low-latency.
- Memory-mapped files in high-performance computing (HPC) applications, where large datasets need to be processed quickly.
- Memory-mapped files in scientific computing applications, where large datasets need to be analyzed and processed.
- Memory-mapped files in gaming applications, where large game assets need to be loaded and accessed quickly.

### LMAX Disruptor

- LMAX Disruptor is a high-performance inter-thread messaging library designed for low-latency and high-throughput applications.
- It is based on a ring buffer data structure that allows multiple producers and consumers to communicate efficiently without locks or contention.
- The Disruptor pattern is particularly useful in scenarios where low latency and high throughput are critical, such as in financial trading systems, real-time analytics, and gaming applications.
- The LMAX Disruptor library provides a simple and efficient way to implement the Disruptor pattern in Java applications, with features such as event batching, backpressure handling, and support for multiple event types.
- The Disruptor pattern can help reduce latency and improve performance by minimizing context switching and reducing the overhead of traditional locking mechanisms.
- The LMAX Disruptor library is open-source and available on GitHub, with extensive documentation and examples to help developers get started.

---

## Miscellaneous

### IEEE -> Institute of Electrical and Electronics Engineers

- IEEE is a professional association dedicated to advancing technology for the benefit of humanity.
- It is known for developing industry standards in various fields, including computer networking, telecommunications, and electronics.
- Some well-known IEEE standards include IEEE 802.3 (Ethernet), IEEE 802.11 (Wi-Fi), and IEEE 754 (floating-point arithmetic).
- The organization also publishes a wide range of technical literature, including journals, conference proceedings, and standards documents.

### POSIX

- POSIX (Portable Operating System Interface) is a family of standards specified by the IEEE for maintaining compatibility between operating systems.
- POSIX defines the application programming interface (API), along with command line shells and utility interfaces for software compatibility with variants of Unix and other operating systems.

### Reproducibility

- Ability to consistently reproduce the same results under the same conditions used to create the original results.
