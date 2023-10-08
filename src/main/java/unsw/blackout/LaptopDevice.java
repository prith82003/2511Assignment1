package unsw.blackout;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    protected static final int LAPTOP_RANGE = 100000;

    public LaptopDevice(String deviceId, Angle position) {
        super(deviceId, position, LAPTOP_RANGE);
    }
}
