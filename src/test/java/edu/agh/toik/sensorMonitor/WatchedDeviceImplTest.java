package edu.agh.toik.sensorMonitor;

import edu.agh.toik.sensorMonitor.interfaces.Sensor;
import edu.agh.toik.sensorMonitor.types.IntegerData;
import edu.agh.toik.sensorMonitor.types.StringData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class WatchedDeviceImplTest {

    private WatchedDeviceImpl instance;

    @Test
    public void testGetSensor() throws Exception {
        assertNotNull(instance.getSensor("qwer", new IntegerData()));
        assertNotNull(instance.getSensor("asdf", new StringData()));
    }

    @Before
    public void before() {
        List<Sensor> sensors = new LinkedList<>();
        sensors.add(new SensorImpl<>(0, "asdf", "", new StringData(), Mockito.mock(Server.class)));
        sensors.add(new SensorImpl<>(1, "qwer", "", new IntegerData(), Mockito.mock(Server.class)));

        this.instance = new WatchedDeviceImpl(sensors);
    }

    @Test(expected = NoSuchSensorException.class)
    public void testGetNonExistingSensor_1() throws NoSuchSensorException {

        assertNull(instance.getSensor("asdf", new IntegerData()));
    }

    @Test(expected = NoSuchSensorException.class)
    public void testGetNonExistingSensor_2() throws NoSuchSensorException {

        assertNull(instance.getSensor("qwer", new StringData()));
    }

    @Test(expected = NoSuchSensorException.class)
    public void testGetNonExistingSensor_3() throws NoSuchSensorException {

        assertNull(instance.getSensor(null, null));
    }
}