---
title: Onboarding
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# Onboarding — your first 30 minutes

## Scope

The student path from fresh laptop to first green test, first sim drive, and
first small code change. Covers WPILib 2026 only — the 2027 beta move is a
separate checklist in `docs/2027_MIGRATION.md`. Does NOT cover mechanism tuning
or auto design (see `docs/MECHANISMS.md`, `docs/TELEOP_AUTO.md`).

## Content

### 0. Install (once)

1. Install **WPILib 2026 WPILib VS Code** (`frccode2026`) — it bundles the JDK
   at `C:\Users\Public\wpilib\2026\jdk` and the offline maven cache. Open this
   folder with it, not plain VS Code.
2. Clone the repo. Confirm team 8334 in `.wpilib/wpilib_preferences.json`.
3. Every terminal session starts with (Windows PowerShell, repo root):
   ```powershell
   $env:JAVA_HOME = "C:\Users\Public\wpilib\2026\jdk"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

### 1. First build (~1 min)

```powershell
powershell -File tools/lock/acquire.ps1 -Resource gradle-build -Reason "first build"
.\gradlew.bat compileJava --offline
powershell -File tools/lock/release.ps1 -Resource gradle-build
```

The lock protects `build/` on this shared tree — always hold `gradle-build`
while compiling or testing. If acquire prints a holder and exits `3`, wait and
retry; never kill another agent's process (see `docs/COORDINATION.md`).
Success ends with `BUILD SUCCESSFUL`.

### 2. First test (~1 min)

```powershell
.\gradlew.bat test --offline --tests "frc.robot.Utils.AllianceFlipUtilTest"
.\gradlew.bat test --offline   # full suite: 13 files / 32 tests
```

Green means `BUILD SUCCESSFUL`. Ignore two harmless artifacts: a
`SubsystemManager isolated exploding.update` warning (an intentional
fault-isolation test) and PowerShell `NativeCommandError` exit 1 — both are
expected. If you write a test touching sim physics, `Timer`, or HAL natives,
call `HAL.initialize(500, 0)` in setup or the JVM dies in `wpiHal.dll`.

Copy `src/test/java/frc/robot/TeleopTest.java` as your first test template —
pure functions, no HAL setup. Avoid copying `ActionsTest` or
`Vector2dSlewRateLimiterTest` first (they need HAL init + `Timer` delays).

### 3. First sim (~2 min)

```powershell
powershell -File tools/lock/acquire.ps1 -Resource sim-gui -Reason "first sim"
.\gradlew.bat simulateJava
```

Only one sim runs at a time (fixed NT/CameraServer ports). In SimGUI: enable
with the Driver Station, drive with a plugged controller, watch the `Field`
widget. Sim needs no hardware and no real swerve offsets
(see `docs/SWERVE_SETUP.md`). Release the lock when done.

### 4. First code change

- **New button binding:** add it in `Teleop.teleopPeriodic()` next to the
  existing slow-mode / re-zero / e-stop bindings; ports live in
  `Hardware/PortMap.java`. Deadband + shaping stay in `Teleop` — never
  deadband inside `Hardware/Controller`.
- **New dashboard number:** don't. Numbers go through the tunables mechanism
  (`KNOWN_ISSUES.md` §G), not raw SmartDashboard — ask a lead first.
- **New mechanism:** copy `Subsystems/Mechanism.java` (never change the IO
  boundary), add control law, register via `SubsystemManager`
  (see `docs/MECHANISMS.md`).
- **New doc:** copy `docs/_TEMPLATE.md` and add a row to the `docs/INDEX.md`
  table — a guide isn't finished without it.

### 5. First deploy

Checklist: battery in, USB or radio connected, team 8334, nobody else
deploying (hold the `deploy` lock — last writer wins). Deploy target today is
still roboRIO; SystemCore imaging comes with the 2027 beta.

## Verification

- Verified against: `test --offline --rerun-tasks` 2026-10-07 (see `docs/CHANGELOG.md`).
- Next review due: when the 2027 beta lands (JDK path + commands change).

## Related

- `README.md`, `AGENTS.md`, `docs/INDEX.md`, `docs/COORDINATION.md`
