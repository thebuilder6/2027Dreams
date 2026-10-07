package frc.robot.Subsystems.swerve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.Subsystems.swerve.SwerveModuleIO.SwerveModuleIOOutputs;
import frc.robot.Subsystems.swerve.SwerveModuleIO.SwerveModuleIOOutputMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wpilib.hardware.hal.HAL;

class SwerveModuleIOSimTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize();
  }

  @Test
  void voltageSpinsUpBothAxes() {
    SwerveModuleIOSim io = new SwerveModuleIOSim();
    SwerveModuleIO.SwerveModuleIOInputs inputs = new SwerveModuleIO.SwerveModuleIOInputs();
    SwerveModuleIOOutputs outputs = new SwerveModuleIOOutputs();
    outputs.mode = SwerveModuleIOOutputMode.VOLTAGE;
    outputs.driveVolts = 6.0;
    outputs.steerVolts = 6.0;
    io.applyOutputs(outputs);
    for (int i = 0; i < 50; i++) {
      io.updateInputs(inputs);
    }
    assertTrue(inputs.connected);
    assertTrue(inputs.driveVelocityRadsPerSec > 0.0);
    assertTrue(inputs.drivePositionRads > 0.0);
    assertTrue(inputs.driveCurrentAmps > 0.0);
    assertTrue(inputs.steerVelocityDegPerSec > 0.0);
  }

  @Test
  void stopDecaysToZeroVolts() {
    SwerveModuleIOSim io = new SwerveModuleIOSim();
    SwerveModuleIO.SwerveModuleIOInputs inputs = new SwerveModuleIO.SwerveModuleIOInputs();
    SwerveModuleIOOutputs outputs = new SwerveModuleIOOutputs();
    outputs.mode = SwerveModuleIOOutputMode.VOLTAGE;
    outputs.driveVolts = 6.0;
    io.applyOutputs(outputs);
    io.updateInputs(inputs);
    io.stop();
    io.updateInputs(inputs);
    assertEquals(0.0, inputs.driveAppliedVolts);
    assertEquals(0.0, inputs.steerAppliedVolts);
  }
}
