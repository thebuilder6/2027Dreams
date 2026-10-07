package frc.robot.Subsystems;

import frc.robot.Data.Constants;
import frc.robot.Data.Constants.SwerveConstants;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Navigation.FieldMap;
import frc.robot.Subsystems.swerve.SwerveGyroIO;
import frc.robot.Subsystems.swerve.SwerveGyroIOInputsAutoLogged;
import frc.robot.Subsystems.swerve.SwerveModuleIO;
import frc.robot.Subsystems.swerve.SwerveModuleIOInputsAutoLogged;
import frc.robot.Subsystems.swerve.SwerveModuleIO.SwerveModuleIOOutputs;
import frc.robot.Telemetry.Alert;
import frc.robot.Telemetry.Alert.AlertType;
import frc.robot.Telemetry.LoggedTracer;
import frc.robot.Utils.AllianceFlipUtil;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveDriveKinematics;
import org.wpilib.math.kinematics.SwerveDriveOdometry;
import org.wpilib.math.kinematics.SwerveModulePosition;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.math.system.DCMotor;
import org.wpilib.math.util.Units;
import org.littletonrobotics.junction.Logger;

/**
 * Temporary vendor-free swerve (WPILib kinematics + ModuleIO, 6328 pattern).
 * Owns odometry and the open-loop control law: kV feedforward on drive,
 * P control on steer. No slip model, no closed-loop wheel control — honest
 * scaffold limits documented in {@code docs/SWERVE_SETUP.md}. YAGSL replaces
 * this whole file on return; {@code Teleop} and missions only see
 * {@link DriveControl}.
 *
 * <p>Module order everywhere: FL, FR, BL, BR.
 */
public class SwerveDrive implements DriveControl, Subsystem {
  private static final int MODULE_COUNT = 4;

  private final SwerveModuleIO[] modules;
  private final SwerveGyroIO gyro;
  private final SwerveModuleIOInputsAutoLogged[] moduleInputs =
      new SwerveModuleIOInputsAutoLogged[MODULE_COUNT];
  private final SwerveGyroIOInputsAutoLogged gyroInputs = new SwerveGyroIOInputsAutoLogged();
  private final SwerveModuleIOOutputs[] moduleOutputs = new SwerveModuleIOOutputs[MODULE_COUNT];

  private final SwerveDriveKinematics kinematics;
  private final SwerveDriveOdometry odometry;
  private final double voltsPerMeterPerSec;
  private final Alert disconnectedAlert =
      new Alert("Drive", "Module or gyro disconnected", AlertType.WARNING);

  private ChassisVelocities desired = new ChassisVelocities();
  private boolean desiredFieldRelative = true;
  private Pose2d pose;
  private SwerveModulePosition[] lastPositions;

  public SwerveDrive(SwerveModuleIO[] modules, SwerveGyroIO gyro, Translation2d[] positions) {
    if (modules == null || modules.length != MODULE_COUNT
        || positions == null || positions.length != MODULE_COUNT) {
      throw new IllegalArgumentException("SwerveDrive needs exactly 4 modules and positions");
    }
    this.modules = modules.clone();
    this.gyro = gyro;
    for (int i = 0; i < MODULE_COUNT; i++) {
      moduleInputs[i] = new SwerveModuleIOInputsAutoLogged();
      moduleOutputs[i] = new SwerveModuleIOOutputs();
    }
    kinematics = new SwerveDriveKinematics(positions);
    lastPositions = new SwerveModulePosition[MODULE_COUNT];
    for (int i = 0; i < MODULE_COUNT; i++) {
      lastPositions[i] = new SwerveModulePosition();
    }
    pose = startPose();
    odometry = new SwerveDriveOdometry(kinematics, pose.getRotation(), lastPositions, pose);
    // Steady-state feedforward matched to the sim plant (same gearing):
    // V = mps * G / (Kv * R). Open-loop — characterization lands with hardware.
    voltsPerMeterPerSec =
        SwerveConstants.DRIVE_GEARING / (DCMotor.getNEO(1).Kv * SwerveConstants.WHEEL_RADIUS_M);
    SubsystemManager.registerSubsystem(this);
  }

