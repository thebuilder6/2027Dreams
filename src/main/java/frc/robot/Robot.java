// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Auto.AutoMissionExecutor;
import frc.robot.Auto.MissionBase;
import frc.robot.Auto.Missions.DoNothingMission;
import frc.robot.Data.Constants;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.Vision;
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
  private final Teleop teleop = new Teleop();
  private final AutoMissionExecutor autoExecutor = new AutoMissionExecutor();
  private final SendableChooser<MissionBase> autoChooser = new SendableChooser<>();
  private final SwerveBase swerveBase = SwerveBase.getInstance();
  private final Vision vision = Vision.getInstance();

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

    DriverStation.silenceJoystickConnectionWarning(true);
    autoChooser.setDefaultOption("Do Nothing", new DoNothingMission());
    SmartDashboard.putData("Auto Mission", autoChooser);
  }

  @Override
  public void robotInit() {
    SubsystemManager.initializeSubsystems();
  }

  @Override
  public void robotPeriodic() {
    SubsystemManager.updateSubsystems();
    SubsystemManager.logSubsystems();
  }

  @Override
  public void autonomousInit() {
    MissionBase selected = autoChooser.getSelected();
    if (selected == null) {
      selected = new DoNothingMission();
    }
    autoExecutor.start(selected);
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    autoExecutor.stop();
    teleop.init();
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {
    autoExecutor.stop();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

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
        edu.wpi.first.wpilibj.simulation.BatterySim.calculateDefaultBatteryLoadedVoltage(
            totalCurrentDraw);
    edu.wpi.first.wpilibj.simulation.RoboRioSim.setVInVoltage(loadedVoltage);
  }
}
