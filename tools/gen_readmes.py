import pathlib
"""Phase 4: regenerate the per-folder README.md files from the file headers.
Run AFTER phase 3 (final names). Writes 01-DSA, 02-Java-Core, 03-LLD, 04-HLD-System-Design READMEs
and refreshes the AAScratches/README.md map table.
"""
import json, re, pathlib, sys
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from check_headers import parse_header, ROOT
SCR = pathlib.Path(__file__).resolve().parent
MK = json.loads((SCR / "mustknow.json").read_text(encoding="utf-8"))

# One-line "what is here" per topic folder (edit freely).
DESC = {
    "01-DSA/01-Arrays": "Prefix sums, Kadane, cyclic sort, in-place tricks, rotations.",
    "01-DSA/02-Two-Pointers-Sliding-Window": "Fixed and variable windows, opposite-end pointers, longest or shortest subarray and substring.",
    "01-DSA/03-Binary-Search": "Classic, rotated arrays, lower and upper bound, binary search on the answer space.",
    "01-DSA/04-Strings": "Parsing, encoding, palindromes, formatting, roman numerals, word games.",
    "01-DSA/05-Hashing-Prefix-Sum": "HashMap and HashSet counting, prefix sum plus map, grouping and set operations.",
    "01-DSA/06-Linked-List": "Reversal, cycle detection, merge, reorder, doubly linked lists.",
    "01-DSA/07-Stack-Queue-Monotonic": "Expression evaluation, parentheses, monotonic stack and deque patterns.",
    "01-DSA/08-Heap-Priority-Queue": "Top-K, median of a stream, scheduling with a min or max heap.",
    "01-DSA/09-Trees-BST": "Traversals, LCA variants, BST operations, construction from traversals, path sums.",
    "01-DSA/10-Trie": "Prefix trees for word search, suggestions and distinct substrings.",
    "01-DSA/11-Graphs": "BFS, DFS, topological sort, union-find, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, SCC.",
    "01-DSA/12-Dynamic-Programming": "1-D and 2-D DP, knapsack family, LCS and LIS, partition and matrix-chain problems.",
    "01-DSA/13-Greedy": "Jump games, stock trading, scheduling, local-choice proofs.",
    "01-DSA/14-Backtracking-Recursion": "Subsets, permutations, combination sum, N-Queens, word search, basic recursion.",
    "01-DSA/15-Intervals": "Merge, insert, meeting rooms, sweep line and difference arrays.",
    "01-DSA/16-Matrix": "Rotation, spiral and diagonal traversal, grid simulation.",
    "01-DSA/17-Math-Bit-Manipulation": "Number theory, primes, GCD and LCM, fast exponentiation, bit tricks.",
    "01-DSA/18-Sorting-Searching-Algorithms": "Sorting algorithm implementations, quick select, KMP, Rabin-Karp, segment tree.",
    "01-DSA/19-Design-Data-Structures": "LeetCode design problems: LRU and LFU cache, min stack, hit counter, time-based KV store.",
    "01-DSA/20-Scenario-Based-Problems": "Real-world style questions from Karat, Atlassian and onsite rounds: logs, votes, ratings, distances.",
    "02-Java-Core/Concurrency": "Threads, locks, producer-consumer, CompletableFuture, ThreadLocal, executors.",
    "02-Java-Core/IO": "Scanner, CSV reading and appending, basic input and output.",
    "02-Java-Core/Language-Features": "Generics, enums, varargs, immutability, dynamic proxies, streams, default methods, date-time.",
    "02-Java-Core/Security-Crypto": "AES, RSA and Caesar cipher examples.",
    "02-Java-Core/Tricky-MCQ": "Output-prediction puzzles: what does this print, does it compile.",
    "03-LLD/Design-Patterns/1. Creational Design Patterns": "Factory, Builder, Prototype, Abstract Factory, Singleton.",
    "03-LLD/Design-Patterns/2. Structural Patterns": "Adapter, Facade, Decorator, Proxy, Composite, Flyweight, Bridge.",
    "03-LLD/Design-Patterns/3. Behavioral Patterns": "Strategy, Template, Observer, Iterator, Command, Memento, State, Chain of Responsibility, Mediator, Visitor, Interpreter.",
    "03-LLD/Problems": "Classic LLD interview problems: Tic-Tac-Toe, parking lot.",
    "03-LLD/Data-Structure-Implementations": "HashMap and a dynamic array deque built from primitives.",
    "03-LLD/WorkFlowExecutor": "A small workflow executor with a bounded queue (needs Guava; read, do not run).",
    "04-HLD-System-Design/code": "Building blocks you can be asked to sketch: ID generator, wide-column store, Merkle tree, erasure coding.",
}
NOTES = {
    "01-DSA": [("DSA memory keypoints", "notes/DSA_Memory_Keypoints.md"), ("DSA memory keypoints II", "notes/DSA_Memory_Keypoints_II.md"),
               ("Classic 150 roadmap", "roadmaps/Classic_150_Roadmap.md"), ("Atlassian question list", "roadmaps/Atlassian_Question_List.md"),
               ("LeetCode 500 links", "roadmaps/LeetCode_500_Links.md")],
    "02-Java-Core": [("Core Java Q&A", "notes/Core_Java_QA.md"), ("Java 9 to 21 Q&A", "notes/Java_9_to_21_QA.md"), ("Java 8 Streams Q&A", "notes/Java8_Streams_QA.md"),
                     ("Garbage Collection Q&A", "notes/GC_QA.md"), ("Generics Q&A", "notes/Generics_QA.md"), ("Collections Q&A", "notes/Collections_QA.md")],
    "03-LLD": [],
    "04-HLD-System-Design": [("HLD keypoints", "notes/HLD_System_Design_Keypoints.md"), ("Payment system design", "notes/Payment_System_Design.md"),
                             ("Distributed transactions and Saga Q&A", "notes/Distributed_Transactions_Saga_QA.md")],
}
TITLES = {"01-DSA": "DSA practice", "02-Java-Core": "Java Core", "03-LLD": "Low-Level Design", "04-HLD-System-Design": "HLD and System Design"}
INTRO = {
    "01-DSA": "Every solved problem, grouped by technique. Inside a folder the filename order **is** the practice order: `A` building blocks, `B` easy, `C` medium, `D` hard, numbered by dependency. Open any file: the header tells you the problem, the approach, the one insight to remember, and the follow-ups; `main()` runs the cases.",
    "02-Java-Core": "Language and JVM demos, one runnable file each, plus interview Q&A notes.",
    "03-LLD": "The GoF patterns, classic LLD interview problems, and data structures built from scratch. Each pattern file maps every class to its pattern role in the header.",
    "04-HLD-System-Design": "System design notes and a few building blocks in code.",
}

