package edu.agh.toik.sensorMonitor;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    final AtomicReference<Socket> socketAtomicReference = new AtomicReference<>();

    List<Consumer<String>> onMessageListeners = Collections.synchronizedList(new LinkedList<>());

    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();

    List<Consumer<Server>> onConnectionEstablishListeners = Collections.synchronizedList(new LinkedList<>());

    public void send(String serialized) throws InvalidStateException {
        LOGGER.debug("Send information to server:\n {}", serialized);

        Socket socket = socketAtomicReference.get();
        if (socket != null){
            try {
                serializer.serialize(serialized.getBytes(), socket.getOutputStream());
            } catch (IOException e) {
                fixConnection(e, socket);
                LOGGER.error("Cannot send message to server");
            }
        }
    }


    public void addOnMessageListener(Consumer<String> listener) {
        this.onMessageListeners.add(listener);
    }

    public void addOnConnectionEstablishListeners(Consumer<Server> listener) {
        onConnectionEstablishListeners.add(listener);
    }

    public void connect(InetAddress address, int port) {

        final Thread listeningThread = new Thread(() -> {
            while (true) {
                Socket socket = establishConnection(address, port);
                try {
                    if (socket != null) {
                        final String message = new String(serializer.deserialize(socket.getInputStream()));
                        LOGGER.debug("Message from server received: {}", message);
                        onMessageListeners.forEach(l -> l.accept(message));
                    } else {
                        Thread.sleep(500);
                    }
                } catch (IOException e) {
                    fixConnection(e, socket);
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                }
            }
        });
        listeningThread.setDaemon(true);
        listeningThread.start();

    }

    private void fixConnection(IOException e, Socket socket) {
        LOGGER.error("Connection to sensor monitor server lost. Try to reestablish", e);
        IOUtils.closeQuietly(socket);
        socketAtomicReference.compareAndSet(socket, null);

    }

    private Socket establishConnection(InetAddress address, int port) {
        Socket socket = this.socketAtomicReference.get();
        if (socket == null) {
            try {
                socket = new Socket(address, port);
                socketAtomicReference.set(socket);
                LOGGER.info("Connected with sensor monitor server on " + address + ", port " + port);
                onConnectionEstablishListeners.forEach(listener -> listener.accept(this));
            } catch (IOException e) {
                LOGGER.error("Cannot connect to server. All messages  will be discard until connection reestablish");
            }
        }
        return socket;
    }
}
