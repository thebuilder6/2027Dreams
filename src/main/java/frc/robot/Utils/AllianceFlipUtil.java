package frc.robot.Utils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Navigation.FieldMap;

/**
 * Blue-origin coordinate standard. Define every field point once for Blue;
 * mirror for Red. Only place that reads {@code DriverStation.getAlliance()}
 * for geometry — everything else takes an explicit {@code isRed} flag.
 */
public final class AllianceFlipUtil {
  private AllianceFlipUtil() {}

  public static boolean isRedAlliance() {
    return DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red;
  }

  public static Translation2d apply(Translation2d translation, boolean isRed) {
    if (isRed && translation != null) {
      return new Translation2d(FieldMap.fieldLength() - translation.getX(), translation.getY());
    }
    return translation;
  }

  public static Translation2d apply(Translation2d translation) {
    return apply(translation, isRedAlliance());
  }

  public static Translation3d apply(Translation3d translation, boolean isRed) {
    if (isRed && translation != null) {
      return new Translation3d(
          FieldMap.fieldLength() - translation.getX(), translation.getY(), translation.getZ());
    }
    return translation;
  }

  public static Translation3d apply(Translation3d translation) {
    return apply(translation, isRedAlliance());
  }

  /** Mirror across the field midline: 180° − θ. */
  public static Rotation2d apply(Rotation2d rotation, boolean isRed) {
    if (isRed && rotation != null) {
      return new Rotation2d(-rotation.getCos(), rotation.getSin());
    }
    return rotation;
  }

  public static Rotation2d apply(Rotation2d rotation) {
    return apply(rotation, isRedAlliance());
  }

  public static Pose2d apply(Pose2d pose, boolean isRed) {
    if (pose == null) {
      return null;
    }
    if (isRed) {
      return new Pose2d(apply(pose.getTranslation(), true), apply(pose.getRotation(), true));
    }
    return pose;
  }

  public static Pose2d apply(Pose2d pose) {
    return apply(pose, isRedAlliance());
  }

  /** Driver pushing "away" is +X on Blue, −X (180°) on Red. */
  public static Rotation2d getDriverRelativeHeading(Rotation2d driverHeading) {
    if (driverHeading == null) {
      return new Rotation2d();
    }
    return isRedAlliance() ? driverHeading.plus(Rotation2d.fromDegrees(180)) : driverHeading;
  }

  public static boolean isPoseInAllianceZone(Pose2d pose, boolean isRedAlliance) {
    return FieldMap.isPoseInAllianceZone(pose, isRedAlliance);
  }

  public static boolean isPoseInAllianceZone(Pose2d pose) {
    return isPoseInAllianceZone(pose, isRedAlliance());
  }
}
