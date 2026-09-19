# AI-Assisted Development Playbook
## A Practical Guide for Working Safely and Effectively with Large, Unfamiliar Repositories

> **Purpose:** Use this as a real-time playbook when you join an unfamiliar codebase, receive a large or incomplete requirement, and need to use an AI assistant/agent effectively without blindly asking it to "implement the ticket."

---

# 0. The Core Principle

The goal is **not**:

> Give the requirement to AI → AI writes code → developer reviews it.

The goal is:

> **Discover → Understand → Challenge → Clarify → Plan → Implement → Verify → Review**

AI should progressively build a model of the system with you.

For a large repository, the developer does **not** need to understand every line of code. The developer needs enough verified understanding of the **relevant system boundaries, flows, dependencies, conventions, and risks** to make safe decisions.

A useful mental model:

```text
Business Requirement
        |
        v
+-------------------+
|  DISCOVERY        |
|  Repository       |
|  Architecture     |
|  Flows            |
|  Dependencies     |
+-------------------+
        |
        v
+-------------------+
|  REQUIREMENT      |
|  ANALYSIS         |
|  Gaps             |
|  Ambiguities      |
|  Contradictions   |
|  Assumptions      |
+-------------------+
        |
        v
+-------------------+
|  CLARIFICATION    |
|  Questions        |
|  Decisions        |
|  Scope            |
+-------------------+
        |
        v
+-------------------+
|  DESIGN / PLAN    |
|  Files            |
|  APIs             |
|  Events           |
|  DB                |
|  Tests             |
+-------------------+
        |
        v
+-------------------+
|  IMPLEMENTATION   |
|  Small increments |
|  Tests             |
+-------------------+
        |
        v
+-------------------+
|  VERIFICATION     |
|  Diff              |
|  Tests             |
|  Edge cases        |
|  Security          |
|  Compatibility     |
+-------------------+
```

---

# 1. FIRST: Do Not Promise a Delivery Date Before Discovery

## The situation

You are a new developer.

You join a company with:

- several large repositories
- unfamiliar architecture
- multiple microservices
- undocumented conventions
- external dependencies
- event-driven flows
- legacy code
- incomplete tests
- an incomplete requirement

A project manager says:

> "This is urgent. Can you finish it in one week?"

The dangerous response is:

> "Yes, I'll try."

The equally unhelpful response is:

> "I don't know."

The professional response is:

> **"I can commit to completing discovery and producing an evidence-based estimate first. I don't want to commit to a one-week implementation before I understand the affected services, dependencies, integration points, and requirement gaps."**

### The principle

**Commit to the next measurable step before committing to the final date.**

For example:

```text
Day 1–2:
Repository discovery + requirement analysis

Day 2–3:
Architecture/design + dependency analysis

Day 3:
Clarify open questions

Day 3–4:
Detailed implementation estimate

Then:
Implementation + testing + integration
```

Do not turn an arbitrary deadline into a technical commitment.

---

# 2. How to Push Back Professionally

## What NOT to say

Avoid:

> "One week is impossible."

unless you already have sufficient evidence.

Avoid:

> "I'm new, so I can't estimate."

Instead, explain **what is unknown**.

## Better response

```text
"I understand the one-week target and I'll prioritize the work.

However, I have not yet validated the full implementation scope. This touches
an unfamiliar set of services, and the requirement appears to leave some
behavior unspecified.

Rather than commit to an arbitrary date, I propose that I spend the first
1–2 days mapping the affected flows, dependencies, and implementation scope.
I can then provide a breakdown of:
- what can be completed within one week,
- what is blocked by clarification/dependencies,
- and the realistic completion range for the full scope.

If the one-week date is fixed, we can also discuss reducing scope or
accepting specific risks."
```

This changes the conversation from:

```text
Developer vs PM
```

to:

```text
Scope + Evidence + Constraints + Trade-offs
```

---

# 3. Use the Four-Way Delivery Conversation

When pressured for a date, explicitly surface:

```text
        SCOPE
          |
          |
TIME -----+----- QUALITY
          |
          |
      RESOURCES
```

Usually you cannot independently maximize all four.

If the date is fixed:

> "What scope can we remove?"

If the scope is fixed:

> "Can we move the date?"

If both are fixed:

> "Can we add engineers or accept defined risks?"

If quality is non-negotiable:

> "Then the estimate needs to reflect testing, integration, migration, and rollout."

## Useful phrase

> "If one week is a hard deadline, let's explicitly agree which scope or validation steps can be reduced. I don't want an unspoken trade-off to become a production risk."

---

# 4. Separate Estimate From Commitment

A preliminary estimate is not a commitment.

Use three levels:

### Level 1 — Rough range

```text
"I currently see this as approximately 3–6 weeks,
but this is before repository discovery."
```

### Level 2 — Evidence-based estimate

```text
"After tracing the affected flows and identifying
four services plus two external dependencies,
the implementation is approximately 15–20 working days."
```

### Level 3 — Commitment

Only commit when:

- scope is understood
- dependencies are identified
- major unknowns are resolved
- acceptance criteria are clear
- implementation plan exists
- test/rollout work is included

---

# 5. Your AI-Assisted Workflow

Use this lifecycle for large tasks:

```text
PHASE 0  → Delivery conversation
PHASE 1  → Repository reconnaissance
PHASE 2  → Flow discovery
PHASE 3  → Requirement analysis
PHASE 4  → Gap / ambiguity analysis
PHASE 5  → Cross-repository impact analysis
PHASE 6  → Architecture / implementation plan
PHASE 7  → Clarification with humans
PHASE 8  → Incremental implementation
PHASE 9  → Automated verification
PHASE 10 → AI review + human review
PHASE 11 → PR / rollout
PHASE 12 → Update documentation / knowledge
```

---

# 6. PHASE 1 — Repository Reconnaissance

## Objective

Understand:

- repository purpose
- major modules
- entry points
- service boundaries
- data stores
- messaging
- external integrations
- build system
- deployment
- tests
- configuration
- documentation

## First AI prompt

