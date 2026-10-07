package frc.robot.Subsystems;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import frc.robot.Telemetry.Alert;
import frc.robot.Telemetry.Alert.AlertType;

/**
 * Explicit pending state: no drive implementation is configured (YAGSL has no
 * 2027 release yet — see {@code attic/yagsl-drive/README.md}). Commands are
 * ignored, pose reads origin. Named for what it is so nobody mistakes it for
 * a working drivebase. Raises one latched alert on first use.
 */
public final class UnconfiguredDrive implements DriveControl {
  private final Alert unconfiguredAlert =
      new Alert("Drive", "Unconfigured: YAGSL-2027 pending", AlertType.WARNING);
  private boolean warned = false;

  private void warnOnce() {
    if (!warned) {
      warned = true;
      unconfiguredAlert.set(true);
    }
  }

  @Override
  public void drive(Translation2d translation, double rotationRadS, boolean fieldRelative) {
    warnOnce();
  }

  @Override
  public void driveFieldOriented(ChassisVelocities speeds) {
    warnOnce();
  }

  @Override
  public void stop() {}

  @Override
  public void zeroGyroWithAlliance() {
    warnOnce();
  }

  @Override
  public Pose2d getPose() {
    return new Pose2d();
  }

  @Override
  public Rotation2d getHeading() {
    return new Rotation2d();
  }
}
