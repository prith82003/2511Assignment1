package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    private static final int STANDARD_VELOCITY = 2500;
    private static final int STANDARD_RANGE = 150000;
    private static final int STANDARD_MAX_FILES = 3;
    private static final int STANDARD_MAX_BYTES = 80;

    private static final int STANDARD_MAX_BYTES_INOUT_PER_MIN = 1;

    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, STANDARD_VELOCITY, STANDARD_RANGE, STANDARD_MAX_FILES, STANDARD_MAX_BYTES,
                STANDARD_MAX_BYTES_INOUT_PER_MIN, STANDARD_MAX_BYTES_INOUT_PER_MIN);
    }
}
