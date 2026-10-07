package frc.robot.Interfaces;

import java.util.Collections;
import java.util.Set;

/**
 * One step of an autonomous routine: setup once, tick until finished, clean
 * up once. Composed by {@code SeriesAction}; driven by {@code ActionMission}.
 */
public interface Action {
  /** Setup, run once when the action starts. */
  void start();

  /** Iterative logic, called every tick until {@link #isFinished}. */
  void update();

  /** True when the action is complete. */
  boolean isFinished();

  /** Cleanup, run once when finished. */
  void done();

  /** Subsystem classes this action mutates. Empty by default. */
  default Set<Class<?>> getRequirements() {
    return Collections.emptySet();
  }
}
