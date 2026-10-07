---
title: Known Issues and Roadmap
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: living
---

# Known Issues & Roadmap

> **Latest clean run (2026-10-07, offline): 13 test files / 33 tests, `BUILD SUCCESSFUL`.** Counts are source tests plus the measured run; historical counts below are checkpoints, not current totals.

Add entries as `- [ ] description`. Include repro or file refs so future sessions can verify.
Status tags: `[OPEN]`, `[PARTIAL]` (partly implemented), `[STALE]` (no repro since), and `[RESOLVED]` (fixed/closed).

## Current Top Priorities

Ranked by foundation-before-features. All game-agnostic; kickoff adds a `Game/` implementation without rewriting the base.

1. **Drive template** `[RESOLVED]` — `SwerveBase` + `DriveIO`/`DriveIOSparkMax`/`DriveIOSim`, YAGSL `2026.1.14` pinned, 8 placeholder JSONs (must regenerate via configurator — see `docs/SWERVE_SETUP.md`). Evidence: 11/11 green 2026-10-07. Open follow-up: YAGSL-vs-custom decision is settled (YAGSL, per team choice); path following + vision fusion still open.
2. **Mechanism template** `[RESOLVED]` — `Mechanism` + `MechanismIO`/`SparkMax`/`Sim` (`DCMotorSim`, no vendor physics), `Hardware/SparkMaxMotor` wrapper, `RobotState` pose holder fed by `SwerveBase`, sim battery model in `Robot`. Evidence: 16/16 green 2026-10-07. Open follow-ups: control laws (PID+FF, gravity arm, jam detection) arrive with real mechanisms; see §C.
3. **Vision stub** `[RESOLVED]` — `VisionIO` (input-only) + `VisionIOSim` (connected, zero targets), `Vision` owns validity (`getEstimatedPose` empty unless fresh, 150 ms stale reject). Wired into `Robot`. Evidence: 19/19 green 2026-10-07. Open follow-ups: camera IOs, tag layout, and fusion at kickoff; see `docs/VISION.md`.
4. **2027 beta import** `[OPEN]` — Java 25, `org.wpilib` renames, Commands v3 decision, vendor alpha pins. See `docs/2027_MIGRATION.md`.

## A. Foundations

- [x] `[RESOLVED]` Generic `Subsystem`/`SubsystemManager`, Blue-origin geometry, `GameDefinition` seam, auto executor, input shaping, `Alert`, `LoggedRobot` + AdvantageKit 26.0.2, lock protocol, agent docs. Evidence: 5/5 green 2026-10-07.

## B. Drive / navigation (from 2026 `Navigation/`, `SwerveBase`)

All game geometry stays behind `Game/GameDefinition`; 2026 hub/ramp/trench numbers are never copied literally.

- [ ] `[OPEN]` `StaticPathfinder` A* roadmap + endpoint validation + escape waypoint + `findPathWithStatus` (`DIRECT`/`ROADMAP`/`LOCAL_RECOVERY`/`UNREACHABLE`). 2026 ref: `Navigation/StaticPathfinder.java`, `FieldMapTest`, `StaticPathfinderTrenchMaskTest`, `PathingQualityTest`.
- [ ] `[OPEN]` `TrajectoryController` holonomic controller + waypoint-plane gating + cross-track gate (open-field vs trench-specific) + hold-position streaming. 2026 ref: `Navigation/TrajectoryController.java`.
- [ ] `[OPEN]` `DynamicRouter` APF decompose-not-sum + `DynamicObstacle` shared `Timer.getTimestamp()` clock + `MAX_BACKPRESSURE_FRACTION`/`LATERAL_FRACTION`/`MAX_LATERAL_MPS` + pose-parity tie-break. 2026 ref: `Navigation/DynamicRouter.java`, `DynamicObstacle.java`, `DynamicRouterTest`, `RepulsionBoundsTest`.
- [ ] `[OPEN]` `ContactWatchdog` stall/deadlock/trench-reverse + `TargetProgressWatchdog` peer-independent blacklist (3.0 s / 0.25 m / 1.0 m / 20 s TTL / 1.2 s escape / `STATIC_ESCALATION_COUNT`) + `FuelTargetMemory` per-agent latch (`SWITCH_RATIO`). 2026 ref: `Navigation/ContactWatchdog.java`, `Navigation/TargetProgressWatchdog.java`, `Intelligence/FuelTargetMemory.java`, `StuckRecoveryTest`, `TargetProgressWatchdog*Test`, `ContactWatchdogTest`.
- [ ] `[OPEN]` `GlidePoints` strategic waypoints (Blue-origin, explicit `neutral` flag, never name-prefix sniff) + `FieldMap` obstacle/AABB single-ownership + `isPointInHardObstacle` vs perimeter-band distinction. 2026 ref: `Navigation/GlidePoints.java`, `Navigation/FieldMap.java`.
- [x] `[RESOLVED 2026-10-07]` `Utils/Vector2dSlewRateLimiter` + input slew/state reset on mode init. 2026 ref: `Utils/Vector2dSlewRateLimiter.java`, `Teleop.java:113-114` deadbands. **Ported verbatim; `Teleop` now slews the translation vector (direction-preserving) instead of dual 1D limiters. Covered by `Vector2dSlewRateLimiterTest` (rate convergence, direction preservation, reset).**
- [ ] `[OPEN]` Path following + vision fusion into `SwerveBase` (Choreo tracking, dynamic std-devs, `RobotState` estimator singleton). 2026 ref: `Subsystems/SwerveBase.java`, `ARCHITECTURE.md` §3A.

