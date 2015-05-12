package edu.agh.toik.sensorMonitor.types;

import edu.agh.toik.sensorMonitor.interfaces.DataType;
import edu.agh.toik.sensorMonitor.interfaces.TypeNames;

public class IntegerData implements DataType<java.lang.Integer> {

    @Override
    public String getTypeName() {
        return TypeNames.INTEGER;
    }

    @Override
    public boolean isCorrectType(Integer value) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }
}
