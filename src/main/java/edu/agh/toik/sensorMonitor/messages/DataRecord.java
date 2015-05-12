package edu.agh.toik.sensorMonitor.messages;

import java.util.Date;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class DataRecord<T> {
    private final T value;
    private final Long timestamp;

    public DataRecord(T value) {
        this.value = value;
        this.timestamp = new Date().getTime();
    }
}
