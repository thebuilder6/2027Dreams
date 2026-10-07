package frc.robot.Telemetry;

import edu.wpi.first.wpilibj.Timer;
import org.littletonrobotics.junction.Logger;

/**
 * Per-phase cycle-time tracer (6328 {@code LoggedTracer} pattern). Bracket
 * work in {@code robotPeriodic} to see where loop time goes in AdvantageScope.
 *
 * <pre>
 * LoggedTracer.reset();
 * SubsystemManager.updateSubsystems();
 * LoggedTracer.record("Update");
 * </pre>
 */
public final class LoggedTracer {
  private static double startTime = Timer.getTimestamp();

  private LoggedTracer() {}

  public static void reset() {
    startTime = Timer.getTimestamp();
  }

  public static void record(String key) {
    double now = Timer.getTimestamp();
    Logger.recordOutput("LoggedTracer/" + key, (now - startTime) * 1000.0);
    startTime = now;
  }
}
