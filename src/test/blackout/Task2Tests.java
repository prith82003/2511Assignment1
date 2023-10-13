package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.HashMap;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)

public class Task2Tests {
    public void initialiseTest(BlackoutController bc) {
        bc.createDevice("Device A", "HandheldDevice", Angle.fromDegrees(20));
        bc.createDevice("Device B", "LaptopDevice", Angle.fromDegrees(30));
        bc.createDevice("Device C", "DesktopDevice", Angle.fromDegrees(40));
        bc.createDevice("Device D", "LaptopDevice", Angle.fromDegrees(50));

        bc.createSatellite("Satellite A", "StandardSatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(15));
        bc.createSatellite("Satellite B", "TeleportingSatellite", RADIUS_OF_JUPITER + 20_000, Angle.fromDegrees(25));
        bc.createSatellite("Satellite C", "RelaySatellite", RADIUS_OF_JUPITER + 30_000, Angle.fromDegrees(35));
        bc.createSatellite("Satellite D", "TeleportingSatellite", RADIUS_OF_JUPITER + 30_000, Angle.fromDegrees(45));

        bc.addFileToDevice("Device A", "file A", "Content");
        bc.addFileToDevice("Device A", "empty file", "");

        var resDevices = bc.listDeviceIds();
        assertListAreEqualIgnoringOrder(resDevices, Arrays.asList("Device A", "Device B", "Device C", "Device D"));

        var resSatellites = bc.listSatelliteIds();
        assertListAreEqualIgnoringOrder(resSatellites,
                Arrays.asList("Satellite A", "Satellite B", "Satellite C", "Satellite D"));

        var devAResponse = bc.getInfo("Device A");

        var fileResponse = new FileInfoResponse("file A", "Content", 7, true);
        var map = new HashMap<String, FileInfoResponse>();
        map.put("file A", fileResponse);
        fileResponse = new FileInfoResponse("empty file", "", 0, true);
        map.put("empty file", fileResponse);

        assertEquals(
                new EntityInfoResponse("Device A", Angle.fromDegrees(20), RADIUS_OF_JUPITER, "HandheldDevice", map),
                devAResponse, "Entity Response Failed");
    }

    @Test
    public void testRelayMovement() {
        var bc = new BlackoutController();

        bc.createSatellite("relay sat A", "RelaySatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(320));
        bc.createSatellite("relay sat B", "RelaySatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(345));
        bc.createSatellite("relay sat C", "RelaySatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(20));

        bc.simulate();

        var relSatInfo = bc.getInfo("relay sat A");

        assertTrue(relSatInfo.getPosition().toDegrees() < 320, "Relay Satellite A Movement Failed");

        relSatInfo = bc.getInfo("relay sat B");

        assertTrue(relSatInfo.getPosition().toDegrees() > 345, "Relay Satellite B Movement Failed");

        relSatInfo = bc.getInfo("relay sat C");

        assertTrue(relSatInfo.getPosition().toDegrees() > 20, "Relay Satellite C Movement Failed");
    }

    @Test
    public void testTeleportingMovement() {
        var bc = new BlackoutController();

        bc.createSatellite("teleporting sat A", "TeleportingSatellite", RADIUS_OF_JUPITER + 10_000,
                Angle.fromDegrees(320));
        bc.createSatellite("teleporting sat B", "TeleportingSatellite", RADIUS_OF_JUPITER + 10_000,
                Angle.fromDegrees(0));
        bc.createSatellite("teleporting sat C", "TeleportingSatellite", RADIUS_OF_JUPITER + 10_000,
                Angle.fromDegrees(180));

        bc.simulate();

        var relSatInfo = bc.getInfo("teleporting sat A");

        assertTrue(relSatInfo.getPosition().toDegrees() > 320, "Teleporting Satellite A Movement Failed");

        relSatInfo = bc.getInfo("teleporting sat B");

        assertTrue(relSatInfo.getPosition().toDegrees() > 0 && relSatInfo.getPosition().toDegrees() < 10,
                "Teleporting Satellite B Movement Failed");

        relSatInfo = bc.getInfo("teleporting sat C");

        assertTrue(relSatInfo.getPosition().toDegrees() == 0, "Teleporting Satellite C Movement Failed");
    }

    @Test
    public void testCorrupt() {
        var bc = new BlackoutController();

        bc.createDevice("Device A", "HandheldDevice", Angle.fromDegrees(180));
        bc.addFileToDevice("Device A", "Big File", "content".repeat(10));

        bc.createSatellite("Tel Sat", "TeleportingSatellite", RADIUS_OF_JUPITER + 5_000, Angle.fromDegrees(178));
        assertDoesNotThrow(() -> bc.sendFile("Big File", "Device A", "Tel Sat"), "File Send Failed");

        bc.simulate(10);

        var devInfo = bc.getInfo("Device A");
        var telSatInfo = bc.getInfo("Tel Sat");

        assertFalse(devInfo.getFiles().get("Big File").getData().contains("t"), "File Wasn't Corrupted");
        assertTrue(telSatInfo.getFiles().size() == 0, "File Wasn't Deleted From Satellite");
    }

    @Test
    public void testInstantDownload() {
        var bc = new BlackoutController();

        bc.createDevice("Device A", "HandheldDevice", Angle.fromDegrees(10));
        bc.createSatellite("Tel Satellite", "TeleportingSatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(0));

        String ogContent = "content".repeat(5);

        bc.addFileToDevice("Device A", "Big File", ogContent);
        assertDoesNotThrow(() -> bc.sendFile("Big File", "Device A", "Tel Satellite"), "File Send Failed");

        while (bc.getInfo("Tel Satellite").getPosition().compareTo(Angle.fromDegrees(178)) == -1) {
            bc.simulate();
        }

        System.out.println(bc.getInfo("Tel Satellite"));

        bc.createDevice("Device B", "HandheldDevice", Angle.fromDegrees(180));
        assertDoesNotThrow(() -> bc.sendFile("Big File", "Tel Satellite", "Device B"), "File Send 2 Failed");

        bc.simulate(40);

        var devInfo = bc.getInfo("Device B");
        assertTrue(devInfo.getFiles().containsKey("Big File"), "instant download failed");

        var content = devInfo.getFiles().get("Big File").getData();
        System.out.println("Content: " + content);
        assertFalse(content == ogContent, "t's not deleted");
    }

    @Test
    public void testRelayConnections() {
        var bc = new BlackoutController();

        bc.createDevice("Device A", "HandheldDevice", Angle.fromDegrees(10));

        // Create 5 relay satellites each differing by 20 degrees at height jupiter + 5000
        for (int i = 0; i < 5; i++) {
            bc.createSatellite("Relay Satellite " + i, "RelaySatellite", RADIUS_OF_JUPITER + 5_000,
                    Angle.fromDegrees(20 * i));
        }

        bc.createSatellite("sat", "StandardSatellite", RADIUS_OF_JUPITER + 10_000, Angle.fromDegrees(70));
        var commEntities = bc.communicableEntitiesInRange("Device A");

        assertListAreEqualIgnoringOrder(commEntities, Arrays.asList("Relay Satellite 0", "Relay Satellite 1",
                "Relay Satellite 2", "Relay Satellite 3", "Relay Satellite 4", "sat"));
    }
}
