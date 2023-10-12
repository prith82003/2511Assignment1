package unsw.blackout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.stream.Collectors;
import java.util.Hashtable;
import java.util.List;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private static Dictionary<String, Entity> entities;

    public BlackoutController() {
        entities = new Hashtable<>();
    }

    public void createDevice(String deviceId, String type, Angle position) {
        Device newDevice = null;
        if (type.equals("HandheldDevice")) {
            newDevice = new HandheldDevice(deviceId, position);
        } else if (type.equals("LaptopDevice")) {
            newDevice = new LaptopDevice(deviceId, position);
        } else if (type.equals("DesktopDevice")) {
            newDevice = new DesktopDevice(deviceId, position);
        }

        entities.put(deviceId, newDevice);
    }

    public void removeDevice(String deviceId) {
        entities.remove(deviceId);
    }

    public static boolean entityExists(String id) {
        return entities.get(id) != null;
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        Satellite newSatellite = null;
        if (type.equals("StandardSatellite")) {
            newSatellite = new StandardSatellite(satelliteId, height, position);
        } else if (type.equals("RelaySatellite")) {
            newSatellite = new RelaySatellite(satelliteId, height, position);
        } else if (type.equals("TeleportingSatellite")) {
            newSatellite = new TeleportingSatellite(satelliteId, height, position);
        } else if (type.equals("ShrinkingSatellite")) {
            newSatellite = new ShrinkingSatellite(satelliteId, height, position);
        }

        entities.put(satelliteId, newSatellite);
    }

    public void removeSatellite(String satelliteId) {
        entities.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return (Collections.list(entities.elements()).stream().filter(e -> e instanceof Device).map(e -> {
            return e.getId();
        })).collect(Collectors.toList());
    }

    public List<String> listSatelliteIds() {
        return (Collections.list(entities.elements()).stream().filter(e -> e instanceof Satellite).map(e -> {
            return e.getId();
        })).collect(Collectors.toList());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        entities.get(deviceId).addFile(new File(filename, content, content.length()));
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = entities.get(id);

        if (entity == null)
            return null;

        EntityInfoResponse response = entity.getInfo();

        return response;
    }

    public void simulate() {
        // Loop through each satellite
        for (Entity e : Collections.list(entities.elements())) {
            if (e instanceof Satellite) {
                ((Satellite) e).simulate();
            }
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
        Entity entity = entities.get(id);

        if (entity == null)
            return null;

        List<String> communicableEntities = new ArrayList<>();

        for (Entity e : Collections.list(entities.elements())) {
            if (e.equals(entity))
                continue;

            if (entity instanceof Device && e instanceof Device)
                continue;

            if (Helper.canCommunicate(entity, e, true).canTransfer())
                communicableEntities.add(e.getId());
        }

        return communicableEntities;
    }

    public static boolean relaySatelliteInRange(Entity source, Entity dest) {
        return relaySatelliteInRange(source, dest, false);
    }

    public static boolean relaySatelliteInRange(Entity source, Entity dest, boolean debug) {
        List<RelaySatellite> relays = new ArrayList<>();
        List<RelaySatellite> relayPath = new ArrayList<>();
        var pathAvailable = checkRelayConnection(source, dest, relays, relayPath);
        if (debug) {
            System.out.print("Path Available: " + pathAvailable + ", " + source.getId() + " -> ");

            for (RelaySatellite r : relayPath) {
                System.out.print(r.getId() + " -> ");
            }

            System.out.println(dest.getId());
        }

        System.out.println("Checked Satellites: " + relays);

        return pathAvailable;
    }

    private static boolean checkRelayConnection(Entity source, Entity dest, List<RelaySatellite> relays,
            List<RelaySatellite> path) {
        for (Entity e : source.getEntitiesInRange(Collections.list(entities.elements()))) {
            if (e.getId().equals(dest.getId())) {
                return true;
            }

            if (e instanceof RelaySatellite) {
                RelaySatellite relay = (RelaySatellite) e;
                if (relays.contains(relay))
                    continue;

                relays.add(relay);
                path.add(relay);

                if (checkRelayConnection(relay, dest, relays, path))
                    return true;

                path.remove(relay);
            }
        }

        return false;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity source = entities.get(fromId);

        File file = source.getFiles().get(fileName);
        Entity dest = entities.get(toId);

        checkErrors(file, source, dest, fileName, toId);
        Connection transfer = Helper.canTransfer(source, dest, file.getCompleteBytes());

        if (!transfer.canTransfer())
            return;

        Satellite satellite = (source instanceof Satellite) ? (Satellite) source : (Satellite) dest;
        satellite.startTransfer(transfer, file);
    }

    private void checkErrors(File file, Entity source, Entity dest, String fileName, String toId)
            throws FileTransferException {
        if (file == null)
            throw new FileTransferException.VirtualFileNotFoundException(fileName + " Not Found");

        var files = dest.getFiles();
        if (files.get(fileName) != null)
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName + " Already Exists in " + toId);
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
