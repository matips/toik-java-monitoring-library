package edu.agh.toik.sensorMonitor.examples;

import edu.agh.toik.sensorMonitor.interfaces.TypeNames;
import edu.agh.toik.sensorMonitor.messages.DataRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest(DataRecord.class)
@RunWith(PowerMockRunner.class)
public class SimpleProgramTest {
    public static final String EXPECTED_INIT_MESSAGW = new JSONObject(new HashMap<String, Object>() {{
        put("deviceName", "my simple device");
        put("sensors", new JSONArray(Arrays.asList(
                new JSONObject(new HashMap<String, Object>() {
                    {
                        put("id", 1);
                        put("name", "processorUsage");
                        put("description", "");
                        put("dataType", TypeNames.INTEGER);
                        put("isActive", false);
                    }
                }),
                new JSONObject(new HashMap<String, Object>() {
                    {
                        put("id", 2);
                        put("name", "stringSensor");
                        put("description", "");
                        put("dataType", TypeNames.STRING);
                        put("isActive", false);
                    }
                })
        )));

    }}).toString();
    public static final String FIRST_SENSOR_ACTIVATION_REQUEST = new JSONObject(new HashMap<String, Object>() {{
        put("sensorsToUpdate", new JSONObject(new HashMap<Integer, Boolean>() {{
            put(1, true);
            put(2, false);
        }}));
    }}).toString();
    volatile Socket serverSocket;
    volatile int port;

    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();

    private void connect() {
        try {
            ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
            port = serverSocket.getLocalPort();
            this.serverSocket = serverSocket.accept();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testRun() throws Exception {
        final double mockCpuLoad = 0.76;
        final Date currentDate = mock(Date.class);
        when(currentDate.getTime()).thenReturn(12345678l);
        whenNew(Date.class).withNoArguments().thenReturn(currentDate);

        SimpleProgram instance = new SimpleProgram();
        new Thread(this::connect).start();
        Thread.sleep(50);
        instance.run(serverSocket.getInetAddress(), port);
        instance.cpuUsageProducer = () -> mockCpuLoad;

        Thread.sleep(50);
        final InputStream serverInputStream = serverSocket.getInputStream();
        final OutputStream serverOutputStream = serverSocket.getOutputStream();

        String initMessaeg = new String(serializer.deserialize(serverInputStream));
        JSONAssert.assertEquals(EXPECTED_INIT_MESSAGW, initMessaeg, true);

        //expect no messages until activation request
        Thread.sleep(2000);
        assertEquals(serverInputStream.available(), 0);

        //do activation
        serializer.serialize(FIRST_SENSOR_ACTIVATION_REQUEST.getBytes(), serverOutputStream);
        Thread.sleep(1000);
        //after 1 s

        serializer.serialize(FIRST_SENSOR_ACTIVATION_REQUEST.getBytes(), serverOutputStream);

        final String intValueMsg = new String(serializer.deserialize(serverInputStream));
        JSONAssert.assertEquals(new JSONObject(new HashMap<String, JSONObject>() {{
            put("sensors", new JSONObject(new HashMap<Integer, JSONObject>() {{
                put(1, new JSONObject(new HashMap<String, Object>() {{
                    put("value", (int) (mockCpuLoad * 100));
                    put("timestamp", currentDate.getTime());
                }}));
            }}));
        }}).toString(), intValueMsg, true);
    }
    @Test
    public void testInit() throws Exception {
        final Date currentDate = mock(Date.class);
        when(currentDate.getTime()).thenReturn(12345678l);
        whenNew(Date.class).withNoArguments().thenReturn(currentDate);

        SimpleProgram instance = new SimpleProgram();
        new Thread(this::connect).start();

        Thread.sleep(50);
        instance.init(InetAddress.getLocalHost(), port);
        Thread.sleep(50);
        final InputStream serverInputStream = serverSocket.getInputStream();
        String initMessaeg = new String(serializer.deserialize(serverInputStream));
        JSONAssert.assertEquals(EXPECTED_INIT_MESSAGW, initMessaeg, true);

        instance.generateInt(92);

        Thread.sleep(50);
        assertEquals(serverInputStream.available(), 0);

        instance.generateStr("simpleStr");

        Thread.sleep(50);
        assertEquals(serverInputStream.available(), 0);

        final OutputStream serverOutputStream = serverSocket.getOutputStream();
        serializer.serialize(FIRST_SENSOR_ACTIVATION_REQUEST.getBytes(), serverOutputStream);

        Thread.sleep(50);

        instance.generateInt(666);
        final String intValueMsg = new String(serializer.deserialize(serverInputStream));
        JSONAssert.assertEquals(new JSONObject(new HashMap<String, JSONObject>() {{
            put("sensors", new JSONObject(new HashMap<Integer, JSONObject>() {{
                put(1, new JSONObject(new HashMap<String, Object>() {{
                    put("value", 666);
                    put("timestamp", currentDate.getTime());
                }}));
            }}));
        }}).toString(), intValueMsg, true);

        instance.generateStr("ex");
        Thread.sleep(50);
        assertEquals(serverInputStream.available(), 0);
    }

}