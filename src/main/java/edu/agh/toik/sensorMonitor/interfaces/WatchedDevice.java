package edu.agh.toik.sensorMonitor.interfaces;

import edu.agh.toik.sensorMonitor.NoSuchSensorException;

public interface WatchedDevice {
    <T> Sensor<T> getSensor(String name, DataType<T> type) throws NoSuchSensorException;
}