def link(rel):
    return rel.replace(" ", "%20")

def gen(top):
    topdir = ROOT / top
    folders = {}
    for p in sorted(topdir.rglob("*.java")):
        f = p.relative_to(ROOT).as_posix().rsplit("/", 1)[0]
        folders.setdefault(f, []).append(p)
    lines = [f"# {TITLES[top]}", "", INTRO[top], ""]
    if NOTES[top]:
        lines += ["## Notes", ""] + [f"- [{t}]({l})" for t, l in NOTES[top]] + [""]
    lines += ["## Topics", "", "| Folder | Files | Must-know | What is here |", "|---|---|---|---|"]
    total = 0; total_mk = 0
    parsed = {}
    for f, ps in folders.items():
        rows = []
        for p in ps:
            h = parse_header(p.read_text(encoding="utf-8", errors="replace")) or {}
            rows.append((p, h))
        parsed[f] = rows
        mk = sum(1 for _, h in rows if h.get("mustKnow"))
        total += len(rows); total_mk += mk
        sub = f[len(top) + 1:]
        lines.append(f"| [{sub}]({link(sub)}/) | {len(rows)} | {mk} | {DESC.get(f, '')} |")
    lines.append(f"| **Total** | **{total}** | **{total_mk}** | |")
    lines.append("")
    for f, rows in parsed.items():
        sub = f[len(top) + 1:]
        lines += [f"## {sub}", "", DESC.get(f, ""), ""]
        mks = [(p, h) for p, h in rows if h.get("mustKnow")]
        if mks:
            lines += ["**Do these first:** " + ", ".join(f"[{p.name}]({link(sub)}/{link(p.name)})" for p, _ in mks), ""]
        lines += ["| File | Problem | Level | Key insight |", "|---|---|---|---|"]
        for p, h in rows:
            title = h.get("title") or p.stem.split("_", 1)[-1]
            meta = h.get("meta", "").replace(" | ", " / ").replace("|", "/")
            star = " *" if h.get("mustKnow") else ""
            insight = (h.get("insight") or "").replace("|", "/")
            # first sentence only, capped
            m = re.match(r"(.+?[.!?])(\s|$)", insight)
            insight = (m.group(1) if m else insight)[:220]
            lines.append(f"| [{p.name}]({link(sub)}/{link(p.name)}){star} | {title} | {meta} | {insight} |")
        gaps = MK.get(f, {}).get("gaps", [])
        hi = [g for g in gaps if g["priority"] == "high"][:3]
        if hi:
            lines += ["", "**Worth adding next:**", ""] + [f"- {g['problem']}: {g['technique']}" for g in hi]
        lines.append("")
    lines += ["`*` = must-know. Run any file with `tools/runjava <file>` from the repo root, or open it as an IntelliJ scratch.", ""]
    (topdir / "README.md").write_text("\n".join(lines), encoding="utf-8")
    print(f"{top}: {total} files, {total_mk} must-know, {len(folders)} folders")

if __name__ == "__main__":
    for top in TITLES:
        gen(top)
