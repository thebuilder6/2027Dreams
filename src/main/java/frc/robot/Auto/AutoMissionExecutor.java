package frc.robot.Auto;

import edu.wpi.first.wpilibj.Timer;

/**
 * Runs one {@link MissionBase} on its own thread at 50 Hz. Single-threaded by
 * construction: {@code start} stops any running mission first.
 *
 * <p><b>Thread warning for newcomers:</b> mission {@code run} executes off the
 * main robot thread, so it must never touch subsystems or NetworkTables
 * directly. Inject suppliers/consumers instead (see {@code DriveDistanceMission}).
 * A throwing mission stops silently by design — reproduce it in a unit test,
 * where the exception surfaces, rather than on the robot.
 */
public final class AutoMissionExecutor {
  private static final double PERIOD_SEC = 0.02;

  private Thread thread;
  private volatile MissionBase mission;

  public synchronized void start(MissionBase next) {
    stop();
    mission = next;
    if (mission == null) {
      return;
    }
    mission.init();
    thread =
        new Thread(
            () -> {
              while (!Thread.currentThread().isInterrupted()) {
                MissionBase current = mission;
                if (current == null || current.isDone()) {
                  break;
                }
                try {
                  current.run(Timer.getFPGATimestamp());
                } catch (Exception e) {
                  // Silent by design: this thread has no HAL-safe way to report
                  // in unit tests. Reproduce in a test to see the stack trace.
                  break;
                }
                try {
                  Thread.sleep((long) (PERIOD_SEC * 1000));
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  break;
                }
              }
            });
    thread.setDaemon(true);
    thread.start();
  }

  public synchronized void stop() {
    if (thread != null) {
      thread.interrupt();
      thread = null;
    }
    if (mission != null) {
      try {
        mission.end();
      } finally {
        mission = null;
      }
    }
  }

  public synchronized boolean isRunning() {
    return thread != null && thread.isAlive();
  }
}
