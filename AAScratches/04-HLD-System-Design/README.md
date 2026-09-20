# HLD and System Design

System design notes and a few building blocks in code.

## Notes

- [HLD keypoints](notes/HLD_System_Design_Keypoints.md)
- [Payment system design](notes/Payment_System_Design.md)
- [Distributed transactions and Saga Q&A](notes/Distributed_Transactions_Saga_QA.md)

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [code](code/) | 4 | 2 | Building blocks you can be asked to sketch: ID generator, wide-column store, Merkle tree, erasure coding. |
| **Total** | **4** | **2** | |

## code

Building blocks you can be asked to sketch: ID generator, wide-column store, Merkle tree, erasure coding.

**Do these first:** [A01_SnowflakeIdGenerator.java](code/A01_SnowflakeIdGenerator.java), [A02_WideColumnStore.java](code/A02_WideColumnStore.java)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_SnowflakeIdGenerator.java](code/A01_SnowflakeIdGenerator.java) * | Snowflake ID Generator | HLD building block | Custom epoch (2021-01-01), not the Unix epoch: 41 bits starting at 1970 would have expired in 2039; starting the clock recently buys the full 69 years. |
| [A02_WideColumnStore.java](code/A02_WideColumnStore.java) * | Wide-Column Store (Cassandra / HBase data model) | HLD building block | Fixed: columns lived in a HashMap, so a row's cells came back in arbitrary order and no range query was possible. |
| [C01_MerkleTree.java](code/C01_MerkleTree.java) | Merkle Tree - anti-entropy between replicas | HLD building block | Odd level -> the last hash is paired with itself (Bitcoin's rule). |
| [D01_ErasureCoding.java](code/D01_ErasureCoding.java) | Erasure Coding - XOR parity instead of 3x replication | HLD building block | Fixed: xor() read b[i] for i up to a.length, so unequal blocks threw ArrayIndexOutOfBoundsException (or silently truncated). |

**Worth adding next:**

- Consistent hashing ring with virtual nodes: TreeMap/sorted ring of hash positions, N virtual nodes per physical node, successor lookup via ceilingEntry, add/remove node and report which keys move
- Distributed rate limiter (token bucket + sliding-window counter): Token bucket with lazy refill computed from elapsed time, sliding-window log and sliding-window counter variants, per-key limiter map, then the distributed version using an atomic check-and-decrement (Redis INCR/EXPIRE or a Lua script) with a note on why GET-then-SET races
- Bloom filter (with false-positive math and a counting variant): Bit array plus k independent hashes derived by double hashing, sizing from m = -n*ln(p)/(ln2)^2 and k = (m/n)*ln2, measured false-positive rate, counting Bloom filter for deletes

`*` = must-know. Run any file with `tools/runjava <file>` from the repo root, or open it as an IntelliJ scratch.
