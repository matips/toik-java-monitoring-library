package edu.agh.toik.sensorMonitor;

import java.util.Collection;

public class WatchedDeviceImpl implements WatchedDevice {
    final Collection<Sensor> sensors;

    public WatchedDeviceImpl(Collection<Sensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public <T> Sensor<T> getSensor(final String name, final DataType<T> type) throws NoSuchSensorException {
        return sensors.stream()
                .filter(sensor -> sensor.getName().equals(name))
                .filter(sensor -> sensor.getDataType().equals(type))
                .map(sensor -> (Sensor<T>) sensor)
                .findAny()
                .orElseThrow(NoSuchSensorException::new);

    }

}
