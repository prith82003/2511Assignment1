package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private static final int RELAY_VELOCITY = 1500;
    private static final int RELAY_RANGE = 300000;
    private static final int RELAY_MAX_FILES = 0;
    private static final int RELAY_MAX_BYTES = 0;
    private static final int RELAY_MAX_BYTES_IN_PER_MIN = 15;
    private static final int RELAY_MAX_BYTES_OUT_PER_MIN = 10;

    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, RELAY_VELOCITY, RELAY_RANGE, RELAY_MAX_FILES, RELAY_MAX_BYTES,
                RELAY_MAX_BYTES_IN_PER_MIN, RELAY_MAX_BYTES_OUT_PER_MIN);
    }
}
