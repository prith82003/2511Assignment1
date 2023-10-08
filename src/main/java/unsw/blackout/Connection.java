package unsw.blackout;

public final class Connection {
    private final Entity source;
    private final Entity dest;

    private RelaySatellite relay;

    private final boolean canTransfer;
    private final boolean needsRelay;

    public Connection(Entity source, Entity dest) {
        this.source = source;
        this.dest = dest;

        canTransfer = false;
        needsRelay = false;
    }

    public Connection(Entity source, Entity dest, RelaySatellite relay, boolean canTransfer) {
        this.source = source;
        this.dest = dest;
        this.relay = relay;

        this.canTransfer = canTransfer;
        needsRelay = false;
    }

    public Connection(Entity source, Entity dest, RelaySatellite relay, boolean canTransfer, boolean needsRelay) {
        this.source = source;
        this.dest = dest;
        this.relay = relay;

        this.needsRelay = needsRelay;
        this.canTransfer = canTransfer;
    }

    public Connection(boolean canTransfer) {
        this.canTransfer = canTransfer;
        needsRelay = false;
        source = null;
        dest = null;
    }

    public boolean canTransfer() {
        return canTransfer;
    }

    @Override
    public String toString() {
        return "Connection [source=" + source.getId() + ", dest=" + dest.getId() + ", relay=" + relay + ", canTransfer="
                + canTransfer + ", needsRelay=" + needsRelay + "]";
    }

    public boolean canTransferNow() {
        System.out.println("Checking if Can Transfer");

        if (needsRelay) {
            System.out.println("Transferring using Relay");
            RelaySatellite relay = BlackoutController.relaySatelliteInRange(source, dest);
            if (relay != null) {
                System.out.println("Relay Found: " + relay.getId() + " ?== " + this.relay.getId());
                return relay.equals(this.relay);
            }

            System.out.println("No Relays Found");
            return false;
        }

        if (!BlackoutController.entityExists(source.getId()) || !BlackoutController.entityExists(dest.getId()))
            return false;

        return Helper.canCommunicate(source, dest, false).canTransfer();
    }

    public boolean needsRelay() {
        return needsRelay;
    }

    public Entity getSource() {
        return source;
    }

    public Entity getDest() {
        return dest;
    }
}
