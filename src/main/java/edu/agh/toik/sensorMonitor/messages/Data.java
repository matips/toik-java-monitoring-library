package edu.agh.toik.sensorMonitor.messages;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Data<T> {
    private Map<Integer, DataRecord<T>> sensors = new TreeMap<>();

    public Data<T> add(int sensorId, T element) {
        sensors.put(sensorId, new DataRecord<>(element));
        return this;
    }


}
