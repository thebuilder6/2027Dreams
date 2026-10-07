package frc.robot.Auto;

import edu.wpi.first.wpilibj.Timer;

/**
 * Runs one {@link MissionBase} on its own thread at 50 Hz. Single-threaded by
 * construction: {@code start} stops any running mission first.
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
