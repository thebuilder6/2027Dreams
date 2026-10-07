package frc.robot.Subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.Data.Constants.SwerveConstants;
import frc.robot.Subsystems.swerve.SwerveGyroIOSim;
import frc.robot.Subsystems.swerve.SwerveModuleIO;
import frc.robot.Subsystems.swerve.SwerveModuleIOSim;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.math.geometry.Translation2d;

/**
 * Temporary-scaffold coverage: injected sim IOs, no hardware, no timer.
 * Cmd 2 m/s for 10 s (500 ticks ≫ any motor lag) so the assertion holds
 * regardless of plant time constants.
 */
class SwerveDriveTest {
  @BeforeAll
  static void initHal() {
    HAL.initialize();
  }

  @BeforeEach
  void clear() {
    SubsystemManager.clearForTest();
  }

  private SwerveDrive simDrive() {
    return new SwerveDrive(
        new SwerveModuleIO[] {
          new SwerveModuleIOSim(),
          new SwerveModuleIOSim(),
          new SwerveModuleIOSim(),
          new SwerveModuleIOSim()
        },
        new SwerveGyroIOSim(),
        SwerveConstants.modulePositions());
  }

  @Test
  void drivesForwardToTargetDistance() {
    SwerveDrive drive = simDrive();
    drive.drive(new Translation2d(2.0, 0.0), 0.0, false);
    for (int i = 0; i < 500; i++) {
      drive.update();
    }
    // Start pose is Blue (1, 4): expect x ≈ 1 + 20, y ≈ 4.
    assertTrue(drive.getPose().getX() > 18.0);
    assertTrue(drive.getPose().getX() < 21.5);
    assertEquals(4.0, drive.getPose().getY(), 0.3);
    assertTrue(drive.getSimulationCurrentDraw() > 0.0);
  }

  @Test
  void stopHoldsPose() {
    SwerveDrive drive = simDrive();
    drive.drive(new Translation2d(2.0, 0.0), 0.0, false);
    for (int i = 0; i < 100; i++) {
      drive.update();
    }
    drive.stop();
    double x = drive.getPose().getX();
    for (int i = 0; i < 100; i++) {
      drive.update();
    }
    assertEquals(x, drive.getPose().getX(), 0.5);
  }

  @Test
  void zeroGyroSetsAllianceHeading() {
    SwerveDrive drive = simDrive();
    drive.zeroGyroWithAlliance();
    // No driver station in tests → BLUE default → 0°.
    assertEquals(0.0, drive.getHeading().getDegrees(), 1.0);
  }
}
