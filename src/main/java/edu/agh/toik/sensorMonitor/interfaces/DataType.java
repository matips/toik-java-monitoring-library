package edu.agh.toik.sensorMonitor.interfaces;

public interface DataType<T> {
    String getTypeName();

    boolean isCorrectType(T value);

}
