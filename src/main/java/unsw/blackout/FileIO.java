package unsw.blackout;

public final class FileIO {
    private final File sourceFile;
    private final File destFile;

    private Connection connection;

    private int offset;

    protected FileIO(String filename, Connection connection) {
        this.connection = connection;

        sourceFile = connection.getSource().getFile(filename);
        destFile = connection.getDest().getFile(filename);

        offset = 0;
    }

    public String getFilename() {
        return sourceFile.getName();
    }

    public boolean canTransfer() {
        return connection.canTransferNow();
    }

    public void transferContent(int numBytes) {
        write(read(numBytes));
    }

    private String read(int numBytes) {
        if (offset + numBytes > sourceFile.getNumBytes()) {
            numBytes = sourceFile.getNumBytes() - offset;
        }
        String content = sourceFile.getContent(offset, offset + numBytes);
        offset += numBytes;
        return content;
    }

    private void write(String content) {
        destFile.appendContent(content);
    }

    public void delete() {
        connection.getDest().removeFile(destFile.getName());
    }

    public boolean isFileComplete() {
        return destFile.isFinished();
    }

    public Entity getSource() {
        return connection.getSource();
    }

    public Entity getDest() {
        return connection.getDest();
    }
}
