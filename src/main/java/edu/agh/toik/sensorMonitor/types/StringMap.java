package edu.agh.toik.sensorMonitor.types;

import edu.agh.toik.sensorMonitor.interfaces.DataType;

import java.util.Map;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/26/2015.
 */
public class StringMap implements DataType<Map<String, String>> {
    @Override
    public String getTypeName() {
        return "STRING_MAP";
    }

    @Override
    public boolean isCorrectType(Map<String, String> value) {
        return value.entrySet().stream()
                .allMatch(entry -> entry.getKey() instanceof String && entry.getValue() instanceof String);
    }

    @Override
    public Map<String, String> preProcess(Map<String, String> value) {
        return com.google.common.collect.ImmutableMap.copyOf(value);
    }
}
