package edu.agh.toik.sensorMonitor;


import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerTest {
    @Test
    public void testConnect() throws Exception {
        final Server instance = new Server();

        final Socket serverSocket = connect(instance);
        assertTrue(serverSocket.isConnected());
    }

    private Socket connect(Server instance) throws IOException, InterruptedException {
        final ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        final int port = serverSocket.getLocalPort();

        instance.connect(serverSocket.getInetAddress(), port);
        Thread.sleep(50);
        return serverSocket.accept();
    }

    @Test
    public void testSend() throws Exception {
        final Server instance = new Server();
        final Socket connection = connect(instance);

        instance.send("123456");
        byte[] payload = new byte[10];

        new BufferedInputStream(connection.getInputStream()).read(payload);
        assertArrayEquals(payload, new byte[]{
                0, 0, 0, 6,
                '1', '2', '3', '4', '5', '6'});
    }

    @Test
    public void testReceive() throws Exception {
        final Server instance = new Server();
        final Socket connection = connect(instance);

        Thread.sleep(50);
        final Consumer<String> listenerMock = mock(Consumer.class);

        instance.addOnMessageListener(listenerMock);

        connection.getOutputStream().write(new byte[]{
                0, 0, 0, 6,
                '1', '2', '3', '4', '5', '6'});

        Thread.sleep(50);
        verify(listenerMock).accept("123456");

    }

}