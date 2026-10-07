package frc.robot.Telemetry;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * One persistent driver alert. Self-registers with {@link AlertManager} on
 * construction; the manager owns dashboard publishing, this class owns the
 * rate-limited console report so a fault can't spam the DS log.
 *
 * <p>Migrates to the 2027 {@code Telemetry} alerts API during the beta import —
 * call sites stay the same.
 */
public class Alert {
  public enum AlertType {
    INFO,
    WARNING,
    ERROR
  }

  private final String group;
  private String text;
  private final AlertType type;
  private boolean active;
  private long lastReportNanos;

  private static final long REPORT_THROTTLE_NANOS = 2_000_000_000L;

  public Alert(String group, String text, AlertType type) {
    this.group = group;
    this.text = text;
    this.type = type;
    AlertManager.register(this);
  }

  public Alert(String text, AlertType type) {
    this("General", text, type);
  }

  public void set(boolean active) {
    this.active = active;
    SmartDashboard.putBoolean("Alert/" + group + "/" + text, active);
    if (active) {
      long now = System.nanoTime();
      if (now - lastReportNanos > REPORT_THROTTLE_NANOS) {
        lastReportNanos = now;
        if (type == AlertType.ERROR) {
          DriverStation.reportError("Alert ERROR [" + group + "]: " + text, false);
        } else {
          DriverStation.reportWarning("Alert " + type + " [" + group + "]: " + text, false);
        }
      }
    }
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public String getGroup() {
    return group;
  }

  public AlertType getType() {
    return type;
  }

  public boolean isActive() {
    return active;
  }
}
