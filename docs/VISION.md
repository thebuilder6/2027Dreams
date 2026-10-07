---
title: Vision (Stub)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Vision — stub until kickoff

## Scope

Covers the `VisionIO` contract, the empty sim twin, and estimate-validity
policy. Does **not** cover cameras, coprocessors, AprilTag layouts, or pose
fusion — those arrive with hardware + the game year's field.

## Content

- `Subsystems/vision/VisionIO.java`: input-only contract (`connected`,
  `hasTarget`, `estimatedPose`, `observationTimeSecs`, `latencySecs`,
  `tagCount`, `avgTagDistanceMeters`). No outputs — sensors aren't commanded.
- `Subsystems/vision/VisionIOSim.java`: reports connected, zero targets. The
  honest answer for an unknown game. At kickoff it generates simulated
  detections from the year's `AprilTagFieldLayout` + true pose (2026 ref:
  `Sim/VisionSim.java`, `Sim/LimelightSim.java`).
- `Subsystems/Vision.java`: owns validity. `getEstimatedPose()` is empty
  unless connected + targeted + fresher than `STALE_TIMEOUT_SECS` (0.150 s,
  2026 value). One instance per camera at kickoff; `VisionIOPhotonVision` /
  `VisionIOLimelight` arrive with the cameras.
- Fusion policy (reserved): estimators correct `RobotState`, never drive
  state directly. Rejects to carry over from 2026: stale packets, excess yaw
  rate, distant/single tags, odometry fallback with driver alert.

## Verification

- Verified against: `cleanTest test --offline` 2026-10-07 (see `CHANGELOG.md`).
- Next review due: kickoff (layout + cameras).

## Related

- `docs/ARCHITECTURE.md`, `docs/MECHANISMS.md` (IO pattern)
