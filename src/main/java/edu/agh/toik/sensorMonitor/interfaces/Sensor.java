package edu.agh.toik.sensorMonitor.interfaces;

import edu.agh.toik.sensorMonitor.InvalidType;

public interface Sensor<T> {
    int getId();

    String getName();

    String getDescription();

    DataType getDataType();

    boolean isActive();

    void push(T value) throws InvalidType;
}
