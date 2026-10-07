package frc.robot.Auto.Actions;

import org.wpilib.system.Timer;
import frc.robot.Interfaces.Action;

/** Waits a fixed duration, then finishes. */
public class WaitAction implements Action {
  private final double seconds;
  private final Timer timer = new Timer();

  public WaitAction(double seconds) {
    this.seconds = seconds;
  }

  @Override
  public void start() {
    timer.restart();
  }

  @Override
  public void update() {}

  @Override
  public boolean isFinished() {
    return timer.hasElapsed(seconds);
  }

  @Override
  public void done() {
    timer.stop();
  }
}
