package frc.robot.Subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.vision.VisionIO.VisionIOInputs;
import frc.robot.Subsystems.vision.VisionIOSim;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisionTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize(500, 0);
  }

  @BeforeEach
  void clear() {
    SubsystemManager.clearForTest();
  }

  @Test
  void simReportsNoTargetForUnknownGame() {
    VisionIOSim io = new VisionIOSim();
    Vision vision = new Vision(io);
    vision.update();
    assertTrue(vision.getEstimatedPose().isEmpty());
  }

  @Test
  void staleObservationRejected() {
    final VisionIOInputs canned = new VisionIOInputs();
    canned.connected = true;
    canned.hasTarget = true;
    canned.estimatedPose = new Pose2d(1.0, 2.0, new Rotation2d());
    canned.observationTimeSecs = Timer.getTimestamp() - 10.0;
    canned.latencySecs = 0.0;
    Vision vision =
        new Vision(
            new frc.robot.Subsystems.vision.VisionIO() {
              @Override
              public void updateInputs(VisionIOInputs inputs) {
                inputs.connected = canned.connected;
                inputs.hasTarget = canned.hasTarget;
                inputs.estimatedPose = canned.estimatedPose;
                inputs.observationTimeSecs = canned.observationTimeSecs;
                inputs.latencySecs = canned.latencySecs;
              }
            });
    vision.update();
    assertTrue(vision.getEstimatedPose().isEmpty());
  }

  @Test
  void freshObservationAccepted() {
    final VisionIOInputs canned = new VisionIOInputs();
    canned.connected = true;
    canned.hasTarget = true;
    canned.estimatedPose = new Pose2d(1.0, 2.0, Rotation2d.fromDegrees(30));
    canned.observationTimeSecs = Timer.getTimestamp();
    canned.latencySecs = 0.02;
    Vision vision =
        new Vision(
            new frc.robot.Subsystems.vision.VisionIO() {
              @Override
              public void updateInputs(VisionIOInputs inputs) {
                inputs.connected = canned.connected;
                inputs.hasTarget = canned.hasTarget;
                inputs.estimatedPose = canned.estimatedPose;
                inputs.observationTimeSecs = canned.observationTimeSecs;
                inputs.latencySecs = canned.latencySecs;
              }
            });
    vision.update();
    assertEquals(canned.estimatedPose, vision.getEstimatedPose().orElseThrow());
  }
}
