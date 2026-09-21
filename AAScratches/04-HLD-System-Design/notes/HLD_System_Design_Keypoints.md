# HLD System Design Keypoints

A grouped, cleaned-up reference of the keywords, acronyms, numbers and keypoints from my system-design interview memory dump (originally titled "System Design Interview — Keywords and Keypoints").

Blocks marked **Correction** flag a statement that was wrong or out of date in an
earlier version of these notes, and say what the right distinction is. Those are the
parts worth re-reading the night before an interview: a confidently stated wrong
version of any of them is worse than never raising the topic.

## The facts most often got wrong — read this first

| Topic | The wrong version | The right version |
| ----- | ----------------- | ----------------- |
| [CAP](#cap-theorem-and-pacelc) | "Pick two of the three" | Only *during a partition*, and then it is C or A |
| [PACELC](#cap-theorem-and-pacelc) | The letters mis-expanded | Partition → A or C; **E**lse → **L** or **C** |
| [Quorum](#consistency-replication-and-quorum) | `R + W > N` gives strong consistency | It gives read/write **overlap**, not linearizability |
| [Quorum](#consistency-replication-and-quorum) | `W = N/2, R = N/2` balances both | That is `R + W = N` — no overlap at all |
| [Isolation](#acid-base-and-isolation-levels) | Serializable prevents phantom reads | It prevents *every* anomaly, write skew included |
| [OSI](#load-balancing-api-gateways-and-networking) | Layer 1 = Application | Layer 1 = **Physical**; Application is layer 7 |
| [2PC](#distributed-transactions) | "It rolls back on failure" | Coordinator dies → participants **block**, holding locks |
| [Saga](#distributed-transactions) | "It rolls back" | It **compensates**; and it gives up isolation, not just atomicity |
| [Cassandra](#storage-and-databases) | `PRIMARY KEY ((a, b))` = partition + clustering | Double parens = **composite partition key** |
| [Powers of two](#back-of-envelope-numbers-and-powers-of-two) | `2^10 = 1 KB` | `2^10` bytes = 1 **KiB**; 1 KB = 1,000 bytes |
| [RSA vs ECC](#security-cryptography-and-encoding) | RSA 2048 ≈ ECC 256 | RSA **3072** ≈ ECC 256; RSA 2048 ≈ ECC 224 |
| [Kafka](#cap-theorem-and-pacelc) | `acks=all` means no data loss | Also needs `min.insync.replicas >= 2` |

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

PACELC = **P**artition → **A**vailability or **C**onsistency; **E**lse → **L**atency or **C**onsistency.

```text
If Partition (P)
 └── Choose Availability (A) or Consistency (C)
Else (E)
 └── Choose Latency (L) or Consistency (C)
```

> **Correction — the expansion of the acronym was wrong.**
> The note used to read "Partition tolerance, Consistency, Eventual consistency,
> Latency, Consistency". Two letters were mis-assigned: the **A** is Availability,
> not a second Consistency, and the **E** is the English word *Else*, not "Eventual
> consistency". The ASCII tree underneath was right all along, which is the giveaway
> — the tree says "Availability (A)" and "Else (E)". Getting this wrong in an
> interview is expensive because the whole point of PACELC is that it adds the
> *else* branch to CAP: even with a perfectly healthy network you are still paying
> for consistency, in latency.

Why PACELC exists at all: CAP only tells you what happens during a partition, which
is rare. PACELC points out that the interesting trade-off is the one you make every
single day, partition or no partition — a strongly consistent read has to talk to a
quorum or a leader, and that costs a round trip.

| System | During partition (PAC) | Normal operation (ELC) | Shorthand |
| ------ | ---------------------- | ---------------------- | --------- |
| Cassandra, Riak (default tuning) | Availability | Latency | PA/EL |
| DynamoDB (eventually consistent reads) | Availability | Latency | PA/EL |
| DynamoDB (strongly consistent reads) | Consistency | Consistency | PC/EC |
| HBase, etcd, ZooKeeper, Spanner | Consistency | Consistency | PC/EC |
| MongoDB (majority read/write concern) | Consistency | Consistency | PC/EC |

### CAP Theorem (Distributed Systems)

The one-sentence version to say in an interview:

> **When a network partition happens, a distributed system must choose between
> answering with possibly stale data (A) and refusing to answer (C). When there is no
> partition, it can have both.**

> **Correction — "choose two of the three" is the standard misstatement, and it was
> in these notes twice (here and in the glossary).**
> CAP is not a menu you order from once at design time. Partition tolerance is not
> an option you can decline: partitions are a property of the network, not of your
> architecture, and networks partition whether or not your design has a plan for it.
> So P is a given, and the theorem reduces to a **conditional**: *during* a
> partition, you must give up C or A. The rest of the time — which is almost all of
> the time — a well-built system provides both, which is exactly why "CP systems"
> like Spanner advertise five nines of availability without contradicting anything.
>
> The second half of the correction is that C and A in CAP are narrow technical
> terms, not their everyday meanings:
>
> - **C** is *linearizability*: every read returns the most recent committed write,
>   as if there were one copy of the data. It is **not** ACID's C (constraints hold).
> - **A** means *every request to a non-failing node gets a non-error response*. A
>   system that answers 99.99% of the time is **not** "A" in CAP's sense; CAP's A is
>   an absolute, which is why the theorem is a proof and not a rule of thumb.

What the three labels actually mean:

| Label | Behaviour during a partition | Honest reading |
| ----- | ---------------------------- | -------------- |
| CP | Minority side refuses reads/writes rather than serve stale data | "Consistent, and unavailable on the minority side" |
| AP | Every side keeps answering; replicas diverge and reconcile later | "Available, and temporarily inconsistent" |
| CA | Not achievable in a system that spans a network | Really means "single node" — one node cannot partition from itself |

So the corrected version of the three original bullets:

- **CA** — a single node. It is trivially consistent and available because there is
  no network to partition. It is not a distributed-systems category; it is the
  absence of one. A single node still goes down, but that is a *failure*, not a
  partition.
- **CP** — during a partition the minority side stops serving, so the system looks
  unavailable there. Examples: HBase, ZooKeeper, etcd, Spanner.
- **AP** — during a partition every side keeps serving, so replicas can disagree
  until they converge. Examples: Cassandra and Riak at their default settings,
  CouchDB, DynamoDB on eventually-consistent reads.

### Where the usual databases actually sit

> **Correction — the tech labels in the original bullets were too absolute.**
> "MongoDB is CP" and "Cassandra is AP" were true of old defaults, and both are now
> better described as *tunable per operation*. Naming the knob is what earns the
> point, not naming the letter.

| System | Default behaviour | The knob that moves it |
| ------ | ----------------- | ---------------------- |
| Cassandra | Tunable per query, not fixed AP | Consistency level: `ONE` (AP-ish) … `QUORUM` … `ALL` (CP-ish) |
| MongoDB | CP by default since 3.6/4.0 | `writeConcern: majority`, `readConcern: majority`/`linearizable` |
| DynamoDB | Eventually consistent reads by default | `ConsistentRead=true` per read; transactions for multi-item atomicity |
| PostgreSQL | Single primary, so CP within the cluster | `synchronous_commit` and `synchronous_standby_names` for the replicas |
| Kafka | Durability is tunable, not fixed | `acks=all` + `min.insync.replicas=2`, and `unclean.leader.election=false` |

The Kafka row is the one most often stated wrongly: Kafka does **not** guarantee no
data loss out of the box. `acks=all` alone is not enough either — without
`min.insync.replicas >= 2` a single surviving replica satisfies "all", and enabling
unclean leader election trades committed data for availability, by design.

---

## Back-of-envelope numbers and powers of two

### Basic identities

- 2^10 = 1,024 ≈ 1,000 = 10^3 → 1 KiB (and ≈ 1 KB)
- a^b * a^c = a^(b+c)
- 2^20 = 2^10 * 2^10 ≈ 1,000 * 1,000 = 10^3 * 10^3 → 1 MiB (and ≈ 1 MB)

> **Correction — KB and KiB are different units, and the note used one symbol for
> both.** 1 KB is exactly 1,000 bytes (decimal, SI). 1 KiB is exactly 1,024 bytes
> (binary, IEC). The approximation `2^10 ≈ 10^3` is the right *estimation* move and
> you should keep using it — just do not write `2^10 = 1 KB` as an equality, because
> it is off by 2.4% and that error compounds: by the exabyte scale the same
> approximation is off by more than 15%. Disk vendors sell in KB/MB/TB, operating
> systems report in KiB/MiB/TiB, and that single mismatch is the whole reason a
> "1 TB" drive shows up as 931 GB.

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

### How much the approximation actually costs you

The table above rounds `2^10` to `10^3`, which is the right thing to do in a
back-of-envelope. It is worth knowing which direction the error runs, and how fast it
compounds, so you can say "call it 1.1 TB" instead of being caught out.

| Power | Exact value | Approximated as | Understates the true value by |
| ----- | ----------- | --------------- | ----------------------------- |
| 2^10 | 1,024 | 10^3 | 2.4% |
| 2^20 | 1,048,576 | 10^6 | 4.9% |
| 2^30 | 1,073,741,824 | 10^9 | 7.4% |
| 2^40 | 1,099,511,627,776 | 10^12 | 10.0% |
| 2^50 | 1,125,899,906,842,624 | 10^15 | 12.6% |
| 2^60 | 1,152,921,504,606,846,976 | 10^18 | 15.3% |

Rule of thumb: the error is roughly **2.4% per factor of 2^10**, and it always runs
the same way — the real number is *bigger* than your estimate. Under 1 TB it is
noise. Above that, say the direction out loud.

### Time

- 1 day = 24 hours = 1,440 minutes = 86,400 seconds = 86,400,000 milliseconds
- Approximation: 1 day ≈ 100,000 s = 10^5 s, so ≈ 10^5 * 10^3 = 10^8 milliseconds

Both lines are arithmetically correct. The part worth adding is **which way the
approximation lies**, because you use it in the denominator:

- 10^5 is 15.7% *larger* than the real 86,400, so dividing a daily volume by 10^5
  gives a QPS that is about 14% **too low**.
- Worked example: 1,000,000 requests/day ÷ 10^5 = **10 QPS**; the true figure is
  1,000,000 ÷ 86,400 = **11.6 QPS**. For sizing that is fine — but say "call it
  10 to 12 QPS", not "10 QPS", or an interviewer probing your rounding will find it.
- The fix if you want to be conservative on capacity: estimate QPS with 10^5, then
  add ~15%, or just size for peak rather than average anyway.

Other durations in the same style:

| Duration | Exact seconds | Estimate to use | Note |
| -------- | ------------- | --------------- | ---- |
| 1 hour | 3,600 | ≈ 4 × 10^3 | Over-estimates by 11% |
| 1 day | 86,400 | ≈ 10^5 | Over-estimates by 16% |
| 1 month (30 d) | 2,592,000 | ≈ 2.6 × 10^6 | Within 0.3% |
| 1 year | 31,536,000 | ≈ π × 10^7 | Within 0.4%, and easy to remember |

Peak vs average: traffic is never flat. A common interview default is **peak QPS ≈
2× average**, and for consumer apps with a sharp evening peak, 3–5×. State the
multiplier you are assuming rather than sizing for the average and hoping.

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
| Availability | Percentage of time the system is operational (the SLA sense). CAP's "A" is stricter: *every* request to a live node gets a non-error answer |
| Reliability | Ability to perform consistently over time even in the face of failures |
| Maintainability | Ease of making changes to the system |
| Consistency | Every read returns the latest committed write. Careful: CAP's "C" (linearizability) and ACID's "C" (constraints stay satisfied) are different properties that share a name |
| Partition Tolerance | System keeps operating despite network partitions. Not a design choice — the network partitions regardless, so every distributed system needs an answer for it |
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
| CAP Theorem | Consistency, Availability, Partition Tolerance — **during a network partition** you must give up C or A; P is not optional. See [CAP theorem and PACELC](#cap-theorem-and-pacelc) |
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
- Between AZs → synchronous replication is practical; AWS specifies single-digit
  millisecond round trips, typically around 1 ms, and AZs no more than ~100 km apart.
- Between Regions → used for disaster recovery, and usually asynchronous because the
  round trip is tens to hundreds of milliseconds.

Latency numbers worth memorising, since every capacity answer leans on them:

| Hop | Round trip | What it rules out |
| --- | ---------- | ----------------- |
| Same rack / same AZ | ~0.5 ms or less | Nothing — synchronous anything is fine |
| Cross-AZ, same region | ~1 ms (single-digit) | Nothing much; synchronous quorum writes are normal |
| Cross-region, same continent | ~10–40 ms | Synchronous writes on the request path |
| Cross-continent (e.g. us-east ↔ eu-west) | ~70–100 ms | Any per-request consensus round trip |
| Memory reference | ~100 ns | — |
| SSD random read | ~100 µs | — |
| Disk seek | ~10 ms | — |

The rule these numbers encode: **synchronous replication within a region, asynchronous
across regions.** A cross-continent synchronous commit adds 100 ms to every write, so
multi-region strong consistency (Spanner) needs special infrastructure and is a
deliberate, expensive choice rather than a default.

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
- Together → Primary Key `(user_id, order_id)` — **single** parentheses

> **Correction — the notation `((user_id, order_id))` means the opposite of what the
> note said.** In CQL, the *inner* parentheses mark the partition key, so wrapping
> both columns turns them both into the partition key and leaves no clustering column.

| Written as | Partition key | Clustering columns | Rows land on |
| ---------- | ------------- | ------------------ | ------------ |
| `PRIMARY KEY ((user_id), order_id)` | `user_id` | `order_id` | One partition per user |
| `PRIMARY KEY (user_id, order_id)` | `user_id` | `order_id` | Identical — the outer form is shorthand |
| `PRIMARY KEY ((user_id, order_id))` | `user_id` **and** `order_id` | *none* | A separate partition per order |

That third row is the one to avoid by accident. It gives every order its own
partition, so `SELECT * FROM orders WHERE user_id = ?` no longer works — you would
have to supply both columns on every read, because Cassandra cannot locate a
partition without the complete partition key.

Concept:

- The PARTITION KEY decides which node(s) in the Cassandra cluster will store the row. It is hashed, so partitions are scattered and you cannot range-scan across them.
- Clustering Columns are the data sorting key within a partition. Clustering columns define the order of rows inside a partition — like how an index in a sorted file works. They are stored sorted on disk (ascending unless you set `WITH CLUSTERING ORDER BY`), which is why range queries *within* one partition are cheap and range queries *across* partitions are not supported.
- The trap to name in an interview is the **hot / unbounded partition**: one partition lives on one replica set, so a key like `country` or a partition that grows forever (all of a chat room's messages under one `room_id`) concentrates load on a few nodes. The fix is to add a bucket to the partition key — `((room_id, day), message_id)`.

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

SQL defines four isolation levels (in order of increasing isolation strength). The
right way to read the matrix is **cumulative**: each level prevents everything the
weaker levels prevent, *plus* one more anomaly.

| Isolation level | Dirty read | Non-repeatable read | Phantom read | Write skew |
| --------------- | ---------- | ------------------- | ------------ | ---------- |
| Read Uncommitted | Possible | Possible | Possible | Possible |
| Read Committed | Prevented | Possible | Possible | Possible |
| Repeatable Read | Prevented | Prevented | Possible (per the standard) | Possible |
| Snapshot Isolation | Prevented | Prevented | Prevented | **Possible** |
| Serializable | Prevented | Prevented | Prevented | Prevented |

> **Correction — the old table said Serializable prevents "phantom reads", which
> reads as though that is *all* it prevents.** Serializable prevents every anomaly in
> the table; phantoms are just the last one it picks up on the way. The old "What It
> Prevents" column was really "the one extra anomaly this level adds", and stating it
> as the complete list is the mistake that gets caught in follow-ups.
>
> The second correction is that the table was missing **write skew**, which is the
> anomaly interviewers actually ask about, because it is the one that survives
> snapshot isolation. Concretely: two doctors are on call, and a rule says at least
> one must remain. Both run `SELECT count(*) FROM oncall WHERE on_call = true` (both
> see 2), both conclude it is safe, and both remove themselves. Each transaction read
> a consistent snapshot and wrote a different row, so nothing conflicts — and the
> invariant is broken. Only true serializability catches it.

What the anomalies are, in one line each:

| Anomaly | What you observe |
| ------- | ---------------- |
| Dirty read | You read a value another transaction wrote and then rolled back |
| Non-repeatable read | You read the same **row** twice and get two different values |
| Phantom read | You run the same **query** twice and the set of matching rows changed |
| Lost update | Two read-modify-writes race; the second silently overwrites the first |
| Write skew | Two transactions read the same set, write different rows, and jointly break an invariant no single one broke |

> **Correction — "the four SQL levels" describe a standard, not any real engine.**
> Every engine deviates, and quoting the standard at an engine that does something
> else is a common trip-up:

| Engine | The deviation worth knowing |
| ------ | --------------------------- |
| PostgreSQL | `READ UNCOMMITTED` silently behaves as `READ COMMITTED` — dirty reads are impossible. Its `REPEATABLE READ` is snapshot isolation, so it *does* prevent phantoms, but allows write skew. Its `SERIALIZABLE` is SSI, which catches write skew by aborting one transaction |
| MySQL / InnoDB | Default is `REPEATABLE READ`, not `READ COMMITTED`. Gap and next-key locks mean plain reads mostly do not see phantoms either, but locking reads behave differently from plain ones |
| Oracle | Only offers `READ COMMITTED` and `SERIALIZABLE`, and its `SERIALIZABLE` is really snapshot isolation — so write skew is possible under a level literally named Serializable |
| SQL Server | Defaults to `READ COMMITTED` with locks; `READ_COMMITTED_SNAPSHOT` switches it to MVCC and changes behaviour under load |

The default that matters most: **PostgreSQL and Oracle default to Read Committed,
MySQL defaults to Repeatable Read.** If a design relies on a level, set it explicitly
rather than inheriting whatever the engine chose.

#### 4. Durability

- Once a transaction is committed, its changes are permanent, even if the system crashes immediately afterward.
- That's possible because the RDBMS flushes the **write-ahead log record** to durable storage before confirming the commit — not the data pages themselves.

Worth being precise about the mechanism, because it is a common follow-up:

- The order is **log first, data pages later**. Writing a small sequential log record
  and `fsync`-ing it is fast; rewriting scattered 8 KB pages is not. On restart, the
  engine replays the log to bring the pages forward. This is why it is called
  write-*ahead* logging.
- **Durability is a dial, not a constant.** PostgreSQL's `synchronous_commit = off`
  acknowledges a commit before the log is flushed, trading a small window of possible
  data loss for a large latency win. MySQL has the same dial as
  `innodb_flush_log_at_trx_commit`. If someone says "the database guarantees
  durability", the right question is "at what `fsync` setting, and on how many nodes".
- **Durable on one node is not durable.** A single-node `fsync` survives a process
  crash and a power cut; it does not survive the disk or the machine. Production
  durability means the commit is on a quorum of replicas, which is what
  `synchronous_standby_names` (Postgres) or `acks=all` plus `min.insync.replicas`
  (Kafka) actually buy you.

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

- Single-leader (formerly "master-slave"), multi-leader (formerly "master-master"),
  and leaderless. The modern vocabulary is leader/follower or primary/replica; the old
  terms still appear in documentation, so recognise both.
- Full replication (keep N whole copies) vs erasure coding (the original notes'
  "hard copy vs erasure encoding").

### Erasure encoding

Instead of simply replicating data (e.g., keeping 3 full copies), erasure encoding splits data into smaller fragments and adds redundant parity fragments. Using these fragments, the system can reconstruct the original data even if some fragments are lost.

Key idea:

- Break data into k data blocks.
- Compute m parity blocks using mathematical functions (often XOR or Reed-Solomon).
- Store a total of k + m blocks across nodes.
- If up to m blocks are lost, the original data can still be reconstructed (true for
  maximum-distance-separable codes such as Reed-Solomon; not every code has this
  property, though the ones in production storage do).

Why anyone bothers — the storage-overhead comparison is the whole argument:

| Scheme | Storage overhead | Survives | Cost when a node dies |
| ------ | ---------------- | -------- | --------------------- |
| 3× replication | 200% (3.0× raw) | 2 node losses | Copy one whole replica |
| Reed-Solomon (6, 3) | 50% (1.5× raw) | 3 losses of 9 | Read 6 fragments, recompute |
| Reed-Solomon (10, 4) | 40% (1.4× raw) | 4 losses of 14 | Read 10 fragments, recompute |

The trade-off to state: erasure coding gives the same or better durability for less
than half the storage, but **reads are more expensive during a failure** (you must
fetch k fragments from k different nodes and do the maths, instead of reading one
replica), and **repair is network-heavy**. So it is used for cold and warm data —
S3 and HDFS archives, backups, media — while hot data stays replicated. Never say
"erasure coding is better than replication" without naming that access pattern.

Libraries exist in Java for production use:

- Jerasure (C, but bindings exist)
- Backblaze Reed-Solomon Java implementation
- Apache Commons Math + Hadoop EC

### Checksum or hashing

- Checksum or hashing -> data integrity verification
- Algorithms: MD5, SHA-1, SHA-256, HMAC, CRC32

> **Correction — listing these together implies they are interchangeable. They are
> not, and three of the five must not be used for security.** The question that
> separates them is *what are you defending against*: accidental corruption, or a
> deliberate attacker.

| Algorithm | Defends against | Use it for | Status |
| --------- | --------------- | ---------- | ------ |
| CRC32 | Accidental corruption only | Network frames, storage blocks, dedupe hints | Fine, and not a hash function in the security sense |
| MD5 | Nothing, against an attacker | Legacy checksums, cache keys, ETags | **Collisions are trivial** — never for signatures or passwords |
| SHA-1 | Nothing, against an attacker | Legacy git object ids | **Broken** (SHAttered, 2017); deprecated everywhere |
| SHA-256 | Collisions and tampering | Content addressing, digital signatures, Merkle trees | Current default |
| HMAC-SHA256 | Tampering **by someone without the key** | Webhook signatures, API request signing, tokens | Current default for authenticity |

The distinction worth being able to state: a **hash** proves the data has not changed
by accident; an **HMAC** proves it was produced by someone holding the shared secret.
A bare hash appended to a message defends against nothing, because an attacker who
changes the message can simply recompute the hash. Also note that none of these is a
*password* hash — passwords need a deliberately slow, salted function (bcrypt, scrypt,
Argon2), and using SHA-256 for them is a classic design-review finding.

### Quorum consensus (redundant distributed systems)

- N = Total number of replicas holding a copy of the data
- W = Write considered successful when acknowledged by W replicas
- R = Read considered successful when R replicas have responded with data
- The coordinator must wait for W replicas to acknowledge the write; the same for R replicas to respond to the read.
- `W + R > N` guarantees that the read set and the write set **overlap in at least
  one replica**, so at least one replica in every read has seen the latest
  acknowledged write.

> **Correction 1 — `W + R > N` gives overlap, not strong consistency.**
> The overlap is real and it is the point of the formula, but it is a weaker
> guarantee than the words "strong consistency" imply, and the gap is where
> interview follow-ups live:
>
> - Overlap tells you *a* replica in the read set has the newest value. It does not
>   tell you how the coordinator picks which of the R answers to return; that needs
>   versioning (a timestamp, a vector clock) and a last-write-wins or
>   application-level merge rule.
> - **Writes are not atomic across replicas.** A write that reaches W of N replicas
>   and then fails is not rolled back on the replicas that got it. A subsequent read
>   may see it or not, and Dynamo-style stores do not undo partial writes.
> - **Sloppy quorums break it outright.** When Cassandra or Dynamo accept a write on
>   a substitute replica via hinted handoff, that replica is not in the normal set of
>   N, so `W + R > N` no longer describes an overlap with the real replicas at all.
> - Concurrent writes can still produce conflicts that quorum arithmetic cannot
>   resolve on its own.
>
> Accurate phrasing: *`R + W > N` guarantees the read and write sets intersect, which
> gives read-your-writes and monotonic reads on a single key.* True linearizability
> needs a consensus protocol (Raft, Paxos), not a quorum formula.

> **Correction 2 — `W = N/2, R = N/2` does not satisfy the formula.**
> `N/2 + N/2 = N`, and `N` is not `> N`. By the very next row of the original table,
> that configuration is eventually consistent — the two rows contradicted each other.
> The balanced setting you want is the **majority quorum**:
>
> `W = R = floor(N/2) + 1`
>
> For N = 3 that is W = R = 2, giving `2 + 2 = 4 > 3`. For N = 5 it is W = R = 3,
> giving `6 > 5`. A majority on both sides always overlaps, because two majorities of
> the same set cannot be disjoint — that is the whole trick.

| Setting (N = 3) | W | R | W + R > N? | Effect |
| --------------- | - | - | ---------- | ------ |
| Fast writes | 1 | 3 | 4 > 3 ✔ | Cheap writes, expensive reads, still overlapping |
| Fast reads | 3 | 1 | 4 > 3 ✔ | Cheap reads, but one replica down blocks all writes |
| Balanced majority | 2 | 2 | 4 > 3 ✔ | Tolerates one node down for both reads and writes |
| The broken "balanced" | 1.5 | 1.5 | 3 > 3 ✘ | `N/2` each way — no overlap; eventually consistent |
| Fastest, weakest | 1 | 1 | 2 > 3 ✘ | Eventual consistency, lowest latency |

Depending on the use case we can choose the values of W and R to achieve the desired
consistency level. Two practical notes: `W = N` means any single replica failure
stops writes entirely, which is why it is rare in production; and N is the
**replication factor**, not the cluster size — a 100-node Cassandra cluster with
RF = 3 has N = 3 for quorum purposes.

### Strong consistency via consensus

> **Correction — ZooKeeper, etcd and Consul do not achieve consistency by locking,
> and they do not wait for *every* replica.**
> The original note said strong consistency "works by forcing a replica not to accept
> new reads/writes until every replica has agreed on the latest write". Two errors:
>
> - **The mechanism is consensus, not locking.** These systems run a replicated log
>   protocol — ZAB in ZooKeeper, Raft in etcd and Consul. Writes go to an elected
>   leader, which appends to a log replicated to the followers. Distributed locks are
>   something you can *build on top* of that log; they are not how the log itself
>   stays consistent.
> - **It is a majority, not everybody.** Waiting for every replica would make the
>   system less available than a single node, since any one failure would stop it.
>   Consensus commits an entry once a **majority** has it, which is precisely what
>   lets a 5-node etcd cluster lose 2 nodes and keep serving.

- Strong consistency in practice means routing through a leader elected by consensus,
  and committing once a quorum has durably accepted the entry.
- The cost is real and worth naming: every write pays at least one round trip to a
  majority, so throughput is bounded by the slowest node in that majority and by
  cross-AZ latency. That is why these systems store *metadata* — leader election,
  configuration, service registries, locks — and not your application's data.
- The caveat on distributed locks: a lock held by a process that has hung or been
  paused can expire while that process still believes it holds it. Safe use needs a
  **fencing token** — a monotonically increasing number handed out with the lock, which
  the downstream resource checks and uses to reject stale holders.

---

## Distributed transactions

Examples of distributed transaction implementations.

### What each one does on failure

This is the table that gets probed, and "it rolls back" is the wrong answer for
three of these four rows.

| Protocol | On a participant failure | On a coordinator failure | Consistency reached |
| -------- | ------------------------ | ------------------------ | ------------------- |
| 2PC | Coordinator broadcasts abort; all roll back | **Blocks** — participants that voted YES hold locks, in doubt | Atomic, strongly consistent |
| 3PC | Aborts; timeouts let participants decide alone | Participants can time out and decide | Atomic *only* if the network never partitions |
| TCC | Cancel runs against every reserved resource | A recovery job re-drives Confirm or Cancel | Eventually consistent |
| Saga | Compensating transactions for the committed steps | Orchestrator resumes from its persisted log | Eventually consistent |

The distinction underneath all four: a **rollback** erases a transaction nobody ever
saw. A **compensation** is a new business transaction that offsets one that was
already committed and already visible to others. You cannot un-send an email; you
send an apology.

### Two-Phase Commit (2PC)

- 2PC is a protocol that ensures all participants in a distributed transaction either commit or abort the transaction together.
- It consists of two phases: the prepare phase and the commit phase.
- In the prepare phase, the coordinator asks all participants if they are ready to commit.
- If all participants respond positively, the coordinator sends a commit message in the commit phase.
- If any participant responds negatively or fails to respond, the coordinator sends an abort message.

> **Correction — the note described the happy path and the participant-failure path,
> and left out the one thing 2PC is famous for: it blocks.**
> Once a participant has voted YES it must hold its locks and wait for the decision.
> If the coordinator crashes in that window, the participant is stuck **in doubt** —
> it cannot commit (others may have voted NO) and cannot abort (others may already
> have committed). It cannot ask the other participants either, because they are in
> the same position. So it waits, holding row locks, and every transaction touching
> those rows queues behind it. There is no timeout that is safe to take.
>
> This is not a footnote; it is the reason Saga, TCC and everything else in this
> section exist. Say it explicitly: **2PC is safe but blocking, and the blocking
> window is bounded by the coordinator's recovery time, not by any timeout you set.**

Where 2PC is still the right answer: one trust boundary, one datacentre, short
transactions, and a coordinator whose log is itself durable — XA across two local
resource managers, for example. Systems that need atomic multi-shard commit at scale
run 2PC *on top of* a consensus-replicated coordinator (Spanner, CockroachDB,
YugabyteDB), which is exactly what makes coordinator failure survivable.

### Three-Phase Commit (3PC)

- 3PC is an extension of 2PC that adds an additional phase to reduce the chances of blocking in case of failures.
- It consists of three phases: the canCommit phase, the preCommit phase, and the doCommit phase.
- In the canCommit phase, the coordinator asks all participants if they can commit.
- If all participants respond positively, the coordinator sends a preCommit message in the preCommit phase.
- Participants acknowledge the preCommit message and prepare to commit.
- Finally, in the doCommit phase, the coordinator sends a doCommit message to all participants to finalize the transaction.
- If any participant fails to respond or encounters an error, the coordinator can send an abort message.

> **Correction — 3PC only removes blocking under assumptions that real networks do
> not meet, and it is essentially unused in practice.**
> The extra preCommit phase lets a participant that times out infer the likely
> decision and proceed alone, which removes the blocking of 2PC — but **only** under a
> fail-stop model with bounded message delay and no network partitions. Add a
> partition and 3PC can produce a genuinely inconsistent outcome: one side times out
> and commits while the other aborts, which is worse than blocking, because at least
> a blocked 2PC is still correct when it unblocks.
>
> That is why you will not find 3PC in production systems. Know it for the exam
> answer, and name what replaced it: consensus-based commit, where the *decision*
> itself is replicated through Paxos or Raft so that losing the coordinator loses
> nothing. If asked "would you use 3PC", the strong answer is "no — I would replicate
> the coordinator instead".

### TC/C (Try Confirm/Cancel)

- TC/C is a pattern that separates the transaction into three distinct steps: try, confirm, and cancel.
- In the try step, the system attempts to perform the necessary operations for the transaction.
- If all operations succeed, the system proceeds to the confirmation step, where it finalizes the transaction.
- If any operation fails during the try step, the system enters the cancel step, where it rolls back any changes made during the try step.
- This pattern is particularly useful in scenarios where operations can be performed independently and can be rolled back if needed.

Two rules that make TCC actually work, and that the note did not state:

- **Try reserves, it does not apply.** Try puts the resource in a held state — funds
  frozen, seat blocked, stock allocated — visible as "pending" rather than as a
  completed change. That reservation is what makes Confirm cheap and guaranteed.
- **Confirm and Cancel must be idempotent and must eventually succeed.** Once Try has
  passed on every participant, there is no going back to "nothing happened"; the
  system retries Confirm (or Cancel) until it lands. A recovery job re-drives the ones
  left dangling, so both operations get called more than once and must tolerate it.
- Compared with 2PC, the reservation lives in *your* business tables rather than in
  database locks, so nothing is held at the storage layer and nothing blocks. That is
  the trade: application complexity instead of coordinator blocking.

### Saga Pattern

- Saga is a pattern for managing long-lived transactions in a distributed system.
- A saga is a **sequence of local transactions** `T1 … Tn`, each with a matching
  compensating transaction `C1 … Cn`. Each `Ti` commits on its own service, in its own
  database, immediately.
- If step `Ti` fails, the system runs `Ci-1 … C1` — in reverse order — to compensate
  the steps that already committed.
- **This pattern is useful for scenarios where transactions span multiple services and need to be eventually consistent.**

> **Correction 1 — the individual steps are not "sagas".**
> The note read "breaks down a transaction into smaller, independent steps called
> sagas". The whole sequence is *the* saga; the steps are local transactions with
> compensations. Small thing, but using the word both ways makes the rest of the
> explanation unfollowable.

> **Correction 2 — compensation is not rollback, and this is the substantive point.**
> `T2` has already committed and is already visible to everyone else by the time `T3`
> fails. `C2` cannot erase it; `C2` is a *new* transaction that offsets it — refund the
> charge, release the seat, credit the stock back. Consequences the interviewer will
> push on: compensations can themselves fail (so they must be retried, and therefore
> idempotent), some actions have no compensation at all (money paid to a third party,
> a shipment dispatched, an email sent), and the compensated end state is not
> necessarily identical to the original one — a refund leaves two ledger entries, not
> zero.

> **Correction 3 — Saga gives up isolation, not just atomicity.**
> This is the half that memorised answers always miss. Between `T2` committing and
> `C2` running, other transactions can read the intermediate state — a dirty read at
> the business level.

The standard countermeasures, worth being able to name:

| Countermeasure | What it does |
| -------------- | ------------ |
| Semantic lock | Mark the record `PENDING`/`RESERVED` so other readers know not to trust it |
| Commutative updates | Design steps so order does not matter (increment, not set) |
| Pessimistic ordering | Run the step most likely to fail *first*, before anything visible commits |
| Re-read value | Re-check the row immediately before acting, to detect a concurrent change |
| Versioning | Version records so a compensation can tell whether someone else moved |

**Choreography vs orchestration**, the follow-up that always comes next:

| Aspect | Choreography | Orchestration |
| ------ | ------------ | ------------- |
| How it runs | Each service reacts to events; no central control | A coordinator calls each step in turn |
| Best for | Few services, a stable linear flow | Branching logic, many steps, audit requirements |
| Weakness | No single place shows the flow; hard to debug | One codebase and one team become a coupling point |
| Failure recovery | Each service emits compensating events | Coordinator replays from its persisted state |

The orchestrator is **not** a single point of failure if its state is durable — that
is what Temporal, Netflix Conductor, AWS Step Functions and Camunda sell. It is a
single point of *coupling*, which is the honest trade-off to name.

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

- Examples of Caches -> Redis, Memcached, Varnish, AWS **ElastiCache** (spelled with
  a lowercase "i", no second "c").
- Licence change worth knowing, because it now comes up in "why did you pick X":
  Redis left the BSD licence in March 2024 for the source-available RSALv2/SSPLv1,
  the Linux Foundation forked the last BSD version as **Valkey** (backed by AWS,
  Google and Oracle), and Redis 8.0 in May 2025 added AGPLv3 back as a third option.
  So "Redis is open source" needs a version attached to be true. *(Checked September
  2026.)*

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

Three things this minimal handler is missing, all of which an interviewer will ask
about, and all of which are real production bugs rather than nitpicks:

| Missing | Why it matters | What to do |
| ------- | -------------- | ---------- |
| Signature verification | The endpoint is public, so anyone can POST `payment_success` | Verify the provider's HMAC signature header before parsing |
| Idempotency | Providers retry until they get a 2xx, so duplicates are normal | Dedupe on the provider's event id, in the same transaction as the effect |
| Fast acknowledgement | Slow handlers time out, so the provider retries and you process twice | Persist the raw event, return 2xx, process asynchronously |

The ordering rule that ties them together: **verify, persist, acknowledge, then
process**. Doing the business work before returning 200 is what turns an ordinary
slow dependency into duplicate charges.

### Notification channels

- Push Notifications -> APNs (Apple Push Notification service), FCM (Firebase Cloud
  Messaging), and Web Push for browsers. Note that SSE and WebSockets are *in-app*
  channels — they only work while your page or app is open — whereas APNs/FCM are the
  OS-level channels that reach a closed app. Mixing the two in one list hides that
  distinction, and it is usually the first question asked about a notification design.
- Mail Notifications -> Third-party services (SendGrid, Amazon SES, Mailgun)
- SMS Notifications -> Third-party services (Twilio, Vonage — formerly Nexmo — Plivo, AWS SNS)

---

## Load balancing, API gateways and networking

### Load balancers and API gateways

- Examples of Load Balancers -> Nginx, HAProxy, AWS ELB, Google Cloud Load Balancing
- Examples of API Gateways -> Kong, Apigee, AWS API Gateway, NGINX API Gateway, Kubernetes Ingress + API Gateway (like Ambassador, Istio, or Traefik)

### The OSI (Open Systems Interconnection) Model

Mnemonic, top down: **A**ll **P**eople **S**eem **T**o **N**eed **D**ata
**P**rocessing (the original notes' "APS TNDP" is the same initials).

| Layer # | Name | Examples | Carries |
| ------- | ---- | -------- | ------- |
| 7 | Application | HTTP, FTP, SMTP, DNS | Data |
| 6 | Presentation | Encryption (TLS), compression, encoding | Data |
| 5 | Session | RPC, NetBIOS, session management | Data |
| 4 | Transport | TCP, UDP, QUIC | Segment / datagram |
| 3 | Network | IP, ICMP, routers | Packet |
| 2 | Data Link | Ethernet, MAC, switches, ARP | Frame |
| 1 | Physical | Cables, hubs, radio, voltages | Bits |

> **Correction — the numbering was upside down, and this one has practical
> consequences.** The original table numbered Application as layer 1 and Physical as
> layer 7. OSI numbers from the **bottom up**: layer 1 is Physical, layer 7 is
> Application. The old table happened to get layer 4 right by coincidence (Transport
> is in the middle either way), which makes the error easy to miss.
>
> It matters because the layer numbers are used as vocabulary every day:
>
> - An **L4 load balancer** (AWS NLB, HAProxy in TCP mode) routes on IP and port. It
>   cannot see a URL path, cannot terminate TLS in the usual sense, and cannot do
>   host-based routing — but it is fast and protocol-agnostic.
> - An **L7 load balancer** (AWS ALB, NGINX, Envoy) parses HTTP, so it can route on
>   path or header, terminate TLS, retry idempotent requests and do sticky sessions —
>   at more CPU per request.
> - "**L3 vs L2**" in networking means routing (IP, across subnets) vs switching
>   (MAC, within a subnet).
>
> Under the old numbering, "L7 load balancer" would have meant a hub, and "L3" would
> have meant the session layer. Anyone using the standard numbering would be talking
> past you.

Practical note: OSI is a teaching model. Real traffic runs on the four-layer TCP/IP
model, where OSI layers 5–7 collapse into one Application layer. TLS is the classic
awkward case — nominally presentation (6), in practice sitting between transport and
application, and usually described as "layer 6-ish" or just "above TCP".

### Network protocols

| Protocol | Expansion | Used for |
| -------- | --------- | -------- |
| SMTP | Simple Mail Transfer Protocol | Sending emails |
| IMAP | Internet Message Access Protocol | Reading mail that **stays on the server**; the client syncs state (read, flagged, folders), so multiple devices see the same mailbox |
| POP3 | Post Office Protocol 3 | Downloading mail to one device; by default it then deletes the server copy, though "leave a copy on the server" is a standard client option. No server-side state, so it does not sync across devices |
| FTP | File Transfer Protocol | Transferring files over a network |
| SFTP | **SSH** File Transfer Protocol | Transferring files securely over an SSH connection (port 22). "Secure FTP" is a common misreading — SFTP is not FTP at all, and FTPS (FTP wrapped in TLS) is a different protocol again |
| SSH | Secure Shell | Secure remote login and other secure network services over an insecure network |
| TLS | Transport Layer Security | To check authenticity and integrity of the server; securing communications over a computer network |
| SSL | Secure Sockets Layer | The predecessor to TLS. **All SSL versions are deprecated and prohibited** (SSL 2.0 and 3.0 by RFC, TLS 1.0/1.1 deprecated in 2021); current practice is TLS 1.2 and TLS 1.3. "SSL" survives only as a habit — "SSL certificate" means a TLS certificate |
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

> **Correction — RSA 2048 and ECC 256 are *not* the same security level, and the
> "Equivalent Security: Yes" column did not say anything.**
> The right frame is a third quantity — the **security strength in bits**, meaning
> roughly "an attacker needs 2^k operations". You then read off the key size each
> algorithm needs to reach that strength. On that scale RSA 2048 is ~112-bit and ECC
> 256 is ~128-bit, so ECC 256 is the stronger of the two; RSA needs **3072** bits to
> match it. (Figures per NIST SP 800-57.)

| Security strength | RSA key size | ECC key size | Status |
| ----------------- | ------------ | ------------ | ------ |
| 80-bit | 1024 | 160 | Broken in practice; do not use |
| 112-bit | 2048 | 224 | Legacy minimum; being phased out |
| 128-bit | 3072 | 256 | Today's normal target |
| 192-bit | 7680 | 384 | High assurance |
| 256-bit | 15360 | 521 | Paranoid / very long-lived data |

The reason to care in a system-design answer: RSA key sizes grow much faster than ECC
ones for the same strength, so ECC gives smaller keys, smaller signatures, less
bandwidth per TLS handshake and less CPU — which is why modern TLS overwhelmingly
uses ECDHE for key exchange, and why constrained and mobile clients prefer ECDSA or
Ed25519 certificates.

### Algorithm families

- Examples of Symmetric Encryption -> AES, DES, 3DES, Blowfish, RC4, ChaCha20.
  Status matters as much as the list: **AES (128/256, in GCM mode) and ChaCha20-Poly1305
  are the current choices**. DES is broken by brute force, 3DES was disallowed by NIST
  after 2023, RC4 was prohibited in TLS by RFC 7465, and Blowfish's 64-bit block is
  too small for modern traffic. Name a *mode* as well as a cipher — "AES" alone does
  not say whether it authenticates, and AES-GCM (or AES-CBC plus a separate HMAC) is
  what you actually deploy.
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

Arithmetic check: 1 + 41 + 5 + 5 + 12 = **64 bits**, which fits a Java `long`
(1 sign bit + 41 timestamp + 5 data centre + 5 machine + 12 sequence). Two derived
numbers worth having ready:

- **41 bits of milliseconds ≈ 69.7 years** from the custom epoch
  (2^41 ms = 2.199 × 10^12 ms ÷ 86,400,000 ms/day ÷ 365.25 ≈ 69.7 years). Twitter's
  epoch was November 2010, so that generation of IDs runs out around 2080. Always set
  a custom epoch — using the Unix epoch throws away 40 of those years for nothing.
- **Throughput ceiling: 4,096 IDs per millisecond per machine**, so ~4.1 million per
  second per machine, and 32 × 32 = 1,024 machine slots. Past 4,096 in one
  millisecond the generator must block until the clock ticks over.
- The failure mode to name: **clock skew**. If NTP steps the clock backwards, the
  generator can reissue IDs it has already handed out. The standard mitigation is to
  refuse to generate (throw) while the clock is behind the last-seen timestamp, rather
  than silently producing duplicates.

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

> **Correction — the list above is all upside and no cost, which is not how mmap has
> been viewed for some years.** The counter-argument is well known enough that "we'd
> use mmap for speed" invites a follow-up, so know the other side:

| Problem | What actually happens |
| ------- | --------------------- |
| Invisible stalls | A page fault is a blocking disk read that looks like a plain memory access, so a thread stops with no I/O call in the profile |
| No control over eviction | The OS decides what stays resident; the database usually knows better than the OS which pages it will need |
| No control over writeback | The OS can flush a dirty page at any time, which makes write-ahead-log ordering guarantees hard to enforce |
| Transparent I/O errors | A failed read surfaces as SIGBUS, not as an error return you can handle |
| TLB shootdowns | Unmapping on a many-core machine forces cross-CPU interrupts and costs scale badly |

The short version, from the CIDR 2022 paper *"Are You Sure You Want to Use MMAP in
Your DBMS?"*: mmap is excellent for read-mostly data that fits in memory and for
sharing pages between processes, and a poor fit for a storage engine that needs to
control durability ordering and buffer replacement itself. MongoDB dropped its
mmap-based storage engine, and Postgres uses a shared buffer pool rather than mmap,
for exactly these reasons.

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
