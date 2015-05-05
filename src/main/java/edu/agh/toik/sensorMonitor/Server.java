package edu.agh.toik.sensorMonitor;

import com.google.gson.Gson;
import edu.agh.toik.sensorMonitor.interfaces.Sensor;
import edu.agh.toik.sensorMonitor.messages.ServerRequest;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Server {

    Map<Integer, SensorImpl> sensors;
    Socket socket;

    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();

    public void send(String serialized) throws IOException {
        serializer.serialize(serialized.getBytes(), socket.getOutputStream());
    }

    private void onServerRequest(ServerRequest serverRequest) {
        serverRequest.getSensorsToUpdate().forEach((key, isActive) -> sensors.get(key).setActive(isActive));
    }

    public void connect(InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        new Thread() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        final byte[] json = serializer.deserialize(socket.getInputStream());
                        final ServerRequest serverRequest = new Gson().fromJson(new String(json), ServerRequest.class);
                        onServerRequest(serverRequest);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void setSensors(List<SensorImpl> sensors) {
        this.sensors = sensors.stream().collect(Collectors.toMap(Sensor::getId, i -> i));
    }
}
