package unsw.blackout;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    private static final int RELAY_VELOCITY = 1500;
    private static final int RELAY_RANGE = 300000;
    private static final int RELAY_MAX_FILES = 0;
    private static final int RELAY_MAX_BYTES = 0;
    private static final int RELAY_MAX_BYTES_IN_PER_MIN = 15;
    private static final int RELAY_MAX_BYTES_OUT_PER_MIN = 10;

    private boolean moveAntiClockwise = false;

    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, RELAY_VELOCITY, RELAY_RANGE, RELAY_MAX_FILES, RELAY_MAX_BYTES,
                RELAY_MAX_BYTES_IN_PER_MIN, RELAY_MAX_BYTES_OUT_PER_MIN);
    }

    @Override
    protected void updatePosition() {
        double angularVelocity = linearVelocity / getHeight();
        Angle position = getPosition();

        if (position.toDegrees() > 190) {
            if (position.toDegrees() < 345)
                moveAntiClockwise = false;
            else
                moveAntiClockwise = true;
        } else if (position.toDegrees() < 140)
            moveAntiClockwise = true;

        if (moveAntiClockwise)
            position = position.add(Angle.fromRadians(angularVelocity));
        else
            position = position.subtract(Angle.fromRadians(angularVelocity));

        setPosition(position);
    }
}
