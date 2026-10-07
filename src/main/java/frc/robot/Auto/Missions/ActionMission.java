package frc.robot.Auto.Missions;

import frc.robot.Auto.MissionBase;
import frc.robot.Interfaces.Action;

/**
 * Adapts one {@link Action} (or composition) to the mission executor:
 * {@code init→start}, {@code run→update}, {@code isDone→isFinished},
 * {@code end→done}.
 */
public class ActionMission extends MissionBase {
  private final Action action;

  public ActionMission(Action action) {
    this.action = action;
  }

  @Override
  public void init() {
    action.start();
  }

  @Override
  public void run(double timestampSec) {
    action.update();
    if (action.isFinished()) {
      setDone(true);
    }
  }

  @Override
  public void end() {
    action.done();
  }
}
