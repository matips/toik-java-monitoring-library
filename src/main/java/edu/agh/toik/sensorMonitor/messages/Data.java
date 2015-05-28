package edu.agh.toik.sensorMonitor.messages;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/5/2015.
 */
public class Data<T> {
    @Expose
    private Map<Integer, DataRecord<T>> sensors = new HashMap<>();

    public Data<T> add(int sensorId, T element) {
        sensors.put(sensorId, new DataRecord<>(element));
        return this;
    }


}
