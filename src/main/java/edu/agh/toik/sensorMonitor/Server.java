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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    volatile Socket socket;
    private ReadWriteLock socketModificationLock = new ReentrantReadWriteLock();
    private final Lock readLock = socketModificationLock.readLock();
    private final Lock writeLock = socketModificationLock.writeLock();
    List<Consumer<String>> onMessageListeners = Collections.synchronizedList(new LinkedList<>());

    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();

    List<Consumer<Server>> onConnectionEstablishListeners = Collections.synchronizedList(new LinkedList<>());

    public void send(String serialized) throws InvalidStateException {
        LOGGER.debug("Send information to server:\n {}", serialized);

        if (readLock.tryLock()) {
            try {
                if (socket != null) {
                    serializer.serialize(serialized.getBytes(), socket.getOutputStream());
                } else {
                    LOGGER.error("Cannot send message to server");
                }
            } catch (IOException ex) {
                looseConnection(ex);
                throw new InvalidStateException("No connection to the server");
            } finally {
                readLock.unlock();
            }
        } else {
            LOGGER.error("Cannot send to socket (read lock blocked)");
            throw new InvalidStateException("Concoction to the server is not establish yet");
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
                establishConnection(address, port);
                readLock.lock();
                try {
                    if (socket != null) {
                        final String message = new String(serializer.deserialize(socket.getInputStream()));
                        LOGGER.debug("Message from server received: {}", message);
                        onMessageListeners.forEach(l -> l.accept(message));
                    } else{
                        Thread.sleep(500);
                    }
                } catch (IOException e) {
                    looseConnection(e);
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                } finally {
                    readLock.unlock();
                }
            }
        });
        listeningThread.setDaemon(true);
        listeningThread.start();

    }

    private void looseConnection(IOException e) {
        LOGGER.error("Connection to sensor monitor server lost. Try to reestablish", e);
        IOUtils.closeQuietly(socket);
        if (writeLock.tryLock()) {
            socket = null;
            writeLock.unlock();
        }
    }

    private void establishConnection(InetAddress address, int port) {
        if (socket == null) {
            writeLock.lock();
            try {
                this.socket = new Socket(address, port);
                LOGGER.info("Connected with sensor monitor server on " + address + ", port " + port);
                onConnectionEstablishListeners.forEach(listener -> listener.accept(this));
            } catch (IOException e) {
                this.socket = null;
                LOGGER.error("Cannot connect to server. All messages  will be discard until connection reestablish");
            } finally {
                writeLock.unlock();
            }
        }
    }
}
