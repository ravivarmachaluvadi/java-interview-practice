# Local LLM

A model running entirely on this laptop, used to review practice solutions and
explain problems at **zero cloud cost**. Set up 16 Sep 2026.

## At a glance

| Key | Value |
| --- | --- |
| Engine | LM Studio 0.4.24 (`LM Studio.exe` reports `0.4.24.0`) |
| CLI | `lms`, at `~/.lmstudio/bin/lms` |
| Model | `openai/gpt-oss-20b` -- 12.11 GB on disk, mixture-of-experts |
| Endpoint | `http://127.0.0.1:1234/v1` (OpenAI-compatible) |
| Machine | Intel Core Ultra 7 255H, 31.4 GB RAM, Intel Arc 140T integrated GPU |
| Review command | `review-code`, in `~/bin` -- **not** in this repo's `tools/` |

Also installed: `text-embedding-nomic-embed-text-v1.5` (84 MB), an embedding
model, not a chat model.

## How current is this page

| Claim | Status |
| --- | --- |
| Engine version, model, CLI flags, hardware | **Re-verified Sep 2026** against the installed app and `lms --help` |
| `review-code` location and flags | **Re-verified** against `~/bin/review-code.py` |
| Tokens/sec and RAM figures | Measured once, 16 Sep 2026, on this machine. **Not re-measured.** Treat as the shape of the result, not a current benchmark |
| "278 files documented", "33/33 mains", "~60 min" | A one-off run in Sep 2026. No log survives, so these are **unverified** today |
| "642 files" in the `javac` story | Was the repo's size at commit `2be7401`. The repo now holds **578** `.java` files |

## Start it

```bash
lms server start                    # once per reboot
lms unload --all                    # ALWAYS first - see the traps below
lms load openai/gpt-oss-20b --gpu off -c 16384 --parallel 1 -y
```

Loading takes about 45 seconds.

## Check it

```bash
lms status                          # is the server up?   (= lms server status)
lms ps                              # which model is loaded, and with what settings
lms ls                              # which models are on disk
```

An idle loaded model uses **~0% CPU**, so seeing no CPU activity does *not* mean
it is unloaded. Check `lms ps`, not Task Manager.

## Stop it

Three separate things are running, and they stop independently. Pick the level
you need:

| Goal | Command | Frees |
| --- | --- | --- |
| Free the RAM, keep the server ready | `lms unload --all` | ~12 GB |
| Also shut the API down | `lms server stop` | a little |
| Quit entirely | Close LM Studio from the system tray | the app's own footprint |

```bash
lms unload --all                    # the one you normally want
lms server stop                     # only if you want port 1234 closed
```

**`lms unload --all` is the one that matters** -- the model is the 12 GB. The
server itself and the LM Studio app are small by comparison. Leaving the server
running costs almost nothing and saves you a step next time.

Stopping is optional: the model is loaded with no TTL, so it stays until you
unload it or reboot. If you would rather it release itself after idling, load it
with `--ttl 1800` (30 minutes) and it unloads automatically.

## Two traps that cost real time

**1. `lms load` does not replace -- it stacks.** Loading twice without unloading
gives you two 12 GB instances and takes the machine to under 1 GB free. Always
`lms unload --all` first.

**2. Load flags do not persist.** If the GUI auto-loads the model, you silently
get the GUI's own defaults rather than the flags above, which is slower and uses
far more RAM. Re-load with the command above.

## Why `--gpu off` -- counter-intuitive, and measured once

| Setting | Speed | RAM consumed |
| --- | --- | --- |
| `--gpu max` | 14.0 tok/s | 22.6 GB |
| **`--gpu off`** | **16.1 tok/s** | **12.2 GB** |

On that run, turning the GPU off was about 15% faster and used 10.4 GB less.
Those two rows are a single measurement session from 16 Sep 2026 and have not
been repeated.

The reasoning behind the result does not depend on the exact numbers. The Intel
Arc 140T is an *integrated* GPU, so it has no memory of its own -- it borrows
system RAM. `--gpu max` therefore keeps the model in RAM *and* in shared "VRAM":
two copies of a 12 GB model. It is also slower, because token generation is
limited by memory bandwidth, which the iGPU shares with the CPU, while adding
transfer overhead on top. The "VRAM" that LM Studio reports for this GPU is
system RAM relabelled.

## Everything else barely matters

