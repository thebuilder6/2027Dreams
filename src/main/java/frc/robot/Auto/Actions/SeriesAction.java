package frc.robot.Auto.Actions;

import frc.robot.Interfaces.Action;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Runs actions sequentially, advancing only when the current one finishes. */
public class SeriesAction implements Action {
  private final ArrayList<Action> actions;
  private int currentIndex = 0;

  public SeriesAction(List<Action> actions) {
    this.actions = new ArrayList<>(actions);
  }

  public SeriesAction(Action... actions) {
    this(actions != null ? Arrays.asList(actions) : List.of());
  }

  @Override
  public void start() {
    currentIndex = 0;
    if (!actions.isEmpty()) {
      actions.get(0).start();
    }
  }

  @Override
  public void update() {
    if (currentIndex < actions.size()) {
      Action current = actions.get(currentIndex);
      current.update();
      if (current.isFinished()) {
        current.done();
        currentIndex++;
        if (currentIndex < actions.size()) {
          actions.get(currentIndex).start();
        }
      }
    }
  }

  @Override
  public boolean isFinished() {
    return currentIndex >= actions.size();
  }

  @Override
  public void done() {}

  @Override
  public Set<Class<?>> getRequirements() {
    Set<Class<?>> reqs = new HashSet<>();
    for (Action action : actions) {
      reqs.addAll(action.getRequirements());
    }
    return reqs;
  }
}
