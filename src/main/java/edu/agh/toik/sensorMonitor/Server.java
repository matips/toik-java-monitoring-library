package edu.agh.toik.sensorMonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    Socket socket;
    List<Consumer<String>> onMessageListeners = new LinkedList<>();

    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();

    public void send(String serialized) throws IOException {
        LOGGER.debug("Send information to server:\n {}", serialized);
        serializer.serialize(serialized.getBytes(), socket.getOutputStream());
    }


    public void addOnMessageListener(Consumer<String> listener) {
        this.onMessageListeners.add(listener);
    }

    public void connect(InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        LOGGER.info("Connected with sensor monitor server on " + address + ", port " + port);
        new Thread() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        final String message = new String(serializer.deserialize(socket.getInputStream()));
                        LOGGER.debug("Message from server received: {}", message);
                        onMessageListeners.forEach(l -> l.accept(message));
                    }
                } catch (IOException e) {
                    LOGGER.error("Connection to sensor monitor server lost. No reconnection strategy is implemented, server will not be reported any more", e);
                }
            }
        }.start();

    }
}
