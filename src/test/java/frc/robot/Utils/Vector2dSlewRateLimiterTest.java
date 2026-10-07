package frc.robot.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Vector2dSlewRateLimiterTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize(500, 0);
  }

  @Test
  void reachesTargetWithinRate() throws Exception {
    Vector2dSlewRateLimiter limiter = new Vector2dSlewRateLimiter(1000.0);
    // One real tick must elapse: with dt == 0 the limiter correctly holds
    // (paused-sim guard) instead of converging.
    Thread.sleep(30);
    Translation2d out = limiter.calculate(1.0, 0.0);
    assertEquals(1.0, out.getX(), 1e-9);
  }

  @Test
  void preservesDirectionUnderLimit() throws Exception {
    // Slew from full-forward toward full-strafe: the vector must rotate
    // toward the target, never clip to an axis.
    Vector2dSlewRateLimiter limiter =
        new Vector2dSlewRateLimiter(2.0, new Translation2d(0.0, 1.0));
    Thread.sleep(50);
    Translation2d out = limiter.calculate(1.0, 0.0);
    assertTrue(out.getX() > 0.0);
    assertTrue(out.getY() > 0.0 && out.getY() < 1.0);
    assertTrue(out.getNorm() <= 1.0 + 1e-9);
  }

  @Test
  void resetRestoresState() {
    Vector2dSlewRateLimiter limiter = new Vector2dSlewRateLimiter(1000.0);
    limiter.calculate(0.5, 0.5);
    limiter.reset(0.0, 0.0);
    assertEquals(new Translation2d(), limiter.getLastValue());
  }
}
