---
title: External Resources
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# External Resources

## Scope

Single map for vendor docs, 2027 migration sources, and leading-team repos studied for structure (not game answers). Ported from 2026 `TitanRoboticsBuildSeason/docs/RESOURCES.md` and updated for SystemCore / 2027 beta. Does NOT duplicate per-guide tuning content — links only.

Check here before web-searching; prefer pinned vendor versions in `vendordeps/`.

## Content

### Primary (check first)

- https://docs.wpilib.org/ — WPILib (2026: Java 17, GradleRIO 2026.2.1; 2027 beta: Java 25, `org.wpilib`, Commands v3 + OpMode — see `docs/2027_MIGRATION.md`)
- https://docs.advantagekit.org/ — AdvantageKit IO abstraction, `@AutoLog`, replay (2026 pin `26.0.2` in `vendordeps/AdvantageKit.json`; 2027 alpha exists, no template projects yet)
- https://docs.yagsl.com/ — YAGSL swerve (2026.1.14 pinned; SystemCore-era beta `2026.8.18` still on WPILib 2026.2.1)
- https://yet-another-software-suite.github.io/YAGSL/javadocs/ — YAGSL API
- https://choreo.autos/ — Choreo trajectories (pinned `2026.0.3`)
- https://api.typesafe.ai/docs — TypeSafe System One endpoint/schema (client seam deferred, see `KNOWN_ISSUES.md` §H)

### 2027 migration sources

- https://docs.wpilib.org/en/2027/docs/yearly-overview/yearly-changelog.html — roboRIO → SystemCore change list (researched 2026-10-07, distilled in `docs/2027_MIGRATION.md`)
- https://docs.wpilib.org/en/2027/docs/software/systemcore-info/systemcore-introduction.html — SystemCore (Limelight, CM5 + RP2350, CAN-FD/MotionCore, onboard IMU)
- Vendor 2027 alphas (verify at install via `wpilibsuite/vendor-json-repo/2027_alpha*_metadata.json`): AdvantageKit 2027 alpha, YAGSL `2026.8.18`, Phoenix `26.70.0-alpha-2` (targets alpha 7 / SystemCore image 14)

### Vision / sim

- https://docs.photonvision.org/ — PhotonVision + sim (PhotonLib pinned `v2026.3.4`)
- https://docs.limelightvision.io/docs/docs-limelight/ — Limelight MegaTag2
- https://shenzhen-robotics-alliance.github.io/maple-sim/ — IronMaple physics

### Hardware / dashboard

- https://codedocs.revrobotics.com/ — REVLib / SparkMax (pinned `2026.0.5`)
- https://frc-elastic.gitbook.io/docs — Elastic Dashboard widgets/layout
- https://yet-another-software-suite.github.io/YALL/ + `/javadocs/` — YALL
- Phoenix 6 pinned `26.1.0` (`vendordeps/`); don't bump any pin without checking Sim compat (see `AGENTS.md`)

### Leading-team repos (structure study only)

Study structure, never copy game answers. What to steal is listed per repo; details in `docs/TEAM_COMPARISON.md`.

- https://github.com/Mechanical-Advantage/RobotCode2026Public — 6328 "Darwin" (MIT). Take: `LoggedRobot` REAL/SIM/REPLAY shape, `*IO` inputs+outputs structs, `updateInputs`+`processInputs`, `RobotState` observer, `FinanceDepartment`, `LoggedTracer`, `CheckPullRequest`. Idun offboard (Mac mini + RIO dual-deploy) is 2026-only; SystemCore makes it unnecessary.
- https://github.com/frc1678/C2026-Public — 1678 "Limestone". Take: command-based autos (`frc.robot.autos`), superstructure sequencing, `frc.lib.io` reuse pattern, hybrid driver/auto hopper contracts.
- https://github.com/Team254/FRC-2025-Public (+ https://www.team254.com/resources/robot-software/) — 254 library/user split (`com.team254.lib.*` vs `com.team254.frc2025.*`), `RobotState`, AdvantageKit IO per subsystem, sim + PathPlanner integration.

## Verification

- Verified against: port from 2026 `RESOURCES.md` (last_verified 2026-09-28) + `docs/2027_MIGRATION.md` vendor-status lines; team URLs confirmed 2026-10-07 via web search (6328/1678/254 public pages).
- Next review due: when the 2027 beta is installed (re-pin all vendor alphas, confirm AdvantageKit 2027 template).

## Related

- `docs/2027_MIGRATION.md`, `docs/TEAM_COMPARISON.md`, `docs/SWERVE_SETUP.md`
