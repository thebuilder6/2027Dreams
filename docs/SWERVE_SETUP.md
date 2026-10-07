---
title: Swerve Setup (YAGSL)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Swerve setup — YAGSL 2026.1.14 + sim

## Scope

Covers the `deploy/swerve` JSONs, vendorpins, and how sim works without
hardware. Does **not** cover path following, vision fusion, or tuning — those
land as separate features.

## Content

- Vendorpins (`vendordeps/`): YAGSL `2026.1.14`, REVLib `2026.0.5`, plus
  required-but-unused Phoenix5/6-replay, Redux, Thrifty JSONs so YAGSL
  resolves. Don't bump without checking sim compat.
- `src/main/deploy/swerve/` (8 files) is currently **placeholder config copied
  from the 2026 robot**. Before wiring to hardware: regenerate with the YAGSL
  configurator against YOUR chassis (module CAN IDs, encoder offsets, wheel
  size, gear ratios). **Never hand-guess `absoluteEncoderOffset`** — a wrong
  offset drives sideways. `SwerveConfigTest` guards file presence and shape,
  never offset values.
- Root `swervedrive.json`: navX IMU, `invertedIMU false`, 4 module names.
- Sim needs no hardware: `DriveIOSim` wraps the parsed `SwerveDrive` (motors
  expose sim states in simulation); constructed with `null` it degrades to a
  pose/voltage recorder for physics-free tests. Rigid-body carpet physics
  (IronMaple-style) is a future upgrade, not a requirement.
- `SwerveBase` owns the parser. Nothing else touches YAGSL. Public API:
  `drive / driveFieldOriented / stop / zeroGyroWithAlliance / getPose /
  getHeading`. Start pose Blue `(1,4,0°)` / Red `(len−1,4,180°)`; length comes
  from `FieldMap`, not a literal.

## Verification

- Verified against: `cleanTest test --offline` 2026-10-07 (see `CHANGELOG.md`).
- Next review due: when real chassis JSONs land (offsets measured).

## Related

- `docs/ARCHITECTURE.md`, `docs/TEAM_COMPARISON.md` (YAGSL vs hand-rolled)
