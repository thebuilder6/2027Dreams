package frc.robot.Hardware;

import org.wpilib.driverstation.GenericHID.RumbleType;
import org.wpilib.driverstation.XboxController;
import java.util.HashMap;
import java.util.Map;

/**
 * Team controller: Xbox passthrough with two rules.
 *
 * <ul>
 *   <li>Sticks are <b>never</b> deadbanded here — deadband + shaping live in
 *       {@code Teleop} so every input path shares one cleanup chain.
 *   <li>Rumble writes are deduplicated so per-tick calls don't spam CAN.
 * </ul>
 *
 * <p>Sequenced haptic patterns land with the driver-feedback pass (see
 * {@code KNOWN_ISSUES.md} §D); this class stays a thin input device until then.
 */
public class Controller extends XboxController {
  private final Map<Integer, Boolean> debounceButtons = new HashMap<>();
  private double lastLeftRumble = -1.0;
  private double lastRightRumble = -1.0;

  public Controller(int port) {
    super(port);
  }

  @Override
  public double getLeftX() {
    return super.getLeftX();
  }

  @Override
  public double getLeftY() {
    return super.getLeftY();
  }

  @Override
  public double getRightX() {
    return super.getRightX();
  }

  @Override
  public double getRightY() {
    return super.getRightY();
  }

  @Override
  public void setRumble(RumbleType type, double value) {
    // 2027 has no both-sides constant: emulate by driving both grips.
    if (type == RumbleType.LEFT_RUMBLE) {
      if (Math.abs(value - lastLeftRumble) > 0.01) {
        lastLeftRumble = value;
        super.setRumble(type, value);
      }
    } else if (type == RumbleType.RIGHT_RUMBLE) {
      if (Math.abs(value - lastRightRumble) > 0.01) {
        lastRightRumble = value;
        super.setRumble(type, value);
      }
    } else {
      setRumble(RumbleType.LEFT_RUMBLE, value);
      setRumble(RumbleType.RIGHT_RUMBLE, value);
    }
  }

  /**
   * Edge-triggered button read: returns {@code true} exactly once per press.
   * This is a press <b>latch</b>, not a debounced level — do not poll it as
   * "is held". Prefer WPILib's {@code getXButtonPressed()} style reads (as
   * {@code Teleop} does) unless you need a raw button number.
   *
   * @param button WPILib raw button number (1-based, see controller layout)
   */
  public boolean getButtonPressedOnce(int button) {
    boolean latched = debounceButtons.getOrDefault(button, false);
    if (getHID().getRawButton(button) && latched) {
      debounceButtons.put(button, false);
      return false;
    } else if (getHID().getRawButtonPressed(button)) {
      debounceButtons.put(button, true);
      return true;
    }
    return latched;
  }

  public boolean isOperational() {
    return isConnected();
  }
}
