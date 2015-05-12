package edu.agh.toik.sensorMonitor.interfaces;

import edu.agh.toik.sensorMonitor.types.IntegerData;
import edu.agh.toik.sensorMonitor.types.StringData;

public interface DataType<T> {
    static IntegerData INTEGER = new IntegerData();
    static StringData STRING = new StringData();
    String getTypeName();

    boolean isCorrectType(T value);

}
