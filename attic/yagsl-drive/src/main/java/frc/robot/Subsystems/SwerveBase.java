package frc.robot.Subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Data.Constants;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Navigation.FieldMap;
import frc.robot.Navigation.RobotState;
import frc.robot.Subsystems.drive.DriveIO;
import frc.robot.Subsystems.drive.DriveIOInputsAutoLogged;
import frc.robot.Subsystems.drive.DriveIOSim;
import frc.robot.Subsystems.drive.DriveIOSparkMax;
import frc.robot.Telemetry.LoggedTracer;
import java.io.File;
import org.littletonrobotics.junction.Logger;
import swervelib.SwerveDrive;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

/**
 * Generic YAGSL swerve wrapper. Owns the {@code SwerveDrive} parser output and
 * nothing else — path following, vision fusion, and watchdogs plug in later
 * without touching this file's public API.
 *
 * <p>Public API: {@code drive / driveFieldOriented / stop / zeroGyroWithAlliance
 * / getPose / getHeading}. Everything else stays inside.
 */
public class SwerveBase implements Subsystem {
  private static SwerveBase instance = null;

  private final DriveIO io;
  private final DriveIOInputsAutoLogged inputs = new DriveIOInputsAutoLogged();
  private final SwerveDrive swerveDrive;
  private final Field2d field = new Field2d();

  public static synchronized SwerveBase getInstance() {
    if (instance == null) {
      instance = new SwerveBase();
    }
    return instance;
  }

  public SwerveBase() {
    SwerveDrive drive = createSwerveDrive();
    this.swerveDrive = drive;
    this.io =
        RobotBase.isSimulation() ? new DriveIOSim(drive) : new DriveIOSparkMax(drive);
    SubsystemManager.registerSubsystem(this);
  }

  private static SwerveDrive createSwerveDrive() {
    boolean blue =
        DriverStation.getAlliance()
            .map(alliance -> alliance == DriverStation.Alliance.Blue)
            .orElse(true);
    Pose2d startingPose =
        blue
            ? new Pose2d(1.0, 4.0, Rotation2d.fromDegrees(0))
            : new Pose2d(FieldMap.fieldLength() - 1.0, 4.0, Rotation2d.fromDegrees(180));
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.NONE;
    try {
      SwerveDrive drive =
          new SwerveParser(new File(Filesystem.getDeployDirectory(), "swerve"))
              .createSwerveDrive(Constants.MAX_SPEED, startingPose);
      drive.setHeadingCorrection(true);
      drive.setCosineCompensator(!SwerveDriveTelemetry.isSimulation);
      drive.setAngularVelocityCompensation(true, true, 0.1);
      drive.setModuleEncoderAutoSynchronize(false, 1);
      return drive;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse deploy/swerve — see docs/SWERVE_SETUP.md", e);
    }
  }

  /** Closed-loop velocity drive. {@code false} = never open-loop percent. */
  public void drive(Translation2d translation, double rotationRadS, boolean fieldRelative) {
    swerveDrive.drive(translation, rotationRadS, fieldRelative, false);
  }

  public void driveFieldOriented(ChassisSpeeds speeds) {
    swerveDrive.driveFieldOriented(speeds);
  }

  public void stop() {
    swerveDrive.drive(new ChassisSpeeds());
  }

  /** Re-zeroes the gyro to the alliance-forward heading (0° Blue / 180° Red). */
  public void zeroGyroWithAlliance() {
    boolean blue =
        DriverStation.getAlliance()
            .map(alliance -> alliance == DriverStation.Alliance.Blue)
            .orElse(true);
    Rotation2d heading = blue ? new Rotation2d() : Rotation2d.fromDegrees(180);
    io.zeroGyro();
    io.setPose(new Pose2d(getPose().getTranslation(), heading));
  }

  public Pose2d getPose() {
    return inputs.odometryPose;
  }

  public Rotation2d getHeading() {
    return Rotation2d.fromDegrees(inputs.gyroYawDeg);
  }

  @Override
  public void update() {
    LoggedTracer.reset();
    io.updateInputs(inputs);
    Logger.processInputs("SwerveBase", inputs);
    RobotState.getInstance().setDrivePose(inputs.odometryPose);
    LoggedTracer.record("SwerveBase/Inputs");
  }

  @Override
  public void simulationUpdate() {}

  @Override
  public double getSimulationCurrentDraw() {
    double current = 0.0;
    for (int i = 0; i < 4; i++) {
      current += Math.abs(inputs.driveCurrentAmps[i]) + Math.abs(inputs.steerCurrentAmps[i]);
    }
    return current;
  }

  @Override
  public void initialize() {}

  @Override
  public void log() {
    field.setRobotPose(getPose());
    SmartDashboard.putData("Field", field);
    Logger.recordOutput("SwerveBase/Pose", getPose());
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "SwerveBase";
  }
}
