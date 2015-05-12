package edu.agh.toik.sensorMonitor;

import com.google.gson.*;
import edu.agh.toik.sensorMonitor.interfaces.DataType;
import edu.agh.toik.sensorMonitor.interfaces.Sensor;
import edu.agh.toik.sensorMonitor.interfaces.WatchedDevice;
import edu.agh.toik.sensorMonitor.messages.InitialMessage;
import edu.agh.toik.sensorMonitor.messages.ServerRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class SensorsConfigurationImpl implements edu.agh.toik.sensorMonitor.interfaces.SensorsConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(SensorsConfigurationImpl.class);
    private String deviceName;
    private InetAddress address;
    private int port;
    private Map<Integer, SensorImpl> sensors = new HashMap<>();
    private int sensorsNumerator = 0;
    private Server server = new Server();

    @Override
    public SensorsConfigurationImpl setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }


    @Override
    public SensorsConfigurationImpl addSensor(String name, DataType type) {
        this.addSensor(name, "", type);
        return this;
    }

    @Override
    public <T> SensorsConfigurationImpl addSensor(String name, String description, DataType<T> type) {
        final int id = ++sensorsNumerator;
        this.sensors.put(id, new SensorImpl<>(id, name, description, type, server));

        return this;
    }

    @Override
    public SensorsConfigurationImpl setServer(InetAddress address, int port) {
        this.port = port;
        this.address = address;
        return this;
    }

    private void onServerMessage(String json) {
        final ServerRequest serverRequest = new Gson().fromJson(json, ServerRequest.class);
        LOGGER.info("updating sensors active for " + serverRequest.getSensorsToUpdate().size() + " sensors");
        serverRequest.getSensorsToUpdate().forEach((key, isActive) -> sensors.get(key).setActive(isActive));
    }

    @Override
    public WatchedDevice open() throws IOException {
        final List<Sensor> sensorsCasted = new LinkedList<>(sensors.values());

        server.addOnMessageListener(this::onServerMessage);

        InitialMessage initialMessage = new InitialMessage();
        initialMessage.setDeviceName(deviceName);
        initialMessage.setSensors(sensorsCasted);

        final String serialized = new GsonBuilder()
                .registerTypeAdapter(DataType.class, new JsonSerializer<DataType>() {
                    @Override
                    public JsonElement serialize(DataType dataType, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(dataType.getTypeName());
                    }
                }).create().toJson(initialMessage);

        server.addOnConnectionEstablishListeners(server -> {
            try {
                server.send(serialized);
            } catch (InvalidStateException e) {
                LOGGER.error(e);
            }
        });
        server.connect(address, port);

        return new WatchedDeviceImpl(sensorsCasted);
    }
}
