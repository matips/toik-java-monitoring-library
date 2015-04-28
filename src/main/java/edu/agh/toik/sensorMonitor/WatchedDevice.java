package edu.agh.toik.sensorMonitor;

public interface WatchedDevice {
    <T> Sensor<T> getSensor(String name, DataType<T> type) throws NoSuchSensorException;
}
