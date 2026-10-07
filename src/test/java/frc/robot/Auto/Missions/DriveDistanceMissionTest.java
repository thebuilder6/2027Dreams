package frc.robot.Auto.Missions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DriveDistanceMissionTest {
  @Test
  void completesAfterTravelingDistance() {
    List<ChassisVelocities> commands = new ArrayList<>();
    double[] x = {0.0};
    DriveDistanceMission mission =
        new DriveDistanceMission(
            () -> new Pose2d(x[0], 0.0, new org.wpilib.math.geometry.Rotation2d()),
            commands::add,
            2.0,
            5.0,
            1.0);
    mission.init();
    // Simulate 20 ms ticks at commanded speed.
    for (int i = 0; i < 200 && !mission.isDone(); i++) {
      mission.run(i * 0.02);
      if (!commands.isEmpty()) {
        x[0] += commands.get(commands.size() - 1).vx * 0.02;
      }
    }
    assertTrue(mission.isDone());
  }

  @Test
  void timesOutWhenStuck() {
    DriveDistanceMission mission =
        new DriveDistanceMission(
            Pose2d::new, speeds -> {}, 2.0, 1.0, 1.0);
    mission.init();
    mission.run(0.0);
    assertFalse(mission.isDone());
    mission.run(1.01);
    assertTrue(mission.isDone());
  }
}
