package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class Satellite extends Entity {
    private final int linearVelocity;
    private final int maxFiles;
    private final int maxBytes;
    private final int maxBytesOutPerMin;
    private final int maxBytesInPerMin;

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
        double angularVelocity = linearVelocity / getHeight();
        Angle position = getPosition();
        position = position.add(Angle.fromDegrees(angularVelocity));

        if (position.toDegrees() > 360) {
            position = Angle.fromDegrees(position.toDegrees() - 360);
        }

        setPosition(position);

        if (!fw.isEmpty())
            transferFiles();
    }

    private void transferFiles() {
        for (int i = 0; i < fr.size(); i++) {
            FileReader reader = fr.get(i);
            FileWriter writer = fw.get(i);

            if (!reader.canTransfer() || !writer.canTransfer()) {
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

            int readBytes = Math.min(bytesIn, bytesOut);
            writer.write(reader.read(readBytes));
        }
    }

    public void startTransfer(Entity source, Entity dest, File f) {
        fr.add(new FileReader(f, source, dest));
        fw.add(new FileWriter(dest.addFile(f.getName()), source, dest));
    }

    public boolean hasBandwidth() {
        if (fr.size() >= maxBytesInPerMin || fw.size() >= maxBytesOutPerMin)
            return false;

        return true;
    }

    public boolean isStorageFull() {
        if (getFiles().size() >= maxFiles)
            return true;

        return false;
    }
}
