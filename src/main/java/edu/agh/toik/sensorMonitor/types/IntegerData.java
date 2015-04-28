package edu.agh.toik.sensorMonitor.types;

import edu.agh.toik.sensorMonitor.DataType;
import edu.agh.toik.sensorMonitor.TypeNames;

public class IntegerData implements DataType<java.lang.Integer> {

    @Override
    public String getTypeName() {
        return TypeNames.INTEGER;
    }

}