```text
You are helping me understand an unfamiliar production repository.

Do NOT modify any files.

Explore the repository and produce a repository map.

I want:
1. What this repository appears to do
2. Major modules/packages
3. Main application entry points
4. Important APIs
5. Important domain objects
6. Databases/data stores
7. Messaging/event systems
8. External service integrations
9. Configuration and deployment structure
10. Test structure
11. Important architectural patterns
12. Areas that appear legacy or unusual

For every important conclusion:
- cite the relevant file/path
- distinguish verified facts from inference
- do not invent missing information

At the end, list what you still do NOT understand.
Do not make code changes.
```

## Important rule

Always tell the AI:

> **"Do not invent. Distinguish verified facts from inference."**

---

# 7. PHASE 2 — Find the Golden Paths

Do not ask AI to understand millions of lines at once.

Pick representative flows.

Examples:

```text
Create customer
Update customer
Search customer
Process customer event
Generate report
```

Prompt:

```text
Find 3–5 representative end-to-end business flows in this repository.

For each flow, trace:

API/event entry point
→ controller/consumer
→ service
→ domain logic
→ repository/data access
→ database/external system
→ response/event

Give me:
- exact file paths
- important classes/methods
- important interfaces
- data transformations
- transactions
- asynchronous boundaries
- error handling
- external dependencies

Do not modify code.

Mark anything that is uncertain.
```

This gives you **working knowledge**, not a generic architecture lecture.

---

# 8. PHASE 3 — Learn the Repository's Conventions

Before implementing new code, ask AI to identify existing patterns.

```text
Analyze existing implementations and identify the repository's conventions for:

- REST controllers
- request/response DTOs
- service classes
- domain logic
- repositories
- exception handling
- validation
- transactions
- logging
- metrics
- caching
- messaging
- database migrations
- configuration
- security
- unit tests
- integration tests

For each convention:
1. State the observed pattern.
2. Give 2–3 representative file examples.
3. Explain whether it appears consistent or inconsistent.
4. Do not recommend a new architecture yet.

The goal is to understand how THIS repository is built.
```

This prevents AI from applying generic textbook architecture that doesn't match the company codebase.

---

# 9. PHASE 4 — Understand the Requirement

Now give the MD/spec.

Do NOT immediately ask:

> "Implement this."

First ask:

```text
Analyze this requirement against the repository.

Do not modify code.

Produce:

1. Requirement summary
2. Explicit acceptance criteria
3. Requirements implied by the document
4. Affected business flows
5. Likely affected components
6. Relevant repositories
7. APIs involved
8. Events/messages involved
9. Database changes
10. External integrations
11. Backward compatibility concerns
12. Security concerns
13. Performance concerns
14. Testing implications
15. Deployment/rollout implications

Most importantly:
Separate VERIFIED FROM CODE, VERIFIED FROM REQUIREMENT,
INFERENCE, and UNKNOWN.

Do not silently resolve ambiguities.
```

---

# 10. Requirement Maturity Check

Treat requirements as potentially incomplete.

Classify each requirement:

| Category | Meaning |
|---|---|
| Explicit | Clearly stated |
| Implied | Strongly suggested by context |
| Ambiguous | Multiple interpretations possible |
| Missing | Necessary information absent |
| Contradictory | Conflicts with code or another requirement |
| Assumption | Developer/AI would have to guess |

Example:

```text
Requirement:
"Customer status should be synchronized."

AI analysis:

Explicit:
Status must be synchronized.

Unknown:
- Which system is the source of truth?
- Synchronous or asynchronous?
- Which statuses?
- What happens on failure?
- Is ordering guaranteed?
- How are existing customers migrated?
- What happens to consumers using the old event schema?
```

This is where AI can turn a half-cooked requirement into a **question list**.

---

# 11. PHASE 5 — Cross-Repository Impact Analysis

For large systems, one repository is rarely enough.

Build an impact graph:

```text
Requirement
    |
    +--> customer-service
    |
    +--> customer-api
    |
    +--> event-contracts
    |
    +--> notification-service
    |
    +--> analytics-service
    |
    +--> deployment/configuration
```

Prompt:

```text
Based on the requirement and the repository analysis,
identify every repository that may be affected.

For each repository:
- explain why it may be affected
- identify the relevant API/event/data dependency
- identify the exact code paths if available
- identify whether the dependency is confirmed or inferred
- identify what must be verified in the other repository

Do not assume that a repository is affected merely because its name sounds relevant.
Use code/configuration/API/event evidence.
```

---

# 12. Build a Dependency Matrix

Maintain something like:

| Repository | Component | Dependency | Evidence | Status |
|---|---|---|---|---|
| customer-service | CustomerService | customer-db | code | Confirmed |
| customer-service | EventPublisher | Kafka topic X | code | Confirmed |
| notification-service | Consumer | topic X | code | Confirmed |
| analytics-service | Pipeline | customer event | inferred | Verify |
| external CRM | API | customer update | requirement | Verify |

This becomes extremely useful during estimation.

---

# 13. PHASE 6 — Separate Facts, Assumptions, Questions

Create three explicit lists.

## Facts

Things verified from code or authoritative documentation.

```text
CustomerService publishes CustomerUpdatedEvent.
```

## Assumptions

Things currently believed but not verified.

```text
Analytics may consume CustomerUpdatedEvent.
```

## Questions

Things that require human/product/domain clarification.

```text
Should historical customers be migrated?
```

This simple separation prevents AI from converting guesses into facts.

---

# 14. PHASE 7 — Ask AI to Challenge the Design

Once you have a proposed approach:

```text
Here is the current requirement analysis and proposed approach.

Act as a skeptical senior engineer.

Do not rewrite the solution yet.

Look for:
- missing requirements
- incorrect assumptions
- race conditions
- backward compatibility issues
- data consistency issues
- transaction boundaries
- event ordering problems
- duplicate processing
- failure/retry behavior
- security issues
- performance bottlenecks
- migration concerns
- observability gaps
- testing gaps
- deployment/rollback risks

For every concern:
- explain why it matters
- identify evidence from the repository
- classify severity
- state what information would resolve it

Do not invent facts.
```

AI is particularly useful as a **second pair of eyes** here.

---

# 15. PHASE 8 — Create the Implementation Plan

