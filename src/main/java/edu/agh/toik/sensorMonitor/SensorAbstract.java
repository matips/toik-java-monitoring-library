package edu.agh.toik.sensorMonitor;

public abstract class SensorAbstract<T> implements Sensor<T> {
    final int id;
    final String name;
    final String description;
    final DataType<T> dataType;

    public SensorAbstract(int id, String name, String description, DataType<T> dataType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public DataType<T> getDataType() {
        return dataType;
    }
}
