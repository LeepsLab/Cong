/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/
package edu.ucsc.leeps.fire.cong.logging;

import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.logging.LogEvent;
import java.util.Map;

/**
 *
 * @author dev
 */
public class TickEvent implements LogEvent {

    public String period;
    public int subperiod;
    public int millisLeft;
    public int subject;
    public int group;
    public int match;
    public float[] strategy;
    public float[] target;
    public float[] popStrategy;
    public float[] matchStrategy;
    public float[] realizedStrategy;
    public float[] realizedPopStrategy;
    public float[] realizedMatchStrategy;
    public float payoff;
    public Config config;

    public void log(
            String period, int subject,
            int subperiod, int millisLeft,
            int group, int matchGroup,
            Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies,
            Map<Integer, float[]> realizedStrategies, Map<Integer, float[]> realizedMatchStrategies,
            Map<Integer, float[]> targets,
            Config config) {
        float length = config.length;
        float percent = (float) (length * (millisLeft / 1000f)) / length;
        this.period = period;
        this.subject = subject;
        this.config = config;
        this.subperiod = subperiod;
        this.millisLeft = millisLeft;
        this.group = group;
        this.strategy = strategies.get(subject);
        this.target = targets.get(subject);
        if (config.subperiods != 0 && config.probPayoffs) {
            this.payoff = config.payoffFunction.getPayoff(
                    subject, percent,
                    realizedStrategies, realizedMatchStrategies,
                    config);
            this.realizedStrategy = realizedStrategies.get(subject);
        } else {
            this.payoff = config.payoffFunction.getPayoff(
                    subject, percent,
                    strategies, matchStrategies,
                    config);
        }
    }

    public String getDelimiter() {
        return ",";
    }
}
