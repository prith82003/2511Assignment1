package unsw.blackout;

import unsw.utils.MathsHelper;

public final class Helper {
    public static final Connection canCommunicate(Entity source, Entity dest) {
        return canCommunicate(source, dest, false);
    }

    /**
     * Checks if the source and dest can physically communicate
     * @param source
     * @param dest
     * @return If entities can communicate
     */
    public static final Connection canCommunicate(Entity source, Entity dest, boolean checkRelay) {
        double dist = MathsHelper.getDistance(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition());
        boolean visible = MathsHelper.isVisible(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition());

        if (source instanceof Satellite) {
            if (!((Satellite) source).canTransfer(new Connection(source, dest)))
                return new Connection(false);
        }

        if (dest instanceof Satellite) {
            if (!((Satellite) dest).canTransfer(new Connection(source, dest)))
                return new Connection(false);
        }

        // Handle Multiple Relays in Connection
        boolean canTransfer = dist <= source.getRange() && visible;
        if (!canTransfer && checkRelay)
            canTransfer = BlackoutController.relaySatelliteInRange(source, dest);

        return new Connection(source, dest, canTransfer);
    }

    /**
     * Checks if the source and dest can communicate and if
     * the source has enough bandwidth and the dest has enough
     * storage space.
     * @param source
     * @param dest
     * @param fileSize
     * @return If the File can be transferred between the source and dest
     * @throws FileTransferException
     */
    public static final Connection canTransfer(Entity source, Entity dest, int fileSize) throws FileTransferException {
        if (source instanceof Satellite) {
            if (!(((Satellite) source).hasBandwidth()))
                throw new FileTransferException.VirtualFileNoBandwidthException(
                        source.getId() + " Does Not Have Enough Bandwidth (source)");
        }

        if (dest instanceof Satellite) {
            if (!((Satellite) dest).hasBandwidth())
                throw new FileTransferException.VirtualFileNoBandwidthException(
                        dest.getId() + " Does Not Have Enough Bandwidth (dest)");
            try {
                ((Satellite) dest).isStorageFull(fileSize);
            } catch (FileTransferException e) {
                throw e;
            }
        }

        return canCommunicate(source, dest, true);
    }
}
