package unsw.blackout;

import unsw.utils.MathsHelper;

public final class FileIO {
    private final File sourceFile;
    private final File destFile;

    private final Entity source;
    private final Entity dest;

    private int offset;

    protected FileIO(String filename, Entity source, Entity dest) {
        sourceFile = source.getFile(filename);
        destFile = dest.getFile(filename);

        this.source = source;
        this.dest = dest;

        offset = 0;
    }

    public boolean canTransfer() {
        return (MathsHelper.getDistance(source.getHeight(), source.getPosition(), dest.getHeight(),
                dest.getPosition()) <= source.getRange())
                && MathsHelper.isVisible(source.getHeight(), source.getPosition(), dest.getHeight(),
                        dest.getPosition());
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

    public void close() {
        destFile.fileComplete();
    }

    public void delete() {
        dest.removeFile(destFile.getName());
    }

    public boolean isFileComplete() {
        return destFile.isFinished();
    }
}
