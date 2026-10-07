package frc.robot.Data;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotBase;

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

    public static final double AUTO_DRIVE_KP = 10.0;
    public static final double AUTO_TURN_KP = 7.5;
  }

  /** Fails CI when tuning is left on. Run: {@code java -cp <jar> frc.robot.Data.Constants$CheckPullRequest}. */
  public static class CheckPullRequest {
    public static void main(String... args) {
      if (TUNING_MODE) {
        System.err.println("Do not merge, tuning mode is enabled.");
        System.exit(1);
      }
    }
  }
}
