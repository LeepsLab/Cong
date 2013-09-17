package edu.ucsc.leeps.fire.cong.logging;

import edu.ucsc.leeps.fire.logging.LogEvent;

public class StrategyChangeEvent implements LogEvent {

    public long timestamp;
    public int id;
    public float[] newStrategy;
    public float[] targetStrategy;

    public StrategyChangeEvent(long timestamp, int id, float[] newStrategy, float[] targetStrategy) {
        this.timestamp = timestamp;
        this.id = id;
        this.newStrategy = newStrategy;
        this.targetStrategy = targetStrategy;
    }

    public String getDelimiter() {
        return ",";
    }
}
