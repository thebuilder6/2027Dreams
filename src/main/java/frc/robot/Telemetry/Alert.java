package frc.robot.Telemetry;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Minimal driver-alert surface. Rate-limited so a fault can't spam the console.
 * Migrates to the 2027 {@code Telemetry} alerts API during the beta import —
 * call sites stay the same. (2027 migration renames SmartDashboard/Alert.)
 */
public final class Alert {
  public enum AlertType {
    ERROR,
    WARNING,
    INFO
  }

  private final String key;
  private final AlertType type;
  private boolean active;
  private long lastReportNanos;

  private static final long REPORT_THROTTLE_NANOS = 2_000_000_000L;

  public Alert(String key, AlertType type) {
    this.key = key;
    this.type = type;
  }

  public void set(boolean active) {
    this.active = active;
    SmartDashboard.putBoolean("Alert/" + key, active);
    if (active) {
      long now = System.nanoTime();
      if (now - lastReportNanos > REPORT_THROTTLE_NANOS) {
        lastReportNanos = now;
        if (type == AlertType.ERROR) {
          DriverStation.reportError("Alert ERROR: " + key, false);
        } else {
          DriverStation.reportWarning("Alert " + type + ": " + key, false);
        }
      }
    }
  }

  public boolean isActive() {
    return active;
  }
}
