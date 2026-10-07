package frc.robot.Interfaces;

import org.wpilib.command2.Command;

/**
 * Project subsystem contract. Extends WPILib's command {@code Subsystem} so the
 * scheduler can require our subsystems later (Commands v2 today, v3 after the
 * 2027 migration), but adds the lifecycle 2026Dreams relies on.
 */
public interface Subsystem extends org.wpilib.command2.Subsystem {
  /** Called every 20 ms from {@code Robot.robotPeriodic}. */
  void update();

  /** Called once at startup. */
  void initialize();

  /** Publish telemetry. No console prints — use {@code Telemetry.Alert}. */
  void log();

  /** True when the subsystem is healthy and enabled. */
  boolean isEnabled();

  /** Simulation-only update. Default no-op so hardware subsystems skip it. */
  default void simulationUpdate() {}

  /** Simulated current draw in amps for the battery model. Default 0. */
  default double getSimulationCurrentDraw() {
    return 0.0;
  }

  /** Stable name for fault isolation messages. */
  String getName();

  @Override
  default Command idle() {
    return org.wpilib.command2.Subsystem.super.idle();
  }
}
