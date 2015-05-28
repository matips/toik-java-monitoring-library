package edu.agh.toik.sensorMonitor.examples;

import com.sun.management.OperatingSystemMXBean;
import edu.agh.toik.sensorMonitor.NoSuchSensorException;
import edu.agh.toik.sensorMonitor.SensorsConfigurationImpl;
import edu.agh.toik.sensorMonitor.interfaces.DataType;
import edu.agh.toik.sensorMonitor.interfaces.WatchedDevice;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;

/**
 * Created by Mateusz Pszczolka (SG0220005) on 5/12/2015.
 */
public class SimpleProgram {

    public static final String PROCESSOR_USAGE_SENSOR_NAME = "processorUsage";
    public static final String STRING_SENSOR_NAME = "stringSensor";
    public static final String MAP_OF_STRING_SENSOR_NAME = "mapOfStringSensor";
    public static final Random RANDOM = new Random();
    private WatchedDevice device;

    private static String[] STRINGS_TO_EMMIT = {"We", "invite", "you", "to", "use", "of", "monitoring", "library"};
    Supplier<Double> cpuUsageProducer;
    private AtomicInteger randRobinStrings = new AtomicInteger();

    public SimpleProgram() {
        final java.lang.management.OperatingSystemMXBean gettedBean = getOperatingSystemMXBean();
        if (gettedBean instanceof com.sun.management.OperatingSystemMXBean)
            cpuUsageProducer = ((OperatingSystemMXBean) gettedBean)::getProcessCpuLoad;
        else {
            System.out.println("Cannot use com.sun.management.OperatingSystemMXBean to measure cpu usage. Your result might be not real");
            cpuUsageProducer = gettedBean::getSystemLoadAverage;
        }
    }

    public void init(InetAddress adress, int port) throws IOException {
        device = new SensorsConfigurationImpl()
                .addSensor(PROCESSOR_USAGE_SENSOR_NAME, DataType.INTEGER)
                .addSensor(STRING_SENSOR_NAME, DataType.STRING)
                .addSensor(MAP_OF_STRING_SENSOR_NAME, DataType.STRING_MAP)
                .setDeviceName("my simple device")
                .setServer(adress, port)
                .open();
    }

    public void generateInt(int value) throws NoSuchSensorException, IOException {
        device.getSensor(PROCESSOR_USAGE_SENSOR_NAME, DataType.INTEGER)
                .push(value);
    }

    public void generateStr(String msg) throws NoSuchSensorException, IOException {
        device.getSensor(STRING_SENSOR_NAME, DataType.STRING)
                .push(msg);
    }

    public void generateMapOfString(Map<String, String> map) throws NoSuchSensorException {
        device.getSensor(MAP_OF_STRING_SENSOR_NAME, DataType.STRING_MAP)
                .push(map);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: <serverName> <port>");
            System.exit(-1);
        } else {
            new SimpleProgram().run(InetAddress.getByName(args[0]), Integer.valueOf(args[1]));
        }
    }

    public void reportAboutProcessorUsage() {
        final double processCpuTime = cpuUsageProducer.get() * 10000;
        try {
            generateInt((int) processCpuTime);
        } catch (NoSuchSensorException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sent processCpuTime: " + processCpuTime);
    }

    private void reportExampleString() {
        try {
            generateStr(STRINGS_TO_EMMIT[randRobinStrings.getAndIncrement() % STRINGS_TO_EMMIT.length]);
        } catch (NoSuchSensorException | IOException e) {
            e.printStackTrace();
        }
    }

    private void reportSomeMap() {
        try {
            generateMapOfString(new HashMap<String, String>() {{
                put("oxygenium ", RANDOM.nextInt(100) + "%");
                put("nitrogenium", RANDOM.nextInt(100) + "%");
                put("carbonii dioxidum", RANDOM.nextInt(100) + "%");
                put("vapor", RANDOM.nextInt(100) + "%");
            }});
        } catch (NoSuchSensorException e) {
            e.printStackTrace();
        }
    }

    public void run(InetAddress adress, Integer port) throws IOException {
        init(adress, port);

        final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::reportAboutProcessorUsage, 0, 1, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::reportExampleString, 1, 4, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::reportSomeMap, 1, 2, TimeUnit.SECONDS);
    }
}
