"""
=====================================================================
 LRU Cache                                   LeetCode 146 | Medium   MUST-KNOW
=====================================================================

PROBLEM
  Design a cache with a fixed capacity. get(key) returns the value or -1; put(key, value)
  adds or updates. When a put goes over capacity, the least recently used key is removed.
  Both must be O(1). A get counts as a use, and so does a put to an existing key.

EXAMPLE
  capacity 2: put(1,1) put(2,2) get(1) -> 1    put(3,3) evicts 2, since 1 was used later
  get(2) -> -1    put(4,4) evicts 1    get(1) -> -1    get(3) -> 3    get(4) -> 4

APPROACH  (an ordered dict as the usage order)
  1. Keep the items in a collections.OrderedDict: oldest use first, newest last.
  2. get: missing -> -1; else move_to_end(key) and return the value.
  3. put: an existing key moves to the end; set the value; if over capacity,
     popitem(last=False) removes the oldest.

KEY INSIGHT
  An OrderedDict is a hash map threaded on a doubly linked list - the same pair a Java
  answer builds by hand - so finding a key, moving it to the end and dropping the front
  are all O(1).

COMPLEXITY
  Time  O(1) for get and put
  Space O(capacity)

INTERVIEW FOLLOW-UPS
  - Build it without OrderedDict: a dict of key -> node and a doubly linked list with dummy
    head and tail nodes (01-DSA/19-Design-Data-Structures has the Java version).
  - functools.lru_cache: the same policy as a decorator for function results.
  - LFU instead (LeetCode 460): a count per key, and a list per count.
  - Many threads at once: one lock around get and put.

RUN
  main() runs LeetCode's example and an update, printing actual vs expected.
"""
from collections import OrderedDict


class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.items = OrderedDict()

    def get(self, key):
        if key not in self.items:
            return -1
        self.items.move_to_end(key)
        return self.items[key]

    def put(self, key, value):
        if key in self.items:
            self.items.move_to_end(key)
        self.items[key] = value
        if len(self.items) > self.capacity:
            self.items.popitem(last=False)


def check(label, actual, expected):
    print(f"{label}: {actual}   expected {expected}")


def main():
    cache = LRUCache(2)
    cache.put(1, 1)
    cache.put(2, 2)
    check("case 1 get(1)        ", cache.get(1), 1)
    cache.put(3, 3)                            # evicts 2: 1 was used more recently
    check("case 2 get(2) evicted", cache.get(2), -1)
    cache.put(4, 4)                            # evicts 1
    check("case 3 get(1) evicted", cache.get(1), -1)
    check("case 4 get(3)        ", cache.get(3), 3)
    check("case 5 get(4)        ", cache.get(4), 4)
    cache.put(3, 30)                           # an update is a use too: 4 is now the oldest
    cache.put(5, 5)
    check("case 6 updated get(3)", cache.get(3), 30)
    check("case 7 get(4) evicted", cache.get(4), -1)


if __name__ == "__main__":
    main()
