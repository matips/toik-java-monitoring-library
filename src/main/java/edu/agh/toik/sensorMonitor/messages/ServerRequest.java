package edu.agh.toik.sensorMonitor.messages;

import java.util.Map;

public class ServerRequest {
    Map<Integer, Boolean> sensorsToUpdate;

    public Map<Integer, Boolean> getSensorsToUpdate() {
        return sensorsToUpdate;
    }

}


