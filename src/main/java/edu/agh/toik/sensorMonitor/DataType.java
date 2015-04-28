package edu.agh.toik.sensorMonitor;

public interface DataType<T> {
    String getTypeName();

    boolean isCorrectType(T value);

}
