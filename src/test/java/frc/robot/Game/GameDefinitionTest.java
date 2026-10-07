package frc.robot.Game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.wpilib.math.geometry.Translation2d;
import frc.robot.Navigation.FieldMap;
import org.junit.jupiter.api.Test;

class GameDefinitionTest {
  @Test
  void unknownGameAnswersSafely() {
    UnknownGame game = new UnknownGame();
    assertEquals("unknown-2027", game.gameName());
    assertFalse(game.isInAllianceZone(new Translation2d(2.0, 4.0), false));
    assertFalse(FieldMap.isPoseInAllianceZone(null, false));
  }
}
