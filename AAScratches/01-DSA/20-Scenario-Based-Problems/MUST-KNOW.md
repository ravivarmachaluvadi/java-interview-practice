# 20-Scenario-Based-Problems — must-know order

**Techniques in this topic:** Karat / CodeSignal-style screens: messy real-world input, no clean array signature - parsing is half the problem, Frequency map + argmax, with an explicit deterministic tie-break rule, Group-by-key into buckets (HashMap<String, List<T>>), then aggregate per bucket, Sort-then-scan over per-entity timestamps; fixed-size time windows (60 minutes) on sorted times, Last-seen-index single pass for minimum distance between two tokens in a stream, Tokenizing free text: regex splits, case-folding, index vs character-offset positions, Top-k selection: full sort vs heap, and when the k-vs-n trade-off actually matters, Multi-key / positional comparators and lexicographic tie-breaks in Java, Inverted-index style multi-map modelling (user->items, item->users) for two-hop traversal, Data-structure design under an interface: keep a running maximum cheap across mutations, Complexity stated in the problem's own variables (W words, S length, n events, k distinct counts)

| | |
|---|---|
| Problems | 17 |
| Must-know | 5 |

## Must-know — do these first

| # | Problem | Technique |
|---|---|---|
| 1 | `A02_FindTopIpaddress.java` | Frequency map plus argmax scan |
| 2 | `A05_MinimumDistanceBetweenWordsV2.java` | Last-seen index, single pass |
| 3 | `C02_HighAccessEmployees.java` | Sort times, check i minus i-2 window |
| 4 | `C03_BadgeAccess.java` | Group by person, sliding 60-min window |
| 5 | `D01_PopularityTracker.java` | Count map plus count-to-ID bucket TreeMap |

## Full practice order


### A — Building blocks

- `A01_StudentGrade.java` — Threshold bucketing if-else ladder
  - The floor of the folder - ordered threshold branching, the primitive every scoring/tiering problem here reduces to.
- `A02_FindTopIpaddress.java` — Frequency map plus argmax scan **[must-know]**
  - Parse a token out of a log line, count into a HashMap, scan for the max - the single primitive that four other files in this folder assume.
- `A03_ElectionWinner.java` — Count votes with lexicographic tie-break
  - Same count-and-argmax as the IP problem, but adds the deterministic tie-break rule interviewers always probe for; note the file's comparison bug when maxVotes ties at the initial state.
- `A04_BestAverageGrade.java` — Group-by-key then aggregate
  - Upgrades the counter map to HashMap<String,List<Integer>> - the group-then-aggregate shape that BadgeAccess, HighAccessEmployees and TopVideos all reuse.
- `A05_MinimumDistanceBetweenWordsV2.java` — Last-seen index, single pass **[must-know]**
  - LC 243 in its purest form: O(n) time, O(1) space by remembering the last position of each word - the exact engine the two harder distance files in this folder wrap in parsing.

### B — Easy

- `B01_MinimumDistanceBetweenWords.java` — Closed-form midpoint arithmetic
  - Pure index arithmetic with no data structure, but it teaches the midpoint-of-a-word offset that ShortestDistanceBetweenTwoWords needs to be understood at all.
- `B02_ShortestDistance.java` — Tokenize paragraph, last-seen index
  - Takes the A-tier last-seen pattern and adds the real interview friction: splitting on \\W+, case-insensitive matching, and the not-found return contract.
- `B03_TopVideos.java` — Aggregate by key, then top-k
  - Adds sorting on top of the group-by from BestAverageGrade; the follow-up 'why not a size-k min-heap' is the whole point and sets up NearestPlacesFinder.
- `B04_KARAT_R1.java` — Letter multiset containment
  - The canonical Karat round-1 opener - char-count array per word against the note, letters cannot be reused; easy once you see it as multiset subset, and it needs complexity in W and S.

### C — Medium

- `C01_ShortestDistanceBetweenTwoWords.java` — Character-offset midpoints, all-pairs min
  - Combines the midpoint arithmetic and the tokenizing from tier B, and the distance is now in characters not word indices - the variant that trips people who only memorised LC 243.
