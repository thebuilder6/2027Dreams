package frc.robot.Data;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.util.Units;
import org.wpilib.framework.RobotBase;

/**
 * Robot-wide constants. Game numbers live in {@code Game/}, never here.
 * Follows 6328: mode includes REPLAY, and a pull-request guard fails CI when
 * tuning is left on.
 */
public final class Constants {
  private Constants() {}

  public enum Mode {
    /** Running on a real robot. */
    REAL,
    /** Running in desktop simulation. */
    SIM,
    /** Replaying from a log file (launch with {@code -Dfrc.replay=true}). */
    REPLAY
  }

  /** Set with {@code -Dfrc.replay=true} by the replay launcher. */
  public static Mode getMode() {
    if (Boolean.getBoolean("frc.replay")) {
      return Mode.REPLAY;
    }
    return RobotBase.isReal() ? Mode.REAL : Mode.SIM;
  }

  /** Set true to expose tuning controls on the dashboard. Gated, never default-on. */
  public static final boolean TUNING_MODE = false;

  /** Main loop period. 6328 runs 5 ms; we stay at 20 ms until odometry needs more. */
  public static final double LOOP_PERIOD_SECS = 0.02;

  public static final double MAX_SPEED = Units.feetToMeters(15); // ~4.57 m/s
  public static final double MAX_ROTATION_SPEED = 8.0; // rad/s

  public static final class OperatorConstants {
    private OperatorConstants() {}

    public static final double TRANSLATION_SLEW_RATE = 16.0; // m/s^2
    public static final double ROTATION_SLEW_RATE = 10.0; // rad/s^2
    public static final double TRANSLATION_DEADBAND = 0.08; // circular, see Teleop
    public static final double ROTATION_DEADBAND = 0.06;
  }

  public static final class AutonConstants {
    private AutonConstants() {}

    // Placeholders — tune on a real chassis (see docs/SWERVE_SETUP.md).
    public static final double AUTO_DRIVE_KP = 10.0;
    public static final double AUTO_TURN_KP = 7.5;
  }

  /**
   * Temporary vendor-free swerve geometry + plant. Every number is a
   * placeholder until the 2027 chassis is measured — measure, don't tune
   * around, wrong geometry. YAGSL owns these again on return.
   */
  public static final class SwerveConstants {
    private SwerveConstants() {}

    public static final double TRACK_WIDTH_M = 0.55;
    public static final double WHEEL_BASE_M = 0.55;
    public static final double WHEEL_RADIUS_M = 0.0508;
    public static final double DRIVE_GEARING = 6.0;
    public static final double DRIVE_INERTIA_KG_M2 = 0.004;
    public static final double STEER_GEARING = 12.0;
    public static final double STEER_INERTIA_KG_M2 = 0.0005;
    /** Open-loop steer P (volts per degree of wrapped error). */
    public static final double STEER_KP_VOLTS_PER_DEG = 0.12;
    public static final int DRIVE_CURRENT_LIMIT_AMPS = 40;

    /** Module order everywhere: FL, FR, BL, BR. */
    public static Translation2d[] modulePositions() {
      double hx = WHEEL_BASE_M / 2.0;
      double hy = TRACK_WIDTH_M / 2.0;
      return new Translation2d[] {
        new Translation2d(hx, hy),
        new Translation2d(hx, -hy),
        new Translation2d(-hx, hy),
        new Translation2d(-hx, -hy)
      };
    }
  }

  /** Fails CI when tuning is left on. Run: {@code java -cp <jar> frc.robot.Data.Constants$CheckPullRequest}. */
  public static class CheckPullRequest {
    public static void main(String... args) {
      if (TUNING_MODE) {
        System.err.println("Do not merge, tuning mode is enabled (set Constants.TUNING_MODE = false).");
        System.exit(1);
      }
    }
  }
}
