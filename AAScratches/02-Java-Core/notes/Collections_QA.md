# Collections Q&A

Interview notes on Java collections; currently a single comparison table plus a prompt for questions still to be explored.

## Questions to explore

> Explain, and give tricky interview questions (with answers) to ask a dev with 10 years of experience, on switch expressions.

This prompt was kept from the original note. It is about switch expressions rather than collections; that topic is covered in [Java 9 to 21 Q&A](Java_9_to_21_QA.md).

## 1. How does `PriorityQueue` differ from `TreeSet`?

| Aspect | PriorityQueue | TreeSet |
| --- | --- | --- |
| Duplicates | Allowed | Not allowed |
| Ordering | Based on comparator | Sorted & unique |
| Iteration | Not ordered | Ordered |
| Backed by | Heap | Red-black tree |
