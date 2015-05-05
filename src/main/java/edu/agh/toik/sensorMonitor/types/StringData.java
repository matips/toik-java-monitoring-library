package edu.agh.toik.sensorMonitor.types;

import edu.agh.toik.sensorMonitor.DataType;
import edu.agh.toik.sensorMonitor.TypeNames;

public class StringData implements DataType<String> {

    @Override
    public java.lang.String getTypeName() {
        return TypeNames.STRING;
    }

    @Override
    public boolean isCorrectType(String value) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }
}
