package edu.agh.toik.sensorMonitor.messages;

import edu.agh.toik.sensorMonitor.interfaces.Sensor;

import java.util.List;

public class InitialMessage {
    String deviceName;
    List<Sensor> sensors;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}
