package frc.robot.Auto.Missions;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.Auto.MissionBase;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Drives forward a set distance then stops. Pose and drive are injected so
 * the mission is testable without hardware; production wires
 * {@code SwerveBase::getPose} + a drive consumer.
 */
public class DriveDistanceMission extends MissionBase {
  private final Supplier<Pose2d> poseSupplier;
  private final Consumer<ChassisSpeeds> driveConsumer;
  private final double distanceMeters;
  private final double timeoutSecs;
  private final double driveSpeedMps;

  private Pose2d startPose = null;
  private double startTimeSecs = 0.0;
  private boolean started = false;

  public DriveDistanceMission(
      Supplier<Pose2d> poseSupplier,
      Consumer<ChassisSpeeds> driveConsumer,
      double distanceMeters,
      double timeoutSecs) {
    this(poseSupplier, driveConsumer, distanceMeters, timeoutSecs, 1.0);
  }

  public DriveDistanceMission(
      Supplier<Pose2d> poseSupplier,
      Consumer<ChassisSpeeds> driveConsumer,
      double distanceMeters,
      double timeoutSecs,
      double driveSpeedMps) {
    this.poseSupplier = poseSupplier;
    this.driveConsumer = driveConsumer;
    this.distanceMeters = distanceMeters;
    this.timeoutSecs = timeoutSecs;
    this.driveSpeedMps = driveSpeedMps;
  }

  @Override
  public void init() {
    started = false;
    startPose = null;
  }

  @Override
  public void run(double timestampSec) {
    if (!started) {
      started = true;
      startTimeSecs = timestampSec;
      startPose = poseSupplier.get();
    }
    double elapsed = timestampSec - startTimeSecs;
    double traveled = poseSupplier.get().getTranslation().getDistance(startPose.getTranslation());
    if (traveled >= distanceMeters || elapsed >= timeoutSecs) {
      driveConsumer.accept(new ChassisSpeeds());
      setDone(true);
      return;
    }
    driveConsumer.accept(new ChassisSpeeds(driveSpeedMps, 0.0, 0.0));
  }

  @Override
  public void end() {
    driveConsumer.accept(new ChassisSpeeds());
  }
}