## C. Mechanisms (first real `TemplateIO` consumers)

- [ ] `[OPEN]` Shooter: `ShooterIO`/`ShooterIOSparkMax`/`ShooterIOSim` + dual-flywheel PID+FF + RPM distance-table seam + kicker state machine (RPM-error gate, zone/ceiling/solution interlocks) + `ShooterSim` ballistics + `ShotTracker` + `calibrate_shooter.py`. 2026 ref: `Subsystems/Shooter.java`, `Subsystems/shooter/*`, `Sim/ShooterSim.java`, `Sim/ShotTracker.java`, `tools/tune/calibrate_shooter.py`, `Test/ShooterTuning.java`.
- [ ] `[OPEN]` Intake: `IntakeIO`/`IntakeIOSparkMax`/`IntakeIOSim` + ProfiledPID arm + `ArmFeedforward` + state set (Standby/Down/Feeding/Ejecting/…) + jam detect/eject (30 A / 0.5 s / 1.0 s) + encoder fallback + `calibrate_intake.py`. 2026 ref: `Subsystems/Intake.java`, `Subsystems/intake/*`, `Test/IntakeTesting.java`, `tools/tune/calibrate_intake.py`.
- [ ] `[OPEN]` Vision stub: `VisionIO`/`VisionIOPhotonVision`/`VisionIOLimelight`/`VisionIOSim` + `Vision` subsystem + `Sim/VisionSim.java` + `Sim/LimelightSim.java` + stale/yaw-rate/range rejects + odometry fallback + `VisionTesting`. 2026 ref: `Subsystems/Vision.java`, `Subsystems/vision/*`, `ThirdParty/LimelightHelpers.java`, `docs/VISION_GUIDE.md`.
- [ ] `[PARTIAL]` `RobotState` estimator singleton + `FinanceDepartment`-style current tracking (needs current-draw owners from drive + mechanisms). 2026 lesson: 6328 pattern, see `docs/TEAM_COMPARISON.md`. **2026-10-07: holder exists (`Navigation/RobotState.java`, fed by `SwerveBase`); per-subsystem draw exists (`getSimulationCurrentDraw` + battery model in `Robot`). Still open: vision fusion into the estimate + FinanceDepartment-style budgeting.**

## D. Auto / teleop / test mode