Only now ask for the implementation plan.

```text
Create a detailed implementation plan based ONLY on:
1. Verified repository behavior
2. Confirmed requirements
3. Explicitly approved assumptions

For each change include:
- repository
- file/path
- class/function
- purpose
- dependency
- test required
- migration required
- rollout concern

Order the work according to dependencies.

Do not modify files.
```

A useful output:

```text
1. Event contract
2. Producer
3. Consumer
4. Domain logic
5. API
6. Database migration
7. Tests
8. Integration tests
9. Deployment configuration
10. Observability
```

---

# 16. Estimate From the Plan

Now estimation becomes meaningful.

Estimate by workstream:

```text
Discovery                 2–3 days
Requirement clarification 1–2 days
Design                     2 days
Service A                  3 days
Service B                  4 days
Event contract             1 day
Database migration         2 days
Integration testing        3 days
Regression testing         3 days
Deployment/validation      2 days
Contingency                20%
```

Do not estimate only coding time.

Large production changes commonly include:

```text
Coding
+
Testing
+
Integration
+
Migration
+
Review
+
Deployment
+
Monitoring
+
Rollback preparation
```

---

# 17. Handling the "One Week" Pressure

After discovery, you may discover:

```text
Actual scope:

4 repositories
7 services
2 APIs
3 Kafka topics
1 database migration
2 external integrations
unknown consumer compatibility
incomplete tests
```

Now you have evidence.

Tell the PM/lead:

```text
"I've completed the initial impact analysis.

The work is larger than a single-service change. It affects four repositories,
seven services, two APIs, three event flows, and a database migration.

There are also two unresolved product/architecture questions and one
consumer-compatibility risk.

Based on the current scope, one week does not represent a credible
full-delivery estimate.

I see three options:

A. Keep the one-week target and reduce scope to [specific subset].

B. Keep the full scope and allow approximately [range] working days.

C. Keep the scope/date and add the required engineering capacity,
while explicitly accepting the identified risks.

I can proceed immediately with the highest-priority work while we resolve
the remaining questions."
```

Notice what this does:

**You are not saying "No."**

You are saying:

> **"Here is the evidence. Here are the options. Please choose the trade-off."**

---

# 18. Never Let "Urgent" Remove Discovery

Urgency should change priorities.

It should not eliminate engineering reasoning.

Bad:

```text
Urgent
 ↓
Skip discovery
 ↓
Generate code
 ↓
Production incident
```

Better:

```text
Urgent
 ↓
Focused discovery
 ↓
Identify critical path
 ↓
Reduce scope if necessary
 ↓
Implement
 ↓
Verify
 ↓
Release
```

---

# 19. PHASE 9 — Implementation With AI

Once the plan is approved, work in small increments.

Bad:

```text
"Implement the entire feature."
```

Better:

```text
"Implement step 1 from the approved plan.

Before modifying:
- inspect the existing implementation
- identify the exact files
- state the intended change briefly

Then make only this change.

Afterward:
- show the diff
- run the relevant tests
- report failures
- do not modify unrelated code.
```

Then continue step by step.

---

# 20. Keep the AI Inside the Approved Scope

Use:

```text
Do not:
- refactor unrelated code
- rename unrelated classes
- upgrade dependencies unless required
- change public contracts unless specified
- alter configuration unrelated to this task
- "clean up" nearby code
- change formatting across the repository
```

This dramatically reduces accidental scope expansion.

---

# 21. Ask AI to Explain Before Changing

For risky changes:

```text
Before modifying the code:
1. Explain the current behavior.
2. Explain why it needs to change.
3. Identify the smallest safe change.
4. Identify affected callers/consumers.
5. Identify tests that should protect the behavior.

Wait for my approval before modifying.
```

This is especially useful for unfamiliar code.

---

# 22. The AI Back-and-Forth Pattern

Real AI-assisted development should look more like this:

### Developer

```text
Here is the requirement.
Do not code.
First determine what we need to understand.
```

### AI

```text
I found three potentially affected services.
The main flow is A → B → C.

I am uncertain whether D consumes the same event.
```

### Developer

```text
Good. Investigate D.
```

### AI

```text
D does consume the event, but only in production configuration.
Here is the evidence.
```

### Developer

```text
Now trace the event contract across all consumers.
```

### AI

```text
There are four consumers.
Two appear compatible.
One deserializes strictly and may break.
```

### Developer

```text
Don't implement yet.
Create the list of questions we need answered.
```

### AI

```text
1. Is backward compatibility required?
2. Should old consumers receive the new field?
3. What is the source of truth?
...
```

### Developer

```text
Answers:
1. Yes.
2. Yes.
3. Customer-service.
Now update the implementation plan.
```

### AI

```text
Updated plan:
1. Add optional event field.
2. Update producer.
3. Update consumer...
```

### Developer

```text
Implement step 1 only.
```

This is **collaboration**, not one-shot prompting.

---

# 23. Use an AI "Working Memory" Document

For large tasks, maintain a file such as:

```text
docs/ai-task-context.md
```

or:

```text
.task/
    requirement.md
    repository-map.md
    flow-analysis.md
    impact-analysis.md
    decisions.md
    implementation-plan.md
    verification.md
```

Suggested `decisions.md`:

```markdown
# Decisions

## D001 — Source of truth
Customer-service is the source of truth for customer status.

Evidence:
- CustomerService.java
- architecture.md

Decision:
Approved by tech lead on YYYY-MM-DD.

## D002 — Event compatibility
The new event field must be optional for existing consumers.

Reason:
Two older consumers use strict deserialization.
```

This prevents repeated rediscovery.

---

# 24. Context Pack for an AI Agent

For repeated work, create a compact context pack:

```text
PROJECT.md
ARCHITECTURE.md
CONVENTIONS.md
SERVICES.md
EVENTS.md
DECISIONS.md
REQUIREMENT.md
IMPLEMENTATION_PLAN.md
```

The purpose is not to dump the entire repository into the model.

The purpose is to provide **high-value, stable context**.

---

# 25. What to Give the AI vs What to Keep in Your Head

AI should help with:

