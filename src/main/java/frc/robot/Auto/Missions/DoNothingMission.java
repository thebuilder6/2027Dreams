package frc.robot.Auto.Missions;

import frc.robot.Auto.MissionBase;

/** Safe default auto: stays still. Chooser default until a real mission lands. */
public final class DoNothingMission extends MissionBase {
  @Override
  public void run(double timestampSec) {
    setDone(false);
  }
}
