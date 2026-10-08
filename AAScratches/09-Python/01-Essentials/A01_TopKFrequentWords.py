"""
=====================================================================
 Top K Frequent Words                        LeetCode 692 | Medium   MUST-KNOW
=====================================================================

PROBLEM
  Given a list of words and k, return the k most frequent words, most frequent first.
  Words with the same count come in alphabetical order.

EXAMPLE
  ["i", "love", "leetcode", "i", "love", "coding"], k=2   ->  ["i", "love"]
  ["the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"], k=4
                                                          ->  ["the", "is", "sunny", "day"]
  ["b", "a"], k=2                                         ->  ["a", "b"]   a tie goes A to Z

APPROACH  (count, then sort by a two-part key)
  1. Count every word with collections.Counter.
  2. Sort the distinct words by the key (-count, word): higher counts first, ties A to Z.
  3. Take the first k.

KEY INSIGHT
  One sort key gives both orders: negating the count turns "bigger first" into ascending
  order, and the word itself breaks ties. No comparator function is needed.

COMPLEXITY
  Time  O(n + m log m)  n words to count, m distinct words to sort
  Space O(m)            the counter

INTERVIEW FOLLOW-UPS
  - O(n log k) instead: heapq.nsmallest(k, counts, key=lambda w: (-counts[w], w)).
  - Counter.most_common(k) breaks ties by first appearance, not A to Z: why it is not enough.
  - Words arriving as a stream too big for memory: count in chunks, add the Counters up.

RUN
  main() runs 3 cases and prints actual vs expected.
"""
from collections import Counter


def top_k_frequent(words, k):
    counts = Counter(words)
    ordered = sorted(counts, key=lambda word: (-counts[word], word))
    return ordered[:k]


def check(label, actual, expected):
    print(f"{label}: {actual}   expected {expected}")


def main():
    check("case 1 typical", top_k_frequent(["i", "love", "leetcode", "i", "love", "coding"], 2),
          ["i", "love"])
    check("case 2 longer ", top_k_frequent(
        ["the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"], 4),
        ["the", "is", "sunny", "day"])
    check("case 3 a tie  ", top_k_frequent(["b", "a"], 2), ["a", "b"])


if __name__ == "__main__":
    main()