- `C02_HighAccessEmployees.java` — Sort times, check i minus i-2 window **[must-know]**
  - LC 2933 and the clean canonical form of the time-window family: after sorting, three-in-an-hour collapses to one comparison of times[i] - times[i-2] < 60.
- `C03_BadgeAccess.java` — Group by person, sliding 60-min window **[must-know]**
  - The Karat original that HighAccessEmployees is the stripped-down version of - same windowing, but you must reconstruct the window's members and format minutes back to HHMM.
- `C04_NearestPlacesFinder.java` — Haversine distance, k-nearest by sort
  - Top-k again but with an expensive computed key, so it forces the two staff-level answers: cache the distance (decorate-sort-undecorate) and use a bounded max-heap for k much smaller than n.
- `C05_RankTeamsByVotes.java` — Position count matrix, multi-key comparator
  - Generalises the single tie-break from ElectionWinner into a full 26-by-n positional tally with a cascading comparator - the comparator design is the interview signal, not the counting.
- `C06_MovieRecommender.java` — Bipartite maps, two-hop traversal
  - The most open-ended medium here: build user->ratings and movie->likers inverted indexes, then hop user -> similar users -> unseen liked movies, and defend the similarity and ordering rules you invented.

### D — Hard

- `D01_PopularityTracker.java` — Count map plus count-to-ID bucket TreeMap **[must-know]**
  - Design-under-an-interface with a running maximum across increments and decrements - the exact bucket-map skeleton behind LFU cache and All O(1), and the one problem here that reads as staff-level.
- `D02_Election.java` — Josephus recurrence
  - Needs the non-obvious insight that the survivor's index maps back through (f(n-1) + k - 1) % n + 1; unsimulatable in O(n) any other way, and the 1-indexing is where everyone loses it.

## Interview readiness

This folder is a solid but narrow Karat/CodeSignal set: 17 files that are really about 12 distinct problems, because ShortestDistance, ShortestDistanceBetweenTwoWords, MinimumDistanceBetweenWords and MinimumDistanceBetweenWordsV2 are four takes on one last-seen-index problem, Election/ElectionWinner are two, and BadgeAccess and HighAccessEmployees are literally the same 60-minute-window solution written twice. What is genuinely covered is the "flat list of records" family: parse messy input, group by key, count, argmax with a tie-break, top-k, multi-key comparator, and one interface-design problem (PopularityTracker) plus one two-hop inverted index (MovieRecommender). That is most of a Karat round 1, and he would pass those. The hole is round 2 and the harder product-company screens, which are almost always relational rather than tabular: records that describe edges between entities (parent/child, student/course, enter/exit) where the answer needs graph traversal, all-pairs enumeration, or a state machine over paired events, not a frequency map. The Karat ancestor question and the browsing-history question in particular are the two most reported second-round questions in this format and neither is here in any form. Adjacent folders cover the mechanics he would need (AccountsMerge, FloodFill, MeetingRooms, BasicCalculator, TextJustification, SubdomainVisitCount), so this is not a technique deficit — it is that he has never done the scenario framing where the interviewer adds part 2 and part 3 on top of a question he just solved, which is exactly how these rounds are run. Two to three sessions on the gaps below, done as multi-part problems rather than single functions, would close it.

## Gaps — canonical problems NOT in this folder

