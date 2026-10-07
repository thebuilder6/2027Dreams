package frc.robot.Subsystems.drive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.junit.jupiter.api.Test;

class DriveIOSimTest {
  @Test
  void nullDriveRecordsPoseAndVoltages() {
    DriveIOSim io = new DriveIOSim();
    DriveIOInputsAutoLogged inputs = new DriveIOInputsAutoLogged();

    Pose2d target = new Pose2d(2.0, 3.0, Rotation2d.fromDegrees(90));
    io.setPose(target);
    io.setModuleDriveVoltage(0, 6.0);
    io.setModuleAngleVoltage(2, -3.0);
    io.updateInputs(inputs);

    assertEquals(target, inputs.odometryPose);
    assertEquals(6.0, inputs.driveAppliedVolts[0], 1e-9);
    assertEquals(-3.0, inputs.steerAppliedVolts[2], 1e-9);
  }

  @Test
  void stopZeroesAllVolts() {
    DriveIOSim io = new DriveIOSim();
    io.setModuleDriveVoltage(1, 12.0);
    io.stop();
    DriveIOInputsAutoLogged inputs = new DriveIOInputsAutoLogged();
    io.updateInputs(inputs);
    for (int i = 0; i < 4; i++) {
      assertEquals(0.0, inputs.driveAppliedVolts[i], 1e-9);
      assertEquals(0.0, inputs.steerAppliedVolts[i], 1e-9);
    }
  }

  @Test
  void chassisSpeedsAcceptedWithoutDrive() {
    DriveIOSim io = new DriveIOSim();
    io.setChassisSpeeds(new ChassisSpeeds(1.0, 0.0, 0.5));
    io.zeroGyro();
    DriveIOInputsAutoLogged inputs = new DriveIOInputsAutoLogged();
    io.updateInputs(inputs);
    assertEquals(0.0, inputs.gyroYawDeg, 1e-9);
    assertTrue(inputs.odometryPose.equals(new Pose2d()));
  }
}