- [ ] `[PARTIAL]` Auto actions: `WaitAction`/`WaitForBallAction`/`WaitUntilMarkerAction`/`ShootAction`/`IntakeAction`/`AutoAimAction`/`FollowChoreoPath`/`SeriesAction`/`ParallelAction`/`ParallelRaceAction`/`BranchAction`/`LambdaAction`. 2026 ref: `Auto/Actions/*`. **2026-10-07: `Interfaces/Action` + `WaitAction` + `SeriesAction` + `ActionMission` adapter exist (`ActionsTest`); game/parallel/branch variants still open.**
- [ ] `[PARTIAL]` Auto missions: `AutoMission` contract + `AutoMissionChooser` (dashboard + headless pin) + `MissionBase` thread lifecycle (`stop()` interrupts worker, `teleopInit` stops latent mission) + `Example`/`DepotShoot`/`Shooter`/`DynamicChoreo`/`AdvancedChoreo` patterns + Choreo pipeline + `AutoMissionExecutorTest`/`AutoMissionChooserTest`/`AutoEnhancementsTest`. 2026 ref: `Auto/Missions/*`, `Auto/AutoMission*.java`. **2026-10-07: executor + `MissionBase` + `DoNothing`/`DriveDistance` + names-chooser exist (`DriveDistanceMissionTest`); dashboard chooser variants, Choreo, and remaining missions still open.**
- [ ] `[OPEN]` Teleop assist: `AutonomousTeleopAgent` shared-authority (`updateSmartAssist` + `blendSpeeds` single-owner, breakout thresholds, arrival deactivation, hold-while-held anti-chatter) + settle/plant-and-fire gate + `DriverAssistTest`/`TunnelAndAssistanceTest`. 2026 ref: `Intelligence/AutonomousTeleopAgent.java`.
- [ ] `[OPEN]` Driver feedback: `Hardware/Controller.java` haptics (`TARGET_LOCKED`/`BALL_ACQUIRED`/`HARDWARE_WARNING`/`MATCH_TIME_WARNING`) + `Subsystems/LEDs.java` Blinkin states + `AlertManager` list-caching + `TeleopTest` + `ControllerHapticsTest`. 2026 ref: `Telemetry/AlertManager.java`, `Telemetry/Alert.java`.
- [ ] `[OPEN]` Test mode: `Test/TestMode.java` + `Test/Diagnostics.java` 15 s preflight (CAN audit, swerve pulse, steer check, intake profile, shooter ramp, vision link) + `Test/SysIdManager.java` (5 routines) + `Test/DriveCharacterization.java` delegation + `Test/ShooterTuning.java` + `Test/IntakeTesting.java` + `Test/VisionTesting.java` + `Test/README.md`. 2026 ref: `Test/*`, `SysIdManagerTest`, `DiagnosticsTest`, `ShooterTuningTest`, `IntakeTestingTest`.

## E. Intelligence — Jev brain (all as `Game/` consumers, post-kickoff)

- [ ] `[OPEN]` Core policy: `JevDecisionEngine.evaluatePolicy` (stateless, explicit no-information path — never fall through to a confident wrong action) + `WorldState`/`WorldStateBuilder` + `StrategicObjective`/`StrategicPlan` + `Archetype` + `AIActionIntent` + `ObjectiveCommitment` per-agent latch + `JevDecisionEngineTest`/`JevDecisionEngineModeTest`/`ObjectiveCommitmentTest`. 2026 ref: `Intelligence/JevDecisionEngine.java`, `Intelligence/WorldState*.java`, `Intelligence/Strategic*.java`, `Intelligence/Archetype.java`, `Intelligence/AIActionIntent.java`, `Intelligence/ObjectiveCommitment.java`.
- [ ] `[OPEN]` Knowledge tiers: `MatchKnowledge` sealed (`ClairvoyantKnowledge`/`ObservedKnowledge`) + per-tick `WorldStateBuilder` zone counts + roster/score/pose degradation alerts + `TierKnowledgeTest`. 2026 ref: `Intelligence/MatchKnowledge.java`, `ClairvoyantKnowledge.java`, `ObservedKnowledge.java`, `docs/KNOWLEDGE_MODEL.md`.
- [ ] `[OPEN]` Co-pilot + coach: `AutonomousTeleopAgent` `CO_PILOT` path + `MatchCoach` HUD + `TypeSafeJevClient` seam (opt-in, local fallback; credential/cap work deferred) + `MatchCoachTest`. 2026 ref: `Intelligence/MatchCoach.java`, `Intelligence/TypeSafeJevClient.java`, `tools/coaching/jev_coach.py`.
- [ ] `[OPEN]` Multi-robot coordination: central mark selection (`selectMarkExcluding`) + intent-sharing/zone/corridor deconfliction + per-ally loaded-scorer context (deadlock recovery is the v1 foundation). 2026 ref: `Sim/AIRobotSim.java:694-812`, `KNOWN_ISSUES.md` §G.

## F. Sim match engine (needs 2027 game impl to activate)

