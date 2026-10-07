package frc.robot.Subsystems.vision;

/**
 * Desktop-sim VisionIO. Reports a disconnected camera (no targets) until the
 * game year's AprilTag layout lands at kickoff — at that point this class
 * generates simulated detections from the layout + true pose, mirroring
 * 2026's {@code VisionSim}/{@code LimelightSim}. Returning empty is the honest
 * answer for an unknown game, not a degraded guess.
 */
public class VisionIOSim implements VisionIO {

  @Override
  public void updateInputs(VisionIOInputs inputs) {
    inputs.connected = true;
    inputs.hasTarget = false;
    inputs.tagCount = 0;
    inputs.avgTagDistanceMeters = 0.0;
  }
}
