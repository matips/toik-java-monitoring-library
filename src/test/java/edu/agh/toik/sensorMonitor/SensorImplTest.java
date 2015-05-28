package edu.agh.toik.sensorMonitor;

import edu.agh.toik.sensorMonitor.messages.DataRecord;
import edu.agh.toik.sensorMonitor.types.IntegerData;
import edu.agh.toik.sensorMonitor.types.StringMap;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest(DataRecord.class)
@RunWith(PowerMockRunner.class)
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

        assertTrue(captor.getValue().startsWith("{\"sensors\":{\"0\":{\"value\":123,\"timestamp\":"));
    }

    @Test
    public void testPushStringMap() throws Exception {
        //initialize power mock (to mock timestamp generation)
        final Date currentDate = mock(Date.class);
        when(currentDate.getTime()).thenReturn(12345678l);
        whenNew(Date.class).withNoArguments().thenReturn(currentDate);

        //create instance
        SensorImpl<Map<String, String>> instance = new SensorImpl<>(0, "testSensor", "", new StringMap(), serverMock);

        //this is normally trigger by server
        instance.setActive(true);

        //user interaction with sensor
        instance.push(new HashMap<String, String>() {
            {
                put("key1", "val2");
                put("key2", "val2");
            }
        });
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(serverMock).send(captor.capture());


        JSONAssert.assertEquals(new JSONObject(new HashMap<String, JSONObject>() {{
            put("sensors", new JSONObject(new HashMap<Integer, JSONObject>() {{
                put(0, new JSONObject(new HashMap<String, Object>() {{
                    put("value", new JSONObject() {{
                        put("key1", "val2");
                        put("key2", "val2");
                    }});
                    put("timestamp", currentDate.getTime());
                }}));
            }}));
        }}).toString(), captor.getValue(), true);
    }

    @Test(expected = InvalidType.class)
    public void testInvalidData() throws IOException, InvalidType {
        instance.setActive(true);
        ((SensorImpl) instance).push("asdf");
    }
}