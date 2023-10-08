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
        if (angle < 0)
            angle += 360;
        setPosition(Angle.fromDegrees(angle));

        updatePosition();

        if (!fileIOs.isEmpty())
            transferFiles();
    }

    private void transferFiles() {
        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);

            if (!fileIO.canTransfer()) {
                System.out.println("File Transfer Failed");
                fileIO.delete();
                fileIOs.remove(i);
                i--;
            }
        }

        if (fileIOs.isEmpty())
            return;

        int bytesOut = getBytesOut();
        int bytesIn = getBytesIn();

        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);

            int numBytes = bytesOut;
            if (fileIO.getDest().equals(this))
                numBytes = bytesIn;

            if (fileIO.getSource() instanceof Satellite && fileIO.getDest() instanceof Satellite)
                numBytes = Math.min(((Satellite) fileIO.getSource()).getBytesOut(),
                        ((Satellite) fileIO.getDest()).getBytesIn());

            fileIO.transferContent(numBytes);

            if (fileIO.isFileComplete()) {
                System.out.println("File Transfer Complete");
                fileIOs.remove(i);
                i--;
                continue;
            }
        }
    }

    protected int getBytesIn() {
        int numIn = 0;
        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);
            if (fileIO.getDest().equals(this))
                numIn++;
        }

        return maxBytesInPerMin / Math.max(1, numIn);
    }

    protected int getBytesOut() {
        int numOut = 0;
        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);
            if (fileIO.getSource().equals(this))
                numOut++;
        }

        return maxBytesOutPerMin / Math.max(1, numOut);
    }

    protected void onTeleport() {
        for (int i = 0; i < fileIOs.size(); i++) {
            FileIO fileIO = fileIOs.get(i);
            if (fileIO.getSource() instanceof Device && fileIO.getDest() instanceof TeleportingSatellite)
                corruptFile(fileIO);

            if (fileIO.getSource() instanceof TeleportingSatellite)
                instantDownload(fileIO);

            fileIOs.remove(fileIO);
            i--;
        }
    }

    private void corruptFile(FileIO io) {
        System.out.println("Corrupting File");
        File f = io.getSource().getFile(io.getFilename());
        String content = f.getAllContent();
        content = content.replaceAll("t", "");
        f.setContent(content);

        io.getDest().removeFile(f.getName());
    }

    private void instantDownload(FileIO io) {
        System.out.println("Instant Download");
        File f = io.getSource().getFile(io.getFilename());
        Entity dest = io.getDest();
        File destFile = dest.getFile(f.getName());

        String content = f.getAllContent();

        content = content.replaceAll("t", "");
        destFile.setContent(content);
    }

    public void startTransfer(Connection connection, File f) {
        if (!canTransfer(connection))
            return;

        connection.getDest().addFile(f.getName(), f.getNumBytes());
        fileIOs.add(new FileIO(f.getName(), connection));
    }

    public boolean hasBandwidth() {
        if (fileIOs.size() >= maxBytesInPerMin || fileIOs.size() >= maxBytesOutPerMin) {
            return false;
        }

        return true;
    }

    public boolean isStorageFull(int newFileSize) throws FileTransferException {
        if (getFiles().size() >= maxFiles)
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");

        int totalBytes = newFileSize;
        for (var f : getFiles().entrySet()) {
            totalBytes += f.getValue().getCompleteBytes();
        }

        if (totalBytes >= maxBytes)
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");

        return false;
    }

    /**
     * Check Satellite Specific Constraints for Transferring Files
     * @param source
     * @param dest
     * @return
     */
    protected boolean canTransfer(Connection connection) {
        if (connection.getSource() instanceof StandardSatellite && connection.getDest() instanceof DesktopDevice)
            return false;

        if (connection.getSource() instanceof DesktopDevice && connection.getDest() instanceof StandardSatellite)
            return false;

        return true;
    }

    /**
     * Update Satellite Position
     */
    protected abstract void updatePosition();
}
