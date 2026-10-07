package frc.robot.Subsystems.vision;

import org.wpilib.math.geometry.Pose2d;
import org.littletonrobotics.junction.AutoLog;

/**
 * IO contract for one vision camera (Limelight or PhotonVision coprocessor —
 * the estimator doesn't care). Input-only: there are no outputs to command.
 *
 * <p>Poses are WPILib estimates in Blue-origin field coordinates. The
 * subsystem owns staleness policy; IO just reports what the camera said.
 */
public interface VisionIO {

  @AutoLog
  public static class VisionIOInputs {
    public boolean connected = false;
    public boolean hasTarget = false;
    /** Field-relative pose estimate at observation time. */
    public Pose2d estimatedPose = new Pose2d();
    /** FPGA timestamp of the observation, seconds. */
    public double observationTimeSecs = 0.0;
    /** Pipeline + network latency, seconds. */
    public double latencySecs = 0.0;
    public int tagCount = 0;
    public double avgTagDistanceMeters = 0.0;
  }

  public default void updateInputs(VisionIOInputs inputs) {}
}
