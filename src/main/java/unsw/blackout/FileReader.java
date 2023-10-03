package unsw.blackout;

public class FileReader extends FileIO {
    private int offset;

    public FileReader(File f, Entity source, Entity dest) {
        super(f, source, dest);
        offset = 0;
    }

    public void open(File f) {
        this.f = f;
        offset = 0;
    }

    public String read(int numBytes) {
        if (offset + numBytes > f.getNumBytes()) {
            numBytes = f.getNumBytes() - offset;
        }
        String content = f.getContent(offset, offset + numBytes);
        offset += numBytes;
        return content;
    }

    public boolean fileComplete() {
        return offset >= f.getNumBytes();
    }
}
