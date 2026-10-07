package frc.robot.Navigation;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Translation2d;
import frc.robot.Game.GameDefinition;
import frc.robot.Game.UnknownGame;

/**
 * Single owner of field dimensions and zone tests. Dimensions come from the
 * active {@link GameDefinition} — never hardcode a second copy elsewhere.
 * Game-specific features (hubs, ramps, trenches) belong in the {@code Game/}
 * implementation, not here.
 */
public final class FieldMap {
  private static GameDefinition game = new UnknownGame();

  private FieldMap() {}

  /** Kickoff swaps the placeholder for the real game. */
  public static void setGame(GameDefinition next) {
    if (next != null) {
      game = next;
    }
  }

  public static GameDefinition getGame() {
    return game;
  }

  public static double fieldLength() {
    return game.fieldLength();
  }

  public static double fieldWidth() {
    return game.fieldWidth();
  }

  public static boolean isInAllianceZone(Translation2d blueOriginTranslation, boolean isRedAlliance) {
    if (blueOriginTranslation == null) {
      return false;
    }
    return game.isInAllianceZone(blueOriginTranslation, isRedAlliance);
  }

  public static boolean isPoseInAllianceZone(Pose2d pose, boolean isRedAlliance) {
    if (pose == null) {
      return false;
    }
    return isInAllianceZone(pose.getTranslation(), isRedAlliance);
  }
}