- [ ] `[OPEN]` `Sim/GameSim.java` scoring loop + `Sim/HubSchedule.java` pattern (phase/seed single-owner) + `Sim/MatchScoreTracker.java` (fuel/auto/teleop/climb/fouls, per-bot reconciliation residuals + unattributed canary) + `Sim/RefereeSim.java` pattern + `Sim/ShotTracker.java`. 2026 ref: `Sim/GameSim.java`, `Sim/HubSchedule.java`, `Sim/MatchScoreTracker.java`, `Sim/RefereeSim.java`, `HubScheduleTest`, `MatchScoreTrackerTest`, `RefereeSimTest`, `HubShiftShotAllowanceTest`.
- [ ] `[OPEN]` Multi-bot sim: `Sim/AIRobotSim.java` + `Sim/AIRobotInstance.java` (6 slots, archetype/pose/preload, settle gate, nav arbitration single-owner) + `Sim/TrainingMatchScenario.java` (scenario owns archetype) + `Sim/HeadlessMatchDriver.java` (DS sequence, wpilog, markdown + JSONL, `logTag` for both artifacts) + `AIRobotSimTest`/`AIRobotInstanceNavArbitrationTest`/`HeadlessMatchDriver*Test`/`TrainingMatchScenario*Test`. 2026 ref: `Sim/AIRobotSim.java`, `Sim/AIRobotInstance.java`.
- [ ] `[OPEN]` Determinism + metrics: `Sim/MatchDeterminism.java` CRN (named sub-streams, sorted iteration, no `Math.random()` in decision/sim paths) + `Sim/BotMatchMetrics.java` (archetype/path/stall/recoveries, `StallCause` taxonomy) + `Sim/LoopHealth.java` + `Sim/SimDashboardKeys.java` + `MatchDeterminismTest`/`BotMatchMetricsStallCauseTest`/`StallCauseTaxonomyTest`. 2026 ref: `Sim/MatchDeterminism.java`, `Sim/BotMatchMetrics.java`, `Sim/LoopHealth.java`.
- [ ] `[OPEN]` Score rig: `tools/score/` (`sweep.ps1` resumable variant×seed×replica, `compare.py` paired bootstrap + role-aware guardrails + frozen params, `DecisionCards.java` + `run-cards.ps1` + `decision_cards.tsv`, `dumpSimLaunch`) + `tools/nav/` (`verify_roadmap.py`, `sweep-seeds.ps1`) + `sweep` lock resource. 2026 ref: `tools/score/*`, `tools/nav/*`, `docs/SCORE_RIG_RESULTS.md`, `docs/SWEEP_FINDINGS.md`, `ARCHITECTURE.md` §3L.

## G. Telemetry / hardware / tooling / docs

- [ ] `[PARTIAL]` Telemetry: `AlertManager` + `Dashboard` (Elastic tabs) + `TunableNumber`/`LoggedTunableNumber` (with value setter + `PolicyWeights`-style seam) + AdvantageScope layouts + build/git metadata widgets + `DashboardTest`/`AlertManagerTest`. 2026 ref: `Telemetry/Dashboard.java`, `Telemetry/TunableNumber.java`, `Telemetry/AlertManager.java`. **2026-10-07: `Alert` is now group/text/type + self-registering, `AlertManager` publishes banner + severity tables on change and runs in `Robot.robotPeriodic` (`AlertManagerTest`). Dashboard/Tunables still open.**
- [ ] `[PARTIAL]` Hardware: `Hardware/PortMap.java` full IDs + `Hardware/Controller.java` passthrough (no deadband) + `Hardware/NeoSparkMaxMotor.java` (`optimizeCanBusUtilization`, smart current limits) + `Subsystems/HardwareIOTest` + `WPILibTricksEnhancementsTest`. 2026 ref: `Hardware/*`. **2026-10-07: `Controller` exists (passthrough sticks, debounced buttons, deduped rumble; patterns deferred to driver-feedback pass) and `Teleop` uses it; `SparkMaxMotor` covers the wrapper portion. Full IDs need hardware.**
- [ ] `[OPEN]` Tuning/docs: `tools/tune/tune.py` suite + `SHOOTER/INTAKE/SWERVE_TUNING_GUIDE.md` + `PIT_TUNING_CHECKLIST.md` + `SIMULATION_GUIDE.md` + `OPERATORS_GUIDE.md` + `ONBOARDING.md` + `AUTONOMOUS_GUIDE.md` + `RESOURCES.md` + `Test/README.md` (each new guide copies `docs/_TEMPLATE.md` + row in `docs/INDEX.md`). 2026 ref: `TitanRoboticsBuildSeason/docs/*`, `tools/tune/*`.

## H. Deliberately not ported (2026-specific or deferred)

- 2026 game geometry/rules (`Arena2026Rebuilt`, `RebuiltHub`/`RebuiltFuelOnField`, hub/ramp/trench/depot coordinates, 6.4 shift table, G407/G418/G420 values, `SimLaunchEfficiency`/ballistics jitter as tunable) — kickoff `GameDefinition` replaces them.
- `TypeSafeJevClient` live cloud + per-match spend cap + credential provisioning — seam only until scheduled.
- YOLOv8 NPU Ball Hunt, Idun offboard, bespoke dashboard replacement — no owner/need yet.
- Archived contaminated baselines/reports/JSONL v1 rows — never import; re-baseline clean.

## I. Wishlist / fun / might-want (post-foundation, prioritized latest)

Anytime (no robot needed) — docs-only or sim-only toys that pay back in morale or onboarding.

