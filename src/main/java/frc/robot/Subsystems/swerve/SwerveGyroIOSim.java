package frc.robot.Subsystems.swerve;

/**
 * Desktop-sim gyro: integrates the chassis rate fed by the drive each cycle.
 * Self-consistent with commanded motion by construction — it measures what
 * was commanded, not physics. Yaw wraps at ±180° for readability; consumers
 * use {@code Rotation2d}, which accepts any range.
 */
public class SwerveGyroIOSim implements SwerveGyroIO {
  public static final double LOOP_PERIOD_SECS = 0.02;

  private double yawDeg = 0.0;
  private double yawVelocityDegPerSec = 0.0;

  @Override
  public void updateInputs(SwerveGyroIOInputs inputs) {
    yawDeg += yawVelocityDegPerSec * LOOP_PERIOD_SECS;
    yawDeg = wrapDegrees(yawDeg);
    inputs.connected = true;
    inputs.yawDeg = yawDeg;
    inputs.yawVelocityDegPerSec = yawVelocityDegPerSec;
  }

  @Override
  public void setYawDeg(double yawDeg) {
    this.yawDeg = wrapDegrees(yawDeg);
  }

  @Override
  public void setYawVelocityDegPerSec(double yawVelocityDegPerSec) {
    this.yawVelocityDegPerSec = yawVelocityDegPerSec;
  }

  private static double wrapDegrees(double degrees) {
    double wrapped = degrees % 360.0;
    if (wrapped > 180.0) {
      wrapped -= 360.0;
    } else if (wrapped < -180.0) {
      wrapped += 360.0;
    }
    return wrapped;
  }
}
