# 2027Dreams — FRC Team 8334

Java Timed Skeleton robot project (WPILib 2026.2.1).

> Note: You asked for Year 2027, but WPILib 2027 is not released yet.
> This project scaffolds against the latest installed version (2026).
> When 2027 WPILib releases, open it in 2027 WPILib VS Code and use
> `WPILib: Set Project to 2027` to upgrade.

## Template
- Language: Java 17
- Base: Timed Skeleton (`TimedRobot` stub, no comments/example code)
- Package: `frc.robot`
- Main class: `frc.robot.Main`

## Project layout
```
.vscode/                  VS Code + WPILib settings
.wpilib/wpilib_preferences.json  Team number, year, language
src/main/java/frc/robot/  Main.java, Robot.java
src/main/deploy/          Static files deployed to /home/lvuser/deploy
vendordeps/               Vendor JSON deps (WPILibNewCommands)
build.gradle              GradleRIO 2026.2.1
settings.gradle
gradle/wrapper/
```

## Prerequisites
- WPILib 2026 WPILib VS Code (C:\Users\Public\wpilib\2026\vscode) — recommended to open this folder with `frccode2026`
- JDK 17 (bundled at `C:\Users\Public\wpilib\2026\jdk`)
- Team number set to 8334 in `.wpilib/wpilib_preferences.json`

## Build / Simulate
```powershell
# Windows
.\gradlew.bat build
.\gradlew.bat runSimulation
.\gradlew.bat deploy   # needs robot connection + team number
```

Or in WPILib VS Code: `Ctrl+Shift+P → WPILib: Build Robot Code / Simulate Robot Code / Deploy Robot Code`.

## Git
This folder is a git repo with an initial commit.
To link to GitHub (gh auth currently expired):
```powershell
gh auth login -h github.com
gh repo create 2027Dreams --source=. --private --push
# or for existing remote:
# git remote add origin https://github.com/<org>/2027Dreams.git
# git push -u origin main
```

## Next steps
- Add subsystems/commands under `frc.robot`
- Add vendordeps via `WPILib: Manage Vendor Libraries`
- Set `Current Game` / update firmware when 2027 season starts
