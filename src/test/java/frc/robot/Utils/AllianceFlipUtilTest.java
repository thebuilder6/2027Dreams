package frc.robot.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.jupiter.api.Test;

class AllianceFlipUtilTest {
  @Test
  void blueAppliesAreIdentity() {
    Translation2d t = new Translation2d(2.0, 4.0);
    assertEquals(t, AllianceFlipUtil.apply(t, false));
    Pose2d p = new Pose2d(1.0, 2.0, Rotation2d.fromDegrees(0));
    assertEquals(p, AllianceFlipUtil.apply(p, false));
  }

  @Test
  void redMirrorsXAndHeading() {
    Translation2d mirrored = AllianceFlipUtil.apply(new Translation2d(1.0, 2.0), true);
    assertEquals(16.541 - 1.0, mirrored.getX(), 1e-9);
    assertEquals(2.0, mirrored.getY(), 1e-9);

    Pose2d mirroredPose =
        AllianceFlipUtil.apply(new Pose2d(1.0, 2.0, Rotation2d.fromDegrees(0)), true);
    assertEquals(16.541 - 1.0, mirroredPose.getX(), 1e-9);
    assertEquals(180.0, mirroredPose.getRotation().getDegrees(), 1e-9);
  }

  @Test
  void nullsPassThrough() {
    assertNull(AllianceFlipUtil.apply((Pose2d) null, true));
  }
}
