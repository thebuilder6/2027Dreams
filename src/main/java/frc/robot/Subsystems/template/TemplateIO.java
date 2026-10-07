package frc.robot.Subsystems.template;

import org.wpilib.math.geometry.Rotation2d;
import org.littletonrobotics.junction.AutoLog;

/**
 * Copy-paste starting point for every mechanism IO layer (6328 pattern).
 *
 * <ul>
 *   <li>Inputs flow one way per cycle: {@code io.updateInputs(inputs)} then
 *       {@code Logger.processInputs(name, inputs)}. Subsystem logic reads the
 *       cached {@code inputs} object, never the IO directly — so replayed log
 *       data looks identical to live hardware.
 *   <li>Outputs are a struct applied with {@code applyOutputs}, not individual
 *       setters — the whole command is visible in one place (and replayable).
 *   <li>Every method has a no-op default. The sim/real implementations override
 *       what they need; an empty anonymous class is a valid "disconnected" IO.
 * </ul>
 */
public interface TemplateIO {
  @AutoLog
  public static class TemplateIOInputs {
    public boolean connected = false;
    public double positionRads = 0.0;
    public double velocityRadsPerSec = 0.0;
    public double appliedVolts = 0.0;
    public double supplyCurrentAmps = 0.0;
    public double tempCelsius = 0.0;
  }

  public enum TemplateIOOutputMode {
    COAST,
    BRAKE,
    RUN
  }

  public static class TemplateIOOutputs {
    public TemplateIOOutputMode mode = TemplateIOOutputMode.COAST;
    public double velocityRadPerSec = 0.0;
    public double feedforwardVolts = 0.0;
    public Rotation2d targetRotation = Rotation2d.ZERO;
  }

  default void updateInputs(TemplateIOInputs inputs) {}

  default void applyOutputs(TemplateIOOutputs outputs) {}
}
