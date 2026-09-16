# Local LLM

A model running entirely on this laptop, used to review practice solutions and explain
problems at **zero cloud cost**. Set up 16 Sep 2026.

| | |
|---|---|
| Engine | LM Studio 0.4.24 |
| Model | `openai/gpt-oss-20b` — 12.11 GB, mixture-of-experts |
| Endpoint | `http://127.0.0.1:1234/v1` (OpenAI-compatible) |
| Speed | **~16 tokens/sec** |
| RAM while loaded | ~12 GB of 31 GB |

## Start it

```bash
lms server start                    # once per reboot
lms unload --all                    # ALWAYS first - see the trap below
lms load openai/gpt-oss-20b --gpu off -c 16384 --parallel 1 -y
```

Loading takes about 45 seconds.

## Check it

```bash
lms status                          # is the server up?
lms ps                              # which model is loaded, and with what settings
```

An idle loaded model uses **~0% CPU**, so seeing no CPU activity does *not* mean it is
unloaded. Check `lms ps`, not Task Manager.

## Stop it

Three separate things are running, and they stop independently. Pick the level you need:

| Goal | Command | Frees |
|---|---|---|
| Free the RAM, keep the server ready | `lms unload --all` | **~12 GB** |
| Also shut the API down | `lms server stop` | a little |
| Quit entirely | Close LM Studio from the system tray | **~540 MB** |

```bash
lms unload --all                    # the one you normally want
lms server stop                     # only if you want port 1234 closed
```

**`lms unload --all` is the one that matters** — the model is the 12 GB. The server
itself and the LM Studio app are small by comparison. Leaving the server running costs
almost nothing and saves you a step next time.

Stopping is optional: the model is loaded with no TTL, so it stays until you unload it
or reboot. If you would rather it release itself after idling, load it with
`--ttl 1800` (30 minutes) and it unloads automatically.

## Two traps that cost real time

**1. `lms load` does not replace — it stacks.** Loading twice without unloading gives
you two 12 GB instances and takes the machine to 0.4 GB free. Always `lms unload --all`
first.

**2. Load flags do not persist.** If the GUI auto-loads the model, you silently get
`--gpu max -c 8192 --parallel 4`, which is slower and uses 22 GB. Re-load with the
command above.

## Why `--gpu off` — this is counter-intuitive and measured

| Setting | Speed | RAM consumed |
|---|---|---|
| `--gpu max` | 14.0 tok/s | **22.6 GB** |
| **`--gpu off`** | **16.1 tok/s** | **12.2 GB** |

**Turning the GPU off is 15% faster and uses 10.4 GB less.**

The Intel Arc 140T is an *integrated* GPU, so it has no memory of its own — it borrows
system RAM. `--gpu max` therefore keeps the model in RAM *and* in shared "VRAM": two
copies of a 12 GB model. It is also slower, because token generation is limited by
memory bandwidth, which the iGPU shares with the CPU, while adding transfer overhead on
top. The "17.9 GiB VRAM" that LM Studio reports is system RAM relabelled.

## Everything else barely matters

| Setting | Effect |
|---|---|
| Context 4K / 8K / 16K / 32K | 13.6 / 14.0 / 13.6 / 13.6 tok/s — no effect on speed |
| `--parallel 4` vs `1` | No speed change; use 1 for a single user |
| Prompt 87 tokens vs 1776 | 13.6 vs 13.0 — about 4% |
| `--speculative-draft-mtp` | Unsupported: needs a GGUF with a bundled MTP head |

Measured as the median of 3 runs, warmed up, identical prompt.

**The second-biggest lever is machine load.** An earlier round measured 8–10 tok/s and
wrongly blamed context length; those runs were competing with two other jobs. Quiet
machine ~16 tok/s, busy machine ~8. Close other work rather than tuning flags.

## Using it with this repo

```bash
review-code AAScratches/01-DSA/09-Trees-BST/C05_LowestCommonAncestor.java
review-code --diff                  # review uncommitted changes
lms chat                            # ask it to explain a problem
```

`review-code` runs an explicit Java/Spring pitfall checklist — `@Transactional`
self-invocation, unsafe singleton state, resource leaks, N+1 queries,
equals/hashCode, boundary conditions.

## What it is good and bad at — measured, not guessed

Given a Spring service with planted bugs, it caught the unsafe static
`SimpleDateFormat`, the unsafe `HashMap` in a singleton and a missing injection — but
missed that `@Transactional` self-invocation bypasses the Spring proxy. **With an
explicit checklist in the prompt, that same bug became its top finding.**

| Good at | Weak at |
|---|---|
| Bounded mechanical work — describe this, demo that, rewrite this for that | Open judgment: *"is this subtly wrong?"* |
| Following a checklist you hand it | Knowing what to look for on its own |
| Text you supply: summarising, rewriting, quizzing | Anything where being confidently wrong looks identical to being right |

**Do not use it to learn a concept you do not already know.** It will state wrong
things about JVM internals or Kafka semantics with total confidence, and while you are
still learning the topic you cannot tell. Use it to *drill* material you already have,
never to *author* the explanation you will trust.

For correctness of code, prefer the compiler. On the 642 files in this repo, `javac`
found a genuine bug — a self-referential lambda, which Java forbids — in under two
minutes. A model review had waved the same code through.

## Track record on this repo

The model wrote problem descriptions and `main()` methods for 278 files here.

| | |
|---|---|
| Files documented | 278 |
| Broke compilation | **1** → 99.6% clean |
| Generated `main()` calling only real methods | **33 / 33** |
| Time | ~60 min, zero cloud cost |

The one failure was a self-referential lambda: plausible-looking, and illegal in Java.
That is the signature failure mode — *plausible* rather than *correct*.
