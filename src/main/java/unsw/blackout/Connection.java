package unsw.blackout;

public final class Connection {
    private final Entity source;
    private final Entity dest;

    private final boolean canTransfer;
    private final boolean needsRelay;

    public Connection(Entity source, Entity dest) {
        this.source = source;
        this.dest = dest;

        canTransfer = false;
        needsRelay = false;
    }

    public Connection(Entity source, Entity dest, boolean canTransfer) {
        this.source = source;
        this.dest = dest;

        this.canTransfer = canTransfer;
        needsRelay = false;
    }

    public Connection(Entity source, Entity dest, boolean canTransfer, boolean needsRelay) {
        this.source = source;
        this.dest = dest;

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
        return "Connection [source=" + source.getId() + ", dest=" + dest.getId() + ", canTransfer=" + canTransfer
                + ", needsRelay=" + needsRelay + "]";
    }

    public boolean canTransferNow() {
        System.out.println("Checking if Can Transfer");

        if (!BlackoutController.entityExists(source.getId()) || !BlackoutController.entityExists(dest.getId()))
            return false;

        return Helper.canCommunicate(source, dest, true).canTransfer();
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
