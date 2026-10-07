package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Data.Constants;
import frc.robot.Hardware.PortMap;

/**
 * Driver/operator input. Owns controllers + input cleanup chain. Subsystem
 * calls plug in here as mechanisms land. Order matters: circular deadband →
 * cubic shape → slew → scale → Red flip.
 */
public final class Teleop {
  public static final double TRANSLATION_DEADBAND = Constants.OperatorConstants.TRANSLATION_DEADBAND;
  public static final double ROTATION_DEADBAND = Constants.OperatorConstants.ROTATION_DEADBAND;

  private final XboxController driver = new XboxController(PortMap.DRIVER_CONTROLLER);
  private final XboxController operator = new XboxController(PortMap.OPERATOR_CONTROLLER);

  private final SlewRateLimiter xLimiter =
      new SlewRateLimiter(Constants.OperatorConstants.TRANSLATION_SLEW_RATE);
  private final SlewRateLimiter yLimiter =
      new SlewRateLimiter(Constants.OperatorConstants.TRANSLATION_SLEW_RATE);
  private final SlewRateLimiter rotLimiter =
      new SlewRateLimiter(Constants.OperatorConstants.ROTATION_SLEW_RATE);

  /** Cubic: precision at center, 100% at full throw. */
  public static double shapeInput(double x) {
    double a = Math.abs(x);
    return Math.signum(x) * (0.7 * a * a * a + 0.3 * a);
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

  /** 1D rotation deadband + shape. */
  public static double shapeRotation(double x) {
    return MathUtil.applyDeadband(x, ROTATION_DEADBAND) == 0.0
        ? 0.0
        : shapeInput(MathUtil.applyDeadband(x, ROTATION_DEADBAND) / (1.0 - ROTATION_DEADBAND));
  }

  /** Reset slew/limiter state on mode entry so stale state can't cap response. */
  public void init() {
    xLimiter.reset(0.0);
    yLimiter.reset(0.0);
    rotLimiter.reset(0.0);
  }

  public XboxController getDriver() {
    return driver;
  }

  public XboxController getOperator() {
    return operator;
  }
}
