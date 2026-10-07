package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Data.Constants;
import frc.robot.Hardware.Controller;
import frc.robot.Hardware.PortMap;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Utils.AllianceFlipUtil;
import frc.robot.Utils.Vector2dSlewRateLimiter;

/**
 * Driver/operator input. Owns controllers + input cleanup chain. Subsystem
 * calls plug in here as mechanisms land. Order matters: circular deadband →
 * cubic shape → slew → scale → Red flip.
 *
 * <p>Bindings (driver): left stick drive, right stick rotate, left-stick-click
 * toggles slow mode, double-tap A re-zeroes the gyro, Back+Start e-stops.
 */
public final class Teleop {
  public static final double TRANSLATION_DEADBAND = Constants.OperatorConstants.TRANSLATION_DEADBAND;
  public static final double ROTATION_DEADBAND = Constants.OperatorConstants.ROTATION_DEADBAND;
  public static final double SLOW_TRANSLATION_SCALE = 0.35;
  public static final double SLOW_ROTATION_SCALE = 0.50;
  private static final double REZERO_DOUBLE_TAP_SECS = 0.4;
  /** Cubic blend: heavy center precision, still 100% at full throw. */
  private static final double SHAPE_CUBIC_WEIGHT = 0.7;
  private static final double SHAPE_LINEAR_WEIGHT = 0.3;

  private final Controller driver = new Controller(PortMap.DRIVER_CONTROLLER);
  private final Controller operator = new Controller(PortMap.OPERATOR_CONTROLLER);
  private final SwerveBase swerve;

  private final Vector2dSlewRateLimiter transLimiter =
      new Vector2dSlewRateLimiter(Constants.OperatorConstants.TRANSLATION_SLEW_RATE);
  private final SlewRateLimiter rotLimiter =
      new SlewRateLimiter(Constants.OperatorConstants.ROTATION_SLEW_RATE);

  private boolean slowMode = false;
  private double lastAPressSecs = -1.0;

  public Teleop(SwerveBase swerve) {
    this.swerve = swerve;
  }

  /** Cubic: precision at center, 100% at full throw. */
  public static double shapeInput(double x) {
    double a = Math.abs(x);
    return Math.signum(x) * (SHAPE_CUBIC_WEIGHT * a * a * a + SHAPE_LINEAR_WEIGHT * a);
  }

  /** Circular (not square) translation deadband + shape, angle preserved. */
  public static Translation2d shapeTranslation(double x, double y) {
    double mag = Math.hypot(x, y);
    if (mag < TRANSLATION_DEADBAND) {
      return new Translation2d();
    }
    double scaled = shapeInput(Math.min(1.0, (mag - TRANSLATION_DEADBAND) / (1.0 - TRANSLATION_DEADBAND)));
    return new Translation2d(x / mag * scaled, y / mag * scaled);
  }

  /** 1D rotation deadband + shape (applyDeadband already normalizes to full scale). */
  public static double shapeRotation(double x) {
    double deadbanded = MathUtil.applyDeadband(x, ROTATION_DEADBAND);
    return deadbanded == 0.0 ? 0.0 : shapeInput(deadbanded);
  }

  /** Reset slew/limiter state on mode entry so stale state can't cap response. */
  public void init() {
    transLimiter.reset(0.0, 0.0);
    rotLimiter.reset(0.0);
    slowMode = false;
    lastAPressSecs = -1.0;
  }

  /** One 20 ms tick: e-stop check, drive, mechanism bindings. */
  public void teleopPeriodic() {
    if (driver.getBackButton() && driver.getStartButton()) {
      swerve.stop();
      return;
    }
    if (driver.getLeftStickButtonPressed()) {
      slowMode = !slowMode;
    }
    if (driver.getAButtonPressed()) {
      double now = Timer.getTimestamp();
      if (lastAPressSecs >= 0.0 && now - lastAPressSecs <= REZERO_DOUBLE_TAP_SECS) {
        swerve.zeroGyroWithAlliance();
        lastAPressSecs = -1.0;
      } else {
        lastAPressSecs = now;
      }
    }

    // Stick up/left read negative: negate so push-away is +forward/+left.
    // Convention: shaped (x = strafe, y = forward). The limiter stores
    // (forward, strafe), so vx reads getX() and vy reads getY() below.
    Translation2d shaped =
        shapeTranslation(-driver.getLeftX(), -driver.getLeftY());
    // Red: push-away is -X (away from the Red driver), so negate both axes.
    boolean isRed = AllianceFlipUtil.isRedAlliance();
    double forward = (isRed ? -shaped.getY() : shaped.getY());
    double strafe = (isRed ? -shaped.getX() : shaped.getX());

    double transScale = (slowMode ? SLOW_TRANSLATION_SCALE : 1.0) * Constants.MAX_SPEED;
    double rotScale = (slowMode ? SLOW_ROTATION_SCALE : 1.0) * Constants.MAX_ROTATION_SPEED;
    Translation2d limited = transLimiter.calculate(forward, strafe);
    double vx = limited.getX() * transScale;
    double vy = limited.getY() * transScale;
    double omega = rotLimiter.calculate(shapeRotation(-driver.getRightX())) * rotScale;
    swerve.driveFieldOriented(new ChassisSpeeds(vx, vy, omega));
  }

  public boolean isSlowMode() {
    return slowMode;
  }

  public Controller getDriver() {
    return driver;
  }

  public Controller getOperator() {
    return operator;
  }
}
