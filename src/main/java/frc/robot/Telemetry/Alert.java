package frc.robot.Telemetry;

import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.telemetry.Telemetry;

/**
 * One persistent driver alert. Self-registers with {@link AlertManager} on
 * construction; the manager owns dashboard publishing, this class owns the
 * rate-limited DS-log report so a fault can't spam the log.
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
    Telemetry.log("Alert/" + group + "/" + text, active);
    if (active) {
      long now = System.nanoTime();
      if (now - lastReportNanos > REPORT_THROTTLE_NANOS) {
        lastReportNanos = now;
        if (type == AlertType.ERROR) {
          DriverStationErrors.reportError("Alert ERROR [" + group + "]: " + text, false);
        } else {
          DriverStationErrors.reportWarning(
              "Alert " + type + " [" + group + "]: " + text, false);
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
