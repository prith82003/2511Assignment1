package unsw.blackout;

import java.util.Hashtable;
import java.util.Map;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public abstract class Entity {
    private final String id;
    private double height;
    private Angle position;
    private final int range;

    private Map<String, File> files = new Hashtable<>();

    protected Entity(String id, double height, Angle position, int range) {
        this.id = id;
        this.height = height;
        this.position = position;
        this.range = range;
    }

    protected Entity(String id, Angle position, int range) {
        this(id, 69911, position, range);
    }

    protected Map<String, File> getFiles() {
        return files;
    }

    public Map<String, FileInfoResponse> getFileResponses() {
        Map<String, FileInfoResponse> fileResponses = new Hashtable<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            var fileInfo = new FileInfoResponse(entry.getKey(), entry.getValue().getAllContent(),
                    entry.getValue().getNumBytes(), true);
            fileResponses.put(entry.getKey(), fileInfo);
        }
        return fileResponses;
    }

    protected void setFiles(Map<String, File> files) {
        this.files = files;
    }

    public File addFile(String name) {
        File f = new File(name, "");
        files.put(f.getName(), f);
        return f;
    }

    public String getId() {
        return id;
    }

    public double getHeight() {
        return height;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public int getRange() {
        return range;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }
}
