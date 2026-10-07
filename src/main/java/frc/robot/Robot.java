// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.wpilib.telemetry.Telemetry;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;
import frc.robot.Auto.AutoMissionExecutor;
import frc.robot.Auto.MissionBase;
import frc.robot.Auto.Missions.DoNothingMission;
import frc.robot.Auto.Missions.DriveDistanceMission;
import frc.robot.Data.Constants;
import frc.robot.Data.Constants.SwerveConstants;
import frc.robot.Hardware.PortMap;
import frc.robot.Subsystems.DriveControl;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Subsystems.SwerveDrive;
import frc.robot.Subsystems.swerve.SwerveGyroIO;
import frc.robot.Subsystems.swerve.SwerveGyroIOSim;
import frc.robot.Subsystems.swerve.SwerveModuleIO;
import frc.robot.Subsystems.swerve.SwerveModuleIOSim;
import frc.robot.Subsystems.swerve.SwerveModuleIOSparkMax;
import frc.robot.Subsystems.Vision;
import frc.robot.Telemetry.AlertManager;
import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

/**
 * Generic 2027 base. Extends AdvantageKit {@code LoggedRobot} from day one
 * (6328 pattern): every input flows through a loggable IO layer so matches
 * replay exactly. Control logic never depends on the logger — it only reads
 * the cached inputs object.
 */
public class Robot extends LoggedRobot {
  private final DriveControl swerveBase = createDrive();
  private final Vision vision = Vision.getInstance();
  private final Teleop teleop = new Teleop(swerveBase);
  private final AutoMissionExecutor autoExecutor = new AutoMissionExecutor();
  private final Selectable<String> autoChooser = new Selectable<>();
  private static final String AUTO_DO_NOTHING = "Do Nothing";
  private static final String AUTO_DRIVE_FORWARD = "Drive Forward 2m";

  public Robot() {
    super(Constants.LOOP_PERIOD_SECS);
    Logger.recordMetadata("ProjectName", BuildConstants.ROBOT_NAME);
    Logger.recordMetadata("GitSHA", BuildConstants.GIT_SHA);
    Logger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH);
    Logger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE);
    Logger.recordMetadata("GitDirty", BuildConstants.DIRTY == 1 ? "true" : "false");

    switch (Constants.getMode()) {
      case REAL:
        Logger.addDataReceiver(new WPILOGWriter());
        Logger.addDataReceiver(new NT4Publisher());
        break;
      case SIM:
        Logger.addDataReceiver(new NT4Publisher());
        break;
      case REPLAY:
        setUseTiming(false);
        String logPath = LogFileUtil.findReplayLog();
        Logger.setReplaySource(new WPILOGReader(logPath));
        Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim")));
        break;
    }
    Logger.start();

    // Joystick-connection warnings are automatic persistent alerts in 2027 —
    // no silencing call remains.
    autoChooser.addDefault(AUTO_DO_NOTHING, AUTO_DO_NOTHING);
    autoChooser.add(AUTO_DRIVE_FORWARD, AUTO_DRIVE_FORWARD);
    autoChooser.publishTunable(Tunables.getTable("Auto"));
    Telemetry.log("Auto/Selected", AUTO_DO_NOTHING);
    // No robotInit() in 2027: one-time init runs here in the constructor.
    SubsystemManager.initializeSubsystems();
  }

  /** Drive factory: sim twins off-hardware, SparkMax + stub gyro on real. */
  private static DriveControl createDrive() {
    if (Constants.getMode() == Constants.Mode.REAL) {
      return new SwerveDrive(
          new SwerveModuleIO[] {
            new SwerveModuleIOSparkMax(
                PortMap.RESERVED_DRIVE_FL,
                PortMap.RESERVED_STEER_FL,
                false,
                SwerveConstants.DRIVE_CURRENT_LIMIT_AMPS),
            new SwerveModuleIOSparkMax(
                PortMap.RESERVED_DRIVE_FR,
                PortMap.RESERVED_STEER_FR,
                false,
                SwerveConstants.DRIVE_CURRENT_LIMIT_AMPS),
            new SwerveModuleIOSparkMax(
                PortMap.RESERVED_DRIVE_BL,
                PortMap.RESERVED_STEER_BL,
                false,
                SwerveConstants.DRIVE_CURRENT_LIMIT_AMPS),
            new SwerveModuleIOSparkMax(
                PortMap.RESERVED_DRIVE_BR,
                PortMap.RESERVED_STEER_BR,
                false,
                SwerveConstants.DRIVE_CURRENT_LIMIT_AMPS)
          },
          // No chassis IMU yet: disconnected stub reports origin-only heading.
          new SwerveGyroIO() {},
          SwerveConstants.modulePositions());
    }
    return new SwerveDrive(
        new SwerveModuleIO[] {
          new SwerveModuleIOSim(), new SwerveModuleIOSim(),
          new SwerveModuleIOSim(), new SwerveModuleIOSim()
        },
        new SwerveGyroIOSim(),
        SwerveConstants.modulePositions());
  }

  /** Fresh mission per selection — chooser holds names, never stateful instances. */
  MissionBase getAutoMissionForParams(String name) {
    if (AUTO_DRIVE_FORWARD.equals(name)) {
      return new DriveDistanceMission(
          swerveBase::getPose, swerveBase::driveFieldOriented, 2.0, 5.0);
    }
    return new DoNothingMission();
  }

  @Override
  public void robotPeriodic() {
    SubsystemManager.updateSubsystems();
    SubsystemManager.logSubsystems();
    AlertManager.update();
  }

  @Override
  public void autonomousInit() {
    autoExecutor.start(getAutoMissionForParams(autoChooser.getSelected()));
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    autoExecutor.stop();
    teleop.init();
  }

  @Override
  public void teleopPeriodic() {
    teleop.teleopPeriodic();
  }

  @Override
  public void disabledInit() {
    autoExecutor.stop();
    swerveBase.stop();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void utilityInit() {}

  @Override
  public void utilityPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {
    SubsystemManager.simulationUpdateSubsystems();
    double totalCurrentDraw = 0.0;
    for (frc.robot.Interfaces.Subsystem subsystem : SubsystemManager.getSubsystems()) {
      totalCurrentDraw += subsystem.getSimulationCurrentDraw();
    }
    double loadedVoltage =
        org.wpilib.simulation.BatterySim.calculateDefaultBatteryLoadedVoltage(
            totalCurrentDraw);
    org.wpilib.simulation.RoboRioSim.setVInVoltage(loadedVoltage);
  }
}
