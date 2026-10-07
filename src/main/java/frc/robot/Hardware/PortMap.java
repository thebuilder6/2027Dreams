package frc.robot.Hardware;

/**
 * Single owner of every port number. When wiring changes, only this file changes.
 * IDs are reserved placeholders until the 2027 chassis is known — do not wire
 * hardware to these numbers yet.
 */
public final class PortMap {
  private PortMap() {}

  // USB ports on the driver station
  public static final int DRIVER_CONTROLLER = 0;
  public static final int OPERATOR_CONTROLLER = 1;

  // CAN IDs — reserved, unassigned until build season
  public static final int RESERVED_DRIVE_FL = -1;
  public static final int RESERVED_DRIVE_FR = -1;
  public static final int RESERVED_DRIVE_BL = -1;
  public static final int RESERVED_DRIVE_BR = -1;
  public static final int RESERVED_MECHANISM_A = -1;
  public static final int RESERVED_MECHANISM_B = -1;

  // DIO — reserved
  public static final int RESERVED_ENCODER = -1;
}
