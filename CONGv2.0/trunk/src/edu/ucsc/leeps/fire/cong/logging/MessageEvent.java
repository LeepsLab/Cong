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
 * This is the log event for logging messages sent in the chatroom.
 */
public class MessageEvent implements LogEvent {

    public String period;
    public int subperiod;
    public int secondsLeft;
    public int subject;
    public int population;
    public String alias;
    public String text;
    
    /**
     * Constructer.  Does nothing, values have to initialized outside of the constructer.
     * @param period
     * @param subject
     * @param subperiod
     * @param millisLeft
     * @param group
     * @param matchGroup
     * @param strategies
     * @param matchStrategies
     * @param realizedStrategies
     * @param realizedMatchStrategies
     * @param targets
     * @param config 
     */
    public void log(String period, int subject, int subperiod, int millisLeft, int group, int matchGroup, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies, Map<Integer, float[]> realizedStrategies, Map<Integer, float[]> realizedMatchStrategies, Map<Integer, float[]> targets, Config config) {
        return;
    }
    
    /**
     * Returns the character |
     * @return "|"
     */
    public String getDelimiter() {
        return "|";
    }
}
