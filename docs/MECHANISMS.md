---
title: Mechanisms (TemplateIO Consumers)
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Mechanisms — first TemplateIO consumer

## Scope

Covers the generic rotary mechanism (`Mechanism` + `MechanismIO` + Spark/Sim),
`RobotState`, and the sim battery model. Does **not** cover control laws
(PID+FF, gravity compensation, jam detection) — real mechanisms add those by
copying `Mechanism.java`, never by changing the IO boundary.

## Content

- `Subsystems/Mechanism.java`: `STANDBY/RUNNING`, open-loop volts. Real
  mechanisms (intake, shooter, arm) copy this file and add their control law;
  the `update → processInputs → applyOutputs` shape stays identical.
- `Subsystems/mechanism/MechanismIO.java`: `@AutoLog` inputs + outputs struct
  (`STOP/VOLTAGE/DUTY`). `MechanismIOSparkMax` (NEO via `Hardware/SparkMaxMotor`,
  null-safe construction) vs `MechanismIOSim` (WPILib `DCMotorSim`, no vendor
  physics needed).
- `Hardware/SparkMaxMotor.java`: thin REV wrapper (configure/inverted/brake/
  current-limit, RPM/rotation/volt/amp reads). IO classes never touch REV API.
- `Navigation/RobotState.java`: central estimated-pose holder. `SwerveBase`
  feeds drive odometry each cycle; vision fusion corrects here later.
  Consumers read the estimate, never a subsystem directly.
- Battery: `Robot.simulationPeriodic` sums `getSimulationCurrentDraw()` over
  all subsystems → `BatterySim` → `RoboRioSim`. Mechanisms report
  `|currentAmps|`; keep every new subsystem's draw honest or brownout sim lies.

## Verification

- Verified against: `cleanTest test --offline` 2026-10-07 (see `CHANGELOG.md`).
- Next review due: when the first real mechanism (control law) lands.

## Related

- `docs/ARCHITECTURE.md`, `docs/SWERVE_SETUP.md`