```text
Repository search
Flow tracing
Dependency discovery
Pattern detection
Code generation
Test generation
Diff review
Documentation
Alternative designs
Edge-case discovery
```

Developer should retain responsibility for:

```text
Business intent
Product decisions
Architecture ownership
Risk acceptance
Security decisions
Trade-offs
Final scope
Final code approval
Production responsibility
```

AI can inform your decision.

It should not silently make business decisions for you.

---

# 26. Verification Loop

After implementation:

```text
Implementation
      ↓
Compile
      ↓
Unit tests
      ↓
Integration tests
      ↓
Static analysis
      ↓
Git diff review
      ↓
Requirement review
      ↓
Edge-case review
      ↓
Security review
      ↓
Deployment review
```

Useful prompt:

```text
Review the current git diff against the original requirement.

Do not change anything.

Check:
1. Every requirement is implemented.
2. No requirement is implemented incorrectly.
3. No unrelated code was changed.
4. Existing behavior is preserved where required.
5. Tests cover the changed behavior.
6. Error handling is adequate.
7. Logging/metrics are adequate.
8. Backward compatibility is preserved.
9. Security implications are addressed.
10. Rollback is possible.

Return:
- confirmed items
- concerns
- missing tests
- unresolved questions
```

---

# 27. Ask AI to Try to Break the Implementation

Use a dedicated adversarial pass:

```text
Assume this implementation will fail in production.

Try to find how.

Focus on:
- concurrency
- retries
- duplicate events
- partial failures
- timeouts
- stale cache
- database transaction boundaries
- null/empty inputs
- large payloads
- malformed data
- backward compatibility
- deployment ordering
- rollback
- permissions
- observability

For every issue, explain the failure scenario and whether
the repository currently protects against it.
```

---

# 28. AI Review Is Not Human Review

Use several review passes:

```text
AI review
    +
Automated tests
    +
Static analysis
    +
Human code review
    +
Integration validation
```

AI can miss:

- business context
- undocumented tribal knowledge
- organizational constraints
- operational realities
- intentional historical decisions

Therefore:

> **AI review increases review coverage; it does not eliminate human ownership.**

---

# 29. Useful Prompt Library

## Repository overview

```text
Map this repository without changing anything.
Identify architecture, modules, entry points, dependencies,
data stores, messaging, external integrations and tests.
Cite evidence using file paths.
Separate facts from inference.
```

## Flow tracing

```text
Trace [BUSINESS FLOW] end-to-end.
Start at the entry point and follow the execution/data flow
through services, repositories, events and external systems.
Include exact file paths and methods.
```

## Find existing pattern

```text
Find 3 existing implementations similar to [FEATURE].
Compare them and identify the repository's established pattern.
Do not invent a new pattern.
```

## Requirement analysis

```text
Analyze this requirement against the existing codebase.
Do not implement.
Identify affected components, gaps, contradictions,
ambiguities, assumptions and missing acceptance criteria.
```

## Cross-repository analysis

```text
Identify repositories outside this one that may be affected.
Use actual API, event, schema, configuration or dependency evidence.
Clearly mark inferred dependencies.
```

## Clarification questions

```text
Generate the smallest set of questions that must be answered
before implementation can be considered safe.
Prioritize questions that can materially change scope, design,
timeline or production risk.
```

## Design review

```text
Act as a skeptical senior engineer.
Review this design for correctness, compatibility,
failure handling, performance, security and operational risk.
Do not rewrite it yet.
```

## Implementation

```text
Implement only [STEP].
Follow existing repository conventions.
Do not refactor unrelated code.
Run relevant tests.
Show the diff and summarize changed behavior.
```

## Test generation

```text
Analyze the changed behavior and identify missing tests.
Include happy path, validation, edge cases, failures,
backward compatibility and integration behavior.
```

## Diff review

```text
Review this git diff.
Identify unintended changes, behavior changes,
missing tests, potential regressions and violations
of repository conventions.
Do not modify anything.
```

## Production-readiness review

```text
Review this change as if it is going to production tomorrow.
Check observability, failure handling, retries, timeouts,
security, migrations, compatibility, rollout and rollback.
```

---

# 30. A Complete Real-World Simulation

## Scenario

You are a new developer at a company.

There are:

```text
customer-service
customer-api
customer-events
notification-service
analytics-service
admin-portal
```

The PM gives you:

```markdown
# Customer Status Enhancement

We need to support customer status updates.

When a customer status changes, the system should reflect the
new status everywhere.

This should be completed within one week.
```

At first glance:

> "Sounds like a small change."

Do NOT start coding.

---

## Conversation 1 — PM

### PM

> Can you finish this by Friday?

### Developer

> I can prioritize it, but I don't want to commit to Friday before I understand the affected services and event flows. I'll first map the implementation scope and identify any requirement gaps. I'll provide an evidence-based estimate and, if Friday is fixed, a concrete scope-cut option.

This is the first protection against a false commitment.

---

# 31. AI Discovery

### Developer → AI

```text
I am new to this organization and need to implement the attached
Customer Status Enhancement requirement.

Do not modify code.

First, map the repositories and identify the existing customer-status flow.

Start with:
1. source of truth
2. write path
3. read paths
4. events
5. consumers
6. database
7. caches
8. external integrations

Do not assume "everywhere" means every repository.
Find actual dependencies.
Separate verified facts from inference.
```

### AI discovers

```text
customer-service owns Customer.status.

customer-api reads customer-service.

customer-events publishes CustomerUpdatedEvent.

notification-service consumes CustomerUpdatedEvent.

analytics-service consumes CustomerUpdatedEvent.

admin-portal calls customer-api.

There is also a Redis cache.

Potential issue:
notification-service uses an older event schema.
```

Now the task has changed.

The original requirement sounded like:

```text
Change one field.
```

The actual system appears to be:

```text
Customer DB
   ↓
customer-service
   ↓
Event
 ├── notification-service
 └── analytics-service

customer-api
   ↓
admin portal

Redis
   ↑
customer-service
```

---

# 32. AI Requirement Challenge

### Developer → AI

```text
Now analyze the requirement against what you found.

Specifically investigate what "reflect the new status everywhere" could mean.

List:
- verified behavior
- ambiguous behavior
- missing requirements
- compatibility risks
- migration requirements
- questions for product/architecture

Do not code.
```

