# Effective Work Techniques

A personal working-style note -- mindset reminders and the techniques that create
impact at work -- plus a few practical snippets for generating test data with
Instancio.

## At a glance

| Section | What is in it |
| --- | --- |
| [Tools](#tools) | The dictation site used for drafting by voice |
| [Mindset and working style](#mindset-and-working-style) | Two reminders to reread on a bad day |
| [Very important techniques](#very-important-techniques) | The visible-work techniques, with what each one is for |
| [Dealing with rude people](#dealing-with-rude-people) | Why it is usually not about you |
| [Test data with Instancio](#test-data-with-instancio) | Dependency, settings snippet and the prompts that generate the classes |

## Tools

| Tool | Link | Used for |
| --- | --- | --- |
| Speechnotes | <https://speechnotes.co/dictate/> | Dictating long messages and notes instead of typing them |

Checked September 2026: the page loads and the dictation tool works.

## Mindset and working style

- **"You are wrong" == "I doubt it."** Someone saying you are wrong is stating
  their confidence level, not a fact about your work. Treat it as a question to
  answer with evidence, not a verdict to accept.
- **Blocked? Name the blocker, then route around it.** Write down exactly what is
  blocking you. If the rest of the task can move without it, do that first and
  come back to the blocker later.

## Very important techniques

The theme: **work that other people can see counts twice.** Real work still has
to happen, but on its own it is invisible.

| Technique | What it means in practice |
| --- | --- |
| Voice vibration work | Carry interest, zeal and enthusiasm in your *voice* on calls. Sounding engaged creates more impact than the same work delivered flatly -- and the reverse is not true: quiet excellent work does not automatically read as excellent |
| Messaging work | Say what you are doing, in writing, where people can see it |
| Communication work | More contact with the team; be a known quantity rather than a name on a ticket |
| More surface work, less deep work | Deliberately weight your day toward the visible parts |
| Pina pataram, lona lotaram | Telugu: "grand on the outside, hollow inside" -- the failure mode the line above shades into if surface work is *all* you do |
| Slow and steady on real work | The deep work still gets done, just without burning yourself against a deadline |

## Dealing with rude people

Some people are rude and mean because of their own insecurity, or because they
have already checked out of the work and the company. It is about their
situation, not about you. Do not take it personally and do not react to it.
Ignore it and stay on your own work.

## Test data with Instancio

Instancio generates fully populated objects for tests, so you do not hand-write a
`FullContactDto` with forty fields set to `"test"`.

### Dependency

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

Version 4.0.0 is what this note was written against. The current release on Maven
Central is 6.0.1 as of September 2026; check
<https://central.sonatype.com/artifact/org.instancio/instancio-core> before
copying the block into a new project.

### Keeping generated collections small

By default Instancio fills arrays and collections with several elements, which
makes assertion output noisy. Cap them:

```java
Settings settings = Settings.create()
        .set(Keys.ARRAY_MIN_LENGTH, 1)
        .set(Keys.ARRAY_MAX_LENGTH, 2)
        .set(Keys.COLLECTION_MIN_SIZE, 1)
        .set(Keys.COLLECTION_MAX_SIZE, 1);

FullContactDto dto = Instancio.of(FullContactDto.class)
        .withSettings(settings)
        .create();
```

**Correction to the earlier version of this note.** It called
`Instancio.setDefaultSettings(settings)`. That method does not appear in the
Instancio user guide or in the published javadoc, so the snippet above uses the
two documented routes instead:

| Scope | How |
| --- | --- |
| One object | `Instancio.of(X.class).withSettings(settings).create()` |
| Every object in the project | an `instancio.properties` file on the classpath |

This was checked against the current user guide and javadoc. It was **not**
checked against version 4.0.0 specifically, so if 4.0.0 did expose
`setDefaultSettings`, the old snippet may have compiled at the time.

### Prompts that go with this

| Step | Prompt or action |
| --- | --- |
| 1 | "Create an object and set dummy data for `FullContactDto` for a test case" |
| 2 | "Generate the entity class in Java, with the remarks column as the description" |
| 3 | Email-draft technique: paste code into a draft email on the phone, open the draft on the laptop, copy it out |
