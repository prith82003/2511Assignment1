package unsw.blackout;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    private static final int HANDHELD_RANGE = 50000000;

    public HandheldDevice(String deviceId, Angle position) {
        super(deviceId, position, HANDHELD_RANGE);
    }
}
