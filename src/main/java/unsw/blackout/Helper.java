package unsw.blackout;

import unsw.utils.MathsHelper;

public final class Helper {
    public static final boolean canCommunicate(Entity source, Entity dest) {
        double dist = MathsHelper.getDistance(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition());
        boolean visible = MathsHelper.isVisible(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition());

        return dist <= dest.getRange() && visible;
    }

    public static final boolean canTransfer(Entity source, Entity dest) throws FileTransferException {
        if (source instanceof Satellite && !((Satellite) source).hasBandwidth())
            throw new FileTransferException.VirtualFileNoBandwidthException(source.getId()
                    + " Does Not Have Enough Bandwidth");
        if (dest instanceof Satellite) {
            if (((Satellite) dest).hasBandwidth())
                throw new FileTransferException.VirtualFileNoBandwidthException(dest.getId()
                        + " Does Not Have Enough Bandwidth");
            if (((Satellite) dest).isStorageFull())
                throw new FileTransferException.VirtualFileNoStorageSpaceException(dest.getId()
                        + " Does Not Have Enough Storage Space");
        }

        return canCommunicate(source, dest);
    }
}
