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

    }

    // TODO: Check Relay Connections
}
