package frc.robot.Subsystems;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

/**
 * Guards the YAGSL deploy config: all 8 files present, root lists 4 modules,
 * every module declares an encoder offset. It does NOT validate the offset
 * values — those come from the YAGSL configurator against the real chassis
 * (see docs/SWERVE_SETUP.md). A green test with placeholder offsets still
 * means "regenerate before wiring to hardware".
 */
class SwerveConfigTest {
  private static final File SWERVE_DIR = new File("src/main/deploy/swerve");

  @Test
  void allEightFilesPresent() throws Exception {
    String[] expected = {
      "swervedrive.json",
      "controllerproperties.json",
      "modules/frontleft.json",
      "modules/frontright.json",
      "modules/backleft.json",
      "modules/backright.json",
      "modules/physicalproperties.json",
      "modules/pidfproperties.json"
    };
    for (String name : expected) {
      assertTrue(new File(SWERVE_DIR, name).isFile(), "missing " + name);
    }
  }

  @Test
  void rootListsFourModules() throws Exception {
    String root = Files.readString(new File(SWERVE_DIR, "swervedrive.json").toPath());
    for (String module :
        new String[] {"frontleft.json", "frontright.json", "backleft.json", "backright.json"}) {
      assertTrue(root.contains(module), "root missing " + module);
    }
  }

  @Test
  void everyModuleDeclaresEncoderOffset() throws Exception {
    for (String module :
        new String[] {"frontleft.json", "frontright.json", "backleft.json", "backright.json"}) {
      String json =
          Files.readString(new File(SWERVE_DIR, "modules/" + module).toPath());
      assertTrue(json.contains("absoluteEncoderOffset"), module + " missing offset");
    }
  }
}
