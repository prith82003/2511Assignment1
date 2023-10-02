package unsw.blackout;

import unsw.utils.Angle;

public abstract class Device extends Entity {
    protected Device(String deviceId, Angle position, int range) {
        super(deviceId, position, range);
    }

    public void addFile(File file) {
        var files = getFiles();
        files.put(file.getName(), file);
        setFiles(files);
    }
}
