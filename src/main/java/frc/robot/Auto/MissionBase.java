package frc.robot.Auto;

/**
 * Minimal autonomous mission contract. Runs at 50 Hz via
 * {@link AutoMissionExecutor}. Interruptible; {@code disabledInit} always stops.
 */
public abstract class MissionBase {
  private volatile boolean done = false;

  /** Called once when the mission starts. */
  public void init() {}

  /** Called at 50 Hz until {@link #isDone()} or interrupted. */
  public abstract void run(double timestampSec);

  public boolean isDone() {
    return done;
  }

  protected void setDone(boolean done) {
    this.done = done;
  }

  /** Called once on stop/interrupt — must leave hardware safe. */
  public void end() {}
}
