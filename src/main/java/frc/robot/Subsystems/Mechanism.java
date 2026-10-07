package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.mechanism.MechanismIO;
import frc.robot.Subsystems.mechanism.MechanismIOInputsAutoLogged;
import frc.robot.Subsystems.mechanism.MechanismIO.MechanismIOOutputs;
import frc.robot.Subsystems.mechanism.MechanismIO.MechanismIOOutputMode;
import frc.robot.Subsystems.mechanism.MechanismIOSim;
import frc.robot.Subsystems.mechanism.MechanismIOSparkMax;
import frc.robot.Telemetry.LoggedTracer;
import org.littletonrobotics.junction.Logger;

/**
 * Generic single-motor mechanism template (roller, flywheel, or unloaded
 * pivot). Real mechanisms copy this file and add their control law (PID+FF,
 * gravity compensation, jam detection) — the IO boundary and lifecycle stay
 * identical.
 *
 * <p>Not instantiated by {@code Robot}: there is no mechanism hardware yet.
 * Construct with a CAN id when the first real mechanism lands.
 */
public class Mechanism implements Subsystem {
  public enum State {
    STANDBY,
    RUNNING
  }

  private final MechanismIO io;
  private final MechanismIOInputsAutoLogged inputs = new MechanismIOInputsAutoLogged();
  private final MechanismIOOutputs outputs = new MechanismIOOutputs();
  private State state = State.STANDBY;
  private double targetVolts = 0.0;

  public Mechanism(MechanismIO io) {
    this.io = io;
    SubsystemManager.registerSubsystem(this);
  }

  /** Convenience: Spark hardware on real, physics sim otherwise. */
  public Mechanism(int canId) {
    this(
        RobotBase.isSimulation()
            ? new MechanismIOSim()
            : new MechanismIOSparkMax(canId, false, true, 40));
  }

  /** Run open-loop at a voltage. Control laws replace this, not the IO. */
  public void run(double volts) {
    state = State.RUNNING;
    targetVolts = volts;
  }

  public void stop() {
    state = State.STANDBY;
    targetVolts = 0.0;
  }

  public State getState() {
    return state;
  }

  public double getPositionRads() {
    return inputs.positionRads;
  }

  public double getVelocityRadsPerSec() {
    return inputs.velocityRadsPerSec;
  }

  public double getCurrentAmps() {
    return inputs.currentAmps;
  }

  @Override
  public void update() {
    LoggedTracer.reset();
    io.updateInputs(inputs);
    Logger.processInputs("Mechanism", inputs);
    if (state == State.RUNNING) {
      outputs.mode = MechanismIOOutputMode.VOLTAGE;
      outputs.volts = targetVolts;
    } else {
      outputs.mode = MechanismIOOutputMode.STOP;
      outputs.volts = 0.0;
    }
    io.applyOutputs(outputs);
    LoggedTracer.record("Mechanism/Inputs");
  }

  @Override
  public void simulationUpdate() {}

  @Override
  public double getSimulationCurrentDraw() {
    return Math.abs(inputs.currentAmps);
  }

  @Override
  public void initialize() {}

  @Override
  public void log() {
    Logger.recordOutput("Mechanism/State", state.name());
    Logger.recordOutput("Mechanism/TargetVolts", targetVolts);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "Mechanism";
  }
}
