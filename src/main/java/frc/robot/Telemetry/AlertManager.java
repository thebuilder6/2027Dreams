package frc.robot.Telemetry;

import org.wpilib.telemetry.Telemetry;
import frc.robot.Telemetry.Alert.AlertType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns dashboard publishing for all {@link Alert}s. Alerts self-register on
 * construction; {@code Robot.robotPeriodic} calls {@link #update()} every
 * tick. NT writes happen only when the alert set actually changes.
 */
public final class AlertManager {
  private static final List<Alert> alerts = new ArrayList<>();
  private static String lastBanner = null;
  private static List<String> lastErrors = null;
  private static List<String> lastWarnings = null;
  private static List<String> lastInfos = null;

  private AlertManager() {}

  public static synchronized void register(Alert alert) {
    if (!alerts.contains(alert)) {
      alerts.add(alert);
    }
  }

  /** Test isolation + clean-state resets. */
  public static synchronized void resetAll() {
    for (Alert alert : alerts) {
      alert.set(false);
    }
    lastErrors = null;
    lastWarnings = null;
    lastInfos = null;
    lastBanner = null;
  }

  public static synchronized List<Alert> getActiveAlerts() {
    List<Alert> active = new ArrayList<>();
    for (Alert alert : alerts) {
      if (alert.isActive()) {
        active.add(alert);
      }
    }
    return Collections.unmodifiableList(active);
  }

  public static synchronized boolean hasActiveErrors() {
    for (Alert alert : alerts) {
      if (alert.isActive() && alert.getType() == AlertType.ERROR) {
        return true;
      }
    }
    return false;
  }

  public static synchronized void update() {
    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();
    List<String> infos = new ArrayList<>();
    for (Alert alert : alerts) {
      if (!alert.isActive()) {
        continue;
      }
      String formatted = "[" + alert.getGroup() + "] " + alert.getText();
      switch (alert.getType()) {
        case ERROR:
          errors.add(formatted);
          break;
        case WARNING:
          warnings.add(formatted);
          break;
        case INFO:
          infos.add(formatted);
          break;
      }
    }

    String banner;
    if (!errors.isEmpty()) {
      banner = "[ERROR (" + errors.size() + ")] " + errors.get(0);
    } else if (!warnings.isEmpty()) {
      banner = "[WARN (" + warnings.size() + ")] " + warnings.get(0);
    } else if (!infos.isEmpty()) {
      banner = "[INFO] " + infos.get(0);
    } else {
      banner = "[NOMINAL] Systems Operational";
    }

    if (!errors.equals(lastErrors)
        || !warnings.equals(lastWarnings)
        || !infos.equals(lastInfos)
        || !banner.equals(lastBanner)) {
      Telemetry.log("Alerts/Errors", errors.toArray(new String[0]));
      Telemetry.log("Alerts/Warnings", warnings.toArray(new String[0]));
      Telemetry.log("Alerts/Infos", infos.toArray(new String[0]));
      Telemetry.log("Driver/AlertBanner", banner);
      lastErrors = new ArrayList<>(errors);
      lastWarnings = new ArrayList<>(warnings);
      lastInfos = new ArrayList<>(infos);
      lastBanner = banner;
    }
  }
}