### AI

```text
Ambiguities:

1. Is customer-service the source of truth? Verified: yes.

2. Does "everywhere" include analytics historical data?
Unknown.

3. Should notification-service send a notification for every
status change? Unknown.

4. Should Redis be invalidated immediately? Existing code suggests yes.

5. Is event schema backward compatibility required?
Important because one consumer uses strict deserialization.

6. Should existing customers be migrated?
Unknown.

7. Are status transitions unrestricted?
Unknown.
```

This is exactly what you want.

---

# 33. Human Clarification

Send questions to the product owner:

```text
1. Which status transitions are valid?
2. Should historical customer records be migrated?
3. Should every status change generate a notification?
4. Does "everywhere" include analytics historical data?
5. Must existing event consumers remain backward compatible?
6. What is the expected behavior if downstream consumers are unavailable?
```

Product answers:

```text
1. Only ACTIVE → SUSPENDED and SUSPENDED → ACTIVE.
2. No historical migration.
3. Yes.
4. No.
5. Yes.
6. Event retry is acceptable; API update should still succeed.
```

Now the requirement is significantly more mature.

---

# 34. Updated Plan

Ask AI:

```text
Update the implementation plan using ONLY the clarified answers
and verified repository behavior.

Separate confirmed scope from anything still unknown.
```

AI produces:

```text
1. Add validated status transition logic.
2. Update customer-service API.
3. Invalidate Redis after successful persistence.
4. Extend CustomerUpdatedEvent compatibly.
5. Update notification-service.
6. Verify analytics compatibility.
7. Add unit tests.
8. Add integration tests.
9. Add event compatibility tests.
10. Validate retry behavior.
```

Now estimation is meaningful.

---

# 35. New Delivery Conversation

You can now tell the PM:

```text
"I completed the initial impact analysis.

This is not a single-service change. It affects customer-service,
the API layer, event contracts, notification-service, Redis behavior,
and integration tests. Historical analytics migration is explicitly
out of scope, which reduces the effort.

I estimate the full implementation at approximately 2–3 weeks,
including integration and regression testing.

If Friday is mandatory, I can propose a first milestone by Friday:
- status transition support
- API changes
- unit tests
- event contract changes

The downstream integration and full regression would follow.

That gives us a usable milestone without pretending the entire
cross-service change can safely be completed in one week."
```

This is a **credible engineering conversation**.

---

# 36. What AI Changed Here

Without AI:

```text
Requirement
 ↓
Developer explores manually
 ↓
Reads hundreds of files
 ↓
Misses one consumer
 ↓
Starts coding
 ↓
Integration issue appears later
```

With disciplined AI usage:

```text
Requirement
 ↓
AI repository discovery
 ↓
Flow tracing
 ↓
Cross-repository impact
 ↓
Requirement ambiguity detection
 ↓
Human clarification
 ↓
Implementation plan
 ↓
Evidence-based estimate
 ↓
Incremental implementation
 ↓
Automated verification
 ↓
AI review
 ↓
Human review
```

AI didn't replace engineering judgment.

It **accelerated the discovery and reasoning loop**.

---

# 37. What NOT to Do

## Anti-pattern 1

```text
"Here is the requirement. Implement it."
```

### Problem

AI has insufficient context.

---

## Anti-pattern 2

```text
"Read the entire repository and understand everything."
```

### Problem

Too much context, poor signal-to-noise ratio.

---

## Anti-pattern 3

```text
"Use the best architecture."
```

### Problem

"Best" generic architecture may conflict with the organization's existing system.

---

## Anti-pattern 4

```text
"Fix everything related to this code."
```

### Problem

Scope explosion.

---

## Anti-pattern 5

```text
"The AI said this is a 3-day task."
```

### Problem

AI cannot know undocumented dependencies, organizational constraints,
review queues, deployment windows, or business decisions unless you provide that information.

---

# 38. The Golden Rules

### Rule 1

**Discovery before implementation.**

### Rule 2

**Repository conventions before generic best practices.**

### Rule 3

**Facts ≠ assumptions.**

### Rule 4

**Requirements are inputs to validate, not unquestionable truth.**

### Rule 5

**Cross-repository dependencies must be explicitly investigated.**

### Rule 6

**Never promise a deadline before understanding scope.**

### Rule 7

**If the deadline is fixed, negotiate scope/resources/risk explicitly.**

### Rule 8

**Implement incrementally.**

### Rule 9

**Make tests part of the implementation, not an afterthought.**

### Rule 10

**Use AI to challenge your thinking, not merely confirm it.**

### Rule 11

**Keep humans responsible for business and architectural decisions.**

### Rule 12

**The developer remains accountable for what reaches production.**

---

# 39. Where Tools Such as Coding-Agent Slash Commands Fit

If you use a coding-agent framework, reusable workflows or slash commands, map them to the lifecycle rather than memorizing commands.

Conceptually:

```text
/discover
     ↓
/analyze
     ↓
/trace
     ↓
/questions
     ↓
/plan
     ↓
/implement
     ↓
/test
     ↓
/review
```

Different tools/frameworks may use different names.

The important thing is the **workflow**, not the command names.

If your environment has reusable skills such as:

- repository exploration
- planning
- test generation
- code review
- debugging
- documentation
- verification

use them as **gates in the workflow**.

Do not use an implementation command before the discovery and planning stages simply because the command exists.

---

# 40. The 15-Minute Emergency Version

Sometimes the PM says:

> "We need an answer now."

Use this compressed workflow:

```text
1. What does the requirement explicitly say?
2. What repositories are obviously involved?
3. What is the source of truth?
4. What is the main end-to-end flow?
5. What APIs/events/data stores are touched?
6. What is unknown?
7. What could materially change the estimate?
8. What can safely be delivered as a first milestone?
```

Then tell stakeholders:

```text
"I can give you a preliminary range based on the current evidence.
The estimate will become reliable after I verify the identified
dependencies and clarify the listed questions."
```

---

# 41. The Developer's Real-Time Checklist

Copy this into your task notes.

