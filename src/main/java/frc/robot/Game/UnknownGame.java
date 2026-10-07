package frc.robot.Game;

import org.wpilib.math.geometry.Translation2d;

/**
 * Placeholder until kickoff. Carries standard FRC field dimensions so generic
 * navigation compiles, but answers "no zone" for everything — no subsystem may
 * treat these numbers as game truth.
 */
public final class UnknownGame implements GameDefinition {
  public static final double DEFAULT_FIELD_LENGTH = 16.541;
  public static final double DEFAULT_FIELD_WIDTH = 8.069;

  @Override
  public String gameName() {
    return "unknown-2027";
  }

  @Override
  public double fieldLength() {
    return DEFAULT_FIELD_LENGTH;
  }

  @Override
  public double fieldWidth() {
    return DEFAULT_FIELD_WIDTH;
  }

  @Override
  public boolean isInAllianceZone(Translation2d blueOriginTranslation, boolean isRedAlliance) {
    return false;
  }
}
