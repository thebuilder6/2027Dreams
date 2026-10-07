package frc.robot.Subsystems.swerve;

import org.littletonrobotics.junction.AutoLog;

/**
 * Gyro IO contract: yaw + yaw rate only. Sim integrates the commanded chassis
 * rate; real is disconnected until the chassis IMU lands (SystemCore onboard
 * IMU or CAN gyro — see {@code docs/SWERVE_SETUP.md}).
 */
public interface SwerveGyroIO {

  @AutoLog
  public static class SwerveGyroIOInputs {
    public boolean connected = false;
    public double yawDeg = 0.0;
    public double yawVelocityDegPerSec = 0.0;
  }

  public default void updateInputs(SwerveGyroIOInputs inputs) {}

  /** Re-zeroes the yaw reading. No-op unless the impl honors it. */
  public default void setYawDeg(double yawDeg) {}

  /**
   * Feeds the true chassis rate (sim only). Lets the sim gyro stay consistent
   * with commanded motion without the drive reaching into the impl.
   */
  public default void setYawVelocityDegPerSec(double yawVelocityDegPerSec) {}
}
