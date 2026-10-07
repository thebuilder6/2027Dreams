package frc.robot.Subsystems.swerve;

import org.littletonrobotics.junction.AutoLog;

/**
 * IO contract for one swerve module (drive roller + steer pivot). Follows
 * {@code Subsystems/template/TemplateIO}: inputs flow up once per cycle,
 * commands flow down as one outputs struct. Temporary vendor-free scaffold —
 * YAGSL returns when a 2027 release lands (see {@code attic/yagsl-drive}).
 */
public interface SwerveModuleIO {

  @AutoLog
  public static class SwerveModuleIOInputs {
    public boolean connected = false;
    public double drivePositionRads = 0.0;
    public double driveVelocityRadsPerSec = 0.0;
    public double driveAppliedVolts = 0.0;
    public double driveCurrentAmps = 0.0;
    public double steerPositionDeg = 0.0;
    public double steerVelocityDegPerSec = 0.0;
    public double steerAppliedVolts = 0.0;
    public double steerCurrentAmps = 0.0;
  }

  public enum SwerveModuleIOOutputMode {
    STOP,
    VOLTAGE
  }

  public static class SwerveModuleIOOutputs {
    public SwerveModuleIOOutputMode mode = SwerveModuleIOOutputMode.STOP;
    public double driveVolts = 0.0;
    public double steerVolts = 0.0;
  }

  public default void updateInputs(SwerveModuleIOInputs inputs) {}

  public default void applyOutputs(SwerveModuleIOOutputs outputs) {}

  public default void stop() {
    applyOutputs(new SwerveModuleIOOutputs());
  }
}
