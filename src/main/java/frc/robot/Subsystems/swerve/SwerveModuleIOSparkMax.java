package frc.robot.Subsystems.swerve;

import frc.robot.Hardware.SparkMaxMotor;

/**
 * Real-hardware module: two NEOs on SparkMaxes behind {@link SparkMaxMotor},
 * so no vendor API leaks into subsystem logic. Steer angle comes from the
 * relative encoder (placeholder — absolute encoder lands with the chassis).
 */
public class SwerveModuleIOSparkMax implements SwerveModuleIO {
  private final SparkMaxMotor drive;
  private final SparkMaxMotor steer;

  public SwerveModuleIOSparkMax(
      int driveCanId, int steerCanId, boolean driveInverted, int currentLimitAmps) {
    drive = new SparkMaxMotor(driveCanId);
    drive.configure(driveInverted, true, currentLimitAmps);
    steer = new SparkMaxMotor(steerCanId);
    steer.configure(false, true, currentLimitAmps);
  }

  @Override
  public void updateInputs(SwerveModuleIOInputs inputs) {
    inputs.connected = drive.isConnected() && steer.isConnected();
    inputs.drivePositionRads = drive.getPositionRotations() * 2.0 * Math.PI;
    inputs.driveVelocityRadsPerSec = drive.getVelocityRPM() * 2.0 * Math.PI / 60.0;
    inputs.driveAppliedVolts = drive.getAppliedVoltage();
    inputs.driveCurrentAmps = drive.getOutputCurrent();
    inputs.steerPositionDeg = steer.getPositionRotations() * 360.0;
    inputs.steerVelocityDegPerSec = steer.getVelocityRPM() * 360.0 / 60.0;
    inputs.steerAppliedVolts = steer.getAppliedVoltage();
    inputs.steerCurrentAmps = steer.getOutputCurrent();
  }

  @Override
  public void applyOutputs(SwerveModuleIOOutputs outputs) {
    if (outputs.mode == SwerveModuleIOOutputMode.VOLTAGE) {
      drive.setVoltage(outputs.driveVolts);
      steer.setVoltage(outputs.steerVolts);
    } else {
      drive.stop();
      steer.stop();
    }
  }
}
