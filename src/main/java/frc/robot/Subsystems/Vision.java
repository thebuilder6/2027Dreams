package frc.robot.Subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Subsystems.vision.VisionIO;
import frc.robot.Subsystems.vision.VisionIOInputsAutoLogged;
import frc.robot.Subsystems.vision.VisionIOSim;
import frc.robot.Telemetry.Alert;
import frc.robot.Telemetry.Alert.AlertType;
import frc.robot.Telemetry.LoggedTracer;
import java.util.Optional;
import org.littletonrobotics.junction.Logger;

/**
 * Generic vision subsystem (one camera; add instances per camera at kickoff).
 * Owns estimate validity: stale observations and unconnected cameras never
 * reach consumers — use {@link #getEstimatedPose} which is empty unless fresh.
 */
public class Vision implements Subsystem {
  /** Observations older than this are rejected (2026 value). */
  public static final double STALE_TIMEOUT_SECS = 0.150;

  private static Vision instance = null;

  private final VisionIO io;
  private final VisionIOInputsAutoLogged inputs = new VisionIOInputsAutoLogged();
  private final Alert disconnectedAlert =
      new Alert("Vision", "Disconnected: odometry only", AlertType.WARNING);

  public static synchronized Vision getInstance() {
    if (instance == null) {
      instance = new Vision();
    }
    return instance;
  }

  public Vision() {
    // No camera hardware yet: sim twin on sim, disconnected twin on real.
    // PhotonVision/Limelight IOs arrive with the cameras (see docs/VISION.md).
    this(RobotBase.isSimulation() ? new VisionIOSim() : new VisionIO() {});
  }

  Vision(VisionIO io) {
    this.io = io;
    SubsystemManager.registerSubsystem(this);
  }

  /** Fresh field-relative estimate, or empty when stale/disconnected. */
  public Optional<Pose2d> getEstimatedPose() {
    if (!inputs.connected || !inputs.hasTarget) {
      return Optional.empty();
    }
    double ageSecs = Timer.getTimestamp() - (inputs.observationTimeSecs + inputs.latencySecs);
    if (ageSecs > STALE_TIMEOUT_SECS) {
      return Optional.empty();
    }
    return Optional.of(inputs.estimatedPose);
  }

  @Override
  public void update() {
    LoggedTracer.reset();
    io.updateInputs(inputs);
    Logger.processInputs("Vision", inputs);
    disconnectedAlert.set(!inputs.connected);
    LoggedTracer.record("Vision/Inputs");
  }

  @Override
  public void initialize() {}

  @Override
  public void log() {
    Logger.recordOutput("Vision/HasTarget", getEstimatedPose().isPresent());
    Logger.recordOutput("Vision/TagCount", inputs.tagCount);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "Vision";
  }
}
