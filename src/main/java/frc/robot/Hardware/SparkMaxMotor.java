package frc.robot.Hardware;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import org.wpilib.hardware.bus.CANPort;

/**
 * Thin REV SparkMax (NEO) wrapper so IO classes never touch the REV API
 * directly. Construction never throws — a missing controller yields a null
 * motor and zeroed reads, so sim/CI without hardware still constructs.
 *
 * <p>Silence is deliberate, not a swallowed error: there is no CAN bus in
 * sim or unit tests, so failure is the common case there. On a real robot,
 * call {@link #isConnected()} after construction to detect a missing
 * controller (and raise an {@code Alert}) instead of driving blind.
 *
 * <p>2027 note: REV alpha-8 takes an explicit {@link CANPort} bus
 * (SystemCore has multiple CAN buses; {@code CAN_S0} is the default) and all
 * status reads are {@code Signal}s — hence the small {@code read()} helper.
 */
public class SparkMaxMotor {
  private SparkMax motor;
  private RelativeEncoder encoder;
  private final int canId;
  private double lastVoltage = 0.0;

  public SparkMaxMotor(int canId) {
    this(CANPort.CAN_S0, canId);
  }

  public SparkMaxMotor(CANPort bus, int canId) {
    this.canId = canId;
    try {
      motor = new SparkMax(bus, canId, MotorType.kBrushless);
      encoder = motor.getEncoder();
    } catch (Exception e) {
      motor = null;
      encoder = null;
    }
  }

  public void configure(boolean inverted, boolean brake, int currentLimitAmps) {
    if (motor == null) {
      return;
    }
    SparkMaxConfig config = new SparkMaxConfig();
    config.inverted(inverted);
    config.idleMode(brake ? IdleMode.kBrake : IdleMode.kCoast);
    config.smartCurrentLimit(currentLimitAmps);
    config.signals.primaryEncoderVelocityPeriodMs(20);
    config.signals.appliedOutputPeriodMs(20);
    config.signals.outputCurrentPeriodMs(50);
    motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setVoltage(double volts) {
    lastVoltage = volts;
    if (motor != null) {
      motor.setVoltage(volts);
    }
  }

  public void setDuty(double duty) {
    lastVoltage = duty * 12.0;
    if (motor != null) {
      motor.setThrottle(duty);
    }
  }

  public void stop() {
    setVoltage(0.0);
  }

  private static double read(com.revrobotics.util.Signal<Double> signal, double fallback) {
    try {
      Double value = signal.get();
      return value != null ? value : fallback;
    } catch (Exception e) {
      return fallback;
    }
  }

  /** Rotor velocity in RPM. */
  public double getVelocityRPM() {
    return encoder != null ? read(encoder.getVelocity(), 0.0) : 0.0;
  }

  /** Rotor position in rotations. */
  public double getPositionRotations() {
    return encoder != null ? read(encoder.getPosition(), 0.0) : 0.0;
  }

  public double getAppliedVoltage() {
    return lastVoltage;
  }

  public double getOutputCurrent() {
    return motor != null ? read(motor.getOutputCurrent(), 0.0) : 0.0;
  }

  public double getMotorTemperature() {
    return motor != null ? read(motor.getMotorTemperature(), 0.0) : 0.0;
  }

  public boolean isConnected() {
    return motor != null;
  }

  public int getCanId() {
    return canId;
  }
}
