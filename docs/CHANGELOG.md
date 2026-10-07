---
title: Changelog
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# Changelog

- 2026-10-07: Research doc port — new `docs/RESOURCES.md` (from 2026 `RESOURCES.md` + 2027 alphas/SystemCore sources + verified leading-team repos: 6328 Darwin, 1678 Limestone, 254 2025 public) + `INDEX.md` row. Docs-only; `test --offline --rerun-tasks` 7 files / 16 tests green.
- 2026-10-07: 2026 feature map → roadmap — expanded `KNOWN_ISSUES.md` §§B–H from `TitanRoboticsBuildSeason` (nav stack, shooter/intake/vision IO, auto actions/missions, assist/haptics, TestMode/SysId/Diagnostics, Jev core + knowledge tiers + coach seam, sim engine + determinism + score rig, telemetry/hardware/tuning docs; §H lists deliberately-not-ported 2026 game/cloud items). Docs-only; `test --offline --rerun-tasks` 5 files / 11 tests green.

- 2026-10-07: 2026 feature map → roadmap — expanded `KNOWN_ISSUES.md` §§B–H from `TitanRoboticsBuildSeason` (nav stack, shooter/intake/vision IO, auto actions/missions, assist/haptics, TestMode/SysId/Diagnostics, Jev core + knowledge tiers + coach seam, sim engine + determinism + score rig, telemetry/hardware/tuning docs; §H lists deliberately-not-ported 2026 game/cloud items). Docs-only; `test --offline --rerun-tasks` 5 files / 11 tests green.

- 2026-10-07: Foundations — generic `Subsystem`/`SubsystemManager`, `Constants`, `PortMap`, Blue-origin `FieldMap` + `AllianceFlipUtil`, `GameDefinition`/`UnknownGame` seam, `MissionBase`/`AutoMissionExecutor`/`DoNothing`, `Teleop` input shaping, `Alert`, `Robot` wiring. Tests: 3 files / 5 tests green (`compileJava --offline` + `test --offline` BUILD SUCCESSFUL 2026-10-07).
- 2026-10-07: 6328 takeaways — `Robot extends LoggedRobot` (AdvantageKit 26.0.2, REAL/SIM/REPLAY via `-Dfrc.replay=true`), `generateBuildConstants` (atomic) + `replayWatch` task, `Mode.REPLAY` + `CheckPullRequest` guard, `Subsystems/template/TemplateIO.java` (inputs + outputs structs), `Telemetry/LoggedTracer.java`. `@AutoLog` generation verified (`TemplateIOInputsAutoLogged`). Tests still 3 files / 5 tests green.
- 2026-10-07: Agent docs to 2026 shape — expanded `AGENTS.md` (coordination, generated-code ban, conventions, strict docs contract), ported `tools/lock/` (`Lock.psm1` + acquire/release/status; 3 resources, `sweep` arrives with the rig), `docs/COORDINATION.md`, `docs/_TEMPLATE.md`, root `KNOWN_ISSUES.md` with roadmap, `.locks/` gitignored. Lock round-trip verified via `status.ps1`; `cleanTest test --offline` 5/5 green.
- 2026-10-07: Drive template (YAGSL) — `SwerveBase` singleton (parse-once, alliance start pose from `FieldMap`, heading/cosine/skew config), `DriveIO` + `DriveIOSparkMax` + `DriveIOSim` (null-drive degrades to pose/voltage recorder), 8 placeholder swerve JSONs, `docs/SWERVE_SETUP.md`. One fix: `Field2d` goes via `SmartDashboard.putData` (not `Logger.recordOutput` — no matching overload). `cleanTest test --offline` 11/11 green.
- 2026-10-07: Mechanism template — `Mechanism` (`STANDBY/RUNNING`, open-loop volts; real mechanisms copy it and add control laws), `MechanismIO` + `MechanismIOSparkMax` + `MechanismIOSim` (`DCMotorSim` via `LinearSystemId`, no vendor physics), `Hardware/SparkMaxMotor` null-safe REV wrapper, `RobotState` pose holder fed by `SwerveBase`, battery model (`BatterySim` → `RoboRioSim`) in `Robot.simulationPeriodic`, `docs/MECHANISMS.md`. One real bug: `DCMotorSim` ctor takes a `LinearSystem`, not `DCMotor`; one test-env lesson: sim-physics tests must `HAL.initialize(500, 0)` or the JVM dies in `wpiHal.dll` (rule added to `AGENTS.md`). `cleanTest test --offline` 16/16 green.
- 2026-10-07: Vision stub — `VisionIO` (input-only) + `VisionIOSim` (connected, zero targets), `Vision` owns validity (`getEstimatedPose` empty unless fresh, 150 ms stale reject), wired into `Robot`, `docs/VISION.md`. One tree hazard: `Vision.java` package line was changed to `.vision` by another session and broke `Robot` — restored to `Subsystems` (matches 2026) and verified by full read. `cleanTest test --offline` 19/19 green.
