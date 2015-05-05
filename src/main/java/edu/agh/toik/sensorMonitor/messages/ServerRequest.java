package edu.agh.toik.sensorMonitor.messages;

import java.util.Map;

public class ServerRequest {
    Map<Integer, Boolean> sensorsToUpdate;

    public Map<Integer, Boolean> getSensorsToUpdate() {
        return sensorsToUpdate;
    }

    public void setSensorsToUpdate(Map<Integer, Boolean> sensorsToUpdate) {
        this.sensorsToUpdate = sensorsToUpdate;
    }
}