- [ ] `[OPEN]` Demo mode: choreographed LED + drive routine for outreach (figure-8, spin, hood wave) behind a dashboard button, never in match code path. 2026 ref: `Subsystems/LEDs.java` states.
- [ ] `[OPEN]` Motor music: play a short jingle through the flywheel/swerve audible hum for pit demos (gated behind `Utility`, volume-limited, never during matches).
- [ ] `[OPEN]` Photo mode: one-button pose (arm up, LEDs gold strobe, brake) for team pictures.
- [ ] `[OPEN]` New-member driving school: capped-speed teleop profile + cone-slalom auto drill + scoreboard. 2026 ref: `TrainingMatchScenario` pattern (§F).
- [ ] `[OPEN]` Pit tuning checklist automation: `tools/tune/tune.py` one-command pre-match sweep (mechanical clearance, sensor zero, 3-shot benchmark) printing PASS/FAIL. 2026 ref: `docs/PIT_TUNING_CHECKLIST.md`.

Driver experience (might-want, needs drive-team sign-off):

- [ ] `[OPEN]` Rumble language v2: distinct patterns for intake-acquired vs shot-ready vs endgame countdown vs assist-breakout. 2026 ref: `Hardware/Controller.java` haptics (§D).
- [ ] `[OPEN]` Driver-assist transparency HUD: current objective + why ("holding — hub dark 4 s") on Elastic/AdvantageScope. 2026 gap: `CoPilot/Objective` published but unexplained (§E).
- [ ] `[OPEN]` Wall-intake + trench-centering assists (snap-to-wall angle, auto-center in corridor). Seen on 581 2026; needs corridor geometry from §B first.
- [ ] `[OPEN]` Snake mode (auto-align heading to travel while intaking) + power manager (per-action current budgets + operator turbo override). Seen on 581 2026; needs `FinanceDepartment` budgeting (§C).

Competitive might-wants (post-kickoff, each needs a `GameDefinition` first):

- [ ] `[OPEN]` Shoot-on-the-move (velocity feedforward + time-of-flight recursion). Seen on 9032 2026; needs shot table (§C) + `RobotState` velocity (§B).
- [ ] `[OPEN]` Dynamic autos: pit-selected action list with real-time path generation (mirror/flip per alliance, replan on disruption). Seen on 9032/581 2026; needs Choreo + actions (§D).
- [ ] `[OPEN]` Fuel-cluster mapping: coprocessor aggregates detections into field poses, auto picks the richest lane. Seen on 581 2026 (Limelight SnapScript); needs vision (§C).
- [ ] `[OPEN]` Opportunistic subsumption ("2 things at once"): intake while staging/defending, homeward harvest bias, full-hopper/low-clearance gates. 2026 `[PARTIAL]` — pin-duration evidence never reached `WorldState`.
- [ ] `[OPEN]` 2-step horizon plan (`StrategicPlan` current/next + shift budget + harvest cutoff). 2026 `[PARTIAL]` — corridor broadcast + per-ally loaded-scorer screening open.
- [ ] `[OPEN]` Post-match LLM log review per bot (actions → suggested changes). 2026 `[PARTIAL]` — `jev_coach.py --report` exists, per-bot analysis missing.
- [ ] `[OPEN]` AI-assisted scouting: hybrid human-trace + auto ball/shot attribution (full-auto not reliable per 334 2026 — start narrow). Research brief §5.
- [ ] `[OPEN]` Headless scripted player (joystick-scripted opponent) so fitness measures our driving, not Jev-vs-Jev. 2026 note: G418 goes live + baselines break across the change.

Engineering craft (offseason-friendly):

- [ ] `[OPEN]` Camera offset calibrator: spin-in-place + least-squares solve for robot-to-camera transforms. Seen on 4533 2026 (`offset_finder.py`); kills the 1 cm CAD-offset drift class.
- [ ] `[OPEN]` Zero-allocation vision transport (UDP + ring buffer + cursor reads, no `new` in hot loop). Seen on 4533 Whacknet; only if NT latency ever binds us.
- [ ] `[OPEN]` AI code-review layer (checklist in repo, e.g. Blue-origin/single-owner/no-`Math.random()` rules) + sim-crash CI gate. Seen on 360 2026 (~35 bugs caught); fits our lock protocol.
- [ ] `[OPEN]` AdvantageScope Lite + SFTP log download + DS live mode evaluation at SystemCore bring-up. Research brief §4.
- [ ] `[OPEN]` Commands v3 coroutine autos + `@Autonomous`/`@Teleop`/`@Utility` OpMode structure evaluation once the beta lands (no `RobotContainer`). Research brief §1; decision already tracked in §A item 4.
