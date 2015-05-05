package edu.agh.toik.sensorMonitor;

import com.google.gson.Gson;
import edu.agh.toik.sensorMonitor.interfaces.DataType;
import edu.agh.toik.sensorMonitor.interfaces.Sensor;
import edu.agh.toik.sensorMonitor.interfaces.WatchedDevice;
import edu.agh.toik.sensorMonitor.messages.InitialMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class SensorsConfigurationImpl implements edu.agh.toik.sensorMonitor.interfaces.SensorsConfiguration {
    private String deviceName;
    private InetAddress address;
    private int port;
    private List<SensorImpl> sensors = new LinkedList<>();
    private int sensorsNumerator = 0;
    private Server server = new Server();

    @Override
    public SensorsConfigurationImpl setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    @Override
    public SensorsConfigurationImpl setIp(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        return this;
    }

    @Override
    public SensorsConfigurationImpl addSensor(String name, DataType type) {
        this.addSensor(name, "", type);
        return this;
    }

    @Override
    public <T> SensorsConfigurationImpl addSensor(String name, String description, DataType<T> type) {
        this.sensors.add(new SensorImpl<>(++sensorsNumerator, name, description, type, server));
        return this;
    }

    @Override
    public SensorsConfigurationImpl setServer(InetAddress address, int port) {
        this.port = port;
        this.address = address;
        return this;
    }

    @Override
    public WatchedDevice open() throws IOException {
        final List<Sensor> sensorsCasted = this.sensors.stream().collect(Collectors.toList());

        this.server.connect(address, port);
        this.server.setSensors(this.sensors);

        InitialMessage initialMessage = new InitialMessage();
        initialMessage.setDeviceName(deviceName);
        initialMessage.setSensors(sensorsCasted);

        final String serialized = new Gson().toJson(initialMessage);
        this.server.send(serialized);
        return new WatchedDeviceImpl(sensorsCasted);
    }
}
