package unsw.blackout;

public class Satellite {
    private final int linearVelocity;
    private final int range;
    private final int maxFiles;
    private final int maxBytes;

    public Satellite(int linearVelocity, int range, int maxFiles, int maxBytes) {
        this.linearVelocity = linearVelocity;
        this.range = range;
        this.maxFiles = maxFiles;
        this.maxBytes = maxBytes;
    }
}
