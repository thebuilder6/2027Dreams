---
title: Swerve Setup (vendor-free scaffold)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# Swerve setup — temporary vendor-free scaffold (YAGSL is the target)

> **Branch `wpilib-2027-alpha7`:** YAGSL has no 2027 release, so the robot
> drives on `Subsystems/SwerveDrive.java` (WPILib kinematics + `ModuleIO`,
> 6328 pattern) behind `Subsystems/DriveControl.java`. **Temporary by team
> decision** — YAGSL returns when a 2027 release lands (return path +
> 8.18 schema warning in `attic/yagsl-drive/README.md`).

## Scope

Covers the scaffold's geometry placeholders, control law limits, and how sim
works without hardware. Does **not** cover path following, vision fusion, or
tuning — those land as separate features.

## Content

- `Subsystems/swerve/`: `SwerveModuleIO` (inputs + outputs structs, same shape
  as `MechanismIO`), `SwerveModuleIOSim` (reuses the `MechanismIOSim` plant ×2
  — dynamics math has one owner), `SwerveModuleIOSparkMax` (2× NEO via
  `Hardware/SparkMaxMotor`; steer angle is relative-encoder placeholder),
  `SwerveGyroIO`/`SwerveGyroIOSim` (integrates commanded chassis rate).
- `Subsystems/SwerveDrive.java`: odometry + open-loop law (kV feedforward on
  drive matched to the sim plant, P on wrapped steer error). **Limits, stated
  plainly:** no slip model, no closed-loop wheel control, sim gyro measures
  commanded (not physical) motion, all `SwerveConstants` are placeholders.
- Geometry placeholders (`Data/Constants.SwerveConstants`, all unmeasured):
  0.55 m track/width, 0.0508 m wheel radius, 6:1 drive / 12:1 steer, steer P
  0.12 V/deg. Measure on the chassis — never tune around wrong geometry.
- CAN IDs (`Hardware/PortMap`): `RESERVED_DRIVE_*`/`RESERVED_STEER_*`, all −1.
  Real mode constructs against them and reports disconnected until wired
  (null-safe `SparkMaxMotor` path) — do not wire hardware to these yet.
- `Robot.java` picks impls by `Constants.getMode()` (sim twins off-hardware,
  SparkMax + stub gyro on real). Ctor-injection throughout, so tests inject
  sim IOs directly (see `SwerveDriveTest`).
- Sim needs no hardware: `gradlew run` drives with a plugged controller,
  `Field` widget shows the pose. Physics-free unit tests construct
  `SwerveModuleIOSim` directly.

## Verification

- Verified against: `test --offline --rerun-tasks` 2026-10-07 (see `docs/CHANGELOG.md`).
- Next review due: chassis measured (replace placeholders) or YAGSL-2027 lands
  (execute `attic/yagsl-drive/README.md`).

## Related

- `docs/ARCHITECTURE.md`, `docs/TEAM_COMPARISON.md` (YAGSL vs hand-rolled),
  `attic/yagsl-drive/README.md`, `docs/MECHANISMS.md` (IO pattern)
