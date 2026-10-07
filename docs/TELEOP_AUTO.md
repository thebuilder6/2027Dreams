---
title: Teleop + Auto Loop
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Teleop + auto loop

## Scope

Covers driver input wiring, the mission executor contract, and the two
template missions. Does **not** cover mechanism bindings (they land with real
mechanisms), co-pilot assist, or Choreo autos.

## Content

- `Teleop.java`: owns both controllers. Cleanup order — circular deadband →
  cubic shape → slew → scale → Red flip (negated sticks, Red negates X+Y so
  push-away is always away from the driver). Bindings: left stick drive,
  right stick rotate, stick-click slow mode (0.35×/0.50×), double-tap A (0.4 s)
  re-zeros to the alliance heading, Back+Start e-stops the drive.
- `Auto/AutoMissionExecutor.java`: one mission on its own 50 Hz thread.
  `start` stops any running mission first; `disabledInit` always stops.
- `Auto/Missions/DriveDistanceMission.java`: pose-supplier + drive-consumer
  injection (testable without hardware). `DoNothingMission` stays the chooser
  default.
- `Robot.java`: chooser holds **names**, `getAutoMissionForParams` builds a
  fresh mission per selection (stateful instances are never reused).
- Composable steps: `Interfaces/Action` + `Auto/Actions/WaitAction` +
  `Auto/Actions/SeriesAction`, adapted by `Auto/Missions/ActionMission`
  (`init→start`, `run→update`, `isDone→isFinished`, `end→done`).

## Verification

- Verified against: `cleanTest test --offline` 2026-10-07 (see `CHANGELOG.md`).
- Next review due: first mechanism binding.

## Related

- `docs/ARCHITECTURE.md`, `docs/SWERVE_SETUP.md`
