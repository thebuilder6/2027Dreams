---
title: 2027 Migration (WPILib beta + SystemCore)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# 2027 migration — WPILib 2026 → 2027 beta, roboRIO → SystemCore

Source: `docs.wpilib.org/en/2027/.../yearly-changelog` + `.../systemcore-introduction` (researched 2026-10-07). Executed 2026-10-07 on branch `wpilib-2027-alpha7` — this file is now the record, not the plan.

## What is confirmed for 2027

- **Controller:** roboRIO gone. SystemCore only (Limelight, CM5 + RP2350, custom Linux). MotionCore over CAN-FD. Multiple CAN buses, SmartIO, onboard IMU, Expansion Hub.
- **Removed HW APIs:** relay, analog output, SPI + SPI IMUs, analog gyro, DMA, interrupts/counters, ultrasonic, analog triggers, Nidec, Servo. Code touching these must go behind IO interfaces now.
- **Language:** Java 25, C++23. Ubuntu LTS only. Codespaces/CI images must move off Java 17.
- **Packages:** `edu.wpi.first.*` → `org.wpilib.*`. VS Code importer handles most renames; our wrapper is `Interfaces/Subsystem` so the blast radius is small.
- **Frameworks:** Commands v3 (new) + OpMode (FTC-style). `Test` mode → `Utility`. Decide v2-vs-v3 after the beta examples land; do not mix both in one codebase.
- **Telemetry:** `SmartDashboard/SendableChooser/Sendable` → new `Telemetry` + `Tunables` APIs (`Selectable` replaces chooser). Constants → `ALL_CAPS`. `DriverStation` splits into `MatchState`/`RobotState`. NT3 removed. `AprilTag/CameraServer` move to vendordeps.
- **Controllers:** single `Gamepad` class + default deadband (our `Teleop` deadbands stay canonical — verify no double-deadband).
- **Vendor status (verified 2026-10-07 against alpha-7):** AdvantageKit `27.0.0-alpha-6` ✓, REVLib `2027.0.0-alpha-8` ✓ (explicit `CANPort` bus, `Signal` reads, `setThrottle`). **No YAGSL 2027 exists** (latest `2026.10.03`, 2026-only; old repo archived) — drive is attic'd, see below. Phoenix/Photon/Choreo/Redux/Thrifty/Studica unused by this tree, so not pinned. Check `wpilibsuite/vendor-json-repo/2027_alpha*_metadata.json` before adding any vendor.

## Checklist — executed 2026-10-07 (branch `wpilib-2027-alpha7`)

1. [x] Installed WPILib `2027_alpha7` side-by-side. JDK `C:\Users\Public\wpilib\2027_alpha7\jdk` (Java 25 LTS), GradleRIO `2027.0.0-alpha-7`, Gradle wrapper 9.4.1, `org.wpilib` group. Launcher: `wpilibcode2027_alpha7.cmd`.
2. [x] Imported without the VS Code UI: template `build.gradle`/`settings.gradle` lifted from the bundled `vscode-wpilib` VSIX (`resources/gradle/{java,shared}`), then the VSIX `java_replacements.json` map applied across `src/` (36/48 files touched). `projectYear: 2027_alpha7`, SystemCore deploy target (`/home/systemcore/deploy`), `application` plugin replaces the fat-jar manifest flow.
3. [x] `SmartDashboard`/`SendableChooser` → `Telemetry.log` + `Selectable` (published via `Tunables.getTable`); `Alliance.BLUE/RED`; `MatchState.getAlliance()`; `Timer.getTimestamp()`; `ChassisSpeeds` → `ChassisVelocities` (`vx/vy/omega`); `Translation2d.getAngle()` → `Optional`; `Rotation2d.ZERO`; Back/Start → View/Menu buttons; `getRawButton` via `getHID()`. Deleted `DriverStation.silenceJoystickConnectionWarning` (gone; joystick warnings are automatic alerts now).
4. [x] Pinned AdvantageKit alpha-6 + REVLib alpha-8 (+ bundled CommandsV2). 2026 vendor JSONs moved to `attic/2026-vendordeps/`.
5. [x] `Robot extends LoggedRobot` survives (akit alpha-6, same `junction` packages). DONE 2026-10-07 on akit 26.0.2; re-verified on alpha-6 this migration.
6. [x] Removed-HW compile: no SPI/servo/relay references in tree (grep clean; YAGSL/CANcoder code left with the attic).
7. [ ] Image SystemCore + new Driver Station; `deploy` smoke test before any feature work. **Still open — needs hardware.**
8. [x] Drive: temporary vendor-free scaffold live (`Subsystems/SwerveDrive.java` on `DriveControl`; `UnconfiguredDrive` deleted). YAGSL stays the target — return path in `attic/yagsl-drive/README.md`.

## Alpha-7 API surprises (not in the pre-install research)

- No `robotInit()` — one-time init moves into the constructor. Test mode is `utilityInit`/`utilityPeriodic`.
- `DCMotorSim` ctor takes a `LinearSystem`; the `LinearSystemId` plant factories are gone. `MechanismIOSim` now integrates the analytic first-order motor model (`A = −G²·Kt/(Kv·R·J)`, `B = G·Kt/(R·J)`) with current from `DCMotor.getCurrent(ω, V)`.
- Exact, not Euler: `MechanismIOSim` integrates the closed-form step response (`ω += (ωss − ω)·(1 − e^(A·dt))`) — identical for slow plants, stable for stiff ones (high gearing + tiny inertia, e.g. steer, explodes under Euler at 20 ms; found by the swerve scaffold's failing test).
- `SwerveModuleState` → `SwerveModuleVelocity`; kinematics are immutable (`desaturateWheelVelocities` returns a new array, `@NoDiscard` enforced at compile).
- `HAL.initialize()` is no-arg in tests.
- `Selectable` needs `publishTunable(Tunables.getTable(...))` to appear on dashboards; `Field2d` publishes via `Telemetry.log` (it implements `TelemetryLoggable`).
- Sim runs via `gradlew run` (application plugin), not `simulateJava*`.
