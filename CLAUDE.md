# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is (one line)

Crucible is a **behavioral (BDD) test-automation framework**, published as a set of reusable Java libraries (Maven mono-repo, Java 17), plus a separate .NET results dashboard. It is not an application — consumers build their own *client framework* (`demo-framework/` is the reference) that depends on the Crucible artifacts and runs Cucumber.

## Documentation

Detailed docs live in `docs/`. Read the relevant one before working in that area rather than duplicating its content here:

- **`docs/architecture.md`** — what Crucible is, exact tech-stack versions, the 9-module layout + dependency graph, scenario-execution lifecycle (hooks, serial vs. parallel), where state lives, auth model, external integrations.
- **`docs/build-and-test.md`** — build/test/run/deploy commands (Maven wrapper, single-test invocation, `-DskipTests`, version bump), runtime parameters, the `demo-framework` and dashboard build flows, CI status.
- **`docs/data-model.md`** — the framework's "domain" (test-execution state + config): `MasterMind`/`CentralCommand` registries, page-object model, API/DB/user/report POJOs, and config/data file schemas.
- **`docs/conventions.md`** — code patterns to follow (static facades, thin step defs, tags-as-data, naming) and an explicit list of anti-patterns NOT to reproduce.
- **`docs/known-issues.md`** — honest inventory of bugs, fragility, concurrency hazards, security weaknesses, and mid-migration state, cited to files.
- **`docs/lessons-learned.md`** — running notes (currently empty).

## Quick start

```bash
./mvnw clean install -DskipTests   # fast local install of all reactor modules to ~/.m2
```

`demo-framework/` and `dashboard/` are **not** in the root reactor — build them separately (see `docs/build-and-test.md`). Crucible does **not** run through the standard IDE Cucumber runner; scenarios launch via a `RunCucumberTestBase` subclass. Details in `docs/architecture.md` and `docs/build-and-test.md`.

## Read these before doing any work
- /docs/architecture.md — system architecture
- /docs/conventions.md — coding patterns and anti-patterns
- /docs/data-model.md — domain model
- /docs/build-and-test.md — exact commands
- /docs/known-issues.md — known fragility
- /docs/lessons-learned.md — things you've gotten wrong before; do not repeat

## Commands
- Build: `[exact command]`
- Run all tests: `[exact command]`
- Run a single test: `[exact command]`
- Lint: `[exact command]`

## Hard rules — non-negotiable
1. Do not introduce new dependencies without asking first.
2. Every code change must include or update tests.
3. Match the existing code style. If unsure what the style is, search the codebase for similar code first. If the existing style does not match normal coding conventions, bring it to my attention first.
4. If you are uncertain about something, ask. Do not guess and do not invent APIs.
5. Do not mark a task complete until the Definition of Done is met (below).

## Definition of Done
A task is not done until ALL of these are true:
- Code compiles cleanly
- All existing tests still pass
- New tests written for the new behavior, and they pass
- No new lint errors
- Documentation updated if behavior changed
- You have re-read your own diff and are confident it does what was asked

## How to ask for help
If you get stuck, do not invent a solution. Stop, summarize what you've tried, and ask. I would rather answer one question than fix a wrong implementation.