package edu.agh.toik.sensorMonitor;

import com.google.gson.Gson;
import edu.agh.toik.sensorMonitor.interfaces.DataType;
import edu.agh.toik.sensorMonitor.interfaces.Sensor;
import edu.agh.toik.sensorMonitor.messages.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SensorImpl<T> implements Sensor<T> {
    private static final Logger LOGGER = LogManager.getLogger(SensorImpl.class);

    final int id;
    final String name;
    final String description;
    final DataType<T> dataType;
    transient final Server server;
    boolean isActive = false;

    public SensorImpl(int id, String name, String description, DataType<T> dataType, Server server) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
        this.server = server;
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

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public void push(T value) throws InvalidType {
        if (isActive()) {
            try {
                if (dataType.isCorrectType(value)) {
                    Gson gson = new Gson();
                    final String serialized = gson.toJson(new Data<T>().add(this.id, value));
                    server.send(serialized);
                } else {
                    throw new InvalidType();
                }
            } catch (ClassCastException ex) {
                throw new InvalidType(ex);
            } catch (InvalidStateException e) {
                LOGGER.warn("Cannot push value to the server", e);
            }

        }
    }
}
