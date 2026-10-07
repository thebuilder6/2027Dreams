package frc.robot.Telemetry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import frc.robot.Telemetry.Alert.AlertType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertManagerTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize(500, 0);
  }

  @BeforeEach
  void clear() {
    AlertManager.resetAll();
  }

  @Test
  void nominalWithNoActiveAlerts() {
    AlertManager.update();
    assertTrue(AlertManager.getActiveAlerts().isEmpty());
    assertFalse(AlertManager.hasActiveErrors());
  }

  @Test
  void errorSurfacesAndClears() {
    Alert alert = new Alert("Test", "Boom", AlertType.ERROR);
    alert.set(true);
    AlertManager.update();
    assertTrue(AlertManager.hasActiveErrors());
    assertTrue(AlertManager.getActiveAlerts().contains(alert));
    alert.set(false);
    AlertManager.update();
    assertFalse(AlertManager.hasActiveErrors());
  }
}
