package frc.robot.Auto.Actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import frc.robot.Auto.Missions.ActionMission;
import frc.robot.Interfaces.Action;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ActionsTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize(500, 0);
  }

  private static final class InstantAction implements Action {
    final List<String> log;
    final String name;

    InstantAction(List<String> log, String name) {
      this.log = log;
      this.name = name;
    }

    @Override
    public void start() {
      log.add("start:" + name);
    }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
      return true;
    }

    @Override
    public void done() {
      log.add("done:" + name);
    }
  }

  @Test
  void seriesRunsInOrder() {
    List<String> log = new ArrayList<>();
    SeriesAction series =
        new SeriesAction(new InstantAction(log, "a"), new InstantAction(log, "b"));
    series.start();
    series.update();
    series.update();
    assertTrue(series.isFinished());
    assertEquals(List.of("start:a", "done:a", "start:b", "done:b"), log);
  }

  @Test
  void emptySeriesIsImmediatelyDone() {
    SeriesAction series = new SeriesAction(List.of());
    series.start();
    assertTrue(series.isFinished());
  }

  @Test
  void shortWaitFinishes() throws Exception {
    WaitAction wait = new WaitAction(0.05);
    wait.start();
    assertFalse(wait.isFinished());
    Thread.sleep(80);
    assertTrue(wait.isFinished());
    wait.done();
  }

  @Test
  void actionMissionAdaptsLifecycle() {
    List<String> log = new ArrayList<>();
    ActionMission mission = new ActionMission(new InstantAction(log, "x"));
    mission.init();
    mission.run(0.0);
    assertTrue(mission.isDone());
    mission.end();
    assertEquals(List.of("start:x", "done:x"), log);
  }
}
