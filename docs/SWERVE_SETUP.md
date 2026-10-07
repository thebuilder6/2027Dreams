---
title: Swerve Setup (YAGSL — parked)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: needs-review
---

# Swerve setup — PARKED until YAGSL-2027 (was: YAGSL 2026.1.14 + sim)

> **Branch `wpilib-2027-alpha7`:** no YAGSL 2027 release exists, so the whole
> stack moved to `attic/yagsl-drive` (code + JSONs + tests + return path).
> `Teleop` and missions program to `Subsystems/DriveControl.java`; `Robot`
> wires `UnconfiguredDrive`. Everything below describes the parked 2026 state
> and reactivates on return — do not treat file paths here as current.

## Scope

Covers the `deploy/swerve` JSONs, vendorpins, and how sim works without
hardware. Does **not** cover path following, vision fusion, or tuning — those
land as separate features.

## Content

- Vendorpins at park time (`attic/2026-vendordeps/`): YAGSL `2026.1.14`,
  REVLib `2026.0.5`, plus required-but-unused Phoenix5/6-replay, Redux,
  Thrifty JSONs so YAGSL resolves. Don't bump without checking sim compat.
- `attic/yagsl-drive/src/main/deploy/swerve/` (8 files) is **placeholder
  config copied from the 2026 robot**. Before wiring to hardware: regenerate
  with the YAGSL configurator against YOUR chassis (module CAN IDs, encoder
  offsets, wheel size, gear ratios). **Never hand-guess
  `absoluteEncoderOffset`** — a wrong offset drives sideways.
  `SwerveConfigTest` guards file presence and shape, never offset values.
- Root `swervedrive.json`: navX IMU, `invertedIMU false`, 4 module names.
- Sim needs no hardware: `DriveIOSim` wraps the parsed `SwerveDrive` (motors
  expose sim states in simulation); constructed with `null` it degrades to a
  pose/voltage recorder for physics-free tests. Rigid-body carpet physics
  (IronMaple-style) is a future upgrade, not a requirement.
- `SwerveBase` owns the parser. Nothing else touches YAGSL. Public API
  (== `DriveControl`): `drive / driveFieldOriented / stop /
  zeroGyroWithAlliance / getPose / getHeading`. Start pose Blue `(1,4,0°)` /
  Red `(len−1,4,180°)`; length comes from `FieldMap`, not a literal.

## Verification

- Last verified on 2026 tree: `cleanTest test --offline` 11/11 green 2026-10-07.
- Next review due: when YAGSL-2027 lands (execute `attic/yagsl-drive/README.md`).

## Related

- `docs/ARCHITECTURE.md`, `docs/TEAM_COMPARISON.md` (YAGSL vs hand-rolled),
  `attic/yagsl-drive/README.md`
