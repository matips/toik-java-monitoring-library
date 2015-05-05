package edu.agh.toik.sensorMonitor;

import edu.agh.toik.sensorMonitor.types.IntegerData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class SensorImplTest {

    private Server serverMock;
    private SensorImpl<Integer> instance;

    @Before
    public void before() {
        serverMock = mock(Server.class);
        instance = new SensorImpl<>(0, "testSensor", "", new IntegerData(), serverMock);

    }

    @Test
    public void testPushNoActive() throws Exception {
        instance.push(123);
        verifyNoMoreInteractions(serverMock);
    }

    @Test
    public void testPush() throws Exception {
        instance.setActive(true);
        instance.push(123);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(serverMock).send(captor.capture());

        assertTrue(captor.getValue().startsWith("{\"sensors\":{\"0\":{\"value\":123,\"timeStamp\":"));
    }

    @Test(expected = InvalidType.class)
    public void testInvalidData() throws IOException, InvalidType {
        instance.setActive(true);
        ((SensorImpl) instance).push("asdf");
    }
}