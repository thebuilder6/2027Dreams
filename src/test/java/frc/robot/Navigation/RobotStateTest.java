package frc.robot.Navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RobotStateTest {
  @BeforeEach
  void clear() {
    RobotState.clearForTest();
  }

  @Test
  void defaultsToOrigin() {
    assertEquals(new Pose2d(), RobotState.getInstance().getEstimatedPose());
  }

  @Test
  void drivePoseRoundTrips() {
    Pose2d pose = new Pose2d(1.5, 2.5, Rotation2d.fromDegrees(45));
    RobotState.getInstance().setDrivePose(pose);
    assertEquals(pose, RobotState.getInstance().getEstimatedPose());
  }

  @Test
  void nullPoseIgnored() {
    RobotState.getInstance().setDrivePose(null);
    assertEquals(new Pose2d(), RobotState.getInstance().getEstimatedPose());
  }
}
