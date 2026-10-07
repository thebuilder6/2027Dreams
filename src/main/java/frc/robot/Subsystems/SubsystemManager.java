package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Interfaces.Subsystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static registry. Subsystems self-register in their constructors so
 * {@code Robot} never lists them. One throwing subsystem is isolated with a
 * throttled warning — it never kills the loop. Only {@code Exception} is
 * contained; {@code Error} propagates.
 */
public final class SubsystemManager {
  private static final List<Subsystem> subsystems = new ArrayList<>();
  private static final long REPORT_THROTTLE_NANOS = 10_000_000_000L;
  private static final Map<String, Long> lastReportNanos = new HashMap<>();

  private SubsystemManager() {}

  public static void registerSubsystem(Subsystem subsystem) {
    subsystems.add(subsystem);
  }

  /** Test-only reset. Production code never calls this. */
  static void clearForTest() {
    subsystems.clear();
    lastReportNanos.clear();
  }

  public static void initializeSubsystems() {
    for (Subsystem s : new ArrayList<>(subsystems)) {
      s.initialize();
    }
  }

  public static void updateSubsystems() {
    for (Subsystem s : subsystems) {
      runGuarded(s, "update", s::update);
    }
  }

  public static void simulationUpdateSubsystems() {
    for (Subsystem s : subsystems) {
      runGuarded(s, "simulationUpdate", s::simulationUpdate);
    }
  }

  public static void logSubsystems() {
    for (Subsystem s : subsystems) {
      runGuarded(s, "log", s::log);
    }
  }

  private static void runGuarded(Subsystem subsystem, String phase, Runnable call) {
    try {
      call.run();
    } catch (Exception e) {
      long now = System.nanoTime();
      String name;
      try {
        name = subsystem.getName();
      } catch (Exception ignored) {
        name = subsystem.getClass().getSimpleName();
      }
      Long last = lastReportNanos.get(name);
      if (last == null || now - last > REPORT_THROTTLE_NANOS) {
        lastReportNanos.put(name, now);
        DriverStation.reportWarning(
            "SubsystemManager isolated "
                + name
                + "."
                + phase
                + " failure: "
                + e
                + " (contract: docs/ARCHITECTURE.md)",
            false);
      }
    }
  }

  public static List<Subsystem> getSubsystems() {
    return Collections.unmodifiableList(subsystems);
  }
}
