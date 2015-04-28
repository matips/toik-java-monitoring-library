package edu.agh.toik.sensorMonitor;

public interface Sensor<T> {
    int getId();

    String getName();

    String getDescription();

    DataType getDataType();

    void push(T value) throws InvalidType;
}
