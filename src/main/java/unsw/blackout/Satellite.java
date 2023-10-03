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

    private List<FileReader> fr = new ArrayList<FileReader>();
    private List<FileWriter> fw = new ArrayList<FileWriter>();

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

        if (!fw.isEmpty())
            transferFiles();
    }

    protected void updatePosition() {
        double angularVelocity = linearVelocity / getHeight();
        Angle position = getPosition();

        position = position.subtract(Angle.fromRadians(angularVelocity));

        setPosition(position);
    }

    private void transferFiles() {
        for (int i = 0; i < fr.size(); i++) {
            FileWriter writer = fw.get(i);

            if (!writer.canTransfer()) {
                // TODO: Handle Teleport Case

                writer.delete();
                fr.remove(i);
                fw.remove(i);
                i--;
            }
        }

        if (fr.isEmpty() || fw.isEmpty())
            return;

        int bytesOut = maxBytesOutPerMin / fw.size();
        int bytesIn = maxBytesInPerMin / fr.size();

        for (int i = 0; i < fw.size(); i++) {
            FileWriter writer = fw.get(i);
            FileReader reader = fr.get(i);

            if (reader.fileComplete()) {
                writer.close();
                fr.remove(i);
                fw.remove(i);
                i--;
                continue;
            }

            int readBytes = Math.min(bytesIn, bytesOut);
            writer.write(reader.read(readBytes));
        }
    }

    public void startTransfer(Entity source, Entity dest, File f) {
        if (!canTransfer(source, dest))
            return;

        fr.add(new FileReader(f, source, dest));
        fw.add(new FileWriter(dest.addFile(f.getName(), f.getNumBytes()), source, dest));
    }

    public boolean hasBandwidth() {
        if (fr.size() >= maxBytesInPerMin || fw.size() >= maxBytesOutPerMin) {
            System.out.println(getId() + " has no bandwidth, reading: " + fr.size() + ", writing: " + fw.size());

            return false;
        }

        return true;
    }

    public boolean isStorageFull() {
        if (getFiles().size() >= maxFiles)
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