```markdown
# AI-Assisted Development Checklist

## Before promising a date
- [ ] Understand high-level requirement
- [ ] Identify affected repositories
- [ ] Identify major dependencies
- [ ] Identify unknowns
- [ ] Identify requirement ambiguities
- [ ] Do not commit based only on ticket size

## Repository discovery
- [ ] Repository map
- [ ] Architecture
- [ ] Entry points
- [ ] Golden paths
- [ ] Data stores
- [ ] Events
- [ ] External systems
- [ ] Existing patterns
- [ ] Testing strategy

## Requirement analysis
- [ ] Explicit requirements
- [ ] Acceptance criteria
- [ ] Ambiguities
- [ ] Missing requirements
- [ ] Contradictions
- [ ] Assumptions
- [ ] Open questions

## Cross-repository
- [ ] APIs
- [ ] Events
- [ ] Schemas/contracts
- [ ] Consumers
- [ ] Databases
- [ ] Configuration
- [ ] Deployment dependencies

## Planning
- [ ] Files/components
- [ ] Dependency order
- [ ] Database changes
- [ ] Migration
- [ ] Tests
- [ ] Rollout
- [ ] Rollback
- [ ] Observability

## Implementation
- [ ] Small increments
- [ ] No unrelated refactoring
- [ ] Existing conventions followed
- [ ] Tests after each logical step
- [ ] Review diff

## Verification
- [ ] Unit tests
- [ ] Integration tests
- [ ] Contract compatibility
- [ ] Edge cases
- [ ] Security
- [ ] Performance
- [ ] Failure/retry behavior
- [ ] Deployment
- [ ] Rollback

## Delivery
- [ ] Scope confirmed
- [ ] Estimate evidence-based
- [ ] Risks communicated
- [ ] Dependencies communicated
- [ ] Stakeholder expectations aligned
```

---

# 42. The One-Page Mental Model

When you're under pressure, remember:

```text
             DON'T START WITH CODE
                     |
                     v
              UNDERSTAND THE ASK
                     |
                     v
             MAP THE SYSTEM
                     |
                     v
              TRACE THE FLOW
                     |
                     v
          FIND CROSS-REPO IMPACT
                     |
                     v
          SEPARATE FACTS / GUESSES
                     |
                     v
           FIND REQUIREMENT GAPS
                     |
                     v
             ASK HUMANS
                     |
                     v
               MAKE A PLAN
                     |
                     v
              ESTIMATE WORK
                     |
                     v
           NEGOTIATE SCOPE/DATE
                     |
                     v
          IMPLEMENT IN INCREMENTS
                     |
                     v
                TEST
                     |
                     v
          ASK AI TO BREAK IT
                     |
                     v
              REVIEW DIFF
                     |
                     v
                 SHIP
```

---


---

# 43. Prompt Engineering, Context Engineering, and Agent Engineering

The workflow above already uses these ideas, but for effective use of modern coding agents such as **Codex, Claude Code, ChatGPT coding agents, or local agents**, it is useful to make them explicit.

The key insight is:

> **Do not try to make one giant prompt that contains everything. Build a system in which the agent can progressively discover, retrieve, reason about, modify, and verify the right context.**

Think of three layers:

```text
PROMPT ENGINEERING
        |
        v
How do I communicate the task clearly?
        |
        v
CONTEXT ENGINEERING
        |
        v
What information should the agent have?
        |
        v
AGENT ENGINEERING
        |
        v
What tools, workflow, checkpoints and verification
should the agent use to complete the task?
```

These three work together.

---

## 43.1 Prompt Engineering: Give the Agent a Clear Job

A good coding prompt should generally specify:

```text
ROLE / MODE
GOAL
CONTEXT
SCOPE
CONSTRAINTS
SUCCESS CRITERIA
PROCESS
VERIFICATION
OUTPUT FORMAT
```

### Example

Instead of:

```text
Implement customer status.
```

Use:

```text
You are working as a senior engineer in an existing production codebase.

Goal:
Implement the approved customer-status enhancement.

Context:
- customer-service owns customer status.
- CustomerUpdatedEvent is consumed by notification-service and analytics-service.
- The approved requirement is attached below.
- The implementation plan is already agreed.

Scope:
Only implement the approved status-change flow.

Constraints:
- Follow existing repository conventions.
- Do not refactor unrelated code.
- Preserve backward compatibility for existing event consumers.
- Do not change database schema unless the plan explicitly requires it.

Process:
1. Inspect the relevant existing implementation.
2. Confirm the files you intend to modify.
3. Implement one logical step at a time.
4. Run relevant tests after each step.
5. Inspect the final diff.

Success criteria:
- Approved acceptance criteria are satisfied.
- Existing behavior remains compatible.
- Relevant tests pass.

If you encounter an ambiguity that could change behavior or scope,
STOP and ask rather than guessing.
```

The important improvement is not verbosity.

It is **precision**.

---

# 43.2 Context Engineering: Give the Agent the Right Information

Context engineering is broader than prompting.

The question becomes:

> **What information does the agent need at this moment to make a correct decision?**

For a large software task:

```text
                 TASK
                  |
       +----------+----------+
       |          |          |
     Code       Docs      History
       |          |          |
       +----------+----------+
                  |
              Context
                  |
                  v
                 AI
```

Useful context includes:

```text
Repository structure
Relevant source files
Architecture documents
API contracts
Database schemas
Event schemas
Tests
Build output
Logs
Git history
Existing examples
Design decisions
Product requirements
Acceptance criteria
Previous decisions
```

But **more context is not automatically better**.

A better rule:

> **Give the agent the smallest high-value context that allows it to make the next correct decision.**

---

## 43.3 Use Progressive Context, Not Context Dumping

Bad:

```text
"Here are 500 files. Understand everything and implement this."
```

Better:

```text
Step 1 → Repository map
Step 2 → Relevant flow
Step 3 → Relevant components
Step 4 → Requirement
Step 5 → Cross-repository dependencies
Step 6 → Clarifications
Step 7 → Plan
Step 8 → Implementation
```

This is one of the most important patterns for large repositories.

---

# 43.4 Stable Context vs Task Context