| Setting | Effect on that run |
| --- | --- |
| Context 4K / 8K / 16K / 32K | 13.6 / 14.0 / 13.6 / 13.6 tok/s -- no effect on speed |
| `--parallel 4` vs `1` | No speed change; use 1 for a single user |
| Prompt 87 tokens vs 1776 | 13.6 vs 13.0 -- about 4% |
| `--speculative-draft-mtp` | Unsupported: needs a GGUF with a bundled MTP head |

Measured as the median of 3 runs, warmed up, identical prompt, on 16 Sep 2026.

**The second-biggest lever is machine load.** An earlier round measured 8-10
tok/s and wrongly blamed context length; those runs were competing with two other
jobs. Quiet machine ~16 tok/s, busy machine ~8. Close other work rather than
tuning flags.

## Using it with this repo

`review-code` is a personal script in `~/bin`, on `PATH`. It is **not** part of
this repository -- the repo's own tooling is listed further down.

```bash
review-code AAScratches/01-DSA/09-Trees-BST/C12_LCA.java   # review named files
review-code --diff                       # review uncommitted changes
review-code --diff HEAD~1                # review changes since a ref
review-code -                            # read code from stdin
review-code --check                      # verify server and model, then exit
lms chat                                 # ask it to explain a problem
```

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `LOCAL_LLM_API` | `http://127.0.0.1:1234/v1` | Where the OpenAI-compatible server is |
| `LOCAL_LLM_MODEL` | `openai/gpt-oss-20b` | Which loaded model to call |
| `REVIEW_MAX_CHARS` | `6000` | How much code is sent in one review |

`review-code` runs an explicit Java/Spring pitfall checklist -- `@Transactional`
self-invocation, unsafe singleton state, resource leaks, N+1 queries,
`equals`/`hashCode`, boundary conditions, null and `Optional` misuse, blocking
calls on reactive threads -- and asks for severity, the triggering input and a
concrete fix for each finding.

**One inconsistency to know about.** When the model is not loaded, `review-code`
prints a hint suggesting `lms load ... --gpu max -c 8192 --ttl 1800 -y`. That
hint predates the measurement above; prefer the `--gpu off -c 16384` line in
[Start it](#start-it). `REVIEW_MAX_CHARS` defaults to 6000 for an 8K context, so
with `-c 16384` you can raise it.

### The repo's own tooling is separate

None of these call the model; they are plain Python and `javac`.

| Tool | What it does |
| --- | --- |
| `tools/runjava` / `runjava.py` | Compiles and runs a single practice file whatever the class inside is called |
| `tools/verify_all.py` | Compiles and runs every file, and checks each printed value against its stated `expected` |
| `tools/check_headers.py` | Checks the header block in every practice file |
| `tools/check_references.py` | Checks cross-references between files and indexes |
| `tools/gen_readmes.py` | Regenerates the per-folder README indexes |

## What it is good and bad at

Given a Spring service with planted bugs, it caught the unsafe static
`SimpleDateFormat`, the unsafe `HashMap` in a singleton and a missing injection
-- but missed that `@Transactional` self-invocation bypasses the Spring proxy.
**With an explicit checklist in the prompt, that same bug became its top
finding.** That single exercise is why the checklist in `review-code` exists; the
transcript was not kept, so this is a recollection rather than a reproducible
result.

| Good at | Weak at |
| --- | --- |
| Bounded mechanical work -- describe this, demo that, rewrite this for that | Open judgment: *"is this subtly wrong?"* |
| Following a checklist you hand it | Knowing what to look for on its own |
| Text you supply: summarising, rewriting, quizzing | Anything where being confidently wrong looks identical to being right |

**Do not use it to learn a concept you do not already know.** It will state wrong
things about JVM internals or Kafka semantics with total confidence, and while
you are still learning the topic you cannot tell. Use it to *drill* material you
already have, never to *author* the explanation you will trust.

For correctness of code, prefer the compiler. On the 642-file baseline of this
repo (commit `2be7401`; it holds 578 `.java` files today), `javac` found a
genuine bug -- a self-referential lambda, which Java forbids -- in under two
minutes. A model review had waved the same code through.

## Track record on this repo

The model wrote problem descriptions and `main()` methods for 278 files here.

| Measure | Value |
| --- | --- |
| Files documented | 278 |
| Broke compilation | 1, so 99.6% clean |
| Generated `main()` calling only real methods | 33 of 33 |
| Time | ~60 min, zero cloud cost |

**These four numbers come from a run in September 2026 whose log was not kept, so
they cannot be re-derived from the repository today.** They are recorded here as
the owner's account of that session, not as a measurement you can reproduce.

The one failure was a self-referential lambda: plausible-looking, and illegal in
Java. That is the signature failure mode -- *plausible* rather than *correct*.
