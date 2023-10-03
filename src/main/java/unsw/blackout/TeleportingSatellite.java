package unsw.blackout;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    private static final int TELEPORTING_VELOCITY = 1000;
    private static final int TELEPORTING_RANGE = 200000;
    private static final int TELEPORTING_MAX_FILES = Integer.MAX_VALUE;
    private static final int TELEPORTING_MAX_BYTES = 200;
    private static final int TELEPORTING_MAX_BYTES_IN_PER_MIN = 15;
    private static final int TELEPORTING_MAX_BYTES_OUT_PER_MIN = 10;

    private boolean moveAntiClockwise = false;

    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, TELEPORTING_VELOCITY, TELEPORTING_RANGE, TELEPORTING_MAX_FILES,
                TELEPORTING_MAX_BYTES, TELEPORTING_MAX_BYTES_IN_PER_MIN, TELEPORTING_MAX_BYTES_OUT_PER_MIN);
        moveAntiClockwise = true;
    }

    @Override
    protected void updatePosition() {
        double angularVelocity = linearVelocity / getHeight();
        Angle position = getPosition();

        if (position.toDegrees() == 180) {
            position = Angle.fromDegrees(0);
            moveAntiClockwise = !moveAntiClockwise;
        } else {
            if (moveAntiClockwise)
                position = position.add(Angle.fromRadians(angularVelocity));
            else
                position = position.subtract(Angle.fromRadians(angularVelocity));
        }

        setPosition(position);
    }

    @Override
    protected boolean canTransfer(Entity source, Entity dest) {
        return true;
    }
}
