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
    double prevPosition;

    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, TELEPORTING_VELOCITY, TELEPORTING_RANGE, TELEPORTING_MAX_FILES,
                TELEPORTING_MAX_BYTES, TELEPORTING_MAX_BYTES_IN_PER_MIN, TELEPORTING_MAX_BYTES_OUT_PER_MIN);
        moveAntiClockwise = true;
        prevPosition = position.toDegrees();
    }

    @Override
    protected void updatePosition() {
        double angularVelocity = Math.abs(linearVelocity / getHeight());
        Angle position = getPosition();

        if (moveAntiClockwise)
            position = position.add(Angle.fromRadians(angularVelocity));
        else
            position = position.subtract(Angle.fromRadians(angularVelocity));

        if (checkTeleport(position.toDegrees())) {
            position = Angle.fromDegrees(0);
            moveAntiClockwise = !moveAntiClockwise;
            onTeleport();
        }

        setPosition(position);
        prevPosition = position.toDegrees();
    }

    private boolean checkTeleport(double position) {
        if (prevPosition <= 180 && position >= 180 && moveAntiClockwise)
            return true;

        if (prevPosition >= 180 && position <= 180 && !moveAntiClockwise)
            return true;

        return false;
    }
}
