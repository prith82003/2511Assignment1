package unsw.blackout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Entity {
    private final String id;
    private final double height;
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
            fileResponses.put(entry.getKey(), entry.getValue().getInfo());
        }
        return fileResponses;
    }

    public List<Entity> getEntitiesInRange(List<Entity> entities) {
        List<Entity> entitiesInRange = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getId().equals(id))
                continue;

            if (isInRange(e))
                entitiesInRange.add(e);
        }
        return entitiesInRange;
    }

    private boolean isInRange(Entity e) {
        double distance = MathsHelper.getDistance(height, position, e.getHeight(), e.getPosition());
        boolean isVisible = MathsHelper.isVisible(height, position, e.getHeight(), e.getPosition());
        return distance <= range && isVisible;
    }

    public void addFile(String name, int size) {
        File f = new File(name, "", size);
        files.put(f.getName(), f);
    }

    public void addFile(File f) {
        files.put(f.getName(), f);
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entity))
            return false;

        if (obj.getClass() != getClass())
            return false;

        Entity e = (Entity) obj;

        return e.id.equals(id);
    }
}
