package edu.ucsc.leeps.fire.cong.logging;

import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.logging.LogEvent;

/**
 *
 * @author dev
 */
public class TickEvent implements LogEvent {

    public String period;
    public int subperiod;
    public int secondsLeft;
    public int subject;
    public int population;
    public int match;
    public int world;
    public float[] strategy;
    public float[] target;
    public float[] popStrategy;
    public float[] matchStrategy;
    public float[] realizedStrategy;
    public float[] realizedPopStrategy;
    public float[] realizedMatchStrategy;
    public float payoff;
    public Config config;

    public String getDelimiter() {
        return ",";
    }
}