| Priority | Problem | Technique | Why it matters |
|---|---|---|---|
| high | Common Ancestor (Karat, 3 parts): given parent/child pairs, (1) list individuals with zero parents and with exactly one parent, (2) do two individuals share any common ancestor, (3) find the earliest/furthest ancestor of an individual | Build a directed graph from edge pairs (child->parents), degree counting for part 1, BFS/DFS upward + ancestor-set intersection for part 2, BFS with level tracking for part 3 | This is the single most frequently reported Karat second-round question and nothing in the folder resembles it. His covered themes are all flat-record aggregation; here the input is a list of edges and the answer needs traversal. The three-part escalation is the whole point of the question and he has never rehearsed it. AllNodesDistanceKInBinaryTree and CourseSchedule in other folders are not substitutes - neither starts from unlabelled pair-of-strings input with no root given. |
| high | Longest Common Continuous Subarray / browsing history (Karat): given two users' ordered page-visit lists, return the longest contiguous run they share; follow-up, do it across many users and return the pair with the longest shared run | DP grid over two sequences (or rolling-hash / suffix comparison), then all-pairs over users for the follow-up | The other canonical Karat round-2 question, and the only one in the common set that is not solvable with a HashMap. Everything in his folder is single-pass or group-by; this needs a 2D DP he has to derive on the spot from lists of strings rather than a clean int[][] signature. The follow-up also forces him to state complexity in users and history length, which his notes show he practises elsewhere but not here. |
| high | Student Course Pairs (Karat): given (student, course) pairs, output every pair of students with the courses they have in common; follow-up, return the pair sharing the most courses | Group student->set(courses), enumerate all student pairs, set intersection; or invert to course->students and count co-occurrences per pair with a composite key | Third member of the standard Karat round-2 rotation. MovieRecommender gives him inverted-index two-hop traversal but not all-pairs enumeration, canonical pair ordering as a map key, or the O(n^2 * c) vs co-occurrence-counting trade-off the interviewer always asks about. Output formatting (every pair, including pairs with zero shared courses) is a specific trap he has not met. |
| high | Badge access records validation (the actual Karat Badge Access part 1): given records of (name, "enter"/"exit"), return everyone who exited without entering and everyone who entered without exiting | Per-person state machine over the ordered record stream, plus a leftover-state sweep at the end | He believes this is covered - BadgeAccess.java and HighAccessEmployees.java are both the 60-minute-window part, duplicated, and neither touches the enter/exit pairing half that is asked first in the real question. Pairing stateful open/close events and reporting both failure directions is a different skill from windowing sorted timestamps, and getting handed the part he has not done after breezing through the part he has done twice is the worst way to meet it. |
| high | Analyze User Website Visit Pattern (LC 1152): given (user, timestamp, website) triples, find the 3-page sequence visited by the most distinct users, lexicographically smallest on a tie | Sort by timestamp, group per user, enumerate all 3-subsequences per user as a deduplicated set, count across users, argmax with lexicographic tie-break | The standard hard scenario question at Amazon and in CodeSignal screens, and it stacks three things he has each done separately but never together: sort-then-group by user, combination enumeration with per-user dedup (counting distinct users, not occurrences), and a lexicographic tie-break. The dedup is where most candidates silently get a wrong answer and never find out. |
| medium | Invalid Transactions (LC 1169): parse "name,time,amount,city" strings and return every transaction over $1000 or occurring within 60 minutes of another transaction by the same name in a different city | Parse into records, group by name, pairwise comparison within the group on time and city, mark both sides of an offending pair | Closest thing to a production alerting rule that gets asked in a screen, and it exercises the failure mode his 60-minute-window practice does not: the condition is symmetric, so both transactions in a bad pair are invalid, and duplicates with identical fields must each be flagged. He has the window idea but has only ever applied it to a one-directional scan. |
| medium | Design Underground System (LC 1396): checkIn(id, station, t) / checkOut(id, station, t) / getAverageTime(start, end) under a given interface | In-flight map id->(station,time) for open journeys plus a route->(totalTime,count) running aggregate for O(1) average | The standard interface-design scenario at product companies. PopularityTracker gives him one interface-with-running-aggregate problem, but this one pairs open and closed events and keeps a running mean per composite key, and the interviewer's follow-up is always "why not store every journey and average on read" - a trade-off he should be able to answer with numbers rather than by rewriting. |
| medium | Meeting scheduler / free-slot finder: given two people's busy calendars plus each person's working-hours bounds and a required duration, return every slot where both are free (Employee Free Time is the many-person version) | Merge and sort busy intervals, intersect the two busy sets or complement within bounds, then filter gaps by duration; heap sweep for the n-person case | The one calendar-shaped scenario question that keeps appearing in these rounds, and it is absent here. The intervals folder has MergeIntervals, MeetingRoomsII and IntervalListIntersections, so the mechanics are in reach, but he has not practised the scenario framing where the bounds and the minimum duration turn a clean interval problem into a parsing-plus-complement problem and off-by-one at the bounds decides the result. |

