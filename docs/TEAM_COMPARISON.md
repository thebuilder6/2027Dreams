---
title: Team Comparison (structure, not game code)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# Team implementations vs 2026Dreams lessons

Compare structure, not 2026 game answers. Researched 2026-10-07 against
`Mechanical-Advantage/RobotCode2026Public` ("Darwin", MIT) + `docs.advantagekit.org`.

## 6328 Darwin — verified from source

- `Robot.java`: `extends IdunRobot` (offboard Mac mini + roboRIO C++ dual deploy via `idun/`). Logger shape matches ours (REAL: WPILOG+NT, SIM: NT-only, REPLAY: reader) plus timing modes `SYNC/FIXED/FAST`, `IdunServer`, scheduler command logging, `RobotState` via `AutoLogOutputManager`, `FinanceDepartment` battery tracking.
- `Constants.java`: `RobotType DARWIN/ALPHABOT/SIMBOT`, mode derived from platform, `loopPeriodSecs = 0.005` (we stay at 0.02 until odometry needs more), `CheckDeploy`/`CheckPullRequest` guards.
- Subsystems are command-based `FullSubsystem` (`Drive.java:29`) with `periodic()` + `periodicAfterScheduler()`; no custom manager — `robotPeriodic` runs `VirtualSubsystem` → scheduler → after-scheduler hooks.
- IO is finer than ours was: `ModuleIO.java` has **both** `ModuleIOInputs` and `ModuleIOOutputs` (`applyOutputs`). `Drive.java:71-81` does the same `updateInputs` + `processInputs` two-step, then feeds a central `RobotState` odometry observer.
- Utilities adopted: `LoggedTracer` (per-phase ms), `CheckPullRequest` guard, outputs-struct IO. Deferred: `RobotState` singleton estimator (needs a drive), `FinanceDepartment` (needs current-draw owners), `LoggedTunableNumber` (needs the tuning UI pass), Idun offboard (SystemCore CM5 makes it unnecessary).

| Approach | Representative | Strengths | Costs | What 2027Dreams takes |
|---|---|---|---|---|
| AdvantageKit + LoggedRobot reference | 6328 Mech. Advantage | IO split, replay, `Alert`/tunable discipline | Annotation + logging boilerplate | Our target end-state: `*IO` split, `LoggedRobot`, replay-first |
| Command-based + state machines | 1678 Citrus / 254-style | Scheduler owns requirements, composable autos | Command sprawl; easy to hide game numbers in commands | Keep our `SubsystemManager` + `MissionBase`; adopt commands only via the `Subsystem` (extends WPILib `Subsystem`) seam |
| YAGSL swerve template | YAGSL examples | JSON config speed, sim built in | Config-magic risk (offsets), version churn in betas — **confirmed 2026-10-07: no YAGSL 2027 exists, stack attic'd, `DriveControl` seam holds the shape** | Keep JSON-behind-wrapper (`SwerveBase` owns the parser, nothing else touches it) |
| Dual-vision (LL MT2 + Photon coproc) | 8334 2026 | Redundancy, sim stubs | Two calibrations, NT surface area | Keep `VisionIO` stub with sim twin; defer fusion choice until the game is known |
| Custom AI/co-pilot + score rig | 8334 2026 | Measurable policy work (cards, sweeps, attribution) | Needs determinism + attribution discipline | Re-add as `Game/` consumers only: seeded RNG, sorted iteration, per-bot attribution, JSONL rows |

2026Dreams warnings to enforce structurally: no parallel Blue/Red literals (derive Red), no zone edge outside its owner, no unseeded `Math.random()` in decision/sim paths, no test rewrite standing in for a fix, no sim statistic quoted from contaminated/parallel runs.

## Innovation-landscape review (2026-10-07, alpha-7 branch)

Evaluated an external 2026–2027 innovation report against what this migration
proved. Verdicts below are build-backed; everything else is a rumor with a gate.

- **Commands v3 is optional, not mandatory.** Alpha-7 still ships Commands v2
  (bundled `CommandsV2.json`), the timed-skeleton template is still iterative,
  and our `SubsystemManager` + threaded-mission architecture survived with
  renames only. Spike v3 on one branch with one auto before any rewrite talk.
- **YAGSL-on-SystemCore claims are premature.** No YAGSL 2027 exists, so the
  stack is attic'd regardless of what ecosystem posts assume. Revisit only when
  a release lands; until then the drive decision is vendor-free swerve vs.
  waiting, tracked in `KNOWN_ISSUES.md` item 5.
- **Telemetry migration is done, not a to-do.** `Telemetry.log`,
  `Selectable` + `Tunables` table, `@AutoLog` structs all landed in this
  migration and sim boots with them.
- **Unverified rumors (do not roadmap until sourced):** BLine/BLine-Lib
  (absent from WPILib's vendor metadata), YAMS adoption claims (present in the
  2026 Gradle cache as `yams`/`yall`, never evaluated), Quarky, Hailo-8
  pricing/dates, RP2350 firmware details, CTRE/REV LED-code changes, Limelight
  OS USB/OTA specifics, and all team anecdotes. The report's own citations are
  bare domains, and its ML-vision numbers are self-flagged as unavailable.
- **NPU vision stays kickoff-gated.** No vision game exists yet; `VisionIO`
  stub + sim twin is the correct holding pattern (see `docs/VISION.md`).
