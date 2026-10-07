package frc.robot.Subsystems;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.ChassisVelocities;

/**
 * Minimal drive contract: everything {@code Teleop} and autonomous missions
 * need, nothing YAGSL-specific. The YAGSL {@code SwerveBase} (in
 * {@code attic/yagsl-drive} until YAGSL-2027 lands) implements this on return,
 * as will any future vendor-free swerve.
 */
public interface DriveControl {
  /** Closed-loop velocity drive. */
  void drive(Translation2d translation, double rotationRadS, boolean fieldRelative);

  void driveFieldOriented(ChassisVelocities speeds);

  void stop();

  /** Re-zeroes the gyro to the alliance-forward heading (0° Blue / 180° Red). */
  void zeroGyroWithAlliance();

  Pose2d getPose();

  Rotation2d getHeading();
}
