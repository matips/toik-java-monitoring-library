package edu.agh.toik.sensorMonitor.interfaces;

import edu.agh.toik.sensorMonitor.types.IntegerData;
import edu.agh.toik.sensorMonitor.types.StringData;
import edu.agh.toik.sensorMonitor.types.StringMap;

public interface DataType<T> {
    static IntegerData INTEGER = new IntegerData();
    static StringData STRING = new StringData();
    static StringMap STRING_MAP = new StringMap();

    String getTypeName();

    boolean isCorrectType(T value);

    default T preProcess(T value) {
        return value;
    }

}
