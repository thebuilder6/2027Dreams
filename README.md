# 2027Dreams — FRC Team 8334

Generic 2027 robot base (WPILib 2026.2.1, Java 17): YAGSL swerve, AdvantageKit
logging, IO-layer subsystems, auto/teleop loop. No game code until kickoff —
game numbers live in `Game/` only.

New here? Start with `docs/ONBOARDING.md` (install → build → test → sim in
about 30 minutes). Agents: read `AGENTS.md` first.

## Quickstart (Windows PowerShell, repo root)

All commands need the WPILib 2026 JDK on `PATH`:

```powershell
$env:JAVA_HOME = "C:\Users\Public\wpilib\2026\jdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat compileJava --offline   # fast compile
.\gradlew.bat test --offline          # full suite (13 files / 33 tests)
.\gradlew.bat simulateJava            # desktop SimGUI (needs sim-gui lock)
```

- Always use `.\gradlew.bat`, never system `gradle`; always `--offline`.
- `build/` is single-tenant on this shared tree: guard it with
  `powershell -File tools/lock/acquire.ps1 -Resource gradle-build -Reason "..."`
  (release afterwards) — see `AGENTS.md` §Resource coordination.
- One test: `.\gradlew.bat test --offline --tests "frc.robot.Utils.AllianceFlipUtilTest"`.
- Green looks like `BUILD SUCCESSFUL`. Two harmless warnings: a
  `SubsystemManager isolated exploding.update` line (an intentional
  fault-isolation test) and PowerShell `NativeCommandError` exit 1 — trust the
  `BUILD SUCCESSFUL` line.

## Project layout

```
src/main/java/frc/robot/  Robot.java, Teleop.java (+ Auto/, Game/, Hardware/,
                          Interfaces/, Navigation/, Subsystems/, Telemetry/,
                          Utils/, Data/)
src/main/deploy/swerve/   8 placeholder YAGSL JSONs (regenerate: docs/SWERVE_SETUP.md)
src/test/java/frc/robot/  Mirrors main; copy TeleopTest.java as your first test
vendordeps/               YAGSL, REVLib, Phoenix, AdvantageKit pins
tools/lock/               Shared-tree locks (gradle-build / sim-gui / deploy)
docs/                     Start at docs/INDEX.md (map of every guide)
```

## Docs (single map: `docs/INDEX.md`)

| You want | Read |
|---|---|
| Build/test/sim rules | `AGENTS.md` |
| Architecture contracts | `docs/ARCHITECTURE.md` |
| Swerve JSON setup | `docs/SWERVE_SETUP.md` |
| First mechanism | `docs/MECHANISMS.md` |
| Vision stub | `docs/VISION.md` |
| Teleop + auto loop | `docs/TELEOP_AUTO.md` |
| Roadmap / what is missing | `KNOWN_ISSUES.md` |
| 2027 beta checklist | `docs/2027_MIGRATION.md` |

Rule: `build.gradle` + source beat prose when they conflict.

## Deploy

Needs robot connection + team 8334 (already in `.wpilib/wpilib_preferences.json`).
Target is still roboRIO; the SystemCore move happens with the 2027 beta
(see `docs/2027_MIGRATION.md`). Guard shared deploys with the `deploy` lock.
