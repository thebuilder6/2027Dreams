---
title: 2027 Migration (WPILib beta + SystemCore)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# 2027 migration — WPILib 2026 → 2027 beta, roboRIO → SystemCore

Source: `docs.wpilib.org/en/2027/.../yearly-changelog` + `.../systemcore-introduction` (researched 2026-10-07). Update this file when the beta is installed.

## What is confirmed for 2027

- **Controller:** roboRIO gone. SystemCore only (Limelight, CM5 + RP2350, custom Linux). MotionCore over CAN-FD. Multiple CAN buses, SmartIO, onboard IMU, Expansion Hub.
- **Removed HW APIs:** relay, analog output, SPI + SPI IMUs, analog gyro, DMA, interrupts/counters, ultrasonic, analog triggers, Nidec, Servo. Code touching these must go behind IO interfaces now.
- **Language:** Java 25, C++23. Ubuntu LTS only. Codespaces/CI images must move off Java 17.
- **Packages:** `edu.wpi.first.*` → `org.wpilib.*`. VS Code importer handles most renames; our wrapper is `Interfaces/Subsystem` so the blast radius is small.
- **Frameworks:** Commands v3 (new) + OpMode (FTC-style). `Test` mode → `Utility`. Decide v2-vs-v3 after the beta examples land; do not mix both in one codebase.
- **Telemetry:** `SmartDashboard/SendableChooser/Sendable` → new `Telemetry` + `Tunables` APIs (`Selectable` replaces chooser). Constants → `ALL_CAPS`. `DriverStation` splits into `MatchState`/`RobotState`. NT3 removed. `AprilTag/CameraServer` move to vendordeps.
- **Controllers:** single `Gamepad` class + default deadband (our `Teleop` deadbands stay canonical — verify no double-deadband).
- **Vendor status (beta, verify at install):** AdvantageKit 2027 alpha exists but no template projects yet; YAGSL `2026.8.18` is the SystemCore-era beta (still on WPILib 2026.2.1); Phoenix `26.70.0-alpha-2` targets alpha 7 / SystemCore image 14. Check `wpilibsuite/vendor-json-repo/2027_alpha*_metadata.json` before pinning.

## Checklist to flip this repo

1. Install WPILib 2027 beta side-by-side; record JDK path + GradleRIO version here.
2. Run `WPILib: Import Project`, then fix `org.wpilib` imports (expect `Main/Robot/Subsystem/AllianceFlipUtil/FieldMap`).
3. Replace `SmartDashboard/SendableChooser` with `Telemetry/Selectable`; rename constants to `ALL_CAPS` where the compiler demands.
4. Pin vendordeps alphas (AdvantageKit, YAGSL, REV, Photon, Choreo, CTRE) — one PR per vendor, sim-compile each.
5. `Robot extends LoggedRobot` + AdvantageKit logger wiring (REAL: wpilog+NT, SIM: NT-only, REPLAY: reader). DONE 2026-10-07 on akit 26.0.2 — re-verify against the 2027 alpha when it lands (package `org.wpilib` renames will touch these files).
6. Validate removed-HW compile (SPI/servo/relay references must be zero: grep before deploy).
7. Image SystemCore + new Driver Station; `deploy` smoke test before any feature work.
