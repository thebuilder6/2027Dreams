package frc.robot.Subsystems.mechanism;

import frc.robot.Hardware.SparkMaxMotor;

/** Real-hardware MechanismIO: one NEO on a SparkMax. */
public class MechanismIOSparkMax implements MechanismIO {
  private final SparkMaxMotor motor;

  public MechanismIOSparkMax(int canId, boolean inverted, boolean brake, int currentLimitAmps) {
    motor = new SparkMaxMotor(canId);
    motor.configure(inverted, brake, currentLimitAmps);
  }

  @Override
  public void updateInputs(MechanismIOInputs inputs) {
    inputs.connected = motor.isConnected();
    inputs.positionRads =
        motor.getPositionRotations() * 2.0 * Math.PI;
    inputs.velocityRadsPerSec =
        motor.getVelocityRPM() * 2.0 * Math.PI / 60.0;
    inputs.appliedVolts = motor.getAppliedVoltage();
    inputs.currentAmps = motor.getOutputCurrent();
    inputs.tempCelsius = motor.getMotorTemperature();
  }

  @Override
  public void applyOutputs(MechanismIOOutputs outputs) {
    switch (outputs.mode) {
      case VOLTAGE:
        motor.setVoltage(outputs.volts);
        break;
      case DUTY:
        motor.setDuty(outputs.duty);
        break;
      case STOP:
      default:
        motor.stop();
        break;
    }
  }
}
