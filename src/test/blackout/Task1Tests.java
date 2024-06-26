package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.HashMap;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)

public class Task1Tests {
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
    public void testRemove() {
        var bc = new BlackoutController();
        initialiseTest(bc);

        bc.removeDevice("Device A");
        var list = bc.listDeviceIds();

        assertListAreEqualIgnoringOrder(list, Arrays.asList("Device B", "Device C", "Device D"));

        bc.removeSatellite("Satellite A");

        bc.simulate(20);

        list = bc.listSatelliteIds();

        assertListAreEqualIgnoringOrder(list, Arrays.asList("Satellite B", "Satellite C", "Satellite D"));
    }

    @Test
    public void testAddFile() {
        var bc = new BlackoutController();
        initialiseTest(bc);

        bc.addFileToDevice("Device A", "file B", "Content");
        // create fileResponses
        var fileResponse = new FileInfoResponse("file B", "Content", 7, true);
        var map = new HashMap<String, FileInfoResponse>();
        map.put("file B", fileResponse);
        map.put("file A", new FileInfoResponse("file A", "Content", 7, true));
        map.put("empty file", new FileInfoResponse("empty file", "", 0, true));

        // create entityResponse
        var entityResponse = new EntityInfoResponse("Device A", Angle.fromDegrees(20), RADIUS_OF_JUPITER,
                "HandheldDevice", map);

        assertEquals(entityResponse, bc.getInfo("Device A"), "Entity Response Failed");
    }
}
