package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public abstract class Satellite extends Entity {
    protected final int linearVelocity;
    protected final int maxFiles;
    protected final int maxBytes;
    protected final int maxBytesOutPerMin;
    protected final int maxBytesInPerMin;

    private List<FileIO> fileIOs = new ArrayList<>();

    public Satellite(String satelliteId, double height, Angle position, int linearVelocity, int range, int maxFiles,
            int maxBytes, int bytesIn, int bytesOut) {
        super(satelliteId, height, position, range);
        this.linearVelocity = linearVelocity;
        this.maxFiles = maxFiles;
        this.maxBytes = maxBytes;
        this.maxBytesInPerMin = bytesIn;
        this.maxBytesOutPerMin = bytesOut;
    }

    public void simulate() {
        Angle position = getPosition();
        var angle = position.toDegrees() % 360;
        setPosition(Angle.fromDegrees(angle));

        updatePosition();

        if (!fileIOs.isEmpty())
            transferFiles();
    }

    protected void updatePosition() {
        double angularVelocity = linearVelocity / getHeight();
        Angle position = getPosition();

        position = position.subtract(Angle.fromRadians(angularVelocity));

        setPosition(position);
    }

    private void transferFiles() {
        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);

            if (!fileIO.canTransfer()) {
                fileIO.delete();
                fileIOs.remove(i);
                i--;
            }
        }

        if (fileIOs.isEmpty())
            return;

        int bytesOut = maxBytesOutPerMin / fileIOs.size();
        int bytesIn = maxBytesInPerMin / fileIOs.size();

        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);

            if (fileIO.isFileComplete()) {
                System.out.println("File Transfer Complete");
                fileIOs.remove(i);
                i--;
                continue;
            }

            int readBytes = Math.min(bytesIn, bytesOut);
            fileIO.transferContent(readBytes);
        }
    }

    public void startTransfer(Entity source, Entity dest, File f) {
        if (!canTransfer(source, dest))
            return;

        dest.addFile(f.getName(), f.getNumBytes());
        fileIOs.add(new FileIO(f.getName(), source, dest));
    }

    public boolean hasBandwidth() {
        if (fileIOs.size() >= maxBytesInPerMin || fileIOs.size() >= maxBytesOutPerMin) {
            return false;
        }

        return true;
    }

    public boolean isStorageFull() {
        if (getFiles().size() >= maxFiles)
            return true;

        int totalBytes = 0;
        for (var f : getFiles().entrySet()) {
            totalBytes += f.getValue().getCompleteBytes();
        }

        if (totalBytes >= maxBytes)
            return true;

        return false;
    }

    /**
     * Check Satellite Specific Constraints for Transferring Files
     * @param source
     * @param dest
     * @return
     */
    protected abstract boolean canTransfer(Entity source, Entity dest);
}
