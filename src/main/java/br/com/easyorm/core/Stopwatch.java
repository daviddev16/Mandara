package br.com.easyorm.core;

import java.util.LinkedHashMap;
import java.util.Map;

// poor implementation
public final class Stopwatch {

    private static boolean enabled = false;
    
    private static final Map<String, Stopwatch> watches = new LinkedHashMap<String, Stopwatch>();
    
    public long startTime, endTime, elapsedTime;
    
    private final String key;
    
    public Stopwatch(String key) {
        this.key = key;
    }
    
    public Stopwatch begin() {
        startTime = System.nanoTime();
        return this;
    }
    
    public Stopwatch end() {
        elapsedTime = (endTime = System.nanoTime()) - startTime;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s | Start: %d ns | End: %d ns | Elapsed: ~ %d ns / ~ %f ms", 
                key, startTime, endTime, elapsedTime, (double)(elapsedTime) / 1_000_000);
    }
    
    public static void beginStopwatch(String key) {
        if (!enabled) 
            return;
        watches
            .put(key, new Stopwatch(key)
            .begin());
    }
    
    public static void endStopwatch(String key) {
        if (!enabled) 
            return;
        watches
            .get(key)
            .end();
    }
    
    public static void summary() {
        if (!enabled) 
            return;
        for (Stopwatch stopwatch : watches.values()) {
            System.out.println(stopwatch.toString());
        }
        watches.clear();
        System.out.println();
    }
    
    public static synchronized void enableStopwatch() {
        enabled = true;
    }
    
    public static synchronized void disableStopwatch() {
        enabled = false;
    }
    
}
