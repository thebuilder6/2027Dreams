package frc.robot.Subsystems.swerve;

import frc.robot.Subsystems.mechanism.MechanismIO;
import frc.robot.Subsystems.mechanism.MechanismIO.MechanismIOOutputs;
import frc.robot.Subsystems.mechanism.MechanismIOSim;
import org.wpilib.math.system.DCMotor;
import org.wpilib.math.util.Units;

/**
 * Desktop-sim module: two analytic first-order DC-motor models (drive roller
 * + steer pivot) reused from {@link MechanismIOSim}, so the dynamics math has
 * one owner. Kinematic sim, not physics — no slip, no carpet. Steer angle
 * grows unbounded internally; consumers wrap with {@code Rotation2d}.
 */
public class SwerveModuleIOSim implements SwerveModuleIO {
  private final MechanismIOSim drive;
  private final MechanismIOSim steer;
  private final MechanismIO.MechanismIOInputs driveInputs =
      new MechanismIO.MechanismIOInputs();
  private final MechanismIO.MechanismIOInputs steerInputs =
      new MechanismIO.MechanismIOInputs();

  public SwerveModuleIOSim() {
    this(DCMotor.getNEO(1), 6.0, 0.004, 12.0, 0.0005);
  }

  public SwerveModuleIOSim(
      DCMotor gearbox,
      double driveGearing,
      double driveInertiaKgM2,
      double steerGearing,
      double steerInertiaKgM2) {
    drive = new MechanismIOSim(gearbox, driveGearing, driveInertiaKgM2);
    steer = new MechanismIOSim(gearbox, steerGearing, steerInertiaKgM2);
  }

  @Override
  public void updateInputs(SwerveModuleIOInputs inputs) {
    drive.updateInputs(driveInputs);
    steer.updateInputs(steerInputs);
    inputs.connected = true;
    inputs.drivePositionRads = driveInputs.positionRads;
    inputs.driveVelocityRadsPerSec = driveInputs.velocityRadsPerSec;
    inputs.driveAppliedVolts = driveInputs.appliedVolts;
    inputs.driveCurrentAmps = driveInputs.currentAmps;
    inputs.steerPositionDeg = Units.radiansToDegrees(steerInputs.positionRads);
    inputs.steerVelocityDegPerSec =
        Units.radiansToDegrees(steerInputs.velocityRadsPerSec);
    inputs.steerAppliedVolts = steerInputs.appliedVolts;
    inputs.steerCurrentAmps = steerInputs.currentAmps;
  }

  @Override
  public void applyOutputs(SwerveModuleIOOutputs outputs) {
    MechanismIOOutputs driveOutputs = new MechanismIOOutputs();
    MechanismIOOutputs steerOutputs = new MechanismIOOutputs();
    if (outputs.mode == SwerveModuleIO.SwerveModuleIOOutputMode.VOLTAGE) {
      driveOutputs.mode = MechanismIO.MechanismIOOutputMode.VOLTAGE;
      driveOutputs.volts = outputs.driveVolts;
      steerOutputs.mode = MechanismIO.MechanismIOOutputMode.VOLTAGE;
      steerOutputs.volts = outputs.steerVolts;
    } else {
      driveOutputs.mode = MechanismIO.MechanismIOOutputMode.STOP;
      steerOutputs.mode = MechanismIO.MechanismIOOutputMode.STOP;
    }
    drive.applyOutputs(driveOutputs);
    steer.applyOutputs(steerOutputs);
  }
}
