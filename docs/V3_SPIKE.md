---
title: Commands v3 Spike (Blocked by Design)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Commands v3 spike — blocked by vendor mutual exclusion

## Scope

Records the v3 evaluation and why no in-tree spike code exists. Does **not**
retread the v3 tutorial content in WPILib docs — read those for API shape.

## Content

- **Attempted:** one auto (`drive-forward-2m`) as a v3 coroutine
  (`Command.noRequirements(c -> {...waitUntil...})`) against `DriveControl`,
  run under an isolated `Scheduler.createIndependentScheduler()` in a unit
  test. API verified via `javap` on `commandsv3-java` (coroutine
  fork/await/waitUntil, priorities, `Selectable`-style naming required).
- **Blocked:** GradleRIO alpha-7 refuses Commands v2 + v3 vendordeps in one
  project (`Conflicting Vendor Dependency ... Users can not have both`).
  Our `Interfaces/Subsystem` extends v2 `Subsystem`, so the spike cannot
  compile in-tree without first rewriting the subsystem contract — which is
  exactly the rewrite the spike was supposed to evaluate. Spike files were
  reverted, not shipped half-broken.
- **Verdict:** v3 stays opt-in-future. It is genuinely available (not
  mandatory — v2 still ships and our stack runs on it), but adopting it means
  rewriting `Interfaces/Subsystem` → v3 `Mechanism`, `MissionBase` threading
  → scheduler ownership, and trigger scopes. That decision waits for
  post-kickoff, when autos have real requirements to judge against. The
  migration doc's "do not mix both" rule stands.

## Verification

- Verified against: alpha-7 build failure text 2026-10-07 + `javap` API
  surface + WPILib v3 migration guide; tree reverted to green
  (`cleanTest test` 13 files / 32 tests).
- Next review due: post-kickoff (first real auto).

## Related

- `docs/2027_MIGRATION.md`, `docs/TEAM_COMPARISON.md`, `docs/TELEOP_AUTO.md`
