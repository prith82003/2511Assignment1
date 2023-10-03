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
        return this.files;
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public Map<String, FileInfoResponse> getFileResponses() {
        Map<String, FileInfoResponse> fileResponses = new Hashtable<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            // var fileInfo = new FileInfoResponse(entry.getKey(), entry.getValue().getAllContent(),
            //         entry.getValue().getCompleteBytes(), entry.getValue().isFinished());
            fileResponses.put(entry.getKey(), entry.getValue().getInfo());
        }
        return fileResponses;
    }

    protected void setFiles(Map<String, File> files) {
        this.files = files;
    }

    public File addFile(String name, int size) {
        File f = new File(name, "", size);
        files.put(f.getName(), f);
        return f;
    }

    public void removeFile(String name) {
        files.remove(name);
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
