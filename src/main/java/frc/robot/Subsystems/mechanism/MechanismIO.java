package frc.robot.Subsystems.mechanism;

import org.littletonrobotics.junction.AutoLog;

/**
 * IO contract for one generic rotary mechanism (roller, flywheel, or pivot
 * without gravity load — gravity-compensated arms extend this). Follows
 * {@code Subsystems/template/TemplateIO}: inputs flow up once per cycle,
 * commands flow down as one outputs struct.
 */
public interface MechanismIO {

  @AutoLog
  public static class MechanismIOInputs {
    public boolean connected = false;
    public double positionRads = 0.0;
    public double velocityRadsPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
    public double tempCelsius = 0.0;
  }

  public enum MechanismIOOutputMode {
    STOP,
    VOLTAGE,
    DUTY
  }

  public static class MechanismIOOutputs {
    public MechanismIOOutputMode mode = MechanismIOOutputMode.STOP;
    public double volts = 0.0;
    public double duty = 0.0;
  }

  public default void updateInputs(MechanismIOInputs inputs) {}

  public default void applyOutputs(MechanismIOOutputs outputs) {}

  public default void stop() {
    MechanismIOOutputs outputs = new MechanismIOOutputs();
    outputs.mode = MechanismIOOutputMode.STOP;
    applyOutputs(outputs);
  }
}