Separate context into two categories.

### Stable context

Information that applies to many tasks:

```text
ARCHITECTURE.md
CONVENTIONS.md
SERVICES.md
EVENTS.md
CODING_STANDARDS.md
TESTING.md
DECISIONS.md
```

### Task-specific context

Information relevant only to the current change:

```text
REQUIREMENT.md
TASK_CONTEXT.md
IMPACT_ANALYSIS.md
IMPLEMENTATION_PLAN.md
OPEN_QUESTIONS.md
```

Conceptually:

```text
Stable project context
        +
Task context
        +
Relevant source
        +
Tool results
        =
Current agent context
```

This makes repeated AI sessions much more effective.

---

# 43.5 Context Hierarchy

Not all information deserves equal weight.

A useful hierarchy is:

```text
1. Explicit business requirement
2. Approved architectural decisions
3. Existing production behavior
4. API/event contracts
5. Tests
6. Repository conventions
7. Documentation
8. Historical implementation patterns
9. AI inference
10. AI assumption
```

When sources conflict, the agent should **surface the conflict**, not silently choose one.

Use this instruction:

```text
When requirement, documentation, tests and implementation disagree,
do not silently resolve the conflict.

Report:
- what each source says
- which source appears authoritative
- what decision is required
```

---

# 43.6 Evidence-Based Context

For repository work, ask the agent to cite evidence.

```text
For every architectural conclusion, provide:
- file path
- class/method
- relevant configuration/event/API
- confidence: confirmed / inferred / unknown
```

This turns:

```text
"Kafka is probably involved."
```

into:

```text
Confirmed:
CustomerUpdatedEvent is published by CustomerEventPublisher.java.

Confirmed:
notification-service subscribes to topic X.

Unknown:
Whether analytics-service consumes the same topic in production.
```

This is much safer.

---

# 43.7 Agent Engineering: Design the Workflow

An agent is not merely an LLM with a large prompt.

An effective coding agent needs:

```text
Model
+
Context
+
Tools
+
Workflow
+
State
+
Verification
+
Human checkpoints
```

For example:

```text
                AGENT
                  |
        +---------+---------+
        |         |         |
      Search    Edit      Execute
        |         |         |
      Files     Files     Tests
        |         |         |
        +---------+---------+
                  |
             Observe result
                  |
                  v
              Reason again
                  |
                  v
             Next action
```

---

# 43.8 Give Agents the Right Tools

For software development, useful tools include:

```text
read_file
search_code
find_references
list_directory
git_diff
git_log
run_tests
run_build
run_linter
run_static_analysis
database inspection
API inspection
documentation search
```

The agent should have **read-heavy access during discovery** and increasingly controlled write/execute access during implementation.

---

# 43.9 Use Human Approval Gates

For high-impact changes, don't let the agent jump directly from discovery to production changes.

Use gates:

```text
DISCOVERY
   |
   v
[HUMAN CHECK]
   |
   v
PLAN
   |
   v
[HUMAN CHECK]
   |
   v
IMPLEMENT
   |
   v
TEST
   |
   v
[HUMAN CHECK]
   |
   v
PR / DEPLOY
```

Human approval is particularly valuable before:

- database migrations
- public API changes
- event schema changes
- security changes
- infrastructure changes
- destructive operations
- broad refactors
- production deployment

---

# 43.10 Give the Agent Stop Conditions

This is a highly useful agent-engineering technique.

Tell the agent when it should **stop and ask** rather than continue.

Example:

```text
STOP and ask me if:
- business behavior is ambiguous
- two repositories disagree
- an API contract must change unexpectedly
- a database migration is required but not approved
- backward compatibility is unclear
- you discover a requirement that materially expands scope
- you need to make a product decision
- tests reveal behavior that contradicts the requirement
```

This prevents an agent from "solving" ambiguity by inventing behavior.

---

# 43.11 Give the Agent a Definition of Done

Do not rely on:

> "Done when the code compiles."

Define completion:

```text
Definition of Done:

- [ ] Requirement implemented
- [ ] Acceptance criteria satisfied
- [ ] Unit tests added
- [ ] Integration tests updated
- [ ] Existing tests pass
- [ ] Backward compatibility verified
- [ ] Error handling reviewed
- [ ] Observability reviewed
- [ ] Git diff reviewed
- [ ] No unrelated changes
- [ ] Documentation updated if required
```

The agent can then use this as a checklist.

---

# 43.12 Use a Plan → Execute → Verify Loop

A powerful general-purpose agent pattern:

```text
PLAN
  ↓
EXECUTE
  ↓
OBSERVE
  ↓
VERIFY
  ↓
CORRECT
  ↓
VERIFY AGAIN
```

Do not let:

```text
PLAN
 ↓
50 file changes
 ↓
"Done"
```

be the default for large tasks.

---

# 43.13 Ask for Plans Before Large Changes

For risky tasks:

```text
Do not modify files yet.

First produce:
1. Current behavior
2. Target behavior
3. Affected components
4. Proposed changes
5. Dependency order
6. Test strategy
7. Risks
8. Rollback considerations

Wait for approval.
```

For small, low-risk changes, you can reduce the ceremony.

The workflow should be proportional to risk.

---

# 43.14 Keep the Agent's State Externalized

Long conversations are not a substitute for durable project knowledge.

Maintain:

```text
.task/
    requirement.md
    repository-map.md
    flow-analysis.md
    impact-analysis.md
    open-questions.md
    decisions.md
    implementation-plan.md
    verification.md
```

This makes the work recoverable if:

- the conversation gets too long
- you switch models
- you switch agents
- another developer takes over
- the task pauses for two weeks

---

# 43.15 Avoid "Prompt Stuffing"

A common mistake is:

```text
Huge system prompt
+
Entire repository
+
Entire ticket history
+
Entire documentation
+
All previous conversations
```

This creates noise.

Instead:

```text
Current objective
      +
Relevant context
      +
Relevant tools
      +
Known decisions
      +
Explicit constraints
```

Think **context selection**, not context accumulation.

---

# 43.16 Use the Repository as a Source of Truth

For code behavior, prefer actual evidence:

