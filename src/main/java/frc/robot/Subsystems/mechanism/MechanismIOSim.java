package frc.robot.Subsystems.mechanism;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;

/**
 * Desktop-sim MechanismIO: WPILib {@code DCMotorSim} (NEO, configurable
 * gearing/inertia). No vendor physics needed — steps at the 20 ms loop.
 */
public class MechanismIOSim implements MechanismIO {
  public static final double LOOP_PERIOD_SECS = 0.02;

  private final DCMotorSim sim;
  private double appliedVolts = 0.0;

  public MechanismIOSim() {
    this(DCMotor.getNEO(1), 1.0, 0.001);
  }

  public MechanismIOSim(DCMotor gearbox, double gearing, double momentOfInertiaKgM2) {
    sim =
        new DCMotorSim(
            LinearSystemId.createDCMotorSystem(gearbox, momentOfInertiaKgM2, gearing), gearbox);
  }

  @Override
  public void updateInputs(MechanismIOInputs inputs) {
    sim.setInputVoltage(appliedVolts);
    sim.update(LOOP_PERIOD_SECS);
    inputs.connected = true;
    inputs.positionRads = sim.getAngularPositionRad();
    inputs.velocityRadsPerSec = sim.getAngularVelocityRadPerSec();
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = sim.getCurrentDrawAmps();
    inputs.tempCelsius = 0.0;
  }

  @Override
  public void applyOutputs(MechanismIOOutputs outputs) {
    switch (outputs.mode) {
      case VOLTAGE:
        appliedVolts = outputs.volts;
        break;
      case DUTY:
        appliedVolts = outputs.duty * 12.0;
        break;
      case STOP:
      default:
        appliedVolts = 0.0;
        break;
    }
  }
}
