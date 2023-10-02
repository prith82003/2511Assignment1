package unsw.blackout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import java.util.Hashtable;
import java.util.List;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private Dictionary<String, Device> devices = new Hashtable<>();
    private Dictionary<String, Satellite> satellites = new Hashtable<>();

    private List<Entity> entities = new ArrayList<>();

    public void createDevice(String deviceId, String type, Angle position) {
        Device newDevice = null;
        if (type == "HandheldDevice") {
            newDevice = new HandheldDevice(deviceId, position);
        } else if (type == "LaptopDevice") {
            newDevice = new LaptopDevice(deviceId, position);
        } else if (type == "DesktopDevice") {
            newDevice = new DesktopDevice(deviceId, position);
        }

        devices.put(deviceId, newDevice);
        entities.add(newDevice);
    }

    public void removeDevice(String deviceId) {
        devices.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        Satellite newSatellite = null;
        if (type == "StandardSatellite") {
            newSatellite = new StandardSatellite(satelliteId, height, position);
        } else if (type == "RelaySatellite") {
            newSatellite = new RelaySatellite(satelliteId, height, position);
        } else if (type == "TeleportingSatellite") {
            newSatellite = new TeleportingSatellite(satelliteId, height, position);
        }

        satellites.put(satelliteId, newSatellite);
        entities.add(newSatellite);
    }

    public void removeSatellite(String satelliteId) {
        satellites.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return Collections.list(devices.keys());
    }

    public List<String> listSatelliteIds() {
        return Collections.list(satellites.keys());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        devices.get(deviceId).addFile(new File(filename, content));
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = entities.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);

        if (entity == null)
            return null;

        Map<String, FileInfoResponse> fileResponses = entity.getFileResponses();
        EntityInfoResponse response = new EntityInfoResponse(id, entity.getPosition(), entity.getHeight(),
                entity.getType(), fileResponses);

        return response;
    }

    public void simulate() {
        for (Satellite s : Collections.list(satellites.elements())) {
            s.simulate();
        }
    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        Entity entity = entities.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);

        if (entity == null)
            return null;

        List<String> communicableEntities = new ArrayList<>();

        for (Entity e : entities) {
            if (e.getId().equals(id))
                continue;

            if (Helper.canCommunicate(entity, e))
                communicableEntities.add(e.getId());
        }

        return communicableEntities;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity source = entities.stream().filter(e -> e.getId().equals(fromId)).findFirst().orElse(null);

        File file = null;
        for (Map.Entry<String, File> entry : source.getFiles().entrySet()) {
            if (entry.getKey().equals(fileName)) {
                file = entry.getValue();
            }
        }

        if (file == null)
            throw new FileTransferException.VirtualFileNotFoundException(fileName + " Not Found");

        Entity dest = entities.stream().filter(e -> e.getId().equals(toId)).findFirst().orElse(null);

        for (Map.Entry<String, File> entry : dest.getFiles().entrySet()) {
            if (entry.getKey().equals(fileName))
                throw new FileTransferException.VirtualFileAlreadyExistsException(
                        fileName + " Already Exists in " + toId);
        }

        boolean canTransfer = true;
        try {
            canTransfer = Helper.canTransfer(source, dest);
        } catch (FileTransferException e) {
            throw e;
        }

        if (!canTransfer)
            return;

        Satellite satellite = (source instanceof Satellite) ? (Satellite) source : (Satellite) dest;
        satellite.startTransfer(source, dest, file);
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
}
