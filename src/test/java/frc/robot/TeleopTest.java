package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.math.geometry.Translation2d;
import org.junit.jupiter.api.Test;

class TeleopTest {
  @Test
  void shapeIsOddAndFullScale() {
    assertEquals(1.0, Teleop.shapeInput(1.0), 1e-9);
    assertEquals(-1.0, Teleop.shapeInput(-1.0), 1e-9);
    assertEquals(-Teleop.shapeInput(0.37), Teleop.shapeInput(-0.37), 1e-9);
    assertTrue(Math.abs(Teleop.shapeInput(0.5)) < 0.5);
  }

  @Test
  void translationDeadbandIsCircular() {
    assertEquals(new Translation2d(), Teleop.shapeTranslation(0.05, 0.05));
    Translation2d full = Teleop.shapeTranslation(0.0, -1.0);
    assertEquals(1.0, full.getNorm(), 1e-9);
    // Corner: square deadband would clip, circular preserves direction.
    Translation2d corner = Teleop.shapeTranslation(0.7, 0.7);
    assertEquals(Math.PI / 4, corner.getAngle().getRadians(), 1e-9);
  }

  @Test
  void rotationDeadbandZeroesDrift() {
    assertEquals(0.0, Teleop.shapeRotation(0.03), 1e-9);
    assertEquals(0.0, Teleop.shapeRotation(-0.03), 1e-9);
    assertEquals(1.0, Teleop.shapeRotation(1.0), 1e-6);
  }
}
