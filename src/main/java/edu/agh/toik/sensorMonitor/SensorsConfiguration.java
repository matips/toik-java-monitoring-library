package edu.agh.toik.sensorMonitor;

import java.net.InetAddress;

public interface SensorsConfiguration {
    SensorsConfiguration setDeviceName();

    SensorsConfiguration setIp(String address, int port);

    SensorsConfiguration addSensor(String name, DataType type);

    SensorsConfiguration addSensor(String name, String description, DataType type);

    SensorsConfiguration setServer(InetAddress address, int port);

    WatchedDevice open();
}
