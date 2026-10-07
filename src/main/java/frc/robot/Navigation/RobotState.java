package frc.robot.Navigation;

import org.wpilib.math.geometry.Pose2d;
import org.littletonrobotics.junction.Logger;

/**
 * Central estimated-pose holder (6328 {@code RobotState} pattern, minimal).
 * Today it mirrors the drive odometry pose; vision fusion writes corrected
 * poses here later. Consumers read the estimate — never a subsystem directly.
 */
public final class RobotState {
  private static RobotState instance = null;

  private Pose2d estimatedPose = new Pose2d();

  private RobotState() {}

  public static synchronized RobotState getInstance() {
    if (instance == null) {
      instance = new RobotState();
    }
    return instance;
  }

  /** Test-only reset. */
  static void clearForTest() {
    instance = null;
  }

  public synchronized void setDrivePose(Pose2d pose) {
    if (pose != null) {
      estimatedPose = pose;
      Logger.recordOutput("RobotState/EstimatedPose", estimatedPose);
    }
  }

  public synchronized Pose2d getEstimatedPose() {
    return estimatedPose;
  }

  public synchronized void reset(Pose2d pose) {
    estimatedPose = pose != null ? pose : new Pose2d();
  }
}
