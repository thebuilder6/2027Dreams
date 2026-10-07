---
title: Resource Coordination
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Resource Coordination

How concurrent agents (and agents racing a human) avoid corrupting each other's
builds, sims and deploys. The protocol every agent must follow is in
`AGENTS.md` under "Resource coordination"; this guide is the reasoning and the
recovery recipes. Ported from 2026Dreams; the `sweep` resource arrives with the
score rig.

## Scope

Covers the three shared resources below and the `tools/lock` module that guards
them. Does **not** cover: the score rig (lands later with its own guide), game
logic, or intra-task parallelism.

## Content

### The three resources

| Resource | Held during | Why it must be exclusive |
|---|---|---|
| `gradle-build` | `compileJava` / `test` | `build/test-results/*.xml` is clobbered by a concurrent run, and the green-suite baseline cited in `docs/CHANGELOG.md` is read out of those XML files. |
| `sim-gui` | `simulateJava` / SimGUI | Owns NT4 5810, WebServer 5800, CameraServer 1181-1182 for the whole run. Hardcoded constants with no environment override. |
| `deploy` | `gradlew deploy` | One controller target; last writer wins. |

### Using it

```powershell
powershell -File tools/lock/status.ps1        # who holds what, and any dashboard
powershell -File tools/lock/acquire.ps1 -Resource gradle-build -Reason "full suite"
powershell -File tools/lock/release.ps1 -Resource gradle-build
```

Exit codes: `0` acquired, `3` timed out, `1` bad usage. Wrap protected work in
`try` / `finally` and release in the `finally`.

### Waiting, and what "stale" means

A blocked acquire prints a line every 15 seconds, so the wait is visible rather
than a silent hang. On timeout it throws naming the holder, its PID, how long it
has held the lock, and its stated reason.

Stale locks (owner process gone) are reclaimed automatically and loudly
(`[lock] STALE`). Liveness is anchored on PID *plus* process start time, so a
recycled PID can't deadlock the lock. There is no time-based expiry: a long
sim hold is legitimate, and killing it on a timer would be the exact failure
this module prevents.

### Recovery recipes

- `extractReleaseNative` fails on undeletable `build/jni` DLLs → `status.ps1`,
  find the PID holding the file, kill **that PID only**, re-run. Never
  `taskkill /IM java.exe`, never `gradlew --stop`.
- A test failure appears only in the full suite but not with `--tests` on the
  same file → suspect a concurrent Gradle run on this tree; check `status.ps1`
  and re-run under the lock before chasing a regression. A daemon-locked
  one-off failure clears on a clean (`--rerun-tasks`) re-run — that re-run is
  the tiebreaker.
- Fan-out: an orchestrator holds `gradle-build` once for the whole batch;
  workers run under it. Concurrent `gradlew` runs are not a tuning problem,
  they invalidate the result.
- `status.ps1` also reports a running Elastic/AdvantageScope/SimGUI. Legal for
  a GUI sim, but it will contaminate a rig sweep when `sweep` lands — check
  before sweeping.
- A lock you don't hold blocks you → wait or work a different resource. Do not
  delete someone else's `.locks/*.lock` file; `Exit-Lock` already refuses to
  release locks it doesn't own.

### Advisory, not mandatory

The lock protects agent-against-agent. A human in a terminal or a teammate in
VS Code bypasses it silently. A timeout is a reason to wait, never a reason to
assume the path is clear.

## Verification

- Verified against: `tools/lock/status.ps1` all-free on 2026-10-07; module logic ported verbatim from 2026Dreams (battle-tested Sep–Oct 2026).
- Next review due: 2026-11-07, or when the `sweep` resource lands.

## Related

- `AGENTS.md` §Resource coordination (the protocol)
- `docs/INDEX.md` (map)