```text
Code
Tests
Configuration
Contracts
Runtime behavior
```

over assumptions.

For business intent:

```text
Approved requirements
Product decisions
Architecture decisions
```

over AI inference.

A useful instruction:

```text
Never change behavior merely because you believe it is a better design.
First determine whether the requested behavior is intentional.
If uncertain, ask.
```

---

# 43.17 Agentic Coding: Don't Optimize for Maximum Autonomy

Maximum autonomy is not the same as maximum productivity.

A better objective is:

> **Maximum useful autonomy within clearly defined boundaries.**

For example:

### Low risk

```text
"Fix this typo and run the test."
```

Agent can act almost autonomously.

### Medium risk

```text
"Add validation to this existing API."
```

Agent can inspect → plan → implement → test → report.

### High risk

```text
"Change the event schema consumed by seven services."
```

Use:

```text
Discover
→ impact analysis
→ design
→ human approval
→ incremental implementation
→ compatibility testing
```

---

# 43.18 A Practical Agent Instruction Template

Use this as a reusable starting point:

```text
You are working in an existing production software repository.

OBJECTIVE
[What needs to be achieved]

AUTHORITATIVE CONTEXT
[Requirement / approved design / decisions]

RELEVANT REPOSITORIES
[List]

SCOPE
[What may change]

OUT OF SCOPE
[What must not change]

CONSTRAINTS
[Compatibility, security, performance, conventions]

WORKING RULES
1. Inspect before modifying.
2. Use existing repository patterns.
3. Do not invent undocumented behavior.
4. Separate facts, inference and unknowns.
5. Ask before making business decisions.
6. Avoid unrelated refactoring.
7. Make small, reviewable changes.
8. Run relevant verification after changes.
9. Inspect the final diff.
10. Report unresolved risks.

STOP CONDITIONS
[When the agent must ask instead of guessing]

DEFINITION OF DONE
[Concrete acceptance and verification criteria]
```

---

# 43.19 The Best Prompt Is Often a Sequence of Prompts

For complex work, don't search for a magical "master prompt."

Use a conversation like:

```text
1. Explore
2. Trace
3. Analyze
4. Challenge
5. Clarify
6. Plan
7. Implement
8. Test
9. Attack
10. Review
```

Each step produces better context for the next.

This is **progressive prompting + context engineering + agent orchestration**.

---

# 43.20 How This Applies to Codex / Claude Code / Cloud Agents

The underlying principles transfer across tools.

Whether the agent is:

```text
Codex
Claude Code
ChatGPT coding agent
Cloud-hosted agent
Local coding agent
```

the fundamental workflow remains:

```text
                 YOUR GOAL
                    |
                    v
              CONTEXT ENGINE
                    |
                    v
              LLM / REASONING
                    |
                    v
                  TOOLS
                    |
                    v
               CODE / TEST
                    |
                    v
                OBSERVE
                    |
                    v
               REASON AGAIN
                    |
                    v
              VERIFY / REVIEW
```

The cloud provider may manage:

- model hosting
- inference
- scaling
- GPU
- KV cache
- batching
- infrastructure

You still need to manage:

- context
- task decomposition
- constraints
- tools
- scope
- verification
- human decisions
- repository knowledge

Therefore, **these skills transfer even when the underlying model or platform changes.**

---

# 43.21 The "AI Operating Model" for a Developer

For every significant task, think in these five questions:

### 1. What does the AI need to know?

**Context engineering**

### 2. What exactly do I want it to accomplish?

**Prompt engineering**

### 3. What can it inspect/change/execute?

**Tool engineering**

### 4. When should it stop and ask me?

**Agent boundaries**

### 5. How do we know the result is correct?

**Verification engineering**

Together:

```text
Prompt
   +
Context
   +
Tools
   +
Boundaries
   +
Verification
   =
Reliable AI-assisted development
```

---

# 43.22 Final Upgrade to the Workflow

The complete workflow should therefore be:

```text
             BUSINESS REQUIREMENT
                     |
                     v
             DELIVERY DISCUSSION
                     |
                     v
              REPOSITORY DISCOVERY
                     |
                     v
                 FLOW TRACE
                     |
                     v
           CONTEXT ENGINEERING
                     |
                     v
          REQUIREMENT ANALYSIS
                     |
                     v
         FACT / ASSUMPTION / UNKNOWN
                     |
                     v
          CROSS-REPO IMPACT ANALYSIS
                     |
                     v
             HUMAN CLARIFICATION
                     |
                     v
            PROMPT / TASK DESIGN
                     |
                     v
             IMPLEMENTATION PLAN
                     |
                     v
             HUMAN APPROVAL GATE
                     |
                     v
            AGENTIC IMPLEMENTATION
                     |
                     v
              TEST / OBSERVE
                     |
                     v
              AGENT SELF-REVIEW
                     |
                     v
           ADVERSARIAL / EDGE REVIEW
                     |
                     v
               HUMAN REVIEW
                     |
                     v
                  SHIP
                     |
                     v
             DOCUMENT DECISIONS
```

This is the **complete AI-assisted development operating model**.

The objective is not to make the AI do everything.

The objective is to make the entire **developer + AI + repository + tools + verification system** more effective.


# 44. Final Principle

The future skill is not:

> **"I know how to prompt an LLM."**

It is:

> **"I know how to give an AI agent progressively better context, use it to investigate a complex software system, distinguish facts from assumptions, expose ambiguity, formulate and challenge an implementation plan, execute changes safely, and verify the result."**

For a senior developer, this is a force multiplier.

Your existing engineering knowledge remains the foundation:

```text
Java
Spring Boot
Microservices
Databases
Distributed systems
AWS
Testing
System design
Production experience
```

AI adds:

```text
Faster repository discovery
Faster code navigation
Faster implementation
Faster test creation
Broader review
Faster documentation
Faster debugging
```

The winning combination is:

```text
        DEVELOPER JUDGMENT
                +
          AI CAPABILITY
                +
        REPOSITORY CONTEXT
                +
        AUTOMATED VERIFICATION
                =
       AI-ASSISTED ENGINEERING
```

**Use AI aggressively for exploration and execution, but conservatively for assumptions and commitments.**
