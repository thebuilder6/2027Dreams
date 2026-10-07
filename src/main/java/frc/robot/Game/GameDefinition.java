package frc.robot.Game;

import edu.wpi.first.math.geometry.Translation2d;

/**
 * Game-agnostic field definition. Generic code (navigation, auto, telemetry)
 * programs against this interface; each season provides one implementation.
 * All coordinates are Blue-origin (X=0 at the Blue wall).
 */
public interface GameDefinition {
  /** Short name, e.g. {@code "unknown-2027"}. */
  String gameName();

  /** Field length in meters (X). */
  double fieldLength();

  /** Field width in meters (Y). */
  double fieldWidth();

  /**
   * True when a Blue-origin translation is inside the caller's own alliance
   * scoring zone. Symmetric games mirror the zone; asymmetric games implement
   * both sides explicitly and document why.
   */
  boolean isInAllianceZone(Translation2d blueOriginTranslation, boolean isRedAlliance);
}
