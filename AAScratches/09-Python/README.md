# Python practice

Python practice, one runnable file each, with the same header as the Java files: problem, example, approach, key insight, complexity, follow-ups. `main()` prints each actual result next to the expected one.

## Topics

| Folder | Files | Must-know | What is here |
|---|---|---|---|
| [01-Essentials](01-Essentials/) | 3 | 2 | Python as interviews use it: collections (Counter, OrderedDict), sorting with keys, decorators and closures. |
| **Total** | **3** | **2** | |

## 01-Essentials

Python as interviews use it: collections (Counter, OrderedDict), sorting with keys, decorators and closures.

**Do these first:** [A01_TopKFrequentWords.py](01-Essentials/A01_TopKFrequentWords.py), [C01_LRUCache.py](01-Essentials/C01_LRUCache.py)

| File | Problem | Level | Key insight |
|---|---|---|---|
| [A01_TopKFrequentWords.py](01-Essentials/A01_TopKFrequentWords.py) * | Top K Frequent Words | LeetCode 692 / Medium | One sort key gives both orders: negating the count turns "bigger first" into ascending order, and the word itself breaks ties. |
| [B01_RetryDecorator.py](01-Essentials/B01_RetryDecorator.py) | Retry Decorator | Python / Medium | @retry(3) is two calls: retry(3) runs once, where the function is defined, and returns the real decorator, which then receives the function. |
| [C01_LRUCache.py](01-Essentials/C01_LRUCache.py) * | LRU Cache | LeetCode 146 / Medium | An OrderedDict is a hash map threaded on a doubly linked list - the same pair a Java answer builds by hand - so finding a key, moving it to the end and dropping the front are all O(1). |

`*` = must-know. Open any file in Code Viewer (`tools/codeview`): Ctrl+Enter runs it with Python and ticks each expected line; Practice hides the solution. Or run `python <file>`.