  private static Pose2d startPose() {
    boolean red = AllianceFlipUtil.isRedAlliance();
    double x = red ? FieldMap.fieldLength() - 1.0 : 1.0;
    Rotation2d heading = red ? Rotation2d.fromDegrees(180) : new Rotation2d();
    return new Pose2d(x, 4.0, heading);
  }

  @Override
  public void drive(Translation2d translation, double rotationRadS, boolean fieldRelative) {
    desired = new ChassisVelocities(translation.getX(), translation.getY(), rotationRadS);
    desiredFieldRelative = fieldRelative;
  }

  @Override
  public void driveFieldOriented(ChassisVelocities speeds) {
    desired = speeds;
    desiredFieldRelative = true;
  }

  @Override
  public void stop() {
    desired = new ChassisVelocities();
    desiredFieldRelative = false;
  }

  @Override
  public void zeroGyroWithAlliance() {
    boolean red = AllianceFlipUtil.isRedAlliance();
    Rotation2d heading = red ? Rotation2d.fromDegrees(180) : new Rotation2d();
    gyro.setYawDeg(heading.getDegrees());
    pose = new Pose2d(pose.getTranslation(), heading);
    odometry.resetPosition(heading, lastPositions, pose);
  }

  @Override
  public Pose2d getPose() {
    return pose;
  }

  @Override
  public Rotation2d getHeading() {
    if (gyroInputs.connected) {
      return Rotation2d.fromDegrees(gyroInputs.yawDeg);
    }
    return pose.getRotation();
  }

  @Override
  public void update() {
    LoggedTracer.reset();
    boolean allConnected = true;
    for (int i = 0; i < MODULE_COUNT; i++) {
      modules[i].updateInputs(moduleInputs[i]);
      Logger.processInputs("SwerveDrive/Module" + i, moduleInputs[i]);
      allConnected &= moduleInputs[i].connected;
    }
    gyro.updateInputs(gyroInputs);
    Logger.processInputs("SwerveDrive/Gyro", gyroInputs);
    LoggedTracer.record("SwerveDrive/Inputs");
    disconnectedAlert.set(!allConnected || !gyroInputs.connected);

    Rotation2d yaw = getHeading();
    for (int i = 0; i < MODULE_COUNT; i++) {
      lastPositions[i] =
          new SwerveModulePosition(
              moduleInputs[i].drivePositionRads * SwerveConstants.WHEEL_RADIUS_M,
              Rotation2d.fromDegrees(moduleInputs[i].steerPositionDeg));
    }
    pose = odometry.update(yaw, lastPositions);

    ChassisVelocities robotRelative =
        desiredFieldRelative ? desired.toRobotRelative(yaw) : desired;
    SwerveModuleVelocity[] states = kinematics.toSwerveModuleVelocities(robotRelative);
    states = SwerveDriveKinematics.desaturateWheelVelocities(states, Constants.MAX_SPEED);
    for (int i = 0; i < MODULE_COUNT; i++) {
      Rotation2d current = Rotation2d.fromDegrees(moduleInputs[i].steerPositionDeg);
      SwerveModuleVelocity optimized = states[i].optimize(current);
      double steerErrorDeg = optimized.angle.minus(current).getDegrees();
      moduleOutputs[i].mode = SwerveModuleIO.SwerveModuleIOOutputMode.VOLTAGE;
      moduleOutputs[i].driveVolts = optimized.velocity * voltsPerMeterPerSec;
      moduleOutputs[i].steerVolts = steerErrorDeg * SwerveConstants.STEER_KP_VOLTS_PER_DEG;
      modules[i].applyOutputs(moduleOutputs[i]);
    }
    // Sim gyro follows the commanded chassis rate (see SwerveGyroIO).
    gyro.setYawVelocityDegPerSec(Units.radiansToDegrees(desired.omega));
  }

  @Override
  public void simulationUpdate() {}

  @Override
  public double getSimulationCurrentDraw() {
    double current = 0.0;
    for (int i = 0; i < MODULE_COUNT; i++) {
      current +=
          Math.abs(moduleInputs[i].driveCurrentAmps) + Math.abs(moduleInputs[i].steerCurrentAmps);
    }
    return current;
  }

  @Override
  public void initialize() {
    stop();
  }

  @Override
  public void log() {
    Logger.recordOutput("SwerveDrive/Pose", pose);
    Logger.recordOutput("SwerveDrive/DesiredOmega", desired.omega);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "SwerveDrive";
  }
}
