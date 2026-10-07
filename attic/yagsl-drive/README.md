# Attic: YAGSL drive (2026-only, no 2027 release yet)

Moved here on the `wpilib-2027-alpha7` migration branch. As of 2026-10-07 the
latest YAGSL release is `2026.10.03` (2026/WPILib-2026 only); nothing here
compiles against WPILib 2027 alpha-7 (`edu.wpi.first` → `org.wpilib`).

## Contents

- `src/main/java/.../Subsystems/SwerveBase.java` — YAGSL wrapper singleton.
- `src/main/java/.../Subsystems/drive/` — `DriveIO` + SparkMax/Sim.
- `src/test/...` — `DriveIOSimTest`, `SwerveConfigTest`.
- `src/main/deploy/swerve/` — 8 placeholder JSONs (still need configurator
  regeneration before hardware use).
- `../2026-vendordeps/` — YAGSL + Phoenix5/6-replay + Redux + Thrifty +
  WPILibNewCommands 2026 JSONs.

## Return path (when YAGSL-2027 lands)

1. Add the YAGSL-2027 vendor JSON (+ its `requires` set).
2. `git mv` these files back to their pre-attic paths.
3. Apply the `edu.wpi.first` → `org.wpilib` renames (same map as the rest of
   the branch) + any YAGSL API changes.
4. Make `SwerveBase` implement `Subsystems/DriveControl.java` (it already
   satisfies the shape: `drive / driveFieldOriented / stop /
   zeroGyroWithAlliance / getPose / getHeading`).
5. Replace `UnconfiguredDrive` wiring in `Robot.java`, restore
   `SwerveConfigTest`, re-verify `cleanTest test`.
