package frc.robot.Subsystems.mechanism;

import org.wpilib.math.system.DCMotor;

/**
 * Desktop-sim MechanismIO: analytic first-order DC-motor model (WPILib
 * alpha-7 removed the {@code LinearSystemId} plant factories, so the model
 * below is written out explicitly).
 *
 * <p>Discrete dynamics, exact step response at the 20 ms loop:
 * {@code ω += (ωss − ω)·(1 − e^(A·dt))} with {@code ωss = −(B/A)·V},
 * {@code A = −G²·Kt/(Kv·R·J)}, {@code B = G·Kt/(R·J)} — the same model
 * {@code LinearSystemId} generated in 2026. Exact (not Euler) so stiff
 * plants (high gearing, tiny inertia — e.g. steer) stay stable at 20 ms.
 * Current draw comes from {@code DCMotor.getCurrent(ω, V)}.
 */
public class MechanismIOSim implements MechanismIO {
  public static final double LOOP_PERIOD_SECS = 0.02;

  private final DCMotor gearbox;
  private final double gearing;
  private final double momentOfInertiaKgM2;
  private final double plantA;
  private final double plantB;

  private double positionRads = 0.0;
  private double velocityRadsPerSec = 0.0;
  private double appliedVolts = 0.0;

  public MechanismIOSim() {
    this(DCMotor.getNEO(1), 1.0, 0.001);
  }

  public MechanismIOSim(DCMotor gearbox, double gearing, double momentOfInertiaKgM2) {
    this.gearbox = gearbox;
    this.gearing = gearing;
    this.momentOfInertiaKgM2 = momentOfInertiaKgM2;
    double kt = gearbox.Kt;
    double kv = gearbox.Kv;
    double r = gearbox.R;
    this.plantA =
        -gearing * gearing * kt / (kv * r * momentOfInertiaKgM2);
    this.plantB = gearing * kt / (r * momentOfInertiaKgM2);
  }

  @Override
  public void updateInputs(MechanismIOInputs inputs) {
    double steadyStateRadsPerSec = -(plantB / plantA) * appliedVolts;
    velocityRadsPerSec +=
        (steadyStateRadsPerSec - velocityRadsPerSec)
            * (1.0 - Math.exp(plantA * LOOP_PERIOD_SECS));
    positionRads += velocityRadsPerSec * LOOP_PERIOD_SECS;
    inputs.connected = true;
    inputs.positionRads = positionRads;
    inputs.velocityRadsPerSec = velocityRadsPerSec;
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = gearbox.getCurrent(velocityRadsPerSec, appliedVolts);
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
