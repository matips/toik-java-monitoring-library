package edu.agh.toik.sensorMonitor;

import edu.agh.toik.sensorMonitor.interfaces.WatchedDevice;
import edu.agh.toik.sensorMonitor.types.IntegerData;
import edu.agh.toik.sensorMonitor.types.StringData;
import org.junit.Test;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SensorsConfigurationImplTest {
    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();
    private volatile Socket connection;

    @Test
    public void testOpen() throws Exception {
        final ServerSocket serverSocket = new ServerSocket(0);
        final int port = serverSocket.getLocalPort();

        new Thread(() -> {
            try {
                connection = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        WatchedDevice result = new SensorsConfigurationImpl()
                .setServer(InetAddress.getLocalHost(), port)
                .setDeviceName("testDeviceName")
                .addSensor("sensor1", new IntegerData())
                .addSensor("sensor2", new IntegerData())
                .addSensor("stringSensor", new StringData())
                .open();
        Thread.sleep(70);
//        assertEquals("", new String(serializer.deserialize(connection.getInputStream())));
    }
}