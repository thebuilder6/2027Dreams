package frc.robot.Subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.Subsystems.mechanism.MechanismIOInputsAutoLogged;
import frc.robot.Subsystems.mechanism.MechanismIOSim;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MechanismTest {
  @BeforeAll
  static void initHal() {
    // DCMotorSim reads battery voltage via HAL natives — without this the
    // test JVM dies in wpiHal.dll (see hs_err_pid*.log, gitignored).
    HAL.initialize(500, 0);
  }

  @BeforeEach
  void clear() {
    SubsystemManager.clearForTest();
  }

  @Test
  void voltageSpinsUpSim() {
    MechanismIOSim io = new MechanismIOSim(DCMotor.getNEO(1), 1.0, 0.001);
    Mechanism mechanism = new Mechanism(io);
    mechanism.run(12.0);
    for (int i = 0; i < 50; i++) {
      mechanism.update();
    }
    assertTrue(mechanism.getVelocityRadsPerSec() > 0.0);
    assertTrue(mechanism.getPositionRads() > 0.0);
    assertTrue(mechanism.getCurrentAmps() > 0.0);
    assertTrue(mechanism.getSimulationCurrentDraw() > 0.0);
  }

  @Test
  void stopKillsVoltage() {
    MechanismIOSim io = new MechanismIOSim();
    Mechanism mechanism = new Mechanism(io);
    mechanism.run(12.0);
    mechanism.update();
    mechanism.stop();
    mechanism.update();
    MechanismIOInputsAutoLogged inputs = new MechanismIOInputsAutoLogged();
    io.updateInputs(inputs);
    assertEquals(0.0, inputs.appliedVolts, 1e-9);
    assertEquals(Mechanism.State.STANDBY, mechanism.getState());
  }
}
