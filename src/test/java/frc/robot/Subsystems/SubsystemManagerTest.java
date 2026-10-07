package frc.robot.Subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.Interfaces.Subsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubsystemManagerTest {
  private static final class CountingSubsystem implements Subsystem {
    int updates;
    final boolean explode;

    CountingSubsystem(boolean explode) {
      this.explode = explode;
      SubsystemManager.registerSubsystem(this);
    }

    @Override
    public void update() {
      if (explode) {
        throw new RuntimeException("boom");
      }
      updates++;
    }

    @Override
    public void initialize() {}

    @Override
    public void log() {}

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public String getName() {
      return explode ? "exploding" : "counting";
    }
  }

  @BeforeEach
  void clear() {
    SubsystemManager.clearForTest();
  }

  @Test
  void oneBadSubsystemCannotKillTheLoop() {
    CountingSubsystem good = new CountingSubsystem(false);
    new CountingSubsystem(true);
    SubsystemManager.updateSubsystems();
    SubsystemManager.updateSubsystems();
    assertEquals(2, good.updates);
    assertTrue(SubsystemManager.getSubsystems().size() == 2);
  }
}
