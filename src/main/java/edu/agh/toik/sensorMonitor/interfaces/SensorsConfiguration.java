package edu.agh.toik.sensorMonitor.interfaces;

import edu.agh.toik.sensorMonitor.SensorsConfigurationImpl;

import java.io.IOException;
import java.net.InetAddress;

public interface SensorsConfiguration {
    SensorsConfiguration setDeviceName(String deviceName);

    SensorsConfiguration setIp(InetAddress address, int port);

    <T> SensorsConfiguration addSensor(String name, DataType<T> type);

    <T> SensorsConfigurationImpl addSensor(String name, String description, DataType<T> type);

    SensorsConfiguration setServer(InetAddress address, int port);

    WatchedDevice open() throws IOException;
}
