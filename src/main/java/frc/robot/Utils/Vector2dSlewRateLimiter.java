package frc.robot.Utils;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.system.Timer;

/**
 * True 2D vector slew rate limiter.
 *
 * <p>Unlike independent 1D limiters applied separately to X and Y, this
 * constrains the Euclidean rate of change of the 2D velocity vector:
 * {@code ||v_target - v_prev|| <= maxRate * dt}. Direction survives sudden
 * angle shifts (full-forward to full-strafe) instead of clipping corners.
 */
public class Vector2dSlewRateLimiter {
  private double rateLimit;
  private Translation2d prevVal;
  private double prevTime;

  /**
   * @param rateLimit maximum rate of change of the vector per second (e.g. m/s^2).
   */
  public Vector2dSlewRateLimiter(double rateLimit) {
    this(rateLimit, new Translation2d());
  }

  public Vector2dSlewRateLimiter(double rateLimit, Translation2d initialValue) {
    this.rateLimit = rateLimit;
    this.prevVal = (initialValue != null) ? initialValue : new Translation2d();
    this.prevTime = Timer.getTimestamp();
  }

  /** Filters the target vector to enforce the acceleration limit. */
  public Translation2d calculate(Translation2d target) {
    double currentTime = Timer.getTimestamp();
    double dt = currentTime - prevTime;
    prevTime = currentTime;

    // Guard against paused sim / loop overruns.
    if (dt <= 0.0) {
      return prevVal;
    }
    if (dt > 0.1) {
      dt = 0.02;
    }

    double maxChange = rateLimit * dt;
    Translation2d delta = target.minus(prevVal);
    double deltaNorm = delta.getNorm();

    if (deltaNorm <= maxChange) {
      prevVal = target;
    } else {
      prevVal = prevVal.plus(delta.times(maxChange / deltaNorm));
    }
    return prevVal;
  }

  public Translation2d calculate(double targetX, double targetY) {
    return calculate(new Translation2d(targetX, targetY));
  }

  public void reset(Translation2d value) {
    this.prevVal = (value != null) ? value : new Translation2d();
    this.prevTime = Timer.getTimestamp();
  }

  public void reset(double x, double y) {
    reset(new Translation2d(x, y));
  }

  public void setRateLimit(double rateLimit) {
    this.rateLimit = Math.max(0.0, rateLimit);
  }

  public double getRateLimit() {
    return rateLimit;
  }

  public Translation2d getLastValue() {
    return prevVal;
  }
}
