package edu.agh.toik.sensorMonitor;

public class InvalidType extends RuntimeException {
    public InvalidType(Exception ex) {
        super(ex);
    }

    public InvalidType() {

    }
}
