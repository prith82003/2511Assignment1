package unsw.blackout;

import unsw.utils.MathsHelper;

public abstract class FileIO {
    protected File f;

    protected Entity source;
    protected Entity dest;

    protected FileIO(File f, Entity source, Entity dest) {
        this.f = f;

        this.source = source;
        this.dest = dest;
    }

    public boolean canTransfer() {
        return (MathsHelper.getDistance(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition()) <= source.getRange())
                && MathsHelper.isVisible(source.getHeight(), source.getPosition(), dest.getHeight(),
                        dest.getPosition());
    }
}
