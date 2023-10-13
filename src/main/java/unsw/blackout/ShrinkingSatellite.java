package unsw.blackout;

import unsw.utils.Angle;
import unsw.utils.ZippedFileUtils;

public class ShrinkingSatellite extends StandardSatellite {
    public ShrinkingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
    }

    @Override
    protected void onFinishTransfer(Connection connection, String name) {
        if (connection.getDest().equals(this))
            getFile(name).setContent(ZippedFileUtils.zipFile(getFile(name).getContent()), false);
        else if (!(connection.getDest() instanceof ShrinkingSatellite))
            connection.getDest().getFile(name).setContent(ZippedFileUtils.unzipFile(getFile(name).getContent()));
    }
}
